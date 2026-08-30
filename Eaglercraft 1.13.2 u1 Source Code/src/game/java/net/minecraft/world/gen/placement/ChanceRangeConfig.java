package net.minecraft.world.gen.placement;

public class ChanceRangeConfig implements IPlacementConfig {
   public final float chance;
   public final int topOffset;
   public final int bottomOffset;
   public final int top;

   public ChanceRangeConfig(float chance, int bottomOffset, int topOffset, int top) {
      this.chance = chance;
      this.bottomOffset = bottomOffset;
      this.topOffset = topOffset;
      this.top = top;
   }
}
