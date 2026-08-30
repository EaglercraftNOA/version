package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockStainedGlassPane extends BlockGlassPane {
   private final EnumDyeColor color;

   public BlockStainedGlassPane(EnumDyeColor colorIn, Block.Properties properties) {
      super(properties);
      this.color = colorIn;
      this.setDefaultState(this.stateContainer.getBaseState().with(NORTH, Boolean.valueOf(false)).with(EAST, Boolean.valueOf(false)).with(SOUTH, Boolean.valueOf(false)).with(WEST, Boolean.valueOf(false)).with(WATERLOGGED, Boolean.valueOf(false)));
   }

   public EnumDyeColor getColor() {
      return this.color;
   }

   public BlockRenderLayer getRenderLayer() {
      return BlockRenderLayer.TRANSLUCENT;
   }

   public void onBlockAdded(IBlockState state, World worldIn, BlockPos pos, IBlockState oldState) {
      if (oldState.getBlock() != state.getBlock()) {
         if (!worldIn.isRemote) {
            BlockBeacon.updateColorAsync(worldIn, pos);
         }

      }
   }

   public void onReplaced(IBlockState state, World worldIn, BlockPos pos, IBlockState newState, boolean isMoving) {
      if (state.getBlock() != newState.getBlock()) {
         if (!worldIn.isRemote) {
            BlockBeacon.updateColorAsync(worldIn, pos);
         }

      }
   }
}
