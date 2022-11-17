package cn.edu.gzhu.utils;

public class ToAscii {
    public static String strToAscii(String str) {
        char[] charArr = str.toCharArray();
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < charArr.length; i++) {
            int charInt = charArr[i];
            if(i == charArr.length -1) {
                sb.append(charInt);
                continue;
            }
            sb.append(charInt).append("");
        }
        return sb.toString();
    }


    public static String asciiToToStr(String str) {
        String[] strArr = str.split(" ");
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < strArr.length; i++) {
            char char1 = (char) Integer.parseInt(strArr[i]);
            sb.append(char1);
        }
        return sb.toString();
    }

}
