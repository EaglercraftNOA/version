/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.glfw;

import org.lwjgl.system.Callback;

public abstract class GLFWWindowPosCallback extends Callback implements GLFWWindowPosCallbackI {

	protected GLFWWindowPosCallback() {
		super(0L);
	}
}
