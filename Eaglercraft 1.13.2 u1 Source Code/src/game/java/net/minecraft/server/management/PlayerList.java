package net.minecraft.server.management;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import net.lax1dude.eaglercraft.v1_8.EaglerSocketAddress;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePluginMessageProtocol;
import net.lax1dude.eaglercraft.v1_8.sp.server.skins.PlayerTextureData;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SPacketChangeGameState;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.network.play.server.SPacketCustomPayload;
import net.minecraft.network.play.server.SPacketEntityEffect;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.network.play.server.SPacketHeldItemChange;
import net.minecraft.network.play.server.SPacketJoinGame;
import net.minecraft.network.play.server.SPacketPlayerAbilities;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import net.minecraft.network.play.server.SPacketRespawn;
import net.minecraft.network.play.server.SPacketServerDifficulty;
import net.minecraft.network.play.server.SPacketSetExperience;
import net.minecraft.network.play.server.SPacketSpawnPosition;
import net.minecraft.network.play.server.SPacketTagsList;
import net.minecraft.network.play.server.SPacketTeams;
import net.minecraft.network.play.server.SPacketTimeUpdate;
import net.minecraft.network.play.server.SPacketUpdateRecipes;
import net.minecraft.network.play.server.SPacketWorldBorder;
import net.minecraft.potion.PotionEffect;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.StatList;
import net.minecraft.stats.StatisticsManagerServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.GameType;
import net.minecraft.world.IWorld;
import net.minecraft.world.WorldServer;
import net.minecraft.world.border.IBorderListener;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.storage.IPlayerFileData;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class PlayerList {
   public static final File FILE_PLAYERBANS = new File("banned-players.json");
   public static final File FILE_IPBANS = new File("banned-ips.json");
   public static final File FILE_OPS = new File("ops.json");
   public static final File FILE_WHITELIST = new File("whitelist.json");
   private static final Logger LOGGER = LogManager.getLogger();
   private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
   private final MinecraftServer server;
   private final List<EntityPlayerMP> players = Lists.newArrayList();
   private final Map<UUID, EntityPlayerMP> uuidToPlayerMap = Maps.newHashMap();
   private final UserListBans bannedPlayers = new UserListBans(FILE_PLAYERBANS);
   private final UserListIPBans bannedIPs = new UserListIPBans(FILE_IPBANS);
   private final UserListOps ops = new UserListOps(FILE_OPS);
   private final UserListWhitelist whiteListedPlayers = new UserListWhitelist(FILE_WHITELIST);
   private final Map<UUID, StatisticsManagerServer> playerStatFiles = Maps.newHashMap();
   private final Map<UUID, PlayerAdvancements> advancements = Maps.newHashMap();
   private IPlayerFileData playerDataManager;
   private boolean whiteListEnforced;
   protected int maxPlayers;
   private int viewDistance;
   private GameType gameType;
   private boolean commandsAllowedForAll;
   private int playerPingIndex;

   public PlayerList(MinecraftServer server) {
      this.server = server;
      this.getBannedPlayers().setLanServer(true);
      this.getBannedIPs().setLanServer(true);
      this.maxPlayers = 8;
   }

   public void initializeConnectionToPlayer(NetworkManager netManager, EntityPlayerMP playerIn, GamePluginMessageProtocol protocolVersion, PlayerTextureData textureData, EaglercraftUUID clientBrandUUID) {
      playerIn.textureData = textureData;
      playerIn.clientBrandUUID = clientBrandUUID;
      GameProfile gameprofile = playerIn.getGameProfile();
      PlayerProfileCache playerprofilecache = this.server.getPlayerProfileCache();
      GameProfile gameprofile1 = playerprofilecache.getProfileByUUID(gameprofile.getId());
      String s = gameprofile1 == null ? gameprofile.getName() : gameprofile1.getName();
      playerprofilecache.addEntry(gameprofile);
      NBTTagCompound nbttagcompound = this.readPlayerDataFromFile(playerIn);
      playerIn.setWorld(this.server.getWorld(playerIn.dimension));
      playerIn.interactionManager.setWorld((WorldServer)playerIn.world);
      String s1 = "local";
      if (netManager.getRemoteAddress() != null) {
         s1 = netManager.getRemoteAddress().toString();
      }

      LOGGER.info("{}[{}] logged in with entity id {} at ({}, {}, {})", playerIn.getName().getString(), s1, playerIn.getEntityId(), playerIn.posX, playerIn.posY, playerIn.posZ);
      WorldServer worldserver = this.server.getWorld(playerIn.dimension);
      WorldInfo worldinfo = worldserver.getWorldInfo();
      this.setPlayerGameTypeBasedOnOther(playerIn, (EntityPlayerMP)null, worldserver);
      NetHandlerPlayServer nethandlerplayserver = new NetHandlerPlayServer(this.server, netManager, playerIn, protocolVersion);
      nethandlerplayserver.sendPacket(new SPacketJoinGame(playerIn.getEntityId(), playerIn.interactionManager.getGameType(), worldinfo.isHardcore(), worldserver.dimension.getType(), worldserver.getDifficulty(), this.getMaxPlayers(), worldinfo.getGenerator(), worldserver.getGameRules().getBoolean("reducedDebugInfo")));
      nethandlerplayserver.sendPacket(new SPacketCustomPayload(SPacketCustomPayload.BRAND, (new PacketBuffer(Unpooled.buffer())).writeString(this.getServer().getServerModName())));
      nethandlerplayserver.sendPacket(new SPacketServerDifficulty(worldinfo.getDifficulty(), worldinfo.isDifficultyLocked()));
      nethandlerplayserver.sendPacket(new SPacketPlayerAbilities(playerIn.abilities));
      nethandlerplayserver.sendPacket(new SPacketHeldItemChange(playerIn.inventory.currentItem));
      nethandlerplayserver.sendPacket(new SPacketUpdateRecipes(this.server.getRecipeManager().getRecipes()));
      nethandlerplayserver.sendPacket(new SPacketTagsList(this.server.getNetworkTagManager()));
      this.updatePermissionLevel(playerIn);
      playerIn.getStats().markAllDirty();
      playerIn.getRecipeBook().init(playerIn);
      this.sendScoreboard(worldserver.getScoreboard(), playerIn);
      this.server.refreshStatusNextTick();
      ITextComponent itextcomponent;
      if (playerIn.getGameProfile().getName().equalsIgnoreCase(s)) {
         itextcomponent = new TextComponentTranslation("multiplayer.player.joined", playerIn.getDisplayName());
      } else {
         itextcomponent = new TextComponentTranslation("multiplayer.player.joined.renamed", playerIn.getDisplayName(), s);
      }

      this.sendMessage(itextcomponent.applyTextStyle(TextFormatting.YELLOW));
      this.playerLoggedIn(playerIn);
      nethandlerplayserver.setPlayerLocation(playerIn.posX, playerIn.posY, playerIn.posZ, playerIn.rotationYaw, playerIn.rotationPitch);
      this.sendWorldInfo(playerIn, worldserver);
      if (!this.server.getResourcePackUrl().isEmpty()) {
         playerIn.loadResourcePack(this.server.getResourcePackUrl(), this.server.getResourcePackHash());
      }

      for(PotionEffect potioneffect : playerIn.getActivePotionEffects()) {
         nethandlerplayserver.sendPacket(new SPacketEntityEffect(playerIn.getEntityId(), potioneffect));
      }

      if (nbttagcompound != null && nbttagcompound.contains("RootVehicle", 10)) {
         NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("RootVehicle");
         Entity entity1 = AnvilChunkLoader.readWorldEntity(nbttagcompound1.getCompound("Entity"), worldserver, true);
         if (entity1 != null) {
            UUID uuid = nbttagcompound1.getUniqueId("Attach");
            if (entity1.getUniqueID().equals(uuid)) {
               playerIn.startRiding(entity1, true);
            } else {
               for(Entity entity : entity1.getRecursivePassengers()) {
                  if (entity.getUniqueID().equals(uuid)) {
                     playerIn.startRiding(entity, true);
                     break;
                  }
               }
            }

            if (!playerIn.isPassenger()) {
               LOGGER.warn("Couldn't reattach entity to player");
               worldserver.removeEntityDangerously(entity1);

               for(Entity entity2 : entity1.getRecursivePassengers()) {
                  worldserver.removeEntityDangerously(entity2);
               }
            }
         }
      }

      playerIn.addSelfToInternalCraftingInventory();
   }

   protected void sendScoreboard(ServerScoreboard scoreboardIn, EntityPlayerMP playerIn) {
      Set<ScoreObjective> set = Sets.newHashSet();

      for(ScorePlayerTeam scoreplayerteam : scoreboardIn.getTeams()) {
         playerIn.connection.sendPacket(new SPacketTeams(scoreplayerteam, 0));
      }

      for(int i = 0; i < 19; ++i) {
         ScoreObjective scoreobjective = scoreboardIn.getObjectiveInDisplaySlot(i);
         if (scoreobjective != null && !set.contains(scoreobjective)) {
            for(Packet<?> packet : scoreboardIn.getCreatePackets(scoreobjective)) {
               playerIn.connection.sendPacket(packet);
            }

            set.add(scoreobjective);
         }
      }

   }

   public void func_212504_a(WorldServer p_212504_1_) {
      this.playerDataManager = p_212504_1_.getSaveHandler().getPlayerNBTManager();
      p_212504_1_.getWorldBorder().addListener(new IBorderListener() {
         public void onSizeChanged(WorldBorder border, double newSize) {
            PlayerList.this.sendPacketToAllPlayers(new SPacketWorldBorder(border, SPacketWorldBorder.Action.SET_SIZE));
         }

         public void onTransitionStarted(WorldBorder border, double oldSize, double newSize, long time) {
            PlayerList.this.sendPacketToAllPlayers(new SPacketWorldBorder(border, SPacketWorldBorder.Action.LERP_SIZE));
         }

         public void onCenterChanged(WorldBorder border, double x, double z) {
            PlayerList.this.sendPacketToAllPlayers(new SPacketWorldBorder(border, SPacketWorldBorder.Action.SET_CENTER));
         }

         public void onWarningTimeChanged(WorldBorder border, int newTime) {
            PlayerList.this.sendPacketToAllPlayers(new SPacketWorldBorder(border, SPacketWorldBorder.Action.SET_WARNING_TIME));
         }

         public void onWarningDistanceChanged(WorldBorder border, int newDistance) {
            PlayerList.this.sendPacketToAllPlayers(new SPacketWorldBorder(border, SPacketWorldBorder.Action.SET_WARNING_BLOCKS));
         }

         public void onDamageAmountChanged(WorldBorder border, double newAmount) {
         }

         public void onDamageBufferChanged(WorldBorder border, double newSize) {
         }
      });
   }

   public void preparePlayer(EntityPlayerMP playerIn, @Nullable WorldServer oldWorld) {
      WorldServer worldserver = playerIn.getServerWorld();
      if (oldWorld != null) {
         oldWorld.getPlayerChunkMap().removePlayer(playerIn);
      }

      worldserver.getPlayerChunkMap().addPlayer(playerIn);
      worldserver.getChunkProvider().getChunk((int)playerIn.posX >> 4, (int)playerIn.posZ >> 4, true, true);
      if (oldWorld != null) {
         CriteriaTriggers.CHANGED_DIMENSION.trigger(playerIn, oldWorld.dimension.getType(), worldserver.dimension.getType());
         if (oldWorld.dimension.getType() == DimensionType.NETHER && playerIn.world.dimension.getType() == DimensionType.OVERWORLD && playerIn.getEnteredNetherPosition() != null) {
            CriteriaTriggers.NETHER_TRAVEL.trigger(playerIn, playerIn.getEnteredNetherPosition());
         }
      }

   }

   public int getEntityViewDistance() {
      return PlayerChunkMap.getFurthestViewableBlock(this.getViewDistance());
   }

   @Nullable
   public NBTTagCompound readPlayerDataFromFile(EntityPlayerMP playerIn) {
      NBTTagCompound nbttagcompound = this.server.getWorld(DimensionType.OVERWORLD).getWorldInfo().getPlayerNBTTagCompound();
      NBTTagCompound nbttagcompound1;
      if (playerIn.getName().getString().equals(this.server.getServerOwner()) && nbttagcompound != null) {
         nbttagcompound1 = nbttagcompound;
         playerIn.read(nbttagcompound);
         LOGGER.debug("loading single player");
      } else {
         nbttagcompound1 = this.playerDataManager.readPlayerData(playerIn);
      }

      return nbttagcompound1;
   }

   protected void writePlayerData(EntityPlayerMP playerIn) {
      this.playerDataManager.writePlayerData(playerIn);
      StatisticsManagerServer statisticsmanagerserver = this.playerStatFiles.get(playerIn.getUniqueID());
      if (statisticsmanagerserver != null) {
         statisticsmanagerserver.saveStatFile();
      }

      PlayerAdvancements playeradvancements = this.advancements.get(playerIn.getUniqueID());
      if (playeradvancements != null) {
         playeradvancements.save();
      }

   }

   public void playerLoggedIn(EntityPlayerMP playerIn) {
      this.players.add(playerIn);
      this.uuidToPlayerMap.put(playerIn.getUniqueID(), playerIn);
      this.sendPacketToAllPlayers(new SPacketPlayerListItem(SPacketPlayerListItem.Action.ADD_PLAYER, playerIn));
      WorldServer worldserver = this.server.getWorld(playerIn.dimension);

      for(int i = 0; i < this.players.size(); ++i) {
         playerIn.connection.sendPacket(new SPacketPlayerListItem(SPacketPlayerListItem.Action.ADD_PLAYER, this.players.get(i)));
      }

      worldserver.spawnEntity(playerIn);
      this.preparePlayer(playerIn, (WorldServer)null);
      this.server.getCustomBossEvents().onPlayerLogin(playerIn);
   }

   public void serverUpdateMovingPlayer(EntityPlayerMP playerIn) {
      playerIn.getServerWorld().getPlayerChunkMap().updateMovingPlayer(playerIn);
   }

   public void playerLoggedOut(EntityPlayerMP playerIn) {
      WorldServer worldserver = playerIn.getServerWorld();
      playerIn.addStat(StatList.LEAVE_GAME);
      this.writePlayerData(playerIn);
      if (playerIn.isPassenger()) {
         Entity entity = playerIn.getLowestRidingEntity();
         if (entity.isOnePlayerRiding()) {
            LOGGER.debug("Removing player mount");
            playerIn.stopRiding();
            worldserver.removeEntityDangerously(entity);

            for(Entity entity1 : entity.getRecursivePassengers()) {
               worldserver.removeEntityDangerously(entity1);
            }

            worldserver.getChunk(playerIn.chunkCoordX, playerIn.chunkCoordZ).markDirty();
         }
      }

      worldserver.removeEntity(playerIn);
      worldserver.getPlayerChunkMap().removePlayer(playerIn);
      playerIn.getAdvancements().dispose();
      this.players.remove(playerIn);
      this.server.getCustomBossEvents().onPlayerLogout(playerIn);
      UUID uuid = playerIn.getUniqueID();
      EntityPlayerMP entityplayermp = this.uuidToPlayerMap.get(uuid);
      if (entityplayermp == playerIn) {
         this.uuidToPlayerMap.remove(uuid);
         this.playerStatFiles.remove(uuid);
         this.advancements.remove(uuid);
      }

      this.sendPacketToAllPlayers(new SPacketPlayerListItem(SPacketPlayerListItem.Action.REMOVE_PLAYER, playerIn));
   }

   @Nullable
   public ITextComponent canPlayerLogin(EaglerSocketAddress p_206258_1_, GameProfile p_206258_2_) {
      if (this.bannedPlayers.isBanned(p_206258_2_)) {
         UserListBansEntry userlistbansentry = this.bannedPlayers.getEntry(p_206258_2_);
         ITextComponent itextcomponent1 = new TextComponentTranslation("multiplayer.disconnect.banned.reason", userlistbansentry.getBanReason());
         if (userlistbansentry.getBanEndDate() != null) {
            itextcomponent1.appendSibling(new TextComponentTranslation("multiplayer.disconnect.banned.expiration", DATE_FORMAT.format(userlistbansentry.getBanEndDate())));
         }

         return itextcomponent1;
      } else if (!this.canJoin(p_206258_2_)) {
         return new TextComponentTranslation("multiplayer.disconnect.not_whitelisted");
      } else if (this.bannedIPs.isBanned(p_206258_1_)) {
         UserListIPBansEntry userlistipbansentry = this.bannedIPs.getBanEntry(p_206258_1_);
         ITextComponent itextcomponent = new TextComponentTranslation("multiplayer.disconnect.banned_ip.reason", userlistipbansentry.getBanReason());
         if (userlistipbansentry.getBanEndDate() != null) {
            itextcomponent.appendSibling(new TextComponentTranslation("multiplayer.disconnect.banned_ip.expiration", DATE_FORMAT.format(userlistipbansentry.getBanEndDate())));
         }

         return itextcomponent;
      } else {
         return this.players.size() >= this.maxPlayers && !this.bypassesPlayerLimit(p_206258_2_) ? new TextComponentTranslation("multiplayer.disconnect.server_full") : null;
      }
   }

   public EntityPlayerMP createPlayerForUser(GameProfile profile) {
      UUID uuid = EntityPlayer.getUUID(profile);
      List<EntityPlayerMP> list = Lists.newArrayList();

      for(int i = 0; i < this.players.size(); ++i) {
         EntityPlayerMP entityplayermp = this.players.get(i);
         if (entityplayermp.getUniqueID().equals(uuid)) {
            list.add(entityplayermp);
         }
      }

      EntityPlayerMP entityplayermp2 = this.uuidToPlayerMap.get(profile.getId());
      if (entityplayermp2 != null && !list.contains(entityplayermp2)) {
         list.add(entityplayermp2);
      }

      for(EntityPlayerMP entityplayermp1 : list) {
         entityplayermp1.connection.disconnect(new TextComponentTranslation("multiplayer.disconnect.duplicate_login"));
      }

      PlayerInteractionManager playerinteractionmanager;
      if (this.server.isDemo()) {
         playerinteractionmanager = new DemoPlayerInteractionManager(this.server.getWorld(DimensionType.OVERWORLD));
      } else {
         playerinteractionmanager = new PlayerInteractionManager(this.server.getWorld(DimensionType.OVERWORLD));
      }

      return new EntityPlayerMP(this.server, this.server.getWorld(DimensionType.OVERWORLD), profile, playerinteractionmanager);
   }

   public EntityPlayerMP recreatePlayerEntity(EntityPlayerMP playerIn, DimensionType dimension, boolean conqueredEnd) {
      playerIn.getServerWorld().getEntityTracker().removePlayerFromTrackers(playerIn);
      playerIn.getServerWorld().getEntityTracker().untrack(playerIn);
      playerIn.getServerWorld().getPlayerChunkMap().removePlayer(playerIn);
      this.players.remove(playerIn);
      this.server.getWorld(playerIn.dimension).removeEntityDangerously(playerIn);
      BlockPos blockpos = playerIn.getBedLocation();
      boolean flag = playerIn.isSpawnForced();
      playerIn.dimension = dimension;
      PlayerInteractionManager playerinteractionmanager;
      if (this.server.isDemo()) {
         playerinteractionmanager = new DemoPlayerInteractionManager(this.server.getWorld(playerIn.dimension));
      } else {
         playerinteractionmanager = new PlayerInteractionManager(this.server.getWorld(playerIn.dimension));
      }

      EntityPlayerMP entityplayermp = new EntityPlayerMP(this.server, this.server.getWorld(playerIn.dimension), playerIn.getGameProfile(), playerinteractionmanager);
      entityplayermp.clientBrandUUID = playerIn.clientBrandUUID;
      entityplayermp.textureData = playerIn.textureData;
      entityplayermp.connection = playerIn.connection;
      entityplayermp.copyFrom(playerIn, conqueredEnd);
      entityplayermp.setEntityId(playerIn.getEntityId());
      entityplayermp.setPrimaryHand(playerIn.getPrimaryHand());

      for(String s : playerIn.getTags()) {
         entityplayermp.addTag(s);
      }

      WorldServer worldserver = this.server.getWorld(playerIn.dimension);
      this.setPlayerGameTypeBasedOnOther(entityplayermp, playerIn, worldserver);
      if (blockpos != null) {
         BlockPos blockpos1 = EntityPlayer.getBedSpawnLocation(this.server.getWorld(playerIn.dimension), blockpos, flag);
         if (blockpos1 != null) {
            entityplayermp.setLocationAndAngles((double)((float)blockpos1.getX() + 0.5F), (double)((float)blockpos1.getY() + 0.1F), (double)((float)blockpos1.getZ() + 0.5F), 0.0F, 0.0F);
            entityplayermp.setSpawnPoint(blockpos, flag);
         } else {
            entityplayermp.connection.sendPacket(new SPacketChangeGameState(0, 0.0F));
         }
      }

      worldserver.getChunkProvider().getChunk((int)entityplayermp.posX >> 4, (int)entityplayermp.posZ >> 4, true, true);

      while(!worldserver.isCollisionBoxesEmpty(entityplayermp, entityplayermp.getBoundingBox()) && entityplayermp.posY < 256.0D) {
         entityplayermp.setPosition(entityplayermp.posX, entityplayermp.posY + 1.0D, entityplayermp.posZ);
      }

      entityplayermp.connection.sendPacket(new SPacketRespawn(entityplayermp.dimension, entityplayermp.world.getDifficulty(), entityplayermp.world.getWorldInfo().getGenerator(), entityplayermp.interactionManager.getGameType()));
      BlockPos blockpos2 = worldserver.getSpawnPoint();
      entityplayermp.connection.setPlayerLocation(entityplayermp.posX, entityplayermp.posY, entityplayermp.posZ, entityplayermp.rotationYaw, entityplayermp.rotationPitch);
      entityplayermp.connection.sendPacket(new SPacketSpawnPosition(blockpos2));
      entityplayermp.connection.sendPacket(new SPacketSetExperience(entityplayermp.experience, entityplayermp.experienceTotal, entityplayermp.experienceLevel));
      this.sendWorldInfo(entityplayermp, worldserver);
      this.updatePermissionLevel(entityplayermp);
      worldserver.getPlayerChunkMap().addPlayer(entityplayermp);
      worldserver.spawnEntity(entityplayermp);
      this.players.add(entityplayermp);
      this.uuidToPlayerMap.put(entityplayermp.getUniqueID(), entityplayermp);
      entityplayermp.addSelfToInternalCraftingInventory();
      entityplayermp.setHealth(entityplayermp.getHealth());
      return entityplayermp;
   }

   public void updatePermissionLevel(EntityPlayerMP player) {
      GameProfile gameprofile = player.getGameProfile();
      int i = this.server.getPermissionLevel(gameprofile);
      this.sendPlayerPermissionLevel(player, i);
   }

   public void changePlayerDimension(EntityPlayerMP player, DimensionType dimensionIn) {
      DimensionType dimensiontype = player.dimension;
      WorldServer worldserver = this.server.getWorld(player.dimension);
      player.dimension = dimensionIn;
      WorldServer worldserver1 = this.server.getWorld(player.dimension);
      player.connection.sendPacket(new SPacketRespawn(player.dimension, player.world.getDifficulty(), player.world.getWorldInfo().getGenerator(), player.interactionManager.getGameType()));
      this.updatePermissionLevel(player);
      worldserver.removeEntityDangerously(player);
      player.removed = false;
      this.transferEntityToWorld(player, dimensiontype, worldserver, worldserver1);
      this.preparePlayer(player, worldserver);
      player.connection.setPlayerLocation(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
      player.interactionManager.setWorld(worldserver1);
      player.connection.sendPacket(new SPacketPlayerAbilities(player.abilities));
      this.sendWorldInfo(player, worldserver1);
      this.sendInventory(player);

      for(PotionEffect potioneffect : player.getActivePotionEffects()) {
         player.connection.sendPacket(new SPacketEntityEffect(player.getEntityId(), potioneffect));
      }

   }

   public void transferEntityToWorld(Entity entityIn, DimensionType lastDimension, WorldServer oldWorldIn, WorldServer toWorldIn) {
      double d0 = entityIn.posX;
      double d1 = entityIn.posZ;
      double d2 = 8.0D;
      float f = entityIn.rotationYaw;
      oldWorldIn.profiler.startSection("moving");
      if (entityIn.dimension == DimensionType.NETHER) {
         d0 = MathHelper.clamp(d0 / 8.0D, toWorldIn.getWorldBorder().minX() + 16.0D, toWorldIn.getWorldBorder().maxX() - 16.0D);
         d1 = MathHelper.clamp(d1 / 8.0D, toWorldIn.getWorldBorder().minZ() + 16.0D, toWorldIn.getWorldBorder().maxZ() - 16.0D);
         entityIn.setLocationAndAngles(d0, entityIn.posY, d1, entityIn.rotationYaw, entityIn.rotationPitch);
         if (entityIn.isAlive()) {
            oldWorldIn.tickEntity(entityIn, false);
         }
      } else if (entityIn.dimension == DimensionType.OVERWORLD) {
         d0 = MathHelper.clamp(d0 * 8.0D, toWorldIn.getWorldBorder().minX() + 16.0D, toWorldIn.getWorldBorder().maxX() - 16.0D);
         d1 = MathHelper.clamp(d1 * 8.0D, toWorldIn.getWorldBorder().minZ() + 16.0D, toWorldIn.getWorldBorder().maxZ() - 16.0D);
         entityIn.setLocationAndAngles(d0, entityIn.posY, d1, entityIn.rotationYaw, entityIn.rotationPitch);
         if (entityIn.isAlive()) {
            oldWorldIn.tickEntity(entityIn, false);
         }
      } else {
         BlockPos blockpos;
         if (lastDimension == DimensionType.THE_END) {
            blockpos = toWorldIn.getSpawnPoint();
         } else {
            blockpos = toWorldIn.getSpawnCoordinate();
         }

         d0 = (double)blockpos.getX();
         entityIn.posY = (double)blockpos.getY();
         d1 = (double)blockpos.getZ();
         entityIn.setLocationAndAngles(d0, entityIn.posY, d1, 90.0F, 0.0F);
         if (entityIn.isAlive()) {
            oldWorldIn.tickEntity(entityIn, false);
         }
      }

      oldWorldIn.profiler.endSection();
      if (lastDimension != DimensionType.THE_END) {
         oldWorldIn.profiler.startSection("placing");
         d0 = (double)MathHelper.clamp((int)d0, -29999872, 29999872);
         d1 = (double)MathHelper.clamp((int)d1, -29999872, 29999872);
         if (entityIn.isAlive()) {
            entityIn.setLocationAndAngles(d0, entityIn.posY, d1, entityIn.rotationYaw, entityIn.rotationPitch);
            toWorldIn.getDefaultTeleporter().placeInPortal(entityIn, f);
            toWorldIn.spawnEntity(entityIn);
            toWorldIn.tickEntity(entityIn, false);
         }

         oldWorldIn.profiler.endSection();
      }

      entityIn.setWorld(toWorldIn);
   }

   public void tick() {
      if (++this.playerPingIndex > 600) {
         this.sendPacketToAllPlayers(new SPacketPlayerListItem(SPacketPlayerListItem.Action.UPDATE_LATENCY, this.players));
         this.playerPingIndex = 0;
      }

   }

   public void sendPacketToAllPlayers(Packet<?> packetIn) {
      for(int i = 0; i < this.players.size(); ++i) {
         (this.players.get(i)).connection.sendPacket(packetIn);
      }

   }

   public void sendPacketToAllPlayersInDimension(Packet<?> packetIn, DimensionType dimension) {
      for(int i = 0; i < this.players.size(); ++i) {
         EntityPlayerMP entityplayermp = this.players.get(i);
         if (entityplayermp.dimension == dimension) {
            entityplayermp.connection.sendPacket(packetIn);
         }
      }

   }

   public void sendMessageToAllTeamMembers(EntityPlayer player, ITextComponent message) {
      Team team = player.getTeam();
      if (team != null) {
         for(String s : team.getMembershipCollection()) {
            EntityPlayerMP entityplayermp = this.getPlayerByUsername(s);
            if (entityplayermp != null && entityplayermp != player) {
               entityplayermp.sendMessage(message);
            }
         }

      }
   }

   public void sendMessageToTeamOrAllPlayers(EntityPlayer player, ITextComponent message) {
      Team team = player.getTeam();
      if (team == null) {
         this.sendMessage(message);
      } else {
         for(int i = 0; i < this.players.size(); ++i) {
            EntityPlayerMP entityplayermp = this.players.get(i);
            if (entityplayermp.getTeam() != team) {
               entityplayermp.sendMessage(message);
            }
         }

      }
   }

   public String[] getOnlinePlayerNames() {
      String[] astring = new String[this.players.size()];

      for(int i = 0; i < this.players.size(); ++i) {
         astring[i] = this.players.get(i).getGameProfile().getName();
      }

      return astring;
   }

   public UserListBans getBannedPlayers() {
      return this.bannedPlayers;
   }

   public UserListIPBans getBannedIPs() {
      return this.bannedIPs;
   }

   public void addOp(GameProfile profile) {
      this.ops.addEntry(new UserListOpsEntry(profile, this.server.getOpPermissionLevel(), this.ops.bypassesPlayerLimit(profile)));
      EntityPlayerMP entityplayermp = this.getPlayerByUUID(profile.getId());
      if (entityplayermp != null) {
         this.updatePermissionLevel(entityplayermp);
      }

   }

   public void removeOp(GameProfile profile) {
      this.ops.removeEntry(profile);
      EntityPlayerMP entityplayermp = this.getPlayerByUUID(profile.getId());
      if (entityplayermp != null) {
         this.updatePermissionLevel(entityplayermp);
      }

   }

   private void sendPlayerPermissionLevel(EntityPlayerMP player, int permLevel) {
      if (player.connection != null) {
         byte b0;
         if (permLevel <= 0) {
            b0 = 24;
         } else if (permLevel >= 4) {
            b0 = 28;
         } else {
            b0 = (byte)(24 + permLevel);
         }

         player.connection.sendPacket(new SPacketEntityStatus(player, b0));
      }

      this.server.getCommandManager().send(player);
   }

   public boolean canJoin(GameProfile profile) {
      return !this.whiteListEnforced || this.ops.hasEntry(profile) || this.whiteListedPlayers.hasEntry(profile);
   }

   public boolean canSendCommands(GameProfile profile) {
      return this.ops.hasEntry(profile) || this.server.isSinglePlayer() && this.server.getWorld(DimensionType.OVERWORLD).getWorldInfo().areCommandsAllowed() && this.server.getServerOwner().equalsIgnoreCase(profile.getName()) || this.commandsAllowedForAll;
   }

   @Nullable
   public EntityPlayerMP getPlayerByUsername(String username) {
      for(EntityPlayerMP entityplayermp : this.players) {
         if (entityplayermp.getGameProfile().getName().equalsIgnoreCase(username)) {
            return entityplayermp;
         }
      }

      return null;
   }

   public void sendToAllNearExcept(@Nullable EntityPlayer except, double x, double y, double z, double radius, DimensionType dimension, Packet<?> packetIn) {
      for(int i = 0; i < this.players.size(); ++i) {
         EntityPlayerMP entityplayermp = this.players.get(i);
         if (entityplayermp != except && entityplayermp.dimension == dimension) {
            double d0 = x - entityplayermp.posX;
            double d1 = y - entityplayermp.posY;
            double d2 = z - entityplayermp.posZ;
            if (d0 * d0 + d1 * d1 + d2 * d2 < radius * radius) {
               entityplayermp.connection.sendPacket(packetIn);
            }
         }
      }

   }

   public void saveAllPlayerData() {
      for(int i = 0; i < this.players.size(); ++i) {
         this.writePlayerData(this.players.get(i));
      }

   }

   public void saveAll() {
      this.saveAllPlayerData();
   }

   public UserListWhitelist getWhitelistedPlayers() {
      return this.whiteListedPlayers;
   }

   public String[] getWhitelistedPlayerNames() {
      return this.whiteListedPlayers.getKeys();
   }

   public UserListOps getOppedPlayers() {
      return this.ops;
   }

   public String[] getOppedPlayerNames() {
      return this.ops.getKeys();
   }

   public void reloadWhitelist() {
   }

   public void sendWorldInfo(EntityPlayerMP playerIn, WorldServer worldIn) {
      WorldBorder worldborder = this.server.getWorld(DimensionType.OVERWORLD).getWorldBorder();
      playerIn.connection.sendPacket(new SPacketWorldBorder(worldborder, SPacketWorldBorder.Action.INITIALIZE));
      playerIn.connection.sendPacket(new SPacketTimeUpdate(worldIn.getGameTime(), worldIn.getDayTime(), worldIn.getGameRules().getBoolean("doDaylightCycle")));
      BlockPos blockpos = worldIn.getSpawnPoint();
      playerIn.connection.sendPacket(new SPacketSpawnPosition(blockpos));
      if (worldIn.isRaining()) {
         playerIn.connection.sendPacket(new SPacketChangeGameState(1, 0.0F));
         playerIn.connection.sendPacket(new SPacketChangeGameState(7, worldIn.getRainStrength(1.0F)));
         playerIn.connection.sendPacket(new SPacketChangeGameState(8, worldIn.getThunderStrength(1.0F)));
      }

   }

   public void sendInventory(EntityPlayerMP playerIn) {
      playerIn.sendContainerToPlayer(playerIn.inventoryContainer);
      playerIn.setPlayerHealthUpdated();
      playerIn.connection.sendPacket(new SPacketHeldItemChange(playerIn.inventory.currentItem));
   }

   public int getCurrentPlayerCount() {
      return this.players.size();
   }

   public int getMaxPlayers() {
      return this.maxPlayers;
   }

   public String[] getAvailablePlayerDat() {
      return this.server.getWorld(DimensionType.OVERWORLD).getSaveHandler().getPlayerNBTManager().getAvailablePlayerDat();
   }

   public boolean isWhiteListEnabled() {
      return this.whiteListEnforced;
   }

   public void setWhiteListEnabled(boolean whitelistEnabled) {
      this.whiteListEnforced = whitelistEnabled;
   }

   public List<EntityPlayerMP> getPlayersMatchingAddress(String address) {
      List<EntityPlayerMP> list = Lists.newArrayList();

      for(EntityPlayerMP entityplayermp : this.players) {
         if (entityplayermp.getPlayerIP().equals(address)) {
            list.add(entityplayermp);
         }
      }

      return list;
   }

   public int getViewDistance() {
      return this.viewDistance;
   }

   public MinecraftServer getServer() {
      return this.server;
   }

   public NBTTagCompound getHostPlayerData() {
      return null;
   }

   @OnlyIn(Dist.CLIENT)
   public void setGameType(GameType gameModeIn) {
      this.gameType = gameModeIn;
   }

   private void setPlayerGameTypeBasedOnOther(EntityPlayerMP target, EntityPlayerMP source, IWorld worldIn) {
      if (source != null) {
         target.interactionManager.setGameType(source.interactionManager.getGameType());
      } else if (this.gameType != null) {
         target.interactionManager.setGameType(this.gameType);
      }

      target.interactionManager.initializeGameType(worldIn.getWorldInfo().getGameType());
   }

   @OnlyIn(Dist.CLIENT)
   public void setCommandsAllowedForAll(boolean p_72387_1_) {
      this.commandsAllowedForAll = p_72387_1_;
   }

   public void removeAllPlayers() {
      for(int i = 0; i < this.players.size(); ++i) {
         (this.players.get(i)).connection.disconnect(new TextComponentTranslation("multiplayer.disconnect.server_shutdown"));
      }

   }

   public void sendMessage(ITextComponent component, boolean isSystem) {
      this.server.sendMessage(component);
      ChatType chattype = isSystem ? ChatType.SYSTEM : ChatType.CHAT;
      this.sendPacketToAllPlayers(new SPacketChat(component, chattype));
   }

   public void sendMessage(ITextComponent component) {
      this.sendMessage(component, true);
   }

   public StatisticsManagerServer getPlayerStats(EntityPlayer playerIn) {
      UUID uuid = playerIn.getUniqueID();
      StatisticsManagerServer statisticsmanagerserver = uuid == null ? null : this.playerStatFiles.get(uuid);
      if (statisticsmanagerserver == null) {
         File file1 = new File(this.server.getWorld(DimensionType.OVERWORLD).getSaveHandler().getWorldDirectory().getPath(), "stats");
         File file2 = new File(file1, uuid + ".json");
         if (!file2.exists()) {
            File file3 = new File(file1, playerIn.getName().getString() + ".json");
            if (file3.exists() && file3.isFile()) {
               file3.renameTo(file2);
            }
         }

         statisticsmanagerserver = new StatisticsManagerServer(this.server, file2);
         this.playerStatFiles.put(uuid, statisticsmanagerserver);
      }

      return statisticsmanagerserver;
   }

   public PlayerAdvancements getPlayerAdvancements(EntityPlayerMP p_192054_1_) {
      UUID uuid = p_192054_1_.getUniqueID();
      PlayerAdvancements playeradvancements = this.advancements.get(uuid);
      if (playeradvancements == null) {
         File file1 = new File(this.server.getWorld(DimensionType.OVERWORLD).getSaveHandler().getWorldDirectory().getPath(), "advancements");
         File file2 = new File(file1, uuid + ".json");
         playeradvancements = new PlayerAdvancements(this.server, file2, p_192054_1_);
         this.advancements.put(uuid, playeradvancements);
      }

      playeradvancements.setPlayer(p_192054_1_);
      return playeradvancements;
   }

   public void setViewDistance(int distance) {
      this.viewDistance = distance;

      for(WorldServer worldserver : this.server.getWorlds()) {
         if (worldserver != null) {
            worldserver.getPlayerChunkMap().setPlayerViewRadius(distance);
            worldserver.getEntityTracker().setViewDistance(distance);
         }
      }

   }

   public List<EntityPlayerMP> getPlayers() {
      return this.players;
   }

   @Nullable
   public EntityPlayerMP getPlayerByUUID(UUID playerUUID) {
      return this.uuidToPlayerMap.get(playerUUID);
   }

   @Nullable
   public EntityPlayerMP getPlayer(UUID playerUUID) {
      return this.getPlayerByUUID(playerUUID);
   }

   public boolean bypassesPlayerLimit(GameProfile profile) {
      return false;
   }

   public void reloadResources() {
      for(PlayerAdvancements playeradvancements : this.advancements.values()) {
         playeradvancements.reload();
      }

      this.sendPacketToAllPlayers(new SPacketTagsList(this.server.getNetworkTagManager()));
      SPacketUpdateRecipes spacketupdaterecipes = new SPacketUpdateRecipes(this.server.getRecipeManager().getRecipes());

      for(EntityPlayerMP entityplayermp : this.players) {
         entityplayermp.connection.sendPacket(spacketupdaterecipes);
         entityplayermp.getRecipeBook().init(entityplayermp);
      }

   }

   public boolean commandsAllowedForAll() {
      return this.commandsAllowedForAll;
   }
}
