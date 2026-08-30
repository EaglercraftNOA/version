package net.minecraft.entity.item;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.stats.StatList;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityItem extends Entity {
   private static final DataParameter<ItemStack> ITEM = EntityDataManager.createKey(EntityItem.class, DataSerializers.ITEM_STACK);
   private int age;
   private int pickupDelay;
   private int health = 5;
   private UUID thrower;
   private UUID owner;
   public float hoverStart = (float)(Math.random() * Math.PI * 2.0D);

   public EntityItem(World worldIn) {
      super(EntityType.ITEM, worldIn);
      this.setSize(0.25F, 0.25F);
   }

   public EntityItem(World worldIn, double x, double y, double z) {
      this(worldIn);
      this.setPosition(x, y, z);
      this.rotationYaw = (float)(Math.random() * 360.0D);
      this.motionX = (double)((float)(Math.random() * (double)0.2F - (double)0.1F));
      this.motionY = (double)0.2F;
      this.motionZ = (double)((float)(Math.random() * (double)0.2F - (double)0.1F));
   }

   public EntityItem(World worldIn, double x, double y, double z, ItemStack stack) {
      this(worldIn, x, y, z);
      this.setItem(stack);
   }

   protected boolean canTriggerWalking() {
      return false;
   }

   protected void registerData() {
      this.getDataManager().register(ITEM, ItemStack.EMPTY);
   }

   public void tick() {
      if (this.getItem().isEmpty()) {
         this.remove();
      } else {
         super.tick();
         if (this.pickupDelay > 0 && this.pickupDelay != 32767) {
            --this.pickupDelay;
         }

         this.prevPosX = this.posX;
         this.prevPosY = this.posY;
         this.prevPosZ = this.posZ;
         double d0 = this.motionX;
         double d1 = this.motionY;
         double d2 = this.motionZ;
         if (this.areEyesInFluid(FluidTags.WATER)) {
            this.applyFloatMotion();
         } else if (!this.hasNoGravity()) {
            this.motionY -= (double)0.04F;
         }

         if (this.world.isRemote) {
            this.noClip = false;
         } else {
            this.noClip = this.pushOutOfBlocks(this.posX, (this.getBoundingBox().minY + this.getBoundingBox().maxY) / 2.0D, this.posZ);
         }

         this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
         boolean flag = (int)this.prevPosX != (int)this.posX || (int)this.prevPosY != (int)this.posY || (int)this.prevPosZ != (int)this.posZ;
         if (flag || this.ticksExisted % 25 == 0) {
            if (this.world.getFluidState(new BlockPos(this)).isTagged(FluidTags.LAVA)) {
               this.motionY = (double)0.2F;
               this.motionX = (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
               this.motionZ = (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
               this.playSound(SoundEvents.ENTITY_GENERIC_BURN, 0.4F, 2.0F + this.rand.nextFloat() * 0.4F);
            }

            if (!this.world.isRemote) {
               this.searchForOtherItemsNearby();
            }
         }

         float f = 0.98F;
         if (this.onGround) {
            f = this.world.getBlockState(new BlockPos(MathHelper.floor(this.posX), MathHelper.floor(this.getBoundingBox().minY) - 1, MathHelper.floor(this.posZ))).getBlock().getSlipperiness() * 0.98F;
         }

         this.motionX *= (double)f;
         this.motionY *= (double)0.98F;
         this.motionZ *= (double)f;
         if (this.onGround) {
            this.motionY *= -0.5D;
         }

         if (this.age != -32768) {
            ++this.age;
         }

         this.isAirBorne |= this.handleWaterMovement();
         if (!this.world.isRemote) {
            double d3 = this.motionX - d0;
            double d4 = this.motionY - d1;
            double d5 = this.motionZ - d2;
            double d6 = d3 * d3 + d4 * d4 + d5 * d5;
            if (d6 > 0.01D) {
               this.isAirBorne = true;
            }
         }

         if (!this.world.isRemote && this.age >= 6000) {
            this.remove();
         }

      }
   }

   private void applyFloatMotion() {
      if (this.motionY < (double)0.06F) {
         this.motionY += (double)5.0E-4F;
      }

      this.motionX *= (double)0.99F;
      this.motionZ *= (double)0.99F;
   }

   private void searchForOtherItemsNearby() {
      for(EntityItem entityitem : this.world.getEntitiesWithinAABB(EntityItem.class, this.getBoundingBox().grow(0.5D, 0.0D, 0.5D))) {
         this.combineItems(entityitem);
      }

   }

   private boolean combineItems(EntityItem other) {
      if (other == this) {
         return false;
      } else if (other.isAlive() && this.isAlive()) {
         ItemStack itemstack = this.getItem();
         ItemStack itemstack1 = other.getItem().copy();
         if (this.pickupDelay != 32767 && other.pickupDelay != 32767) {
            if (this.age != -32768 && other.age != -32768) {
               if (itemstack1.getItem() != itemstack.getItem()) {
                  return false;
               } else if (itemstack1.hasTag() ^ itemstack.hasTag()) {
                  return false;
               } else if (itemstack1.hasTag() && !itemstack1.getTag().equals(itemstack.getTag())) {
                  return false;
               } else if (itemstack1.getItem() == null) {
                  return false;
               } else if (itemstack1.getCount() < itemstack.getCount()) {
                  return other.combineItems(this);
               } else if (itemstack1.getCount() + itemstack.getCount() > itemstack1.getMaxStackSize()) {
                  return false;
               } else {
                  itemstack1.grow(itemstack.getCount());
                  other.pickupDelay = Math.max(other.pickupDelay, this.pickupDelay);
                  other.age = Math.min(other.age, this.age);
                  other.setItem(itemstack1);
                  this.remove();
                  return true;
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public void setAgeToCreativeDespawnTime() {
      this.age = 4800;
   }

   protected void dealFireDamage(int amount) {
      this.attackEntityFrom(DamageSource.IN_FIRE, (float)amount);
   }

   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (this.isInvulnerableTo(source)) {
         return false;
      } else if (!this.getItem().isEmpty() && this.getItem().getItem() == Items.NETHER_STAR && source.isExplosion()) {
         return false;
      } else {
         this.markVelocityChanged();
         this.health = (int)((float)this.health - amount);
         if (this.health <= 0) {
            this.remove();
         }

         return false;
      }
   }

   public void writeAdditional(NBTTagCompound compound) {
      compound.putShort("Health", (short)this.health);
      compound.putShort("Age", (short)this.age);
      compound.putShort("PickupDelay", (short)this.pickupDelay);
      if (this.getThrowerId() != null) {
         compound.put("Thrower", NBTUtil.writeUniqueId(this.getThrowerId()));
      }

      if (this.getOwnerId() != null) {
         compound.put("Owner", NBTUtil.writeUniqueId(this.getOwnerId()));
      }

      if (!this.getItem().isEmpty()) {
         compound.put("Item", this.getItem().write(new NBTTagCompound()));
      }

   }

   public void readAdditional(NBTTagCompound compound) {
      this.health = compound.getShort("Health");
      this.age = compound.getShort("Age");
      if (compound.contains("PickupDelay")) {
         this.pickupDelay = compound.getShort("PickupDelay");
      }

      if (compound.contains("Owner", 10)) {
         this.owner = NBTUtil.readUniqueId(compound.getCompound("Owner"));
      }

      if (compound.contains("Thrower", 10)) {
         this.thrower = NBTUtil.readUniqueId(compound.getCompound("Thrower"));
      }

      NBTTagCompound nbttagcompound = compound.getCompound("Item");
      this.setItem(ItemStack.read(nbttagcompound));
      if (this.getItem().isEmpty()) {
         this.remove();
      }

   }

   public void onCollideWithPlayer(EntityPlayer entityIn) {
      if (!this.world.isRemote) {
         ItemStack itemstack = this.getItem();
         Item item = itemstack.getItem();
         int i = itemstack.getCount();
         if (this.pickupDelay == 0 && (this.owner == null || 6000 - this.age <= 200 || this.owner.equals(entityIn.getUniqueID())) && entityIn.inventory.addItemStackToInventory(itemstack)) {
            entityIn.onItemPickup(this, i);
            if (itemstack.isEmpty()) {
               this.remove();
               itemstack.setCount(i);
            }

            entityIn.addStat(StatList.ITEM_PICKED_UP.get(item), i);
         }

      }
   }

   public ITextComponent getName() {
      ITextComponent itextcomponent = this.getCustomName();
      return (ITextComponent)(itextcomponent != null ? itextcomponent : new TextComponentTranslation(this.getItem().getTranslationKey()));
   }

   public boolean canBeAttackedWithItem() {
      return false;
   }

   @Nullable
   public Entity changeDimension(DimensionType p_212321_1_) {
      Entity entity = super.changeDimension(p_212321_1_);
      if (!this.world.isRemote && entity instanceof EntityItem) {
         ((EntityItem)entity).searchForOtherItemsNearby();
      }

      return entity;
   }

   public ItemStack getItem() {
      return this.getDataManager().get(ITEM);
   }

   public void setItem(ItemStack stack) {
      this.getDataManager().set(ITEM, stack);
   }

   @Nullable
   public UUID getOwnerId() {
      return this.owner;
   }

   public void setOwnerId(@Nullable UUID p_200217_1_) {
      this.owner = p_200217_1_;
   }

   @Nullable
   public UUID getThrowerId() {
      return this.thrower;
   }

   public void setThrowerId(@Nullable UUID p_200216_1_) {
      this.thrower = p_200216_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public int getAge() {
      return this.age;
   }

   public void setDefaultPickupDelay() {
      this.pickupDelay = 10;
   }

   public void setNoPickupDelay() {
      this.pickupDelay = 0;
   }

   public void setInfinitePickupDelay() {
      this.pickupDelay = 32767;
   }

   public void setPickupDelay(int ticks) {
      this.pickupDelay = ticks;
   }

   public boolean cannotPickup() {
      return this.pickupDelay > 0;
   }

   public void setNoDespawn() {
      this.age = -6000;
   }

   public void makeFakeItem() {
      this.setInfinitePickupDelay();
      this.age = 5999;
   }
}
