package net.minecraft.item;

import net.minecraft.block.BlockFire;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemFireCharge extends Item {
   public ItemFireCharge(Item.Properties builder) {
      super(builder);
   }

   public EnumActionResult onItemUse(ItemUseContext context) {
      World world = context.getWorld();
      if (world.isRemote) {
         return EnumActionResult.SUCCESS;
      } else {
         BlockPos blockpos = context.getPos().offset(context.getFace());
         if (world.getBlockState(blockpos).isAir()) {
            world.playSound((EntityPlayer)null, blockpos, SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.BLOCKS, 1.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
            world.setBlockState(blockpos, ((BlockFire)Blocks.FIRE).getStateForPlacement(world, blockpos));
         }

         context.getItem().shrink(1);
         return EnumActionResult.SUCCESS;
      }
   }
}
