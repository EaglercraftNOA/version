package org.lwjgl.stb;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.PointerBuffer;

/**
 * Minimal stub for stb_vorbis decoding.
 * Produces no audio data but allows the game to continue.
 */
public final class STBVorbis {

	private STBVorbis() {
	}

	public static long stb_vorbis_open_pushdata(ByteBuffer data, IntBuffer dataOffset, IntBuffer error,
			STBVorbisAlloc alloc) {
		if (dataOffset != null) {
			dataOffset.put(0, 0);
		}
		if (error != null) {
			error.put(0, 0);
		}
		return 1L;
	}

	public static void stb_vorbis_get_info(long handle, STBVorbisInfo info) {
		if (info != null) {
			info.setChannels(2);
			info.setSampleRate(44100);
		}
	}

	public static int stb_vorbis_decode_frame_pushdata(long handle, ByteBuffer data, IntBuffer channels,
			PointerBuffer output, IntBuffer samples) {
		if (channels != null) {
			channels.put(0, 2);
		}
		if (samples != null) {
			samples.put(0, 0);
		}
		int remaining = data != null ? data.remaining() : 0;
		return remaining;
	}

	public static int stb_vorbis_get_error(long handle) {
		return 1;
	}

	public static void stb_vorbis_close(long handle) {
	}
}
