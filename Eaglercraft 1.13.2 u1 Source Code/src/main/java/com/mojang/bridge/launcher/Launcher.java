package com.mojang.bridge.launcher;

import com.mojang.bridge.Bridge;
import com.mojang.bridge.game.RunningGame;

/**
 * A representation of the Launcher.
 *
 * <p>Games may retrieve the instance of the running launcher with {@link Bridge#getLauncher()}.</p>
 *
 * <p>Launchers should provide themselves with a {@link LauncherProvider}.</p>
 */
public interface Launcher {
    /**
     * Provide the launcher with the current instance of the game.
     *
     * @param runningGame Game instance
     */
    void registerGame(RunningGame runningGame);
}
