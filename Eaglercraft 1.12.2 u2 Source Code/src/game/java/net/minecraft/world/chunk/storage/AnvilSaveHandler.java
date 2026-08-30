package net.minecraft.world.chunk.storage;

import javax.annotation.Nullable;

import net.lax1dude.eaglercraft.internal.vfs2.VFile2;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldProviderEnd;
import net.minecraft.world.WorldProviderHell;
import net.minecraft.world.storage.SaveHandler;
import net.minecraft.world.storage.WorldInfo;

public class AnvilSaveHandler extends SaveHandler {
	public AnvilSaveHandler(VFile2 p_i46650_1_, String p_i46650_2_, boolean p_i46650_3_, DataFixer dataFixerIn) {
		super(p_i46650_1_, p_i46650_2_, p_i46650_3_, dataFixerIn);
	}

	/**
	 * initializes and returns the chunk loader for the specified world provider
	 */
	public IChunkLoader getChunkLoader(WorldProvider provider) {
		VFile2 file1 = this.getWorldDirectory();

		if (provider instanceof WorldProviderHell) {
			VFile2 file3 = new VFile2(file1, "DIM-1");
			return new AnvilChunkLoader(file3, this.dataFixer);
		} else if (provider instanceof WorldProviderEnd) {
			VFile2 file2 = new VFile2(file1, "DIM1");
			return new AnvilChunkLoader(file2, this.dataFixer);
		} else {
			return new AnvilChunkLoader(file1, this.dataFixer);
		}
	}

	/**
	 * Saves the given World Info with the given NBTTagCompound as the Player.
	 */
	public void saveWorldInfoWithPlayer(WorldInfo worldInformation, @Nullable NBTTagCompound tagCompound) {
		worldInformation.setSaveVersion(19133);
		super.saveWorldInfoWithPlayer(worldInformation, tagCompound);
	}

	/**
	 * Called to flush all changes to disk, waiting for them to complete.
	 */
	public void flush() {
		RegionFileCache.clearRegionFileReferences();
	}
}