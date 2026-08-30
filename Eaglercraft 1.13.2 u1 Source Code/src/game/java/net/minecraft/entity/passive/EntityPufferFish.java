package net.minecraft.entity.passive;

import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SPacketChangeGameState;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

public class EntityPufferFish extends AbstractFish {
   private static final DataParameter<Integer> PUFF_STATE = EntityDataManager.createKey(EntityPufferFish.class, DataSerializers.VARINT);
   private int puffTimer;
   private int deflateTimer;
   private static final Predicate<EntityLivingBase> ENEMY_MATCHER = (p_210139_0_) -> {
      if (p_210139_0_ == null) {
         return false;
      } else if (!(p_210139_0_ instanceof EntityPlayer) || !((EntityPlayer)p_210139_0_).isSpectator() && !((EntityPlayer)p_210139_0_).isCreative()) {
         return p_210139_0_.getCreatureAttribute() != CreatureAttribute.WATER;
      } else {
         return false;
      }
   };
   private float originalWidth = -1.0F;
   private float originalHeight;

   public EntityPufferFish(World p_i48853_1_) {
      super(EntityType.PUFFERFISH, p_i48853_1_);
      this.setSize(0.7F, 0.7F);
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(PUFF_STATE, 0);
   }

   public int getPuffState() {
      return this.dataManager.get(PUFF_STATE);
   }

   public void setPuffState(int p_203714_1_) {
      this.dataManager.set(PUFF_STATE, p_203714_1_);
      this.onPuffStateChanged(p_203714_1_);
   }

   private void onPuffStateChanged(int state) {
      float f = 1.0F;
      if (state == 1) {
         f = 0.7F;
      } else if (state == 0) {
         f = 0.5F;
      }

      this.updateSize(f);
   }

   protected final void setSize(float width, float height) {
      boolean flag = this.originalWidth > 0.0F;
      this.originalWidth = width;
      this.originalHeight = height;
      if (!flag) {
         this.updateSize(1.0F);
      }

   }

   private void updateSize(float scale) {
      super.setSize(this.originalWidth * scale, this.originalHeight * scale);
   }

   public void notifyDataManagerChange(DataParameter<?> key) {
      this.onPuffStateChanged(this.getPuffState());
      super.notifyDataManagerChange(key);
   }

   public void writeAdditional(NBTTagCompound compound) {
      super.writeAdditional(compound);
      compound.putInt("PuffState", this.getPuffState());
   }

   public void readAdditional(NBTTagCompound compound) {
      super.readAdditional(compound);
      this.setPuffState(compound.getInt("PuffState"));
   }

   @Nullable
   protected ResourceLocation getLootTable() {
      return LootTableList.ENTITIES_PUFFERFISH;
   }

   protected ItemStack getFishBucket() {
      return new ItemStack(Items.PUFFERFISH_BUCKET);
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.tasks.addTask(1, new EntityPufferFish.AIPuff(this));
   }

   public void tick() {
      if (this.isAlive() && !this.world.isRemote) {
         if (this.puffTimer > 0) {
            if (this.getPuffState() == 0) {
               this.playSound(SoundEvents.ENTITY_PUFFER_FISH_BLOW_UP, this.getSoundVolume(), this.getSoundPitch());
               this.setPuffState(1);
            } else if (this.puffTimer > 40 && this.getPuffState() == 1) {
               this.playSound(SoundEvents.ENTITY_PUFFER_FISH_BLOW_UP, this.getSoundVolume(), this.getSoundPitch());
               this.setPuffState(2);
            }

            ++this.puffTimer;
         } else if (this.getPuffState() != 0) {
            if (this.deflateTimer > 60 && this.getPuffState() == 2) {
               this.playSound(SoundEvents.ENTITY_PUFFER_FISH_BLOW_OUT, this.getSoundVolume(), this.getSoundPitch());
               this.setPuffState(1);
            } else if (this.deflateTimer > 100 && this.getPuffState() == 1) {
               this.playSound(SoundEvents.ENTITY_PUFFER_FISH_BLOW_OUT, this.getSoundVolume(), this.getSoundPitch());
               this.setPuffState(0);
            }

            ++this.deflateTimer;
         }
      }

      super.tick();
   }

   public void livingTick() {
      super.livingTick();
      if (this.getPuffState() > 0) {
         for(EntityLiving entityliving : this.world.getEntitiesWithinAABB(EntityLiving.class, this.getBoundingBox().grow(0.3D), ENEMY_MATCHER)) {
            if (entityliving.isAlive()) {
               this.attack(entityliving);
            }
         }
      }

   }

   private void attack(EntityLiving p_205719_1_) {
      int i = this.getPuffState();
      if (p_205719_1_.attackEntityFrom(DamageSource.causeMobDamage(this), (float)(1 + i))) {
         p_205719_1_.addPotionEffect(new PotionEffect(MobEffects.POISON, 60 * i, 0));
         this.playSound(SoundEvents.ENTITY_PUFFER_FISH_STING, 1.0F, 1.0F);
      }

   }

   public void onCollideWithPlayer(EntityPlayer entityIn) {
      int i = this.getPuffState();
      if (entityIn instanceof EntityPlayerMP && i > 0 && entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), (float)(1 + i))) {
         ((EntityPlayerMP)entityIn).connection.sendPacket(new SPacketChangeGameState(9, 0.0F));
         entityIn.addPotionEffect(new PotionEffect(MobEffects.POISON, 60 * i, 0));
      }

   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_PUFFER_FISH_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_PUFFER_FISH_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return SoundEvents.ENTITY_PUFFER_FISH_HURT;
   }

   protected SoundEvent getFlopSound() {
      return SoundEvents.ENTITY_PUFFER_FISH_FLOP;
   }

   static class AIPuff extends EntityAIBase {
      private final EntityPufferFish fish;

      public AIPuff(EntityPufferFish fish) {
         this.fish = fish;
      }

      public boolean shouldExecute() {
         List<EntityLivingBase> list = this.fish.world.getEntitiesWithinAABB(EntityLivingBase.class, this.fish.getBoundingBox().grow(2.0D), EntityPufferFish.ENEMY_MATCHER);
         return !list.isEmpty();
      }

      public void startExecuting() {
         this.fish.puffTimer = 1;
         this.fish.deflateTimer = 0;
      }

      public void resetTask() {
         this.fish.puffTimer = 0;
      }

      public boolean shouldContinueExecuting() {
         List<EntityLivingBase> list = this.fish.world.getEntitiesWithinAABB(EntityLivingBase.class, this.fish.getBoundingBox().grow(2.0D), EntityPufferFish.ENEMY_MATCHER);
         return !list.isEmpty();
      }
   }
}
