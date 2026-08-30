package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketChangeGameState implements Packet<INetHandlerPlayClient> {
   public static final String[] MESSAGE_NAMES = new String[]{"block.minecraft.bed.not_valid"};
   private int state;
   private float value;

   public SPacketChangeGameState() {
   }

   public SPacketChangeGameState(int stateIn, float valueIn) {
      this.state = stateIn;
      this.value = valueIn;
   }

   public void readPacketData(PacketBuffer buf) throws IOException {
      this.state = buf.readUnsignedByte();
      this.value = buf.readFloat();
   }

   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeByte(this.state);
      buf.writeFloat(this.value);
   }

   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleChangeGameState(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int getGameState() {
      return this.state;
   }

   @OnlyIn(Dist.CLIENT)
   public float getValue() {
      return this.value;
   }
}
