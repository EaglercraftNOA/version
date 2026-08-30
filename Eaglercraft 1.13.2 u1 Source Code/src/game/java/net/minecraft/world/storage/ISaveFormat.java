package net.minecraft.world.storage;

import java.io.IOException;
import java.util.List;
import javax.annotation.Nullable;

import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import net.minecraft.client.AnvilConverterException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IProgressUpdate;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface ISaveFormat {
   @OnlyIn(Dist.CLIENT)
   default long createBackup(String worldName) throws IOException {
      return 0L;
   }

   @OnlyIn(Dist.CLIENT)
   String getName();

   ISaveHandler getSaveLoader(String saveName, @Nullable MinecraftServer server);

   @OnlyIn(Dist.CLIENT)
   List<WorldSummary> getSaveList() throws AnvilConverterException;

   @OnlyIn(Dist.CLIENT)
   void flushCache();

   @Nullable
   WorldInfo getWorldInfo(String saveName);

   @OnlyIn(Dist.CLIENT)
   boolean isNewLevelIdAcceptable(String saveName);

   @OnlyIn(Dist.CLIENT)
   boolean deleteWorldDirectory(String saveName);

   @OnlyIn(Dist.CLIENT)
   void renameWorld(String dirName, String newName);

   @OnlyIn(Dist.CLIENT)
   boolean isConvertible(String saveName);

   boolean isOldMapFormat(String saveName);

   boolean convertMapFormat(String filename, IProgressUpdate progressCallback);

   @OnlyIn(Dist.CLIENT)
   boolean canLoadWorld(String saveName);

   VFile2 getFile(String saveName, String filePath);

   @OnlyIn(Dist.CLIENT)
   VFile2 getWorldFolder(String saveName);

   @OnlyIn(Dist.CLIENT)
   VFile2 getBackupsFolder();
}
