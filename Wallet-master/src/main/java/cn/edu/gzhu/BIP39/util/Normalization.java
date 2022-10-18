package cn.edu.gzhu.BIP39.util;

import java.text.Normalizer;

/**
 * @author Touale
 * @description Normalization
 * @date 2022/1/27 16:46
 */
public final class Normalization {
    public static String normalizeNFKD(final String string) {
        return Normalizer.normalize(string, Normalizer.Form.NFKD);
    }

    public static char normalizeNFKD(final char c) {
        return normalizeNFKD("" + c).charAt(0);
    }

}
