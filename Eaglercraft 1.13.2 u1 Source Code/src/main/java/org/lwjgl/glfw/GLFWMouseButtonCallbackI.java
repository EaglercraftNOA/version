/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.glfw;

/** Functional interface for GLFW mouse button callbacks. */
@FunctionalInterface
public interface GLFWMouseButtonCallbackI {
	void invoke(long window, int button, int action, int mods);
}
