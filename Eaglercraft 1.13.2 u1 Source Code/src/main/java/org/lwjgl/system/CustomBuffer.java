/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.system;

/**
 * Shadow base class matching the binary signature of real LWJGL's
 * {@link org.lwjgl.system.CustomBuffer} so that game code compiled against
 * Eaglercraft's shadow buffer types produces bytecode whose erased generic
 * return types align with the real LWJGL implementation at runtime.
 *
 * <p>At desktop runtime, the rootProject jar excludes {@code org/lwjgl/system/**}
 * so real LWJGL's CustomBuffer is loaded instead. For TeaVM/WASM-GC, this
 * shadow provides a usable stub.</p>
 */
public abstract class CustomBuffer<SELF extends CustomBuffer<SELF>> extends Pointer.Default {

	protected int mark;
	protected int position;
	protected int limit;
	protected int capacity;

	protected CustomBuffer(long address, int mark, int position, int limit, int capacity) {
		super(address);
		this.mark = mark;
		this.position = position;
		this.limit = limit;
		this.capacity = capacity;
	}

	@SuppressWarnings("unchecked")
	protected SELF self() {
		return (SELF) this;
	}

	public abstract int sizeof();

	public int capacity() {
		return capacity;
	}

	public int position() {
		return position;
	}

	public SELF position(int position) {
		if (position < 0 || limit < position) {
			throw new IllegalArgumentException();
		}
		this.position = position;
		if (position < mark) {
			mark = -1;
		}
		return self();
	}

	public int limit() {
		return limit;
	}

	public SELF limit(int limit) {
		if (limit < 0 || capacity < limit) {
			throw new IllegalArgumentException();
		}
		this.limit = limit;
		if (position > limit) {
			position = limit;
		}
		if (mark > limit) {
			mark = -1;
		}
		return self();
	}

	public int remaining() {
		return limit - position;
	}

	public boolean hasRemaining() {
		return position < limit;
	}

	public SELF clear() {
		position = 0;
		limit = capacity;
		mark = -1;
		return self();
	}

	public SELF flip() {
		limit = position;
		position = 0;
		mark = -1;
		return self();
	}

	public SELF rewind() {
		position = 0;
		mark = -1;
		return self();
	}
}
