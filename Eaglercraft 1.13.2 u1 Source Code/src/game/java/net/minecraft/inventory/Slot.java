package net.minecraft.inventory;

import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class Slot {
   private final int slotIndex;
   public final IInventory inventory;
   public int slotNumber;
   public int xPos;
   public int yPos;

   public Slot(IInventory inventoryIn, int index, int xPosition, int yPosition) {
      this.inventory = inventoryIn;
      this.slotIndex = index;
      this.xPos = xPosition;
      this.yPos = yPosition;
   }

   public void onSlotChange(ItemStack p_75220_1_, ItemStack p_75220_2_) {
      int i = p_75220_2_.getCount() - p_75220_1_.getCount();
      if (i > 0) {
         this.onCrafting(p_75220_2_, i);
      }

   }

   protected void onCrafting(ItemStack stack, int amount) {
   }

   protected void onSwapCraft(int p_190900_1_) {
   }

   protected void onCrafting(ItemStack stack) {
   }

   public ItemStack onTake(EntityPlayer thePlayer, ItemStack stack) {
      this.onSlotChanged();
      return stack;
   }

   public boolean isItemValid(ItemStack stack) {
      return true;
   }

   public ItemStack getStack() {
      return this.inventory.getStackInSlot(this.slotIndex);
   }

   public boolean getHasStack() {
      return !this.getStack().isEmpty();
   }

   public void putStack(ItemStack stack) {
      this.inventory.setInventorySlotContents(this.slotIndex, stack);
      this.onSlotChanged();
   }

   public void onSlotChanged() {
      this.inventory.markDirty();
   }

   public int getSlotStackLimit() {
      return this.inventory.getInventoryStackLimit();
   }

   public int getItemStackLimit(ItemStack stack) {
      return this.getSlotStackLimit();
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public String getSlotTexture() {
      return null;
   }

   public ItemStack decrStackSize(int amount) {
      return this.inventory.decrStackSize(this.slotIndex, amount);
   }

   public boolean isHere(IInventory inv, int slotIn) {
      return inv == this.inventory && slotIn == this.slotIndex;
   }

   public boolean canTakeStack(EntityPlayer playerIn) {
      return true;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isEnabled() {
      return true;
   }
}
