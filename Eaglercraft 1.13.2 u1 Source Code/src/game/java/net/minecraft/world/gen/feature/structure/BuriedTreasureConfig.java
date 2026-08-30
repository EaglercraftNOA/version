package net.minecraft.world.gen.feature.structure;

import net.minecraft.world.gen.feature.IFeatureConfig;

public class BuriedTreasureConfig implements IFeatureConfig {
   public final float probability;

   public BuriedTreasureConfig(float probability) {
      this.probability = probability;
   }
}
