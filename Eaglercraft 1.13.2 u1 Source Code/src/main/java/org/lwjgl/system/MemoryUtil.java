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
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;


public final class MemoryUtil {

	/** Null pointer constant. */
	public static final long NULL = 0L;

	/** Page size (simulated). */
	public static final int PAGE_SIZE = 4096;

	/** Cache line size (simulated). */
	public static final int CACHE_LINE_SIZE = 64;

	private static final Object ALLOC_LOCK = new Object();
	private static long nextAddress = 0x1000L;

	// Sorted by base address so an interior pointer can be resolved with an O(log n)
	// floorEntry lookup instead of scanning every live allocation.
	private static final NavigableMap<Long, Allocation> allocations = new TreeMap<>();
	private static final Map<ByteBuffer, Long> bufferBase = Collections.synchronizedMap(new IdentityHashMap<>());
	private static final Map<IntBuffer, Long> intBufferBase = Collections.synchronizedMap(new IdentityHashMap<>());
	private static final Map<FloatBuffer, Long> floatBufferBase = Collections.synchronizedMap(new IdentityHashMap<>());
	private static final Map<ShortBuffer, Long> shortBufferBase = Collections.synchronizedMap(new IdentityHashMap<>());

	private static final class Allocation {
		final long base;
		final ByteBuffer buffer;
		final int size;

		Allocation(long base, ByteBuffer buffer, int size) {
			this.base = base;
			this.buffer = buffer;
			this.size = size;
		}
	}

	private MemoryUtil() {
	}

	private static int align(int size) {
		int align = 16;
		return (size + (align - 1)) & ~(align - 1);
	}

	private static long register(ByteBuffer buffer) {
		synchronized (ALLOC_LOCK) {
			long base = nextAddress;
			nextAddress += align(buffer.capacity());
			Allocation alloc = new Allocation(base, buffer, buffer.capacity());
			allocations.put(base, alloc);
			bufferBase.put(buffer, base);
			return base;
		}
	}

	private static Allocation findAllocation(long address) {
		synchronized (ALLOC_LOCK) {
			Map.Entry<Long, Allocation> entry = allocations.floorEntry(address);
			if (entry != null) {
				Allocation alloc = entry.getValue();
				if (address >= alloc.base && address < alloc.base + alloc.size) {
					return alloc;
				}
			}
		}
		return null;
	}

	// ---- Buffer allocation methods ----

	public static ByteBuffer memAlloc(int size) {
		ByteBuffer buf = ByteBuffer.allocateDirect(size).order(ByteOrder.nativeOrder());
		register(buf);
		return buf;
	}

	public static ByteBuffer memCalloc(int size) {
		ByteBuffer buf = memAlloc(size);
		for (int i = 0; i < size; i++) {
			buf.put(i, (byte) 0);
		}
		return buf;
	}

	public static IntBuffer memAllocInt(int size) {
		ByteBuffer buf = memAlloc(size * 4);
		IntBuffer view = buf.asIntBuffer();
		intBufferBase.put(view, memAddress(buf));
		return view;
	}

	public static FloatBuffer memAllocFloat(int size) {
		ByteBuffer buf = memAlloc(size * 4);
		FloatBuffer view = buf.asFloatBuffer();
		floatBufferBase.put(view, memAddress(buf));
		return view;
	}

	public static ShortBuffer memAllocShort(int size) {
		ByteBuffer buf = memAlloc(size * 2);
		ShortBuffer view = buf.asShortBuffer();
		shortBufferBase.put(view, memAddress(buf));
		return view;
	}

	private static void memFreeByteBuffer(ByteBuffer buffer) {
		if (buffer == null) {
			return;
		}
		Long base = bufferBase.remove(buffer);
		if (base != null) {
			synchronized (ALLOC_LOCK) {
				allocations.remove(base);
			}
		}
	}

	private static void memFreeIntBuffer(IntBuffer buffer) {
		if (buffer == null) {
			return;
		}
		Long base = intBufferBase.remove(buffer);
		if (base != null) {
			synchronized (ALLOC_LOCK) {
				Allocation alloc = allocations.remove(base);
				if (alloc != null) {
					bufferBase.remove(alloc.buffer);
				}
			}
		}
	}

	private static void memFreeFloatBuffer(FloatBuffer buffer) {
		if (buffer == null) {
			return;
		}
		Long base = floatBufferBase.remove(buffer);
		if (base != null) {
			synchronized (ALLOC_LOCK) {
				Allocation alloc = allocations.remove(base);
				if (alloc != null) {
					bufferBase.remove(alloc.buffer);
				}
			}
		}
	}

	private static void memFreeShortBuffer(ShortBuffer buffer) {
		if (buffer == null) {
			return;
		}
		Long base = shortBufferBase.remove(buffer);
		if (base != null) {
			synchronized (ALLOC_LOCK) {
				Allocation alloc = allocations.remove(base);
				if (alloc != null) {
					bufferBase.remove(alloc.buffer);
				}
			}
		}
	}

	public static void memFree(java.nio.Buffer buffer) {
		if (buffer instanceof ByteBuffer) {
			memFreeByteBuffer((ByteBuffer) buffer);
		} else if (buffer instanceof IntBuffer) {
			memFreeIntBuffer((IntBuffer) buffer);
		} else if (buffer instanceof FloatBuffer) {
			memFreeFloatBuffer((FloatBuffer) buffer);
		} else if (buffer instanceof ShortBuffer) {
			memFreeShortBuffer((ShortBuffer) buffer);
		}
	}

	// ---- Native-like allocation ----

	public static long nmemAlloc(long size) {
		if (size <= 0) {
			return NULL;
		}
		int sz = (int) Math.min(Integer.MAX_VALUE, size);
		ByteBuffer buf = ByteBuffer.allocateDirect(sz).order(ByteOrder.nativeOrder());
		return register(buf);
	}

	public static long nmemCalloc(long num, long size) {
		long total = num * size;
		long addr = nmemAlloc(total);
		if (addr != NULL) {
			ByteBuffer buf = memByteBuffer(addr, (int) total);
			if (buf != null) {
				for (int i = 0; i < buf.capacity(); i++) {
					buf.put(i, (byte) 0);
				}
			}
		}
		return addr;
	}

	public static void nmemFree(long address) {
		if (address == NULL) {
			return;
		}
		synchronized (ALLOC_LOCK) {
			Allocation alloc = allocations.remove(address);
			if (alloc == null) {
				// try to free by range match
				alloc = findAllocation(address);
				if (alloc != null) {
					allocations.remove(alloc.base);
				}
			}
			if (alloc != null) {
				bufferBase.remove(alloc.buffer);
			}
		}
	}

	// ---- Address/pointer methods (simulated) ----

	public static long memAddress(ByteBuffer buffer) {
		if (buffer == null) {
			return NULL;
		}
		Long base = bufferBase.get(buffer);
		if (base == null) {
			base = register(buffer);
		}
		return base + buffer.position();
	}

	public static long memAddress(IntBuffer buffer) {
		if (buffer == null) {
			return NULL;
		}
		Long base = intBufferBase.get(buffer);
		return base != null ? base + ((long) buffer.position() * 4L) : System.identityHashCode(buffer);
	}

	public static long memAddress(FloatBuffer buffer) {
		if (buffer == null) {
			return NULL;
		}
		Long base = floatBufferBase.get(buffer);
		return base != null ? base + ((long) buffer.position() * 4L) : System.identityHashCode(buffer);
	}

	public static long memAddress(ShortBuffer buffer) {
		if (buffer == null) {
			return NULL;
		}
		Long base = shortBufferBase.get(buffer);
		return base != null ? base + ((long) buffer.position() * 2L) : System.identityHashCode(buffer);
	}

	public static long memAddress(ByteBuffer buffer, int position) {
		return memAddress(buffer) + position;
	}

	public static long memAddressSafe(ByteBuffer buffer) {
		return buffer != null ? memAddress(buffer) : NULL;
	}

	public static long memAddressSafe(IntBuffer buffer) {
		return buffer != null ? memAddress(buffer) : NULL;
	}

	public static long memAddressSafe(FloatBuffer buffer) {
		return buffer != null ? memAddress(buffer) : NULL;
	}

	// ---- Memory copy/set methods ----

	public static void memCopy(ByteBuffer src, ByteBuffer dst) {
		if (src == null || dst == null) {
			return;
		}
		int count = Math.min(src.remaining(), dst.remaining());
		int sPos = src.position();
		int dPos = dst.position();
		for (int i = 0; i < count; i++) {
			dst.put(dPos + i, src.get(sPos + i));
		}
	}

	public static void memCopy(long src, long dst, long bytes) {
		if (bytes <= 0) {
			return;
		}
		ByteBuffer srcBuf = memByteBuffer(src, (int) bytes);
		ByteBuffer dstBuf = memByteBuffer(dst, (int) bytes);
		if (srcBuf == null || dstBuf == null) {
			return;
		}
		for (int i = 0; i < bytes; i++) {
			dstBuf.put(i, srcBuf.get(i));
		}
	}

	public static void memCopy(long src, ByteBuffer dst, long bytes) {
		if (bytes <= 0 || dst == null) {
			return;
		}
		ByteBuffer srcBuf = memByteBuffer(src, (int) bytes);
		if (srcBuf == null) {
			return;
		}
		int dPos = dst.position();
		for (int i = 0; i < bytes && (dPos + i) < dst.limit(); i++) {
			dst.put(dPos + i, srcBuf.get(i));
		}
	}

	public static void memSet(ByteBuffer buffer, int value) {
		if (buffer == null) {
			return;
		}
		for (int i = buffer.position(); i < buffer.limit(); i++) {
			buffer.put(i, (byte) value);
		}
	}

	public static void memSet(long address, int value, long bytes) {
		if (address == NULL || bytes <= 0) {
			return;
		}
		ByteBuffer buf = memByteBuffer(address, (int) bytes);
		if (buf == null) {
			return;
		}
		for (int i = 0; i < bytes; i++) {
			buf.put(i, (byte) value);
		}
	}

	public static int memGetInt(long address) {
		ByteBuffer buf = memByteBuffer(address, 4);
		return buf == null ? 0 : buf.order(ByteOrder.nativeOrder()).getInt(0);
	}

	public static void memPutInt(long address, int value) {
		ByteBuffer buf = memByteBuffer(address, 4);
		if (buf != null) {
			buf.order(ByteOrder.nativeOrder()).putInt(0, value);
		}
	}

	public static byte memGetByte(long address) {
		ByteBuffer buf = memByteBuffer(address, 1);
		return buf == null ? 0 : buf.get(0);
	}

	public static void memPutByte(long address, byte value) {
		ByteBuffer buf = memByteBuffer(address, 1);
		if (buf != null) {
			buf.put(0, value);
		}
	}

	// ---- ByteBuffer view helpers ----

	public static ByteBuffer memByteBuffer(long address, int capacity) {
		if (address == NULL || capacity <= 0) {
			return null;
		}
		Allocation alloc = findAllocation(address);
		if (alloc == null) {
			return null;
		}
		int offset = (int) (address - alloc.base);
		int max = alloc.size - offset;
		int size = Math.min(capacity, Math.max(0, max));
		ByteBuffer dup = alloc.buffer.duplicate().order(ByteOrder.nativeOrder());
		dup.position(offset);
		dup.limit(offset + size);
		ByteBuffer slice = dup.slice().order(ByteOrder.nativeOrder());
		bufferBase.put(slice, address);
		return slice;
	}

	public static IntBuffer memIntBuffer(long address, int capacity) {
		Allocation alloc = findAllocation(address);
		if (alloc == null) {
			return null;
		}
		int offset = (int) (address - alloc.base);
		int maxBytes = alloc.size - offset;
		int maxInts = maxBytes / 4;
		int requested = capacity;
		if (requested > maxInts) {
			requested = maxInts;
		}
		ByteBuffer bb = memByteBuffer(address, requested * 4);
		if (bb == null) {
			return null;
		}
		IntBuffer ib = bb.asIntBuffer();
		intBufferBase.put(ib, address);
		return ib;
	}

	public static FloatBuffer memFloatBuffer(long address, int capacity) {
		Allocation alloc = findAllocation(address);
		if (alloc == null) {
			return null;
		}
		int offset = (int) (address - alloc.base);
		int maxBytes = alloc.size - offset;
		int maxFloats = maxBytes / 4;
		int requested = capacity;
		if (requested > maxFloats) {
			requested = maxFloats;
		}
		ByteBuffer bb = memByteBuffer(address, requested * 4);
		if (bb == null) {
			return null;
		}
		FloatBuffer fb = bb.asFloatBuffer();
		floatBufferBase.put(fb, address);
		return fb;
	}

	public static ByteBuffer memRealloc(ByteBuffer buffer, int newCapacity) {
		if (buffer == null) {
			return memAlloc(newCapacity);
		}
		int oldPosition = buffer.position();
		ByteBuffer newBuf = memAlloc(newCapacity);
		int copy = Math.min(buffer.capacity(), newCapacity);
		ByteBuffer dup = buffer.duplicate();
		dup.clear();
		dup.limit(copy);
		newBuf.put(dup);
		newBuf.position(Math.min(oldPosition, newCapacity));
		newBuf.limit(newCapacity);
		memFree(buffer);
		return newBuf;
	}

	// ---- String encoding/decoding methods ----

	public static ByteBuffer memUTF8(CharSequence text) {
		return memUTF8(text, true);
	}

	public static ByteBuffer memUTF8(CharSequence text, boolean nullTerminated) {
		if (text == null) {
			return null;
		}
		byte[] bytes = text.toString().getBytes(StandardCharsets.UTF_8);
		int len = nullTerminated ? bytes.length + 1 : bytes.length;
		ByteBuffer buf = memAlloc(len);
		buf.put(bytes);
		if (nullTerminated) {
			buf.put((byte) 0);
		}
		buf.flip();
		return buf;
	}

	public static ByteBuffer memUTF8(CharSequence text, boolean nullTerminated, ByteBuffer target) {
		if (target == null) {
			return null;
		}
		byte[] bytes = text == null ? new byte[0] : text.toString().getBytes(StandardCharsets.UTF_8);
		target.clear();
		target.put(bytes);
		if (nullTerminated) {
			target.put((byte) 0);
		}
		target.flip();
		return target;
	}

	public static String memUTF8(ByteBuffer buffer) {
		if (buffer == null) {
			return null;
		}
		int len = buffer.remaining();
		byte[] bytes = new byte[len];
		int pos = buffer.position();
		for (int i = 0; i < len; i++) {
			bytes[i] = buffer.get(pos + i);
		}
		int end = bytes.length;
		for (int i = 0; i < bytes.length; i++) {
			if (bytes[i] == 0) {
				end = i;
				break;
			}
		}
		return new String(bytes, 0, end, StandardCharsets.UTF_8);
	}

	public static String memUTF8(long address) {
		if (address == NULL) {
			return null;
		}
		Allocation alloc = findAllocation(address);
		if (alloc == null) {
			return null;
		}
		int offset = (int) (address - alloc.base);
		int max = alloc.size - offset;
		ByteBuffer buf = memByteBuffer(address, max);
		if (buf == null) {
			return null;
		}
		int end = 0;
		while (end < buf.capacity() && buf.get(end) != 0) {
			end++;
		}
		byte[] bytes = new byte[end];
		for (int i = 0; i < end; i++) {
			bytes[i] = buf.get(i);
		}
		return new String(bytes, StandardCharsets.UTF_8);
	}

	public static ByteBuffer memASCII(CharSequence text) {
		return memASCII(text, true);
	}

	public static ByteBuffer memASCII(CharSequence text, boolean nullTerminated) {
		if (text == null) {
			return null;
		}
		byte[] bytes = text.toString().getBytes(StandardCharsets.US_ASCII);
		int len = nullTerminated ? bytes.length + 1 : bytes.length;
		ByteBuffer buf = memAlloc(len);
		buf.put(bytes);
		if (nullTerminated) {
			buf.put((byte) 0);
		}
		buf.flip();
		return buf;
	}

	public static String memASCII(ByteBuffer buffer) {
		if (buffer == null) {
			return null;
		}
		int len = buffer.remaining();
		byte[] bytes = new byte[len];
		int pos = buffer.position();
		for (int i = 0; i < len; i++) {
			bytes[i] = buffer.get(pos + i);
		}
		int end = bytes.length;
		for (int i = 0; i < bytes.length; i++) {
			if (bytes[i] == 0) {
				end = i;
				break;
			}
		}
		return new String(bytes, 0, end, StandardCharsets.US_ASCII);
	}

	public static String memASCII(ByteBuffer buffer, int length) {
		if (buffer == null) {
			return null;
		}
		int len = Math.max(0, Math.min(length, buffer.remaining()));
		byte[] bytes = new byte[len];
		int pos = buffer.position();
		for (int i = 0; i < len; i++) {
			bytes[i] = buffer.get(pos + i);
		}
		int end = bytes.length;
		for (int i = 0; i < bytes.length; i++) {
			if (bytes[i] == 0) {
				end = i;
				break;
			}
		}
		return new String(bytes, 0, end, StandardCharsets.US_ASCII);
	}

	public static int memLengthUTF8(CharSequence text) {
		return text.toString().getBytes(StandardCharsets.UTF_8).length;
	}

	public static int memLengthUTF8(CharSequence text, boolean nullTerminated) {
		int len = memLengthUTF8(text);
		return nullTerminated ? len + 1 : len;
	}

	public static int memLengthASCII(CharSequence text) {
		return text.length();
	}

	public static int memLengthUTF16(CharSequence text) {
		return text.length() * 2;
	}
}
