/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.openal;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class AL10 {

	// ---- Error codes ----
	public static final int AL_NO_ERROR = 0;
	public static final int AL_INVALID_NAME = 0xA001;
	public static final int AL_INVALID_ENUM = 0xA002;
	public static final int AL_INVALID_VALUE = 0xA003;
	public static final int AL_INVALID_OPERATION = 0xA004;
	public static final int AL_OUT_OF_MEMORY = 0xA005;

	// ---- State ----
	public static final int AL_DISTANCE_MODEL = 0xD000;
	public static final int AL_DOPPLER_FACTOR = 0xC000;
	public static final int AL_SPEED_OF_SOUND = 0xC003;

	// ---- Source properties ----
	public static final int AL_POSITION = 0x1004;
	public static final int AL_VELOCITY = 0x1006;
	public static final int AL_DIRECTION = 0x1005;
	public static final int AL_GAIN = 0x100A;
	public static final int AL_MIN_GAIN = 0x100D;
	public static final int AL_MAX_GAIN = 0x100E;
	public static final int AL_ORIENTATION = 0x100F;
	public static final int AL_PITCH = 0x1003;
	public static final int AL_MAX_DISTANCE = 0x1023;
	public static final int AL_ROLLOFF_FACTOR = 0x1021;
	public static final int AL_REFERENCE_DISTANCE = 0x1020;
	public static final int AL_CONE_INNER_ANGLE = 0x1001;
	public static final int AL_CONE_OUTER_ANGLE = 0x1002;
	public static final int AL_CONE_OUTER_GAIN = 0x1022;
	public static final int AL_SOURCE_RELATIVE = 0x202;
	public static final int AL_LOOPING = 0x1007;
	public static final int AL_BUFFER = 0x1009;
	public static final int AL_SOURCE_STATE = 0x1010;
	public static final int AL_BUFFERS_QUEUED = 0x1015;
	public static final int AL_BUFFERS_PROCESSED = 0x1016;
	public static final int AL_SEC_OFFSET = 0x1024;
	public static final int AL_SAMPLE_OFFSET = 0x1025;
	public static final int AL_BYTE_OFFSET = 0x1026;
	public static final int AL_SOURCE_TYPE = 0x1027;

	// ---- Source states ----
	public static final int AL_INITIAL = 0x1011;
	public static final int AL_PLAYING = 0x1012;
	public static final int AL_PAUSED = 0x1013;
	public static final int AL_STOPPED = 0x1014;

	// ---- Buffer format ----
	public static final int AL_FORMAT_MONO8 = 0x1100;
	public static final int AL_FORMAT_MONO16 = 0x1101;
	public static final int AL_FORMAT_STEREO8 = 0x1102;
	public static final int AL_FORMAT_STEREO16 = 0x1103;

	// ---- Buffer properties ----
	public static final int AL_FREQUENCY = 0x2001;
	public static final int AL_BITS = 0x2002;
	public static final int AL_CHANNELS = 0x2003;
	public static final int AL_SIZE = 0x2004;

	// ---- Listener ----
	public static final int AL_VENDOR = 0xB001;
	public static final int AL_VERSION = 0xB002;
	public static final int AL_RENDERER = 0xB003;
	public static final int AL_EXTENSIONS = 0xB004;

	// ---- Distance models ----
	public static final int AL_INVERSE_DISTANCE = 0xD001;
	public static final int AL_INVERSE_DISTANCE_CLAMPED = 0xD002;
	public static final int AL_LINEAR_DISTANCE = 0xD003;
	public static final int AL_LINEAR_DISTANCE_CLAMPED = 0xD004;
	public static final int AL_EXPONENT_DISTANCE = 0xD005;
	public static final int AL_EXPONENT_DISTANCE_CLAMPED = 0xD006;
	public static final int AL_NONE = 0;
	public static final int AL_FALSE = 0;
	public static final int AL_TRUE = 1;

	// ---- Source ----
	public static int alGenSources() { return 0; }
	public static void alGenSources(int[] sources) { }
	public static void alGenSources(IntBuffer sources) {
		if (sources == null) {
			return;
		}
		for (int i = sources.position(), l = sources.limit(); i < l; ++i) {
			sources.put(i, 0);
		}
	}
	public static void alDeleteSources(int source) { }
	public static void alDeleteSources(int[] sources) { }
	public static void alDeleteSources(IntBuffer sources) { }
	public static boolean alIsSource(int source) { return false; }
	public static void alSourcePlay(int source) { }
	public static void alSourcePause(int source) { }
	public static void alSourceStop(int source) { }
	public static void alSourceRewind(int source) { }
	public static void alSourcef(int source, int param, float value) { }
	public static void alSource3f(int source, int param, float v1, float v2, float v3) { }
	public static void alSourcefv(int source, int param, float[] values) { }
	public static void alSourcefv(int source, int param, FloatBuffer values) { }
	public static void alSourcei(int source, int param, int value) { }
	public static void alSourceQueueBuffers(int source, int[] buffers) { }
	public static void alSourceQueueBuffers(int source, IntBuffer buffers) { }
	public static void alSourceUnqueueBuffers(int source, int[] buffers) { }
	public static void alSourceUnqueueBuffers(int source, IntBuffer buffers) { }
	public static float alGetSourcef(int source, int param) { return 0f; }
	public static int alGetSourcei(int source, int param) { return 0; }

	// ---- Buffer ----
	public static int alGenBuffers() { return 0; }
	public static void alGenBuffers(int[] buffers) { }
	public static void alGenBuffers(IntBuffer buffers) {
		if (buffers == null) {
			return;
		}
		for (int i = buffers.position(), l = buffers.limit(); i < l; ++i) {
			buffers.put(i, 0);
		}
	}
	public static void alDeleteBuffers(int buffer) { }
	public static void alDeleteBuffers(int[] buffers) { }
	public static void alDeleteBuffers(IntBuffer buffers) { }
	public static boolean alIsBuffer(int buffer) { return false; }
	public static void alBufferData(int buffer, int format, java.nio.ByteBuffer data, int freq) { }
	public static void alBufferData(int buffer, int format, short[] data, int freq) { }
	public static int alGetBufferi(int buffer, int param) { return 0; }

	// ---- Listener ----
	public static void alListenerf(int param, float value) { }
	public static void alListener3f(int param, float v1, float v2, float v3) { }
	public static void alListenerfv(int param, float[] values) { }
	public static void alListenerfv(int param, FloatBuffer values) { }
	public static float alGetListenerf(int param) { return 0f; }

	// ---- State ----
	public static void alEnable(int cap) { }
	public static void alDistanceModel(int model) { }
	public static void alDopplerFactor(float value) { }
	public static void alDopplerVelocity(float value) { }
	public static void alSpeedOfSound(float value) { }

	// ---- Error ----
	public static int alGetError() { return AL_NO_ERROR; }
	public static String alGetString(int param) { return "Eaglercraft WebAudio"; }
	public static boolean alGetBoolean(int param) { return false; }
	public static int alGetInteger(int param) { return 0; }
	public static float alGetFloat(int param) { return 0f; }
	public static double alGetDouble(int param) { return 0.0; }
	public static boolean alIsExtensionPresent(String extName) { return false; }
}
