package net.minecraft.inventory;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;

public class InventoryBasic implements IInventory, IRecipeHelperPopulator {
   private final ITextComponent inventoryTitle;
   private final int slotsCount;
   private final NonNullList<ItemStack> inventoryContents;
   private List<IInventoryChangedListener> listeners;
   private ITextComponent customName;

   public InventoryBasic(ITextComponent title, int slotCount) {
      this.inventoryTitle = title;
      this.slotsCount = slotCount;
      this.inventoryContents = NonNullList.withSize(slotCount, ItemStack.EMPTY);
   }

   public void addListener(IInventoryChangedListener listener) {
      if (this.listeners == null) {
         this.listeners = Lists.newArrayList();
      }

      this.listeners.add(listener);
   }

   public void removeListener(IInventoryChangedListener listener) {
      this.listeners.remove(listener);
   }

   public ItemStack getStackInSlot(int index) {
      return index >= 0 && index < this.inventoryContents.size() ? this.inventoryContents.get(index) : ItemStack.EMPTY;
   }

   public ItemStack decrStackSize(int index, int count) {
      ItemStack itemstack = ItemStackHelper.getAndSplit(this.inventoryContents, index, count);
      if (!itemstack.isEmpty()) {
         this.markDirty();
      }

      return itemstack;
   }

   public ItemStack addItem(ItemStack stack) {
      ItemStack itemstack = stack.copy();

      for(int i = 0; i < this.slotsCount; ++i) {
         ItemStack itemstack1 = this.getStackInSlot(i);
         if (itemstack1.isEmpty()) {
            this.setInventorySlotContents(i, itemstack);
            this.markDirty();
            return ItemStack.EMPTY;
         }

         if (ItemStack.areItemsEqual(itemstack1, itemstack)) {
            int j = Math.min(this.getInventoryStackLimit(), itemstack1.getMaxStackSize());
            int k = Math.min(itemstack.getCount(), j - itemstack1.getCount());
            if (k > 0) {
               itemstack1.grow(k);
               itemstack.shrink(k);
               if (itemstack.isEmpty()) {
                  this.markDirty();
                  return ItemStack.EMPTY;
               }
            }
         }
      }

      if (itemstack.getCount() != stack.getCount()) {
         this.markDirty();
      }

      return itemstack;
   }

   public ItemStack removeStackFromSlot(int index) {
      ItemStack itemstack = this.inventoryContents.get(index);
      if (itemstack.isEmpty()) {
         return ItemStack.EMPTY;
      } else {
         this.inventoryContents.set(index, ItemStack.EMPTY);
         return itemstack;
      }
   }

   public void setInventorySlotContents(int index, ItemStack stack) {
      this.inventoryContents.set(index, stack);
      if (!stack.isEmpty() && stack.getCount() > this.getInventoryStackLimit()) {
         stack.setCount(this.getInventoryStackLimit());
      }

      this.markDirty();
   }

   public int getSizeInventory() {
      return this.slotsCount;
   }

   public boolean isEmpty() {
      for(ItemStack itemstack : this.inventoryContents) {
         if (!itemstack.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   public ITextComponent getName() {
      return this.customName != null ? this.customName : this.inventoryTitle;
   }

   @Nullable
   public ITextComponent getCustomName() {
      return this.customName;
   }

   public boolean hasCustomName() {
      return this.customName != null;
   }

   public void setCustomName(@Nullable ITextComponent name) {
      this.customName = name;
   }

   public int getInventoryStackLimit() {
      return 64;
   }

   public void markDirty() {
      if (this.listeners != null) {
         for(int i = 0; i < this.listeners.size(); ++i) {
            this.listeners.get(i).onInventoryChanged(this);
         }
      }

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
      this.inventoryContents.clear();
   }

   public void fillStackedContents(RecipeItemHelper helper) {
      for(ItemStack itemstack : this.inventoryContents) {
         helper.accountStack(itemstack);
      }

   }
}
