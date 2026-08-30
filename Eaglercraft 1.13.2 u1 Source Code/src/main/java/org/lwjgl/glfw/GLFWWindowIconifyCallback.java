/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.glfw;

import org.lwjgl.system.Callback;

public abstract class GLFWWindowIconifyCallback extends Callback implements GLFWWindowIconifyCallbackI {

	protected GLFWWindowIconifyCallback() {
		super(0L);
	}
}
