/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.openal;

public class ALC10 {

	public static final int ALC_FALSE = 0;
	public static final int ALC_TRUE = 1;
	public static final int ALC_FREQUENCY = 0x1007;
	public static final int ALC_REFRESH = 0x1008;
	public static final int ALC_SYNC = 0x1009;
	public static final int ALC_NO_ERROR = 0;
	public static final int ALC_INVALID_DEVICE = 0xA001;
	public static final int ALC_INVALID_CONTEXT = 0xA002;
	public static final int ALC_INVALID_ENUM = 0xA003;
	public static final int ALC_INVALID_VALUE = 0xA004;
	public static final int ALC_OUT_OF_MEMORY = 0xA005;
	public static final int ALC_MAJOR_VERSION = 0x1000;
	public static final int ALC_MINOR_VERSION = 0x1001;
	public static final int ALC_ATTRIBUTES_SIZE = 0x1002;
	public static final int ALC_ALL_ATTRIBUTES = 0x1003;
	public static final int ALC_DEFAULT_DEVICE_SPECIFIER = 0x1004;
	public static final int ALC_DEVICE_SPECIFIER = 0x1005;
	public static final int ALC_EXTENSIONS = 0x1006;

	/** Opens a device. */
	public static long alcOpenDevice(CharSequence deviceName) { return 1L; }
	public static long alcOpenDevice(java.nio.ByteBuffer deviceName) { return 1L; }

	/** Closes a device. */
	public static boolean alcCloseDevice(long device) { return true; }

	/** Creates a context. */
	public static long alcCreateContext(long device, int[] attrList) { return 1L; }
	public static long alcCreateContext(long device, java.nio.IntBuffer attrList) { return 1L; }

	/** Makes a context current. */
	public static boolean alcMakeContextCurrent(long context) { return true; }

	/** Processes a context. */
	public static void alcProcessContext(long context) { }

	/** Suspends a context. */
	public static void alcSuspendContext(long context) { }

	/** Destroys a context. */
	public static void alcDestroyContext(long context) { }

	/** Returns the current context. */
	public static long alcGetCurrentContext() { return 1L; }

	/** Returns the device for a context. */
	public static long alcGetContextsDevice(long context) { return 1L; }

	public static int alcGetError(long device) { return ALC_NO_ERROR; }

	public static boolean alcIsExtensionPresent(long device, CharSequence extName) { return false; }

	public static String alcGetString(long device, int param) {
		switch (param) {
		case ALC_DEFAULT_DEVICE_SPECIFIER:
		case ALC_DEVICE_SPECIFIER:
			return "Eaglercraft WebAudio";
		case ALC_EXTENSIONS:
			return "";
		default:
			return null;
		}
	}

	public static int alcGetInteger(long device, int param) {
		switch (param) {
		case ALC_MAJOR_VERSION: return 1;
		case ALC_MINOR_VERSION: return 1;
		default: return 0;
		}
	}

	public static void alcGetIntegerv(long device, int param, java.nio.IntBuffer values) {
		if (values != null && values.remaining() > 0) {
			values.put(values.position(), alcGetInteger(device, param));
		}
	}
}
