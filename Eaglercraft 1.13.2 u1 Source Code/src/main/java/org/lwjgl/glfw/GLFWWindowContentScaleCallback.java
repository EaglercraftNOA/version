/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.glfw;

import org.lwjgl.system.Callback;

public abstract class GLFWWindowContentScaleCallback extends Callback implements GLFWWindowContentScaleCallbackI {

	protected GLFWWindowContentScaleCallback() {
		super(0L);
	}
}
