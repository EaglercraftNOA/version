package net.minecraft.block;

import net.minecraft.block.material.EnumPushReaction;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockStructureVoid extends Block {
   private static final VoxelShape SHAPE = Block.makeCuboidShape(5.0D, 5.0D, 5.0D, 11.0D, 11.0D, 11.0D);

   protected BlockStructureVoid(Block.Properties properties) {
      super(properties);
   }

   public EnumBlockRenderType getRenderType(IBlockState state) {
      return EnumBlockRenderType.INVISIBLE;
   }

   public VoxelShape getShape(IBlockState state, IBlockReader worldIn, BlockPos pos) {
      return SHAPE;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   @OnlyIn(Dist.CLIENT)
   public float getAmbientOcclusionLightValue(IBlockState state) {
      return 1.0F;
   }

   public void dropBlockAsItemWithChance(IBlockState state, World worldIn, BlockPos pos, float chancePerItem, int fortune) {
   }

   public EnumPushReaction getPushReaction(IBlockState state) {
      return EnumPushReaction.DESTROY;
   }

   public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return BlockFaceShape.UNDEFINED;
   }
}
