package com.mojang.authlib;

public interface ProfileLookupCallback {
    void onProfileLookupSucceeded(GameProfile profile);

    void onProfileLookupFailed(GameProfile profile, Exception exception);
}
