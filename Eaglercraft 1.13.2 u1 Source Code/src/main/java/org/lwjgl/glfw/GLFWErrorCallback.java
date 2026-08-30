/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.glfw;

import org.lwjgl.system.Callback;

/**
 * Extends {@link GLFWErrorCallbackI} with a static {@link #createPrint} factory
 * and a human-readable {@link #getDescription} method.
 */
public abstract class GLFWErrorCallback extends Callback implements GLFWErrorCallbackI {

	protected GLFWErrorCallback() {
		super(0L);
	}

	public static GLFWErrorCallback create(GLFWErrorCallbackI instance) {
		return new GLFWErrorCallback() {
			@Override
			public void invoke(int error, long description) {
				instance.invoke(error, description);
			}
		};
	}

	/**
	 * Creates a GLFWErrorCallback that prints errors to System.err.
	 */
	public static GLFWErrorCallback createPrint() {
		return createPrint(System.err);
	}

	/**
	 * Creates a GLFWErrorCallback that prints errors to the given stream.
	 */
	public static GLFWErrorCallback createPrint(java.io.PrintStream stream) {
		return new GLFWErrorCallback() {
			@Override
			public void invoke(int error, long description) {
				stream.println("[LWJGL] GLFW error " + error + ": " + getDescription(description));
			}
		};
	}

	/**
	 * Creates a GLFWErrorCallback that throws an IllegalStateException.
	 */
	public static GLFWErrorCallback createThrow() {
		return new GLFWErrorCallback() {
			@Override
			public void invoke(int error, long description) {
				throw new IllegalStateException("GLFW error " + error + ": " + getDescription(description));
			}
		};
	}

	/**
	 * Converts an error description pointer to a String. In TeaVM, the description
	 * is stored as a hash-code reference; we return a placeholder.
	 */
	public static String getDescription(long description) {
		// In native LWJGL3, this would be memUTF8(description).
		// Under TeaVM, error descriptions are not native strings.
		return "error code " + description;
	}

	/**
	 * Sets this callback as the current GLFW error callback and returns the
	 * previously set callback (or null).
	 */
	public GLFWErrorCallback set() {
		return GLFW.glfwSetErrorCallback(this);
	}
}
