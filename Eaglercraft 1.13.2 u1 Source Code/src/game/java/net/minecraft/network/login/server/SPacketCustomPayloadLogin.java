package net.minecraft.network.login.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.INetHandlerLoginClient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketCustomPayloadLogin implements Packet<INetHandlerLoginClient> {
   private int transaction;
   private ResourceLocation channel;
   private PacketBuffer payload;

   public void readPacketData(PacketBuffer buf) throws IOException {
      this.transaction = buf.readVarInt();
      this.channel = buf.readResourceLocation();
      int i = buf.readableBytes();
      if (i >= 0 && i <= 1048576) {
         this.payload = new PacketBuffer(buf.readBytes(i));
      } else {
         throw new IOException("Payload may not be larger than 1048576 bytes");
      }
   }

   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeVarInt(this.transaction);
      buf.writeResourceLocation(this.channel);
      buf.writeBytes(this.payload.copy());
   }

   public void processPacket(INetHandlerLoginClient handler) {
      handler.handleCustomPayloadLogin(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int getTransaction() {
      return this.transaction;
   }
}
