/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.opengl;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import net.lax1dude.eaglercraft.v1_8.internal.IBufferGL;
import net.lax1dude.eaglercraft.v1_8.internal.PlatformRuntime;
import net.lax1dude.eaglercraft.v1_8.opengl.EaglercraftGPU;

import static net.lax1dude.eaglercraft.v1_8.internal.PlatformOpenGL.*;

public class GL15 extends GL14 {

	public static final int GL_ARRAY_BUFFER = 0x8892;
	public static final int GL_ELEMENT_ARRAY_BUFFER = 0x8893;
	public static final int GL_ARRAY_BUFFER_BINDING = 0x8894;
	public static final int GL_ELEMENT_ARRAY_BUFFER_BINDING = 0x8895;
	public static final int GL_STREAM_DRAW = 0x88E0;
	public static final int GL_STREAM_READ = 0x88E1;
	public static final int GL_STREAM_COPY = 0x88E2;
	public static final int GL_STATIC_DRAW = 0x88E4;
	public static final int GL_STATIC_READ = 0x88E5;
	public static final int GL_STATIC_COPY = 0x88E6;
	public static final int GL_DYNAMIC_DRAW = 0x88E8;
	public static final int GL_DYNAMIC_READ = 0x88E9;
	public static final int GL_DYNAMIC_COPY = 0x88EA;
	public static final int GL_READ_ONLY = 0x88B8;
	public static final int GL_WRITE_ONLY = 0x88B9;
	public static final int GL_READ_WRITE = 0x88BA;
	public static final int GL_BUFFER_SIZE = 0x8764;
	public static final int GL_BUFFER_USAGE = 0x8765;
	public static final int GL_BUFFER_ACCESS = 0x88BB;
	public static final int GL_BUFFER_MAPPED = 0x88BC;
	public static final int GL_BUFFER_MAP_POINTER = 0x88BD;
	public static final int GL_QUERY_COUNTER_BITS = 0x8864;
	public static final int GL_CURRENT_QUERY = 0x8865;
	public static final int GL_QUERY_RESULT = 0x8866;
	public static final int GL_QUERY_RESULT_AVAILABLE = 0x8867;
	public static final int GL_SAMPLES_PASSED = 0x8914;
	public static final int GL_SRC1_ALPHA = 0x8589;

	private static final AtomicInteger NEXT_BUFFER_ID = new AtomicInteger(1);
	private static final Map<Integer, IBufferGL> BUFFERS = new HashMap<>();
	private static final Map<Integer, Integer> BUFFER_SIZES = new HashMap<>();
	private static int boundArrayBuffer = 0;
	private static int boundElementArrayBuffer = 0;
	private static ByteBuffer mappedBuffer = null;
	private static int mappedTarget = 0;

	private static IBufferGL getBuffer(int id) {
		return id == 0 ? null : BUFFERS.get(id);
	}

	private static int getBoundBufferId(int target) {
		switch (target) {
		case GL_ARRAY_BUFFER:
			return boundArrayBuffer;
		case GL_ELEMENT_ARRAY_BUFFER:
			return boundElementArrayBuffer;
		default:
			return 0;
		}
	}

	private static void setBoundBufferId(int target, int buffer) {
		switch (target) {
		case GL_ARRAY_BUFFER:
			boundArrayBuffer = buffer;
			break;
		case GL_ELEMENT_ARRAY_BUFFER:
			boundElementArrayBuffer = buffer;
			break;
		default:
			break;
		}
	}

	private static void setBoundBufferSize(int target, int size) {
		int buffer = getBoundBufferId(target);
		if (buffer != 0) {
			BUFFER_SIZES.put(buffer, Math.max(0, size));
		}
	}

	private static net.lax1dude.eaglercraft.v1_8.internal.buffer.ByteBuffer copyBuffer(ByteBuffer src) {
		if (src == null) {
			return null;
		}
		int remaining = src.remaining();
		net.lax1dude.eaglercraft.v1_8.internal.buffer.ByteBuffer dst = PlatformRuntime.allocateByteBuffer(remaining);
		int pos = src.position();
		for (int i = 0; i < remaining; ++i) {
			dst.put(i, src.get(pos + i));
		}
		dst.position(0).limit(remaining);
		return dst;
	}

	private static net.lax1dude.eaglercraft.v1_8.internal.buffer.IntBuffer copyBuffer(IntBuffer src) {
		if (src == null) {
			return null;
		}
		int remaining = src.remaining();
		net.lax1dude.eaglercraft.v1_8.internal.buffer.IntBuffer dst = PlatformRuntime.allocateIntBuffer(remaining);
		int pos = src.position();
		for (int i = 0; i < remaining; ++i) {
			dst.put(i, src.get(pos + i));
		}
		dst.position(0).limit(remaining);
		return dst;
	}

	private static net.lax1dude.eaglercraft.v1_8.internal.buffer.FloatBuffer copyBuffer(FloatBuffer src) {
		if (src == null) {
			return null;
		}
		int remaining = src.remaining();
		net.lax1dude.eaglercraft.v1_8.internal.buffer.FloatBuffer dst = PlatformRuntime.allocateFloatBuffer(remaining);
		int pos = src.position();
		for (int i = 0; i < remaining; ++i) {
			dst.put(i, src.get(pos + i));
		}
		dst.position(0).limit(remaining);
		return dst;
	}

	public static int glGenBuffers() {
		int id = NEXT_BUFFER_ID.getAndIncrement();
		BUFFERS.put(id, _wglGenBuffers());
		return id;
	}

	public static void glGenBuffers(IntBuffer buffers) {
		if (buffers == null) {
			return;
		}
		for (int i = buffers.position(); i < buffers.limit(); ++i) {
			buffers.put(i, glGenBuffers());
		}
	}

	public static void glBindBuffer(int target, int buffer) {
		IBufferGL obj = getBuffer(buffer);
		setBoundBufferId(target, buffer);
		if (target == GL_ARRAY_BUFFER) {
			EaglercraftGPU.bindVAOGLArrayBufferNow(obj);
		} else if (target == GL_ELEMENT_ARRAY_BUFFER) {
			EaglercraftGPU.bindVAOGLElementArrayBuffer(obj);
		} else {
			_wglBindBuffer(target, obj);
		}
	}

	public static void glBufferData(int target, ByteBuffer data, int usage) {
		setBoundBufferSize(target, data == null ? 0 : data.remaining());
		net.lax1dude.eaglercraft.v1_8.internal.buffer.ByteBuffer copy = copyBuffer(data);
		try {
			_wglBufferData(target, copy, usage);
		} finally {
			if (copy != null) {
				PlatformRuntime.freeByteBuffer(copy);
			}
		}
	}

	public static void glBufferData(int target, net.lax1dude.eaglercraft.v1_8.internal.buffer.ByteBuffer data, int usage) {
		setBoundBufferSize(target, data == null ? 0 : data.remaining());
		_wglBufferData(target, data, usage);
	}

	public static void glBufferData(int target, IntBuffer data, int usage) {
		setBoundBufferSize(target, data == null ? 0 : data.remaining() << 2);
		net.lax1dude.eaglercraft.v1_8.internal.buffer.IntBuffer copy = copyBuffer(data);
		try {
			_wglBufferData(target, copy, usage);
		} finally {
			if (copy != null) {
				PlatformRuntime.freeIntBuffer(copy);
			}
		}
	}

	public static void glBufferData(int target, FloatBuffer data, int usage) {
		setBoundBufferSize(target, data == null ? 0 : data.remaining() << 2);
		net.lax1dude.eaglercraft.v1_8.internal.buffer.FloatBuffer copy = copyBuffer(data);
		try {
			_wglBufferData(target, copy, usage);
		} finally {
			if (copy != null) {
				PlatformRuntime.freeFloatBuffer(copy);
			}
		}
	}

	public static void glBufferData(int target, int size, int usage) {
		setBoundBufferSize(target, size);
		_wglBufferData(target, size, usage);
	}

	public static void glBufferData(int target, long size, int usage) {
		setBoundBufferSize(target, size > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) size);
		_wglBufferData(target, (int) size, usage);
	}

	public static ByteBuffer glMapBuffer(int target, int access) {
		if (mappedBuffer != null) {
			return null;
		}
		int buffer = getBoundBufferId(target);
		if (buffer == 0) {
			return null;
		}
		int size = BUFFER_SIZES.getOrDefault(buffer, 0);
		mappedTarget = target;
		mappedBuffer = ByteBuffer.allocateDirect(size).order(java.nio.ByteOrder.nativeOrder());
		return mappedBuffer;
	}

	public static boolean glUnmapBuffer(int target) {
		if (mappedBuffer == null || mappedTarget != target) {
			return false;
		}
		ByteBuffer upload = mappedBuffer.duplicate().order(java.nio.ByteOrder.nativeOrder());
		upload.position(0).limit(mappedBuffer.capacity());
		net.lax1dude.eaglercraft.v1_8.internal.buffer.ByteBuffer copy = copyBuffer(upload);
		try {
			_wglBufferSubData(target, 0, copy);
		} finally {
			if (copy != null) {
				PlatformRuntime.freeByteBuffer(copy);
			}
			mappedBuffer = null;
			mappedTarget = 0;
		}
		return true;
	}

	public static void glDeleteBuffers(int buffer) {
		IBufferGL obj = BUFFERS.remove(buffer);
		BUFFER_SIZES.remove(buffer);
		if (boundArrayBuffer == buffer) {
			boundArrayBuffer = 0;
		}
		if (boundElementArrayBuffer == buffer) {
			boundElementArrayBuffer = 0;
		}
		if (obj != null) {
			_wglDeleteBuffers(obj);
		}
	}

	public static void glDeleteBuffers(IntBuffer buffers) {
		if (buffers == null) {
			return;
		}
		for (int i = buffers.position(); i < buffers.limit(); ++i) {
			glDeleteBuffers(buffers.get(i));
		}
	}
}
