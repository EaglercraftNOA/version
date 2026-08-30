package net.minecraft.world.gen.feature;

import net.minecraft.block.Block;

public class BlockBlobConfig implements IFeatureConfig {
   public final Block block;
   public final int startRadius;

   public BlockBlobConfig(Block block, int p_i48690_2_) {
      this.block = block;
      this.startRadius = p_i48690_2_;
   }
}
