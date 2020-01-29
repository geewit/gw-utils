package io.geewit.core.utils.enums;

import org.junit.Test;

import java.util.Map;

public class EnumMapUtilsTest {

    @Test
    public void toBinaryTest() {
        int value = 3;
        Map<UserExtraInfoRequest, Boolean> binaryMap = EnumMapUtils.toEnumMap(UserExtraInfoRequest.class, value);
        System.out.println(binaryMap);
        value = EnumMapUtils.toBinary(binaryMap);
        System.out.println(value);
    }


    @Test
    public void isTest() {
        boolean isTrue = EnumMapUtils.is(UserExtraInfoRequest.companies, 3);
        System.out.println("isTrue = " + isTrue);
    }

    @Test
    public void allTrueTest() {
        int allTrue = EnumMapUtils.allTrue(UserExtraInfoRequest.class);
        System.out.println("allTrue = " + allTrue);
    }
}
