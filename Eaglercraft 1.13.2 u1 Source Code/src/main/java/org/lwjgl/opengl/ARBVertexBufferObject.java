package org.lwjgl.opengl;

public class ARBVertexBufferObject {

	public static int glGenBuffersARB() {
		return GL15.glGenBuffers();
	}

	public static void glGenBuffersARB(java.nio.IntBuffer buffers) {
		GL15.glGenBuffers(buffers);
	}

	public static void glBindBufferARB(int target, int buffer) {
		GL15.glBindBuffer(target, buffer);
	}

	public static void glBufferDataARB(int target, java.nio.ByteBuffer data, int usage) {
		GL15.glBufferData(target, data, usage);
	}

	public static void glBufferDataARB(int target, net.lax1dude.eaglercraft.v1_8.internal.buffer.ByteBuffer data, int usage) {
		GL15.glBufferData(target, data, usage);
	}

	public static void glDeleteBuffersARB(int buffer) {
		GL15.glDeleteBuffers(buffer);
	}

	public static void glDeleteBuffersARB(java.nio.IntBuffer buffers) {
		GL15.glDeleteBuffers(buffers);
	}
}
