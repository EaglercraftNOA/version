package org.lwjgl.opengl;

import org.lwjgl.system.Callback;

public abstract class GLDebugMessageARBCallback extends Callback {

	@FunctionalInterface
	public interface CallbackI {
		void invoke(int source, int type, int id, int severity, int length, long message, long userParam);
	}

	private final CallbackI callback;

	protected GLDebugMessageARBCallback(CallbackI callback) {
		super(0L);
		this.callback = callback;
	}

	public void invoke(int source, int type, int id, int severity, int length, long message, long userParam) {
		if (callback != null) {
			callback.invoke(source, type, id, severity, length, message, userParam);
		}
	}

	public static GLDebugMessageARBCallback create(CallbackI cb) {
		return new GLDebugMessageARBCallback(cb) {};
	}
}
