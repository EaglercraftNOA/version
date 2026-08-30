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

public class ItemSeeds extends Item {
   private final IBlockState field_195978_a;

   public ItemSeeds(Block crop, Item.Properties builder) {
      super(builder);
      this.field_195978_a = crop.getDefaultState();
   }

   public EnumActionResult onItemUse(ItemUseContext context) {
      IWorld iworld = context.getWorld();
      BlockPos blockpos = context.getPos().up();
      if (context.getFace() == EnumFacing.UP && iworld.isAirBlock(blockpos) && this.field_195978_a.isValidPosition(iworld, blockpos)) {
         iworld.setBlockState(blockpos, this.field_195978_a, 11);
         ItemStack itemstack = context.getItem();
         EntityPlayer entityplayer = context.getPlayer();
         if (entityplayer instanceof EntityPlayerMP) {
            CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP)entityplayer, blockpos, itemstack);
         }

         itemstack.shrink(1);
         return EnumActionResult.SUCCESS;
      } else {
         return EnumActionResult.FAIL;
      }
   }
}
