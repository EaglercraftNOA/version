package dev.redstudio.alfheim.utils;

import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;

/**
 * Represents a slice of a world containing a collection of loaded chunks.
 */
public class WorldChunkSlice {

	private static final int DIAMETER = 5;
	private static final int RADIUS = DIAMETER / 2;

	private final int x;
	private final int z;
	private final Chunk[] chunks;

	public WorldChunkSlice(final IChunkProvider chunkProvider, final int x, final int z) {
		chunks = new Chunk[DIAMETER * DIAMETER];

		for (int xDiff = -RADIUS; xDiff <= RADIUS; xDiff++) {
			for (int zDiff = -RADIUS; zDiff <= RADIUS; zDiff++) {
				chunks[((xDiff + RADIUS) * DIAMETER) + (zDiff + RADIUS)] = chunkProvider.getChunk(x + xDiff,
						z + zDiff, false, false);
			}
		}

		this.x = x - RADIUS;
		this.z = z - RADIUS;
	}

	public boolean isLoaded(final int x, final int z, final int radius) {
		final int xStart = ((x - radius) >> 4) - this.x;
		final int zStart = ((z - radius) >> 4) - this.z;
		final int xEnd = ((x + radius) >> 4) - this.x;
		final int zEnd = ((z + radius) >> 4) - this.z;

		for (int currentX = xStart; currentX <= xEnd; ++currentX) {
			for (int currentZ = zStart; currentZ <= zEnd; ++currentZ) {
				if (currentX < 0 || currentX >= DIAMETER || currentZ < 0 || currentZ >= DIAMETER
						|| getChunk(currentX, currentZ) == null) {
					return false;
				}
			}
		}

		return true;
	}

	public Chunk getChunkFromWorldCoords(final int x, final int z) {
		return getChunk((x >> 4) - this.x, (z >> 4) - this.z);
	}

	private Chunk getChunk(final int x, final int z) {
		if (x < 0 || x >= DIAMETER || z < 0 || z >= DIAMETER) {
			return null;
		}
		return chunks[(x * DIAMETER) + z];
	}
}
