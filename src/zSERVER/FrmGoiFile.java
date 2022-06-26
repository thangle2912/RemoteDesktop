/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zSERVER;

import java.io.*;
import java.net.Socket;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author Admin
 */
public class FrmGoiFile extends javax.swing.JDialog implements Runnable {

    Socket socketToClient;

    public FrmGoiFile(Socket mayKhach) {
        initComponents();
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        socketToClient = mayKhach;
    }

    @Override
    public void run() {
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnGoi = new javax.swing.JButton();
        txtFile = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        btnGoi.setText("Gửi");
        btnGoi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGoiActionPerformed(evt);
            }
        });

        txtFile.setEditable(false);

        jLabel1.setText("Tập tin:");

        jLabel2.setText("Chọn tập tin và ấn nút gởi");

        jButton2.setText("Tìm");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtFile, javax.swing.GroupLayout.PREFERRED_SIZE, 305, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel2))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(175, 175, 175)
                        .addComponent(btnGoi, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(34, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(21, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtFile, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(jButton2))
                .addGap(18, 18, 18)
                .addComponent(btnGoi)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnGoiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGoiActionPerformed

        try {
            if (txtFile.getText().isEmpty()) {
                JOptionPane.showConfirmDialog(null, "Bạn chưa chọn tập tin để gởi!");
                return;
            }
            File myFile = new File(txtFile.getText());
            byte[] mybytearray = new byte[(int) myFile.length()];
            FileInputStream fis = new FileInputStream(myFile);
            BufferedInputStream bis = new BufferedInputStream(fis);

            DataInputStream dis = new DataInputStream(bis);
            dis.readFully(mybytearray, 0, mybytearray.length);

            OutputStream os = socketToClient.getOutputStream();

            DataOutputStream dos = new DataOutputStream(os);
            String filename = chooser.getSelectedFile().getName();
            System.err.println("S[Gởi file]: Chuẩn bị gởi file: " + filename);
            dos.writeUTF(filename);
            dos.writeLong(mybytearray.length);
            System.err.println("S[Gởi file]: Bắt đầu gởi file!");
            dos.write(mybytearray, 0, mybytearray.length);
            dos.flush();
            System.err.println("S[Gởi file]: Hoàn tất gởi file!");
            socketToClient.close();
            dispose();
        } catch (Exception ex) {
        }

    }//GEN-LAST:event_btnGoiActionPerformed
    JFileChooser chooser;
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        chooser = new JFileChooser(".");
        int status = chooser.showOpenDialog(null);
        if (status == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            txtFile.setText(f.getPath());
        }
    }//GEN-LAST:event_jButton2ActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnGoi;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JTextField txtFile;
    // End of variables declaration//GEN-END:variables
}
