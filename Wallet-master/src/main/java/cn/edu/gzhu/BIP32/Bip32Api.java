package cn.edu.gzhu.BIP32;


import cn.edu.gzhu.BIP32.extended.ExtendedPrivateKey;
import cn.edu.gzhu.BIP32.extended.ExtendedPublicKey;
import cn.edu.gzhu.BIP32.netWorksManage.netWorks.Bitcoin;

import static cn.edu.gzhu.BIP32.util.ByteUtils.hex2Bytes;

import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;

/**
 * @author Touale
 * @description Applicaiton
 * @date 2022/1/27 17:15
 */
public class Bip32Api {
    public static void main(String[] args) {

        /**
         * 126e057d3066eda1d9e3bf5587a7436c8ab62c27918b552495ba751f036d2eff213b4a86663f20933ca7eb53c72e24dac8e18a85ec23afed96fb499df0016603
         * xprvA34iPJ4KaLi7pVq7YoNcdsSnZjnWj8DmtwJ5456ZwkoVBt5Lw9na5gpUWqFzSotyPJFD9FP5ugg2kdt2kA6UNZrQv3vNWoaXMqxDYTmmAYZ
         * xpub6G44nobDQiGR2yuaepud11PX7md18awdGADfrTWBW6LU4gQVUh6pdV8xN63gPmSJkQ9AK5R79Qhf6WX7eWAy9MmHL3qZdP4fGLG5DjwCnMq
         * privateKey:487d0f6b54b34067f8a5db746ceb6efc48086981a3adea6595f8063af37e6ca6
         * publicKey:0228861b0aa65d9b20b262150d6a703fcf7a2542238383d089fabb73e9ac157e55
         * address:6e66199123d9b561dee0a92980fb0c9c5534106e
         */
        ExtendedPrivateKey rootKey = ExtendedPrivateKey.fromSeed(hex2Bytes(
                        "126e057d3066eda1d9e3bf5587a7436c8ab62c27918b552495ba751f036d2eff213b4a86663f20933ca7eb53c72e24dac8e18a85ec23afed96fb499df0016603"),
                Bitcoin.MAIN_NET);
        ExtendedPrivateKey childPri = rootKey.derive("m/44'/60'/0'/0/0");
        ExtendedPublicKey childPub = childPri.neuter();
        System.out.println(childPri.extendedBase58());
        System.out.println(childPub.extendedBase58());


        // 4. get key pair
        byte[] privateKeyBytes = childPri.getKey();
        ECKeyPair keyPair = ECKeyPair.create(privateKeyBytes);

        // we 've gotten what we need
        String privateKey = childPri.getPrivateKey();
        String publicKey = childPri.neuter().getPublicKey();
        String address = Keys.getAddress(keyPair);

        System.out.println("privateKey:"+privateKey);
        System.out.println("publicKey:"+publicKey);
        System.out.println("address:"+ address);

    }
}
