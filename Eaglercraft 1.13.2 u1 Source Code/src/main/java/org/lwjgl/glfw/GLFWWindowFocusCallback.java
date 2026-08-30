/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.glfw;

import org.lwjgl.system.Callback;

public abstract class GLFWWindowFocusCallback extends Callback implements GLFWWindowFocusCallbackI {

	protected GLFWWindowFocusCallback() {
		super(0L);
	}
}
