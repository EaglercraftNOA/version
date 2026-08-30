package net.minecraft.inventory;

import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class InventoryCraftResult implements IInventory, IRecipeHolder {
   private final NonNullList<ItemStack> stackResult = NonNullList.withSize(1, ItemStack.EMPTY);
   private IRecipe recipeUsed;

   public int getSizeInventory() {
      return 1;
   }

   public boolean isEmpty() {
      for(ItemStack itemstack : this.stackResult) {
         if (!itemstack.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   public ItemStack getStackInSlot(int index) {
      return this.stackResult.get(0);
   }

   public ITextComponent getName() {
      return new TextComponentString("Result");
   }

   public boolean hasCustomName() {
      return false;
   }

   @Nullable
   public ITextComponent getCustomName() {
      return null;
   }

   public ItemStack decrStackSize(int index, int count) {
      return ItemStackHelper.getAndRemove(this.stackResult, 0);
   }

   public ItemStack removeStackFromSlot(int index) {
      return ItemStackHelper.getAndRemove(this.stackResult, 0);
   }

   public void setInventorySlotContents(int index, ItemStack stack) {
      this.stackResult.set(0, stack);
   }

   public int getInventoryStackLimit() {
      return 64;
   }

   public void markDirty() {
   }

   public boolean isUsableByPlayer(EntityPlayer player) {
      return true;
   }

   public void openInventory(EntityPlayer player) {
   }

   public void closeInventory(EntityPlayer player) {
   }

   public boolean isItemValidForSlot(int index, ItemStack stack) {
      return true;
   }

   public int getField(int id) {
      return 0;
   }

   public void setField(int id, int value) {
   }

   public int getFieldCount() {
      return 0;
   }

   public void clear() {
      this.stackResult.clear();
   }

   public void setRecipeUsed(@Nullable IRecipe recipe) {
      this.recipeUsed = recipe;
   }

   @Nullable
   public IRecipe getRecipeUsed() {
      return this.recipeUsed;
   }
}
