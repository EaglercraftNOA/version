/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.system;

import java.util.function.Supplier;

public class Configuration<T> {

	/** Whether debug mode is enabled. */
	public static final Configuration<Boolean> DEBUG = new Configuration<>("org.lwjgl.util.Debug", false);

	/** Whether debug stream output is enabled. */
	public static final Configuration<Boolean> DEBUG_STREAM = new Configuration<>("org.lwjgl.util.DebugStream", false);

	/** Whether debug memory allocator is enabled. */
	public static final Configuration<Boolean> DEBUG_MEMORY_ALLOCATOR = new Configuration<>("org.lwjgl.util.DebugAllocator", false);

	/** Whether debug stack is enabled. */
	public static final Configuration<Boolean> DEBUG_STACK = new Configuration<>("org.lwjgl.util.DebugStack", false);

	/** Whether debug functions are enabled. */
	public static final Configuration<Boolean> DEBUG_FUNCTIONS = new Configuration<>("org.lwjgl.util.DebugFunctions", false);

	/** The GLFW library name (no-op in TeaVM). */
	public static final Configuration<String> GLFW_LIBRARY_NAME = new Configuration<>("org.lwjgl.glfw.libname", null);

	/** The OpenAL library name (no-op in TeaVM). */
	public static final Configuration<String> OPENAL_LIBRARY_NAME = new Configuration<>("org.lwjgl.openal.libname", null);

	/** The OpenGL library name (no-op in TeaVM). */
	public static final Configuration<String> OPENGL_LIBRARY_NAME = new Configuration<>("org.lwjgl.opengl.libname", null);

	/** The stack size for MemoryStack. */
	public static final Configuration<Integer> STACK_SIZE = new Configuration<>("org.lwjgl.system.stackSize", 64);

	/** Whether to disable checks. */
	public static final Configuration<Boolean> DISABLE_CHECKS = new Configuration<>("org.lwjgl.util.NoChecks", false);

	/** Whether to disable function checks. */
	public static final Configuration<Boolean> DISABLE_FUNCTION_CHECKS = new Configuration<>("org.lwjgl.util.NoFunctionChecks", false);

	private final String property;
	private T value;

	@SuppressWarnings("unchecked")
	Configuration(String property, T defaultValue) {
		this.property = property;
		this.value = defaultValue;
	}

	public String getProperty() {
		return property;
	}

	public void set(T value) {
		this.value = value;
	}

	@SuppressWarnings("unchecked")
	public T get() {
		return value;
	}

	@SuppressWarnings("unchecked")
	public T get(T defaultValue) {
		return value != null ? value : defaultValue;
	}

	@SuppressWarnings("unchecked")
	public T get(Supplier<T> defaultValue) {
		return value != null ? value : defaultValue.get();
	}
}
