package zSERVER.REMOTE;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class ClientScreenReciever extends Thread {

    private ObjectInputStream cObjectInputStream = null;
    private JPanel cPanel = null;
    private boolean continueLoop = true;

    public ClientScreenReciever(ObjectInputStream ois, JPanel p) {
        cObjectInputStream = ois;
        cPanel = p;
        // Thread start
        start();
    }

    public void stopReceive() {
        // Báo thread dừng khi tắt remote desktop
        continueLoop = false;
    }

    @Override
    public void run() {
        try {
            while (continueLoop) {
                // Nhận ảnh trả về từ client
                byte[] bytes = (byte[]) cObjectInputStream.readObject();
                System.out.println("New image recieved");
                // Hiển thị ảnh lên panel
                showScreenShot(bytes);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void showScreenShot(byte[] bytes) throws IOException {
        try {
            final BufferedImage img = ImageIO.read(new ByteArrayInputStream(bytes));
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    try {
                        Image image = img.getScaledInstance(cPanel.getWidth(), cPanel.getHeight(), Image.SCALE_FAST);
                        if (image == null) {
                            return;
                        }
                        // Hiển thị ảnh lên panel
                        Graphics graphics = cPanel.getGraphics();
                        graphics.drawImage(image, 0, 0, cPanel.getWidth(), cPanel.getHeight(), cPanel);
                    } catch (Exception ex) {
                    }
                }
            });
        } catch (Exception ex) {
        }
    }
}
