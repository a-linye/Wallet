## 以太坊钱包开发,创建的钱包的流程为:
- 1、随机生成一组助记词_BIP39协议
- 2、生成 seed_BIP39协议
- 3、生成 master key
- 4、生成 child key
- 5、第一组child key即m/44'/60'/0'/0/0 得到私钥,keystore及地址

## Reference
- 1.https://github.com/NovaCrypto/BIP32
- 2.https://github.com/NovaCrypto/BIP39
- 3.https://github.com/NovaCrypto/BIP44