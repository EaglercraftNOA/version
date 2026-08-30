/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.stb;

public final class STBImageResize {

	private STBImageResize() {
	}

	public static boolean stbir_resize_uint8(
			byte[] input_pixels, int input_w, int input_h, int input_stride_in_bytes,
			byte[] output_pixels, int output_w, int output_h, int output_stride_in_bytes,
			int num_channels) {
		throw new UnsupportedOperationException("stb_image_resize not available under Eaglercraft");
	}

	public static void nstbir_resize_uint8(long input_pixels, int input_w, int input_h, int input_stride_in_bytes,
			long output_pixels, int output_w, int output_h, int output_stride_in_bytes,
			int num_channels) {
		if (input_pixels == 0L || output_pixels == 0L) {
			return;
		}
		int inStride = input_stride_in_bytes;
		int outStride = output_stride_in_bytes != 0 ? output_stride_in_bytes : output_w * num_channels;
		java.nio.ByteBuffer inBuf = org.lwjgl.system.MemoryUtil.memByteBuffer(input_pixels, inStride * input_h);
		java.nio.ByteBuffer outBuf = org.lwjgl.system.MemoryUtil.memByteBuffer(output_pixels, outStride * output_h);
		if (inBuf == null || outBuf == null) {
			return;
		}
		for (int y = 0; y < output_h; y++) {
			int srcY = (int) ((long) y * input_h / output_h);
			for (int x = 0; x < output_w; x++) {
				int srcX = (int) ((long) x * input_w / output_w);
				int srcIndex = srcY * inStride + srcX * num_channels;
				int dstIndex = y * outStride + x * num_channels;
				for (int c = 0; c < num_channels; c++) {
					outBuf.put(dstIndex + c, inBuf.get(srcIndex + c));
				}
			}
		}
	}
}
