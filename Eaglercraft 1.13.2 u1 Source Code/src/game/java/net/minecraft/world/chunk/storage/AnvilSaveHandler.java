package net.minecraft.world.chunk.storage;

import com.mojang.datafixers.DataFixer;
import javax.annotation.Nullable;
import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.storage.SaveHandler;
import net.minecraft.world.storage.ThreadedFileIOBase;
import net.minecraft.world.storage.WorldInfo;

public class AnvilSaveHandler extends SaveHandler {
   public AnvilSaveHandler(VFile2 p_i49568_1_, String p_i49568_2_, @Nullable MinecraftServer p_i49568_3_, DataFixer p_i49568_4_) {
      super(p_i49568_1_, p_i49568_2_, p_i49568_3_, p_i49568_4_);
   }

   public IChunkLoader getChunkLoader(Dimension provider) {
      VFile2 file1 = provider.getType().getDirectory(this.getWorldDirectory());
      return new AnvilChunkLoader(file1, this.dataFixer);
   }

   public void saveWorldInfoWithPlayer(WorldInfo worldInformation, @Nullable NBTTagCompound tagCompound) {
      worldInformation.setSaveVersion(19133);
      super.saveWorldInfoWithPlayer(worldInformation, tagCompound);
   }

   public void flush() {
      try {
         ThreadedFileIOBase.getThreadedIOInstance().waitForFinish();
      } catch (InterruptedException interruptedexception) {
         interruptedexception.printStackTrace();
      }

      RegionFileCache.clearRegionFileReferences();
   }
}
