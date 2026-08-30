package net.minecraft.world.gen.surfacebuilders;

import net.minecraft.block.state.IBlockState;

public class SurfaceBuilderConfig implements ISurfaceBuilderConfig {
   private final IBlockState topMaterial;
   private final IBlockState underMaterial;
   private final IBlockState underWaterMaterial;

   public SurfaceBuilderConfig(IBlockState topMaterial, IBlockState underMaterial, IBlockState underWaterMaterial) {
      this.topMaterial = topMaterial;
      this.underMaterial = underMaterial;
      this.underWaterMaterial = underWaterMaterial;
   }

   public IBlockState getTopMaterial() {
      return this.topMaterial;
   }

   public IBlockState getUnderMaterial() {
      return this.underMaterial;
   }

   public IBlockState getUnderWaterMaterial() {
      return this.underWaterMaterial;
   }
}
