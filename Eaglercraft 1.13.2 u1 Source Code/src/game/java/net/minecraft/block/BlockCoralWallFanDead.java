package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
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

public class BlockCoralWallFanDead extends BlockCoralFan {
   public static final DirectionProperty FACING = BlockHorizontal.HORIZONTAL_FACING;
   private static final Map<EnumFacing, VoxelShape> SHAPES = Maps.newEnumMap(ImmutableMap.of(EnumFacing.NORTH, Block.makeCuboidShape(0.0D, 4.0D, 5.0D, 16.0D, 12.0D, 16.0D), EnumFacing.SOUTH, Block.makeCuboidShape(0.0D, 4.0D, 0.0D, 16.0D, 12.0D, 11.0D), EnumFacing.WEST, Block.makeCuboidShape(5.0D, 4.0D, 0.0D, 16.0D, 12.0D, 16.0D), EnumFacing.EAST, Block.makeCuboidShape(0.0D, 4.0D, 0.0D, 11.0D, 12.0D, 16.0D)));

   protected BlockCoralWallFanDead(Block.Properties builder) {
      super(builder);
      this.setDefaultState(this.stateContainer.getBaseState().with(FACING, EnumFacing.NORTH).with(WATERLOGGED, Boolean.valueOf(true)));
   }

   public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      return SHAPES.get(state.get(FACING));
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

   public IBlockState updatePostPlacement(IBlockState stateIn, EnumFacing facing, IBlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
      if (stateIn.get(WATERLOGGED)) {
         worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
      }

      return facing.getOpposite() == stateIn.get(FACING) && !stateIn.isValidPosition(worldIn, currentPos) ? Blocks.AIR.getDefaultState() : stateIn;
   }

   public boolean isValidPosition(IBlockState state, IWorldReaderBase worldIn, BlockPos pos) {
      EnumFacing enumfacing = state.get(FACING);
      BlockPos blockpos = pos.offset(enumfacing.getOpposite());
      IBlockState iblockstate = worldIn.getBlockState(blockpos);
      return iblockstate.getBlockFaceShape(worldIn, blockpos, enumfacing) == BlockFaceShape.SOLID && !isExceptBlockForAttachWithPiston(iblockstate.getBlock());
   }

   @Nullable
   public IBlockState getStateForPlacement(BlockItemUseContext context) {
      IBlockState iblockstate = super.getStateForPlacement(context);
      IWorldReaderBase iworldreaderbase = context.getWorld();
      BlockPos blockpos = context.getPos();
      EnumFacing[] aenumfacing = context.getNearestLookingDirections();

      for(EnumFacing enumfacing : aenumfacing) {
         if (enumfacing.getAxis().isHorizontal()) {
            iblockstate = iblockstate.with(FACING, enumfacing.getOpposite());
            if (iblockstate.isValidPosition(iworldreaderbase, blockpos)) {
               return iblockstate;
            }
         }
      }

      return null;
   }
}
