package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public abstract class BlockAbstractBanner extends BlockContainer {
   private final EnumDyeColor color;

   protected BlockAbstractBanner(EnumDyeColor p_i48453_1_, Block.Properties properties) {
      super(properties);
      this.color = p_i48453_1_;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public boolean canSpawnInBlock() {
      return true;
   }

   public TileEntity createNewTileEntity(IBlockReader worldIn) {
      return new TileEntityBanner(this.color);
   }

   public IItemProvider getItemDropped(IBlockState state, World worldIn, BlockPos pos, int fortune) {
      return Items.WHITE_BANNER;
   }

   public BlockFaceShape getBlockFaceShape(IBlockReader worldIn, IBlockState state, BlockPos pos, EnumFacing face) {
      return BlockFaceShape.UNDEFINED;
   }

   public ItemStack getItem(IBlockReader worldIn, BlockPos pos, IBlockState state) {
      TileEntity tileentity = worldIn.getTileEntity(pos);
      return tileentity instanceof TileEntityBanner ? ((TileEntityBanner)tileentity).getItem(state) : super.getItem(worldIn, pos, state);
   }

   public void dropBlockAsItemWithChance(IBlockState state, World worldIn, BlockPos pos, float chancePerItem, int fortune) {
      spawnAsEntity(worldIn, pos, this.getItem(worldIn, pos, state));
   }

   public void harvestBlock(World worldIn, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack stack) {
      if (te instanceof TileEntityBanner) {
         spawnAsEntity(worldIn, pos, ((TileEntityBanner)te).getItem(state));
         player.addStat(StatList.BLOCK_MINED.get(this));
      } else {
         super.harvestBlock(worldIn, player, pos, state, (TileEntity)null, stack);
      }

   }

   public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, @Nullable EntityLivingBase placer, ItemStack stack) {
      TileEntity tileentity = worldIn.getTileEntity(pos);
      if (tileentity instanceof TileEntityBanner) {
         ((TileEntityBanner)tileentity).loadFromItemStack(stack, this.color);
      }

   }

   public EnumDyeColor getColor() {
      return this.color;
   }
}
