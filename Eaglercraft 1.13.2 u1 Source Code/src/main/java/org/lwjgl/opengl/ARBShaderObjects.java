package org.lwjgl.opengl;

public class ARBShaderObjects {

	public static int glGetObjectParameteriARB(int obj, int pname) {
		if (pname == GL20.GL_COMPILE_STATUS || pname == GL20.GL_SHADER_TYPE || pname == GL20.GL_INFO_LOG_LENGTH) {
			return GL20.glGetShaderi(obj, pname);
		}
		return GL20.glGetProgrami(obj, pname);
	}

	public static void glAttachObjectARB(int programObj, int shaderObj) {
		GL20.glAttachShader(programObj, shaderObj);
	}

	public static void glDeleteObjectARB(int obj) {
		GL20.glDeleteProgram(obj);
		GL20.glDeleteShader(obj);
	}

	public static int glCreateShaderObjectARB(int shaderType) {
		return GL20.glCreateShader(shaderType);
	}

	public static void glShaderSourceARB(int shaderObj, CharSequence source) {
		GL20.glShaderSource(shaderObj, source);
	}

	public static void glCompileShaderARB(int shaderObj) {
		GL20.glCompileShader(shaderObj);
	}

	public static String glGetInfoLogARB(int obj, int maxLength) {
		String shaderLog = GL20.glGetShaderInfoLog(obj, maxLength);
		if (shaderLog != null && !shaderLog.isEmpty()) {
			return shaderLog;
		}
		return GL20.glGetProgramInfoLog(obj, maxLength);
	}

	public static void glUseProgramObjectARB(int programObj) {
		GL20.glUseProgram(programObj);
	}

	public static int glCreateProgramObjectARB() {
		return GL20.glCreateProgram();
	}

	public static void glLinkProgramARB(int programObj) {
		GL20.glLinkProgram(programObj);
	}

	public static int glGetUniformLocationARB(int programObj, CharSequence name) {
		return GL20.glGetUniformLocation(programObj, name);
	}

	public static void glUniform1ivARB(int location, java.nio.IntBuffer value) {
		GL20.glUniform1iv(location, value);
	}

	public static void glUniform1iARB(int location, int value) {
		GL20.glUniform1i(location, value);
	}

	public static void glUniform1fvARB(int location, java.nio.FloatBuffer value) {
		GL20.glUniform1fv(location, value);
	}

	public static void glUniform2ivARB(int location, java.nio.IntBuffer value) {
		GL20.glUniform2iv(location, value);
	}

	public static void glUniform2fvARB(int location, java.nio.FloatBuffer value) {
		GL20.glUniform2fv(location, value);
	}

	public static void glUniform3ivARB(int location, java.nio.IntBuffer value) {
		GL20.glUniform3iv(location, value);
	}

	public static void glUniform3fvARB(int location, java.nio.FloatBuffer value) {
		GL20.glUniform3fv(location, value);
	}

	public static void glUniform4ivARB(int location, java.nio.IntBuffer value) {
		GL20.glUniform4iv(location, value);
	}

	public static void glUniform4fvARB(int location, java.nio.FloatBuffer value) {
		GL20.glUniform4fv(location, value);
	}

	public static void glUniformMatrix2fvARB(int location, boolean transpose, java.nio.FloatBuffer value) {
		GL20.glUniformMatrix2fv(location, transpose, value);
	}

	public static void glUniformMatrix3fvARB(int location, boolean transpose, java.nio.FloatBuffer value) {
		GL20.glUniformMatrix3fv(location, transpose, value);
	}

	public static void glUniformMatrix4fvARB(int location, boolean transpose, java.nio.FloatBuffer value) {
		GL20.glUniformMatrix4fv(location, transpose, value);
	}
}
