package cn.edu.gzhu.entity;

import lombok.Data;

@Data
public class KeyMnemonicDTO {

    private String mnemonic;

    private EcKeys ecKeys;

}
