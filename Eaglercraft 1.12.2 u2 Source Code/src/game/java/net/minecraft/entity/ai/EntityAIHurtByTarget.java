package net.minecraft.entity.ai;

import java.util.List;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.util.math.AxisAlignedBB;

public class EntityAIHurtByTarget extends EntityAITarget {
	private final boolean entityCallsForHelp;

	/** Store the previous revengeTimer value */
	private int revengeTimerOld;
	private final Class<?>[] targetClasses;

	public EntityAIHurtByTarget(EntityCreature creatureIn, boolean entityCallsForHelpIn, Class<?>... targetClassesIn) {
		super(creatureIn, true);
		this.entityCallsForHelp = entityCallsForHelpIn;
		this.targetClasses = targetClassesIn;
		this.setMutexBits(1);
	}

	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	public boolean shouldExecute() {
		int i = this.taskOwner.getRevengeTimer();
		EntityLivingBase entitylivingbase = this.taskOwner.getAITarget();
		return i != this.revengeTimerOld && entitylivingbase != null && this.isSuitableTarget(entitylivingbase, false);
	}

	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	public void startExecuting() {
		this.taskOwner.setAttackTarget(this.taskOwner.getAITarget());
		this.target = this.taskOwner.getAttackTarget();
		this.revengeTimerOld = this.taskOwner.getRevengeTimer();
		this.unseenMemoryTicks = 300;

		if (this.entityCallsForHelp) {
			this.alertOthers();
		}

		super.startExecuting();
	}

	protected void alertOthers() {
		double d0 = this.getTargetDistance();

		List<EntityCreature> lst = this.taskOwner.world.getEntitiesWithinAABB(this.taskOwner.getClass(),
				(new AxisAlignedBB(this.taskOwner.posX, this.taskOwner.posY, this.taskOwner.posZ,
						this.taskOwner.posX + 1.0D, this.taskOwner.posY + 1.0D, this.taskOwner.posZ + 1.0D))
						.expand(d0, 10.0D, d0));

		for (int i = 0, l = lst.size(); i < l; ++i) {
			EntityCreature entitycreature = lst.get(i);
			if (this.taskOwner != entitycreature && entitycreature.getAttackTarget() == null
					&& (!(this.taskOwner instanceof EntityTameable) || ((EntityTameable) this.taskOwner)
							.getOwner() == ((EntityTameable) entitycreature).getOwner())
					&& !entitycreature.isOnSameTeam(this.taskOwner.getAITarget())) {
				boolean flag = false;

				for (int j = 0; j < this.targetClasses.length; ++j) {
					if (entitycreature.getClass() == this.targetClasses[j]) {
						flag = true;
						break;
					}
				}

				if (!flag) {
					this.setEntityAttackTarget(entitycreature, this.taskOwner.getAITarget());
				}
			}
		}
	}

	protected void setEntityAttackTarget(EntityCreature creatureIn, EntityLivingBase entityLivingBaseIn) {
		creatureIn.setAttackTarget(entityLivingBaseIn);
	}
}