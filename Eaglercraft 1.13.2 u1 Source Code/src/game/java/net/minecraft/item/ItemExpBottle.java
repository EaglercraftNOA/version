package net.minecraft.item;

import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemExpBottle extends Item {
   public ItemExpBottle(Item.Properties builder) {
      super(builder);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean hasEffect(ItemStack stack) {
      return true;
   }

   public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
      ItemStack itemstack = playerIn.getHeldItem(handIn);
      if (!playerIn.abilities.isCreativeMode) {
         itemstack.shrink(1);
      }

      worldIn.playSound((EntityPlayer)null, playerIn.posX, playerIn.posY, playerIn.posZ, SoundEvents.ENTITY_EXPERIENCE_BOTTLE_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
      if (!worldIn.isRemote) {
         EntityExpBottle entityexpbottle = new EntityExpBottle(worldIn, playerIn);
         entityexpbottle.shoot(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, -20.0F, 0.7F, 1.0F);
         worldIn.spawnEntity(entityexpbottle);
      }

      playerIn.addStat(StatList.ITEM_USED.get(this));
      return new ActionResult<>(EnumActionResult.SUCCESS, itemstack);
   }
}
