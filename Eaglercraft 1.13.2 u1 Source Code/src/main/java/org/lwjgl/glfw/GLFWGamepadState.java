/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.glfw;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import org.lwjgl.system.MemoryStack;

/**
 * Describes the state of a gamepad.
 */
public class GLFWGamepadState {

	/** Button states: {@link GLFW#GLFW_PRESS} or {@link GLFW#GLFW_RELEASE}. Length = 15. */
	private final ByteBuffer buttons;

	/** Axis values in the range [-1.0, 1.0]. Length = 6. */
	private final FloatBuffer axes;

	public GLFWGamepadState() {
		buttons = ByteBuffer.allocateDirect(15).order(ByteOrder.nativeOrder());
		axes = ByteBuffer.allocateDirect(6 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
	}

	public static GLFWGamepadState calloc(MemoryStack stack) {
		GLFWGamepadState state = new GLFWGamepadState();
		ByteBuffer buttons = state.buttons();
		FloatBuffer axes = state.axes();
		while (buttons.hasRemaining()) {
			buttons.put((byte) 0);
		}
		while (axes.hasRemaining()) {
			axes.put(0.0f);
		}
		buttons.clear();
		axes.clear();
		return state;
	}

	public byte buttons(int index) {
		return buttons.get(index);
	}

	public float axes(int index) {
		return axes.get(index);
	}

	public ByteBuffer buttons() {
		ByteBuffer ret = buttons.duplicate();
		ret.clear();
		return ret;
	}

	public FloatBuffer axes() {
		FloatBuffer ret = axes.duplicate();
		ret.clear();
		return ret;
	}

	/** Set a button state. */
	public GLFWGamepadState buttons(int index, byte value) {
		buttons.put(index, value);
		return this;
	}

	/** Set an axis value. */
	public GLFWGamepadState axes(int index, float value) {
		axes.put(index, value);
		return this;
	}
}
