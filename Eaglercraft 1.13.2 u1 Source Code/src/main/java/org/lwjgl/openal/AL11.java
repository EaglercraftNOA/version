/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.openal;

public class AL11 extends AL10 {

	public static final int AL_STATIC = 0x1028;
	public static final int AL_STREAMING = 0x1029;
	public static final int AL_UNDETERMINED = 0x1030;
	public static final int AL_LINEAR_DISTANCE = 0xD003;
	public static final int AL_LINEAR_DISTANCE_CLAMPED = 0xD004;
	public static final int AL_EXPONENT_DISTANCE = 0xD005;
	public static final int AL_EXPONENT_DISTANCE_CLAMPED = 0xD006;

	public static void alSource3i(int source, int param, int v1, int v2, int v3) { }
	public static void alSourceQueueBuffers(int source, int buffer) { }
	public static int alSourceUnqueueBuffers(int source) { return 0; }
}
