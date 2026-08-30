package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockTallFlower extends BlockDoublePlant implements IGrowable {
   public BlockTallFlower(Block.Properties properties) {
      super(properties);
   }

   public boolean isReplaceable(IBlockState state, BlockItemUseContext useContext) {
      return false;
   }

   public boolean canGrow(IBlockReader worldIn, BlockPos pos, IBlockState state, boolean isClient) {
      return true;
   }

   public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, IBlockState state) {
      return true;
   }

   public void grow(World worldIn, Random rand, BlockPos pos, IBlockState state) {
      spawnAsEntity(worldIn, pos, new ItemStack(this));
   }
}
