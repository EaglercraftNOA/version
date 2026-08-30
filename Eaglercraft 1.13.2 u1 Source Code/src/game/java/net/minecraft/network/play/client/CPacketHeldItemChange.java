package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CPacketHeldItemChange implements Packet<INetHandlerPlayServer> {
   private int slotId;

   public CPacketHeldItemChange() {
   }

   @OnlyIn(Dist.CLIENT)
   public CPacketHeldItemChange(int slotIdIn) {
      this.slotId = slotIdIn;
   }

   public void readPacketData(PacketBuffer buf) throws IOException {
      this.slotId = buf.readShort();
   }

   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeShort(this.slotId);
   }

   public void processPacket(INetHandlerPlayServer handler) {
      handler.processHeldItemChange(this);
   }

   public int getSlotId() {
      return this.slotId;
   }
}
