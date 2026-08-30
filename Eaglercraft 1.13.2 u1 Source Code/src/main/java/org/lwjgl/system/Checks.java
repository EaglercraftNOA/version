/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.system;

import net.lax1dude.eaglercraft.v1_8.internal.buffer.Buffer;

public final class Checks {

	private Checks() {}

	public static final boolean CHECKS = true;
	public static final boolean DEBUG = false;
	public static final boolean DEBUG_FUNCTIONS = false;

	public static <T> T check(T value) {
		if (value == null) {
			throw new NullPointerException();
		}
		return value;
	}

	public static long check(long pointer) {
		if (pointer == 0L) {
			throw new NullPointerException();
		}
		return pointer;
	}

	public static void check(Buffer buf, int size) {
		if (buf.remaining() < size) {
			throw new IllegalArgumentException("Buffer too small: remaining = " + buf.remaining() + ", required = " + size);
		}
	}

	public static int checkNT(int value) {
		if (value < 0) {
			throw new IllegalArgumentException("Negative value: " + value);
		}
		return value;
	}

	public static long checkFunctionAddress(long functionAddress) {
		if (functionAddress == 0L) {
			throw new IllegalStateException("This function is not available.");
		}
		return functionAddress;
	}

	public static void checkGLError(int error) {
		if (error != 0) {
			throw new IllegalStateException("OpenGL error: " + error);
		}
	}

	public static int castToInt(long value) {
		if (value < Integer.MIN_VALUE || value > Integer.MAX_VALUE) {
			throw new ArithmeticException("Value does not fit in int: " + value);
		}
		return (int) value;
	}
}
