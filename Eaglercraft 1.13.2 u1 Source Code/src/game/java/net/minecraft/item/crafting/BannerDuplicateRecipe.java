package net.minecraft.item.crafting;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBanner;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BannerDuplicateRecipe extends IRecipeHidden {
   public BannerDuplicateRecipe(ResourceLocation p_i48171_1_) {
      super(p_i48171_1_);
   }

   public boolean matches(IInventory inv, World worldIn) {
      if (!(inv instanceof InventoryCrafting)) {
         return false;
      } else {
         EnumDyeColor enumdyecolor = null;
         ItemStack itemstack = null;
         ItemStack itemstack1 = null;

         for(int i = 0; i < inv.getSizeInventory(); ++i) {
            ItemStack itemstack2 = inv.getStackInSlot(i);
            Item item = itemstack2.getItem();
            if (item instanceof ItemBanner) {
               ItemBanner itembanner = (ItemBanner)item;
               if (enumdyecolor == null) {
                  enumdyecolor = itembanner.getColor();
               } else if (enumdyecolor != itembanner.getColor()) {
                  return false;
               }

               boolean flag = TileEntityBanner.getPatterns(itemstack2) > 0;
               if (flag) {
                  if (itemstack != null) {
                     return false;
                  }

                  itemstack = itemstack2;
               } else {
                  if (itemstack1 != null) {
                     return false;
                  }

                  itemstack1 = itemstack2;
               }
            }
         }

         return itemstack != null && itemstack1 != null;
      }
   }

   public ItemStack getCraftingResult(IInventory inv) {
      for(int i = 0; i < inv.getSizeInventory(); ++i) {
         ItemStack itemstack = inv.getStackInSlot(i);
         if (!itemstack.isEmpty() && TileEntityBanner.getPatterns(itemstack) > 0) {
            ItemStack itemstack1 = itemstack.copy();
            itemstack1.setCount(1);
            return itemstack1;
         }
      }

      return ItemStack.EMPTY;
   }

   public NonNullList<ItemStack> getRemainingItems(IInventory inv) {
      NonNullList<ItemStack> nonnulllist = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);

      for(int i = 0; i < nonnulllist.size(); ++i) {
         ItemStack itemstack = inv.getStackInSlot(i);
         if (!itemstack.isEmpty()) {
            if (itemstack.getItem().hasContainerItem()) {
               nonnulllist.set(i, new ItemStack(itemstack.getItem().getContainerItem()));
            } else if (itemstack.hasTag() && TileEntityBanner.getPatterns(itemstack) > 0) {
               ItemStack itemstack1 = itemstack.copy();
               itemstack1.setCount(1);
               nonnulllist.set(i, itemstack1);
            }
         }
      }

      return nonnulllist;
   }

   public IRecipeSerializer<?> getSerializer() {
      return RecipeSerializers.CRAFTING_SPECIAL_BANNERDUPLICATE;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean canFit(int width, int height) {
      return width * height >= 2;
   }
}
