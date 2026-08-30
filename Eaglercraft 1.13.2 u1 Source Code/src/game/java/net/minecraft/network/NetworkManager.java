package net.minecraft.network;

import io.netty.util.AttributeKey;
import java.io.IOException;
import javax.annotation.Nullable;
import javax.crypto.SecretKey;
import net.lax1dude.eaglercraft.v1_8.EaglerSocketAddress;
import net.lax1dude.eaglercraft.v1_8.internal.EnumEaglerConnectionState;
import net.lax1dude.eaglercraft.v1_8.socket.CompressionNotSupportedException;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public abstract class NetworkManager {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final Marker NETWORK_MARKER = MarkerManager.getMarker("NETWORK");
   public static final Marker NETWORK_PACKETS_MARKER = MarkerManager.getMarker("NETWORK_PACKETS", NETWORK_MARKER);
   public static final AttributeKey<EnumConnectionState> PROTOCOL_ATTRIBUTE_KEY = AttributeKey.valueOf("protocol");
   private final EnumPacketDirection direction;
   protected INetHandler packetListener;
   protected EnumConnectionState packetState = EnumConnectionState.HANDSHAKING;
   protected ITextComponent terminationReason;
   protected boolean disconnected;
   protected boolean clientDisconnected;
   private int field_211394_q;
   private int field_211395_r;
   private float field_211396_s;
   private float field_211397_t;
   private int ticks;

   public NetworkManager(EnumPacketDirection packetDirection) {
      this.direction = packetDirection;
   }

   public abstract void connect();

   public abstract EnumEaglerConnectionState getConnectStatus();

   public void setConnectionState(EnumConnectionState newState) {
      this.packetState = newState;
      LOGGER.debug("Enabled auto read");
   }

   protected static <T extends INetHandler> void processPacket(Packet<T> p_197664_0_, INetHandler p_197664_1_) {
      p_197664_0_.processPacket((T)p_197664_1_);
   }

   public void setNetHandler(INetHandler handler) {
      Validate.notNull(handler, "packetListener");
      LOGGER.debug("Set listener of {} to {}", this, handler);
      this.packetListener = handler;
   }

   public void setListener(INetHandler handler) {
      this.setNetHandler(handler);
   }

   public abstract void sendPacket(Packet<?> packetIn);

   public void sendPacket(Packet<?> packetIn, @Nullable PacketSendListener p_201058_2_) {
      this.sendPacket(packetIn);
      if (p_201058_2_ != null) {
         p_201058_2_.accept(PacketSendListener.SUCCESS);
      }
   }

   public void tick() {
      try {
         this.processReceivedPackets();
      } catch (IOException ioexception) {
         LOGGER.error("Failed to process packets", (Throwable)ioexception);
      }

      if (this.packetListener instanceof ITickable) {
         ((ITickable)this.packetListener).tick();
      }

      if (this.ticks++ % 20 == 0) {
         this.field_211397_t = this.field_211397_t * 0.75F + (float)this.field_211395_r * 0.25F;
         this.field_211396_s = this.field_211396_s * 0.75F + (float)this.field_211394_q * 0.25F;
         this.field_211395_r = 0;
         this.field_211394_q = 0;
      }
   }

   public void processReceivedPackets() throws IOException {
   }

   @Nullable
   public EaglerSocketAddress getRemoteAddress() {
      return null;
   }

   public abstract void closeChannel(ITextComponent message);

   public boolean isLocalChannel() {
      return false;
   }

   public void enableEncryption(SecretKey key) {
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isEncrypted() {
      return false;
   }

   public boolean getIsencrypted() {
      return this.isEncrypted();
   }

   public boolean isChannelOpen() {
      return this.getConnectStatus() == EnumEaglerConnectionState.CONNECTED;
   }

   public boolean hasNoChannel() {
      return false;
   }

   public INetHandler getNetHandler() {
      return this.packetListener;
   }

   @Nullable
   public ITextComponent getExitMessage() {
      return this.terminationReason;
   }

   public void disableAutoRead() {
   }

   public void setCompressionTreshold(int compressionTreshold) {
      throw new CompressionNotSupportedException();
   }

   public void setCompressionThreshold(int threshold) {
      if (threshold >= 0) {
         this.setCompressionTreshold(threshold);
      }
   }

   public boolean checkDisconnected() {
      if (!this.isChannelOpen()) {
         this.handleDisconnection();
         return true;
      } else {
         return false;
      }
   }

   protected void doClientDisconnect(ITextComponent msg) {
      if (!this.clientDisconnected) {
         this.clientDisconnected = true;
         if (this.packetListener != null) {
            this.packetListener.onDisconnect(msg);
         }
      }
   }

   public void handleDisconnection() {
      if (!this.disconnected) {
         this.disconnected = true;
         if (this.getExitMessage() != null) {
            this.getNetHandler().onDisconnect(this.getExitMessage());
         } else if (this.getNetHandler() != null) {
            this.getNetHandler().onDisconnect(new TextComponentTranslation("multiplayer.disconnect.generic"));
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public float getPacketsReceived() {
      return this.field_211396_s;
   }

   @OnlyIn(Dist.CLIENT)
   public float getPacketsSent() {
      return this.field_211397_t;
   }

   public EnumPacketDirection getDirection() {
      return this.direction;
   }

   public void injectRawFrame(byte[] data) {
   }
}
