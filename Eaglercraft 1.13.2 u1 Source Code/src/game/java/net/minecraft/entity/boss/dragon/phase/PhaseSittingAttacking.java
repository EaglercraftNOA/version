package net.minecraft.entity.boss.dragon.phase;

import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.init.SoundEvents;

public class PhaseSittingAttacking extends PhaseSittingBase {
   private int attackingTicks;

   public PhaseSittingAttacking(EntityDragon dragonIn) {
      super(dragonIn);
   }

   public void clientTick() {
      this.dragon.world.playSound(this.dragon.posX, this.dragon.posY, this.dragon.posZ, SoundEvents.ENTITY_ENDER_DRAGON_GROWL, this.dragon.getSoundCategory(), 2.5F, 0.8F + this.dragon.getRNG().nextFloat() * 0.3F, false);
   }

   public void serverTick() {
      if (this.attackingTicks++ >= 40) {
         this.dragon.getPhaseManager().setPhase(PhaseType.SITTING_FLAMING);
      }

   }

   public void initPhase() {
      this.attackingTicks = 0;
   }

   public PhaseType<PhaseSittingAttacking> getType() {
      return PhaseType.SITTING_ATTACKING;
   }
}
