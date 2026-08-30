package net.minecraft.entity.boss.dragon.phase;

import java.util.Arrays;
import net.minecraft.entity.boss.EntityDragon;
import net.peyton.eagler.minecraft.DragonPhaseConstructor;

public class PhaseList<T extends IPhase> {
	private static PhaseList<?>[] phases = new PhaseList[0];
	public static final PhaseList<PhaseHoldingPattern> HOLDING_PATTERN = create(PhaseHoldingPattern::new,
			"HoldingPattern");
	public static final PhaseList<PhaseStrafePlayer> STRAFE_PLAYER = create(PhaseStrafePlayer::new, "StrafePlayer");
	public static final PhaseList<PhaseLandingApproach> LANDING_APPROACH = create(PhaseLandingApproach::new,
			"LandingApproach");
	public static final PhaseList<PhaseLanding> LANDING = create(PhaseLanding::new, "Landing");
	public static final PhaseList<PhaseTakeoff> TAKEOFF = create(PhaseTakeoff::new, "Takeoff");
	public static final PhaseList<PhaseSittingFlaming> SITTING_FLAMING = create(PhaseSittingFlaming::new,
			"SittingFlaming");
	public static final PhaseList<PhaseSittingScanning> SITTING_SCANNING = create(PhaseSittingScanning::new,
			"SittingScanning");
	public static final PhaseList<PhaseSittingAttacking> SITTING_ATTACKING = create(PhaseSittingAttacking::new,
			"SittingAttacking");
	public static final PhaseList<PhaseChargingPlayer> CHARGING_PLAYER = create(PhaseChargingPlayer::new,
			"ChargingPlayer");
	public static final PhaseList<PhaseDying> DYING = create(PhaseDying::new, "Dying");
	public static final PhaseList<PhaseHover> HOVER = create(PhaseHover::new, "Hover");
	private final DragonPhaseConstructor<? extends IPhase> clazz;
	private final int id;
	private final String name;
	
	private PhaseList(int idIn, DragonPhaseConstructor<? extends IPhase> clazzIn, String nameIn) {
		this.id = idIn;
		this.clazz = clazzIn;
		this.name = nameIn;
	}

	public IPhase createPhase(EntityDragon dragon) {
		try {
			return clazz.createPhase(dragon);
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

	public static PhaseList<?> getById(int p_188738_0_) {
		return p_188738_0_ >= 0 && p_188738_0_ < phases.length ? phases[p_188738_0_] : HOLDING_PATTERN;
	}

	public static int getTotalPhases() {
		return phases.length;
	}

	private static <T extends IPhase> PhaseList<T> create(DragonPhaseConstructor<? extends IPhase> phaseIn, String nameIn) {
		PhaseList<T> phaselist = new PhaseList<T>(phases.length, phaseIn, nameIn);
		phases = (PhaseList[]) Arrays.copyOf(phases, phases.length + 1);
		phases[phaselist.getId()] = phaselist;
		return phaselist;
	}
}
