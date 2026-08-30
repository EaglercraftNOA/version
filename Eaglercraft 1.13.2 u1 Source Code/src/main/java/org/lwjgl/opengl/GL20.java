/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.opengl;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import net.lax1dude.eaglercraft.v1_8.internal.IProgramGL;
import net.lax1dude.eaglercraft.v1_8.internal.IShaderGL;
import net.lax1dude.eaglercraft.v1_8.internal.IUniformGL;
import net.lax1dude.eaglercraft.v1_8.internal.PlatformRuntime;
import net.lax1dude.eaglercraft.v1_8.opengl.EaglercraftGPU;

import static net.lax1dude.eaglercraft.v1_8.internal.PlatformOpenGL.*;

public class GL20 extends GL15 {

	public static final int GL_FRAGMENT_SHADER = 0x8B30;
	public static final int GL_VERTEX_SHADER = 0x8B31;
	public static final int GL_MAX_VERTEX_ATTRIBS = 0x8869;
	public static final int GL_MAX_VERTEX_UNIFORM_COMPONENTS = 0x8B4A;
	public static final int GL_MAX_VARYING_FLOATS = 0x8B4B;
	public static final int GL_MAX_VERTEX_TEXTURE_IMAGE_UNITS = 0x8B4C;
	public static final int GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS = 0x8B4D;
	public static final int GL_MAX_TEXTURE_IMAGE_UNITS = 0x8872;
	public static final int GL_MAX_TEXTURE_COORDS = 0x8871;
	public static final int GL_MAX_FRAGMENT_UNIFORM_COMPONENTS = 0x8B49;
	public static final int GL_SHADER_TYPE = 0x8B4F;
	public static final int GL_DELETE_STATUS = 0x8B80;
	public static final int GL_COMPILE_STATUS = 0x8B81;
	public static final int GL_LINK_STATUS = 0x8B82;
	public static final int GL_VALIDATE_STATUS = 0x8B83;
	public static final int GL_INFO_LOG_LENGTH = 0x8B84;
	public static final int GL_ATTACHED_SHADERS = 0x8B85;
	public static final int GL_ACTIVE_UNIFORMS = 0x8B86;
	public static final int GL_ACTIVE_UNIFORM_MAX_LENGTH = 0x8B87;
	public static final int GL_ACTIVE_ATTRIBUTES = 0x8B89;
	public static final int GL_ACTIVE_ATTRIBUTE_MAX_LENGTH = 0x8B8A;
	public static final int GL_SHADING_LANGUAGE_VERSION = 0x8B8C;
	public static final int GL_CURRENT_PROGRAM = 0x8B8D;
	public static final int GL_FLOAT_VEC2 = 0x8B50;
	public static final int GL_FLOAT_VEC3 = 0x8B51;
	public static final int GL_FLOAT_VEC4 = 0x8B52;
	public static final int GL_INT_VEC2 = 0x8B53;
	public static final int GL_INT_VEC3 = 0x8B54;
	public static final int GL_INT_VEC4 = 0x8B55;
	public static final int GL_BOOL = 0x8B56;
	public static final int GL_BOOL_VEC2 = 0x8B57;
	public static final int GL_BOOL_VEC3 = 0x8B58;
	public static final int GL_BOOL_VEC4 = 0x8B59;
	public static final int GL_FLOAT_MAT2 = 0x8B5A;
	public static final int GL_FLOAT_MAT3 = 0x8B5B;
	public static final int GL_FLOAT_MAT4 = 0x8B5C;
	public static final int GL_SAMPLER_1D = 0x8B5D;
	public static final int GL_SAMPLER_2D = 0x8B5E;
	public static final int GL_SAMPLER_3D = 0x8B5F;
	public static final int GL_SAMPLER_CUBE = 0x8B60;
	public static final int GL_SAMPLER_1D_SHADOW = 0x8B61;
	public static final int GL_SAMPLER_2D_SHADOW = 0x8B62;
	public static final int GL_MAX_DRAW_BUFFERS = 0x8824;
	public static final int GL_DRAW_BUFFER0 = 0x8825;
	public static final int GL_DRAW_BUFFER1 = 0x8826;
	public static final int GL_DRAW_BUFFER2 = 0x8827;
	public static final int GL_DRAW_BUFFER3 = 0x8828;
	public static final int GL_DRAW_BUFFER4 = 0x8829;
	public static final int GL_DRAW_BUFFER5 = 0x882A;
	public static final int GL_DRAW_BUFFER6 = 0x882B;
	public static final int GL_DRAW_BUFFER7 = 0x882C;
	public static final int GL_DRAW_BUFFER8 = 0x882D;
	public static final int GL_DRAW_BUFFER9 = 0x882E;
	public static final int GL_DRAW_BUFFER10 = 0x882F;
	public static final int GL_DRAW_BUFFER11 = 0x8830;
	public static final int GL_DRAW_BUFFER12 = 0x8831;
	public static final int GL_DRAW_BUFFER13 = 0x8832;
	public static final int GL_DRAW_BUFFER14 = 0x8833;
	public static final int GL_DRAW_BUFFER15 = 0x8834;
	public static final int GL_BLEND_EQUATION_RGB = 0x8009;
	public static final int GL_BLEND_EQUATION_ALPHA = 0x883D;
	public static final int GL_STENCIL_BACK_FUNC = 0x8800;
	public static final int GL_STENCIL_BACK_FAIL = 0x8801;
	public static final int GL_STENCIL_BACK_PASS_DEPTH_FAIL = 0x8802;
	public static final int GL_STENCIL_BACK_PASS_DEPTH_PASS = 0x8803;
	public static final int GL_STENCIL_BACK_REF = 0x8CA3;
	public static final int GL_STENCIL_BACK_VALUE_MASK = 0x8CA4;
	public static final int GL_STENCIL_BACK_WRITEMASK = 0x8CA5;
	public static final int GL_UPPER_LEFT = 0x8CA2;
	public static final int GL_LOWER_LEFT = 0x8CA1;

	private static final AtomicInteger NEXT_SHADER_ID = new AtomicInteger(1);
	private static final AtomicInteger NEXT_PROGRAM_ID = new AtomicInteger(1);
	private static final AtomicInteger NEXT_UNIFORM_ID = new AtomicInteger(1);

	private static final Map<Integer, IShaderGL> SHADERS = new HashMap<>();
	private static final Map<Integer, Integer> SHADER_TYPES = new HashMap<>();
	private static final Map<Integer, IProgramGL> PROGRAMS = new HashMap<>();
	private static final Map<Integer, IUniformGL> UNIFORMS = new HashMap<>();

	private static IShaderGL shader(int id) {
		return SHADERS.get(id);
	}

	private static IProgramGL program(int id) {
		return id == 0 ? null : PROGRAMS.get(id);
	}

	private static IUniformGL uniform(int id) {
		return UNIFORMS.get(id);
	}

	private static String trimInfoLog(String s, int maxLength) {
		if (s == null) {
			return "";
		}
		if (maxLength > 0 && s.length() > maxLength) {
			return s.substring(0, maxLength);
		}
		return s;
	}

	private static net.lax1dude.eaglercraft.v1_8.internal.buffer.FloatBuffer copyFloatBuffer(FloatBuffer src) {
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

	public static int glCreateShader(int type) {
		int id = NEXT_SHADER_ID.getAndIncrement();
		SHADERS.put(id, _wglCreateShader(type));
		SHADER_TYPES.put(id, type);
		return id;
	}

	public static void glDeleteShader(int shader) {
		IShaderGL obj = SHADERS.remove(shader);
		SHADER_TYPES.remove(shader);
		if (obj != null) {
			_wglDeleteShader(obj);
		}
	}

	public static void glShaderSource(int shader, CharSequence source) {
		IShaderGL obj = shader(shader);
		if (obj != null) {
			String src = source == null ? "" : source.toString();
			_wglShaderSource(obj, translateDesktopGLSLToGLES(src, SHADER_TYPES.getOrDefault(shader, 0)));
		}
	}

	/**
	 * Minecraft 1.17 ships shaders written in desktop GLSL (e.g. {@code #version 150}).
	 * The Eaglercraft desktop runtime renders via OpenGL ES 3.0 through ANGLE, which
	 * only accepts the GLES dialect. This rewrites the version directive and injects
	 * the precision qualifiers GLES requires.
	 *
	 * <p>The translation is intentionally narrow: it only fires when the source
	 * starts with a desktop {@code #version} directive that is not already an
	 * {@code es} profile. Sources already targeting GLES (e.g. Eaglercraft's own
	 * shaders prepared by {@code GLSLHeader}) pass through unchanged.</p>
	 */
	private static String translateDesktopGLSLToGLES(String src, int shaderType) {
		if (src == null || src.isEmpty()) {
			return src;
		}
		int versionIdx = src.indexOf("#version");
		if (versionIdx < 0) {
			return src;
		}
		int lineEnd = src.indexOf('\n', versionIdx);
		if (lineEnd < 0) {
			lineEnd = src.length();
		}
		String versionLine = src.substring(versionIdx, lineEnd);
		if (versionLine.contains(" es")) {
			return src;
		}
		int numStart = versionIdx + "#version".length();
		while (numStart < lineEnd && Character.isWhitespace(src.charAt(numStart))) {
			numStart++;
		}
		int numEnd = numStart;
		while (numEnd < lineEnd && Character.isDigit(src.charAt(numEnd))) {
			numEnd++;
		}
		int versionNum;
		try {
			versionNum = Integer.parseInt(src.substring(numStart, numEnd));
		} catch (NumberFormatException ex) {
			return src;
		}
		String esVersion;
		boolean precision330 = false;
		if (versionNum >= 330) {
			esVersion = "300 es";
			precision330 = true;
		} else if (versionNum >= 140) {
			esVersion = "300 es";
		} else {
			esVersion = "100";
		}
		src = patchDesktopGLSLForGLES(src, shaderType);
		StringBuilder sb = new StringBuilder(src.length() + 256);
		sb.append(src, 0, versionIdx);
		sb.append("#version ").append(esVersion).append('\n');
		if (esVersion.endsWith(" es")) {
			sb.append("precision highp float;\n");
			sb.append("precision highp int;\n");
			sb.append("precision highp sampler2D;\n");
			sb.append("precision highp sampler2DArray;\n");
			sb.append("precision highp sampler3D;\n");
			sb.append("precision highp samplerCube;\n");
			if (precision330) {
				sb.append("precision highp sampler2DShadow;\n");
			}
		}
		sb.append("// ").append(versionLine).append('\n');
		sb.append(src, lineEnd + (lineEnd < src.length() ? 1 : 0), src.length());
		return sb.toString();
	}

	private static String patchDesktopGLSLForGLES(String src, int shaderType) {
		src = patchDesktopGLSLLines(src, GL20::patchDesktopGLSLLineForGLES);
		if (shaderType == GL_FRAGMENT_SHADER && src.indexOf("layout(location") < 0) {
			src = patchDesktopGLSLLines(src, GL20::patchDesktopGLSLFragmentLineForGLES);
		}
		return src;
	}

	private static String patchDesktopGLSLLineForGLES(String line) {
		String trimmed = line.trim();
		if ("vec4 minecraft_sample_lightmap(sampler2D lightMap, ivec2 uv) {".equals(trimmed)) {
			return withLeadingWhitespace(line, "vec4 minecraft_sample_lightmap(sampler2D lightMap, vec2 uv) {");
		}
		if ("return texture(lightMap, clamp(uv / 256.0, vec2(0.5 / 16.0), vec2(15.5 / 16.0)));".equals(trimmed)) {
			return withLeadingWhitespace(line, "return texture(lightMap, clamp((uv + vec2(8.0)) / 256.0, vec2(0.5 / 16.0), vec2(15.5 / 16.0)));");
		}
		if ("lightMapColor = texelFetch(Sampler2, UV2 / 16, 0);".equals(trimmed)) {
			return withLeadingWhitespace(line, "lightMapColor = " + sampleLightmapFromSampler2UV2() + ";");
		}
		if ("overlayColor = texelFetch(Sampler1, UV1, 0);".equals(trimmed)) {
			return withLeadingWhitespace(line, "overlayColor = texelFetch(Sampler1, ivec2(UV1), 0);");
		}
		if ("vertexColor = Color * texelFetch(Sampler2, UV2 / 16, 0);".equals(trimmed)) {
			return withLeadingWhitespace(line, "vertexColor = Color * " + sampleLightmapFromSampler2UV2() + ";");
		}
		if ("vertexColor = Color * ColorModulator * texelFetch(Sampler2, UV2 / 16, 0);".equals(trimmed)) {
			return withLeadingWhitespace(line, "vertexColor = Color * ColorModulator * " + sampleLightmapFromSampler2UV2() + ";");
		}
		if ("vertexColor = minecraft_mix_light(Light0_Direction, Light1_Direction, Normal, Color) * texelFetch(Sampler2, UV2 / 16, 0);".equals(trimmed)) {
			return withLeadingWhitespace(line, "vertexColor = minecraft_mix_light(Light0_Direction, Light1_Direction, Normal, Color) * " + sampleLightmapFromSampler2UV2() + ";");
		}
		if ("in ivec2 UV1;".equals(trimmed)) {
			return withLeadingWhitespace(line, "in vec2 UV1;");
		}
		if ("in ivec2 UV2;".equals(trimmed)) {
			return withLeadingWhitespace(line, "in vec2 UV2;");
		}
		if ("in vec2 UV1;".equals(trimmed)) {
			return withLeadingWhitespace(line, "in vec2 UV1;");
		}
		if ("in vec2 UV2;".equals(trimmed)) {
			return withLeadingWhitespace(line, "in vec2 UV2;");
		}
		if ("attribute ivec2 UV1;".equals(trimmed)) {
			return withLeadingWhitespace(line, "attribute vec2 UV1;");
		}
		if ("attribute ivec2 UV2;".equals(trimmed)) {
			return withLeadingWhitespace(line, "attribute vec2 UV2;");
		}
		if ("attribute vec2 UV1;".equals(trimmed)) {
			return withLeadingWhitespace(line, "attribute vec2 UV1;");
		}
		if ("attribute vec2 UV2;".equals(trimmed)) {
			return withLeadingWhitespace(line, "attribute vec2 UV2;");
		}
		if ("texCoord1 = UV1;".equals(trimmed)) {
			return withLeadingWhitespace(line, "texCoord1 = UV1;");
		}
		if ("texCoord2 = UV2;".equals(trimmed)) {
			return withLeadingWhitespace(line, "texCoord2 = clamp((UV2 + vec2(8.0)) / 256.0, vec2(0.5 / 16.0), vec2(15.5 / 16.0));");
		}
		return line;
	}

	private static String sampleLightmapFromSampler2UV2() {
		return "texture(Sampler2, clamp((UV2 + vec2(8.0)) / 256.0, vec2(0.5 / 16.0), vec2(15.5 / 16.0)))";
	}

	private static String patchDesktopGLSLFragmentLineForGLES(String line) {
		return "out vec4 fragColor;".equals(line.trim())
				? withLeadingWhitespace(line, "layout(location = 0) out vec4 fragColor;")
				: line;
	}

	private static String patchDesktopGLSLLines(String src, GLSLLinePatcher patcher) {
		StringBuilder patchedSrc = null;
		int lineStart = 0;
		for (int i = 0; i <= src.length(); ++i) {
			if (i == src.length() || src.charAt(i) == '\n') {
				int lineEnd = i;
				int contentEnd = lineEnd;
				if (contentEnd > lineStart && src.charAt(contentEnd - 1) == '\r') {
					--contentEnd;
				}

				String line = src.substring(lineStart, contentEnd);
				String patchedLine = patcher.patch(line);
				if (patchedSrc == null && !line.equals(patchedLine)) {
					patchedSrc = new StringBuilder(src.length() + 128);
					patchedSrc.append(src, 0, lineStart);
				}

				if (patchedSrc != null) {
					patchedSrc.append(patchedLine);
					if (contentEnd < lineEnd) {
						patchedSrc.append('\r');
					}
					if (i < src.length()) {
						patchedSrc.append('\n');
					}
				}
				lineStart = i + 1;
			}
		}
		return patchedSrc == null ? src : patchedSrc.toString();
	}

	private static String withLeadingWhitespace(String line, String patchedLine) {
		int i = 0;
		while (i < line.length() && Character.isWhitespace(line.charAt(i))) {
			++i;
		}
		return i == 0 ? patchedLine : line.substring(0, i) + patchedLine;
	}

	@FunctionalInterface
	private static interface GLSLLinePatcher {
		String patch(String line);
	}

	public static void glCompileShader(int shader) {
		IShaderGL obj = shader(shader);
		if (obj != null) {
			_wglCompileShader(obj);
		}
	}

	public static int glGetShaderi(int shader, int pname) {
		IShaderGL obj = shader(shader);
		return obj != null ? _wglGetShaderi(obj, pname) : 0;
	}

	public static String glGetShaderInfoLog(int shader, int maxLength) {
		IShaderGL obj = shader(shader);
		return trimInfoLog(obj != null ? _wglGetShaderInfoLog(obj) : "", maxLength);
	}

	public static int glCreateProgram() {
		int id = NEXT_PROGRAM_ID.getAndIncrement();
		PROGRAMS.put(id, _wglCreateProgram());
		return id;
	}

	public static void glDeleteProgram(int program) {
		IProgramGL obj = PROGRAMS.remove(program);
		if (obj != null) {
			_wglDeleteProgram(obj);
		}
	}

	public static void glAttachShader(int program, int shader) {
		IProgramGL prg = program(program);
		IShaderGL sh = shader(shader);
		if (prg != null && sh != null) {
			_wglAttachShader(prg, sh);
		}
	}

	public static void glLinkProgram(int program) {
		IProgramGL prg = program(program);
		if (prg != null) {
			_wglLinkProgram(prg);
		}
	}

	public static int glGetProgrami(int program, int pname) {
		IProgramGL prg = program(program);
		return prg != null ? _wglGetProgrami(prg, pname) : 0;
	}

	public static String glGetProgramInfoLog(int program, int maxLength) {
		IProgramGL prg = program(program);
		return trimInfoLog(prg != null ? _wglGetProgramInfoLog(prg) : "", maxLength);
	}

	public static void glUseProgram(int program) {
		EaglercraftGPU.bindGLShaderProgram(program(program));
	}

	public static void glBindAttribLocation(int program, int index, CharSequence name) {
		IProgramGL prg = program(program);
		if (prg != null) {
			_wglBindAttribLocation(prg, index, name == null ? "" : name.toString());
		}
	}

	public static int glGetUniformLocation(int program, CharSequence name) {
		IProgramGL prg = program(program);
		if (prg == null) {
			return -1;
		}
		IUniformGL u = _wglGetUniformLocation(prg, name == null ? "" : name.toString());
		if (u == null) {
			return -1;
		}
		int id = NEXT_UNIFORM_ID.getAndIncrement();
		UNIFORMS.put(id, u);
		return id;
	}

	public static int glGetAttribLocation(int program, CharSequence name) {
		IProgramGL prg = program(program);
		return prg != null ? _wglGetAttribLocation(prg, name == null ? "" : name.toString()) : -1;
	}

	public static void glScissor(int x, int y, int width, int height) {
		_wglScissor(x, y, width, height);
	}

	public static void glVertexAttribPointer(int index, int size, int type, boolean normalized, int stride, long pointer) {
		EaglercraftGPU.vertexAttribPointer(index, size, type, normalized, stride, (int) pointer);
	}

	public static void glEnableVertexAttribArray(int index) {
		EaglercraftGPU.enableVertexAttribArray(index);
	}

	public static void glDisableVertexAttribArray(int index) {
		EaglercraftGPU.disableVertexAttribArray(index);
	}

	public static void glUniform1i(int location, int v0) {
		_wglUniform1i(uniform(location), v0);
	}

	public static void glUniform1iv(int location, IntBuffer value) {
		if (value != null && value.remaining() > 0) {
			_wglUniform1i(uniform(location), value.get(value.position()));
		}
	}

	public static void glUniform1fv(int location, FloatBuffer value) {
		if (value != null && value.remaining() > 0) {
			_wglUniform1f(uniform(location), value.get(value.position()));
		}
	}

	public static void glUniform2iv(int location, IntBuffer value) {
		if (value != null && value.remaining() >= 2) {
			int p = value.position();
			_wglUniform2i(uniform(location), value.get(p), value.get(p + 1));
		}
	}

	public static void glUniform2fv(int location, FloatBuffer value) {
		if (value != null && value.remaining() >= 2) {
			int p = value.position();
			_wglUniform2f(uniform(location), value.get(p), value.get(p + 1));
		}
	}

	public static void glUniform3iv(int location, IntBuffer value) {
		if (value != null && value.remaining() >= 3) {
			int p = value.position();
			_wglUniform3i(uniform(location), value.get(p), value.get(p + 1), value.get(p + 2));
		}
	}

	public static void glUniform3fv(int location, FloatBuffer value) {
		if (value != null && value.remaining() >= 3) {
			int p = value.position();
			_wglUniform3f(uniform(location), value.get(p), value.get(p + 1), value.get(p + 2));
		}
	}

	public static void glUniform4iv(int location, IntBuffer value) {
		if (value != null && value.remaining() >= 4) {
			int p = value.position();
			_wglUniform4i(uniform(location), value.get(p), value.get(p + 1), value.get(p + 2), value.get(p + 3));
		}
	}

	public static void glUniform4fv(int location, FloatBuffer value) {
		if (value != null && value.remaining() >= 4) {
			int p = value.position();
			_wglUniform4f(uniform(location), value.get(p), value.get(p + 1), value.get(p + 2), value.get(p + 3));
		}
	}

	public static void glUniform1ivDirect(int location, net.lax1dude.eaglercraft.v1_8.internal.buffer.IntBuffer value) {
		if (value != null && value.remaining() > 0) {
			_wglUniform1i(uniform(location), value.get(value.position()));
		}
	}

	public static void glUniform1fvDirect(int location, net.lax1dude.eaglercraft.v1_8.internal.buffer.FloatBuffer value) {
		if (value != null && value.remaining() > 0) {
			_wglUniform1f(uniform(location), value.get(value.position()));
		}
	}

	public static void glUniform2ivDirect(int location, net.lax1dude.eaglercraft.v1_8.internal.buffer.IntBuffer value) {
		if (value != null && value.remaining() >= 2) {
			int p = value.position();
			_wglUniform2i(uniform(location), value.get(p), value.get(p + 1));
		}
	}

	public static void glUniform2fvDirect(int location, net.lax1dude.eaglercraft.v1_8.internal.buffer.FloatBuffer value) {
		if (value != null && value.remaining() >= 2) {
			int p = value.position();
			_wglUniform2f(uniform(location), value.get(p), value.get(p + 1));
		}
	}

	public static void glUniform3ivDirect(int location, net.lax1dude.eaglercraft.v1_8.internal.buffer.IntBuffer value) {
		if (value != null && value.remaining() >= 3) {
			int p = value.position();
			_wglUniform3i(uniform(location), value.get(p), value.get(p + 1), value.get(p + 2));
		}
	}

	public static void glUniform3fvDirect(int location, net.lax1dude.eaglercraft.v1_8.internal.buffer.FloatBuffer value) {
		if (value != null && value.remaining() >= 3) {
			int p = value.position();
			_wglUniform3f(uniform(location), value.get(p), value.get(p + 1), value.get(p + 2));
		}
	}

	public static void glUniform4ivDirect(int location, net.lax1dude.eaglercraft.v1_8.internal.buffer.IntBuffer value) {
		if (value != null && value.remaining() >= 4) {
			int p = value.position();
			_wglUniform4i(uniform(location), value.get(p), value.get(p + 1), value.get(p + 2), value.get(p + 3));
		}
	}

	public static void glUniform4fvDirect(int location, net.lax1dude.eaglercraft.v1_8.internal.buffer.FloatBuffer value) {
		if (value != null && value.remaining() >= 4) {
			int p = value.position();
			_wglUniform4f(uniform(location), value.get(p), value.get(p + 1), value.get(p + 2), value.get(p + 3));
		}
	}

	public static void glUniformMatrix2fv(int location, boolean transpose, FloatBuffer value) {
		net.lax1dude.eaglercraft.v1_8.internal.buffer.FloatBuffer copy = copyFloatBuffer(value);
		try {
			_wglUniformMatrix2fv(uniform(location), transpose, copy);
		} finally {
			if (copy != null) {
				PlatformRuntime.freeFloatBuffer(copy);
			}
		}
	}

	public static void glUniformMatrix3fv(int location, boolean transpose, FloatBuffer value) {
		net.lax1dude.eaglercraft.v1_8.internal.buffer.FloatBuffer copy = copyFloatBuffer(value);
		try {
			_wglUniformMatrix3fv(uniform(location), transpose, copy);
		} finally {
			if (copy != null) {
				PlatformRuntime.freeFloatBuffer(copy);
			}
		}
	}

	public static void glUniformMatrix4fv(int location, boolean transpose, FloatBuffer value) {
		net.lax1dude.eaglercraft.v1_8.internal.buffer.FloatBuffer copy = copyFloatBuffer(value);
		try {
			_wglUniformMatrix4fv(uniform(location), transpose, copy);
		} finally {
			if (copy != null) {
				PlatformRuntime.freeFloatBuffer(copy);
			}
		}
	}

	public static void glUniformMatrix2fvDirect(int location, boolean transpose,
			net.lax1dude.eaglercraft.v1_8.internal.buffer.FloatBuffer value) {
		_wglUniformMatrix2fv(uniform(location), transpose, value);
	}

	public static void glUniformMatrix3fvDirect(int location, boolean transpose,
			net.lax1dude.eaglercraft.v1_8.internal.buffer.FloatBuffer value) {
		_wglUniformMatrix3fv(uniform(location), transpose, value);
	}

	public static void glUniformMatrix4fvDirect(int location, boolean transpose,
			net.lax1dude.eaglercraft.v1_8.internal.buffer.FloatBuffer value) {
		_wglUniformMatrix4fv(uniform(location), transpose, value);
	}
}
