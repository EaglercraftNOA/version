package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class BlockGlass extends BlockBreakable {
   public BlockGlass(Block.Properties properties) {
      super(properties);
   }

   public boolean propagatesSkylightDown(IBlockState state, IBlockReader reader, BlockPos pos) {
      return true;
   }

   public int quantityDropped(IBlockState state, Random random) {
      return 0;
   }

   public BlockRenderLayer getRenderLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   protected boolean canSilkHarvest() {
      return true;
   }
}
