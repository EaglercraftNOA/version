package net.minecraft.entity.monster;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.fluid.Fluid;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.particles.IParticleData;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityMagmaCube extends EntitySlime {
   public EntityMagmaCube(World worldIn) {
      super(EntityType.MAGMA_CUBE, worldIn);
      this.isImmuneToFire = true;
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue((double)0.2F);
   }

   public boolean canSpawn(IWorld worldIn, boolean fromSpawner) {
      return worldIn.getDifficulty() != EnumDifficulty.PEACEFUL;
   }

   public boolean isNotColliding(IWorldReaderBase worldIn) {
      return worldIn.checkNoEntityCollision(this, this.getBoundingBox()) && worldIn.isCollisionBoxesEmpty(this, this.getBoundingBox()) && !worldIn.containsAnyLiquid(this.getBoundingBox());
   }

   protected void setSlimeSize(int size, boolean resetHealth) {
      super.setSlimeSize(size, resetHealth);
      this.getAttribute(SharedMonsterAttributes.ARMOR).setBaseValue((double)(size * 3));
   }

   @OnlyIn(Dist.CLIENT)
   public int getBrightnessForRender() {
      return 15728880;
   }

   public float getBrightness() {
      return 1.0F;
   }

   protected IParticleData getSquishParticle() {
      return Particles.FLAME;
   }

   @Nullable
   protected ResourceLocation getLootTable() {
      return this.isSmallSlime() ? LootTableList.EMPTY : LootTableList.ENTITIES_MAGMA_CUBE;
   }

   public boolean isBurning() {
      return false;
   }

   protected int getJumpDelay() {
      return super.getJumpDelay() * 4;
   }

   protected void alterSquishAmount() {
      this.squishAmount *= 0.9F;
   }

   protected void jump() {
      this.motionY = (double)(0.42F + (float)this.getSlimeSize() * 0.1F);
      this.isAirBorne = true;
   }

   protected void handleFluidJump(Tag<Fluid> p_180466_1_) {
      if (p_180466_1_ == FluidTags.LAVA) {
         this.motionY = (double)(0.22F + (float)this.getSlimeSize() * 0.05F);
         this.isAirBorne = true;
      } else {
         super.handleFluidJump(p_180466_1_);
      }

   }

   public void fall(float distance, float damageMultiplier) {
   }

   protected boolean canDamagePlayer() {
      return this.isServerWorld();
   }

   protected int getAttackStrength() {
      return super.getAttackStrength() + 2;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return this.isSmallSlime() ? SoundEvents.ENTITY_MAGMA_CUBE_HURT_SMALL : SoundEvents.ENTITY_MAGMA_CUBE_HURT;
   }

   protected SoundEvent getDeathSound() {
      return this.isSmallSlime() ? SoundEvents.ENTITY_MAGMA_CUBE_DEATH_SMALL : SoundEvents.ENTITY_MAGMA_CUBE_DEATH;
   }

   protected SoundEvent getSquishSound() {
      return this.isSmallSlime() ? SoundEvents.ENTITY_MAGMA_CUBE_SQUISH_SMALL : SoundEvents.ENTITY_MAGMA_CUBE_SQUISH;
   }

   protected SoundEvent getJumpSound() {
      return SoundEvents.ENTITY_MAGMA_CUBE_JUMP;
   }
}
