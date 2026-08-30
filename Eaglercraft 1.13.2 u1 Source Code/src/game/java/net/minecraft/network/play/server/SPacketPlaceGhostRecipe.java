package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPacketPlaceGhostRecipe implements Packet<INetHandlerPlayClient> {
   private int windowId;
   private ResourceLocation recipe;

   public SPacketPlaceGhostRecipe() {
   }

   public SPacketPlaceGhostRecipe(int p_i47615_1_, IRecipe p_i47615_2_) {
      this.windowId = p_i47615_1_;
      this.recipe = p_i47615_2_.getId();
   }

   @OnlyIn(Dist.CLIENT)
   public ResourceLocation getRecipeId() {
      return this.recipe;
   }

   @OnlyIn(Dist.CLIENT)
   public int getWindowId() {
      return this.windowId;
   }

   public void readPacketData(PacketBuffer buf) throws IOException {
      this.windowId = buf.readByte();
      this.recipe = buf.readResourceLocation();
   }

   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeByte(this.windowId);
      buf.writeResourceLocation(this.recipe);
   }

   public void processPacket(INetHandlerPlayClient handler) {
      handler.handlePlaceGhostRecipe(this);
   }
}
