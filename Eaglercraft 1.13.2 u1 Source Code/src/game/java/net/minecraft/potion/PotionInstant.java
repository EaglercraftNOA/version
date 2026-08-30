package net.minecraft.potion;

public class PotionInstant extends Potion {
   public PotionInstant(boolean isBadEffectIn, int liquidColorIn) {
      super(isBadEffectIn, liquidColorIn);
   }

   public boolean isInstant() {
      return true;
   }

   public boolean isReady(int duration, int amplifier) {
      return duration >= 1;
   }
}
