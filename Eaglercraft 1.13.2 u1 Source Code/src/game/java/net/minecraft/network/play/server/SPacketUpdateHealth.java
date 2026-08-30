package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketUpdateHealth implements Packet<INetHandlerPlayClient> {
   private float health;
   private int foodLevel;
   private float saturationLevel;

   public SPacketUpdateHealth() {
   }

   public SPacketUpdateHealth(float healthIn, int foodLevelIn, float saturationLevelIn) {
      this.health = healthIn;
      this.foodLevel = foodLevelIn;
      this.saturationLevel = saturationLevelIn;
   }

   public void readPacketData(PacketBuffer buf) throws IOException {
      this.health = buf.readFloat();
      this.foodLevel = buf.readVarInt();
      this.saturationLevel = buf.readFloat();
   }

   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeFloat(this.health);
      buf.writeVarInt(this.foodLevel);
      buf.writeFloat(this.saturationLevel);
   }

   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleUpdateHealth(this);
   }

   @OnlyIn(Dist.CLIENT)
   public float getHealth() {
      return this.health;
   }

   @OnlyIn(Dist.CLIENT)
   public int getFoodLevel() {
      return this.foodLevel;
   }

   @OnlyIn(Dist.CLIENT)
   public float getSaturationLevel() {
      return this.saturationLevel;
   }
}
