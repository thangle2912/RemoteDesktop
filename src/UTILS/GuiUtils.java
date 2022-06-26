package UTILS;

import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JDialog;

/**
 *
 * @author Admin
 */
public class GuiUtils {
    public static void showDialog(JDialog jDialog, boolean model){
        Toolkit tk = Toolkit.getDefaultToolkit();
        Dimension screenSize = tk.getScreenSize();
        int screenHeight = screenSize.height;
        int screenWidth = screenSize.width;
        jDialog.setSize(screenWidth / 2, screenHeight / 2);
        jDialog.setLocation(screenWidth / 4, screenHeight / 4);
    }
}
