package net.minecraft.entity.passive;

import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

public class EntityChicken extends EntityAnimal {
   private static final Ingredient TEMPTATION_ITEMS = Ingredient.fromItems(Items.WHEAT_SEEDS, Items.MELON_SEEDS, Items.PUMPKIN_SEEDS, Items.BEETROOT_SEEDS);
   public float wingRotation;
   public float destPos;
   public float oFlapSpeed;
   public float oFlap;
   public float wingRotDelta = 1.0F;
   public int timeUntilNextEgg;
   public boolean chickenJockey;

   public EntityChicken(World worldIn) {
      super(EntityType.CHICKEN, worldIn);
      this.setSize(0.4F, 0.7F);
      this.timeUntilNextEgg = this.rand.nextInt(6000) + 6000;
      this.setPathPriority(PathNodeType.WATER, 0.0F);
   }

   protected void initEntityAI() {
      this.tasks.addTask(0, new EntityAISwimming(this));
      this.tasks.addTask(1, new EntityAIPanic(this, 1.4D));
      this.tasks.addTask(2, new EntityAIMate(this, 1.0D));
      this.tasks.addTask(3, new EntityAITempt(this, 1.0D, false, TEMPTATION_ITEMS));
      this.tasks.addTask(4, new EntityAIFollowParent(this, 1.1D));
      this.tasks.addTask(5, new EntityAIWanderAvoidWater(this, 1.0D));
      this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
      this.tasks.addTask(7, new EntityAILookIdle(this));
   }

   public float getEyeHeight() {
      return this.height;
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(4.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
   }

   public void livingTick() {
      super.livingTick();
      this.oFlap = this.wingRotation;
      this.oFlapSpeed = this.destPos;
      this.destPos = (float)((double)this.destPos + (double)(this.onGround ? -1 : 4) * 0.3D);
      this.destPos = MathHelper.clamp(this.destPos, 0.0F, 1.0F);
      if (!this.onGround && this.wingRotDelta < 1.0F) {
         this.wingRotDelta = 1.0F;
      }

      this.wingRotDelta = (float)((double)this.wingRotDelta * 0.9D);
      if (!this.onGround && this.motionY < 0.0D) {
         this.motionY *= 0.6D;
      }

      this.wingRotation += this.wingRotDelta * 2.0F;
      if (!this.world.isRemote && !this.isChild() && !this.isChickenJockey() && --this.timeUntilNextEgg <= 0) {
         this.playSound(SoundEvents.ENTITY_CHICKEN_EGG, 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
         this.entityDropItem(Items.EGG);
         this.timeUntilNextEgg = this.rand.nextInt(6000) + 6000;
      }

   }

   public void fall(float distance, float damageMultiplier) {
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_CHICKEN_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return SoundEvents.ENTITY_CHICKEN_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_CHICKEN_DEATH;
   }

   protected void playStepSound(BlockPos pos, IBlockState blockIn) {
      this.playSound(SoundEvents.ENTITY_CHICKEN_STEP, 0.15F, 1.0F);
   }

   @Nullable
   protected ResourceLocation getLootTable() {
      return LootTableList.ENTITIES_CHICKEN;
   }

   public EntityChicken createChild(EntityAgeable ageable) {
      return new EntityChicken(this.world);
   }

   public boolean isBreedingItem(ItemStack stack) {
      return TEMPTATION_ITEMS.test(stack);
   }

   protected int getExperiencePoints(EntityPlayer player) {
      return this.isChickenJockey() ? 10 : super.getExperiencePoints(player);
   }

   public void readAdditional(NBTTagCompound compound) {
      super.readAdditional(compound);
      this.chickenJockey = compound.getBoolean("IsChickenJockey");
      if (compound.contains("EggLayTime")) {
         this.timeUntilNextEgg = compound.getInt("EggLayTime");
      }

   }

   public void writeAdditional(NBTTagCompound compound) {
      super.writeAdditional(compound);
      compound.putBoolean("IsChickenJockey", this.chickenJockey);
      compound.putInt("EggLayTime", this.timeUntilNextEgg);
   }

   public boolean canDespawn() {
      return this.isChickenJockey() && !this.isBeingRidden();
   }

   public void updatePassenger(Entity passenger) {
      super.updatePassenger(passenger);
      float f = MathHelper.sin(this.renderYawOffset * ((float)Math.PI / 180F));
      float f1 = MathHelper.cos(this.renderYawOffset * ((float)Math.PI / 180F));
      float f2 = 0.1F;
      float f3 = 0.0F;
      passenger.setPosition(this.posX + (double)(0.1F * f), this.posY + (double)(this.height * 0.5F) + passenger.getYOffset() + 0.0D, this.posZ - (double)(0.1F * f1));
      if (passenger instanceof EntityLivingBase) {
         ((EntityLivingBase)passenger).renderYawOffset = this.renderYawOffset;
      }

   }

   public boolean isChickenJockey() {
      return this.chickenJockey;
   }

   public void setChickenJockey(boolean jockey) {
      this.chickenJockey = jockey;
   }
}
