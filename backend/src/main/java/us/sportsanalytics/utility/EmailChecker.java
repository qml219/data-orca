package us.sportsanalytics.utility;

import java.util.regex.Pattern;

public class EmailChecker {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$");

    public static boolean isValid(String value) {
        return value != null && EMAIL_PATTERN.matcher(value.trim()).matches();
    }
}
