package net.minecraft.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class ItemSeedFood extends ItemFood {
   private final IBlockState field_195972_b;

   public ItemSeedFood(int healAmountIn, float saturation, Block crop, Item.Properties builder) {
      super(healAmountIn, saturation, false, builder);
      this.field_195972_b = crop.getDefaultState();
   }

   public EnumActionResult onItemUse(ItemUseContext context) {
      IWorld iworld = context.getWorld();
      BlockPos blockpos = context.getPos().up();
      if (context.getFace() == EnumFacing.UP && iworld.isAirBlock(blockpos) && this.field_195972_b.isValidPosition(iworld, blockpos)) {
         iworld.setBlockState(blockpos, this.field_195972_b, 11);
         EntityPlayer entityplayer = context.getPlayer();
         ItemStack itemstack = context.getItem();
         if (entityplayer instanceof EntityPlayerMP) {
            CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP)entityplayer, blockpos, itemstack);
         }

         itemstack.shrink(1);
         return EnumActionResult.SUCCESS;
      } else {
         return EnumActionResult.PASS;
      }
   }
}
