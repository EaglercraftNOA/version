package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CPacketUpdateBeacon implements Packet<INetHandlerPlayServer> {
   private int primaryEffect;
   private int secondaryEffect;

   public CPacketUpdateBeacon() {
   }

   @OnlyIn(Dist.CLIENT)
   public CPacketUpdateBeacon(int primaryEffectIn, int secondaryEffectIn) {
      this.primaryEffect = primaryEffectIn;
      this.secondaryEffect = secondaryEffectIn;
   }

   public void readPacketData(PacketBuffer buf) throws IOException {
      this.primaryEffect = buf.readVarInt();
      this.secondaryEffect = buf.readVarInt();
   }

   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeVarInt(this.primaryEffect);
      buf.writeVarInt(this.secondaryEffect);
   }

   public void processPacket(INetHandlerPlayServer handler) {
      handler.processUpdateBeacon(this);
   }

   public int getPrimaryEffect() {
      return this.primaryEffect;
   }

   public int getSecondaryEffect() {
      return this.secondaryEffect;
   }
}
