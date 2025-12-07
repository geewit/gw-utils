package io.geewit.utils.i18n;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import static java.math.BigInteger.ZERO;

/**
 * Utility methods for converting numeric amounts to Chinese uppercase text.
 */
public final class CurrencyChineseUtils {
    private static final String[] CN_UPPER_NUMBER = {"零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"};
    private static final String[] CN_UPPER_UNIT = {"", "拾", "佰", "仟"};
    private static final String[] CN_GROUP_UNIT = {"", "万", "亿", "兆"};
    private static final String CN_NEGATIVE = "负";
    private static final String CN_FULL = "整";
    private static final String CN_YUAN = "元";
    private static final String CN_JIAO = "角";
    private static final String CN_FEN = "分";
    private static final BigInteger TEN_THOUSAND = BigInteger.valueOf(10_000);

    private CurrencyChineseUtils() {
    }

    public static String toChineseUppercase(BigDecimal amount) {
        if (amount == null) {
            return "零元整";
        }
        BigDecimal normalized = amount.setScale(2, RoundingMode.HALF_UP);
        boolean negative = normalized.signum() < 0;
        BigDecimal absolute = normalized.abs();
        BigInteger scaled = absolute.movePointRight(2).toBigInteger();
        if (scaled.equals(ZERO)) {
            return "零元整";
        }

        BigInteger integerPart = absolute.setScale(0, RoundingMode.DOWN).toBigInteger();
        int fraction = absolute.remainder(BigDecimal.ONE).movePointRight(2).intValue();
        int jiao = fraction / 10;
        int fen = fraction % 10;

        String integerText = convertIntegerPart(integerPart);
        StringBuilder result = new StringBuilder();
        if (negative) {
            result.append(CN_NEGATIVE);
        }
        if (integerText.isEmpty()) {
            result.append(CN_UPPER_NUMBER[0]).append(CN_YUAN);
        } else {
            result.append(integerText).append(CN_YUAN);
        }
        if (jiao == 0 && fen == 0) {
            result.append(CN_FULL);
            return result.toString();
        }
        if (jiao > 0) {
            result.append(CN_UPPER_NUMBER[jiao]).append(CN_JIAO);
        } else if (!integerText.isEmpty() && fen > 0) {
            result.append(CN_UPPER_NUMBER[0]);
        }
        if (fen > 0) {
            result.append(CN_UPPER_NUMBER[fen]).append(CN_FEN);
        }
        return result.toString();
    }

    private static String convertIntegerPart(BigInteger value) {
        if (value.equals(ZERO)) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        int groupIndex = 0;
        boolean needZero = false;
        while (value.compareTo(ZERO) > 0) {
            int group = value.mod(TEN_THOUSAND).intValue();
            if (group == 0) {
                if (!result.isEmpty()) {
                    needZero = true;
                }
            } else {
                String groupText = convertGroup(group);
                if (groupIndex > 0) {
                    groupText = groupText + CN_GROUP_UNIT[groupIndex];
                }
                if (needZero) {
                    result.insert(0, CN_UPPER_NUMBER[0]);
                    needZero = false;
                }
                result.insert(0, groupText);
            }
            value = value.divide(TEN_THOUSAND);
            groupIndex++;
        }
        return result.toString();
    }

    private static String convertGroup(int value) {
        StringBuilder group = new StringBuilder();
        int unitIndex = 0;
        boolean zero = false;
        while (value > 0) {
            int digit = value % 10;
            if (digit == 0) {
                if (!zero && !group.isEmpty()) {
                    group.insert(0, CN_UPPER_NUMBER[0]);
                }
                zero = true;
            } else {
                zero = false;
                group.insert(0, CN_UPPER_UNIT[unitIndex]);
                group.insert(0, CN_UPPER_NUMBER[digit]);
            }
            unitIndex++;
            value /= 10;
        }
        String text = group.toString();
        return text.replaceAll("零+$", "").replaceAll("零零+", "零");
    }
}
