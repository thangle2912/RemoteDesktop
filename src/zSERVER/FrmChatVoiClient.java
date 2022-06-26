/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zSERVER;

import PACKAGES.PacketChat;
import PACKAGES.PacketTin;
import UTILS.DataUtils;
import java.net.Socket;

/**
 *
 * @author Admin
 */
public class FrmChatVoiClient extends javax.swing.JDialog implements Runnable{

    boolean isContinued = true;
    Socket _mayClient;
    PacketChat _pkgChat;
    public FrmChatVoiClient(Socket mayClient) {
        this._mayClient = mayClient;
        initComponents();
        setTitle(DataUtils.layTenMay(mayClient) +" ("+
                DataUtils.layIPMay(mayClient)+")");
        setVisible(true);
        _pkgChat = new PacketChat();
    }
     @Override
    public void run() {
        // Đảm bảo sau khi chat xong 1 client
        // còn chat các lần tiếp theo nữa
        while(isContinued){
            // Nếu không dùng thread
            // chương trình sẽ chờ nhận dữ liệu client ở đây
            // kết quả chương trình sẽ bị treo do đợi
            String msg = DataUtils.nhanDuLieu(_mayClient);
            if(msg != null && !msg.isEmpty()){
                hienThiMessage(msg);
            }
        }
    }

    private void hienThiMessage(String msg){
        PacketTin pkgTin = new PacketTin();
        pkgTin.phanTichMessage(msg);
        if(pkgTin.isId(PacketChat.ID)){
            txtMessages.append(DataUtils.layTenMay(_mayClient)+": "
                    +pkgTin.getMessage()+"\n");
        }
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
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        txtMessages.setEditable(false);
        txtMessages.setColumns(20);
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
        _pkgChat.khoiTao("",txtInput.getText());
        txtMessages.append("Server: "+txtInput.getText()+"\n");
        txtInput.setText("");
        DataUtils.goiDuLieu(_mayClient,_pkgChat.toString());
    }//GEN-LAST:event_btnSendActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
       isContinued = false;
    }//GEN-LAST:event_formWindowClosing

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSend;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextArea txtInput;
    private javax.swing.JTextArea txtMessages;
    // End of variables declaration//GEN-END:variables

    
}
