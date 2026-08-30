package net.minecraft.world.gen.feature;

public class TwoFeatureChoiceConfig implements IFeatureConfig {
   public final Feature<?> trueFeature;
   public final IFeatureConfig trueConfig;
   public final Feature<?> falseFeature;
   public final IFeatureConfig falseConfig;

   public <FC extends IFeatureConfig> TwoFeatureChoiceConfig(Feature<?> trueFeature, IFeatureConfig trueConfig, Feature<?> falseFeature, IFeatureConfig falseConfig) {
      this.trueFeature = trueFeature;
      this.trueConfig = trueConfig;
      this.falseFeature = falseFeature;
      this.falseConfig = falseConfig;
   }
}
