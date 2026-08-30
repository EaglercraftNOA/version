package org.lwjgl.opengl;

public class ARBVertexShader {

	public static int glGetAttribLocationARB(int programObj, CharSequence name) {
		return GL20.glGetAttribLocation(programObj, name);
	}
}
