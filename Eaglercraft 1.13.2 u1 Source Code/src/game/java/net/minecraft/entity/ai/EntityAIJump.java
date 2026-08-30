package net.minecraft.entity.ai;

import net.minecraft.entity.passive.EntityDolphin;
import net.minecraft.fluid.IFluidState;
import net.minecraft.init.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

public class EntityAIJump extends EntityAIBase {
   private static final int[] field_211697_a = new int[]{0, 1, 4, 5, 6, 7};
   private final EntityDolphin dolphin;
   private final int chance;
   private boolean outOfWater;

   public EntityAIJump(EntityDolphin p_i48938_1_, int p_i48938_2_) {
      this.dolphin = p_i48938_1_;
      this.chance = p_i48938_2_;
      this.setMutexBits(5);
   }

   public boolean shouldExecute() {
      if (this.dolphin.getRNG().nextInt(this.chance) != 0) {
         return false;
      } else {
         EnumFacing enumfacing = this.dolphin.getAdjustedHorizontalFacing();
         int i = enumfacing.getXOffset();
         int j = enumfacing.getZOffset();
         BlockPos blockpos = new BlockPos(this.dolphin);

         for(int k : field_211697_a) {
            if (!this.canSwimIn(blockpos, i, j, k) || !this.canLeapIn(blockpos, i, j, k)) {
               return false;
            }
         }

         return true;
      }
   }

   private boolean canSwimIn(BlockPos pos, int dx, int dz, int horizontalMultiplier) {
      BlockPos blockpos = pos.add(dx * horizontalMultiplier, 0, dz * horizontalMultiplier);
      return this.dolphin.world.getFluidState(blockpos).isTagged(FluidTags.WATER) && !this.dolphin.world.getBlockState(blockpos).getMaterial().blocksMovement();
   }

   private boolean canLeapIn(BlockPos pos, int dx, int dz, int horizontalMultiplier) {
      return this.dolphin.world.getBlockState(pos.add(dx * horizontalMultiplier, 1, dz * horizontalMultiplier)).isAir() && this.dolphin.world.getBlockState(pos.add(dx * horizontalMultiplier, 2, dz * horizontalMultiplier)).isAir();
   }

   public boolean shouldContinueExecuting() {
      return (!(this.dolphin.motionY * this.dolphin.motionY < (double)0.03F) || this.dolphin.rotationPitch == 0.0F || !(Math.abs(this.dolphin.rotationPitch) < 10.0F) || !this.dolphin.isInWater()) && !this.dolphin.onGround;
   }

   public boolean isInterruptible() {
      return false;
   }

   public void startExecuting() {
      EnumFacing enumfacing = this.dolphin.getAdjustedHorizontalFacing();
      this.dolphin.motionX += (double)enumfacing.getXOffset() * 0.6D;
      this.dolphin.motionY += 0.7D;
      this.dolphin.motionZ += (double)enumfacing.getZOffset() * 0.6D;
      this.dolphin.getNavigator().clearPath();
   }

   public void resetTask() {
      this.dolphin.rotationPitch = 0.0F;
   }

   public void tick() {
      boolean flag = this.outOfWater;
      if (!flag) {
         IFluidState ifluidstate = this.dolphin.world.getFluidState(new BlockPos(this.dolphin));
         this.outOfWater = ifluidstate.isTagged(FluidTags.WATER);
      }

      if (this.outOfWater && !flag) {
         this.dolphin.playSound(SoundEvents.ENTITY_DOLPHIN_JUMP, 1.0F, 1.0F);
      }

      if (this.dolphin.motionY * this.dolphin.motionY < (double)0.03F && this.dolphin.rotationPitch != 0.0F) {
         this.dolphin.rotationPitch = this.updateRotation(this.dolphin.rotationPitch, 0.0F, 0.2F);
      } else {
         double d2 = Math.sqrt(this.dolphin.motionX * this.dolphin.motionX + this.dolphin.motionY * this.dolphin.motionY + this.dolphin.motionZ * this.dolphin.motionZ);
         double d0 = Math.sqrt(this.dolphin.motionX * this.dolphin.motionX + this.dolphin.motionZ * this.dolphin.motionZ);
         double d1 = Math.signum(-this.dolphin.motionY) * Math.acos(d0 / d2) * (double)(180F / (float)Math.PI);
         this.dolphin.rotationPitch = (float)d1;
      }

   }

   protected float updateRotation(float p_205147_1_, float p_205147_2_, float p_205147_3_) {
      float f;
      for(f = p_205147_2_ - p_205147_1_; f < -180.0F; f += 360.0F) {
         ;
      }

      while(f >= 180.0F) {
         f -= 360.0F;
      }

      return p_205147_1_ + p_205147_3_ * f;
   }
}
