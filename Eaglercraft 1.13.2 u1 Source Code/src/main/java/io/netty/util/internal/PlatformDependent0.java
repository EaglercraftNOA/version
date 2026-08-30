/*
 * Copyright 2013 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 */
package io.netty.util.internal;

import java.nio.ByteBuffer;

final class PlatformDependent0 {
    private static final Throwable UNSAFE_UNAVAILABILITY_CAUSE =
            new UnsupportedOperationException("direct memory access is not available in the Eaglercraft runtime");
    private static final int JAVA_VERSION = javaVersion0();
    static final Object UNSAFE = null;

    static final int HASH_CODE_ASCII_SEED = 0xc2b2ae35;
    static final int HASH_CODE_C1 = 0xcc9e2d51;
    static final int HASH_CODE_C2 = 0x1b873593;

    private PlatformDependent0() {
    }

    static boolean isExplicitNoUnsafe() {
        return true;
    }

    static boolean isUnaligned() {
        return false;
    }

    static boolean hasUnsafe() {
        return false;
    }

    static Throwable getUnsafeUnavailabilityCause() {
        return UNSAFE_UNAVAILABILITY_CAUSE;
    }

    static boolean unalignedAccess() {
        return false;
    }

    static void throwException(Throwable cause) {
        ObjectUtil.checkNotNull(cause, "cause");
        if (cause instanceof RuntimeException) {
            throw (RuntimeException) cause;
        }
        if (cause instanceof Error) {
            throw (Error) cause;
        }
        throw new RuntimeException(cause);
    }

    static boolean hasDirectBufferNoCleanerConstructor() {
        return false;
    }

    static ByteBuffer reallocateDirectNoCleaner(ByteBuffer buffer, int capacity) {
        ByteBuffer replacement = ByteBuffer.allocateDirect(capacity);
        ByteBuffer copy = buffer.duplicate();
        copy.clear();
        copy.limit(Math.min(copy.remaining(), capacity));
        replacement.put(copy);
        replacement.clear();
        return replacement;
    }

    static ByteBuffer allocateDirectNoCleaner(int capacity) {
        return ByteBuffer.allocateDirect(capacity);
    }

    static boolean hasAllocateArrayMethod() {
        return false;
    }

    static byte[] allocateUninitializedArray(int size) {
        return new byte[size];
    }

    static ByteBuffer newDirectBuffer(long address, int capacity) {
        return ByteBuffer.allocateDirect(capacity);
    }

    static long directBufferAddress(ByteBuffer buffer) {
        return 0L;
    }

    static long byteArrayBaseOffset() {
        return 0L;
    }

    static Object getObject(Object object, long fieldOffset) {
        return null;
    }

    static int getInt(Object object, long fieldOffset) {
        return 0;
    }

    static long objectFieldOffset(Object field) {
        return -1L;
    }

    static byte getByte(long address) {
        return 0;
    }

    static short getShort(long address) {
        return 0;
    }

    static int getInt(long address) {
        return 0;
    }

    static long getLong(long address) {
        return 0L;
    }

    static byte getByte(byte[] data, int index) {
        return data[index];
    }

    static short getShort(byte[] data, int index) {
        return (short) ((data[index] & 255) << 8 | data[index + 1] & 255);
    }

    static int getInt(byte[] data, int index) {
        return (data[index] & 255) << 24 | (data[index + 1] & 255) << 16 | (data[index + 2] & 255) << 8 | data[index + 3] & 255;
    }

    static long getLong(byte[] data, int index) {
        return ((long)getInt(data, index) & 0xffffffffL) << 32 | (long)getInt(data, index + 4) & 0xffffffffL;
    }

    static void putByte(long address, byte value) {
    }

    static void putShort(long address, short value) {
    }

    static void putInt(long address, int value) {
    }

    static void putLong(long address, long value) {
    }

    static void putByte(byte[] data, int index, byte value) {
        data[index] = value;
    }

    static void putShort(byte[] data, int index, short value) {
        data[index] = (byte)(value >>> 8);
        data[index + 1] = (byte)value;
    }

    static void putInt(byte[] data, int index, int value) {
        data[index] = (byte)(value >>> 24);
        data[index + 1] = (byte)(value >>> 16);
        data[index + 2] = (byte)(value >>> 8);
        data[index + 3] = (byte)value;
    }

    static void putLong(byte[] data, int index, long value) {
        putInt(data, index, (int)(value >>> 32));
        putInt(data, index + 4, (int)value);
    }

    static void copyMemory(long srcAddr, long dstAddr, long length) {
    }

    static void copyMemory(Object src, long srcOffset, Object dst, long dstOffset, long length) {
        if (src instanceof byte[] && dst instanceof byte[]) {
            System.arraycopy(src, (int)srcOffset, dst, (int)dstOffset, (int)length);
        }
    }

    static void setMemory(long address, long bytes, byte value) {
    }

    static void setMemory(Object o, long offset, long bytes, byte value) {
        if (o instanceof byte[]) {
            byte[] data = (byte[])o;
            for (int i = 0; i < bytes; ++i) {
                data[(int)offset + i] = value;
            }
        }
    }

    static boolean equals(byte[] bytes1, int startPos1, byte[] bytes2, int startPos2, int length) {
        for (int i = 0; i < length; ++i) {
            if (bytes1[startPos1 + i] != bytes2[startPos2 + i]) {
                return false;
            }
        }
        return true;
    }

    static int equalsConstantTime(byte[] bytes1, int startPos1, byte[] bytes2, int startPos2, int length) {
        int result = 0;
        for (int i = 0; i < length; ++i) {
            result |= bytes1[startPos1 + i] ^ bytes2[startPos2 + i];
        }
        return ConstantTimeUtils.equalsConstantTime(result, 0);
    }

    static boolean isZero(byte[] bytes, int startPos, int length) {
        for (int i = 0; i < length; ++i) {
            if (bytes[startPos + i] != 0) {
                return false;
            }
        }
        return true;
    }

    static int hashCodeAscii(byte[] bytes, int startPos, int length) {
        int hash = HASH_CODE_ASCII_SEED;
        for (int i = startPos; i < startPos + length; ++i) {
            hash = hash * HASH_CODE_C1 + hashCodeAsciiSanitize(bytes[i]);
        }
        return hash;
    }

    static int hashCodeAsciiCompute(long value, int hash) {
        return hash * HASH_CODE_C1 +
                hashCodeAsciiSanitize((int) value) * HASH_CODE_C2 +
                (int) ((value & 0x1f1f1f1f00000000L) >>> 32);
    }

    static int hashCodeAsciiSanitize(int value) {
        return value & 0x1f1f1f1f;
    }

    static int hashCodeAsciiSanitize(short value) {
        return value & 0x1f1f;
    }

    static int hashCodeAsciiSanitize(byte value) {
        return value & 0x1f;
    }

    static Object getLoaderToken(final Class<?> clazz) {
        return null;
    }

    static Object getContextLoaderToken() {
        return null;
    }

    static Object getSystemLoaderToken() {
        return null;
    }

    static int addressSize() {
        return 0;
    }

    static long allocateMemory(long size) {
        return 0L;
    }

    static void freeMemory(long address) {
    }

    static long reallocateMemory(long address, long newSize) {
        return 0L;
    }

    static boolean isAndroid() {
        return false;
    }

    static boolean isExplicitTryReflectionSetAccessible() {
        return false;
    }

    static int javaVersion() {
        return JAVA_VERSION;
    }

    private static int javaVersion0() {
        return majorVersion(SystemPropertyUtil.get("java.specification.version", "1.8"));
    }

    static int majorVersionFromJavaSpecificationVersion() {
        return majorVersion(SystemPropertyUtil.get("java.specification.version", "1.8"));
    }

    static int majorVersion(final String javaSpecVersion) {
        String[] components = javaSpecVersion.split("\\.");
        if (components.length == 0) {
            return 8;
        }
        int first = Integer.parseInt(components[0]);
        return first == 1 && components.length > 1 ? Integer.parseInt(components[1]) : first;
    }
}
