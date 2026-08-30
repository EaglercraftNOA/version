package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketTimeUpdate implements Packet<INetHandlerPlayClient> {
   private long totalWorldTime;
   private long worldTime;

   public SPacketTimeUpdate() {
   }

   public SPacketTimeUpdate(long totalWorldTimeIn, long worldTimeIn, boolean doDaylightCycle) {
      this.totalWorldTime = totalWorldTimeIn;
      this.worldTime = worldTimeIn;
      if (!doDaylightCycle) {
         this.worldTime = -this.worldTime;
         if (this.worldTime == 0L) {
            this.worldTime = -1L;
         }
      }

   }

   public void readPacketData(PacketBuffer buf) throws IOException {
      this.totalWorldTime = buf.readLong();
      this.worldTime = buf.readLong();
   }

   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeLong(this.totalWorldTime);
      buf.writeLong(this.worldTime);
   }

   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleTimeUpdate(this);
   }

   @OnlyIn(Dist.CLIENT)
   public long getTotalWorldTime() {
      return this.totalWorldTime;
   }

   @OnlyIn(Dist.CLIENT)
   public long getWorldTime() {
      return this.worldTime;
   }
}
