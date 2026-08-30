/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.glfw;

import org.lwjgl.system.Callback;

public abstract class GLFWDropCallback extends Callback implements GLFWDropCallbackI {

	protected GLFWDropCallback() {
		super(0L);
	}

	public static String getName(long names, int index) {
		return GLFW.getDropName(names, index);
	}
}
