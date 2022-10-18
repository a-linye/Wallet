package cn.edu.gzhu.entity;

import lombok.Data;

/**
 * @author Touale
 * @description EcKeys
 * @date 2022/1/28 16:36
 */
@Data
public class EcKeys {
    private String privateKey;
    private String publicKey;
    private String address;

    public String getAddress() {
        return address;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }
}
