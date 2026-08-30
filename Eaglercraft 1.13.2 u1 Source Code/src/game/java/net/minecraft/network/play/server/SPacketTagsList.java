package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.tags.NetworkTagManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketTagsList implements Packet<INetHandlerPlayClient> {
   private NetworkTagManager tags;

   public SPacketTagsList() {
   }

   public SPacketTagsList(NetworkTagManager p_i48211_1_) {
      this.tags = p_i48211_1_;
   }

   public void readPacketData(PacketBuffer buf) throws IOException {
      this.tags = NetworkTagManager.read(buf);
   }

   public void writePacketData(PacketBuffer buf) throws IOException {
      this.tags.write(buf);
   }

   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleTags(this);
   }

   @OnlyIn(Dist.CLIENT)
   public NetworkTagManager getTags() {
      return this.tags;
   }
}
