package net.minecraft.world.gen;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMaps;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import com.arlenh7.eaglercraft.v1_17.java.util.concurrent.CompletableFuture;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.TaskManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.ChunkStatus;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.gen.tasks.ProtoChunkScheduler;
import net.minecraft.world.storage.SessionLockException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkProviderServer implements IChunkProvider {
   private static final Logger LOGGER = LogManager.getLogger();
   private final LongSet droppedChunks = new LongOpenHashSet();
   private final IChunkGenerator<?> chunkGenerator;
   private final IChunkLoader chunkLoader;
   private final Long2ObjectMap<Chunk> loadedChunks = Long2ObjectMaps.synchronize(new ChunkCacheNeighborNotification(8192));
   private Chunk lastAccessed;
   private final ProtoChunkScheduler chunkScheduler;
   private final TaskManager<ChunkPos, ChunkStatus, ChunkPrimer> taskManager;
   private final WorldServer world;
   private final IThreadListener mainThread;

   public ChunkProviderServer(WorldServer worldIn, IChunkLoader chunkLoaderIn, IChunkGenerator<?> chunkGeneratorIn, IThreadListener threadListener) {
      this.world = worldIn;
      this.chunkLoader = chunkLoaderIn;
      this.chunkGenerator = chunkGeneratorIn;
      this.mainThread = threadListener;
      this.chunkScheduler = new ProtoChunkScheduler(2, worldIn, chunkGeneratorIn, chunkLoaderIn, threadListener);
      this.taskManager = new TaskManager<>(this.chunkScheduler);
   }

   public Collection<Chunk> getLoadedChunks() {
      return this.loadedChunks.values();
   }

   public void queueUnload(Chunk chunkIn) {
      if (this.world.dimension.canDropChunk(chunkIn.x, chunkIn.z)) {
         this.droppedChunks.add(ChunkPos.asLong(chunkIn.x, chunkIn.z));
      }

   }

   public void queueUnloadAll() {
      for(Chunk chunk : this.loadedChunks.values()) {
         this.queueUnload(chunk);
      }

   }

   public void touch(int x, int z) {
      this.droppedChunks.remove(ChunkPos.asLong(x, z));
   }

   @Nullable
   public Chunk getChunk(int x, int z, boolean load, boolean generate) {
      Chunk chunk1 = this.lastAccessed;
      if (chunk1 != null) {
         ChunkPos chunkpos = chunk1.getPos();
         if (chunkpos.x == x && chunkpos.z == z) {
            return chunk1;
         }
      }

      Chunk chunk;
      synchronized(this.chunkLoader) {
         long i = ChunkPos.asLong(x, z);
         chunk = this.loadedChunks.get(i);
         if (chunk != null) {
            this.lastAccessed = chunk;
            return chunk;
         }

         if (load) {
            try {
               chunk = this.chunkLoader.loadChunk(this.world, x, z, (p_212471_3_) -> {
                  p_212471_3_.setLastSaveTime(this.world.getGameTime());
                  this.loadedChunks.put(ChunkPos.asLong(x, z), p_212471_3_);
               });
            } catch (Exception exception) {
               LOGGER.error("Couldn't load chunk", (Throwable)exception);
            }
         }
      }

      if (chunk != null) {
         this.mainThread.addScheduledTask(chunk::onLoad);
         return chunk;
      } else if (generate) {
         try {
            this.taskManager.startBatch();
            this.taskManager.addToBatch(new ChunkPos(x, z));
            CompletableFuture<ChunkPrimer> completablefuture = this.taskManager.finishBatch();
            return completablefuture.thenApply(this::convertToChunk).join();
         } catch (RuntimeException runtimeexception) {
            throw this.makeReportedException(x, z, runtimeexception);
         }
      } else {
         return null;
      }
   }

   public IChunk getChunkOrPrimer(int x, int z, boolean p_201713_3_) {
      IChunk ichunk = this.getChunk(x, z, true, false);
      return ichunk != null ? ichunk : this.chunkScheduler.func_212537_b(new ChunkPos(x, z), p_201713_3_);
   }

   public CompletableFuture<ChunkPrimer> loadChunks(Iterable<ChunkPos> positions, Consumer<Chunk> loadedChunkConsumer) {
      this.taskManager.startBatch();

      for(ChunkPos chunkpos : positions) {
         Chunk chunk = this.getChunk(chunkpos.x, chunkpos.z, true, false);
         if (chunk != null) {
            loadedChunkConsumer.accept(chunk);
         } else {
            this.taskManager.addToBatch(chunkpos).thenApply(this::convertToChunk).thenAccept(loadedChunkConsumer);
         }
      }

      return this.taskManager.finishBatch();
   }

   private ReportedException makeReportedException(int x, int z, Throwable cause) {
      CrashReport crashreport = CrashReport.makeCrashReport(cause, "Exception generating new chunk");
      CrashReportCategory crashreportcategory = crashreport.makeCategory("Chunk to be generated");
      crashreportcategory.addDetail("Location", String.format("%d,%d", x, z));
      crashreportcategory.addDetail("Position hash", ChunkPos.asLong(x, z));
      crashreportcategory.addDetail("Generator", this.chunkGenerator);
      return new ReportedException(crashreport);
   }

   private Chunk convertToChunk(IChunk chunkIn) {
      ChunkPos chunkpos = chunkIn.getPos();
      int i = chunkpos.x;
      int j = chunkpos.z;
      long k = ChunkPos.asLong(i, j);
      Chunk chunk;
      synchronized(this.loadedChunks) {
         Chunk chunk1 = this.loadedChunks.get(k);
         if (chunk1 != null) {
            return chunk1;
         }

         if (chunkIn instanceof Chunk) {
            chunk = (Chunk)chunkIn;
         } else {
            if (!(chunkIn instanceof ChunkPrimer)) {
               throw new IllegalStateException();
            }

            chunk = new Chunk(this.world, (ChunkPrimer)chunkIn, i, j);
         }

         this.loadedChunks.put(k, chunk);
         this.lastAccessed = chunk;
      }

      this.mainThread.addScheduledTask(chunk::onLoad);
      return chunk;
   }

   private void saveChunkData(IChunk chunkIn) {
      try {
         chunkIn.setLastSaveTime(this.world.getGameTime());
         this.chunkLoader.saveChunk(this.world, chunkIn);
      } catch (IOException ioexception) {
         LOGGER.error("Couldn't save chunk", (Throwable)ioexception);
      } catch (SessionLockException sessionlockexception) {
         LOGGER.error("Couldn't save chunk; already in use by another instance of Minecraft?", (Throwable)sessionlockexception);
      }

   }

   public boolean saveChunks(boolean all) {
      int i = 0;
      this.chunkScheduler.save(() -> {
         return true;
      });
      synchronized(this.chunkLoader) {
         for(Chunk chunk : this.loadedChunks.values()) {
            if (chunk.needsSaving(all)) {
               this.saveChunkData(chunk);
               chunk.setModified(false);
               ++i;
               if (i == 24 && !all) {
                  return false;
               }
            }
         }

         return true;
      }
   }

   public void close() {
      try {
         this.taskManager.shutdown();
      } catch (InterruptedException interruptedexception) {
         LOGGER.error("Couldn't stop taskManager", (Throwable)interruptedexception);
      }

   }

   public void flushToDisk() {
      synchronized(this.chunkLoader) {
         this.chunkLoader.flush();
      }
   }

   public boolean tick(BooleanSupplier hasTimeLeft) {
      if (!this.world.disableLevelSaving) {
         if (!this.droppedChunks.isEmpty()) {
            Iterator<Long> iterator = this.droppedChunks.iterator();

            for(int i = 0; iterator.hasNext() && (hasTimeLeft.getAsBoolean() || i < 200 || this.droppedChunks.size() > 2000); iterator.remove()) {
               Long olong = iterator.next();
               synchronized(this.chunkLoader) {
                  Chunk chunk = this.loadedChunks.get(olong);
                  if (chunk != null) {
                     chunk.onUnload();
                     this.saveChunkData(chunk);
                     this.loadedChunks.remove(olong);
                     this.lastAccessed = null;
                     ++i;
                  }
               }
            }
         }

         this.chunkScheduler.save(hasTimeLeft);
      }

      return false;
   }

   public boolean canSave() {
      return !this.world.disableLevelSaving;
   }

   public String makeString() {
      return "ServerChunkCache: " + this.loadedChunks.size() + " Drop: " + this.droppedChunks.size();
   }

   public List<Biome.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
      return this.chunkGenerator.getPossibleCreatures(creatureType, pos);
   }

   public int spawnMobs(World worldIn, boolean spawnHostileMobs, boolean spawnPeacefulMobs) {
      return this.chunkGenerator.spawnMobs(worldIn, spawnHostileMobs, spawnPeacefulMobs);
   }

   @Nullable
   public BlockPos findNearestStructure(World worldIn, String name, BlockPos pos, int radius, boolean p_211268_5_) {
      return this.chunkGenerator.findNearestStructure(worldIn, name, pos, radius, p_211268_5_);
   }

   public IChunkGenerator<?> getChunkGenerator() {
      return this.chunkGenerator;
   }

   public int getLoadedChunkCount() {
      return this.loadedChunks.size();
   }

   public boolean chunkExists(int x, int z) {
      return this.loadedChunks.containsKey(ChunkPos.asLong(x, z));
   }
}
