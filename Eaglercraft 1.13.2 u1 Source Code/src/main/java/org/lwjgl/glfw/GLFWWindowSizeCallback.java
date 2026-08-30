/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.glfw;

import org.lwjgl.system.Callback;

public abstract class GLFWWindowSizeCallback extends Callback implements GLFWWindowSizeCallbackI {

	protected GLFWWindowSizeCallback() {
		super(0L);
	}
}
