package net.minecraft.server.integrated;

import com.google.common.collect.Lists;
import net.lax1dude.eaglercraft.v1_8.futures.Futures;
import com.google.gson.JsonElement;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.UUID;
import java.util.function.BooleanSupplier;
import net.lax1dude.eaglercraft.v1_8.futures.FutureTask;
import net.lax1dude.eaglercraft.v1_8.sp.server.WorldsDB;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ThreadLanServerPing;
import net.minecraft.command.Commands;
import net.minecraft.crash.CrashReport;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.profiler.Snooper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.util.Util;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldServerDemo;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.minecraft.world.storage.WorldSavedDataStorage;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class IntegratedServer extends MinecraftServer {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Minecraft mc;
   private final WorldSettings worldSettings;
   private boolean isGamePaused;
   private int serverPort = -1;
   private ThreadLanServerPing lanServerPing;
   private UUID playerUuid;

   public IntegratedServer(Minecraft clientIn, String folderNameIn, String worldNameIn, WorldSettings worldSettingsIn, YggdrasilAuthenticationService authServiceIn, MinecraftSessionService sessionServiceIn, GameProfileRepository profileRepoIn, PlayerProfileCache profileCacheIn) {
      super(WorldsDB.newVFile("worlds"), clientIn.getProxy(), clientIn.getDataFixer(), new Commands(false), authServiceIn, sessionServiceIn, profileRepoIn, profileCacheIn);
      this.setServerOwner(clientIn.getSession().getUsername());
      this.setFolderName(folderNameIn);
      this.setWorldName(worldNameIn);
      this.setDemo(clientIn.isDemo());
      this.canCreateBonusChest(worldSettingsIn.isBonusChestEnabled());
      this.setBuildLimit(256);
      this.setPlayerList(new IntegratedPlayerList(this));
      this.mc = clientIn;
      this.worldSettings = this.isDemo() ? WorldServerDemo.DEMO_WORLD_SETTINGS : worldSettingsIn;
   }

   public void loadAllWorlds(String saveName, String worldNameIn, long seed, WorldType type, JsonElement generatorOptions) {
      this.convertMapIfNeeded(saveName);
      ISaveHandler isavehandler = this.getActiveAnvilConverter().getSaveLoader(saveName, this);
      this.setResourcePackFromWorld(this.getFolderName(), isavehandler);
      WorldInfo worldinfo = isavehandler.loadWorldInfo();
      if (worldinfo == null) {
         worldinfo = new WorldInfo(this.worldSettings, worldNameIn);
      } else {
         worldinfo.setWorldName(worldNameIn);
      }

      this.loadDataPacks(isavehandler.getWorldDirectory(), worldinfo);
      WorldSavedDataStorage worldsaveddatastorage = new WorldSavedDataStorage(isavehandler);
      this.func_212369_a(isavehandler, worldsaveddatastorage, worldinfo, this.worldSettings);
      if (this.getWorld(DimensionType.OVERWORLD).getWorldInfo().getDifficulty() == null) {
         this.setDifficultyForAllWorlds(this.mc.gameSettings.difficulty);
      }

      this.initialWorldChunkLoad(worldsaveddatastorage);
   }

   public boolean init() throws IOException {
      LOGGER.info("Starting integrated minecraft server version 1.13.2");
      this.setOnlineMode(true);
      this.setCanSpawnAnimals(true);
      this.setCanSpawnNPCs(true);
      this.setAllowPvp(true);
      this.setAllowFlight(true);
      this.loadAllWorlds(this.getFolderName(), this.getWorldName(), this.worldSettings.getSeed(), this.worldSettings.getTerrainType(), this.worldSettings.getGeneratorOptions());
      this.setMOTD(this.getServerOwner() + " - " + this.getWorld(DimensionType.OVERWORLD).getWorldInfo().getWorldName());
      return true;
   }

   public void tick(BooleanSupplier hasTimeLeft) {
      boolean flag = this.isGamePaused;
      this.isGamePaused = Minecraft.getInstance().getConnection() != null && Minecraft.getInstance().isGamePaused();
      if (!flag && this.isGamePaused) {
         LOGGER.info("Saving and pausing game...");
         this.getPlayerList().saveAllPlayerData();
         this.saveAllWorlds(false);
      }

      FutureTask<?> futuretask;
      if (this.isGamePaused) {
         while((futuretask = this.futureTaskQueue.poll()) != null) {
            Util.runTask(futuretask, LOGGER);
         }
      } else {
         super.tick(hasTimeLeft);
         if (this.mc.gameSettings.renderDistanceChunks != this.getPlayerList().getViewDistance()) {
            LOGGER.info("Changing view distance to {}, from {}", this.mc.gameSettings.renderDistanceChunks, this.getPlayerList().getViewDistance());
            this.getPlayerList().setViewDistance(this.mc.gameSettings.renderDistanceChunks);
         }

         if (this.mc.world != null) {
            WorldInfo worldinfo = this.getWorld(DimensionType.OVERWORLD).getWorldInfo();
            WorldInfo worldinfo1 = this.mc.world.getWorldInfo();
            if (!worldinfo.isDifficultyLocked() && worldinfo1.getDifficulty() != worldinfo.getDifficulty()) {
               LOGGER.info("Changing difficulty to {}, from {}", worldinfo1.getDifficulty(), worldinfo.getDifficulty());
               this.setDifficultyForAllWorlds(worldinfo1.getDifficulty());
            } else if (worldinfo1.isDifficultyLocked() && !worldinfo.isDifficultyLocked()) {
               LOGGER.info("Locking difficulty to {}", (Object)worldinfo1.getDifficulty());

               for(WorldServer worldserver : this.getWorlds()) {
                  if (worldserver != null) {
                     worldserver.getWorldInfo().setDifficultyLocked(true);
                  }
               }
            }
         }
      }

   }

   public boolean canStructuresSpawn() {
      return false;
   }

   public GameType getGameType() {
      return this.worldSettings.getGameType();
   }

   public EnumDifficulty getDifficulty() {
      return this.mc.world.getWorldInfo().getDifficulty();
   }

   public boolean isHardcore() {
      return this.worldSettings.getHardcoreEnabled();
   }

   public boolean allowLoggingRcon() {
      return true;
   }

   public boolean allowLogging() {
      return true;
   }

   public File getDataDirectory() {
      return this.mc.gameDir;
   }

   public boolean isDedicatedServer() {
      return false;
   }

   public boolean shouldUseNativeTransport() {
      return false;
   }

   public void finalTick(CrashReport report) {
      this.mc.crashed(report);
   }

   public CrashReport addServerInfoToCrashReport(CrashReport report) {
      report = super.addServerInfoToCrashReport(report);
      report.getCategory().addDetail("Type", "Integrated Server (map_client.txt)");
      report.getCategory().addDetail("Is Modded", () -> {
         String s = ClientBrandRetriever.getClientModName();
         if (!s.equals("vanilla")) {
            return "Definitely; Client brand changed to '" + s + "'";
         } else {
            s = this.getServerModName();
            if (!"vanilla".equals(s)) {
               return "Definitely; Server brand changed to '" + s + "'";
            } else {
               return Minecraft.class.getSigners() == null ? "Very likely; Jar signature invalidated" : "Probably not. Jar signature remains and both client + server brands are untouched.";
            }
         }
      });
      return report;
   }

   public void setDifficultyForAllWorlds(EnumDifficulty difficulty) {
      super.setDifficultyForAllWorlds(difficulty);
      if (this.mc.world != null) {
         this.mc.world.getWorldInfo().setDifficulty(difficulty);
      }

   }

   public void fillSnooper(Snooper snooper) {
      super.fillSnooper(snooper);
      snooper.addClientStat("snooper_partner", this.mc.getSnooper().getUniqueID());
   }

   public boolean isSnooperEnabled() {
      return Minecraft.getInstance().isSnooperEnabled();
   }

   public boolean shareToLAN(GameType gameMode, boolean cheats, int port) {
      try {
         this.getNetworkSystem().addEndpoint((InetAddress)null, port);
         LOGGER.info("Started serving on {}", (int)port);
         this.serverPort = port;
         this.lanServerPing = new ThreadLanServerPing(this.getMOTD(), port + "");
         this.lanServerPing.start();
         this.getPlayerList().setGameType(gameMode);
         this.getPlayerList().setCommandsAllowedForAll(cheats);
         int i = this.getPermissionLevel(this.mc.player.getGameProfile());
         this.mc.player.setPermissionLevel(i);

         for(EntityPlayerMP entityplayermp : this.getPlayerList().getPlayers()) {
            this.getCommandManager().send(entityplayermp);
         }

         return true;
      } catch (IOException var7) {
         return false;
      }
   }

   public void stopServer() {
      super.stopServer();
      if (this.lanServerPing != null) {
         this.lanServerPing.interrupt();
         this.lanServerPing = null;
      }

   }

   public void initiateShutdown() {
      Futures.getUnchecked(this.addScheduledTask(() -> {
         for(EntityPlayerMP entityplayermp : Lists.newArrayList(this.getPlayerList().getPlayers())) {
            if (!entityplayermp.getUniqueID().equals(this.playerUuid)) {
               this.getPlayerList().playerLoggedOut(entityplayermp);
            }
         }

      }));
      super.initiateShutdown();
      if (this.lanServerPing != null) {
         this.lanServerPing.interrupt();
         this.lanServerPing = null;
      }

   }

   public boolean getPublic() {
      return this.serverPort > -1;
   }

   public int getServerPort() {
      return this.serverPort;
   }

   public void setGameType(GameType gameMode) {
      super.setGameType(gameMode);
      this.getPlayerList().setGameType(gameMode);
   }

   public boolean isCommandBlockEnabled() {
      return true;
   }

   public int getOpPermissionLevel() {
      return 2;
   }

   public void setPlayerUuid(UUID uuid) {
      this.playerUuid = uuid;
   }
}
