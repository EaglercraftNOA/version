package net.minecraft.world.chunk.storage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class RegionFileChunkBuffer extends ByteArrayOutputStream {
	private int chunkX;
	private int chunkZ;

	final RegionFile regionFile;

	public RegionFileChunkBuffer(RegionFile par1RegionFile, int par2, int par3) {
		super(8096);
		this.regionFile = par1RegionFile;
		this.chunkX = par2;
		this.chunkZ = par3;
	}

	public void close() throws IOException {
		this.regionFile.write(this.chunkX, this.chunkZ, this.buf, this.count);
	}
}
