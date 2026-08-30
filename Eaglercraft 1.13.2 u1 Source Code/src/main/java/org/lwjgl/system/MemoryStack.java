/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.system;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import org.lwjgl.PointerBuffer;

public class MemoryStack extends Pointer.Default implements AutoCloseable {

	private static final int DEFAULT_STACK_SIZE = 64 * 1024;
	private static final int MAX_FRAMES = 64;

	private static final ThreadLocal<MemoryStack> TLS = new ThreadLocal<MemoryStack>() {
		@Override
		protected MemoryStack initialValue() {
			return MemoryStack.create();
		}
	};

	private final int size;
	private int pointer;
	private int frameIndex;
	private final int[] frames;

	protected MemoryStack(int size) {
		super(0);
		this.size = size;
		this.pointer = size;
		this.frameIndex = 0;
		this.frames = new int[MAX_FRAMES];
	}

	public static MemoryStack create() {
		return new MemoryStack(DEFAULT_STACK_SIZE);
	}

	public static MemoryStack create(int capacity) {
		return new MemoryStack(capacity);
	}

	public static MemoryStack stackGet() {
		return TLS.get();
	}

	public static MemoryStack stackPush() {
		return stackGet().push();
	}

	public static void stackPop() {
		stackGet().pop();
	}

	public MemoryStack push() {
		if (frameIndex >= MAX_FRAMES) {
			throw new StackOverflowError("MemoryStack overflow: too many nested push() calls");
		}
		frames[frameIndex++] = pointer;
		return this;
	}

	public MemoryStack pop() {
		if (frameIndex <= 0) {
			throw new IllegalStateException("MemoryStack underflow: pop() called without matching push()");
		}
		pointer = frames[--frameIndex];
		return this;
	}

	public int getFrameIndex() {
		return frameIndex;
	}

	public int getPointer() {
		return pointer;
	}

	public int getSize() {
		return size;
	}

	private int align(int offset, int alignment) {
		return offset & ~(alignment - 1);
	}

	public ByteBuffer malloc(int size) {
		pointer = align(pointer - size, 8);
		if (pointer < 0) {
			throw new OutOfMemoryError("MemoryStack out of space: requested " + size + " bytes");
		}
		return ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder());
	}

	public ByteBuffer calloc(int size) {
		ByteBuffer buf = malloc(size);
		for (int i = 0; i < size; i++) {
			buf.put(i, (byte) 0);
		}
		return buf;
	}

	public ByteBuffer mallocByte(int count) {
		return malloc(count);
	}

	public ShortBuffer mallocShort(int count) {
		pointer = align(pointer - count * 2, 8);
		if (pointer < 0) {
			throw new OutOfMemoryError("MemoryStack out of space");
		}
		return ByteBuffer.allocateDirect(count * 2).order(ByteOrder.nativeOrder()).asShortBuffer();
	}

	public IntBuffer mallocInt(int count) {
		pointer = align(pointer - count * 4, 8);
		if (pointer < 0) {
			throw new OutOfMemoryError("MemoryStack out of space");
		}
		return ByteBuffer.allocateDirect(count * 4).order(ByteOrder.nativeOrder()).asIntBuffer();
	}

	public FloatBuffer mallocFloat(int count) {
		pointer = align(pointer - count * 4, 8);
		if (pointer < 0) {
			throw new OutOfMemoryError("MemoryStack out of space");
		}
		return ByteBuffer.allocateDirect(count * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
	}

	public ShortBuffer callocShort(int count) {
		ShortBuffer buf = mallocShort(count);
		for (int i = 0; i < count; i++) {
			buf.put(i, (short) 0);
		}
		return buf;
	}

	public IntBuffer callocInt(int count) {
		IntBuffer buf = mallocInt(count);
		for (int i = 0; i < count; i++) {
			buf.put(i, 0);
		}
		return buf;
	}

	public FloatBuffer callocFloat(int count) {
		FloatBuffer buf = mallocFloat(count);
		for (int i = 0; i < count; i++) {
			buf.put(i, 0.0f);
		}
		return buf;
	}

	public IntBuffer ints(int value) {
		IntBuffer buf = mallocInt(1);
		buf.put(0, value);
		return buf;
	}

	public IntBuffer ints(int... values) {
		IntBuffer buf = mallocInt(values.length);
		for (int i = 0; i < values.length; i++) {
			buf.put(i, values[i]);
		}
		return buf;
	}

	public FloatBuffer floats(float value) {
		FloatBuffer buf = mallocFloat(1);
		buf.put(0, value);
		return buf;
	}

	public FloatBuffer floats(float... values) {
		FloatBuffer buf = mallocFloat(values.length);
		for (int i = 0; i < values.length; i++) {
			buf.put(i, values[i]);
		}
		return buf;
	}

	public ByteBuffer bytes(byte value) {
		ByteBuffer buf = malloc(1);
		buf.put(0, value);
		return buf;
	}

	public ByteBuffer bytes(byte... values) {
		ByteBuffer buf = malloc(values.length);
		for (int i = 0; i < values.length; i++) {
			buf.put(i, values[i]);
		}
		return buf;
	}

	public ByteBuffer UTF8(CharSequence text) {
		return UTF8(text, true);
	}

	public ByteBuffer UTF8(CharSequence text, boolean nullTerminated) {
		return MemoryUtil.memUTF8(text, nullTerminated);
	}

	public ByteBuffer ASCII(CharSequence text) {
		return ASCII(text, true);
	}

	public ByteBuffer ASCII(CharSequence text, boolean nullTerminated) {
		return MemoryUtil.memASCII(text, nullTerminated);
	}

	public static IntBuffer stackInts(int value) {
		return stackGet().ints(value);
	}

	public static IntBuffer stackMallocInt(int size) {
		return stackGet().mallocInt(size);
	}

	public static IntBuffer stackCallocInt(int size) {
		return stackGet().callocInt(size);
	}

	public static FloatBuffer stackMallocFloat(int size) {
		return stackGet().mallocFloat(size);
	}

	public static FloatBuffer stackCallocFloat(int size) {
		return stackGet().callocFloat(size);
	}

	public static ByteBuffer stackMalloc(int size) {
		return stackGet().malloc(size);
	}

	public static ByteBuffer stackCalloc(int size) {
		return stackGet().calloc(size);
	}

	public static ShortBuffer stackMallocShort(int size) {
		return stackGet().mallocShort(size);
	}

	public static ShortBuffer stackCallocShort(int size) {
		return stackGet().callocShort(size);
	}

	public static ByteBuffer stackUTF8(CharSequence text) {
		return stackGet().UTF8(text);
	}

	public static ByteBuffer stackASCII(CharSequence text) {
		return stackGet().ASCII(text);
	}

	public PointerBuffer mallocPointer(int count) {
		return PointerBuffer.allocateDirect(count);
	}

	public long nmalloc(int size) {
		return MemoryUtil.nmemAlloc(size);
	}

	@Override
	public void close() {
		pop();
	}
}
