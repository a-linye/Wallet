package cn.edu.gzhu.entity;

import lombok.Data;

@Data
public class TransactionReq extends BaseQueryDTO{
    private String hash;
    private String nonce;
    private String blockHash;
    private String blockNumber;
    private String address;
}
