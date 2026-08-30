/*
 * Copyright (c) 2023-2025 lax1dude, ayunami2000. All Rights Reserved.
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
 */

package net.lax1dude.eaglercraft.v1_8.sp.server;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.BooleanSupplier;
import net.lax1dude.eaglercraft.v1_8.EagRuntime;
import net.lax1dude.eaglercraft.v1_8.EagUtils;
import net.lax1dude.eaglercraft.v1_8.Filesystem;
import net.lax1dude.eaglercraft.v1_8.internal.IEaglerFilesystem;
import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;
import net.lax1dude.eaglercraft.v1_8.sp.server.internal.ServerPlatformSingleplayer;
import net.lax1dude.eaglercraft.v1_8.sp.server.skins.IntegratedTextureService;
import net.lax1dude.eaglercraft.v1_8.sp.server.voice.IntegratedVoiceService;
import net.minecraft.crash.CrashReport;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Bootstrap;
import net.minecraft.resources.EaglerFolderDataPackFinder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.SharedConstants;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldServerDemo;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.dimension.DimensionType;

public class EaglerMinecraftServer extends MinecraftServer {

	public static final Logger logger = EaglerIntegratedServerWorker.logger;

	public static final VFile2 savesDir = WorldsDB.newVFile("worlds");

	protected EnumDifficulty difficulty;
	protected GameType gamemode;
	protected WorldSettings newWorldSettings;
	protected boolean paused;
	protected EaglerSaveHandler saveHandler;
	protected IntegratedTextureService textureService;
	protected IntegratedVoiceService voiceService;
	protected final List<String> dataPacks;

	private long lastTPSUpdate = 0L;
	private long nextTickTime = EagRuntime.steadyTimeMillis();
	private Thread eaglerServerThread;

	public static int counterTicksPerSecond = 0;
	public static int counterChunkRead = 0;
	public static int counterChunkGenerate = 0;
	public static int counterChunkWrite = 0;
	public static int counterTileUpdate = 0;
	public static int counterLightUpdate = 0;

	public EaglerMinecraftServer(String world, String owner, int viewDistance, EaglerWorldSettings currentWorldSettings, boolean demo) {
		super(world);
		Bootstrap.register();
		this.saveHandler = new EaglerSaveHandler(savesDir, world);
		EaglerPlayerList playerList = new EaglerPlayerList(this, viewDistance);
		this.setPlayerList(playerList);
		this.textureService = new IntegratedTextureService(playerList, WorldsDB.newVFile(saveHandler.getWorldDirectory(), "eagler/skulls"));
		this.voiceService = null;
		this.setServerOwner(owner);
		logger.info("server owner: " + owner);
		this.setDemo(demo);
		this.newWorldSettings = demo ? WorldServerDemo.DEMO_WORLD_SETTINGS : (currentWorldSettings != null ? currentWorldSettings.createWorldSettings() : new EaglerWorldSettings(world, GameType.SURVIVAL.getID(), 0, "", new Random().nextLong(), true, true, false, false, null).createWorldSettings());
		this.canCreateBonusChest(this.newWorldSettings.isBonusChestEnabled());
		this.setBuildLimit(256);
		this.setOnlineMode(false);
		this.setPreventProxyConnections(false);
		this.setAllowPvp(true);
		this.setAllowFlight(true);
		this.paused = false;
		this.dataPacks = currentWorldSettings != null ? currentWorldSettings.dataPacks : Collections.emptyList();
	}

	public IntegratedTextureService getTextureService() {
		return textureService;
	}

	public IntegratedVoiceService getVoiceService() {
		return voiceService;
	}

	public void enableVoice(String[] iceServers) {
		if(iceServers != null) {
			if(voiceService != null) {
				voiceService.changeICEServers(iceServers);
			}else {
				voiceService = new IntegratedVoiceService(iceServers);
				for(EntityPlayerMP player : getPlayerList().getPlayers()) {
					voiceService.handlePlayerLoggedIn(player);
				}
			}
		}
	}

	public void setBaseServerProperties(EnumDifficulty difficulty, GameType gamemode) {
		this.difficulty = difficulty;
		this.gamemode = gamemode;
		this.setCanSpawnAnimals(true);
		this.setCanSpawnNPCs(true);
		this.setAllowPvp(true);
		this.setAllowFlight(true);
	}

	public boolean startServer() throws IOException {
		this.eaglerServerThread = Thread.currentThread();
		boolean flag = this.init();
		this.nextTickTime = EagRuntime.steadyTimeMillis();
		return flag;
	}

	@Override
	public boolean init() throws IOException {
		logger.info("Starting integrated eaglercraft server version {}", SharedConstants.getCurrentVersion().getName());
		this.setCanSpawnAnimals(true);
		this.setCanSpawnNPCs(true);
		this.setAllowPvp(true);
		this.setAllowFlight(true);
		this.copyDataPacks();
		this.loadAllWorlds(this.getFolderName(), this.getWorldName(), this.newWorldSettings.getSeed(), this.newWorldSettings.getTerrainType(), this.newWorldSettings.getGeneratorOptions());
		this.setMOTD(this.getServerOwner() + " - " + this.getWorld(DimensionType.OVERWORLD).getWorldInfo().getWorldName());
		return true;
	}

	private void copyDataPacks() {
		if(dataPacks == null || dataPacks.size() == 0) {
			return;
		}
		VFile2 dstFolder = WorldsDB.newVFile(saveHandler.getWorldDirectory(), "datapacks");
		for(int i = 0, l = dataPacks.size(); i < l; ++i) {
			String pack = dataPacks.get(i);
			if(pack == null || pack.trim().length() == 0) {
				continue;
			}
			VFile2 src = VFile2.create(Filesystem.getHandleFor(ServerPlatformSingleplayer.getClientConfigAdapter().getResourcePacksDB()), EaglerFolderDataPackFinder.DATA_PACKS, pack);
			if(!VFile2.create(Filesystem.getHandleFor(ServerPlatformSingleplayer.getClientConfigAdapter().getResourcePacksDB()), EaglerFolderDataPackFinder.DATA_PACKS, pack, "pack.mcmeta").exists()) {
				logger.warn("Datapack \"{}\" not found in the datapack folder, skipping", pack);
				continue;
			}
			VFile2 dst = WorldsDB.newVFile(dstFolder, pack);
			if(WorldsDB.newVFile(dst, "pack.mcmeta").exists()) {
				continue;
			}
			logger.info("Copying datapack \"{}\" to world datapack folder", pack);
			List<VFile2> files = src.listFiles(true);
			for(int j = 0, m = files.size(); j < m; ++j) {
				VFile2 file = files.get(j);
				byte[] bytes = file.getAllBytes();
				if(bytes == null) {
					continue;
				}
				WorldsDB.newVFile(dst, file.getPath().substring(src.getPath().length())).setAllBytes(bytes);
			}
		}
	}

	public void deleteWorldAndStopServer() {
		this.stopServer();
		logger.info("Deleting world...");
		EaglerIntegratedServerWorker.saveFormat.deleteWorldDirectory(getFolderName());
	}

	public void mainLoop(boolean singleThreadMode) {
		this.eaglerServerThread = Thread.currentThread();
		long now = EagRuntime.steadyTimeMillis();
		this.sendTPSToClient(now);
		if(paused && this.getPlayerList().getCurrentPlayerCount() <= 1) {
			nextTickTime = now + 50L;
			return;
		}
		if(now >= nextTickTime) {
			nextTickTime += 50L;
			this.tick(() -> EagRuntime.steadyTimeMillis() < nextTickTime);
			++counterTicksPerSecond;
			now = EagRuntime.steadyTimeMillis();
			if(now - nextTickTime > (singleThreadMode ? 500L : 2000L)) {
				logger.warn("Can't keep up! Server is {}ms behind", now - nextTickTime);
				nextTickTime = now + 50L;
			}
		}else if(!singleThreadMode) {
			EagUtils.sleep(1);
		}
	}

	@Override
	public void tick(BooleanSupplier hasTimeLeft) {
		if(this.textureService != null) {
			this.textureService.flushCache();
		}
		super.tick(hasTimeLeft);
	}

	@Override
	public boolean isCallingFromMinecraftThread() {
		return Thread.currentThread() == this.eaglerServerThread;
	}

	@Override
	public Thread getServerThread() {
		return this.eaglerServerThread;
	}

	protected void sendTPSToClient(long millis) {
		if(millis - lastTPSUpdate > 1000L) {
			lastTPSUpdate = millis;
			if(isServerRunning()) {
				Iterable<WorldServer> worlds = this.getWorlds();
				List<String> lst = Lists.newArrayList(
						"TPS: " + counterTicksPerSecond + "/20",
						"Chunks: " + countChunksLoaded(worlds) + "/" + countChunksTotal(worlds, this.getPlayerList().getViewDistance()),
						"Entities: " + countEntities(worlds) + "+" + countTileEntities(worlds),
						"R: " + counterChunkRead + ", G: " + counterChunkGenerate + ", W: " + counterChunkWrite,
						"TU: " + counterTileUpdate + ", LU: " + counterLightUpdate
				);
				int players = countPlayerEntities(worlds);
				if(players > 1) {
					lst.add("Players: " + players);
				}
				counterTicksPerSecond = counterChunkRead = counterChunkGenerate = 0;
				counterChunkWrite = counterTileUpdate = counterLightUpdate = 0;
				EaglerIntegratedServerWorker.reportTPS(lst);
			}
		}
	}

	private static int countChunksLoaded(Iterable<WorldServer> worlds) {
		int i = 0;
		for(WorldServer world : worlds) {
			if(world != null) {
				i += world.getChunkProvider().getLoadedChunkCount();
			}
		}
		return i;
	}

	private static int countChunksTotal(Iterable<WorldServer> worlds, int viewDistance) {
		int i = 0;
		int radius = Math.max(1, viewDistance);
		for(WorldServer world : worlds) {
			if(world != null) {
				i += world.playerEntities.size() * (radius * 2 + 1) * (radius * 2 + 1);
				i += world.getChunkProvider().getLoadedChunkCount();
			}
		}
		return i;
	}

	private static int countEntities(Iterable<WorldServer> worlds) {
		int i = 0;
		for(WorldServer world : worlds) {
			if(world != null) {
				i += world.loadedEntityList.size();
			}
		}
		return i;
	}

	private static int countTileEntities(Iterable<WorldServer> worlds) {
		int i = 0;
		for(WorldServer world : worlds) {
			if(world != null) {
				i += world.loadedTileEntityList.size();
			}
		}
		return i;
	}

	private static int countPlayerEntities(Iterable<WorldServer> worlds) {
		int i = 0;
		for(WorldServer world : worlds) {
			if(world != null) {
				i += world.playerEntities.size();
			}
		}
		return i;
	}

	public void setPaused(boolean p) {
		paused = p;
		if(!p) {
			nextTickTime = EagRuntime.steadyTimeMillis();
		}
	}

	public boolean getPaused() {
		return paused;
	}

	@Override
	public boolean canStructuresSpawn() {
		WorldServer world = this.getWorld(DimensionType.OVERWORLD);
		return world != null ? world.getWorldInfo().isMapFeaturesEnabled() : newWorldSettings.isMapFeaturesEnabled();
	}

	@Override
	public GameType getGameType() {
		WorldServer world = this.getWorld(DimensionType.OVERWORLD);
		return world != null ? world.getWorldInfo().getGameType() : newWorldSettings.getGameType();
	}

	@Override
	public EnumDifficulty getDifficulty() {
		return difficulty != null ? difficulty : EnumDifficulty.NORMAL;
	}

	@Override
	public boolean isHardcore() {
		WorldServer world = this.getWorld(DimensionType.OVERWORLD);
		return world != null ? world.getWorldInfo().isHardcore() : newWorldSettings.getHardcoreEnabled();
	}

	@Override
	public int getOpPermissionLevel() {
		return 4;
	}

	@Override
	public boolean allowLoggingRcon() {
		return true;
	}

	@Override
	public boolean allowLogging() {
		return true;
	}

	@Override
	public boolean isDedicatedServer() {
		return false;
	}

	@Override
	public boolean shouldUseNativeTransport() {
		return false;
	}

	@Override
	public boolean isCommandBlockEnabled() {
		return true;
	}

	@Override
	public boolean getPublic() {
		return true;
	}

	@Override
	public boolean shareToLAN(GameType gameMode, boolean cheats, int port) {
		return configureLAN(gameMode, cheats) != null;
	}

	public String configureLAN(GameType gameType, boolean allowCommands) {
		this.setGameType(gameType);
		this.getPlayerList().setGameType(gameType);
		this.getPlayerList().setCommandsAllowedForAll(allowCommands);
		for(EntityPlayerMP player : this.getPlayerList().getPlayers()) {
			this.getCommandManager().send(player);
		}
		return "local";
	}

	public void saveAllChunks(boolean isSilent, boolean flush, boolean skip) {
		this.saveAllWorlds(isSilent);
	}

	@Override
	public CrashReport addServerInfoToCrashReport(CrashReport report) {
		report = super.addServerInfoToCrashReport(report);
		report.getCategory().addDetail("Type", "Eaglercraft Integrated Server");
		report.getCategory().addDetail("Is Modded", () -> "Eaglercraft integrated server");
		return report;
	}

	@Override
	public boolean isSnooperEnabled() {
		return false;
	}

	public boolean isSingleplayerOwner(GameProfile profile) {
		return profile.getName().equalsIgnoreCase(this.getServerOwner());
	}
}
