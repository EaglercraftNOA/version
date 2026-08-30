/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.stb;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public final class STBTruetype {

	private STBTruetype() {
	}

	public static boolean stbtt_InitFont(STBTTFontinfo info, ByteBuffer data) {
		return true;
	}

	public static boolean stbtt_InitFont(STBTTFontinfo info, ByteBuffer data, int offset) {
		return true;
	}

	public static float stbtt_ScaleForPixelHeight(STBTTFontinfo info, float pixels) {
		return 1.0f;
	}

	public static float stbtt_ScaleForMappingEmToPixels(STBTTFontinfo info, float pixels) {
		return 1.0f;
	}

	public static void stbtt_GetFontVMetrics(STBTTFontinfo info, IntBuffer ascent, IntBuffer descent, IntBuffer lineGap) {
		if (ascent != null) ascent.put(0, 0);
		if (descent != null) descent.put(0, 0);
		if (lineGap != null) lineGap.put(0, 0);
	}

	public static int stbtt_FindGlyphIndex(STBTTFontinfo info, int unicode_codepoint) {
		return 0;
	}

	public static void stbtt_GetGlyphBitmapBoxSubpixel(STBTTFontinfo info, int glyph, float scale_x, float scale_y,
			float shift_x, float shift_y, IntBuffer x0, IntBuffer y0, IntBuffer x1, IntBuffer y1) {
		if (x0 != null) x0.put(0, 0);
		if (y0 != null) y0.put(0, 0);
		if (x1 != null) x1.put(0, 0);
		if (y1 != null) y1.put(0, 0);
	}

	public static void stbtt_GetGlyphHMetrics(STBTTFontinfo info, int glyph, IntBuffer advanceWidth,
			IntBuffer leftSideBearing) {
		if (advanceWidth != null) advanceWidth.put(0, 0);
		if (leftSideBearing != null) leftSideBearing.put(0, 0);
	}

	public static void nstbtt_MakeGlyphBitmapSubpixel(long info, long output, int out_w, int out_h, int out_stride,
			float scale_x, float scale_y, float shift_x, float shift_y, int glyph) {
	}
}
