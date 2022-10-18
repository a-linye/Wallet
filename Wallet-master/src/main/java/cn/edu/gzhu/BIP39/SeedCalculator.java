package cn.edu.gzhu.BIP39;

import cn.edu.gzhu.BIP39.encrypt.sha256.CheckedExceptionToRuntime;
import cn.edu.gzhu.BIP39.encrypt.sha512.PBKDF2WithHmacSHA512;
import cn.edu.gzhu.BIP39.encrypt.sha512.SpongyCastlePBKDF2WithHmacSHA512;

import java.util.Arrays;

import static cn.edu.gzhu.BIP39.util.Normalization.normalizeNFKD;
import static cn.edu.gzhu.BIP39.encrypt.sha256.CheckedExceptionToRuntime.toRuntime;

/**
 * @author Touale
 * @description SeedCalculator
 * @date 2022/1/27 16:33
 */
public class SeedCalculator {
    private final byte[] fixedSalt = getUtf8Bytes("mnemonic");

    // Key Stretching Function PBKDF using HMAC-SHA512
    private final PBKDF2WithHmacSHA512 hashAlgorithm;

    public SeedCalculator(final PBKDF2WithHmacSHA512 hashAlgorithm) {
        this.hashAlgorithm = hashAlgorithm;
    }

    public SeedCalculator() {
        this(SpongyCastlePBKDF2WithHmacSHA512.INSTANCE);
    }

    public byte[] calculateSeed(final String mnemonic, final String passphrase) {
        final char[] chars = normalizeNFKD(mnemonic).toCharArray();
        try {
            return calculateSeed(chars, passphrase);
        } finally {
            Arrays.fill(chars, '\0');
        }
    }

    private byte[] calculateSeed(final char[] mnemonicChars, final String passphrase) {
        final String normalizedPassphrase = normalizeNFKD(passphrase);
        final byte[] salt2 = getUtf8Bytes(normalizedPassphrase);
        final byte[] salt = combine(fixedSalt, salt2);
        clear(salt2);
        final byte[] encoded = hash(mnemonicChars, salt);
        clear(salt);
        return encoded;
    }

    private static byte[] combine(final byte[] array1, final byte[] array2) {
        final byte[] bytes = new byte[array1.length + array2.length];
        System.arraycopy(array1, 0, bytes, 0, array1.length);
        System.arraycopy(array2, 0, bytes, array1.length, bytes.length - array1.length);
        return bytes;
    }

    private byte[] hash(final char[] chars, final byte[] salt) {
        return hashAlgorithm.hash(chars, salt);
    }

    private static void clear(final byte[] salt) {
        Arrays.fill(salt, (byte) 0);
    }

    private static byte[] getUtf8Bytes(final String string) {
        return toRuntime(new CheckedExceptionToRuntime.Func<byte[]>() {
            @Override
            public byte[] run() throws Exception {
                return string.getBytes("UTF-8");
            }
        });
    }
}
