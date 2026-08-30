/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.stb;

import java.nio.ByteBuffer;

public final class STBImageWrite {

	private STBImageWrite() {
	}

	public static boolean stbi_write_png(String filename, int w, int h, int comp, ByteBuffer data, int stride_in_bytes) {
		return false;
	}

	public static boolean stbi_write_bmp(String filename, int w, int h, int comp, ByteBuffer data) {
		return false;
	}

	public static boolean stbi_write_tga(String filename, int w, int h, int comp, ByteBuffer data) {
		return false;
	}

	public static boolean stbi_write_jpg(String filename, int w, int h, int comp, ByteBuffer data, int quality) {
		return false;
	}

	public static boolean stbi_write_png_to_func(STBIWriteCallback func, long context, int w, int h, int comp,
			ByteBuffer data, int stride_in_bytes) {
		return false;
	}

	public static int nstbi_write_png_to_func(long func, long context, int w, int h, int comp, long data,
			int stride_in_bytes) {
		return 0;
	}

	public static void stbi_flip_vertically_on_write(boolean flip) {
	}
}
