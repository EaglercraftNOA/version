/*
 * Copyright (c) 2023-2024 lax1dude. All Rights Reserved.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 */

package net.lax1dude.eaglercraft.v1_8.sp.server;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import net.lax1dude.eaglercraft.v1_8.EagUtils;
import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.world.GameType;

public class EaglerSaveFormat {

	private static final Logger logger = LogManager.getLogger("EaglerSaveFormat");

   public static final VFile2 worldsList = WorldsDB.newVFile("worlds_list.txt");
   public static final VFile2 worldsFolder = WorldsDB.newVFile("worlds");

	public final VFile2 savesDirectory;

	public EaglerSaveFormat(VFile2 savesDirectory) {
		this.savesDirectory = savesDirectory;
	}

	public String getName() {
		return "eagler";
	}

	public EaglerSaveHandler getSaveLoader(String name, boolean readOnly) {
		return new EaglerSaveHandler(this.savesDirectory, name);
	}

	public List<SingleplayerWorldSummary> getSaveList() {
		ArrayList<SingleplayerWorldSummary> worlds = new ArrayList<>();
		for(NBTTagCompound levelDat : getSaveListNBT()) {
			String folderName = levelDat.getString("folderNameEagler");
			SingleplayerWorldSummary summary = this.createSummary(folderName, levelDat);
			if(summary != null) {
				worlds.add(summary);
			}
		}
		return worlds;
	}

	public SingleplayerWorldSummary getWorldInfo(String folderName) {
		NBTTagCompound levelDat = readLevelDatAny(folderName);
		if(levelDat == null) {
			return null;
		}
		return createSummary(folderName, levelDat);
	}

	public List<NBTTagCompound> getSaveListNBT() {
		ArrayList<NBTTagCompound> worlds = new ArrayList<>();
		Set<String> visited = new HashSet<>();
		if(worldsList.exists()) {
			String[] lines = worldsList.getAllLines();
			for(int i = 0; i < lines.length; ++i) {
				addWorldNBT(worlds, visited, lines[i]);
			}
		}
      return worlds;
	}

	private void addWorldNBT(List<NBTTagCompound> worlds, Set<String> visited, String folderName) {
		if(StringUtils.isEmpty(folderName)) {
			return;
		}
		folderName = folderName.trim();
		if(folderName.length() <= 0 || !visited.add(folderName)) {
			return;
		}
		NBTTagCompound levelDat = readLevelDatAny(folderName);
		if(levelDat != null) {
			levelDat.putString("folderNameEagler", folderName);
			worlds.add(levelDat);
		}
	}

	private SingleplayerWorldSummary createSummary(String folderName, NBTTagCompound levelDat) {
		NBTTagCompound data = levelDat.getCompound("Data");
		String displayName = data.getString("LevelName");
		if(StringUtils.isEmpty(displayName)) {
			displayName = folderName;
		}
		return new SingleplayerWorldSummary(folderName, displayName, data.getLong("LastPlayed"),
				GameType.getByID(data.getInt("GameType")), data.getBoolean("hardcore"), data.getBoolean("allowCommands"),
				levelDat);
	}

	public void clearPlayers(String worldFolder) {
      VFile2 file = WorldsDB.newVFile(this.savesDirectory, worldFolder, "player");
      deleteFiles(file.listFiles(true), null);
	}

	public boolean duplicateWorld(String worldFolder, String displayName) {
		String newFolderName = displayName.replaceAll("[\\./\"]", "_");
		VFile2 newFolder = WorldsDB.newVFile(savesDirectory, newFolderName);
		while((WorldsDB.newVFile(newFolder, "level.dat")).exists()
				|| (WorldsDB.newVFile(newFolder, "level.dat_old")).exists()) {
			newFolderName += "_";
			newFolder = WorldsDB.newVFile(savesDirectory, newFolderName);
		}
		VFile2 oldFolder = WorldsDB.newVFile(this.savesDirectory, worldFolder);
		String oldPath = oldFolder.getPath();
		int totalSize = 0;
		int lastUpdate = 0;
		List<VFile2> files = oldFolder.listFiles(true);
		for(int i = 0, l = files.size(); i < l; ++i) {
			VFile2 source = files.get(i);
			String fileNameRelative = source.getPath().substring(oldPath.length() + 1);
			totalSize += VFile2.copyFile(source, WorldsDB.newVFile(newFolder, fileNameRelative));
			if(totalSize - lastUpdate > 10000) {
				lastUpdate = totalSize;
				EaglerIntegratedServerWorker.sendProgress("singleplayer.busy.duplicating", totalSize);
			}
		}
		addWorldToList(newFolderName);
		return renameWorld(newFolderName, displayName);
	}

	public boolean renameWorld(String folderName, String displayName) {
		VFile2 levelDatFile = WorldsDB.newVFile(this.savesDirectory, folderName, "level.dat");
		if(levelDatFile.exists()) {
			try {
				NBTTagCompound levelDat;
				try(InputStream inputStream = levelDatFile.getInputStream()) {
					levelDat = CompressedStreamTools.readCompressed(inputStream);
				}
				levelDat.getCompound("Data").putString("LevelName", displayName);
				try(OutputStream outputStream = levelDatFile.getOutputStream()) {
					CompressedStreamTools.writeCompressed(levelDat, outputStream);
				}
				return true;
			}catch(Throwable t) {
				logger.error("Failed to rename world \"{}\"!", folderName);
				logger.error(t);
				return false;
			}
		}
		return renameNativeWorld(folderName, displayName);
	}

	private boolean renameNativeWorld(String folderName, String displayName) {
      return false;
	}

	public boolean deleteWorldDirectory(String worldName) {
      VFile2 file = WorldsDB.newVFile(this.savesDirectory, worldName);
      logger.info("Deleting level " + worldName);
      for(int i = 1; i <= 5; ++i) {
			logger.info("Attempt " + i + "...");
			if(deleteFiles(file.listFiles(true), "singleplayer.busy.deleting")) {
				return true;
			}
			logger.warn("Unsuccessful in deleting contents.");
			if(i < 5) {
				EagUtils.sleep(500);
			}
		}
		return false;
	}

   public boolean canLoadWorld(String worldName) {
      return WorldsDB.newVFile(this.savesDirectory, worldName, "level.dat").exists()
            || WorldsDB.newVFile(this.savesDirectory, worldName, "level.dat_old").exists();
	}

	protected static boolean deleteFiles(List<VFile2> files, String progressString) {
		long totalSize = 0L;
		long lastUpdate = 0L;
		for(int i = 0, l = files.size(); i < l; ++i) {
			VFile2 file = files.get(i);
			if(progressString != null) {
				totalSize += file.length();
				if(totalSize - lastUpdate > 10000) {
					lastUpdate = totalSize;
					EaglerIntegratedServerWorker.sendProgress(progressString, totalSize);
				}
			}
			if(!file.delete()) {
				logger.warn("Couldn't delete file " + file);
				return false;
			}
		}
		return true;
	}

	private NBTTagCompound readLevelDat(String folderName) {
		VFile2 levelDat = WorldsDB.newVFile(this.savesDirectory, folderName, "level.dat");
		if(!levelDat.exists()) {
			levelDat = WorldsDB.newVFile(this.savesDirectory, folderName, "level.dat_old");
		}
		if(!levelDat.exists()) {
			return null;
		}
		try(InputStream inputStream = levelDat.getInputStream()) {
			return CompressedStreamTools.readCompressed(inputStream);
		}catch(Throwable t) {
			logger.error("Failed to read level.dat for \"{}\"!", folderName);
			logger.error(t);
			return null;
		}
	}

	private NBTTagCompound readLevelDatAny(String folderName) {
		NBTTagCompound levelDat = readLevelDat(folderName);
		return levelDat != null ? levelDat : readNativeLevelDat(folderName);
	}

   private NBTTagCompound readNativeLevelDat(String folderName) {
      return null;
   }

	private static void addWorldToList(String folderName) {
		String[] worldsTxt = worldsList.getAllLines();
		if(worldsTxt == null || worldsTxt.length <= 0) {
			worldsTxt = new String[] { folderName };
		}else {
			String[] oldWorlds = worldsTxt;
			worldsTxt = new String[oldWorlds.length + 1];
			System.arraycopy(oldWorlds, 0, worldsTxt, 0, oldWorlds.length);
			worldsTxt[worldsTxt.length - 1] = folderName;
		}
		worldsList.setAllChars(String.join("\n", worldsTxt));
	}

	public static class SingleplayerWorldSummary {
		public final String folderName;
		public final String displayName;
		public final long lastPlayed;
		public final GameType gameType;
		public final boolean hardcore;
		public final boolean commands;
		public final NBTTagCompound levelDat;

		public SingleplayerWorldSummary(String folderName, String displayName, long lastPlayed, GameType gameType,
				boolean hardcore, boolean commands, NBTTagCompound levelDat) {
			this.folderName = folderName;
			this.displayName = displayName;
			this.lastPlayed = lastPlayed;
			this.gameType = gameType;
			this.hardcore = hardcore;
			this.commands = commands;
			this.levelDat = levelDat;
		}
	}
}
