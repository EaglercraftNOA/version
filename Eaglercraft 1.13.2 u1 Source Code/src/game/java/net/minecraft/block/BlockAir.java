package net.minecraft.block;

import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockAir extends Block {
   protected BlockAir(Block.Properties properties) {
      super(properties);
   }

   public EnumBlockRenderType getRenderType(IBlockState state) {
      return EnumBlockRenderType.INVISIBLE;
   }

   public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      return VoxelShapes.empty();
   }

   public boolean isCollidable(IBlockState state) {
      return false;
   }

   public void dropBlockAsItemWithChance(IBlockState state, World worldIn, BlockPos pos, float chancePerItem, int fortune) {
   }

   public boolean isAir(IBlockState state) {
      return true;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return BlockFaceShape.UNDEFINED;
   }
}
