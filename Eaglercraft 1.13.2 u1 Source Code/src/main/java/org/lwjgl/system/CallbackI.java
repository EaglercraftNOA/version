/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.system;

public interface CallbackI extends Pointer {

	default String getSignature() {
		return "";
	}
}
