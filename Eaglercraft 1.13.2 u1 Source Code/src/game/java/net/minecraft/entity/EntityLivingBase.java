package net.minecraft.entity;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLadder;
import net.minecraft.block.BlockTrapDoor;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.enchantment.EnchantmentFrostWalker;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;
import net.minecraft.entity.ai.attributes.AttributeMap;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.passive.IFlyingAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.fluid.Fluid;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.Particles;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemElytra;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SPacketAnimation;
import net.minecraft.network.play.server.SPacketCollectItem;
import net.minecraft.network.play.server.SPacketEntityEquipment;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.PotionUtils;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.stats.StatList;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.CombatRules;
import net.minecraft.util.CombatTracker;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceFluidMode;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class EntityLivingBase extends Entity {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final EntityEquipmentSlot[] EQUIPMENT_SLOTS = EntityEquipmentSlot.values();
   private static final UUID SPRINTING_SPEED_BOOST_ID = UUID.fromString("662A6B8D-DA3E-4C1C-8813-96EA6097278D");
   private static final AttributeModifier SPRINTING_SPEED_BOOST = (new AttributeModifier(SPRINTING_SPEED_BOOST_ID, "Sprinting speed boost", (double)0.3F, 2)).setSaved(false);
   protected static final DataParameter<Byte> LIVING_FLAGS = EntityDataManager.createKey(EntityLivingBase.class, DataSerializers.BYTE);
   private static final DataParameter<Float> HEALTH = EntityDataManager.createKey(EntityLivingBase.class, DataSerializers.FLOAT);
   private static final DataParameter<Integer> POTION_EFFECTS = EntityDataManager.createKey(EntityLivingBase.class, DataSerializers.VARINT);
   private static final DataParameter<Boolean> HIDE_PARTICLES = EntityDataManager.createKey(EntityLivingBase.class, DataSerializers.BOOLEAN);
   private static final DataParameter<Integer> ARROW_COUNT_IN_ENTITY = EntityDataManager.createKey(EntityLivingBase.class, DataSerializers.VARINT);
   private AbstractAttributeMap attributeMap;
   private final CombatTracker combatTracker = new CombatTracker(this);
   private final Map<Potion, PotionEffect> activePotionsMap = Maps.newHashMap();
   private final NonNullList<ItemStack> handInventory = NonNullList.withSize(2, ItemStack.EMPTY);
   private final NonNullList<ItemStack> armorArray = NonNullList.withSize(4, ItemStack.EMPTY);
   public boolean isSwingInProgress;
   public EnumHand swingingHand;
   public int swingProgressInt;
   public int arrowHitTimer;
   public int hurtTime;
   public int maxHurtTime;
   public float attackedAtYaw;
   public int deathTime;
   public float prevSwingProgress;
   public float swingProgress;
   protected int ticksSinceLastSwing;
   public float prevLimbSwingAmount;
   public float limbSwingAmount;
   public float limbSwing;
   public int maxHurtResistantTime = 20;
   public float prevCameraPitch;
   public float cameraPitch;
   public float randomUnused2;
   public float randomUnused1;
   public float renderYawOffset;
   public float prevRenderYawOffset;
   public float rotationYawHead;
   public float prevRotationYawHead;
   public float jumpMovementFactor = 0.02F;
   protected EntityPlayer attackingPlayer;
   protected int recentlyHit;
   protected boolean dead;
   protected int idleTime;
   protected float prevOnGroundSpeedFactor;
   protected float onGroundSpeedFactor;
   protected float movedDistance;
   protected float prevMovedDistance;
   protected float unused180;
   protected int scoreValue;
   protected float lastDamage;
   protected boolean isJumping;
   public float moveStrafing;
   public float moveVertical;
   public float moveForward;
   public float randomYawVelocity;
   protected int newPosRotationIncrements;
   protected double interpTargetX;
   protected double interpTargetY;
   protected double interpTargetZ;
   protected double interpTargetYaw;
   protected double interpTargetPitch;
   protected double interpTargetHeadYaw;
   protected int interpTicksHead;
   private boolean potionsNeedUpdate = true;
   private EntityLivingBase revengeTarget;
   private int revengeTimer;
   private EntityLivingBase lastAttackedEntity;
   private int lastAttackedEntityTime;
   private float landMovementFactor;
   private int jumpTicks;
   private float absorptionAmount;
   protected ItemStack activeItemStack = ItemStack.EMPTY;
   protected int activeItemStackUseCount;
   protected int ticksElytraFlying;
   private BlockPos prevBlockpos;
   private DamageSource lastDamageSource;
   private long lastDamageStamp;
   protected int spinAttackDuration;
   private float swimAnimation;
   private float lastSwimAnimation;

   public static void bootstrapDataManager() {
   }

   protected EntityLivingBase(EntityType<?> type, World p_i48577_2_) {
      super(type, p_i48577_2_);
      this.registerAttributes();
      this.setHealth(this.getMaxHealth());
      this.preventEntitySpawning = true;
      this.randomUnused1 = (float)((Math.random() + 1.0D) * (double)0.01F);
      this.setPosition(this.posX, this.posY, this.posZ);
      this.randomUnused2 = (float)Math.random() * 12398.0F;
      this.rotationYaw = (float)(Math.random() * (double)((float)Math.PI * 2F));
      this.rotationYawHead = this.rotationYaw;
      this.stepHeight = 0.6F;
   }

   public void onKillCommand() {
      this.attackEntityFrom(DamageSource.OUT_OF_WORLD, Float.MAX_VALUE);
   }

   protected void registerData() {
      this.dataManager.register(LIVING_FLAGS, (byte)0);
      this.dataManager.register(POTION_EFFECTS, 0);
      this.dataManager.register(HIDE_PARTICLES, false);
      this.dataManager.register(ARROW_COUNT_IN_ENTITY, 0);
      this.dataManager.register(HEALTH, 1.0F);
   }

   protected void registerAttributes() {
      this.getAttributeMap().registerAttribute(SharedMonsterAttributes.MAX_HEALTH);
      this.getAttributeMap().registerAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE);
      this.getAttributeMap().registerAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
      this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ARMOR);
      this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS);
   }

   protected void updateFallState(double y, boolean onGroundIn, IBlockState state, BlockPos pos) {
      if (!this.isInWater()) {
         this.handleWaterMovement();
      }

      if (!this.world.isRemote && this.fallDistance > 3.0F && onGroundIn) {
         float f = (float)MathHelper.ceil(this.fallDistance - 3.0F);
         if (!state.isAir()) {
            double d0 = Math.min((double)(0.2F + f / 15.0F), 2.5D);
            int i = (int)(150.0D * d0);
            ((WorldServer)this.world).spawnParticle(new BlockParticleData(Particles.BLOCK, state), this.posX, this.posY, this.posZ, i, 0.0D, 0.0D, 0.0D, (double)0.15F);
         }
      }

      super.updateFallState(y, onGroundIn, state, pos);
   }

   public boolean canBreatheUnderwater() {
      return this.getCreatureAttribute() == CreatureAttribute.UNDEAD;
   }

   @OnlyIn(Dist.CLIENT)
   public float getSwimAnimation(float partialTicks) {
      return this.lerp(this.lastSwimAnimation, this.swimAnimation, partialTicks);
   }

   @OnlyIn(Dist.CLIENT)
   protected float lerp(float p_205016_1_, float p_205016_2_, float partialTicks) {
      return p_205016_1_ + (p_205016_2_ - p_205016_1_) * partialTicks;
   }

   public void baseTick() {
      this.prevSwingProgress = this.swingProgress;
      super.baseTick();
      this.world.profiler.startSection("livingEntityBaseTick");
      boolean flag = this instanceof EntityPlayer;
      if (this.isAlive()) {
         if (this.isEntityInsideOpaqueBlock()) {
            this.attackEntityFrom(DamageSource.IN_WALL, 1.0F);
         } else if (flag && !this.world.getWorldBorder().contains(this.getBoundingBox())) {
            double d0 = this.world.getWorldBorder().getClosestDistance(this) + this.world.getWorldBorder().getDamageBuffer();
            if (d0 < 0.0D) {
               double d1 = this.world.getWorldBorder().getDamagePerBlock();
               if (d1 > 0.0D) {
                  this.attackEntityFrom(DamageSource.IN_WALL, (float)Math.max(1, MathHelper.floor(-d0 * d1)));
               }
            }
         }
      }

      if (this.isImmuneToFire() || this.world.isRemote) {
         this.extinguish();
      }

      boolean flag1 = flag && ((EntityPlayer)this).abilities.disableDamage;
      if (this.isAlive()) {
         if (this.areEyesInFluid(FluidTags.WATER) && this.world.getBlockState(new BlockPos(this.posX, this.posY + (double)this.getEyeHeight(), this.posZ)).getBlock() != Blocks.BUBBLE_COLUMN) {
            if (!this.canBreatheUnderwater() && !PotionUtil.canBreatheUnderwater(this) && !flag1) {
               this.setAir(this.decreaseAirSupply(this.getAir()));
               if (this.getAir() == -20) {
                  this.setAir(0);

                  for(int i = 0; i < 8; ++i) {
                     float f2 = this.rand.nextFloat() - this.rand.nextFloat();
                     float f = this.rand.nextFloat() - this.rand.nextFloat();
                     float f1 = this.rand.nextFloat() - this.rand.nextFloat();
                     this.world.addParticle(Particles.BUBBLE, this.posX + (double)f2, this.posY + (double)f, this.posZ + (double)f1, this.motionX, this.motionY, this.motionZ);
                  }

                  this.attackEntityFrom(DamageSource.DROWN, 2.0F);
               }
            }

            if (!this.world.isRemote && this.isPassenger() && this.getRidingEntity() != null && !this.getRidingEntity().canBeRiddenInWater()) {
               this.stopRiding();
            }
         } else if (this.getAir() < this.getMaxAir()) {
            this.setAir(this.determineNextAir(this.getAir()));
         }

         if (!this.world.isRemote) {
            BlockPos blockpos = new BlockPos(this);
            if (!Objects.equal(this.prevBlockpos, blockpos)) {
               this.prevBlockpos = blockpos;
               this.frostWalk(blockpos);
            }
         }
      }

      if (this.isAlive() && this.isInWaterRainOrBubbleColumn()) {
         this.extinguish();
      }

      this.prevCameraPitch = this.cameraPitch;
      if (this.hurtTime > 0) {
         --this.hurtTime;
      }

      if (this.hurtResistantTime > 0 && !(this instanceof EntityPlayerMP)) {
         --this.hurtResistantTime;
      }

      if (this.getHealth() <= 0.0F) {
         this.onDeathUpdate();
      }

      if (this.recentlyHit > 0) {
         --this.recentlyHit;
      } else {
         this.attackingPlayer = null;
      }

      if (this.lastAttackedEntity != null && !this.lastAttackedEntity.isAlive()) {
         this.lastAttackedEntity = null;
      }

      if (this.revengeTarget != null) {
         if (!this.revengeTarget.isAlive()) {
            this.setRevengeTarget((EntityLivingBase)null);
         } else if (this.ticksExisted - this.revengeTimer > 100) {
            this.setRevengeTarget((EntityLivingBase)null);
         }
      }

      this.updatePotionEffects();
      this.prevMovedDistance = this.movedDistance;
      this.prevRenderYawOffset = this.renderYawOffset;
      this.prevRotationYawHead = this.rotationYawHead;
      this.prevRotationYaw = this.rotationYaw;
      this.prevRotationPitch = this.rotationPitch;
      this.world.profiler.endSection();
   }

   protected void frostWalk(BlockPos pos) {
      int i = EnchantmentHelper.getMaxEnchantmentLevel(Enchantments.FROST_WALKER, this);
      if (i > 0) {
         EnchantmentFrostWalker.freezeNearby(this, this.world, pos, i);
      }

   }

   public boolean isChild() {
      return false;
   }

   public boolean canBeRiddenInWater() {
      return false;
   }

   protected void onDeathUpdate() {
      ++this.deathTime;
      if (this.deathTime == 20) {
         if (!this.world.isRemote && (this.isPlayer() || this.recentlyHit > 0 && this.canDropLoot() && this.world.getGameRules().getBoolean("doMobLoot"))) {
            int i = this.getExperiencePoints(this.attackingPlayer);

            while(i > 0) {
               int j = EntityXPOrb.getXPSplit(i);
               i -= j;
               this.world.spawnEntity(new EntityXPOrb(this.world, this.posX, this.posY, this.posZ, j));
            }
         }

         this.remove();

         for(int k = 0; k < 20; ++k) {
            double d2 = this.rand.nextGaussian() * 0.02D;
            double d0 = this.rand.nextGaussian() * 0.02D;
            double d1 = this.rand.nextGaussian() * 0.02D;
            this.world.addParticle(Particles.POOF, this.posX + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, this.posY + (double)(this.rand.nextFloat() * this.height), this.posZ + (double)(this.rand.nextFloat() * this.width * 2.0F) - (double)this.width, d2, d0, d1);
         }
      }

   }

   protected boolean canDropLoot() {
      return !this.isChild();
   }

   protected int decreaseAirSupply(int air) {
      int i = EnchantmentHelper.getRespirationModifier(this);
      return i > 0 && this.rand.nextInt(i + 1) > 0 ? air : air - 1;
   }

   protected int determineNextAir(int currentAir) {
      return Math.min(currentAir + 4, this.getMaxAir());
   }

   protected int getExperiencePoints(EntityPlayer player) {
      return 0;
   }

   protected boolean isPlayer() {
      return false;
   }

   public Random getRNG() {
      return this.rand;
   }

   @Nullable
   public EntityLivingBase getRevengeTarget() {
      return this.revengeTarget;
   }

   public int getRevengeTimer() {
      return this.revengeTimer;
   }

   public void setRevengeTarget(@Nullable EntityLivingBase livingBase) {
      this.revengeTarget = livingBase;
      this.revengeTimer = this.ticksExisted;
   }

   public EntityLivingBase getLastAttackedEntity() {
      return this.lastAttackedEntity;
   }

   public int getLastAttackedEntityTime() {
      return this.lastAttackedEntityTime;
   }

   public void setLastAttackedEntity(Entity entityIn) {
      if (entityIn instanceof EntityLivingBase) {
         this.lastAttackedEntity = (EntityLivingBase)entityIn;
      } else {
         this.lastAttackedEntity = null;
      }

      this.lastAttackedEntityTime = this.ticksExisted;
   }

   public int getIdleTime() {
      return this.idleTime;
   }

   protected void playEquipSound(ItemStack stack) {
      if (!stack.isEmpty()) {
         SoundEvent soundevent = SoundEvents.ITEM_ARMOR_EQUIP_GENERIC;
         Item item = stack.getItem();
         if (item instanceof ItemArmor) {
            soundevent = ((ItemArmor)item).getArmorMaterial().getSoundEvent();
         } else if (item == Items.ELYTRA) {
            soundevent = SoundEvents.ITEM_ARMOR_EQUIP_ELYTRA;
         }

         this.playSound(soundevent, 1.0F, 1.0F);
      }
   }

   public void writeAdditional(NBTTagCompound compound) {
      compound.putFloat("Health", this.getHealth());
      compound.putShort("HurtTime", (short)this.hurtTime);
      compound.putInt("HurtByTimestamp", this.revengeTimer);
      compound.putShort("DeathTime", (short)this.deathTime);
      compound.putFloat("AbsorptionAmount", this.getAbsorptionAmount());

      for(EntityEquipmentSlot entityequipmentslot : EntityEquipmentSlot.values()) {
         ItemStack itemstack = this.getItemStackFromSlot(entityequipmentslot);
         if (!itemstack.isEmpty()) {
            this.getAttributeMap().removeAttributeModifiers(itemstack.getAttributeModifiers(entityequipmentslot));
         }
      }

      compound.put("Attributes", SharedMonsterAttributes.writeAttributes(this.getAttributeMap()));

      for(EntityEquipmentSlot entityequipmentslot1 : EntityEquipmentSlot.values()) {
         ItemStack itemstack1 = this.getItemStackFromSlot(entityequipmentslot1);
         if (!itemstack1.isEmpty()) {
            this.getAttributeMap().applyAttributeModifiers(itemstack1.getAttributeModifiers(entityequipmentslot1));
         }
      }

      if (!this.activePotionsMap.isEmpty()) {
         NBTTagList nbttaglist = new NBTTagList();

         for(PotionEffect potioneffect : this.activePotionsMap.values()) {
            nbttaglist.add((INBTBase)potioneffect.write(new NBTTagCompound()));
         }

         compound.put("ActiveEffects", nbttaglist);
      }

      compound.putBoolean("FallFlying", this.isElytraFlying());
   }

   public void readAdditional(NBTTagCompound compound) {
      this.setAbsorptionAmount(compound.getFloat("AbsorptionAmount"));
      if (compound.contains("Attributes", 9) && this.world != null && !this.world.isRemote) {
         SharedMonsterAttributes.readAttributes(this.getAttributeMap(), compound.getList("Attributes", 10));
      }

      if (compound.contains("ActiveEffects", 9)) {
         NBTTagList nbttaglist = compound.getList("ActiveEffects", 10);

         for(int i = 0; i < nbttaglist.size(); ++i) {
            NBTTagCompound nbttagcompound = nbttaglist.getCompound(i);
            PotionEffect potioneffect = PotionEffect.read(nbttagcompound);
            if (potioneffect != null) {
               this.activePotionsMap.put(potioneffect.getPotion(), potioneffect);
            }
         }
      }

      if (compound.contains("Health", 99)) {
         this.setHealth(compound.getFloat("Health"));
      }

      this.hurtTime = compound.getShort("HurtTime");
      this.deathTime = compound.getShort("DeathTime");
      this.revengeTimer = compound.getInt("HurtByTimestamp");
      if (compound.contains("Team", 8)) {
         String s = compound.getString("Team");
         ScorePlayerTeam scoreplayerteam = this.world.getScoreboard().getTeam(s);
         boolean flag = scoreplayerteam != null && this.world.getScoreboard().addPlayerToTeam(this.getCachedUniqueIdString(), scoreplayerteam);
         if (!flag) {
            LOGGER.warn("Unable to add mob to team \"{}\" (that team probably doesn't exist)", (Object)s);
         }
      }

      if (compound.getBoolean("FallFlying")) {
         this.setFlag(7, true);
      }

   }

   protected void updatePotionEffects() {
      Iterator<Potion> iterator = this.activePotionsMap.keySet().iterator();

      try {
         while(iterator.hasNext()) {
            Potion potion = iterator.next();
            PotionEffect potioneffect = this.activePotionsMap.get(potion);
            if (!potioneffect.tick(this)) {
               if (!this.world.isRemote) {
                  iterator.remove();
                  this.onFinishedPotionEffect(potioneffect);
               }
            } else if (potioneffect.getDuration() % 600 == 0) {
               this.onChangedPotionEffect(potioneffect, false);
            }
         }
      } catch (ConcurrentModificationException var11) {
         ;
      }

      if (this.potionsNeedUpdate) {
         if (!this.world.isRemote) {
            this.updatePotionMetadata();
         }

         this.potionsNeedUpdate = false;
      }

      int i = this.dataManager.get(POTION_EFFECTS);
      boolean flag1 = this.dataManager.get(HIDE_PARTICLES);
      if (i > 0) {
         boolean flag;
         if (this.isInvisible()) {
            flag = this.rand.nextInt(15) == 0;
         } else {
            flag = this.rand.nextBoolean();
         }

         if (flag1) {
            flag &= this.rand.nextInt(5) == 0;
         }

         if (flag && i > 0) {
            double d0 = (double)(i >> 16 & 255) / 255.0D;
            double d1 = (double)(i >> 8 & 255) / 255.0D;
            double d2 = (double)(i >> 0 & 255) / 255.0D;
            this.world.addParticle(flag1 ? Particles.AMBIENT_ENTITY_EFFECT : Particles.ENTITY_EFFECT, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, d0, d1, d2);
         }
      }

   }

   protected void updatePotionMetadata() {
      if (this.activePotionsMap.isEmpty()) {
         this.resetPotionEffectMetadata();
         this.setInvisible(false);
      } else {
         Collection<PotionEffect> collection = this.activePotionsMap.values();
         this.dataManager.set(HIDE_PARTICLES, areAllPotionsAmbient(collection));
         this.dataManager.set(POTION_EFFECTS, PotionUtils.getPotionColorFromEffectList(collection));
         this.setInvisible(this.isPotionActive(MobEffects.INVISIBILITY));
      }

   }

   public static boolean areAllPotionsAmbient(Collection<PotionEffect> potionEffects) {
      for(PotionEffect potioneffect : potionEffects) {
         if (!potioneffect.isAmbient()) {
            return false;
         }
      }

      return true;
   }

   protected void resetPotionEffectMetadata() {
      this.dataManager.set(HIDE_PARTICLES, false);
      this.dataManager.set(POTION_EFFECTS, 0);
   }

   public boolean clearActivePotions() {
      if (this.world.isRemote) {
         return false;
      } else {
         Iterator<PotionEffect> iterator = this.activePotionsMap.values().iterator();

         boolean flag;
         for(flag = false; iterator.hasNext(); flag = true) {
            this.onFinishedPotionEffect(iterator.next());
            iterator.remove();
         }

         return flag;
      }
   }

   public Collection<PotionEffect> getActivePotionEffects() {
      return this.activePotionsMap.values();
   }

   public Map<Potion, PotionEffect> getActivePotionMap() {
      return this.activePotionsMap;
   }

   public boolean isPotionActive(Potion potionIn) {
      return this.activePotionsMap.containsKey(potionIn);
   }

   @Nullable
   public PotionEffect getActivePotionEffect(Potion potionIn) {
      return this.activePotionsMap.get(potionIn);
   }

   public boolean addPotionEffect(PotionEffect p_195064_1_) {
      if (!this.isPotionApplicable(p_195064_1_)) {
         return false;
      } else {
         PotionEffect potioneffect = this.activePotionsMap.get(p_195064_1_.getPotion());
         if (potioneffect == null) {
            this.activePotionsMap.put(p_195064_1_.getPotion(), p_195064_1_);
            this.onNewPotionEffect(p_195064_1_);
            return true;
         } else if (potioneffect.combine(p_195064_1_)) {
            this.onChangedPotionEffect(potioneffect, true);
            return true;
         } else {
            return false;
         }
      }
   }

   public boolean isPotionApplicable(PotionEffect potioneffectIn) {
      if (this.getCreatureAttribute() == CreatureAttribute.UNDEAD) {
         Potion potion = potioneffectIn.getPotion();
         if (potion == MobEffects.REGENERATION || potion == MobEffects.POISON) {
            return false;
         }
      }

      return true;
   }

   public boolean isEntityUndead() {
      return this.getCreatureAttribute() == CreatureAttribute.UNDEAD;
   }

   @Nullable
   public PotionEffect removeActivePotionEffect(@Nullable Potion potioneffectin) {
      return this.activePotionsMap.remove(potioneffectin);
   }

   public boolean removePotionEffect(Potion p_195063_1_) {
      PotionEffect potioneffect = this.removeActivePotionEffect(p_195063_1_);
      if (potioneffect != null) {
         this.onFinishedPotionEffect(potioneffect);
         return true;
      } else {
         return false;
      }
   }

   protected void onNewPotionEffect(PotionEffect id) {
      this.potionsNeedUpdate = true;
      if (!this.world.isRemote) {
         id.getPotion().applyAttributesModifiersToEntity(this, this.getAttributeMap(), id.getAmplifier());
      }

   }

   protected void onChangedPotionEffect(PotionEffect id, boolean p_70695_2_) {
      this.potionsNeedUpdate = true;
      if (p_70695_2_ && !this.world.isRemote) {
         Potion potion = id.getPotion();
         potion.removeAttributesModifiersFromEntity(this, this.getAttributeMap(), id.getAmplifier());
         potion.applyAttributesModifiersToEntity(this, this.getAttributeMap(), id.getAmplifier());
      }

   }

   protected void onFinishedPotionEffect(PotionEffect effect) {
      this.potionsNeedUpdate = true;
      if (!this.world.isRemote) {
         effect.getPotion().removeAttributesModifiersFromEntity(this, this.getAttributeMap(), effect.getAmplifier());
      }

   }

   public void heal(float healAmount) {
      float f = this.getHealth();
      if (f > 0.0F) {
         this.setHealth(f + healAmount);
      }

   }

   public float getHealth() {
      return this.dataManager.get(HEALTH);
   }

   public void setHealth(float health) {
      this.dataManager.set(HEALTH, MathHelper.clamp(health, 0.0F, this.getMaxHealth()));
   }

   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (this.isInvulnerableTo(source)) {
         return false;
      } else if (this.world.isRemote) {
         return false;
      } else if (this.getHealth() <= 0.0F) {
         return false;
      } else if (source.isFireDamage() && this.isPotionActive(MobEffects.FIRE_RESISTANCE)) {
         return false;
      } else {
         this.idleTime = 0;
         float f = amount;
         if ((source == DamageSource.ANVIL || source == DamageSource.FALLING_BLOCK) && !this.getItemStackFromSlot(EntityEquipmentSlot.HEAD).isEmpty()) {
            this.getItemStackFromSlot(EntityEquipmentSlot.HEAD).damageItem((int)(amount * 4.0F + this.rand.nextFloat() * amount * 2.0F), this);
            amount *= 0.75F;
         }

         boolean flag = false;
         float f1 = 0.0F;
         if (amount > 0.0F && this.canBlockDamageSource(source)) {
            this.damageShield(amount);
            f1 = amount;
            amount = 0.0F;
            if (!source.isProjectile()) {
               Entity entity = source.getImmediateSource();
               if (entity instanceof EntityLivingBase) {
                  this.blockUsingShield((EntityLivingBase)entity);
               }
            }

            flag = true;
         }

         this.limbSwingAmount = 1.5F;
         boolean flag1 = true;
         if ((float)this.hurtResistantTime > (float)this.maxHurtResistantTime / 2.0F) {
            if (amount <= this.lastDamage) {
               return false;
            }

            this.damageEntity(source, amount - this.lastDamage);
            this.lastDamage = amount;
            flag1 = false;
         } else {
            this.lastDamage = amount;
            this.hurtResistantTime = this.maxHurtResistantTime;
            this.damageEntity(source, amount);
            this.maxHurtTime = 10;
            this.hurtTime = this.maxHurtTime;
         }

         this.attackedAtYaw = 0.0F;
         Entity entity1 = source.getTrueSource();
         if (entity1 != null) {
            if (entity1 instanceof EntityLivingBase) {
               this.setRevengeTarget((EntityLivingBase)entity1);
            }

            if (entity1 instanceof EntityPlayer) {
               this.recentlyHit = 100;
               this.attackingPlayer = (EntityPlayer)entity1;
            } else if (entity1 instanceof EntityWolf) {
               EntityWolf entitywolf = (EntityWolf)entity1;
               if (entitywolf.isTamed()) {
                  this.recentlyHit = 100;
                  this.attackingPlayer = null;
               }
            }
         }

         if (flag1) {
            if (flag) {
               this.world.setEntityState(this, (byte)29);
            } else if (source instanceof EntityDamageSource && ((EntityDamageSource)source).getIsThornsDamage()) {
               this.world.setEntityState(this, (byte)33);
            } else {
               byte b0;
               if (source == DamageSource.DROWN) {
                  b0 = 36;
               } else if (source.isFireDamage()) {
                  b0 = 37;
               } else {
                  b0 = 2;
               }

               this.world.setEntityState(this, b0);
            }

            if (source != DamageSource.DROWN && (!flag || amount > 0.0F)) {
               this.markVelocityChanged();
            }

            if (entity1 != null) {
               double d1 = entity1.posX - this.posX;

               double d0;
               for(d0 = entity1.posZ - this.posZ; d1 * d1 + d0 * d0 < 1.0E-4D; d0 = (Math.random() - Math.random()) * 0.01D) {
                  d1 = (Math.random() - Math.random()) * 0.01D;
               }

               this.attackedAtYaw = (float)(MathHelper.atan2(d0, d1) * (double)(180F / (float)Math.PI) - (double)this.rotationYaw);
               this.knockBack(entity1, 0.4F, d1, d0);
            } else {
               this.attackedAtYaw = (float)((int)(Math.random() * 2.0D) * 180);
            }
         }

         if (this.getHealth() <= 0.0F) {
            if (!this.checkTotemDeathProtection(source)) {
               SoundEvent soundevent = this.getDeathSound();
               if (flag1 && soundevent != null) {
                  this.playSound(soundevent, this.getSoundVolume(), this.getSoundPitch());
               }

               this.onDeath(source);
            }
         } else if (flag1) {
            this.playHurtSound(source);
         }

         boolean flag2 = !flag || amount > 0.0F;
         if (flag2) {
            this.lastDamageSource = source;
            this.lastDamageStamp = this.world.getGameTime();
         }

         if (this instanceof EntityPlayerMP) {
            CriteriaTriggers.ENTITY_HURT_PLAYER.trigger((EntityPlayerMP)this, source, f, amount, flag);
            if (f1 > 0.0F && f1 < 3.4028235E37F) {
               ((EntityPlayerMP)this).addStat(StatList.DAMAGE_BLOCKED_BY_SHIELD, Math.round(f1 * 10.0F));
            }
         }

         if (entity1 instanceof EntityPlayerMP) {
            CriteriaTriggers.PLAYER_HURT_ENTITY.trigger((EntityPlayerMP)entity1, this, source, f, amount, flag);
         }

         return flag2;
      }
   }

   protected void blockUsingShield(EntityLivingBase p_190629_1_) {
      p_190629_1_.knockBack(this, 0.5F, this.posX - p_190629_1_.posX, this.posZ - p_190629_1_.posZ);
   }

   private boolean checkTotemDeathProtection(DamageSource p_190628_1_) {
      if (p_190628_1_.canHarmInCreative()) {
         return false;
      } else {
         ItemStack itemstack = null;

         for(EnumHand enumhand : EnumHand.values()) {
            ItemStack itemstack1 = this.getHeldItem(enumhand);
            if (itemstack1.getItem() == Items.TOTEM_OF_UNDYING) {
               itemstack = itemstack1.copy();
               itemstack1.shrink(1);
               break;
            }
         }

         if (itemstack != null) {
            if (this instanceof EntityPlayerMP) {
               EntityPlayerMP entityplayermp = (EntityPlayerMP)this;
               entityplayermp.addStat(StatList.ITEM_USED.get(Items.TOTEM_OF_UNDYING));
               CriteriaTriggers.USED_TOTEM.trigger(entityplayermp, itemstack);
            }

            this.setHealth(1.0F);
            this.clearActivePotions();
            this.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 900, 1));
            this.addPotionEffect(new PotionEffect(MobEffects.ABSORPTION, 100, 1));
            this.world.setEntityState(this, (byte)35);
         }

         return itemstack != null;
      }
   }

   @Nullable
   public DamageSource getLastDamageSource() {
      if (this.world.getGameTime() - this.lastDamageStamp > 40L) {
         this.lastDamageSource = null;
      }

      return this.lastDamageSource;
   }

   protected void playHurtSound(DamageSource source) {
      SoundEvent soundevent = this.getHurtSound(source);
      if (soundevent != null) {
         this.playSound(soundevent, this.getSoundVolume(), this.getSoundPitch());
      }

   }

   private boolean canBlockDamageSource(DamageSource damageSourceIn) {
      if (!damageSourceIn.isUnblockable() && this.isActiveItemStackBlocking()) {
         Vec3d vec3d = damageSourceIn.getDamageLocation();
         if (vec3d != null) {
            Vec3d vec3d1 = this.getLook(1.0F);
            Vec3d vec3d2 = vec3d.subtractReverse(new Vec3d(this.posX, this.posY, this.posZ)).normalize();
            vec3d2 = new Vec3d(vec3d2.x, 0.0D, vec3d2.z);
            if (vec3d2.dotProduct(vec3d1) < 0.0D) {
               return true;
            }
         }
      }

      return false;
   }

   public void renderBrokenItemStack(ItemStack stack) {
      super.playSound(SoundEvents.ENTITY_ITEM_BREAK, 0.8F, 0.8F + this.world.rand.nextFloat() * 0.4F);
      this.addItemParticles(stack, 5);
   }

   public void onDeath(DamageSource cause) {
      if (!this.dead) {
         Entity entity = cause.getTrueSource();
         EntityLivingBase entitylivingbase = this.getAttackingEntity();
         if (this.scoreValue >= 0 && entitylivingbase != null) {
            entitylivingbase.awardKillScore(this, this.scoreValue, cause);
         }

         if (entity != null) {
            entity.onKillEntity(this);
         }

         this.dead = true;
         this.getCombatTracker().reset();
         if (!this.world.isRemote) {
            int i = 0;
            if (entity instanceof EntityPlayer) {
               i = EnchantmentHelper.getLootingModifier((EntityLivingBase)entity);
            }

            if (this.canDropLoot() && this.world.getGameRules().getBoolean("doMobLoot")) {
               boolean flag = this.recentlyHit > 0;
               this.dropLoot(flag, i, cause);
            }
         }

         this.world.setEntityState(this, (byte)3);
      }
   }

   protected void dropLoot(boolean wasRecentlyHit, int lootingModifier, DamageSource source) {
      this.dropFewItems(wasRecentlyHit, lootingModifier);
      this.dropEquipment(wasRecentlyHit, lootingModifier);
   }

   protected void dropEquipment(boolean wasRecentlyHit, int lootingModifier) {
   }

   public void knockBack(Entity entityIn, float strength, double xRatio, double zRatio) {
      if (!(this.rand.nextDouble() < this.getAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).getValue())) {
         this.isAirBorne = true;
         float f = MathHelper.sqrt(xRatio * xRatio + zRatio * zRatio);
         this.motionX /= 2.0D;
         this.motionZ /= 2.0D;
         this.motionX -= xRatio / (double)f * (double)strength;
         this.motionZ -= zRatio / (double)f * (double)strength;
         if (this.onGround) {
            this.motionY /= 2.0D;
            this.motionY += (double)strength;
            if (this.motionY > (double)0.4F) {
               this.motionY = (double)0.4F;
            }
         }

      }
   }

   @Nullable
   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return SoundEvents.ENTITY_GENERIC_HURT;
   }

   @Nullable
   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_GENERIC_DEATH;
   }

   protected SoundEvent getFallSound(int heightIn) {
      return heightIn > 4 ? SoundEvents.ENTITY_GENERIC_BIG_FALL : SoundEvents.ENTITY_GENERIC_SMALL_FALL;
   }

   protected void dropFewItems(boolean wasRecentlyHit, int lootingModifier) {
   }

   public boolean isOnLadder() {
      int i = MathHelper.floor(this.posX);
      int j = MathHelper.floor(this.getBoundingBox().minY);
      int k = MathHelper.floor(this.posZ);
      if (this instanceof EntityPlayer && ((EntityPlayer)this).isSpectator()) {
         return false;
      } else {
         BlockPos blockpos = new BlockPos(i, j, k);
         IBlockState iblockstate = this.world.getBlockState(blockpos);
         Block block = iblockstate.getBlock();
         if (block != Blocks.LADDER && block != Blocks.VINE) {
            return block instanceof BlockTrapDoor && this.canGoThroughtTrapDoorOnLadder(blockpos, iblockstate);
         } else {
            return true;
         }
      }
   }

   private boolean canGoThroughtTrapDoorOnLadder(BlockPos pos, IBlockState state) {
      if (state.get(BlockTrapDoor.OPEN)) {
         IBlockState iblockstate = this.world.getBlockState(pos.down());
         if (iblockstate.getBlock() == Blocks.LADDER && iblockstate.get(BlockLadder.FACING) == state.get(BlockTrapDoor.HORIZONTAL_FACING)) {
            return true;
         }
      }

      return false;
   }

   public boolean isAlive() {
      return !this.removed && this.getHealth() > 0.0F;
   }

   public void fall(float distance, float damageMultiplier) {
      super.fall(distance, damageMultiplier);
      PotionEffect potioneffect = this.getActivePotionEffect(MobEffects.JUMP_BOOST);
      float f = potioneffect == null ? 0.0F : (float)(potioneffect.getAmplifier() + 1);
      int i = MathHelper.ceil((distance - 3.0F - f) * damageMultiplier);
      if (i > 0) {
         this.playSound(this.getFallSound(i), 1.0F, 1.0F);
         this.attackEntityFrom(DamageSource.FALL, (float)i);
         int j = MathHelper.floor(this.posX);
         int k = MathHelper.floor(this.posY - (double)0.2F);
         int l = MathHelper.floor(this.posZ);
         IBlockState iblockstate = this.world.getBlockState(new BlockPos(j, k, l));
         if (!iblockstate.isAir()) {
            SoundType soundtype = iblockstate.getBlock().getSoundType();
            this.playSound(soundtype.getFallSound(), soundtype.getVolume() * 0.5F, soundtype.getPitch() * 0.75F);
         }
      }

   }

   @OnlyIn(Dist.CLIENT)
   public void performHurtAnimation() {
      this.maxHurtTime = 10;
      this.hurtTime = this.maxHurtTime;
      this.attackedAtYaw = 0.0F;
   }

   public int getTotalArmorValue() {
      IAttributeInstance iattributeinstance = this.getAttribute(SharedMonsterAttributes.ARMOR);
      return MathHelper.floor(iattributeinstance.getValue());
   }

   protected void damageArmor(float damage) {
   }

   protected void damageShield(float damage) {
   }

   protected float applyArmorCalculations(DamageSource source, float damage) {
      if (!source.isUnblockable()) {
         this.damageArmor(damage);
         damage = CombatRules.getDamageAfterAbsorb(damage, (float)this.getTotalArmorValue(), (float)this.getAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getValue());
      }

      return damage;
   }

   protected float applyPotionDamageCalculations(DamageSource source, float damage) {
      if (source.isDamageAbsolute()) {
         return damage;
      } else {
         if (this.isPotionActive(MobEffects.RESISTANCE) && source != DamageSource.OUT_OF_WORLD) {
            int i = (this.getActivePotionEffect(MobEffects.RESISTANCE).getAmplifier() + 1) * 5;
            int j = 25 - i;
            float f = damage * (float)j;
            float f1 = damage;
            damage = Math.max(f / 25.0F, 0.0F);
            float f2 = f1 - damage;
            if (f2 > 0.0F && f2 < 3.4028235E37F) {
               if (this instanceof EntityPlayerMP) {
                  ((EntityPlayerMP)this).addStat(StatList.DAMAGE_RESISTED, Math.round(f2 * 10.0F));
               } else if (source.getTrueSource() instanceof EntityPlayerMP) {
                  ((EntityPlayerMP)source.getTrueSource()).addStat(StatList.DAMAGE_DEALT_RESISTED, Math.round(f2 * 10.0F));
               }
            }
         }

         if (damage <= 0.0F) {
            return 0.0F;
         } else {
            int k = EnchantmentHelper.getEnchantmentModifierDamage(this.getArmorInventoryList(), source);
            if (k > 0) {
               damage = CombatRules.getDamageAfterMagicAbsorb(damage, (float)k);
            }

            return damage;
         }
      }
   }

   protected void damageEntity(DamageSource damageSrc, float damageAmount) {
      if (!this.isInvulnerableTo(damageSrc)) {
         damageAmount = this.applyArmorCalculations(damageSrc, damageAmount);
         damageAmount = this.applyPotionDamageCalculations(damageSrc, damageAmount);
         float f = damageAmount;
         damageAmount = Math.max(damageAmount - this.getAbsorptionAmount(), 0.0F);
         this.setAbsorptionAmount(this.getAbsorptionAmount() - (f - damageAmount));
         float f1 = f - damageAmount;
         if (f1 > 0.0F && f1 < 3.4028235E37F && damageSrc.getTrueSource() instanceof EntityPlayerMP) {
            ((EntityPlayerMP)damageSrc.getTrueSource()).addStat(StatList.DAMAGE_DEALT_ABSORBED, Math.round(f1 * 10.0F));
         }

         if (damageAmount != 0.0F) {
            float f2 = this.getHealth();
            this.setHealth(f2 - damageAmount);
            this.getCombatTracker().trackDamage(damageSrc, f2, damageAmount);
            this.setAbsorptionAmount(this.getAbsorptionAmount() - damageAmount);
         }
      }
   }

   public CombatTracker getCombatTracker() {
      return this.combatTracker;
   }

   @Nullable
   public EntityLivingBase getAttackingEntity() {
      if (this.combatTracker.getBestAttacker() != null) {
         return this.combatTracker.getBestAttacker();
      } else if (this.attackingPlayer != null) {
         return this.attackingPlayer;
      } else {
         return this.revengeTarget != null ? this.revengeTarget : null;
      }
   }

   public final float getMaxHealth() {
      return (float)this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).getValue();
   }

   public final int getArrowCountInEntity() {
      return this.dataManager.get(ARROW_COUNT_IN_ENTITY);
   }

   public final void setArrowCountInEntity(int count) {
      this.dataManager.set(ARROW_COUNT_IN_ENTITY, count);
   }

   private int getArmSwingAnimationEnd() {
      if (PotionUtil.hasMiningSpeedup(this)) {
         return 6 - (1 + PotionUtil.getMiningSpeedup(this));
      } else {
         return this.isPotionActive(MobEffects.MINING_FATIGUE) ? 6 + (1 + this.getActivePotionEffect(MobEffects.MINING_FATIGUE).getAmplifier()) * 2 : 6;
      }
   }

   public void swingArm(EnumHand hand) {
      if (!this.isSwingInProgress || this.swingProgressInt >= this.getArmSwingAnimationEnd() / 2 || this.swingProgressInt < 0) {
         this.swingProgressInt = -1;
         this.isSwingInProgress = true;
         this.swingingHand = hand;
         if (this.world instanceof WorldServer) {
            ((WorldServer)this.world).getEntityTracker().sendToTracking(this, new SPacketAnimation(this, hand == EnumHand.MAIN_HAND ? 0 : 3));
         }
      }

   }

   @OnlyIn(Dist.CLIENT)
   public void handleStatusUpdate(byte id) {
      boolean flag = id == 33;
      boolean flag1 = id == 36;
      boolean flag2 = id == 37;
      if (id != 2 && !flag && !flag1 && !flag2) {
         if (id == 3) {
            SoundEvent soundevent1 = this.getDeathSound();
            if (soundevent1 != null) {
               this.playSound(soundevent1, this.getSoundVolume(), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
            }

            this.setHealth(0.0F);
            this.onDeath(DamageSource.GENERIC);
         } else if (id == 30) {
            this.playSound(SoundEvents.ITEM_SHIELD_BREAK, 0.8F, 0.8F + this.world.rand.nextFloat() * 0.4F);
         } else if (id == 29) {
            this.playSound(SoundEvents.ITEM_SHIELD_BLOCK, 1.0F, 0.8F + this.world.rand.nextFloat() * 0.4F);
         } else {
            super.handleStatusUpdate(id);
         }
      } else {
         this.limbSwingAmount = 1.5F;
         this.hurtResistantTime = this.maxHurtResistantTime;
         this.maxHurtTime = 10;
         this.hurtTime = this.maxHurtTime;
         this.attackedAtYaw = 0.0F;
         if (flag) {
            this.playSound(SoundEvents.ENCHANT_THORNS_HIT, this.getSoundVolume(), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
         }

         DamageSource damagesource;
         if (flag2) {
            damagesource = DamageSource.ON_FIRE;
         } else if (flag1) {
            damagesource = DamageSource.DROWN;
         } else {
            damagesource = DamageSource.GENERIC;
         }

         SoundEvent soundevent = this.getHurtSound(damagesource);
         if (soundevent != null) {
            this.playSound(soundevent, this.getSoundVolume(), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
         }

         this.attackEntityFrom(DamageSource.GENERIC, 0.0F);
      }

   }

   protected void outOfWorld() {
      this.attackEntityFrom(DamageSource.OUT_OF_WORLD, 4.0F);
   }

   protected void updateArmSwingProgress() {
      int i = this.getArmSwingAnimationEnd();
      if (this.isSwingInProgress) {
         ++this.swingProgressInt;
         if (this.swingProgressInt >= i) {
            this.swingProgressInt = 0;
            this.isSwingInProgress = false;
         }
      } else {
         this.swingProgressInt = 0;
      }

      this.swingProgress = (float)this.swingProgressInt / (float)i;
   }

   public IAttributeInstance getAttribute(IAttribute attribute) {
      return this.getAttributeMap().getAttributeInstance(attribute);
   }

   public AbstractAttributeMap getAttributeMap() {
      if (this.attributeMap == null) {
         this.attributeMap = new AttributeMap();
      }

      return this.attributeMap;
   }

   public CreatureAttribute getCreatureAttribute() {
      return CreatureAttribute.UNDEFINED;
   }

   public ItemStack getHeldItemMainhand() {
      return this.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);
   }

   public ItemStack getHeldItemOffhand() {
      return this.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND);
   }

   public ItemStack getHeldItem(EnumHand hand) {
      if (hand == EnumHand.MAIN_HAND) {
         return this.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);
      } else if (hand == EnumHand.OFF_HAND) {
         return this.getItemStackFromSlot(EntityEquipmentSlot.OFFHAND);
      } else {
         throw new IllegalArgumentException("Invalid hand " + hand);
      }
   }

   public void setHeldItem(EnumHand hand, ItemStack stack) {
      if (hand == EnumHand.MAIN_HAND) {
         this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, stack);
      } else {
         if (hand != EnumHand.OFF_HAND) {
            throw new IllegalArgumentException("Invalid hand " + hand);
         }

         this.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, stack);
      }

   }

   public boolean hasItemInSlot(EntityEquipmentSlot p_190630_1_) {
      return !this.getItemStackFromSlot(p_190630_1_).isEmpty();
   }

   public abstract Iterable<ItemStack> getArmorInventoryList();

   public abstract ItemStack getItemStackFromSlot(EntityEquipmentSlot slotIn);

   public abstract void setItemStackToSlot(EntityEquipmentSlot slotIn, ItemStack stack);

   public void setSprinting(boolean sprinting) {
      super.setSprinting(sprinting);
      IAttributeInstance iattributeinstance = this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
      if (iattributeinstance.getModifier(SPRINTING_SPEED_BOOST_ID) != null) {
         iattributeinstance.removeModifier(SPRINTING_SPEED_BOOST);
      }

      if (sprinting) {
         iattributeinstance.applyModifier(SPRINTING_SPEED_BOOST);
      }

   }

   protected float getSoundVolume() {
      return 1.0F;
   }

   protected float getSoundPitch() {
      return this.isChild() ? (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.5F : (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F;
   }

   protected boolean isMovementBlocked() {
      return this.getHealth() <= 0.0F;
   }

   public void dismountEntity(Entity entityIn) {
      if (!(entityIn instanceof EntityBoat) && !(entityIn instanceof AbstractHorse)) {
         double d1 = entityIn.posX;
         double d13 = entityIn.getBoundingBox().minY + (double)entityIn.height;
         double d14 = entityIn.posZ;
         EnumFacing enumfacing1 = entityIn.getAdjustedHorizontalFacing();
         if (enumfacing1 != null) {
            EnumFacing enumfacing = enumfacing1.rotateY();
            int[][] aint1 = new int[][]{{0, 1}, {0, -1}, {-1, 1}, {-1, -1}, {1, 1}, {1, -1}, {-1, 0}, {1, 0}, {0, 1}};
            double d5 = Math.floor(this.posX) + 0.5D;
            double d6 = Math.floor(this.posZ) + 0.5D;
            double d7 = this.getBoundingBox().maxX - this.getBoundingBox().minX;
            double d8 = this.getBoundingBox().maxZ - this.getBoundingBox().minZ;
            AxisAlignedBB axisalignedbb = new AxisAlignedBB(d5 - d7 / 2.0D, entityIn.getBoundingBox().minY, d6 - d8 / 2.0D, d5 + d7 / 2.0D, Math.floor(entityIn.getBoundingBox().minY) + (double)this.height, d6 + d8 / 2.0D);

            for(int[] aint : aint1) {
               double d9 = (double)(enumfacing1.getXOffset() * aint[0] + enumfacing.getXOffset() * aint[1]);
               double d10 = (double)(enumfacing1.getZOffset() * aint[0] + enumfacing.getZOffset() * aint[1]);
               double d11 = d5 + d9;
               double d12 = d6 + d10;
               AxisAlignedBB axisalignedbb1 = axisalignedbb.offset(d9, 0.0D, d10);
               if (this.world.isCollisionBoxesEmpty(this, axisalignedbb1)) {
                  if (this.world.getBlockState(new BlockPos(d11, this.posY, d12)).isTopSolid()) {
                     this.setPositionAndUpdate(d11, this.posY + 1.0D, d12);
                     return;
                  }

                  BlockPos blockpos = new BlockPos(d11, this.posY - 1.0D, d12);
                  if (this.world.getBlockState(blockpos).isTopSolid() || this.world.getFluidState(blockpos).isTagged(FluidTags.WATER)) {
                     d1 = d11;
                     d13 = this.posY + 1.0D;
                     d14 = d12;
                  }
               } else if (this.world.isCollisionBoxesEmpty(this, axisalignedbb1.offset(0.0D, 1.0D, 0.0D)) && this.world.getBlockState(new BlockPos(d11, this.posY + 1.0D, d12)).isTopSolid()) {
                  d1 = d11;
                  d13 = this.posY + 2.0D;
                  d14 = d12;
               }
            }
         }

         this.setPositionAndUpdate(d1, d13, d14);
      } else {
         double d0 = (double)(this.width / 2.0F + entityIn.width / 2.0F) + 0.4D;
         float f;
         if (entityIn instanceof EntityBoat) {
            f = 0.0F;
         } else {
            f = ((float)Math.PI / 2F) * (float)(this.getPrimaryHand() == EnumHandSide.RIGHT ? -1 : 1);
         }

         float f1 = -MathHelper.sin(-this.rotationYaw * ((float)Math.PI / 180F) - (float)Math.PI + f);
         float f2 = -MathHelper.cos(-this.rotationYaw * ((float)Math.PI / 180F) - (float)Math.PI + f);
         double d2 = Math.abs(f1) > Math.abs(f2) ? d0 / (double)Math.abs(f1) : d0 / (double)Math.abs(f2);
         double d3 = this.posX + (double)f1 * d2;
         double d4 = this.posZ + (double)f2 * d2;
         this.setPosition(d3, entityIn.posY + (double)entityIn.height + 0.001D, d4);
         if (!this.world.isCollisionBoxesEmpty(this, this.getBoundingBox().union(entityIn.getBoundingBox()))) {
            this.setPosition(d3, entityIn.posY + (double)entityIn.height + 1.001D, d4);
            if (!this.world.isCollisionBoxesEmpty(this, this.getBoundingBox().union(entityIn.getBoundingBox()))) {
               this.setPosition(entityIn.posX, entityIn.posY + (double)this.height + 0.001D, entityIn.posZ);
            }
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public boolean getAlwaysRenderNameTagForRender() {
      return this.isCustomNameVisible();
   }

   protected float getJumpUpwardsMotion() {
      return 0.42F;
   }

   protected void jump() {
      this.motionY = (double)this.getJumpUpwardsMotion();
      if (this.isPotionActive(MobEffects.JUMP_BOOST)) {
         this.motionY += (double)((float)(this.getActivePotionEffect(MobEffects.JUMP_BOOST).getAmplifier() + 1) * 0.1F);
      }

      if (this.isSprinting()) {
         float f = this.rotationYaw * ((float)Math.PI / 180F);
         this.motionX -= (double)(MathHelper.sin(f) * 0.2F);
         this.motionZ += (double)(MathHelper.cos(f) * 0.2F);
      }

      this.isAirBorne = true;
   }

   @OnlyIn(Dist.CLIENT)
   protected void handleFluidSneak() {
      this.motionY -= (double)0.04F;
   }

   protected void handleFluidJump(Tag<Fluid> p_180466_1_) {
      this.motionY += (double)0.04F;
   }

   protected float getWaterSlowDown() {
      return 0.8F;
   }

   public void travel(float strafe, float vertical, float forward) {
      if (this.isServerWorld() || this.canPassengerSteer()) {
         double d0 = 0.08D;
         if (this.motionY <= 0.0D && this.isPotionActive(MobEffects.SLOW_FALLING)) {
            d0 = 0.01D;
            this.fallDistance = 0.0F;
         }

         if (!this.isInWater() || this instanceof EntityPlayer && ((EntityPlayer)this).abilities.isFlying) {
            if (!this.isInLava() || this instanceof EntityPlayer && ((EntityPlayer)this).abilities.isFlying) {
               if (this.isElytraFlying()) {
                  if (this.motionY > -0.5D) {
                     this.fallDistance = 1.0F;
                  }

                  Vec3d vec3d = this.getLookVec();
                  float f = this.rotationPitch * ((float)Math.PI / 180F);
                  double d8 = Math.sqrt(vec3d.x * vec3d.x + vec3d.z * vec3d.z);
                  double d10 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
                  double d2 = vec3d.length();
                  float f5 = MathHelper.cos(f);
                  f5 = (float)((double)f5 * (double)f5 * Math.min(1.0D, d2 / 0.4D));
                  this.motionY += d0 * (-1.0D + (double)f5 * 0.75D);
                  if (this.motionY < 0.0D && d8 > 0.0D) {
                     double d3 = this.motionY * -0.1D * (double)f5;
                     this.motionY += d3;
                     this.motionX += vec3d.x * d3 / d8;
                     this.motionZ += vec3d.z * d3 / d8;
                  }

                  if (f < 0.0F && d8 > 0.0D) {
                     double d11 = d10 * (double)(-MathHelper.sin(f)) * 0.04D;
                     this.motionY += d11 * 3.2D;
                     this.motionX -= vec3d.x * d11 / d8;
                     this.motionZ -= vec3d.z * d11 / d8;
                  }

                  if (d8 > 0.0D) {
                     this.motionX += (vec3d.x / d8 * d10 - this.motionX) * 0.1D;
                     this.motionZ += (vec3d.z / d8 * d10 - this.motionZ) * 0.1D;
                  }

                  this.motionX *= (double)0.99F;
                  this.motionY *= (double)0.98F;
                  this.motionZ *= (double)0.99F;
                  this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
                  if (this.collidedHorizontally && !this.world.isRemote) {
                     double d12 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
                     double d4 = d10 - d12;
                     float f6 = (float)(d4 * 10.0D - 3.0D);
                     if (f6 > 0.0F) {
                        this.playSound(this.getFallSound((int)f6), 1.0F, 1.0F);
                        this.attackEntityFrom(DamageSource.FLY_INTO_WALL, f6);
                     }
                  }

                  if (this.onGround && !this.world.isRemote) {
                     this.setFlag(7, false);
                  }
               } else {
                  float f7 = 0.91F;

                  try (BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain(this.posX, this.getBoundingBox().minY - 1.0D, this.posZ)) {
                     if (this.onGround) {
                        f7 = this.world.getBlockState(blockpos$pooledmutableblockpos).getBlock().getSlipperiness() * 0.91F;
                     }

                     float f8 = 0.16277137F / (f7 * f7 * f7);
                     float f9;
                     if (this.onGround) {
                        f9 = this.getAIMoveSpeed() * f8;
                     } else {
                        f9 = this.jumpMovementFactor;
                     }

                     this.moveRelative(strafe, vertical, forward, f9);
                     f7 = 0.91F;
                     if (this.onGround) {
                        f7 = this.world.getBlockState(blockpos$pooledmutableblockpos.setPos(this.posX, this.getBoundingBox().minY - 1.0D, this.posZ)).getBlock().getSlipperiness() * 0.91F;
                     }

                     if (this.isOnLadder()) {
                        float f4 = 0.15F;
                        this.motionX = MathHelper.clamp(this.motionX, (double)-0.15F, (double)0.15F);
                        this.motionZ = MathHelper.clamp(this.motionZ, (double)-0.15F, (double)0.15F);
                        this.fallDistance = 0.0F;
                        if (this.motionY < -0.15D) {
                           this.motionY = -0.15D;
                        }

                        boolean flag = this.isSneaking() && this instanceof EntityPlayer;
                        if (flag && this.motionY < 0.0D) {
                           this.motionY = 0.0D;
                        }
                     }

                     this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
                     if (this.collidedHorizontally && this.isOnLadder()) {
                        this.motionY = 0.2D;
                     }

                     if (this.isPotionActive(MobEffects.LEVITATION)) {
                        this.motionY += (0.05D * (double)(this.getActivePotionEffect(MobEffects.LEVITATION).getAmplifier() + 1) - this.motionY) * 0.2D;
                        this.fallDistance = 0.0F;
                     } else {
                        blockpos$pooledmutableblockpos.setPos(this.posX, 0.0D, this.posZ);
                        if (!this.world.isRemote || this.world.isBlockLoaded(blockpos$pooledmutableblockpos) && this.world.getChunk(blockpos$pooledmutableblockpos).isLoaded()) {
                           if (!this.hasNoGravity()) {
                              this.motionY -= d0;
                           }
                        } else if (this.posY > 0.0D) {
                           this.motionY = -0.1D;
                        } else {
                           this.motionY = 0.0D;
                        }
                     }

                     this.motionY *= (double)0.98F;
                     this.motionX *= (double)f7;
                     this.motionZ *= (double)f7;
                  }
               }
            } else {
               double d6 = this.posY;
               this.moveRelative(strafe, vertical, forward, 0.02F);
               this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
               this.motionX *= 0.5D;
               this.motionY *= 0.5D;
               this.motionZ *= 0.5D;
               if (!this.hasNoGravity()) {
                  this.motionY -= d0 / 4.0D;
               }

               if (this.collidedHorizontally && this.isOffsetPositionInLiquid(this.motionX, this.motionY + (double)0.6F - this.posY + d6, this.motionZ)) {
                  this.motionY = (double)0.3F;
               }
            }
         } else {
            double d1 = this.posY;
            float f1 = this.isSprinting() ? 0.9F : this.getWaterSlowDown();
            float f2 = 0.02F;
            float f3 = (float)EnchantmentHelper.getDepthStriderModifier(this);
            if (f3 > 3.0F) {
               f3 = 3.0F;
            }

            if (!this.onGround) {
               f3 *= 0.5F;
            }

            if (f3 > 0.0F) {
               f1 += (0.54600006F - f1) * f3 / 3.0F;
               f2 += (this.getAIMoveSpeed() - f2) * f3 / 3.0F;
            }

            if (this.isPotionActive(MobEffects.DOLPHINS_GRACE)) {
               f1 = 0.96F;
            }

            this.moveRelative(strafe, vertical, forward, f2);
            this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
            this.motionX *= (double)f1;
            this.motionY *= (double)0.8F;
            this.motionZ *= (double)f1;
            if (!this.hasNoGravity() && !this.isSprinting()) {
               if (this.motionY <= 0.0D && Math.abs(this.motionY - 0.005D) >= 0.003D && Math.abs(this.motionY - d0 / 16.0D) < 0.003D) {
                  this.motionY = -0.003D;
               } else {
                  this.motionY -= d0 / 16.0D;
               }
            }

            if (this.collidedHorizontally && this.isOffsetPositionInLiquid(this.motionX, this.motionY + (double)0.6F - this.posY + d1, this.motionZ)) {
               this.motionY = (double)0.3F;
            }
         }
      }

      this.prevLimbSwingAmount = this.limbSwingAmount;
      double d5 = this.posX - this.prevPosX;
      double d7 = this.posZ - this.prevPosZ;
      double d9 = this instanceof IFlyingAnimal ? this.posY - this.prevPosY : 0.0D;
      float f10 = MathHelper.sqrt(d5 * d5 + d9 * d9 + d7 * d7) * 4.0F;
      if (f10 > 1.0F) {
         f10 = 1.0F;
      }

      this.limbSwingAmount += (f10 - this.limbSwingAmount) * 0.4F;
      this.limbSwing += this.limbSwingAmount;
   }

   public float getAIMoveSpeed() {
      return this.landMovementFactor;
   }

   public void setAIMoveSpeed(float speedIn) {
      this.landMovementFactor = speedIn;
   }

   public boolean attackEntityAsMob(Entity entityIn) {
      this.setLastAttackedEntity(entityIn);
      return false;
   }

   public boolean isPlayerSleeping() {
      return false;
   }

   public void tick() {
      super.tick();
      this.updateActiveHand();
      this.updateSwimAnimation();
      if (!this.world.isRemote) {
         int i = this.getArrowCountInEntity();
         if (i > 0) {
            if (this.arrowHitTimer <= 0) {
               this.arrowHitTimer = 20 * (30 - i);
            }

            --this.arrowHitTimer;
            if (this.arrowHitTimer <= 0) {
               this.setArrowCountInEntity(i - 1);
            }
         }

         for(EntityEquipmentSlot entityequipmentslot : EQUIPMENT_SLOTS) {
            ItemStack itemstack;
            switch(entityequipmentslot.getSlotType()) {
            case HAND:
               itemstack = this.handInventory.get(entityequipmentslot.getIndex());
               break;
            case ARMOR:
               itemstack = this.armorArray.get(entityequipmentslot.getIndex());
               break;
            default:
               continue;
            }

            ItemStack itemstack1 = this.getItemStackFromSlot(entityequipmentslot);
            if (!ItemStack.areItemStacksEqual(itemstack1, itemstack)) {
               ((WorldServer)this.world).getEntityTracker().sendToTracking(this, new SPacketEntityEquipment(this.getEntityId(), entityequipmentslot, itemstack1));
               if (!itemstack.isEmpty()) {
                  this.getAttributeMap().removeAttributeModifiers(itemstack.getAttributeModifiers(entityequipmentslot));
               }

               if (!itemstack1.isEmpty()) {
                  this.getAttributeMap().applyAttributeModifiers(itemstack1.getAttributeModifiers(entityequipmentslot));
               }

               switch(entityequipmentslot.getSlotType()) {
               case HAND:
                  this.handInventory.set(entityequipmentslot.getIndex(), itemstack1.isEmpty() ? ItemStack.EMPTY : itemstack1.copy());
                  break;
               case ARMOR:
                  this.armorArray.set(entityequipmentslot.getIndex(), itemstack1.isEmpty() ? ItemStack.EMPTY : itemstack1.copy());
               }
            }
         }

         if (this.ticksExisted % 20 == 0) {
            this.getCombatTracker().reset();
         }

         if (!this.glowing) {
            boolean flag = this.isPotionActive(MobEffects.GLOWING);
            if (this.getFlag(6) != flag) {
               this.setFlag(6, flag);
            }
         }
      }

      this.livingTick();
      double d0 = this.posX - this.prevPosX;
      double d1 = this.posZ - this.prevPosZ;
      float f3 = (float)(d0 * d0 + d1 * d1);
      float f4 = this.renderYawOffset;
      float f5 = 0.0F;
      this.prevOnGroundSpeedFactor = this.onGroundSpeedFactor;
      float f = 0.0F;
      if (f3 > 0.0025000002F) {
         f = 1.0F;
         f5 = (float)Math.sqrt((double)f3) * 3.0F;
         float f1 = (float)MathHelper.atan2(d1, d0) * (180F / (float)Math.PI) - 90.0F;
         float f2 = MathHelper.abs(MathHelper.wrapDegrees(this.rotationYaw) - f1);
         if (95.0F < f2 && f2 < 265.0F) {
            f4 = f1 - 180.0F;
         } else {
            f4 = f1;
         }
      }

      if (this.swingProgress > 0.0F) {
         f4 = this.rotationYaw;
      }

      if (!this.onGround) {
         f = 0.0F;
      }

      this.onGroundSpeedFactor += (f - this.onGroundSpeedFactor) * 0.3F;
      this.world.profiler.startSection("headTurn");
      f5 = this.updateDistance(f4, f5);
      this.world.profiler.endSection();
      this.world.profiler.startSection("rangeChecks");

      while(this.rotationYaw - this.prevRotationYaw < -180.0F) {
         this.prevRotationYaw -= 360.0F;
      }

      while(this.rotationYaw - this.prevRotationYaw >= 180.0F) {
         this.prevRotationYaw += 360.0F;
      }

      while(this.renderYawOffset - this.prevRenderYawOffset < -180.0F) {
         this.prevRenderYawOffset -= 360.0F;
      }

      while(this.renderYawOffset - this.prevRenderYawOffset >= 180.0F) {
         this.prevRenderYawOffset += 360.0F;
      }

      while(this.rotationPitch - this.prevRotationPitch < -180.0F) {
         this.prevRotationPitch -= 360.0F;
      }

      while(this.rotationPitch - this.prevRotationPitch >= 180.0F) {
         this.prevRotationPitch += 360.0F;
      }

      while(this.rotationYawHead - this.prevRotationYawHead < -180.0F) {
         this.prevRotationYawHead -= 360.0F;
      }

      while(this.rotationYawHead - this.prevRotationYawHead >= 180.0F) {
         this.prevRotationYawHead += 360.0F;
      }

      this.world.profiler.endSection();
      this.movedDistance += f5;
      if (this.isElytraFlying()) {
         ++this.ticksElytraFlying;
      } else {
         this.ticksElytraFlying = 0;
      }

   }

   protected float updateDistance(float p_110146_1_, float p_110146_2_) {
      float f = MathHelper.wrapDegrees(p_110146_1_ - this.renderYawOffset);
      this.renderYawOffset += f * 0.3F;
      float f1 = MathHelper.wrapDegrees(this.rotationYaw - this.renderYawOffset);
      boolean flag = f1 < -90.0F || f1 >= 90.0F;
      if (f1 < -75.0F) {
         f1 = -75.0F;
      }

      if (f1 >= 75.0F) {
         f1 = 75.0F;
      }

      this.renderYawOffset = this.rotationYaw - f1;
      if (f1 * f1 > 2500.0F) {
         this.renderYawOffset += f1 * 0.2F;
      }

      if (flag) {
         p_110146_2_ *= -1.0F;
      }

      return p_110146_2_;
   }

   public void livingTick() {
      if (this.jumpTicks > 0) {
         --this.jumpTicks;
      }

      if (this.newPosRotationIncrements > 0 && !this.canPassengerSteer()) {
         double d0 = this.posX + (this.interpTargetX - this.posX) / (double)this.newPosRotationIncrements;
         double d1 = this.posY + (this.interpTargetY - this.posY) / (double)this.newPosRotationIncrements;
         double d2 = this.posZ + (this.interpTargetZ - this.posZ) / (double)this.newPosRotationIncrements;
         double d3 = MathHelper.wrapDegrees(this.interpTargetYaw - (double)this.rotationYaw);
         this.rotationYaw = (float)((double)this.rotationYaw + d3 / (double)this.newPosRotationIncrements);
         this.rotationPitch = (float)((double)this.rotationPitch + (this.interpTargetPitch - (double)this.rotationPitch) / (double)this.newPosRotationIncrements);
         --this.newPosRotationIncrements;
         this.setPosition(d0, d1, d2);
         this.setRotation(this.rotationYaw, this.rotationPitch);
      } else if (!this.isServerWorld()) {
         this.motionX *= 0.98D;
         this.motionY *= 0.98D;
         this.motionZ *= 0.98D;
      }

      if (this.interpTicksHead > 0) {
         this.rotationYawHead = (float)((double)this.rotationYawHead + MathHelper.wrapDegrees(this.interpTargetHeadYaw - (double)this.rotationYawHead) / (double)this.interpTicksHead);
         --this.interpTicksHead;
      }

      if (Math.abs(this.motionX) < 0.003D) {
         this.motionX = 0.0D;
      }

      if (Math.abs(this.motionY) < 0.003D) {
         this.motionY = 0.0D;
      }

      if (Math.abs(this.motionZ) < 0.003D) {
         this.motionZ = 0.0D;
      }

      this.world.profiler.startSection("ai");
      if (this.isMovementBlocked()) {
         this.isJumping = false;
         this.moveStrafing = 0.0F;
         this.moveForward = 0.0F;
         this.randomYawVelocity = 0.0F;
      } else if (this.isServerWorld()) {
         this.world.profiler.startSection("newAi");
         this.updateEntityActionState();
         this.world.profiler.endSection();
      }

      this.world.profiler.endSection();
      this.world.profiler.startSection("jump");
      if (this.isJumping) {
         if (!(this.submergedHeight > 0.0D) || this.onGround && !(this.submergedHeight > 0.4D)) {
            if (this.isInLava()) {
               this.handleFluidJump(FluidTags.LAVA);
            } else if ((this.onGround || this.submergedHeight > 0.0D && this.submergedHeight <= 0.4D) && this.jumpTicks == 0) {
               this.jump();
               this.jumpTicks = 10;
            }
         } else {
            this.handleFluidJump(FluidTags.WATER);
         }
      } else {
         this.jumpTicks = 0;
      }

      this.world.profiler.endSection();
      this.world.profiler.startSection("travel");
      this.moveStrafing *= 0.98F;
      this.moveForward *= 0.98F;
      this.randomYawVelocity *= 0.9F;
      this.updateElytra();
      AxisAlignedBB axisalignedbb = this.getBoundingBox();
      this.travel(this.moveStrafing, this.moveVertical, this.moveForward);
      this.world.profiler.endSection();
      this.world.profiler.startSection("push");
      if (this.spinAttackDuration > 0) {
         --this.spinAttackDuration;
         this.updateSpinAttack(axisalignedbb, this.getBoundingBox());
      }

      this.collideWithNearbyEntities();
      this.world.profiler.endSection();
   }

   private void updateElytra() {
      boolean flag = this.getFlag(7);
      if (flag && !this.onGround && !this.isPassenger()) {
         ItemStack itemstack = this.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
         if (itemstack.getItem() == Items.ELYTRA && ItemElytra.isUsable(itemstack)) {
            flag = true;
            if (!this.world.isRemote && (this.ticksElytraFlying + 1) % 20 == 0) {
               itemstack.damageItem(1, this);
            }
         } else {
            flag = false;
         }
      } else {
         flag = false;
      }

      if (!this.world.isRemote) {
         this.setFlag(7, flag);
      }

   }

   protected void updateEntityActionState() {
   }

   protected void collideWithNearbyEntities() {
      List<Entity> list = this.world.getEntitiesInAABBexcluding(this, this.getBoundingBox(), EntitySelectors.pushableBy(this));
      if (!list.isEmpty()) {
         int i = this.world.getGameRules().getInt("maxEntityCramming");
         if (i > 0 && list.size() > i - 1 && this.rand.nextInt(4) == 0) {
            int j = 0;

            for(int k = 0; k < list.size(); ++k) {
               if (!list.get(k).isPassenger()) {
                  ++j;
               }
            }

            if (j > i - 1) {
               this.attackEntityFrom(DamageSource.CRAMMING, 6.0F);
            }
         }

         for(int l = 0; l < list.size(); ++l) {
            Entity entity = list.get(l);
            this.collideWithEntity(entity);
         }
      }

   }

   protected void updateSpinAttack(AxisAlignedBB p_204801_1_, AxisAlignedBB p_204801_2_) {
      AxisAlignedBB axisalignedbb = p_204801_1_.union(p_204801_2_);
      List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(this, axisalignedbb);
      if (!list.isEmpty()) {
         for(int i = 0; i < list.size(); ++i) {
            Entity entity = list.get(i);
            if (entity instanceof EntityLivingBase) {
               this.spinAttack((EntityLivingBase)entity);
               this.spinAttackDuration = 0;
               this.motionX *= -0.2D;
               this.motionY *= -0.2D;
               this.motionZ *= -0.2D;
               break;
            }
         }
      } else if (this.collidedHorizontally) {
         this.spinAttackDuration = 0;
      }

      if (!this.world.isRemote && this.spinAttackDuration <= 0) {
         this.setLivingFlag(4, false);
      }

   }

   protected void collideWithEntity(Entity entityIn) {
      entityIn.applyEntityCollision(this);
   }

   protected void spinAttack(EntityLivingBase p_204804_1_) {
   }

   public void startSpinAttack(int p_204803_1_) {
      this.spinAttackDuration = p_204803_1_;
      if (!this.world.isRemote) {
         this.setLivingFlag(4, true);
      }

   }

   public boolean isSpinAttacking() {
      return (this.dataManager.get(LIVING_FLAGS) & 4) != 0;
   }

   public void stopRiding() {
      Entity entity = this.getRidingEntity();
      super.stopRiding();
      if (entity != null && entity != this.getRidingEntity() && !this.world.isRemote) {
         this.dismountEntity(entity);
      }

   }

   public void updateRidden() {
      super.updateRidden();
      this.prevOnGroundSpeedFactor = this.onGroundSpeedFactor;
      this.onGroundSpeedFactor = 0.0F;
      this.fallDistance = 0.0F;
   }

   @OnlyIn(Dist.CLIENT)
   public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport) {
      this.interpTargetX = x;
      this.interpTargetY = y;
      this.interpTargetZ = z;
      this.interpTargetYaw = (double)yaw;
      this.interpTargetPitch = (double)pitch;
      this.newPosRotationIncrements = posRotationIncrements;
   }

   @OnlyIn(Dist.CLIENT)
   public void setHeadRotation(float yaw, int pitch) {
      this.interpTargetHeadYaw = (double)yaw;
      this.interpTicksHead = pitch;
   }

   public void setJumping(boolean jumping) {
      this.isJumping = jumping;
   }

   public void onItemPickup(Entity entityIn, int quantity) {
      if (!entityIn.removed && !this.world.isRemote) {
         EntityTracker entitytracker = ((WorldServer)this.world).getEntityTracker();
         if (entityIn instanceof EntityItem || entityIn instanceof EntityArrow || entityIn instanceof EntityXPOrb) {
            entitytracker.sendToTracking(entityIn, new SPacketCollectItem(entityIn.getEntityId(), this.getEntityId(), quantity));
         }
      }

   }

   public boolean canEntityBeSeen(Entity entityIn) {
      return this.world.rayTraceBlocks(new Vec3d(this.posX, this.posY + (double)this.getEyeHeight(), this.posZ), new Vec3d(entityIn.posX, entityIn.posY + (double)entityIn.getEyeHeight(), entityIn.posZ), RayTraceFluidMode.NEVER, true, false) == null;
   }

   public float getYaw(float partialTicks) {
      return partialTicks == 1.0F ? this.rotationYawHead : this.prevRotationYawHead + (this.rotationYawHead - this.prevRotationYawHead) * partialTicks;
   }

   @OnlyIn(Dist.CLIENT)
   public float getSwingProgress(float partialTickTime) {
      float f = this.swingProgress - this.prevSwingProgress;
      if (f < 0.0F) {
         ++f;
      }

      return this.prevSwingProgress + f * partialTickTime;
   }

   public boolean isServerWorld() {
      return !this.world.isRemote;
   }

   public boolean canBeCollidedWith() {
      return !this.removed;
   }

   public boolean canBePushed() {
      return this.isAlive() && !this.isOnLadder();
   }

   protected void markVelocityChanged() {
      this.velocityChanged = this.rand.nextDouble() >= this.getAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).getValue();
   }

   public float getRotationYawHead() {
      return this.rotationYawHead;
   }

   public void setRotationYawHead(float rotation) {
      this.rotationYawHead = rotation;
   }

   public void setRenderYawOffset(float offset) {
      this.renderYawOffset = offset;
   }

   public float getAbsorptionAmount() {
      return this.absorptionAmount;
   }

   public void setAbsorptionAmount(float amount) {
      if (amount < 0.0F) {
         amount = 0.0F;
      }

      this.absorptionAmount = amount;
   }

   public void sendEnterCombat() {
   }

   public void sendEndCombat() {
   }

   protected void markPotionsDirty() {
      this.potionsNeedUpdate = true;
   }

   public abstract EnumHandSide getPrimaryHand();

   public boolean isHandActive() {
      return (this.dataManager.get(LIVING_FLAGS) & 1) > 0;
   }

   public EnumHand getActiveHand() {
      return (this.dataManager.get(LIVING_FLAGS) & 2) > 0 ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
   }

   protected void updateActiveHand() {
      if (this.isHandActive()) {
         if (this.getHeldItem(this.getActiveHand()) == this.activeItemStack) {
            if (this.getItemInUseCount() <= 25 && this.getItemInUseCount() % 4 == 0) {
               this.updateItemUse(this.activeItemStack, 5);
            }

            if (--this.activeItemStackUseCount == 0 && !this.world.isRemote) {
               this.onItemUseFinish();
            }
         } else {
            this.resetActiveHand();
         }
      }

   }

   private void updateSwimAnimation() {
      this.lastSwimAnimation = this.swimAnimation;
      if (this.isSwimming()) {
         this.swimAnimation = Math.min(1.0F, this.swimAnimation + 0.09F);
      } else {
         this.swimAnimation = Math.max(0.0F, this.swimAnimation - 0.09F);
      }

   }

   protected void setLivingFlag(int key, boolean value) {
      int i = this.dataManager.get(LIVING_FLAGS);
      if (value) {
         i = i | key;
      } else {
         i = i & ~key;
      }

      this.dataManager.set(LIVING_FLAGS, (byte)i);
   }

   public void setActiveHand(EnumHand hand) {
      ItemStack itemstack = this.getHeldItem(hand);
      if (!itemstack.isEmpty() && !this.isHandActive()) {
         this.activeItemStack = itemstack;
         this.activeItemStackUseCount = itemstack.getUseDuration();
         if (!this.world.isRemote) {
            this.setLivingFlag(1, true);
            this.setLivingFlag(2, hand == EnumHand.OFF_HAND);
         }

      }
   }

   public void notifyDataManagerChange(DataParameter<?> key) {
      super.notifyDataManagerChange(key);
      if (LIVING_FLAGS.equals(key) && this.world.isRemote) {
         if (this.isHandActive() && this.activeItemStack.isEmpty()) {
            this.activeItemStack = this.getHeldItem(this.getActiveHand());
            if (!this.activeItemStack.isEmpty()) {
               this.activeItemStackUseCount = this.activeItemStack.getUseDuration();
            }
         } else if (!this.isHandActive() && !this.activeItemStack.isEmpty()) {
            this.activeItemStack = ItemStack.EMPTY;
            this.activeItemStackUseCount = 0;
         }
      }

   }

   public void lookAt(EntityAnchorArgument.Type p_200602_1_, Vec3d p_200602_2_) {
      super.lookAt(p_200602_1_, p_200602_2_);
      this.prevRotationYawHead = this.rotationYawHead;
      this.renderYawOffset = this.rotationYawHead;
      this.prevRenderYawOffset = this.renderYawOffset;
   }

   protected void updateItemUse(ItemStack stack, int eatingParticleCount) {
      if (!stack.isEmpty() && this.isHandActive()) {
         if (stack.getUseAction() == EnumAction.DRINK) {
            this.playSound(SoundEvents.ENTITY_GENERIC_DRINK, 0.5F, this.world.rand.nextFloat() * 0.1F + 0.9F);
         }

         if (stack.getUseAction() == EnumAction.EAT) {
            this.addItemParticles(stack, eatingParticleCount);
            this.playSound(SoundEvents.ENTITY_GENERIC_EAT, 0.5F + 0.5F * (float)this.rand.nextInt(2), (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
         }

      }
   }

   private void addItemParticles(ItemStack stack, int count) {
      for(int i = 0; i < count; ++i) {
         Vec3d vec3d = new Vec3d(((double)this.rand.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);
         vec3d = vec3d.rotatePitch(-this.rotationPitch * ((float)Math.PI / 180F));
         vec3d = vec3d.rotateYaw(-this.rotationYaw * ((float)Math.PI / 180F));
         double d0 = (double)(-this.rand.nextFloat()) * 0.6D - 0.3D;
         Vec3d vec3d1 = new Vec3d(((double)this.rand.nextFloat() - 0.5D) * 0.3D, d0, 0.6D);
         vec3d1 = vec3d1.rotatePitch(-this.rotationPitch * ((float)Math.PI / 180F));
         vec3d1 = vec3d1.rotateYaw(-this.rotationYaw * ((float)Math.PI / 180F));
         vec3d1 = vec3d1.add(this.posX, this.posY + (double)this.getEyeHeight(), this.posZ);
         this.world.addParticle(new ItemParticleData(Particles.ITEM, stack), vec3d1.x, vec3d1.y, vec3d1.z, vec3d.x, vec3d.y + 0.05D, vec3d.z);
      }

   }

   protected void onItemUseFinish() {
      if (!this.activeItemStack.isEmpty() && this.isHandActive()) {
         this.updateItemUse(this.activeItemStack, 16);
         this.setHeldItem(this.getActiveHand(), this.activeItemStack.onItemUseFinish(this.world, this));
         this.resetActiveHand();
      }

   }

   public ItemStack getActiveItemStack() {
      return this.activeItemStack;
   }

   public int getItemInUseCount() {
      return this.activeItemStackUseCount;
   }

   public int getItemInUseMaxCount() {
      return this.isHandActive() ? this.activeItemStack.getUseDuration() - this.getItemInUseCount() : 0;
   }

   public void stopActiveHand() {
      if (!this.activeItemStack.isEmpty()) {
         this.activeItemStack.onPlayerStoppedUsing(this.world, this, this.getItemInUseCount());
      }

      this.resetActiveHand();
   }

   public void resetActiveHand() {
      if (!this.world.isRemote) {
         this.setLivingFlag(1, false);
      }

      this.activeItemStack = ItemStack.EMPTY;
      this.activeItemStackUseCount = 0;
   }

   public boolean isActiveItemStackBlocking() {
      if (this.isHandActive() && !this.activeItemStack.isEmpty()) {
         Item item = this.activeItemStack.getItem();
         if (item.getUseAction(this.activeItemStack) != EnumAction.BLOCK) {
            return false;
         } else {
            return item.getUseDuration(this.activeItemStack) - this.activeItemStackUseCount >= 5;
         }
      } else {
         return false;
      }
   }

   public boolean isElytraFlying() {
      return this.getFlag(7);
   }

   @OnlyIn(Dist.CLIENT)
   public int getTicksElytraFlying() {
      return this.ticksElytraFlying;
   }

   public boolean attemptTeleport(double x, double y, double z) {
      double d0 = this.posX;
      double d1 = this.posY;
      double d2 = this.posZ;
      this.posX = x;
      this.posY = y;
      this.posZ = z;
      boolean flag = false;
      BlockPos blockpos = new BlockPos(this);
      IWorld iworld = this.world;
      Random random = this.getRNG();
      if (iworld.isBlockLoaded(blockpos)) {
         boolean flag1 = false;

         while(!flag1 && blockpos.getY() > 0) {
            BlockPos blockpos1 = blockpos.down();
            IBlockState iblockstate = iworld.getBlockState(blockpos1);
            if (iblockstate.getMaterial().blocksMovement()) {
               flag1 = true;
            } else {
               --this.posY;
               blockpos = blockpos1;
            }
         }

         if (flag1) {
            this.setPositionAndUpdate(this.posX, this.posY, this.posZ);
            if (iworld.isCollisionBoxesEmpty(this, this.getBoundingBox()) && !iworld.containsAnyLiquid(this.getBoundingBox())) {
               flag = true;
            }
         }
      }

      if (!flag) {
         this.setPositionAndUpdate(d0, d1, d2);
         return false;
      } else {
         int i = 128;

         for(int j = 0; j < 128; ++j) {
            double d6 = (double)j / 127.0D;
            float f = (random.nextFloat() - 0.5F) * 0.2F;
            float f1 = (random.nextFloat() - 0.5F) * 0.2F;
            float f2 = (random.nextFloat() - 0.5F) * 0.2F;
            double d3 = d0 + (this.posX - d0) * d6 + (random.nextDouble() - 0.5D) * (double)this.width * 2.0D;
            double d4 = d1 + (this.posY - d1) * d6 + random.nextDouble() * (double)this.height;
            double d5 = d2 + (this.posZ - d2) * d6 + (random.nextDouble() - 0.5D) * (double)this.width * 2.0D;
            iworld.addParticle(Particles.PORTAL, d3, d4, d5, (double)f, (double)f1, (double)f2);
         }

         if (this instanceof EntityCreature) {
            ((EntityCreature)this).getNavigator().clearPath();
         }

         return true;
      }
   }

   public boolean canBeHitWithPotion() {
      return true;
   }

   public boolean attackable() {
      return true;
   }

   @OnlyIn(Dist.CLIENT)
   public void setPartying(BlockPos pos, boolean isPartying) {
   }
}
