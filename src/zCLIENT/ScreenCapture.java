package zCLIENT;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Robot;
import java.io.ObjectOutputStream;
import java.net.Socket;
import zCLIENT.REMOTE.ChupManHinh;

/**
 *
 * @author Admin
 */
public class ScreenCapture{
    
    Socket socket;
    ObjectOutputStream oos;
    ChupManHinh cScreenShot;
    public ScreenCapture(Socket socket) {
        this.socket = socket;
    }

    public void goiAnh() {
        try {
          cScreenShot = new ChupManHinh(1.0f);
          oos =  new ObjectOutputStream(socket.getOutputStream());
          // Lấy màn hình mặc định của hệ thống
          GraphicsEnvironment gEnv = GraphicsEnvironment.getLocalGraphicsEnvironment();
          GraphicsDevice gDev = gEnv.getDefaultScreenDevice();
          
          // Chuẩn bị robot thao tác màn hình
          Robot robot = new Robot(gDev);
          
          oos.writeObject(cScreenShot.execute(robot));
          oos.flush();
        } catch (Exception ex) {
           ex.printStackTrace();
        }
    }
}
