package net.lax1dude.eaglercraft.v1_8.internal;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.teavm.jso.webgl.WebGLUniformLocation;

import net.lax1dude.eaglercraft.v1_8.internal.buffer.ByteBuffer;
import net.lax1dude.eaglercraft.v1_8.internal.buffer.EaglerArrayBufferAllocator;
import net.lax1dude.eaglercraft.v1_8.internal.buffer.FloatBuffer;
import net.lax1dude.eaglercraft.v1_8.internal.buffer.IntBuffer;
import net.lax1dude.eaglercraft.v1_8.internal.teavm.TeaVMClientConfigAdapter;
import net.lax1dude.eaglercraft.v1_8.internal.teavm.WebGL2RenderingContext;
import net.lax1dude.eaglercraft.v1_8.internal.teavm.WebGLANGLEInstancedArrays;
import net.lax1dude.eaglercraft.v1_8.internal.teavm.WebGLBackBuffer;
import net.lax1dude.eaglercraft.v1_8.internal.teavm.WebGLOESVertexArrayObject;
import net.lax1dude.eaglercraft.v1_8.internal.teavm.WebGLVertexArray;
import net.lax1dude.eaglercraft.v1_8.opengl.EaglercraftGPU;

/**
 * Copyright (c) 2022-2024 lax1dude. All Rights Reserved.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 */
public class PlatformOpenGL {
	
	private static final Logger logger = LogManager.getLogger("PlatformOpenGL");
	
	static WebGL2RenderingContext ctx = null;
	static int glesVers = -1;

	static boolean hasANGLEInstancedArrays = false;
	static boolean hasEXTColorBufferFloat = false;
	static boolean hasEXTColorBufferHalfFloat = false;
	static boolean hasEXTShaderTextureLOD = false;
	static boolean hasOESFBORenderMipmap = false;
	static boolean hasOESVertexArrayObject = false;
	static boolean hasOESTextureFloat = false;
	static boolean hasOESTextureFloatLinear = false;
	static boolean hasOESTextureHalfFloat = false;
	static boolean hasOESTextureHalfFloatLinear = false;
	static boolean hasEXTTextureFilterAnisotropic = false;
	static boolean hasWEBGLDebugRendererInfo = false;

	static WebGLANGLEInstancedArrays ANGLEInstancedArrays = null;
	static WebGLOESVertexArrayObject OESVertexArrayObject = null;

	static boolean hasFBO16FSupport = false;
	static boolean hasFBO32FSupport = false;
	static boolean hasLinearHDR16FSupport = false;
	static boolean hasLinearHDR32FSupport = false;

	static final int VAO_IMPL_NONE = -1;
	static final int VAO_IMPL_CORE = 0;
	static final int VAO_IMPL_OES = 1;
	static int vertexArrayImpl = VAO_IMPL_NONE;

	static final int INSTANCE_IMPL_NONE = -1;
	static final int INSTANCE_IMPL_CORE = 0;
	static final int INSTANCE_IMPL_ANGLE = 1;
	static int instancingImpl = INSTANCE_IMPL_NONE;

	private static final int _GL_ARRAY_BUFFER = 0x8892;
	private static final int _GL_ELEMENT_ARRAY_BUFFER = 0x8893;
	private static final int _GL_COPY_READ_BUFFER = 0x8F36;
	private static final int _GL_COPY_WRITE_BUFFER = 0x8F37;
	private static final int _GL_UNIFORM_BUFFER = 0x8A11;
	private static final int _GL_TEXTURE0 = 0x84C0;
	private static final int _GL_TEXTURE_2D = 0x0DE1;
	private static final int _GL_TEXTURE_CUBE_MAP = 0x8513;
	private static final int _GL_TEXTURE_3D = 0x806F;
	private static final int _GL_TEXTURE_2D_ARRAY = 0x8C1A;
	private static final int _GL_FRAMEBUFFER = 0x8D40;
	private static final int _GL_READ_FRAMEBUFFER = 0x8CA8;
	private static final int _GL_DRAW_FRAMEBUFFER = 0x8CA9;
	private static final int _GL_RENDERBUFFER = 0x8D41;
	private static final int MAX_TRACKED_TEXTURE_UNITS = 32;
	private static final int MAX_TRACKED_CAPABILITIES = 32;
	private static final int MAX_TRACKED_DRAW_BUFFERS = 8;

	private static final int[] cachedCapabilityEnums = new int[MAX_TRACKED_CAPABILITIES];
	private static final boolean[] cachedCapabilityValues = new boolean[MAX_TRACKED_CAPABILITIES];
	private static int cachedCapabilityCount = 0;
	private static boolean cachedClearColorKnown = false;
	private static float cachedClearColorR = 0.0f;
	private static float cachedClearColorG = 0.0f;
	private static float cachedClearColorB = 0.0f;
	private static float cachedClearColorA = 0.0f;
	private static boolean cachedClearDepthKnown = false;
	private static float cachedClearDepth = 0.0f;
	private static boolean cachedDepthFuncKnown = false;
	private static int cachedDepthFunc = 0;
	private static boolean cachedDepthMaskKnown = false;
	private static boolean cachedDepthMask = false;
	private static boolean cachedCullFaceKnown = false;
	private static int cachedCullFace = 0;
	private static boolean cachedViewportKnown = false;
	private static int cachedViewportX = 0;
	private static int cachedViewportY = 0;
	private static int cachedViewportW = 0;
	private static int cachedViewportH = 0;
	private static boolean cachedScissorKnown = false;
	private static int cachedScissorX = 0;
	private static int cachedScissorY = 0;
	private static int cachedScissorW = 0;
	private static int cachedScissorH = 0;
	private static boolean cachedBlendFuncKnown = false;
	private static int cachedBlendSrcColor = 0;
	private static int cachedBlendDstColor = 0;
	private static int cachedBlendSrcAlpha = 0;
	private static int cachedBlendDstAlpha = 0;
	private static boolean cachedBlendEquationKnown = false;
	private static int cachedBlendEquation = 0;
	private static boolean cachedBlendColorKnown = false;
	private static float cachedBlendColorR = 0.0f;
	private static float cachedBlendColorG = 0.0f;
	private static float cachedBlendColorB = 0.0f;
	private static float cachedBlendColorA = 0.0f;
	private static boolean cachedColorMaskKnown = false;
	private static boolean cachedColorMaskR = false;
	private static boolean cachedColorMaskG = false;
	private static boolean cachedColorMaskB = false;
	private static boolean cachedColorMaskA = false;
	private static boolean cachedPolygonOffsetKnown = false;
	private static float cachedPolygonOffsetFactor = 0.0f;
	private static float cachedPolygonOffsetUnits = 0.0f;
	private static boolean cachedLineWidthKnown = false;
	private static float cachedLineWidth = 0.0f;
	private static final IBufferGL[] cachedBuffers = new IBufferGL[5];
	private static final boolean[] cachedBuffersKnown = new boolean[5];
	private static IVertexArrayGL cachedVertexArray = null;
	private static boolean cachedVertexArrayKnown = false;
	private static int cachedActiveTextureUnit = 0;
	private static boolean cachedActiveTextureKnown = false;
	private static final ITextureGL[][] cachedTextures = new ITextureGL[MAX_TRACKED_TEXTURE_UNITS][4];
	private static final boolean[][] cachedTexturesKnown = new boolean[MAX_TRACKED_TEXTURE_UNITS][4];
	private static IProgramGL cachedProgram = null;
	private static boolean cachedProgramKnown = false;
	private static IFramebufferGL cachedReadFramebuffer = null;
	private static IFramebufferGL cachedDrawFramebuffer = null;
	private static boolean cachedReadFramebufferKnown = false;
	private static boolean cachedDrawFramebufferKnown = false;
	private static boolean cachedDrawBuffersKnown = false;
	private static final int[] cachedDrawBuffers = new int[MAX_TRACKED_DRAW_BUFFERS];
	private static int cachedDrawBufferCount = 0;
	private static boolean cachedReadBufferKnown = false;
	private static int cachedReadBuffer = 0;
	private static IRenderbufferGL cachedRenderbuffer = null;
	private static boolean cachedRenderbufferKnown = false;

	static void setCurrentContext(int glesVersIn, WebGL2RenderingContext context) {
		ctx = context;
		resetStateCache();
		if(ctx != null) {
			glesVers = glesVersIn;
			boolean allowExts = ((TeaVMClientConfigAdapter)PlatformRuntime.getClientConfigAdapter()).isUseWebGLExtTeaVM();
			if(allowExts) {
				ANGLEInstancedArrays = glesVersIn == 200 ? (WebGLANGLEInstancedArrays) ctx.getExtension("ANGLE_instanced_arrays") : null;
				hasANGLEInstancedArrays = glesVersIn == 200 && ANGLEInstancedArrays != null;
				hasEXTColorBufferFloat = glesVersIn == 300 && ctx.getExtension("EXT_color_buffer_float") != null;
				hasEXTColorBufferHalfFloat = !hasEXTColorBufferFloat
						&& (glesVersIn == 300 || glesVersIn == 200) && ctx.getExtension("EXT_color_buffer_half_float") != null;
				hasEXTShaderTextureLOD = glesVersIn == 200 && ctx.getExtension("EXT_shader_texture_lod") != null;
				hasOESFBORenderMipmap = glesVersIn == 200 && ctx.getExtension("OES_fbo_render_mipmap") != null;
				OESVertexArrayObject = glesVersIn == 200 ? (WebGLOESVertexArrayObject) ctx.getExtension("OES_vertex_array_object") : null;
				hasOESVertexArrayObject = glesVersIn == 200 && OESVertexArrayObject != null;
				hasOESTextureFloat = glesVersIn == 200 && ctx.getExtension("OES_texture_float") != null;
				hasOESTextureFloatLinear = glesVersIn >= 300 && ctx.getExtension("OES_texture_float_linear") != null;
				hasOESTextureHalfFloat = glesVersIn == 200 && ctx.getExtension("OES_texture_half_float") != null;
				hasOESTextureHalfFloatLinear = glesVersIn == 200 && ctx.getExtension("OES_texture_half_float_linear") != null;
				hasEXTTextureFilterAnisotropic = ctx.getExtension("EXT_texture_filter_anisotropic") != null;
			}else {
				hasANGLEInstancedArrays = false;
				hasEXTColorBufferFloat = false;
				hasEXTColorBufferHalfFloat = false;
				hasEXTShaderTextureLOD = false;
				hasOESFBORenderMipmap = false;
				hasOESVertexArrayObject = false;
				hasOESTextureFloat = false;
				hasOESTextureFloatLinear = false;
				hasOESTextureHalfFloat = false;
				hasOESTextureHalfFloatLinear = false;
				hasEXTTextureFilterAnisotropic = false;
			}
			hasWEBGLDebugRendererInfo = ctx.getExtension("WEBGL_debug_renderer_info") != null;
			
			hasFBO16FSupport = ((glesVersIn == 300 || hasOESTextureFloat) && (hasEXTColorBufferFloat || hasEXTColorBufferHalfFloat));
			hasFBO32FSupport = ((glesVersIn == 300 || hasOESTextureHalfFloat) && hasEXTColorBufferFloat);
			hasLinearHDR16FSupport = glesVersIn >= 300 || hasOESTextureHalfFloatLinear;
			hasLinearHDR32FSupport = glesVersIn >= 300 && hasOESTextureFloatLinear;
			
			if(glesVersIn >= 300) {
				vertexArrayImpl = VAO_IMPL_CORE;
				instancingImpl = INSTANCE_IMPL_CORE;
			}else if(glesVersIn == 200) {
				vertexArrayImpl = hasOESVertexArrayObject ? VAO_IMPL_OES : VAO_IMPL_NONE;
				instancingImpl = hasANGLEInstancedArrays ? INSTANCE_IMPL_ANGLE : INSTANCE_IMPL_NONE;
			}else {
				vertexArrayImpl = VAO_IMPL_NONE;
				instancingImpl = INSTANCE_IMPL_NONE;
			}
			
			_wglClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		}else {
			glesVers = -1;
			hasANGLEInstancedArrays = false;
			hasEXTColorBufferFloat = false;
			hasEXTColorBufferHalfFloat = false;
			hasEXTShaderTextureLOD = false;
			hasOESFBORenderMipmap = false;
			hasOESVertexArrayObject = false;
			hasOESTextureFloat = false;
			hasOESTextureFloatLinear = false;
			hasOESTextureHalfFloat = false;
			hasOESTextureHalfFloatLinear = false;
			hasEXTTextureFilterAnisotropic = false;
			hasWEBGLDebugRendererInfo = false;
			ANGLEInstancedArrays = null;
			OESVertexArrayObject = null;
			hasFBO16FSupport = false;
			hasFBO32FSupport = false;
			hasLinearHDR16FSupport = false;
			hasLinearHDR32FSupport = false;
		}
	}

	private static void resetStateCache() {
		cachedCapabilityCount = 0;
		cachedClearColorKnown = false;
		cachedClearDepthKnown = false;
		cachedDepthFuncKnown = false;
		cachedDepthMaskKnown = false;
		cachedCullFaceKnown = false;
		cachedViewportKnown = false;
		cachedScissorKnown = false;
		cachedBlendFuncKnown = false;
		cachedBlendEquationKnown = false;
		cachedBlendColorKnown = false;
		cachedColorMaskKnown = false;
		cachedPolygonOffsetKnown = false;
		cachedLineWidthKnown = false;
		for(int i = 0; i < cachedBuffersKnown.length; ++i) {
			cachedBuffersKnown[i] = false;
			cachedBuffers[i] = null;
		}
		cachedVertexArrayKnown = false;
		cachedVertexArray = null;
		cachedActiveTextureUnit = 0;
		cachedActiveTextureKnown = false;
		for(int i = 0; i < cachedTexturesKnown.length; ++i) {
			for(int j = 0; j < cachedTexturesKnown[i].length; ++j) {
				cachedTexturesKnown[i][j] = false;
				cachedTextures[i][j] = null;
			}
		}
		cachedProgramKnown = false;
		cachedProgram = null;
		cachedReadFramebufferKnown = false;
		cachedDrawFramebufferKnown = false;
		cachedReadFramebuffer = null;
		cachedDrawFramebuffer = null;
		invalidateFramebufferDependentState();
		cachedRenderbufferKnown = false;
		cachedRenderbuffer = null;
	}

	public static void invalidateStateCache() {
		resetStateCache();
	}

	private static boolean shouldUpdateCapability(int glEnum, boolean enabled) {
		for(int i = 0; i < cachedCapabilityCount; ++i) {
			if(cachedCapabilityEnums[i] == glEnum) {
				if(cachedCapabilityValues[i] == enabled) {
					return false;
				}
				cachedCapabilityValues[i] = enabled;
				return true;
			}
		}
		if(cachedCapabilityCount < MAX_TRACKED_CAPABILITIES) {
			cachedCapabilityEnums[cachedCapabilityCount] = glEnum;
			cachedCapabilityValues[cachedCapabilityCount] = enabled;
			++cachedCapabilityCount;
		}
		return true;
	}

	private static int getBufferCacheIndex(int target) {
		switch(target) {
		case _GL_ARRAY_BUFFER:
			return 0;
		case _GL_ELEMENT_ARRAY_BUFFER:
			return 1;
		case _GL_COPY_READ_BUFFER:
			return 2;
		case _GL_COPY_WRITE_BUFFER:
			return 3;
		case _GL_UNIFORM_BUFFER:
			return 4;
		default:
			return -1;
		}
	}

	private static int getTextureCacheIndex(int target) {
		switch(target) {
		case _GL_TEXTURE_2D:
			return 0;
		case _GL_TEXTURE_CUBE_MAP:
			return 1;
		case _GL_TEXTURE_3D:
			return 2;
		case _GL_TEXTURE_2D_ARRAY:
			return 3;
		default:
			return -1;
		}
	}

	private static void invalidateTextureBinding(ITextureGL texture) {
		for(int i = 0; i < cachedTextures.length; ++i) {
			for(int j = 0; j < cachedTextures[i].length; ++j) {
				if(cachedTexturesKnown[i][j] && cachedTextures[i][j] == texture) {
					cachedTexturesKnown[i][j] = false;
					cachedTextures[i][j] = null;
				}
			}
		}
	}

	private static void invalidateBufferBinding(IBufferGL buffer) {
		for(int i = 0; i < cachedBuffers.length; ++i) {
			if(cachedBuffersKnown[i] && cachedBuffers[i] == buffer) {
				cachedBuffersKnown[i] = false;
				cachedBuffers[i] = null;
			}
		}
	}

	private static void invalidateFramebufferBinding(IFramebufferGL framebuffer) {
		if(cachedReadFramebufferKnown && cachedReadFramebuffer == framebuffer) {
			cachedReadFramebufferKnown = false;
			cachedReadFramebuffer = null;
		}
		if(cachedDrawFramebufferKnown && cachedDrawFramebuffer == framebuffer) {
			cachedDrawFramebufferKnown = false;
			cachedDrawFramebuffer = null;
		}
		invalidateFramebufferDependentState();
	}

	private static void invalidateFramebufferDependentState() {
		cachedDrawBuffersKnown = false;
		cachedDrawBufferCount = 0;
		cachedReadBufferKnown = false;
		cachedReadBuffer = 0;
	}

	private static void invalidateElementArrayBindingForVertexArrayChange() {
		cachedBuffersKnown[1] = false;
		cachedBuffers[1] = null;
	}

	private static boolean shouldUpdateDrawBuffer(int buffer) {
		if(cachedDrawBuffersKnown && cachedDrawBufferCount == 1 && cachedDrawBuffers[0] == buffer) {
			return false;
		}
		cachedDrawBuffersKnown = true;
		cachedDrawBufferCount = 1;
		cachedDrawBuffers[0] = buffer;
		return true;
	}

	private static boolean shouldUpdateDrawBuffers(int[] buffers) {
		int len = buffers.length;
		if(cachedDrawBuffersKnown && cachedDrawBufferCount == len && len <= MAX_TRACKED_DRAW_BUFFERS) {
			boolean same = true;
			for(int i = 0; i < len; ++i) {
				if(cachedDrawBuffers[i] != buffers[i]) {
					same = false;
					break;
				}
			}
			if(same) {
				return false;
			}
		}
		if(len <= MAX_TRACKED_DRAW_BUFFERS) {
			cachedDrawBuffersKnown = true;
			cachedDrawBufferCount = len;
			for(int i = 0; i < len; ++i) {
				cachedDrawBuffers[i] = buffers[i];
			}
		}else {
			cachedDrawBuffersKnown = false;
			cachedDrawBufferCount = 0;
		}
		return true;
	}

	public static final List<String> dumpActiveExtensions() {
		List<String> exts = new ArrayList<>();
		if(hasANGLEInstancedArrays) exts.add("ANGLE_instanced_arrays");
		if(hasEXTColorBufferFloat) exts.add("EXT_color_buffer_float");
		if(hasEXTColorBufferHalfFloat) exts.add("EXT_color_buffer_half_float");
		if(hasEXTShaderTextureLOD) exts.add("EXT_shader_texture_lod");
		if(hasOESFBORenderMipmap) exts.add("OES_fbo_render_mipmap");
		if(hasOESVertexArrayObject) exts.add("OES_vertex_array_object");
		if(hasOESTextureFloat) exts.add("OES_texture_float");
		if(hasOESTextureFloatLinear) exts.add("OES_texture_float_linear");
		if(hasOESTextureHalfFloat) exts.add("OES_texture_half_float");
		if(hasOESTextureHalfFloatLinear) exts.add("OES_texture_half_float_linear");
		if(hasEXTTextureFilterAnisotropic) exts.add("EXT_texture_filter_anisotropic");
		if(hasWEBGLDebugRendererInfo) exts.add("WEBGL_debug_renderer_info");
		return exts;
	}

	public static final void _wglEnable(int glEnum) {
		if(shouldUpdateCapability(glEnum, true)) {
			ctx.enable(glEnum);
		}
	}
	
	public static final void _wglDisable(int glEnum) {
		if(shouldUpdateCapability(glEnum, false)) {
			ctx.disable(glEnum);
		}
	}
	
	public static final void _wglClearColor(float r, float g, float b, float a) {
		if(!cachedClearColorKnown || cachedClearColorR != r || cachedClearColorG != g || cachedClearColorB != b || cachedClearColorA != a) {
			cachedClearColorKnown = true;
			cachedClearColorR = r;
			cachedClearColorG = g;
			cachedClearColorB = b;
			cachedClearColorA = a;
			ctx.clearColor(r, g, b, a);
		}
	}
	
	public static final void _wglClearDepth(float f) {
		if(!cachedClearDepthKnown || cachedClearDepth != f) {
			cachedClearDepthKnown = true;
			cachedClearDepth = f;
			ctx.clearDepth(f);
		}
	}
	
	public static final void _wglClear(int bits) {
		ctx.clear(bits);
	}
	
	public static final void _wglDepthFunc(int glEnum) {
		if(!cachedDepthFuncKnown || cachedDepthFunc != glEnum) {
			cachedDepthFuncKnown = true;
			cachedDepthFunc = glEnum;
			ctx.depthFunc(glEnum);
		}
	}
	
	public static final void _wglDepthMask(boolean mask) {
		if(!cachedDepthMaskKnown || cachedDepthMask != mask) {
			cachedDepthMaskKnown = true;
			cachedDepthMask = mask;
			ctx.depthMask(mask);
		}
	}
	
	public static final void _wglCullFace(int glEnum) {
		if(!cachedCullFaceKnown || cachedCullFace != glEnum) {
			cachedCullFaceKnown = true;
			cachedCullFace = glEnum;
			ctx.cullFace(glEnum);
		}
	}
	
	public static final void _wglViewport(int x, int y, int w, int h) {
		if(!cachedViewportKnown || cachedViewportX != x || cachedViewportY != y || cachedViewportW != w || cachedViewportH != h) {
			cachedViewportKnown = true;
			cachedViewportX = x;
			cachedViewportY = y;
			cachedViewportW = w;
			cachedViewportH = h;
			ctx.viewport(x, y, w, h);
		}
	}

	public static final void _wglScissor(int x, int y, int w, int h) {
		if(!cachedScissorKnown || cachedScissorX != x || cachedScissorY != y || cachedScissorW != w || cachedScissorH != h) {
			cachedScissorKnown = true;
			cachedScissorX = x;
			cachedScissorY = y;
			cachedScissorW = w;
			cachedScissorH = h;
			ctx.scissor(x, y, w, h);
		}
	}
	
	public static final void _wglBlendFunc(int src, int dst) {
		if(!cachedBlendFuncKnown || cachedBlendSrcColor != src || cachedBlendDstColor != dst
				|| cachedBlendSrcAlpha != src || cachedBlendDstAlpha != dst) {
			cachedBlendFuncKnown = true;
			cachedBlendSrcColor = src;
			cachedBlendDstColor = dst;
			cachedBlendSrcAlpha = src;
			cachedBlendDstAlpha = dst;
			ctx.blendFunc(src, dst);
		}
	}
	
	public static final void _wglBlendFuncSeparate(int srcColor, int dstColor,
			int srcAlpha, int dstAlpha) {
		if(!cachedBlendFuncKnown || cachedBlendSrcColor != srcColor || cachedBlendDstColor != dstColor
				|| cachedBlendSrcAlpha != srcAlpha || cachedBlendDstAlpha != dstAlpha) {
			cachedBlendFuncKnown = true;
			cachedBlendSrcColor = srcColor;
			cachedBlendDstColor = dstColor;
			cachedBlendSrcAlpha = srcAlpha;
			cachedBlendDstAlpha = dstAlpha;
			ctx.blendFuncSeparate(srcColor, dstColor, srcAlpha, dstAlpha);
		}
	}
	
	public static final void _wglBlendEquation(int glEnum) {
		if(!cachedBlendEquationKnown || cachedBlendEquation != glEnum) {
			cachedBlendEquationKnown = true;
			cachedBlendEquation = glEnum;
			ctx.blendEquation(glEnum);
		}
	}
	
	public static final void _wglBlendColor(float r, float g, float b, float a) {
		if(!cachedBlendColorKnown || cachedBlendColorR != r || cachedBlendColorG != g || cachedBlendColorB != b || cachedBlendColorA != a) {
			cachedBlendColorKnown = true;
			cachedBlendColorR = r;
			cachedBlendColorG = g;
			cachedBlendColorB = b;
			cachedBlendColorA = a;
			ctx.blendColor(r, g, b, a);
		}
	}
	
	public static final void _wglColorMask(boolean r, boolean g, boolean b, boolean a) {
		if(!cachedColorMaskKnown || cachedColorMaskR != r || cachedColorMaskG != g || cachedColorMaskB != b || cachedColorMaskA != a) {
			cachedColorMaskKnown = true;
			cachedColorMaskR = r;
			cachedColorMaskG = g;
			cachedColorMaskB = b;
			cachedColorMaskA = a;
			ctx.colorMask(r, g, b, a);
		}
	}
	
	public static final void _wglDrawBuffers(int buffer) {
		if(glesVers == 200) {
			if(buffer != 0x8CE0) { // GL_COLOR_ATTACHMENT0
				throw new UnsupportedOperationException();
			}
		}else {
			if(shouldUpdateDrawBuffer(buffer)) {
				ctx.drawBuffers(new int[] { buffer });
			}
		}
	}
	
	public static final void _wglDrawBuffers(int[] buffers) {
		if(glesVers == 200) {
			if(buffers.length != 1 || buffers[0] != 0x8CE0) { // GL_COLOR_ATTACHMENT0
				throw new UnsupportedOperationException();
			}
		}else {
			if(shouldUpdateDrawBuffers(buffers)) {
				ctx.drawBuffers(buffers);
			}
		}
	}
	
	public static final void _wglReadBuffer(int buffer) {
		if(!cachedReadBufferKnown || cachedReadBuffer != buffer) {
			cachedReadBufferKnown = true;
			cachedReadBuffer = buffer;
			ctx.readBuffer(buffer);
		}
	}
	
	public static final void _wglReadPixels(int x, int y, int width, int height, int format, int type, ByteBuffer data) {
		ctx.readPixels(x, y, width, height, format, type, EaglerArrayBufferAllocator.getDataView8Unsigned(data));
	}
	
	public static final void _wglReadPixels_u16(int x, int y, int width, int height, int format, int type, ByteBuffer data) {
		ctx.readPixels(x, y, width, height, format, type, EaglerArrayBufferAllocator.getDataView16Unsigned(data));
	}
	
	public static final void _wglReadPixels(int x, int y, int width, int height, int format, int type, IntBuffer data) {
		ctx.readPixels(x, y, width, height, format, type, EaglerArrayBufferAllocator.getDataView32(data));
	}
	
	public static final void _wglReadPixels(int x, int y, int width, int height, int format, int type, FloatBuffer data) {
		ctx.readPixels(x, y, width, height, format, type, EaglerArrayBufferAllocator.getDataView32F(data));
	}
	
	public static final void _wglPolygonOffset(float f1, float f2) {
		if(!cachedPolygonOffsetKnown || cachedPolygonOffsetFactor != f1 || cachedPolygonOffsetUnits != f2) {
			cachedPolygonOffsetKnown = true;
			cachedPolygonOffsetFactor = f1;
			cachedPolygonOffsetUnits = f2;
			ctx.polygonOffset(f1, f2);
		}
	}
	
	public static final void _wglLineWidth(float width) {
		if(!cachedLineWidthKnown || cachedLineWidth != width) {
			cachedLineWidthKnown = true;
			cachedLineWidth = width;
			ctx.lineWidth(width);
		}
	}
	
	public static final IBufferGL _wglGenBuffers() {
		return new OpenGLObjects.BufferGL(ctx.createBuffer());
	}
	
	public static final ITextureGL _wglGenTextures() {
		return new OpenGLObjects.TextureGL(ctx.createTexture());
	}
	
	public static final IVertexArrayGL _wglGenVertexArrays() {
		switch(vertexArrayImpl) {
		case VAO_IMPL_CORE:
			return new OpenGLObjects.VertexArrayGL(ctx.createVertexArray());
		case VAO_IMPL_OES:
			return new OpenGLObjects.VertexArrayGL(OESVertexArrayObject.createVertexArrayOES());
		default:
			throw new UnsupportedOperationException();
		}
	}
	
	public static final IProgramGL _wglCreateProgram() {
		return new OpenGLObjects.ProgramGL(ctx.createProgram());
	}
	
	public static final IShaderGL _wglCreateShader(int type) {
		return new OpenGLObjects.ShaderGL(ctx.createShader(type));
	}
	
	public static final IFramebufferGL _wglCreateFramebuffer() {
		return new OpenGLObjects.FramebufferGL(ctx.createFramebuffer());
	}
	
	public static final IRenderbufferGL _wglCreateRenderbuffer() {
		return new OpenGLObjects.RenderbufferGL(ctx.createRenderbuffer());
	}
	
	public static final IQueryGL _wglGenQueries() {
		return new OpenGLObjects.QueryGL(ctx.createQuery());
	}
	
	public static final void _wglDeleteBuffers(IBufferGL obj) {
		if (obj != null) {
			invalidateBufferBinding(obj);
			ctx.deleteBuffer(((OpenGLObjects.BufferGL)obj).ptr);
		}
	}
	
	public static final void _wglDeleteTextures(ITextureGL obj) {
		if (obj != null) {
			invalidateTextureBinding(obj);
			ctx.deleteTexture(((OpenGLObjects.TextureGL)obj).ptr);
		}
	}
	
	public static final void _wglDeleteVertexArrays(IVertexArrayGL obj) {
		if (obj == null) {
			return;
		}
		if(cachedVertexArrayKnown && cachedVertexArray == obj) {
			cachedVertexArrayKnown = false;
			cachedVertexArray = null;
			invalidateElementArrayBindingForVertexArrayChange();
		}
		WebGLVertexArray ptr = ((OpenGLObjects.VertexArrayGL)obj).ptr;
		switch(vertexArrayImpl) {
		case VAO_IMPL_CORE:
			ctx.deleteVertexArray(ptr);
			break;
		case VAO_IMPL_OES:
			OESVertexArrayObject.deleteVertexArrayOES(ptr);
			break;
		default:
			throw new UnsupportedOperationException();
		}
	}
	
	public static final void _wglDeleteProgram(IProgramGL obj) {
		if (obj != null) {
			if(cachedProgramKnown && cachedProgram == obj) {
				cachedProgramKnown = false;
				cachedProgram = null;
			}
			ctx.deleteProgram(((OpenGLObjects.ProgramGL)obj).ptr);
		}
	}
	
	public static final void _wglDeleteShader(IShaderGL obj) {
		if (obj != null) {
			ctx.deleteShader(((OpenGLObjects.ShaderGL)obj).ptr);
		}
	}
	
	public static final void _wglDeleteFramebuffer(IFramebufferGL obj) {
		if (obj != null) {
			invalidateFramebufferBinding(obj);
			ctx.deleteFramebuffer(((OpenGLObjects.FramebufferGL)obj).ptr);
		}
	}
	
	public static final void _wglDeleteRenderbuffer(IRenderbufferGL obj) {
		if (obj != null) {
			if(cachedRenderbufferKnown && cachedRenderbuffer == obj) {
				cachedRenderbufferKnown = false;
				cachedRenderbuffer = null;
			}
			ctx.deleteRenderbuffer(((OpenGLObjects.RenderbufferGL)obj).ptr);
		}
	}
	
	public static final void _wglDeleteQueries(IQueryGL obj) {
		if (obj != null) {
			ctx.deleteQuery(((OpenGLObjects.QueryGL)obj).ptr);
		}
	}
	
	public static final void _wglBindBuffer(int target, IBufferGL obj) {
		int i = getBufferCacheIndex(target);
		if(i >= 0) {
			if(cachedBuffersKnown[i] && cachedBuffers[i] == obj) {
				return;
			}
			cachedBuffersKnown[i] = true;
			cachedBuffers[i] = obj;
		}
		ctx.bindBuffer(target, obj != null ? ((OpenGLObjects.BufferGL)obj).ptr : null);
	}
	
	public static final void _wglBufferData(int target, ByteBuffer data, int usage) {
		ctx.bufferData(target, EaglerArrayBufferAllocator.getDataView8(data), usage);
	}
	
	public static final void _wglBufferData(int target, IntBuffer data, int usage) {
		ctx.bufferData(target, EaglerArrayBufferAllocator.getDataView32(data), usage);
	}
	
	public static final void _wglBufferData(int target, FloatBuffer data, int usage) {
		ctx.bufferData(target, EaglerArrayBufferAllocator.getDataView32F(data), usage);
	}
	
	public static final void _wglBufferData(int target, int size, int usage) {
		ctx.bufferData(target, size, usage);
	}
	
	public static final void _wglBufferSubData(int target, int offset, ByteBuffer data) {
		ctx.bufferSubData(target, offset, EaglerArrayBufferAllocator.getDataView8(data));
	}
	
	public static final void _wglBufferSubData(int target, int offset, IntBuffer data) {
		ctx.bufferSubData(target, offset, EaglerArrayBufferAllocator.getDataView32(data));
	}
	
	public static final void _wglBufferSubData(int target, int offset, FloatBuffer data) {
		ctx.bufferSubData(target, offset, EaglerArrayBufferAllocator.getDataView32F(data));
	}
	
	public static final void _wglBindVertexArray(IVertexArrayGL obj) {
		if(cachedVertexArrayKnown && cachedVertexArray == obj) {
			return;
		}
		cachedVertexArrayKnown = true;
		cachedVertexArray = obj;
		invalidateElementArrayBindingForVertexArrayChange();
		WebGLVertexArray ptr = obj != null ? ((OpenGLObjects.VertexArrayGL)obj).ptr : null;
		switch(vertexArrayImpl) {
		case VAO_IMPL_CORE:
			ctx.bindVertexArray(ptr);
			break;
		case VAO_IMPL_OES:
			OESVertexArrayObject.bindVertexArrayOES(ptr);
			break;
		default:
			throw new UnsupportedOperationException();
		}
	}
	
	public static final void _wglEnableVertexAttribArray(int index) {
		ctx.enableVertexAttribArray(index);
	}
	
	public static final void _wglDisableVertexAttribArray(int index) {
		ctx.disableVertexAttribArray(index);
	}
	
	public static final void _wglVertexAttribPointer(int index, int size, int type,
			boolean normalized, int stride, int offset) {
		ctx.vertexAttribPointer(index, size, type, normalized, stride, offset);
	}

	public static final void _wglVertexAttribIPointer(int index, int size, int type, int stride, int offset) {
		ctx.vertexAttribIPointer(index, size, type, stride, offset);
	}

	public static final void _wglVertexAttribDivisor(int index, int divisor) {
		switch(instancingImpl) {
		case INSTANCE_IMPL_CORE:
			ctx.vertexAttribDivisor(index, divisor);
			break;
		case INSTANCE_IMPL_ANGLE:
			ANGLEInstancedArrays.vertexAttribDivisorANGLE(index, divisor);
			break;
		default:
			throw new UnsupportedOperationException();
		}
	}
	
	public static final void _wglActiveTexture(int texture) {
		int unit = texture - _GL_TEXTURE0;
		if(unit >= 0 && unit < MAX_TRACKED_TEXTURE_UNITS) {
			if(cachedActiveTextureKnown && cachedActiveTextureUnit == unit) {
				return;
			}
			cachedActiveTextureKnown = true;
			cachedActiveTextureUnit = unit;
		}else {
			cachedActiveTextureKnown = false;
			cachedActiveTextureUnit = unit;
		}
		ctx.activeTexture(texture);
	}
	
	public static final void _wglBindTexture(int target, ITextureGL obj) {
		int targetIndex = getTextureCacheIndex(target);
		if(targetIndex >= 0 && cachedActiveTextureUnit >= 0 && cachedActiveTextureUnit < MAX_TRACKED_TEXTURE_UNITS) {
			if(cachedTexturesKnown[cachedActiveTextureUnit][targetIndex] && cachedTextures[cachedActiveTextureUnit][targetIndex] == obj) {
				return;
			}
			cachedTexturesKnown[cachedActiveTextureUnit][targetIndex] = true;
			cachedTextures[cachedActiveTextureUnit][targetIndex] = obj;
		}
		ctx.bindTexture(target, obj == null ? null : ((OpenGLObjects.TextureGL)obj).ptr);
	}
	
	public static final void _wglTexParameterf(int target, int param, float value) {
		ctx.texParameterf(target, param, value);
	}
	
	public static final void _wglTexParameteri(int target, int param, int value) {
		ctx.texParameteri(target, param, value);
	}

	public static final void _wglTexImage3D(int target, int level, int internalFormat, int width, int height, int depth,
			int border, int format, int type, ByteBuffer data) {
		ctx.texImage3D(target, level, internalFormat, width, height, depth, border, format, type,
				data == null ? null : EaglerArrayBufferAllocator.getDataView8Unsigned(data));
	}
	
	public static final void _wglTexImage2D(int target, int level, int internalFormat, int width,
			int height, int border, int format, int type, ByteBuffer data) {
		ctx.texImage2D(target, level, internalFormat, width, height, border, format, type,
				data == null ? null : EaglerArrayBufferAllocator.getDataView8Unsigned(data));
	}
	
	public static final void _wglTexImage2Du16(int target, int level, int internalFormat, int width,
			int height, int border, int format, int type, ByteBuffer data) {
		ctx.texImage2D(target, level, internalFormat, width, height, border, format, type,
				data == null ? null : EaglerArrayBufferAllocator.getDataView16Unsigned(data));
	}
	
	public static final void _wglTexImage2Df32(int target, int level, int internalFormat, int width,
			int height, int border, int format, int type, ByteBuffer data) {
		ctx.texImage2D(target, level, internalFormat, width, height, border, format, type,
				data == null ? null : EaglerArrayBufferAllocator.getDataView32F(data));
	}
	
	public static final void _wglTexImage2D(int target, int level, int internalFormat, int width,
			int height, int border, int format, int type, IntBuffer data) {
		ctx.texImage2D(target, level, internalFormat, width, height, border, format, type,
				data == null ? null : EaglerArrayBufferAllocator.getDataView8Unsigned(data));
	}
	
	public static final void _wglTexImage2Df32(int target, int level, int internalFormat, int width,
			int height, int border, int format, int type, FloatBuffer data) {
		ctx.texImage2D(target, level, internalFormat, width, height, border, format, type,
				data == null ? null : EaglerArrayBufferAllocator.getDataView32F(data));
	}
	
	public static final void _wglTexSubImage2D(int target, int level, int xoffset, int yoffset,
			int width, int height, int format, int type, ByteBuffer data) {
		ctx.texSubImage2D(target, level, xoffset, yoffset, width, height, format, type,
				data == null ? null : EaglerArrayBufferAllocator.getDataView8Unsigned(data));
	}
	
	public static final void _wglTexSubImage2Du16(int target, int level, int xoffset, int yoffset,
			int width, int height, int format, int type, ByteBuffer data) {
		ctx.texSubImage2D(target, level, xoffset, yoffset, width, height, format, type,
				data == null ? null : EaglerArrayBufferAllocator.getDataView16Unsigned(data));
	}
	
	public static final void _wglTexSubImage2D(int target, int level, int xoffset, int yoffset,
			int width, int height, int format, int type, IntBuffer data) {
		ctx.texSubImage2D(target, level, xoffset, yoffset, width, height, format, type,
				data == null ? null : EaglerArrayBufferAllocator.getDataView8Unsigned(data));
	}
	
	public static final void _wglTexSubImage2Df32(int target, int level, int xoffset, int yoffset,
			int width, int height, int format, int type, FloatBuffer data) {
		ctx.texSubImage2D(target, level, xoffset, yoffset, width, height, format, type,
				data == null ? null : EaglerArrayBufferAllocator.getDataView32F(data));
	}
	
	public static final void _wglCopyTexSubImage2D(int target, int level, int xoffset, int yoffset,
			int x, int y, int width, int height) {
		ctx.copyTexSubImage2D(target, level, xoffset, yoffset, x, y, width, height);
	}

	public static final void _wglTexStorage2D(int target, int levels, int internalFormat, int w, int h) {
		ctx.texStorage2D(target, levels, internalFormat, w, h);
	}

	public static final void _wglPixelStorei(int pname, int value) {
		ctx.pixelStorei(pname, value);
	}
	
	public static final void _wglGenerateMipmap(int target) {
		ctx.generateMipmap(target);
	}
	
	public static final void _wglShaderSource(IShaderGL obj, String source) {
		ctx.shaderSource(((OpenGLObjects.ShaderGL)obj).ptr, source);
	}
	
	public static final void _wglCompileShader(IShaderGL obj) {
		ctx.compileShader(((OpenGLObjects.ShaderGL)obj).ptr);
	}
	
	public static final int _wglGetShaderi(IShaderGL obj, int param) {
		return ctx.getShaderParameteri(((OpenGLObjects.ShaderGL)obj).ptr, param);
	}
	
	public static final String _wglGetShaderInfoLog(IShaderGL obj) {
		return ctx.getShaderInfoLog(((OpenGLObjects.ShaderGL)obj).ptr);
	}
	
	public static final void _wglUseProgram(IProgramGL obj) {
		if(cachedProgramKnown && cachedProgram == obj) {
			return;
		}
		cachedProgramKnown = true;
		cachedProgram = obj;
		ctx.useProgram(obj == null ? null : ((OpenGLObjects.ProgramGL)obj).ptr);
	}
	
	public static final void _wglAttachShader(IProgramGL obj, IShaderGL shader) {
		ctx.attachShader(((OpenGLObjects.ProgramGL)obj).ptr, ((OpenGLObjects.ShaderGL)shader).ptr);
	}
	
	public static final void _wglDetachShader(IProgramGL obj, IShaderGL shader) {
		ctx.detachShader(((OpenGLObjects.ProgramGL)obj).ptr, ((OpenGLObjects.ShaderGL)shader).ptr);
	}
	
	public static final void _wglLinkProgram(IProgramGL obj) {
		ctx.linkProgram(((OpenGLObjects.ProgramGL)obj).ptr);
	}
	
	public static final int _wglGetProgrami(IProgramGL obj, int param) {
		return ctx.getProgramParameteri(((OpenGLObjects.ProgramGL)obj).ptr, param);
	}
	
	public static final String _wglGetProgramInfoLog(IProgramGL obj) {
		return ctx.getProgramInfoLog(((OpenGLObjects.ProgramGL)obj).ptr);
	}
	
	public static final void _wglBindAttribLocation(IProgramGL obj, int index, String name) {
		ctx.bindAttribLocation(((OpenGLObjects.ProgramGL)obj).ptr, index, name);
	}
	
	public static final int _wglGetAttribLocation(IProgramGL obj, String name) {
		return ctx.getAttribLocation(((OpenGLObjects.ProgramGL)obj).ptr, name);
	}
	
	public static final void _wglDrawArrays(int mode, int first, int count) {
		ctx.drawArrays(mode, first, count);
		//checkErr("_wglDrawArrays(" + mode + ", " + first + ", " + count + ");");
	}

	public static final void _wglDrawArraysInstanced(int mode, int first, int count, int instances) {
		switch(instancingImpl) {
		case INSTANCE_IMPL_CORE:
			ctx.drawArraysInstanced(mode, first, count, instances);
			break;
		case INSTANCE_IMPL_ANGLE:
			ANGLEInstancedArrays.drawArraysInstancedANGLE(mode, first, count, instances);
			break;
		default:
			throw new UnsupportedOperationException();
		}
		//checkErr("_wglDrawArraysInstanced(" + mode + ", " + first + ", " + count + ", " + instanced + ");");
	}
	
	public static final void _wglDrawElements(int mode, int count, int type, int offset) {
		ctx.drawElements(mode, count, type, offset);
		//checkErr("_wglDrawElements(" + mode + ", " + count + ", " + type + ", " + offset + ");");
	}
	
	public static void _wglDrawRangeElements(int mode, int start, int end, int count, int type, int offset) {
		ctx.drawRangeElements(mode, start, end, count, type, offset);
		//checkErr("_wglDrawRangeElements(" + mode + ", " + start + ", " + end + ", " + count + ", " + type + ", " + offset + ");");
	}
	
	public static final void _wglDrawElementsInstanced(int mode, int count, int type, int offset, int instances) {
		switch(instancingImpl) {
		case INSTANCE_IMPL_CORE:
			ctx.drawElementsInstanced(mode, count, type, offset, instances);
			break;
		case INSTANCE_IMPL_ANGLE:
			ANGLEInstancedArrays.drawElementsInstancedANGLE(mode, count, type, offset, instances);
			break;
		default:
			throw new UnsupportedOperationException();
		}
		//checkErr("_wglDrawElementsInstanced(" + mode + ", " + count + ", " + type + ", " + offset + ", " + instanced + ");");
	}
	
	public static final IUniformGL _wglGetUniformLocation(IProgramGL obj, String name) {
		WebGLUniformLocation loc = ctx.getUniformLocation(((OpenGLObjects.ProgramGL)obj).ptr, name);
		if(loc != null) {
			return new OpenGLObjects.UniformGL(loc);
		}else {
			return null;
		}
	}
	
	public static final int _wglGetUniformBlockIndex(IProgramGL obj, String name) {
		int i = ctx.getUniformBlockIndex(((OpenGLObjects.ProgramGL)obj).ptr, name);
		if(i > 2147483647) {
			i = -1;
		}
		return i;
	}
	
	public static final void _wglBindBufferRange(int target, int index, IBufferGL buffer, int offset, int size) {
		ctx.bindBufferRange(target, index, ((OpenGLObjects.BufferGL)buffer).ptr, offset, size);
	}

	public static final void _wglUniformBlockBinding(IProgramGL obj, int blockIndex, int bufferIndex) {
		ctx.uniformBlockBinding(((OpenGLObjects.ProgramGL)obj).ptr, blockIndex, bufferIndex);
	}
	
	public static final void _wglUniform1f(IUniformGL obj, float x) {
		if(obj != null) ctx.uniform1f(((OpenGLObjects.UniformGL)obj).ptr, x);
	}
	
	public static final void _wglUniform2f(IUniformGL obj, float x, float y) {
		if(obj != null) ctx.uniform2f(((OpenGLObjects.UniformGL)obj).ptr, x, y);
	}
	
	public static final void _wglUniform3f(IUniformGL obj, float x, float y, float z) {
		if(obj != null) ctx.uniform3f(((OpenGLObjects.UniformGL)obj).ptr, x, y, z);
	}
	
	public static final void _wglUniform4f(IUniformGL obj, float x, float y, float z, float w) {
		if(obj != null) ctx.uniform4f(((OpenGLObjects.UniformGL)obj).ptr, x, y, z, w);
	}
	
	public static final void _wglUniform1i(IUniformGL obj, int x) {
		if(obj != null) ctx.uniform1i(((OpenGLObjects.UniformGL)obj).ptr, x);
	}
	
	public static final void _wglUniform2i(IUniformGL obj, int x, int y) {
		if(obj != null) ctx.uniform2i(((OpenGLObjects.UniformGL)obj).ptr, x, y);
	}
	
	public static final void _wglUniform3i(IUniformGL obj, int x, int y, int z) {
		if(obj != null) ctx.uniform3i(((OpenGLObjects.UniformGL)obj).ptr, x, y, z);
	}
	
	public static final void _wglUniform4i(IUniformGL obj, int x, int y, int z, int w) {
		if(obj != null) ctx.uniform4i(((OpenGLObjects.UniformGL)obj).ptr, x, y, z, w);
	}
	
	public static final void _wglUniformMatrix2fv(IUniformGL obj, boolean transpose, FloatBuffer mat) {
		if(obj != null) ctx.uniformMatrix2fv(((OpenGLObjects.UniformGL)obj).ptr, transpose,
				mat == null ? null : EaglerArrayBufferAllocator.getDataView32F(mat));
	}
	
	public static final void _wglUniformMatrix3fv(IUniformGL obj, boolean transpose, FloatBuffer mat) {
		if(obj != null) ctx.uniformMatrix3fv(((OpenGLObjects.UniformGL)obj).ptr, transpose,
				mat == null ? null : EaglerArrayBufferAllocator.getDataView32F(mat));
	}
	
	public static final void _wglUniformMatrix3x2fv(IUniformGL obj, boolean transpose, FloatBuffer mat) {
		if(obj != null) ctx.uniformMatrix3x2fv(((OpenGLObjects.UniformGL)obj).ptr, transpose,
				mat == null ? null : EaglerArrayBufferAllocator.getDataView32F(mat));
	}
	
	public static final void _wglUniformMatrix4fv(IUniformGL obj, boolean transpose, FloatBuffer mat) {
		if(obj != null) ctx.uniformMatrix4fv(((OpenGLObjects.UniformGL)obj).ptr, transpose,
				mat == null ? null : EaglerArrayBufferAllocator.getDataView32F(mat));
	}
	
	public static final void _wglUniformMatrix4x2fv(IUniformGL obj, boolean transpose, FloatBuffer mat) {
		if(obj != null) ctx.uniformMatrix4x2fv(((OpenGLObjects.UniformGL)obj).ptr, transpose,
				mat == null ? null : EaglerArrayBufferAllocator.getDataView32F(mat));
	}
	
	public static final void _wglUniformMatrix4x3fv(IUniformGL obj, boolean transpose, FloatBuffer mat) {
		if(obj != null) ctx.uniformMatrix4x3fv(((OpenGLObjects.UniformGL)obj).ptr, transpose,
				mat == null ? null : EaglerArrayBufferAllocator.getDataView32F(mat));
	}
	
	public static final void _wglBindFramebuffer(int target, IFramebufferGL framebuffer) {
		boolean bind = true;
		switch(target) {
		case _GL_FRAMEBUFFER:
			bind = !cachedReadFramebufferKnown || !cachedDrawFramebufferKnown
					|| cachedReadFramebuffer != framebuffer || cachedDrawFramebuffer != framebuffer;
			if(bind) {
				cachedReadFramebufferKnown = true;
				cachedDrawFramebufferKnown = true;
				cachedReadFramebuffer = framebuffer;
				cachedDrawFramebuffer = framebuffer;
				invalidateFramebufferDependentState();
			}
			break;
		case _GL_READ_FRAMEBUFFER:
			bind = !cachedReadFramebufferKnown || cachedReadFramebuffer != framebuffer;
			if(bind) {
				cachedReadFramebufferKnown = true;
				cachedReadFramebuffer = framebuffer;
				cachedReadBufferKnown = false;
			}
			break;
		case _GL_DRAW_FRAMEBUFFER:
			bind = !cachedDrawFramebufferKnown || cachedDrawFramebuffer != framebuffer;
			if(bind) {
				cachedDrawFramebufferKnown = true;
				cachedDrawFramebuffer = framebuffer;
				cachedDrawBuffersKnown = false;
				cachedDrawBufferCount = 0;
			}
			break;
		default:
			invalidateFramebufferDependentState();
			break;
		}
		if(framebuffer == null) {
			if(bind) {
				ctx.bindFramebuffer(target, PlatformRuntime.mainFramebuffer);
			}
			if(glesVers != 200) {
				_wglDrawBuffers(WebGL2RenderingContext.COLOR_ATTACHMENT0);
			}
		}else {
			if(bind) {
				ctx.bindFramebuffer(target, ((OpenGLObjects.FramebufferGL) framebuffer).ptr);
			}
		}
	}
	
	public static final int _wglCheckFramebufferStatus(int target) {
		return ctx.checkFramebufferStatus(target);
	}
	
	public static final void _wglFramebufferTexture2D(int target, int attachment, int texTarget,
			ITextureGL texture, int level) {
		ctx.framebufferTexture2D(target, attachment, texTarget, ((OpenGLObjects.TextureGL)texture).ptr, level);
	}
	
	public static final void _wglFramebufferTextureLayer(int target, int attachment, ITextureGL texture, int level, int layer) {
		ctx.framebufferTextureLayer(target, attachment, ((OpenGLObjects.TextureGL) texture).ptr, level, layer);
	}
	
	public static final void _wglBlitFramebuffer(int srcX0, int srcY0, int srcX1, int srcY1,
			int dstX0, int dstY0, int dstX1, int dstY1, int bits, int filter) {
		ctx.blitFramebuffer(srcX0, srcY0, srcX1, srcY1, dstX0, dstY0, dstX1, dstY1, bits, filter);
	}
	
	public static final void _wglBindRenderbuffer(int target, IRenderbufferGL renderbuffer) {
		if(target == _GL_RENDERBUFFER) {
			if(cachedRenderbufferKnown && cachedRenderbuffer == renderbuffer) {
				return;
			}
			cachedRenderbufferKnown = true;
			cachedRenderbuffer = renderbuffer;
		}
		ctx.bindRenderbuffer(target, renderbuffer == null ? null : ((OpenGLObjects.RenderbufferGL)renderbuffer).ptr);
	}
	
	public static final void _wglRenderbufferStorage(int target, int internalformat,
			int width, int height) {
		ctx.renderbufferStorage(target, internalformat, width, height);
	}
	
	public static final void _wglFramebufferRenderbuffer(int target, int attachment,
			int renderbufferTarget, IRenderbufferGL renderbuffer) {
		ctx.framebufferRenderbuffer(target, attachment, renderbufferTarget,
				((OpenGLObjects.RenderbufferGL)renderbuffer).ptr);
	}
	
	public static final String _wglGetString(int param) {
		if(hasWEBGLDebugRendererInfo) {
			String s;
			switch(param) {
			case 0x1f00: // VENDOR
				s = ctx.getParameterString(0x9245); // UNMASKED_VENDOR_WEBGL
				if(s == null) {
					s = ctx.getParameterString(0x1f00); // VENDOR
				}
				return s;
			case 0x1f01: // RENDERER
				s = ctx.getParameterString(0x9246); // UNMASKED_RENDERER_WEBGL
				if(s == null) {
					s = ctx.getParameterString(0x1f01); // RENDERER
				}
				return s;
			default:
				return ctx.getParameterString(param);
			}
		}else {
			return ctx.getParameterString(param);
		}
	}
	
	public static final int _wglGetInteger(int param) {
		return ctx.getParameteri(param);
	}
	
	public static final int _wglGetError() {
		return ctx.getError();
	}
	
	public static final int checkOpenGLESVersion() {
		return glesVers;
	}
	
	public static final boolean checkEXTGPUShader5Capable() {
		return false;
	}
	
	public static final boolean checkOESGPUShader5Capable() {
		return false;
	}
	
	public static final boolean checkFBORenderMipmapCapable() {
		return glesVers >= 300 || hasOESFBORenderMipmap;
	}
	
	public static final boolean checkVAOCapable() {
		return vertexArrayImpl != VAO_IMPL_NONE;
	}
	
	public static final boolean checkInstancingCapable() {
		return instancingImpl != INSTANCE_IMPL_NONE;
	}
	
	public static final boolean checkTexStorageCapable() {
		return glesVers >= 300;
	}
	
	public static final boolean checkTextureLODCapable() {
		return glesVers >= 300 || hasEXTShaderTextureLOD;
	}
	
	public static final boolean checkHDRFramebufferSupport(int bits) {
		switch(bits) {
		case 16:
			return hasFBO16FSupport;
		case 32:
			return hasFBO32FSupport;
		default:
			return false;
		}
	}
	
	public static final boolean checkLinearHDRFilteringSupport(int bits) {
		switch(bits) {
		case 16:
			return hasLinearHDR16FSupport;
		case 32:
			return hasLinearHDR32FSupport;
		default:
			return false;
		}
	}
	
	// legacy
	public static final boolean checkLinearHDR32FSupport() {
		return hasLinearHDR32FSupport;
	}
	
	public static final boolean checkAnisotropicFilteringSupport() {
		return hasEXTTextureFilterAnisotropic;
	}
	
	public static final boolean checkNPOTCapable() {
		return glesVers >= 300;
	}
	
	private static final void checkErr(String name) {
		int i = ctx.getError();
		if(i != 0) {
			logger.error("########## GL ERROR ##########");
			logger.error("@ {}", name);
			do {
				logger.error("#{} - {}", i, EaglercraftGPU.gluErrorString(i));
			}while((i = ctx.getError()) != 0);
			try {
				throw new RuntimeException("GL Error Detected!");
			}catch(Throwable t) {
				logger.log(Level.ERROR, t);
			}
			logger.error("##############################");
		}
	}

	public static final String[] getAllExtensions() {
		return ctx.getSupportedExtensionArray();
	}

	public static final void enterVAOEmulationHook() {
		WebGLBackBuffer.enterVAOEmulationPhase();
	}

}
