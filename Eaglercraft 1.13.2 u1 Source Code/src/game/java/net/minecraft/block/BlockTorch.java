package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Particles;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockTorch extends Block {
   protected static final VoxelShape SHAPE = Block.makeCuboidShape(6.0D, 0.0D, 6.0D, 10.0D, 10.0D, 10.0D);

   protected BlockTorch(Block.Properties properties) {
      super(properties);
   }

   public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      return SHAPE;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public IBlockState updatePostPlacement(IBlockState stateIn, EnumFacing facing, IBlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
      return facing == EnumFacing.DOWN && !this.isValidPosition(stateIn, worldIn, currentPos) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
   }

   public boolean isValidPosition(IBlockState state, IWorldReaderBase worldIn, BlockPos pos) {
      IBlockState iblockstate = worldIn.getBlockState(pos.down());
      Block block = iblockstate.getBlock();
      boolean flag = block instanceof BlockFence || block instanceof BlockStainedGlass || block == Blocks.GLASS || block == Blocks.COBBLESTONE_WALL || block == Blocks.MOSSY_COBBLESTONE_WALL || iblockstate.isTopSolid();
      return flag && block != Blocks.END_GATEWAY;
   }

   @OnlyIn(Dist.CLIENT)
   public void animateTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand) {
      double d0 = (double)pos.getX() + 0.5D;
      double d1 = (double)pos.getY() + 0.7D;
      double d2 = (double)pos.getZ() + 0.5D;
      worldIn.addParticle(Particles.SMOKE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
      worldIn.addParticle(Particles.FLAME, d0, d1, d2, 0.0D, 0.0D, 0.0D);
   }

   public BlockRenderLayer getRenderLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return BlockFaceShape.UNDEFINED;
   }
}
