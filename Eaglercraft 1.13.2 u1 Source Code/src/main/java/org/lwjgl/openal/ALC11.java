/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.openal;

public class ALC11 extends ALC10 {

	public static final int ALC_MONO_SOURCES = 0x1010;
	public static final int ALC_STEREO_SOURCES = 0x1011;
	public static final int ALC_CAPTURE_DEVICE_SPECIFIER = 0x310;
	public static final int ALC_CAPTURE_DEFAULT_DEVICE_SPECIFIER = 0x311;
	public static final int ALC_CAPTURE_SAMPLES = 0x312;

	public static long alcCaptureOpenDevice(CharSequence deviceName, int frequency, int format, int bufferSize) {
		return 0L; // Capture not supported
	}

	public static boolean alcCaptureCloseDevice(long device) { return true; }
	public static void alcCaptureStart(long device) { }
	public static void alcCaptureStop(long device) { }
	public static void alcCaptureSamples(long device, java.nio.ByteBuffer buffer, int samples) { }
}
