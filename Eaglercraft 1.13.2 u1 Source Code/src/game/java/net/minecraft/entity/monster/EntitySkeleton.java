package net.minecraft.entity.monster;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntitySpectralArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

public class EntitySkeleton extends AbstractSkeleton {
   public EntitySkeleton(World worldIn) {
      super(EntityType.SKELETON, worldIn);
   }

   @Nullable
   protected ResourceLocation getLootTable() {
      return LootTableList.ENTITIES_SKELETON;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_SKELETON_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return SoundEvents.ENTITY_SKELETON_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_SKELETON_DEATH;
   }

   SoundEvent getStepSound() {
      return SoundEvents.ENTITY_SKELETON_STEP;
   }

   public void onDeath(DamageSource cause) {
      super.onDeath(cause);
      if (cause.getTrueSource() instanceof EntityCreeper) {
         EntityCreeper entitycreeper = (EntityCreeper)cause.getTrueSource();
         if (entitycreeper.getPowered() && entitycreeper.ableToCauseSkullDrop()) {
            entitycreeper.incrementDroppedSkulls();
            this.entityDropItem(Items.SKELETON_SKULL);
         }
      }

   }

   protected EntityArrow getArrow(float distanceFactor) {
      ItemStack itemstack = this.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND);
      if (itemstack.getItem() == Items.SPECTRAL_ARROW) {
         EntitySpectralArrow entityspectralarrow = new EntitySpectralArrow(this.world, this);
         entityspectralarrow.setEnchantmentEffectsFromEntity(this, distanceFactor);
         return entityspectralarrow;
      } else {
         EntityArrow entityarrow = super.getArrow(distanceFactor);
         if (itemstack.getItem() == Items.TIPPED_ARROW && entityarrow instanceof EntityTippedArrow) {
            ((EntityTippedArrow)entityarrow).setPotionEffect(itemstack);
         }

         return entityarrow;
      }
   }
}
