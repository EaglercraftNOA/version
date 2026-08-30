package net.minecraft.entity.passive;

import javax.annotation.Nullable;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAISkeletonRiders;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemSpawnEgg;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

public class EntitySkeletonHorse extends AbstractHorse {
   private final EntityAISkeletonRiders skeletonTrapAI = new EntityAISkeletonRiders(this);
   private boolean skeletonTrap;
   private int skeletonTrapTime;

   public EntitySkeletonHorse(World worldIn) {
      super(EntityType.SKELETON_HORSE, worldIn);
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(15.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue((double)0.2F);
      this.getAttribute(JUMP_STRENGTH).setBaseValue(this.getModifiedJumpStrength());
   }

   protected void initExtraAI() {
   }

   protected SoundEvent getAmbientSound() {
      super.getAmbientSound();
      return this.areEyesInFluid(FluidTags.WATER) ? SoundEvents.ENTITY_SKELETON_HORSE_AMBIENT_WATER : SoundEvents.ENTITY_SKELETON_HORSE_AMBIENT;
   }

   protected SoundEvent getDeathSound() {
      super.getDeathSound();
      return SoundEvents.ENTITY_SKELETON_HORSE_DEATH;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      super.getHurtSound(damageSourceIn);
      return SoundEvents.ENTITY_SKELETON_HORSE_HURT;
   }

   protected SoundEvent getSwimSound() {
      if (this.onGround) {
         if (!this.isBeingRidden()) {
            return SoundEvents.ENTITY_SKELETON_HORSE_STEP_WATER;
         }

         ++this.gallopTime;
         if (this.gallopTime > 5 && this.gallopTime % 3 == 0) {
            return SoundEvents.ENTITY_SKELETON_HORSE_GALLOP_WATER;
         }

         if (this.gallopTime <= 5) {
            return SoundEvents.ENTITY_SKELETON_HORSE_STEP_WATER;
         }
      }

      return SoundEvents.ENTITY_SKELETON_HORSE_SWIM;
   }

   protected void playSwimSound(float volume) {
      if (this.onGround) {
         super.playSwimSound(0.3F);
      } else {
         super.playSwimSound(Math.min(0.1F, volume * 25.0F));
      }

   }

   protected void playJumpSound() {
      if (this.isInWater()) {
         this.playSound(SoundEvents.ENTITY_SKELETON_HORSE_JUMP_WATER, 0.4F, 1.0F);
      } else {
         super.playJumpSound();
      }

   }

   public CreatureAttribute getCreatureAttribute() {
      return CreatureAttribute.UNDEAD;
   }

   public double getMountedYOffset() {
      return super.getMountedYOffset() - 0.1875D;
   }

   @Nullable
   protected ResourceLocation getLootTable() {
      return LootTableList.ENTITIES_SKELETON_HORSE;
   }

   public void livingTick() {
      super.livingTick();
      if (this.isTrap() && this.skeletonTrapTime++ >= 18000) {
         this.remove();
      }

   }

   public void writeAdditional(NBTTagCompound compound) {
      super.writeAdditional(compound);
      compound.putBoolean("SkeletonTrap", this.isTrap());
      compound.putInt("SkeletonTrapTime", this.skeletonTrapTime);
   }

   public void readAdditional(NBTTagCompound compound) {
      super.readAdditional(compound);
      this.setTrap(compound.getBoolean("SkeletonTrap"));
      this.skeletonTrapTime = compound.getInt("SkeletonTrapTime");
   }

   public boolean canBeRiddenInWater() {
      return true;
   }

   protected float getWaterSlowDown() {
      return 0.96F;
   }

   public boolean isTrap() {
      return this.skeletonTrap;
   }

   public void setTrap(boolean trap) {
      if (trap != this.skeletonTrap) {
         this.skeletonTrap = trap;
         if (trap) {
            this.tasks.addTask(1, this.skeletonTrapAI);
         } else {
            this.tasks.removeTask(this.skeletonTrapAI);
         }

      }
   }

   @Nullable
   public EntityAgeable createChild(EntityAgeable ageable) {
      return new EntitySkeletonHorse(this.world);
   }

   public boolean processInteract(EntityPlayer player, EnumHand hand) {
      ItemStack itemstack = player.getHeldItem(hand);
      if (itemstack.getItem() instanceof ItemSpawnEgg) {
         return super.processInteract(player, hand);
      } else if (!this.isTame()) {
         return false;
      } else if (this.isChild()) {
         return super.processInteract(player, hand);
      } else if (player.isSneaking()) {
         this.openGUI(player);
         return true;
      } else if (this.isBeingRidden()) {
         return super.processInteract(player, hand);
      } else {
         if (!itemstack.isEmpty()) {
            if (itemstack.getItem() == Items.SADDLE && !this.isHorseSaddled()) {
               this.openGUI(player);
               return true;
            }

            if (itemstack.interactWithEntity(player, this, hand)) {
               return true;
            }
         }

         this.mountTo(player);
         return true;
      }
   }
}
