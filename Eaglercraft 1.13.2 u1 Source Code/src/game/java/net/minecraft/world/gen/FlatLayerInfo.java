package net.minecraft.world.gen;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.registry.IRegistry;

public class FlatLayerInfo {
   private final IBlockState layerMaterial;
   private final int layerCount;
   private int layerMinimumY;

   public FlatLayerInfo(int p_i45467_1_, Block layerMaterialIn) {
      this.layerCount = p_i45467_1_;
      this.layerMaterial = layerMaterialIn.getDefaultState();
   }

   public int getLayerCount() {
      return this.layerCount;
   }

   public IBlockState getLayerMaterial() {
      return this.layerMaterial;
   }

   public int getMinY() {
      return this.layerMinimumY;
   }

   public void setMinY(int minY) {
      this.layerMinimumY = minY;
   }

   public String toString() {
      return (this.layerCount > 1 ? this.layerCount + "*" : "") + IRegistry.BLOCK.getKey(this.layerMaterial.getBlock());
   }
}
