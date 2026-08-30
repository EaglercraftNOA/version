package com.mojang.bridge.game;

import com.mojang.bridge.launcher.Launcher;
import com.mojang.bridge.launcher.SessionEventListener;

/**
 * A representation of a running game.
 *
 * <p>To inform the launcher of this running game, you should call {@link Launcher#registerGame(RunningGame)}</p>
 */
public interface RunningGame {
    /**
     * Gets the version of the running game.
     *
     * @return Game version
     */
    GameVersion getVersion();

    /**
     * Gets the currently selected language.
     *
     * @return Current game language
     */
    Language getSelectedLanguage();

    /**
     * Gets the current game session.
     *
     * <p>A "game session" is defined as an actual gameplay session, such as playing in singleplayer, not the client's
     * own session. See the documentation for {@link GameSession} for more.</p>
     *
     * <p>This method may return a "snapshot" of the current session information, and may not be kept up to date.
     * Launchers should call this method to get a fresh {@link GameSession} whenever required.</p>
     *
     * @return A snapshot of the current game session
     */
    GameSession getCurrentSession();

    /**
     * Gets the current performance metrics.
     *
     * <p>This method may return a "snapshot" of the current metrics, and may not be kept up to date.
     * Launchers should call this method to get a fresh {@link PerformanceMetrics} whenever required.</p>
     *
     * @return A snapshot of the current performance metrics.
     */
    PerformanceMetrics getPerformanceMetrics();

    /**
     * Sets the callbacks for session events.
     *
     * <p>`null` may be set to remove the existing listener.</p>
     *
     * <p>The provided listener will be notified about any "game session" events.</p>
     *
     * @param listener A new listener, or {@code null}.
     */
    void setSessionEventListener(SessionEventListener listener);
}
