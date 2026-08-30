package net.minecraft.entity.ai;

import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.math.BlockPos;

public abstract class EntityAIDoorInteract extends EntityAIBase {
   protected EntityLiving entity;
   protected BlockPos doorPosition = BlockPos.ORIGIN;
   protected boolean doorInteract;
   private boolean hasStoppedDoorInteraction;
   private float entityPositionX;
   private float entityPositionZ;

   public EntityAIDoorInteract(EntityLiving entityIn) {
      this.entity = entityIn;
      if (!(entityIn.getNavigator() instanceof PathNavigateGround)) {
         throw new IllegalArgumentException("Unsupported mob type for DoorInteractGoal");
      }
   }

   protected boolean canDestroy() {
      if (!this.doorInteract) {
         return false;
      } else {
         IBlockState iblockstate = this.entity.world.getBlockState(this.doorPosition);
         if (!(iblockstate.getBlock() instanceof BlockDoor)) {
            this.doorInteract = false;
            return false;
         } else {
            return iblockstate.get(BlockDoor.OPEN);
         }
      }
   }

   protected void toggleDoor(boolean open) {
      if (this.doorInteract) {
         IBlockState iblockstate = this.entity.world.getBlockState(this.doorPosition);
         if (iblockstate.getBlock() instanceof BlockDoor) {
            ((BlockDoor)iblockstate.getBlock()).toggleDoor(this.entity.world, this.doorPosition, open);
         }
      }

   }

   public boolean shouldExecute() {
      if (!this.entity.collidedHorizontally) {
         return false;
      } else {
         PathNavigateGround pathnavigateground = (PathNavigateGround)this.entity.getNavigator();
         Path path = pathnavigateground.getPath();
         if (path != null && !path.isFinished() && pathnavigateground.getEnterDoors()) {
            for(int i = 0; i < Math.min(path.getCurrentPathIndex() + 2, path.getCurrentPathLength()); ++i) {
               PathPoint pathpoint = path.getPathPointFromIndex(i);
               this.doorPosition = new BlockPos(pathpoint.x, pathpoint.y + 1, pathpoint.z);
               if (!(this.entity.getDistanceSq((double)this.doorPosition.getX(), this.entity.posY, (double)this.doorPosition.getZ()) > 2.25D)) {
                  this.doorInteract = this.canInteract(this.doorPosition);
                  if (this.doorInteract) {
                     return true;
                  }
               }
            }

            this.doorPosition = (new BlockPos(this.entity)).up();
            this.doorInteract = this.canInteract(this.doorPosition);
            return this.doorInteract;
         } else {
            return false;
         }
      }
   }

   public boolean shouldContinueExecuting() {
      return !this.hasStoppedDoorInteraction;
   }

   public void startExecuting() {
      this.hasStoppedDoorInteraction = false;
      this.entityPositionX = (float)((double)((float)this.doorPosition.getX() + 0.5F) - this.entity.posX);
      this.entityPositionZ = (float)((double)((float)this.doorPosition.getZ() + 0.5F) - this.entity.posZ);
   }

   public void tick() {
      float f = (float)((double)((float)this.doorPosition.getX() + 0.5F) - this.entity.posX);
      float f1 = (float)((double)((float)this.doorPosition.getZ() + 0.5F) - this.entity.posZ);
      float f2 = this.entityPositionX * f + this.entityPositionZ * f1;
      if (f2 < 0.0F) {
         this.hasStoppedDoorInteraction = true;
      }

   }

   private boolean canInteract(BlockPos pos) {
      IBlockState iblockstate = this.entity.world.getBlockState(pos);
      return iblockstate.getBlock() instanceof BlockDoor && iblockstate.getMaterial() == Material.WOOD;
   }
}
