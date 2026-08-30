/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.glfw;

import org.lwjgl.system.Callback;

public abstract class GLFWFramebufferSizeCallback extends Callback implements GLFWFramebufferSizeCallbackI {

	protected GLFWFramebufferSizeCallback() {
		super(0L);
	}
}
