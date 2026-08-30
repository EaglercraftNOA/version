package net.minecraft.entity.passive;

import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.ai.EntityAISit;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Particles;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.IParticleData;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.management.PreYggdrasilConverter;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class EntityTameable extends EntityAnimal implements IEntityOwnable {
   protected static final DataParameter<Byte> TAMED = EntityDataManager.createKey(EntityTameable.class, DataSerializers.BYTE);
   protected static final DataParameter<Optional<UUID>> OWNER_UNIQUE_ID = EntityDataManager.createKey(EntityTameable.class, DataSerializers.OPTIONAL_UNIQUE_ID);
   protected EntityAISit aiSit;

   public static void bootstrapDataManager() {
   }

   protected EntityTameable(EntityType<?> type, World p_i48574_2_) {
      super(type, p_i48574_2_);
      this.setupTamedAI();
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(TAMED, (byte)0);
      this.dataManager.register(OWNER_UNIQUE_ID, Optional.empty());
   }

   public void writeAdditional(NBTTagCompound compound) {
      super.writeAdditional(compound);
      if (this.getOwnerId() == null) {
         compound.putString("OwnerUUID", "");
      } else {
         compound.putString("OwnerUUID", this.getOwnerId().toString());
      }

      compound.putBoolean("Sitting", this.isSitting());
   }

   public void readAdditional(NBTTagCompound compound) {
      super.readAdditional(compound);
      String s;
      if (compound.contains("OwnerUUID", 8)) {
         s = compound.getString("OwnerUUID");
      } else {
         String s1 = compound.getString("Owner");
         s = PreYggdrasilConverter.convertMobOwnerIfNeeded(this.getServer(), s1);
      }

      if (!s.isEmpty()) {
         try {
            this.setOwnerId(UUID.fromString(s));
            this.setTamed(true);
         } catch (Throwable var4) {
            this.setTamed(false);
         }
      }

      if (this.aiSit != null) {
         this.aiSit.setSitting(compound.getBoolean("Sitting"));
      }

      this.setSitting(compound.getBoolean("Sitting"));
   }

   public boolean canBeLeashedTo(EntityPlayer player) {
      return !this.getLeashed();
   }

   protected void playTameEffect(boolean play) {
      IParticleData iparticledata = Particles.HEART;
      if (!play) {
         iparticledata = Particles.SMOKE;
      }

      for(int i = 0; i < 7; ++i) {
         double d0 = this.rand.nextGaussian() * 0.02D;
         double d1 = this.rand.nextGaussian() * 0.02D;
         double d2 = this.rand.nextGaussian() * 0.02D;
         this.world.addParticle(iparticledata, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + 0.5D + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, d0, d1, d2);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public void handleStatusUpdate(byte id) {
      if (id == 7) {
         this.playTameEffect(true);
      } else if (id == 6) {
         this.playTameEffect(false);
      } else {
         super.handleStatusUpdate(id);
      }

   }

   public boolean isTamed() {
      return (this.dataManager.get(TAMED) & 4) != 0;
   }

   public void setTamed(boolean tamed) {
      byte b0 = this.dataManager.get(TAMED);
      if (tamed) {
         this.dataManager.set(TAMED, (byte)(b0 | 4));
      } else {
         this.dataManager.set(TAMED, (byte)(b0 & -5));
      }

      this.setupTamedAI();
   }

   protected void setupTamedAI() {
   }

   public boolean isSitting() {
      return (this.dataManager.get(TAMED) & 1) != 0;
   }

   public void setSitting(boolean sitting) {
      byte b0 = this.dataManager.get(TAMED);
      if (sitting) {
         this.dataManager.set(TAMED, (byte)(b0 | 1));
      } else {
         this.dataManager.set(TAMED, (byte)(b0 & -2));
      }

   }

   @Nullable
   public UUID getOwnerId() {
      return this.dataManager.get(OWNER_UNIQUE_ID).orElse((UUID)null);
   }

   public void setOwnerId(@Nullable UUID p_184754_1_) {
      this.dataManager.set(OWNER_UNIQUE_ID, Optional.ofNullable(p_184754_1_));
   }

   public void setTamedBy(EntityPlayer player) {
      this.setTamed(true);
      this.setOwnerId(player.getUniqueID());
      if (player instanceof EntityPlayerMP) {
         CriteriaTriggers.TAME_ANIMAL.trigger((EntityPlayerMP)player, this);
      }

   }

   @Nullable
   public EntityLivingBase getOwner() {
      try {
         UUID uuid = this.getOwnerId();
         return uuid == null ? null : this.world.getPlayerEntityByUUID(uuid);
      } catch (IllegalArgumentException var2) {
         return null;
      }
   }

   public boolean isOwner(EntityLivingBase entityIn) {
      return entityIn == this.getOwner();
   }

   public EntityAISit getAISit() {
      return this.aiSit;
   }

   public boolean shouldAttackEntity(EntityLivingBase target, EntityLivingBase owner) {
      return true;
   }

   public Team getTeam() {
      if (this.isTamed()) {
         EntityLivingBase entitylivingbase = this.getOwner();
         if (entitylivingbase != null) {
            return entitylivingbase.getTeam();
         }
      }

      return super.getTeam();
   }

   public boolean isOnSameTeam(Entity entityIn) {
      if (this.isTamed()) {
         EntityLivingBase entitylivingbase = this.getOwner();
         if (entityIn == entitylivingbase) {
            return true;
         }

         if (entitylivingbase != null) {
            return entitylivingbase.isOnSameTeam(entityIn);
         }
      }

      return super.isOnSameTeam(entityIn);
   }

   public void onDeath(DamageSource cause) {
      if (!this.world.isRemote && this.world.getGameRules().getBoolean("showDeathMessages") && this.getOwner() instanceof EntityPlayerMP) {
         this.getOwner().sendMessage(this.getCombatTracker().getDeathMessage());
      }

      super.onDeath(cause);
   }
}
