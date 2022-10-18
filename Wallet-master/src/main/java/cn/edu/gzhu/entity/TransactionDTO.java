package cn.edu.gzhu.entity;

import lombok.Data;

@Data
public class TransactionDTO {

    String balance;//账户余额
    int status;//0_交易失败 1_交易成功
    String massage;
}
