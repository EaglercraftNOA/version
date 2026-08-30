/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.glfw;

@FunctionalInterface
public interface GLFWFramebufferSizeCallbackI {
	void invoke(long window, int width, int height);
}
