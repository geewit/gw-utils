package io.geewit.utils.core.enums;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class EnumMapUtilsTest {

    @Test
    void toBinary_nullMap() {
        assertEquals(0, EnumMapUtils.toBinary(null));
    }

    @Test
    void toBinary_emptyMap() {
        assertEquals(0, EnumMapUtils.toBinary(new HashMap<UserExtraInfoRequest, Boolean>()));
    }

    @Test
    void toBinary_withValues() {
        Map<UserExtraInfoRequest, Boolean> map = new HashMap<>();
        map.put(UserExtraInfoRequest.orgs, true);
        map.put(UserExtraInfoRequest.companies, false);
        assertEquals(1, EnumMapUtils.toBinary(map));
    }

    @Test
    void toEnumMap_convertsCorrectly() {
        Map<UserExtraInfoRequest, Boolean> map = EnumMapUtils.toEnumMap(UserExtraInfoRequest.class, 3);
        assertTrue(map.get(UserExtraInfoRequest.orgs));
        assertTrue(map.get(UserExtraInfoRequest.companies));
        assertFalse(map.get(UserExtraInfoRequest.resources));
    }

    @Test
    void newEnumMap_createsMap() {
        Map<UserExtraInfoRequest, Boolean> map = EnumMapUtils.newEnumMap(UserExtraInfoRequest.orgs, true);
        assertEquals(1, map.size());
        assertTrue(map.get(UserExtraInfoRequest.orgs));
    }
}
