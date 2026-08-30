package com.mojang.authlib.yggdrasil.response;

import java.util.Set;
import java.util.UUID;

public class BlockListResponse extends Response {
    /*
    {
      "blockedProfiles": [
        "3fa85f64-5717-4562-b3fc-2c963f66afa6"
      ]
    }
    */

    private Set<UUID> blockedProfiles;

    public Set<UUID> getBlockedProfiles() {
        return blockedProfiles;
    }
}
