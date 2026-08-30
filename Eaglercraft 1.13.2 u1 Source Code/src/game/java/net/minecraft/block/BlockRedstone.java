package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class BlockRedstone extends Block {
   public BlockRedstone(Block.Properties properties) {
      super(properties);
   }

   public boolean canProvidePower(IBlockState state) {
      return true;
   }

   public int getWeakPower(IBlockState blockState, IBlockReader blockAccess, BlockPos pos, EnumFacing side) {
      return 15;
   }
}
