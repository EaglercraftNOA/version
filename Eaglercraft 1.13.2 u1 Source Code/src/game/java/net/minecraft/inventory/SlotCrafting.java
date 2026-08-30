package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class SlotCrafting extends Slot {
   private final InventoryCrafting craftMatrix;
   private final EntityPlayer player;
   private int amountCrafted;

   public SlotCrafting(EntityPlayer player, InventoryCrafting craftingInventory, IInventory inventoryIn, int slotIndex, int xPosition, int yPosition) {
      super(inventoryIn, slotIndex, xPosition, yPosition);
      this.player = player;
      this.craftMatrix = craftingInventory;
   }

   public boolean isItemValid(ItemStack stack) {
      return false;
   }

   public ItemStack decrStackSize(int amount) {
      if (this.getHasStack()) {
         this.amountCrafted += Math.min(amount, this.getStack().getCount());
      }

      return super.decrStackSize(amount);
   }

   protected void onCrafting(ItemStack stack, int amount) {
      this.amountCrafted += amount;
      this.onCrafting(stack);
   }

   protected void onSwapCraft(int p_190900_1_) {
      this.amountCrafted += p_190900_1_;
   }

   protected void onCrafting(ItemStack stack) {
      if (this.amountCrafted > 0) {
         stack.onCrafting(this.player.world, this.player, this.amountCrafted);
      }

      ((IRecipeHolder)this.inventory).onCrafting(this.player);
      this.amountCrafted = 0;
   }

   public ItemStack onTake(EntityPlayer thePlayer, ItemStack stack) {
      this.onCrafting(stack);
      NonNullList<ItemStack> nonnulllist = thePlayer.world.getRecipeManager().getRemainingItems(this.craftMatrix, thePlayer.world);

      for(int i = 0; i < nonnulllist.size(); ++i) {
         ItemStack itemstack = this.craftMatrix.getStackInSlot(i);
         ItemStack itemstack1 = nonnulllist.get(i);
         if (!itemstack.isEmpty()) {
            this.craftMatrix.decrStackSize(i, 1);
            itemstack = this.craftMatrix.getStackInSlot(i);
         }

         if (!itemstack1.isEmpty()) {
            if (itemstack.isEmpty()) {
               this.craftMatrix.setInventorySlotContents(i, itemstack1);
            } else if (ItemStack.areItemsEqual(itemstack, itemstack1) && ItemStack.areItemStackTagsEqual(itemstack, itemstack1)) {
               itemstack1.grow(itemstack.getCount());
               this.craftMatrix.setInventorySlotContents(i, itemstack1);
            } else if (!this.player.inventory.addItemStackToInventory(itemstack1)) {
               this.player.dropItem(itemstack1, false);
            }
         }
      }

      return stack;
   }
}
