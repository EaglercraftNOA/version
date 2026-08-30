package net.minecraft.client.multiplayer;

import com.carrotsearch.hppc.LongObjectHashMap;
import com.carrotsearch.hppc.LongObjectMap;
import com.carrotsearch.hppc.cursors.ObjectCursor;

import java.util.Iterator;

import javax.annotation.Nullable;

import net.lax1dude.eaglercraft.EagRuntime;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.chunk.IChunkProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkProviderClient implements IChunkProvider {
	private static final Logger LOGGER = LogManager.getLogger();

	/**
	 * The completely empty chunk used by ChunkProviderClient when chunkMapping
	 * doesn't contain the requested coordinates.
	 */
	private final Chunk blankChunk;
	private final LongObjectMap<Chunk> chunkMapping = new LongObjectHashMap<Chunk>(8192);

	/** Reference to the World object. */
	private final World worldObj;

	public ChunkProviderClient(World worldIn) {
		this.blankChunk = new EmptyChunk(worldIn, 0, 0);
		this.worldObj = worldIn;
	}

	/**
	 * Unload chunk from ChunkProviderClient's hashmap. Called in response to a
	 * Packet50PreChunk with its mode field set to false
	 */
	public void unloadChunk(int x, int z) {
		Chunk chunk = this.provideChunk(x, z);

		if (!chunk.isEmpty()) {
			chunk.onChunkUnload();
		}

		this.chunkMapping.remove(ChunkPos.asLong(x, z));
	}

	@Nullable
	public Chunk getLoadedChunk(int x, int z) {
		return (Chunk) this.chunkMapping.get(ChunkPos.asLong(x, z));
	}

	/**
	 * loads or generates the chunk at the chunk location specified
	 */
	public Chunk loadChunk(int chunkX, int chunkZ) {
		Chunk chunk = new Chunk(this.worldObj, chunkX, chunkZ);
		this.chunkMapping.put(ChunkPos.asLong(chunkX, chunkZ), chunk);
		chunk.setChunkLoaded(true);
		return chunk;
	}

	public Chunk provideChunk(int x, int z) {
		Chunk chunk = this.chunkMapping.get(ChunkPos.asLong(x, z));
		return chunk == null ? this.blankChunk : chunk;
	}

	/**
	 * Unloads chunks that are marked to be unloaded. This is not guaranteed to
	 * unload every such chunk.
	 */
	public boolean unloadQueuedChunks() {
		long i = EagRuntime.steadyTimeMillis();

		Iterator<ObjectCursor<Chunk>> objectiterator = this.chunkMapping.values().iterator();

		while (objectiterator.hasNext()) {
			Chunk chunk = objectiterator.next().value;
			chunk.onTick(EagRuntime.steadyTimeMillis() - i > 5L);
		}

		if (EagRuntime.steadyTimeMillis() - i > 100L) {
			LOGGER.info("Warning: Clientside chunk ticking took {} ms", (long) (EagRuntime.steadyTimeMillis() - i));
		}

		return false;
	}

	/**
	 * Converts the instance data to a readable string.
	 */
	public String makeString() {
		return "MultiplayerChunkCache: " + this.chunkMapping.size() + ", " + this.chunkMapping.size();
	}

	public boolean func_191062_e(int p_191062_1_, int p_191062_2_) {
		return this.chunkMapping.containsKey(ChunkPos.asLong(p_191062_1_, p_191062_2_));
	}
}