package net.minecraft.util;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.ImmutableMap.Builder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import net.lax1dude.eaglercraft.v1_8.sp.server.WorldsDB;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.storage.RegionFile;
import net.minecraft.world.dimension.DimensionType;

public class WorldChunkEnumerator {
   private static final Pattern REGEX = Pattern.compile("^r\\.(-?[0-9]+)\\.(-?[0-9]+)\\.mca$");
   private final VFile2 pathToWorld;
   private final Map<DimensionType, List<ChunkPos>> endChunks;

   public WorldChunkEnumerator(VFile2 p_i49790_1_) {
      this.pathToWorld = p_i49790_1_;
      Builder<DimensionType, List<ChunkPos>> builder = ImmutableMap.builder();

      for(DimensionType dimensiontype : DimensionType.getAll()) {
         builder.put(dimensiontype, this.getAllChunkPos(dimensiontype));
      }

      this.endChunks = builder.build();
   }

   private List<ChunkPos> getAllChunkPos(DimensionType p_212153_1_) {
      ArrayList<ChunkPos> arraylist = Lists.newArrayList();
      VFile2 file1 = p_212153_1_.getDirectory(this.pathToWorld);
      List<VFile2> list = this.addRegionFiles(file1);

      for(VFile2 file2 : list) {
         arraylist.addAll(this.iterateRegionFile(file2));
      }

      list.sort(Comparator.comparing(VFile2::getPath));
      return arraylist;
   }

   private List<ChunkPos> iterateRegionFile(VFile2 p_212150_1_) {
      List<ChunkPos> list = Lists.newArrayList();
      RegionFile regionfile = null;

      List<ChunkPos> arraylist;
      try {
         Matcher matcher = REGEX.matcher(p_212150_1_.getName());
         if (matcher.matches()) {
            int l = Integer.parseInt(matcher.group(1)) << 5;
            int i = Integer.parseInt(matcher.group(2)) << 5;
            regionfile = new RegionFile(p_212150_1_);

            for(int j = 0; j < 32; ++j) {
               for(int k = 0; k < 32; ++k) {
                  if (regionfile.doesChunkExist(j, k)) {
                     list.add(new ChunkPos(j + l, k + i));
                  }
               }
            }

            return list;
         }

         arraylist = list;
      } catch (Throwable var18) {
         arraylist = Lists.newArrayList();
         return arraylist;
      } finally {
         if (regionfile != null) {
            try {
               regionfile.close();
            } catch (Exception var17) {
               ;
            }
         }

      }

      return arraylist;
   }

   private List<VFile2> addRegionFiles(VFile2 p_212155_1_) {
      VFile2 file1 = WorldsDB.newVFile(p_212155_1_, "region");
      List<VFile2> afile = file1.listFiles(false);
      List<VFile2> ret = Lists.newArrayList();
      for(int i = 0, l = afile.size(); i < l; ++i) {
         VFile2 file = afile.get(i);
         if(file.getName().endsWith(".mca")) {
            ret.add(file);
         }
      }
      return ret;
   }

   public List<ChunkPos> func_212541_a(DimensionType p_212541_1_) {
      return this.endChunks.get(p_212541_1_);
   }
}
