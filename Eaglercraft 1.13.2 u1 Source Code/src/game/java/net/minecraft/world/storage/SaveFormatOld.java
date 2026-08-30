package net.minecraft.world.storage;

import com.google.common.collect.Lists;
import com.mojang.datafixers.DataFixTypes;
import com.mojang.datafixers.DataFixer;
import java.util.List;
import javax.annotation.Nullable;

import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import net.lax1dude.eaglercraft.v1_8.sp.server.WorldsDB;
import net.minecraft.client.AnvilConverterException;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.IProgressUpdate;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SaveFormatOld implements ISaveFormat {
   private static final Logger LOGGER = LogManager.getLogger();
   protected final VFile2 savesDirectory;
   protected final VFile2 field_197717_b;
   protected final DataFixer dataFixer;

   public SaveFormatOld(VFile2 p_i49565_1_, VFile2 p_i49565_2_, DataFixer p_i49565_3_) {
      this.dataFixer = p_i49565_3_;
      this.savesDirectory = p_i49565_1_;
      this.field_197717_b = p_i49565_2_;
   }

   @OnlyIn(Dist.CLIENT)
   public String getName() {
      return "Old Format";
   }

   @OnlyIn(Dist.CLIENT)
   public List<WorldSummary> getSaveList() throws AnvilConverterException {
      List<WorldSummary> list = Lists.newArrayList();

      for(int i = 0; i < 5; ++i) {
         String s = "World" + (i + 1);
         WorldInfo worldinfo = this.getWorldInfo(s);
         if (worldinfo != null) {
            list.add(new WorldSummary(worldinfo, s, "", worldinfo.getSizeOnDisk(), false));
         }
      }

      return list;
   }

   @OnlyIn(Dist.CLIENT)
   public void flushCache() {
   }

   @Nullable
   public WorldInfo getWorldInfo(String saveName) {
      VFile2 file1 = WorldsDB.newVFile(this.savesDirectory, saveName);
      if (!file1.exists()) {
         return null;
      } else {
         VFile2 file2 = WorldsDB.newVFile(file1, "level.dat");
         if (file2.exists()) {
            WorldInfo worldinfo = getWorldData(file2, this.dataFixer);
            if (worldinfo != null) {
               return worldinfo;
            }
         }

         file2 = WorldsDB.newVFile(file1, "level.dat_old");
         return file2.exists() ? getWorldData(file2, this.dataFixer) : null;
      }
   }

   @Nullable
   public static WorldInfo getWorldData(VFile2 p_186353_0_, DataFixer dataFixerIn) {
      try {
         NBTTagCompound nbttagcompound = CompressedStreamTools.readCompressed(p_186353_0_.getInputStream());
         NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("Data");
         NBTTagCompound nbttagcompound2 = nbttagcompound1.contains("Player", 10) ? nbttagcompound1.getCompound("Player") : null;
         nbttagcompound1.remove("Player");
         int i = nbttagcompound1.contains("DataVersion", 99) ? nbttagcompound1.getInt("DataVersion") : -1;
         return new WorldInfo(NBTUtil.update(dataFixerIn, DataFixTypes.LEVEL, nbttagcompound1, i), dataFixerIn, i, nbttagcompound2);
      } catch (Exception exception) {
         LOGGER.error("Exception reading {}", p_186353_0_, exception);
         return null;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void renameWorld(String dirName, String newName) {
      VFile2 file1 = WorldsDB.newVFile(this.savesDirectory, dirName);
      if (file1.exists()) {
         VFile2 file2 = WorldsDB.newVFile(file1, "level.dat");
         if (file2.exists()) {
            try {
               NBTTagCompound nbttagcompound = CompressedStreamTools.readCompressed(file2.getInputStream());
               NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("Data");
               nbttagcompound1.putString("LevelName", newName);
               CompressedStreamTools.writeCompressed(nbttagcompound, file2.getOutputStream());
            } catch (Exception exception) {
               exception.printStackTrace();
            }
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isNewLevelIdAcceptable(String saveName) {
      VFile2 file1 = WorldsDB.newVFile(this.savesDirectory, saveName);
      if (file1.exists()) {
         return false;
      } else {
         return true;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public boolean deleteWorldDirectory(String saveName) {
      VFile2 file1 = WorldsDB.newVFile(this.savesDirectory, saveName);
      if (!file1.exists()) {
         return true;
      } else {
         LOGGER.info("Deleting level {}", (Object)saveName);

         for(int i = 1; i <= 5; ++i) {
            LOGGER.info("Attempt {}...", (int)i);
            if (deleteFiles(file1.listFiles(true))) {
               return file1.delete();
            }

            LOGGER.warn("Unsuccessful in deleting contents.");
         }

         return false;
      }
   }

   @OnlyIn(Dist.CLIENT)
   protected static boolean deleteFiles(List<VFile2> files) {
      for(int i = files.size() - 1; i >= 0; --i) {
         VFile2 file1 = files.get(i);
         if (!file1.delete()) {
            LOGGER.warn("Couldn't delete file {}", (Object)file1.getPath());
            return false;
         }
      }

      return true;
   }

   public ISaveHandler getSaveLoader(String saveName, @Nullable MinecraftServer server) {
      return new SaveHandler(this.savesDirectory, saveName, server, this.dataFixer);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isConvertible(String saveName) {
      return false;
   }

   public boolean isOldMapFormat(String saveName) {
      return false;
   }

   public boolean convertMapFormat(String filename, IProgressUpdate progressCallback) {
      return false;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean canLoadWorld(String saveName) {
      return WorldsDB.newVFile(this.savesDirectory, saveName).exists();
   }

   public VFile2 getFile(String saveName, String filePath) {
      return WorldsDB.newVFile(this.savesDirectory, saveName, filePath);
   }

   @OnlyIn(Dist.CLIENT)
   public VFile2 getWorldFolder(String saveName) {
      return WorldsDB.newVFile(this.savesDirectory, saveName);
   }

   @OnlyIn(Dist.CLIENT)
   public VFile2 getBackupsFolder() {
      return this.field_197717_b;
   }
}
