package net.minecraft.world.chunk;

import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IBlockStatePalette<T> {
   int idFor(T state);

   @Nullable
   T get(int indexKey);

   @OnlyIn(Dist.CLIENT)
   void read(PacketBuffer buf);

   void write(PacketBuffer buf);

   int getSerializedSize();

   void read(NBTTagList nbt);
}
