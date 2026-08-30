package net.minecraft.entity.passive;

import com.google.common.collect.Sets;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockTurtleEgg;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIMoveToBlock;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityMoveHelper;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateSwimmer;
import net.minecraft.pathfinding.WalkAndSwimNodeProcessor;
import net.minecraft.stats.StatList;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReaderBase;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;

public class EntityTurtle extends EntityAnimal {
   private static final DataParameter<BlockPos> HOME_POS = EntityDataManager.createKey(EntityTurtle.class, DataSerializers.BLOCK_POS);
   private static final DataParameter<Boolean> HAS_EGG = EntityDataManager.createKey(EntityTurtle.class, DataSerializers.BOOLEAN);
   private static final DataParameter<Boolean> IS_DIGGING = EntityDataManager.createKey(EntityTurtle.class, DataSerializers.BOOLEAN);
   private static final DataParameter<BlockPos> TRAVEL_POS = EntityDataManager.createKey(EntityTurtle.class, DataSerializers.BLOCK_POS);
   private static final DataParameter<Boolean> GOING_HOME = EntityDataManager.createKey(EntityTurtle.class, DataSerializers.BOOLEAN);
   private static final DataParameter<Boolean> TRAVELLING = EntityDataManager.createKey(EntityTurtle.class, DataSerializers.BOOLEAN);
   private int isDigging;
   public static final Predicate<Entity> TARGET_DRY_BABY = (p_210131_0_) -> {
      if (!(p_210131_0_ instanceof EntityLivingBase)) {
         return false;
      } else {
         return ((EntityLivingBase)p_210131_0_).isChild() && !p_210131_0_.isInWater();
      }
   };

   public EntityTurtle(World worldIn) {
      super(EntityType.TURTLE, worldIn);
      this.setSize(1.2F, 0.4F);
      this.moveHelper = new EntityTurtle.MoveHelper(this);
      this.spawnableBlock = Blocks.SAND;
      this.stepHeight = 1.0F;
   }

   public void setHome(BlockPos position) {
      this.dataManager.set(HOME_POS, position);
   }

   private BlockPos getHome() {
      return this.dataManager.get(HOME_POS);
   }

   private void setTravelPos(BlockPos position) {
      this.dataManager.set(TRAVEL_POS, position);
   }

   private BlockPos getTravelPos() {
      return this.dataManager.get(TRAVEL_POS);
   }

   public boolean hasEgg() {
      return this.dataManager.get(HAS_EGG);
   }

   private void setHasEgg(boolean hasEgg) {
      this.dataManager.set(HAS_EGG, hasEgg);
   }

   public boolean isDigging() {
      return this.dataManager.get(IS_DIGGING);
   }

   private void setDigging(boolean isDigging) {
      this.isDigging = isDigging ? 1 : 0;
      this.dataManager.set(IS_DIGGING, isDigging);
   }

   private boolean isGoingHome() {
      return this.dataManager.get(GOING_HOME);
   }

   private void setGoingHome(boolean isGoingHome) {
      this.dataManager.set(GOING_HOME, isGoingHome);
   }

   private boolean isTravelling() {
      return this.dataManager.get(TRAVELLING);
   }

   private void setTravelling(boolean isTravelling) {
      this.dataManager.set(TRAVELLING, isTravelling);
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(HOME_POS, BlockPos.ORIGIN);
      this.dataManager.register(HAS_EGG, false);
      this.dataManager.register(TRAVEL_POS, BlockPos.ORIGIN);
      this.dataManager.register(GOING_HOME, false);
      this.dataManager.register(TRAVELLING, false);
      this.dataManager.register(IS_DIGGING, false);
   }

   public void writeAdditional(NBTTagCompound compound) {
      super.writeAdditional(compound);
      compound.putInt("HomePosX", this.getHome().getX());
      compound.putInt("HomePosY", this.getHome().getY());
      compound.putInt("HomePosZ", this.getHome().getZ());
      compound.putBoolean("HasEgg", this.hasEgg());
      compound.putInt("TravelPosX", this.getTravelPos().getX());
      compound.putInt("TravelPosY", this.getTravelPos().getY());
      compound.putInt("TravelPosZ", this.getTravelPos().getZ());
   }

   public void readAdditional(NBTTagCompound compound) {
      int i = compound.getInt("HomePosX");
      int j = compound.getInt("HomePosY");
      int k = compound.getInt("HomePosZ");
      this.setHome(new BlockPos(i, j, k));
      super.readAdditional(compound);
      this.setHasEgg(compound.getBoolean("HasEgg"));
      int l = compound.getInt("TravelPosX");
      int i1 = compound.getInt("TravelPosY");
      int j1 = compound.getInt("TravelPosZ");
      this.setTravelPos(new BlockPos(l, i1, j1));
   }

   @Nullable
   public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData entityLivingData, @Nullable NBTTagCompound itemNbt) {
      this.setHome(new BlockPos(this.posX, this.posY, this.posZ));
      this.setTravelPos(BlockPos.ORIGIN);
      return super.onInitialSpawn(difficulty, entityLivingData, itemNbt);
   }

   public boolean canSpawn(IWorld worldIn, boolean fromSpawner) {
      BlockPos blockpos = new BlockPos(this.posX, this.getBoundingBox().minY, this.posZ);
      return blockpos.getY() < worldIn.getSeaLevel() + 4 && super.canSpawn(worldIn, fromSpawner);
   }

   protected void initEntityAI() {
      this.tasks.addTask(0, new EntityTurtle.AIPanic(this, 1.2D));
      this.tasks.addTask(1, new EntityTurtle.AIMate(this, 1.0D));
      this.tasks.addTask(1, new EntityTurtle.AILayEgg(this, 1.0D));
      this.tasks.addTask(2, new EntityTurtle.AIPlayerTempt(this, 1.1D, Blocks.SEAGRASS.asItem()));
      this.tasks.addTask(3, new EntityTurtle.AIGoToWater(this, 1.0D));
      this.tasks.addTask(4, new EntityTurtle.AIGoHome(this, 1.0D));
      this.tasks.addTask(7, new EntityTurtle.AITravel(this, 1.0D));
      this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
      this.tasks.addTask(9, new EntityTurtle.AIWander(this, 1.0D, 100));
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
   }

   public boolean isPushedByWater() {
      return false;
   }

   public boolean canBreatheUnderwater() {
      return true;
   }

   public CreatureAttribute getCreatureAttribute() {
      return CreatureAttribute.WATER;
   }

   public int getTalkInterval() {
      return 200;
   }

   @Nullable
   protected SoundEvent getAmbientSound() {
      return !this.isInWater() && this.onGround && !this.isChild() ? SoundEvents.ENTITY_TURTLE_AMBIENT_LAND : super.getAmbientSound();
   }

   protected void playSwimSound(float volume) {
      super.playSwimSound(volume * 1.5F);
   }

   protected SoundEvent getSwimSound() {
      return SoundEvents.ENTITY_TURTLE_SWIM;
   }

   @Nullable
   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return this.isChild() ? SoundEvents.ENTITY_TURTLE_HURT_BABY : SoundEvents.ENTITY_TURTLE_HURT;
   }

   @Nullable
   protected SoundEvent getDeathSound() {
      return this.isChild() ? SoundEvents.ENTITY_TURTLE_DEATH_BABY : SoundEvents.ENTITY_TURTLE_DEATH;
   }

   protected void playStepSound(BlockPos pos, IBlockState blockIn) {
      SoundEvent soundevent = this.isChild() ? SoundEvents.ENTITY_TURTLE_SHAMBLE_BABY : SoundEvents.ENTITY_TURTLE_SHAMBLE;
      this.playSound(soundevent, 0.15F, 1.0F);
   }

   public boolean canBreed() {
      return super.canBreed() && !this.hasEgg();
   }

   protected float determineNextStepDistance() {
      return this.distanceWalkedOnStepModified + 0.15F;
   }

   public void setScaleForAge(boolean child) {
      this.setScale(child ? 0.3F : 1.0F);
   }

   protected PathNavigate createNavigator(World worldIn) {
      return new EntityTurtle.PathNavigater(this, worldIn);
   }

   @Nullable
   public EntityAgeable createChild(EntityAgeable ageable) {
      return new EntityTurtle(this.world);
   }

   public boolean isBreedingItem(ItemStack stack) {
      return stack.getItem() == Blocks.SEAGRASS.asItem();
   }

   public float getBlockPathWeight(BlockPos pos, IWorldReaderBase worldIn) {
      return !this.isGoingHome() && worldIn.getFluidState(pos).isTagged(FluidTags.WATER) ? 10.0F : super.getBlockPathWeight(pos, worldIn);
   }

   public void livingTick() {
      super.livingTick();
      if (this.isDigging() && this.isDigging >= 1 && this.isDigging % 5 == 0) {
         BlockPos blockpos = new BlockPos(this);
         if (this.world.getBlockState(blockpos.down()).getBlock() == Blocks.SAND) {
            this.world.playEvent(2001, blockpos, Block.getStateId(Blocks.SAND.getDefaultState()));
         }
      }

   }

   protected void onGrowingAdult() {
      super.onGrowingAdult();
      if (this.world.getGameRules().getBoolean("doMobLoot")) {
         this.entityDropItem(Items.SCUTE, 1);
      }

   }

   public void travel(float strafe, float vertical, float forward) {
      if (this.isServerWorld() && this.isInWater()) {
         this.moveRelative(strafe, vertical, forward, 0.1F);
         this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
         this.motionX *= (double)0.9F;
         this.motionY *= (double)0.9F;
         this.motionZ *= (double)0.9F;
         if (this.getAttackTarget() == null && (!this.isGoingHome() || !(this.getDistanceSq(this.getHome()) < 400.0D))) {
            this.motionY -= 0.005D;
         }
      } else {
         super.travel(strafe, vertical, forward);
      }

   }

   public boolean canBeLeashedTo(EntityPlayer player) {
      return false;
   }

   @Nullable
   protected ResourceLocation getLootTable() {
      return LootTableList.ENTITIES_TURTLE;
   }

   public void onStruckByLightning(EntityLightningBolt lightningBolt) {
      this.attackEntityFrom(DamageSource.LIGHTNING_BOLT, Float.MAX_VALUE);
   }

   public void onDeath(DamageSource cause) {
      super.onDeath(cause);
      if (cause == DamageSource.LIGHTNING_BOLT) {
         this.entityDropItem(new ItemStack(Items.BOWL, 1), 0.0F);
      }

   }

   static class AIGoHome extends EntityAIBase {
      private final EntityTurtle turtle;
      private final double field_203128_b;
      private boolean field_203129_c;
      private int field_203130_d;

      AIGoHome(EntityTurtle turtle, double p_i48821_2_) {
         this.turtle = turtle;
         this.field_203128_b = p_i48821_2_;
      }

      public boolean shouldExecute() {
         if (this.turtle.isChild()) {
            return false;
         } else if (this.turtle.hasEgg()) {
            return true;
         } else if (this.turtle.getRNG().nextInt(700) != 0) {
            return false;
         } else {
            return this.turtle.getDistanceSq(this.turtle.getHome()) >= 4096.0D;
         }
      }

      public void startExecuting() {
         this.turtle.setGoingHome(true);
         this.field_203129_c = false;
         this.field_203130_d = 0;
      }

      public void resetTask() {
         this.turtle.setGoingHome(false);
      }

      public boolean shouldContinueExecuting() {
         return this.turtle.getDistanceSq(this.turtle.getHome()) >= 49.0D && !this.field_203129_c && this.field_203130_d <= 600;
      }

      public void tick() {
         BlockPos blockpos = this.turtle.getHome();
         boolean flag = this.turtle.getDistanceSq(blockpos) <= 256.0D;
         if (flag) {
            ++this.field_203130_d;
         }

         if (this.turtle.getNavigator().noPath()) {
            Vec3d vec3d = RandomPositionGenerator.findRandomTargetTowardsScaled(this.turtle, 16, 3, new Vec3d((double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ()), (double)((float)Math.PI / 10F));
            if (vec3d == null) {
               vec3d = RandomPositionGenerator.findRandomTargetBlockTowards(this.turtle, 8, 7, new Vec3d((double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ()));
            }

            if (vec3d != null && !flag && this.turtle.world.getBlockState(new BlockPos(vec3d)).getBlock() != Blocks.WATER) {
               vec3d = RandomPositionGenerator.findRandomTargetBlockTowards(this.turtle, 16, 5, new Vec3d((double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ()));
            }

            if (vec3d == null) {
               this.field_203129_c = true;
               return;
            }

            this.turtle.getNavigator().tryMoveToXYZ(vec3d.x, vec3d.y, vec3d.z, this.field_203128_b);
         }

      }
   }

   static class AIGoToWater extends EntityAIMoveToBlock {
      private final EntityTurtle turtle;

      private AIGoToWater(EntityTurtle turtle, double p_i48819_2_) {
         super(turtle, turtle.isChild() ? 2.0D : p_i48819_2_, 24);
         this.turtle = turtle;
         this.field_203112_e = -1;
      }

      public boolean shouldContinueExecuting() {
         return !this.turtle.isInWater() && this.timeoutCounter <= 1200 && this.shouldMoveTo(this.turtle.world, this.destinationBlock);
      }

      public boolean shouldExecute() {
         if (this.turtle.isChild() && !this.turtle.isInWater()) {
            return super.shouldExecute();
         } else {
            return !this.turtle.isGoingHome() && !this.turtle.isInWater() && !this.turtle.hasEgg() ? super.shouldExecute() : false;
         }
      }

      public int getTargetYOffset() {
         return 1;
      }

      public boolean shouldMove() {
         return this.timeoutCounter % 160 == 0;
      }

      protected boolean shouldMoveTo(IWorldReaderBase worldIn, BlockPos pos) {
         Block block = worldIn.getBlockState(pos).getBlock();
         return block == Blocks.WATER;
      }
   }

   static class AILayEgg extends EntityAIMoveToBlock {
      private final EntityTurtle turtle;

      AILayEgg(EntityTurtle turtle, double p_i48818_2_) {
         super(turtle, p_i48818_2_, 16);
         this.turtle = turtle;
      }

      public boolean shouldExecute() {
         return this.turtle.hasEgg() && this.turtle.getDistanceSq(this.turtle.getHome()) < 81.0D ? super.shouldExecute() : false;
      }

      public boolean shouldContinueExecuting() {
         return super.shouldContinueExecuting() && this.turtle.hasEgg() && this.turtle.getDistanceSq(this.turtle.getHome()) < 81.0D;
      }

      public void tick() {
         super.tick();
         BlockPos blockpos = new BlockPos(this.turtle);
         if (!this.turtle.isInWater() && this.getIsAboveDestination()) {
            if (this.turtle.isDigging < 1) {
               this.turtle.setDigging(true);
            } else if (this.turtle.isDigging > 200) {
               World world = this.turtle.world;
               world.playSound((EntityPlayer)null, blockpos, SoundEvents.ENTITY_TURTLE_LAY_EGG, SoundCategory.BLOCKS, 0.3F, 0.9F + world.rand.nextFloat() * 0.2F);
               world.setBlockState(this.destinationBlock.up(), Blocks.TURTLE_EGG.getDefaultState().with(BlockTurtleEgg.EGGS, Integer.valueOf(this.turtle.rand.nextInt(4) + 1)), 3);
               this.turtle.setHasEgg(false);
               this.turtle.setDigging(false);
               this.turtle.setInLove(600);
            }

            if (this.turtle.isDigging()) {
               this.turtle.isDigging++;
            }
         }

      }

      protected boolean shouldMoveTo(IWorldReaderBase worldIn, BlockPos pos) {
         if (!worldIn.isAirBlock(pos.up())) {
            return false;
         } else {
            Block block = worldIn.getBlockState(pos).getBlock();
            return block == Blocks.SAND;
         }
      }
   }

   static class AIMate extends EntityAIMate {
      private final EntityTurtle turtle;

      AIMate(EntityTurtle turtle, double p_i48822_2_) {
         super(turtle, p_i48822_2_);
         this.turtle = turtle;
      }

      public boolean shouldExecute() {
         return super.shouldExecute() && !this.turtle.hasEgg();
      }

      protected void spawnBaby() {
         EntityPlayerMP entityplayermp = this.animal.getLoveCause();
         if (entityplayermp == null && this.targetMate.getLoveCause() != null) {
            entityplayermp = this.targetMate.getLoveCause();
         }

         if (entityplayermp != null) {
            entityplayermp.addStat(StatList.ANIMALS_BRED);
            CriteriaTriggers.BRED_ANIMALS.trigger(entityplayermp, this.animal, this.targetMate, (EntityAgeable)null);
         }

         this.turtle.setHasEgg(true);
         this.animal.resetInLove();
         this.targetMate.resetInLove();
         Random random = this.animal.getRNG();
         if (this.world.getGameRules().getBoolean("doMobLoot")) {
            this.world.spawnEntity(new EntityXPOrb(this.world, this.animal.posX, this.animal.posY, this.animal.posZ, random.nextInt(7) + 1));
         }

      }
   }

   static class AIPanic extends EntityAIPanic {
      AIPanic(EntityTurtle turtle, double p_i48816_2_) {
         super(turtle, p_i48816_2_);
      }

      public boolean shouldExecute() {
         if (this.creature.getRevengeTarget() == null && !this.creature.isBurning()) {
            return false;
         } else {
            BlockPos blockpos = this.getRandPos(this.creature.world, this.creature, 7, 4);
            if (blockpos != null) {
               this.randPosX = (double)blockpos.getX();
               this.randPosY = (double)blockpos.getY();
               this.randPosZ = (double)blockpos.getZ();
               return true;
            } else {
               return this.findRandomPosition();
            }
         }
      }
   }

   static class AIPlayerTempt extends EntityAIBase {
      private final EntityTurtle turtle;
      private final double speed;
      private EntityPlayer tempter;
      private int cooldown;
      private final Set<Item> temptItems;

      AIPlayerTempt(EntityTurtle turtle, double p_i48812_2_, Item p_i48812_4_) {
         this.turtle = turtle;
         this.speed = p_i48812_2_;
         this.temptItems = Sets.newHashSet(p_i48812_4_);
         this.setMutexBits(3);
      }

      public boolean shouldExecute() {
         if (this.cooldown > 0) {
            --this.cooldown;
            return false;
         } else {
            this.tempter = this.turtle.world.getClosestPlayerToEntity(this.turtle, 10.0D);
            if (this.tempter == null) {
               return false;
            } else {
               return this.isTemptedBy(this.tempter.getHeldItemMainhand()) || this.isTemptedBy(this.tempter.getHeldItemOffhand());
            }
         }
      }

      private boolean isTemptedBy(ItemStack p_203131_1_) {
         return this.temptItems.contains(p_203131_1_.getItem());
      }

      public boolean shouldContinueExecuting() {
         return this.shouldExecute();
      }

      public void resetTask() {
         this.tempter = null;
         this.turtle.getNavigator().clearPath();
         this.cooldown = 100;
      }

      public void tick() {
         this.turtle.getLookHelper().setLookPositionWithEntity(this.tempter, (float)(this.turtle.getHorizontalFaceSpeed() + 20), (float)this.turtle.getVerticalFaceSpeed());
         if (this.turtle.getDistanceSq(this.tempter) < 6.25D) {
            this.turtle.getNavigator().clearPath();
         } else {
            this.turtle.getNavigator().tryMoveToEntityLiving(this.tempter, this.speed);
         }

      }
   }

   static class AITravel extends EntityAIBase {
      private final EntityTurtle turtle;
      private final double field_203138_b;
      private boolean field_203139_c;

      AITravel(EntityTurtle turtle, double p_i48811_2_) {
         this.turtle = turtle;
         this.field_203138_b = p_i48811_2_;
      }

      public boolean shouldExecute() {
         return !this.turtle.isGoingHome() && !this.turtle.hasEgg() && this.turtle.isInWater();
      }

      public void startExecuting() {
         int i = 512;
         int j = 4;
         Random random = this.turtle.rand;
         int k = random.nextInt(1025) - 512;
         int l = random.nextInt(9) - 4;
         int i1 = random.nextInt(1025) - 512;
         if ((double)l + this.turtle.posY > (double)(this.turtle.world.getSeaLevel() - 1)) {
            l = 0;
         }

         BlockPos blockpos = new BlockPos((double)k + this.turtle.posX, (double)l + this.turtle.posY, (double)i1 + this.turtle.posZ);
         this.turtle.setTravelPos(blockpos);
         this.turtle.setTravelling(true);
         this.field_203139_c = false;
      }

      public void tick() {
         if (this.turtle.getNavigator().noPath()) {
            BlockPos blockpos = this.turtle.getTravelPos();
            Vec3d vec3d = RandomPositionGenerator.findRandomTargetTowardsScaled(this.turtle, 16, 3, new Vec3d((double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ()), (double)((float)Math.PI / 10F));
            if (vec3d == null) {
               vec3d = RandomPositionGenerator.findRandomTargetBlockTowards(this.turtle, 8, 7, new Vec3d((double)blockpos.getX(), (double)blockpos.getY(), (double)blockpos.getZ()));
            }

            if (vec3d != null) {
               int i = MathHelper.floor(vec3d.x);
               int j = MathHelper.floor(vec3d.z);
               int k = 34;
               MutableBoundingBox mutableboundingbox = new MutableBoundingBox(i - 34, 0, j - 34, i + 34, 0, j + 34);
               if (!this.turtle.world.isAreaLoaded(mutableboundingbox)) {
                  vec3d = null;
               }
            }

            if (vec3d == null) {
               this.field_203139_c = true;
               return;
            }

            this.turtle.getNavigator().tryMoveToXYZ(vec3d.x, vec3d.y, vec3d.z, this.field_203138_b);
         }

      }

      public boolean shouldContinueExecuting() {
         return !this.turtle.getNavigator().noPath() && !this.field_203139_c && !this.turtle.isGoingHome() && !this.turtle.isInLove() && !this.turtle.hasEgg();
      }

      public void resetTask() {
         this.turtle.setTravelling(false);
         super.resetTask();
      }
   }

   static class AIWander extends EntityAIWander {
      private final EntityTurtle turtle;

      private AIWander(EntityTurtle turtle, double p_i48813_2_, int p_i48813_4_) {
         super(turtle, p_i48813_2_, p_i48813_4_);
         this.turtle = turtle;
      }

      public boolean shouldExecute() {
         return !this.entity.isInWater() && !this.turtle.isGoingHome() && !this.turtle.hasEgg() ? super.shouldExecute() : false;
      }
   }

   static class MoveHelper extends EntityMoveHelper {
      private final EntityTurtle turtle;

      MoveHelper(EntityTurtle turtleIn) {
         super(turtleIn);
         this.turtle = turtleIn;
      }

      private void updateSpeed() {
         if (this.turtle.isInWater()) {
            this.turtle.motionY += 0.005D;
            if (this.turtle.getDistanceSq(this.turtle.getHome()) > 256.0D) {
               this.turtle.setAIMoveSpeed(Math.max(this.turtle.getAIMoveSpeed() / 2.0F, 0.08F));
            }

            if (this.turtle.isChild()) {
               this.turtle.setAIMoveSpeed(Math.max(this.turtle.getAIMoveSpeed() / 3.0F, 0.06F));
            }
         } else if (this.turtle.onGround) {
            this.turtle.setAIMoveSpeed(Math.max(this.turtle.getAIMoveSpeed() / 2.0F, 0.06F));
         }

      }

      public void tick() {
         this.updateSpeed();
         if (this.action == EntityMoveHelper.Action.MOVE_TO && !this.turtle.getNavigator().noPath()) {
            double d0 = this.posX - this.turtle.posX;
            double d1 = this.posY - this.turtle.posY;
            double d2 = this.posZ - this.turtle.posZ;
            double d3 = (double)MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
            d1 = d1 / d3;
            float f = (float)(MathHelper.atan2(d2, d0) * (double)(180F / (float)Math.PI)) - 90.0F;
            this.turtle.rotationYaw = this.limitAngle(this.turtle.rotationYaw, f, 90.0F);
            this.turtle.renderYawOffset = this.turtle.rotationYaw;
            float f1 = (float)(this.speed * this.turtle.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue());
            this.turtle.setAIMoveSpeed(this.turtle.getAIMoveSpeed() + (f1 - this.turtle.getAIMoveSpeed()) * 0.125F);
            this.turtle.motionY += (double)this.turtle.getAIMoveSpeed() * d1 * 0.1D;
         } else {
            this.turtle.setAIMoveSpeed(0.0F);
         }
      }
   }

   static class PathNavigater extends PathNavigateSwimmer {
      PathNavigater(EntityTurtle turtle, World p_i48815_2_) {
         super(turtle, p_i48815_2_);
      }

      protected boolean canNavigate() {
         return true;
      }

      protected PathFinder getPathFinder() {
         return new PathFinder(new WalkAndSwimNodeProcessor());
      }

      public boolean canEntityStandOnPos(BlockPos pos) {
         if (this.entity instanceof EntityTurtle) {
            EntityTurtle entityturtle = (EntityTurtle)this.entity;
            if (entityturtle.isTravelling()) {
               return this.world.getBlockState(pos).getBlock() == Blocks.WATER;
            }
         }

         return !this.world.getBlockState(pos.down()).isAir();
      }
   }
}
