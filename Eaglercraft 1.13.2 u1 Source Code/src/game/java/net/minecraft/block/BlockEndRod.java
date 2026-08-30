package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Particles;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockEndRod extends BlockDirectional {
   protected static final VoxelShape END_ROD_VERTICAL_AABB = Block.makeCuboidShape(6.0D, 0.0D, 6.0D, 10.0D, 16.0D, 10.0D);
   protected static final VoxelShape END_ROD_NS_AABB = Block.makeCuboidShape(6.0D, 6.0D, 0.0D, 10.0D, 10.0D, 16.0D);
   protected static final VoxelShape END_ROD_EW_AABB = Block.makeCuboidShape(0.0D, 6.0D, 6.0D, 16.0D, 10.0D, 10.0D);

   protected BlockEndRod(Block.Properties builder) {
      super(builder);
      this.setDefaultState(this.stateContainer.getBaseState().with(FACING, EnumFacing.UP));
   }

   public IBlockState rotate(IBlockState state, Rotation rot) {
      return state.with(FACING, rot.rotate(state.get(FACING)));
   }

   public IBlockState mirror(IBlockState state, Mirror mirrorIn) {
      return state.with(FACING, mirrorIn.mirror(state.get(FACING)));
   }

   public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      switch(state.get(FACING).getAxis()) {
      case X:
      default:
         return END_ROD_EW_AABB;
      case Z:
         return END_ROD_NS_AABB;
      case Y:
         return END_ROD_VERTICAL_AABB;
      }
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public IBlockState getStateForPlacement(BlockItemUseContext context) {
      EnumFacing enumfacing = context.getFace();
      IBlockState iblockstate = context.getWorld().getBlockState(context.getPos().offset(enumfacing.getOpposite()));
      return iblockstate.getBlock() == this && iblockstate.get(FACING) == enumfacing ? this.getDefaultState().with(FACING, enumfacing.getOpposite()) : this.getDefaultState().with(FACING, enumfacing);
   }

   @OnlyIn(Dist.CLIENT)
   public void animateTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
      EnumFacing enumfacing = stateIn.get(FACING);
      double d0 = (double)pos.getX() + 0.55D - (double)(rand.nextFloat() * 0.1F);
      double d1 = (double)pos.getY() + 0.55D - (double)(rand.nextFloat() * 0.1F);
      double d2 = (double)pos.getZ() + 0.55D - (double)(rand.nextFloat() * 0.1F);
      double d3 = (double)(0.4F - (rand.nextFloat() + rand.nextFloat()) * 0.4F);
      if (rand.nextInt(5) == 0) {
         worldIn.addParticle(Particles.END_ROD, d0 + (double)enumfacing.getXOffset() * d3, d1 + (double)enumfacing.getYOffset() * d3, d2 + (double)enumfacing.getZOffset() * d3, rand.nextGaussian() * 0.005D, rand.nextGaussian() * 0.005D, rand.nextGaussian() * 0.005D);
      }

   }

   public BlockRenderLayer getRenderLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
      builder.add(FACING);
   }

   public EnumPushReaction getPushReaction(IBlockState state) {
      return EnumPushReaction.NORMAL;
   }

   public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return BlockFaceShape.UNDEFINED;
   }
}
