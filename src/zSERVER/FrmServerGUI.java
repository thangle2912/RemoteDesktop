package zSERVER;

import PACKAGES.ComputerTableModel;
import PACKAGES.PacketRemoteDesktop;
import PACKAGES.PacketTheoDoiClient;
import PACKAGES.PacketTruyenFile;
import UTILS.DataUtils;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JOptionPane;

/**
 *
 * @author Admin
 */
public class FrmServerGUI extends javax.swing.JFrame implements Runnable {
    
    private final int mainThreadPortNumber = 999;
    private final int remoteDesktopThreadPortNumber = 998;
    private final int theoDoiClientThreadPortNumber = 997;
    private final int fileTransferThreadPortNumber = 996;
    
    Timer timerUpdateListSocket;
    private int timeUpdateTable = 5; // second
    public FrmServerGUI() {
        initComponents();
        setLocation(300, 100);
        tbComputerInfo.setModel(new ComputerTableModel(new ArrayList()));
        tbComputerInfo.getColumnModel().getColumn(0).setMaxWidth(100);
        setVisible(true);
        // Cập nhật list socket sau mỗi timeUpdateTable giây
        timerUpdateListSocket = new Timer();
        timerUpdateListSocket.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                getTbModel().updateAllElement();
            }
        }, timeUpdateTable * 1000, timeUpdateTable * 1000);
        // server lắng nghe remote desktop
        runThreadRemoteDesktop(); 
        // server lắng nghe gởi/ nhận file
        runThreadTransferFile();
        // server lắng nghe theo dõi client
        runThreadTheoDoiClient();
    }
    
    private ComputerTableModel getTbModel() {
        return (ComputerTableModel) tbComputerInfo.getModel();
    }


    @Override
    public void run() {
       // chat, gởi thông điệp, gởi lệnh shell
       try {
            final ServerSocket server = new ServerSocket(mainThreadPortNumber);
            // Phục vụ nhiều client
            while (true) {
                Socket socket;
                try {
                    // Nếu không dùng thread
                    // chương trình sẽ chờ 1 client đầu tiên ở đây
                    socket = server.accept();
                    getTbModel().addElement(socket);
                    System.out.println("Server: Đã kết nối client thứ "
                            + getTbModel().getSize());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }   
    }
     
    //<editor-fold defaultstate="collapsed" desc="Thread transfer file">
    private void runThreadTransferFile() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final ServerSocket server = new ServerSocket(fileTransferThreadPortNumber);
                    // Phục vụ nhiều client
                    while (true) {
                        Socket socket;
                        try {
                            socket = server.accept();
                            new Thread(new FrmGoiFile(socket)).start();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Thread remote desktop">

    private void runThreadRemoteDesktop() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final ServerSocket server = new ServerSocket(remoteDesktopThreadPortNumber);
                    // Phục vụ nhiều client
                    while (true) {
                        Socket socket;
                        try {
                            socket = server.accept();
                            new Thread(new FrmRemoteDesktop(socket)).start();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Thread theo dõi client">
    private void runThreadTheoDoiClient() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final ServerSocket server = new ServerSocket(theoDoiClientThreadPortNumber);
                    // Phục vụ nhiều client
                    while (true) {
                        Socket socket;
                        try {
                            socket = server.accept();
                            new Thread(new FrmTheoDoiClient(socket)).start();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
    }
    //</editor-fold>
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToolBar1 = new javax.swing.JToolBar();
        btnChatClient = new javax.swing.JButton();
        btnGoiThongDiep = new javax.swing.JButton();
        btnTruyenTapTin = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        jButtonTheoDoiClient = new javax.swing.JButton();
        jButtonChupHinhClient = new javax.swing.JButton();
        jButtonRemoteDesktop = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        jButtonGoiLenhShell = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbComputerInfo = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        lblTrangThai = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jToolBar1.setRollover(true);

        btnChatClient.setIcon(new javax.swing.ImageIcon(getClass().getResource("/RES/chat-client.png"))); // NOI18N
        btnChatClient.setText("Chat Client");
        btnChatClient.setFocusable(false);
        btnChatClient.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnChatClient.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnChatClient.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChatClientActionPerformed(evt);
            }
        });
        jToolBar1.add(btnChatClient);

        btnGoiThongDiep.setIcon(new javax.swing.ImageIcon(getClass().getResource("/RES/send-message.png"))); // NOI18N
        btnGoiThongDiep.setText("Gởi thông điệp");
        btnGoiThongDiep.setFocusable(false);
        btnGoiThongDiep.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnGoiThongDiep.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnGoiThongDiep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGoiThongDiepActionPerformed(evt);
            }
        });
        jToolBar1.add(btnGoiThongDiep);

        btnTruyenTapTin.setIcon(new javax.swing.ImageIcon(getClass().getResource("/RES/filetransfer.png"))); // NOI18N
        btnTruyenTapTin.setText("Truyền tập tin");
        btnTruyenTapTin.setFocusable(false);
        btnTruyenTapTin.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnTruyenTapTin.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnTruyenTapTin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTruyenTapTinActionPerformed(evt);
            }
        });
        jToolBar1.add(btnTruyenTapTin);
        jToolBar1.add(jSeparator1);

        jButtonTheoDoiClient.setIcon(new javax.swing.ImageIcon(getClass().getResource("/RES/tracking-computer.png"))); // NOI18N
        jButtonTheoDoiClient.setText("Theo dõi client");
        jButtonTheoDoiClient.setFocusable(false);
        jButtonTheoDoiClient.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonTheoDoiClient.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonTheoDoiClient.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTheoDoiClientActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonTheoDoiClient);

        jButtonChupHinhClient.setIcon(new javax.swing.ImageIcon(getClass().getResource("/RES/screenshot.png"))); // NOI18N
        jButtonChupHinhClient.setText("Chụp hình client");
        jButtonChupHinhClient.setFocusable(false);
        jButtonChupHinhClient.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonChupHinhClient.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonChupHinhClient.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonChupHinhClientActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonChupHinhClient);

        jButtonRemoteDesktop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/RES/remote.png"))); // NOI18N
        jButtonRemoteDesktop.setText("Điều khiển client");
        jButtonRemoteDesktop.setFocusable(false);
        jButtonRemoteDesktop.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonRemoteDesktop.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonRemoteDesktop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRemoteDesktopActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonRemoteDesktop);
        jToolBar1.add(jSeparator2);

        jButtonGoiLenhShell.setIcon(new javax.swing.ImageIcon(getClass().getResource("/RES/cmd.png"))); // NOI18N
        jButtonGoiLenhShell.setText("Gởi lệnh Shell");
        jButtonGoiLenhShell.setFocusable(false);
        jButtonGoiLenhShell.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonGoiLenhShell.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonGoiLenhShell.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonGoiLenhShellActionPerformed(evt);
            }
        });
        jToolBar1.add(jButtonGoiLenhShell);

        tbComputerInfo.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "STT", "Tên máy", "IP"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(tbComputerInfo);
        tbComputerInfo.getColumnModel().getColumn(0).setResizable(false);
        tbComputerInfo.getColumnModel().getColumn(0).setPreferredWidth(50);
        tbComputerInfo.getColumnModel().getColumn(1).setResizable(false);
        tbComputerInfo.getColumnModel().getColumn(1).setPreferredWidth(250);
        tbComputerInfo.getColumnModel().getColumn(2).setResizable(false);
        tbComputerInfo.getColumnModel().getColumn(2).setPreferredWidth(250);

        lblTrangThai.setText("Trạng thái");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTrangThai)
                .addContainerGap(558, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(lblTrangThai)
                .addGap(0, 1, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane1)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jTabbedPane1.addTab("Thông tin máy tính trong mạng LAN", jPanel1);

        jMenu1.setText("Quản lý");

        jMenuItem1.setText("Thoát");
        jMenu1.add(jMenuItem1);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jTabbedPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnChatClientActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChatClientActionPerformed
        if (tbComputerInfo.getSelectedRowCount() == 0) {
            JOptionPane.showMessageDialog(rootPane, "Bạn chưa chọn máy để chat!");
            return;
        }
        Socket mayClient = getTbModel().getItem(tbComputerInfo.getSelectedRow());
        // Mở form chat với client đã chọn
        new Thread(new FrmChatVoiClient(mayClient)).start();
    }//GEN-LAST:event_btnChatClientActionPerformed
    
    private void btnGoiThongDiepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGoiThongDiepActionPerformed
        if (tbComputerInfo.getSelectedRowCount() == 0) {
            JOptionPane.showMessageDialog(rootPane, "Bạn chưa chọn máy để gởi!");
            return;
        }
        int[] rowsSelected = tbComputerInfo.getSelectedRows();
        List<Socket> dsMayClient = new ArrayList();
        for (int i : rowsSelected) {
            dsMayClient.add(getTbModel().getItem(i));
        }
        // Mở form chat với các client đã chọn
        FrmGoiThongDiep goiThongDiep = new FrmGoiThongDiep(dsMayClient);
        goiThongDiep.setVisible(true);
    }//GEN-LAST:event_btnGoiThongDiepActionPerformed
   
    private void btnTruyenTapTinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTruyenTapTinActionPerformed
        if (tbComputerInfo.getSelectedRowCount() == 0) {
            JOptionPane.showMessageDialog(rootPane,
                    "Bạn chưa chọn máy để gởi file!");
            return;
        }
        Socket mayClient =
                getTbModel().getItem(tbComputerInfo.getSelectedRow());
        // Gởi lệnh yêu cầu client kết nối đến socket server file transfer
        PacketTruyenFile packetTruyenFile = new PacketTruyenFile();
        packetTruyenFile.setCmd(PacketTruyenFile.CMD_KHOIDONG);
        packetTruyenFile.setMessage(String.valueOf(fileTransferThreadPortNumber));
        DataUtils.goiDuLieu(mayClient, packetTruyenFile.toString()); 
    }//GEN-LAST:event_btnTruyenTapTinActionPerformed
    
    private void jButtonGoiLenhShellActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonGoiLenhShellActionPerformed
        if (tbComputerInfo.getSelectedRowCount() == 0) {
            JOptionPane.showMessageDialog(rootPane, "Bạn chưa chọn máy để gởi lệnh shell!");
            return;
        }
        Socket mayClient = getTbModel().getItem(tbComputerInfo.getSelectedRow());
        // Mở form gởi lệnh shell đến client đã chọn
        FrmGoiLenhShell goiLenhShell = new FrmGoiLenhShell(mayClient);
        goiLenhShell.khoiDong();
        goiLenhShell.setVisible(true);
    }//GEN-LAST:event_jButtonGoiLenhShellActionPerformed
    
    private void jButtonRemoteDesktopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRemoteDesktopActionPerformed
        if (tbComputerInfo.getSelectedRowCount() == 0) {
            JOptionPane.showMessageDialog(rootPane, "Bạn chưa chọn máy để điều khiển!");
            return;
        }
        Socket mayClient = getTbModel().getItem(tbComputerInfo.getSelectedRow());
        // Gởi lệnh yêu cầu client kết nối đến socket server remote desktop
        PacketRemoteDesktop pk = new PacketRemoteDesktop();
        pk.setCmd(PacketRemoteDesktop.CMD_KHOIDONG);
        pk.setMessage(String.valueOf(remoteDesktopThreadPortNumber));
        DataUtils.goiDuLieu(mayClient, pk.toString());
    }//GEN-LAST:event_jButtonRemoteDesktopActionPerformed
    
    private void jButtonTheoDoiClientActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTheoDoiClientActionPerformed
        if (tbComputerInfo.getSelectedRowCount() == 0) {
            JOptionPane.showMessageDialog(rootPane, "Bạn chưa chọn máy để điều khiển!");
            return;
        }
        Socket mayClient = getTbModel().getItem(tbComputerInfo.getSelectedRow());
        // Gởi lệnh yêu cầu client kết nối đến socket server remote desktop
        PacketTheoDoiClient pk = new PacketTheoDoiClient();
        pk.setCmd(PacketTheoDoiClient.CMD_KHOIDONG);
        pk.setMessage(String.valueOf(theoDoiClientThreadPortNumber));
        DataUtils.goiDuLieu(mayClient, pk.toString());
        
    }//GEN-LAST:event_jButtonTheoDoiClientActionPerformed

    private void jButtonChupHinhClientActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonChupHinhClientActionPerformed
         if (tbComputerInfo.getSelectedRowCount() == 0) {
            JOptionPane.showMessageDialog(rootPane, "Bạn chưa chọn máy để chụp hình!");
            return;
        }
        Socket mayClient = getTbModel().getItem(tbComputerInfo.getSelectedRow());
        // Mở form chụp hình với client đã chọn
        new Thread(new FrmChupHinhClient(mayClient)).start();
    }//GEN-LAST:event_jButtonChupHinhClientActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnChatClient;
    private javax.swing.JButton btnGoiThongDiep;
    private javax.swing.JButton btnTruyenTapTin;
    private javax.swing.JButton jButtonChupHinhClient;
    private javax.swing.JButton jButtonGoiLenhShell;
    private javax.swing.JButton jButtonRemoteDesktop;
    private javax.swing.JButton jButtonTheoDoiClient;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel lblTrangThai;
    private javax.swing.JTable tbComputerInfo;
    // End of variables declaration//GEN-END:variables
}
