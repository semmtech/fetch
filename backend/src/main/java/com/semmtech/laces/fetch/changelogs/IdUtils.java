package com.semmtech.laces.fetch.changelogs;

import java.util.regex.Pattern;

public class IdUtils {
    private static Pattern pattern = Pattern.compile("\\p{XDigit}+", Pattern.CASE_INSENSITIVE);

    public static boolean isValidHexadecimalId(String id) {
        return pattern.matcher(id).matches();
    }
}
