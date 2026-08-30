package dev.redstudio.alfheim.lighting;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumLightType;
import net.minecraft.world.World;

/**
 * 1.17 uses the vanilla {@link LevelLightEngine}; keep Alfheim callers attached to
 * the real lighting implementation instead of the old 1.8 chunk-section engine.
 */
public final class LightingEngine {

	private final World world;

	public LightingEngine(final World world) {
		this.world = world;
	}

	public void scheduleLightUpdate(final EnumLightType lightType, final BlockPos pos) {
		world.checkLightFor(lightType, pos);
	}

	public void processLightUpdates() {
		world.getChunkProvider().tick(() -> true);
	}

	public void processLightUpdatesForType(final EnumLightType lightType) {
		processLightUpdates();
	}
}
