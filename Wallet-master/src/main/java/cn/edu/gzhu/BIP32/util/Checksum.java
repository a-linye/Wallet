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


import cn.edu.gzhu.BIP32.BadKeySerializationException;

import static cn.edu.gzhu.BIP39.encrypt.sha256.Sha256.sha256Twice;

public final class Checksum {

    public static void confirmExtendedKeyChecksum(final byte[] extendedKeyData) {
        final byte[] expected = checksum(extendedKeyData);
        for (int i = 0; i < 4; i++) {
            if (extendedKeyData[78 + i] != expected[i])
                throw new BadKeySerializationException("Checksum error");
        }
    }

    public static byte[] checksum(final byte[] privateKey) {
        return sha256Twice(privateKey, 0, 78);
    }
}