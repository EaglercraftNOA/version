package net.minecraft.server.management;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.lax1dude.eaglercraft.EaglercraftUUID;
import net.lax1dude.eaglercraft.internal.vfs2.VFile2;
import net.lax1dude.eaglercraft.socket.protocol.GamePluginMessageConstants;
import net.lax1dude.eaglercraft.socket.protocol.GamePluginMessageProtocol;
import net.lax1dude.eaglercraft.socket.protocol.client.GameProtocolMessageController;
import net.lax1dude.eaglercraft.sp.server.EaglerMinecraftServer;
import net.lax1dude.eaglercraft.sp.server.socket.IntegratedServerPlayerNetworkManager;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
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
import net.minecraft.network.play.server.SPacketTeams;
import net.minecraft.network.play.server.SPacketTimeUpdate;
import net.minecraft.network.play.server.SPacketWorldBorder;
import net.minecraft.potion.PotionEffect;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.StatList;
import net.minecraft.stats.StatisticsManagerServer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.DimensionType;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.border.IBorderListener;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.storage.IPlayerFileData;
import net.minecraft.world.storage.WorldInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class ServerConfigurationManager {
	private static final Logger LOG = LogManager.getLogger();

	/** Reference to the MinecraftServer object. */
	private final MinecraftServer mcServer;
	private final List<EntityPlayerMP> playerEntityList = Lists.<EntityPlayerMP>newArrayList();
	private final Map<EaglercraftUUID, EntityPlayerMP> uuidToPlayerMap = Maps
			.<EaglercraftUUID, EntityPlayerMP>newHashMap();

	/** The Set of all whitelisted players. */
	private final Map<EaglercraftUUID, StatisticsManagerServer> playerStatFiles;
	private final Map<EaglercraftUUID, PlayerAdvancements> field_192055_p;

	/** Reference to the PlayerNBTManager object. */
	private IPlayerFileData playerNBTManagerObj;

	/** The maximum number of players that can be connected at a time. */
	protected int maxPlayers;
	protected int viewDistance;
	private GameType gameType;

	/** True if all players are allowed to use commands (cheats). */
	private boolean commandsAllowedForAll;

	/**
	 * index into playerEntities of player to ping, updated every tick; currently
	 * hardcoded to max at 200 players
	 */
	private int playerPingIndex;

	public ServerConfigurationManager(MinecraftServer server) {
		this.playerStatFiles = Maps.<EaglercraftUUID, StatisticsManagerServer>newHashMap();
		this.field_192055_p = Maps.<EaglercraftUUID, PlayerAdvancements>newHashMap();
		this.mcServer = server;
		this.maxPlayers = 8;
	}

	public void initializeConnectionToPlayer(IntegratedServerPlayerNetworkManager netManager, EntityPlayerMP playerIn, int protocolVersion, EaglercraftUUID clientBrandUUID) {
		playerIn.clientBrandUUID = clientBrandUUID;
		NBTTagCompound nbttagcompound = this.readPlayerDataFromFile(playerIn);
		playerIn.setWorld(this.mcServer.worldServerForDimension(playerIn.dimension));
		playerIn.interactionManager.setWorld((WorldServer) playerIn.world);
		String s1 = "channel:" + netManager.playerChannel;

		LOG.info(playerIn.getName() + "[" + s1 + "] logged in with entity id " + playerIn.getEntityId() + " at ("
				+ playerIn.posX + ", " + playerIn.posY + ", " + playerIn.posZ + ")");
		WorldServer worldserver = this.mcServer.worldServerForDimension(playerIn.dimension);
		WorldInfo worldinfo = worldserver.getWorldInfo();
		this.setPlayerGameTypeBasedOnOther(playerIn, (EntityPlayerMP) null, worldserver);
		NetHandlerPlayServer nethandlerplayserver = new NetHandlerPlayServer(this.mcServer, netManager, playerIn);
		nethandlerplayserver.setEaglerMessageController(new GameProtocolMessageController(
				GamePluginMessageProtocol.getByVersion(protocolVersion), GamePluginMessageConstants.SERVER_TO_CLIENT,
				GameProtocolMessageController.createServerHandler(protocolVersion, nethandlerplayserver),
				(ch, msg) -> nethandlerplayserver.sendPacket(new SPacketCustomPayload(ch, msg))));
		nethandlerplayserver.sendPacket(new SPacketJoinGame(playerIn.getEntityId(),
				playerIn.interactionManager.getGameType(), worldinfo.isHardcoreModeEnabled(),
				worldserver.provider.getDimensionType().getId(), worldserver.getDifficulty(), this.getMaxPlayers(),
				worldinfo.getTerrainType(), worldserver.getGameRules().getBoolean("reducedDebugInfo")));
		nethandlerplayserver.sendPacket(new SPacketCustomPayload("MC|Brand",
				(new PacketBuffer(Unpooled.buffer())).writeString(this.getServerInstance().getServerModName())));
		nethandlerplayserver
				.sendPacket(new SPacketServerDifficulty(worldinfo.getDifficulty(), worldinfo.isDifficultyLocked()));
		nethandlerplayserver.sendPacket(new SPacketPlayerAbilities(playerIn.capabilities));
		nethandlerplayserver.sendPacket(new SPacketHeldItemChange(playerIn.inventory.currentItem));
		playerIn.getStatFile().markAllDirty();
		playerIn.func_192037_E().func_192826_c(playerIn);
		this.sendScoreboard((ServerScoreboard) worldserver.getScoreboard(), playerIn);
		TextComponentTranslation chatcomponenttranslation;
		chatcomponenttranslation = new TextComponentTranslation("multiplayer.player.joined",
				new Object[] { playerIn.getDisplayName() });
		chatcomponenttranslation.getStyle().setColor(TextFormatting.YELLOW);
		this.sendChatMsg(chatcomponenttranslation);
		if (playerIn.canCommandSenderUseCommand(2, "give")) {
			TextComponentTranslation shaderF4Msg = new TextComponentTranslation("[EaglercraftX] ");
			shaderF4Msg.getStyle().setColor(TextFormatting.GOLD);
			TextComponentTranslation shaderF4Msg2 = new TextComponentTranslation("command.skull.tip");
			shaderF4Msg2.getStyle().setColor(TextFormatting.AQUA);
			shaderF4Msg.appendSibling(shaderF4Msg2);
			playerIn.addChatMessage(shaderF4Msg);
		}
		this.playerLoggedIn(playerIn);
		nethandlerplayserver.setPlayerLocation(playerIn.posX, playerIn.posY, playerIn.posZ, playerIn.rotationYaw,
				playerIn.rotationPitch);
		this.updateTimeAndWeatherForPlayer(playerIn, worldserver);
		if (this.mcServer.getResourcePackUrl().length() > 0) {
			playerIn.loadResourcePack(this.mcServer.getResourcePackUrl(), this.mcServer.getResourcePackHash());
		}

		for (PotionEffect potioneffect : playerIn.getActivePotionEffects()) {
			nethandlerplayserver.sendPacket(new SPacketEntityEffect(playerIn.getEntityId(), potioneffect));
		}

		playerIn.addSelfToInternalCraftingInventory();
		if (nbttagcompound != null && nbttagcompound.hasKey("Riding", 10)) {
			Entity entity = EntityList.createEntityFromNBT(nbttagcompound.getCompoundTag("Riding"), worldserver);
			if (entity != null) {
				entity.forceSpawn = true;
				worldserver.spawnEntityInWorld(entity);
				playerIn.startRiding(entity);
				entity.forceSpawn = false;
			}
		}
	}

	/**
	 * + checks ban-lists, then white-lists, then space for the server. Returns null
	 * on success, or an error message
	 */
	public String allowUserToConnect(GameProfile gameprofile) {
		return doesPlayerAlreadyExist(gameprofile)
				? "\"" + gameprofile.getName() + "\" is already playing on this world!"
				: null;
	}

	private boolean doesPlayerAlreadyExist(GameProfile gameprofile) {
		for (int i = 0, l = playerEntityList.size(); i < l; ++i) {
			EntityPlayerMP player = playerEntityList.get(i);
			if (player.getName().equalsIgnoreCase(gameprofile.getName())
					|| player.getUniqueID().equals(gameprofile.getId())) {
				return true;
			}
		}
		return false;
	}

	protected void sendScoreboard(ServerScoreboard scoreboardIn, EntityPlayerMP playerIn) {
		Set<ScoreObjective> set = Sets.<ScoreObjective>newHashSet();

		for (ScorePlayerTeam scoreplayerteam : scoreboardIn.getTeams()) {
			playerIn.connection.sendPacket(new SPacketTeams(scoreplayerteam, 0));
		}

		for (int i = 0; i < 19; ++i) {
			ScoreObjective scoreobjective = scoreboardIn.getObjectiveInDisplaySlot(i);

			if (scoreobjective != null && !set.contains(scoreobjective)) {
				List<Packet> packets = scoreboardIn.getCreatePackets(scoreobjective);
				for (int i1 = 0; i1 < packets.size(); i1++) {
					Packet<?> packet = packets.get(i);
					playerIn.connection.sendPacket(packet);
				}

				set.add(scoreobjective);
			}
		}
	}

	/**
	 * Sets the NBT manager to the one for the WorldServer given.
	 */
	public void setPlayerManager(WorldServer[] worldServers) {
		this.playerNBTManagerObj = worldServers[0].getSaveHandler().getPlayerNBTManager();
		worldServers[0].getWorldBorder().addListener(new IBorderListener() {
			public void onSizeChanged(WorldBorder border, double newSize) {
				ServerConfigurationManager.this
						.sendPacketToAllPlayers(new SPacketWorldBorder(border, SPacketWorldBorder.Action.SET_SIZE));
			}

			public void onTransitionStarted(WorldBorder border, double oldSize, double newSize, long time) {
				ServerConfigurationManager.this
						.sendPacketToAllPlayers(new SPacketWorldBorder(border, SPacketWorldBorder.Action.LERP_SIZE));
			}

			public void onCenterChanged(WorldBorder border, double x, double z) {
				ServerConfigurationManager.this
						.sendPacketToAllPlayers(new SPacketWorldBorder(border, SPacketWorldBorder.Action.SET_CENTER));
			}

			public void onWarningTimeChanged(WorldBorder border, int newTime) {
				ServerConfigurationManager.this.sendPacketToAllPlayers(
						new SPacketWorldBorder(border, SPacketWorldBorder.Action.SET_WARNING_TIME));
			}

			public void onWarningDistanceChanged(WorldBorder border, int newDistance) {
				ServerConfigurationManager.this.sendPacketToAllPlayers(
						new SPacketWorldBorder(border, SPacketWorldBorder.Action.SET_WARNING_BLOCKS));
			}

			public void onDamageAmountChanged(WorldBorder border, double newAmount) {
			}

			public void onDamageBufferChanged(WorldBorder border, double newSize) {
			}
		});
	}

	public void preparePlayer(EntityPlayerMP playerIn, WorldServer worldIn) {
		WorldServer worldserver = playerIn.getServerWorld();

		if (worldIn != null) {
			worldIn.getPlayerChunkMap().removePlayer(playerIn);
		}

		worldserver.getPlayerChunkMap().addPlayer(playerIn);
		worldserver.getChunkProvider().provideChunk((int) playerIn.posX >> 4, (int) playerIn.posZ >> 4);

		if (worldIn != null) {
			CriteriaTriggers.field_193134_u.func_193143_a(playerIn, worldIn.provider.getDimensionType(),
					worldserver.provider.getDimensionType());

			if (worldIn.provider.getDimensionType() == DimensionType.NETHER
					&& playerIn.world.provider.getDimensionType() == DimensionType.OVERWORLD
					&& playerIn.func_193106_Q() != null) {
				CriteriaTriggers.field_193131_B.func_193168_a(playerIn, playerIn.func_193106_Q());
			}
		}
	}

	public int getEntityViewDistance() {
		return PlayerChunkMap.getFurthestViewableBlock(this.getViewDistance());
	}

	/**
	 * called during player login. reads the player information from disk.
	 */
	public NBTTagCompound readPlayerDataFromFile(EntityPlayerMP playerIn) {
		NBTTagCompound nbttagcompound = this.mcServer.worldServers[0].getWorldInfo().getPlayerNBTTagCompound();
		NBTTagCompound nbttagcompound1;

		if (playerIn.getName().equals(this.mcServer.getServerOwner()) && nbttagcompound != null) {
			nbttagcompound1 = nbttagcompound;
			playerIn.readFromNBT(nbttagcompound);
			LOG.debug("loading single player");
		} else {
			nbttagcompound1 = this.playerNBTManagerObj.readPlayerData(playerIn);
		}

		return nbttagcompound1;
	}

	/**
	 * also stores the NBTTags if this is an intergratedPlayerList
	 */
	protected void writePlayerData(EntityPlayerMP playerIn) {
		this.playerNBTManagerObj.writePlayerData(playerIn);
		StatisticsManagerServer statisticsmanagerserver = this.playerStatFiles.get(playerIn.getUniqueID());

		if (statisticsmanagerserver != null) {
			statisticsmanagerserver.saveStatFile();
		}

		PlayerAdvancements playeradvancements = this.field_192055_p.get(playerIn.getUniqueID());

		if (playeradvancements != null) {
			playeradvancements.func_192749_b();
		}
	}

	/**
	 * Called when a player successfully logs in. Reads player data from disk and
	 * inserts the player into the world.
	 */
	public void playerLoggedIn(EntityPlayerMP playerIn) {
		this.playerEntityList.add(playerIn);
		this.uuidToPlayerMap.put(playerIn.getUniqueID(), playerIn);
		this.sendPacketToAllPlayers(
				new SPacketPlayerListItem(SPacketPlayerListItem.Action.ADD_PLAYER, new EntityPlayerMP[] { playerIn }));
		WorldServer worldserver = this.mcServer.worldServerForDimension(playerIn.dimension);

		for (int i = 0; i < this.playerEntityList.size(); ++i) {
			playerIn.connection.sendPacket(new SPacketPlayerListItem(SPacketPlayerListItem.Action.ADD_PLAYER,
					new EntityPlayerMP[] { this.playerEntityList.get(i) }));
		}

		worldserver.spawnEntityInWorld(playerIn);
		this.preparePlayer(playerIn, (WorldServer) null);
	}

	/**
	 * Using player's dimension, update the chunks around them
	 */
	public void serverUpdateMovingPlayer(EntityPlayerMP playerIn) {
		playerIn.getServerWorld().getPlayerChunkMap().updateMovingPlayer(playerIn);
	}

	/**
	 * Called when a player disconnects from the game. Writes player data to disk
	 * and removes them from the world.
	 */
	public void playerLoggedOut(EntityPlayerMP playerIn) {
		WorldServer worldserver = playerIn.getServerWorld();
		playerIn.addStat(StatList.LEAVE_GAME);
		this.writePlayerData(playerIn);

		if (playerIn.isRiding()) {
			Entity entity = playerIn.getLowestRidingEntity();

			if (entity.getRecursivePassengersByType(EntityPlayerMP.class).size() == 1) {
				LOG.debug("Removing player mount");
				playerIn.dismountRidingEntity();
				worldserver.removeEntityDangerously(entity);

				for (Entity entity1 : entity.getRecursivePassengers()) {
					worldserver.removeEntityDangerously(entity1);
				}

				worldserver.getChunkFromChunkCoords(playerIn.chunkCoordX, playerIn.chunkCoordZ).setChunkModified();
			}
		}

		worldserver.removeEntity(playerIn);
		worldserver.getPlayerChunkMap().removePlayer(playerIn);
		playerIn.func_192039_O().func_192745_a();
		this.playerEntityList.remove(playerIn);
		EaglercraftUUID uuid = playerIn.getUniqueID();
		EntityPlayerMP entityplayermp = this.uuidToPlayerMap.get(uuid);

		if (entityplayermp == playerIn) {
			this.uuidToPlayerMap.remove(uuid);
			this.playerStatFiles.remove(uuid);
			this.field_192055_p.remove(uuid);
		}

		((EaglerMinecraftServer) mcServer).getSkinService().unregisterPlayer(uuid);
		((EaglerMinecraftServer) mcServer).getCapeService().unregisterPlayer(uuid);
		this.sendPacketToAllPlayers(new SPacketPlayerListItem(SPacketPlayerListItem.Action.REMOVE_PLAYER,
				new EntityPlayerMP[] { playerIn }));
	}

	/**
	 * also checks for multiple logins across servers
	 */
	public EntityPlayerMP createPlayerForUser(GameProfile profile) {
		EaglercraftUUID uuid = EntityPlayer.getUUID(profile);
		List<EntityPlayerMP> list = Lists.<EntityPlayerMP>newArrayList();

		for (int i = 0; i < this.playerEntityList.size(); ++i) {
			EntityPlayerMP entityplayermp = this.playerEntityList.get(i);

			if (entityplayermp.getUniqueID().equals(uuid)) {
				list.add(entityplayermp);
			}
		}

		EntityPlayerMP entityplayermp2 = this.uuidToPlayerMap.get(profile.getId());

		if (entityplayermp2 != null && !list.contains(entityplayermp2)) {
			list.add(entityplayermp2);
		}

		for (int i1 = 0; i1 < list.size(); i1++) {
			EntityPlayerMP entityplayermp1 = list.get(i1);
			entityplayermp1.connection.func_194028_b(
					new TextComponentTranslation("multiplayer.disconnect.duplicate_login", new Object[0]));
		}

		PlayerInteractionManager playerinteractionmanager = new PlayerInteractionManager(
				this.mcServer.worldServerForDimension(0));

		return new EntityPlayerMP(this.mcServer, this.mcServer.worldServerForDimension(0), profile,
				playerinteractionmanager);
	}

	/**
	 * Called on respawn
	 */
	public EntityPlayerMP recreatePlayerEntity(EntityPlayerMP playerIn, int dimension, boolean conqueredEnd) {
		playerIn.getServerWorld().getEntityTracker().removePlayerFromTrackers(playerIn);
		playerIn.getServerWorld().getEntityTracker().untrackEntity(playerIn);
		playerIn.getServerWorld().getPlayerChunkMap().removePlayer(playerIn);
		this.playerEntityList.remove(playerIn);
		this.mcServer.worldServerForDimension(playerIn.dimension).removeEntityDangerously(playerIn);
		BlockPos blockpos = playerIn.getBedLocation();
		boolean flag = playerIn.isSpawnForced();
		playerIn.dimension = dimension;
		PlayerInteractionManager playerinteractionmanager = new PlayerInteractionManager(
				this.mcServer.worldServerForDimension(playerIn.dimension));

		EntityPlayerMP entityplayermp = new EntityPlayerMP(this.mcServer,
				this.mcServer.worldServerForDimension(playerIn.dimension), playerIn.getGameProfile(),
				playerinteractionmanager);
		entityplayermp.clientBrandUUID = playerIn.clientBrandUUID;
		entityplayermp.connection = playerIn.connection;
		entityplayermp.func_193104_a(playerIn, conqueredEnd);
		entityplayermp.setEntityId(playerIn.getEntityId());
		entityplayermp.setCommandStats(playerIn);
		entityplayermp.setPrimaryHand(playerIn.getPrimaryHand());

		Set<String> list = playerIn.getTags();
		for (String s : list) {
			entityplayermp.addTag(s);
		}

		WorldServer worldserver = this.mcServer.worldServerForDimension(playerIn.dimension);
		this.setPlayerGameTypeBasedOnOther(entityplayermp, playerIn, worldserver);

		if (blockpos != null) {
			BlockPos blockpos1 = EntityPlayer
					.getBedSpawnLocation(this.mcServer.worldServerForDimension(playerIn.dimension), blockpos, flag);

			if (blockpos1 != null) {
				entityplayermp.setLocationAndAngles((double) ((float) blockpos1.getX() + 0.5F),
						(double) ((float) blockpos1.getY() + 0.1F), (double) ((float) blockpos1.getZ() + 0.5F), 0.0F,
						0.0F);
				entityplayermp.setSpawnPoint(blockpos, flag);
			} else {
				entityplayermp.connection.sendPacket(new SPacketChangeGameState(0, 0.0F));
			}
		}

		worldserver.getChunkProvider().provideChunk((int) entityplayermp.posX >> 4, (int) entityplayermp.posZ >> 4);

		List<AxisAlignedBB> boundingBoxes = worldserver.getCollisionBoxes(entityplayermp,
				entityplayermp.getEntityBoundingBox());
		while (!boundingBoxes.isEmpty() && entityplayermp.posY < 256.0D) {
			entityplayermp.setPosition(entityplayermp.posX, entityplayermp.posY + 1.0D, entityplayermp.posZ);
		}

		entityplayermp.connection.sendPacket(new SPacketRespawn(entityplayermp.dimension,
				entityplayermp.world.getDifficulty(), entityplayermp.world.getWorldInfo().getTerrainType(),
				entityplayermp.interactionManager.getGameType()));
		BlockPos blockpos2 = worldserver.getSpawnPoint();
		entityplayermp.connection.setPlayerLocation(entityplayermp.posX, entityplayermp.posY, entityplayermp.posZ,
				entityplayermp.rotationYaw, entityplayermp.rotationPitch);
		entityplayermp.connection.sendPacket(new SPacketSpawnPosition(blockpos2));
		entityplayermp.connection.sendPacket(new SPacketSetExperience(entityplayermp.experience,
				entityplayermp.experienceTotal, entityplayermp.experienceLevel));
		this.updateTimeAndWeatherForPlayer(entityplayermp, worldserver);
		this.updatePermissionLevel(entityplayermp);
		worldserver.getPlayerChunkMap().addPlayer(entityplayermp);
		worldserver.spawnEntityInWorld(entityplayermp);
		this.playerEntityList.add(entityplayermp);
		this.uuidToPlayerMap.put(entityplayermp.getUniqueID(), entityplayermp);
		entityplayermp.addSelfToInternalCraftingInventory();
		entityplayermp.setHealth(entityplayermp.getHealth());
		return entityplayermp;
	}

	public void updatePermissionLevel(EntityPlayerMP player) {
		int i = 0;
		i = this.mcServer.isSinglePlayer() && this.mcServer.worldServers[0].getWorldInfo().areCommandsAllowed() ? 4 : i;
		i = this.commandsAllowedForAll ? 4 : i;
		this.sendPlayerPermissionLevel(player, i);
	}

	public void changePlayerDimension(EntityPlayerMP player, int dimensionIn) {
		int i = player.dimension;
		WorldServer worldserver = this.mcServer.worldServerForDimension(player.dimension);
		player.dimension = dimensionIn;
		WorldServer worldserver1 = this.mcServer.worldServerForDimension(player.dimension);
		player.connection.sendPacket(new SPacketRespawn(player.dimension, player.world.getDifficulty(),
				player.world.getWorldInfo().getTerrainType(), player.interactionManager.getGameType()));
		this.updatePermissionLevel(player);
		worldserver.removeEntityDangerously(player);
		player.isDead = false;
		this.transferEntityToWorld(player, i, worldserver, worldserver1);
		this.preparePlayer(player, worldserver);
		player.connection.setPlayerLocation(player.posX, player.posY, player.posZ, player.rotationYaw,
				player.rotationPitch);
		player.interactionManager.setWorld(worldserver1);
		player.connection.sendPacket(new SPacketPlayerAbilities(player.capabilities));
		this.updateTimeAndWeatherForPlayer(player, worldserver1);
		this.syncPlayerInventory(player);

		for (PotionEffect potioneffect : player.getActivePotionEffects()) {
			player.connection.sendPacket(new SPacketEntityEffect(player.getEntityId(), potioneffect));
		}
	}

	/**
	 * Transfers an entity from a world to another world.
	 */
	public void transferEntityToWorld(Entity entityIn, int lastDimension, WorldServer oldWorldIn,
			WorldServer toWorldIn) {
		double d0 = entityIn.posX;
		double d1 = entityIn.posZ;
		float f = entityIn.rotationYaw;

		if (entityIn.dimension == -1) {
			d0 = MathHelper.clamp(d0 / 8.0D, toWorldIn.getWorldBorder().minX() + 16.0D,
					toWorldIn.getWorldBorder().maxX() - 16.0D);
			d1 = MathHelper.clamp(d1 / 8.0D, toWorldIn.getWorldBorder().minZ() + 16.0D,
					toWorldIn.getWorldBorder().maxZ() - 16.0D);
			entityIn.setLocationAndAngles(d0, entityIn.posY, d1, entityIn.rotationYaw, entityIn.rotationPitch);

			if (entityIn.isEntityAlive()) {
				oldWorldIn.updateEntityWithOptionalForce(entityIn, false);
			}
		} else if (entityIn.dimension == 0) {
			d0 = MathHelper.clamp(d0 * 8.0D, toWorldIn.getWorldBorder().minX() + 16.0D,
					toWorldIn.getWorldBorder().maxX() - 16.0D);
			d1 = MathHelper.clamp(d1 * 8.0D, toWorldIn.getWorldBorder().minZ() + 16.0D,
					toWorldIn.getWorldBorder().maxZ() - 16.0D);
			entityIn.setLocationAndAngles(d0, entityIn.posY, d1, entityIn.rotationYaw, entityIn.rotationPitch);

			if (entityIn.isEntityAlive()) {
				oldWorldIn.updateEntityWithOptionalForce(entityIn, false);
			}
		} else {
			BlockPos blockpos;

			if (lastDimension == 1) {
				blockpos = toWorldIn.getSpawnPoint();
			} else {
				blockpos = toWorldIn.getSpawnCoordinate();
			}

			d0 = (double) blockpos.getX();
			entityIn.posY = (double) blockpos.getY();
			d1 = (double) blockpos.getZ();
			entityIn.setLocationAndAngles(d0, entityIn.posY, d1, 90.0F, 0.0F);

			if (entityIn.isEntityAlive()) {
				oldWorldIn.updateEntityWithOptionalForce(entityIn, false);
			}
		}

		if (lastDimension != 1) {
			d0 = (double) MathHelper.clamp((int) d0, -29999872, 29999872);
			d1 = (double) MathHelper.clamp((int) d1, -29999872, 29999872);

			if (entityIn.isEntityAlive()) {
				entityIn.setLocationAndAngles(d0, entityIn.posY, d1, entityIn.rotationYaw, entityIn.rotationPitch);
				toWorldIn.getDefaultTeleporter().placeInPortal(entityIn, f);
				toWorldIn.spawnEntityInWorld(entityIn);
				toWorldIn.updateEntityWithOptionalForce(entityIn, false);
			}
		}

		entityIn.setWorld(toWorldIn);
	}

	/**
	 * self explanitory
	 */
	public void onTick() {
		if (++this.playerPingIndex > 600) {
			this.sendPacketToAllPlayers(
					new SPacketPlayerListItem(SPacketPlayerListItem.Action.UPDATE_LATENCY, this.playerEntityList));
			this.playerPingIndex = 0;
		}
	}

	public void sendPacketToAllPlayers(Packet<?> packetIn) {
		for (int i = 0; i < this.playerEntityList.size(); ++i) {
			(this.playerEntityList.get(i)).connection.sendPacket(packetIn);
		}
	}

	public void sendPacketToAllPlayersInDimension(Packet<?> packetIn, int dimension) {
		for (int i = 0; i < this.playerEntityList.size(); ++i) {
			EntityPlayerMP entityplayermp = this.playerEntityList.get(i);

			if (entityplayermp.dimension == dimension) {
				entityplayermp.connection.sendPacket(packetIn);
			}
		}
	}

	public void sendMessageToAllTeamMembers(EntityPlayer player, ITextComponent message) {
		Team team = player.getTeam();

		if (team != null) {
			for (String s : team.getMembershipCollection()) {
				EntityPlayerMP entityplayermp = this.getPlayerByUsername(s);

				if (entityplayermp != null && entityplayermp != player) {
					entityplayermp.addChatMessage(message);
				}
			}
		}
	}

	public void sendMessageToTeamOrAllPlayers(EntityPlayer player, ITextComponent message) {
		Team team = player.getTeam();

		if (team == null) {
			this.sendChatMsg(message);
		} else {
			for (int i = 0; i < this.playerEntityList.size(); ++i) {
				EntityPlayerMP entityplayermp = this.playerEntityList.get(i);

				if (entityplayermp.getTeam() != team) {
					entityplayermp.addChatMessage(message);
				}
			}
		}
	}

	/**
	 * Get a comma separated list of online players.
	 */
	public String getFormattedListOfPlayers(boolean includeUUIDs) {
		String s = "";
		List<EntityPlayerMP> list = Lists.newArrayList(this.playerEntityList);

		for (int i = 0; i < list.size(); ++i) {
			if (i > 0) {
				s = s + ", ";
			}

			s = s + ((EntityPlayerMP) list.get(i)).getName();

			if (includeUUIDs) {
				s = s + " (" + ((EntityPlayerMP) list.get(i)).getCachedUniqueIdString() + ")";
			}
		}

		return s;
	}

	/**
	 * Returns an array of the usernames of all the connected players.
	 */
	public String[] getAllUsernames() {
		String[] astring = new String[this.playerEntityList.size()];

		for (int i = 0; i < this.playerEntityList.size(); ++i) {
			astring[i] = ((EntityPlayerMP) this.playerEntityList.get(i)).getName();
		}

		return astring;
	}

	public GameProfile[] getAllProfiles() {
		GameProfile[] agameprofile = new GameProfile[this.playerEntityList.size()];

		for (int i = 0; i < this.playerEntityList.size(); ++i) {
			agameprofile[i] = ((EntityPlayerMP) this.playerEntityList.get(i)).getGameProfile();
		}

		return agameprofile;
	}

	private void sendPlayerPermissionLevel(EntityPlayerMP player, int permLevel) {
		if (player != null && player.connection != null) {
			byte b0;

			if (permLevel <= 0) {
				b0 = 24;
			} else if (permLevel >= 4) {
				b0 = 28;
			} else {
				b0 = (byte) (24 + permLevel);
			}

			player.connection.sendPacket(new SPacketEntityStatus(player, b0));
		}
	}

	public boolean canJoin(GameProfile profile) {
		return true;
	}

	public boolean canSendCommands(GameProfile profile) {
		return this.mcServer.isSinglePlayer() && this.mcServer.worldServers[0].getWorldInfo().areCommandsAllowed()
				&& this.mcServer.getServerOwner().equalsIgnoreCase(profile.getName()) || this.commandsAllowedForAll;
	}

	public EntityPlayerMP getPlayerByUsername(String username) {
		for (EntityPlayerMP entityplayermp : this.playerEntityList) {
			if (entityplayermp.getName().equalsIgnoreCase(username)) {
				return entityplayermp;
			}
		}

		return null;
	}

	/**
	 * params: srcPlayer,x,y,z,r,dimension. The packet is not sent to the srcPlayer,
	 * but all other players within the search radius
	 */
	public void sendToAllNearExcept(EntityPlayer except, double x, double y, double z, double radius, int dimension,
			Packet<?> packetIn) {
		for (int i = 0; i < this.playerEntityList.size(); ++i) {
			EntityPlayerMP entityplayermp = this.playerEntityList.get(i);

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

	/**
	 * Saves all of the players' current states.
	 */
	public void saveAllPlayerData() {
		for (int i = 0; i < this.playerEntityList.size(); ++i) {
			this.writePlayerData(this.playerEntityList.get(i));
		}
	}

	/**
	 * Updates the time and weather for the given player to those of the given world
	 */
	public void updateTimeAndWeatherForPlayer(EntityPlayerMP playerIn, WorldServer worldIn) {
		WorldBorder worldborder = this.mcServer.worldServers[0].getWorldBorder();
		playerIn.connection.sendPacket(new SPacketWorldBorder(worldborder, SPacketWorldBorder.Action.INITIALIZE));
		playerIn.connection.sendPacket(new SPacketTimeUpdate(worldIn.getTotalWorldTime(), worldIn.getWorldTime(),
				worldIn.getGameRules().getBoolean("doDaylightCycle")));
		BlockPos blockpos = worldIn.getSpawnPoint();
		playerIn.connection.sendPacket(new SPacketSpawnPosition(blockpos));

		if (worldIn.isRaining()) {
			playerIn.connection.sendPacket(new SPacketChangeGameState(1, 0.0F));
			playerIn.connection.sendPacket(new SPacketChangeGameState(7, worldIn.getRainStrength(1.0F)));
			playerIn.connection.sendPacket(new SPacketChangeGameState(8, worldIn.getThunderStrength(1.0F)));
		}
	}

	/**
	 * sends the players inventory to himself
	 */
	public void syncPlayerInventory(EntityPlayerMP playerIn) {
		playerIn.sendContainerToPlayer(playerIn.inventoryContainer);
		playerIn.setPlayerHealthUpdated();
		playerIn.connection.sendPacket(new SPacketHeldItemChange(playerIn.inventory.currentItem));
	}

	/**
	 * Returns the number of players currently on the server.
	 */
	public int getCurrentPlayerCount() {
		return this.playerEntityList.size();
	}

	/**
	 * Returns the maximum number of players allowed on the server.
	 */
	public int getMaxPlayers() {
		return this.maxPlayers;
	}

	/**
	 * Returns an array of usernames for which player.dat exists for.
	 */
	public String[] getAvailablePlayerDat() {
		return this.mcServer.worldServers[0].getSaveHandler().getPlayerNBTManager().getAvailablePlayerDat();
	}

	public void setWhiteListEnabled(boolean whitelistEnabled) {
	}

	public List<EntityPlayerMP> getPlayersMatchingAddress(String address) {
		List<EntityPlayerMP> list = Lists.<EntityPlayerMP>newArrayList();

		for (EntityPlayerMP entityplayermp : this.playerEntityList) {
			if (entityplayermp.getPlayerIP().equals(address)) {
				list.add(entityplayermp);
			}
		}

		return list;
	}

	/**
	 * Gets the View Distance.
	 */
	public int getViewDistance() {
		return this.viewDistance;
	}

	public MinecraftServer getServerInstance() {
		return this.mcServer;
	}

	/**
	 * On integrated servers, returns the host's player data to be written to
	 * level.dat.
	 */
	public NBTTagCompound getHostPlayerData() {
		return null;
	}

	public void setGameType(GameType gameModeIn) {
		this.gameType = gameModeIn;
	}

	private void setPlayerGameTypeBasedOnOther(EntityPlayerMP target, EntityPlayerMP source, World worldIn) {
		if (source != null) {
			target.interactionManager.setGameType(source.interactionManager.getGameType());
		} else if (this.gameType != null) {
			target.interactionManager.setGameType(this.gameType);
		}

		target.interactionManager.initializeGameType(worldIn.getWorldInfo().getGameType());
	}

	/**
	 * Sets whether all players are allowed to use commands (cheats) on the server.
	 */
	public void setCommandsAllowedForAll(boolean p_72387_1_) {
		this.commandsAllowedForAll = p_72387_1_;
	}

	/**
	 * Kicks everyone with "Server closed" as reason.
	 */
	public void removeAllPlayers() {
		for (int i = 0; i < this.playerEntityList.size(); ++i) {
			(this.playerEntityList.get(i)).connection.func_194028_b(
					new TextComponentTranslation("multiplayer.disconnect.server_shutdown", new Object[0]));
		}
	}

	public void sendChatMsgImpl(ITextComponent component, boolean isSystem) {
		this.mcServer.addChatMessage(component);
		ChatType chattype = isSystem ? ChatType.SYSTEM : ChatType.CHAT;
		this.sendPacketToAllPlayers(new SPacketChat(component, chattype));
	}

	/**
	 * Sends the given string to every player as chat message.
	 */
	public void sendChatMsg(ITextComponent component) {
		this.sendChatMsgImpl(component, true);
	}

	public StatisticsManagerServer getPlayerStatsFile(EntityPlayer playerIn) {
		EaglercraftUUID uuid = playerIn.getUniqueID();
		StatisticsManagerServer statisticsmanagerserver = uuid == null ? null
				: (StatisticsManagerServer) this.playerStatFiles.get(uuid);

		if (statisticsmanagerserver == null) {
			VFile2 file1 = new VFile2(this.mcServer.worldServerForDimension(0).getSaveHandler().getWorldDirectory(),
					"stats");
			VFile2 file2 = new VFile2(file1, uuid + ".json");

			if (!file2.exists()) {
				VFile2 file3 = new VFile2(file1, playerIn.getName() + ".json");

				if (file3.exists()) {
					file3.renameTo(file2);
				}
			}

			statisticsmanagerserver = new StatisticsManagerServer(this.mcServer, file2);
			statisticsmanagerserver.readStatFile();
			this.playerStatFiles.put(uuid, statisticsmanagerserver);
		}

		return statisticsmanagerserver;
	}

	public PlayerAdvancements func_192054_h(EntityPlayerMP p_192054_1_) {
		EaglercraftUUID uuid = p_192054_1_.getUniqueID();
		PlayerAdvancements playeradvancements = this.field_192055_p.get(uuid);

		if (playeradvancements == null) {
			VFile2 file1 = new VFile2(this.mcServer.worldServerForDimension(0).getSaveHandler().getWorldDirectory(),
					"advancements");
			VFile2 file2 = new VFile2(file1, uuid + ".json");
			playeradvancements = new PlayerAdvancements(this.mcServer, file2, p_192054_1_);
			this.field_192055_p.put(uuid, playeradvancements);
		}

		playeradvancements.func_192739_a(p_192054_1_);
		return playeradvancements;
	}

	public void setViewDistance(int distance) {
		this.viewDistance = distance;
		int entityViewDist = getEntityViewDistance();
		
		if (this.mcServer.worldServers != null) {
			for (WorldServer worldserver : this.mcServer.worldServers) {
				if (worldserver != null) {
					worldserver.getPlayerChunkMap().setPlayerViewRadius(distance);
					worldserver.getEntityTracker().setViewDistance(distance);
					worldserver.getEntityTracker().updateMaxTrackingThreshold(entityViewDist);
				}
			}
		}
	}

	public void updatePlayerViewDistance(EntityPlayerMP entityPlayerMP, int viewDistance2) {
		if (entityPlayerMP.getName().equals(mcServer.getServerOwner())) {
			if (viewDistance != viewDistance2) {
				LOG.info("Owner is setting view distance: {}", viewDistance2);
				setViewDistance(viewDistance2);
			}
		}
	}

	public List<EntityPlayerMP> getPlayerList() {
		return this.playerEntityList;
	}

	/**
	 * Get's the EntityPlayerMP object representing the player with the UUID.
	 */
	public EntityPlayerMP getPlayerByUUID(EaglercraftUUID playerUUID) {
		return this.uuidToPlayerMap.get(playerUUID);
	}

	public boolean bypassesPlayerLimit(GameProfile profile) {
		return false;
	}

	public void func_193244_w() {
		for (PlayerAdvancements playeradvancements : this.field_192055_p.values()) {
			playeradvancements.func_193766_b();
		}
	}
}