package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemLead extends Item {
   public ItemLead(Item.Properties builder) {
      super(builder);
   }

   public EnumActionResult onItemUse(ItemUseContext context) {
      World world = context.getWorld();
      BlockPos blockpos = context.getPos();
      Block block = world.getBlockState(blockpos).getBlock();
      if (block instanceof BlockFence) {
         EntityPlayer entityplayer = context.getPlayer();
         if (!world.isRemote && entityplayer != null) {
            attachToFence(entityplayer, world, blockpos);
         }

         return EnumActionResult.SUCCESS;
      } else {
         return EnumActionResult.PASS;
      }
   }

   public static boolean attachToFence(EntityPlayer player, World worldIn, BlockPos fence) {
      EntityLeashKnot entityleashknot = EntityLeashKnot.getKnotForPosition(worldIn, fence);
      boolean flag = false;
      double d0 = 7.0D;
      int i = fence.getX();
      int j = fence.getY();
      int k = fence.getZ();

      for(EntityLiving entityliving : worldIn.getEntitiesWithinAABB(EntityLiving.class, new AxisAlignedBB((double)i - 7.0D, (double)j - 7.0D, (double)k - 7.0D, (double)i + 7.0D, (double)j + 7.0D, (double)k + 7.0D))) {
         if (entityliving.getLeashed() && entityliving.getLeashHolder() == player) {
            if (entityleashknot == null) {
               entityleashknot = EntityLeashKnot.createKnot(worldIn, fence);
            }

            entityliving.setLeashHolder(entityleashknot, true);
            flag = true;
         }
      }

      return flag;
   }
}
