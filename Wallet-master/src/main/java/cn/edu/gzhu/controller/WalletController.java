package cn.edu.gzhu.controller;

import cn.edu.gzhu.api.WalletManager;
import cn.edu.gzhu.entity.EcKeys;
import cn.edu.gzhu.entity.KeyMnemonicDTO;
import cn.edu.gzhu.entity.TransactionDTO;
import cn.edu.gzhu.utils.FileReaderUtils;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import javax.servlet.http.HttpServlet;
import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;


@RequestMapping("/wallet")
@RestController
@Api("冷热钱包")
@Controller
public class WalletController extends HttpServlet {

    //链接到区块链网络
    private final static Web3j web3j = Web3j.build(new HttpService("http://172.22.110.18:8001/"));

    @ApiOperation(value = "获取助记词和密钥对")
    @GetMapping("/getKeyAndMnemonic")
    public KeyMnemonicDTO getKeyAndMnemonic() {

        KeyMnemonicDTO keyMnemonicDTO = new KeyMnemonicDTO();
        WalletManager manager = new WalletManager();
        //生成助记词
        String mnemonic = manager.generatorMnemonic();
        //生成密钥对
        EcKeys ecKeys = manager.mnemonicsToKeyPair(mnemonic, 0);
        keyMnemonicDTO.setEcKeys(ecKeys);
        keyMnemonicDTO.setMnemonic(mnemonic);
        return keyMnemonicDTO;
    }

    @ApiOperation("通过助记词恢复密钥")
    @PostMapping("/restoreKeys")
    public EcKeys restoreKeys(@RequestParam() String mnemonic) {
        WalletManager manager = new WalletManager();
        EcKeys ecKeys = manager.mnemonicsToKeyPair(mnemonic, 0);
        return ecKeys;
    }

    @ApiOperation("热钱包请求生成离线交易")
    @PostMapping("/transactionOrder")
    public void transactionOrder(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam String amount,
            @RequestParam String url
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
        map.put("gasPrice", gasPrice);
        map.put("nonce", nonce);
        map.put("value", amountWei);
        map.put("to", "0x" + to);

        FileWriter fw = null;
        JSONObject object = new JSONObject(map);
        String jsonStr = object.toString();
        try {
            File file = new File(url+"transaction.txt");
            if (!file.exists()) {
                file.createNewFile();
            }
            fw = new FileWriter(url+"transaction.txt");
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(jsonStr);
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fw.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    @ApiOperation(value = "冷钱包对离线交易签名")
    @PostMapping("/signTransaction")
    public void signTransaction(@RequestParam String filePath,
                                @RequestParam String privateKey
    ) {
        //读取交易文件
        File file = new File(filePath);
        //FileWriter fw = null;
        FileOutputStream fileOutputStream = null;
        try {
            if (file.isFile() && file.exists()) {
                InputStreamReader read = new InputStreamReader(new FileInputStream(file), "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = bufferedReader.readLine();
                if (lineTxt != null) {
                    JSONObject jsonObject = JSONObject.parseObject(lineTxt);
                    //私钥生成证书
                    Credentials credentials = Credentials.create(privateKey);
                    System.out.println(jsonObject.getBigInteger("nonce"));
                    System.out.println(jsonObject.getBigInteger("gasPrice"));
                    System.out.println(jsonObject.getBigInteger("gasLimit"));
                    System.out.println(jsonObject.getString("to"));
                    System.out.println( jsonObject.getBigInteger("value"));
                    System.out.println( jsonObject.getString("data"));
                    RawTransaction rawTransaction = RawTransaction.createTransaction(
                            jsonObject.getBigInteger("nonce"),
                            jsonObject.getBigInteger("gasPrice"),
                            jsonObject.getBigInteger("gasLimit"),
                            jsonObject.getString("to"),
                            jsonObject.getBigInteger("value"),
                            jsonObject.getString("data"));
                    //签名交易
                    byte[] signMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
                    fileOutputStream = new FileOutputStream(file);
                    fileOutputStream.write(signMessage);
                }
            }
        } catch (UnsupportedEncodingException | FileNotFoundException e) {
            System.out.println("找不到指定的文件!");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("读取文件内容时出错!");
            e.printStackTrace();
        }finally {
            try {
                //fw.close();
                fileOutputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    @ApiOperation(value = "热钱包广播交易")
    @GetMapping("/transaction")
    public TransactionDTO transaction(@RequestParam String filePath, @RequestParam String address) {

        TransactionDTO transactionDTO = new TransactionDTO();

        try {
            byte[] bytes = FileReaderUtils.readOnce(filePath);
            //广播交易
            String transactionHash = web3j.ethSendRawTransaction(Numeric.toHexString(bytes)).sendAsync().get().getTransactionHash();
            if (transactionHash != null) {
                //交易成功,更新余额
                EthGetBalance ethGetBlance = web3j.ethGetBalance("0x" + address, DefaultBlockParameterName.LATEST).send();
                String balance = Convert.fromWei(new BigDecimal(ethGetBlance.getBalance()), Convert.Unit.ETHER).toPlainString();
                transactionDTO.setBalance(balance);
                transactionDTO.setStatus(1);
                transactionDTO.setMassage("交易成功");
            }else {
                transactionDTO.setBalance("0");
                transactionDTO.setStatus(0);
                transactionDTO.setMassage("交易失败");
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
        return transactionDTO;
    }

    @ApiOperation(value = "查询余额接口，用于测试以太坊测试网络是否有效果")
    @GetMapping("/getAccountIfo")
    public String getAccountIfo(String address) throws IOException {
        EthGetBalance ethGetBlance = web3j.ethGetBalance("0x" + address, DefaultBlockParameterName.LATEST).send();
        String balance = Convert.fromWei(new BigDecimal(ethGetBlance.getBalance()), Convert.Unit.ETHER).toPlainString();
        return balance;
    }


    @ApiOperation(value = "查询余额接口，用于测试以太坊测试网络是否有效果")
    @GetMapping("/getCurrentBalance")
    public String getCurrentBalance(String address) throws IOException {
        EthGetBalance ethGetBlance = web3j.ethGetBalance("0x" + address, DefaultBlockParameterName.LATEST).send();
        String balance = Convert.fromWei(new BigDecimal(ethGetBlance.getBalance()), Convert.Unit.ETHER).toPlainString();
        return balance;
    }

    @ApiOperation(value = "查询交易信息")
    @GetMapping("/getTransaction")
    public void getTransaction(String address) throws IOException {
        //String url = "https://api-ropsten.etherscan.io/api?module=account&action=txlist&address=0xE003d9942B56B3da1A30349A7EC9ba29CEb12360&startblock=0&endblock=99999999&page=1&offset=10&sort=asc&apikey=YourApiKeyToken";
        //String url = "https://api.etherscan.io/api?module=block&action=getblockreward&blockno=1&apikey=YourApiKeyToken";


        String url = "https://api.etherscan.io/api?module=block&action=getblockreward&blockno=1&apikey=YourApiKeyToken";
        RestTemplate restTemplate = new RestTemplate();
        //String tx = restTemplate.getForObject("https://api.etherscan.io/api?module=block&action=getblockreward&blockno=1&apikey=YourApiKeyToken", String.class);
        String body = restTemplate.getForEntity(url, String.class).getBody();
        System.out.println(body);

//        //查询区块哈希
//        EtherScanApi api = new EtherScanApi(EthNetwork.GORLI);
//        int i = api.block().hashCode();
//        return i;
    }

}
