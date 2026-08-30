package net.minecraft.entity.boss.dragon.phase;

import java.util.Arrays;
import net.minecraft.entity.boss.EntityDragon;

public class PhaseType<T extends IPhase> {
   private static PhaseType<?>[] phases = new PhaseType[0];
   public static final PhaseType<PhaseHoldingPattern> HOLDING_PATTERN = create(PhaseHoldingPattern::new, "HoldingPattern");
   public static final PhaseType<PhaseStrafePlayer> STRAFE_PLAYER = create(PhaseStrafePlayer::new, "StrafePlayer");
   public static final PhaseType<PhaseLandingApproach> LANDING_APPROACH = create(PhaseLandingApproach::new, "LandingApproach");
   public static final PhaseType<PhaseLanding> LANDING = create(PhaseLanding::new, "Landing");
   public static final PhaseType<PhaseTakeoff> TAKEOFF = create(PhaseTakeoff::new, "Takeoff");
   public static final PhaseType<PhaseSittingFlaming> SITTING_FLAMING = create(PhaseSittingFlaming::new, "SittingFlaming");
   public static final PhaseType<PhaseSittingScanning> SITTING_SCANNING = create(PhaseSittingScanning::new, "SittingScanning");
   public static final PhaseType<PhaseSittingAttacking> SITTING_ATTACKING = create(PhaseSittingAttacking::new, "SittingAttacking");
   public static final PhaseType<PhaseChargingPlayer> CHARGING_PLAYER = create(PhaseChargingPlayer::new, "ChargingPlayer");
   public static final PhaseType<PhaseDying> DYING = create(PhaseDying::new, "Dying");
   public static final PhaseType<PhaseHover> HOVER = create(PhaseHover::new, "Hover");
   private final IPhaseFactory<? extends IPhase> factory;
   private final int id;
   private final String name;

   private PhaseType(int idIn, IPhaseFactory<? extends IPhase> factoryIn, String nameIn) {
      this.id = idIn;
      this.factory = factoryIn;
      this.name = nameIn;
   }

   public IPhase createPhase(EntityDragon dragon) {
      try {
         return this.factory.create(dragon);
      } catch (Exception exception) {
         throw new Error(exception);
      }
   }

   public int getId() {
      return this.id;
   }

   public String toString() {
      return this.name + " (#" + this.id + ")";
   }

   public static PhaseType<?> getById(int idIn) {
      return idIn >= 0 && idIn < phases.length ? phases[idIn] : HOLDING_PATTERN;
   }

   public static int getTotalPhases() {
      return phases.length;
   }

   private static <T extends IPhase> PhaseType<T> create(IPhaseFactory<T> phaseIn, String nameIn) {
      PhaseType<T> phasetype = new PhaseType<>(phases.length, phaseIn, nameIn);
      phases = Arrays.copyOf(phases, phases.length + 1);
      phases[phasetype.getId()] = phasetype;
      return phasetype;
   }
}
