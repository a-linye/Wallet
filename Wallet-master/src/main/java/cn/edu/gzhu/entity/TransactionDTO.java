package cn.edu.gzhu.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
@TableName("transaction")
@SuppressWarnings("serial")
public class TransactionDTO  extends Model<TransactionDTO> {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("tx_hash")
    @ApiModelProperty(name="hash",value = "交易哈希")
    private String TxHash;

    @TableField("nonce")
    @ApiModelProperty(name="nonce",value = "账户交易次数")
    private String nonce;

    @TableField("blockHash")
    @ApiModelProperty(name="blockHash",value = "区块哈希")
    private String blockHash;

    @TableField("blockNumber")
    @ApiModelProperty(name="blockNumber",value = "区块号")
    private String blockNumber;

    @TableField("transactionIndex")
    @ApiModelProperty(name="transactionIndex",value = "交易索引")
    private String transactionIndex;

    @TableField("tx_from")
    @ApiModelProperty(name="from",value = "交易发起者")
    private String TxFrom;

    @TableField("tx_to")
    @ApiModelProperty(name="to",value = "交易接收者")
    private String TxTo;

    @TableField("tx_value")
    @ApiModelProperty(name="value",value = "转账金额")
    private String TxValue;

    @TableField("gasPrice")
    @ApiModelProperty(name="gasPrice",value = "手续费价格")
    private String gasPrice;

    @TableField("gas")
    @ApiModelProperty(name="gas",value = "用户提交的手续费")
    private String gas;

    @TableField("input")
    @ApiModelProperty(name="input",value = "交易的附加数据")
    private String input;

    @TableField("creates")
    @ApiModelProperty(name="creates",value = "creates")
    private String creates;

    @TableField("publicKey")
    @ApiModelProperty(name="publicKey",value = "公钥")
    private String publicKey;

    @TableField("raw")
    @ApiModelProperty(name="raw",value = "raw")
    private String raw;

    @TableField("r")
    @ApiModelProperty(name="r",value = "交易签名的值")
    private String r;

    @TableField("s")
    @ApiModelProperty(name="s",value = "交易签名的值")
    private String s;

    @TableField("v")
    @ApiModelProperty(name="v",value = "交易签名的值")
    private long v;

    @TableField("create_time")
    @ApiModelProperty(name="createTime",value = "交易创建时间")
    private Date createTime;
}
