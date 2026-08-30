package org.lwjgl.opengl;

public class EXTFramebufferObject {

	public static void glBindFramebufferEXT(int target, int framebuffer) {
		GL30.glBindFramebuffer(target, framebuffer);
	}

	public static void glBindRenderbufferEXT(int target, int renderbuffer) {
		GL30.glBindRenderbuffer(target, renderbuffer);
	}

	public static void glDeleteRenderbuffersEXT(int renderbuffer) {
		GL30.glDeleteRenderbuffers(renderbuffer);
	}

	public static void glDeleteFramebuffersEXT(int framebuffer) {
		GL30.glDeleteFramebuffers(framebuffer);
	}

	public static int glGenFramebuffersEXT() {
		return GL30.glGenFramebuffers();
	}

	public static int glGenRenderbuffersEXT() {
		return GL30.glGenRenderbuffers();
	}

	public static void glRenderbufferStorageEXT(int target, int internalformat, int width, int height) {
		GL30.glRenderbufferStorage(target, internalformat, width, height);
	}

	public static void glFramebufferRenderbufferEXT(int target, int attachment, int renderbufferTarget, int renderbuffer) {
		GL30.glFramebufferRenderbuffer(target, attachment, renderbufferTarget, renderbuffer);
	}

	public static int glCheckFramebufferStatusEXT(int target) {
		return GL30.glCheckFramebufferStatus(target);
	}

	public static void glFramebufferTexture2DEXT(int target, int attachment, int texTarget, int texture, int level) {
		GL30.glFramebufferTexture2D(target, attachment, texTarget, texture, level);
	}
}
