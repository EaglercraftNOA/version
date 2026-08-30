package net.minecraft.world.gen.feature.structure;

import net.minecraft.world.gen.feature.IFeatureConfig;

public class OceanRuinConfig implements IFeatureConfig {
   public final OceanRuinStructure.Type field_204031_a;
   public final float largeProbability;
   public final float clusterProbability;

   public OceanRuinConfig(OceanRuinStructure.Type p_i48866_1_, float largeProbability, float clusterProbability) {
      this.field_204031_a = p_i48866_1_;
      this.largeProbability = largeProbability;
      this.clusterProbability = clusterProbability;
   }
}
