/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.glfw;

import org.lwjgl.system.Callback;

public abstract class GLFWCharCallback extends Callback implements GLFWCharCallbackI {

	protected GLFWCharCallback() {
		super(0L);
	}
}
