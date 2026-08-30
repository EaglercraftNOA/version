/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.glfw;

import java.nio.ByteBuffer;

import org.lwjgl.system.MemoryStack;

/**
 * Image data for window icons and cursors.
 */
public class GLFWImage {

	private int width;
	private int height;
	private ByteBuffer pixels;

	public GLFWImage() {
	}

	public GLFWImage(int width, int height, ByteBuffer pixels) {
		this.width = width;
		this.height = height;
		this.pixels = pixels;
	}

	public int width() { return width; }
	public int height() { return height; }
	public ByteBuffer pixels() { return pixels; }

	public GLFWImage set(int width, int height, ByteBuffer pixels) {
		this.width = width;
		this.height = height;
		this.pixels = pixels;
		return this;
	}

	public GLFWImage width(int width) { this.width = width; return this; }
	public GLFWImage height(int height) { this.height = height; return this; }
	public GLFWImage pixels(ByteBuffer pixels) { this.pixels = pixels; return this; }

	public static Buffer mallocStack(int capacity, MemoryStack stack) {
		return new Buffer(capacity);
	}

	public static Buffer malloc(int capacity, MemoryStack stack) {
		return mallocStack(capacity, stack);
	}

	/**
	 * A buffer of GLFWImage structs.
	 */
	public static class Buffer {
		private final GLFWImage[] images;
		private int position;

		public Buffer(int capacity) {
			this.images = new GLFWImage[capacity];
			for (int i = 0; i < capacity; i++) {
				this.images[i] = new GLFWImage();
			}
			this.position = 0;
		}

		public Buffer(GLFWImage[] images) {
			this.images = images;
			this.position = 0;
		}

		public int remaining() {
			return images.length - position;
		}

		public int capacity() {
			return images.length;
		}

		public GLFWImage get() {
			return images[position++];
		}

		public GLFWImage get(int index) {
			return images[index];
		}

		public Buffer put(GLFWImage image) {
			images[position++] = image;
			return this;
		}

		public Buffer put(int index, GLFWImage image) {
			images[index] = image;
			return this;
		}

		public Buffer position(int pos) {
			this.position = pos;
			return this;
		}

		public int position() {
			return position;
		}

		public Buffer width(int width) {
			images[position].width(width);
			return this;
		}

		public Buffer height(int height) {
			images[position].height(height);
			return this;
		}

		public Buffer pixels(ByteBuffer pixels) {
			images[position].pixels(pixels);
			return this;
		}
	}
}
