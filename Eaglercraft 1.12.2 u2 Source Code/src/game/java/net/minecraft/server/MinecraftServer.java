package net.minecraft.server;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import net.lax1dude.eaglercraft.EagRuntime;
import net.lax1dude.eaglercraft.EaglercraftUUID;
import net.lax1dude.eaglercraft.futures.FutureTask;
import net.lax1dude.eaglercraft.internal.vfs2.VFile2;
import net.lax1dude.eaglercraft.sp.server.EaglerIntegratedServerWorker;
import net.lax1dude.eaglercraft.sp.server.EaglerSaveFormat;
import net.minecraft.advancements.AdvancementManager;
import net.minecraft.advancements.FunctionManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandManager;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.ICrashReportDetail;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Bootstrap;
import net.minecraft.network.ServerStatusResponse;
import net.minecraft.network.play.server.SPacketTimeUpdate;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.ITickable;
import net.minecraft.util.ReportedException;
import net.minecraft.util.Util;
import net.minecraft.util.datafix.DataFixesManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.GameType;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.ServerWorldEventHandler;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldServerMulti;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class MinecraftServer implements ICommandSender {
	private static final Logger LOG = LogManager.getLogger();
	protected final ISaveFormat anvilConverterForAnvilFile;

	private final VFile2 anvilFile;
	private final List<ITickable> tickables = Lists.<ITickable>newArrayList();
	public final ICommandManager commandManager;
	private final ServerStatusResponse statusResponse = new ServerStatusResponse();

	/** The server world instances. */
	public WorldServer[] worldServers;

	/** The player list for this server */
	private ServerConfigurationManager playerList;

	/**
	 * Indicates whether the server is running or not. Set to false to initiate a
	 * shutdown.
	 */
	protected boolean serverRunning = false;

	/** Indicates to other classes that the server is safely stopped. */
	private boolean serverStopped;

	/** Incremented every tick. */
	private int tickCounter;

	/**
	 * The task the server is currently working on(and will output on
	 * outputPercentRemaining).
	 */
	public String currentTask;

	/** The percentage of the current task finished so far. */
	public int percentDone;

	/** True if the server is in online mode. */
	private boolean onlineMode;
	private boolean field_190519_A;

	/** True if the server has animals turned on. */
	private boolean canSpawnAnimals;
	private boolean canSpawnNPCs;

	/** Indicates whether PvP is active on the server or not. */
	private boolean pvpEnabled;

	/** Determines if flight is allowed or not. */
	private boolean allowFlight;

	/** The server MOTD string. */
	private String motd;

	/** Maximum build height. */
	private int buildLimit;
	private int maxPlayerIdleMinutes;
	public final long[] tickTimeArray = new long[100];

	/** Stats are [dimension][tick%100] system.nanoTime is stored. */
	public long[][] timeOfLastDimensionTick;

	/** Username of the server owner (for integrated servers) */
	private String serverOwner;
	private String folderName;
	private String worldName;
	private boolean isDemo;

	/** The texture pack for the server */
	private String resourcePackUrl = "";
	private String resourcePackHash = "";
	private boolean serverIsRunning;

	/**
	 * Set when warned for "Can't keep up", which triggers again after 15 seconds.
	 */
	protected long timeOfLastWarning;
	private String userMessage;
	private boolean startProfiling;
	private boolean isGamemodeForced;
	public final List<FutureTask<?>> futureTaskQueue = new LinkedList<FutureTask<?>>();
	protected long currentTime = getCurrentTimeMillis();
	private static MinecraftServer server;

	protected boolean isGamePaused;

	public MinecraftServer(VFile2 anvilFileIn) {
		this.anvilFile = anvilFileIn;
		this.commandManager = this.createNewCommandManager();
		Bootstrap.register();
		Bootstrap.register2();

		this.anvilConverterForAnvilFile = new EaglerSaveFormat(anvilFileIn, DataFixesManager.createFixer());;
		server = this;
	}

	public ServerCommandManager createNewCommandManager() {
		return new ServerCommandManager(this);
	}

	/**
	 * Initialises the server and starts it.
	 */
	public abstract boolean startServer() throws IOException;

	/**
	 * Typically "menu.convertingLevel", "menu.loadingLevel" or others.
	 */
	protected synchronized void setUserMessage(String message) {
		this.userMessage = message;
	}

	public synchronized String getUserMessage() {
		return this.userMessage;
	}

	public void loadAllWorlds(String saveName, String worldNameIn, WorldSettings worldsettings) {
		this.setUserMessage("menu.loadingLevel");
		this.worldServers = new WorldServer[3];
		this.timeOfLastDimensionTick = new long[this.worldServers.length][100];
		ISaveHandler isavehandler = this.anvilConverterForAnvilFile.getSaveLoader(saveName, true);
		WorldInfo worldinfo = isavehandler.loadWorldInfo();
		if (worldinfo == null) {
			worldinfo = new WorldInfo(worldsettings, worldNameIn);
		} else {
			worldinfo.setWorldName(worldNameIn);
			worldsettings = new WorldSettings(worldinfo);
		}
		
		if (worldinfo.isOldEaglercraftRandom()) {
			LogManager.getLogger("EaglerMinecraftServer")
					.info("Detected a pre-u34 world, using old EaglercraftRandom implementation for world generation");
		}

		for (int j = 0; j < this.worldServers.length; ++j) {
			byte b0 = 0;
			if (j == 1) {
				b0 = -1;
			}

			if (j == 2) {
				b0 = 1;
			}

			if (j == 0) {
				this.worldServers[j] = (WorldServer) (new WorldServer(this, isavehandler, worldinfo, b0)).init();
				this.worldServers[j].initialize(worldsettings);
			} else {
				this.worldServers[j] = (WorldServer) (new WorldServerMulti(this, isavehandler, b0,
						this.worldServers[0])).init();
			}

			this.worldServers[j].addEventListener(new ServerWorldEventHandler(this, this.worldServers[j]));
		}

		this.playerList.setPlayerManager(this.worldServers);
		if (this.worldServers[0].getWorldInfo().getDifficulty() == null) {
			this.setDifficultyForAllWorlds(this.getDifficulty());
		}
		this.initialWorldChunkLoad();
	}

	public void initialWorldChunkLoad() {
		int i1 = 0;
		this.setUserMessage("menu.generatingTerrain");
		LOG.info("Preparing start region for level 0");

		WorldServer worldserver = this.worldServers[0];
		BlockPos blockpos = worldserver.getSpawnPoint();
		long k1 = getCurrentTimeMillis();

		for (int l1 = -192; l1 <= 192 && this.isServerRunning(); l1 += 16) {
			for (int i2 = -192; i2 <= 192 && this.isServerRunning(); i2 += 16) {
				long j2 = getCurrentTimeMillis();

				if (j2 - k1 > 1000L) {
					this.outputPercentRemaining("Preparing spawn area", i1 * 100 / 625);
					k1 = j2;
				}

				++i1;
				worldserver.getChunkProvider().provideChunk(blockpos.getX() + l1 >> 4, blockpos.getZ() + i2 >> 4);
			}
		}

		this.clearCurrentTask();
	}

	public abstract boolean canStructuresSpawn();

	public abstract GameType getGameType();

	/**
	 * Get the server's difficulty
	 */
	public abstract EnumDifficulty getDifficulty();

	/**
	 * Defaults to false.
	 */
	public abstract boolean isHardcore();

	public abstract int getOpPermissionLevel();

	/**
	 * Get if RCON command events should be broadcast to ops
	 */
	public abstract boolean shouldBroadcastRconToOps();

	/**
	 * Get if console command events should be broadcast to ops
	 */
	public abstract boolean shouldBroadcastConsoleToOps();

	/**
	 * Used to display a percent remaining given text and the percentage.
	 */
	protected void outputPercentRemaining(String parString1, int parInt1) {
		this.currentTask = parString1;
		this.percentDone = parInt1;
		LOG.info(parString1 + ": " + parInt1 + "%");
		EaglerIntegratedServerWorker.sendProgress("singleplayer.busy.startingIntegratedServer", parInt1 * 0.01f);
	}

	/**
	 * Set current task to null and set its percentage to 0.
	 */
	protected void clearCurrentTask() {
		this.currentTask = null;
		this.percentDone = 0;
	}

	/**
	 * par1 indicates if a log message should be output.
	 */
	public void saveAllWorlds(boolean isSilent) {
		for (WorldServer worldserver : this.worldServers) {
			if (worldserver != null) {
				if (!isSilent) {
					LOG.info("Saving chunks for level '{}'/{}", worldserver.getWorldInfo().getWorldName(),
							worldserver.provider.getDimensionType().getName());
				}

				try {
					worldserver.saveAllChunks(true, (IProgressUpdate) null);
				} catch (MinecraftException minecraftexception) {
					LOG.warn(minecraftexception.getMessage());
				}
			}
		}
	}

	/**
	 * Saves all necessary data as preparation for stopping the server.
	 */
	public void stopServer() {
		LOG.info("Stopping server");

		if (this.playerList != null) {
			LOG.info("Saving players");
			this.playerList.saveAllPlayerData();
			this.playerList.removeAllPlayers();
		}

		if (this.worldServers != null) {
			LOG.info("Saving worlds");

			for (WorldServer worldserver : this.worldServers) {
				if (worldserver != null) {
					worldserver.disableLevelSaving = false;
				}
			}

			this.saveAllWorlds(false);

			for (WorldServer worldserver1 : this.worldServers) {
				if (worldserver1 != null) {
					worldserver1.flush();
				}
			}
		}
	}

	public boolean isServerRunning() {
		return this.serverRunning;
	}

	/**
	 * Sets the serverRunning variable to false, in order to get the server to shut
	 * down.
	 */
	public void initiateShutdown() {
		this.serverRunning = false;
	}

	/**
	 * Main function called by run() every loop.
	 */
	public void tick() {
		long i = EagRuntime.nanoTime();
		++this.tickCounter;

		if (this.startProfiling) {
			this.startProfiling = false;
		}

		this.updateTimeLightAndEntities();

		if (this.tickCounter % 900 == 0) {
			this.playerList.saveAllPlayerData();
			this.saveAllWorlds(true);
		}

		this.tickTimeArray[this.tickCounter % 100] = EagRuntime.nanoTime() - i;
	}

	public void updateTimeLightAndEntities() {
		synchronized (this.futureTaskQueue) {
			while (!this.futureTaskQueue.isEmpty()) {
				Util.runTask(this.futureTaskQueue.remove(0), LOG);
			}
		}

		for (int j = 0; j < this.worldServers.length; ++j) {
			long i = EagRuntime.nanoTime();

			if (j == 0 || this.getAllowNether()) {
				WorldServer worldserver = this.worldServers[j];

				if (this.tickCounter % 20 == 0) {
					this.playerList.sendPacketToAllPlayersInDimension(
							new SPacketTimeUpdate(worldserver.getTotalWorldTime(), worldserver.getWorldTime(),
									worldserver.getGameRules().getBoolean("doDaylightCycle")),
							worldserver.provider.getDimensionType().getId());
				}

				try {
					worldserver.tick();
				} catch (Throwable throwable1) {
					CrashReport crashreport = CrashReport.makeCrashReport(throwable1, "Exception ticking world");
					worldserver.addWorldInfoToCrashReport(crashreport);
					throw new ReportedException(crashreport);
				}

				try {
					worldserver.updateEntities();
				} catch (Throwable throwable) {
					CrashReport crashreport1 = CrashReport.makeCrashReport(throwable,
							"Exception ticking world entities");
					worldserver.addWorldInfoToCrashReport(crashreport1);
					throw new ReportedException(crashreport1);
				}

				worldserver.getEntityTracker().updateTrackedEntities();
			}

			this.timeOfLastDimensionTick[j][this.tickCounter % 100] = EagRuntime.nanoTime() - i;
		}

		EaglerIntegratedServerWorker.tick();
		this.playerList.onTick();
		this.func_193030_aL().update();

		for (int k = 0; k < this.tickables.size(); ++k) {
			((ITickable) this.tickables.get(k)).update();
		}
	}

	public boolean getAllowNether() {
		return true;
	}

	/**
	 * Logs the message with a level of WARN.
	 */
	public void logWarning(String msg) {
		LOG.warn(msg);
	}

	/**
	 * Gets the worldServer by the given dimension.
	 */
	public WorldServer worldServerForDimension(int dimension) {
		if (dimension == -1) {
			return this.worldServers[1];
		} else {
			return dimension == 1 ? this.worldServers[2] : this.worldServers[0];
		}
	}

	/**
	 * Returns the server's Minecraft version as string.
	 */
	public String getMinecraftVersion() {
		return "1.12.2";
	}

	/**
	 * Returns the number of players currently on the server.
	 */
	public int getCurrentPlayerCount() {
		return this.playerList.getCurrentPlayerCount();
	}

	/**
	 * Returns the maximum number of players allowed on the server.
	 */
	public int getMaxPlayers() {
		return this.playerList.getMaxPlayers();
	}

	/**
	 * Returns an array of the usernames of all the connected players.
	 */
	public String[] getAllUsernames() {
		return this.playerList.getAllUsernames();
	}

	/**
	 * Returns an array of the GameProfiles of all the connected players
	 */
	public GameProfile[] getGameProfiles() {
		return this.playerList.getAllProfiles();
	}

	public String getServerModName() {
		return "vanilla";
	}

	/**
	 * Adds the server info, including from theWorldServer, to the crash report.
	 */
	public CrashReport addServerInfoToCrashReport(CrashReport report) {
		report.getCategory().setDetail("Profiler Position", new ICrashReportDetail<String>() {
			public String call() throws Exception {
				return "N/A (disabled)";
			}
		});

		if (this.playerList != null) {
			report.getCategory().setDetail("Player Count", new ICrashReportDetail<String>() {
				public String call() {
					return MinecraftServer.this.playerList.getCurrentPlayerCount() + " / "
							+ MinecraftServer.this.playerList.getMaxPlayers() + "; "
							+ MinecraftServer.this.playerList.getPlayerList();
				}
			});
		}

		return report;
	}

	public List<String> getTabCompletions(ICommandSender sender, String input, BlockPos pos, boolean hasTargetBlock) {
		List<String> list = Lists.<String>newArrayList();
		boolean flag = input.startsWith("/");

		if (flag) {
			input = input.substring(1);
		}

		if (!flag && !hasTargetBlock) {
			String[] astring = input.split(" ", -1);
			String s2 = astring[astring.length - 1];

			for (String s1 : this.playerList.getAllUsernames()) {
				if (CommandBase.doesStringStartWith(s2, s1)) {
					list.add(s1);
				}
			}

			return list;
		} else {
			boolean flag1 = !input.contains(" ");
			List<String> list1 = this.commandManager.getTabCompletionOptions(sender, input, pos);

			if (!list1.isEmpty()) {
				for (String s : list1) {
					if (flag1 && !hasTargetBlock) {
						list.add("/" + s);
					} else {
						list.add(s);
					}
				}
			}

			return list;
		}
	}

	public boolean isAnvilFileSet() {
		return this.anvilFile != null;
	}

	/**
	 * Get the name of this object. For players this returns their username
	 */
	public String getName() {
		return "Server";
	}

	/**
	 * Send a chat message to the CommandSender
	 */
	public void addChatMessage(ITextComponent component) {
		LOG.info(component.getUnformattedText());
	}

	/**
	 * Returns {@code true} if the CommandSender is allowed to execute the command,
	 * {@code false} if not
	 */
	public boolean canCommandSenderUseCommand(int permLevel, String commandName) {
		return true;
	}

	public ICommandManager getCommandManager() {
		return this.commandManager;
	}

	/**
	 * Returns the username of the server owner (for integrated servers)
	 */
	public String getServerOwner() {
		return this.serverOwner;
	}

	/**
	 * Sets the username of the owner of this server (in the case of an integrated
	 * server)
	 */
	public void setServerOwner(String owner) {
		this.serverOwner = owner;
	}

	public boolean isSinglePlayer() {
		return true;
	}

	public String getFolderName() {
		return this.folderName;
	}

	public void setFolderName(String name) {
		this.folderName = name;
	}

	public void setWorldName(String worldNameIn) {
		this.worldName = worldNameIn;
	}

	public String getWorldName() {
		return this.worldName;
	}

	public void setDifficultyForAllWorlds(EnumDifficulty difficulty) {
		for (WorldServer worldserver1 : this.worldServers) {
			if (worldserver1 != null) {
				if (worldserver1.getWorldInfo().isHardcoreModeEnabled()) {
					worldserver1.getWorldInfo().setDifficulty(EnumDifficulty.HARD);
					worldserver1.setAllowedSpawnTypes(true, true);
				} else if (this.isSinglePlayer()) {
					worldserver1.getWorldInfo().setDifficulty(difficulty);
					worldserver1.setAllowedSpawnTypes(worldserver1.getDifficulty() != EnumDifficulty.PEACEFUL, true);
				} else {
					worldserver1.getWorldInfo().setDifficulty(difficulty);
					worldserver1.setAllowedSpawnTypes(this.allowSpawnMonsters(), this.canSpawnAnimals);
				}
			}
		}
	}

	public void setDifficultyLockedForAllWorlds(boolean locked) {
		for (int i = 0; i < this.worldServers.length; ++i) {
			WorldServer worldserver = this.worldServers[i];
			if (worldserver != null) {
				worldserver.getWorldInfo().setDifficultyLocked(locked);
			}
		}
	}

	public boolean allowSpawnMonsters() {
		return true;
	}

	/**
	 * Gets whether this is a demo or not.
	 */
	public boolean isDemo() {
		return this.isDemo;
	}

	/**
	 * Sets whether this is a demo or not.
	 */
	public void setDemo(boolean demo) {
		this.isDemo = demo;
	}

	public ISaveFormat getActiveAnvilConverter() {
		return this.anvilConverterForAnvilFile;
	}

	public String getResourcePackUrl() {
		return this.resourcePackUrl;
	}

	public String getResourcePackHash() {
		return this.resourcePackHash;
	}

	public void setResourcePack(String url, String hash) {
		this.resourcePackUrl = url;
		this.resourcePackHash = hash;
	}

	public abstract boolean isDedicatedServer();

	public boolean isServerInOnlineMode() {
		return this.onlineMode;
	}

	public void setOnlineMode(boolean online) {
		this.onlineMode = online;
	}

	public boolean func_190518_ac() {
		return this.field_190519_A;
	}

	public boolean getCanSpawnAnimals() {
		return this.canSpawnAnimals;
	}

	public void setCanSpawnAnimals(boolean spawnAnimals) {
		this.canSpawnAnimals = spawnAnimals;
	}

	public boolean getCanSpawnNPCs() {
		return this.canSpawnNPCs;
	}

	public void setCanSpawnNPCs(boolean spawnNpcs) {
		this.canSpawnNPCs = spawnNpcs;
	}

	public boolean isPVPEnabled() {
		return this.pvpEnabled;
	}

	public void setAllowPvp(boolean allowPvp) {
		this.pvpEnabled = allowPvp;
	}

	public boolean isFlightAllowed() {
		return this.allowFlight;
	}

	public void setAllowFlight(boolean allow) {
		this.allowFlight = allow;
	}

	/**
	 * Return whether command blocks are enabled.
	 */
	public abstract boolean isCommandBlockEnabled();

	public String getMOTD() {
		return this.motd;
	}

	public void setMOTD(String motdIn) {
		this.motd = motdIn;
	}

	public int getBuildLimit() {
		return this.buildLimit;
	}

	public void setBuildLimit(int maxBuildHeight) {
		this.buildLimit = maxBuildHeight;
	}

	public boolean isServerStopped() {
		return this.serverStopped;
	}

	public ServerConfigurationManager getPlayerList() {
		return this.playerList;
	}

	public void setPlayerList(ServerConfigurationManager list) {
		this.playerList = list;
	}

	public ServerConfigurationManager getConfigurationManager() {
		return this.playerList;
	}

	public void setConfigManager(ServerConfigurationManager configManager) {
		this.playerList = configManager;
	}

	/**
	 * Sets the game type for all worlds.
	 */
	public void setGameType(GameType gameMode) {
		for (WorldServer worldserver1 : this.worldServers) {
			worldserver1.getWorldInfo().setGameType(gameMode);
		}
	}

	public boolean serverIsInRunLoop() {
		return this.serverIsRunning;
	}

	public boolean getGuiEnabled() {
		return false;
	}

	public int getTickCounter() {
		return this.tickCounter;
	}

	public void enableProfiling() {
		this.startProfiling = true;
	}

	/**
	 * Get the world, if available. <b>{@code null} is not allowed!</b> If you are
	 * not an entity in the world, return the overworld
	 */
	public World getEntityWorld() {
		return this.worldServers[0];
	}

	public boolean isBlockProtected(World worldIn, BlockPos pos, EntityPlayer playerIn) {
		return false;
	}

	/**
	 * Get the forceGamemode field (whether joining players will be put in their old
	 * gamemode or the default one)
	 */
	public boolean getForceGamemode() {
		return this.isGamemodeForced;
	}

	public static long getCurrentTimeMillis() {
		return EagRuntime.steadyTimeMillis();
	}

	public int getMaxPlayerIdleMinutes() {
		return this.maxPlayerIdleMinutes;
	}

	public void setPlayerIdleTimeout(int idleTimeout) {
		this.maxPlayerIdleMinutes = idleTimeout;
	}

	public ServerStatusResponse getServerStatusResponse() {
		return this.statusResponse;
	}

	public Entity getEntityFromUuid(EaglercraftUUID uuid) {
		for (int i = 0; i < this.worldServers.length; ++i) {
			WorldServer worldserver1 = this.worldServers[i];
			if (worldserver1 != null) {
				Entity entity = worldserver1.getEntityFromUuid(uuid);

				if (entity != null) {
					return entity;
				}
			}
		}

		return null;
	}

	/**
	 * Returns true if the command sender should be sent feedback about executed
	 * commands
	 */
	public boolean sendCommandFeedback() {
		return this.worldServers[0].getGameRules().getBoolean("sendCommandFeedback");
	}

	/**
	 * Get the Minecraft server instance
	 */
	public MinecraftServer getServer() {
		return this;
	}

	public int getMaxWorldSize() {
		return 29999984;
	}

	public void addScheduledTask(Runnable runnableToSchedule) {
		Validate.notNull(runnableToSchedule);
		runnableToSchedule.run();
	}

	/**
	 * The compression treshold. If the packet is larger than the specified amount
	 * of bytes, it will be compressed
	 */
	public int getNetworkCompressionThreshold() {
		return 256;
	}

	public int getSpawnRadius(WorldServer worldIn) {
		return worldIn != null ? worldIn.getGameRules().getInt("spawnRadius") : 10;
	}

	public AdvancementManager func_191949_aK() {
		return this.worldServers[0].func_191952_z();
	}

	public FunctionManager func_193030_aL() {
		return this.worldServers[0].func_193037_A();
	}

	public static MinecraftServer getMinecraftServer() {
		return server;
	}

	public void func_193031_aM() {
		this.getPlayerList().saveAllPlayerData();
		this.worldServers[0].getLootTableManager().reloadLootTables();
		this.func_191949_aK().func_192779_a();
		this.func_193030_aL().func_193059_f();
		this.getPlayerList().func_193244_w();
	}
}