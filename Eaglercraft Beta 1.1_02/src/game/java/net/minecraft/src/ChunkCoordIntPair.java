package net.minecraft.src;

public class ChunkCoordIntPair {
	public int chunkXPos;
	public int chunkZPos;

	public ChunkCoordIntPair(int var1, int var2) {
		this.chunkXPos = var1;
		this.chunkZPos = var2;
	}
	
	/**+
	 * converts a chunk coordinate pair to an integer (suitable for
	 * hashing)
	 */
	public static long chunkXZ2Int(int x, int z) {
		return (long) x & 4294967295L | ((long) z & 4294967295L) << 32;
	}

	public int hashCode() {
		return this.chunkXPos << 8 | this.chunkZPos;
	}

	public boolean equals(Object var1) {
		ChunkCoordIntPair var2 = (ChunkCoordIntPair)var1;
		return var2.chunkXPos == this.chunkXPos && var2.chunkZPos == this.chunkZPos;
	}
}
