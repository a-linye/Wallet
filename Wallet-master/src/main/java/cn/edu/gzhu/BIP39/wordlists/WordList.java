package cn.edu.gzhu.BIP39.wordlists;

/**
 * @author Touale
 * @description WordList
 * @date 2022/1/26 21:08
 */
public interface WordList {
    String getWord(final int index);

    char getSpace();
}
