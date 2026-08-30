package net.minecraft.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityTrappedChest;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;

public class BlockTrappedChest extends BlockChest {
   public BlockTrappedChest(Block.Properties properties) {
      super(properties);
   }

   public TileEntity createNewTileEntity(IBlockReader worldIn) {
      return new TileEntityTrappedChest();
   }

   protected Stat<ResourceLocation> getOpenStat() {
      return StatList.CUSTOM.get(StatList.TRIGGER_TRAPPED_CHEST);
   }

   public boolean canProvidePower(IBlockState state) {
      return true;
   }

   public int getWeakPower(IBlockState blockState, IBlockReader blockAccess, BlockPos pos, EnumFacing side) {
      return MathHelper.clamp(TileEntityChest.getPlayersUsing(blockAccess, pos), 0, 15);
   }

   public int getStrongPower(IBlockState blockState, IBlockReader blockAccess, BlockPos pos, EnumFacing side) {
      return side == EnumFacing.UP ? blockState.getWeakPower(blockAccess, pos, side) : 0;
   }
}
