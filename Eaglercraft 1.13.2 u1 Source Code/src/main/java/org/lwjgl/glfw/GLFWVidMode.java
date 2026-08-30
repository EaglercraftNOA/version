/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.glfw;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import org.lwjgl.system.CustomBuffer;

/**
 * Describes a single video mode for a monitor.
 */
public class GLFWVidMode {

	public final int width;
	public final int height;
	public final int redBits;
	public final int greenBits;
	public final int blueBits;
	public final int refreshRate;

	public GLFWVidMode(int width, int height, int redBits, int greenBits, int blueBits, int refreshRate) {
		this.width = width;
		this.height = height;
		this.redBits = redBits;
		this.greenBits = greenBits;
		this.blueBits = blueBits;
		this.refreshRate = refreshRate;
	}

	protected GLFWVidMode(long address, ByteBuffer container) {
		this(container);
	}

	public GLFWVidMode(ByteBuffer container) {
		this(readInt(container, 0), readInt(container, 4), readInt(container, 8), readInt(container, 12),
				readInt(container, 16), readInt(container, 20));
	}

	public int width() { return width; }
	public int height() { return height; }
	public int redBits() { return redBits; }
	public int greenBits() { return greenBits; }
	public int blueBits() { return blueBits; }
	public int refreshRate() { return refreshRate; }

	private static int readInt(ByteBuffer buffer, int offset) {
		if (buffer == null || buffer.capacity() < offset + 4) {
			return 0;
		}
		return buffer.order(ByteOrder.nativeOrder()).getInt(offset);
	}

	@Override
	public String toString() {
		return width + " x " + height + " x " + (redBits + greenBits + blueBits) + " @ " + refreshRate + "Hz";
	}

	/**
	 * A buffer of GLFWVidMode structs.
	 *
	 * <p>Extends {@link CustomBuffer} so that its {@code position(int)} method (and
	 * other generic-typed buffer methods) compiles to bytecode whose erased
	 * return type is {@code org/lwjgl/system/CustomBuffer}, matching real LWJGL.
	 * This lets game code compiled against this shadow run against real LWJGL on
	 * desktop without {@code NoSuchMethodError}.</p>
	 */
	public static class Buffer extends CustomBuffer<Buffer> {
		private final GLFWVidMode[] modes;

		public Buffer(ByteBuffer buffer) {
			this(parseModes(buffer));
		}

		public Buffer(long address, int capacity) {
			this(createEmptyModes(capacity));
		}

		public Buffer(int capacity) {
			this(createEmptyModes(capacity));
		}

		public Buffer(GLFWVidMode[] modes) {
			super(0L, -1, 0, modes.length, modes.length);
			this.modes = modes;
		}

		private static GLFWVidMode[] createEmptyModes(int capacity) {
			int len = Math.max(0, capacity);
			GLFWVidMode[] ret = new GLFWVidMode[len];
			for (int i = 0; i < len; ++i) {
				ret[i] = new GLFWVidMode(0, 0, 0, 0, 0, 0);
			}
			return ret;
		}

		private static GLFWVidMode[] parseModes(ByteBuffer buffer) {
			if (buffer == null) {
				return createEmptyModes(0);
			}
			ByteBuffer view = buffer.duplicate().order(ByteOrder.nativeOrder());
			int entrySize = 24;
			int count = view.remaining() / entrySize;
			if (count <= 0) {
				return createEmptyModes(1);
			}
			GLFWVidMode[] ret = new GLFWVidMode[count];
			int base = view.position();
			for (int i = 0; i < count; ++i) {
				int off = base + i * entrySize;
				ret[i] = new GLFWVidMode(view.getInt(off), view.getInt(off + 4), view.getInt(off + 8),
						view.getInt(off + 12), view.getInt(off + 16), view.getInt(off + 20));
			}
			return ret;
		}

		@Override
		public int sizeof() {
			return 24;
		}

		public GLFWVidMode get() {
			return modes[position++];
		}

		public GLFWVidMode get(int index) {
			return modes[index];
		}

		public int width() {
			return modes[position].width();
		}

		public int height() {
			return modes[position].height();
		}

		public int redBits() {
			return modes[position].redBits();
		}

		public int greenBits() {
			return modes[position].greenBits();
		}

		public int blueBits() {
			return modes[position].blueBits();
		}

		public int refreshRate() {
			return modes[position].refreshRate();
		}
	}
}
