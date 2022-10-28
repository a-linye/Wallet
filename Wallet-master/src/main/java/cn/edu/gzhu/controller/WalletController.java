package cn.edu.gzhu.controller;

import cn.edu.gzhu.api.WalletManager;
import cn.edu.gzhu.entity.EcKeys;
import cn.edu.gzhu.entity.KeyMnemonicDTO;
import cn.edu.gzhu.entity.TransactionDTO;
import cn.edu.gzhu.entity.TransactionReq;
import cn.edu.gzhu.result.ResponseResult;
import cn.edu.gzhu.service.impl.WalletServiceImpl;
import cn.edu.gzhu.utils.MultiPartFile;
import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import javax.annotation.Resource;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;


@RequestMapping("/wallet")
@RestController
@Api("冷热钱包")
public class WalletController extends HttpServlet {

    //链接到区块链网络
    private final static Web3j web3j = Web3j.build(new HttpService("http://172.22.110.18:8001/"));

    @Resource
    private WalletServiceImpl walletService;

    @ApiOperation(value = "获取助记词和密钥对")
    @GetMapping("/getKeyAndMnemonic")
    public ResponseResult<KeyMnemonicDTO> getKeyAndMnemonic() {

        KeyMnemonicDTO keyMnemonicDTO = new KeyMnemonicDTO();
        WalletManager manager = new WalletManager();
        //生成助记词
        String mnemonic = manager.generatorMnemonic();
        //生成密钥对
        EcKeys ecKeys = manager.mnemonicsToKeyPair(mnemonic, 0);
        keyMnemonicDTO.setEcKeys(ecKeys);
        keyMnemonicDTO.setMnemonic(mnemonic);
        return ResponseResult.success(keyMnemonicDTO);
    }

    @ApiOperation("通过助记词恢复密钥")
    @PostMapping("/restoreKeys")
    public ResponseResult<EcKeys> restoreKeys(@RequestParam() String mnemonic) {
        WalletManager manager = new WalletManager();
        EcKeys ecKeys = manager.mnemonicsToKeyPair(mnemonic, 0);
        return ResponseResult.success(ecKeys);
    }

    @ApiOperation("热钱包请求生成离线交易")
    @PostMapping("/transactionOrder")
    public ResponseResult transactionOrder(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam String amount
    ) throws IOException {
        //获取交易手续费
        BigInteger gasPrice = web3j.ethGasPrice().send().getGasPrice();
        BigInteger gasLimit = new BigInteger("210000");

        //金额的单位转换
        BigInteger amountWei = Convert.toWei(amount, Convert.Unit.ETHER).toBigInteger();
        //查询地址交易编号
        BigInteger nonce = web3j.ethGetTransactionCount("0x" + from, DefaultBlockParameterName.PENDING).send().getTransactionCount();
        //离线交易期间是否需要限制用户的交易操作
        Map<String, Object> map = new HashMap<>();
        map.put("data", "");
        map.put("gasLimit", gasLimit);
        map.put("gasPrice", gasPrice);
        map.put("nonce", nonce);
        map.put("value", amountWei);
        map.put("to", "0x" + to);

        try {
            MultiPartFile.uploadDataToFile("transaction.txt", map);
            return ResponseResult.success();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseResult.fail("生成交易失败");
    }

    @ApiOperation("热钱包下载交易信息")
    @PostMapping("/downLoad")
    public ResponseResult downLoad(HttpServletResponse response) throws IOException {
        try {
            MultiPartFile.download("transaction.txt", response);
            return ResponseResult.success();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseResult.fail("交易信息下载失败");
    }

    @ApiOperation(value = "冷钱包对离线交易签名")
    @PostMapping("/signTransaction")
    public ResponseResult signTransaction(
            @RequestParam MultipartFile file,
            @RequestParam String privateKey,
            HttpServletResponse response
    ) {
        BufferedReader bufferedReader = null;
        if (file != null) {
            try {
                bufferedReader = new BufferedReader(new InputStreamReader(file.getInputStream()));
                String lineTxt = bufferedReader.readLine();
                if (lineTxt != null) {
                    JSONObject jsonObject = JSONObject.parseObject(lineTxt);
                    //私钥生成证书
                    Credentials credentials = Credentials.create(privateKey);
                    System.out.println(jsonObject.getBigInteger("nonce"));
                    System.out.println(jsonObject.getBigInteger("gasPrice"));
                    System.out.println(jsonObject.getBigInteger("gasLimit"));
                    System.out.println(jsonObject.getString("to"));
                    System.out.println(jsonObject.getBigInteger("value"));
                    System.out.println(jsonObject.getString("data"));
                    RawTransaction rawTransaction = RawTransaction.createTransaction(
                            jsonObject.getBigInteger("nonce"),
                            jsonObject.getBigInteger("gasPrice"),
                            jsonObject.getBigInteger("gasLimit"),
                            jsonObject.getString("to"),
                            jsonObject.getBigInteger("value"),
                            jsonObject.getString("data"));
                    //签名交易
                    byte[] signMessage = TransactionEncoder.signMessage(rawTransaction, credentials);

                    //附件形式下载
                    response.setHeader("content-disposition", "attachment;fileName=" + URLEncoder.encode("transaction.txt", "UTF-8"));
                    response.setContentType("form/data;charset=utf-8");
                    response.getOutputStream().write(signMessage);
                    response.flushBuffer();
                    return ResponseResult.success("签名成功");
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return ResponseResult.fail("签名失败");
    }


    @ApiOperation(value = "热钱包广播交易")
    @PostMapping("/transaction")
    public ResponseResult transaction(@RequestParam MultipartFile file) {

        try {
            byte[] bytes = file.getBytes();
            //byte[] bytes = FileReaderUtils.readOnce(file);
            //广播交易
            String transactionHash = web3j.ethSendRawTransaction(Numeric.toHexString(bytes)).sendAsync().get().getTransactionHash();
            if (transactionHash != null) {
                //交易成功,保存交易信息
//                EthGetBalance ethGetBlance = web3j.ethGetBalance("0x" + address, DefaultBlockParameterName.LATEST).send();
//                String balance = Convert.fromWei(new BigDecimal(ethGetBlance.getBalance()), Convert.Unit.ETHER).toPlainString();
//                transactionDTO.setBalance(balance);
//                transactionDTO.setStatus(1);
                Optional<Transaction> transactions = web3j.ethGetTransactionByHash(transactionHash).send().getTransaction();
                if (transactions.isPresent()) {
                    Transaction transaction = transactions.get();
                    TransactionDTO transactionDTO = new TransactionDTO();
                    BeanUtil.copyProperties(transaction,transactionDTO);
                    transactionDTO.setTxHash(transaction.getHash());
                    transactionDTO.setTxTo(transaction.getTo());
                    transactionDTO.setTxFrom(transaction.getFrom());
                    transactionDTO.setTxValue(transaction.getValue().toString());
                    boolean save = walletService.save(transactionDTO);
                    if (save){
                        return ResponseResult.success();
                    }
                }
            } else {
                return ResponseResult.fail("交易广播失败");
            }

        } catch (UnsupportedEncodingException | FileNotFoundException e) {
            System.out.println("找不到指定的文件!");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("读取文件内容时出错!");
            e.printStackTrace();
        } catch (ExecutionException | InterruptedException e) {
            System.out.println("交易失败!");
            throw new RuntimeException(e);
        }
        return ResponseResult.fail("交易广播失败");
    }

    @ApiOperation(value = "查询余额接口，用于测试以太坊测试网络是否有效果")
    @GetMapping("/getAccountIfo")
    public ResponseResult<String> getAccountIfo(String address) throws IOException {
        EthGetBalance ethGetBlance = web3j.ethGetBalance("0x" + address, DefaultBlockParameterName.LATEST).send();
        String balance = Convert.fromWei(new BigDecimal(ethGetBlance.getBalance()), Convert.Unit.ETHER).toPlainString();
        return ResponseResult.success(balance);
    }


    @ApiOperation(value = "分页查询查询交易列表")
    @PostMapping("/getTransaction")
    public ResponseResult getTransaction(@RequestBody TransactionReq transactionReq){
        IPage<TransactionDTO> data = walletService.selectAll(transactionReq);
        return ResponseResult.success(data);
    }

}
