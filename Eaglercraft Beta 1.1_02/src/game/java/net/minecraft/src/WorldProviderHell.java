package net.minecraft.src;

import net.lax1dude.eaglercraft.internal.vfs2.VFile2;

public class WorldProviderHell extends WorldProvider {
	public void registerWorldChunkManager() {
		this.worldChunkMgr = new WorldChunkManagerHell(MobSpawnerBase.hell, 1.0D, 0.0D);
		this.field_4220_c = true;
		this.field_6479_d = true;
		this.field_6478_e = true;
		this.worldType = -1;
	}

	public Vec3D func_4096_a(float var1, float var2) {
		return Vec3D.createVector((double)0.2F, (double)0.03F, (double)0.03F);
	}

	protected void generateLightBrightnessTable() {
		float var1 = 0.1F;

		for(int var2 = 0; var2 <= 15; ++var2) {
			float var3 = 1.0F - (float)var2 / 15.0F;
			this.lightBrightnessTable[var2] = (1.0F - var3) / (var3 * 3.0F + 1.0F) * (1.0F - var1) + var1;
		}

	}

	public IChunkProvider getChunkProvider() {
		return new ChunkProviderHell(this.worldObj, this.worldObj.randomSeed);
	}

	public IChunkLoader getChunkLoader(VFile2 var1) {
		VFile2 var2 = new VFile2(var1, "DIM-1");
		return new ChunkLoader(var2, true);
	}

	public boolean canCoordinateBeSpawn(int var1, int var2) {
		int var3 = this.worldObj.func_614_g(var1, var2);
		return var3 == Block.bedrock.blockID ? false : (var3 == 0 ? false : Block.opaqueCubeLookup[var3]);
	}

	public float calculateCelestialAngle(long var1, float var3) {
		return 0.5F;
	}

	public boolean func_6477_d() {
		return false;
	}
}
