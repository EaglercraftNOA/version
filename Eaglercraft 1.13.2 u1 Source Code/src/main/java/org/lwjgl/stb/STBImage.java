/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.stb;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import net.lax1dude.eaglercraft.v1_8.opengl.ImageData;
import org.lwjgl.system.MemoryUtil;

/**
 * stb_image bindings. This stub decodes images using Eaglercraft's ImageData loader.
 */
public final class STBImage {

	public static final int STBI_default = 0;
	public static final int STBI_grey = 1;
	public static final int STBI_grey_alpha = 2;
	public static final int STBI_rgb = 3;
	public static final int STBI_rgb_alpha = 4;

	private static String lastFailure = "STB image not available under Eaglercraft";

	private STBImage() {
	}

	public static ByteBuffer stbi_load_from_memory(ByteBuffer buffer, IntBuffer x, IntBuffer y,
			IntBuffer channels_in_file, int desired_channels) {
		if (buffer == null) {
			lastFailure = "Input buffer is null";
			return null;
		}
		int pos = buffer.position();
		byte[] data = new byte[buffer.remaining()];
		buffer.get(data);
		buffer.position(pos);

		ImageData img = ImageData.loadImageFile(data);
		if (img == null) {
			lastFailure = "Image decode failed";
			return null;
		}

		int width = img.width;
		int height = img.height;
		int nativeChannels = img.alpha ? 4 : 3;
		int outChannels = desired_channels != 0 ? desired_channels : nativeChannels;

		if (x != null) x.put(0, width);
		if (y != null) y.put(0, height);
		if (channels_in_file != null) channels_in_file.put(0, nativeChannels);

		int byteCount = width * height * outChannels;
		long ptr = MemoryUtil.nmemAlloc(byteCount);
		ByteBuffer out = MemoryUtil.memByteBuffer(ptr, byteCount);
		if (out == null) {
			lastFailure = "Allocation failed";
			return null;
		}

		int[] pixels = img.pixels;
		for (int i = 0; i < pixels.length; i++) {
			// ImageData stores pixels as ABGR integers (A in high byte, R in low byte).
			int abgr = pixels[i];
			int r = abgr & 0xFF;
			int g = (abgr >>> 8) & 0xFF;
			int b = (abgr >>> 16) & 0xFF;
			int a = (abgr >>> 24) & 0xFF;
			switch (outChannels) {
			case STBI_grey:
				out.put((byte) ((77 * r + 150 * g + 29 * b) >>> 8));
				break;
			case STBI_grey_alpha:
				out.put((byte) ((77 * r + 150 * g + 29 * b) >>> 8));
				out.put((byte) a);
				break;
			case STBI_rgb:
				out.put((byte) r);
				out.put((byte) g);
				out.put((byte) b);
				break;
			case STBI_rgb_alpha:
			default:
				out.put((byte) r);
				out.put((byte) g);
				out.put((byte) b);
				out.put((byte) a);
				break;
			}
		}
		out.flip();
		return out;
	}

	public static void stbi_image_free(ByteBuffer image) {
		if (image != null) {
			MemoryUtil.memFree(image);
		}
	}

	public static void nstbi_image_free(long address) {
		MemoryUtil.nmemFree(address);
	}

	public static String stbi_failure_reason() {
		return lastFailure;
	}

	public static void stbi_set_flip_vertically_on_load(boolean flip) {
	}
}
