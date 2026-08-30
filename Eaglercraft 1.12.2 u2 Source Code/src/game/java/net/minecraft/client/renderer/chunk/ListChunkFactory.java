package net.minecraft.client.renderer.chunk;

import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ListChunkFactory implements IRenderChunkFactory {
	public RenderChunk create(World worldIn, RenderGlobal p_189565_2_, BlockPos pos, int p_189565_3_) {
		return new ListedRenderChunk(worldIn, p_189565_2_, pos, p_189565_3_);
	}
}
