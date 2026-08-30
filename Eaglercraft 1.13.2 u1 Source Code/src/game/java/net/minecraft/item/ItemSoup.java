package net.minecraft.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.world.World;

public class ItemSoup extends ItemFood {
   public ItemSoup(int healAmountIn, Item.Properties builder) {
      super(healAmountIn, 0.6F, false, builder);
   }

   public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
      super.onItemUseFinish(stack, worldIn, entityLiving);
      return new ItemStack(Items.BOWL);
   }
}
