package org.lwjgl.opengl;

import org.lwjgl.system.MemoryUtil;
import org.lwjgl.system.Callback;

public abstract class GLDebugMessageCallback extends Callback {

	@FunctionalInterface
	public interface CallbackI {
		void invoke(int source, int type, int id, int severity, int length, long message, long userParam);
	}

	private final CallbackI callback;

	protected GLDebugMessageCallback(CallbackI callback) {
		super(0L);
		this.callback = callback;
	}

	public void invoke(int source, int type, int id, int severity, int length, long message, long userParam) {
		if (callback != null) {
			callback.invoke(source, type, id, severity, length, message, userParam);
		}
	}

	public static GLDebugMessageCallback create(CallbackI cb) {
		return new GLDebugMessageCallback(cb) {};
	}

	public static String getMessage(int length, long message) {
		if (message == 0L) {
			return "";
		}
		return MemoryUtil.memUTF8(message);
	}
}
