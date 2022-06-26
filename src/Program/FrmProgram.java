/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Program;

import java.net.Socket;
import java.util.Timer;
import zCLIENT.ChatVoiServer;
import zCLIENT.NhanThongDiep;
import zCLIENT.ScreenCapture;
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
import PACKAGES.*;
import UTILS.DataUtils;
import java.awt.*;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import zCLIENT.REMOTE.GoiManHinh;
import zCLIENT.REMOTE.ThaoTacManHinh;
import zSERVER.FrmChatVoiClient;
import zSERVER.FrmChupHinhClient;
import zSERVER.FrmGoiFile;
import zSERVER.FrmGoiLenhShell;
import zSERVER.FrmGoiThongDiep;
import zSERVER.FrmRemoteDesktop;
import zSERVER.FrmTheoDoiClient;

/**
 *
 * @author Admin
 */
public class FrmProgram extends javax.swing.JFrame implements Runnable {

    Socket socketFromServer;
    ChatVoiServer dialogChatServer;
    NhanThongDiep dialogNhanTDiep;
    boolean continueThread = true;
    String ipServer;
    String passServer;
    final int mainPortServer = 999;
    Socket socketNhanFile;
    ScreenCapture screenCapture;

    private final int mainThreadPortNumber = 999;
    private final int remoteDesktopThreadPortNumber = 1998;
    private final int theoDoiClientThreadPortNumber = 1997;
    private final int fileTransferThreadPortNumber = 1996;

    Timer timerUpdateListSocket;
    private int timeUpdateTable = 5; // second

    boolean StartServer = false;
    boolean StartClient = false;

    String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ123456789";
    String randomString = "";
    int length = 8;

    DatagramSocket serverSocket;
    // Tao cac mang byte de chua dl gui va nhan

    
    InetAddress IPAddress;

    /**
     * Creates new form FrmProgram
     */
    public FrmProgram() {
        initComponents();
        setVisible(true);
        setTitle("Remote Desktop");
//        setLocation(300, 100);
//        setLocationRelativeTo(null);
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("remote.png")));

        tbComputerInfo.setModel(new ComputerTableModel(new ArrayList()));
        tbComputerInfo.getColumnModel().getColumn(0).setMaxWidth(100);

        Random rand = new Random();
        char[] text = new char[length];
        for (int i = 0; i < length; i++) {
            text[i] = characters.charAt(rand.nextInt(characters.length()));
        }
        for (int i = 0; i < text.length; i++) {
            randomString += text[i];
        }
        try {
            ipServer = txtIP.getText();
            lblClientName.setText(InetAddress.getLocalHost().getHostName());
//            lblIPAddress.setText(ipServer);
            lblIPAddress.setText(InetAddress.getLocalHost().getHostAddress());
            lblStatus.setText("Sẵn sàng kết nối");
            System.out.print("Ip :" + InetAddress.getLocalHost().getHostAddress());
            System.out.print("\nName :" + InetAddress.getLocalHost().getHostName());

        } catch (Exception e) {
        }

    }

    private ComputerTableModel getTbModel() {
        return (ComputerTableModel) tbComputerInfo.getModel();
    }

    private void ThreadServer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (StartServer) {
                    try {
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
                    } catch (Exception e) {
                    }
                }
            }
        }).start();
    }

//    private void ThreadClient() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                if (StartClient) {
//                    try {
//                        ipServer = txtIP.getText();
//                        lblClientName.setText(InetAddress.getLocalHost().getHostName());
////            lblIPAddress.setText(ipServer);
//                        lblIPAddress.setText(InetAddress.getLocalHost().getHostAddress());
//                        lblStatus.setText("Sẵn sàng kết nối");
//                    } catch (Exception ex) {
//                    }
//                }
//            }
//        }).start();
//    }
    ////Server
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

    /////Client
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

                // Gửi màn hình
                new GoiManHinh(socketRemote, robot, rectangle);
                // Gửi event chuột/phím thao tác màn hình
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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        computerModel1 = new PACKAGES.ComputerModel();
        jPanel4 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbComputerInfo = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        lblIPAddress = new javax.swing.JLabel();
        lblStatus1 = new javax.swing.JLabel();
        txtPassword = new javax.swing.JLabel();
        lblStatus5 = new javax.swing.JLabel();
        btnGetPass = new RES.MyButton();
        jPanel3 = new javax.swing.JPanel();
        txtIP = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        txtPW = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jButtonConnect = new RES.MyButton();
        jPanel5 = new javax.swing.JPanel();
        btnChatClient = new RES.MyButton();
        btnGoiThongDiep = new RES.MyButton();
        btnTruyenTapTin = new RES.MyButton();
        jButtonChupHinhClient = new RES.MyButton();
        jButtonTheoDoiClient = new RES.MyButton();
        jButtonRemoteDesktop = new RES.MyButton();
        jButtonGoiLenhShell = new RES.MyButton();
        lblStatus2 = new javax.swing.JLabel();
        lblStatus = new javax.swing.JLabel();
        txtCnServer = new javax.swing.JLabel();
        txtCnServer1 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        lblClientName = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));
        setMinimumSize(new java.awt.Dimension(820, 420));
        setPreferredSize(new java.awt.Dimension(820, 490));
        setResizable(false);
        setSize(new java.awt.Dimension(800, 440));

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));
        jPanel4.setMinimumSize(new java.awt.Dimension(800, 400));
        jPanel4.setPreferredSize(new java.awt.Dimension(820, 520));
        jPanel4.setVerifyInputWhenFocusTarget(false);

        jTabbedPane1.setBackground(new java.awt.Color(0, 0, 0));
        jTabbedPane1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        tbComputerInfo.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "STT", "Computer", "IP", "Port"
            }
        ));
        tbComputerInfo.setSelectionBackground(new java.awt.Color(51, 102, 255));
        jScrollPane1.setViewportView(tbComputerInfo);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 334, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Computer Connected", jPanel1);

        lblIPAddress.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        lblIPAddress.setForeground(new java.awt.Color(0, 0, 255));
        lblIPAddress.setText("127.0.0.1");

        lblStatus1.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        lblStatus1.setText("IP Address");

        txtPassword.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        txtPassword.setForeground(new java.awt.Color(0, 0, 255));
        txtPassword.setText("Password");

        lblStatus5.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        lblStatus5.setText("Password");

        btnGetPass.setForeground(new java.awt.Color(255, 255, 255));
        btnGetPass.setText("Password");
        btnGetPass.setAlignmentY(0.0F);
        btnGetPass.setBorderColor(new java.awt.Color(0, 102, 255));
        btnGetPass.setBorderPainted(false);
        btnGetPass.setColor(new java.awt.Color(0, 102, 255));
        btnGetPass.setColorClick(new java.awt.Color(0, 102, 204));
        btnGetPass.setColorOver(new java.awt.Color(0, 102, 255));
        btnGetPass.setRadius(20);
        btnGetPass.setRequestFocusEnabled(false);
        btnGetPass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGetPassActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblIPAddress, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblStatus5)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnGetPass, javax.swing.GroupLayout.DEFAULT_SIZE, 128, Short.MAX_VALUE))
                    .addComponent(lblStatus1))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblStatus1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblIPAddress)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblStatus5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPassword)
                    .addComponent(btnGetPass, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        txtIP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtIPActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel1.setText("IP Server");

        txtPW.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPWActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jLabel2.setText("Password");

        jButtonConnect.setForeground(new java.awt.Color(255, 255, 255));
        jButtonConnect.setText("Connect");
        jButtonConnect.setAlignmentY(0.0F);
        jButtonConnect.setBorderColor(new java.awt.Color(0, 102, 255));
        jButtonConnect.setBorderPainted(false);
        jButtonConnect.setColor(new java.awt.Color(0, 102, 255));
        jButtonConnect.setColorClick(new java.awt.Color(0, 102, 204));
        jButtonConnect.setColorOver(new java.awt.Color(0, 102, 255));
        jButtonConnect.setRadius(20);
        jButtonConnect.setRequestFocusEnabled(false);
        jButtonConnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonConnectActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtIP, javax.swing.GroupLayout.PREFERRED_SIZE, 218, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPW, javax.swing.GroupLayout.PREFERRED_SIZE, 218, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(15, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButtonConnect, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(86, 86, 86))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtIP, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPW, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButtonConnect, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10))
        );

        jPanel5.setBackground(new java.awt.Color(204, 204, 255));
        jPanel5.setFocusable(false);

        btnChatClient.setBackground(new java.awt.Color(204, 204, 255));
        btnChatClient.setForeground(new java.awt.Color(255, 255, 255));
        btnChatClient.setIcon(new javax.swing.ImageIcon(getClass().getResource("/RES/chat-client.png"))); // NOI18N
        btnChatClient.setAlignmentY(0.0F);
        btnChatClient.setBorderColor(new java.awt.Color(204, 204, 255));
        btnChatClient.setBorderPainted(false);
        btnChatClient.setColor(new java.awt.Color(204, 204, 255));
        btnChatClient.setColorClick(new java.awt.Color(153, 204, 255));
        btnChatClient.setColorOver(new java.awt.Color(204, 204, 255));
        btnChatClient.setRadius(20);
        btnChatClient.setRequestFocusEnabled(false);
        btnChatClient.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnChatClientActionPerformed(evt);
            }
        });

        btnGoiThongDiep.setBackground(new java.awt.Color(204, 204, 255));
        btnGoiThongDiep.setForeground(new java.awt.Color(255, 255, 255));
        btnGoiThongDiep.setIcon(new javax.swing.ImageIcon(getClass().getResource("/RES/send-message.png"))); // NOI18N
        btnGoiThongDiep.setAlignmentY(0.0F);
        btnGoiThongDiep.setBorderColor(new java.awt.Color(204, 204, 255));
        btnGoiThongDiep.setBorderPainted(false);
        btnGoiThongDiep.setColor(new java.awt.Color(204, 204, 255));
        btnGoiThongDiep.setColorClick(new java.awt.Color(153, 204, 255));
        btnGoiThongDiep.setColorOver(new java.awt.Color(204, 204, 255));
        btnGoiThongDiep.setRadius(20);
        btnGoiThongDiep.setRequestFocusEnabled(false);
        btnGoiThongDiep.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGoiThongDiepActionPerformed(evt);
            }
        });

        btnTruyenTapTin.setBackground(new java.awt.Color(204, 204, 255));
        btnTruyenTapTin.setForeground(new java.awt.Color(255, 255, 255));
        btnTruyenTapTin.setIcon(new javax.swing.ImageIcon(getClass().getResource("/RES/filetransfer.png"))); // NOI18N
        btnTruyenTapTin.setAlignmentY(0.0F);
        btnTruyenTapTin.setBorderColor(new java.awt.Color(204, 204, 255));
        btnTruyenTapTin.setBorderPainted(false);
        btnTruyenTapTin.setColor(new java.awt.Color(204, 204, 255));
        btnTruyenTapTin.setColorClick(new java.awt.Color(153, 204, 255));
        btnTruyenTapTin.setColorOver(new java.awt.Color(204, 204, 255));
        btnTruyenTapTin.setRadius(20);
        btnTruyenTapTin.setRequestFocusEnabled(false);
        btnTruyenTapTin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTruyenTapTinActionPerformed(evt);
            }
        });

        jButtonChupHinhClient.setBackground(new java.awt.Color(204, 204, 255));
        jButtonChupHinhClient.setForeground(new java.awt.Color(255, 255, 255));
        jButtonChupHinhClient.setIcon(new javax.swing.ImageIcon(getClass().getResource("/RES/screenshot.png"))); // NOI18N
        jButtonChupHinhClient.setAlignmentY(0.0F);
        jButtonChupHinhClient.setBorderColor(new java.awt.Color(204, 204, 255));
        jButtonChupHinhClient.setBorderPainted(false);
        jButtonChupHinhClient.setColor(new java.awt.Color(204, 204, 255));
        jButtonChupHinhClient.setColorClick(new java.awt.Color(153, 204, 255));
        jButtonChupHinhClient.setColorOver(new java.awt.Color(204, 204, 255));
        jButtonChupHinhClient.setRadius(20);
        jButtonChupHinhClient.setRequestFocusEnabled(false);
        jButtonChupHinhClient.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonChupHinhClientActionPerformed(evt);
            }
        });

        jButtonTheoDoiClient.setBackground(new java.awt.Color(204, 204, 255));
        jButtonTheoDoiClient.setForeground(new java.awt.Color(255, 255, 255));
        jButtonTheoDoiClient.setIcon(new javax.swing.ImageIcon(getClass().getResource("/RES/tracking-computer.png"))); // NOI18N
        jButtonTheoDoiClient.setAlignmentY(0.0F);
        jButtonTheoDoiClient.setBorderColor(new java.awt.Color(204, 204, 255));
        jButtonTheoDoiClient.setBorderPainted(false);
        jButtonTheoDoiClient.setColor(new java.awt.Color(204, 204, 255));
        jButtonTheoDoiClient.setColorClick(new java.awt.Color(153, 204, 255));
        jButtonTheoDoiClient.setColorOver(new java.awt.Color(204, 204, 255));
        jButtonTheoDoiClient.setRadius(20);
        jButtonTheoDoiClient.setRequestFocusEnabled(false);
        jButtonTheoDoiClient.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonTheoDoiClientActionPerformed(evt);
            }
        });

        jButtonRemoteDesktop.setBackground(new java.awt.Color(204, 204, 255));
        jButtonRemoteDesktop.setForeground(new java.awt.Color(255, 255, 255));
        jButtonRemoteDesktop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/RES/remote.png"))); // NOI18N
        jButtonRemoteDesktop.setAlignmentY(0.0F);
        jButtonRemoteDesktop.setBorderColor(new java.awt.Color(204, 204, 255));
        jButtonRemoteDesktop.setBorderPainted(false);
        jButtonRemoteDesktop.setColor(new java.awt.Color(204, 204, 255));
        jButtonRemoteDesktop.setColorClick(new java.awt.Color(153, 204, 255));
        jButtonRemoteDesktop.setColorOver(new java.awt.Color(204, 204, 255));
        jButtonRemoteDesktop.setRadius(20);
        jButtonRemoteDesktop.setRequestFocusEnabled(false);
        jButtonRemoteDesktop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRemoteDesktopActionPerformed(evt);
            }
        });

        jButtonGoiLenhShell.setBackground(new java.awt.Color(204, 204, 255));
        jButtonGoiLenhShell.setForeground(new java.awt.Color(255, 255, 255));
        jButtonGoiLenhShell.setIcon(new javax.swing.ImageIcon(getClass().getResource("/RES/cmd.png"))); // NOI18N
        jButtonGoiLenhShell.setAlignmentY(0.0F);
        jButtonGoiLenhShell.setBorderColor(new java.awt.Color(204, 204, 255));
        jButtonGoiLenhShell.setBorderPainted(false);
        jButtonGoiLenhShell.setColor(new java.awt.Color(204, 204, 255));
        jButtonGoiLenhShell.setColorClick(new java.awt.Color(153, 204, 255));
        jButtonGoiLenhShell.setColorOver(new java.awt.Color(204, 204, 255));
        jButtonGoiLenhShell.setRadius(20);
        jButtonGoiLenhShell.setRequestFocusEnabled(false);
        jButtonGoiLenhShell.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonGoiLenhShellActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnGoiThongDiep, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnChatClient, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButtonChupHinhClient, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButtonRemoteDesktop, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnTruyenTapTin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButtonTheoDoiClient, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButtonGoiLenhShell, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(btnChatClient, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15)
                .addComponent(btnGoiThongDiep, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15)
                .addComponent(btnTruyenTapTin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(13, 13, 13)
                .addComponent(jButtonChupHinhClient, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15)
                .addComponent(jButtonTheoDoiClient, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15)
                .addComponent(jButtonRemoteDesktop, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15)
                .addComponent(jButtonGoiLenhShell, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
        );

        lblStatus2.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        lblStatus2.setText("Trạng thái:");
        lblStatus2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        lblStatus.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblStatus.setForeground(new java.awt.Color(0, 0, 255));
        lblStatus.setText("Status");
        lblStatus.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        txtCnServer.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        txtCnServer.setText("Connect Server");

        txtCnServer1.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        txtCnServer1.setText("Allow Remote Control");

        jPanel6.setBackground(new java.awt.Color(51, 102, 255));

        lblClientName.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        lblClientName.setForeground(new java.awt.Color(204, 255, 255));
        lblClientName.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblClientName.setText("MyComputer");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblClientName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lblClientName, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(58, 58, 58)
                        .addComponent(txtCnServer1)
                        .addGap(219, 219, 219)
                        .addComponent(txtCnServer, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(lblStatus2, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(53, Short.MAX_VALUE))))
            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtCnServer1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtCnServer))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(lblStatus2)
                                .addComponent(lblStatus))
                            .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap())))
        );

        jTabbedPane1.getAccessibleContext().setAccessibleName("Computer Connected");
        jPanel5.getAccessibleContext().setAccessibleParent(this);

        getContentPane().add(jPanel4, java.awt.BorderLayout.CENTER);

        getAccessibleContext().setAccessibleParent(this);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonConnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonConnectActionPerformed

        StartClient = true;
        StartServer = false;
        Thread t = new Thread(this);
        t.start();
        ipServer = txtIP.getText();
        passServer = txtPW.getText();
        btnGetPass.setEnabled(false);
        try {
           // Khởi tạo kết nối từ Client đến Server
            lblStatus.setText("Sẵn sàng kết nối");
            socketFromServer = new Socket(ipServer, mainPortServer);
            DataUtils.goiDuLieu(socketFromServer,passServer);
            
            System.out.print("Run");
            String msg = DataUtils.nhanDuLieu(socketFromServer);
            System.err.println(msg.equals("bye bye"));
           
            if(msg.equals("bye bye")){
                lblStatus.setText("Sai mật khẩu");
                JOptionPane.showMessageDialog(null, "Sai mật khẩu. Vui lòng nhập lại!");
                socketFromServer.close();
            }
            else{
                lblStatus.setText("Kết nối Server thành công");
       
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Không thể kết nối với server!");
        }
    }//GEN-LAST:event_jButtonConnectActionPerformed

    private void txtPWActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPWActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtPWActionPerformed

    private void txtIPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtIPActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtIPActionPerformed

    private void btnChatClientActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnChatClientActionPerformed
        // TODO add your handling code here:
        if (tbComputerInfo.getSelectedRowCount() == 0) {
            JOptionPane.showMessageDialog(rootPane, "Bạn chưa chọn máy để chat!");
            return;
        }
        Socket mayClient = getTbModel().getItem(tbComputerInfo.getSelectedRow());
        // Mở form chat với client đã chọn
        new Thread(new FrmChatVoiClient(mayClient)).start();

    }//GEN-LAST:event_btnChatClientActionPerformed

    private void btnGoiThongDiepActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGoiThongDiepActionPerformed
        // TODO add your handling code here:
        if (tbComputerInfo.getSelectedRowCount() == 0) {
            JOptionPane.showMessageDialog(rootPane, "Bạn chưa chọn máy để gửi!");
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
        // TODO add your handling code here:
        if (tbComputerInfo.getSelectedRowCount() == 0) {
            JOptionPane.showMessageDialog(rootPane,
                    "Bạn chưa chọn máy để gửi file!");
            return;
        }
        Socket mayClient
                = getTbModel().getItem(tbComputerInfo.getSelectedRow());
        // Gởi lệnh yêu cầu client kết nối đến socket server file transfer
        PacketTruyenFile packetTruyenFile = new PacketTruyenFile();
        packetTruyenFile.setCmd(PacketTruyenFile.CMD_KHOIDONG);
        packetTruyenFile.setMessage(String.valueOf(fileTransferThreadPortNumber));
        DataUtils.goiDuLieu(mayClient, packetTruyenFile.toString());

    }//GEN-LAST:event_btnTruyenTapTinActionPerformed

    private void jButtonChupHinhClientActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonChupHinhClientActionPerformed
        // TODO add your handling code here:
        if (tbComputerInfo.getSelectedRowCount() == 0) {
            JOptionPane.showMessageDialog(rootPane, "Bạn chưa chọn máy để chụp hình!");
            return;
        }
        Socket mayClient = getTbModel().getItem(tbComputerInfo.getSelectedRow());
        // Mở form chụp hình với client đã chọn
        new Thread(new FrmChupHinhClient(mayClient)).start();

    }//GEN-LAST:event_jButtonChupHinhClientActionPerformed

    private void jButtonTheoDoiClientActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonTheoDoiClientActionPerformed
        // TODO add your handling code here:
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

    private void jButtonRemoteDesktopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRemoteDesktopActionPerformed
        // TODO add your handling code here:
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

    private void jButtonGoiLenhShellActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonGoiLenhShellActionPerformed
        // TODO add your handling code here:
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

    private void btnGetPassActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGetPassActionPerformed
        // TODO add your handling code here:

        txtPassword.setText(randomString);
        System.out.print("\nPass : " + randomString + " \n");
        StartClient = false;
        StartServer = true;
        btnGetPass.setEnabled(false);

        jButtonConnect.setEnabled(false);
        lblStatus.setText("Đang đợi máy khách kết nối");
        ThreadServer();
        Thread server = new Thread(this);
        server.start();

    }//GEN-LAST:event_btnGetPassActionPerformed
    private void formWindowClosed(java.awt.event.WindowEvent evt) {
        continueThread = false;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void run() {

        if (StartServer) {
            try {
                final ServerSocket server = new ServerSocket(mainThreadPortNumber);
                // Phục vụ nhiều client
                while (true) {
                    Socket socket;
                    try {
                        // Nếu không dùng thread
                        // chương trình sẽ chờ 1 client đầu tiên ở đây
                        // Nếu không dùng thread
                        // chương trình sẽ chờ 1 client đầu tiên ở đây
                        socket = server.accept();
                        String msg = DataUtils.nhanDuLieu(socket);
                           
                        System.out.println("this is pass:"+msg);
                        getTbModel().addElement(socket);
                        System.out.println("Server: Đã kết nối client thứ "
                            + getTbModel().getSize());
                        if(msg.equals(randomString)){
                             DataUtils.goiDuLieu(socket, "continue connect");
                        }else{
                             DataUtils.goiDuLieu(socket, "bye bye");
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        if (StartClient) {
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

    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private RES.MyButton btnChatClient;
    private RES.MyButton btnGetPass;
    private RES.MyButton btnGoiThongDiep;
    private RES.MyButton btnTruyenTapTin;
    private PACKAGES.ComputerModel computerModel1;
    private RES.MyButton jButtonChupHinhClient;
    private RES.MyButton jButtonConnect;
    private RES.MyButton jButtonGoiLenhShell;
    private RES.MyButton jButtonRemoteDesktop;
    private RES.MyButton jButtonTheoDoiClient;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lblClientName;
    private javax.swing.JLabel lblIPAddress;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JLabel lblStatus1;
    private javax.swing.JLabel lblStatus2;
    private javax.swing.JLabel lblStatus5;
    private javax.swing.JTable tbComputerInfo;
    private javax.swing.JLabel txtCnServer;
    private javax.swing.JLabel txtCnServer1;
    private javax.swing.JTextField txtIP;
    private javax.swing.JTextField txtPW;
    private javax.swing.JLabel txtPassword;
    // End of variables declaration//GEN-END:variables
}
