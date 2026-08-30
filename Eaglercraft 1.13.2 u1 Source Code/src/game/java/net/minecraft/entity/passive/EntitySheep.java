package net.minecraft.entity.passive;

import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIEatGrass;
import net.minecraft.entity.ai.EntityAIFollowParent;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAIMate;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAITempt;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntitySheep extends EntityAnimal {
   private static final DataParameter<Byte> DYE_COLOR = EntityDataManager.createKey(EntitySheep.class, DataSerializers.BYTE);
   private final InventoryCrafting inventoryCrafting = new InventoryCrafting(new Container() {
      public boolean canInteractWith(EntityPlayer playerIn) {
         return false;
      }
   }, 2, 1);
   private static final Map<EnumDyeColor, IItemProvider> WOOL_BY_COLOR = Util.make(Maps.newEnumMap(EnumDyeColor.class), (p_203402_0_) -> {
      p_203402_0_.put(EnumDyeColor.WHITE, Blocks.WHITE_WOOL);
      p_203402_0_.put(EnumDyeColor.ORANGE, Blocks.ORANGE_WOOL);
      p_203402_0_.put(EnumDyeColor.MAGENTA, Blocks.MAGENTA_WOOL);
      p_203402_0_.put(EnumDyeColor.LIGHT_BLUE, Blocks.LIGHT_BLUE_WOOL);
      p_203402_0_.put(EnumDyeColor.YELLOW, Blocks.YELLOW_WOOL);
      p_203402_0_.put(EnumDyeColor.LIME, Blocks.LIME_WOOL);
      p_203402_0_.put(EnumDyeColor.PINK, Blocks.PINK_WOOL);
      p_203402_0_.put(EnumDyeColor.GRAY, Blocks.GRAY_WOOL);
      p_203402_0_.put(EnumDyeColor.LIGHT_GRAY, Blocks.LIGHT_GRAY_WOOL);
      p_203402_0_.put(EnumDyeColor.CYAN, Blocks.CYAN_WOOL);
      p_203402_0_.put(EnumDyeColor.PURPLE, Blocks.PURPLE_WOOL);
      p_203402_0_.put(EnumDyeColor.BLUE, Blocks.BLUE_WOOL);
      p_203402_0_.put(EnumDyeColor.BROWN, Blocks.BROWN_WOOL);
      p_203402_0_.put(EnumDyeColor.GREEN, Blocks.GREEN_WOOL);
      p_203402_0_.put(EnumDyeColor.RED, Blocks.RED_WOOL);
      p_203402_0_.put(EnumDyeColor.BLACK, Blocks.BLACK_WOOL);
   });
   private static final Map<EnumDyeColor, float[]> DYE_TO_RGB = Maps.newEnumMap(Arrays.stream(EnumDyeColor.values()).collect(Collectors.toMap((EnumDyeColor p_200204_0_) -> {
      return p_200204_0_;
   }, EntitySheep::createSheepColor)));
   private int sheepTimer;
   private EntityAIEatGrass entityAIEatGrass;

   private static float[] createSheepColor(EnumDyeColor p_192020_0_) {
      if (p_192020_0_ == EnumDyeColor.WHITE) {
         return new float[]{0.9019608F, 0.9019608F, 0.9019608F};
      } else {
         float[] afloat = p_192020_0_.getColorComponentValues();
         float f = 0.75F;
         return new float[]{afloat[0] * 0.75F, afloat[1] * 0.75F, afloat[2] * 0.75F};
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static float[] getDyeRgb(EnumDyeColor dyeColor) {
      return DYE_TO_RGB.get(dyeColor);
   }

   public EntitySheep(World worldIn) {
      super(EntityType.SHEEP, worldIn);
      this.setSize(0.9F, 1.3F);
   }

   protected void initEntityAI() {
      this.entityAIEatGrass = new EntityAIEatGrass(this);
      this.tasks.addTask(0, new EntityAISwimming(this));
      this.tasks.addTask(1, new EntityAIPanic(this, 1.25D));
      this.tasks.addTask(2, new EntityAIMate(this, 1.0D));
      this.tasks.addTask(3, new EntityAITempt(this, 1.1D, Ingredient.fromItems(Items.WHEAT), false));
      this.tasks.addTask(4, new EntityAIFollowParent(this, 1.1D));
      this.tasks.addTask(5, this.entityAIEatGrass);
      this.tasks.addTask(6, new EntityAIWanderAvoidWater(this, 1.0D));
      this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
      this.tasks.addTask(8, new EntityAILookIdle(this));
   }

   protected void updateAITasks() {
      this.sheepTimer = this.entityAIEatGrass.getEatingGrassTimer();
      super.updateAITasks();
   }

   public void livingTick() {
      if (this.world.isRemote) {
         this.sheepTimer = Math.max(0, this.sheepTimer - 1);
      }

      super.livingTick();
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(8.0D);
      this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue((double)0.23F);
   }

   protected void registerData() {
      super.registerData();
      this.dataManager.register(DYE_COLOR, (byte)0);
   }

   @Nullable
   protected ResourceLocation getLootTable() {
      if (this.getSheared()) {
         return LootTableList.ENTITIES_SHEEP;
      } else {
         switch(this.getFleeceColor()) {
         case WHITE:
         default:
            return LootTableList.ENTITIES_SHEEP_WHITE;
         case ORANGE:
            return LootTableList.ENTITIES_SHEEP_ORANGE;
         case MAGENTA:
            return LootTableList.ENTITIES_SHEEP_MAGENTA;
         case LIGHT_BLUE:
            return LootTableList.ENTITIES_SHEEP_LIGHT_BLUE;
         case YELLOW:
            return LootTableList.ENTITIES_SHEEP_YELLOW;
         case LIME:
            return LootTableList.ENTITIES_SHEEP_LIME;
         case PINK:
            return LootTableList.ENTITIES_SHEEP_PINK;
         case GRAY:
            return LootTableList.ENTITIES_SHEEP_GRAY;
         case LIGHT_GRAY:
            return LootTableList.ENTITIES_SHEEP_LIGHT_GRAY;
         case CYAN:
            return LootTableList.ENTITIES_SHEEP_CYAN;
         case PURPLE:
            return LootTableList.ENTITIES_SHEEP_PURPLE;
         case BLUE:
            return LootTableList.ENTITIES_SHEEP_BLUE;
         case BROWN:
            return LootTableList.ENTITIES_SHEEP_BROWN;
         case GREEN:
            return LootTableList.ENTITIES_SHEEP_GREEN;
         case RED:
            return LootTableList.ENTITIES_SHEEP_RED;
         case BLACK:
            return LootTableList.ENTITIES_SHEEP_BLACK;
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void handleStatusUpdate(byte id) {
      if (id == 10) {
         this.sheepTimer = 40;
      } else {
         super.handleStatusUpdate(id);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public float getHeadRotationPointY(float p_70894_1_) {
      if (this.sheepTimer <= 0) {
         return 0.0F;
      } else if (this.sheepTimer >= 4 && this.sheepTimer <= 36) {
         return 1.0F;
      } else {
         return this.sheepTimer < 4 ? ((float)this.sheepTimer - p_70894_1_) / 4.0F : -((float)(this.sheepTimer - 40) - p_70894_1_) / 4.0F;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public float getHeadRotationAngleX(float p_70890_1_) {
      if (this.sheepTimer > 4 && this.sheepTimer <= 36) {
         float f = ((float)(this.sheepTimer - 4) - p_70890_1_) / 32.0F;
         return ((float)Math.PI / 5F) + 0.21991149F * MathHelper.sin(f * 28.7F);
      } else {
         return this.sheepTimer > 0 ? ((float)Math.PI / 5F) : this.rotationPitch * ((float)Math.PI / 180F);
      }
   }

   public boolean processInteract(EntityPlayer player, EnumHand hand) {
      ItemStack itemstack = player.getHeldItem(hand);
      if (itemstack.getItem() == Items.SHEARS && !this.getSheared() && !this.isChild()) {
         if (!this.world.isRemote) {
            this.setSheared(true);
            int i = 1 + this.rand.nextInt(3);

            for(int j = 0; j < i; ++j) {
               EntityItem entityitem = this.entityDropItem(WOOL_BY_COLOR.get(this.getFleeceColor()), 1);
               if (entityitem != null) {
                  entityitem.motionY += (double)(this.rand.nextFloat() * 0.05F);
                  entityitem.motionX += (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.1F);
                  entityitem.motionZ += (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.1F);
               }
            }
         }

         itemstack.damageItem(1, player);
         this.playSound(SoundEvents.ENTITY_SHEEP_SHEAR, 1.0F, 1.0F);
      }

      return super.processInteract(player, hand);
   }

   public void writeAdditional(NBTTagCompound compound) {
      super.writeAdditional(compound);
      compound.putBoolean("Sheared", this.getSheared());
      compound.putByte("Color", (byte)this.getFleeceColor().getId());
   }

   public void readAdditional(NBTTagCompound compound) {
      super.readAdditional(compound);
      this.setSheared(compound.getBoolean("Sheared"));
      this.setFleeceColor(EnumDyeColor.byId(compound.getByte("Color")));
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_SHEEP_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return SoundEvents.ENTITY_SHEEP_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_SHEEP_DEATH;
   }

   protected void playStepSound(BlockPos pos, IBlockState blockIn) {
      this.playSound(SoundEvents.ENTITY_SHEEP_STEP, 0.15F, 1.0F);
   }

   public EnumDyeColor getFleeceColor() {
      return EnumDyeColor.byId(this.dataManager.get(DYE_COLOR) & 15);
   }

   public void setFleeceColor(EnumDyeColor color) {
      byte b0 = this.dataManager.get(DYE_COLOR);
      this.dataManager.set(DYE_COLOR, (byte)(b0 & 240 | color.getId() & 15));
   }

   public boolean getSheared() {
      return (this.dataManager.get(DYE_COLOR) & 16) != 0;
   }

   public void setSheared(boolean sheared) {
      byte b0 = this.dataManager.get(DYE_COLOR);
      if (sheared) {
         this.dataManager.set(DYE_COLOR, (byte)(b0 | 16));
      } else {
         this.dataManager.set(DYE_COLOR, (byte)(b0 & -17));
      }

   }

   public static EnumDyeColor getRandomSheepColor(Random random) {
      int i = random.nextInt(100);
      if (i < 5) {
         return EnumDyeColor.BLACK;
      } else if (i < 10) {
         return EnumDyeColor.GRAY;
      } else if (i < 15) {
         return EnumDyeColor.LIGHT_GRAY;
      } else if (i < 18) {
         return EnumDyeColor.BROWN;
      } else {
         return random.nextInt(500) == 0 ? EnumDyeColor.PINK : EnumDyeColor.WHITE;
      }
   }

   public EntitySheep createChild(EntityAgeable ageable) {
      EntitySheep entitysheep = (EntitySheep)ageable;
      EntitySheep entitysheep1 = new EntitySheep(this.world);
      entitysheep1.setFleeceColor(this.getDyeColorMixFromParents(this, entitysheep));
      return entitysheep1;
   }

   public void eatGrassBonus() {
      this.setSheared(false);
      if (this.isChild()) {
         this.addGrowth(60);
      }

   }

   @Nullable
   public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData entityLivingData, @Nullable NBTTagCompound itemNbt) {
      entityLivingData = super.onInitialSpawn(difficulty, entityLivingData, itemNbt);
      this.setFleeceColor(getRandomSheepColor(this.world.rand));
      return entityLivingData;
   }

   private EnumDyeColor getDyeColorMixFromParents(EntityAnimal father, EntityAnimal mother) {
      EnumDyeColor enumdyecolor = ((EntitySheep)father).getFleeceColor();
      EnumDyeColor enumdyecolor1 = ((EntitySheep)mother).getFleeceColor();
      this.inventoryCrafting.setInventorySlotContents(0, new ItemStack(ItemDye.getItem(enumdyecolor)));
      this.inventoryCrafting.setInventorySlotContents(1, new ItemStack(ItemDye.getItem(enumdyecolor1)));
      ItemStack itemstack = father.world.getRecipeManager().getResult(this.inventoryCrafting, ((EntitySheep)father).world);
      Item item = itemstack.getItem();
      EnumDyeColor enumdyecolor2;
      if (item instanceof ItemDye) {
         enumdyecolor2 = ((ItemDye)item).getDyeColor();
      } else {
         enumdyecolor2 = this.world.rand.nextBoolean() ? enumdyecolor : enumdyecolor1;
      }

      return enumdyecolor2;
   }

   public float getEyeHeight() {
      return 0.95F * this.height;
   }
}
