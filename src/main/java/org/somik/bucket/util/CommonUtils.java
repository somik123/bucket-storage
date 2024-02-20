package org.somik.bucket.util;

import java.util.Arrays;
import java.util.Random;

public class CommonUtils {

    // private static final char[] ILLEGAL_CHARACTERS = { '/', '\n', '\r', '\t',
    // '\0', '\f', '`', '?', '*', '\\', '<', '>', '|', '\"', ':' };

    private static final int[] ILLEGAL_CHARACTERS = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18,
            19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 34, 42, 47, 58, 60, 62, 63, 92, 124 };

    public static Boolean isNameValid(String name) {
        for (char c : name.toCharArray()) {
            if (Arrays.binarySearch(ILLEGAL_CHARACTERS, c) >= 0) {
                return false;
            }
        }
        return true;
    }

    public static String cleanFileName(String badFileName) {
        StringBuilder cleanName = new StringBuilder();
        for (int i = 0; i < badFileName.length(); i++) {
            int c = (int) badFileName.charAt(i);
            if (Arrays.binarySearch(ILLEGAL_CHARACTERS, c) < 0) {
                cleanName.append((char) c);
            }
        }
        return cleanName.toString();
    }

    public static String formatSize(long v) {
        if (v < 1024)
            return v + " B";
        int z = (63 - Long.numberOfLeadingZeros(v)) / 10;
        return String.format("%.1f %sB", (double) v / (1L << (z * 10)), " KMGTPE".charAt(z));
    }

    public static String randString() {
		return randString(32, 3);
	}
    
	public static String randString(int len) {
		return randString(len, 3);
	}

    public static String randString(int len, int mix) {
        String mix0 = "abcdefghijkmnpqstuvwxyz";
        String mix1 = "ABCDEFGHJKLMNPQRSTUVWXYZ";
        String mix2 = "23456789";

        String keyspace = mix0;
        if (mix > 0)
            keyspace += mix1;
        if (mix > 1)
            keyspace += mix2;

        String pieces = "";
        int max = keyspace.length() - 1;

        Random rand = new Random();
        for (int i = 0; i < len; i++) {
            int x = rand.nextInt(max);
            pieces += keyspace.charAt(x);
        }

        return pieces;
    }
}
