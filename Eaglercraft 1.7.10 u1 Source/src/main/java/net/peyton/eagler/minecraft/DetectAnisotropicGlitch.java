package net.peyton.eagler.minecraft;

import static net.lax1dude.eaglercraft.opengl.RealOpenGLEnums.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.lax1dude.eaglercraft.Display;
import net.lax1dude.eaglercraft.EagRuntime;
import net.lax1dude.eaglercraft.internal.IBufferGL;
import net.lax1dude.eaglercraft.internal.IProgramGL;
import net.lax1dude.eaglercraft.internal.IShaderGL;
import net.lax1dude.eaglercraft.internal.IVertexArrayGL;
import net.lax1dude.eaglercraft.internal.PlatformOpenGL;
import net.lax1dude.eaglercraft.internal.buffer.ByteBuffer;
import net.lax1dude.eaglercraft.internal.buffer.FloatBuffer;
import net.lax1dude.eaglercraft.opengl.EaglercraftGPU;
import net.lax1dude.eaglercraft.opengl.GlStateManager;

public class DetectAnisotropicGlitch {

	private static Logger LOGGER = LogManager.getLogger("AnisotropicPatch");

	private static boolean hasDetected = false;
	private static boolean hasAngleIssue = false;

	public static boolean hasDetected() {
		return hasDetected;
	}

	public static boolean hasGlitch() {
		if (!hasDetected) {
			hasAngleIssue = detect();
			hasDetected = true;
		}
		return hasAngleIssue;
	}

	public static boolean detect() {
		if (!PlatformOpenGL.checkAnisotropicFilteringSupport()) {
			LOGGER.warn("Anisotropic filtering extension not found, cannot check for ANGLE issue #4994");
			LOGGER.warn("All client features related to this will be disabled");
			LOGGER.warn("This is not a bug, it is simply unsupported on your device, please do not report this!");
			return false;
		}

		String vshSrc = "#version 300 es\n" + "precision lowp float;" + "in vec2 a_pos;" + "out vec2 v_pos;"
				+ "void main() {" + "   gl_Position = vec4((v_pos = a_pos) * 2.0 - 1.0, 0.0, 1.0);" + "}";

		String fshSrc = "#version 300 es\n" + "precision lowp float;" + "precision lowp sampler2D;"
				+ "uniform sampler2D tex;" + "in vec2 v_pos;" + "out vec4 fragColor;" + "void main() {"
				+ "   fragColor = vec4(texture(tex, v_pos).rgb, 1.0);" + "}";

		IShaderGL vsh = PlatformOpenGL._wglCreateShader(GL_VERTEX_SHADER);
		PlatformOpenGL._wglShaderSource(vsh, vshSrc);
		PlatformOpenGL._wglCompileShader(vsh);

		if (PlatformOpenGL._wglGetShaderi(vsh, GL_COMPILE_STATUS) != GL_TRUE) {
			Display.checkContextLost();
			LOGGER.error("Could not check for ANGLE issue #4994, vertex shader did not compile:");
			LOGGER.error(PlatformOpenGL._wglGetShaderInfoLog(vsh));
			PlatformOpenGL._wglDeleteShader(vsh);
			return false;
		}

		IShaderGL fsh = PlatformOpenGL._wglCreateShader(GL_FRAGMENT_SHADER);
		PlatformOpenGL._wglShaderSource(fsh, fshSrc);
		PlatformOpenGL._wglCompileShader(fsh);

		if (PlatformOpenGL._wglGetShaderi(fsh, GL_COMPILE_STATUS) != GL_TRUE) {
			Display.checkContextLost();
			LOGGER.error("Could not check for ANGLE issue #4994, fragment shader did not compile:");
			LOGGER.error(PlatformOpenGL._wglGetShaderInfoLog(fsh));
			PlatformOpenGL._wglDeleteShader(vsh);
			PlatformOpenGL._wglDeleteShader(fsh);
			return false;
		}

		IProgramGL pr = PlatformOpenGL._wglCreateProgram();

		PlatformOpenGL._wglAttachShader(pr, vsh);
		PlatformOpenGL._wglAttachShader(pr, fsh);

		PlatformOpenGL._wglBindAttribLocation(pr, 0, "a_pos");
		PlatformOpenGL._wglBindAttribLocation(pr, 0, "fragColor");

		PlatformOpenGL._wglLinkProgram(pr);

		PlatformOpenGL._wglDetachShader(pr, vsh);
		PlatformOpenGL._wglDetachShader(pr, fsh);

		PlatformOpenGL._wglDeleteShader(vsh);
		PlatformOpenGL._wglDeleteShader(fsh);

		if (PlatformOpenGL._wglGetProgrami(pr, GL_LINK_STATUS) != GL_TRUE) {
			Display.checkContextLost();
			LOGGER.error("Could not check for ANGLE issue #4994, program did not link:");
			LOGGER.error(PlatformOpenGL._wglGetProgramInfoLog(pr));
			PlatformOpenGL._wglDeleteProgram(pr);
			return false;
		}

		EaglercraftGPU.bindGLShaderProgram(pr);

		PlatformOpenGL._wglUniform1i(PlatformOpenGL._wglGetUniformLocation(pr, "tex"), 0);

		byte x0 = (byte) 0x00;
		byte x1 = (byte) 0xFF;

		byte[] pixelsData = new byte[] { x0, x0, x0, x1, x0, x0, x0, x1, x1, x1, x1, x1, x0, x0, x0, x1, x0, x0, x0, x1,
				x0, x0, x0, x1, x1, x1, x1, x1, x0, x0, x0, x1, x0, x0, x0, x1, x0, x0, x0, x1, x1, x1, x1, x1, x0, x0,
				x0, x1 };

		ByteBuffer pixels = EagRuntime.allocateByteBuffer(pixelsData.length);
		pixels.put(pixelsData);
		pixels.rewind();

		int tex = GlStateManager.generateTexture();
		GlStateManager.bindTexture(tex);

		EaglercraftGPU.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		EaglercraftGPU.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
		EaglercraftGPU.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST_MIPMAP_LINEAR);
		EaglercraftGPU.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		EaglercraftGPU.glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAX_ANISOTROPY, 16.0f);

		EaglercraftGPU.glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, 4, 3, 0, GL_RGBA, GL_UNSIGNED_BYTE, pixels);

		PlatformOpenGL._wglGenerateMipmap(GL_TEXTURE_2D);

		float[] vertsData = new float[] { 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f };

		FloatBuffer verts = EagRuntime.allocateFloatBuffer(vertsData.length);
		verts.put(vertsData);
		verts.rewind();

		IBufferGL buf = EaglercraftGPU.createGLArrayBuffer();
		EaglercraftGPU.bindGLArrayBuffer(buf);
		PlatformOpenGL._wglBufferData(GL_ARRAY_BUFFER, verts, GL_STATIC_DRAW);

		IVertexArrayGL arr = EaglercraftGPU.createGLVertexArray();
		EaglercraftGPU.bindGLVertexArray(arr);

		EaglercraftGPU.enableVertexAttribArray(0);
		EaglercraftGPU.vertexAttribPointer(0, 2, GL_FLOAT, false, 8, 0);

		GlStateManager.viewport(0, 0, 400, 300);
		EaglercraftGPU.drawArrays(GL_TRIANGLES, 0, 6);

		EaglercraftGPU.destroyGLVertexArray(arr);
		EaglercraftGPU.destroyGLArrayBuffer(buf);
		GlStateManager.deleteTexture(tex);
		PlatformOpenGL._wglDeleteProgram(pr);

		ByteBuffer readPix = EagRuntime.allocateByteBuffer(4);
		EaglercraftGPU.glReadPixels(175, 150, 1, 1, GL_RGBA, GL_UNSIGNED_BYTE, readPix);

		boolean b = (readPix.get(0) + readPix.get(1) + readPix.get(2)) != 0;
		if (b) {
			LOGGER.info("ANGLE issue #4994 is not patched on this device, enabling anisotropic fix");
		}

		cleanup(); // to be safe

		return b;
	}

	private static void cleanup() {
		Display.checkContextLost(); // to be safe lol
		GlStateManager.clearColor(0.0f, 0.0f, 0.0f, 1.0f);
		GlStateManager.clear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		Display.update();
	}

}
