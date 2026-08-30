/*
 * Copyright 2014 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License, version 2.0.
 */

package io.netty.util.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Native libraries are not available in TeaVM/WebGL runtimes.
 */
public final class NativeLibraryLoader {

    public static void loadFirstAvailable(Object loader, String... names) {
        List<Throwable> suppressed = new ArrayList<Throwable>();
        for (String name : names) {
            try {
                load(name, loader);
                return;
            } catch (Throwable t) {
                suppressed.add(t);
            }
        }
        IllegalArgumentException iae =
                new IllegalArgumentException("Failed to load any of the given libraries: " + Arrays.toString(names));
        ThrowableUtil.addSuppressedAndClear(iae, suppressed);
        throw iae;
    }

    public static void load(String originalName, Object loader) {
        throw new UnsatisfiedLinkError("Native library loading is unavailable: " + originalName);
    }

    private NativeLibraryLoader() {
    }
}
