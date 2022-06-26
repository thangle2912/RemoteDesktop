/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zSERVER;

import java.io.ObjectInputStream;
import java.net.Socket;
import zSERVER.REMOTE.ClientScreenReciever;

/**
 *
 * @author Admin
 */
public class FrmTheoDoiClient extends javax.swing.JDialog implements Runnable {

    Socket socket;
    ClientScreenReciever clientScreenReciever;
    public FrmTheoDoiClient(Socket socket) {
        this.socket = socket;
        initComponents();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    
    @Override
    public void run() {
        //Used to read screenshots and client screen dimension
        ObjectInputStream ois = null;
        try {
            //Read client screen dimension
            ois = new ObjectInputStream(socket.getInputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        //Start recieveing screenshots
        clientScreenReciever = new ClientScreenReciever(ois, jPanelDesktopRemote);
        
    
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelDesktopRemote = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jPanelDesktopRemote.setFocusable(false);
        jPanelDesktopRemote.setPreferredSize(new java.awt.Dimension(800, 600));

        javax.swing.GroupLayout jPanelDesktopRemoteLayout = new javax.swing.GroupLayout(jPanelDesktopRemote);
        jPanelDesktopRemote.setLayout(jPanelDesktopRemoteLayout);
        jPanelDesktopRemoteLayout.setHorizontalGroup(
            jPanelDesktopRemoteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 834, Short.MAX_VALUE)
        );
        jPanelDesktopRemoteLayout.setVerticalGroup(
            jPanelDesktopRemoteLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 474, Short.MAX_VALUE)
        );

        getContentPane().add(jPanelDesktopRemote, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        clientScreenReciever.stopReceive();
    }//GEN-LAST:event_formWindowClosing

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanelDesktopRemote;
    // End of variables declaration//GEN-END:variables
}