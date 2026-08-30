package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockWeb extends Block {
   public BlockWeb(Block.Properties properties) {
      super(properties);
   }

   public void onEntityCollision(IBlockState state, World worldIn, BlockPos pos, Entity entityIn) {
      entityIn.setInWeb();
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public IItemProvider getItemDropped(IBlockState state, World worldIn, BlockPos pos, int fortune) {
      return Items.STRING;
   }

   protected boolean canSilkHarvest() {
      return true;
   }

   public BlockRenderLayer getRenderLayer() {
      return BlockRenderLayer.CUTOUT;
   }

   public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack) {
      if (!worldIn.isRemote && stack.getItem() == Items.SHEARS) {
         player.addStat(StatList.BLOCK_MINED.get(this));
         player.addExhaustion(0.005F);
         spawnAsEntity(worldIn, pos, new ItemStack(this));
      } else {
         super.harvestBlock(worldIn, player, pos, state, te, stack);
      }
   }

   public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return BlockFaceShape.UNDEFINED;
   }
}
