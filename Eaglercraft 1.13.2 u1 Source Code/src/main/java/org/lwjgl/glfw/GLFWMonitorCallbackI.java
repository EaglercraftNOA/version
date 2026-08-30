/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.glfw;

@FunctionalInterface
public interface GLFWMonitorCallbackI {
	void invoke(long monitor, int event);
}
