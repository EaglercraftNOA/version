/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.opengl;

public final class GL {

	private static GLCapabilities capabilities;

	private GL() {
	}

	/**
	 * Creates the GLCapabilities instance for the current context.
	 * Must be called after making a GLFW context current.
	 */
	public static GLCapabilities createCapabilities() {
		capabilities = new GLCapabilities();
		return capabilities;
	}

	/**
	 * Creates the GLCapabilities instance, optionally forwarding compatible.
	 */
	public static GLCapabilities createCapabilities(boolean forwardCompatible) {
		return createCapabilities();
	}

	/**
	 * Returns the GLCapabilities for the current context.
	 */
	public static GLCapabilities getCapabilities() {
		if (capabilities == null) {
			throw new IllegalStateException("No GLCapabilities instance set. Call GL.createCapabilities() first.");
		}
		return capabilities;
	}

	/**
	 * Sets the current capabilities.
	 */
	public static void setCapabilities(GLCapabilities caps) {
		capabilities = caps;
	}

	/**
	 * Destroys capabilities for the current context.
	 */
	public static void destroy() {
		capabilities = null;
	}
}
