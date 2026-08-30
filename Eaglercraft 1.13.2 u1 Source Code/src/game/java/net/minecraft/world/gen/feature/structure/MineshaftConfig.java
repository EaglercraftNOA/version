package net.minecraft.world.gen.feature.structure;

import net.minecraft.world.gen.feature.IFeatureConfig;

public class MineshaftConfig implements IFeatureConfig {
   public final double probability;
   public final MineshaftStructure.Type type;

   public MineshaftConfig(double probability, MineshaftStructure.Type type) {
      this.probability = probability;
      this.type = type;
   }
}
