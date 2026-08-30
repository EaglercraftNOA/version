/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.system;

public interface NativeResource extends AutoCloseable {

	/** Frees this native resource. */
	void free();

	static void safeFree(NativeResource resource) {
		if (resource != null) {
			resource.free();
		}
	}

	@Override
	default void close() {
		safeFree(this);
	}
}
