/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.glfw;

import org.lwjgl.system.Callback;

public abstract class GLFWCursorEnterCallback extends Callback implements GLFWCursorEnterCallbackI {

	protected GLFWCursorEnterCallback() {
		super(0L);
	}
}
