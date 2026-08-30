/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.stb;

/**
 * Font info structure for stb_truetype. Stub for API compatibility.
 */
public class STBTTFontinfo {

	public STBTTFontinfo() {
	}

	public static STBTTFontinfo create() {
		return new STBTTFontinfo();
	}

	public static STBTTFontinfo malloc() {
		return new STBTTFontinfo();
	}

	public long address() {
		return 0L;
	}

	public void free() {
	}
}
