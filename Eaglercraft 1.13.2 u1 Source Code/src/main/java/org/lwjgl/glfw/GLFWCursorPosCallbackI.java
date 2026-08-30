/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.glfw;

/** Functional interface for GLFW cursor position callbacks. */
@FunctionalInterface
public interface GLFWCursorPosCallbackI {
	void invoke(long window, double xpos, double ypos);
}
