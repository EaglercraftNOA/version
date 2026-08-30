package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CPacketPickItem implements Packet<INetHandlerPlayServer> {
   private int pickIndex;

   public CPacketPickItem() {
   }

   @OnlyIn(Dist.CLIENT)
   public CPacketPickItem(int pickIndexIn) {
      this.pickIndex = pickIndexIn;
   }

   public void readPacketData(PacketBuffer buf) throws IOException {
      this.pickIndex = buf.readVarInt();
   }

   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeVarInt(this.pickIndex);
   }

   public void processPacket(INetHandlerPlayServer handler) {
      handler.processPickItem(this);
   }

   public int getPickIndex() {
      return this.pickIndex;
   }
}
