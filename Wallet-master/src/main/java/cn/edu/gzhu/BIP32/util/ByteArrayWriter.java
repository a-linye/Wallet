/*
 *  BIP32 library, a Java implementation of BIP32
 *  Copyright (C) 2017-2019 Alan Evans, NovaCrypto
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 *  Original source: https://github.com/NovaCrypto/BIP32
 *  You can contact the authors via github issues.
 */

package cn.edu.gzhu.BIP32.util;

import java.util.Arrays;

public final class ByteArrayWriter {

    private final byte[] bytes;
    private int idx = 0;

    public ByteArrayWriter(final byte[] target) {
        this.bytes = target;
    }

    public void concat(final byte[] bytesSource, final int length) {
        System.arraycopy(bytesSource, 0, bytes, idx, length);
        idx += length;
    }

    public void concat(final byte[] bytesSource) {
        concat(bytesSource, bytesSource.length);
    }

    /**
     * ser32(i): serialize a 32-bit unsigned integer i as a 4-byte sequence, most significant byte first.
     *
     * @param i a 32-bit unsigned integer
     */
    public void concatSer32(final int i) {
        concat((byte) (i >> 24));
        concat((byte) (i >> 16));
        concat((byte) (i >> 8));
        concat((byte) (i));
    }

    public void concat(final byte b) {
        bytes[idx++] = b;
    }

    public static byte[] tail32(final byte[] bytes64) {
        final byte[] ir = new byte[bytes64.length - 32];
        System.arraycopy(bytes64, 32, ir, 0, ir.length);
        return ir;
    }

    public static byte[] head32(final byte[] bytes64) {
        return Arrays.copyOf(bytes64, 32);
    }
}