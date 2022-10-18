package cn.edu.gzhu.BIP32.netWorksManage.netWorks;

import cn.edu.gzhu.BIP32.netWorksManage.Network;

/**
 * @author Touale
 * @description BitCoin
 * @date 2022/1/27 17:20
 */
public enum Bitcoin implements Network {
    MAIN_NET {
        @Override
        public int getPrivateVersion() {
            return 0x488ade4;
        }

        @Override
        public int getPublicVersion() {
            return 0x0488b21e;
        }

        @Override
        public byte p2pkhVersion() {
            return 0;
        }

        @Override
        public byte p2shVersion() {
            return 0x05;
        }
    },

    TEST_NET {
        @Override
        public int getPrivateVersion() {
            return 0x4358394;
        }

        @Override
        public int getPublicVersion() {
            return 0x043587cf;
        }

        @Override
        public byte p2pkhVersion() {
            return 0x6f;
        }

        @Override
        public byte p2shVersion() {
            return (byte) 0xc4;
        }
    }
}