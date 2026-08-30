package com.mojang.bridge.game;

import java.util.UUID;

/**
 * Representation of a game session.
 *
 * <p>A "game session" is defined as the actual gameplay, such as joining singleplayer or multiplayer.
 * These are typically referred to as "worlds", however, that's a confusing terminology as these sessions contain
 * worlds.</p>
 *
 * <p>Joining a world in singleplayer, quitting, and rejoining the same world counts as two unique sessions. Dying
 * and respawning is not a different session, as the player did not actively leave and rejoin. Equally, teleporting
 * to other worlds (such as the nether) is not a different session, even though it's technically a different world.</p>
 */
public interface GameSession {
    /**
     * The number of real human Players currently playing in this session.
     *
     * <p>As the definition of "multiplayer" is rather loose - this method can be combined with
     * {@link #isRemoteServer()} to determine if the player is playing alone or with others, locally or remotely.</p>
     *
     * @return Number of players in this game session
     */
    int getPlayerCount();

    /**
     * Returns true if this game session is hosted remotely.
     *
     * <p>As the definition of "multiplayer" is rather loose - this method can be combined with
     * {@link #getPlayerCount()} to determine if the player is playing alone or with others, locally or remotely.</p>
     *
     * @return true if the player is connected remotely
     */
    boolean isRemoteServer();

    /**
     * The name of the EnumDifficulty currently used.
     *
     * <p>This name will be in the form of "peaceful" or "hard", but the precise values may change over time.</p>
     *
     * @return Current EnumDifficulty name
     */
    String getDifficulty();

    /**
     * The name of the current game mode being played.
     *
     * <p>This name will be in the form of "survival" or "spectator", but the precise values may change over time.</p>
     *
     * <p>This value only covers the current active players game mode, and not the general game mode of the session as a
     * whole.</p>
     *
     * @return Current game mode name
     */
    String getGameMode();

    /**
     * Gets the session ID of this game session.
     *
     * <p>This is useful for keeping track of if the player is in the same session, or has left and joined a new one.</p>
     *
     * @return Unique identifier of the current game session
     */
    UUID getSessionId();
}
