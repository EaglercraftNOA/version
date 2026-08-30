package com.mojang.bridge.launcher;

import com.mojang.bridge.Bridge;

/**
 * Utility method to initialize the launcher side of the bridge.
 *
 * <p>The game should not use this class directly, only by calling {@link Bridge#getLauncher()}.</p>
 *
 * <p>Launchers should provide the name of their implementation of this class in
 * {@code META-INF/services/com.mojang.bridge.launcher.LauncherProvider} in the format of
 * {code com.example.mylauncher.LauncherProvider} - for more information see {@link java.util.ServiceLoader}.</p>
 */
public interface LauncherProvider {
    /**
     * Create the Launcher instance.
     *
     * <p>This should run any initialization logic that is required, and will only be called once. If something
     * goes wrong, this method may return {@code null} to signify that the launcher is not available.</p>
     *
     * @return Launcher instance, or {@code null}
     */
    Launcher createLauncher();
}
