package net.minecraft.util;

import net.minecraft.nbt.NBTTagCompound;

public class WeightedSpawnerEntity extends WeightedRandom.Item {
   private final NBTTagCompound nbt;

   public WeightedSpawnerEntity() {
      super(1);
      this.nbt = new NBTTagCompound();
      this.nbt.putString("id", "minecraft:pig");
   }

   public WeightedSpawnerEntity(NBTTagCompound nbtIn) {
      this(nbtIn.contains("Weight", 99) ? nbtIn.getInt("Weight") : 1, nbtIn.getCompound("Entity"));
   }

   public WeightedSpawnerEntity(int itemWeightIn, NBTTagCompound nbtIn) {
      super(itemWeightIn);
      this.nbt = nbtIn;
   }

   public NBTTagCompound toCompoundTag() {
      NBTTagCompound nbttagcompound = new NBTTagCompound();
      if (!this.nbt.contains("id", 8)) {
         this.nbt.putString("id", "minecraft:pig");
      } else if (!this.nbt.getString("id").contains(":")) {
         this.nbt.putString("id", (new ResourceLocation(this.nbt.getString("id"))).toString());
      }

      nbttagcompound.put("Entity", this.nbt);
      nbttagcompound.putInt("Weight", this.itemWeight);
      return nbttagcompound;
   }

   public NBTTagCompound getNbt() {
      return this.nbt;
   }
}
