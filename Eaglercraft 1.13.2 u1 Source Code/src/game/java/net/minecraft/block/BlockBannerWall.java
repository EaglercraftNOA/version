package net.minecraft.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.EnumDyeColor;
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

public class BlockBannerWall extends BlockAbstractBanner {
   public static final DirectionProperty HORIZONTAL_FACING = BlockHorizontal.HORIZONTAL_FACING;
   private static final Map<EnumFacing, VoxelShape> BANNER_SHAPES = Maps.newEnumMap(ImmutableMap.of(EnumFacing.NORTH, Block.makeCuboidShape(0.0D, 0.0D, 14.0D, 16.0D, 12.5D, 16.0D), EnumFacing.SOUTH, Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 12.5D, 2.0D), EnumFacing.WEST, Block.makeCuboidShape(14.0D, 0.0D, 0.0D, 16.0D, 12.5D, 16.0D), EnumFacing.EAST, Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 2.0D, 12.5D, 16.0D)));

   public BlockBannerWall(EnumDyeColor color, Block.Properties properties) {
      super(color, properties);
      this.setDefaultState(this.stateContainer.getBaseState().with(HORIZONTAL_FACING, EnumFacing.NORTH));
   }

   public String getTranslationKey() {
      return this.asItem().getTranslationKey();
   }

   public boolean isValidPosition(IBlockState state, IWorldReaderBase worldIn, BlockPos pos) {
      return worldIn.getBlockState(pos.offset(state.get(HORIZONTAL_FACING).getOpposite())).getMaterial().isSolid();
   }

   public IBlockState updatePostPlacement(IBlockState stateIn, EnumFacing facing, IBlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
      return facing == stateIn.get(HORIZONTAL_FACING).getOpposite() && !stateIn.isValidPosition(worldIn, currentPos) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
   }

   public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      return BANNER_SHAPES.get(state.get(HORIZONTAL_FACING));
   }

   public IBlockState getStateForPlacement(BlockItemUseContext context) {
      IBlockState iblockstate = this.getDefaultState();
      IWorldReaderBase iworldreaderbase = context.getWorld();
      BlockPos blockpos = context.getPos();
      EnumFacing[] aenumfacing = context.getNearestLookingDirections();

      for(EnumFacing enumfacing : aenumfacing) {
         if (enumfacing.getAxis().isHorizontal()) {
            EnumFacing enumfacing1 = enumfacing.getOpposite();
            iblockstate = iblockstate.with(HORIZONTAL_FACING, enumfacing1);
            if (iblockstate.isValidPosition(iworldreaderbase, blockpos)) {
               return iblockstate;
            }
         }
      }

      return null;
   }

   public IBlockState rotate(IBlockState state, Rotation rot) {
      return state.with(HORIZONTAL_FACING, rot.rotate(state.get(HORIZONTAL_FACING)));
   }

   public IBlockState mirror(IBlockState state, Mirror mirrorIn) {
      return state.rotate(mirrorIn.toRotation(state.get(HORIZONTAL_FACING)));
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
      builder.add(HORIZONTAL_FACING);
   }
}
