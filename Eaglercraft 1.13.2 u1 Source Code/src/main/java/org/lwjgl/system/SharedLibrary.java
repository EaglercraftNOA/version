/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.system;

/**
 * Represents a loaded shared/native library.
 * In TeaVM, this is a stub since there are no native libraries.
 */
public abstract class SharedLibrary extends Pointer.Default implements FunctionProvider, NativeResource {

	private final String name;

	protected SharedLibrary(String name) {
		super(0);
		this.name = name;
	}

	/**
	 * Returns the library name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the path to the library.
	 */
	public String getPath() {
		return name;
	}

	@Override
	public String toString() {
		return getName();
	}

	/**
	 * Stub implementation for TeaVM where no native libraries exist.
	 */
	public static class Stub extends SharedLibrary {

		public Stub(String name) {
			super(name);
		}

		@Override
		public long getFunctionAddress(String functionName) {
			return functionName.hashCode() | 1L;
		}

		@Override
		public void free() {
		}
	}
}
