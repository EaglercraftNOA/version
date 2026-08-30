package net.minecraft.world.storage;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

import net.lax1dude.eaglercraft.EagUtils;
import net.lax1dude.eaglercraft.internal.vfs2.VFile2;
import net.minecraft.client.AnvilConverterException;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.datafix.FixTypes;
import net.peyton.eagler.fs.FileUtils;
import net.peyton.eagler.fs.WorldsDB;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SaveFormatOld implements ISaveFormat {
	private static final Logger LOGGER = LogManager.getLogger();

	/**
	 * Reference to the File object representing the directory for the world saves
	 */
	public final VFile2 savesDirectory;
	protected final DataFixer dataFixer;

	public SaveFormatOld(VFile2 savesDirectoryIn, DataFixer dataFixerIn) {
		this.dataFixer = dataFixerIn;
		/*
		 * Why THE FUCK does directly setting saves dir to "savesDirectoryIn" crash!?!?
		 */
		this.savesDirectory = new VFile2(savesDirectoryIn.getPath());
	}

	/**
	 * Returns the name of the save format.
	 */
	public String getName() {
		return "Old Format";
	}

	public List<WorldSummary> getSaveList() throws AnvilConverterException {
		return FileUtils.getSaveList(this.savesDirectory, this);
	}

	public void flushCache() {
	}

	@Nullable

	/**
	 * Returns the world's WorldInfo object
	 */
	public WorldInfo getWorldInfo(String saveName) {
		VFile2 file1 = WorldsDB.newVFile(this.savesDirectory, saveName);
		VFile2 file2 = WorldsDB.newVFile(file1, "level.dat");

		if (file2.exists()) {
			WorldInfo worldinfo = getWorldData(file2, this.dataFixer);

			if (worldinfo != null) {
				return worldinfo;
			}
		}

		file2 = WorldsDB.newVFile(file1, "level.dat_old");
		return file2.exists() ? getWorldData(file2, this.dataFixer) : null;
	}

	@Nullable
	public static WorldInfo getWorldData(VFile2 p_186353_0_, DataFixer dataFixerIn) {
		try {
			NBTTagCompound nbttagcompound = CompressedStreamTools.readCompressed(p_186353_0_.getInputStream());
			NBTTagCompound nbttagcompound1 = nbttagcompound.getCompoundTag("Data");
			return new WorldInfo(dataFixerIn.process(FixTypes.LEVEL, nbttagcompound1));
		} catch (Exception exception) {
			LOGGER.error("Exception reading {}", p_186353_0_, exception);
			return null;
		}
	}

	/**
	 * Renames the world by storing the new name in level.dat. It does *not* rename
	 * the directory containing the world data.
	 */
	public void renameWorld(String dirName, String newName) {
		VFile2 file1 = new VFile2(this.savesDirectory, dirName);
		VFile2 file2 = new VFile2(file1, "level.dat");

		if (file2.exists()) {

			try {
				NBTTagCompound nbttagcompound = CompressedStreamTools.readCompressed(file2.getInputStream());
				NBTTagCompound nbttagcompound1 = nbttagcompound.getCompoundTag("Data");
				nbttagcompound1.setString("LevelName", newName);
				CompressedStreamTools.writeCompressed(nbttagcompound, file2.getOutputStream());
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}
	}

	public boolean isNewLevelIdAcceptable(String saveName) {
		VFile2 file1 = new VFile2(this.savesDirectory, saveName);
		VFile2 file2 = new VFile2(file1, "level.dat");

		if (file2.exists()) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Deletes a world directory.
	 */
	public boolean deleteWorldDirectory(String saveName) {
		FileUtils.removeWorldIfExists(saveName);
		VFile2 file1 = new VFile2(this.savesDirectory, saveName);

		LOGGER.info("Deleting level {}", (Object) saveName);

		for (int i = 1; i <= 5; ++i) {
			LOGGER.info("Attempt {}...", (int) i);

			if (deleteFiles(file1.listFiles(true))) {
				return true;
			}

			LOGGER.warn("Unsuccessful in deleting contents.");

			if (i < 5) {
				EagUtils.sleep(500);
			}
		}

		return false;
	}

	/**
	 * Deletes a list of files and directories.
	 */
	protected static boolean deleteFiles(List<VFile2> files) {
		for (int i = 0, l = files.size(); i < l; ++i) {
			VFile2 file1 = files.get(i);

			if (!file1.delete()) {
				LOGGER.warn("Couldn't delete file " + file1.getPath());
				return false;
			}
		}

		return true;
	}

	/**
	 * Returns back a loader for the specified save directory
	 */
	public ISaveHandler getSaveLoader(String saveName, boolean storePlayerdata) {
		return new SaveHandler(this.savesDirectory, saveName, storePlayerdata, this.dataFixer);
	}

	public boolean isConvertible(String saveName) {
		return false;
	}

	/**
	 * gets if the map is old chunk saving (true) or McRegion (false)
	 */
	public boolean isOldMapFormat(String saveName) {
		return false;
	}

	/**
	 * converts the map to mcRegion
	 */
	public boolean convertMapFormat(String filename, IProgressUpdate progressCallback) {
		return false;
	}

	/**
	 * Return whether the given world can be loaded.
	 */
	public boolean canLoadWorld(String saveName) {
		VFile2 file1 = new VFile2(this.savesDirectory, saveName);
		VFile2 file2 = new VFile2(file1, "level.dat");
		return file2.exists();
	}

	public VFile2 getFile(String p_186352_1_, String p_186352_2_) {
		return new VFile2(new VFile2(this.savesDirectory, p_186352_1_), p_186352_2_);
	}
}
