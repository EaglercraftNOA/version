package net.minecraft.world;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.storage.WorldSavedData;

public class ForcedChunksSaveData extends WorldSavedData {
   private LongSet chunks = new LongOpenHashSet();

   public ForcedChunksSaveData(String p_i49814_1_) {
      super(p_i49814_1_);
   }

   public void read(NBTTagCompound nbt) {
      this.chunks = new LongOpenHashSet(nbt.getLongArray("Forced"));
   }

   public NBTTagCompound write(NBTTagCompound compound) {
      compound.putLongArray("Forced", this.chunks.toLongArray());
      return compound;
   }

   public LongSet getChunks() {
      return this.chunks;
   }
}
