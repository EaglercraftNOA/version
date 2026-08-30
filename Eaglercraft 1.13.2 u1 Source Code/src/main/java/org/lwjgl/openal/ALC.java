/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.openal;

public final class ALC {

	private static ALCCapabilities capabilities;

	private ALC() {
	}

	/**
	 * Creates ALCCapabilities for the given device.
	 */
	public static ALCCapabilities createCapabilities(long device) {
		capabilities = new ALCCapabilities();
		return capabilities;
	}

	/** Returns the current ALC capabilities. */
	public static ALCCapabilities getCapabilities() {
		if (capabilities == null) {
			throw new IllegalStateException("No ALCCapabilities instance set. Call ALC.createCapabilities() first.");
		}
		return capabilities;
	}

	/** Destroys the current capabilities. */
	public static void destroy() {
		capabilities = null;
	}
}
