package net.lax1dude.eaglercraft.sp.server;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;

import net.lax1dude.eaglercraft.internal.vfs2.VFile2;
import net.lax1dude.eaglercraft.sp.server.skins.IntegratedCapeService;
import net.lax1dude.eaglercraft.sp.server.skins.IntegratedSkinService;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Util;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.storage.ISaveFormat;

public class EaglerMinecraftServer extends MinecraftServer {

	public static final Logger logger = EaglerIntegratedServerWorker.logger;

	protected EnumDifficulty difficulty;
	protected GameType gamemode;
	protected WorldSettings newWorldSettings;
	protected IntegratedSkinService skinService;
	protected IntegratedCapeService capeService;

	public static int counterTicksPerSecond = 0;
	public static int counterChunkRead = 0;
	public static int counterChunkGenerate = 0;
	public static int counterChunkWrite = 0;
	public static int counterTileUpdate = 0;
	public static int counterLightUpdate = 0;

	private final List<Runnable> scheduledTasks = new LinkedList();

	private long lastTPSUpdate = 0l;

	public EaglerMinecraftServer(String mcDataDir, String folderName, String worldName, String owner, int viewDistance, WorldSettings currentWorldSettings, boolean demo, ISaveFormat format) {
		super(new VFile2(mcDataDir, "worlds"));
		this.skinService = new IntegratedSkinService(
				new VFile2(anvilConverterForAnvilFile.getSaveLoader(folderName, false).getWorldDirectory().getPath(),
						"eagler/skulls"));
		this.capeService = new IntegratedCapeService();
		this.setServerOwner(owner);
		logger.info("server owner: " + owner);
		this.setFolderName(folderName);
		this.setWorldName(worldName);
		this.setDemo(demo);
		this.setBuildLimit(256);
		this.setConfigManager(new EaglerPlayerList(this, viewDistance));
		this.newWorldSettings = currentWorldSettings;
		this.isGamePaused = false;
	}

	public IntegratedSkinService getSkinService() {
		return skinService;
	}
	
	public IntegratedCapeService getCapeService() {
		return capeService;
	}

	public void setBaseServerProperties(EnumDifficulty difficulty, GameType gamemode) {
		this.difficulty = difficulty;
		this.gamemode = gamemode;
		this.setCanSpawnAnimals(true);
		this.setCanSpawnNPCs(true);
		this.setAllowPvp(true);
		this.setAllowFlight(true);
	}

	@Override
	public void addScheduledTask(Runnable var1) {
		scheduledTasks.add(var1);
	}

	@Override
	public boolean startServer() throws IOException {
		logger.info("Starting integrated eaglercraft server version 1.12.2");
		this.setOnlineMode(false);
		this.setCanSpawnAnimals(true);
		this.setCanSpawnNPCs(true);
		this.setAllowPvp(true);
		this.setAllowFlight(true);
		this.loadAllWorlds(this.getFolderName(), this.getWorldName(), newWorldSettings);
		this.setMOTD(this.getServerOwner() + " - " + this.worldServers[0].getWorldInfo().getWorldName());
		serverRunning = true;
		return true;
	}

	public void mainLoop(boolean singleThreadMode) {
		long k = getCurrentTimeMillis();
		this.sendTPSToClient(k);
		if (isGamePaused) {
			currentTime = k;
			return;
		}

		long j = k - this.currentTime;
		if ((j > (singleThreadMode ? 500L : 2000L)
				&& this.currentTime - this.timeOfLastWarning >= (singleThreadMode ? 5000L : 15000L))) {
			logger.warn(
					"Can\'t keep up! Did the system time change, or is the server overloaded? Running {}ms behind, skipping {} tick(s)",
					new Object[] { Long.valueOf(j), Long.valueOf(j / 50L) });
			j = 100L;
			this.currentTime = k - 100l;
			this.timeOfLastWarning = this.currentTime;
		}

		if (j < 0L) {
			logger.warn("Time ran backwards! Did the system time change?");
			j = 0L;
			this.currentTime = k;
		}

		if (this.worldServers[0].areAllPlayersAsleep()) {
			this.currentTime = k;
			this.tick();
			++counterTicksPerSecond;
		} else {
			if (j > 50L) {
				this.currentTime += 50l;
				this.tick();
				++counterTicksPerSecond;
			}
		}
	}

	public void updateTimeLightAndEntities() {
		this.skinService.flushCache();
		super.updateTimeLightAndEntities();
	}

	protected void sendTPSToClient(long millis) {
		if (millis - lastTPSUpdate > 1000l) {
			lastTPSUpdate = millis;
			if (serverRunning && this.worldServers != null) {
				List<String> lst = Lists.newArrayList("TPS: " + counterTicksPerSecond + "/20",
						"Chunks: " + countChunksLoaded(this.worldServers) + "/" + countChunksTotal(this.worldServers),
						"Entities: " + countEntities(this.worldServers) + "+" + countTileEntities(this.worldServers),
						"R: " + counterChunkRead + ", G: " + counterChunkGenerate + ", W: " + counterChunkWrite,
						"TU: " + counterTileUpdate + ", LU: " + counterLightUpdate);
				int players = countPlayerEntities(this.worldServers);
				if (players > 1) {
					lst.add("Players: " + players);
				}
				counterTicksPerSecond = counterChunkRead = counterChunkGenerate = 0;
				counterChunkWrite = counterTileUpdate = counterLightUpdate = 0;
				EaglerIntegratedServerWorker.reportTPS(lst);
			}
		}
	}

	private static int countChunksLoaded(WorldServer[] worlds) {
		int i = 0;
		for (int j = 0; j < worlds.length; ++j) {
			if (worlds[j] != null) {
				i += worlds[j].getChunkProvider().getLoadedChunkCount();
			}
		}
		return i;
	}

	private static int countChunksTotal(WorldServer[] worlds) {
		int i = 0;
		for (int j = 0; j < worlds.length; ++j) {
			if (worlds[j] != null) {
				// List<EntityPlayer> players = worlds[j].playerEntities;
				// for(int l = 0, n = players.size(); l < n; ++l) {
				// i += ((EntityPlayerMP)players.get(l)).loadedChunks.size();
				// }
				i += worlds[j].getChunkProvider().getLoadedChunkCount();
			}
		}
		return i;
	}

	private static int countEntities(WorldServer[] worlds) {
		int i = 0;
		for (int j = 0; j < worlds.length; ++j) {
			if (worlds[j] != null) {
				i += worlds[j].loadedEntityList.size();
			}
		}
		return i;
	}

	private static int countTileEntities(WorldServer[] worlds) {
		int i = 0;
		for (int j = 0; j < worlds.length; ++j) {
			if (worlds[j] != null) {
				i += worlds[j].loadedTileEntityList.size();
			}
		}
		return i;
	}

	private static int countPlayerEntities(WorldServer[] worlds) {
		int i = 0;
		for (int j = 0; j < worlds.length; ++j) {
			if (worlds[j] != null) {
				i += worlds[j].playerEntities.size();
			}
		}
		return i;
	}

	public void setPaused(boolean p) {
		isGamePaused = p;
		if (!p) {
			currentTime = System.currentTimeMillis();
		}
	}

	public boolean getPaused() {
		return isGamePaused;
	}

	@Override
	public boolean canStructuresSpawn() {
		return worldServers != null ? worldServers[0].getWorldInfo().isMapFeaturesEnabled()
				: newWorldSettings.isMapFeaturesEnabled();
	}

	@Override
	public GameType getGameType() {
		return worldServers != null ? worldServers[0].getWorldInfo().getGameType() : newWorldSettings.getGameType();
	}

	@Override
	public EnumDifficulty getDifficulty() {
		return difficulty;
	}

	@Override
	public boolean isHardcore() {
		return worldServers != null ? worldServers[0].getWorldInfo().isHardcoreModeEnabled()
				: newWorldSettings.getHardcoreEnabled();
	}

	@Override
	public int getOpPermissionLevel() {
		return 4;
	}

	@Override
	public boolean shouldBroadcastRconToOps() {
		return false;
	}

	@Override
	public boolean shouldBroadcastConsoleToOps() {
		return false;
	}

	@Override
	public boolean isDedicatedServer() {
		return false;
	}

	@Override
	public boolean isCommandBlockEnabled() {
		return true;
	}

}
