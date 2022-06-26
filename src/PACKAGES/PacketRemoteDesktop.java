package PACKAGES;

/**
 *
 * @author Nhom 1142014_1142066
 */
public class PacketRemoteDesktop extends PacketTin {
    public static final String ID = "remotedesktop";
    public static final String CMD_CHAPNHAN = "chapnhan";
    public static final String CMD_KHOIDONG = "khoidong";
    public static final String CMD_HOANTAT = "hoantat";
    public PacketRemoteDesktop() {
        setId(ID);
    }
}
