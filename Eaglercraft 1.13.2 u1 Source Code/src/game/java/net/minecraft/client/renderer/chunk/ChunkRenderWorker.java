package net.minecraft.client.renderer.chunk;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.RegionRenderCacheBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ChunkRenderWorker {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final BlockRenderLayer[] blockRenderLayers = BlockRenderLayer.values();
   private final ChunkRenderDispatcher chunkRenderDispatcher;
   private final RegionRenderCacheBuilder regionRenderCacheBuilder;
   private final BlockPos.MutableBlockPos playerPos = new BlockPos.MutableBlockPos();
   private final BlockPos.MutableBlockPos chunkCheckPos = new BlockPos.MutableBlockPos();

   public ChunkRenderWorker(ChunkRenderDispatcher chunkRenderDispatcherIn) {
      this(chunkRenderDispatcherIn, (RegionRenderCacheBuilder)null);
   }

   public ChunkRenderWorker(ChunkRenderDispatcher chunkRenderDispatcherIn, @Nullable RegionRenderCacheBuilder regionRenderCacheBuilderIn) {
      this.chunkRenderDispatcher = chunkRenderDispatcherIn;
      this.regionRenderCacheBuilder = regionRenderCacheBuilderIn;
   }

   protected void processTask(final ChunkRenderTask generator) throws InterruptedException {
      if (generator.getStatus() != ChunkRenderTask.Status.PENDING) {
         if (!generator.isFinished()) {
            LOGGER.warn("Chunk render task was {} when I expected it to be pending; ignoring task", (Object)generator.getStatus());
         }

         generator.getRenderChunk().clearCompileTask(generator);
         generator.clearReferences();
         return;
      }

      BlockPos.MutableBlockPos blockpos = this.playerPos.setPos(Minecraft.getInstance().player);
      BlockPos blockpos1 = generator.getRenderChunk().getPosition();
      double d0 = (double)(blockpos1.getX() + 8 - blockpos.getX());
      double d1 = (double)(blockpos1.getY() + 8 - blockpos.getY());
      double d2 = (double)(blockpos1.getZ() + 8 - blockpos.getZ());
      if (d0 * d0 + d1 * d1 + d2 * d2 > 576.0D) {
         World world = generator.getRenderChunk().getWorld();
         BlockPos.MutableBlockPos blockpos$mutableblockpos = this.chunkCheckPos;
         if (!this.isChunkExisting(blockpos$mutableblockpos.setPos(blockpos1).move(EnumFacing.WEST, 16), world) || !this.isChunkExisting(blockpos$mutableblockpos.setPos(blockpos1).move(EnumFacing.NORTH, 16), world) || !this.isChunkExisting(blockpos$mutableblockpos.setPos(blockpos1).move(EnumFacing.EAST, 16), world) || !this.isChunkExisting(blockpos$mutableblockpos.setPos(blockpos1).move(EnumFacing.SOUTH, 16), world)) {
            generator.getRenderChunk().clearCompileTask(generator);
            generator.clearReferences();
            return;
         }
      }

      generator.setStatus(ChunkRenderTask.Status.COMPILING);
      Entity entity = Minecraft.getInstance().getRenderViewEntity();
      if (entity == null) {
         generator.finish();
         generator.getRenderChunk().clearCompileTask(generator);
         generator.clearReferences();
      } else {
         generator.setRegionRenderCacheBuilder(this.regionRenderCacheBuilder);
         float f = (float)(entity.prevPosX + (entity.posX - entity.prevPosX) + ActiveRenderInfo.getViewPositionX());
         float f1 = (float)(entity.prevPosY + (entity.posY - entity.prevPosY) + ActiveRenderInfo.getViewPositionY());
         float f2 = (float)(entity.prevPosZ + (entity.posZ - entity.prevPosZ) + ActiveRenderInfo.getViewPositionZ());
         ChunkRenderTask.Type chunkrendertask$type = generator.getType();
         if (chunkrendertask$type == ChunkRenderTask.Type.REBUILD_CHUNK) {
            generator.getRenderChunk().rebuildChunk(f, f1, f2, generator);
         } else if (chunkrendertask$type == ChunkRenderTask.Type.RESORT_TRANSPARENCY) {
            generator.getRenderChunk().resortTransparency(f, f1, f2, generator);
         }

         if (generator.getStatus() != ChunkRenderTask.Status.COMPILING) {
            if (!generator.isFinished()) {
               LOGGER.warn("Chunk render task was {} when I expected it to be compiling; aborting task", (Object)generator.getStatus());
            }

            generator.getRenderChunk().clearCompileTask(generator);
            generator.clearReferences();
            return;
         }

         generator.setStatus(ChunkRenderTask.Status.UPLOADING);
         CompiledChunk compiledchunk = generator.getCompiledChunk();
         if (chunkrendertask$type == ChunkRenderTask.Type.REBUILD_CHUNK) {
            for(BlockRenderLayer blockrenderlayer : blockRenderLayers) {
               if (compiledchunk.isLayerStarted(blockrenderlayer)) {
                  this.chunkRenderDispatcher.uploadChunk(blockrenderlayer, generator.getRegionRenderCacheBuilder().getBuilder(blockrenderlayer), generator.getRenderChunk(), compiledchunk, generator.getDistanceSq());
               }
            }
         } else if (chunkrendertask$type == ChunkRenderTask.Type.RESORT_TRANSPARENCY) {
            this.chunkRenderDispatcher.uploadChunk(BlockRenderLayer.TRANSLUCENT, generator.getRegionRenderCacheBuilder().getBuilder(BlockRenderLayer.TRANSLUCENT), generator.getRenderChunk(), compiledchunk, generator.getDistanceSq());
         }

         generator.setStatus(ChunkRenderTask.Status.DONE);
         generator.getRenderChunk().setCompiledChunk(compiledchunk);
         generator.getRenderChunk().clearCompileTask(generator);
         generator.clearReferences();
      }
   }

   private boolean isChunkExisting(BlockPos pos, World worldIn) {
      return !worldIn.getChunk(pos.getX() >> 4, pos.getZ() >> 4).isEmpty();
   }
}
