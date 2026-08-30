/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.system;

public interface Pointer {

	/** The pointer size in bytes, fixed to 4 for TeaVM/WASM environment. */
	int POINTER_SIZE = 4;

	/** The pointer size power-of-two shift (log2 of POINTER_SIZE). */
	int POINTER_SHIFT = 2;

	/** The raw pointer address (or handle). */
	long address();

	/** Default implementation of Pointer that stores a simple address/handle. */
	abstract class Default implements Pointer {

		protected long address;

		protected Default() {
			this.address = 0L;
		}

		protected Default(long address) {
			this.address = address;
		}

		@Override
		public long address() {
			return address;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (!(o instanceof Pointer)) return false;
			return address == ((Pointer) o).address();
		}

		@Override
		public int hashCode() {
			return Long.hashCode(address);
		}

		@Override
		public String toString() {
			return String.format("%s pointer [0x%X]", getClass().getSimpleName(), address);
		}
	}
}
