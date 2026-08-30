package net.minecraft.network;

import com.mojang.authlib.GameProfile;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import net.lax1dude.eaglercraft.v1_8.EaglercraftVersion;
import net.lax1dude.eaglercraft.v1_8.EaglerInputStream;
import net.lax1dude.eaglercraft.v1_8.ClientUUIDLoadingCache;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePluginMessageProtocol;
import net.lax1dude.eaglercraft.v1_8.sp.server.skins.IntegratedTexturePackets;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.login.INetHandlerLoginServer;
import net.minecraft.network.login.client.CPacketCustomPayloadLogin;
import net.minecraft.network.login.client.CPacketEncryptionResponse;
import net.minecraft.network.login.client.CPacketLoginStart;
import net.minecraft.network.login.server.SPacketDisconnectLogin;
import net.minecraft.network.login.server.SPacketEnableCompression;
import net.minecraft.network.login.server.SPacketLoginSuccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NetHandlerLoginServer implements INetHandlerLoginServer, ITickable {
   private static final AtomicInteger AUTHENTICATOR_THREAD_ID = new AtomicInteger(0);
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Random RANDOM = new Random();
   private final byte[] verifyToken = new byte[4];
   private final MinecraftServer server;
   public final NetworkManager networkManager;
   private NetHandlerLoginServer.LoginState currentLoginState = NetHandlerLoginServer.LoginState.HELLO;
   private int connectionTimer;
   private GameProfile loginGameProfile;
   private final String serverId = "";
   private EntityPlayerMP player;
   private byte[] loginSkinPacket;
   private byte[] loginCapePacket;
   private int selectedProtocol = 3;
   private EaglercraftUUID clientBrandUUID;

   public NetHandlerLoginServer(MinecraftServer serverIn, NetworkManager networkManagerIn) {
      this.server = serverIn;
      this.networkManager = networkManagerIn;
      RANDOM.nextBytes(this.verifyToken);
   }

   public void tick() {
      if (this.currentLoginState == NetHandlerLoginServer.LoginState.READY_TO_ACCEPT) {
         this.tryAcceptPlayer();
      } else if (this.currentLoginState == NetHandlerLoginServer.LoginState.DELAY_ACCEPT) {
         EntityPlayerMP entityplayermp = this.server.getPlayerList().getPlayerByUUID(this.loginGameProfile.getId());
         if (entityplayermp == null) {
            this.currentLoginState = NetHandlerLoginServer.LoginState.READY_TO_ACCEPT;
            this.server.getPlayerList().initializeConnectionToPlayer(this.networkManager, this.player, GamePluginMessageProtocol.getByVersion(this.selectedProtocol), IntegratedTexturePackets.handleTextureData(this.loginSkinPacket, this.loginCapePacket), this.clientBrandUUID);
            this.player = null;
         }
      }

      if (this.connectionTimer++ == 600) {
         this.disconnect(new TextComponentTranslation("multiplayer.disconnect.slow_login"));
      }

   }

   public void disconnect(ITextComponent reason) {
      try {
         LOGGER.info("Disconnecting {}: {}", this.getConnectionInfo(), reason.getString());
         this.networkManager.sendPacket(new SPacketDisconnectLogin(reason));
         this.networkManager.closeChannel(reason);
      } catch (Exception exception) {
         LOGGER.error("Error whilst disconnecting player", (Throwable)exception);
      }

   }

   public void tryAcceptPlayer() {
      if (!this.loginGameProfile.isComplete()) {
         this.loginGameProfile = this.getOfflineProfile(this.loginGameProfile);
      }

      ITextComponent itextcomponent = this.server.getPlayerList().canPlayerLogin(this.networkManager.getRemoteAddress(), this.loginGameProfile);
      if (itextcomponent != null) {
         this.disconnect(itextcomponent);
      } else {
         this.currentLoginState = NetHandlerLoginServer.LoginState.ACCEPTED;
         if (this.server.getNetworkCompressionThreshold() >= 0 && !this.networkManager.isLocalChannel()) {
            this.networkManager.sendPacket(new SPacketEnableCompression(this.server.getNetworkCompressionThreshold()), (p_210149_1_) -> {
               this.networkManager.setCompressionThreshold(this.server.getNetworkCompressionThreshold());
            });
         }

         this.networkManager.sendPacket(new SPacketLoginSuccess(this.loginGameProfile, this.selectedProtocol));
         EntityPlayerMP entityplayermp = this.server.getPlayerList().getPlayerByUUID(this.loginGameProfile.getId());
         if (entityplayermp != null) {
            this.currentLoginState = NetHandlerLoginServer.LoginState.DELAY_ACCEPT;
            this.player = this.server.getPlayerList().createPlayerForUser(this.loginGameProfile);
         } else {
            this.server.getPlayerList().initializeConnectionToPlayer(this.networkManager, this.server.getPlayerList().createPlayerForUser(this.loginGameProfile), GamePluginMessageProtocol.getByVersion(this.selectedProtocol), IntegratedTexturePackets.handleTextureData(this.loginSkinPacket, this.loginCapePacket), this.clientBrandUUID);
         }
      }

   }

   public void onDisconnect(ITextComponent reason) {
      LOGGER.info("{} lost connection: {}", this.getConnectionInfo(), reason.getString());
   }

   public String getConnectionInfo() {
      return this.loginGameProfile != null ? this.loginGameProfile + " (" + this.networkManager.getRemoteAddress() + ")" : String.valueOf((Object)this.networkManager.getRemoteAddress());
   }

   public void processLoginStart(CPacketLoginStart packetIn) {
      Validate.validState(this.currentLoginState == NetHandlerLoginServer.LoginState.HELLO, "Unexpected hello packet");
      byte[] abyte = packetIn.getProtocols();
      if (abyte != null && abyte.length > 0) {
         try {
            DataInputStream datainputstream = new DataInputStream(new EaglerInputStream(abyte));
            int i = -1;
            int j = datainputstream.readUnsignedShort();

            for(int k = 0; k < j; ++k) {
               int l = datainputstream.readUnsignedShort();
               if ((l == 3 || l == 4 || l == 5) && l > i) {
                  i = l;
               }
            }

            if (i == -1) {
               this.disconnect(new TextComponentString("Unknown protocol!"));
               return;
            }

            this.selectedProtocol = i;
         } catch (IOException ioexception) {
            this.selectedProtocol = 3;
         }
      } else {
         this.selectedProtocol = 3;
      }

      this.loginGameProfile = packetIn.getProfile();
      this.loginSkinPacket = packetIn.getSkin();
      this.loginCapePacket = packetIn.getCape();
      this.clientBrandUUID = this.selectedProtocol <= 3 ? EaglercraftVersion.legacyClientUUIDInSharedWorld : packetIn.getBrandUUID();
      if (ClientUUIDLoadingCache.PENDING_UUID.equals(this.clientBrandUUID) || ClientUUIDLoadingCache.VANILLA_UUID.equals(this.clientBrandUUID)) {
         this.clientBrandUUID = null;
      }

      this.currentLoginState = NetHandlerLoginServer.LoginState.READY_TO_ACCEPT;
   }

   public void processEncryptionResponse(CPacketEncryptionResponse packetIn) {
      this.disconnect(new TextComponentTranslation("multiplayer.disconnect.unexpected_query_response"));
   }

   public void processCustomPayloadLogin(CPacketCustomPayloadLogin p_209526_1_) {
      this.disconnect(new TextComponentTranslation("multiplayer.disconnect.unexpected_query_response"));
   }

   protected GameProfile getOfflineProfile(GameProfile original) {
      UUID uuid = EntityPlayer.getOfflineUUID(original.getName());
      return new GameProfile(uuid, original.getName());
   }

   static enum LoginState {
      HELLO,
      KEY,
      AUTHENTICATING,
      NEGOTIATING,
      READY_TO_ACCEPT,
      DELAY_ACCEPT,
      ACCEPTED;
   }
}
