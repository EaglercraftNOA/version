package net.minecraft.world.gen.feature;

import net.minecraft.fluid.Fluid;

public class LiquidsConfig implements IFeatureConfig {
   public final Fluid fluid;

   public LiquidsConfig(Fluid fluid) {
      this.fluid = fluid;
   }
}
