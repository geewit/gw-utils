package io.geewit.utils.core.uuid.util.internal;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ByteUtilTest {

    @Test
    void toNumber_singleByte() {
        byte[] bytes = {(byte) 0x01};
        assertEquals(0x01L, ByteUtil.toNumber(bytes));
    }

    @Test
    void toNumber_twoBytes() {
        byte[] bytes = {(byte) 0x01, (byte) 0x02};
        // Big-endian: (0x01 << 8) | 0x02 = 0x0102
        assertEquals(0x0102L, ByteUtil.toNumber(bytes));
    }

    @Test
    void toNumber_fourBytes() {
        byte[] bytes = {(byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04};
        // Big-endian: 0x01020304
        assertEquals(0x01020304L, ByteUtil.toNumber(bytes));
    }

    @Test
    void toNumber_eightBytes() {
        byte[] bytes = {
                (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04,
                (byte) 0x05, (byte) 0x06, (byte) 0x07, (byte) 0x08
        };
        // Big-endian: 0x0102030405060708
        assertEquals(0x0102030405060708L, ByteUtil.toNumber(bytes));
    }

    @Test
    void toNumber_allFF() {
        byte[] bytes = {(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF};
        assertEquals(0xFFFFFFFFL, ByteUtil.toNumber(bytes));
    }

    @Test
    void toNumber_withOffset() {
        byte[] bytes = {0, 0, (byte) 0x01, (byte) 0x02};
        assertEquals(0x0102L, ByteUtil.toNumber(bytes, 2, 4));
    }

    @Test
    void toNumber_offsetAndLength() {
        byte[] bytes = {0, 0, (byte) 0xAB, (byte) 0xCD};
        assertEquals(0xABCDL, ByteUtil.toNumber(bytes, 2, 4));
    }

    @Test
    void toNumber_offsetZero() {
        byte[] bytes = {(byte) 0x12, (byte) 0x34};
        assertEquals(0x1234L, ByteUtil.toNumber(bytes, 0, 2));
    }

    @Test
    void toNumber_partialArray() {
        byte[] bytes = {(byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05};
        // Only first 3 bytes
        assertEquals(0x010203L, ByteUtil.toNumber(bytes, 0, 3));
    }

    @Test
    void toNumber_negativeBytes() {
        // In Java, byte is signed, but we treat as unsigned via & 0xffL
        byte[] bytes = {(byte) 0xFF};
        assertEquals(0xFFL, ByteUtil.toNumber(bytes));
    }

    @Test
    void toNumber_allZeros() {
        byte[] bytes = {0, 0, 0, 0};
        assertEquals(0L, ByteUtil.toNumber(bytes));
    }

    @Test
    void toNumber_withOffsetAllZeros() {
        byte[] bytes = {0, 0, 0, 0, 0};
        assertEquals(0L, ByteUtil.toNumber(bytes, 2, 5));
    }
}