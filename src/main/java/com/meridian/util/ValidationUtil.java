package com.meridian.util;

import java.util.regex.Pattern;

public final class ValidationUtil {

    private static final Pattern EMAIL =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private ValidationUtil() { }

    public static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static boolean isValidEmail(String email) {
        return !isBlank(email) && EMAIL.matcher(email.trim()).matches();
    }

    public static boolean isStrongPassword(String pw) {
        return pw != null && pw.length() >= 8
                && pw.chars().anyMatch(Character::isLetter)
                && pw.chars().anyMatch(Character::isDigit);
    }

    public static int parseId(String s) {
        if (s == null) {
            return -1;
        }
        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
