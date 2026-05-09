package io.geewit.utils.i18n;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CurrencyChineseUtilsTest {

    @Test
    void toChineseUppercase_zero() {
        assertEquals("零元整", CurrencyChineseUtils.toChineseUppercase(BigDecimal.ZERO));
    }

    @Test
    void toChineseUppercase_null() {
        assertEquals("零元整", CurrencyChineseUtils.toChineseUppercase(null));
    }

    @Test
    void toChineseUppercase_oneYuan() {
        assertEquals("壹元整", CurrencyChineseUtils.toChineseUppercase(new BigDecimal("1")));
    }

    @Test
    void toChineseUppercase_tenYuan() {
        assertEquals("壹拾元整", CurrencyChineseUtils.toChineseUppercase(new BigDecimal("10")));
    }

    @Test
    void toChineseUppercase_elevenYuan() {
        assertEquals("壹拾壹元整", CurrencyChineseUtils.toChineseUppercase(new BigDecimal("11")));
    }

    @Test
    void toChineseUppercase_oneHundredYuan() {
        assertEquals("壹佰元整", CurrencyChineseUtils.toChineseUppercase(new BigDecimal("100")));
    }

    @Test
    void toChineseUppercase_oneThousandYuan() {
        assertEquals("壹仟元整", CurrencyChineseUtils.toChineseUppercase(new BigDecimal("1000")));
    }

    @Test
    void toChineseUppercase_tenThousandYuan() {
        assertEquals("壹万元整", CurrencyChineseUtils.toChineseUppercase(new BigDecimal("10000")));
    }

    @Test
    void toChineseUppercase_oneHundredMillionYuan() {
        assertEquals("壹亿元整", CurrencyChineseUtils.toChineseUppercase(new BigDecimal("100000000")));
    }

    @Test
    void toChineseUppercase_complexNumber() {
        assertEquals("壹仟贰佰叁拾肆元整", CurrencyChineseUtils.toChineseUppercase(new BigDecimal("1234")));
    }

    @Test
    void toChineseUppercase_withJiao() {
        assertEquals("壹元伍角", CurrencyChineseUtils.toChineseUppercase(new BigDecimal("1.50")));
    }

    @Test
    void toChineseUppercase_withFen() {
        assertEquals("壹元零伍分", CurrencyChineseUtils.toChineseUppercase(new BigDecimal("1.05")));
    }

    @Test
    void toChineseUppercase_withJiaoAndFen() {
        assertEquals("壹元伍角陆分", CurrencyChineseUtils.toChineseUppercase(new BigDecimal("1.56")));
    }

    @Test
    void toChineseUppercase_onlyFen() {
        assertEquals("零元伍分", CurrencyChineseUtils.toChineseUppercase(new BigDecimal("0.05")));
    }

    @Test
    void toChineseUppercase_negative() {
        assertEquals("负壹元整", CurrencyChineseUtils.toChineseUppercase(new BigDecimal("-1")));
    }

    @Test
    void toChineseUppercase_negativeWithDecimal() {
        assertEquals("负壹元伍角", CurrencyChineseUtils.toChineseUppercase(new BigDecimal("-1.50")));
    }

    @Test
    void toChineseUppercase_withZeroInMiddle() {
        assertEquals("壹仟零壹元整", CurrencyChineseUtils.toChineseUppercase(new BigDecimal("1001")));
    }

    @Test
    void toChineseUppercase_withMultipleZeros() {
        assertEquals("壹仟零壹拾元整", CurrencyChineseUtils.toChineseUppercase(new BigDecimal("1010")));
    }

    @Test
    void toChineseUppercase_largeNumber() {
        assertEquals("壹仟贰佰叁拾肆亿伍仟陆佰柒拾捌万玖仟零壹拾贰元整",
                CurrencyChineseUtils.toChineseUppercase(new BigDecimal("123456789012")));
    }

    @Test
    void toChineseUppercase_rounding() {
        assertEquals("壹元整", CurrencyChineseUtils.toChineseUppercase(new BigDecimal("1.004")));
    }

    @Test
    void toChineseUppercase_roundingUp() {
        assertEquals("壹元零壹分", CurrencyChineseUtils.toChineseUppercase(new BigDecimal("1.005")));
    }

    @Test
    void toChineseUppercase_zeroYuanWithFen() {
        assertEquals("零元伍分", CurrencyChineseUtils.toChineseUppercase(new BigDecimal("0.05")));
    }

    @Test
    void toChineseUppercase_withZeroInGroup() {
        // 100000001 = 1亿零1 -> tests needZero logic
        assertEquals("壹亿零壹元整", CurrencyChineseUtils.toChineseUppercase(new BigDecimal("100000001")));
    }

    @Test
    void toChineseUppercase_withZeroBetweenGroups() {
        // 100000100 = 1亿零1佰 -> tests needZero + groupText
        assertEquals("壹亿零壹佰元整", CurrencyChineseUtils.toChineseUppercase(new BigDecimal("100000100")));
    }

    @Test
    void toChineseUppercase_withZeroInTenPlace() {
        // 100000010 = 1亿零1拾 -> tests needZero
        assertEquals("壹亿零壹拾元整", CurrencyChineseUtils.toChineseUppercase(new BigDecimal("100000010")));
    }

    @Test
    void toChineseUppercase_withTrailingZeroGroup() {
        // 100000000 = 1亿 -> no zero needed for trailing empty groups
        assertEquals("壹亿元整", CurrencyChineseUtils.toChineseUppercase(new BigDecimal("100000000")));
    }
}