package cn.edu.gzhu.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigInteger;

@Data
public class RawTransactionDTO implements Serializable {
    private BigInteger nonce;
    private BigInteger gasPrice;
    private BigInteger gasLimit;
    private String to;
    private BigInteger value;
    private String data;
}
