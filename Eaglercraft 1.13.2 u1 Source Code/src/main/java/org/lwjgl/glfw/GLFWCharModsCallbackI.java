/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.glfw;

@FunctionalInterface
public interface GLFWCharModsCallbackI {
	void invoke(long window, int codepoint, int mods);
}
