/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.opengl;

import net.lax1dude.eaglercraft.v1_8.internal.PlatformRuntime;
import net.lax1dude.eaglercraft.v1_8.internal.PlatformOpenGL;
import net.lax1dude.eaglercraft.v1_8.internal.buffer.ByteBuffer;
import net.lax1dude.eaglercraft.v1_8.internal.buffer.FloatBuffer;
import net.lax1dude.eaglercraft.v1_8.internal.buffer.IntBuffer;
import net.lax1dude.eaglercraft.v1_8.opengl.EaglercraftGPU;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.lax1dude.eaglercraft.v1_8.opengl.RealOpenGLEnums;
import org.lwjgl.system.MemoryUtil;

import static net.lax1dude.eaglercraft.v1_8.opengl.EaglercraftGPU.*;
import static net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.alphaFunc;
import static net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.blendFunc;
import static net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.clear;
import static net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.clearColor;
import static net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.clearDepth;
import static net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.color;
import static net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.colorMask;
import static net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.cullFace;
import static net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.deleteTexture;
import static net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.depthFunc;
import static net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.depthMask;
import static net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.disableAlpha;
import static net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.disableBlend;
import static net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.disableColorMaterial;
import static net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.disableCull;
import static net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.disableDepth;
import static net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.disableFog;
import static net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.disableLighting;
import static net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.disableMCLight;
import static net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.disableOverlayFramebufferBlending;
import static net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.disablePolygonOffset;
import static net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.disableTexGen;
import static net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.disableTexture2D;
import static net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.doPolygonOffset;
import static net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.enableAlpha;
import static net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.enableBlend;
import static net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.enableColorMaterial;
import static net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.enableCull;
import static net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.enableDepth;
import static net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.enableFog;
import static net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.enableLighting;
import static net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.enableMCLight;
import static net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.enableOverlayFramebufferBlending;
import static net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.enablePolygonOffset;
import static net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.enableTexGen;
import static net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.enableTexture2D;
import static net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.generateTexture;
import static net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.getFloat;
import static net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.loadIdentity;
import static net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.matrixMode;
import static net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.ortho;
import static net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.popMatrix;
import static net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.pushMatrix;
import static net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.rotate;
import static net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.scale;
import static net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.setFog;
import static net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.setFogDensity;
import static net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.setFogEnd;
import static net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.setFogStart;
import static net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.shadeModel;
import static net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.translate;
import static net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.tryBlendFuncSeparate;
import static net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.viewport;

public class GL11 extends RealOpenGLEnums {

	private static final class ClientArrayPointer {
		java.nio.ByteBuffer buffer;
		ByteBuffer eaglerBuffer;
		int offset;
		int size;
		int type;
		int stride;
		boolean enabled;

		void set(java.nio.ByteBuffer ptr, int size, int type, int stride) {
			this.buffer = ptr;
			this.eaglerBuffer = null;
			this.offset = ptr != null ? ptr.position() : 0;
			this.size = size;
			this.type = type;
			this.stride = stride;
		}

		void set(ByteBuffer ptr, int size, int type, int stride) {
			this.buffer = null;
			this.eaglerBuffer = ptr;
			this.offset = ptr != null ? ptr.position() : 0;
			this.size = size;
			this.type = type;
			this.stride = stride;
		}

		boolean hasBuffer() {
			return buffer != null || eaglerBuffer != null;
		}

		int strideBytes() {
			int packed = glTypeSize(type) * size;
			return stride != 0 ? stride : packed;
		}
	}

	private static final ClientArrayPointer vertexPointerState = new ClientArrayPointer();
	private static final ClientArrayPointer colorPointerState = new ClientArrayPointer();
	private static final ClientArrayPointer normalPointerState = new ClientArrayPointer();
	private static final ClientArrayPointer[] texCoordPointerState = new ClientArrayPointer[] {
			new ClientArrayPointer(), new ClientArrayPointer(), new ClientArrayPointer(), new ClientArrayPointer() };
	private static ByteBuffer legacyArrayBuffer = null;
	private static int legacyArrayBufferSize = 0;
	private static int unpackRowLength = 0;
	private static int unpackSkipRows = 0;
	private static int unpackSkipPixels = 0;
	private static int unpackAlignment = 4;
	private static int packRowLength = 0;
	private static int packSkipRows = 0;
	private static int packSkipPixels = 0;
	private static int packAlignment = 4;
	// Last values actually sent to WebGL, initialized to the GL spec defaults, so redundant
	// glPixelStorei calls (e.g. the per-frame lightmap/dynamic-texture uploads) are skipped.
	private static int sentUnpackRowLength = 0;
	private static int sentUnpackSkipRows = 0;
	private static int sentUnpackSkipPixels = 0;
	private static int sentUnpackAlignment = 4;
	private static int sentPackRowLength = 0;
	private static int sentPackSkipRows = 0;
	private static int sentPackSkipPixels = 0;
	private static int sentPackAlignment = 4;

	// ---- Enable / Disable ----

	public static void glEnable(int cap) {
		switch (cap) {
		case GL_DEPTH_TEST:
			enableDepth();
			break;
		case GL_CULL_FACE:
			enableCull();
			break;
		case GL_BLEND:
			enableBlend();
			break;
		case GL_RESCALE_NORMAL:
			break;
		case GL_TEXTURE_2D:
			enableTexture2D();
			break;
		case GL_LIGHTING:
			enableLighting();
			break;
		case GL_LIGHT0:
			enableMCLight(0);
			break;
		case GL_LIGHT1:
			enableMCLight(1);
			break;
		case GL_ALPHA_TEST:
			enableAlpha();
			break;
		case GL_FOG:
			enableFog();
			break;
		case GL_COLOR_MATERIAL:
			enableColorMaterial();
			break;
		case GL_TEXTURE_GEN_S:
		case GL_TEXTURE_GEN_T:
		case GL_TEXTURE_GEN_R:
		case GL_TEXTURE_GEN_Q:
			enableTexGen();
			break;
		case GL_POLYGON_OFFSET_FILL:
			enablePolygonOffset();
			break;
		case GL_SCISSOR_TEST:
			PlatformOpenGL._wglEnable(GL_SCISSOR_TEST);
			break;
		case GL_OVERLAY_FRAMEBUFFER_BLENDING:
			enableOverlayFramebufferBlending();
			break;
		default:
			break;
		}
	}

	public static void glDisable(int cap) {
		switch (cap) {
		case GL_DEPTH_TEST:
			disableDepth();
			break;
		case GL_CULL_FACE:
			disableCull();
			break;
		case GL_BLEND:
			disableBlend();
			break;
		case GL_RESCALE_NORMAL:
			break;
		case GL_TEXTURE_2D:
			disableTexture2D();
			break;
		case GL_LIGHTING:
			disableLighting();
			break;
		case GL_LIGHT0:
			disableMCLight(0);
			break;
		case GL_LIGHT1:
			disableMCLight(1);
			break;
		case GL_ALPHA_TEST:
			disableAlpha();
			break;
		case GL_FOG:
			disableFog();
			break;
		case GL_COLOR_MATERIAL:
			disableColorMaterial();
			break;
		case GL_TEXTURE_GEN_S:
		case GL_TEXTURE_GEN_T:
		case GL_TEXTURE_GEN_R:
		case GL_TEXTURE_GEN_Q:
			disableTexGen();
			break;
		case GL_POLYGON_OFFSET_FILL:
			disablePolygonOffset();
			break;
		case GL_SCISSOR_TEST:
			PlatformOpenGL._wglDisable(GL_SCISSOR_TEST);
			break;
		case GL_OVERLAY_FRAMEBUFFER_BLENDING:
			disableOverlayFramebufferBlending();
			break;
		default:
			break;
		}
	}

	// ---- Depth ----

	public static void glDepthFunc(int func) {
		depthFunc(func);
	}

	public static void glDepthMask(boolean flag) {
		depthMask(flag);
	}

	public static void glClearDepth(double depth) {
		clearDepth((float) depth);
	}

	public static void glClearDepth(float depth) {
		clearDepth(depth);
	}

	// ---- Alpha / Blend ----

	public static void glAlphaFunc(int func, float ref) {
		alphaFunc(func, ref);
	}

	public static void glBlendFunc(int sfactor, int dfactor) {
		blendFunc(sfactor, dfactor);
	}

	public static void glBlendFuncSeparate(int sfactorRGB, int dfactorRGB, int sfactorAlpha, int dfactorAlpha) {
		tryBlendFuncSeparate(sfactorRGB, dfactorRGB, sfactorAlpha, dfactorAlpha);
	}

	// ---- Cull ----

	public static void glCullFace(int mode) {
		cullFace(mode);
	}

	// ---- Shade model ----

	public static void glShadeModel(int mode) {
		shadeModel(mode);
	}

	// ---- Matrix ops ----

	public static void glMatrixMode(int mode) {
		matrixMode(mode);
	}

	public static void glLoadIdentity() {
		loadIdentity();
	}

	public static void glPushMatrix() {
		pushMatrix();
	}

	public static void glPopMatrix() {
		popMatrix();
	}

	public static void glTranslatef(float x, float y, float z) {
		translate(x, y, z);
	}

	public static void glTranslated(double x, double y, double z) {
		translate((float) x, (float) y, (float) z);
	}

	public static void glRotatef(float angle, float x, float y, float z) {
		rotate(angle, x, y, z);
	}

	public static void glScalef(float x, float y, float z) {
		scale(x, y, z);
	}

	public static void glScaled(double x, double y, double z) {
		scale((float) x, (float) y, (float) z);
	}

	public static void glOrtho(double left, double right, double bottom, double top, double zNear, double zFar) {
		ortho(left, right, bottom, top, zNear, zFar);
	}

	/** Eaglercraft extension: rotate by Euler angles in ZYX order (radians). */
	public static void glRotateZYXRad(float x, float y, float z) {
		GlStateManager.rotateZYXRad(x, y, z);
	}

	// ---- Viewport / Scissor ----

	public static void glViewport(int x, int y, int width, int height) {
		viewport(x, y, width, height);
	}

	// ---- Color ----

	public static void glColor4f(float r, float g, float b, float a) {
		color(r, g, b, a);
	}

	public static void glColor3f(float r, float g, float b) {
		color(r, g, b);
	}

	public static void glColor4d(double r, double g, double b, double a) {
		color((float) r, (float) g, (float) b, (float) a);
	}

	public static void glColor3d(double r, double g, double b) {
		color((float) r, (float) g, (float) b);
	}

	public static void glColor4ub(byte r, byte g, byte b, byte a) {
		color((r & 0xFF) / 255.0f, (g & 0xFF) / 255.0f, (b & 0xFF) / 255.0f, (a & 0xFF) / 255.0f);
	}

	public static void glColorMask(boolean r, boolean g, boolean b, boolean a) {
		colorMask(r, g, b, a);
	}

	public static void glColorMaterial(int face, int mode) {
	}

	// ---- Clear ----

	public static void glClearColor(float r, float g, float b, float a) {
		clearColor(r, g, b, a);
	}

	public static void glClear(int mask) {
		clear(mask);
	}

	// ---- Texture ----

	public static void glBindTexture(int target, int texture) {
		if (target != GL_TEXTURE_2D) {
			throw new RuntimeException("Only 2D texture types are supported!");
		}
		GlStateManager.bindTexture(texture);
	}

	public static int glGenTextures() {
		return generateTexture();
	}

	public static void glGenTextures(int[] textures) {
		if (textures == null) {
			return;
		}
		for (int i = 0; i < textures.length; ++i) {
			textures[i] = glGenTextures();
		}
	}

	public static void glDeleteTextures(int texture) {
		deleteTexture(texture);
	}

	public static void glDeleteTextures(int[] textures) {
		if (textures == null) {
			return;
		}
		for (int texture : textures) {
			glDeleteTextures(texture);
		}
	}

	/** @deprecated Use {@link #glDeleteTextures(int)} */
	public static void glDeleteTexture(int texture) {
		deleteTexture(texture);
	}

	public static void glTexParameteri(int target, int pname, int param) {
		EaglercraftGPU.glTexParameteri(target, pname, param);
	}

	public static void glTexParameterf(int target, int pname, float param) {
		EaglercraftGPU.glTexParameterf(target, pname, param);
	}

	public static void glTexImage2D(int target, int level, int internalFormat, int width, int height, int border, int format, int type, ByteBuffer pixels) {
		EaglercraftGPU.glTexImage2D(target, level, internalFormat, width, height, border, format, type, pixels);
	}

	public static void glTexImage2D(int target, int level, int internalFormat, int width, int height, int border, int format, int type, IntBuffer pixels) {
		EaglercraftGPU.glTexImage2D(target, level, internalFormat, width, height, border, format, type, pixels);
	}

	public static void glTexImage2D(int target, int level, int internalFormat, int width, int height, int border, int format, int type, long pixels) {
		if (pixels == 0L || width <= 0 || height <= 0) {
			// null pixels version (allocate texture storage)
			EaglercraftGPU.glTexImage2D(target, level, internalFormat, width, height, border, format, type,
					(ByteBuffer) null);
			return;
		}
		int byteLen = computeUploadPointerLength(width, height, format, type);
		if (byteLen <= 0) {
			return;
		}
		java.nio.ByteBuffer ptr = MemoryUtil.memByteBuffer(pixels, byteLen).order(java.nio.ByteOrder.nativeOrder());
		EaglercraftGPU.glTexImage2D(target, level, internalFormat, width, height, border, format, type, ptr);
	}

	public static void glTexSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height, int format, int type, ByteBuffer pixels) {
		EaglercraftGPU.glTexSubImage2D(target, level, xoffset, yoffset, width, height, format, type, pixels);
	}

	public static void glTexSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height, int format, int type, IntBuffer pixels) {
		EaglercraftGPU.glTexSubImage2D(target, level, xoffset, yoffset, width, height, format, type, pixels);
	}

	public static void glCopyTexSubImage2D(int target, int level, int xoffset, int yoffset, int x, int y, int width, int height) {
		EaglercraftGPU.glCopyTexSubImage2D(target, level, xoffset, yoffset, x, y, width, height);
	}

	// ---- Fog ----

	public static void glFogf(int pname, float param) {
		switch (pname) {
		case GL_FOG_DENSITY:
			setFogDensity(param);
			return;
		case GL_FOG_START:
			setFogStart(param);
			return;
		case GL_FOG_END:
			setFogEnd(param);
			return;
		default:
			return;
		}
	}

	public static void glFogi(int pname, int param) {
		switch (pname) {
		case GL_FOG_MODE:
			setFog(param);
			return;
		default:
			return;
		}
	}

	public static void glFogfv(int pname, FloatBuffer params) {
		EaglercraftGPU.glFog(pname, params);
	}

	/** @deprecated Use {@link #glFogfv(int, FloatBuffer)} */
	public static void glFog(int pname, FloatBuffer params) {
		EaglercraftGPU.glFog(pname, params);
	}

	// ---- Normal ----

	public static void glNormal3f(float nx, float ny, float nz) {
		EaglercraftGPU.glNormal3f(nx, ny, nz);
	}

	// ---- Display lists ----

	public static int glGenLists(int range) {
		return EaglercraftGPU.glGenLists(range);
	}

	public static void glDeleteLists(int list, int range) {
		for (int i = 0; i < range; i++) {
			EaglercraftGPU.glDeleteLists(list + i);
		}
	}

	/** @deprecated Use {@link #glDeleteLists(int, int)} */
	public static void glDeleteLists(int list) {
		EaglercraftGPU.glDeleteLists(list);
	}

	public static void glNewList(int list, int mode) {
		EaglercraftGPU.glNewList(list, mode);
	}

	public static void glEndList() {
		EaglercraftGPU.glEndList();
	}

	public static void glCallList(int list) {
		EaglercraftGPU.glCallList(list);
	}

	public static void glCallLists(IntBuffer lists) {
		while (lists.hasRemaining()) {
			glCallList(lists.get());
		}
	}

	/** Eaglercraft extension: flush display list. */
	public static void glFlushList(int list, boolean ignoreIfNull) {
		EaglercraftGPU.flushDisplayList(list, ignoreIfNull);
	}

	/** Eaglercraft extension: flush display list. */
	public static void glFlushList(int list) {
		EaglercraftGPU.flushDisplayList(list, true);
	}

	// ---- Polygon offset ----

	public static void glPolygonOffset(float factor, float units) {
		doPolygonOffset(factor, units);
	}

	// ---- Line ----

	public static void glLineWidth(float width) {
		EaglercraftGPU.glLineWidth(width);
	}

	// ---- Get ----

	public static int glGetError() {
		return EaglercraftGPU.glGetError();
	}

	public static String glGetString(int name) {
		return EaglercraftGPU.glGetString(name);
	}

	public static int glGetInteger(int pname) {
		return EaglercraftGPU.glGetInteger(pname);
	}

	public static void glGetIntegerv(int pname, int[] params) {
		EaglercraftGPU.glGetInteger(pname, params);
	}

	public static void glGetFloatv(int pname, float[] params) {
		getFloat(pname, params);
	}

	public static void glGetFloatv(int pname, FloatBuffer params) {
		getFloat(pname, params);
	}

	/** @deprecated Use {@link #glGetFloatv(int, float[])} */
	public static void glGetFloat(int pname, float[] params) {
		getFloat(pname, params);
	}

	// ---- Read pixels ----

	public static void glReadPixels(int x, int y, int width, int height, int format, int type, ByteBuffer pixels) {
		EaglercraftGPU.glReadPixels(x, y, width, height, format, type, pixels);
	}

	// ---- Pixel store ----

	public static void glPixelStorei(int pname, int param) {
		switch (pname) {
		case GL_UNPACK_ROW_LENGTH:
			unpackRowLength = Math.max(0, param);
			if (param == sentUnpackRowLength) return;
			sentUnpackRowLength = param;
			break;
		case GL_UNPACK_SKIP_ROWS:
			unpackSkipRows = Math.max(0, param);
			if (param == sentUnpackSkipRows) return;
			sentUnpackSkipRows = param;
			break;
		case GL_UNPACK_SKIP_PIXELS:
			unpackSkipPixels = Math.max(0, param);
			if (param == sentUnpackSkipPixels) return;
			sentUnpackSkipPixels = param;
			break;
		case GL_UNPACK_ALIGNMENT:
			unpackAlignment = sanitizeAlignment(param);
			param = unpackAlignment;
			if (param == sentUnpackAlignment) return;
			sentUnpackAlignment = param;
			break;
		case GL_PACK_ROW_LENGTH:
			packRowLength = Math.max(0, param);
			if (param == sentPackRowLength) return;
			sentPackRowLength = param;
			break;
		case GL_PACK_SKIP_ROWS:
			packSkipRows = Math.max(0, param);
			if (param == sentPackSkipRows) return;
			sentPackSkipRows = param;
			break;
		case GL_PACK_SKIP_PIXELS:
			packSkipPixels = Math.max(0, param);
			if (param == sentPackSkipPixels) return;
			sentPackSkipPixels = param;
			break;
		case GL_PACK_ALIGNMENT:
			packAlignment = sanitizeAlignment(param);
			param = packAlignment;
			if (param == sentPackAlignment) return;
			sentPackAlignment = param;
			break;
		default:
			break;
		}
		net.lax1dude.eaglercraft.v1_8.internal.PlatformOpenGL._wglPixelStorei(pname, param);
	}

	// ---- Flush / Finish ----

	public static void glFlush() {
	}

	public static void glFinish() {
	}

	// ---- Blend equation ----

	public static void glBlendEquation(int mode) {
		EaglercraftGPU.glBlendEquation(mode);
	}

	// ---- Tex storage ----

	public static void glTexStorage2D(int target, int levels, int internalFormat, int width, int height) {
		EaglercraftGPU.glTexStorage2D(target, levels, internalFormat, width, height);
	}

	// ---- Active texture ----

	public static void glActiveTexture(int texture) {
		GlStateManager.setActiveTexture(texture);
	}

	// ---- Compatibility methods used by Minecraft 1.14 fixed-function bridge ----

	public static void glPushAttrib(int mask) {
	}

	public static void glPopAttrib() {
	}

	public static void glLightfv(int light, int pname, FloatBuffer params) {
	}

	public static void glLightModelfv(int pname, FloatBuffer params) {
	}

	public static void glPolygonMode(int face, int mode) {
	}

	public static void glLogicOp(int opcode) {
	}

	private static GlStateManager.TexGen mapTexGen(int coord) {
		switch (coord) {
		case GL_S:
			return GlStateManager.TexGen.S;
		case GL_T:
			return GlStateManager.TexGen.T;
		case GL_R:
			return GlStateManager.TexGen.R;
		case GL_Q:
			return GlStateManager.TexGen.Q;
		default:
			return GlStateManager.TexGen.S;
		}
	}

	public static void glTexGeni(int coord, int pname, int param) {
		if (pname == GL_TEXTURE_GEN_MODE) {
			GlStateManager.texGen(mapTexGen(coord), param);
		}
	}

	public static void glTexGenfv(int coord, int pname, FloatBuffer params) {
		GlStateManager.func_179105_a(mapTexGen(coord), pname, params);
	}

	public static void glTexEnvfv(int target, int pname, FloatBuffer params) {
	}

	public static void glTexEnvi(int target, int pname, int param) {
	}

	public static void glTexEnvf(int target, int pname, float param) {
	}

	public static int glGetTexLevelParameteri(int target, int level, int pname) {
		return EaglercraftGPU.glGetTexLevelParameteri(target, level, pname);
	}

	public static void glTexSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height, int format, int type, long pixels) {
		if (pixels == 0L || width <= 0 || height <= 0) {
			return;
		}
		int byteLen = computeUploadPointerLength(width, height, format, type);
		if (byteLen <= 0) {
			return;
		}
		java.nio.ByteBuffer ptr = MemoryUtil.memByteBuffer(pixels, byteLen).order(java.nio.ByteOrder.nativeOrder());
		EaglercraftGPU.glTexSubImage2D(target, level, xoffset, yoffset, width, height, format, type, ptr);
	}

	public static void glGetTexImage(int target, int level, int format, int type, long pixels) {
	}

	public static void glStencilFunc(int func, int ref, int mask) {
	}

	public static void glStencilMask(int mask) {
	}

	public static void glStencilOp(int sfail, int dpfail, int dppass) {
	}

	public static void glClearStencil(int s) {
	}

	public static void glRotated(double angle, double x, double y, double z) {
		rotate((float) angle, (float) x, (float) y, (float) z);
	}

	public static void glMultMatrixf(FloatBuffer matrix) {
		float[] m = new float[16];
		int pos = matrix.position();
		for (int i = 0; i < 16 && (pos + i) < matrix.limit(); ++i) {
			m[i] = matrix.get(pos + i);
		}
		GlStateManager.multMatrix(m);
	}

	public static void glTexCoord2f(float s, float t) {
		EaglercraftGPU.glMultiTexCoord2f(GL_TEXTURE0, s, t);
	}

	public static void glVertex3f(float x, float y, float z) {
	}

	public static void glNormalPointer(int type, int stride, long pointer) {
		normalPointerState.set((java.nio.ByteBuffer)null, 3, type, stride);
		normalPointerState.offset = (int) pointer;
	}

	public static void glNormalPointer(int type, int stride, ByteBuffer pointer) {
		normalPointerState.set(pointer, 3, type, stride);
	}

	public static void glTexCoordPointer(int size, int type, int stride, long pointer) {
		ClientArrayPointer p = getClientTexCoordPointer();
		p.set((java.nio.ByteBuffer)null, size, type, stride);
		p.offset = (int) pointer;
	}

	public static void glTexCoordPointer(int size, int type, int stride, ByteBuffer pointer) {
		getClientTexCoordPointer().set(pointer, size, type, stride);
	}

	public static void glVertexPointer(int size, int type, int stride, long pointer) {
		vertexPointerState.set((java.nio.ByteBuffer)null, size, type, stride);
		vertexPointerState.offset = (int) pointer;
	}

	public static void glVertexPointer(int size, int type, int stride, ByteBuffer pointer) {
		vertexPointerState.set(pointer, size, type, stride);
	}

	public static void glColorPointer(int size, int type, int stride, long pointer) {
		colorPointerState.set((java.nio.ByteBuffer)null, size, type, stride);
		colorPointerState.offset = (int) pointer;
	}

	public static void glColorPointer(int size, int type, int stride, ByteBuffer pointer) {
		colorPointerState.set(pointer, size, type, stride);
	}

	public static void glDisableClientState(int array) {
		switch (array) {
		case 32884: // GL_VERTEX_ARRAY
			vertexPointerState.enabled = false;
			break;
		case 32888: // GL_TEXTURE_COORD_ARRAY
			getClientTexCoordPointer().enabled = false;
			break;
		case 32886: // GL_COLOR_ARRAY
			colorPointerState.enabled = false;
			break;
		case 32885: // GL_NORMAL_ARRAY
			normalPointerState.enabled = false;
			break;
		default:
			break;
		}
	}

	public static void glEnableClientState(int array) {
		switch (array) {
		case 32884: // GL_VERTEX_ARRAY
			vertexPointerState.enabled = true;
			break;
		case 32888: // GL_TEXTURE_COORD_ARRAY
			getClientTexCoordPointer().enabled = true;
			break;
		case 32886: // GL_COLOR_ARRAY
			colorPointerState.enabled = true;
			break;
		case 32885: // GL_NORMAL_ARRAY
			normalPointerState.enabled = true;
			break;
		default:
			break;
		}
	}

	public static void glBegin(int mode) {
	}

	public static void glEnd() {
	}

	public static void glDrawArrays(int mode, int first, int count) {
		if (count > 0 && vertexPointerState.enabled && vertexPointerState.hasBuffer()) {
			renderLegacyClientArrays(mode, first, count);
			return;
		}
		EaglercraftGPU.drawArrays(mode, first, count);
	}

	private static ClientArrayPointer getClientTexCoordPointer() {
		int idx = GL13.getClientActiveTextureUnit();
		if (idx < 0 || idx >= texCoordPointerState.length) {
			idx = 0;
		}
		return texCoordPointerState[idx];
	}

	private static int glTypeSize(int type) {
		switch (type) {
		case GL_BYTE:
		case GL_UNSIGNED_BYTE:
			return 1;
		case GL_SHORT:
		case GL_UNSIGNED_SHORT:
			return 2;
		case GL_INT:
		case GL_UNSIGNED_INT:
		case GL_FLOAT:
			return 4;
		default:
			return 4;
		}
	}

	private static int guessPixelStrideBytes(int format, int type) {
		int channels;
		switch (format) {
		case GL_RED:
		case GL_ALPHA:
		case GL_LUMINANCE:
			channels = 1;
			break;
		case 0x8227: // GL_RG
		case GL_LUMINANCE_ALPHA:
			channels = 2;
			break;
		case GL_RGB:
			channels = 3;
			break;
		case GL_RGBA:
		default:
			channels = 4;
			break;
		}
		return channels * glTypeSize(type);
	}

	private static int sanitizeAlignment(int alignment) {
		switch (alignment) {
		case 1:
		case 2:
		case 4:
		case 8:
			return alignment;
		default:
			// Some callers pass pixel size (e.g. 3 for RGB). OpenGL only accepts 1/2/4/8.
			// 1 is the safest fallback for tightly packed rows.
			return 1;
		}
	}

	private static long alignTo(long value, int alignment) {
		long a = sanitizeAlignment(alignment);
		long mask = a - 1L;
		return (value + mask) & ~mask;
	}

	private static int computePointerLength(int width, int height, int format, int type, int rowLength, int skipRows,
			int skipPixels, int alignment) {
		int pixelStride = guessPixelStrideBytes(format, type);
		if (pixelStride <= 0 || width <= 0 || height <= 0) {
			return -1;
		}
		long rowPixels = rowLength > 0 ? rowLength : width;
		if (rowPixels < width) {
			rowPixels = width;
		}
		long rowStride = alignTo(rowPixels * pixelStride, alignment);
		long start = (long) Math.max(0, skipRows) * rowStride + (long) Math.max(0, skipPixels) * pixelStride;
		long total = start + (long) (height - 1) * rowStride + (long) width * pixelStride;
		return total > Integer.MAX_VALUE ? -1 : (int) total;
	}

	private static int computeUploadPointerLength(int width, int height, int format, int type) {
		return computePointerLength(width, height, format, type, unpackRowLength, unpackSkipRows, unpackSkipPixels,
				unpackAlignment);
	}

	private static int computeReadPointerLength(int width, int height, int format, int type) {
		return computePointerLength(width, height, format, type, packRowLength, packSkipRows, packSkipPixels,
				packAlignment);
	}

	private static float readComponentAsFloat(java.nio.ByteBuffer buf, int type, int index) {
		switch (type) {
		case GL_FLOAT:
			return buf.getFloat(index);
		case GL_UNSIGNED_BYTE:
			return (float) (buf.get(index) & 255);
		case GL_BYTE:
			return (float) buf.get(index);
		case GL_UNSIGNED_SHORT:
			return (float) (buf.getShort(index) & 0xFFFF);
		case GL_SHORT:
			return (float) buf.getShort(index);
		case GL_UNSIGNED_INT:
			return (float) (buf.getInt(index) & 0xFFFFFFFFL);
		case GL_INT:
			return (float) buf.getInt(index);
		default:
			return 0.0f;
		}
	}

	private static float readComponentAsFloat(ByteBuffer buf, int type, int index) {
		switch (type) {
		case GL_FLOAT:
			return buf.getFloat(index);
		case GL_UNSIGNED_BYTE:
			return (float) (buf.get(index) & 255);
		case GL_BYTE:
			return (float) buf.get(index);
		case GL_UNSIGNED_SHORT:
			return (float) (buf.getShort(index) & 0xFFFF);
		case GL_SHORT:
			return (float) buf.getShort(index);
		case GL_UNSIGNED_INT:
			return (float) (buf.getInt(index) & 0xFFFFFFFFL);
		case GL_INT:
			return (float) buf.getInt(index);
		default:
			return 0.0f;
		}
	}

	private static float readComponentAsFloat(ClientArrayPointer pointer, int index) {
		return pointer.eaglerBuffer != null ? readComponentAsFloat(pointer.eaglerBuffer, pointer.type, index) : readComponentAsFloat(pointer.buffer, pointer.type, index);
	}

	private static byte readComponentByte(ClientArrayPointer pointer, int index) {
		return pointer.eaglerBuffer != null ? pointer.eaglerBuffer.get(index) : pointer.buffer.get(index);
	}

	private static byte floatToColorByte(float f) {
		int v = Math.round(f * 255.0f);
		if (v < 0) {
			v = 0;
		} else if (v > 255) {
			v = 255;
		}
		return (byte) v;
	}

	private static byte toSignedNormalByte(float f) {
		int v = Math.round(f * 127.0f);
		if (v < -128) {
			v = -128;
		} else if (v > 127) {
			v = 127;
		}
		return (byte) v;
	}

	private static void renderLegacyClientArrays(int mode, int first, int count) {
		ClientArrayPointer tex0 = texCoordPointerState[0];
		ClientArrayPointer tex1 = texCoordPointerState[1];

		boolean hasColor = colorPointerState.enabled && colorPointerState.hasBuffer();
		boolean hasTex0 = tex0.enabled && tex0.hasBuffer();
		boolean hasNormal = normalPointerState.enabled && normalPointerState.hasBuffer();
		boolean hasLightmap = tex1.enabled && tex1.hasBuffer();

		int attribBits = 0;
		if (hasColor) {
			attribBits |= EaglercraftGPU.ATTRIB_COLOR;
		}
		if (hasTex0) {
			attribBits |= EaglercraftGPU.ATTRIB_TEXTURE;
		}
		if (hasNormal) {
			attribBits |= EaglercraftGPU.ATTRIB_NORMAL;
		}
		if (hasLightmap) {
			attribBits |= EaglercraftGPU.ATTRIB_LIGHTMAP;
		}

		int dstStride = 12;
		if (hasColor) {
			dstStride += 4;
		}
		if (hasTex0) {
			dstStride += 8;
		}
		if (hasNormal) {
			dstStride += 4;
		}
		if (hasLightmap) {
			dstStride += 4;
		}

		ByteBuffer dst = getLegacyArrayBuffer(dstStride * count);
		{
			int posStride = vertexPointerState.strideBytes();
			int colorStride = hasColor ? colorPointerState.strideBytes() : 0;
			int tex0Stride = hasTex0 ? tex0.strideBytes() : 0;
			int normalStride = hasNormal ? normalPointerState.strideBytes() : 0;
			int tex1Stride = hasLightmap ? tex1.strideBytes() : 0;

			for (int i = 0; i < count; ++i) {
				int srcVertex = first + i;
				int dstBase = i * dstStride;
				int out = dstBase;

				int posBase = vertexPointerState.offset + srcVertex * posStride;
				for (int c = 0; c < 3; ++c) {
					float v = readComponentAsFloat(vertexPointerState, posBase + c * glTypeSize(vertexPointerState.type));
					dst.putFloat(out, v);
					out += 4;
				}

				if (hasColor) {
					int colorBase = colorPointerState.offset + srcVertex * colorStride;
					byte r;
					byte g;
					byte b;
					byte a;
					if (colorPointerState.type == GL_FLOAT) {
						r = floatToColorByte(readComponentAsFloat(colorPointerState, colorBase));
						g = floatToColorByte(readComponentAsFloat(colorPointerState, colorBase + 4));
						b = floatToColorByte(readComponentAsFloat(colorPointerState, colorBase + 8));
						a = colorPointerState.size >= 4 ? floatToColorByte(readComponentAsFloat(colorPointerState, colorBase + 12)) : (byte) 255;
					} else {
						r = readComponentByte(colorPointerState, colorBase);
						g = colorPointerState.size > 1 ? readComponentByte(colorPointerState, colorBase + glTypeSize(colorPointerState.type)) : (byte) 255;
						b = colorPointerState.size > 2 ? readComponentByte(colorPointerState, colorBase + glTypeSize(colorPointerState.type) * 2) : (byte) 255;
						a = colorPointerState.size > 3 ? readComponentByte(colorPointerState, colorBase + glTypeSize(colorPointerState.type) * 3) : (byte) 255;
					}
					dst.put(out, r);
					dst.put(out + 1, g);
					dst.put(out + 2, b);
					dst.put(out + 3, a);
					out += 4;
				}

				if (hasTex0) {
					int texBase = tex0.offset + srcVertex * tex0Stride;
					float u = readComponentAsFloat(tex0, texBase);
					float v = readComponentAsFloat(tex0, texBase + glTypeSize(tex0.type));
					dst.putFloat(out, u);
					dst.putFloat(out + 4, v);
					out += 8;
				}

				if (hasNormal) {
					int nBase = normalPointerState.offset + srcVertex * normalStride;
					byte nx;
					byte ny;
					byte nz;
					if (normalPointerState.type == GL_FLOAT) {
						nx = toSignedNormalByte(readComponentAsFloat(normalPointerState, nBase));
						ny = toSignedNormalByte(readComponentAsFloat(normalPointerState, nBase + 4));
						nz = toSignedNormalByte(readComponentAsFloat(normalPointerState, nBase + 8));
					} else {
						nx = readComponentByte(normalPointerState, nBase);
						ny = readComponentByte(normalPointerState, nBase + glTypeSize(normalPointerState.type));
						nz = readComponentByte(normalPointerState, nBase + glTypeSize(normalPointerState.type) * 2);
					}
					dst.put(out, nx);
					dst.put(out + 1, ny);
					dst.put(out + 2, nz);
					dst.put(out + 3, (byte) 0);
					out += 4;
				}

				if (hasLightmap) {
					int lmBase = tex1.offset + srcVertex * tex1Stride;
					short lmU = (short) (int) readComponentAsFloat(tex1, lmBase);
					short lmV = (short) (int) readComponentAsFloat(tex1, lmBase + glTypeSize(tex1.type));
					dst.putShort(out, lmU);
					dst.putShort(out + 2, lmV);
				}
			}

			dst.position(0).limit(dstStride * count);
			EaglercraftGPU.renderBuffer(dst, attribBits, mode, count);
		}
	}

	private static ByteBuffer getLegacyArrayBuffer(int size) {
		if (legacyArrayBuffer == null || legacyArrayBufferSize < size) {
			if (legacyArrayBuffer != null) {
				PlatformRuntime.freeByteBuffer(legacyArrayBuffer);
			}
			legacyArrayBufferSize = size;
			legacyArrayBuffer = PlatformRuntime.allocateByteBuffer(size);
		}
		legacyArrayBuffer.clear();
		return legacyArrayBuffer;
	}

	public static void glPixelTransferf(int pname, float param) {
	}

	public static void glReadPixels(int x, int y, int width, int height, int format, int type, long pixels) {
		if (pixels == 0L || width <= 0 || height <= 0) {
			return;
		}
		int byteLen = computeReadPointerLength(width, height, format, type);
		if (byteLen <= 0) {
			return;
		}
		java.nio.ByteBuffer ptr = MemoryUtil.memByteBuffer(pixels, byteLen).order(java.nio.ByteOrder.nativeOrder());
		EaglercraftGPU.glReadPixels(x, y, width, height, format, type, ptr);
	}

	public static void glDrawElements(int mode, int count, int type, long indices) {
		EaglercraftGPU.drawElements(mode, count, type, (int) indices);
	}

	public static void glDrawPixels(int width, int height, int format, int type, long pixels) {
		if (pixels == 0L || width <= 0 || height <= 0) {
			return;
		}
		int byteLen = computeUploadPointerLength(width, height, format, type);
		if (byteLen <= 0) {
			return;
		}
		int previousTexture = GlStateManager.getBoundTexture();
		int texture = generateTexture();
		ByteBuffer vertices = PlatformRuntime.allocateByteBuffer(80);
		try {
			GlStateManager.bindTexture(texture);
			EaglercraftGPU.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
			EaglercraftGPU.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
			EaglercraftGPU.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
			EaglercraftGPU.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
			java.nio.ByteBuffer ptr = MemoryUtil.memByteBuffer(pixels, byteLen).order(java.nio.ByteOrder.nativeOrder());
			EaglercraftGPU.glTexImage2D(GL_TEXTURE_2D, 0, format, width, height, 0, format, type, ptr);
			putDrawPixelsVertex(vertices, 0, 0.0f, 0.0f, 0.0f, 0.0f);
			putDrawPixelsVertex(vertices, 20, width, 0.0f, 1.0f, 0.0f);
			putDrawPixelsVertex(vertices, 40, width, height, 1.0f, 1.0f);
			putDrawPixelsVertex(vertices, 60, 0.0f, height, 0.0f, 1.0f);
			vertices.position(0).limit(80);
			EaglercraftGPU.renderBuffer(vertices, EaglercraftGPU.ATTRIB_TEXTURE, GL_QUADS, 4);
		} finally {
			PlatformRuntime.freeByteBuffer(vertices);
			deleteTexture(texture);
			GlStateManager.bindTexture(previousTexture);
		}
	}

	private static void putDrawPixelsVertex(ByteBuffer vertices, int base, float x, float y, float u, float v) {
		vertices.putFloat(base, x);
		vertices.putFloat(base + 4, y);
		vertices.putFloat(base + 8, 0.0f);
		vertices.putFloat(base + 12, u);
		vertices.putFloat(base + 16, v);
	}

	public static void glGetIntegerv(int pname, IntBuffer params) {
		if (params != null && params.remaining() > 0) {
			params.put(params.position(), glGetInteger(pname));
		}
	}

	// Reused scratch arrays for the non-array source path so these conversions don't
	// allocate per call. Render-thread only.
	private static float[] copyFloatScratch = new float[0];
	private static int[] copyIntScratch = new int[0];
	private static byte[] copyByteScratch = new byte[0];

	private static FloatBuffer copyFloatBuffer(java.nio.FloatBuffer src) {
		if (src == null) {
			return null;
		}
		int remaining = src.remaining();
		FloatBuffer dst = PlatformRuntime.allocateFloatBuffer(remaining);
		// Bulk copy via lax's array put (typed-array set) instead of a per-element loop.
		if (src.hasArray()) {
			dst.put(src.array(), src.arrayOffset() + src.position(), remaining);
		} else {
			float[] tmp = copyFloatScratch;
			if (tmp.length < remaining) {
				tmp = new float[remaining];
				copyFloatScratch = tmp;
			}
			src.duplicate().get(tmp, 0, remaining);
			dst.put(tmp, 0, remaining);
		}
		dst.position(0).limit(remaining);
		return dst;
	}

	private static IntBuffer copyIntBuffer(java.nio.IntBuffer src) {
		if (src == null) {
			return null;
		}
		int remaining = src.remaining();
		IntBuffer dst = PlatformRuntime.allocateIntBuffer(remaining);
		if (src.hasArray()) {
			dst.put(src.array(), src.arrayOffset() + src.position(), remaining);
		} else {
			int[] tmp = copyIntScratch;
			if (tmp.length < remaining) {
				tmp = new int[remaining];
				copyIntScratch = tmp;
			}
			src.duplicate().get(tmp, 0, remaining);
			dst.put(tmp, 0, remaining);
		}
		dst.position(0).limit(remaining);
		return dst;
	}

	private static ByteBuffer copyByteBuffer(java.nio.ByteBuffer src) {
		if (src == null) {
			return null;
		}
		int remaining = src.remaining();
		ByteBuffer dst = PlatformRuntime.allocateByteBuffer(remaining);
		if (src.hasArray()) {
			dst.put(src.array(), src.arrayOffset() + src.position(), remaining);
		} else {
			byte[] tmp = copyByteScratch;
			if (tmp.length < remaining) {
				tmp = new byte[remaining];
				copyByteScratch = tmp;
			}
			src.duplicate().get(tmp, 0, remaining);
			dst.put(tmp, 0, remaining);
		}
		dst.position(0).limit(remaining);
		return dst;
	}

	public static void glLightfv(int light, int pname, java.nio.FloatBuffer params) {
		FloatBuffer copy = copyFloatBuffer(params);
		try {
			glLightfv(light, pname, copy);
		} finally {
			if (copy != null) {
				PlatformRuntime.freeFloatBuffer(copy);
			}
		}
	}

	public static void glLightModelfv(int pname, java.nio.FloatBuffer params) {
		FloatBuffer copy = copyFloatBuffer(params);
		try {
			glLightModelfv(pname, copy);
		} finally {
			if (copy != null) {
				PlatformRuntime.freeFloatBuffer(copy);
			}
		}
	}

	public static void glFogfv(int pname, java.nio.FloatBuffer params) {
		FloatBuffer copy = copyFloatBuffer(params);
		try {
			glFogfv(pname, copy);
		} finally {
			if (copy != null) {
				PlatformRuntime.freeFloatBuffer(copy);
			}
		}
	}

	public static void glTexGenfv(int coord, int pname, java.nio.FloatBuffer params) {
		FloatBuffer copy = copyFloatBuffer(params);
		try {
			glTexGenfv(coord, pname, copy);
		} finally {
			if (copy != null) {
				PlatformRuntime.freeFloatBuffer(copy);
			}
		}
	}

	public static void glTexEnvfv(int target, int pname, java.nio.FloatBuffer params) {
		FloatBuffer copy = copyFloatBuffer(params);
		try {
			glTexEnvfv(target, pname, copy);
		} finally {
			if (copy != null) {
				PlatformRuntime.freeFloatBuffer(copy);
			}
		}
	}

	public static void glTexImage2D(int target, int level, int internalFormat, int width, int height, int border, int format, int type, java.nio.IntBuffer pixels) {
		IntBuffer copy = copyIntBuffer(pixels);
		try {
			glTexImage2D(target, level, internalFormat, width, height, border, format, type, copy);
		} finally {
			if (copy != null) {
				PlatformRuntime.freeIntBuffer(copy);
			}
		}
	}

	public static void glTexImage2D(int target, int level, int internalFormat, int width, int height, int border, int format, int type, java.nio.ByteBuffer pixels) {
		ByteBuffer copy = copyByteBuffer(pixels);
		try {
			glTexImage2D(target, level, internalFormat, width, height, border, format, type, copy);
		} finally {
			if (copy != null) {
				PlatformRuntime.freeByteBuffer(copy);
			}
		}
	}

	public static void glTexSubImage2D(int target, int level, int xoffset, int yoffset, int width, int height, int format, int type, java.nio.IntBuffer pixels) {
		IntBuffer copy = copyIntBuffer(pixels);
		try {
			glTexSubImage2D(target, level, xoffset, yoffset, width, height, format, type, copy);
		} finally {
			if (copy != null) {
				PlatformRuntime.freeIntBuffer(copy);
			}
		}
	}

	public static void glMultMatrixf(java.nio.FloatBuffer matrix) {
		FloatBuffer copy = copyFloatBuffer(matrix);
		try {
			glMultMatrixf(copy);
		} finally {
			if (copy != null) {
				PlatformRuntime.freeFloatBuffer(copy);
			}
		}
	}

	public static void glNormalPointer(int type, int stride, java.nio.ByteBuffer pointer) {
		normalPointerState.set(pointer, 3, type, stride);
	}

	public static void glTexCoordPointer(int size, int type, int stride, java.nio.ByteBuffer pointer) {
		getClientTexCoordPointer().set(pointer, size, type, stride);
	}

	public static void glVertexPointer(int size, int type, int stride, java.nio.ByteBuffer pointer) {
		vertexPointerState.set(pointer, size, type, stride);
	}

	public static void glColorPointer(int size, int type, int stride, java.nio.ByteBuffer pointer) {
		colorPointerState.set(pointer, size, type, stride);
	}

	public static void glReadPixels(int x, int y, int width, int height, int format, int type, java.nio.ByteBuffer pixels) {
		EaglercraftGPU.glReadPixels(x, y, width, height, format, type, pixels);
	}

	public static void glGetIntegerv(int pname, java.nio.IntBuffer params) {
		if (params != null && params.remaining() > 0) {
			params.put(params.position(), glGetInteger(pname));
		}
	}

	public static void glGetFloatv(int pname, java.nio.FloatBuffer params) {
		if (params == null) {
			return;
		}
		float[] arr = new float[Math.max(params.remaining(), 16)];
		getFloat(pname, arr);
		int pos = params.position();
		int len = Math.min(params.remaining(), arr.length);
		for (int i = 0; i < len; ++i) {
			params.put(pos + i, arr[i]);
		}
	}
}
