package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BlockBarrier extends Block {
   protected BlockBarrier(Block.Properties properties) {
      super(properties);
   }

   public boolean propagatesSkylightDown(IBlockState state, IBlockReader reader, BlockPos pos) {
      return true;
   }

   public EnumBlockRenderType getRenderType(IBlockState state) {
      return EnumBlockRenderType.INVISIBLE;
   }

   public boolean isSolid(IBlockState state) {
      return false;
   }

   @OnlyIn(Dist.CLIENT)
   public float getAmbientOcclusionLightValue(IBlockState state) {
      return 1.0F;
   }

   public void dropBlockAsItemWithChance(IBlockState state, World worldIn, BlockPos pos, float chancePerItem, int fortune) {
   }
}
