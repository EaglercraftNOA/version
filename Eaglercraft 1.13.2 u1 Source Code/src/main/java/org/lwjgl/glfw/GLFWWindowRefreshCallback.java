/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.glfw;

import org.lwjgl.system.Callback;

public abstract class GLFWWindowRefreshCallback extends Callback implements GLFWWindowRefreshCallbackI {

	protected GLFWWindowRefreshCallback() {
		super(0L);
	}
}
