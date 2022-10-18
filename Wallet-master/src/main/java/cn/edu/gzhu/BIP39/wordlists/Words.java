package cn.edu.gzhu.BIP39.wordlists;

/**
 * @author Touale
 * @description Words
 * @date 2022/1/26 21:01
 */
public enum Words {
    TWELVE(128),
    FIFTEEN(160),
    EIGHTEEN(192),
    TWENTY_ONE(224),
    TWENTY_FOUR(256);

    private final int bitLength;

    Words(int bitLength) {
        this.bitLength = bitLength;
    }

    public int bitLength() {
        return bitLength;
    }

    public int byteLength() {
        return bitLength / 8;
    }
}