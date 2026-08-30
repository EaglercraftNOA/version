package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketHeldItemChange implements Packet<INetHandlerPlayClient> {
   private int heldItemHotbarIndex;

   public SPacketHeldItemChange() {
   }

   public SPacketHeldItemChange(int hotbarIndexIn) {
      this.heldItemHotbarIndex = hotbarIndexIn;
   }

   public void readPacketData(PacketBuffer buf) throws IOException {
      this.heldItemHotbarIndex = buf.readByte();
   }

   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeByte(this.heldItemHotbarIndex);
   }

   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleHeldItemChange(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int getHeldItemHotbarIndex() {
      return this.heldItemHotbarIndex;
   }
}
