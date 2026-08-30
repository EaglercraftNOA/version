package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public class BlockCactus extends Block {
   public static final IntegerProperty AGE = BlockStateProperties.AGE_0_15;
   protected static final VoxelShape field_196400_b = Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 15.0D, 15.0D);
   protected static final VoxelShape field_196401_c = Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);

   protected BlockCactus(Block.Properties properties) {
      super(properties);
      this.setDefaultState(this.stateContainer.getBaseState().with(AGE, Integer.valueOf(0)));
   }

   public void tick(IBlockState state, World worldIn, BlockPos pos, Random random) {
      if (!state.isValidPosition(worldIn, pos)) {
         worldIn.destroyBlock(pos, true);
      } else {
         BlockPos blockpos = pos.up();
         if (worldIn.isAirBlock(blockpos)) {
            int i;
            for(i = 1; worldIn.getBlockState(pos.down(i)).getBlock() == this; ++i) {
               ;
            }

            if (i < 3) {
               int j = state.get(AGE);
               if (j == 15) {
                  worldIn.setBlockState(blockpos, this.getDefaultState());
                  IBlockState iblockstate = state.with(AGE, Integer.valueOf(0));
                  worldIn.setBlockState(pos, iblockstate, 4);
                  iblockstate.neighborChanged(worldIn, blockpos, this, pos);
               } else {
                  worldIn.setBlockState(pos, state.with(AGE, Integer.valueOf(j + 1)), 4);
               }

            }
         }
      }
   }

   public VoxelShape getCollisionShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      return field_196400_b;
   }

   public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      return field_196401_c;
   }

   public boolean isSolid(IBlockState state) {
      return true;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public IBlockState updatePostPlacement(IBlockState stateIn, EnumFacing facing, IBlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
      if (!stateIn.isValidPosition(worldIn, currentPos)) {
         worldIn.getPendingBlockTicks().scheduleTick(currentPos, this, 1);
      }

      return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
   }

   public boolean isValidPosition(IBlockState state, IWorldReaderBase worldIn, BlockPos pos) {
      for(EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
         IBlockState iblockstate = worldIn.getBlockState(pos.offset(enumfacing));
         Material material = iblockstate.getMaterial();
         if (material.isSolid() || worldIn.getFluidState(pos.offset(enumfacing)).isTagged(FluidTags.LAVA)) {
            return false;
         }
      }

      Block block = worldIn.getBlockState(pos.down()).getBlock();
      return (block == Blocks.CACTUS || block == Blocks.SAND || block == Blocks.RED_SAND) && !worldIn.getBlockState(pos.up()).getMaterial().isLiquid();
   }

   public void onEntityCollision(IBlockState state, World worldIn, BlockPos pos, Entity entityIn) {
      entityIn.attackEntityFrom(DamageSource.CACTUS, 1.0F);
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

   public boolean allowsMovement(IBlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
      return false;
   }
}
