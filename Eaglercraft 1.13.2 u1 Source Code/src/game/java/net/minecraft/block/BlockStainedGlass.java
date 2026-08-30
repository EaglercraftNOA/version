package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockStainedGlass extends BlockBreakable {
   private final EnumDyeColor color;

   public BlockStainedGlass(EnumDyeColor colorIn, Block.Properties properties) {
      super(properties);
      this.color = colorIn;
   }

   public boolean propagatesSkylightDown(IBlockState state, IBlockReader reader, BlockPos pos) {
      return true;
   }

   public EnumDyeColor getColor() {
      return this.color;
   }

   public BlockRenderLayer getRenderLayer() {
      return BlockRenderLayer.TRANSLUCENT;
   }

   public int quantityDropped(IBlockState state, Random random) {
      return 0;
   }

   protected boolean canSilkHarvest() {
      return true;
   }

   public boolean isFullCube(IBlockState state) {
      return false;
   }

   public void onBlockAdded(IBlockState state, World worldIn, BlockPos pos, IBlockState oldState) {
      if (oldState.getBlock() != state.getBlock()) {
         if (!worldIn.isRemote) {
            BlockBeacon.updateColorAsync(worldIn, pos);
         }

      }
   }

   public void onReplaced(IBlockState state, World worldIn, BlockPos pos, IBlockState newState, boolean isMoving) {
      if (state.getBlock() != newState.getBlock()) {
         if (!worldIn.isRemote) {
            BlockBeacon.updateColorAsync(worldIn, pos);
         }

      }
   }
}
