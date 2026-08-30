/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.glfw;

import org.lwjgl.system.Callback;

public abstract class GLFWMouseButtonCallback extends Callback implements GLFWMouseButtonCallbackI {

	protected GLFWMouseButtonCallback() {
		super(0L);
	}
}
