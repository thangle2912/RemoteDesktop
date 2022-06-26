/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zSERVER;

import PACKAGES.PacketChupAnh;
import UTILS.DataUtils;
import UTILS.ObjectUtils;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.Date;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Admin
 */
public class FrmChupHinhClient extends javax.swing.JDialog implements Runnable {

    Socket socket;
    BufferedImage img;
    private boolean isContinued = true;
    public FrmChupHinhClient(Socket socket) {
        this.socket = socket;
        initComponents();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    public void run() {
        byte[] bytes;
        ObjectInputStream ois;
        while (isContinued) {
            try {
                ois = new ObjectInputStream(socket.getInputStream());
                bytes = (byte[]) ois.readObject();
                if (bytes != null) {
                    showScreenShot(bytes);
                }
            } catch (Exception ex) {
                isContinued = false;
                ex.printStackTrace();
            }
        }
    }

    public void showScreenShot(byte[] bytes) {
        try {
            img = ImageIO.read(new ByteArrayInputStream(bytes));
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    try {
                        Image image = img.getScaledInstance(jPanelDesktopRemote.getWidth(), jPanelDesktopRemote.getHeight(), Image.SCALE_SMOOTH);
                        if (image == null) {
                            return;
                        }
                        // Hiển thị ảnh lên panel
                        Graphics graphics = jPanelDesktopRemote.getGraphics();
                        graphics.drawImage(image, 0, 0, jPanelDesktopRemote.getWidth(), jPanelDesktopRemote.getHeight(), jPanelDesktopRemote);
                    } catch (Exception ex) {
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        btnChupHinh = new javax.swing.JButton();
        btnLuu = new javax.swing.JButton();
        jPanelDesktopRemote = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        btnChupHinh.setText("Chụp hình");
        btnChupHinh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChupHinhActionPerformed(evt);
            }
        });

        btnLuu.setText("Lưu");
        btnLuu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLuuActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(315, 315, 315)
                .addComponent(btnChupHinh)
                .addGap(33, 33, 33)
                .addComponent(btnLuu, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(363, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnChupHinh)
                    .addComponent(btnLuu))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        getContentPane().add(jPanel1, java.awt.BorderLayout.PAGE_START);

        jPanelDesktopRemote.setFocusable(false);

        javax.swing.GroupLayout jPanelDesktopRemoteLayout = new javax.swing.GroupLayout(jPanelDesktopRemote);
        jPanelDesktopRemote.setLayout(jPanelDesktopRemoteLayout);
        jPanelDesktopRemoteLayout.setHorizontalGroup(
            jPanelDesktopRemoteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 869, Short.MAX_VALUE)
        );
        jPanelDesktopRemoteLayout.setVerticalGroup(
            jPanelDesktopRemoteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 487, Short.MAX_VALUE)
        );

        getContentPane().add(jPanelDesktopRemote, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnChupHinhActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChupHinhActionPerformed
        PacketChupAnh p = new PacketChupAnh();
        DataUtils.goiDuLieu(socket, p.toString());
    }//GEN-LAST:event_btnChupHinhActionPerformed

    private void btnLuuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLuuActionPerformed
        try {
            String filename = "ScreeenShot_" + socket.getInetAddress().getHostName()
                    + "_" + ObjectUtils.formatDate(new Date(), "ddMMyyyyhhmmss")
                    + ".png";

            JFileChooser chooser = new JFileChooser(filename);
            chooser.setFileFilter(new FileNameExtensionFilter("Images file", "png"));
            chooser.setSelectedFile(new File(filename));
            chooser.setMultiSelectionEnabled(false);

            if (chooser.showSaveDialog(null) != JFileChooser.APPROVE_OPTION) {
                return;
            }
            // retrieve image
            File outputfile = chooser.getSelectedFile();
            ImageIO.write(img, "png", outputfile);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }//GEN-LAST:event_btnLuuActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        isContinued = false;
    }//GEN-LAST:event_formWindowClosing

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnChupHinh;
    private javax.swing.JButton btnLuu;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanelDesktopRemote;
    // End of variables declaration//GEN-END:variables
}