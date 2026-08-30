/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.glfw;

import org.lwjgl.system.Callback;

public abstract class GLFWWindowCloseCallback extends Callback implements GLFWWindowCloseCallbackI {

	protected GLFWWindowCloseCallback() {
		super(0L);
	}
}
