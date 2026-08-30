package net.minecraft.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.world.World;

public class ItemArrow extends Item {
   public ItemArrow(Item.Properties builder) {
      super(builder);
   }

   public EntityArrow createArrow(World worldIn, ItemStack stack, EntityLivingBase p_200887_3_) {
      EntityTippedArrow entitytippedarrow = new EntityTippedArrow(worldIn, p_200887_3_);
      entitytippedarrow.setPotionEffect(stack);
      return entitytippedarrow;
   }
}
