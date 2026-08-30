package net.minecraft.entity.item;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.LockCode;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.storage.loot.ILootContainer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;

public abstract class EntityMinecartContainer extends EntityMinecart implements ILockableContainer, ILootContainer {
   private NonNullList<ItemStack> minecartContainerItems = NonNullList.withSize(36, ItemStack.EMPTY);
   private boolean dropContentsWhenDead = true;
   private ResourceLocation lootTable;
   private long lootTableSeed;

   protected EntityMinecartContainer(EntityType<?> p_i48536_1_, World p_i48536_2_) {
      super(p_i48536_1_, p_i48536_2_);
   }

   protected EntityMinecartContainer(EntityType<?> p_i48537_1_, double p_i48537_2_, double p_i48537_4_, double p_i48537_6_, World p_i48537_8_) {
      super(p_i48537_1_, p_i48537_8_, p_i48537_2_, p_i48537_4_, p_i48537_6_);
   }

   public void killMinecart(DamageSource source) {
      super.killMinecart(source);
      if (this.world.getGameRules().getBoolean("doEntityDrops")) {
         InventoryHelper.dropInventoryItems(this.world, this, this);
      }

   }

   public boolean isEmpty() {
      for(ItemStack itemstack : this.minecartContainerItems) {
         if (!itemstack.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   public ItemStack getStackInSlot(int index) {
      this.addLoot((EntityPlayer)null);
      return this.minecartContainerItems.get(index);
   }

   public ItemStack decrStackSize(int index, int count) {
      this.addLoot((EntityPlayer)null);
      return ItemStackHelper.getAndSplit(this.minecartContainerItems, index, count);
   }

   public ItemStack removeStackFromSlot(int index) {
      this.addLoot((EntityPlayer)null);
      ItemStack itemstack = this.minecartContainerItems.get(index);
      if (itemstack.isEmpty()) {
         return ItemStack.EMPTY;
      } else {
         this.minecartContainerItems.set(index, ItemStack.EMPTY);
         return itemstack;
      }
   }

   public void setInventorySlotContents(int index, ItemStack stack) {
      this.addLoot((EntityPlayer)null);
      this.minecartContainerItems.set(index, stack);
      if (!stack.isEmpty() && stack.getCount() > this.getInventoryStackLimit()) {
         stack.setCount(this.getInventoryStackLimit());
      }

   }

   public boolean replaceItemInInventory(int inventorySlot, ItemStack itemStackIn) {
      if (inventorySlot >= 0 && inventorySlot < this.getSizeInventory()) {
         this.setInventorySlotContents(inventorySlot, itemStackIn);
         return true;
      } else {
         return false;
      }
   }

   public void markDirty() {
   }

   public boolean isUsableByPlayer(EntityPlayer player) {
      if (this.removed) {
         return false;
      } else {
         return !(player.getDistanceSq(this) > 64.0D);
      }
   }

   public void openInventory(EntityPlayer player) {
   }

   public void closeInventory(EntityPlayer player) {
   }

   public boolean isItemValidForSlot(int index, ItemStack stack) {
      return true;
   }

   public int getInventoryStackLimit() {
      return 64;
   }

   @Nullable
   public Entity changeDimension(DimensionType p_212321_1_) {
      this.dropContentsWhenDead = false;
      return super.changeDimension(p_212321_1_);
   }

   public void remove() {
      if (this.dropContentsWhenDead) {
         InventoryHelper.dropInventoryItems(this.world, this, this);
      }

      super.remove();
   }

   public void setDropItemsWhenDead(boolean dropWhenDead) {
      this.dropContentsWhenDead = dropWhenDead;
   }

   protected void writeAdditional(NBTTagCompound compound) {
      super.writeAdditional(compound);
      if (this.lootTable != null) {
         compound.putString("LootTable", this.lootTable.toString());
         if (this.lootTableSeed != 0L) {
            compound.putLong("LootTableSeed", this.lootTableSeed);
         }
      } else {
         ItemStackHelper.saveAllItems(compound, this.minecartContainerItems);
      }

   }

   protected void readAdditional(NBTTagCompound compound) {
      super.readAdditional(compound);
      this.minecartContainerItems = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
      if (compound.contains("LootTable", 8)) {
         this.lootTable = new ResourceLocation(compound.getString("LootTable"));
         this.lootTableSeed = compound.getLong("LootTableSeed");
      } else {
         ItemStackHelper.loadAllItems(compound, this.minecartContainerItems);
      }

   }

   public boolean processInitialInteract(EntityPlayer player, EnumHand hand) {
      if (!this.world.isRemote) {
         player.displayGUIChest(this);
      }

      return true;
   }

   protected void applyDrag() {
      float f = 0.98F;
      if (this.lootTable == null) {
         int i = 15 - Container.calcRedstoneFromInventory(this);
         f += (float)i * 0.001F;
      }

      this.motionX *= (double)f;
      this.motionY *= 0.0D;
      this.motionZ *= (double)f;
   }

   public int getField(int id) {
      return 0;
   }

   public void setField(int id, int value) {
   }

   public int getFieldCount() {
      return 0;
   }

   public boolean isLocked() {
      return false;
   }

   public void setLockCode(LockCode code) {
   }

   public LockCode getLockCode() {
      return LockCode.EMPTY_CODE;
   }

   public void addLoot(@Nullable EntityPlayer player) {
      if (this.lootTable != null && this.world.getServer() != null) {
         LootTable loottable = this.world.getServer().getLootTableManager().getLootTableFromLocation(this.lootTable);
         this.lootTable = null;
         Random random;
         if (this.lootTableSeed == 0L) {
            random = new Random();
         } else {
            random = new Random(this.lootTableSeed);
         }

         LootContext.Builder lootcontext$builder = (new LootContext.Builder((WorldServer)this.world)).withPosition(new BlockPos(this));
         if (player != null) {
            lootcontext$builder.withLuck(player.getLuck());
         }

         loottable.fillInventory(this, random, lootcontext$builder.build());
      }

   }

   public void clear() {
      this.addLoot((EntityPlayer)null);
      this.minecartContainerItems.clear();
   }

   public void setLootTable(ResourceLocation lootTableIn, long lootTableSeedIn) {
      this.lootTable = lootTableIn;
      this.lootTableSeed = lootTableSeedIn;
   }

   public ResourceLocation getLootTable() {
      return this.lootTable;
   }
}
