/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.glfw;

import org.lwjgl.system.Callback;

/**
 * Extends {@link GLFWMonitorCallbackI} with a simple factory and {@link #set()} helper.
 */
public abstract class GLFWMonitorCallback extends Callback implements GLFWMonitorCallbackI {

	protected GLFWMonitorCallback() {
		super(0L);
	}

	public static GLFWMonitorCallback create(GLFWMonitorCallbackI instance) {
		return new GLFWMonitorCallback() {
			@Override
			public void invoke(long monitor, int event) {
				instance.invoke(monitor, event);
			}
		};
	}

	/**
	 * Sets this callback as the current GLFW monitor callback and returns the
	 * previously set callback (or null).
	 */
	public GLFWMonitorCallback set() {
		return GLFW.glfwSetMonitorCallback(this);
	}
}
