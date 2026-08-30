package net.minecraft.block;

import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockPane extends BlockFourWay {
   protected BlockPane(Block.Properties builder) {
      super(1.0F, 1.0F, 16.0F, 16.0F, 16.0F, builder);
      this.setDefaultState(this.stateContainer.getBaseState().with(NORTH, Boolean.valueOf(false)).with(EAST, Boolean.valueOf(false)).with(SOUTH, Boolean.valueOf(false)).with(WEST, Boolean.valueOf(false)).with(WATERLOGGED, Boolean.valueOf(false)));
   }

   public IBlockState getStateForPlacement(BlockItemUseContext context) {
      IBlockReader iblockreader = context.getWorld();
      BlockPos blockpos = context.getPos();
      IFluidState ifluidstate = context.getWorld().getFluidState(context.getPos());
      BlockPos blockpos1 = blockpos.north();
      BlockPos blockpos2 = blockpos.south();
      BlockPos blockpos3 = blockpos.west();
      BlockPos blockpos4 = blockpos.east();
      IBlockState iblockstate = iblockreader.getBlockState(blockpos1);
      IBlockState iblockstate1 = iblockreader.getBlockState(blockpos2);
      IBlockState iblockstate2 = iblockreader.getBlockState(blockpos3);
      IBlockState iblockstate3 = iblockreader.getBlockState(blockpos4);
      return this.getDefaultState().with(NORTH, Boolean.valueOf(this.attachesTo(iblockstate, iblockstate.getBlockFaceShape(iblockreader, blockpos1, EnumFacing.SOUTH)))).with(SOUTH, Boolean.valueOf(this.attachesTo(iblockstate1, iblockstate1.getBlockFaceShape(iblockreader, blockpos2, EnumFacing.NORTH)))).with(WEST, Boolean.valueOf(this.attachesTo(iblockstate2, iblockstate2.getBlockFaceShape(iblockreader, blockpos3, EnumFacing.EAST)))).with(EAST, Boolean.valueOf(this.attachesTo(iblockstate3, iblockstate3.getBlockFaceShape(iblockreader, blockpos4, EnumFacing.WEST)))).with(WATERLOGGED, Boolean.valueOf(ifluidstate.getFluid() == Fluids.WATER));
   }

   public IBlockState updatePostPlacement(IBlockState stateIn, EnumFacing facing, IBlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
      if (stateIn.get(WATERLOGGED)) {
         worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
      }

      return facing.getAxis().isHorizontal() ? stateIn.with(FACING_TO_PROPERTY_MAP.get(facing), Boolean.valueOf(this.attachesTo(facingState, facingState.getBlockFaceShape(worldIn, facingPos, facing.getOpposite())))) : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isSideInvisible(IBlockState state, IBlockState adjacentBlockState, EnumFacing side) {
      if (adjacentBlockState.getBlock() == this) {
         if (!side.getAxis().isHorizontal()) {
            return true;
         }

         if (state.get(FACING_TO_PROPERTY_MAP.get(side)) && adjacentBlockState.get(FACING_TO_PROPERTY_MAP.get(side.getOpposite()))) {
            return true;
         }
      }

      return super.isSideInvisible(state, adjacentBlockState, side);
   }

   public final boolean attachesTo(IBlockState p_196417_1_, BlockFaceShape p_196417_2_) {
      Block block = p_196417_1_.getBlock();
      return !shouldSkipAttachment(block) && p_196417_2_ == BlockFaceShape.SOLID || p_196417_2_ == BlockFaceShape.MIDDLE_POLE_THIN;
   }

   public static boolean shouldSkipAttachment(Block p_196418_0_) {
      return p_196418_0_ instanceof BlockShulkerBox || p_196418_0_ instanceof BlockLeaves || p_196418_0_ == Blocks.BEACON || p_196418_0_ == Blocks.CAULDRON || p_196418_0_ == Blocks.GLOWSTONE || p_196418_0_ == Blocks.ICE || p_196418_0_ == Blocks.SEA_LANTERN || p_196418_0_ == Blocks.PISTON || p_196418_0_ == Blocks.STICKY_PISTON || p_196418_0_ == Blocks.PISTON_HEAD || p_196418_0_ == Blocks.MELON || p_196418_0_ == Blocks.PUMPKIN || p_196418_0_ == Blocks.CARVED_PUMPKIN || p_196418_0_ == Blocks.JACK_O_LANTERN || p_196418_0_ == Blocks.BARRIER;
   }

   protected boolean canSilkHarvest() {
      return true;
   }

   public BlockRenderLayer getRenderLayer() {
      return BlockRenderLayer.CUTOUT_MIPPED;
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
      builder.add(NORTH, EAST, WEST, SOUTH, WATERLOGGED);
   }

   public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return face != EnumFacing.UP && face != EnumFacing.DOWN ? BlockFaceShape.MIDDLE_POLE_THIN : BlockFaceShape.CENTER_SMALL;
   }
}
