/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.glfw;

/** Functional interface for GLFW key callbacks. */
@FunctionalInterface
public interface GLFWKeyCallbackI {
	/**
	 * Called when a key is pressed, released, or repeated.
	 * @param window the window that received the event
	 * @param key the GLFW key code
	 * @param scancode the platform-specific scancode
	 * @param action GLFW_PRESS, GLFW_RELEASE, or GLFW_REPEAT
	 * @param mods modifier key bitmask
	 */
	void invoke(long window, int key, int scancode, int action, int mods);
}
