/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.glfw;

/** Functional interface for GLFW character input callbacks. */
@FunctionalInterface
public interface GLFWCharCallbackI {
	/**
	 * Called when a Unicode character is input.
	 * @param window the window that received the event
	 * @param codepoint the Unicode code point
	 */
	void invoke(long window, int codepoint);
}
