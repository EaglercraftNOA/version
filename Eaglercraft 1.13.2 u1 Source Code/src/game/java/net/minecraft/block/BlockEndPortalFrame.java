package net.minecraft.block;

import com.google.common.base.Predicates;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockPattern;
import net.minecraft.block.state.pattern.BlockStateMatcher;
import net.minecraft.block.state.pattern.FactoryBlockPattern;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockEndPortalFrame extends Block {
   public static final DirectionProperty FACING = BlockHorizontal.HORIZONTAL_FACING;
   public static final BooleanProperty EYE = BlockStateProperties.EYE;
   protected static final VoxelShape BASE_SHAPE = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 13.0D, 16.0D);
   protected static final VoxelShape EYE_SHAPE = Block.makeCuboidShape(4.0D, 13.0D, 4.0D, 12.0D, 16.0D, 12.0D);
   protected static final VoxelShape BASE_WITH_EYE_SHAPE = VoxelShapes.or(BASE_SHAPE, EYE_SHAPE);
   private static BlockPattern portalShape;

   public BlockEndPortalFrame(Block.Properties properties) {
      super(properties);
      this.setDefaultState(this.stateContainer.getBaseState().with(FACING, EnumFacing.NORTH).with(EYE, Boolean.valueOf(false)));
   }

   public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      return state.get(EYE) ? BASE_WITH_EYE_SHAPE : BASE_SHAPE;
   }

   public IItemProvider getItemDropped(IBlockState state, World worldIn, BlockPos pos, int fortune) {
      return Items.AIR;
   }

   public IBlockState getStateForPlacement(BlockItemUseContext context) {
      return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite()).with(EYE, Boolean.valueOf(false));
   }

   public boolean hasComparatorInputOverride(IBlockState state) {
      return true;
   }

   public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos) {
      return blockState.get(EYE) ? 15 : 0;
   }

   public IBlockState rotate(IBlockState state, Rotation rot) {
      return state.with(FACING, rot.rotate(state.get(FACING)));
   }

   public IBlockState mirror(IBlockState state, Mirror mirrorIn) {
      return state.rotate(mirrorIn.toRotation(state.get(FACING)));
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
      builder.add(FACING, EYE);
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public static BlockPattern getOrCreatePortalShape() {
      if (portalShape == null) {
         portalShape = FactoryBlockPattern.start().aisle("?vvv?", ">???<", ">???<", ">???<", "?^^^?").where('?', BlockWorldState.hasState(BlockStateMatcher.ANY)).where('^', BlockWorldState.hasState(BlockStateMatcher.forBlock(Blocks.END_PORTAL_FRAME).where(EYE, Predicates.equalTo(true)).where(FACING, Predicates.equalTo(EnumFacing.SOUTH)))).where('>', BlockWorldState.hasState(BlockStateMatcher.forBlock(Blocks.END_PORTAL_FRAME).where(EYE, Predicates.equalTo(true)).where(FACING, Predicates.equalTo(EnumFacing.WEST)))).where('v', BlockWorldState.hasState(BlockStateMatcher.forBlock(Blocks.END_PORTAL_FRAME).where(EYE, Predicates.equalTo(true)).where(FACING, Predicates.equalTo(EnumFacing.NORTH)))).where('<', BlockWorldState.hasState(BlockStateMatcher.forBlock(Blocks.END_PORTAL_FRAME).where(EYE, Predicates.equalTo(true)).where(FACING, Predicates.equalTo(EnumFacing.EAST)))).build();
      }

      return portalShape;
   }

   public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return face == EnumFacing.DOWN ? BlockFaceShape.SOLID : BlockFaceShape.UNDEFINED;
   }

   public boolean allowsMovement(IBlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
      return false;
   }
}
