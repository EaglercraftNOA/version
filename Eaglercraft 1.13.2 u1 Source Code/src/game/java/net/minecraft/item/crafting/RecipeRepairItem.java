package net.minecraft.item.crafting;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class RecipeRepairItem extends IRecipeHidden {
   public RecipeRepairItem(ResourceLocation p_i48163_1_) {
      super(p_i48163_1_);
   }

   public boolean matches(IInventory inv, World worldIn) {
      if (!(inv instanceof InventoryCrafting)) {
         return false;
      } else {
         List<ItemStack> list = Lists.newArrayList();

         for(int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack itemstack = inv.getStackInSlot(i);
            if (!itemstack.isEmpty()) {
               list.add(itemstack);
               if (list.size() > 1) {
                  ItemStack itemstack1 = list.get(0);
                  if (itemstack.getItem() != itemstack1.getItem() || itemstack1.getCount() != 1 || itemstack.getCount() != 1 || !itemstack1.getItem().isDamageable()) {
                     return false;
                  }
               }
            }
         }

         return list.size() == 2;
      }
   }

   public ItemStack getCraftingResult(IInventory inv) {
      List<ItemStack> list = Lists.newArrayList();

      for(int i = 0; i < inv.getSizeInventory(); ++i) {
         ItemStack itemstack = inv.getStackInSlot(i);
         if (!itemstack.isEmpty()) {
            list.add(itemstack);
            if (list.size() > 1) {
               ItemStack itemstack1 = list.get(0);
               if (itemstack.getItem() != itemstack1.getItem() || itemstack1.getCount() != 1 || itemstack.getCount() != 1 || !itemstack1.getItem().isDamageable()) {
                  return ItemStack.EMPTY;
               }
            }
         }
      }

      if (list.size() == 2) {
         ItemStack itemstack3 = list.get(0);
         ItemStack itemstack4 = list.get(1);
         if (itemstack3.getItem() == itemstack4.getItem() && itemstack3.getCount() == 1 && itemstack4.getCount() == 1 && itemstack3.getItem().isDamageable()) {
            Item item = itemstack3.getItem();
            int j = item.getMaxDamage() - itemstack3.getDamage();
            int k = item.getMaxDamage() - itemstack4.getDamage();
            int l = j + k + item.getMaxDamage() * 5 / 100;
            int i1 = item.getMaxDamage() - l;
            if (i1 < 0) {
               i1 = 0;
            }

            ItemStack itemstack2 = new ItemStack(itemstack3.getItem());
            itemstack2.setDamage(i1);
            return itemstack2;
         }
      }

      return ItemStack.EMPTY;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean canFit(int width, int height) {
      return width * height >= 2;
   }

   public IRecipeSerializer<?> getSerializer() {
      return RecipeSerializers.CRAFTING_SPECIAL_REPAIRITEM;
   }
}
