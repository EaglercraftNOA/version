package org.lwjgl.stb;

import org.lwjgl.system.MemoryStack;

/**
 * Stub for stb_vorbis_info.
 */
public class STBVorbisInfo {

	private int sampleRate = 44100;
	private int channels = 2;

	public static STBVorbisInfo mallocStack(MemoryStack stack) {
		return new STBVorbisInfo();
	}

	public int sample_rate() {
		return sampleRate;
	}

	public int channels() {
		return channels;
	}

	void setSampleRate(int sampleRate) {
		this.sampleRate = sampleRate;
	}

	void setChannels(int channels) {
		this.channels = channels;
	}
}
