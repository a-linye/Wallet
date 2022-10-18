package cn.edu.gzhu.BIP39.encrypt.sha512;

/**
 * @author Touale
 * @description PBKDF2WithHmacSHA512
 * @date 2022/1/27 16:35
 */
public interface PBKDF2WithHmacSHA512 {
    byte[] hash(final char[] chars, final byte[] salt);
}
