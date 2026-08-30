/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl;

/**
 * LWJGL3 version information.
 */
public final class Version {

	/** LWJGL3 major version. */
	public static final int VERSION_MAJOR = 3;

	/** LWJGL3 minor version. */
	public static final int VERSION_MINOR = 3;

	/** LWJGL3 revision. */
	public static final int VERSION_REVISION = 4;

	/** Build type. */
	public static final String BUILD_TYPE = "arlencraft";

	private Version() {}

	/**
	 * Returns the LWJGL3 version string.
	 */
	public static String getVersion() {
		return VERSION_MAJOR + "." + VERSION_MINOR + "." + VERSION_REVISION + " " + BUILD_TYPE;
	}

	@Override
	public String toString() {
		return getVersion();
	}
}
