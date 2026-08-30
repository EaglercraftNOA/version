package net.minecraft.server.management;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import java.io.File;

public class UserListWhitelist extends UserList<GameProfile, UserListWhitelistEntry> {
   public UserListWhitelist(File p_i1132_1_) {
      super(p_i1132_1_);
   }

   protected UserListEntry<GameProfile> createEntry(JsonObject entryData) {
      return new UserListWhitelistEntry(entryData);
   }

   public boolean isWhitelisted(GameProfile profile) {
      return this.hasEntry(profile);
   }

   public String[] getKeys() {
      String[] astring = new String[this.getEntries().size()];
      int i = 0;

      for(UserListEntry<GameProfile> userlistentry : this.getEntries()) {
         astring[i++] = userlistentry.getValue().getName();
      }

      return astring;
   }

   protected String getObjectKey(GameProfile obj) {
      return obj.getId().toString();
   }
}
