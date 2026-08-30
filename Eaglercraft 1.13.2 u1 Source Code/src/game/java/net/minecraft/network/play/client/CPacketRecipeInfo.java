package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CPacketRecipeInfo implements Packet<INetHandlerPlayServer> {
   private CPacketRecipeInfo.Purpose purpose;
   private ResourceLocation recipe;
   private boolean isGuiOpen;
   private boolean filteringCraftable;
   private boolean isFurnaceGuiOpen;
   private boolean furnaceFilteringCraftable;

   public CPacketRecipeInfo() {
   }

   public CPacketRecipeInfo(IRecipe p_i47518_1_) {
      this.purpose = CPacketRecipeInfo.Purpose.SHOWN;
      this.recipe = p_i47518_1_.getId();
   }

   @OnlyIn(Dist.CLIENT)
   public CPacketRecipeInfo(boolean p_i48734_1_, boolean p_i48734_2_, boolean p_i48734_3_, boolean p_i48734_4_) {
      this.purpose = CPacketRecipeInfo.Purpose.SETTINGS;
      this.isGuiOpen = p_i48734_1_;
      this.filteringCraftable = p_i48734_2_;
      this.isFurnaceGuiOpen = p_i48734_3_;
      this.furnaceFilteringCraftable = p_i48734_4_;
   }

   public void readPacketData(PacketBuffer buf) throws IOException {
      this.purpose = buf.readEnumValue(CPacketRecipeInfo.Purpose.class);
      if (this.purpose == CPacketRecipeInfo.Purpose.SHOWN) {
         this.recipe = buf.readResourceLocation();
      } else if (this.purpose == CPacketRecipeInfo.Purpose.SETTINGS) {
         this.isGuiOpen = buf.readBoolean();
         this.filteringCraftable = buf.readBoolean();
         this.isFurnaceGuiOpen = buf.readBoolean();
         this.furnaceFilteringCraftable = buf.readBoolean();
      }

   }

   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeEnumValue(this.purpose);
      if (this.purpose == CPacketRecipeInfo.Purpose.SHOWN) {
         buf.writeResourceLocation(this.recipe);
      } else if (this.purpose == CPacketRecipeInfo.Purpose.SETTINGS) {
         buf.writeBoolean(this.isGuiOpen);
         buf.writeBoolean(this.filteringCraftable);
         buf.writeBoolean(this.isFurnaceGuiOpen);
         buf.writeBoolean(this.furnaceFilteringCraftable);
      }

   }

   public void processPacket(INetHandlerPlayServer handler) {
      handler.handleRecipeBookUpdate(this);
   }

   public CPacketRecipeInfo.Purpose getPurpose() {
      return this.purpose;
   }

   public ResourceLocation getRecipeId() {
      return this.recipe;
   }

   public boolean isGuiOpen() {
      return this.isGuiOpen;
   }

   public boolean isFilteringCraftable() {
      return this.filteringCraftable;
   }

   public boolean isFurnaceGuiOpen() {
      return this.isFurnaceGuiOpen;
   }

   public boolean isFurnaceFilteringCraftable() {
      return this.furnaceFilteringCraftable;
   }

   public static enum Purpose {
      SHOWN,
      SETTINGS;
   }
}
