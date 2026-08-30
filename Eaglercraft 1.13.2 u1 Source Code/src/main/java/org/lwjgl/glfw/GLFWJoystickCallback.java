/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.glfw;

import org.lwjgl.system.Callback;

public abstract class GLFWJoystickCallback extends Callback implements GLFWJoystickCallbackI {

	protected GLFWJoystickCallback() {
		super(0L);
	}
}
