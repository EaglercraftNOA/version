/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.glfw;

import org.lwjgl.system.Callback;

public abstract class GLFWCursorPosCallback extends Callback implements GLFWCursorPosCallbackI {

	protected GLFWCursorPosCallback() {
		super(0L);
	}
}
