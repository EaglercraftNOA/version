package net.minecraft.world.gen.feature;

public class RandomDefaultFeatureListConfig implements IFeatureConfig {
   public final Feature<?>[] features;
   public final IFeatureConfig[] configs;
   public final float[] chances;
   public final Feature<?> defaultFeature;
   public final IFeatureConfig defaultConfig;

   public <FC extends IFeatureConfig> RandomDefaultFeatureListConfig(Feature<?>[] p_i48671_1_, IFeatureConfig[] p_i48671_2_, float[] p_i48671_3_, Feature<FC> p_i48671_4_, FC p_i48671_5_) {
      this.features = p_i48671_1_;
      this.configs = p_i48671_2_;
      this.chances = p_i48671_3_;
      this.defaultFeature = p_i48671_4_;
      this.defaultConfig = p_i48671_5_;
   }
}
