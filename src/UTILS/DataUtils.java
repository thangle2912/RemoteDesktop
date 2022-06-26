package UTILS;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 *
 * @author Admin
 */
public class DataUtils {

    public static String nhanDuLieu(Socket sender) {
        try {
            BufferedReader inputServer =
                    new BufferedReader(new InputStreamReader(sender.getInputStream()));
            String msg = inputServer.readLine();
            System.out.println("Nhan may "
                    + sender.getInetAddress().getHostAddress()+":"+sender.getPort()
                    + " > "+ msg);
            return msg;
        } catch (IOException ex) {
            return "";
        }
    }

    public static boolean goiDuLieu(Socket receiver, String msg) {
        PrintWriter outputToClient;
        try {
            outputToClient = new PrintWriter(receiver.getOutputStream(), true);
            outputToClient.println(msg);
            System.out.println("Gui may "
                    + receiver.getInetAddress().getHostAddress()+":"+receiver.getPort()
                    + " > "+msg);
            return true;
        } catch (IOException ex) {
            return false;
        }
    }
 
    public static String layTenMay(Socket socket) {
        return socket.getInetAddress().getHostName();
    }

    public static String layIPMay(Socket socket) {
        return socket.getInetAddress().getHostAddress();
    }
    public static boolean checkConnectClosed(Socket socket){
        PrintWriter out;
        try {
            out = new PrintWriter(socket.getOutputStream(), true);
            out.print(" ");
            return out.checkError();
        } catch (IOException ex) {
        }
        return true;
    }
}
