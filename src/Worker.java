
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Myndert
 */
public class Worker {
   
   /**
    * Vaildates that the file provided is a ZIP archive and checks to see if there is a "Game.rgssad" file, indicating
    * that it is a Insurgence patch file.
    * 
    * @param f The file selected by the user to be a patch file.
    * 
    * @return a handle to the corresponding ZipFile object if the provided file is valid. Returns null if an exception
    * occurred or if the file provided is not a valid patch.
    */
   public static ZipFile loadAndValidateArchiveFile(File f)
   {
      ZipFile archive = null;
      
      try
      {
         // Try and reload the file as a ZIP archive and retrieve the file entries thereof.
         archive = new ZipFile(f, ZipFile.OPEN_READ);
         Enumeration<? extends ZipEntry> entries = archive.entries();
         
         boolean foundRGSSAD = false;
         
         // Cycle through the entries and see if any of them are a "Game.rgssad" file. If one is found,
         // then the patch is valid and so we can prematurely exit the while loop.
         while (entries.hasMoreElements())
         {
            ZipEntry entry = entries.nextElement();
            
            if (entry.toString().endsWith("Game.rgssad"))
            {
               foundRGSSAD = true;
               break;
            }
         }
         
         // If the file was a valid ZIP archive, but not a valid patch file, set the archive handle to null.
         if (!foundRGSSAD) { archive = null; }
      }
      catch (ZipException zipEx)
      {
         JOptionPane.showMessageDialog(null, "There was a formatting error with the ZIP archive: "+zipEx.getMessage(), "ZIP Exception", JOptionPane.ERROR_MESSAGE);
      }
      catch (IOException ioEx)
      {
         JOptionPane.showMessageDialog(null, "An I/O exception occurred while trying to read the ZIP file: "+ioEx.getMessage(), "I/O Exception", JOptionPane.ERROR_MESSAGE);
      }
      catch (SecurityException secEx)
      {
         JOptionPane.showMessageDialog(null, "You do not have the proper permissions to read from the file you selected. Contact system administrator.", "Security Exception", JOptionPane.ERROR_MESSAGE);
      }
      
      return archive;
      
   }
   
   /**
    * This is the primary executor of the program. It handles file discovery, file copying, and printing various
    * status messages.
    * 
    * @param archive a handle to the ZIP archive containing the files to patch.
    * @param baseDir the base directory of the core game files.
    * @param output The JTextArea to use to output status messages to, via a custom {@link TextAreaOutputStream}.
    * @param progBar The JProgressBar used to visualize our program's progress.
    */
   public static void patchGame(ZipFile archive, File baseDir, JTextArea output, JProgressBar progBar)
   {
      PrintStream log = new PrintStream(new TextAreaOutputStream(output));
      
      // Print introductory information, to make sure that the proper files are selected.
      log.println("[Initializing]");
      log.println("  Patch Archive:  " + archive.getName());
      log.println("  Core Directory: " + baseDir.getAbsolutePath());
      log.println();
      
      log.println("[Discovering files]");
      
      // Look through the archive and find the files to patch.
      HashMap<ZipEntry, String> filesToCopy = discoverFiles(archive, log);
      
      // Update the JProgressBar so that it knows the total number of files to copy -- it will increment its progress
      // by one for every file copied.
      progBar.setMaximum(filesToCopy.size());
      
      log.println();
      
      log.println("[Copying files]");
      
      boolean errorOccurred = false; // Used to detect if an error occurred during copying.
      
      try
      {
         // Copy the files from the archive to the baseDir.
         copyFiles(archive, filesToCopy, baseDir, log, progBar);
      }
      catch (IOException ioEx)
      {
         JOptionPane.showMessageDialog(null, "Could not copy a file; aborting. " + ioEx.getMessage(), "I/O Exception", JOptionPane.ERROR_MESSAGE);
         errorOccurred = true;
      }
      catch (SecurityException secEx)
      {
         JOptionPane.showMessageDialog(null, "You do not have proper permission to write to the destination directory. Contact system administrator.", "Security Exception", JOptionPane.ERROR_MESSAGE);
         errorOccurred = true;
      }
      
      log.println();
      log.println("[ Patch " + (errorOccurred? "Failed" : "Succeeded") + " ]");
   }
   
   /**
    * This method iterates through the entries in the archive to catalog which files it should use to patch the core game.
    * The files are stored in a map, where each entry is associated with the final path it will be copied to in the core
    * files.
    * 
    * @param archive The ZIP archive to read from.
    * @param log The print stream to output status information to.
    * @return A HashMap that stores the ZipEntries read from the archive and associates them with the relative path to copy
    * them to in relation to the core file directories.
    */
   private static HashMap<ZipEntry, String> discoverFiles(ZipFile archive, PrintStream log)
   {
      HashMap<ZipEntry, String> directoryStruct = new HashMap<ZipEntry, String>();
      
      Enumeration<? extends ZipEntry> entries = archive.entries();
      
      while (entries.hasMoreElements())
      {
         // Conveniently, ZipEntry#getName() returns the location of the entry file in the archive.
         ZipEntry entry = entries.nextElement();
         String entryPath = entry.getName();
         String relativePath = "/"; // "/" is used to denote files in the top directory of the archive.
         
         // Filter out any unnecessary, or redundant, files. These include all files in the __MACOSX/ directory, all
         // .DS_Store and .rtf files, and any hidden files.
         if (isErroneousFile(entryPath)) { continue; }
         
         System.out.println(entryPath);
         
         String[] directory = entryPath.split("/");
         String fileName = directory[directory.length-1];
         
         // If the file is not in the top-level directory, then we have to extract the true relative path of the entry.
         if (directory.length > 2)
         {
            // This if-statement is used to detect if the patch archive provided is an original-format archive (which
            // uses the "Copy into true:directory:path" notation) or the new simple-merge format.
            if (directory[1].contains("Copy into"))
            {
               // For original-format archives, truncate "Copy..." to just the "true:directory:path" and replace the
               // colons with slashes (e.g. "Copy into true:directory:path" --> "true/directory/path").
               relativePath += (directory[1].substring(10).replace(":", "/")) + "/";
            }
            else
            {
               // For the new simple-merge format, reduce the name of the entry to contain just the relative path of
               // the file (which excludes the name of the file and any preamble).
               int firstSlash = entryPath.indexOf('/');
               int lastSlash = entryPath.lastIndexOf('/');
               relativePath = entryPath.substring(firstSlash, lastSlash+1);
            }
         }
         
         log.print("  Discovered " + fileName + " for " + relativePath);
         relativePath += fileName;
         
         directoryStruct.put(entry, relativePath);
         
         log.println();
      }
      
      return directoryStruct;
   }
   
   /**
    * Once the files have been discovered, copy them to their proper locations.
    * 
    * @param archive The archive to copy the files from.
    * @param fileDirectoryMap The map containing the relative directory tree information where the files are to be copied to.
    * @param baseDir The base directory of the core game files.
    * @param log The PrintStream to output status information to.
    * @param progBar The progress bar to update as files are copied.
    * @throws IOException If any errors occur during file read/write, they will be thrown as an I/O Exception.
    */
   private static void copyFiles(ZipFile archive, HashMap<ZipEntry, String> fileDirectoryMap, File baseDir, PrintStream log, JProgressBar progBar)
           throws IOException
   {
      Set<ZipEntry> files = fileDirectoryMap.keySet();
      int numCopied = 0;
      
      for (ZipEntry file : files)
      {
         String relativeDestination = fileDirectoryMap.get(file);
         String[] subDir = relativeDestination.split("/");
         
         String fileName = subDir[subDir.length - 1];
         String absoluteDestination = baseDir.getAbsolutePath() + relativeDestination;
         
         log.println("  Attempting to copy " + fileName + " as " + absoluteDestination + "... ");
         
         File destination = new File(absoluteDestination);
         
         // Detect if another older version of the file exists in the destination and delete the old if necessary.
         if (!destination.createNewFile())
         {
            log.println("    Old version exists. Will overwrite.");
            Files.delete(destination.toPath());
         }
         
         log.print("    Copying... ");
         
         // Now for the copy logic, which entails reading the information from the zip entry and writing the information
         // to the new file destination.
         //
         // I am unsure of whether there is an easier or faster way of doing this, but this works.
         BufferedOutputStream fileOut = null;
         try
         {
            fileOut = new BufferedOutputStream(new FileOutputStream(destination));
            InputStream fileIn = null;
            
            try
            {
               fileIn = archive.getInputStream(file);
               
               int b = fileIn.read();
               while (b != -1)
               {
                  fileOut.write(b);
                  b = fileIn.read();
               }
               
               log.println("SUCCESS");
            }
            finally
            {
               if (fileIn != null) { fileIn.close(); }
            }
            
            fileOut.flush();
         }
         finally
         {
            if (fileOut != null) { fileOut.close(); }
         }
         
         // If copying completed successfully, update the progress bar.
         numCopied++;
         progBar.setValue(numCopied);
      }
   }
   
   /**
    * A simple method to filter out any unnecessary or redundant files. Redundant files encompass all files in the
    * __MACOSX directory; any hidden, .DS_Store, or .rtf files; and any pure directories (which Java treats as any other
    * file).
    * 
    * @param name The name/path of the file in question.
    * @return true if the file is redundant; false otherwise.
    */
   private static boolean isErroneousFile(String name)
   {
      return (name.startsWith("__MACOSX") || name.endsWith(".DS_Store") || name.endsWith("/") || name.endsWith(".rtf") || name.contains("/."));
   }
   
   /**
    * This is a utility class that wraps a JTextArea in an OutputStream that can them be encompassed in a PrintStream,
    * so that one can abstractly deal with outputting information.
    */
   static class TextAreaOutputStream extends OutputStream
   {
      private final JTextArea output;
      
      TextAreaOutputStream(JTextArea output)
      {
         this.output = output;
      }
      
      @Override
      public void write(int b) throws IOException {
         output.append(String.valueOf((char)b));
         output.setCaretPosition(output.getDocument().getLength());
      }
      
   }
}
