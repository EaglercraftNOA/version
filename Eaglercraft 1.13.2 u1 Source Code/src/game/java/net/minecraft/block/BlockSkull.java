package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

public class BlockSkull extends BlockAbstractSkull {
   public static final IntegerProperty ROTATION = BlockStateProperties.ROTATION_0_15;
   protected static final VoxelShape SHAPE = Block.makeCuboidShape(4.0D, 0.0D, 4.0D, 12.0D, 8.0D, 12.0D);

   protected BlockSkull(BlockSkull.ISkullType type, Block.Properties properties) {
      super(type, properties);
      this.setDefaultState(this.stateContainer.getBaseState().with(ROTATION, Integer.valueOf(0)));
   }

   public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      return SHAPE;
   }

   public VoxelShape getRenderShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      return VoxelShapes.empty();
   }

   public IBlockState getStateForPlacement(BlockItemUseContext context) {
      return this.getDefaultState().with(ROTATION, Integer.valueOf(MathHelper.floor((double)(context.getPlacementYaw() * 16.0F / 360.0F) + 0.5D) & 15));
   }

   public IBlockState rotate(IBlockState state, Rotation rot) {
      return state.with(ROTATION, Integer.valueOf(rot.rotate(state.get(ROTATION), 16)));
   }

   public IBlockState mirror(IBlockState state, Mirror mirrorIn) {
      return state.with(ROTATION, Integer.valueOf(mirrorIn.mirrorRotation(state.get(ROTATION), 16)));
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
      builder.add(ROTATION);
   }

   public interface ISkullType {
   }

   public static enum Types implements BlockSkull.ISkullType {
      SKELETON,
      WITHER_SKELETON,
      PLAYER,
      ZOMBIE,
      CREEPER,
      DRAGON;
   }
}
