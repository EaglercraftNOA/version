package net.minecraft.block;

import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;

public class BlockWall extends BlockFourWay {
   public static final BooleanProperty UP = BlockStateProperties.UP;
   private final VoxelShape[] wallShapes;
   private final VoxelShape[] wallCollisionShapes;

   public BlockWall(Block.Properties properties) {
      super(0.0F, 3.0F, 0.0F, 14.0F, 24.0F, properties);
      this.setDefaultState(this.stateContainer.getBaseState().with(UP, Boolean.valueOf(true)).with(NORTH, Boolean.valueOf(false)).with(EAST, Boolean.valueOf(false)).with(SOUTH, Boolean.valueOf(false)).with(WEST, Boolean.valueOf(false)).with(WATERLOGGED, Boolean.valueOf(false)));
      this.wallShapes = this.func_196408_a(4.0F, 3.0F, 16.0F, 0.0F, 14.0F);
      this.wallCollisionShapes = this.func_196408_a(4.0F, 3.0F, 24.0F, 0.0F, 24.0F);
   }

   public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      return state.get(UP) ? this.wallShapes[this.getIndex(state)] : super.getShape(state, worldIn, pos);
   }

   public VoxelShape getCollisionShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      return state.get(UP) ? this.wallCollisionShapes[this.getIndex(state)] : super.getCollisionShape(state, worldIn, pos);
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public boolean allowsMovement(IBlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
      return false;
   }

   private boolean attachesTo(IBlockState p_196421_1_, BlockFaceShape p_196421_2_) {
      Block block = p_196421_1_.getBlock();
      boolean flag = p_196421_2_ == BlockFaceShape.MIDDLE_POLE_THICK || p_196421_2_ == BlockFaceShape.MIDDLE_POLE && block instanceof BlockFenceGate;
      return !isExcepBlockForAttachWithPiston(block) && p_196421_2_ == BlockFaceShape.SOLID || flag;
   }

   public static boolean isExcepBlockForAttachWithPiston(Block p_194143_0_) {
      return Block.isExceptBlockForAttachWithPiston(p_194143_0_) || p_194143_0_ == Blocks.BARRIER || p_194143_0_ == Blocks.MELON || p_194143_0_ == Blocks.PUMPKIN || p_194143_0_ == Blocks.CARVED_PUMPKIN || p_194143_0_ == Blocks.JACK_O_LANTERN || p_194143_0_ == Blocks.FROSTED_ICE || p_194143_0_ == Blocks.TNT;
   }

   public IBlockState getStateForPlacement(BlockItemUseContext context) {
      IWorldReaderBase iworldreaderbase = context.getWorld();
      BlockPos blockpos = context.getPos();
      IFluidState ifluidstate = context.getWorld().getFluidState(context.getPos());
      BlockPos blockpos1 = blockpos.north();
      BlockPos blockpos2 = blockpos.east();
      BlockPos blockpos3 = blockpos.south();
      BlockPos blockpos4 = blockpos.west();
      IBlockState iblockstate = iworldreaderbase.getBlockState(blockpos1);
      IBlockState iblockstate1 = iworldreaderbase.getBlockState(blockpos2);
      IBlockState iblockstate2 = iworldreaderbase.getBlockState(blockpos3);
      IBlockState iblockstate3 = iworldreaderbase.getBlockState(blockpos4);
      boolean flag = this.attachesTo(iblockstate, iblockstate.getBlockFaceShape(iworldreaderbase, blockpos1, EnumFacing.SOUTH));
      boolean flag1 = this.attachesTo(iblockstate1, iblockstate1.getBlockFaceShape(iworldreaderbase, blockpos2, EnumFacing.WEST));
      boolean flag2 = this.attachesTo(iblockstate2, iblockstate2.getBlockFaceShape(iworldreaderbase, blockpos3, EnumFacing.NORTH));
      boolean flag3 = this.attachesTo(iblockstate3, iblockstate3.getBlockFaceShape(iworldreaderbase, blockpos4, EnumFacing.EAST));
      boolean flag4 = (!flag || flag1 || !flag2 || flag3) && (flag || !flag1 || flag2 || !flag3);
      return this.getDefaultState().with(UP, Boolean.valueOf(flag4 || !iworldreaderbase.isAirBlock(blockpos.up()))).with(NORTH, Boolean.valueOf(flag)).with(EAST, Boolean.valueOf(flag1)).with(SOUTH, Boolean.valueOf(flag2)).with(WEST, Boolean.valueOf(flag3)).with(WATERLOGGED, Boolean.valueOf(ifluidstate.getFluid() == Fluids.WATER));
   }

   public IBlockState updatePostPlacement(IBlockState stateIn, EnumFacing facing, IBlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
      if (stateIn.get(WATERLOGGED)) {
         worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
      }

      if (facing == EnumFacing.DOWN) {
         return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
      } else {
         boolean flag = facing == EnumFacing.NORTH ? this.attachesTo(facingState, facingState.getBlockFaceShape(worldIn, facingPos, facing.getOpposite())) : stateIn.get(NORTH);
         boolean flag1 = facing == EnumFacing.EAST ? this.attachesTo(facingState, facingState.getBlockFaceShape(worldIn, facingPos, facing.getOpposite())) : stateIn.get(EAST);
         boolean flag2 = facing == EnumFacing.SOUTH ? this.attachesTo(facingState, facingState.getBlockFaceShape(worldIn, facingPos, facing.getOpposite())) : stateIn.get(SOUTH);
         boolean flag3 = facing == EnumFacing.WEST ? this.attachesTo(facingState, facingState.getBlockFaceShape(worldIn, facingPos, facing.getOpposite())) : stateIn.get(WEST);
         boolean flag4 = (!flag || flag1 || !flag2 || flag3) && (flag || !flag1 || flag2 || !flag3);
         return stateIn.with(UP, Boolean.valueOf(flag4 || !worldIn.isAirBlock(currentPos.up()))).with(NORTH, Boolean.valueOf(flag)).with(EAST, Boolean.valueOf(flag1)).with(SOUTH, Boolean.valueOf(flag2)).with(WEST, Boolean.valueOf(flag3));
      }
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
      builder.add(UP, NORTH, EAST, WEST, SOUTH, WATERLOGGED);
   }

   public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return face != EnumFacing.UP && face != EnumFacing.DOWN ? BlockFaceShape.MIDDLE_POLE_THICK : BlockFaceShape.CENTER_BIG;
   }
}
