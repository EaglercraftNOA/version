/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 * New code should use org.lwjgl.glfw.GLFW directly.
 */
package org.lwjgl.opengl;

import org.lwjgl.LWJGLException;
import org.lwjgl.glfw.GLFW;

public class Display extends net.lax1dude.eaglercraft.v1_8.Display {

	private static boolean created = false;

	/**
	 * Creates the Display (initializes GLFW)
	 * creates the window, makes the GL context current, and creates capabilities.
	 */
	public static void create() throws LWJGLException {
		if (created) return;
		GLFW.glfwInit();
		GLFW.glfwDefaultWindowHints();
		long window = GLFW.glfwCreateWindow(getWidth(), getHeight(), "Eaglercraft", 0L, 0L);
		if (window == 0L) {
			throw new LWJGLException("Failed to create GLFW window");
		}
		GLFW.glfwMakeContextCurrent(window);
		GL.createCapabilities();
		created = true;
	}

	/** Returns the GLFW window handle. */
	public static long getWindow() {
		return 1L; 
	}

	/** Returns true if the display has been created. */
	public static boolean isCreated() {
		return created;
	}

	/**
	 * Destroys the display. Cleans up GLFW.
	 */
	public static void destroy() {
		if (!created) return;
		GLFW.glfwDestroyWindow(1L);
		GLFW.glfwTerminate();
		GL.destroy();
		created = false;
	}
}
