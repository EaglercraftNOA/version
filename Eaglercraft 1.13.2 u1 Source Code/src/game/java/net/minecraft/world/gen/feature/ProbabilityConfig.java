package net.minecraft.world.gen.feature;

public class ProbabilityConfig implements IFeatureConfig {
   public final float probability;

   public ProbabilityConfig(float probability) {
      this.probability = probability;
   }
}
