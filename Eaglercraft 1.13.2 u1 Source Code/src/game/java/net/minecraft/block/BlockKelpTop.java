package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.item.BlockItemUseContext;
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

public class BlockKelpTop extends Block implements ILiquidContainer {
   public static final IntegerProperty AGE = BlockStateProperties.AGE_0_25;
   protected static final VoxelShape SHAPE = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 9.0D, 16.0D);

   protected BlockKelpTop(Block.Properties builder) {
      super(builder);
      this.setDefaultState(this.stateContainer.getBaseState().with(AGE, Integer.valueOf(0)));
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      return SHAPE;
   }

   @Nullable
   public IBlockState getStateForPlacement(BlockItemUseContext context) {
      IFluidState ifluidstate = context.getWorld().getFluidState(context.getPos());
      return ifluidstate.isTagged(FluidTags.WATER) && ifluidstate.getLevel() == 8 ? this.randomAge(context.getWorld()) : null;
   }

   public IBlockState randomAge(IWorld p_209906_1_) {
      return this.getDefaultState().with(AGE, Integer.valueOf(p_209906_1_.getRandom().nextInt(25)));
   }

   public BlockRenderLayer getRenderLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return BlockFaceShape.UNDEFINED;
   }

   public IFluidState getFluidState(IBlockState state) {
      return Fluids.WATER.getStillFluidState(false);
   }

   public void tick(IBlockState state, World worldIn, BlockPos pos, Random random) {
      if (!state.isValidPosition(worldIn, pos)) {
         worldIn.destroyBlock(pos, true);
      } else {
         BlockPos blockpos = pos.up();
         IBlockState iblockstate = worldIn.getBlockState(blockpos);
         if (iblockstate.getBlock() == Blocks.WATER && state.get(AGE) < 25 && random.nextDouble() < 0.14D) {
            worldIn.setBlockState(blockpos, state.cycle(AGE));
         }

      }
   }

   public boolean isValidPosition(IBlockState state, IWorldReaderBase worldIn, BlockPos pos) {
      BlockPos blockpos = pos.down();
      IBlockState iblockstate = worldIn.getBlockState(blockpos);
      Block block = iblockstate.getBlock();
      if (block == Blocks.MAGMA_BLOCK) {
         return false;
      } else {
         return block == this || block == Blocks.KELP_PLANT || Block.doesSideFillSquare(iblockstate.getCollisionShape(worldIn, blockpos), EnumFacing.UP);
      }
   }

   public IBlockState updatePostPlacement(IBlockState stateIn, EnumFacing facing, IBlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
      if (!stateIn.isValidPosition(worldIn, currentPos)) {
         if (facing == EnumFacing.DOWN) {
            return Blocks.AIR.getDefaultState();
         }

         worldIn.getPendingBlockTicks().scheduleTick(currentPos, this, 1);
      }

      if (facing == EnumFacing.UP && facingState.getBlock() == this) {
         return Blocks.KELP_PLANT.getDefaultState();
      } else {
         worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
         return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
      }
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
      builder.add(AGE);
   }

   public boolean canContainFluid(IBlockReader worldIn, BlockPos pos, IBlockState state, Fluid fluidIn) {
      return false;
   }

   public boolean receiveFluid(IWorld worldIn, BlockPos pos, IBlockState state, IFluidState fluidStateIn) {
      return false;
   }
}
