package net.minecraft.world.gen.placement;

public class LakeChanceConfig implements IPlacementConfig {
   public final int chance;

   public LakeChanceConfig(int chance) {
      this.chance = chance;
   }
}
