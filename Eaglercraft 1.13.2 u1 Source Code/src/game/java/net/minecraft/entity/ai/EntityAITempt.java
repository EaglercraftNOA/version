package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.pathfinding.PathNavigateGround;

public class EntityAITempt extends EntityAIBase {
   private final EntityCreature temptedEntity;
   private final double speed;
   private double targetX;
   private double targetY;
   private double targetZ;
   private double pitch;
   private double yaw;
   private EntityPlayer temptingPlayer;
   private int delayTemptCounter;
   private boolean isRunning;
   private final Ingredient temptItem;
   private final boolean scaredByPlayerMovement;

   public EntityAITempt(EntityCreature p_i47822_1_, double p_i47822_2_, Ingredient p_i47822_4_, boolean p_i47822_5_) {
      this(p_i47822_1_, p_i47822_2_, p_i47822_5_, p_i47822_4_);
   }

   public EntityAITempt(EntityCreature p_i47823_1_, double p_i47823_2_, boolean p_i47823_4_, Ingredient p_i47823_5_) {
      this.temptedEntity = p_i47823_1_;
      this.speed = p_i47823_2_;
      this.temptItem = p_i47823_5_;
      this.scaredByPlayerMovement = p_i47823_4_;
      this.setMutexBits(3);
      if (!(p_i47823_1_.getNavigator() instanceof PathNavigateGround)) {
         throw new IllegalArgumentException("Unsupported mob type for TemptGoal");
      }
   }

   public boolean shouldExecute() {
      if (this.delayTemptCounter > 0) {
         --this.delayTemptCounter;
         return false;
      } else {
         this.temptingPlayer = this.temptedEntity.world.getClosestPlayerToEntity(this.temptedEntity, 10.0D);
         if (this.temptingPlayer == null) {
            return false;
         } else {
            return this.isTempting(this.temptingPlayer.getHeldItemMainhand()) || this.isTempting(this.temptingPlayer.getHeldItemOffhand());
         }
      }
   }

   protected boolean isTempting(ItemStack stack) {
      return this.temptItem.test(stack);
   }

   public boolean shouldContinueExecuting() {
      if (this.scaredByPlayerMovement) {
         if (this.temptedEntity.getDistanceSq(this.temptingPlayer) < 36.0D) {
            if (this.temptingPlayer.getDistanceSq(this.targetX, this.targetY, this.targetZ) > 0.010000000000000002D) {
               return false;
            }

            if (Math.abs((double)this.temptingPlayer.rotationPitch - this.pitch) > 5.0D || Math.abs((double)this.temptingPlayer.rotationYaw - this.yaw) > 5.0D) {
               return false;
            }
         } else {
            this.targetX = this.temptingPlayer.posX;
            this.targetY = this.temptingPlayer.posY;
            this.targetZ = this.temptingPlayer.posZ;
         }

         this.pitch = (double)this.temptingPlayer.rotationPitch;
         this.yaw = (double)this.temptingPlayer.rotationYaw;
      }

      return this.shouldExecute();
   }

   public void startExecuting() {
      this.targetX = this.temptingPlayer.posX;
      this.targetY = this.temptingPlayer.posY;
      this.targetZ = this.temptingPlayer.posZ;
      this.isRunning = true;
   }

   public void resetTask() {
      this.temptingPlayer = null;
      this.temptedEntity.getNavigator().clearPath();
      this.delayTemptCounter = 100;
      this.isRunning = false;
   }

   public void tick() {
      this.temptedEntity.getLookHelper().setLookPositionWithEntity(this.temptingPlayer, (float)(this.temptedEntity.getHorizontalFaceSpeed() + 20), (float)this.temptedEntity.getVerticalFaceSpeed());
      if (this.temptedEntity.getDistanceSq(this.temptingPlayer) < 6.25D) {
         this.temptedEntity.getNavigator().clearPath();
      } else {
         this.temptedEntity.getNavigator().tryMoveToEntityLiving(this.temptingPlayer, this.speed);
      }

   }

   public boolean isRunning() {
      return this.isRunning;
   }
}
