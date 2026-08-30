/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.opengl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import net.lax1dude.eaglercraft.v1_8.internal.IFramebufferGL;
import net.lax1dude.eaglercraft.v1_8.internal.IRenderbufferGL;
import net.lax1dude.eaglercraft.v1_8.internal.IVertexArrayGL;
import net.lax1dude.eaglercraft.v1_8.opengl.EaglercraftGPU;

import static net.lax1dude.eaglercraft.v1_8.internal.PlatformOpenGL.*;

public class GL30 extends GL21 {

	// ---- Framebuffer Object ----
	public static final int GL_FRAMEBUFFER = 0x8D40;
	public static final int GL_READ_FRAMEBUFFER = 0x8CA8;
	public static final int GL_DRAW_FRAMEBUFFER = 0x8CA9;
	public static final int GL_RENDERBUFFER = 0x8D41;

	public static final int GL_FRAMEBUFFER_COMPLETE = 0x8CD5;
	public static final int GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT = 0x8CD6;
	public static final int GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT = 0x8CD7;
	public static final int GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER = 0x8CDB;
	public static final int GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER = 0x8CDC;
	public static final int GL_FRAMEBUFFER_UNSUPPORTED = 0x8CDD;
	public static final int GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE = 0x8D56;

	public static final int GL_COLOR_ATTACHMENT0 = 0x8CE0;
	public static final int GL_COLOR_ATTACHMENT1 = 0x8CE1;
	public static final int GL_COLOR_ATTACHMENT2 = 0x8CE2;
	public static final int GL_COLOR_ATTACHMENT3 = 0x8CE3;
	public static final int GL_COLOR_ATTACHMENT4 = 0x8CE4;
	public static final int GL_COLOR_ATTACHMENT5 = 0x8CE5;
	public static final int GL_COLOR_ATTACHMENT6 = 0x8CE6;
	public static final int GL_COLOR_ATTACHMENT7 = 0x8CE7;
	public static final int GL_COLOR_ATTACHMENT8 = 0x8CE8;
	public static final int GL_COLOR_ATTACHMENT9 = 0x8CE9;
	public static final int GL_COLOR_ATTACHMENT10 = 0x8CEA;
	public static final int GL_COLOR_ATTACHMENT11 = 0x8CEB;
	public static final int GL_COLOR_ATTACHMENT12 = 0x8CEC;
	public static final int GL_COLOR_ATTACHMENT13 = 0x8CED;
	public static final int GL_COLOR_ATTACHMENT14 = 0x8CEE;
	public static final int GL_COLOR_ATTACHMENT15 = 0x8CEF;
	public static final int GL_DEPTH_ATTACHMENT = 0x8D00;
	public static final int GL_STENCIL_ATTACHMENT = 0x8D20;
	public static final int GL_DEPTH_STENCIL_ATTACHMENT = 0x821A;

	public static final int GL_MAX_COLOR_ATTACHMENTS = 0x8CDF;
	public static final int GL_MAX_RENDERBUFFER_SIZE = 0x84E8;
	public static final int GL_MAX_SAMPLES = 0x8D57;

	public static final int GL_RENDERBUFFER_WIDTH = 0x8D42;
	public static final int GL_RENDERBUFFER_HEIGHT = 0x8D43;
	public static final int GL_RENDERBUFFER_INTERNAL_FORMAT = 0x8D44;
	public static final int GL_RENDERBUFFER_RED_SIZE = 0x8D50;
	public static final int GL_RENDERBUFFER_GREEN_SIZE = 0x8D51;
	public static final int GL_RENDERBUFFER_BLUE_SIZE = 0x8D52;
	public static final int GL_RENDERBUFFER_ALPHA_SIZE = 0x8D53;
	public static final int GL_RENDERBUFFER_DEPTH_SIZE = 0x8D54;
	public static final int GL_RENDERBUFFER_STENCIL_SIZE = 0x8D55;
	public static final int GL_RENDERBUFFER_SAMPLES = 0x8CAB;

	public static final int GL_FRAMEBUFFER_BINDING = 0x8CA6;
	public static final int GL_DRAW_FRAMEBUFFER_BINDING = 0x8CA6;
	public static final int GL_READ_FRAMEBUFFER_BINDING = 0x8CAA;
	public static final int GL_RENDERBUFFER_BINDING = 0x8CA7;

	// ---- Vertex Array Object ----
	public static final int GL_VERTEX_ARRAY_BINDING = 0x85B5;

	// ---- Texture ----
	public static final int GL_RGBA32F = 0x8814;
	public static final int GL_RGB32F = 0x8815;
	public static final int GL_RGBA16F = 0x881A;
	public static final int GL_RGB16F = 0x881B;
	public static final int GL_R11F_G11F_B10F = 0x8C3A;
	public static final int GL_RGB9_E5 = 0x8C3D;
	public static final int GL_RGBA32UI = 0x8D70;
	public static final int GL_RGB32UI = 0x8D71;
	public static final int GL_RGBA16UI = 0x8D76;
	public static final int GL_RGB16UI = 0x8D77;
	public static final int GL_RGBA8UI = 0x8D7C;
	public static final int GL_RGB8UI = 0x8D7D;
	public static final int GL_RGBA32I = 0x8D82;
	public static final int GL_RGB32I = 0x8D83;
	public static final int GL_RGBA16I = 0x8D88;
	public static final int GL_RGB16I = 0x8D89;
	public static final int GL_RGBA8I = 0x8D8E;
	public static final int GL_RGB8I = 0x8D8F;
	public static final int GL_RED_INTEGER = 0x8D94;
	public static final int GL_GREEN_INTEGER = 0x8D95;
	public static final int GL_BLUE_INTEGER = 0x8D96;
	public static final int GL_RGB_INTEGER = 0x8D98;
	public static final int GL_RGBA_INTEGER = 0x8D99;
	public static final int GL_BGR_INTEGER = 0x8D9A;
	public static final int GL_BGRA_INTEGER = 0x8D9B;
	public static final int GL_HALF_FLOAT = 0x140B;
	public static final int GL_RG = 0x8227;
	public static final int GL_RG_INTEGER = 0x8228;
	public static final int GL_R8 = 0x8229;
	public static final int GL_RG8 = 0x822B;
	public static final int GL_R16F = 0x822D;
	public static final int GL_R32F = 0x822E;
	public static final int GL_RG16F = 0x822F;
	public static final int GL_RG32F = 0x8230;
	public static final int GL_R8I = 0x8231;
	public static final int GL_R8UI = 0x8232;
	public static final int GL_R16I = 0x8233;
	public static final int GL_R16UI = 0x8234;
	public static final int GL_R32I = 0x8235;
	public static final int GL_R32UI = 0x8236;
	public static final int GL_RG8I = 0x8237;
	public static final int GL_RG8UI = 0x8238;
	public static final int GL_RG16I = 0x8239;
	public static final int GL_RG16UI = 0x823A;
	public static final int GL_RG32I = 0x823B;
	public static final int GL_RG32UI = 0x823C;
	public static final int GL_DEPTH_STENCIL = 0x84F9;
	public static final int GL_UNSIGNED_INT_24_8 = 0x84FA;
	public static final int GL_DEPTH24_STENCIL8 = 0x88F0;
	public static final int GL_TEXTURE_STENCIL_SIZE = 0x88F1;
	public static final int GL_DEPTH32F_STENCIL8 = 0x8CAD;
	public static final int GL_FLOAT_32_UNSIGNED_INT_24_8_REV = 0x8DAD;

	// ---- Misc GL30 ----
	public static final int GL_MAP_READ_BIT = 0x0001;
	public static final int GL_MAP_WRITE_BIT = 0x0002;
	public static final int GL_MAP_INVALIDATE_RANGE_BIT = 0x0004;
	public static final int GL_MAP_INVALIDATE_BUFFER_BIT = 0x0008;
	public static final int GL_MAP_FLUSH_EXPLICIT_BIT = 0x0010;
	public static final int GL_MAP_UNSYNCHRONIZED_BIT = 0x0020;

	public static final int GL_COMPARE_REF_TO_TEXTURE = 0x884E;
	public static final int GL_TEXTURE_2D_ARRAY = 0x8C1A;
	public static final int GL_TEXTURE_BINDING_2D_ARRAY = 0x8C1D;
	public static final int GL_MAX_ARRAY_TEXTURE_LAYERS = 0x88FF;

	public static final int GL_UNIFORM_BUFFER = 0x8A11;
	public static final int GL_UNIFORM_BUFFER_BINDING = 0x8A28;
	public static final int GL_UNIFORM_BUFFER_START = 0x8A29;
	public static final int GL_UNIFORM_BUFFER_SIZE = 0x8A2A;
	public static final int GL_MAX_VERTEX_UNIFORM_BLOCKS = 0x8A2B;
	public static final int GL_MAX_FRAGMENT_UNIFORM_BLOCKS = 0x8A2D;
	public static final int GL_MAX_COMBINED_UNIFORM_BLOCKS = 0x8A2E;
	public static final int GL_MAX_UNIFORM_BUFFER_BINDINGS = 0x8A2F;
	public static final int GL_MAX_UNIFORM_BLOCK_SIZE = 0x8A30;
	public static final int GL_UNIFORM_BUFFER_OFFSET_ALIGNMENT = 0x8A34;

	public static final int GL_TRANSFORM_FEEDBACK_BUFFER = 0x8C8E;
	public static final int GL_TRANSFORM_FEEDBACK_BUFFER_BINDING = 0x8C8F;

	private static final AtomicInteger NEXT_FRAMEBUFFER_ID = new AtomicInteger(1);
	private static final AtomicInteger NEXT_RENDERBUFFER_ID = new AtomicInteger(1);
	private static final AtomicInteger NEXT_VERTEX_ARRAY_ID = new AtomicInteger(1);
	private static final Map<Integer, IFramebufferGL> FRAMEBUFFERS = new HashMap<>();
	private static final Map<Integer, IRenderbufferGL> RENDERBUFFERS = new HashMap<>();
	private static final Map<Integer, IVertexArrayGL> VERTEX_ARRAYS = new HashMap<>();

	private static IFramebufferGL framebuffer(int id) {
		return id == 0 ? null : FRAMEBUFFERS.get(id);
	}

	private static IRenderbufferGL renderbuffer(int id) {
		return id == 0 ? null : RENDERBUFFERS.get(id);
	}

	private static IVertexArrayGL vertexArray(int id) {
		return id == 0 ? null : VERTEX_ARRAYS.get(id);
	}

	// ---- Generate mipmap ----
	public static void glGenerateMipmap(int target) {
		_wglGenerateMipmap(target);
	}

	// ---- Tex storage ----
	public static void glTexStorage2D(int target, int levels, int internalformat, int width, int height) {
		EaglercraftGPU.glTexStorage2D(target, levels, internalformat, width, height);
	}

	// ---- String indexed ----
	public static String glGetStringi(int name, int index) {
		if (name == 0x1F03) { // GL_EXTENSIONS
			String[] extensions = getAllExtensions();
			return index >= 0 && index < extensions.length ? extensions[index] : "";
		}
		return GL11.glGetString(name);
	}

	public static int glGenVertexArrays() {
		int id = NEXT_VERTEX_ARRAY_ID.getAndIncrement();
		VERTEX_ARRAYS.put(id, EaglercraftGPU.createGLVertexArray());
		return id;
	}

	public static void glBindVertexArray(int array) {
		EaglercraftGPU.bindGLVertexArray(vertexArray(array));
	}

	public static void glDeleteVertexArrays(int array) {
		IVertexArrayGL vao = VERTEX_ARRAYS.remove(array);
		if (vao != null) {
			EaglercraftGPU.destroyGLVertexArray(vao);
		}
	}

	public static void glVertexAttribIPointer(int index, int size, int type, int stride, long pointer) {
		EaglercraftGPU.vertexAttribIPointer(index, size, type, stride, (int) pointer);
	}

	public static void glBlitFramebuffer(int srcX0, int srcY0, int srcX1, int srcY1, int dstX0, int dstY0,
			int dstX1, int dstY1, int mask, int filter) {
		_wglBlitFramebuffer(srcX0, srcY0, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, mask, filter);
	}

	public static int glGenFramebuffers() {
		int id = NEXT_FRAMEBUFFER_ID.getAndIncrement();
		FRAMEBUFFERS.put(id, _wglCreateFramebuffer());
		return id;
	}

	public static void glBindFramebuffer(int target, int framebuffer) {
		_wglBindFramebuffer(target, framebuffer(framebuffer));
	}

	public static void glDeleteFramebuffers(int framebuffer) {
		IFramebufferGL fb = FRAMEBUFFERS.remove(framebuffer);
		if (fb != null) {
			_wglDeleteFramebuffer(fb);
		}
	}

	public static int glGenRenderbuffers() {
		int id = NEXT_RENDERBUFFER_ID.getAndIncrement();
		RENDERBUFFERS.put(id, _wglCreateRenderbuffer());
		return id;
	}

	public static void glBindRenderbuffer(int target, int renderbuffer) {
		_wglBindRenderbuffer(target, renderbuffer(renderbuffer));
	}

	public static void glDeleteRenderbuffers(int renderbuffer) {
		IRenderbufferGL rb = RENDERBUFFERS.remove(renderbuffer);
		if (rb != null) {
			_wglDeleteRenderbuffer(rb);
		}
	}

	public static void glRenderbufferStorage(int target, int internalformat, int width, int height) {
		_wglRenderbufferStorage(target, internalformat, width, height);
	}

	public static void glFramebufferRenderbuffer(int target, int attachment, int renderbuffertarget, int renderbuffer) {
		_wglFramebufferRenderbuffer(target, attachment, renderbuffertarget, renderbuffer(renderbuffer));
	}

	public static int glCheckFramebufferStatus(int target) {
		int status = _wglCheckFramebufferStatus(target);
		return status == 0 ? GL_FRAMEBUFFER_COMPLETE : status;
	}

	public static void glFramebufferTexture2D(int target, int attachment, int textarget, int texture, int level) {
		EaglercraftGPU.glFramebufferTexture2D(target, attachment, textarget, texture, level);
	}
}
