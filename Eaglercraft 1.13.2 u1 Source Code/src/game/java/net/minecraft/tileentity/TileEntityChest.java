package net.minecraft.tileentity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.state.properties.ChestType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TileEntityChest extends TileEntityLockableLoot implements IChestLid, ITickable {
   private NonNullList<ItemStack> chestContents = NonNullList.withSize(27, ItemStack.EMPTY);
   protected float lidAngle;
   protected float prevLidAngle;
   protected int numPlayersUsing;
   private int ticksSinceSync;

   protected TileEntityChest(TileEntityType<?> typeIn) {
      super(typeIn);
   }

   public TileEntityChest() {
      this(TileEntityType.CHEST);
   }

   public int getSizeInventory() {
      return 27;
   }

   public boolean isEmpty() {
      for(ItemStack itemstack : this.chestContents) {
         if (!itemstack.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   public ITextComponent getName() {
      ITextComponent itextcomponent = this.getCustomName();
      return (ITextComponent)(itextcomponent != null ? itextcomponent : new TextComponentTranslation("container.chest"));
   }

   public void read(NBTTagCompound compound) {
      super.read(compound);
      this.chestContents = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
      if (!this.checkLootAndRead(compound)) {
         ItemStackHelper.loadAllItems(compound, this.chestContents);
      }

      if (compound.contains("CustomName", 8)) {
         this.customName = ITextComponent.Serializer.fromJson(compound.getString("CustomName"));
      }

   }

   public NBTTagCompound write(NBTTagCompound compound) {
      super.write(compound);
      if (!this.checkLootAndWrite(compound)) {
         ItemStackHelper.saveAllItems(compound, this.chestContents);
      }

      ITextComponent itextcomponent = this.getCustomName();
      if (itextcomponent != null) {
         compound.putString("CustomName", ITextComponent.Serializer.toJson(itextcomponent));
      }

      return compound;
   }

   public int getInventoryStackLimit() {
      return 64;
   }

   public void tick() {
      int i = this.pos.getX();
      int j = this.pos.getY();
      int k = this.pos.getZ();
      ++this.ticksSinceSync;
      if (!this.world.isRemote && this.numPlayersUsing != 0 && (this.ticksSinceSync + i + j + k) % 200 == 0) {
         this.numPlayersUsing = 0;
         float f = 5.0F;

         for(EntityPlayer entityplayer : this.world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB((double)((float)i - 5.0F), (double)((float)j - 5.0F), (double)((float)k - 5.0F), (double)((float)(i + 1) + 5.0F), (double)((float)(j + 1) + 5.0F), (double)((float)(k + 1) + 5.0F)))) {
            if (entityplayer.openContainer instanceof ContainerChest) {
               IInventory iinventory = ((ContainerChest)entityplayer.openContainer).getLowerChestInventory();
               if (iinventory == this || iinventory instanceof InventoryLargeChest && ((InventoryLargeChest)iinventory).isPartOfLargeChest(this)) {
                  ++this.numPlayersUsing;
               }
            }
         }
      }

      this.prevLidAngle = this.lidAngle;
      float f1 = 0.1F;
      if (this.numPlayersUsing > 0 && this.lidAngle == 0.0F) {
         this.playSound(SoundEvents.BLOCK_CHEST_OPEN);
      }

      if (this.numPlayersUsing == 0 && this.lidAngle > 0.0F || this.numPlayersUsing > 0 && this.lidAngle < 1.0F) {
         float f2 = this.lidAngle;
         if (this.numPlayersUsing > 0) {
            this.lidAngle += 0.1F;
         } else {
            this.lidAngle -= 0.1F;
         }

         if (this.lidAngle > 1.0F) {
            this.lidAngle = 1.0F;
         }

         float f3 = 0.5F;
         if (this.lidAngle < 0.5F && f2 >= 0.5F) {
            this.playSound(SoundEvents.BLOCK_CHEST_CLOSE);
         }

         if (this.lidAngle < 0.0F) {
            this.lidAngle = 0.0F;
         }
      }

   }

   private void playSound(SoundEvent soundIn) {
      ChestType chesttype = this.getBlockState().get(BlockChest.TYPE);
      if (chesttype != ChestType.LEFT) {
         double d0 = (double)this.pos.getX() + 0.5D;
         double d1 = (double)this.pos.getY() + 0.5D;
         double d2 = (double)this.pos.getZ() + 0.5D;
         if (chesttype == ChestType.RIGHT) {
            EnumFacing enumfacing = BlockChest.getDirectionToAttached(this.getBlockState());
            d0 += (double)enumfacing.getXOffset() * 0.5D;
            d2 += (double)enumfacing.getZOffset() * 0.5D;
         }

         this.world.playSound((EntityPlayer)null, d0, d1, d2, soundIn, SoundCategory.BLOCKS, 0.5F, this.world.rand.nextFloat() * 0.1F + 0.9F);
      }
   }

   public boolean receiveClientEvent(int id, int type) {
      if (id == 1) {
         this.numPlayersUsing = type;
         return true;
      } else {
         return super.receiveClientEvent(id, type);
      }
   }

   public void openInventory(EntityPlayer player) {
      if (!player.isSpectator()) {
         if (this.numPlayersUsing < 0) {
            this.numPlayersUsing = 0;
         }

         ++this.numPlayersUsing;
         this.onOpenOrClose();
      }

   }

   public void closeInventory(EntityPlayer player) {
      if (!player.isSpectator()) {
         --this.numPlayersUsing;
         this.onOpenOrClose();
      }

   }

   protected void onOpenOrClose() {
      Block block = this.getBlockState().getBlock();
      if (block instanceof BlockChest) {
         this.world.addBlockEvent(this.pos, block, 1, this.numPlayersUsing);
         this.world.notifyNeighborsOfStateChange(this.pos, block);
      }

   }

   public String getGuiID() {
      return "minecraft:chest";
   }

   public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
      this.fillWithLoot(playerIn);
      return new ContainerChest(playerInventory, this, playerIn);
   }

   protected NonNullList<ItemStack> getItems() {
      return this.chestContents;
   }

   protected void setItems(NonNullList<ItemStack> itemsIn) {
      this.chestContents = itemsIn;
   }

   @OnlyIn(Dist.CLIENT)
   public float getLidAngle(float partialTicks) {
      return this.prevLidAngle + (this.lidAngle - this.prevLidAngle) * partialTicks;
   }

   public static int getPlayersUsing(IBlockReader reader, BlockPos posIn) {
      IBlockState iblockstate = reader.getBlockState(posIn);
      if (iblockstate.getBlock().hasTileEntity()) {
         TileEntity tileentity = reader.getTileEntity(posIn);
         if (tileentity instanceof TileEntityChest) {
            return ((TileEntityChest)tileentity).numPlayersUsing;
         }
      }

      return 0;
   }

   public static void swapContents(TileEntityChest chest, TileEntityChest otherChest) {
      NonNullList<ItemStack> nonnulllist = chest.getItems();
      chest.setItems(otherChest.getItems());
      otherChest.setItems(nonnulllist);
   }
}
