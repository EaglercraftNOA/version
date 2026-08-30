package net.minecraft.item.crafting;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IRecipe {
   boolean matches(IInventory inv, World worldIn);

   ItemStack getCraftingResult(IInventory inv);

   @OnlyIn(Dist.CLIENT)
   boolean canFit(int width, int height);

   ItemStack getRecipeOutput();

   default NonNullList<ItemStack> getRemainingItems(IInventory inv) {
      NonNullList<ItemStack> nonnulllist = NonNullList.withSize(inv.getSizeInventory(), ItemStack.EMPTY);

      for(int i = 0; i < nonnulllist.size(); ++i) {
         Item item = inv.getStackInSlot(i).getItem();
         if (item.hasContainerItem()) {
            nonnulllist.set(i, new ItemStack(item.getContainerItem()));
         }
      }

      return nonnulllist;
   }

   default NonNullList<Ingredient> getIngredients() {
      return NonNullList.create();
   }

   default boolean isDynamic() {
      return false;
   }

   @OnlyIn(Dist.CLIENT)
   default String getGroup() {
      return "";
   }

   ResourceLocation getId();

   IRecipeSerializer<?> getSerializer();
}
