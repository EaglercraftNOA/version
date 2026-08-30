/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.system;

/**
 * Shared library loading for LWJGL3.
 * In the TeaVM environment, there are no native shared libraries to load.
 * All "library" functions are backed by JavaScript/WebGL/WebAudio APIs instead.
 */
public abstract class Library {

	private Library() {}

	/**
	 * Loads a native library.
	 */
	public static SharedLibrary loadNative(Class<?> context, String module, String name, boolean bundled) {
		return new SharedLibrary.Stub(name);
	}

	/**
	 * Loads a native library.
	 */
	public static SharedLibrary loadNative(Class<?> context, String module, Configuration<String> nameConfig) {
		String name = nameConfig != null ? nameConfig.get(module) : module;
		return new SharedLibrary.Stub(name != null ? name : module);
	}

	/**
	 * Loads a native library by name.
	 */
	public static SharedLibrary loadNative(String name) {
		return new SharedLibrary.Stub(name);
	}

	/**
	 * Loads a system library.
	 */
	public static SharedLibrary loadSystem(String name) {
		return new SharedLibrary.Stub(name);
	}

	/**
	 * Initializes a native library by loading it and resolving function pointers.
	 */
	public static void initialize() {
	}
}
