/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

/**
 * Buffer utility methods for LWJGL3.
 */
public final class BufferUtils {

	private BufferUtils() {
	}

	public static ByteBuffer createByteBuffer(int capacity) {
		return ByteBuffer.allocateDirect(capacity).order(ByteOrder.nativeOrder());
	}

	public static ShortBuffer createShortBuffer(int capacity) {
		return createByteBuffer(capacity * 2).asShortBuffer();
	}

	public static IntBuffer createIntBuffer(int capacity) {
		return createByteBuffer(capacity * 4).asIntBuffer();
	}

	public static FloatBuffer createFloatBuffer(int capacity) {
		return createByteBuffer(capacity * 4).asFloatBuffer();
	}

	public static void zeroBuffer(ByteBuffer buffer) {
		for (int i = 0; i < buffer.capacity(); i++) {
			buffer.put(i, (byte) 0);
		}
	}

	public static void zeroBuffer(ShortBuffer buffer) {
		for (int i = 0; i < buffer.capacity(); i++) {
			buffer.put(i, (short) 0);
		}
	}

	public static void zeroBuffer(IntBuffer buffer) {
		for (int i = 0; i < buffer.capacity(); i++) {
			buffer.put(i, 0);
		}
	}

	public static void zeroBuffer(FloatBuffer buffer) {
		for (int i = 0; i < buffer.capacity(); i++) {
			buffer.put(i, 0.0f);
		}
	}
}
