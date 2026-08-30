package net.minecraft.world.gen.feature;

import java.util.Random;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class ShrubFeature extends AbstractTreeFeature<NoFeatureConfig> {
   private final IBlockState leavesMetadata;
   private final IBlockState woodMetadata;

   public ShrubFeature(IBlockState p_i46450_1_, IBlockState p_i46450_2_) {
      super(false);
      this.woodMetadata = p_i46450_1_;
      this.leavesMetadata = p_i46450_2_;
   }

   public boolean place(Set<BlockPos> changedBlocks, IWorld worldIn, Random rand, BlockPos position) {
      for(IBlockState iblockstate = worldIn.getBlockState(position); (iblockstate.isAir() || iblockstate.isIn(BlockTags.LEAVES)) && position.getY() > 0; iblockstate = worldIn.getBlockState(position)) {
         position = position.down();
      }

      Block block = worldIn.getBlockState(position).getBlock();
      if (Block.isDirt(block) || block == Blocks.GRASS_BLOCK) {
         position = position.up();
         this.setLogState(changedBlocks, worldIn, position, this.woodMetadata);

         for(int i = position.getY(); i <= position.getY() + 2; ++i) {
            int j = i - position.getY();
            int k = 2 - j;

            for(int l = position.getX() - k; l <= position.getX() + k; ++l) {
               int i1 = l - position.getX();

               for(int j1 = position.getZ() - k; j1 <= position.getZ() + k; ++j1) {
                  int k1 = j1 - position.getZ();
                  if (Math.abs(i1) != k || Math.abs(k1) != k || rand.nextInt(2) != 0) {
                     BlockPos blockpos = new BlockPos(l, i, j1);
                     IBlockState iblockstate1 = worldIn.getBlockState(blockpos);
                     if (iblockstate1.isAir() || iblockstate1.isIn(BlockTags.LEAVES)) {
                        this.setBlockState(worldIn, blockpos, this.leavesMetadata);
                     }
                  }
               }
            }
         }
      }

      return true;
   }
}
