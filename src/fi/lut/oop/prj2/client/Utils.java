package fi.lut.oop.prj2.client;

import java.util.Random;

/**
 * User: Marek Salát
 * Date: 11.3.14
 * Time: 21:16
 */
public class Utils {

    static Random random = new Random();
    static char[] chars;

    /**
     * Creates random string from english lowercase and uppercase alphabet and numbers
     *
     * @param length
     * @return
     */
    public static String randomString(final int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            char c = chars[random.nextInt(chars.length)];
            sb.append(c);
        }
        return sb.toString();
    }

    /**
     * Join array of string by separator. join(";", {"asdf", "cvb"}) will produce "asdf;cvb"
     * @param join
     * @param strings
     * @return
     */
    public static String join(String join, String... strings) {
        if (strings == null || strings.length == 0) {
            return "";
        } else if (strings.length == 1) {
            return strings[0];
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(strings[0]);
            for (int i = 1; i < strings.length; i++) {
                sb.append(join).append(strings[i]);
            }
            return sb.toString();
        }
    }

    static {
        String alphabet = "abcdefghijklmnopqrstuvwxyz";
        alphabet += alphabet.toUpperCase() + "0123456789";
        chars = alphabet.toCharArray();
    }

}
