package net.minecraft.entity.ai;

import java.util.List;
import java.util.Random;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Particles;
import net.minecraft.stats.StatList;
import net.minecraft.world.World;

public class EntityAIMate extends EntityAIBase {
   protected final EntityAnimal animal;
   private final Class<? extends EntityAnimal> mateClass;
   protected World world;
   protected EntityAnimal targetMate;
   private int spawnBabyDelay;
   private final double moveSpeed;

   public EntityAIMate(EntityAnimal animal, double speedIn) {
      this(animal, speedIn, animal.getClass());
   }

   public EntityAIMate(EntityAnimal p_i47306_1_, double p_i47306_2_, Class<? extends EntityAnimal> p_i47306_4_) {
      this.animal = p_i47306_1_;
      this.world = p_i47306_1_.world;
      this.mateClass = p_i47306_4_;
      this.moveSpeed = p_i47306_2_;
      this.setMutexBits(3);
   }

   public boolean shouldExecute() {
      if (!this.animal.isInLove()) {
         return false;
      } else {
         this.targetMate = this.getNearbyMate();
         return this.targetMate != null;
      }
   }

   public boolean shouldContinueExecuting() {
      return this.targetMate.isAlive() && this.targetMate.isInLove() && this.spawnBabyDelay < 60;
   }

   public void resetTask() {
      this.targetMate = null;
      this.spawnBabyDelay = 0;
   }

   public void tick() {
      this.animal.getLookHelper().setLookPositionWithEntity(this.targetMate, 10.0F, (float)this.animal.getVerticalFaceSpeed());
      this.animal.getNavigator().tryMoveToEntityLiving(this.targetMate, this.moveSpeed);
      ++this.spawnBabyDelay;
      if (this.spawnBabyDelay >= 60 && this.animal.getDistanceSq(this.targetMate) < 9.0D) {
         this.spawnBaby();
      }

   }

   private EntityAnimal getNearbyMate() {
      List<EntityAnimal> list = this.world.getEntitiesWithinAABB(this.mateClass, this.animal.getBoundingBox().grow(8.0D));
      double d0 = Double.MAX_VALUE;
      EntityAnimal entityanimal = null;

      for(EntityAnimal entityanimal1 : list) {
         if (this.animal.canMateWith(entityanimal1) && this.animal.getDistanceSq(entityanimal1) < d0) {
            entityanimal = entityanimal1;
            d0 = this.animal.getDistanceSq(entityanimal1);
         }
      }

      return entityanimal;
   }

   protected void spawnBaby() {
      EntityAgeable entityageable = this.animal.createChild(this.targetMate);
      if (entityageable != null) {
         EntityPlayerMP entityplayermp = this.animal.getLoveCause();
         if (entityplayermp == null && this.targetMate.getLoveCause() != null) {
            entityplayermp = this.targetMate.getLoveCause();
         }

         if (entityplayermp != null) {
            entityplayermp.addStat(StatList.ANIMALS_BRED);
            CriteriaTriggers.BRED_ANIMALS.trigger(entityplayermp, this.animal, this.targetMate, entityageable);
         }

         this.animal.setGrowingAge(6000);
         this.targetMate.setGrowingAge(6000);
         this.animal.resetInLove();
         this.targetMate.resetInLove();
         entityageable.setGrowingAge(-24000);
         entityageable.setLocationAndAngles(this.animal.posX, this.animal.posY, this.animal.posZ, 0.0F, 0.0F);
         this.world.spawnEntity(entityageable);
         Random random = this.animal.getRNG();

         for(int i = 0; i < 7; ++i) {
            double d0 = random.nextGaussian() * 0.02D;
            double d1 = random.nextGaussian() * 0.02D;
            double d2 = random.nextGaussian() * 0.02D;
            double d3 = random.nextDouble() * (double)this.animal.width * 2.0D - (double)this.animal.width;
            double d4 = 0.5D + random.nextDouble() * (double)this.animal.height;
            double d5 = random.nextDouble() * (double)this.animal.width * 2.0D - (double)this.animal.width;
            this.world.addParticle(Particles.HEART, this.animal.posX + d3, this.animal.posY + d4, this.animal.posZ + d5, d0, d1, d2);
         }

         if (this.world.getGameRules().getBoolean("doMobLoot")) {
            this.world.spawnEntity(new EntityXPOrb(this.world, this.animal.posX, this.animal.posY, this.animal.posZ, random.nextInt(7) + 1));
         }

      }
   }
}
