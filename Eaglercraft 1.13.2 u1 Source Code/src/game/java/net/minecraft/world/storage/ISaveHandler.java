package net.minecraft.world.storage;

import com.mojang.datafixers.DataFixer;
import javax.annotation.Nullable;
import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.feature.template.TemplateManager;

public interface ISaveHandler {
   @Nullable
   WorldInfo loadWorldInfo();

   void checkSessionLock() throws SessionLockException;

   IChunkLoader getChunkLoader(Dimension provider);

   void saveWorldInfoWithPlayer(WorldInfo worldInformation, NBTTagCompound tagCompound);

   void saveWorldInfo(WorldInfo worldInformation);

   IPlayerFileData getPlayerNBTManager();

   void flush();

   VFile2 getWorldDirectory();

   @Nullable
   VFile2 getDataFile(DimensionType p_212423_1_, String p_212423_2_);

   TemplateManager getStructureTemplateManager();

   DataFixer getFixer();
}
