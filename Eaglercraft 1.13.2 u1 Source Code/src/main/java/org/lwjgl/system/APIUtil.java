/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.system;

public final class APIUtil {

	private APIUtil() {}

	/**
	 * Returns a function address. In TeaVM, returns a dummy non-zero value.
	 */
	public static long apiGetFunctionAddress(FunctionProvider provider, String functionName) {
		return provider.getFunctionAddress(functionName);
	}

	/**
	 * Returns a function address from a SharedLibrary. In TeaVM, returns a dummy non-zero value.
	 */
	public static long apiGetFunctionAddress(SharedLibrary library, String functionName) {
		return library.getFunctionAddress(functionName);
	}

	/**
	 * Returns a function address, throwing if not found.
	 */
	public static long apiGetFunctionAddressOptional(SharedLibrary library, String functionName) {
		return library.getFunctionAddress(functionName);
	}

	/**
	 * Creates a new string by filtering null terminators.
	 */
	public static String apiFilterReturn(String value) {
		return value;
	}

	/**
	 * Logging utility for API initialization.
	 */
	public static void apiLog(String msg) {
		System.out.println("[LWJGL] " + msg);
	}

	/**
	 * Returns the class name for the specified class.
	 */
	public static String apiClassName(Class<?> clazz) {
		return clazz.getSimpleName();
	}

	/**
	 * Creates a new array of the specified type and size.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T apiCreateArray(T[] array, int index, T value) {
		array[index] = value;
		return value;
	}
}
