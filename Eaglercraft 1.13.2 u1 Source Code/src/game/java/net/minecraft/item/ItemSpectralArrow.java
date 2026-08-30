package net.minecraft.item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntitySpectralArrow;
import net.minecraft.world.World;

public class ItemSpectralArrow extends ItemArrow {
   public ItemSpectralArrow(Item.Properties builder) {
      super(builder);
   }

   public EntityArrow createArrow(World worldIn, ItemStack stack, EntityLivingBase p_200887_3_) {
      return new EntitySpectralArrow(worldIn, p_200887_3_);
   }
}
