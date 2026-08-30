package net.minecraft.world.storage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

import javax.annotation.Nullable;

import net.lax1dude.eaglercraft.internal.vfs2.VFile2;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.gen.structure.template.TemplateManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SaveHandler implements ISaveHandler, IPlayerFileData {
	private static final Logger LOGGER = LogManager.getLogger();

	/** The directory in which to save world data. */
	private final VFile2 worldDirectory;

	/** The directory in which to save player data. */
	private final VFile2 playersDirectory;
	private final VFile2 mapDataDir;

	/**
	 * The time in milliseconds when this field was initialized. Stored in the
	 * session lock file.
	 */
	private final long initializationTime = MinecraftServer.getCurrentTimeMillis();

	/** The directory name of the world */
	private final String saveDirectoryName;
	private final TemplateManager structureTemplateManager;
	protected final DataFixer dataFixer;

	public SaveHandler(VFile2 p_i46648_1_, String saveDirectoryNameIn, boolean p_i46648_3_, DataFixer dataFixerIn) {
		this.dataFixer = dataFixerIn;
		this.worldDirectory = new VFile2(p_i46648_1_, saveDirectoryNameIn);
		this.playersDirectory = new VFile2(this.worldDirectory, "playerdata");
		this.mapDataDir = new VFile2(this.worldDirectory, "data");
		this.saveDirectoryName = saveDirectoryNameIn;

		if (p_i46648_3_) {
			this.structureTemplateManager = new TemplateManager(
					(new VFile2(this.worldDirectory, "structures")).toString(), dataFixerIn);
		} else {
			this.structureTemplateManager = null;
		}

		this.setSessionLock();
	}

	/**
	 * Creates a session lock file for this process
	 */
	private void setSessionLock() {
	}

	/**
	 * Gets the File object corresponding to the base directory of this world.
	 */
	public VFile2 getWorldDirectory() {
		return this.worldDirectory;
	}

	/**
	 * Checks the session lock to prevent save collisions
	 */
	public void checkSessionLock() throws MinecraftException {
	}

	/**
	 * initializes and returns the chunk loader for the specified world provider
	 */
	public IChunkLoader getChunkLoader(WorldProvider provider) {
		throw new RuntimeException("Old Chunk Storage is no longer supported.");
	}

	@Nullable

	/**
	 * Loads and returns the world info
	 */
	public WorldInfo loadWorldInfo() {
		VFile2 file1 = new VFile2(this.worldDirectory, "level.dat");

		if (file1.exists()) {
			WorldInfo worldinfo = SaveFormatOld.getWorldData(file1, this.dataFixer);

			if (worldinfo != null) {
				return worldinfo;
			}
		}

		file1 = new VFile2(this.worldDirectory, "level.dat_old");
		return file1.exists() ? SaveFormatOld.getWorldData(file1, this.dataFixer) : null;
	}

	/**
	 * Saves the given World Info with the given NBTTagCompound as the Player.
	 */
	public void saveWorldInfoWithPlayer(WorldInfo worldInformation, @Nullable NBTTagCompound tagCompound) {
		NBTTagCompound nbttagcompound = worldInformation.cloneNBTCompound(tagCompound);
		NBTTagCompound nbttagcompound1 = new NBTTagCompound();
		nbttagcompound1.setTag("Data", nbttagcompound);

		try {
			VFile2 file1 = new VFile2(this.worldDirectory, "level.dat_new");
			VFile2 file2 = new VFile2(this.worldDirectory, "level.dat_old");
			VFile2 file3 = new VFile2(this.worldDirectory, "level.dat");
			CompressedStreamTools.writeCompressed(nbttagcompound1, file1.getOutputStream());

			if (file2.exists()) {
				file2.delete();
			}

			file3.renameTo(file2);

			if (file3.exists()) {
				file3.delete();
			}

			file1.renameTo(file3);

			if (file1.exists()) {
				file1.delete();
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	/**
	 * used to update level.dat from old format to MCRegion format
	 */
	public void saveWorldInfo(WorldInfo worldInformation) {
		this.saveWorldInfoWithPlayer(worldInformation, (NBTTagCompound) null);
	}

	/**
	 * Writes the player data to disk from the specified PlayerEntityMP.
	 */
	public void writePlayerData(EntityPlayer player) {
		try {
			NBTTagCompound nbttagcompound = player.writeToNBT(new NBTTagCompound());
			VFile2 file1 = new VFile2(this.playersDirectory, player.getCachedUniqueIdString() + ".dat.tmp");
			VFile2 file2 = new VFile2(this.playersDirectory, player.getCachedUniqueIdString() + ".dat");
			CompressedStreamTools.writeCompressed(nbttagcompound, file1.getOutputStream());

			if (file2.exists()) {
				file2.delete();
			}

			file1.renameTo(file2);
		} catch (Exception var5) {
			LOGGER.warn("Failed to save player data for {}", (Object) player.getName());
		}
	}

	@Nullable

	/**
	 * Reads the player data from disk into the specified PlayerEntityMP.
	 */
	public NBTTagCompound readPlayerData(EntityPlayer player) {
		NBTTagCompound nbttagcompound = null;

		try {
			VFile2 file1 = new VFile2(this.playersDirectory, player.getCachedUniqueIdString() + ".dat");

			if (file1.exists()) {
				nbttagcompound = CompressedStreamTools.readCompressed(file1.getInputStream());
			}
		} catch (Exception var4) {
			LOGGER.warn("Failed to load player data for {}", (Object) player.getName());
		}

		if (nbttagcompound != null) {
			player.readFromNBT(this.dataFixer.process(FixTypes.PLAYER, nbttagcompound));
		}

		return nbttagcompound;
	}

	public IPlayerFileData getPlayerNBTManager() {
		return this;
	}

	/**
	 * Returns an array of usernames for which player.dat exists for.
	 */
	public String[] getAvailablePlayerDat() {
		List<String> astring = this.playersDirectory.listFilenames(false);

		for (int i = 0, l = astring.size(); i < l; ++i) {
			String str = astring.get(i);
			if (str.endsWith(".dat")) {
				astring.set(i, str.substring(0, str.length() - 4));
			}
		}

		return astring.toArray(new String[astring.size()]);
	}

	/**
	 * Called to flush all changes to disk, waiting for them to complete.
	 */
	public void flush() {
	}

	/**
	 * Gets the file location of the given map
	 */
	public VFile2 getMapFileFromName(String mapName) {
		return new VFile2(this.mapDataDir, mapName + ".dat");
	}

	public TemplateManager getStructureTemplateManager() {
		return this.structureTemplateManager;
	}
}
