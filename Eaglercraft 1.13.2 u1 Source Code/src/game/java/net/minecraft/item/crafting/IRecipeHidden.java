package net.minecraft.item.crafting;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public abstract class IRecipeHidden implements IRecipe {
   private final ResourceLocation id;

   public IRecipeHidden(ResourceLocation idIn) {
      this.id = idIn;
   }

   public ResourceLocation getId() {
      return this.id;
   }

   public boolean isDynamic() {
      return true;
   }

   public ItemStack getRecipeOutput() {
      return ItemStack.EMPTY;
   }
}
