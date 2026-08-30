package net.minecraft.client.multiplayer;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ChunkProviderClient implements IChunkProvider {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Chunk blankChunk;
   private final Long2ObjectMap<Chunk> loadedChunks = new Long2ObjectOpenHashMap<Chunk>(8192) {
      protected void rehash(int p_rehash_1_) {
         if (p_rehash_1_ > this.key.length) {
            super.rehash(p_rehash_1_);
         }

      }
   };
   private final World world;

   public ChunkProviderClient(World worldIn) {
      this.blankChunk = new EmptyChunk(worldIn, 0, 0);
      this.world = worldIn;
   }

   public void unloadChunk(int x, int z) {
      Chunk chunk = this.loadedChunks.remove(ChunkPos.asLong(x, z));
      if (chunk != null) {
         chunk.onUnload();
      }

   }

   @Nullable
   public Chunk getChunk(int x, int z, boolean load, boolean generate) {
      Chunk chunk = this.loadedChunks.get(ChunkPos.asLong(x, z));
      return generate && chunk == null ? this.blankChunk : chunk;
   }

   public Chunk func_212474_a(int x, int z, PacketBuffer buf, int p_212474_4_, boolean p_212474_5_) {
      long i = ChunkPos.asLong(x, z);
      Chunk chunk = this.loadedChunks.get(i);
      if (chunk == null) {
         chunk = new Chunk(this.world, x, z, new Biome[256]);
         this.loadedChunks.put(i, chunk);
      }

      chunk.read(buf, p_212474_4_, p_212474_5_);
      chunk.setLoaded(true);
      return chunk;
   }

   public boolean tick(BooleanSupplier hasTimeLeft) {
      long i = Util.milliTime();

      for(Chunk chunk : this.loadedChunks.values()) {
         chunk.tick(Util.milliTime() - i > 5L);
      }

      if (Util.milliTime() - i > 100L) {
         LOGGER.info("Warning: Clientside chunk ticking took {} ms", (long)(Util.milliTime() - i));
      }

      return false;
   }

   public String makeString() {
      return "MultiplayerChunkCache: " + this.loadedChunks.size() + ", " + this.loadedChunks.size();
   }

   public IChunkGenerator<?> getChunkGenerator() {
      return null;
   }
}
