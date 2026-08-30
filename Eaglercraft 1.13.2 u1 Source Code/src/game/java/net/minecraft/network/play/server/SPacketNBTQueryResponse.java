package net.minecraft.network.play.server;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketNBTQueryResponse implements Packet<INetHandlerPlayClient> {
   private int transactionId;
   @Nullable
   private NBTTagCompound tag;

   public SPacketNBTQueryResponse() {
   }

   public SPacketNBTQueryResponse(int p_i49757_1_, @Nullable NBTTagCompound p_i49757_2_) {
      this.transactionId = p_i49757_1_;
      this.tag = p_i49757_2_;
   }

   public void readPacketData(PacketBuffer buf) throws IOException {
      this.transactionId = buf.readVarInt();
      this.tag = buf.readCompoundTag();
   }

   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeVarInt(this.transactionId);
      buf.writeCompoundTag(this.tag);
   }

   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleNBTQueryResponse(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int getTransactionId() {
      return this.transactionId;
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public NBTTagCompound getTag() {
      return this.tag;
   }

   public boolean shouldSkipErrors() {
      return true;
   }
}
