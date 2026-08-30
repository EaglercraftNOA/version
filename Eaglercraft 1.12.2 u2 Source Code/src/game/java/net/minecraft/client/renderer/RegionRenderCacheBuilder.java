package net.minecraft.client.renderer;

import net.lax1dude.eaglercraft.opengl.WorldRenderer;
import net.minecraft.util.BlockRenderLayer;

public class RegionRenderCacheBuilder {
	private final WorldRenderer[] worldRenderers = new WorldRenderer[BlockRenderLayer._VALUES.length];

	public RegionRenderCacheBuilder() {
		this.worldRenderers[BlockRenderLayer.SOLID.ordinal()] = new WorldRenderer(2097152);
		this.worldRenderers[BlockRenderLayer.CUTOUT.ordinal()] = new WorldRenderer(131072);
		this.worldRenderers[BlockRenderLayer.CUTOUT_MIPPED.ordinal()] = new WorldRenderer(131072);
		this.worldRenderers[BlockRenderLayer.TRANSLUCENT.ordinal()] = new WorldRenderer(262144);
	}

	public WorldRenderer getWorldRendererByLayer(BlockRenderLayer layer) {
		return this.worldRenderers[layer.ordinal()];
	}

	public WorldRenderer getWorldRendererByLayerId(int id) {
		return this.worldRenderers[id];
	}
}
