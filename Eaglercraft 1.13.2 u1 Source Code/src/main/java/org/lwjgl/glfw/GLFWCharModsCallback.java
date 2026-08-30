/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.glfw;

import org.lwjgl.system.Callback;

public abstract class GLFWCharModsCallback extends Callback implements GLFWCharModsCallbackI {

	protected GLFWCharModsCallback() {
		super(0L);
	}
}
