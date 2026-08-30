package net.minecraft.world.storage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import net.lax1dude.eaglercraft.internal.vfs2.VFile2;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.storage.IChunkLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SaveHandler implements ISaveHandler, IPlayerFileData {
	private static final Logger logger = LogManager.getLogger();

	/** The directory in which to save world data. */
	private final VFile2 worldDirectory;

	/** The directory in which to save player data. */
	private final VFile2 playersDirectory;
	private final VFile2 mapDataDir;

	/**
	 * The time in milliseconds when this field was initialized. Stored in the
	 * session lock file.
	 */
	private final long initializationTime = MinecraftServer.getSystemTimeMillis();

	/** The directory name of the world */
	private final String saveDirectoryName;
	private static final String __OBFID = "CL_00000585";

	public SaveHandler(VFile2 p_i2146_1_, String p_i2146_2_) {
		System.err.println("[SaveHandler] Constructor called:");
		System.err.println("  Base directory: " + p_i2146_1_.getPath());
		System.err.println("  World name: " + p_i2146_2_);

		this.worldDirectory = net.lax1dude.eaglercraft.sp.server.WorldsDB.newVFile(p_i2146_1_.getPath(), p_i2146_2_);
		System.err.println("  Final worldDirectory: " + this.worldDirectory.getPath());
		this.playersDirectory = net.lax1dude.eaglercraft.sp.server.WorldsDB.newVFile(this.worldDirectory.getPath(), "playerdata");
		this.mapDataDir = net.lax1dude.eaglercraft.sp.server.WorldsDB.newVFile(this.worldDirectory.getPath(), "data");
		this.saveDirectoryName = p_i2146_2_;

		this.setSessionLock();
	}

	/**
	 * Creates a session lock file for this process
	 */
	private void setSessionLock() {
		try {
			VFile2 var1 = new VFile2(this.worldDirectory, "session.lock");
			DataOutputStream var2 = new DataOutputStream(var1.getOutputStream());

			try {
				var2.writeLong(this.initializationTime);
			} finally {
				var2.close();
			}
		} catch (IOException var7) {
			var7.printStackTrace();
			throw new RuntimeException("Failed to check session lock, aborting");
		}
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
		try {
			VFile2 var1 = new VFile2(this.worldDirectory, "session.lock");
			DataInputStream var2 = new DataInputStream(var1.getInputStream());

			try {
				if (var2.readLong() != this.initializationTime) {
					throw new MinecraftException("The save is being accessed from another location, aborting");
				}
			} finally {
				var2.close();
			}
		} catch (IOException var7) {
			throw new MinecraftException("Failed to check session lock, aborting");
		}
	}

	/**
	 * Returns the chunk loader with the provided world provider
	 */
	public IChunkLoader getChunkLoader(WorldProvider p_75763_1_) {

		net.lax1dude.eaglercraft.internal.vfs2.VFile2 worldDirectory = this.getWorldDirectory();
		
		if (p_75763_1_ instanceof net.minecraft.world.WorldProviderHell) {
			return new net.lax1dude.eaglercraft.sp.server.EaglerChunkLoader(
				new net.lax1dude.eaglercraft.internal.vfs2.VFile2(worldDirectory, "DIM-1"));
		} else if (p_75763_1_ instanceof net.minecraft.world.WorldProviderEnd) {
			return new net.lax1dude.eaglercraft.sp.server.EaglerChunkLoader(
				new net.lax1dude.eaglercraft.internal.vfs2.VFile2(worldDirectory, "DIM1"));
		} else {
			return new net.lax1dude.eaglercraft.sp.server.EaglerChunkLoader(worldDirectory);
		}
	}

	/**
	 * Loads and returns the world info
	 */
	public WorldInfo loadWorldInfo() {
		VFile2 var1 = new VFile2(this.worldDirectory, "level.dat");
		NBTTagCompound var2;
		NBTTagCompound var3;

		if (var1.exists()) {
			try {
				var2 = CompressedStreamTools.readCompressed(var1.getInputStream());
				var3 = var2.getCompoundTag("Data");
				return new WorldInfo(var3);
			} catch (Exception var5) {
				var5.printStackTrace();
			}
		}

		var1 = new VFile2(this.worldDirectory, "level.dat_old");

		if (var1.exists()) {
			try {
				var2 = CompressedStreamTools.readCompressed(var1.getInputStream());
				var3 = var2.getCompoundTag("Data");
				return new WorldInfo(var3);
			} catch (Exception var4) {
				var4.printStackTrace();
			}
		}

		return null;
	}

	/**
	 * Saves the given World Info with the given NBTTagCompound as the Player.
	 */
	public void saveWorldInfoWithPlayer(WorldInfo p_75755_1_, NBTTagCompound p_75755_2_) {
		System.err.println("[SaveHandler] saveWorldInfoWithPlayer called for: " + p_75755_1_.getWorldName());
		System.err.println("  worldDirectory: " + this.worldDirectory.getPath());
		NBTTagCompound var3 = p_75755_1_.cloneNBTCompound(p_75755_2_);
		NBTTagCompound var4 = new NBTTagCompound();
		var4.setTag("Data", var3);

		try {
			VFile2 var5 = new VFile2(this.worldDirectory, "level.dat_new");
			VFile2 var6 = new VFile2(this.worldDirectory, "level.dat_old");
			VFile2 var7 = new VFile2(this.worldDirectory, "level.dat");
			System.err.println("  Writing to: " + var7.getPath());
			System.err.println("  var5 (level.dat_new): " + var5.getPath());
			CompressedStreamTools.writeCompressed(var4, var5.getOutputStream());

			if (var7.exists()) {
				if (var6.exists()) {
					var6.delete();
				}
				var7.renameTo(var6);
			}

			var5.renameTo(var7);

			if (var5.exists()) {
				var5.delete();
			}
			
			System.err.println("  Final check - level.dat exists: " + var7.exists());
			System.err.println("  Final path: " + var7.getPath());
		} catch (Exception var8) {
			System.err.println("  ERROR during save:");
			var8.printStackTrace();
		}
	}

	/**
	 * Saves the passed in world info.
	 */
	public void saveWorldInfo(WorldInfo p_75761_1_) {
		NBTTagCompound var2 = p_75761_1_.getNBTTagCompound();
		NBTTagCompound var3 = new NBTTagCompound();
		var3.setTag("Data", var2);

		try {
			VFile2 var4 = new VFile2(this.worldDirectory, "level.dat_new");
			VFile2 var5 = new VFile2(this.worldDirectory, "level.dat_old");
			VFile2 var6 = new VFile2(this.worldDirectory, "level.dat");
			CompressedStreamTools.writeCompressed(var3, var4.getOutputStream());

			if (var6.exists()) {
				if (var5.exists()) {
					var5.delete();
				}
				var6.renameTo(var5);
			}

			var4.renameTo(var6);

			if (var4.exists()) {
				var4.delete();
			}
		} catch (Exception var7) {
			var7.printStackTrace();
		}
	}

	/**
	 * Writes the player data to disk from the specified PlayerEntityMP.
	 */
	public void writePlayerData(EntityPlayer p_75753_1_) {
		try {
			NBTTagCompound var2 = new NBTTagCompound();
			p_75753_1_.writeToNBT(var2);
			VFile2 var3 = new VFile2(this.playersDirectory, p_75753_1_.getUniqueID().toString() + ".dat.tmp");
			VFile2 var4 = new VFile2(this.playersDirectory, p_75753_1_.getUniqueID().toString() + ".dat");
			CompressedStreamTools.writeCompressed(var2, var3.getOutputStream());

			if (var4.exists()) {
				var4.delete();
			}

			var3.renameTo(var4);
		} catch (Exception var5) {
			logger.warn("Failed to save player data for " + p_75753_1_.getCommandSenderName());
		}
	}

	/**
	 * Reads the player data from disk into the specified PlayerEntityMP.
	 */
	public NBTTagCompound readPlayerData(EntityPlayer p_75752_1_) {
		NBTTagCompound var2 = null;

		try {
			VFile2 var3 = new VFile2(this.playersDirectory, p_75752_1_.getUniqueID().toString() + ".dat");

			if (var3.exists()) {
				var2 = CompressedStreamTools.readCompressed(var3.getInputStream());
			}
		} catch (Exception var4) {
			logger.warn("Failed to load player data for " + p_75752_1_.getCommandSenderName());
		}

		if (var2 != null) {
			p_75752_1_.readFromNBT(var2);
		}

		return var2;
	}

	/**
	 * returns null if no saveHandler is relevent (eg. SMP)
	 */
	public IPlayerFileData getSaveHandler() {
		return this;
	}

	/**
	 * Returns an array of usernames for which player.dat exists for.
	 */
	public String[] getAvailablePlayerDat() {
		String[] var1 = this.playersDirectory.getAllLines();

		for (int var2 = 0; var2 < var1.length; ++var2) {
			if (var1[var2].endsWith(".dat")) {
				var1[var2] = var1[var2].substring(0, var1[var2].length() - 4);
			}
		}

		return var1;
	}

	/**
	 * Called to flush all changes to disk, waiting for them to complete.
	 */
	public void flush() {
	}

	/**
	 * Gets the file location of the given map
	 */
	public VFile2 getMapFileFromName(String p_75758_1_) {
		return new VFile2(this.mapDataDir, p_75758_1_ + ".dat");
	}

	/**
	 * Returns the name of the directory where world information is saved.
	 */
	public String getWorldDirectoryName() {
		return this.saveDirectoryName;
	}
}
