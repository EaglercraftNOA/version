package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;

public class BlockStandingSign extends BlockSign {
   public static final IntegerProperty ROTATION = BlockStateProperties.ROTATION_0_15;

   public BlockStandingSign(Block.Properties properties) {
      super(properties);
      this.setDefaultState(this.stateContainer.getBaseState().with(ROTATION, Integer.valueOf(0)).with(WATERLOGGED, Boolean.valueOf(false)));
   }

   public boolean isValidPosition(IBlockState state, IWorldReaderBase worldIn, BlockPos pos) {
      return worldIn.getBlockState(pos.down()).getMaterial().isSolid();
   }

   public IBlockState getStateForPlacement(BlockItemUseContext context) {
      IFluidState ifluidstate = context.getWorld().getFluidState(context.getPos());
      return this.getDefaultState().with(ROTATION, Integer.valueOf(MathHelper.floor((double)((180.0F + context.getPlacementYaw()) * 16.0F / 360.0F) + 0.5D) & 15)).with(WATERLOGGED, Boolean.valueOf(ifluidstate.getFluid() == Fluids.WATER));
   }

   public IBlockState updatePostPlacement(IBlockState stateIn, EnumFacing facing, IBlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
      return facing == EnumFacing.DOWN && !this.isValidPosition(stateIn, worldIn, currentPos) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
   }

   public IBlockState rotate(IBlockState state, Rotation rot) {
      return state.with(ROTATION, Integer.valueOf(rot.rotate(state.get(ROTATION), 16)));
   }

   public IBlockState mirror(IBlockState state, Mirror mirrorIn) {
      return state.with(ROTATION, Integer.valueOf(mirrorIn.mirrorRotation(state.get(ROTATION), 16)));
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
      builder.add(ROTATION, WATERLOGGED);
   }
}
