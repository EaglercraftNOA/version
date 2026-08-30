package net.minecraft.util;

import com.mojang.authlib.GameProfile;
import com.mojang.util.UUIDTypeAdapter;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.lax1dude.eaglercraft.v1_8.profile.EaglerProfile;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Session {
   private final String username;
   private final String playerID;
   private final String token;
   private final Session.Type sessionType;
   private GameProfile profile;

   public Session(String usernameIn, String playerIDIn, String tokenIn, String sessionTypeIn) {
      this.username = usernameIn;
      this.playerID = playerIDIn;
      this.token = tokenIn;
      this.sessionType = Session.Type.setSessionType(sessionTypeIn);
      this.reset();
   }

   public String getSessionID() {
      return "token:" + this.token + ":" + this.playerID;
   }

   public String getPlayerID() {
      return this.playerID;
   }

   public String getUsername() {
      return this.username;
   }

   public String getToken() {
      return this.token;
   }

   public GameProfile getProfile() {
      return this.profile;
   }

   public GameProfile getGameProfile() {
      return this.profile;
   }

   public void reset() {
      try {
         UUID uuid = UUIDTypeAdapter.fromString(this.getPlayerID());
         this.profile = new GameProfile(uuid, this.getUsername());
      } catch (IllegalArgumentException var2) {
         this.profile = new GameProfile((UUID)null, this.getUsername());
      }
   }

   public void update(String serverUsername, UUID uuid) {
      this.profile = new GameProfile(uuid, serverUsername);
   }

   public void setLAN() {
      this.update(EaglerProfile.getName(), EntityPlayer.getOfflineUUID(EaglerProfile.getName()));
   }

   @OnlyIn(Dist.CLIENT)
   public static enum Type {
      LEGACY("legacy"),
      MOJANG("mojang");

      private static final Map<String, Session.Type> SESSION_TYPES = Arrays.stream(values()).collect(Collectors.toMap((p_199876_0_) -> {
         return p_199876_0_.sessionType;
      }, Function.identity()));
      private final String sessionType;

      private Type(String sessionTypeIn) {
         this.sessionType = sessionTypeIn;
      }

      @Nullable
      public static Session.Type setSessionType(String sessionTypeIn) {
         return SESSION_TYPES.get(sessionTypeIn.toLowerCase(Locale.ROOT));
      }
   }
}
