/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
*/
package org.lwjgl.glfw;

/**
 * Utility class with convenience methods for GLFW callbacks.
 */
public final class Callbacks {

	private Callbacks() {
	}

	/**
	 * Resets all callbacks for the specified GLFW window to {@code null}.
	 * This is typically called before destroying a window.
	 *
	 * @param window the GLFW window handle
	 */
	public static void glfwFreeCallbacks(long window) {
		GLFW.glfwSetKeyCallback(window, null);
		GLFW.glfwSetCharCallback(window, null);
		GLFW.glfwSetCharModsCallback(window, null);
		GLFW.glfwSetMouseButtonCallback(window, null);
		GLFW.glfwSetCursorPosCallback(window, null);
		GLFW.glfwSetCursorEnterCallback(window, null);
		GLFW.glfwSetScrollCallback(window, null);
		GLFW.glfwSetDropCallback(window, null);
		GLFW.glfwSetWindowSizeCallback(window, null);
		GLFW.glfwSetWindowCloseCallback(window, null);
		GLFW.glfwSetWindowFocusCallback(window, null);
		GLFW.glfwSetWindowIconifyCallback(window, null);
		GLFW.glfwSetWindowMaximizeCallback(window, null);
		GLFW.glfwSetWindowPosCallback(window, null);
		GLFW.glfwSetWindowRefreshCallback(window, null);
		GLFW.glfwSetWindowContentScaleCallback(window, null);
		GLFW.glfwSetFramebufferSizeCallback(window, null);
	}
}
