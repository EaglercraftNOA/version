package net.minecraft.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemAppleGoldEnchanted extends ItemFood {
   public ItemAppleGoldEnchanted(int healAmountIn, float saturation, boolean meat, Item.Properties builder) {
      super(healAmountIn, saturation, meat, builder);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean hasEffect(ItemStack stack) {
      return true;
   }

   protected void onFoodEaten(ItemStack stack, World worldIn, EntityPlayer player) {
      if (!worldIn.isRemote) {
         player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 400, 1));
         player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 6000, 0));
         player.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 6000, 0));
         player.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 2400, 3));
      }

   }
}
