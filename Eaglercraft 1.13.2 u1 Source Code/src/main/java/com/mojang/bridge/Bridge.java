package com.mojang.bridge;

import com.mojang.bridge.launcher.Launcher;
import com.mojang.bridge.launcher.LauncherProvider;

import java.util.ServiceLoader;

/**
 * Utility method of retrieving the current {@link Launcher} via the game.
 *
 * <p>Only the game should use this class. Launchers should provide themselves via {@link LauncherProvider} instead.</p>
 */
public class Bridge {
    private static boolean INITIALIZED;
    private static Launcher LAUNCHER;

    private Bridge() {
    }

    /**
     * Retrieve the current {@link Launcher} instance, if it exists.
     *
     * <p>This method is lazy and will only initialize the launcher when first called.</p>
     *
     * @return A {@link Launcher} object, or {@code null} if it isn't available.
     */
    public static Launcher getLauncher() {
        if (!INITIALIZED) {
            synchronized (Bridge.class) {
                if (!INITIALIZED) {
                    LAUNCHER = createLauncher();
                    INITIALIZED = true;
                }
            }
        }
        return LAUNCHER;
    }

    private static Launcher createLauncher() {
        for (final LauncherProvider provider : ServiceLoader.load(LauncherProvider.class)) {
            final Launcher launcher = provider.createLauncher();
            if (launcher != null) {
                return launcher;
            }
        }
        return null;
    }
}
