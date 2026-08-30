/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.system;

public interface FunctionProvider {

	long getFunctionAddress(String functionName);
	FunctionProvider DUMMY = functionName -> 1L;
}
