package com.mojang.bridge.launcher;

import com.mojang.bridge.game.GameSession;

/**
 * A callback for all game session related events.
 */
public interface SessionEventListener {
    /**
     * A listener that will do nothing.
     */
    SessionEventListener NONE = new SessionEventListener() {
    };

    /**
     * Fired on the start of a new game session.
     *
     * @param session A snapshot of the game session that was just joined.
     */
    default void onStartGameSession(final GameSession session) {
    }

    /**
     * Fired on the end of an old game session.
     *
     * @param session A snapshot of the game session that was just left.
     */
    default void onLeaveGameSession(final GameSession session) {
    }
}
