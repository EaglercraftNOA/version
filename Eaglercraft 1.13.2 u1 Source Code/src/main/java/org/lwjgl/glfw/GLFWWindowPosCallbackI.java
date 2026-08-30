/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.glfw;

@FunctionalInterface
public interface GLFWWindowPosCallbackI {
	void invoke(long window, int xpos, int ypos);
}
