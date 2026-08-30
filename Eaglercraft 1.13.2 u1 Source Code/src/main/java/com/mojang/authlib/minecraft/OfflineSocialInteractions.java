package com.mojang.authlib.minecraft;

import java.util.UUID;

public class OfflineSocialInteractions implements SocialInteractionsService {
    @Override
    public boolean serversAllowed() {
        return true;
    }

    @Override
    public boolean realmsAllowed() {
        return true;
    }

    @Override
    public boolean chatAllowed() {
        return true;
    }

    @Override
    public boolean telemetryAllowed() {
        return false;
    }

    @Override
    public boolean isBlockedPlayer(final UUID playerID) {
        return false;
    }
}
