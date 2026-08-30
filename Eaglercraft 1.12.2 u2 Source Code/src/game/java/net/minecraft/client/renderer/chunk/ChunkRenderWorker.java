package net.minecraft.client.renderer.chunk;

import javax.annotation.Nullable;

import net.lax1dude.eaglercraft.minecraft.ChunkUpdateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RegionRenderCacheBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockRenderLayer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkRenderWorker {
	private static final Logger LOGGER = LogManager.getLogger();
	private final ChunkUpdateManager chunkRenderDispatcher;
	private final RegionRenderCacheBuilder regionRenderCacheBuilder;

	public ChunkRenderWorker(ChunkUpdateManager p_i46201_1_) {
		this(p_i46201_1_, (RegionRenderCacheBuilder) null);
	}

	public ChunkRenderWorker(ChunkUpdateManager chunkRenderDispatcherIn,
			@Nullable RegionRenderCacheBuilder regionRenderCacheBuilderIn) {
		this.chunkRenderDispatcher = chunkRenderDispatcherIn;
		this.regionRenderCacheBuilder = regionRenderCacheBuilderIn;
	}

	protected void processTask(final ChunkCompileTaskGenerator generator) throws InterruptedException {
		if (generator.getStatus() != ChunkCompileTaskGenerator.Status.PENDING) {
			if (!generator.isFinished()) {
				LOGGER.warn("Chunk render task was " + generator.getStatus()
						+ " when I expected it to be pending; ignoring task");
			}

			return;
		}

		generator.setStatus(ChunkCompileTaskGenerator.Status.COMPILING);

		Entity entity = Minecraft.getMinecraft().getRenderViewEntity();
		if (entity == null) {
			generator.finish();
		} else {
			generator.setRegionRenderCacheBuilder(this.getRegionRenderCacheBuilder());
			float f = (float) entity.posX;
			float f1 = (float) entity.posY + entity.getEyeHeight();
			float f2 = (float) entity.posZ;
			ChunkCompileTaskGenerator.Type chunkcompiletaskgenerator$type = generator.getType();
			if (chunkcompiletaskgenerator$type == ChunkCompileTaskGenerator.Type.REBUILD_CHUNK) {
				generator.getRenderChunk().rebuildChunk(f, f1, f2, generator);
			} else if (chunkcompiletaskgenerator$type == ChunkCompileTaskGenerator.Type.RESORT_TRANSPARENCY) {
				generator.getRenderChunk().resortTransparency(f, f1, f2, generator);
			}

			if (generator.getStatus() != ChunkCompileTaskGenerator.Status.COMPILING) {
				if (!generator.isFinished()) {
					LOGGER.warn("Chunk render task was " + generator.getStatus()
							+ " when I expected it to be compiling; aborting task");
				}

				this.freeRenderBuilder(generator);
				return;
			}

			generator.setStatus(ChunkCompileTaskGenerator.Status.UPLOADING);

			final CompiledChunk compiledchunk = generator.getCompiledChunk();
			if (chunkcompiletaskgenerator$type == ChunkCompileTaskGenerator.Type.REBUILD_CHUNK) {
				BlockRenderLayer[] layers = BlockRenderLayer._VALUES;
				for (int i = 0; i < layers.length; ++i) {
					BlockRenderLayer enumworldblocklayer = layers[i];
					if (!compiledchunk.isLayerEmpty(enumworldblocklayer)) {
						this.chunkRenderDispatcher.uploadChunk(enumworldblocklayer,
								generator.getRegionRenderCacheBuilder().getWorldRendererByLayer(enumworldblocklayer),
								generator.getRenderChunk(), compiledchunk);
						generator.setStatus(ChunkCompileTaskGenerator.Status.DONE);
					}
				}
			} else if (chunkcompiletaskgenerator$type == ChunkCompileTaskGenerator.Type.RESORT_TRANSPARENCY) {
				this.chunkRenderDispatcher.uploadChunk(BlockRenderLayer.TRANSLUCENT,
						generator.getRegionRenderCacheBuilder().getWorldRendererByLayer(BlockRenderLayer.TRANSLUCENT),
						generator.getRenderChunk(), compiledchunk);
				generator.setStatus(ChunkCompileTaskGenerator.Status.DONE);
			}

		}
	}

	private RegionRenderCacheBuilder getRegionRenderCacheBuilder() throws InterruptedException {
		return this.regionRenderCacheBuilder;
	}

	private void freeRenderBuilder(ChunkCompileTaskGenerator taskGenerator) {

	}
}
