package net.minecraft.world.storage;

import com.mojang.datafixers.DataFixer;
import javax.annotation.Nullable;
import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SaveHandlerMP implements ISaveHandler {
   public WorldInfo loadWorldInfo() {
      return null;
   }

   public void checkSessionLock() throws SessionLockException {
   }

   public IChunkLoader getChunkLoader(Dimension provider) {
      return null;
   }

   public void saveWorldInfoWithPlayer(WorldInfo worldInformation, NBTTagCompound tagCompound) {
   }

   public void saveWorldInfo(WorldInfo worldInformation) {
   }

   public IPlayerFileData getPlayerNBTManager() {
      return null;
   }

   public void flush() {
   }

   @Nullable
   public VFile2 getDataFile(DimensionType p_212423_1_, String p_212423_2_) {
      return null;
   }

   public VFile2 getWorldDirectory() {
      return null;
   }

   public TemplateManager getStructureTemplateManager() {
      return null;
   }

   public DataFixer getFixer() {
      return null;
   }
}
