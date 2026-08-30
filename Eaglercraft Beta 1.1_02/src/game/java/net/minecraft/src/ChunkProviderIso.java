package net.minecraft.src;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkProviderIso implements IChunkProvider {
	private Chunk[] chunks = new Chunk[256];
	private World worldObj;
	private IChunkLoader chunkLoader;
	byte[] field_899_a = new byte[-Short.MIN_VALUE];
	
	private Logger LOGGER = LogManager.getLogger();

	public ChunkProviderIso(World var1, IChunkLoader var2) {
		this.worldObj = var1;
		this.chunkLoader = var2;
	}

	public boolean chunkExists(int var1, int var2) {
		int var3 = var1 & 15 | (var2 & 15) * 16;
		return this.chunks[var3] != null && this.chunks[var3].isAtLocation(var1, var2);
	}

	public Chunk provideChunk(int var1, int var2) {
		int var3 = var1 & 15 | (var2 & 15) * 16;

		try {
			if(!this.chunkExists(var1, var2)) {
				Chunk var4 = this.func_543_c(var1, var2);
				if(var4 == null) {
					var4 = new Chunk(this.worldObj, this.field_899_a, var1, var2);
					var4.field_1524_q = true;
					var4.neverSave = true;
				}

				this.chunks[var3] = var4;
			}

			return this.chunks[var3];
		} catch (Exception var5) {
			LOGGER.error(var5);
			return null;
		}
	}

	private Chunk func_543_c(int var1, int var2) {
		try {
			return this.chunkLoader.loadChunk(this.worldObj, var1, var2);
		} catch (IOException var4) {
			LOGGER.error(var4);
			return null;
		}
	}

	public void populate(IChunkProvider var1, int var2, int var3) {
	}

	public boolean saveChunks(boolean var1, IProgressUpdate var2) {
		return true;
	}

	public boolean func_532_a() {
		return false;
	}

	public boolean func_536_b() {
		return false;
	}
}
