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
import net.minecraft.state.BooleanProperty;
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

public class BlockCoralPlantBase extends Block implements IBucketPickupHandler, ILiquidContainer {
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   private static final VoxelShape field_212559_a = Block.makeCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 4.0D, 14.0D);

   protected BlockCoralPlantBase(Block.Properties p_i49810_1_) {
      super(p_i49810_1_);
      this.setDefaultState(this.stateContainer.getBaseState().with(WATERLOGGED, Boolean.valueOf(true)));
   }

   protected void updateIfDry(IBlockState state, IWorld worldIn, BlockPos pos) {
      if (!isInWater(state, worldIn, pos)) {
         worldIn.getPendingBlockTicks().scheduleTick(pos, this, 60 + worldIn.getRandom().nextInt(40));
      }

   }

   protected static boolean isInWater(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      if (state.get(WATERLOGGED)) {
         return true;
      } else {
         for(EnumFacing enumfacing : EnumFacing._VALUES) {
            if (worldIn.getFluidState(pos.offset(enumfacing)).isTagged(FluidTags.WATER)) {
               return true;
            }
         }

         return false;
      }
   }

   protected boolean canSilkHarvest() {
      return true;
   }

   public int quantityDropped(IBlockState state, Random random) {
      return 0;
   }

   @Nullable
   public IBlockState getStateForPlacement(BlockItemUseContext context) {
      IFluidState ifluidstate = context.getWorld().getFluidState(context.getPos());
      return this.getDefaultState().with(WATERLOGGED, Boolean.valueOf(ifluidstate.isTagged(FluidTags.WATER) && ifluidstate.getLevel() == 8));
   }

   public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      return field_212559_a;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public BlockRenderLayer getRenderLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return BlockFaceShape.UNDEFINED;
   }

   public IBlockState updatePostPlacement(IBlockState stateIn, EnumFacing facing, IBlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
      if (stateIn.get(WATERLOGGED)) {
         worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
      }

      return facing == EnumFacing.DOWN && !this.isValidPosition(stateIn, worldIn, currentPos) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
   }

   public boolean isValidPosition(IBlockState state, IWorldReaderBase worldIn, BlockPos pos) {
      return worldIn.getBlockState(pos.down()).isTopSolid();
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
      builder.add(WATERLOGGED);
   }

   public IFluidState getFluidState(IBlockState state) {
      return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
   }

   public Fluid pickupFluid(IWorld worldIn, BlockPos pos, IBlockState state) {
      if (state.get(WATERLOGGED)) {
         worldIn.setBlockState(pos, state.with(WATERLOGGED, Boolean.valueOf(false)), 3);
         return Fluids.WATER;
      } else {
         return Fluids.EMPTY;
      }
   }

   public boolean canContainFluid(IBlockReader worldIn, BlockPos pos, IBlockState state, Fluid fluidIn) {
      return !state.get(WATERLOGGED) && fluidIn == Fluids.WATER;
   }

   public boolean receiveFluid(IWorld worldIn, BlockPos pos, IBlockState state, IFluidState fluidStateIn) {
      if (!state.get(WATERLOGGED) && fluidStateIn.getFluid() == Fluids.WATER) {
         if (!worldIn.isRemote()) {
            worldIn.setBlockState(pos, state.with(WATERLOGGED, Boolean.valueOf(true)), 3);
            worldIn.getPendingFluidTicks().scheduleTick(pos, fluidStateIn.getFluid(), fluidStateIn.getFluid().getTickRate(worldIn));
         }

         return true;
      } else {
         return false;
      }
   }
}
