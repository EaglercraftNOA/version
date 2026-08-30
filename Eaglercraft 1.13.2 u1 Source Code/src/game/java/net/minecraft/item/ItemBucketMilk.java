package net.minecraft.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemBucketMilk extends Item {
   public ItemBucketMilk(Item.Properties builder) {
      super(builder);
   }

   public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
      if (entityLiving instanceof EntityPlayerMP) {
         EntityPlayerMP entityplayermp = (EntityPlayerMP)entityLiving;
         CriteriaTriggers.CONSUME_ITEM.trigger(entityplayermp, stack);
         entityplayermp.addStat(StatList.ITEM_USED.get(this));
      }

      if (entityLiving instanceof EntityPlayer && !((EntityPlayer)entityLiving).abilities.isCreativeMode) {
         stack.shrink(1);
      }

      if (!worldIn.isRemote) {
         entityLiving.clearActivePotions();
      }

      return stack.isEmpty() ? new ItemStack(Items.BUCKET) : stack;
   }

   public int getUseDuration(ItemStack stack) {
      return 32;
   }

   public EnumAction getUseAction(ItemStack stack) {
      return EnumAction.DRINK;
   }

   public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn) {
      playerIn.setActiveHand(handIn);
      return new ActionResult<>(EnumActionResult.SUCCESS, playerIn.getHeldItem(handIn));
   }
}
