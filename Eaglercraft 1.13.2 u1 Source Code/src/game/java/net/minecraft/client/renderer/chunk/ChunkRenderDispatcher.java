package net.minecraft.client.renderer.chunk;

import com.google.common.collect.Queues;
import java.util.Queue;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RegionRenderCacheBuilder;
import net.minecraft.client.renderer.VertexBufferUploader;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ChunkRenderDispatcher {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Queue<ChunkRenderTask> queueChunkUpdates = Queues.newPriorityQueue();
   private final VertexBufferUploader vertexUploader = new VertexBufferUploader();
   private final ChunkRenderWorker renderWorker;

   public ChunkRenderDispatcher() {
      this.renderWorker = new ChunkRenderWorker(this, new RegionRenderCacheBuilder());
   }

   public String getDebugInfo() {
      return String.format("pC: %03d", this.queueChunkUpdates.size());
   }

	public boolean runChunkUploads(long finishTimeNano) {
		boolean flag = false;
		long exportBudgetNanos = 3000000L;
		long frameT0 = Util.nanoTime();
		long deadline;
		if(finishTimeNano > 0L) {
			deadline = Math.min(frameT0 + exportBudgetNanos, finishTimeNano);
		} else {
			deadline = frameT0 + exportBudgetNanos;
		}

		while(true) {
			ChunkRenderTask chunkrendertask = this.queueChunkUpdates.poll();
			if (chunkrendertask == null) {
				break;
			}

			try {
				this.renderWorker.processTask(chunkrendertask);
				flag = true;
			} catch (InterruptedException var5) {
				LOGGER.warn("Skipped task due to interrupt");
			}

			if (Util.nanoTime() >= deadline) {
				break;
			}
		}

		return flag;
	}

	public boolean updateChunkLater(RenderChunk chunkRenderer) {
		if (this.queueChunkUpdates.size() > 256) {
			chunkRenderer.clearNeedsUpdate();
			return false;
		}
		ChunkRenderTask chunkrendertask = chunkRenderer.makeCompileTaskChunk();
		chunkrendertask.addFinishRunnable(new RemoveTaskRunnable(this, chunkrendertask));
		boolean flag = this.queueChunkUpdates.offer(chunkrendertask);
		if (!flag) {
			chunkrendertask.finish();
			chunkRenderer.clearNeedsUpdate();
		}

		return flag;
	}

   public boolean updateChunkNow(RenderChunk chunkRenderer) {
      try {
         this.renderWorker.processTask(chunkRenderer.makeCompileTaskChunk());
      } catch (InterruptedException var3) {
         ;
      }

      return true;
   }

   public void stopChunkUpdates() {
      this.clearChunkUpdates();
   }

   public boolean updateTransparencyLater(RenderChunk chunkRenderer) {
      ChunkRenderTask chunkrendertask = chunkRenderer.makeCompileTaskTransparency();
      if (chunkrendertask == null) {
         return true;
      } else {
         chunkrendertask.addFinishRunnable(new RemoveTaskRunnable(this, chunkrendertask));
         return this.queueChunkUpdates.offer(chunkrendertask);
      }
   }

   public void uploadChunk(BlockRenderLayer layerIn, BufferBuilder builderIn, RenderChunk renderChunkIn, CompiledChunk compiledChunkIn, double distanceSqIn) {
      if (OpenGlHelper.useVbo()) {
         this.uploadVertexBuffer(builderIn, renderChunkIn.getVertexBufferByLayer(layerIn.ordinal()));
      } else {
         this.uploadDisplayList(builderIn, ((ListedRenderChunk)renderChunkIn).getDisplayList(layerIn, compiledChunkIn), renderChunkIn);
      }

      builderIn.setTranslation(0.0D, 0.0D, 0.0D);
   }

   private void uploadDisplayList(BufferBuilder bufferBuilderIn, int list, RenderChunk chunkRenderer) {
      if (list < 0) {
         bufferBuilderIn.reset();
         bufferBuilderIn.discardLargeBuffer();
         return;
      }

      WorldVertexBufferUploader.uploadDisplayList(list, bufferBuilderIn);
   }

   private void uploadVertexBuffer(BufferBuilder bufferBuilderIn, VertexBuffer vertexBufferIn) {
      this.vertexUploader.setVertexBuffer(vertexBufferIn);
      this.vertexUploader.draw(bufferBuilderIn);
   }

   public void clearChunkUpdates() {
      while(!this.queueChunkUpdates.isEmpty()) {
         ChunkRenderTask chunkrendertask = this.queueChunkUpdates.poll();
         if (chunkrendertask != null) {
            chunkrendertask.finish();
         }
      }

   }

   public boolean hasNoChunkUpdates() {
      return this.queueChunkUpdates.isEmpty();
   }

   public void stopWorkerThreads() {
      this.clearChunkUpdates();
   }

   public boolean hasNoFreeRenderBuilders() {
      return false;
   }

   private static class RemoveTaskRunnable implements Runnable {
      private final ChunkRenderDispatcher dispatcher;
      private final ChunkRenderTask task;

      private RemoveTaskRunnable(ChunkRenderDispatcher dispatcherIn, ChunkRenderTask taskIn) {
         this.dispatcher = dispatcherIn;
         this.task = taskIn;
      }

      public void run() {
         this.dispatcher.queueChunkUpdates.remove(this.task);
      }
   }
}
