package net.lax1dude.eaglercraft.sp.server;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists;

import net.lax1dude.eaglercraft.internal.vfs2.VFile2;
import net.minecraft.init.Bootstrap;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;

public class EaglerMinecraftServer extends MinecraftServer {

	public static final Logger logger = EaglerIntegratedServerWorker.logger;

	protected boolean isGamePaused = false;
	protected EnumDifficulty difficulty;
	protected WorldSettings.GameType gamemode;
	protected WorldSettings newWorldSettings;

	protected EaglerSaveHandler saveHandler;

	public static int counterTicksPerSecond = 0;
	public static int counterChunkRead = 0;
	public static int counterChunkGenerate = 0;
	public static int counterChunkWrite = 0;
	public static int counterTileUpdate = 0;
	public static final VFile2 savesDir = WorldsDB.newVFile("worlds");
	public static int counterLightUpdate = 0;

	private final List<Runnable> scheduledTasks = new LinkedList();

	private long lastTPSUpdate = 0l;
	protected long currentTime = MinecraftServer.getCurrentTimeMillis();
	protected long timeOfLastWarning = 0L;

	public EaglerMinecraftServer(String world, String owner, int viewDistance, WorldSettings currentWorldSettings,
			boolean demo) {
		// Use 'worlds' base directory to be consistent with Eaglercraft structure
		super(WorldsDB.newVFile("worlds"), null);
		this.saveHandler = new EaglerSaveHandler(WorldsDB.newVFile("worlds"), world);
		Bootstrap.func_151354_b();
		EaglerPlayerList playerList = new EaglerPlayerList(this, viewDistance);
		this.setServerOwner(owner);
		logger.info("server owner: " + owner);
		this.setDemo(demo);
		this.canCreateBonusChest(currentWorldSettings != null && currentWorldSettings.isBonusChestEnabled());
		this.setBuildLimit(256);
		this.serverConfigManager = (playerList);
		this.newWorldSettings = currentWorldSettings;
		// Set only the world name (not path) for folderName and worldName
		String worldNameOnly = VFile2.splitPath(world)[VFile2.splitPath(world).length - 1];
		this.setFolderName(worldNameOnly);
		this.setWorldName(worldNameOnly);
	}

	public void setBaseServerProperties(EnumDifficulty difficulty, WorldSettings.GameType gamemode) {
		this.difficulty = difficulty;
		this.gamemode = gamemode;
		this.setCanSpawnAnimals(true);
		this.setCanSpawnNPCs(true);
		this.setAllowPvp(true);
		this.setAllowFlight(true);
	}

	@Override
	public boolean startServer() throws IOException {
		logger.info("Starting integrated eaglercraft server version 1.12.2");
		this.setOnlineMode(false);
		this.setCanSpawnAnimals(true);
		this.setCanSpawnNPCs(true);
		this.setAllowPvp(true);
		this.setAllowFlight(true);
		this.loadAllWorlds(saveHandler, this.getWorldName(), newWorldSettings);

		this.setMOTD(this.getServerOwner() + " - " + this.worldServers[0].getWorldInfo().getWorldName());
		serverRunning = true;
		return true;
	}

	@Override
	protected void initialWorldChunkLoad() {
		WorldServer worldServer = this.worldServers[0];

		if (worldServer.getGameRules().getGameRuleBooleanValue("loadSpawnChunks")) {
			super.initialWorldChunkLoad();
			return;
		}

		this.setUserMessage("menu.generatingTerrain");
		logger.info("Preparing minimal start region for Eagler integrated server");
		int viewDistance = this.getConfigurationManager().getViewDistance();
		if (viewDistance < 3) viewDistance = 3;
		if (viewDistance > 12) viewDistance = 12; // Cap at 12 for performance
		
		int radiusBlocks = viewDistance * 16;
		int chunksPerAxis = (radiusBlocks * 2) / 16 + 1;
		int totalChunks = chunksPerAxis * chunksPerAxis;
		int loadedChunks = 0;
		long lastProgress = MinecraftServer.getCurrentTimeMillis();
		net.minecraft.util.ChunkCoordinates spawn = worldServer.getSpawnPoint();

		for (int x = -radiusBlocks; x <= radiusBlocks && this.isServerRunning(); x += 16) {
			for (int z = -radiusBlocks; z <= radiusBlocks && this.isServerRunning(); z += 16) {
				long now = MinecraftServer.getCurrentTimeMillis();

				if (now - lastProgress > 250L) {
					this.outputPercentRemaining("Preparing spawn area", (loadedChunks * 100) / totalChunks);
					lastProgress = now;
				}

				++loadedChunks;
				worldServer.theChunkProviderServer.loadChunk((spawn.posX + x) >> 4, (spawn.posZ + z) >> 4);
			}
		}

		List<net.minecraft.world.ChunkCoordIntPair> chunksToPopulate = new LinkedList<net.minecraft.world.ChunkCoordIntPair>();
		for (int x = -radiusBlocks; x <= radiusBlocks && this.isServerRunning(); x += 16) {
			for (int z = -radiusBlocks; z <= radiusBlocks && this.isServerRunning(); z += 16) {
				int chunkX = (spawn.posX + x) >> 4;
				int chunkZ = (spawn.posZ + z) >> 4;
				net.minecraft.world.chunk.Chunk chunk = worldServer.theChunkProviderServer.loadChunk(chunkX, chunkZ);
				if (chunk != null && !chunk.isTerrainPopulated) {
					chunksToPopulate.add(new net.minecraft.world.ChunkCoordIntPair(chunkX, chunkZ));
				}
			}
		}

		for (net.minecraft.world.ChunkCoordIntPair coord : chunksToPopulate) {
			try {
				worldServer.theChunkProviderServer.populate(worldServer.theChunkProviderServer, coord.chunkXPos, coord.chunkZPos);
			} catch (Exception ex) {
				logger.warn("Failed to populate spawn chunk at " + coord.chunkXPos + "," + coord.chunkZPos + ": "
						+ ex.getMessage());
			}
		}

		EaglerIntegratedServerWorker.sendProgress("singleplayer.busy.startingIntegratedServer", 1.0f);
		this.clearCurrentTask();
		
		// Reset currentTime after initial load to prevent massive TPS catch-up spikes
		this.currentTime = MinecraftServer.getCurrentTimeMillis();
	}

	public void mainLoop(boolean singleThreadMode) {
		long k = MinecraftServer.getCurrentTimeMillis();
		this.sendTPSToClient(k);
		if (isGamePaused) {
			currentTime = k;
			return;
		}

		long j = k - this.currentTime;
		
		// More lenient timing for TeaVM - allow up to 3 seconds lag before warning
		long maxLag = singleThreadMode ? 3000L : 2000L;
		long warningInterval = singleThreadMode ? 10000L : 15000L;
		
		if ((j > maxLag && this.currentTime - this.timeOfLastWarning >= warningInterval)) {
			logger.warn(
					"Can\'t keep up! Did the system time change, or is the server overloaded? Running {}ms behind, skipping {} tick(s)",
					new Object[] { Long.valueOf(j), Long.valueOf(j / 50L) });
			// More aggressive catch-up for TeaVM
			j = singleThreadMode ? 200L : 100L;
			this.currentTime = k - j;
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
			long tickThreshold = 50L;
			int catchUp = 0;
			while (j >= tickThreshold) {
				this.currentTime += tickThreshold;
				this.tick();
				++counterTicksPerSecond;
				j -= tickThreshold;
				if (++catchUp > 4)
					break;
			}
		}
	}

	public void updateTimeLightAndEntities() {
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
				// i += worlds[j].loadedTileEntityList.size();
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
		return (worldServers != null && worldServers[0] != null) ? worldServers[0].getWorldInfo().isMapFeaturesEnabled()
				: newWorldSettings.isMapFeaturesEnabled();
	}

	@Override
	public WorldSettings.GameType getGameType() {
		return (worldServers != null && worldServers[0] != null) ? worldServers[0].getWorldInfo().getGameType()
				: newWorldSettings.getGameType();
	}

	@Override
	public EnumDifficulty func_147135_j() {
		return difficulty;
	}

	@Override
	public EnumDifficulty getDifficulty() {
		return difficulty;
	}

	@Override
	public boolean isHardcore() {
		return (worldServers != null && worldServers[0] != null)
				? worldServers[0].getWorldInfo().isHardcoreModeEnabled()
				: newWorldSettings.getHardcoreEnabled();
	}

	@Override
	public boolean isDedicatedServer() {
		return false;
	}

	@Override
	public boolean isCommandBlockEnabled() {
		return true;
	}

	@Override
	public String shareToLAN(WorldSettings.GameType gamemode, boolean cheats) {
		return null;
	}

	@Override
	public boolean func_152363_m() {
		return false;
	}

	@Override
	public int func_110455_j() {
		return 4; 
	}

}
