package net.minecraft.entity.monster;

import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

public class EntityEndermite extends EntityMob {
   private int lifetime;
   private boolean playerSpawned;

   public EntityEndermite(World worldIn) {
      super(EntityType.ENDERMITE, worldIn);
      this.experienceValue = 3;
      this.setSize(0.4F, 0.3F);
   }

   protected void initEntityAI() {
      this.tasks.addTask(1, new EntityAISwimming(this));
      this.tasks.addTask(2, new EntityAIAttackMelee(this, 1.0D, false));
      this.tasks.addTask(3, new EntityAIWanderAvoidWater(this, 1.0D));
      this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
      this.tasks.addTask(8, new EntityAILookIdle(this));
      this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
      this.targetTasks.addTask(2, new EntityAINearestAttackableTarget<>(this, EntityPlayer.class, true));
   }

   public float getEyeHeight() {
      return 0.1F;
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(8.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
      this.getAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.0D);
   }

   protected boolean canTriggerWalking() {
      return false;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_ENDERMITE_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return SoundEvents.ENTITY_ENDERMITE_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_ENDERMITE_DEATH;
   }

   protected void playStepSound(BlockPos pos, IBlockState blockIn) {
      this.playSound(SoundEvents.ENTITY_ENDERMITE_STEP, 0.15F, 1.0F);
   }

   @Nullable
   protected ResourceLocation getLootTable() {
      return LootTableList.ENTITIES_ENDERMITE;
   }

   public void readAdditional(NBTTagCompound compound) {
      super.readAdditional(compound);
      this.lifetime = compound.getInt("Lifetime");
      this.playerSpawned = compound.getBoolean("PlayerSpawned");
   }

   public void writeAdditional(NBTTagCompound compound) {
      super.writeAdditional(compound);
      compound.putInt("Lifetime", this.lifetime);
      compound.putBoolean("PlayerSpawned", this.playerSpawned);
   }

   public void tick() {
      this.renderYawOffset = this.rotationYaw;
      super.tick();
   }

   public void setRenderYawOffset(float offset) {
      this.rotationYaw = offset;
      super.setRenderYawOffset(offset);
   }

   public double getYOffset() {
      return 0.1D;
   }

   public boolean isSpawnedByPlayer() {
      return this.playerSpawned;
   }

   public void setSpawnedByPlayer(boolean spawnedByPlayer) {
      this.playerSpawned = spawnedByPlayer;
   }

   public void livingTick() {
      super.livingTick();
      if (this.world.isRemote) {
         for(int i = 0; i < 2; ++i) {
            this.world.addParticle(Particles.PORTAL, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, (this.rand.nextDouble() - 0.5D) * 2.0D, -this.rand.nextDouble(), (this.rand.nextDouble() - 0.5D) * 2.0D);
         }
      } else {
         if (!this.isNoDespawnRequired()) {
            ++this.lifetime;
         }

         if (this.lifetime >= 2400) {
            this.remove();
         }
      }

   }

   protected boolean isValidLightLevel() {
      return true;
   }

   public boolean canSpawn(IWorld worldIn, boolean fromSpawner) {
      if (super.canSpawn(worldIn, fromSpawner)) {
         EntityPlayer entityplayer = worldIn.getClosestPlayerToEntity(this, 5.0D);
         return entityplayer == null;
      } else {
         return false;
      }
   }

   public CreatureAttribute getCreatureAttribute() {
      return CreatureAttribute.ARTHROPOD;
   }
}
