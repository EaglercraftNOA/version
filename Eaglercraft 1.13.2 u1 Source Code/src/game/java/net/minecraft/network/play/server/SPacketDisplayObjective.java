package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketDisplayObjective implements Packet<INetHandlerPlayClient> {
   private int position;
   private String scoreName;

   public SPacketDisplayObjective() {
   }

   public SPacketDisplayObjective(int positionIn, @Nullable ScoreObjective objective) {
      this.position = positionIn;
      if (objective == null) {
         this.scoreName = "";
      } else {
         this.scoreName = objective.getName();
      }

   }

   public void readPacketData(PacketBuffer buf) throws IOException {
      this.position = buf.readByte();
      this.scoreName = buf.readString(16);
   }

   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeByte(this.position);
      buf.writeString(this.scoreName);
   }

   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleDisplayObjective(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int getPosition() {
      return this.position;
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public String getName() {
      return Objects.equals(this.scoreName, "") ? null : this.scoreName;
   }
}
