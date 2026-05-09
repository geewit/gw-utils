package io.geewit.utils.core.enums;

import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class BinaryUtilsTest {

    @Test
    void toBinary_singleEnum() {
        assertEquals(1, BinaryUtils.toBinary(UserExtraInfoRequest.orgs));
        assertEquals(2, BinaryUtils.toBinary(UserExtraInfoRequest.companies));
        assertEquals(4, BinaryUtils.toBinary(UserExtraInfoRequest.resources));
    }

    @Test
    void toBinary_nullEnum() {
        assertEquals(0, BinaryUtils.toBinary((UserExtraInfoRequest) null));
    }

    @Test
    void toBinary_multipleEnums() {
        int result = BinaryUtils.toBinary(UserExtraInfoRequest.orgs, UserExtraInfoRequest.companies);
        assertEquals(3, result);
    }

    @Test
    void toBinary_varargsNull() {
        assertEquals(0, BinaryUtils.toBinary((UserExtraInfoRequest[]) null));
    }

    @Test
    void toBinary_varargsEmpty() {
        assertEquals(0, BinaryUtils.toBinary(new UserExtraInfoRequest[0]));
    }

    @Test
    void toBinary_collection() {
        List<UserExtraInfoRequest> list = Arrays.asList(UserExtraInfoRequest.orgs, UserExtraInfoRequest.resources);
        assertEquals(5, BinaryUtils.toBinary(list));
    }

    @Test
    void toBinary_collectionNull() {
        assertEquals(0, BinaryUtils.toBinary((Collection<UserExtraInfoRequest>) null));
    }

    @Test
    void fromBinary_convertsBack() {
        int binary = BinaryUtils.toBinary(UserExtraInfoRequest.orgs, UserExtraInfoRequest.companies);
        EnumSet<UserExtraInfoRequest> result = BinaryUtils.fromBinary(binary, UserExtraInfoRequest.class);
        assertTrue(result.contains(UserExtraInfoRequest.orgs));
        assertTrue(result.contains(UserExtraInfoRequest.companies));
        assertFalse(result.contains(UserExtraInfoRequest.resources));
    }

    @Test
    void fromBinaryToValues_returnsValues() {
        int binary = BinaryUtils.toBinary(UserExtraInfoRequest.orgs, UserExtraInfoRequest.companies);
        List<Integer> values = BinaryUtils.fromBinaryToValues(binary, UserExtraInfoRequest.class);
        assertTrue(values.contains(1));
        assertTrue(values.contains(2));
        assertFalse(values.contains(4));
    }

    @Test
    void allTrue_allBitsSet() {
        int all = BinaryUtils.allTrue(UserExtraInfoRequest.class);
        assertTrue(all > 0);
        assertTrue(BinaryUtils.is(UserExtraInfoRequest.orgs, all));
        assertTrue(BinaryUtils.is(UserExtraInfoRequest.companies, all));
    }

    @Test
    void is_true() {
        int binary = BinaryUtils.toBinary(UserExtraInfoRequest.orgs, UserExtraInfoRequest.companies);
        assertTrue(BinaryUtils.is(UserExtraInfoRequest.orgs, binary));
    }

    @Test
    void is_false() {
        int binary = BinaryUtils.toBinary(UserExtraInfoRequest.orgs);
        assertFalse(BinaryUtils.is(UserExtraInfoRequest.companies, binary));
    }

    @Test
    void any_true() {
        int binary = BinaryUtils.toBinary(UserExtraInfoRequest.orgs);
        assertTrue(BinaryUtils.any(UserExtraInfoRequest.class, binary));
    }

    @Test
    void any_false() {
        assertFalse(BinaryUtils.any(UserExtraInfoRequest.class, 0));
    }

    @Test
    void hasAny_withCollection_true() {
        int binary = BinaryUtils.toBinary(UserExtraInfoRequest.orgs, UserExtraInfoRequest.companies);
        List<UserExtraInfoRequest> check = Collections.singletonList(UserExtraInfoRequest.companies);
        assertTrue(BinaryUtils.hasAny(check, binary));
    }

    @Test
    void hasAny_withCollection_false() {
        int binary = BinaryUtils.toBinary(UserExtraInfoRequest.orgs);
        List<UserExtraInfoRequest> check = Collections.singletonList(UserExtraInfoRequest.companies);
        assertFalse(BinaryUtils.hasAny(check, binary));
    }

    @Test
    void hasAny_nullCollection() {
        assertFalse(BinaryUtils.hasAny(null, 1));
    }

    @Test
    void hasAll_allMatch() {
        int binary = BinaryUtils.toBinary(UserExtraInfoRequest.orgs, UserExtraInfoRequest.companies);
        List<UserExtraInfoRequest> check = Arrays.asList(UserExtraInfoRequest.orgs, UserExtraInfoRequest.companies);
        assertTrue(BinaryUtils.hasAll(check, binary));
    }

    @Test
    void hasAll_notAllMatch() {
        int binary = BinaryUtils.toBinary(UserExtraInfoRequest.orgs);
        List<UserExtraInfoRequest> check = Arrays.asList(UserExtraInfoRequest.orgs, UserExtraInfoRequest.companies);
        assertFalse(BinaryUtils.hasAll(check, binary));
    }

    @Test
    void hasAll_nullCollection() {
        assertFalse(BinaryUtils.hasAll(null, 1));
    }
}
