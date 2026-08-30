package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CPacketPlaceRecipe implements Packet<INetHandlerPlayServer> {
   private int windowId;
   private ResourceLocation recipeId;
   private boolean placeAll;

   public CPacketPlaceRecipe() {
   }

   @OnlyIn(Dist.CLIENT)
   public CPacketPlaceRecipe(int p_i47614_1_, IRecipe p_i47614_2_, boolean p_i47614_3_) {
      this.windowId = p_i47614_1_;
      this.recipeId = p_i47614_2_.getId();
      this.placeAll = p_i47614_3_;
   }

   public void readPacketData(PacketBuffer buf) throws IOException {
      this.windowId = buf.readByte();
      this.recipeId = buf.readResourceLocation();
      this.placeAll = buf.readBoolean();
   }

   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeByte(this.windowId);
      buf.writeResourceLocation(this.recipeId);
      buf.writeBoolean(this.placeAll);
   }

   public void processPacket(INetHandlerPlayServer handler) {
      handler.processPlaceRecipe(this);
   }

   public int getWindowId() {
      return this.windowId;
   }

   public ResourceLocation getRecipeId() {
      return this.recipeId;
   }

   public boolean shouldPlaceAll() {
      return this.placeAll;
   }
}
