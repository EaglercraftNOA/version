package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;

public class BlockWallSign extends BlockSign {
   public static final DirectionProperty FACING = BlockHorizontal.HORIZONTAL_FACING;
   private static final Map<EnumFacing, VoxelShape> SHAPES = Maps.newEnumMap(ImmutableMap.of(EnumFacing.NORTH, Block.makeCuboidShape(0.0D, 4.5D, 14.0D, 16.0D, 12.5D, 16.0D), EnumFacing.SOUTH, Block.makeCuboidShape(0.0D, 4.5D, 0.0D, 16.0D, 12.5D, 2.0D), EnumFacing.EAST, Block.makeCuboidShape(0.0D, 4.5D, 0.0D, 2.0D, 12.5D, 16.0D), EnumFacing.WEST, Block.makeCuboidShape(14.0D, 4.5D, 0.0D, 16.0D, 12.5D, 16.0D)));

   public BlockWallSign(Block.Properties properties) {
      super(properties);
      this.setDefaultState(this.stateContainer.getBaseState().with(FACING, EnumFacing.NORTH).with(WATERLOGGED, Boolean.valueOf(false)));
   }

   public String getTranslationKey() {
      return this.asItem().getTranslationKey();
   }

   public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      return SHAPES.get(state.get(FACING));
   }

   public boolean isValidPosition(IBlockState state, IWorldReaderBase worldIn, BlockPos pos) {
      return worldIn.getBlockState(pos.offset(state.get(FACING).getOpposite())).getMaterial().isSolid();
   }

   @Nullable
   public IBlockState getStateForPlacement(BlockItemUseContext context) {
      IBlockState iblockstate = this.getDefaultState();
      IFluidState ifluidstate = context.getWorld().getFluidState(context.getPos());
      IWorldReaderBase iworldreaderbase = context.getWorld();
      BlockPos blockpos = context.getPos();
      EnumFacing[] aenumfacing = context.getNearestLookingDirections();

      for(EnumFacing enumfacing : aenumfacing) {
         if (enumfacing.getAxis().isHorizontal()) {
            EnumFacing enumfacing1 = enumfacing.getOpposite();
            iblockstate = iblockstate.with(FACING, enumfacing1);
            if (iblockstate.isValidPosition(iworldreaderbase, blockpos)) {
               return iblockstate.with(WATERLOGGED, Boolean.valueOf(ifluidstate.getFluid() == Fluids.WATER));
            }
         }
      }

      return null;
   }

   public IBlockState updatePostPlacement(IBlockState stateIn, EnumFacing facing, IBlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
      return facing.getOpposite() == stateIn.get(FACING) && !stateIn.isValidPosition(worldIn, currentPos) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
   }

   public IBlockState rotate(IBlockState state, Rotation rot) {
      return state.with(FACING, rot.rotate(state.get(FACING)));
   }

   public IBlockState mirror(IBlockState state, Mirror mirrorIn) {
      return state.rotate(mirrorIn.toRotation(state.get(FACING)));
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
      builder.add(FACING, WATERLOGGED);
   }
}
