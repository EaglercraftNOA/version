package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemShears extends Item {
   public ItemShears(Item.Properties builder) {
      super(builder);
   }

   public boolean onBlockDestroyed(ItemStack stack, World worldIn, IBlockState state, BlockPos pos, EntityLivingBase entityLiving) {
      if (!worldIn.isRemote) {
         stack.damageItem(1, entityLiving);
      }

      Block block = state.getBlock();
      return !state.isIn(BlockTags.LEAVES) && block != Blocks.COBWEB && block != Blocks.GRASS && block != Blocks.FERN && block != Blocks.DEAD_BUSH && block != Blocks.VINE && block != Blocks.TRIPWIRE && !block.isIn(BlockTags.WOOL) ? super.onBlockDestroyed(stack, worldIn, state, pos, entityLiving) : true;
   }

   public boolean canHarvestBlock(IBlockState blockIn) {
      Block block = blockIn.getBlock();
      return block == Blocks.COBWEB || block == Blocks.REDSTONE_WIRE || block == Blocks.TRIPWIRE;
   }

   public float getDestroySpeed(ItemStack stack, IBlockState state) {
      Block block = state.getBlock();
      if (block != Blocks.COBWEB && !state.isIn(BlockTags.LEAVES)) {
         return block.isIn(BlockTags.WOOL) ? 5.0F : super.getDestroySpeed(stack, state);
      } else {
         return 15.0F;
      }
   }
}
