/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.system;

import net.lax1dude.eaglercraft.v1_8.internal.EnumPlatformOS;
import net.lax1dude.eaglercraft.v1_8.internal.EnumPlatformType;
import net.lax1dude.eaglercraft.v1_8.internal.PlatformRuntime;

/**
 * Platform detection for LWJGL3.
 * In the Eaglercraft/TeaVM environment, this delegates to Eaglercraft's platform detection.
 */
public enum Platform {

	LINUX("Linux"),
	MACOSX("macOS"),
	WINDOWS("Windows"),
	FREEBSD("FreeBSD");

	private final String name;

	Platform(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	private static final Platform CURRENT;

	static {
		EnumPlatformOS os = PlatformRuntime.getPlatformOS();
		if (os == null) {
			CURRENT = LINUX; // default fallback
		} else {
			switch (os) {
				case WINDOWS:
					CURRENT = WINDOWS;
					break;
				case MACOS:
					CURRENT = MACOSX;
					break;
				case LINUX:
				case CHROMEBOOK_LINUX:
					CURRENT = LINUX;
					break;
				default:
					CURRENT = LINUX;
					break;
			}
		}
	}

	/**
	 * Returns the current platform.
	 */
	public static Platform get() {
		return CURRENT;
	}

	/**
	 * CPU architecture enumeration.
	 */
	public enum Architecture {
		X86,
		X64,
		ARM32,
		ARM64;

		/** Always X64 in WebAssembly/JS environment. */
		public static Architecture get() {
			return X64;
		}
	}

	/**
	 * Returns the map library name for the current platform.
	 * In TeaVM, this is a no-op that returns the input name.
	 */
	public static String mapLibraryName(String name) {
		return name;
	}

	/**
	 * Returns the map library name with path for the current platform.
	 * In TeaVM, this is a no-op that returns the input name.
	 */
	public static String mapLibraryNameBundled(String name) {
		return name;
	}
}
