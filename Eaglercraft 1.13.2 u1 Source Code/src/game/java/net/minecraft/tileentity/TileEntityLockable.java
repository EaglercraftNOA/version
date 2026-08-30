package net.minecraft.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.LockCode;

public abstract class TileEntityLockable extends TileEntity implements ILockableContainer {
   private LockCode code = LockCode.EMPTY_CODE;

   protected TileEntityLockable(TileEntityType<?> typeIn) {
      super(typeIn);
   }

   public void read(NBTTagCompound compound) {
      super.read(compound);
      this.code = LockCode.read(compound);
   }

   public NBTTagCompound write(NBTTagCompound compound) {
      super.write(compound);
      if (this.code != null) {
         this.code.write(compound);
      }

      return compound;
   }

   public boolean isLocked() {
      return this.code != null && !this.code.isEmpty();
   }

   public LockCode getLockCode() {
      return this.code;
   }

   public void setLockCode(LockCode code) {
      this.code = code;
   }
}
