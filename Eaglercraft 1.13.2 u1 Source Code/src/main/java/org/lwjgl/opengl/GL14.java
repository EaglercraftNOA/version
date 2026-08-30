/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.opengl;

import net.lax1dude.eaglercraft.v1_8.opengl.EaglercraftGPU;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;

public class GL14 extends GL13 {

	public static final int GL_BLEND_DST_RGB = 0x80C8;
	public static final int GL_BLEND_SRC_RGB = 0x80C9;
	public static final int GL_BLEND_DST_ALPHA = 0x80CA;
	public static final int GL_BLEND_SRC_ALPHA = 0x80CB;
	public static final int GL_POINT_FADE_THRESHOLD_SIZE = 0x8128;
	public static final int GL_DEPTH_COMPONENT16 = 0x81A5;
	public static final int GL_DEPTH_COMPONENT24 = 0x81A6;
	public static final int GL_DEPTH_COMPONENT32 = 0x81A7;
	public static final int GL_MIRRORED_REPEAT = 0x8370;
	public static final int GL_MAX_TEXTURE_LOD_BIAS = 0x84FD;
	public static final int GL_TEXTURE_LOD_BIAS = 0x8501;
	public static final int GL_INCR_WRAP = 0x8507;
	public static final int GL_DECR_WRAP = 0x8508;
	public static final int GL_TEXTURE_DEPTH_SIZE = 0x884A;
	public static final int GL_TEXTURE_COMPARE_MODE = 0x884C;
	public static final int GL_TEXTURE_COMPARE_FUNC = 0x884D;
	public static final int GL_FUNC_ADD = 0x8006;
	public static final int GL_FUNC_SUBTRACT = 0x800A;
	public static final int GL_FUNC_REVERSE_SUBTRACT = 0x800B;
	public static final int GL_MIN = 0x8007;
	public static final int GL_MAX = 0x8008;
	public static final int GL_CONSTANT_COLOR = 0x8001;
	public static final int GL_ONE_MINUS_CONSTANT_COLOR = 0x8002;
	public static final int GL_CONSTANT_ALPHA = 0x8003;
	public static final int GL_ONE_MINUS_CONSTANT_ALPHA = 0x8004;

	public static void glBlendFuncSeparate(int sfactorRGB, int dfactorRGB, int sfactorAlpha, int dfactorAlpha) {
		GlStateManager.tryBlendFuncSeparate(sfactorRGB, dfactorRGB, sfactorAlpha, dfactorAlpha);
	}

	public static void glBlendEquation(int mode) {
		EaglercraftGPU.glBlendEquation(mode);
	}

	public static void glBlendColor(float red, float green, float blue, float alpha) {
		GlStateManager.setBlendConstants(red, green, blue, alpha);
	}
}
