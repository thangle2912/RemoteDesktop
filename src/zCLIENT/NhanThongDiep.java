/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zCLIENT;

import UTILS.ObjectUtils;
import java.net.Socket;
import java.util.Date;
import javax.swing.JDialog;

/**
 *
 * @author Admin
 */
public class NhanThongDiep extends JDialog {

    Socket _mayServer;
    public NhanThongDiep(Socket mayServer) {
        _mayServer = mayServer;
        initComponents();
        setLocationRelativeTo(null);
        
    }
    public void nhanDuLieu(String cmd, String msg){
        txtMessage.append("Server ("+
             ObjectUtils.formatDate(new Date(), "dd-mm hh:mm:ss")+"): "+msg+"\n");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        txtMessage = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Thông điệp từ máy chủ");

        txtMessage.setBackground(new java.awt.Color(0, 102, 255));
        txtMessage.setColumns(20);
        txtMessage.setFont(new java.awt.Font("Monospaced", 0, 14)); // NOI18N
        txtMessage.setForeground(new java.awt.Color(255, 255, 255));
        txtMessage.setRows(5);
        jScrollPane1.setViewportView(txtMessage);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 512, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea txtMessage;
    // End of variables declaration//GEN-END:variables
}
