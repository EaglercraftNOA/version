package net.minecraft.world.gen.placement;

public class DepthAverageConfig implements IPlacementConfig {
   public final int count;
   public final int baseline;
   public final int spread;

   public DepthAverageConfig(int count, int baseline, int spread) {
      this.count = count;
      this.baseline = baseline;
      this.spread = spread;
   }
}
