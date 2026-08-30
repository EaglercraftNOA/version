package net.minecraft.world.gen.feature;

import net.minecraft.block.state.IBlockState;

public class IcebergConfig implements IFeatureConfig {
   public final IBlockState state;

   public IcebergConfig(IBlockState state) {
      this.state = state;
   }
}
