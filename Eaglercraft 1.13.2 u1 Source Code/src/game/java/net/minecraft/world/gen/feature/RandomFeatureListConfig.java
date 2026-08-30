package net.minecraft.world.gen.feature;

public class RandomFeatureListConfig implements IFeatureConfig {
   public final Feature<?>[] field_202454_a;
   public final IFeatureConfig[] configs;
   public final int count;

   public RandomFeatureListConfig(Feature<?>[] p_i48670_1_, IFeatureConfig[] p_i48670_2_, int p_i48670_3_) {
      this.field_202454_a = p_i48670_1_;
      this.configs = p_i48670_2_;
      this.count = p_i48670_3_;
   }
}
