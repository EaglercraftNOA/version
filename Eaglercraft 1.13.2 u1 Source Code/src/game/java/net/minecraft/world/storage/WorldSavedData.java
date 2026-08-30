package net.minecraft.world.storage;

import net.minecraft.nbt.NBTTagCompound;

public abstract class WorldSavedData {
   private final String name;
   private boolean dirty;

   public WorldSavedData(String name) {
      this.name = name;
   }

   public abstract void read(NBTTagCompound nbt);

   public abstract NBTTagCompound write(NBTTagCompound compound);

   public void markDirty() {
      this.setDirty(true);
   }

   public void setDirty(boolean isDirty) {
      this.dirty = isDirty;
   }

   public boolean isDirty() {
      return this.dirty;
   }

   public String getName() {
      return this.name;
   }
}
