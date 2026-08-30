package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;

public abstract class BlockDirtSnowySpreadable extends BlockDirtSnowy {
   protected BlockDirtSnowySpreadable(Block.Properties builder) {
      super(builder);
   }

   private static boolean canSurviveAt(IWorldReaderBase worldIn, BlockPos pos) {
      BlockPos blockpos = pos.up();
      return worldIn.getLight(blockpos) >= 4 || worldIn.getBlockState(blockpos).getOpacity(worldIn, blockpos) < worldIn.getMaxLightLevel();
   }

   private static boolean canSpreadTo(IWorldReaderBase worldIn, BlockPos pos) {
      BlockPos blockpos = pos.up();
      return worldIn.getLight(blockpos) >= 4 && worldIn.getBlockState(blockpos).getOpacity(worldIn, blockpos) < worldIn.getMaxLightLevel() && !worldIn.getFluidState(blockpos).isTagged(FluidTags.WATER);
   }

   public void tick(IBlockState state, World worldIn, BlockPos pos, Random random) {
      if (!worldIn.isRemote) {
         if (!canSurviveAt(worldIn, pos)) {
            worldIn.setBlockState(pos, Blocks.DIRT.getDefaultState());
         } else {
            if (worldIn.getLight(pos.up()) >= 9) {
               for(int i = 0; i < 4; ++i) {
                  BlockPos blockpos = pos.add(random.nextInt(3) - 1, random.nextInt(5) - 3, random.nextInt(3) - 1);
                  if (!worldIn.isBlockPresent(blockpos)) {
                     return;
                  }

                  if (worldIn.getBlockState(blockpos).getBlock() == Blocks.DIRT && canSpreadTo(worldIn, blockpos)) {
                     worldIn.setBlockState(blockpos, this.getDefaultState());
                  }
               }
            }

         }
      }
   }
}
