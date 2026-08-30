package net.minecraft.client.multiplayer;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.lax1dude.eaglercraft.v1_8.EagRuntime;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ServerData {
   public String serverName;
   public String serverIP;
   public String populationInfo;
   public String serverMOTD;
   public long pingToServer;
   public int version = 404;
   public String gameVersion = "1.13.2";
   public boolean pinged;
   public String playerList;
   private ServerData.ServerResourceMode resourceMode = ServerData.ServerResourceMode.PROMPT;
   private String serverIcon;
   private boolean lanServer;
   public boolean hideAddress = false;
   public boolean enableCookies;

   public ServerData(String name, String ip, boolean isLan) {
      this.serverName = name;
      this.serverIP = ip;
      this.lanServer = isLan;
      this.enableCookies = EagRuntime.getConfiguration().isEnableServerCookies();
   }

   public NBTTagCompound getNBTCompound() {
      NBTTagCompound nbttagcompound = new NBTTagCompound();
      nbttagcompound.putString("name", this.serverName);
      nbttagcompound.putString("ip", this.serverIP);
      if (this.serverIcon != null) {
         nbttagcompound.putString("icon", this.serverIcon);
      }

      if (this.resourceMode == ServerData.ServerResourceMode.ENABLED) {
         nbttagcompound.putBoolean("acceptTextures", true);
      } else if (this.resourceMode == ServerData.ServerResourceMode.DISABLED) {
         nbttagcompound.putBoolean("acceptTextures", false);
      }

      nbttagcompound.putBoolean("hideAddress", this.hideAddress);
      nbttagcompound.putBoolean("enableCookies", this.enableCookies);
      return nbttagcompound;
   }

   public ServerData.ServerResourceMode getResourceMode() {
      return this.resourceMode;
   }

   public void setResourceMode(ServerData.ServerResourceMode mode) {
      this.resourceMode = mode;
   }

   public static ServerData getServerDataFromNBTCompound(NBTTagCompound nbtCompound) {
      ServerData serverdata = new ServerData(nbtCompound.getString("name"), nbtCompound.getString("ip"), false);
      if (nbtCompound.contains("icon", 8)) {
         serverdata.setBase64EncodedIconData(nbtCompound.getString("icon"));
      }

      if (nbtCompound.contains("acceptTextures", 1)) {
         if (nbtCompound.getBoolean("acceptTextures")) {
            serverdata.setResourceMode(ServerData.ServerResourceMode.ENABLED);
         } else {
            serverdata.setResourceMode(ServerData.ServerResourceMode.DISABLED);
         }
      } else {
         serverdata.setResourceMode(ServerData.ServerResourceMode.PROMPT);
      }

      if (nbtCompound.contains("hideAddress", 1)) {
         serverdata.hideAddress = nbtCompound.getBoolean("hideAddress");
      } else {
         serverdata.hideAddress = false;
      }

      if (nbtCompound.contains("enableCookies", 1)) {
         serverdata.enableCookies = nbtCompound.getBoolean("enableCookies");
      } else {
         serverdata.enableCookies = EagRuntime.getConfiguration().isEnableServerCookies();
      }

      return serverdata;
   }

   public String getBase64EncodedIconData() {
      return this.serverIcon;
   }

   public void setBase64EncodedIconData(String icon) {
      this.serverIcon = icon;
   }

   public boolean isOnLAN() {
      return this.lanServer;
   }

   public void copyFrom(ServerData serverDataIn) {
      this.serverIP = serverDataIn.serverIP;
      this.serverName = serverDataIn.serverName;
      this.setResourceMode(serverDataIn.getResourceMode());
      this.serverIcon = serverDataIn.serverIcon;
      this.lanServer = serverDataIn.lanServer;
      this.hideAddress = serverDataIn.hideAddress;
      this.enableCookies = serverDataIn.enableCookies;
   }

   @OnlyIn(Dist.CLIENT)
   public static enum ServerResourceMode {
      ENABLED("enabled"),
      DISABLED("disabled"),
      PROMPT("prompt");

      private final ITextComponent motd;

      private ServerResourceMode(String name) {
         this.motd = new TextComponentTranslation("addServer.resourcePack." + name);
      }

      public ITextComponent getMotd() {
         return this.motd;
      }
   }
}
