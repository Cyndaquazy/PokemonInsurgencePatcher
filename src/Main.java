
import java.awt.Color;
import java.io.File;
import java.util.zip.ZipFile;
import javax.swing.filechooser.FileFilter;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Myndert
 */
public class Main extends javax.swing.JFrame {

   private static final int BUTTON_PATCH = 0;
   private static final int BUTTON_DATA = 1;
   private static final int BUTTON_ACTION = 2;
   
   private static final Color COLOR_ERROR = new Color(255, 153, 153);
   private static final Color COLOR_GOOD = new Color(153, 255, 153);
   
   
   private ZipFile patchArchive = null;
   private File baseDirectory = null;
   
   
   /**
    * Creates new form Main
    */
   public Main() {
      initComponents();
   }

   /**
    * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The
    * content of this method is always regenerated by the Form Editor.
    */
   @SuppressWarnings("unchecked")
   // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
   private void initComponents() {

      patchGameButton = new javax.swing.JButton();
      patchArchiveLocationLabel = new javax.swing.JLabel();
      rootGameDataFolderLabel = new javax.swing.JLabel();
      patchArchiveLocationField = new javax.swing.JTextField();
      browsePatchArchiveLocationButton = new javax.swing.JButton();
      gameDataLocationField = new javax.swing.JTextField();
      browseGameDataLocationButton = new javax.swing.JButton();
      statusScrollPane = new javax.swing.JScrollPane();
      statusArea = new javax.swing.JTextArea();
      patchProgressBar = new javax.swing.JProgressBar();

      setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

      patchGameButton.setText("Patch Game");
      patchGameButton.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            patchGameButtonActionPerformed(evt);
         }
      });

      patchArchiveLocationLabel.setText("Patch Archive Location (*.zip):");

      rootGameDataFolderLabel.setText("Location of Core Game.rgssad:");

      patchArchiveLocationField.setEditable(false);

      browsePatchArchiveLocationButton.setText("Browse...");
      browsePatchArchiveLocationButton.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            browsePatchArchiveLocationButtonActionPerformed(evt);
         }
      });

      gameDataLocationField.setEditable(false);
      gameDataLocationField.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            gameDataLocationFieldActionPerformed(evt);
         }
      });

      browseGameDataLocationButton.setText("Browse...");
      browseGameDataLocationButton.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            browseGameDataLocationButtonActionPerformed(evt);
         }
      });

      statusScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

      statusArea.setEditable(false);
      statusArea.setBackground(new java.awt.Color(0, 0, 0));
      statusArea.setColumns(20);
      statusArea.setFont(new java.awt.Font("Consolas", 0, 12)); // NOI18N
      statusArea.setForeground(new java.awt.Color(255, 255, 255));
      statusArea.setRows(5);
      statusArea.setText("[Pokémon Insurgence Patcher, v1.0]\nAuthor: Myndert Papenhuyzen (/u/Cyndaquazy)\n    \nThis program is not affliated with Suzerain or any of the\ndevelopment staff of Pokémon Insurgence and is provided AS IS\nwith no expressed warranty; use at your own risk.\n\n-----\n\n");
      statusScrollPane.setViewportView(statusArea);

      javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
      getContentPane().setLayout(layout);
      layout.setHorizontalGroup(
         layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
               .addGroup(layout.createSequentialGroup()
                  .addContainerGap()
                  .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                     .addComponent(statusScrollPane)
                     .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                           .addGroup(layout.createSequentialGroup()
                              .addComponent(patchArchiveLocationLabel)
                              .addGap(0, 0, Short.MAX_VALUE))
                           .addGroup(layout.createSequentialGroup()
                              .addGap(10, 10, 10)
                              .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                 .addComponent(patchArchiveLocationField)
                                 .addComponent(gameDataLocationField))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                           .addComponent(browsePatchArchiveLocationButton)
                           .addComponent(browseGameDataLocationButton, javax.swing.GroupLayout.Alignment.TRAILING)))
                     .addGroup(layout.createSequentialGroup()
                        .addComponent(rootGameDataFolderLabel)
                        .addGap(0, 0, Short.MAX_VALUE))))
               .addGroup(layout.createSequentialGroup()
                  .addGap(205, 205, 205)
                  .addComponent(patchGameButton)
                  .addGap(0, 196, Short.MAX_VALUE))
               .addGroup(layout.createSequentialGroup()
                  .addContainerGap()
                  .addComponent(patchProgressBar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
            .addContainerGap())
      );
      layout.setVerticalGroup(
         layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(patchArchiveLocationLabel)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
               .addComponent(patchArchiveLocationField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
               .addComponent(browsePatchArchiveLocationButton))
            .addGap(18, 18, 18)
            .addComponent(rootGameDataFolderLabel)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
               .addComponent(gameDataLocationField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
               .addComponent(browseGameDataLocationButton))
            .addGap(18, 18, 18)
            .addComponent(patchGameButton)
            .addGap(18, 18, 18)
            .addComponent(patchProgressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(statusScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 247, Short.MAX_VALUE)
            .addContainerGap())
      );

      pack();
   }// </editor-fold>//GEN-END:initComponents

   private void patchGameButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_patchGameButtonActionPerformed
      doAction(BUTTON_ACTION);
   }//GEN-LAST:event_patchGameButtonActionPerformed

   private void browsePatchArchiveLocationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browsePatchArchiveLocationButtonActionPerformed
      doAction(BUTTON_PATCH);
   }//GEN-LAST:event_browsePatchArchiveLocationButtonActionPerformed

   private void browseGameDataLocationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseGameDataLocationButtonActionPerformed
      doAction(BUTTON_DATA);
   }//GEN-LAST:event_browseGameDataLocationButtonActionPerformed

   private void gameDataLocationFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gameDataLocationFieldActionPerformed
      // TODO add your handling code here:
   }//GEN-LAST:event_gameDataLocationFieldActionPerformed

   /**
    * @param args the command line arguments
    */
   public static void main(String args[]) {
      /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
       * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
       */
      try {
         for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
            if ("Nimbus".equals(info.getName())) {
               javax.swing.UIManager.setLookAndFeel(info.getClassName());
               break;
            }
         }
      } catch (ClassNotFoundException ex) {
         java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
      } catch (InstantiationException ex) {
         java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
      } catch (IllegalAccessException ex) {
         java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
      } catch (javax.swing.UnsupportedLookAndFeelException ex) {
         java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
      }
        //</editor-fold>

      /* Create and display the form */
      java.awt.EventQueue.invokeLater(new Runnable()
      {
         @Override
         public void run()
         {
            new Main().setVisible(true);
         }
      });
   }

   private void doAction(int buttonSrc)
   {
      JFileChooser chooser = new JFileChooser();
      
      switch(buttonSrc)
      {
         case BUTTON_PATCH:
            chooser.setFileFilter(getFileFilterFor("zip"));
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
            {
               File f = chooser.getSelectedFile();
               ZipFile z = Worker.loadAndValidateArchiveFile(f);
               
               patchArchiveLocationField.setText(f.getAbsolutePath());
               
               if (z == null)
               { 
                  JOptionPane.showMessageDialog(this, "Not valid patch -- no Game.rgssad found.", "Bad Patch", JOptionPane.ERROR_MESSAGE);
                  patchArchiveLocationField.setBackground(COLOR_ERROR);
               }
               else
               {
                  this.patchArchive = z;
                  patchArchiveLocationField.setBackground(COLOR_GOOD);
               }
            }
            break;
         case BUTTON_DATA:
            chooser.setFileFilter(getFileFilterFor("rgssad"));
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
            {
               File f = chooser.getSelectedFile();
               gameDataLocationField.setText(f.getAbsolutePath());
               
               if (f.getAbsolutePath().endsWith("Game.rgssad"))
               {
                  this.baseDirectory = f.getParentFile();
                  gameDataLocationField.setBackground(COLOR_GOOD);
               }
               else
               {
                  JOptionPane.showMessageDialog(this, "Not a Game.rgssad file.", "Bad File", JOptionPane.ERROR_MESSAGE);
                  gameDataLocationField.setBackground(COLOR_ERROR);
               }
            }
            break;
         case BUTTON_ACTION:
            if (patchArchive != null && baseDirectory != null)
            {
               Thread t = new Thread(new Runnable()
               {
                  @Override
                  public void run()
                  {
                     Worker.patchGame(patchArchive, baseDirectory, statusArea, patchProgressBar);
                  }
               });
               
               t.start();
            }
            else
            {
               JOptionPane.showMessageDialog(this, "Patch Archive or Core Game.rgssad not set.", "Can't Continue", JOptionPane.ERROR_MESSAGE);
            }
            break;
         default:
            //do nothing
      }
   }
   
   private FileFilter getFileFilterFor(String ext)
   {
      final String extension = ext;
      
      return (new FileFilter()
      {
         @Override public String getDescription() { return extension+" Files"; }
         @Override public boolean accept(File f) { return f.isDirectory() || f.getAbsolutePath().endsWith(extension); }
      });
   }
   
   // Variables declaration - do not modify//GEN-BEGIN:variables
   private javax.swing.JButton browseGameDataLocationButton;
   private javax.swing.JButton browsePatchArchiveLocationButton;
   private javax.swing.JTextField gameDataLocationField;
   private javax.swing.JTextField patchArchiveLocationField;
   private javax.swing.JLabel patchArchiveLocationLabel;
   private javax.swing.JButton patchGameButton;
   private javax.swing.JProgressBar patchProgressBar;
   private javax.swing.JLabel rootGameDataFolderLabel;
   private javax.swing.JTextArea statusArea;
   private javax.swing.JScrollPane statusScrollPane;
   // End of variables declaration//GEN-END:variables
}
