
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
   
   public static ZipFile loadAndValidateArchiveFile(File f)
   {
      ZipFile archive = null;
      
      try
      {
         archive = new ZipFile(f, ZipFile.OPEN_READ);
         Enumeration<? extends ZipEntry> entries = archive.entries();
         
         boolean foundRGSSAD = false;
         
         while (entries.hasMoreElements())
         {
            ZipEntry entry = entries.nextElement();
            
            if (entry.toString().endsWith("Game.rgssad"))
            {
               foundRGSSAD = true;
               break;
            }
         }
         
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
   
   public static void patchGame(ZipFile archive, File baseDir, JTextArea output, JProgressBar progBar)
   {
      PrintStream log = new PrintStream(new TextAreaOutputStream(output));
      
      log.println("[Initializing]");
      log.println("  Patch Archive:  " + archive.getName());
      log.println("  Core Directory: " + baseDir.getAbsolutePath());
      log.println();
      
      log.println("[Discovering files]");
      
      HashMap<ZipEntry, String> filesToCopy = discoverFiles(archive, log);
      
      progBar.setMaximum(filesToCopy.size());
      
      log.println();
      
      log.println("[Copying files]");
      
      boolean errorOccurred = false;
      
      try
      {
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
   
   private static HashMap<ZipEntry, String> discoverFiles(ZipFile archive, PrintStream log)
   {
      HashMap<ZipEntry, String> directoryStruct = new HashMap<ZipEntry, String>();
      
      Enumeration<? extends ZipEntry> entries = archive.entries();
      
      while (entries.hasMoreElements())
      {
         ZipEntry entry = entries.nextElement();
         String entryPath = entry.getName();
         String relativePath = "/";
         
         if (isErroneousFile(entryPath)) { continue; }
         
         System.out.println(entryPath);
         
         String[] directory = entryPath.split("/");
         String fileName = directory[directory.length-1];
         
         if (directory.length > 2)
         {
            if (directory[1].contains("Copy into"))
            {
               relativePath += (directory[1].substring(10).replace(":", "/")) + "/";
            }
            else
            {
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
         
         if (!destination.createNewFile())
         {
            log.println("    Old version exists. Will overwrite.");
            Files.delete(destination.toPath());
         }
         
         log.print("    Copying... ");
         
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
         
         numCopied++;
         progBar.setValue(numCopied);
      }
   }
   
   private static boolean isErroneousFile(String name)
   {
      return (name.startsWith("__MACOSX") || name.endsWith(".DS_Store") || name.endsWith("/") || name.endsWith(".rtf") || name.contains("/."));
   }
   
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
