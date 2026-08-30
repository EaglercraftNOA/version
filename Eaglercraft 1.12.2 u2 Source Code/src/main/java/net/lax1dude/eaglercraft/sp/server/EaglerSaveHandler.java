package net.lax1dude.eaglercraft.sp.server;

import net.lax1dude.eaglercraft.internal.vfs2.VFile2;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.storage.SaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.datafix.DataFixer;

public class EaglerSaveHandler extends SaveHandler {

	public EaglerSaveHandler(VFile2 p_i46648_1_, String saveDirectoryNameIn, boolean p_i46648_3_,
			DataFixer dataFixerIn) {
		super(p_i46648_1_, saveDirectoryNameIn, p_i46648_3_, dataFixerIn);
	}

	@Override
	public IChunkLoader getChunkLoader(WorldProvider provider) {
		return new EaglerChunkLoader(
				new VFile2(this.getWorldDirectory(), "level" + provider.getDimensionType().getId()), this.dataFixer);
	}

	@Override
	public void saveWorldInfoWithPlayer(WorldInfo worldInformation, NBTTagCompound tagCompound) {
		worldInformation.setSaveVersion(19133);
		super.saveWorldInfoWithPlayer(worldInformation, tagCompound);
	}

	@Override
	public void saveWorldInfo(WorldInfo worldInformation) {
		worldInformation.setSaveVersion(19133);
		super.saveWorldInfo(worldInformation);
	}
}
