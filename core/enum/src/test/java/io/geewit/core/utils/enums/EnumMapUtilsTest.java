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
}
