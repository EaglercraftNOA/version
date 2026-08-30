package net.minecraft.entity.ai;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Items;
import net.minecraft.init.Particles;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class EntityAIBreakBlock extends EntityAIMoveToBlock {
   private final Block block;
   private final EntityLiving entity;
   private int breakingTime;

   public EntityAIBreakBlock(Block p_i48795_1_, EntityCreature creature, double speed, int yMax) {
      super(creature, speed, 24, yMax);
      this.block = p_i48795_1_;
      this.entity = creature;
   }

   public boolean shouldExecute() {
      if (!this.entity.world.getGameRules().getBoolean("mobGriefing")) {
         return false;
      } else {
         return this.entity.getRNG().nextInt(20) != 0 ? false : super.shouldExecute();
      }
   }

   protected int getRunDelay(EntityCreature p_203109_1_) {
      return 0;
   }

   public boolean shouldContinueExecuting() {
      return super.shouldContinueExecuting();
   }

   public void resetTask() {
      super.resetTask();
      this.entity.fallDistance = 1.0F;
   }

   public void startExecuting() {
      super.startExecuting();
      this.breakingTime = 0;
   }

   public void playBreakingSound(IWorld p_203114_1_, BlockPos p_203114_2_) {
   }

   public void playBrokenSound(World p_203116_1_, BlockPos p_203116_2_) {
   }

   public void tick() {
      super.tick();
      World world = this.entity.world;
      BlockPos blockpos = new BlockPos(this.entity);
      BlockPos blockpos1 = this.findTarget(blockpos, world);
      Random random = this.entity.getRNG();
      if (this.getIsAboveDestination() && blockpos1 != null) {
         if (this.breakingTime > 0) {
            this.entity.motionY = 0.3D;
            if (!world.isRemote) {
               double d0 = 0.08D;
               ((WorldServer)world).spawnParticle(new ItemParticleData(Particles.ITEM, new ItemStack(Items.EGG)), (double)blockpos1.getX() + 0.5D, (double)blockpos1.getY() + 0.7D, (double)blockpos1.getZ() + 0.5D, 3, ((double)random.nextFloat() - 0.5D) * 0.08D, ((double)random.nextFloat() - 0.5D) * 0.08D, ((double)random.nextFloat() - 0.5D) * 0.08D, (double)0.15F);
            }
         }

         if (this.breakingTime % 2 == 0) {
            this.entity.motionY = -0.3D;
            if (this.breakingTime % 6 == 0) {
               this.playBreakingSound(world, this.destinationBlock);
            }
         }

         if (this.breakingTime > 60) {
            world.removeBlock(blockpos1);
            if (!world.isRemote) {
               for(int i = 0; i < 20; ++i) {
                  double d1 = random.nextGaussian() * 0.02D;
                  double d2 = random.nextGaussian() * 0.02D;
                  double d3 = random.nextGaussian() * 0.02D;
                  ((WorldServer)world).spawnParticle(Particles.POOF, (double)blockpos1.getX() + 0.5D, (double)blockpos1.getY(), (double)blockpos1.getZ() + 0.5D, 1, d1, d2, d3, (double)0.15F);
               }

               this.playBrokenSound(world, this.destinationBlock);
            }
         }

         ++this.breakingTime;
      }

   }

   @Nullable
   private BlockPos findTarget(BlockPos p_203115_1_, IBlockReader p_203115_2_) {
      if (p_203115_2_.getBlockState(p_203115_1_).getBlock() == this.block) {
         return p_203115_1_;
      } else {
         BlockPos[] ablockpos = new BlockPos[]{p_203115_1_.down(), p_203115_1_.west(), p_203115_1_.east(), p_203115_1_.north(), p_203115_1_.south(), p_203115_1_.down().down()};

         for(BlockPos blockpos : ablockpos) {
            if (p_203115_2_.getBlockState(blockpos).getBlock() == this.block) {
               return blockpos;
            }
         }

         return null;
      }
   }

   protected boolean shouldMoveTo(IWorldReaderBase worldIn, BlockPos pos) {
      Block block = worldIn.getBlockState(pos).getBlock();
      return block == this.block && worldIn.getBlockState(pos.up()).isAir() && worldIn.getBlockState(pos.up(2)).isAir();
   }
}
