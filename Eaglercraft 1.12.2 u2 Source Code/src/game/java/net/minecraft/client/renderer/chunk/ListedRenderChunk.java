package net.minecraft.client.renderer.chunk;

import net.lax1dude.eaglercraft.opengl.EaglercraftGPU;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ListedRenderChunk extends RenderChunk {
	private final int[] baseDisplayList;

	public ListedRenderChunk(World p_i47121_1_, RenderGlobal p_i47121_2_, BlockPos pos, int p_i47121_3_) {
		super(p_i47121_1_, p_i47121_2_, pos, p_i47121_3_);
		this.baseDisplayList = new int[BlockRenderLayer._VALUES.length];
		for (int i = 0; i < this.baseDisplayList.length; ++i) {
			this.baseDisplayList[i] = GLAllocation.generateDisplayLists();
		}
	}

	public int getDisplayList(BlockRenderLayer layer, CompiledChunk p_178600_2_) {
		return !p_178600_2_.isLayerEmpty(layer) ? this.baseDisplayList[layer.ordinal()] : -1;
	}

	public void deleteGlResources() {
		super.deleteGlResources();
		for (int i = 0; i < this.baseDisplayList.length; ++i) {
			GLAllocation.deleteDisplayLists(this.baseDisplayList[i]);
		}
	}
	
	public void rebuildChunk(float x, float y, float z, ChunkCompileTaskGenerator generator) {
		super.rebuildChunk(x, y, z, generator);
		BlockRenderLayer[] layers = BlockRenderLayer._VALUES;
		for (int i = 0; i < layers.length; ++i) {
			if (generator.getCompiledChunk().isLayerEmpty(layers[i])) {
				EaglercraftGPU.flushDisplayList(this.baseDisplayList[i]);
			}
		}
	}
}
