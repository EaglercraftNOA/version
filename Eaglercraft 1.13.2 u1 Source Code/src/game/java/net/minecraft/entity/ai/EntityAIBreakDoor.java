package net.minecraft.entity.ai;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.world.EnumDifficulty;

public class EntityAIBreakDoor extends EntityAIDoorInteract {
   private int breakingTime;
   private int previousBreakProgress = -1;

   public EntityAIBreakDoor(EntityLiving entityIn) {
      super(entityIn);
   }

   public boolean shouldExecute() {
      if (!super.shouldExecute()) {
         return false;
      } else if (!this.entity.world.getGameRules().getBoolean("mobGriefing")) {
         return false;
      } else {
         return !this.canDestroy();
      }
   }

   public void startExecuting() {
      super.startExecuting();
      this.breakingTime = 0;
   }

   public boolean shouldContinueExecuting() {
      double d0 = this.entity.getDistanceSq(this.doorPosition);
      return this.breakingTime <= 240 && !this.canDestroy() && d0 < 4.0D;
   }

   public void resetTask() {
      super.resetTask();
      this.entity.world.sendBlockBreakProgress(this.entity.getEntityId(), this.doorPosition, -1);
   }

   public void tick() {
      super.tick();
      if (this.entity.getRNG().nextInt(20) == 0) {
         this.entity.world.playEvent(1019, this.doorPosition, 0);
      }

      ++this.breakingTime;
      int i = (int)((float)this.breakingTime / 240.0F * 10.0F);
      if (i != this.previousBreakProgress) {
         this.entity.world.sendBlockBreakProgress(this.entity.getEntityId(), this.doorPosition, i);
         this.previousBreakProgress = i;
      }

      if (this.breakingTime == 240 && this.entity.world.getDifficulty() == EnumDifficulty.HARD) {
         this.entity.world.removeBlock(this.doorPosition);
         this.entity.world.playEvent(1021, this.doorPosition, 0);
         this.entity.world.playEvent(2001, this.doorPosition, Block.getStateId(this.entity.world.getBlockState(this.doorPosition)));
      }

   }
}
