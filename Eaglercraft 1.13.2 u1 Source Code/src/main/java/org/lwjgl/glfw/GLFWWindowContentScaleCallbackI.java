/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.glfw;

@FunctionalInterface
public interface GLFWWindowContentScaleCallbackI {
	void invoke(long window, float xscale, float yscale);
}
