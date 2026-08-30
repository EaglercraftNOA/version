/*
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

package net.lax1dude.eaglercraft.v1_8.opengl;

import net.lax1dude.eaglercraft.v1_8.internal.ITextureGL;
import net.lax1dude.eaglercraft.v1_8.internal.buffer.FloatBuffer;
import net.lax1dude.eaglercraft.v1_8.vector.Matrix4f;
import net.lax1dude.eaglercraft.v1_8.vector.Vector3f;
import net.lax1dude.eaglercraft.v1_8.vector.Vector4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.util.math.MathHelper;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.List;
import java.util.stream.IntStream;
import javax.annotation.Nullable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import static net.lax1dude.eaglercraft.v1_8.internal.PlatformOpenGL.*;

public class GlStateManager extends RealOpenGLEnums {

	private static final GlStateManager.BlendState BLEND = new GlStateManager.BlendState();
	private static final GlStateManager.TextureState[] TEXTURES = IntStream.range(0, 12).mapToObj((p_157120_) -> {
		return new GlStateManager.TextureState();
	}).toArray((p_157122_) -> {
		return new GlStateManager.TextureState[p_157122_];
	});

	private static final GlStateManager.DepthState DEPTH = new GlStateManager.DepthState();
	private static final GlStateManager.CullState CULL = new GlStateManager.CullState();
	private static final GlStateManager.PolygonOffsetState POLY_OFFSET = new GlStateManager.PolygonOffsetState();
	private static final GlStateManager.ColorLogicState COLOR_LOGIC = new GlStateManager.ColorLogicState();
	private static final GlStateManager.StencilState STENCIL = new GlStateManager.StencilState();
	private static final GlStateManager.ScissorState SCISSOR = new GlStateManager.ScissorState();

	private static final GlStateManager.ColorMask COLOR_MASK = new GlStateManager.ColorMask();

	private static int currentProgram = -1;
	private static int currentArrayBuffer = -1;
	private static int currentElementBuffer = -1;
	private static int currentVertexArray = -1;
	private static int currentDrawFramebuffer = -1;
	private static int currentReadFramebuffer = -1;

	static final Logger logger = LogManager.getLogger("GlStateManager");

	static boolean stateDepthTest = false;
	static boolean stateDepthTestStash = false;
	static int stateDepthFunc = -1;
	static boolean stateDepthMask = true;

	static boolean stateCull = false;
	static boolean stateCullStash = false;
	static int stateCullFace = GL_BACK;

	static boolean statePolygonOffset = false;
	static float statePolygonOffsetFactor = 0.0f;
	static float statePolygonOffsetUnits = 0.0f;

	static float stateColorR = 1.0f;
	static float stateColorG = 1.0f;
	static float stateColorB = 1.0f;
	static float stateColorA = 1.0f;
	static int stateColorSerial = 0;

	static float stateShaderBlendSrcColorR = 1.0f;
	static float stateShaderBlendSrcColorG = 1.0f;
	static float stateShaderBlendSrcColorB = 1.0f;
	static float stateShaderBlendSrcColorA = 1.0f;
	static float stateShaderBlendAddColorR = 0.0f;
	static float stateShaderBlendAddColorG = 0.0f;
	static float stateShaderBlendAddColorB = 0.0f;
	static float stateShaderBlendAddColorA = 0.0f;
	static int stateShaderBlendColorSerial = 0;
	static boolean stateEnableShaderBlendColor = false;

	static boolean stateBlend = false;
	static boolean stateBlendStash = false;
	static boolean stateGlobalBlend = true;
	static int stateBlendEquation = -1;
	static int stateBlendSRC = -1;
	static int stateBlendDST = -1;
	static boolean stateEnableOverlayFramebufferBlending = false;

	static boolean stateAlphaTest = false;
	static float stateAlphaTestRef = 0.1f;

	static boolean stateMaterial = false;
	static boolean stateLighting = false;
	static int stateLightsStackPointer = 0;
	static final boolean[][] stateLightsEnabled = new boolean[2][8];
	static final Vector4f[][] stateLightsStack = new Vector4f[2][8];
	static final int[] stateLightingSerial = new int[2];

	static float stateLightingAmbientR = 0.0f;
	static float stateLightingAmbientG = 0.0f;
	static float stateLightingAmbientB = 0.0f;
	static int stateLightingAmbientSerial = 0;

	static float stateNormalX = 0.0f;
	static float stateNormalY = 0.0f;
	static float stateNormalZ = -1.0f;
	static int stateNormalSerial = 0;

	static boolean stateFog = false;
	static boolean stateFogEXP = false;
	static float stateFogDensity = 1.0f;
	static float stateFogStart = 0.0f;
	static float stateFogEnd = 1.0f;
	static float stateFogColorR = 1.0f;
	static float stateFogColorG = 1.0f;
	static float stateFogColorB = 1.0f;
	static float stateFogColorA = 1.0f;
	static int stateFogSerial = 0;

	static int activeTexture = 0;
	static final boolean[] stateTexture = new boolean[16];
	static final int[] boundTexture = new int[] { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 };

	static int stateAnisotropicFixSerial = 0;
	static float stateAnisotropicFixW = 1024.0f;
	static float stateAnisotropicFixH = 1024.0f;
	static boolean enableAnisotropicFix = false;
	static boolean enableAnisotropicPatch = false;
	static boolean hintAnisotropicPatch = false;

	static boolean stateTexGen = false;

	static int viewportX = -1;
	static int viewportY = -1;
	static int viewportW = -1;
	static int viewportH = -1;

	static int colorMaskBits = 15;

	static float clearColorR = -999.0f;
	static float clearColorG = -999.0f;
	static float clearColorB = -999.0f;
	static float clearColorA = -999.0f;

	static float clearDepth = -999.0f;

	public static enum TexGen {
		S, T, R, Q;

		int source = GL_OBJECT_LINEAR;
		int plane = GL_OBJECT_PLANE;
		Vector4f vector = new Vector4f();

	}

	static float blendConstantR = -999.0f;
	static float blendConstantG = -999.0f;
	static float blendConstantB = -999.0f;
	static float blendConstantA = -999.0f;

	static int stateTexGenSerial = 0;

	static int stateMatrixMode = GL_MODELVIEW;

	static final Matrix4f[] modelMatrixStack = new Matrix4f[48];
	static final int[] modelMatrixStackAccessSerial = new int[48];
	private static int modelMatrixAccessSerial = 0;
	static int modelMatrixStackPointer = 0;

	static final Matrix4f[] projectionMatrixStack = new Matrix4f[8];
	static final int[] projectionMatrixStackAccessSerial = new int[8];
	private static int projectionMatrixAccessSerial = 0;
	static int projectionMatrixStackPointer = 0;

	static final float[] textureCoordsX = new float[8];
	static final float[] textureCoordsY = new float[8];
	static final int[] textureCoordsAccessSerial = new int[8];

	static final Matrix4f[][] textureMatrixStack = new Matrix4f[8][8];
	static final int[][] textureMatrixStackAccessSerial = new int[8][8];
	static final int[] textureMatrixAccessSerial = new int[8];
	static final int[] textureMatrixStackPointer = new int[8];

	static boolean stateUseExtensionPipeline = false;

	private static final Matrix4f tmpInvertedMatrix = new Matrix4f();
	
	public static final void anisotropicPatch(boolean e) {
		enableAnisotropicPatch = e;
	}
	
	public static final void hintAnisotropicFix(boolean hint) {
		hintAnisotropicPatch = hint;
	}

	static {
		populateStack(modelMatrixStack);
		populateStack(projectionMatrixStack);
		populateStack(textureMatrixStack);
		populateStack(stateLightsStack);
	}

	static void populateStack(Matrix4f[] stack) {
		for (int i = 0; i < stack.length; ++i) {
			stack[i] = new Matrix4f();
		}
	}

	static void populateStack(Matrix4f[][] stack) {
		for (int i = 0; i < stack.length; ++i) {
			populateStack(stack[i]);
		}
	}

	static void populateStack(Vector4f[][] stack) {
		for (int i = 0; i < stack.length; ++i) {
			for (int j = 0; j < stack[i].length; ++j) {
				stack[i][j] = new Vector4f(0.0f, -1.0f, 0.0f, 0.0f);
			}
		}
	}

	public static void _disableScissorTest() {
		SCISSOR.mode.disable();
	}

	public static void _enableScissorTest() {
		SCISSOR.mode.enable();
	}

	public static void _scissorBox(int p_84169_, int p_84170_, int p_84171_, int p_84172_) {
		GL20.glScissor(p_84169_, p_84170_, p_84171_, p_84172_);
	}

	public static void pushLightCoords() {
		int push = stateLightsStackPointer + 1;
		if (push < stateLightsStack.length) {
			Vector4f[] copyFrom = stateLightsStack[stateLightsStackPointer];
			boolean[] copyFrom2 = stateLightsEnabled[stateLightsStackPointer];
			Vector4f[] copyTo = stateLightsStack[push];
			boolean[] copyTo2 = stateLightsEnabled[push];
			for (int i = 0; i < copyFrom.length; ++i) {
				if (copyFrom2[i]) {
					copyTo[i].set(copyFrom[i]);
					copyTo2[i] = true;
				} else {
					copyTo2[i] = false;
				}
			}
			stateLightingSerial[push] = stateLightingSerial[stateLightsStackPointer];
			stateLightsStackPointer = push;
		} else {
			Throwable t = new IndexOutOfBoundsException("GL_LIGHT direction stack overflow!" + " Exceeded "
					+ stateLightsStack.length + " calls to GlStateManager.pushLightCoords");
			logger.error(t);
		}
	}

	public static void popLightCoords() {
		if (stateLightsStackPointer > 0) {
			--stateLightsStackPointer;
		} else {
			Throwable t = new IndexOutOfBoundsException("GL_LIGHT direction stack underflow!"
					+ " Called GlStateManager.popLightCoords on an empty light stack");
			logger.error(t);
		}
	}

	public static void disableAlpha() {
		stateAlphaTest = false;
	}

	public static void enableAlpha() {
		stateAlphaTest = true;
	}

	public static void alphaFunc(int func, float ref) {
		if (func != GL_GREATER) {
			throw new UnsupportedOperationException("Only GL_GREATER alphaFunc is supported");
		} else {
			stateAlphaTestRef = ref;
		}
	}

	public static void enableLighting() {
		stateLighting = true;
	}

	public static void disableLighting() {
		stateLighting = false;
	}

	public static void enableExtensionPipeline() {
		stateUseExtensionPipeline = true;
	}

	public static void disableExtensionPipeline() {
		stateUseExtensionPipeline = false;
	}

	public static boolean isExtensionPipeline() {
		return stateUseExtensionPipeline;
	}

	private static final Vector4f paramVector4 = new Vector4f();

	public static void enableMCLight(int light, float diffuse, double dirX, double dirY, double dirZ, double dirW) {
		if (dirW != 0.0)
			throw new IllegalArgumentException("dirW must be 0.0!");
		paramVector4.x = (float) dirX;
		paramVector4.y = (float) dirY;
		paramVector4.z = (float) dirZ;
		paramVector4.w = (float) 0.0f;
		Matrix4f.transform(modelMatrixStack[modelMatrixStackPointer], paramVector4, paramVector4);
		Vector4f dest = stateLightsStack[stateLightsStackPointer][light];
		float len = MathHelper.sqrt(
				paramVector4.x * paramVector4.x + paramVector4.y * paramVector4.y + paramVector4.z * paramVector4.z);
		dest.x = paramVector4.x / len;
		dest.y = paramVector4.y / len;
		dest.z = paramVector4.z / len;
		dest.w = diffuse;
		stateLightsEnabled[stateLightsStackPointer][light] = true;
		++stateLightingSerial[stateLightsStackPointer];
	}

	public static int glCheckFramebufferStatus(int p_84509_) {
		return GL30.glCheckFramebufferStatus(p_84509_);
	}

	public static void _glFramebufferTexture2D(int p_84174_, int p_84175_, int p_84176_, int p_84177_, int p_84178_) {
		GL30.glFramebufferTexture2D(p_84174_, p_84175_, p_84176_, p_84177_, p_84178_);
	}

	public static void glActiveTexture(int p_84515_) {
		GL13.glActiveTexture(p_84515_);
	}
	
	public static void enableMCLight(int light) {
		stateLightsEnabled[stateLightsStackPointer][light] = true;
		++stateLightingSerial[stateLightsStackPointer];
	}

	public static void disableMCLight(int light) {
		stateLightsEnabled[stateLightsStackPointer][light] = false;
		++stateLightingSerial[stateLightsStackPointer];
	}

	public static void setMCLightAmbient(float r, float g, float b) {
		stateLightingAmbientR = r;
		stateLightingAmbientG = g;
		stateLightingAmbientB = b;
		++stateLightingAmbientSerial;
	}

	public static void _glUseProgram(int p_84479_) {
		GL20.glUseProgram(p_84479_);
	}

	public static int glCreateProgram() {
		return GL20.glCreateProgram();
	}

	public static void glDeleteProgram(int p_84485_) {
		if (currentProgram == p_84485_) {
			currentProgram = -1;
		}
		GL20.glDeleteProgram(p_84485_);
	}

	public static void glLinkProgram(int p_84491_) {
		GL20.glLinkProgram(p_84491_);
	}

	public static int _glGetUniformLocation(int p_84346_, CharSequence p_84347_) {
		return GL20.glGetUniformLocation(p_84346_, p_84347_);
	}

	public static void _glUniform1(int p_84264_, IntBuffer p_84265_) {
		GL20.glUniform1iv(p_84264_, p_84265_);
	}

	public static void _glUniform1i(int p_84468_, int p_84469_) {
		GL20.glUniform1i(p_84468_, p_84469_);
	}

	public static void _glUniform1(int p_84349_, java.nio.FloatBuffer p_84350_) {
		GL20.glUniform1fv(p_84349_, p_84350_);
	}

	public static void _glUniform2(int p_84352_, IntBuffer p_84353_) {
		GL20.glUniform2iv(p_84352_, p_84353_);
	}

	public static void _glUniform2(int p_84402_, java.nio.FloatBuffer p_84403_) {
		GL20.glUniform2fv(p_84402_, p_84403_);
	}

	public static void _glUniform3(int p_84405_, IntBuffer p_84406_) {
		GL20.glUniform3iv(p_84405_, p_84406_);
	}

	public static void _glUniform3(int p_84436_, java.nio.FloatBuffer p_84437_) {
		GL20.glUniform3fv(p_84436_, p_84437_);
	}

	public static void _glUniform4(int p_84439_, IntBuffer p_84440_) {
		GL20.glUniform4iv(p_84439_, p_84440_);
	}

	public static void _glUniform4(int p_84462_, java.nio.FloatBuffer p_84463_) {
		GL20.glUniform4fv(p_84462_, p_84463_);
	}

	public static void _glUniformMatrix2(int p_84270_, boolean p_84271_, java.nio.FloatBuffer p_84272_) {
		GL20.glUniformMatrix2fv(p_84270_, p_84271_, p_84272_);
	}

	public static void _glUniformMatrix3(int p_84355_, boolean p_84356_, java.nio.FloatBuffer p_84357_) {
		GL20.glUniformMatrix3fv(p_84355_, p_84356_, p_84357_);
	}

	public static void _glUniformMatrix4(int p_84408_, boolean p_84409_, java.nio.FloatBuffer p_84410_) {
		GL20.glUniformMatrix4fv(p_84408_, p_84409_, p_84410_);
	}

	public static void _glUniform1Direct(int p_84264_, net.lax1dude.eaglercraft.v1_8.internal.buffer.IntBuffer p_84265_) {
		GL20.glUniform1ivDirect(p_84264_, p_84265_);
	}

	public static void _glUniform1Direct(int p_84349_, FloatBuffer p_84350_) {
		GL20.glUniform1fvDirect(p_84349_, p_84350_);
	}

	public static void _glUniform2Direct(int p_84352_, net.lax1dude.eaglercraft.v1_8.internal.buffer.IntBuffer p_84353_) {
		GL20.glUniform2ivDirect(p_84352_, p_84353_);
	}

	public static void _glUniform2Direct(int p_84402_, FloatBuffer p_84403_) {
		GL20.glUniform2fvDirect(p_84402_, p_84403_);
	}

	public static void _glUniform3Direct(int p_84405_, net.lax1dude.eaglercraft.v1_8.internal.buffer.IntBuffer p_84406_) {
		GL20.glUniform3ivDirect(p_84405_, p_84406_);
	}

	public static void _glUniform3Direct(int p_84436_, FloatBuffer p_84437_) {
		GL20.glUniform3fvDirect(p_84436_, p_84437_);
	}

	public static void _glUniform4Direct(int p_84439_, net.lax1dude.eaglercraft.v1_8.internal.buffer.IntBuffer p_84440_) {
		GL20.glUniform4ivDirect(p_84439_, p_84440_);
	}

	public static void _glUniform4Direct(int p_84462_, FloatBuffer p_84463_) {
		GL20.glUniform4fvDirect(p_84462_, p_84463_);
	}

	public static void _glUniformMatrix2Direct(int p_84270_, boolean p_84271_, FloatBuffer p_84272_) {
		GL20.glUniformMatrix2fvDirect(p_84270_, p_84271_, p_84272_);
	}

	public static void _glUniformMatrix3Direct(int p_84355_, boolean p_84356_, FloatBuffer p_84357_) {
		GL20.glUniformMatrix3fvDirect(p_84355_, p_84356_, p_84357_);
	}

	public static void _glUniformMatrix4Direct(int p_84408_, boolean p_84409_, FloatBuffer p_84410_) {
		GL20.glUniformMatrix4fvDirect(p_84408_, p_84409_, p_84410_);
	}

	public static void enableColorMaterial() {
		stateMaterial = true;
	}

	public static void disableColorMaterial() {
		stateMaterial = false;
	}

	public static void disableDepth() {
		if (stateDepthTest) {
			_wglDisable(GL_DEPTH_TEST);
			stateDepthTest = false;
		}
	}

	public static void enableDepth() {
		if (!stateDepthTest) {
			_wglEnable(GL_DEPTH_TEST);
			stateDepthTest = true;
		}
	}

	public static void eagPushStateForGLES2BlitHack() {
		stateDepthTestStash = stateDepthTest;
		stateCullStash = stateCull;
		stateBlendStash = stateBlend;
	}

	public static void eagPopStateForGLES2BlitHack() {
		if (stateDepthTestStash) {
			enableDepth();
		} else {
			disableDepth();
		}
		if (stateCullStash) {
			enableCull();
		} else {
			disableCull();
		}
		if (stateBlendStash) {
			enableBlend();
		} else {
			disableBlend();
		}
	}

	public static void depthFunc(int depthFunc) {
		if (depthFunc != stateDepthFunc) {
			_wglDepthFunc(depthFunc);
			stateDepthFunc = depthFunc;
		}
	}

	public static void depthMask(boolean flagIn) {
		if (flagIn != stateDepthMask) {
			_wglDepthMask(flagIn);
			stateDepthMask = flagIn;
		}
	}

	public static void disableBlend() {
		if (stateBlend) {
			if (stateGlobalBlend)
				_wglDisable(GL_BLEND);
			stateBlend = false;
		}
	}

	public static void enableBlend() {
		if (!stateBlend) {
			if (stateGlobalBlend)
				_wglEnable(GL_BLEND);
			stateBlend = true;
		}
	}

	public static void _disableBlend() {
		BLEND.mode.disable();
	}

	public static void _enableBlend() {
		BLEND.mode.enable();
	}

	public static void _blendFunc(int p_84329_, int p_84330_) {
		if (p_84329_ != BLEND.srcRgb || p_84330_ != BLEND.dstRgb) {
			BLEND.srcRgb = p_84329_;
			BLEND.dstRgb = p_84330_;
			GL11.glBlendFunc(p_84329_, p_84330_);
		}

	}

	public static void _blendFuncSeparate(int p_84336_, int p_84337_, int p_84338_, int p_84339_) {
		if (p_84336_ != BLEND.srcRgb || p_84337_ != BLEND.dstRgb || p_84338_ != BLEND.srcAlpha || p_84339_ != BLEND.dstAlpha) {
			BLEND.srcRgb = p_84336_;
			BLEND.dstRgb = p_84337_;
			BLEND.srcAlpha = p_84338_;
			BLEND.dstAlpha = p_84339_;
			glBlendFuncSeparate(p_84336_, p_84337_, p_84338_, p_84339_);
		}

	}

	public static void globalDisableBlend() {
		if (stateBlend) {
			_wglDisable(GL_BLEND);
		}
		stateGlobalBlend = false;
	}

	public static void globalEnableBlend() {
		if (stateBlend) {
			_wglEnable(GL_BLEND);
		}
		stateGlobalBlend = true;
	}

	public static void _glBindFramebuffer(int p_84487_, int p_84488_) {
		if (p_84487_ == 36009) {
			boolean drawSame = p_84488_ == currentDrawFramebuffer;
			boolean readSame = p_84488_ == currentReadFramebuffer;
			if (drawSame && readSame) {
				return;
			}
			currentDrawFramebuffer = p_84488_;
			currentReadFramebuffer = p_84488_;
		} else if (p_84487_ == 36008) {
			if (p_84488_ == currentReadFramebuffer) {
				return;
			}
			currentReadFramebuffer = p_84488_;
		} else if (p_84487_ == 36160) {
			if (p_84488_ == currentDrawFramebuffer) {
				return;
			}
			currentDrawFramebuffer = p_84488_;
		}
		GL30.glBindFramebuffer(p_84487_, p_84488_);
	}

	public static void _glBlitFrameBuffer(int p_84189_, int p_84190_, int p_84191_, int p_84192_, int p_84193_, int p_84194_, int p_84195_, int p_84196_, int p_84197_, int p_84198_) {
		GL30.glBlitFramebuffer(p_84189_, p_84190_, p_84191_, p_84192_, p_84193_, p_84194_, p_84195_, p_84196_, p_84197_, p_84198_);
	}

	public static void _glBindRenderbuffer(int p_157066_, int p_157067_) {
		GL30.glBindRenderbuffer(p_157066_, p_157067_);
	}

	public static void _glDeleteRenderbuffers(int p_157075_) {
		GL30.glDeleteRenderbuffers(p_157075_);
	}

	public static void _glDeleteFramebuffers(int p_84503_) {
		if (currentDrawFramebuffer == p_84503_) {
			currentDrawFramebuffer = -1;
		}
		if (currentReadFramebuffer == p_84503_) {
			currentReadFramebuffer = -1;
		}
		GL30.glDeleteFramebuffers(p_84503_);
	}

	public static int glGenFramebuffers() {
		return GL30.glGenFramebuffers();
	}

	public static int glGenRenderbuffers() {
		return GL30.glGenRenderbuffers();
	}

	public static void blendFunc(int srcFactor, int dstFactor) {
		if (stateEnableOverlayFramebufferBlending) {
			tryBlendFuncSeparate(srcFactor, dstFactor, 0, 1);
			return;
		}
		int srcBits = (srcFactor | (srcFactor << 16));
		int dstBits = (dstFactor | (dstFactor << 16));
		if (srcBits != stateBlendSRC || dstBits != stateBlendDST) {
			_wglBlendFunc(srcFactor, dstFactor);
			stateBlendSRC = srcBits;
			stateBlendDST = dstBits;
		}
	}

	public static void _glBufferData(int p_84257_, ByteBuffer p_84258_, int p_84259_) {
		GL15.glBufferData(p_84257_, p_84258_, p_84259_);
	}

	public static void _glBufferDataDirect(int p_84257_,
			net.lax1dude.eaglercraft.v1_8.internal.buffer.ByteBuffer p_84258_, int p_84259_) {
		_wglBufferData(p_84257_, p_84258_, p_84259_);
	}

	public static void _glBufferData(int p_157071_, long p_157072_, int p_157073_) {
		GL15.glBufferData(p_157071_, p_157072_, p_157073_);
	}

	public static void tryBlendFuncSeparate(int srcFactor, int dstFactor, int srcFactorAlpha, int dstFactorAlpha) {
		if (stateEnableOverlayFramebufferBlending) { // game overlay framebuffer in EntityRenderer.java
			srcFactorAlpha = GL_ONE;
			dstFactorAlpha = GL_ONE_MINUS_SRC_ALPHA;
		}
		int srcBits = (srcFactor | (srcFactorAlpha << 16));
		int dstBits = (dstFactor | (dstFactorAlpha << 16));
		if (srcBits != stateBlendSRC || dstBits != stateBlendDST) {
			_wglBlendFuncSeparate(srcFactor, dstFactor, srcFactorAlpha, dstFactorAlpha);
			stateBlendSRC = srcBits;
			stateBlendDST = dstBits;
		}
	}

	public static void _clearStencil(int p_84554_) {
		GL11.glClearStencil(p_84554_);
	}

	public static void _drawElements(int p_157054_, int p_157055_, int p_157056_, long p_157057_) {
		GL11.glDrawElements(p_157054_, p_157055_, p_157056_, p_157057_);
	}

	public static void _clear(int p_84267_, boolean p_84268_) {
		GL11.glClear(p_84267_);
		if (p_84268_) {
			_getError();
		}

	}

	public static int _getError() {
		return GL11.glGetError();
	}

	public static String _getString(int p_84090_) {
		return GL11.glGetString(p_84090_);
	}

	public static int _getInteger(int p_84093_) {
		return GL11.glGetInteger(p_84093_);
	}

	public static void enableOverlayFramebufferBlending() {
		stateEnableOverlayFramebufferBlending = true;
	}

	public static void disableOverlayFramebufferBlending() {
		stateEnableOverlayFramebufferBlending = false;
	}

	public static void setShaderBlendSrc(float r, float g, float b, float a) {
		stateShaderBlendSrcColorR = r;
		stateShaderBlendSrcColorG = g;
		stateShaderBlendSrcColorB = b;
		stateShaderBlendSrcColorA = a;
		++stateShaderBlendColorSerial;
	}

	public static void setShaderBlendAdd(float r, float g, float b, float a) {
		stateShaderBlendAddColorR = r;
		stateShaderBlendAddColorG = g;
		stateShaderBlendAddColorB = b;
		stateShaderBlendAddColorA = a;
		++stateShaderBlendColorSerial;
	}

	public static void enableShaderBlendAdd() {
		stateEnableShaderBlendColor = true;
	}

	public static void disableShaderBlendAdd() {
		stateEnableShaderBlendColor = false;
	}

	public static void setBlendConstants(float r, float g, float b, float a) {
		if (r != blendConstantR || g != blendConstantG || b != blendConstantB || a != blendConstantA) {
			_wglBlendColor(r, g, b, a);
			blendConstantR = r;
			blendConstantG = g;
			blendConstantB = b;
			blendConstantA = a;
		}
	}

	public static void _clearColor(float p_84319_, float p_84320_, float p_84321_, float p_84322_) {
		GL11.glClearColor(p_84319_, p_84320_, p_84321_, p_84322_);
	}

	public static void enableFog() {
		stateFog = true;
	}

	public static void disableFog() {
		stateFog = false;
	}

	public static void _bindTexture(int p_84545_) {
		if (p_84545_ != TEXTURES[activeTexture].binding) {
			TEXTURES[activeTexture].binding = p_84545_;
			GL11.glBindTexture(3553, p_84545_);
		}

	}

	public static void setFog(int param) {
		stateFogEXP = param == GL_EXP;
		++stateFogSerial;
	}

	public static void setFogDensity(float param) {
		stateFogDensity = param;
		++stateFogSerial;
	}

	public static void setFogStart(float param) {
		stateFogStart = param;
		++stateFogSerial;
	}

	public static void setFogEnd(float param) {
		stateFogEnd = param;
		++stateFogSerial;
	}

	public static void enableCull() {
		if (!stateCull) {
			_wglEnable(GL_CULL_FACE);
			stateCull = true;
		}
	}

	public static void disableCull() {
		if (stateCull) {
			_wglDisable(GL_CULL_FACE);
			stateCull = false;
		}
	}

	public static void cullFace(int mode) {
		if (stateCullFace != mode) {
			_wglCullFace(mode);
			stateCullFace = mode;
		}
	}

	public static void enablePolygonOffset() {
		if (!statePolygonOffset) {
			_wglEnable(GL_POLYGON_OFFSET_FILL);
			statePolygonOffset = true;
		}
	}

	public static void disablePolygonOffset() {
		if (statePolygonOffset) {
			_wglDisable(GL_POLYGON_OFFSET_FILL);
			statePolygonOffset = false;
		}
	}

	public static void doPolygonOffset(float factor, float units) {
		if (factor != statePolygonOffsetFactor || units != statePolygonOffsetUnits) {
			_wglPolygonOffset(-factor, units);
			statePolygonOffsetFactor = factor;
			statePolygonOffsetUnits = units;
		}
	}

	public static void glBlendFuncSeparate(int p_84389_, int p_84390_, int p_84391_, int p_84392_) {
		GL14.glBlendFuncSeparate(p_84389_, p_84390_, p_84391_, p_84392_);
	}

	public static void _enableColorLogicOp() {
		COLOR_LOGIC.enable.enable();
	}

	public static void _disableColorLogicOp() {
		COLOR_LOGIC.enable.disable();
	}

	public static void _logicOp(int p_84533_) {
		if (p_84533_ != COLOR_LOGIC.op) {
			COLOR_LOGIC.op = p_84533_;
			GL11.glLogicOp(p_84533_);
		}

	}

	public static void enableColorLogic() {
		COLOR_LOGIC.enable.enable();
	}

	public static void disableColorLogic() {
		COLOR_LOGIC.enable.disable();
	}

	public static void _polygonMode(int p_84517_, int p_84518_) {
		GL11.glPolygonMode(p_84517_, p_84518_);
	}

	public static void _enablePolygonOffset() {
		POLY_OFFSET.fill.enable();
	}

	public static void _disablePolygonOffset() {
		POLY_OFFSET.fill.disable();
	}

	public static void _polygonOffset(float p_84137_, float p_84138_) {
		if (p_84137_ != POLY_OFFSET.factor || p_84138_ != POLY_OFFSET.units) {
			POLY_OFFSET.factor = p_84137_;
			POLY_OFFSET.units = p_84138_;
			GL11.glPolygonOffset(p_84137_, p_84138_);
		}

	}

	public static void colorLogicOp(int opcode) {

	}

	public static void _deleteTexture(int p_84542_) {
		GL11.glDeleteTextures(p_84542_);

		for(GlStateManager.TextureState glstatemanager$texturestate : TEXTURES) {
			if (glstatemanager$texturestate.binding == p_84542_) {
				glstatemanager$texturestate.binding = -1;
			}
		}

	}

	public static int _genTexture() {
		return GL11.glGenTextures();
	}

	public static void _genTextures(int[] p_84306_) {
		GL11.glGenTextures(p_84306_);
	}

	public static void _deleteTextures(int[] p_84366_) {

		for(GlStateManager.TextureState glstatemanager$texturestate : TEXTURES) {
			for(int i : p_84366_) {
				if (glstatemanager$texturestate.binding == i) {
					glstatemanager$texturestate.binding = -1;
				}
			}
		}

		GL11.glDeleteTextures(p_84366_);
	}

	public static void _glDrawPixels(int p_157079_, int p_157080_, int p_157081_, int p_157082_, long p_157083_) {
		GL11.glDrawPixels(p_157079_, p_157080_, p_157081_, p_157082_, p_157083_);
	}

	public static void enableTexGen() {
		stateTexGen = true;
	}

	public static void disableTexGen() {
		stateTexGen = false;
	}

	public static void texGen(GlStateManager.TexGen coord, int source) {
		coord.source = source;
		++stateTexGenSerial;
	}

	public static void func_179105_a(GlStateManager.TexGen coord, int plane, FloatBuffer vector) {
		coord.plane = plane;
		coord.vector.load(vector);
		if (plane == GL_EYE_PLANE) {
			tmpInvertedMatrix.load(GlStateManager.modelMatrixStack[GlStateManager.modelMatrixStackPointer]).invert()
					.transpose();
			Matrix4f.transform(tmpInvertedMatrix, coord.vector, coord.vector);
		}
		++stateTexGenSerial;
	}

	public static void _stencilOp(int p_84453_, int p_84454_, int p_84455_) {
		if (p_84453_ != STENCIL.fail || p_84454_ != STENCIL.zfail || p_84455_ != STENCIL.zpass) {
			STENCIL.fail = p_84453_;
			STENCIL.zfail = p_84454_;
			STENCIL.zpass = p_84455_;
			GL11.glStencilOp(p_84453_, p_84454_, p_84455_);
		}

	}

	public static void setActiveTexture(int texture) {
		int textureIdx = texture - GL_TEXTURE0;
		if (textureIdx != activeTexture) {
			_wglActiveTexture(texture);
			activeTexture = textureIdx;
		}
	}

	public static void _pixelStore(int p_84523_, int p_84524_) {
		GL11.glPixelStorei(p_84523_, p_84524_);
	}

	public static void _enableTexture() {
		TEXTURES[activeTexture].enable = true;
	}

	public static void _disableTexture() {
		TEXTURES[activeTexture].enable = false;
	}

	public static void enableTexture2D() {
		stateTexture[activeTexture] = true;
	}

	public static void disableTexture2D() {
		stateTexture[activeTexture] = false;
	}

	public static void texCoords2D(float x, float y) {
		textureCoordsX[activeTexture] = x;
		textureCoordsY[activeTexture] = y;
		++textureCoordsAccessSerial[activeTexture];
	}

	public static void texCoords2DDirect(int tex, float x, float y) {
		textureCoordsX[tex] = x;
		textureCoordsY[tex] = y;
		++textureCoordsAccessSerial[tex];
	}

	public static float getTexCoordX(int tex) {
		return textureCoordsX[tex];
	}

	public static float getTexCoordY(int tex) {
		return textureCoordsY[tex];
	}

	public static int generateTexture() {
		return EaglercraftGPU.mapTexturesGL.register(_wglGenTextures());
	}

	public static void deleteTexture(int texture) {
		unbindTextureIfCached(texture);
		ITextureGL textureObj = EaglercraftGPU.mapTexturesGL.free(texture);
		if (textureObj != null) {
			_wglDeleteTextures(textureObj);
		}
	}

	static void unbindTextureIfCached(int texture) {
		boolean f1, f2 = false;
		for (int i = 0; i < boundTexture.length; ++i) {
			if (boundTexture[i] == texture) {
				f1 = i != activeTexture;
				if (f2 || f1) {
					_wglActiveTexture(GL_TEXTURE0 + i);
					f2 = f1;
				}
				_wglBindTexture(GL_TEXTURE_2D, null);
				if (EaglercraftGPU.checkOpenGLESVersion() >= 300) {
					_wglBindTexture(GL_TEXTURE_3D, null);
				}
				boundTexture[i] = -1;
			}
		}
		if (f2) {
			_wglActiveTexture(GL_TEXTURE0 + activeTexture);
		}
	}
	
	public static ITextureGL getCurrentBoundTexture() {
		return EaglercraftGPU.getNativeTexture(getBoundTexture());
	}
	
	protected static final void updateAnisotropicPatch() {
		stateAnisotropicFixSerial++;
		//if(activeTexture == GL_TEXTURE0) {
			enableAnisotropicFix = false;
			ITextureGL boundTexture = getCurrentBoundTexture();
			if(enableAnisotropicPatch && boundTexture != null && boundTexture.isAnisotropic() && boundTexture.isNearest()) {
				enableAnisotropicFix = true;
				stateAnisotropicFixW = boundTexture.getWidth();
				stateAnisotropicFixH = boundTexture.getHeight();
			}
		//}
	}

	public static void bindTexture(int texture) {
		if (texture != boundTexture[activeTexture]) {
			_wglBindTexture(GL_TEXTURE_2D, EaglercraftGPU.mapTexturesGL.get(texture));
			boundTexture[activeTexture] = texture;
			
			//if(activeTexture == GL_TEXTURE0) {
				updateAnisotropicPatch();
			//}
		}
	}

	public static void _readPixels(int p_84220_, int p_84221_, int p_84222_, int p_84223_, int p_84224_, int p_84225_, ByteBuffer p_84226_) {
		GL11.glReadPixels(p_84220_, p_84221_, p_84222_, p_84223_, p_84224_, p_84225_, p_84226_);
	}

	public static void _readPixels(int p_157101_, int p_157102_, int p_157103_, int p_157104_, int p_157105_, int p_157106_, long p_157107_) {
		GL11.glReadPixels(p_157101_, p_157102_, p_157103_, p_157104_, p_157105_, p_157106_, p_157107_);
	}
	
	public static void bindTexture2(int texture) {
		_wglBindTexture(GL_TEXTURE_2D, EaglercraftGPU.mapTexturesGL.get(texture));
		boundTexture[activeTexture] = texture;
		updateAnisotropicPatch();
	}

	public static void bindTexture3D(int texture) {
		if (texture != boundTexture[activeTexture]) {
			_wglBindTexture(GL_TEXTURE_3D, EaglercraftGPU.mapTexturesGL.get(texture));
			boundTexture[activeTexture] = texture;
		}
	}

	public static void _texImage2D(int p_84210_, int p_84211_, int p_84212_, int p_84213_, int p_84214_, int p_84215_, int p_84216_, int p_84217_, @Nullable IntBuffer p_84218_) {
		GL11.glTexImage2D(p_84210_, p_84211_, p_84212_, p_84213_, p_84214_, p_84215_, p_84216_, p_84217_, p_84218_);
	}

	public static void _texSubImage2D(int p_84200_, int p_84201_, int p_84202_, int p_84203_, int p_84204_, int p_84205_, int p_84206_, int p_84207_, long p_84208_) {
		GL11.glTexSubImage2D(p_84200_, p_84201_, p_84202_, p_84203_, p_84204_, p_84205_, p_84206_, p_84207_, p_84208_);
	}

	public static void _getTexImage(int p_84228_, int p_84229_, int p_84230_, int p_84231_, long p_84232_) {
		GL11.glGetTexImage(p_84228_, p_84229_, p_84230_, p_84231_, p_84232_);
	}

	public static void _viewport(int p_84431_, int p_84432_, int p_84433_, int p_84434_) {
		GlStateManager.Viewport.INSTANCE.x = p_84431_;
		GlStateManager.Viewport.INSTANCE.y = p_84432_;
		GlStateManager.Viewport.INSTANCE.width = p_84433_;
		GlStateManager.Viewport.INSTANCE.height = p_84434_;
		GL11.glViewport(p_84431_, p_84432_, p_84433_, p_84434_);
	}

	public static void _colorMask(boolean p_84301_, boolean p_84302_, boolean p_84303_, boolean p_84304_) {
		if (p_84301_ != COLOR_MASK.red || p_84302_ != COLOR_MASK.green || p_84303_ != COLOR_MASK.blue || p_84304_ != COLOR_MASK.alpha) {
			COLOR_MASK.red = p_84301_;
			COLOR_MASK.green = p_84302_;
			COLOR_MASK.blue = p_84303_;
			COLOR_MASK.alpha = p_84304_;
			GL11.glColorMask(p_84301_, p_84302_, p_84303_, p_84304_);
		}

	}

	public static void _stencilFunc(int p_84427_, int p_84428_, int p_84429_) {
		if (p_84427_ != STENCIL.func.func || p_84427_ != STENCIL.func.ref || p_84427_ != STENCIL.func.mask) {
			STENCIL.func.func = p_84427_;
			STENCIL.func.ref = p_84428_;
			STENCIL.func.mask = p_84429_;
			GL11.glStencilFunc(p_84427_, p_84428_, p_84429_);
		}

	}

	public static void _stencilMask(int p_84551_) {
		if (p_84551_ != STENCIL.mask) {
			STENCIL.mask = p_84551_;
			GL11.glStencilMask(p_84551_);
		}

	}

	public static void _clearDepth(double p_84122_) {
		GL11.glClearDepth(p_84122_);
	}

	public static void quickBindTexture(int unit, int texture) {
		int unitBase = unit - GL_TEXTURE0;
		int previousActiveTexture = activeTexture;
		TEXTURES[unitBase].enable = true;
		TEXTURES[unitBase].binding = texture;
		if (texture != boundTexture[unitBase]) {
			if (unitBase != previousActiveTexture) {
				_wglActiveTexture(unit);
				activeTexture = unitBase;
			}
			_wglBindTexture(GL_TEXTURE_2D, EaglercraftGPU.mapTexturesGL.get(texture));
			boundTexture[unitBase] = texture;
			updateAnisotropicPatch();
			if (unitBase != previousActiveTexture) {
				_wglActiveTexture(GL_TEXTURE0 + previousActiveTexture);
				activeTexture = previousActiveTexture;
			}
		}
	}

	public static void _enableCull() {
		CULL.enable.enable();
	}

	public static void _disableCull() {
		CULL.enable.disable();
	}

	public static int _getTexLevelParameter(int p_84385_, int p_84386_, int p_84387_) {
		return GL11.glGetTexLevelParameteri(p_84385_, p_84386_, p_84387_);
	}

	public static void _blendEquation(int p_84380_) {
		GL14.glBlendEquation(p_84380_);
	}

	public static int glGetProgrami(int p_84382_, int p_84383_) {
		return GL20.glGetProgrami(p_84382_, p_84383_);
	}

	public static void glAttachShader(int p_84424_, int p_84425_) {
		GL20.glAttachShader(p_84424_, p_84425_);
	}

	public static void glDeleteShader(int p_84422_) {
		GL20.glDeleteShader(p_84422_);
	}

	public static int glCreateShader(int p_84448_) {
		return GL20.glCreateShader(p_84448_);
	}

	public static void glShaderSource(int p_157117_, List<String> p_157118_) {
		StringBuilder stringbuilder = new StringBuilder();

		for(String s : p_157118_) {
			stringbuilder.append(s);
		}

		GL20.glShaderSource(p_157117_, stringbuilder);

	}

	public static String glGetProgramInfoLog(int p_84499_, int p_84500_) {
		return GL20.glGetProgramInfoLog(p_84499_, p_84500_);
	}

	public static void _disableDepthTest() {
		DEPTH.mode.disable();
	}

	public static void _enableDepthTest() {
		DEPTH.mode.enable();
	}

	public static void _glDeleteBuffers(int p_84497_) {
		if (currentArrayBuffer == p_84497_) {
			currentArrayBuffer = -1;
		}
		if (currentElementBuffer == p_84497_) {
			currentElementBuffer = -1;
		}
		GL15.glDeleteBuffers(p_84497_);
	}

	public static void _depthFunc(int p_84324_) {
		if (p_84324_ != DEPTH.func) {
			DEPTH.func = p_84324_;
			GL11.glDepthFunc(p_84324_);
		}

	}

	public static void _depthMask(boolean p_84299_) {
		if (p_84299_ != DEPTH.mask) {
			DEPTH.mask = p_84299_;
			GL11.glDepthMask(p_84299_);
		}

	}

	public static void _glDeleteVertexArrays(int p_157077_) {
		if (currentVertexArray == p_157077_) {
			currentVertexArray = -1;
		}
		GL30.glDeleteVertexArrays(p_157077_);
	}

	public static void glCompileShader(int p_84466_) {
		GL20.glCompileShader(p_84466_);
	}

	public static int glGetShaderi(int p_84450_, int p_84451_) {
		return GL20.glGetShaderi(p_84450_, p_84451_);
	}

	public static String glGetShaderInfoLog(int p_84493_, int p_84494_) {
		return GL20.glGetShaderInfoLog(p_84493_, p_84494_);
	}

	public static int _glGetAttribLocation(int p_84399_, CharSequence p_84400_) {
		return GL20.glGetAttribLocation(p_84399_, p_84400_);
	}

	public static void _glBindAttribLocation(int p_157062_, int p_157063_, CharSequence p_157064_) {
		GL20.glBindAttribLocation(p_157062_, p_157063_, p_157064_);
	}

	public static int _glGenBuffers() {
		return GL15.glGenBuffers();
	}

	public static int _glGenVertexArrays() {
		return GL30.glGenVertexArrays();
	}

	public static void _glBindBuffer(int p_84481_, int p_84482_) {
		GL15.glBindBuffer(p_84481_, p_84482_);
	}

	public static void _glBindVertexArray(int p_157069_) {
		GL30.glBindVertexArray(p_157069_);
	}

	public static void _vertexAttribPointer(int p_84239_, int p_84240_, int p_84241_, boolean p_84242_, int p_84243_, long p_84244_) {
		GL20.glVertexAttribPointer(p_84239_, p_84240_, p_84241_, p_84242_, p_84243_, p_84244_);
	}

	public static void _vertexAttribIPointer(int p_157109_, int p_157110_, int p_157111_, int p_157112_, long p_157113_) {
		GL30.glVertexAttribIPointer(p_157109_, p_157110_, p_157111_, p_157112_, p_157113_);
	}

	public static void _enableVertexAttribArray(int p_84566_) {
		GL20.glEnableVertexAttribArray(p_84566_);
	}

	public static void _disableVertexAttribArray(int p_84087_) {
		GL20.glDisableVertexAttribArray(p_84087_);
	}

	public static int _getTextureId(int p_157060_) {
		return p_157060_ >= 0 && p_157060_ < 12 && TEXTURES[p_157060_].enable ? TEXTURES[p_157060_].binding : 0;
	}

	public static int _getActiveTexture() {
		return activeTexture + '\u84c0';
	}

	public static void _activeTexture(int p_84539_) {
		// Delegate to setActiveTexture so the real _wglActiveTexture switch happens.
		// Setting the activeTexture field here first would make setActiveTexture's
		// guard skip the actual unit switch, leaving multi-unit binds (e.g. the
		// lightmap on unit 2) stuck on unit 0.
		setActiveTexture(p_84539_);
	}

	public static void shadeModel(int mode) {

	}

	public static void enableRescaleNormal() {
		// still not sure what this is for
	}

	public static void disableRescaleNormal() {

	}

	public static void viewport(int x, int y, int w, int h) {
		if (viewportX != x || viewportY != y || viewportW != w || viewportH != h) {
			_wglViewport(x, y, w, h);
			viewportX = x;
			viewportY = y;
			viewportW = w;
			viewportH = h;
		}
	}

	public static void colorMask(boolean red, boolean green, boolean blue, boolean alpha) {
		int bits = (red ? 1 : 0) | (green ? 2 : 0) | (blue ? 4 : 0) | (alpha ? 8 : 0);
		if (bits != colorMaskBits) {
			_wglColorMask(red, green, blue, alpha);
			colorMaskBits = bits;
		}
	}

	public static void clearDepth(float depth) {
		if (depth != clearDepth) {
			_wglClearDepth(depth);
			clearDepth = depth;
		}
	}

	public static void clearColor(float red, float green, float blue, float alpha) {
		if (red != clearColorR || green != clearColorG || blue != clearColorB || alpha != clearColorA) {
			_wglClearColor(red, green, blue, alpha);
			clearColorR = red;
			clearColorG = green;
			clearColorB = blue;
			clearColorA = alpha;
		}
	}

	public static void clear(int mask) {
		_wglClear(mask);
	}

	public static void matrixMode(int mode) {
		stateMatrixMode = mode;
	}

	public static void loadIdentity() {
		switch (stateMatrixMode) {
		case GL_MODELVIEW:
		default:
			modelMatrixStack[modelMatrixStackPointer].setIdentity();
			modelMatrixStackAccessSerial[modelMatrixStackPointer] = ++modelMatrixAccessSerial;
			break;
		case GL_PROJECTION:
			projectionMatrixStack[projectionMatrixStackPointer].setIdentity();
			projectionMatrixStackAccessSerial[projectionMatrixStackPointer] = ++projectionMatrixAccessSerial;
			break;
		case GL_TEXTURE:
			textureMatrixStack[activeTexture][textureMatrixStackPointer[activeTexture]].setIdentity();
			textureMatrixStackAccessSerial[activeTexture][textureMatrixStackPointer[activeTexture]] = ++textureMatrixAccessSerial[activeTexture];
			break;
		}
	}

	public static void pushMatrix() {
		int push;
		switch (stateMatrixMode) {
		case GL_MODELVIEW:
		default:
			push = modelMatrixStackPointer + 1;
			if (push < modelMatrixStack.length) {
				modelMatrixStack[push].load(modelMatrixStack[modelMatrixStackPointer]);
				modelMatrixStackAccessSerial[push] = modelMatrixStackAccessSerial[modelMatrixStackPointer];
				modelMatrixStackPointer = push;
			} else {
				Throwable t = new IndexOutOfBoundsException("GL_MODELVIEW matrix stack overflow!" + " Exceeded "
						+ modelMatrixStack.length + " calls to GlStateManager.pushMatrix");
				logger.error(t);
			}
			break;
		case GL_PROJECTION:
			push = projectionMatrixStackPointer + 1;
			if (push < projectionMatrixStack.length) {
				projectionMatrixStack[push].load(projectionMatrixStack[projectionMatrixStackPointer]);
				projectionMatrixStackAccessSerial[push] = projectionMatrixStackAccessSerial[projectionMatrixStackPointer];
				projectionMatrixStackPointer = push;
			} else {
				Throwable t = new IndexOutOfBoundsException("GL_PROJECTION matrix stack overflow!" + " Exceeded "
						+ projectionMatrixStack.length + " calls to GlStateManager.pushMatrix");
				logger.error(t);
			}
			break;
		case GL_TEXTURE:
			push = textureMatrixStackPointer[activeTexture] + 1;
			if (push < textureMatrixStack.length) {
				int ptr = textureMatrixStackPointer[activeTexture];
				textureMatrixStack[activeTexture][push].load(textureMatrixStack[activeTexture][ptr]);
				textureMatrixStackAccessSerial[activeTexture][push] = textureMatrixStackAccessSerial[activeTexture][ptr];
				textureMatrixStackPointer[activeTexture] = push;
			} else {
				Throwable t = new IndexOutOfBoundsException("GL_TEXTURE #" + activeTexture + " matrix stack overflow!"
						+ " Exceeded " + textureMatrixStack.length + " calls to GlStateManager.pushMatrix");
				logger.error(t);
			}
			break;
		}
	}

	public static void popMatrix() {
		switch (stateMatrixMode) {
		case GL_MODELVIEW:
		default:
			if (modelMatrixStackPointer > 0) {
				--modelMatrixStackPointer;
			} else {
				Throwable t = new IndexOutOfBoundsException("GL_MODELVIEW matrix stack underflow!"
						+ " Called GlStateManager.popMatrix on an empty matrix stack");
				logger.error(t);
			}
			break;
		case GL_PROJECTION:
			if (projectionMatrixStackPointer > 0) {
				--projectionMatrixStackPointer;
			} else {
				Throwable t = new IndexOutOfBoundsException("GL_PROJECTION matrix stack underflow!"
						+ " Called GlStateManager.popMatrix on an empty matrix stack");
				logger.error(t);
			}
			break;
		case GL_TEXTURE:
			if (textureMatrixStackPointer[activeTexture] > 0) {
				--textureMatrixStackPointer[activeTexture];
			} else {
				Throwable t = new IndexOutOfBoundsException("GL_TEXTURE #" + activeTexture
						+ " matrix stack underflow!  Called GlStateManager.popMatrix on an empty matrix stack");
				logger.error(t);
			}
			break;
		}
	}

	private static Matrix4f getMatrixIncr() {
		Matrix4f mat;
		int _i, _j;
		switch (stateMatrixMode) {
		case GL_MODELVIEW:
			_j = modelMatrixStackPointer;
			mat = modelMatrixStack[_j];
			modelMatrixStackAccessSerial[_j] = ++modelMatrixAccessSerial;
			break;
		case GL_PROJECTION:
			_j = projectionMatrixStackPointer;
			mat = projectionMatrixStack[_j];
			projectionMatrixStackAccessSerial[_j] = ++projectionMatrixAccessSerial;
			break;
		case GL_TEXTURE:
			_i = activeTexture;
			_j = textureMatrixStackPointer[_i];
			mat = textureMatrixStack[_i][_j];
			textureMatrixStackAccessSerial[_i][_j] = ++textureCoordsAccessSerial[_i];
			break;
		default:
			throw new IllegalStateException();
		}
		return mat;
	}

	public static void getFloat(int pname, float[] params) {
		switch (pname) {
		case GL_MODELVIEW_MATRIX:
			modelMatrixStack[modelMatrixStackPointer].store(params);
			break;
		case GL_PROJECTION_MATRIX:
			projectionMatrixStack[projectionMatrixStackPointer].store(params);
			break;
		case GL_TEXTURE_MATRIX:
			textureMatrixStack[activeTexture][textureMatrixStackPointer[activeTexture]].store(params);
			break;
		default:
			throw new UnsupportedOperationException("glGetFloat can only be used to retrieve matricies!");
		}
	}

	public static void getFloat(int pname, FloatBuffer params) {
		switch (pname) {
		case GL_MODELVIEW_MATRIX:
			modelMatrixStack[modelMatrixStackPointer].store(params);
			break;
		case GL_PROJECTION_MATRIX:
			projectionMatrixStack[projectionMatrixStackPointer].store(params);
			break;
		case GL_TEXTURE_MATRIX:
			textureMatrixStack[activeTexture][textureMatrixStackPointer[activeTexture]].store(params);
			break;
		default:
			throw new UnsupportedOperationException("glGetFloat can only be used to retrieve matricies!");
		}
	}

	public static void ortho(double left, double right, double bottom, double top, double zNear, double zFar) {
		Matrix4f matrix = getMatrixIncr();
		paramMatrix.m00 = 2.0f / (float) (right - left);
		paramMatrix.m01 = 0.0f;
		paramMatrix.m02 = 0.0f;
		paramMatrix.m03 = 0.0f;
		paramMatrix.m10 = 0.0f;
		paramMatrix.m11 = 2.0f / (float) (top - bottom);
		paramMatrix.m12 = 0.0f;
		paramMatrix.m13 = 0.0f;
		paramMatrix.m20 = 0.0f;
		paramMatrix.m21 = 0.0f;
		paramMatrix.m22 = 2.0f / (float) (zFar - zNear);
		paramMatrix.m23 = 0.0f;
		paramMatrix.m30 = (float) (-(right + left) / (right - left));
		paramMatrix.m31 = (float) (-(top + bottom) / (top - bottom));
		paramMatrix.m32 = (float) ((zFar + zNear) / (zFar - zNear));
		paramMatrix.m33 = 1.0f;
		Matrix4f.mul(matrix, paramMatrix, matrix);
	}

	private static final float toRad = 0.0174532925f;

	public static void rotate(float angle, float x, float y, float z) {
		Matrix4f matrix = getMatrixIncr();
		if (x == 0.0f) {
			if (y == 0.0f) {
				if (z == 1.0f || z == -1.0f) {
					_glRotatefZ(matrix, toRad * angle * z);
					return;
				}
			} else if ((y == 1.0f || y == -1.0f) && z == 0.0f) {
				_glRotatefY(matrix, toRad * angle * y);
				return;
			}
		} else if ((x == 1.0f || x == -1.0f) && y == 0.0f && z == 0.0f) {
			_glRotatefX(matrix, toRad * angle * x);
			return;
		}
		_glRotatef(matrix, toRad * angle, x, y, z);
	}

	public static void _texParameter(int p_84161_, int p_84162_, float p_84163_) {
		GL11.glTexParameterf(p_84161_, p_84162_, p_84163_);
	}

	public static void _texParameter(int p_84332_, int p_84333_, int p_84334_) {
		GL11.glTexParameteri(p_84332_, p_84333_, p_84334_);
	}

	public static void rotateXYZ(float x, float y, float z) {
		Matrix4f matrix = getMatrixIncr();
		if (x != 0.0f)
			_glRotatefX(matrix, toRad * x);
		if (y != 0.0f)
			_glRotatefY(matrix, toRad * y);
		if (z != 0.0f)
			_glRotatefZ(matrix, toRad * z);
	}

	public static void rotateZYX(float x, float y, float z) {
		Matrix4f matrix = getMatrixIncr();
		if (z != 0.0f)
			_glRotatefZ(matrix, toRad * z);
		if (y != 0.0f)
			_glRotatefY(matrix, toRad * y);
		if (x != 0.0f)
			_glRotatefX(matrix, toRad * x);
	}

	public static void rotateXYZRad(float x, float y, float z) {
		Matrix4f matrix = getMatrixIncr();
		if (x != 0.0f)
			_glRotatefX(matrix, x);
		if (y != 0.0f)
			_glRotatefY(matrix, y);
		if (z != 0.0f)
			_glRotatefZ(matrix, z);
	}

	public static void rotateZYXRad(float x, float y, float z) {
		Matrix4f matrix = getMatrixIncr();
		if (z != 0.0f)
			_glRotatefZ(matrix, z);
		if (y != 0.0f)
			_glRotatefY(matrix, y);
		if (x != 0.0f)
			_glRotatefX(matrix, x);
	}

	private static void _glRotatefX(Matrix4f mat, float angle) {
		float sin = MathHelper.sin(angle);
		float cos = MathHelper.cos(angle);
		float lm10 = mat.m10, lm11 = mat.m11, lm12 = mat.m12, lm13 = mat.m13, lm20 = mat.m20, lm21 = mat.m21,
				lm22 = mat.m22, lm23 = mat.m23;
		mat.m20 = lm10 * -sin + lm20 * cos;
		mat.m21 = lm11 * -sin + lm21 * cos;
		mat.m22 = lm12 * -sin + lm22 * cos;
		mat.m23 = lm13 * -sin + lm23 * cos;
		mat.m10 = lm10 * cos + lm20 * sin;
		mat.m11 = lm11 * cos + lm21 * sin;
		mat.m12 = lm12 * cos + lm22 * sin;
		mat.m13 = lm13 * cos + lm23 * sin;
	}

	private static void _glRotatefY(Matrix4f mat, float angle) {
		float sin = MathHelper.sin(angle);
		float cos = MathHelper.cos(angle);
		float nm00 = mat.m00 * cos + mat.m20 * -sin;
		float nm01 = mat.m01 * cos + mat.m21 * -sin;
		float nm02 = mat.m02 * cos + mat.m22 * -sin;
		float nm03 = mat.m03 * cos + mat.m23 * -sin;
		mat.m20 = mat.m00 * sin + mat.m20 * cos;
		mat.m21 = mat.m01 * sin + mat.m21 * cos;
		mat.m22 = mat.m02 * sin + mat.m22 * cos;
		mat.m23 = mat.m03 * sin + mat.m23 * cos;
		mat.m00 = nm00;
		mat.m01 = nm01;
		mat.m02 = nm02;
		mat.m03 = nm03;
	}

	private static void _glRotatefZ(Matrix4f mat, float angle) {
		float dirX = MathHelper.sin(angle);
		float dirY = MathHelper.cos(angle);
		float nm00 = mat.m00 * dirY + mat.m10 * dirX;
		float nm01 = mat.m01 * dirY + mat.m11 * dirX;
		float nm02 = mat.m02 * dirY + mat.m12 * dirX;
		float nm03 = mat.m03 * dirY + mat.m13 * dirX;
		mat.m10 = mat.m00 * -dirX + mat.m10 * dirY;
		mat.m11 = mat.m01 * -dirX + mat.m11 * dirY;
		mat.m12 = mat.m02 * -dirX + mat.m12 * dirY;
		mat.m13 = mat.m03 * -dirX + mat.m13 * dirY;
		mat.m00 = nm00;
		mat.m01 = nm01;
		mat.m02 = nm02;
		mat.m03 = nm03;
	}

	private static void _glRotatef(Matrix4f mat, float angle, float x, float y, float z) {
		float s = MathHelper.sin(angle);
		float c = MathHelper.cos(angle);
		float C = 1.0f - c;
		float xx = x * x, xy = x * y, xz = x * z;
		float yy = y * y, yz = y * z;
		float zz = z * z;
		float rm00 = xx * C + c;
		float rm01 = xy * C + z * s;
		float rm02 = xz * C - y * s;
		float rm10 = xy * C - z * s;
		float rm11 = yy * C + c;
		float rm12 = yz * C + x * s;
		float rm20 = xz * C + y * s;
		float rm21 = yz * C - x * s;
		float rm22 = zz * C + c;
		float nm00 = mat.m00 * rm00 + mat.m10 * rm01 + mat.m20 * rm02;
		float nm01 = mat.m01 * rm00 + mat.m11 * rm01 + mat.m21 * rm02;
		float nm02 = mat.m02 * rm00 + mat.m12 * rm01 + mat.m22 * rm02;
		float nm03 = mat.m03 * rm00 + mat.m13 * rm01 + mat.m23 * rm02;
		float nm10 = mat.m00 * rm10 + mat.m10 * rm11 + mat.m20 * rm12;
		float nm11 = mat.m01 * rm10 + mat.m11 * rm11 + mat.m21 * rm12;
		float nm12 = mat.m02 * rm10 + mat.m12 * rm11 + mat.m22 * rm12;
		float nm13 = mat.m03 * rm10 + mat.m13 * rm11 + mat.m23 * rm12;
		mat.m20 = mat.m00 * rm20 + mat.m10 * rm21 + mat.m20 * rm22;
		mat.m21 = mat.m01 * rm20 + mat.m11 * rm21 + mat.m21 * rm22;
		mat.m22 = mat.m02 * rm20 + mat.m12 * rm21 + mat.m22 * rm22;
		mat.m23 = mat.m03 * rm20 + mat.m13 * rm21 + mat.m23 * rm22;
		mat.m00 = nm00;
		mat.m01 = nm01;
		mat.m02 = nm02;
		mat.m03 = nm03;
		mat.m10 = nm10;
		mat.m11 = nm11;
		mat.m12 = nm12;
		mat.m13 = nm13;
	}

	public static void scale(float x, float y, float z) {
		Matrix4f matrix = getMatrixIncr();
		matrix.m00 *= x;
		matrix.m01 *= x;
		matrix.m02 *= x;
		matrix.m03 *= x;
		matrix.m10 *= y;
		matrix.m11 *= y;
		matrix.m12 *= y;
		matrix.m13 *= y;
		matrix.m20 *= z;
		matrix.m21 *= z;
		matrix.m22 *= z;
		matrix.m23 *= z;
	}

	public static void scale(double x, double y, double z) {
		Matrix4f matrix = getMatrixIncr();
		matrix.m00 *= x;
		matrix.m01 *= x;
		matrix.m02 *= x;
		matrix.m03 *= x;
		matrix.m10 *= y;
		matrix.m11 *= y;
		matrix.m12 *= y;
		matrix.m13 *= y;
		matrix.m20 *= z;
		matrix.m21 *= z;
		matrix.m22 *= z;
		matrix.m23 *= z;
	}

	public static void translate(float x, float y, float z) {
		Matrix4f matrix = getMatrixIncr();
		matrix.m30 = matrix.m00 * x + matrix.m10 * y + matrix.m20 * z + matrix.m30;
		matrix.m31 = matrix.m01 * x + matrix.m11 * y + matrix.m21 * z + matrix.m31;
		matrix.m32 = matrix.m02 * x + matrix.m12 * y + matrix.m22 * z + matrix.m32;
		matrix.m33 = matrix.m03 * x + matrix.m13 * y + matrix.m23 * z + matrix.m33;
	}

	public static void translate(double x, double y, double z) {
		float _x = (float) x;
		float _y = (float) y;
		float _z = (float) z;
		Matrix4f matrix = getMatrixIncr();
		matrix.m30 = matrix.m00 * _x + matrix.m10 * _y + matrix.m20 * _z + matrix.m30;
		matrix.m31 = matrix.m01 * _x + matrix.m11 * _y + matrix.m21 * _z + matrix.m31;
		matrix.m32 = matrix.m02 * _x + matrix.m12 * _y + matrix.m22 * _z + matrix.m32;
		matrix.m33 = matrix.m03 * _x + matrix.m13 * _y + matrix.m23 * _z + matrix.m33;
	}

	private static final Matrix4f paramMatrix = new Matrix4f();

	public static void multMatrix(float[] matrix) {
		paramMatrix.load(matrix);
		Matrix4f mat = getMatrixIncr();
		Matrix4f.mul(mat, paramMatrix, mat);
	}

	public static void multMatrix(Matrix4f matrix) {
		Matrix4f mat = getMatrixIncr();
		Matrix4f.mul(mat, matrix, mat);
	}

	public static void color(float colorRed, float colorGreen, float colorBlue, float colorAlpha) {
		stateColorR = colorRed;
		stateColorG = colorGreen;
		stateColorB = colorBlue;
		stateColorA = colorAlpha;
		++stateColorSerial;
	}

	public static void color4f(float colorRed, float colorGreen, float colorBlue, float colorAlpha) {
		color(colorRed, colorGreen, colorBlue, colorAlpha);
	}

	public static void color(float colorRed, float colorGreen, float colorBlue) {
		stateColorR = colorRed;
		stateColorG = colorGreen;
		stateColorB = colorBlue;
		stateColorA = 1.0f;
		++stateColorSerial;
	}

	public static void resetColor() {
		stateColorR = 1.0f;
		stateColorG = 1.0f;
		stateColorB = 1.0f;
		stateColorA = 1.0f;
		++stateColorSerial;
	}

	public static void callList(int list) {
		EaglercraftGPU.glCallList(list);
	}

	public static void gluPerspective(float fovy, float aspect, float zNear, float zFar) {
		Matrix4f matrix = getMatrixIncr();
		float cotangent = (float) Math.cos(fovy * toRad * 0.5f) / (float) Math.sin(fovy * toRad * 0.5f);
		paramMatrix.m00 = cotangent / aspect;
		paramMatrix.m01 = 0.0f;
		paramMatrix.m02 = 0.0f;
		paramMatrix.m03 = 0.0f;
		paramMatrix.m10 = 0.0f;
		paramMatrix.m11 = cotangent;
		paramMatrix.m12 = 0.0f;
		paramMatrix.m13 = 0.0f;
		paramMatrix.m20 = 0.0f;
		paramMatrix.m21 = 0.0f;
		paramMatrix.m22 = (zFar + zNear) / (zFar - zNear);
		paramMatrix.m23 = -1.0f;
		paramMatrix.m30 = 0.0f;
		paramMatrix.m31 = 0.0f;
		paramMatrix.m32 = 2.0f * zFar * zNear / (zFar - zNear);
		paramMatrix.m33 = 0.0f;
		Matrix4f.mul(matrix, paramMatrix, matrix);
	}

	public static void gluLookAt(Vector3f eye, Vector3f center, Vector3f up) {
		Matrix4f matrix = getMatrixIncr();
		float x = center.x - eye.x;
		float y = center.y - eye.y;
		float z = center.z - eye.z;
		float xyzLen = (float) Math.sqrt(x * x + y * y + z * z);
		x /= xyzLen;
		y /= xyzLen;
		z /= xyzLen;
		float ux = up.x;
		float uy = up.y;
		float uz = up.z;
		xyzLen = (float) Math.sqrt(ux * ux + uy * uy + uz * uz);
		ux /= xyzLen;
		uy /= xyzLen;
		uz /= xyzLen;
		float lxx = y * uz - z * uy;
		float lxy = ux * z - uz * x;
		float lxz = x * uy - y * ux;
		float lyx = lxy * z - lxz * y;
		float lyy = x * lxz - z * lxx;
		float lyz = lxx * y - lxy * x;
		paramMatrix.m00 = lxx;
		paramMatrix.m01 = lyx;
		paramMatrix.m02 = -x;
		paramMatrix.m03 = 0.0f;
		paramMatrix.m10 = lxy;
		paramMatrix.m11 = lyy;
		paramMatrix.m12 = -y;
		paramMatrix.m13 = 0.0f;
		paramMatrix.m20 = lxz;
		paramMatrix.m21 = lyz;
		paramMatrix.m22 = -z;
		paramMatrix.m23 = 0.0f;
		paramMatrix.m30 = -eye.x;
		paramMatrix.m31 = -eye.y;
		paramMatrix.m32 = -eye.z;
		paramMatrix.m33 = 1.0f;
		Matrix4f.mul(matrix, paramMatrix, matrix);
	}

	public static void transform(Vector4f vecIn, Vector4f vecOut) {
		Matrix4f matrix;
		switch (stateMatrixMode) {
		case GL_MODELVIEW:
			matrix = modelMatrixStack[modelMatrixStackPointer];
			break;
		case GL_PROJECTION:
		default:
			matrix = projectionMatrixStack[projectionMatrixStackPointer];
			break;
		case GL_TEXTURE:
			matrix = textureMatrixStack[activeTexture][textureMatrixStackPointer[activeTexture]];
			break;
		}
		Matrix4f.transform(matrix, vecIn, vecOut);
	}

	private static final Matrix4f unprojA = new Matrix4f();
	private static final Matrix4f unprojB = new Matrix4f();
	private static final Vector4f unprojC = new Vector4f();

	public static void gluUnProject(float p1, float p2, float p3, float[] modelview, float[] projection, int[] viewport,
			float[] objectcoords) {
		unprojA.load(modelview);
		unprojB.load(projection);
		Matrix4f.mul(unprojA, unprojB, unprojB);
		unprojB.invert();
		unprojC.set(((p1 - (float) viewport[0]) / (float) viewport[2]) * 2f - 1f,
				((p2 - (float) viewport[1]) / (float) viewport[3]) * 2f - 1f, p3, 1.0f);
		Matrix4f.transform(unprojB, unprojC, unprojC);
		objectcoords[0] = unprojC.x / unprojC.w;
		objectcoords[1] = unprojC.y / unprojC.w;
		objectcoords[2] = unprojC.z / unprojC.w;
	}

	public static void getMatrix(Matrix4f mat) {
		switch (stateMatrixMode) {
		case GL_MODELVIEW:
			mat.load(modelMatrixStack[modelMatrixStackPointer]);
			break;
		case GL_PROJECTION:
		default:
			mat.load(projectionMatrixStack[projectionMatrixStackPointer]);
			break;
		case GL_TEXTURE:
			mat.load(textureMatrixStack[activeTexture][textureMatrixStackPointer[activeTexture]]);
			break;
		}
	}

	public static void loadMatrix(Matrix4f mat) {
		switch (stateMatrixMode) {
		case GL_MODELVIEW:
			modelMatrixStack[modelMatrixStackPointer].load(mat);
			modelMatrixStackAccessSerial[modelMatrixStackPointer] = ++modelMatrixAccessSerial;
			break;
		case GL_PROJECTION:
		default:
			projectionMatrixStack[projectionMatrixStackPointer].load(mat);
			projectionMatrixStackAccessSerial[projectionMatrixStackPointer] = ++projectionMatrixAccessSerial;
			break;
		case GL_TEXTURE:
			textureMatrixStack[activeTexture][textureMatrixStackPointer[activeTexture]].load(mat);
			textureMatrixStackAccessSerial[activeTexture][textureMatrixStackPointer[activeTexture]] = ++textureMatrixAccessSerial[activeTexture];
			break;
		}
	}

	public static int getModelViewSerial() {
		return modelMatrixStackAccessSerial[modelMatrixStackPointer];
	}

	public static Matrix4f getModelViewReference() {
		return modelMatrixStack[modelMatrixStackPointer];
	}

	public static Matrix4f getProjectionReference() {
		return projectionMatrixStack[projectionMatrixStackPointer];
	}

	public static void recompileShaders() {
		FixedFunctionPipeline.flushCache();
	}

	public static int getBoundTexture() {
		return boundTexture[activeTexture];
	}

	static void setTextureCachedSize(int target, int w, int h) {
		if (target == GL_TEXTURE_2D) {
			ITextureGL tex = EaglercraftGPU.getNativeTexture(boundTexture[activeTexture]);
			if (tex != null) {
				tex.setCacheSize(w, h);
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	static class BlendState {
		public final GlStateManager.BooleanState mode = new GlStateManager.BooleanState(3042);
		public int srcRgb = 1;
		public int dstRgb = 0;
		public int srcAlpha = 1;
		public int dstAlpha = 0;
	}

	@OnlyIn(Dist.CLIENT)
	static class BooleanState {
		private final int state;
		private boolean enabled;

		public BooleanState(int p_84588_) {
			this.state = p_84588_;
		}

		public void disable() {
			this.setEnabled(false);
		}

		public void enable() {
			this.setEnabled(true);
		}

		public void setEnabled(boolean p_84591_) {
			if (p_84591_ != this.enabled) {
				this.enabled = p_84591_;
				if (p_84591_) {
					GL11.glEnable(this.state);
				} else {
					GL11.glDisable(this.state);
				}
			}

		}
	}

	@OnlyIn(Dist.CLIENT)
	static class ColorLogicState {
		public final GlStateManager.BooleanState enable = new GlStateManager.BooleanState(3058);
		public int op = 5379;
	}

	@OnlyIn(Dist.CLIENT)
	static class ColorMask {
		public boolean red = true;
		public boolean green = true;
		public boolean blue = true;
		public boolean alpha = true;
	}

	@OnlyIn(Dist.CLIENT)
	static class CullState {
		public final GlStateManager.BooleanState enable = new GlStateManager.BooleanState(2884);
		public int mode = 1029;
	}

	@OnlyIn(Dist.CLIENT)
	static class DepthState {
		public final GlStateManager.BooleanState mode = new GlStateManager.BooleanState(2929);
		public boolean mask = true;
		public int func = 513;
	}

	@OnlyIn(Dist.CLIENT)
	public static enum DestFactor {
		CONSTANT_ALPHA(32771),
		CONSTANT_COLOR(32769),
		DST_ALPHA(772),
		DST_COLOR(774),
		ONE(1),
		ONE_MINUS_CONSTANT_ALPHA(32772),
		ONE_MINUS_CONSTANT_COLOR(32770),
		ONE_MINUS_DST_ALPHA(773),
		ONE_MINUS_DST_COLOR(775),
		ONE_MINUS_SRC_ALPHA(771),
		ONE_MINUS_SRC_COLOR(769),
		SRC_ALPHA(770),
		SRC_COLOR(768),
		ZERO(0);

		public final int value;

		private DestFactor(int p_84652_) {
			this.value = p_84652_;
		}
	}

	@OnlyIn(Dist.CLIENT)
	public static enum LogicOp {
		AND(5377),
		AND_INVERTED(5380),
		AND_REVERSE(5378),
		CLEAR(5376),
		COPY(5379),
		COPY_INVERTED(5388),
		EQUIV(5385),
		INVERT(5386),
		NAND(5390),
		NOOP(5381),
		NOR(5384),
		OR(5383),
		OR_INVERTED(5389),
		OR_REVERSE(5387),
		SET(5391),
		XOR(5382);

		public final int value;

		private LogicOp(int p_84721_) {
			this.value = p_84721_;
		}
	}

	@OnlyIn(Dist.CLIENT)
	static class PolygonOffsetState {
		public final GlStateManager.BooleanState fill = new GlStateManager.BooleanState(32823);
		public final GlStateManager.BooleanState line = new GlStateManager.BooleanState(10754);
		public float factor;
		public float units;
	}

	@OnlyIn(Dist.CLIENT)
	static class ScissorState {
		public final GlStateManager.BooleanState mode = new GlStateManager.BooleanState(3089);
	}

	@OnlyIn(Dist.CLIENT)
	public static enum SourceFactor {
		CONSTANT_ALPHA(32771),
		CONSTANT_COLOR(32769),
		DST_ALPHA(772),
		DST_COLOR(774),
		ONE(1),
		ONE_MINUS_CONSTANT_ALPHA(32772),
		ONE_MINUS_CONSTANT_COLOR(32770),
		ONE_MINUS_DST_ALPHA(773),
		ONE_MINUS_DST_COLOR(775),
		ONE_MINUS_SRC_ALPHA(771),
		ONE_MINUS_SRC_COLOR(769),
		SRC_ALPHA(770),
		SRC_ALPHA_SATURATE(776),
		SRC_COLOR(768),
		ZERO(0);

		public final int value;

		private SourceFactor(int p_84757_) {
			this.value = p_84757_;
		}
	}

	@OnlyIn(Dist.CLIENT)
	static class StencilFunc {
		public int func = 519;
		public int ref;
		public int mask = -1;
	}

	@OnlyIn(Dist.CLIENT)
	static class StencilState {
		public final StencilFunc func = new GlStateManager.StencilFunc();
		public int mask = -1;
		public int fail = 7680;
		public int zfail = 7680;
		public int zpass = 7680;
	}

	@OnlyIn(Dist.CLIENT)
	static class TextureState {
		public boolean enable;
		public int binding;
	}

	@OnlyIn(Dist.CLIENT)
	public static enum Viewport {
		INSTANCE;

		protected int x;
		protected int y;
		protected int width;
		protected int height;

		public static int x() {
			return INSTANCE.x;
		}

		public static int y() {
			return INSTANCE.y;
		}

		public static int width() {
			return INSTANCE.width;
		}

		public static int height() {
			return INSTANCE.height;
		}
	}

}
