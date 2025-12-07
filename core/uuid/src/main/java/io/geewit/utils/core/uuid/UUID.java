package io.geewit.utils.core.uuid;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * 一个不可变的 128 位 UUID 类，采用 36 进制编码（使用 0–9 与 a–z）
 * 来表示字符串形式，从而相比传统的 16 进制编码缩短了字符串长度（固定 25 个字符）。
 *
 * <p>
 * 所有与字符串交互的方法均不包含分隔符，
 * {@code toString()} 返回固定 25 个字符的 36 进制表示，
 * {@code fromString(String)} 解析 36 进制表示的 UUID（允许输入长度不足 25 字符，前导零补齐）。
 * </p>
 *
 * <p>
 * UUID 的生成、解析等内部逻辑与原有实现一致，同时在字符串转换中充分考虑了高性能要求。
 * </p>
 *
 * @since 1.5
 */
public final class UUID implements java.io.Serializable, Comparable<UUID> {

    @java.io.Serial
    private static final long serialVersionUID = 1L;

    private final long mostSigBits;
    private final long leastSigBits;

    // 用于 36 进制编码的字符表
    private static final char[] BASE36_DIGITS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    // 内部安全随机数生成器，延迟初始化
    private static class Holder {
        static final SecureRandom numberGenerator = new SecureRandom();
    }

    // 通过 16 字节数组构造 UUID（内部使用）
    private UUID(byte[] data) {
        assert data.length == 16 : "data must be 16 bytes in length";
        long msb = data[0] & 0xff;
        msb = (msb << 8) | (data[1] & 0xff);
        msb = (msb << 8) | (data[2] & 0xff);
        msb = (msb << 8) | (data[3] & 0xff);
        msb = (msb << 8) | (data[4] & 0xff);
        msb = (msb << 8) | (data[5] & 0xff);
        msb = (msb << 8) | (data[6] & 0xff);
        msb = (msb << 8) | (data[7] & 0xff);
        long lsb = data[8] & 0xff;
        lsb = (lsb << 8) | (data[9] & 0xff);
        lsb = (lsb << 8) | (data[10] & 0xff);
        lsb = (lsb << 8) | (data[11] & 0xff);
        lsb = (lsb << 8) | (data[12] & 0xff);
        lsb = (lsb << 8) | (data[13] & 0xff);
        lsb = (lsb << 8) | (data[14] & 0xff);
        lsb = (lsb << 8) | (data[15] & 0xff);
        this.mostSigBits = msb;
        this.leastSigBits = lsb;
    }

    /**
     * 使用指定的两个 long 值构造 UUID，其中 {@code mostSigBits} 为高 64 位，
     * {@code leastSigBits} 为低 64 位。
     *
     * @param mostSigBits  UUID 的高 64 位
     * @param leastSigBits UUID 的低 64 位
     */
    public UUID(long mostSigBits, long leastSigBits) {
        this.mostSigBits = mostSigBits;
        this.leastSigBits = leastSigBits;
    }

    /**
     * 生成一个类型 4（随机生成）的 UUID，使用加密安全的随机数生成器。
     *
     * @return 随机生成的 UUID
     */
    public static UUID randomUUID() {
        var ng = Holder.numberGenerator;
        byte[] randomBytes = new byte[16];
        ng.nextBytes(randomBytes);
        // 设置版本号为 4（随机 UUID）
        randomBytes[6] &= 0x0f;
        randomBytes[6] |= 0x40;
        // 设置 variant 为 IETF
        randomBytes[8] &= 0x3f;
        randomBytes[8] |= (byte) 0x80;
        return new UUID(randomBytes);
    }

    /**
     * 根据指定的字节数组生成一个基于名称（MD5）的 UUID（类型 3）。
     *
     * @param name 用于构造 UUID 的字节数组
     * @return 基于指定数组生成的 UUID
     */
    public static UUID nameUUIDFromBytes(byte[] name) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException nsae) {
            throw new InternalError("MD5 not supported", nsae);
        }
        byte[] md5Bytes = md.digest(name);
        // 设置版本号为 3（基于名称 UUID）
        md5Bytes[6] &= 0x0f;
        md5Bytes[6] |= 0x30;
        // 设置 variant 为 IETF
        md5Bytes[8] &= 0x3f;
        md5Bytes[8] |= (byte) 0x80;
        return new UUID(md5Bytes);
    }

    /**
     * 根据 36 进制字符串生成 UUID。
     * 输入字符串应只包含数字和字母（0–9、a–z 或 A–Z），长度不能超过 25 个字符，
     * 若不足 25 个字符，则视为前导零。
     *
     * @param s 36 进制字符串表示的 UUID
     * @return 对应的 UUID
     * @throws IllegalArgumentException 如果字符串包含非法字符或长度超过 25
     */
    public static UUID fromString(String s) {
        if (s.length() > 25) {
            throw new IllegalArgumentException("Invalid UUID string: " + s);
        }
        long hi = 0L;
        long lo = 0L;
        for (int i = 0; i < s.length(); i++) {
            int digit = charToDigit(s.charAt(i));
            // 将当前 128 位数乘 36 并加上当前数字
            long prodLow = lo * 36;
            long carry = Math.multiplyHigh(lo, 36);
            long prodHigh = hi * 36 + carry;
            long newLow = prodLow + digit;
            if (Long.compareUnsigned(newLow, prodLow) < 0) {
                prodHigh++;
            }
            hi = prodHigh;
            lo = newLow;
        }
        return new UUID(hi, lo);
    }

    // 将单个字符转换为对应的数字（支持 0–9、a–z、A–Z）
    private static int charToDigit(char c) {
        if (c >= '0' && c <= '9') {
            return c - '0';
        }
        if (c >= 'a' && c <= 'z') {
            return c - 'a' + 10;
        }
        if (c >= 'A' && c <= 'Z') {
            return c - 'A' + 10;
        }
        throw new IllegalArgumentException("Invalid base36 character: " + c);
    }

    /**
     * 返回 UUID 的低 64 位。
     *
     * @return UUID 的低 64 位
     */
    public long getLeastSignificantBits() {
        return leastSigBits;
    }

    /**
     * 返回 UUID 的高 64 位。
     *
     * @return UUID 的高 64 位
     */
    public long getMostSignificantBits() {
        return mostSigBits;
    }

    /**
     * 返回此 UUID 的版本号（1~4）。
     *
     * @return UUID 的版本号
     */
    public int version() {
        return (int) ((mostSigBits >> 12) & 0x0f);
    }

    /**
     * 返回此 UUID 的变体值。
     *
     * @return UUID 的变体
     */
    public int variant() {
        if ((leastSigBits & 0x8000000000000000L) == 0) {
            return 0;
        } else if ((leastSigBits & 0xC000000000000000L) == 0x8000000000000000L) {
            return 2;
        } else if ((leastSigBits & 0xE000000000000000L) == 0xC000000000000000L) {
            return 6;
        } else {
            return 7;
        }
    }

    /**
     * 返回 UUID 的时间戳（仅对版本 1 有意义）。
     *
     * @return UUID 的时间戳
     * @throws UnsupportedOperationException 如果此 UUID 不是基于时间的 UUID（版本 1）
     */
    public long timestamp() {
        if (this.version() != 1) {
            throw new UnsupportedOperationException("Not a time-based UUID");
        }
        return ((mostSigBits & 0x0FFFL) << 48)
                | (((mostSigBits >> 16) & 0x0FFFFL) << 32)
                | (mostSigBits >>> 32);
    }

    /**
     * 返回 UUID 的时钟序列（仅对版本 1 有意义）。
     *
     * @return UUID 的时钟序列
     * @throws UnsupportedOperationException 如果此 UUID 不是基于时间的 UUID（版本 1）
     */
    public int clockSequence() {
        if (this.version() != 1) {
            throw new UnsupportedOperationException("Not a time-based UUID");
        }
        return (int) ((leastSigBits & 0x3FFF000000000000L) >>> 48);
    }

    /**
     * 返回 UUID 的节点值（仅对版本 1 有意义）。
     *
     * @return UUID 的节点值
     * @throws UnsupportedOperationException 如果此 UUID 不是基于时间的 UUID（版本 1）
     */
    public long node() {
        if (this.version() != 1) {
            throw new UnsupportedOperationException("Not a time-based UUID");
        }
        return leastSigBits & 0x0000FFFFFFFFFFFFL;
    }

    /**
     * 返回此 UUID 的 36 进制字符串表示，不包含任何分隔符，固定 25 个字符（不足时左侧补零）。
     *
     * <p>实现原理：将 128 位数分解为 4 个 32 位无符号整数，
     * 然后通过 25 次“除以 36—取余”迭代得到各位数字（余数对应 BASE36_DIGITS 表中的字符）。</p>
     *
     * @return 36 进制字符串表示的 UUID
     */
    @Override
    public String toString() {
        // 将 128 位数表示为 4 个 32 位的无符号整数
        int[] parts = new int[4];
        parts[0] = (int) (mostSigBits >>> 32);
        parts[1] = (int) mostSigBits;
        parts[2] = (int) (leastSigBits >>> 32);
        parts[3] = (int) leastSigBits;
        char[] buf = new char[25];
        // 固定进行 25 次除法，每次获得一个 36 进制数字（从低位开始）
        for (int pos = 24; pos >= 0; pos--) {
            int remainder;
            {
                long dividend = parts[0] & 0xFFFFFFFFL;
                int quotient = (int) (dividend / 36);
                remainder = (int) (dividend % 36);
                parts[0] = quotient;
            }
            {
                long dividend = ((long) remainder << 32) | (parts[1] & 0xFFFFFFFFL);
                int quotient = (int) (dividend / 36);
                remainder = (int) (dividend % 36);
                parts[1] = quotient;
            }
            {
                long dividend = ((long) remainder << 32) | (parts[2] & 0xFFFFFFFFL);
                int quotient = (int) (dividend / 36);
                remainder = (int) (dividend % 36);
                parts[2] = quotient;
            }
            {
                long dividend = ((long) remainder << 32) | (parts[3] & 0xFFFFFFFFL);
                int quotient = (int) (dividend / 36);
                remainder = (int) (dividend % 36);
                parts[3] = quotient;
            }
            buf[pos] = BASE36_DIGITS[remainder];
        }
        return new String(buf);
    }

    /**
     * 返回 UUID 的哈希值。
     *
     * @return UUID 的哈希码
     */
    @Override
    public int hashCode() {
        return Long.hashCode(mostSigBits ^ leastSigBits);
    }

    /**
     * 比较此 UUID 与指定对象是否相等。
     *
     * @param obj 要比较的对象
     * @return 如果两个 UUID 相等则返回 {@code true}，否则返回 {@code false}
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof UUID other)) {
            return false;
        }
        return mostSigBits == other.mostSigBits && leastSigBits == other.leastSigBits;
    }

    /**
     * 按数值顺序比较两个 UUID。
     *
     * @param val 要比较的 UUID
     * @return -1、0 或 1，分别表示此 UUID 小于、等于或大于指定 UUID
     */
    @Override
    public int compareTo(UUID val) {
        int cmp = Long.compare(this.mostSigBits, val.mostSigBits);
        return cmp != 0 ? cmp : Long.compare(this.leastSigBits, val.leastSigBits);
    }
}
