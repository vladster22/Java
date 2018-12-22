package Libs;

import java.nio.ByteBuffer;
import java.util.Date;

public abstract class Bytes {

    public static byte[] toBytes(final byte number) {
        return new byte[]{number};
    }

    public static byte[] toBytes(final short number) {
        return ByteBuffer.allocate(2).putShort(number).array();
    }

    public static byte[] toBytes(final int number) {
        return ByteBuffer.allocate(4).putInt(number).array();
    }

    public static byte[] toBytes(final long number) {
        return ByteBuffer.allocate(8).putLong(number).array();
    }

    public static byte[] toBytes(final float number) {
        return ByteBuffer.allocate(4).putFloat(number).array();
    }

    public static byte[] toBytes(final double number) {
        return ByteBuffer.allocate(8).putDouble(number).array();
    }

    public static byte[] toBytes(final char symbol) {
        return ByteBuffer.allocate(2).putChar(symbol).array();
    }

    public static byte[] toBytes(final String string) {
        return string.getBytes();
    }

    public static byte[] toBytes(final Date date) {
        return toBytes(date.getTime());
    }

    public static byte toByte(final byte[] bytes) {

        if (bytes.length != 1) {
            throw new IllegalArgumentException("The byte array must contain 1 bytes.");
        } else {
            return bytes[0];
        }
    }

    public static short toShort(final byte[] bytes) {

        if(bytes.length != 2) {
            throw new IllegalArgumentException("The byte array must contain 2 bytes.");
        } else {
            return ByteBuffer.wrap(bytes).getShort();
        }
    }

    public static int toInt(final byte[] bytes) {

        if (bytes.length != 4) {
            throw new IllegalArgumentException("The byte array must contain 4 bytes.");
        } else {
            return ByteBuffer.wrap(bytes).getInt();
        }
    }

    public static long toLong(final byte[] bytes) {

        if (bytes.length != 8) {
            throw new IllegalArgumentException("The byte array must contain 8 bytes.");
        } else {
            return ByteBuffer.wrap(bytes).getLong();
        }
    }

    public static float toFloat(final byte[] bytes) {

        if (bytes.length != 4) {
            throw new IllegalArgumentException("The byte array must contain 4 bytes.");
        } else {
            return ByteBuffer.wrap(bytes).getFloat();
        }
    }

    public static double toDouble(final byte[] bytes) {

        if (bytes.length != 8) {
            throw new IllegalArgumentException("The byte array must contain 8 bytes.");
        } else {
            return ByteBuffer.wrap(bytes).getDouble();
        }
    }

    public static char toChar(final byte[] bytes) {

        if(bytes.length > 1) {
            throw new IllegalArgumentException("The byte array must contain one bytes.");
        } else {
            return ByteBuffer.wrap(bytes).getChar();
        }
    }

    public static String toString(final byte[] bytes) {
        return new String(bytes);
    }

    public static Date toDate(final byte[] bytes) {
        return new Date(toLong(bytes));
    }

    public static byte toByte(final boolean[] booleans) {

        byte result = 0;
        for (int i = 0; i < Byte.SIZE; i++) {
            if (i < booleans.length && booleans[i]) {
                result |= 1 << i;
            }
        }
        return result;
    }

    public static boolean[] toBooleans(final byte value) {

        boolean[] booleans = new boolean[Byte.SIZE];
        byte start = 0x01;
        for (int i = 0; i < booleans.length; i++) {
            booleans[i] = ((value & start) != 0);
            start = (byte)(start << 1);
        }
        return booleans;
    }

    public static byte[] merge(final byte[] first, final byte[] two) {

        byte[] merge = new byte[first.length + two.length];
        for (int i = 0; i < merge.length; i++) {
            merge[i] = i < first.length ? first[i] : two[i-first.length];
        }
        return merge;
    }

    public static byte[] resize(final byte[] bytes, final int size) {

        if (size < 1) {
            throw new IllegalArgumentException("Incorrect size.");
        }

        byte[] resize = new byte[size];
        for (int i = 0; i < resize.length; i++) {
            resize[i] = i < bytes.length ? bytes[i] : 0x00;
        }
        return resize;
    }

    public static byte[] cut(final byte[] bytes, final int start, final int end) {

        if (start < 0 || end > bytes.length - 1 || start > end) {
            System.out.println(bytes.length + " s " + start + " " + end);
            throw new IllegalArgumentException("Error in cup position.");
        }

        byte[] cut = new byte[end - start + 1];
        for (int i = 0 , j = start; i < cut.length; i++, j++) {
            cut[i] = bytes[j];
        }
        return cut;
    }

    public static byte[] replace(final byte[] bytes, final int start, final byte[] replaces) {

        if (start < 0 || start + replaces.length > bytes.length) {
            throw new IllegalArgumentException("Error in start position.");
        }

        byte[] replace = new byte[bytes.length];
        for (int i = 0; i < replace.length; i++) {
            replace[i] = i >= start && i < start + replaces.length ? replaces[i - start] : bytes[i];
        }
        return replace;
    }

    public static byte[] dropFreeEnd(final byte[] bytes) {

        int dropFree = 1;
        while (dropFree < bytes.length && bytes[bytes.length - dropFree] == 0x00) {
            ++dropFree;
        }
        return Bytes.cut(bytes, 0, bytes.length - dropFree);
    }

    public static byte[] push(final byte[] bytes, final byte push) {

        byte[] result = new byte[bytes.length + 1];
        for (int i = 0; i < bytes.length; i++) {
            result[i] = bytes[i];
        }
        result[bytes.length] = push;
        return result;
    }

}
