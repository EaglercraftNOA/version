package net.minecraft.world.gen.feature;

import net.minecraft.block.Block;

public class BushConfig implements IFeatureConfig {
   public final Block block;

   public BushConfig(Block block) {
      this.block = block;
   }
}
