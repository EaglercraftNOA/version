/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.glfw;

@FunctionalInterface
public interface GLFWWindowIconifyCallbackI {
	void invoke(long window, boolean iconified);
}
