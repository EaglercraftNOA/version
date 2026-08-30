package net.minecraft.item;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemSimpleFoiled extends Item {
   public ItemSimpleFoiled(Item.Properties builder) {
      super(builder);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean hasEffect(ItemStack stack) {
      return true;
   }
}
