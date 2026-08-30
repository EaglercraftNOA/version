/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.glfw;

import org.lwjgl.system.Callback;

public abstract class GLFWScrollCallback extends Callback implements GLFWScrollCallbackI {

	protected GLFWScrollCallback() {
		super(0L);
	}
}
