package net.minecraft.client.renderer.chunk;

import com.google.common.primitives.Doubles;
import net.minecraft.client.renderer.RegionRenderCacheBuilder;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ChunkRenderTask implements Comparable<ChunkRenderTask> {
   private final RenderChunk renderChunk;
   private Runnable[] listFinishRunnables;
   private int finishRunnableCount;
   private final ChunkRenderTask.Type type;
   private final double distanceSq;
   private RegionRenderCacheBuilder regionRenderCacheBuilder;
   private CompiledChunk compiledChunk;
   private ChunkRenderTask.Status status = ChunkRenderTask.Status.PENDING;
   private boolean finished;

   public ChunkRenderTask(RenderChunk renderChunkIn, ChunkRenderTask.Type typeIn, double distanceSqIn) {
      this.renderChunk = renderChunkIn;
      this.type = typeIn;
      this.distanceSq = distanceSqIn;
   }

   public ChunkRenderTask.Status getStatus() {
      return this.status;
   }

   public RenderChunk getRenderChunk() {
      return this.renderChunk;
   }

   public CompiledChunk getCompiledChunk() {
      return this.compiledChunk;
   }

   public void setCompiledChunk(CompiledChunk compiledChunkIn) {
      this.compiledChunk = compiledChunkIn;
   }

   public RegionRenderCacheBuilder getRegionRenderCacheBuilder() {
      return this.regionRenderCacheBuilder;
   }

   public void setRegionRenderCacheBuilder(RegionRenderCacheBuilder regionRenderCacheBuilderIn) {
      this.regionRenderCacheBuilder = regionRenderCacheBuilderIn;
   }

   public void clearReferences() {
      this.regionRenderCacheBuilder = null;
      this.compiledChunk = null;
      this.listFinishRunnables = null;
      this.finishRunnableCount = 0;
   }

   public void setStatus(ChunkRenderTask.Status statusIn) {
      this.status = statusIn;
   }

   public void finish() {
      if (this.type == ChunkRenderTask.Type.REBUILD_CHUNK && this.status != ChunkRenderTask.Status.DONE) {
         this.renderChunk.setNeedsUpdate(false);
      }

      this.finished = true;
      this.status = ChunkRenderTask.Status.DONE;

      for(int i = 0; i < this.finishRunnableCount; ++i) {
         this.listFinishRunnables[i].run();
      }

   }

   public void addFinishRunnable(Runnable runnable) {
      if (this.listFinishRunnables == null) {
         this.listFinishRunnables = new Runnable[2];
      } else if (this.finishRunnableCount >= this.listFinishRunnables.length) {
         Runnable[] arunnable = new Runnable[this.listFinishRunnables.length << 1];
         System.arraycopy(this.listFinishRunnables, 0, arunnable, 0, this.listFinishRunnables.length);
         this.listFinishRunnables = arunnable;
      }

      this.listFinishRunnables[this.finishRunnableCount++] = runnable;
      if (this.finished) {
         runnable.run();
      }

   }

   public ChunkRenderTask.Type getType() {
      return this.type;
   }

   public boolean isFinished() {
      return this.finished;
   }

   public int compareTo(ChunkRenderTask p_compareTo_1_) {
      return Doubles.compare(this.distanceSq, p_compareTo_1_.distanceSq);
   }

   public double getDistanceSq() {
      return this.distanceSq;
   }

   @OnlyIn(Dist.CLIENT)
   public static enum Status {
      PENDING,
      COMPILING,
      UPLOADING,
      DONE;
   }

   @OnlyIn(Dist.CLIENT)
   public static enum Type {
      REBUILD_CHUNK,
      RESORT_TRANSPARENCY;
   }
}
