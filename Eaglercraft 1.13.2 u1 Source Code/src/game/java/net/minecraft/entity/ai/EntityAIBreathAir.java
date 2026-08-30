package net.minecraft.entity.ai;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.MoverType;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.PathType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorldReaderBase;

public class EntityAIBreathAir extends EntityAIBase {
   private final EntityCreature field_205142_a;

   public EntityAIBreathAir(EntityCreature p_i48940_1_) {
      this.field_205142_a = p_i48940_1_;
      this.setMutexBits(3);
   }

   public boolean shouldExecute() {
      return this.field_205142_a.getAir() < 140;
   }

   public boolean shouldContinueExecuting() {
      return this.shouldExecute();
   }

   public boolean isInterruptible() {
      return false;
   }

   public void startExecuting() {
      this.navigate();
   }

   private void navigate() {
      Iterable<BlockPos.MutableBlockPos> iterable = BlockPos.MutableBlockPos.getAllInBoxMutable(MathHelper.floor(this.field_205142_a.posX - 1.0D), MathHelper.floor(this.field_205142_a.posY), MathHelper.floor(this.field_205142_a.posZ - 1.0D), MathHelper.floor(this.field_205142_a.posX + 1.0D), MathHelper.floor(this.field_205142_a.posY + 8.0D), MathHelper.floor(this.field_205142_a.posZ + 1.0D));
      BlockPos blockpos = null;

      for(BlockPos blockpos1 : iterable) {
         if (this.canBreatheAt(this.field_205142_a.world, blockpos1)) {
            blockpos = blockpos1;
            break;
         }
      }

      if (blockpos == null) {
         blockpos = new BlockPos(this.field_205142_a.posX, this.field_205142_a.posY + 8.0D, this.field_205142_a.posZ);
      }

      this.field_205142_a.getNavigator().tryMoveToXYZ((double)blockpos.getX(), (double)(blockpos.getY() + 1), (double)blockpos.getZ(), 1.0D);
   }

   public void tick() {
      this.navigate();
      this.field_205142_a.moveRelative(this.field_205142_a.moveStrafing, this.field_205142_a.moveVertical, this.field_205142_a.moveForward, 0.02F);
      this.field_205142_a.move(MoverType.SELF, this.field_205142_a.motionX, this.field_205142_a.motionY, this.field_205142_a.motionZ);
   }

   private boolean canBreatheAt(IWorldReaderBase worldIn, BlockPos pos) {
      IBlockState iblockstate = worldIn.getBlockState(pos);
      return (worldIn.getFluidState(pos).isEmpty() || iblockstate.getBlock() == Blocks.BUBBLE_COLUMN) && iblockstate.allowsMovement(worldIn, pos, PathType.LAND);
   }
}
