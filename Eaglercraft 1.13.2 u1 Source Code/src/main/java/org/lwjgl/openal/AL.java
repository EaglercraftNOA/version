/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.openal;

public final class AL {

	private static ALCapabilities capabilities;

	private AL() {
	}

	/**
	 * Creates ALCapabilities for the current context. In Eaglercraft, this is
	 * always available once the runtime has initialized.
	 */
	public static ALCapabilities createCapabilities(ALCCapabilities alcCaps) {
		capabilities = new ALCapabilities();
		return capabilities;
	}

	/** Creates capabilities using default ALC capabilities. */
	public static ALCapabilities createCapabilities(ALCCapabilities alcCaps, boolean forwardCompatible) {
		return createCapabilities(alcCaps);
	}

	/** Returns the current AL capabilities. */
	public static ALCapabilities getCapabilities() {
		if (capabilities == null) {
			throw new IllegalStateException("No ALCapabilities instance set. Call AL.createCapabilities() first.");
		}
		return capabilities;
	}

	/** Destroys the current capabilities. */
	public static void destroy() {
		capabilities = null;
	}
}
