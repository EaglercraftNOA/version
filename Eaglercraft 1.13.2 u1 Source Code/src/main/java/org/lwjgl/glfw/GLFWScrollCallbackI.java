/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.glfw;

/** Functional interface for GLFW scroll callbacks. */
@FunctionalInterface
public interface GLFWScrollCallbackI {
	void invoke(long window, double xoffset, double yoffset);
}
