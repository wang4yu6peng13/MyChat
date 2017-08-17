package utils;

import java.text.DecimalFormat;

public class StringHelper {
    public static boolean isNullOrTrimEmpty(String str) {
        return str != null && str.trim().isEmpty();
    }

    public static String moneyMulti100(String money) {
        return Integer.toString((int) (Float.valueOf(money) * 100));
    }

    public static String moneyDivideBy100(int money) {
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        return decimalFormat.format((float) money / 100);
    }
}
