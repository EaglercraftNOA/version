/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.system.Pointer;
import org.lwjgl.system.MemoryUtil;

/**
 * A buffer of pointer-sized integers for LWJGL3 compatibility.
 * Pointers are simulated as 32-bit integers.
 */
public class PointerBuffer implements Comparable<PointerBuffer> {

	private final IntBuffer backing;

	private PointerBuffer(IntBuffer backing) {
		this.backing = backing;
	}

	public static PointerBuffer allocateDirect(int capacity) {
		ByteBuffer bb = ByteBuffer.allocateDirect(capacity * Pointer.POINTER_SIZE).order(ByteOrder.nativeOrder());
		return new PointerBuffer(bb.asIntBuffer());
	}

	public static PointerBuffer create(IntBuffer buffer) {
		return new PointerBuffer(buffer);
	}

	public IntBuffer getBuffer() {
		return backing;
	}

	public int sizeof() {
		return Pointer.POINTER_SIZE;
	}

	public int capacity() {
		return backing.capacity();
	}

	public int position() {
		return backing.position();
	}

	public PointerBuffer position(int newPosition) {
		backing.position(newPosition);
		return this;
	}

	public int limit() {
		return backing.limit();
	}

	public PointerBuffer limit(int newLimit) {
		backing.limit(newLimit);
		return this;
	}

	public int remaining() {
		return backing.remaining();
	}

	public boolean hasRemaining() {
		return backing.hasRemaining();
	}

	public PointerBuffer flip() {
		backing.flip();
		return this;
	}

	public PointerBuffer clear() {
		backing.clear();
		return this;
	}

	public PointerBuffer rewind() {
		backing.position(0);
		return this;
	}

	public long get() {
		return backing.get();
	}

	public PointerBuffer put(long value) {
		backing.put((int) value);
		return this;
	}

	public long get(int index) {
		return backing.get(index);
	}

	public String getStringUTF8(int index) {
		return MemoryUtil.memUTF8(get(index));
	}

	public PointerBuffer put(int index, long value) {
		backing.put(index, (int) value);
		return this;
	}

	public PointerBuffer put(Pointer pointer) {
		return put(pointer.address());
	}

	public PointerBuffer put(ByteBuffer buffer) {
		return put(System.identityHashCode(buffer));
	}

	public PointerBuffer put(IntBuffer buffer) {
		return put(System.identityHashCode(buffer));
	}

	/**
	 * Returns a new PointerBuffer view with the given size.
	 * This is a lightweight stub used by OggAudioStream.
	 */
	public PointerBuffer getPointerBuffer(int size) {
		return PointerBuffer.allocateDirect(size);
	}

	/**
	 * Returns a FloatBuffer view for the pointer at the given index.
	 * This stub returns a zero-filled buffer of the requested length.
	 */
	public FloatBuffer getFloatBuffer(int index, int length) {
		return ByteBuffer.allocateDirect(length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
	}

	@Override
	public int compareTo(PointerBuffer other) {
		int thisRemaining = remaining();
		int otherRemaining = other.remaining();
		int n = Math.min(thisRemaining, otherRemaining);
		for (int i = 0; i < n; i++) {
			long cmp = get(position() + i) - other.get(other.position() + i);
			if (cmp != 0) return cmp < 0 ? -1 : 1;
		}
		return thisRemaining - otherRemaining;
	}

	@Override
	public String toString() {
		return String.format("PointerBuffer[pos=%d lim=%d cap=%d]", position(), limit(), capacity());
	}
}
