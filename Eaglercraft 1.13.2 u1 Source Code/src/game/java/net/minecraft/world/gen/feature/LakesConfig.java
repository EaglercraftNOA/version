package net.minecraft.world.gen.feature;

import net.minecraft.block.Block;

public class LakesConfig implements IFeatureConfig {
   public final Block block;

   public LakesConfig(Block block) {
      this.block = block;
   }
}
