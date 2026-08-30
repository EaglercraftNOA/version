package net.minecraft.world.biome;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.concurrent.TimeUnit;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.biome.provider.BiomeProvider;

public class BiomeCache {
   private final BiomeProvider provider;
   private final LoadingCache<ChunkPos, BiomeCache.Entry> cacheMap = CacheBuilder.newBuilder().expireAfterAccess(30000L, TimeUnit.MILLISECONDS).build(new CacheLoader<ChunkPos, BiomeCache.Entry>() {
      public BiomeCache.Entry load(ChunkPos p_load_1_) throws Exception {
         return BiomeCache.this.new Entry(p_load_1_.x, p_load_1_.z);
      }
   });

   public BiomeCache(BiomeProvider provider) {
      this.provider = provider;
   }

   public BiomeCache.Entry getEntry(int x, int z) {
      x = x >> 4;
      z = z >> 4;
      return this.cacheMap.getUnchecked(new ChunkPos(x, z));
   }

   public Biome getBiome(int x, int z, Biome defaultValue) {
      Biome biome = this.getEntry(x, z).getBiome(x, z);
      return biome == null ? defaultValue : biome;
   }

   public void cleanupCache() {
   }

   public Biome[] getCachedBiomes(int x, int z) {
      return this.getEntry(x, z).biomes;
   }

   public class Entry {
      private final Biome[] biomes;

      public Entry(int x, int z) {
         this.biomes = BiomeCache.this.provider.getBiomes(x << 4, z << 4, 16, 16, false);
      }

      public Biome getBiome(int x, int z) {
         return this.biomes[x & 15 | (z & 15) << 4];
      }
   }
}
