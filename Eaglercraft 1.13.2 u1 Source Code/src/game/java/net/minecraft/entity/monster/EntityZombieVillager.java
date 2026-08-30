package net.minecraft.entity.monster;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityZombieVillager extends EntityZombie {
   private static final DataParameter<Boolean> CONVERTING = EntityDataManager.createKey(EntityZombieVillager.class, DataSerializers.BOOLEAN);
   private static final DataParameter<Integer> PROFESSION = EntityDataManager.createKey(EntityZombieVillager.class, DataSerializers.VARINT);
   private int conversionTime;
   private UUID converstionStarter;

   public EntityZombieVillager(World worldIn) {
      super(EntityType.ZOMBIE_VILLAGER, worldIn);
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(CONVERTING, false);
      this.dataManager.register(PROFESSION, 0);
   }

   public void setProfession(int profession) {
      this.dataManager.set(PROFESSION, profession);
   }

   public int getProfession() {
      return Math.max(this.dataManager.get(PROFESSION) % 6, 0);
   }

   public void writeAdditional(NBTTagCompound compound) {
      super.writeAdditional(compound);
      compound.putInt("Profession", this.getProfession());
      compound.putInt("ConversionTime", this.isConverting() ? this.conversionTime : -1);
      if (this.converstionStarter != null) {
         compound.putUniqueId("ConversionPlayer", this.converstionStarter);
      }

   }

   public void readAdditional(NBTTagCompound compound) {
      super.readAdditional(compound);
      this.setProfession(compound.getInt("Profession"));
      if (compound.contains("ConversionTime", 99) && compound.getInt("ConversionTime") > -1) {
         this.startConverting(compound.hasUniqueId("ConversionPlayer") ? compound.getUniqueId("ConversionPlayer") : null, compound.getInt("ConversionTime"));
      }

   }

   @Nullable
   public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData entityLivingData, @Nullable NBTTagCompound itemNbt) {
      this.setProfession(this.world.rand.nextInt(6));
      return super.onInitialSpawn(difficulty, entityLivingData, itemNbt);
   }

   public void tick() {
      if (!this.world.isRemote && this.isConverting()) {
         int i = this.getConversionProgress();
         this.conversionTime -= i;
         if (this.conversionTime <= 0) {
            this.finishConversion();
         }
      }

      super.tick();
   }

   public boolean processInteract(EntityPlayer player, EnumHand hand) {
      ItemStack itemstack = player.getHeldItem(hand);
      if (itemstack.getItem() == Items.GOLDEN_APPLE && this.isPotionActive(MobEffects.WEAKNESS)) {
         if (!player.abilities.isCreativeMode) {
            itemstack.shrink(1);
         }

         if (!this.world.isRemote) {
            this.startConverting(player.getUniqueID(), this.rand.nextInt(2401) + 3600);
         }

         return true;
      } else {
         return false;
      }
   }

   protected boolean shouldDrown() {
      return false;
   }

   public boolean canDespawn() {
      return !this.isConverting();
   }

   public boolean isConverting() {
      return this.getDataManager().get(CONVERTING);
   }

   protected void startConverting(@Nullable UUID conversionStarterIn, int conversionTimeIn) {
      this.converstionStarter = conversionStarterIn;
      this.conversionTime = conversionTimeIn;
      this.getDataManager().set(CONVERTING, true);
      this.removePotionEffect(MobEffects.WEAKNESS);
      this.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, conversionTimeIn, Math.min(this.world.getDifficulty().getId() - 1, 0)));
      this.world.setEntityState(this, (byte)16);
   }

   @OnlyIn(Dist.CLIENT)
   public void handleStatusUpdate(byte id) {
      if (id == 16) {
         if (!this.isSilent()) {
            this.world.playSound(this.posX + 0.5D, this.posY + 0.5D, this.posZ + 0.5D, SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, this.getSoundCategory(), 1.0F + this.rand.nextFloat(), this.rand.nextFloat() * 0.7F + 0.3F, false);
         }
      } else {
         super.handleStatusUpdate(id);
      }

   }

   protected void finishConversion() {
      EntityVillager entityvillager = new EntityVillager(this.world);
      entityvillager.copyLocationAndAnglesFrom(this);
      entityvillager.setProfession(this.getProfession());
      entityvillager.finalizeMobSpawn(this.world.getDifficultyForLocation(new BlockPos(entityvillager)), (IEntityLivingData)null, (NBTTagCompound)null, false);
      entityvillager.setLookingForHome();
      if (this.isChild()) {
         entityvillager.setGrowingAge(-24000);
      }

      this.world.removeEntity(this);
      entityvillager.setNoAI(this.isAIDisabled());
      if (this.hasCustomName()) {
         entityvillager.setCustomName(this.getCustomName());
         entityvillager.setCustomNameVisible(this.isCustomNameVisible());
      }

      this.world.spawnEntity(entityvillager);
      if (this.converstionStarter != null) {
         EntityPlayer entityplayer = this.world.getPlayerEntityByUUID(this.converstionStarter);
         if (entityplayer instanceof EntityPlayerMP) {
            CriteriaTriggers.CURED_ZOMBIE_VILLAGER.trigger((EntityPlayerMP)entityplayer, this, entityvillager);
         }
      }

      entityvillager.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 200, 0));
      this.world.playEvent((EntityPlayer)null, 1027, new BlockPos((int)this.posX, (int)this.posY, (int)this.posZ), 0);
   }

   protected int getConversionProgress() {
      int i = 1;
      if (this.rand.nextFloat() < 0.01F) {
         int j = 0;
         BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

         for(int k = (int)this.posX - 4; k < (int)this.posX + 4 && j < 14; ++k) {
            for(int l = (int)this.posY - 4; l < (int)this.posY + 4 && j < 14; ++l) {
               for(int i1 = (int)this.posZ - 4; i1 < (int)this.posZ + 4 && j < 14; ++i1) {
                  Block block = this.world.getBlockState(blockpos$mutableblockpos.setPos(k, l, i1)).getBlock();
                  if (block == Blocks.IRON_BARS || block instanceof BlockBed) {
                     if (this.rand.nextFloat() < 0.3F) {
                        ++i;
                     }

                     ++j;
                  }
               }
            }
         }
      }

      return i;
   }

   protected float getSoundPitch() {
      return this.isChild() ? (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 2.0F : (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F;
   }

   public SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_ZOMBIE_VILLAGER_AMBIENT;
   }

   public SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return SoundEvents.ENTITY_ZOMBIE_VILLAGER_HURT;
   }

   public SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_ZOMBIE_VILLAGER_DEATH;
   }

   public SoundEvent getStepSound() {
      return SoundEvents.ENTITY_ZOMBIE_VILLAGER_STEP;
   }

   @Nullable
   protected ResourceLocation getLootTable() {
      return LootTableList.ENTITIES_ZOMBIE_VILLAGER;
   }

   protected ItemStack getSkullDrop() {
      return ItemStack.EMPTY;
   }
}
