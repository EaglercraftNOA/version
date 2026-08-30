package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public class BlockReed extends Block {
   public static final IntegerProperty AGE = BlockStateProperties.AGE_0_15;
   protected static final VoxelShape SHAPE = Block.makeCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D);

   protected BlockReed(Block.Properties properties) {
      super(properties);
      this.setDefaultState(this.stateContainer.getBaseState().with(AGE, Integer.valueOf(0)));
   }

   public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      return SHAPE;
   }

   public void tick(IBlockState state, World worldIn, BlockPos pos, Random random) {
      if (state.isValidPosition(worldIn, pos) && worldIn.isAirBlock(pos.up())) {
         int i;
         for(i = 1; worldIn.getBlockState(pos.down(i)).getBlock() == this; ++i) {
            ;
         }

         if (i < 3) {
            int j = state.get(AGE);
            if (j == 15) {
               worldIn.setBlockState(pos.up(), this.getDefaultState());
               worldIn.setBlockState(pos, state.with(AGE, Integer.valueOf(0)), 4);
            } else {
               worldIn.setBlockState(pos, state.with(AGE, Integer.valueOf(j + 1)), 4);
            }
         }
      }

   }

   public IBlockState updatePostPlacement(IBlockState stateIn, EnumFacing facing, IBlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
      return !stateIn.isValidPosition(worldIn, currentPos) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
   }

   public boolean isValidPosition(IBlockState state, IWorldReaderBase worldIn, BlockPos pos) {
      Block block = worldIn.getBlockState(pos.down()).getBlock();
      if (block == this) {
         return true;
      } else {
         if (block == Blocks.GRASS_BLOCK || block == Blocks.DIRT || block == Blocks.COARSE_DIRT || block == Blocks.PODZOL || block == Blocks.SAND || block == Blocks.RED_SAND) {
            BlockPos blockpos = pos.down();

            for(EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
               IBlockState iblockstate = worldIn.getBlockState(blockpos.offset(enumfacing));
               IFluidState ifluidstate = worldIn.getFluidState(blockpos.offset(enumfacing));
               if (ifluidstate.isTagged(FluidTags.WATER) || iblockstate.getBlock() == Blocks.FROSTED_ICE) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public BlockRenderLayer getRenderLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
      builder.add(AGE);
   }

   public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return BlockFaceShape.UNDEFINED;
   }
}
