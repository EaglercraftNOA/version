package com.mojang.authlib.minecraft;

import java.util.UUID;

public interface SocialInteractionsService {

    /**
     * Checks if the user is allowed to play on multiplayer servers.
     *
     * @return True if the user is allowed to play on multiplayer servers
     */
    boolean serversAllowed();

    /**
     * Checks if the user is allowed to play on realms.
     *
     * @return True if the user is allowed to play on realms
     */
    boolean realmsAllowed();

    /**
     * Checks if the user is allowed to access chat in online games.
     *
     * @return True if the user is allowed to chat
     */
    boolean chatAllowed();

    /**
     * Check if the user is allowed to send telemetry.
     *
     * @return True if the user is allowed to send telemetry
     */
    boolean telemetryAllowed();

    /**
     * Check if a player is on the block list.
     *
     * @param playerID A valid player UUID
     * @return True if communications from the player should be blocked
     */
    boolean isBlockedPlayer(UUID playerID);
}
