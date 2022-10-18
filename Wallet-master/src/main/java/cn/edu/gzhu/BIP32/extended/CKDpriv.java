package cn.edu.gzhu.BIP32.extended;

import cn.edu.gzhu.BIP32.extended.ExtendedPrivateKey;

/**
 * @author Touale
 * @description CKDpriv
 * @date 2022/1/27 17:23
 */
public interface CKDpriv {
    /**
     * Calculates the private key of the child at index.
     *
     * @param index The child index to calculate.
     * @return The private key of the child.
     */
    ExtendedPrivateKey cKDpriv(final int index);
}
