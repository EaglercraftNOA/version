package net.minecraft.entity.monster;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSilverfish;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

public class EntitySilverfish extends EntityMob {
   private EntitySilverfish.AISummonSilverfish summonSilverfish;

   public EntitySilverfish(World worldIn) {
      super(EntityType.SILVERFISH, worldIn);
      this.setSize(0.4F, 0.3F);
   }

   protected void initEntityAI() {
      this.summonSilverfish = new EntitySilverfish.AISummonSilverfish(this);
      this.tasks.addTask(1, new EntityAISwimming(this));
      this.tasks.addTask(3, this.summonSilverfish);
      this.tasks.addTask(4, new EntityAIAttackMelee(this, 1.0D, false));
      this.tasks.addTask(5, new EntitySilverfish.AIHideInStone(this));
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
      this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
   }

   public double getYOffset() {
      return 0.1D;
   }

   public float getEyeHeight() {
      return 0.1F;
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(8.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
      this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(1.0D);
   }

   protected boolean canTriggerWalking() {
      return false;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_SILVERFISH_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return SoundEvents.ENTITY_SILVERFISH_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_SILVERFISH_DEATH;
   }

   protected void playStepSound(BlockPos pos, IBlockState blockIn) {
      this.playSound(SoundEvents.ENTITY_SILVERFISH_STEP, 0.15F, 1.0F);
   }

   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (this.isInvulnerableTo(source)) {
         return false;
      } else {
         if ((source instanceof EntityDamageSource || source == DamageSource.MAGIC) && this.summonSilverfish != null) {
            this.summonSilverfish.notifyHurt();
         }

         return super.attackEntityFrom(source, amount);
      }
   }

   @Nullable
   protected ResourceLocation getLootTable() {
      return LootTableList.ENTITIES_SILVERFISH;
   }

   public void tick() {
      this.renderYawOffset = this.rotationYaw;
      super.tick();
   }

   public void setRenderYawOffset(float offset) {
      this.rotationYaw = offset;
      super.setRenderYawOffset(offset);
   }

   public float getBlockPathWeight(BlockPos pos, IWorldReaderBase worldIn) {
      return BlockSilverfish.canContainSilverfish(worldIn.getBlockState(pos.down())) ? 10.0F : super.getBlockPathWeight(pos, worldIn);
   }

   protected boolean isValidLightLevel() {
      return true;
   }

   public boolean canSpawn(IWorld worldIn, boolean fromSpawner) {
      if (super.canSpawn(worldIn, fromSpawner)) {
         EntityPlayer entityplayer = worldIn.getNearestPlayerNotCreative(this, 5.0D);
         return entityplayer == null;
      } else {
         return false;
      }
   }

   public CreatureAttribute getCreatureAttribute() {
      return CreatureAttribute.ARTHROPOD;
   }

   static class AIHideInStone extends EntityAIWander {
      private EnumFacing facing;
      private boolean doMerge;

      public AIHideInStone(EntitySilverfish silverfishIn) {
         super(silverfishIn, 1.0D, 10);
         this.setMutexBits(1);
      }

      public boolean shouldExecute() {
         if (this.entity.getAttackTarget() != null) {
            return false;
         } else if (!this.entity.getNavigator().noPath()) {
            return false;
         } else {
            Random random = this.entity.getRNG();
            if (this.entity.world.getGameRules().getBoolean("mobGriefing") && random.nextInt(10) == 0) {
               this.facing = EnumFacing.random(random);
               BlockPos blockpos = (new BlockPos(this.entity.posX, this.entity.posY + 0.5D, this.entity.posZ)).offset(this.facing);
               IBlockState iblockstate = this.entity.world.getBlockState(blockpos);
               if (BlockSilverfish.canContainSilverfish(iblockstate)) {
                  this.doMerge = true;
                  return true;
               }
            }

            this.doMerge = false;
            return super.shouldExecute();
         }
      }

      public boolean shouldContinueExecuting() {
         return this.doMerge ? false : super.shouldContinueExecuting();
      }

      public void startExecuting() {
         if (!this.doMerge) {
            super.startExecuting();
         } else {
            IWorld iworld = this.entity.world;
            BlockPos blockpos = (new BlockPos(this.entity.posX, this.entity.posY + 0.5D, this.entity.posZ)).offset(this.facing);
            IBlockState iblockstate = iworld.getBlockState(blockpos);
            if (BlockSilverfish.canContainSilverfish(iblockstate)) {
               iworld.setBlockState(blockpos, BlockSilverfish.infest(iblockstate.getBlock()), 3);
               this.entity.spawnExplosionParticle();
               this.entity.remove();
            }

         }
      }
   }

   static class AISummonSilverfish extends EntityAIBase {
      private final EntitySilverfish silverfish;
      private int lookForFriends;

      public AISummonSilverfish(EntitySilverfish silverfishIn) {
         this.silverfish = silverfishIn;
      }

      public void notifyHurt() {
         if (this.lookForFriends == 0) {
            this.lookForFriends = 20;
         }

      }

      public boolean shouldExecute() {
         return this.lookForFriends > 0;
      }

      public void tick() {
         --this.lookForFriends;
         if (this.lookForFriends <= 0) {
            World world = this.silverfish.world;
            Random random = this.silverfish.getRNG();
            BlockPos blockpos = new BlockPos(this.silverfish);

            for(int i = 0; i <= 5 && i >= -5; i = (i <= 0 ? 1 : 0) - i) {
               for(int j = 0; j <= 10 && j >= -10; j = (j <= 0 ? 1 : 0) - j) {
                  for(int k = 0; k <= 10 && k >= -10; k = (k <= 0 ? 1 : 0) - k) {
                     BlockPos blockpos1 = blockpos.add(j, i, k);
                     IBlockState iblockstate = world.getBlockState(blockpos1);
                     Block block = iblockstate.getBlock();
                     if (block instanceof BlockSilverfish) {
                        if (world.getGameRules().getBoolean("mobGriefing")) {
                           world.destroyBlock(blockpos1, true);
                        } else {
                           world.setBlockState(blockpos1, ((BlockSilverfish)block).getMimickedBlock().getDefaultState(), 3);
                        }

                        if (random.nextBoolean()) {
                           return;
                        }
                     }
                  }
               }
            }
         }

      }
   }
}
