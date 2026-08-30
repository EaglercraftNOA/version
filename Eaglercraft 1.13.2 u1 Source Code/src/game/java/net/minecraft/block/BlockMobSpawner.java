package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockMobSpawner extends BlockContainer {
   protected BlockMobSpawner(Block.Properties builder) {
      super(builder);
   }

   public TileEntity createNewTileEntity(IBlockReader worldIn) {
      return new TileEntityMobSpawner();
   }

   public IItemProvider getItemDropped(IBlockState state, World worldIn, BlockPos pos, int fortune) {
      return Items.AIR;
   }

   public void dropBlockAsItemWithChance(IBlockState state, World worldIn, BlockPos pos, float chancePerItem, int fortune) {
      super.dropBlockAsItemWithChance(state, worldIn, pos, chancePerItem, fortune);
      int i = 15 + worldIn.rand.nextInt(15) + worldIn.rand.nextInt(15);
      this.dropXpOnBlockBreak(worldIn, pos, i);
   }

   public EnumBlockRenderType getRenderType(IBlockState state) {
      return EnumBlockRenderType.MODEL;
   }

   public BlockRenderLayer getRenderLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   public ItemStack getItem(IBlockReader worldIn, BlockPos pos, IBlockState state) {
      return ItemStack.EMPTY;
   }
}
