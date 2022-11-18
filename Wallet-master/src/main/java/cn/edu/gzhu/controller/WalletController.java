package cn.edu.gzhu.controller;

import cn.edu.gzhu.api.WalletManager;
import cn.edu.gzhu.entity.*;
import cn.edu.gzhu.result.ResponseResult;
import cn.edu.gzhu.service.impl.WalletServiceImpl;
import cn.edu.gzhu.utils.Aes;
import cn.edu.gzhu.utils.MultiPartFile;
import cn.edu.gzhu.utils.Shamir1;
import cn.edu.gzhu.utils.ToAscii;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
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
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
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
                    BeanUtil.copyProperties(transaction, transactionDTO);
                    transactionDTO.setTxHash(transaction.getHash());
                    transactionDTO.setTxTo(transaction.getTo());
                    transactionDTO.setTxFrom(transaction.getFrom());
                    transactionDTO.setTxValue(transaction.getValue().toString());
                    boolean save = walletService.save(transactionDTO);
                    if (save) {
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
    public ResponseResult getTransaction(@RequestBody TransactionReq transactionReq) {
        IPage<TransactionDTO> data = walletService.selectAll(transactionReq);
        return ResponseResult.success(data);
    }

    @ApiOperation(value = "门限密钥加密")
    @PostMapping("/encryption")
    public ResponseResult encryption(@RequestParam String mnemonics, @RequestParam String password, HttpServletResponse response) {
        if (password.length() != 16) {
            return ResponseResult.fail("密码长度错误");
        }
        try {
            String aesPwd = Aes.Encrypt(mnemonics, password);
            String asciiPwd = ToAscii.strToAscii(aesPwd);
            final BigInteger secret = new BigInteger(asciiPwd);

            final Random random = new Random();
            final BigInteger prime = new BigInteger("156804681138953738841981891602908576685172682831672945866413335334809525974827777178164263961707052367620733619560801175565197765638236062095225289241828660391778546509568968600952385561433647554511465765336647559608051805912842383899213360743516446641290400761825923628321376402980469567748721757862451355359");
            final SecretShare[] shares = Shamir1.split(secret, 6, 10, prime, random);
            String output = shares[0].toString() + "\n" + shares[1].toString() + "\n" + shares[2].toString() + "\n" + shares[3].toString() + "\n" + shares[4].toString() + "\n" + shares[5].toString() + "\n" + shares[6].toString() + "\n" + shares[7].toString() + "\n" + shares[8].toString() + "\n" + shares[9].toString();

            //将输出保存到文件中
            //附件形式下载
            response.setHeader("content-disposition", "attachment;fileName=" + URLEncoder.encode("shares.txt", "UTF-8"));
            response.getOutputStream().write(output.getBytes(StandardCharsets.UTF_8));
            response.flushBuffer();
            System.out.println(output);
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @ApiOperation(value = "门限密钥解密")
    @PostMapping("/decrypt")
    public ResponseResult decrypt(@RequestBody Password password) {

        SecretShare[] shares = new SecretShare[6];


        String password1 = password.getPassword1();
        String password2 = password.getPassword2();
        String password3 = password.getPassword3();
        String password4 = password.getPassword4();
        String password5 = password.getPassword5();
        String password6 = password.getPassword6();

        if ( StrUtil.isAllNotEmpty(password1, password2, password3,password4,password5,password6)){
            return ResponseResult.fail("参数错误");
        }
        Integer n1 = password.getN1();
        Integer n2 = password.getN2();
        Integer n3 = password.getN3();
        Integer n4 = password.getN4();
        Integer n5 = password.getN5();
        Integer n6 = password.getN6();

        shares[0] = new SecretShare(n1, new BigInteger(password1));
        shares[1] = new SecretShare(n2, new BigInteger(password2));
        shares[2] = new SecretShare(n3, new BigInteger(password3));
        shares[3] = new SecretShare(n4, new BigInteger(password4));
        shares[4] = new SecretShare(n5, new BigInteger(password5));
        shares[5] = new SecretShare(n6, new BigInteger(password6));


        try {
            final BigInteger prime = new BigInteger("15680468113895373884198189160290857668517268" +
                    "28316729458664133353348095259748277771781642639617070523676207336195608011755651" +
                    "97765638236062095225289241828660391778546509568968600952385561433647554511465" +
                    "76533664755960805180591284238389921336074351644664129040076182592362832137640298" +
                    "0469567748721757862451355359");

            BigInteger result = Shamir1.combine(shares, prime);

            String jiemi1 = result.toString();
            System.out.println(jiemi1);

            //将门限定解出来的密钥转化为分割的ASCII
            char[] charArr = jiemi1.toCharArray();
            int[] inputResult = new int[1000];
            int j = 0;
            int length = 0;
            for (int l = 0; l < charArr.length; l++) {
                if ((int) (charArr[l] - '0') != 1) {
                    inputResult[j] = (10 * (int) (charArr[l] - '0')) + (int) (charArr[l + 1] - '0');
                    l++;
                    j++;
                } else if ((int) (charArr[l] - '0') == 1) {
                    inputResult[j] = (100 * (int) (charArr[l] - '0')) + (10 * (int) (charArr[l + 1] - '0')) + (int) (charArr[l + 2] - '0');
                    l = l + 2;
                    j++;
                }

            }

            for (int m = 0; m < inputResult.length; m++) {
                if (inputResult[m] != 0) {
                    length++;
                }
            }

            System.out.println(inputResult);
            System.out.println(length);
            String input = "";
            for (int n = 0; n < length; n++) {
                if (n < length - 1) {
                    input += inputResult[n] + " ";
                } else {
                    input += inputResult[n] + "";
                }
            }
            System.out.println(input);

            String s1 = ToAscii.asciiToToStr(input);
            System.out.println(s1);
            return ResponseResult.success(s1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseResult.fail("参数错误");
    }

    @ApiOperation(value = "恢复助记词")
    @GetMapping("/recover")
    public ResponseResult recover(@RequestParam String sSrc,@RequestParam String password) {
        String decrypt = null;
        try {
            decrypt = Aes.Decrypt(sSrc,password);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ResponseResult.success(decrypt);
    }
}
