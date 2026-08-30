package com.mojang.authlib.minecraft;

import com.google.common.collect.ImmutableMap;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import java.net.InetAddress;
import java.util.Map;

public class OfflineMinecraftSessionService implements MinecraftSessionService {
   public void joinServer(GameProfile profile, String authenticationToken, String serverId) throws AuthenticationException {
   }

   public GameProfile hasJoinedServer(GameProfile user, String serverId, InetAddress address) throws AuthenticationUnavailableException {
      return user;
   }

   public Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> getTextures(GameProfile profile, boolean requireSecure) {
      return ImmutableMap.of();
   }

   public GameProfile fillProfileProperties(GameProfile profile, boolean requireSecure) {
      return profile;
   }
}
