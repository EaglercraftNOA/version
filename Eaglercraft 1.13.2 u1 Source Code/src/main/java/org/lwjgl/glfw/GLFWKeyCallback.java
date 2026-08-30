/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.glfw;

import org.lwjgl.system.Callback;

public abstract class GLFWKeyCallback extends Callback implements GLFWKeyCallbackI {

	protected GLFWKeyCallback() {
		super(0L);
	}
}
