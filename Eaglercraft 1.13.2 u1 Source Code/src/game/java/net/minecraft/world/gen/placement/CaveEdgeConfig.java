package net.minecraft.world.gen.placement;

import net.minecraft.world.gen.GenerationStage;

public class CaveEdgeConfig implements IPlacementConfig {
   final GenerationStage.Carving step;
   final float probability;

   public CaveEdgeConfig(GenerationStage.Carving step, float probability) {
      this.step = step;
      this.probability = probability;
   }
}
