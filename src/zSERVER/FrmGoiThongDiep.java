/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zSERVER;

import PACKAGES.PacketThongDiep;
import UTILS.DataUtils;
import UTILS.ObjectUtils;
import java.net.Socket;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Admin
 */
public class FrmGoiThongDiep extends javax.swing.JDialog{

    List<Socket>  _dsMayClient;
    PacketThongDiep _PkgThongDiep;
    public FrmGoiThongDiep(List<Socket> _dsMayClient) {
        this._dsMayClient = _dsMayClient;
        initComponents();
        setTitle("Gởi thông điệp đến client");
        _PkgThongDiep = new PacketThongDiep();
        this.setLocationRelativeTo(null);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        txtMessages = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtInput = new javax.swing.JTextArea();
        btnSend = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Chat với client");
        setType(java.awt.Window.Type.POPUP);

        txtMessages.setColumns(20);
        txtMessages.setEditable(false);
        txtMessages.setRows(5);
        jScrollPane1.setViewportView(txtMessages);

        txtInput.setColumns(20);
        txtInput.setRows(5);
        jScrollPane2.setViewportView(txtInput);

        btnSend.setText("Gửi");
        btnSend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSendActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnSend)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 226, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(btnSend, javax.swing.GroupLayout.DEFAULT_SIZE, 39, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    
    private void btnSendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSendActionPerformed
        _PkgThongDiep.khoiTao("",txtInput.getText());
        txtMessages.append("Server ("+
             ObjectUtils.formatDate(new Date(), "dd-mm hh:mm:ss")
                +"): "+txtInput.getText()+"\n");
        txtInput.setText("");
        for(Socket s : _dsMayClient){
            DataUtils.goiDuLieu(s,_PkgThongDiep.toString());
        }
       
    }//GEN-LAST:event_btnSendActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSend;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea txtInput;
    private javax.swing.JTextArea txtMessages;
    // End of variables declaration//GEN-END:variables

   

    
}
