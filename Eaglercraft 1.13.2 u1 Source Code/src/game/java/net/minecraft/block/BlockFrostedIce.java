package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class BlockFrostedIce extends BlockIce {
   public static final IntegerProperty AGE = BlockStateProperties.AGE_0_3;

   public BlockFrostedIce(Block.Properties properties) {
      super(properties);
      this.setDefaultState(this.stateContainer.getBaseState().with(AGE, Integer.valueOf(0)));
   }

   public void tick(IBlockState state, World worldIn, BlockPos pos, Random random) {
      if ((random.nextInt(3) == 0 || this.shouldMelt(worldIn, pos, 4)) && worldIn.getLight(pos) > 11 - state.get(AGE) - state.getOpacity(worldIn, pos) && this.slightlyMelt(state, worldIn, pos)) {
         try (BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain()) {
            for(EnumFacing enumfacing : EnumFacing._VALUES) {
               blockpos$pooledmutableblockpos.setPos(pos).move(enumfacing);
               IBlockState iblockstate = worldIn.getBlockState(blockpos$pooledmutableblockpos);
               if (iblockstate.getBlock() == this && !this.slightlyMelt(iblockstate, worldIn, blockpos$pooledmutableblockpos)) {
                  worldIn.getPendingBlockTicks().scheduleTick(blockpos$pooledmutableblockpos, this, MathHelper.nextInt(random, 20, 40));
               }
            }
         }

      } else {
         worldIn.getPendingBlockTicks().scheduleTick(pos, this, MathHelper.nextInt(random, 20, 40));
      }
   }

   private boolean slightlyMelt(IBlockState state, World worldIn, BlockPos pos) {
      int i = state.get(AGE);
      if (i < 3) {
         worldIn.setBlockState(pos, state.with(AGE, Integer.valueOf(i + 1)), 2);
         return false;
      } else {
         this.turnIntoWater(state, worldIn, pos);
         return true;
      }
   }

   public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos) {
      if (blockIn == this && this.shouldMelt(worldIn, pos, 2)) {
         this.turnIntoWater(state, worldIn, pos);
      }

      super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
   }

   private boolean shouldMelt(IBlockReader worldIn, BlockPos pos, int neighborsRequired) {
      int i = 0;

      try (BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain()) {
         for(EnumFacing enumfacing : EnumFacing._VALUES) {
            blockpos$pooledmutableblockpos.setPos(pos).move(enumfacing);
            if (worldIn.getBlockState(blockpos$pooledmutableblockpos).getBlock() == this) {
               ++i;
               if (i >= neighborsRequired) {
                  boolean flag = false;
                  return flag;
               }
            }
         }

         return true;
      }
   }

   protected void fillStateContainer(StateContainer.Builder<Block, IBlockState> builder) {
      builder.add(AGE);
   }

   public ItemStack getItem(IBlockReader worldIn, BlockPos pos, IBlockState state) {
      return ItemStack.EMPTY;
   }
}
