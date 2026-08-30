package org.lwjgl.opengl;

public class ARBFramebufferObject {

	public static void glBindFramebuffer(int target, int framebuffer) {
		GL30.glBindFramebuffer(target, framebuffer);
	}

	public static void glBindRenderbuffer(int target, int renderbuffer) {
		GL30.glBindRenderbuffer(target, renderbuffer);
	}

	public static void glDeleteRenderbuffers(int renderbuffer) {
		GL30.glDeleteRenderbuffers(renderbuffer);
	}

	public static void glDeleteFramebuffers(int framebuffer) {
		GL30.glDeleteFramebuffers(framebuffer);
	}

	public static int glGenFramebuffers() {
		return GL30.glGenFramebuffers();
	}

	public static int glGenRenderbuffers() {
		return GL30.glGenRenderbuffers();
	}

	public static void glRenderbufferStorage(int target, int internalformat, int width, int height) {
		GL30.glRenderbufferStorage(target, internalformat, width, height);
	}

	public static void glFramebufferRenderbuffer(int target, int attachment, int renderbufferTarget, int renderbuffer) {
		GL30.glFramebufferRenderbuffer(target, attachment, renderbufferTarget, renderbuffer);
	}

	public static int glCheckFramebufferStatus(int target) {
		return GL30.glCheckFramebufferStatus(target);
	}

	public static void glFramebufferTexture2D(int target, int attachment, int texTarget, int texture, int level) {
		GL30.glFramebufferTexture2D(target, attachment, texTarget, texture, level);
	}
}
