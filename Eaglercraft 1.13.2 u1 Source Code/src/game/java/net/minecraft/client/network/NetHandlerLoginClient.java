package net.minecraft.client.network;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.exceptions.InvalidCredentialsException;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenRealmsProxy;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.INetHandlerLoginClient;
import net.minecraft.network.login.client.CPacketCustomPayloadLogin;
import net.minecraft.network.login.server.SPacketCustomPayloadLogin;
import net.minecraft.network.login.server.SPacketDisconnectLogin;
import net.minecraft.network.login.server.SPacketEnableCompression;
import net.minecraft.network.login.server.SPacketEncryptionRequest;
import net.minecraft.network.login.server.SPacketLoginSuccess;
import net.minecraft.realms.DisconnectedRealmsScreen;
import net.minecraft.util.HttpUtil;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class NetHandlerLoginClient implements INetHandlerLoginClient {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Minecraft mc;
   @Nullable
   private final GuiScreen previousGuiScreen;
   private final Consumer<ITextComponent> statusMessageConsumer;
   private final NetworkManager networkManager;
   private GameProfile gameProfile;

   public NetHandlerLoginClient(NetworkManager p_i49527_1_, Minecraft p_i49527_2_, @Nullable GuiScreen p_i49527_3_, Consumer<ITextComponent> p_i49527_4_) {
      this.networkManager = p_i49527_1_;
      this.mc = p_i49527_2_;
      this.previousGuiScreen = p_i49527_3_;
      this.statusMessageConsumer = p_i49527_4_;
   }

   public void handleEncryptionRequest(SPacketEncryptionRequest packetIn) {
      this.networkManager.closeChannel(new TextComponentTranslation("disconnect.loginFailedInfo", "Encryption is not supported"));
   }

   @Nullable
   private ITextComponent joinServer(String p_209522_1_) {
      try {
         this.getSessionService().joinServer(this.mc.getSession().getProfile(), this.mc.getSession().getToken(), p_209522_1_);
         return null;
      } catch (AuthenticationUnavailableException var3) {
         return new TextComponentTranslation("disconnect.loginFailedInfo", new TextComponentTranslation("disconnect.loginFailedInfo.serversUnavailable"));
      } catch (InvalidCredentialsException var4) {
         return new TextComponentTranslation("disconnect.loginFailedInfo", new TextComponentTranslation("disconnect.loginFailedInfo.invalidSession"));
      } catch (AuthenticationException authenticationexception) {
         return new TextComponentTranslation("disconnect.loginFailedInfo", authenticationexception.getMessage());
      }
   }

   private MinecraftSessionService getSessionService() {
      return this.mc.getSessionService();
   }

   public void handleLoginSuccess(SPacketLoginSuccess packetIn) {
      this.statusMessageConsumer.accept(new TextComponentTranslation("connect.joining"));
      this.gameProfile = packetIn.getProfile();
      this.networkManager.setConnectionState(EnumConnectionState.PLAY);
      this.networkManager.setNetHandler(new NetHandlerPlayClient(this.mc, this.previousGuiScreen, this.networkManager, this.gameProfile));
   }

   public void onDisconnect(ITextComponent reason) {
      if (this.previousGuiScreen != null && this.previousGuiScreen instanceof GuiScreenRealmsProxy) {
         this.mc.displayGuiScreen((new DisconnectedRealmsScreen(((GuiScreenRealmsProxy)this.previousGuiScreen).getProxy(), "connect.failed", reason)).getProxy());
      } else {
         this.mc.displayGuiScreen(new GuiDisconnected(this.previousGuiScreen, "connect.failed", reason));
      }

   }

   public void handleDisconnect(SPacketDisconnectLogin packetIn) {
      this.networkManager.closeChannel(packetIn.getReason());
   }

   public void handleEnableCompression(SPacketEnableCompression packetIn) {
      if (!this.networkManager.isLocalChannel()) {
         this.networkManager.setCompressionThreshold(packetIn.getCompressionThreshold());
      }

   }

   public void handleCustomPayloadLogin(SPacketCustomPayloadLogin p_209521_1_) {
      this.statusMessageConsumer.accept(new TextComponentTranslation("connect.negotiating"));
      this.networkManager.sendPacket(new CPacketCustomPayloadLogin(p_209521_1_.getTransaction(), (PacketBuffer)null));
   }
}
