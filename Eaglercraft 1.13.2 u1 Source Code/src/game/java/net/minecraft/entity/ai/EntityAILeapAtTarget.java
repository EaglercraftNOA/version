package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;

public class EntityAILeapAtTarget extends EntityAIBase {
   private final EntityLiving leaper;
   private EntityLivingBase leapTarget;
   private final float leapMotionY;

   public EntityAILeapAtTarget(EntityLiving leapingEntity, float leapMotionYIn) {
      this.leaper = leapingEntity;
      this.leapMotionY = leapMotionYIn;
      this.setMutexBits(5);
   }

   public boolean shouldExecute() {
      this.leapTarget = this.leaper.getAttackTarget();
      if (this.leapTarget == null) {
         return false;
      } else {
         double d0 = this.leaper.getDistanceSq(this.leapTarget);
         if (!(d0 < 4.0D) && !(d0 > 16.0D)) {
            if (!this.leaper.onGround) {
               return false;
            } else {
               return this.leaper.getRNG().nextInt(5) == 0;
            }
         } else {
            return false;
         }
      }
   }

   public boolean shouldContinueExecuting() {
      return !this.leaper.onGround;
   }

   public void startExecuting() {
      double d0 = this.leapTarget.posX - this.leaper.posX;
      double d1 = this.leapTarget.posZ - this.leaper.posZ;
      float f = MathHelper.sqrt(d0 * d0 + d1 * d1);
      if ((double)f >= 1.0E-4D) {
         this.leaper.motionX += d0 / (double)f * 0.5D * (double)0.8F + this.leaper.motionX * (double)0.2F;
         this.leaper.motionZ += d1 / (double)f * 0.5D * (double)0.8F + this.leaper.motionZ * (double)0.2F;
      }

      this.leaper.motionY = (double)this.leapMotionY;
   }
}
