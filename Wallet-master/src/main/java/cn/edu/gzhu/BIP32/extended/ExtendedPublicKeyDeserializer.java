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

package cn.edu.gzhu.BIP32.extended;



import cn.edu.gzhu.BIP32.Deserializer;
import cn.edu.gzhu.BIP32.HdKey;
import cn.edu.gzhu.BIP32.netWorksManage.Networks;
import cn.edu.gzhu.BIP32.netWorksManage.netWorks.DefaultNetworks;
import cn.edu.gzhu.BIP32.util.ByteArrayReader;

import java.util.Arrays;

import static cn.edu.gzhu.BIP32.util.Checksum.confirmExtendedKeyChecksum;
import static io.github.novacrypto.base58.Base58.base58Decode;


final class ExtendedPublicKeyDeserializer implements Deserializer<ExtendedPublicKey> {

    static final ExtendedPublicKeyDeserializer DEFAULT = new ExtendedPublicKeyDeserializer(DefaultNetworks.INSTANCE);

    private final Networks networks;

    ExtendedPublicKeyDeserializer(final Networks networks) {
        this.networks = networks;
    }

    @Override
    public ExtendedPublicKey deserialize(final CharSequence extendedBase58Key) {
        final byte[] extendedKeyData = base58Decode(extendedBase58Key);
        try {
            return deserialize(extendedKeyData);
        } finally {
            Arrays.fill(extendedKeyData, (byte) 0);
        }
    }

    @Override
    public ExtendedPublicKey deserialize(final byte[] extendedKeyData) {
        confirmExtendedKeyChecksum(extendedKeyData);
        final ByteArrayReader reader = new ByteArrayReader(extendedKeyData);
        return new ExtendedPublicKey(new HdKey.Builder()
                .network(networks.findByPublicVersion(reader.readSer32()))
                .depth(reader.read())
                .parentFingerprint(reader.readSer32())
                .childNumber(reader.readSer32())
                .chainCode(reader.readRange(32))
                .key(reader.readRange(33))
                .neutered(true)
                .build()
        );
    }
}