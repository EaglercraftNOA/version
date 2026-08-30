package net.minecraft.world.chunk.storage;

import com.google.common.collect.Maps;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;
import javax.annotation.Nullable;
import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import net.lax1dude.eaglercraft.v1_8.sp.server.WorldsDB;

public class RegionFileCache {
   private static final Map<VFile2, RegionFile> REGIONS_BY_FILE = Maps.newHashMap();

   public static synchronized RegionFile createOrLoadRegionFile(VFile2 worldDir, int chunkX, int chunkZ) {
      VFile2 file1 = WorldsDB.newVFile(worldDir, "region");
      VFile2 file2 = WorldsDB.newVFile(file1, "r." + (chunkX >> 5) + "." + (chunkZ >> 5) + ".mca");
      RegionFile regionfile = REGIONS_BY_FILE.get(file2);
      if (regionfile != null) {
         return regionfile;
      } else {
         if (REGIONS_BY_FILE.size() >= 256) {
            clearRegionFileReferences();
         }

         RegionFile regionfile1 = new RegionFile(file2);
         REGIONS_BY_FILE.put(file2, regionfile1);
         return regionfile1;
      }
   }

   public static synchronized void clearRegionFileReferences() {
      for(RegionFile regionfile : REGIONS_BY_FILE.values()) {
         try {
            if (regionfile != null) {
               regionfile.close();
            }
         } catch (IOException ioexception) {
            ioexception.printStackTrace();
         }
      }

      REGIONS_BY_FILE.clear();
   }

   @Nullable
   public static DataInputStream getChunkInputStream(VFile2 worldDir, int chunkX, int chunkZ) {
      RegionFile regionfile = createOrLoadRegionFile(worldDir, chunkX, chunkZ);
      return regionfile.getChunkDataInputStream(chunkX & 31, chunkZ & 31);
   }

   @Nullable
   public static DataOutputStream getChunkOutputStream(VFile2 worldDir, int chunkX, int chunkZ) throws IOException {
      RegionFile regionfile = createOrLoadRegionFile(worldDir, chunkX, chunkZ);
      return regionfile.getChunkDataOutputStream(chunkX & 31, chunkZ & 31);
   }
}
