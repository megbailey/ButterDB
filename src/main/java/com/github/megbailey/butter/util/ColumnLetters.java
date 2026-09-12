package com.github.megbailey.butter.util;

import com.google.common.collect.HashBiMap;

/**
 * Converts zero-based column indices to spreadsheet letters (A, B, ... Z, AA, AB, ...).
 */
public final class ColumnLetters {
    private ColumnLetters() {}

    public static String toLetter(int index) {
        if (index < 0) {
            throw new IllegalArgumentException("Column index must be >= 0");
        }
        StringBuilder sb = new StringBuilder();
        int n = index + 1;
        while (n > 0) {
            n--;
            sb.insert(0, (char) ('A' + (n % 26)));
            n /= 26;
        }
        return sb.toString();
    }

    public static HashBiMap<Integer, String> buildMap(int columnCount) {
        HashBiMap<Integer, String> map = HashBiMap.create(columnCount);
        for (int i = 0; i < columnCount; i++) {
            map.put(i, toLetter(i));
        }
        return map;
    }

    /** Default capacity covering A–ZZ (702 columns). */
    public static HashBiMap<Integer, String> defaultMap() {
        return buildMap(26 + 26 * 26);
    }
}
