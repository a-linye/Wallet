package cn.edu.gzhu.entity;

import lombok.Data;

import java.math.BigInteger;

@Data
public class SignTransaction {
    private BigInteger nonce;//交易编号
    private BigInteger gasPrice;//手续费
    private BigInteger gasLimit;//手续费上限
    private String to;//转账接收放
    private BigInteger value;//转账金额
    private String data;
    private String privateKey;//私钥
}
