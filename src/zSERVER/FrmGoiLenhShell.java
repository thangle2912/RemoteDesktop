/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zSERVER;

import PACKAGES.PacketShell;
import PACKAGES.PacketTin;
import UTILS.DataUtils;
import java.awt.event.KeyEvent;
import java.net.Socket;

/**
 *
 * @author Admin
 */
public class FrmGoiLenhShell extends javax.swing.JDialog implements Runnable{

    Socket _mayClient;
    Thread _thread;
    PacketShell _pkShell;
    public FrmGoiLenhShell(Socket mayClient) {
        this._mayClient = mayClient;
        initComponents();
        setTitle(DataUtils.layTenMay(mayClient) +" ("+
                DataUtils.layIPMay(mayClient)+")");
        _thread = new Thread(this);
        _pkShell = new PacketShell();
        this.setLocationRelativeTo(null);
        txtInput.requestFocusInWindow();
    }
     @Override
    public void run() {
        // Đảm bảo sau khi gởi shell xong 1 client
        // còn gởi các lần tiếp theo nữa
        while(true){
            // Nếu không dùng thread
            // chương trình sẽ chờ nhận dữ liệu client ở đây
            // kết quả chương trình sẽ bị treo do đợi
            String msg = DataUtils.nhanDuLieu(_mayClient);
            if(msg != null && !msg.isEmpty()){
                hienThiMessage(msg);
            }
        }
    }
    public void khoiDong(){
        _thread.start();
    }
    
    private void hienThiMessage(String msg){
        PacketTin pkgTin = new PacketTin();
        pkgTin.phanTichMessage(msg);
        if(pkgTin.isId(PacketShell.ID)){
            txtMessages.append(DataUtils.layTenMay(_mayClient)+"> "
                    +pkgTin.getMessage()+"\n");
        }
    }

   
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        txtMessages = new javax.swing.JTextArea();
        txtInput = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Chat với client");
        setType(java.awt.Window.Type.POPUP);

        txtMessages.setEditable(false);
        txtMessages.setBackground(new java.awt.Color(0, 0, 0));
        txtMessages.setColumns(20);
        txtMessages.setFont(new java.awt.Font("Monospaced", 0, 14)); // NOI18N
        txtMessages.setForeground(new java.awt.Color(255, 255, 255));
        txtMessages.setLineWrap(true);
        txtMessages.setRows(5);
        txtMessages.setAutoscrolls(false);
        jScrollPane1.setViewportView(txtMessages);

        txtInput.setBackground(new java.awt.Color(102, 102, 255));
        txtInput.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtInput.setForeground(new java.awt.Color(255, 255, 255));
        txtInput.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtInputKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(txtInput)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 723, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 375, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(txtInput, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtInputKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtInputKeyReleased
       if(evt.getKeyCode() == KeyEvent.VK_ENTER){
        _pkShell.khoiTao("",txtInput.getText());
        txtMessages.append("Server> "+txtInput.getText()+"\n");
        txtInput.setText("");
        DataUtils.goiDuLieu(_mayClient,_pkShell.toString());
       }
    }//GEN-LAST:event_txtInputKeyReleased

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField txtInput;
    private javax.swing.JTextArea txtMessages;
    // End of variables declaration//GEN-END:variables

    
}
