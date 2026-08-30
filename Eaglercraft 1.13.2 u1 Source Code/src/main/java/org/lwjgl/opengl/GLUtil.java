/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.opengl;

import net.lax1dude.eaglercraft.v1_8.opengl.EaglercraftGPU;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;

public final class GLUtil {

	private GLUtil() {
	}

	public static String getErrorString(int glError) {
		return EaglercraftGPU.gluErrorString(glError);
	}

	public static void gluPerspective(float fovy, float aspect, float zNear, float zFar) {
		GlStateManager.gluPerspective(fovy, aspect, zNear, zFar);
	}
}
