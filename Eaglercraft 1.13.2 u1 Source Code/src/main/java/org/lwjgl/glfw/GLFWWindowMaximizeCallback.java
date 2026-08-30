/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.glfw;

import org.lwjgl.system.Callback;

public abstract class GLFWWindowMaximizeCallback extends Callback implements GLFWWindowMaximizeCallbackI {

	protected GLFWWindowMaximizeCallback() {
		super(0L);
	}
}
