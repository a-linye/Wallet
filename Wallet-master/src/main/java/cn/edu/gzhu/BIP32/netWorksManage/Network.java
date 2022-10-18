package cn.edu.gzhu.BIP32.netWorksManage;

/**
 * @author Touale
 * @description Network
 * @date 2022/1/27 17:17
 */
public interface Network {
    int getPrivateVersion();

    int getPublicVersion();

    byte p2pkhVersion();

    byte p2shVersion();
}
