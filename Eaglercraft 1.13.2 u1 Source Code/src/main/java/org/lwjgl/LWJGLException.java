package org.lwjgl;

public class LWJGLException extends Exception {
	private static final long serialVersionUID = 1L;

	public LWJGLException() {
		super();
	}

	public LWJGLException(String message) {
		super(message);
	}

	public LWJGLException(String message, Throwable cause) {
		super(message, cause);
	}

	public LWJGLException(Throwable cause) {
		super(cause);
	}
}
