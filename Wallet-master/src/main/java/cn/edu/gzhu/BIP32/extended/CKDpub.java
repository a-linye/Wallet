package cn.edu.gzhu.BIP32.extended;

import cn.edu.gzhu.BIP32.extended.ExtendedPublicKey;

/**
 * @author Touale
 * @description CKDpub
 * @date 2022/1/27 17:23
 */
public interface CKDpub {
    /**
     * Calculates the public key of the child at index.
     *
     * @param index The child index to calculate.
     * @return The public key of the child.
     */
    ExtendedPublicKey cKDpub(final int index);
}
