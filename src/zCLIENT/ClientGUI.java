/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package zCLIENT;

import PACKAGES.*;
import UTILS.DataUtils;
import java.awt.*;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import zCLIENT.REMOTE.GoiManHinh;
import zCLIENT.REMOTE.ThaoTacManHinh;

/**
 *
 * @author Admin
 */
public class ClientGUI extends javax.swing.JFrame implements Runnable {

    Socket socketFromServer;
    ChatVoiServer dialogChatServer;
    NhanThongDiep dialogNhanTDiep;
    boolean continueThread = true;
    String ipServer;
    final int mainPortServer = 999;
    Socket socketNhanFile;
    ScreenCapture screenCapture;

    public ClientGUI() {
        try {
            initComponents();
            setVisible(true);
            ipServer = txtIP.getText();
            lblClientName.setText(InetAddress.getLocalHost().getHostName()
                    + " (" + InetAddress.getLocalHost().getHostAddress() + ")");
//            lblIPAddress.setText(ipServer);
            lblIPAddress.setText(InetAddress.getLocalHost().getHostAddress());
            lblStatus.setText("Đang chờ kết nối đến server...");
        } catch (Exception ex) {
        }
    }

    @Override
    public void run() {
        while (continueThread) {
            try {
                String msg = DataUtils.nhanDuLieu(socketFromServer);
                if (msg != null && !msg.isEmpty()) {
                    xuLyDuLieuTrungTam(msg);
                }
            } catch (Exception e) {
                // e.printStackTrace();
            }
        }
    }
    //<editor-fold defaultstate="collapsed" desc="Xử lý dữ liệu trung tâm">

    private void xuLyDuLieuTrungTam(String msg) throws UnknownHostException, IOException {
        PacketTin pkTin = new PacketTin();
        pkTin.phanTichMessage(msg);
        // Thực hiện các câu lệnh từ Server
        if (pkTin.isId(PacketChat.ID)) {
            if (dialogChatServer == null) {
                // Khởi tạo
                dialogChatServer = new ChatVoiServer(socketFromServer);
            }
            // Gởi dữ liệu đã phân tích
            dialogChatServer.nhanDuLieu(pkTin.getCmd(), pkTin.getMessage().toString());
            if (!dialogChatServer.isVisible()) {
                dialogChatServer.setVisible(true);
            }
        } else if (pkTin.isId(PacketThongDiep.ID)) {
            if (dialogNhanTDiep == null) {
                // Khởi tạo
                dialogNhanTDiep = new NhanThongDiep(socketFromServer);
            }
            // Gởi dữ liệu đã phân tích
            dialogNhanTDiep.nhanDuLieu(pkTin.getCmd(), pkTin.getMessage().toString());
            if (!dialogNhanTDiep.isVisible()) {
                dialogNhanTDiep.setVisible(true);
            }
        } else if (pkTin.isId(PacketTruyenFile.ID)) {
            int port = Integer.parseInt(pkTin.getMessage().toString());
            // Tạo socket nhận file với port đã gởi qua
            socketNhanFile = new Socket(ipServer, port);
            xuLyNhanFile();
        } else if (pkTin.isId(PacketShell.ID)) {
            xuLyLenhShell(pkTin.getMessage());
        } else if (pkTin.isId(PacketRemoteDesktop.ID)) {
            xuLyRemoteDesktop(pkTin);
        } else if (pkTin.isId(PacketTheoDoiClient.ID)) {
            xuLyTheoDoiClient(pkTin);
        } else if (pkTin.isId(PacketChupAnh.ID)) {
            if (screenCapture == null) {
                screenCapture = new ScreenCapture(socketFromServer);
            }
            screenCapture.goiAnh();
        }
        System.err.println(pkTin.toString());
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Remote desktop">
    private void xuLyRemoteDesktop(PacketTin pkTin) {
        int port = Integer.parseInt(pkTin.getMessage().toString());
        // Dùng để xử lý màn hình
        Robot robot;
        // Dùng dể tính độ phân giải và kích thước màn hình cho client
        Rectangle rectangle;
        try {
            // Tạo socket nhận remote với port đã gởi qua
            final Socket socketRemote
                    = new Socket(ipServer, port);
            try {
                // Lấy màn hình mặc định của hệ thống
                GraphicsEnvironment gEnv = GraphicsEnvironment.getLocalGraphicsEnvironment();
                GraphicsDevice gDev = gEnv.getDefaultScreenDevice();

                // Lấy dimension màn hình
                Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
                rectangle = new Rectangle(dim);

                // Chuẩn bị robot thao tác màn hình
                robot = new Robot(gDev);

                // Gởi màn hình
                new GoiManHinh(socketRemote, robot, rectangle);
                // Gởi event chuột/phím thao tác màn hình
                new ThaoTacManHinh(socketRemote, robot);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Theo dõi client">
    private void xuLyTheoDoiClient(PacketTin pkTin) {
        int port = Integer.parseInt(pkTin.getMessage().toString());
        // Dùng để xử lý màn hình
        Robot robot;

        try {
            // Tạo socket nhận remote với port đã gởi qua
            final Socket socketRemote
                    = new Socket(ipServer, port);
            try {
                // Lấy màn hình mặc định của hệ thống
                GraphicsEnvironment gEnv = GraphicsEnvironment.getLocalGraphicsEnvironment();
                GraphicsDevice gDev = gEnv.getDefaultScreenDevice();

                // Chuẩn bị robot thao tác màn hình
                robot = new Robot(gDev);
                // Gởi màn hình 
                new GoiManHinh(socketRemote, robot);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        } catch (IOException ex) {
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Xử lý lệnh shell">
    private void xuLyLenhShell(String commandMsg) {
        PacketShell packetShell = new PacketShell();
        try {
            Process process = Runtime.getRuntime().exec("cmd /c " + commandMsg + "\n");
            BufferedReader input
                    = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line;

            while ((line = input.readLine()) != null) {
                // Send packet
                if (line.equals("")) {
                    continue;
                }
                packetShell.setMessage(line.trim());
                // wait for traffic
                Thread.sleep(100);
                DataUtils.goiDuLieu(socketFromServer, packetShell.toString());
            }
        } catch (Exception ex) {
            packetShell.setMessage("Error: " + ex.getMessage());
            DataUtils.goiDuLieu(socketFromServer, packetShell.toString());
        }

    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Xử lý nhận file">
    private void xuLyNhanFile() throws IOException {
        JFileChooser chooser = new JFileChooser();
        chooser.setMultiSelectionEnabled(false);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (chooser.showSaveDialog(null) != JOptionPane.OK_OPTION) {
            return;
        }
        int bytesRead;
        InputStream in = socketNhanFile.getInputStream();
        DataInputStream clientData = new DataInputStream(in);
        System.err.println("C[Nhận File]: bắt đầu chờ nhận file....");
        String fileName = clientData.readUTF();
        System.err.println("C[Nhận File]: đã thấy tên file: " + fileName);
        String fullPath = chooser.getSelectedFile().getPath() + "\\" + fileName;
        try {
            OutputStream output = new FileOutputStream(fullPath);

            System.err.println("C[Nhận File]: bắt đầu nhận file: " + fileName);
            long size = clientData.readLong();
            byte[] buffer = new byte[3024];
            while (size > 0 && (bytesRead
                    = clientData.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
                output.write(buffer, 0, bytesRead);
                size -= bytesRead;
            }
            output.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.err.println("C[Nhận File]: đã nhận xong: " + fileName);
    }
    //</editor-fold>

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblStatus4 = new javax.swing.JLabel();
        lblClientName = new javax.swing.JLabel();
        lblStatus1 = new javax.swing.JLabel();
        lblIPAddress = new javax.swing.JLabel();
        lblStatus2 = new javax.swing.JLabel();
        lblStatus = new javax.swing.JLabel();
        btnThoat = new javax.swing.JToggleButton();
        jLabel1 = new javax.swing.JLabel();
        txtIP = new javax.swing.JTextField();
        jButtonConnect = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

        lblStatus4.setText("Client:");

        lblClientName.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblClientName.setForeground(new java.awt.Color(0, 0, 255));
        lblClientName.setText("MyComputer");

        lblStatus1.setText("IP Address:");

        lblIPAddress.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblIPAddress.setForeground(new java.awt.Color(0, 0, 255));
        lblIPAddress.setText("127.0.0.1");

        lblStatus2.setText("Trạng thái:");

        lblStatus.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblStatus.setForeground(new java.awt.Color(0, 0, 255));
        lblStatus.setText("Status");

        btnThoat.setText("Thoát");
        btnThoat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnThoatActionPerformed(evt);
            }
        });

        jLabel1.setText("IP Server:");

        txtIP.setText("anonymous");

        jButtonConnect.setText("Kết nối");
        jButtonConnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonConnectActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(lblStatus1)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(lblIPAddress, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(layout.createSequentialGroup()
                                    .addGap(4, 4, 4)
                                    .addComponent(lblStatus2)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(lblStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(7, 7, 7)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel1)
                                    .addComponent(lblStatus4))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblClientName, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(txtIP, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jButtonConnect))))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(135, 135, 135)
                        .addComponent(btnThoat)))
                .addGap(0, 26, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(14, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtIP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonConnect))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblStatus4)
                    .addComponent(lblClientName))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblStatus1)
                    .addComponent(lblIPAddress))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblStatus2)
                    .addComponent(lblStatus))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnThoat)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonConnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonConnectActionPerformed
        ipServer = txtIP.getText();
        try {
            // Khởi tạo kết nối từ Client đến Server
            lblStatus.setText("Đang chờ kết nối đến server...");
            socketFromServer = new Socket(ipServer, mainPortServer);
            lblStatus.setText("Đã kết nối server thành công.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Không thể kết nối với server!");
        }
    }//GEN-LAST:event_jButtonConnectActionPerformed

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        continueThread = false;
    }//GEN-LAST:event_formWindowClosed

    private void btnThoatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThoatActionPerformed
        dispose();
    }//GEN-LAST:event_btnThoatActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton btnThoat;
    private javax.swing.JButton jButtonConnect;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel lblClientName;
    private javax.swing.JLabel lblIPAddress;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblStatus1;
    private javax.swing.JLabel lblStatus2;
    private javax.swing.JLabel lblStatus4;
    private javax.swing.JTextField txtIP;
    // End of variables declaration//GEN-END:variables
}
