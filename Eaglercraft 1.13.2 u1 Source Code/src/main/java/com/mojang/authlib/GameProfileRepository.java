package com.mojang.authlib;

public interface GameProfileRepository {
    void findProfilesByNames(String[] names, Agent agent, ProfileLookupCallback callback);
}
