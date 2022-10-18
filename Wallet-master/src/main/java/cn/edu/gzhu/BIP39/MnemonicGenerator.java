package cn.edu.gzhu.BIP39;

import cn.edu.gzhu.BIP39.wordlists.WordList;

import java.util.Arrays;

import static cn.edu.gzhu.BIP39.util.ByteUtils.next11Bits;
import static cn.edu.gzhu.BIP39.encrypt.sha256.Sha256.sha256;

/**
 * @author Touale
 * @description MnemonicGenerator
 * @date 2022/1/26 21:07
 */
public final class MnemonicGenerator {
    private final WordList wordList;

    // 词袋，如需添加其他词袋  加入wordlists
    public MnemonicGenerator(final WordList wordList) {
        this.wordList = wordList;
    }

    public interface Target {
        void append(final CharSequence string);
    }

    public void createMnemonic(
            final byte[] entropy,
            final Target target) {
        final int[] wordIndexes = wordIndexes(entropy);
        try {
            createMnemonic(wordIndexes, target);
        } finally {
            Arrays.fill(wordIndexes, 0);
        }

    }

    private void createMnemonic(
            final int[] wordIndexes,
            final Target target) {
        final String space = String.valueOf(wordList.getSpace());
        for (int i = 0; i < wordIndexes.length; i++) {
            if (i > 0) target.append(space);
            target.append(wordList.getWord(wordIndexes[i]));
        }
    }

    private static int[] wordIndexes(byte[] entropy) {
        final int ent = entropy.length * 8;
        entropyLengthPreChecks(ent);

        final byte[] entropyWithChecksum = Arrays.copyOf(entropy, entropy.length + 1);
        entropyWithChecksum[entropy.length] = firstByteOfSha256(entropy);

        //checksum length
        final int cs = ent / 32;
        //mnemonic length
        final int ms = (ent + cs) / 11;

        //get the indexes into the word list
        final int[] wordIndexes = new int[ms];
        for (int i = 0, wi = 0; wi < ms; i += 11, wi++) {
            wordIndexes[wi] = next11Bits(entropyWithChecksum, i);
        }
        return wordIndexes;
    }

    private static void entropyLengthPreChecks(final int ent) {
        if (ent < 128)
            throw new RuntimeException("Entropy too low, 128-256 bits allowed");
        if (ent > 256)
            throw new RuntimeException("Entropy too high, 128-256 bits allowed");
        if (ent % 32 > 0)
            throw new RuntimeException("Number of entropy bits must be divisible by 32");
    }

    private static byte firstByteOfSha256(final byte[] entropy) {
        final byte[] hash = sha256(entropy);
        final byte firstByte = hash[0];
        Arrays.fill(hash, (byte) 0);
        return firstByte;
    }
}
