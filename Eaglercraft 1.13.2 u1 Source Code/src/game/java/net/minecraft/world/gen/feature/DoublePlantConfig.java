package net.minecraft.world.gen.feature;

import net.minecraft.block.state.IBlockState;

public class DoublePlantConfig implements IFeatureConfig {
   public final IBlockState state;

   public DoublePlantConfig(IBlockState state) {
      this.state = state;
   }
}
