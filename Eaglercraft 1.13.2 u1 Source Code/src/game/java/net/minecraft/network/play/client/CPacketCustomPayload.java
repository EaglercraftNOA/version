package net.minecraft.network.play.client;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CPacketCustomPayload implements Packet<INetHandlerPlayServer> {
   public static final ResourceLocation BRAND = new ResourceLocation("minecraft:brand");
   private ResourceLocation channel;
   private PacketBuffer data;

   public CPacketCustomPayload() {
   }

   @OnlyIn(Dist.CLIENT)
   public CPacketCustomPayload(ResourceLocation channelIn, PacketBuffer dataIn) {
      this.channel = channelIn;
      this.data = dataIn;
   }

   public void readPacketData(PacketBuffer buf) throws IOException {
      this.channel = buf.readResourceLocation();
      int i = buf.readableBytes();
      if (i >= 0 && i <= 32767) {
         this.data = new PacketBuffer(buf.readBytes(i));
      } else {
         throw new IOException("Payload may not be larger than 32767 bytes");
      }
   }

   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeResourceLocation(this.channel);
      buf.writeBytes((ByteBuf)this.data);
   }

   public void processPacket(INetHandlerPlayServer handler) {
      handler.processCustomPayload(this);
      if (this.data != null) {
         this.data.release();
      }

   }

   public ResourceLocation getChannelName() {
      return this.channel;
   }

   public PacketBuffer getBufferData() {
      return this.data;
   }
}
