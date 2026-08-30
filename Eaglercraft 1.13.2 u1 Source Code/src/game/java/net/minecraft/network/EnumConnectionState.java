package net.minecraft.network;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.network.handshake.client.CPacketHandshake;
import net.minecraft.network.login.client.CPacketCustomPayloadLogin;
import net.minecraft.network.login.client.CPacketEncryptionResponse;
import net.minecraft.network.login.client.CPacketLoginStart;
import net.minecraft.network.login.server.SPacketCustomPayloadLogin;
import net.minecraft.network.login.server.SPacketDisconnectLogin;
import net.minecraft.network.login.server.SPacketEnableCompression;
import net.minecraft.network.login.server.SPacketEncryptionRequest;
import net.minecraft.network.login.server.SPacketLoginSuccess;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.client.CPacketClickWindow;
import net.minecraft.network.play.client.CPacketClientSettings;
import net.minecraft.network.play.client.CPacketClientStatus;
import net.minecraft.network.play.client.CPacketCloseWindow;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketConfirmTransaction;
import net.minecraft.network.play.client.CPacketCreativeInventoryAction;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraft.network.play.client.CPacketEditBook;
import net.minecraft.network.play.client.CPacketEnchantItem;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketInput;
import net.minecraft.network.play.client.CPacketKeepAlive;
import net.minecraft.network.play.client.CPacketNBTQueryEntity;
import net.minecraft.network.play.client.CPacketNBTQueryTileEntity;
import net.minecraft.network.play.client.CPacketPickItem;
import net.minecraft.network.play.client.CPacketPlaceRecipe;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerAbilities;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketRecipeInfo;
import net.minecraft.network.play.client.CPacketRenameItem;
import net.minecraft.network.play.client.CPacketResourcePackStatus;
import net.minecraft.network.play.client.CPacketSeenAdvancements;
import net.minecraft.network.play.client.CPacketSelectTrade;
import net.minecraft.network.play.client.CPacketSpectate;
import net.minecraft.network.play.client.CPacketSteerBoat;
import net.minecraft.network.play.client.CPacketTabComplete;
import net.minecraft.network.play.client.CPacketUpdateBeacon;
import net.minecraft.network.play.client.CPacketUpdateCommandBlock;
import net.minecraft.network.play.client.CPacketUpdateCommandMinecart;
import net.minecraft.network.play.client.CPacketUpdateSign;
import net.minecraft.network.play.client.CPacketUpdateStructureBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.client.CPacketVehicleMove;
import net.minecraft.network.play.server.SPacketAdvancementInfo;
import net.minecraft.network.play.server.SPacketAnimation;
import net.minecraft.network.play.server.SPacketBlockAction;
import net.minecraft.network.play.server.SPacketBlockBreakAnim;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.network.play.server.SPacketCamera;
import net.minecraft.network.play.server.SPacketChangeGameState;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.network.play.server.SPacketCloseWindow;
import net.minecraft.network.play.server.SPacketCollectItem;
import net.minecraft.network.play.server.SPacketCombatEvent;
import net.minecraft.network.play.server.SPacketCommandList;
import net.minecraft.network.play.server.SPacketConfirmTransaction;
import net.minecraft.network.play.server.SPacketCooldown;
import net.minecraft.network.play.server.SPacketCustomPayload;
import net.minecraft.network.play.server.SPacketCustomSound;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraft.network.play.server.SPacketDisconnect;
import net.minecraft.network.play.server.SPacketDisplayObjective;
import net.minecraft.network.play.server.SPacketEffect;
import net.minecraft.network.play.server.SPacketEntity;
import net.minecraft.network.play.server.SPacketEntityAttach;
import net.minecraft.network.play.server.SPacketEntityEffect;
import net.minecraft.network.play.server.SPacketEntityEquipment;
import net.minecraft.network.play.server.SPacketEntityHeadLook;
import net.minecraft.network.play.server.SPacketEntityMetadata;
import net.minecraft.network.play.server.SPacketEntityProperties;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.network.play.server.SPacketEntityTeleport;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.network.play.server.SPacketHeldItemChange;
import net.minecraft.network.play.server.SPacketJoinGame;
import net.minecraft.network.play.server.SPacketKeepAlive;
import net.minecraft.network.play.server.SPacketMaps;
import net.minecraft.network.play.server.SPacketMoveVehicle;
import net.minecraft.network.play.server.SPacketMultiBlockChange;
import net.minecraft.network.play.server.SPacketNBTQueryResponse;
import net.minecraft.network.play.server.SPacketOpenWindow;
import net.minecraft.network.play.server.SPacketParticles;
import net.minecraft.network.play.server.SPacketPlaceGhostRecipe;
import net.minecraft.network.play.server.SPacketPlayerAbilities;
import net.minecraft.network.play.server.SPacketPlayerListHeaderFooter;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import net.minecraft.network.play.server.SPacketPlayerLook;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.network.play.server.SPacketRecipeBook;
import net.minecraft.network.play.server.SPacketRemoveEntityEffect;
import net.minecraft.network.play.server.SPacketResourcePackSend;
import net.minecraft.network.play.server.SPacketRespawn;
import net.minecraft.network.play.server.SPacketScoreboardObjective;
import net.minecraft.network.play.server.SPacketSelectAdvancementsTab;
import net.minecraft.network.play.server.SPacketServerDifficulty;
import net.minecraft.network.play.server.SPacketSetExperience;
import net.minecraft.network.play.server.SPacketSetPassengers;
import net.minecraft.network.play.server.SPacketSetSlot;
import net.minecraft.network.play.server.SPacketSignEditorOpen;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketSpawnExperienceOrb;
import net.minecraft.network.play.server.SPacketSpawnGlobalEntity;
import net.minecraft.network.play.server.SPacketSpawnMob;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.network.play.server.SPacketSpawnPainting;
import net.minecraft.network.play.server.SPacketSpawnPlayer;
import net.minecraft.network.play.server.SPacketSpawnPosition;
import net.minecraft.network.play.server.SPacketStatistics;
import net.minecraft.network.play.server.SPacketStopSound;
import net.minecraft.network.play.server.SPacketTabComplete;
import net.minecraft.network.play.server.SPacketTagsList;
import net.minecraft.network.play.server.SPacketTeams;
import net.minecraft.network.play.server.SPacketTimeUpdate;
import net.minecraft.network.play.server.SPacketTitle;
import net.minecraft.network.play.server.SPacketUnloadChunk;
import net.minecraft.network.play.server.SPacketUpdateBossInfo;
import net.minecraft.network.play.server.SPacketUpdateHealth;
import net.minecraft.network.play.server.SPacketUpdateRecipes;
import net.minecraft.network.play.server.SPacketUpdateScore;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.network.play.server.SPacketUseBed;
import net.minecraft.network.play.server.SPacketWindowItems;
import net.minecraft.network.play.server.SPacketWindowProperty;
import net.minecraft.network.play.server.SPacketWorldBorder;
import net.minecraft.network.status.client.CPacketPing;
import net.minecraft.network.status.client.CPacketServerQuery;
import net.minecraft.network.status.server.SPacketPong;
import net.minecraft.network.status.server.SPacketServerInfo;
import org.apache.logging.log4j.LogManager;

public enum EnumConnectionState {
   HANDSHAKING(-1) {
      {
         this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketHandshake.class);
      }
   },
   PLAY(0) {
      {
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketSpawnObject.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketSpawnExperienceOrb.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketSpawnGlobalEntity.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketSpawnMob.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketSpawnPainting.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketSpawnPlayer.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketAnimation.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketStatistics.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketBlockBreakAnim.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketUpdateTileEntity.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketBlockAction.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketBlockChange.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketUpdateBossInfo.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketServerDifficulty.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketChat.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketMultiBlockChange.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketTabComplete.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketCommandList.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketConfirmTransaction.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketCloseWindow.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketOpenWindow.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketWindowItems.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketWindowProperty.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketSetSlot.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketCooldown.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketCustomPayload.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketCustomSound.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketDisconnect.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketEntityStatus.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketNBTQueryResponse.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketExplosion.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketUnloadChunk.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketChangeGameState.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketKeepAlive.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketChunkData.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketEffect.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketParticles.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketJoinGame.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketMaps.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketEntity.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketEntity.RelMove.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketEntity.Move.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketEntity.Look.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketMoveVehicle.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketSignEditorOpen.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketPlaceGhostRecipe.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketPlayerAbilities.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketCombatEvent.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketPlayerListItem.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketPlayerLook.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketPlayerPosLook.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketUseBed.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketRecipeBook.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketDestroyEntities.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketRemoveEntityEffect.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketResourcePackSend.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketRespawn.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketEntityHeadLook.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketSelectAdvancementsTab.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketWorldBorder.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketCamera.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketHeldItemChange.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketDisplayObjective.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketEntityMetadata.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketEntityAttach.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketEntityVelocity.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketEntityEquipment.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketSetExperience.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketUpdateHealth.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketScoreboardObjective.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketSetPassengers.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketTeams.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketUpdateScore.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketSpawnPosition.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketTimeUpdate.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketTitle.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketStopSound.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketSoundEffect.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketPlayerListHeaderFooter.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketCollectItem.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketEntityTeleport.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketAdvancementInfo.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketEntityProperties.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketEntityEffect.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketUpdateRecipes.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketTagsList.class);
         this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketConfirmTeleport.class);
         this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketNBTQueryTileEntity.class);
         this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketChatMessage.class);
         this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketClientStatus.class);
         this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketClientSettings.class);
         this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketTabComplete.class);
         this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketConfirmTransaction.class);
         this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketEnchantItem.class);
         this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketClickWindow.class);
         this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketCloseWindow.class);
         this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketCustomPayload.class);
         this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketEditBook.class);
         this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketNBTQueryEntity.class);
         this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketUseEntity.class);
         this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketKeepAlive.class);
         this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketPlayer.class);
         this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketPlayer.Position.class);
         this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketPlayer.PositionRotation.class);
         this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketPlayer.Rotation.class);
         this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketVehicleMove.class);
         this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketSteerBoat.class);
         this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketPickItem.class);
         this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketPlaceRecipe.class);
         this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketPlayerAbilities.class);
         this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketPlayerDigging.class);
         this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketEntityAction.class);
         this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketInput.class);
         this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketRecipeInfo.class);
         this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketRenameItem.class);
         this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketResourcePackStatus.class);
         this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketSeenAdvancements.class);
         this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketSelectTrade.class);
         this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketUpdateBeacon.class);
         this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketHeldItemChange.class);
         this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketUpdateCommandBlock.class);
         this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketUpdateCommandMinecart.class);
         this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketCreativeInventoryAction.class);
         this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketUpdateStructureBlock.class);
         this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketUpdateSign.class);
         this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketAnimation.class);
         this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketSpectate.class);
         this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketPlayerTryUseItemOnBlock.class);
         this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketPlayerTryUseItem.class);
      }
   },
   STATUS(1) {
      {
         this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketServerQuery.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketServerInfo.class);
         this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketPing.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketPong.class);
      }
   },
   LOGIN(2) {
      {
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketDisconnectLogin.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketEncryptionRequest.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketLoginSuccess.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketEnableCompression.class);
         this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketCustomPayloadLogin.class);
         this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketLoginStart.class);
         this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketEncryptionResponse.class);
         this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketCustomPayloadLogin.class);
      }
   };

   private static final EnumConnectionState[] STATES_BY_ID = new EnumConnectionState[4];
   private static final Map<Class<? extends Packet<?>>, EnumConnectionState> STATES_BY_CLASS = Maps.newHashMap();
   private final int id;
   private final Map<EnumPacketDirection, BiMap<Integer, Class<? extends Packet<?>>>> directionMaps = Maps.newEnumMap(EnumPacketDirection.class);

   private EnumConnectionState(int protocolId) {
      this.id = protocolId;
   }

   protected EnumConnectionState registerPacket(EnumPacketDirection direction, Class<? extends Packet<?>> packetClass) {
      BiMap<Integer, Class<? extends Packet<?>>> bimap = this.directionMaps.get(direction);
      if (bimap == null) {
         bimap = HashBiMap.create();
         this.directionMaps.put(direction, bimap);
      }

      if (bimap.containsValue(packetClass)) {
         String s = direction + " packet " + packetClass + " is already known to ID " + bimap.inverse().get(packetClass);
         LogManager.getLogger().fatal(s);
         throw new IllegalArgumentException(s);
      } else {
         bimap.put(bimap.size(), packetClass);
         return this;
      }
   }

   public Integer getPacketId(EnumPacketDirection direction, Packet<?> packetIn) throws Exception {
      return this.directionMaps.get(direction).inverse().get(packetIn.getClass());
   }

   @Nullable
   private static Packet<?> createPacketInstance(Class<? extends Packet<?>> packetClass) {
      if (packetClass == CPacketHandshake.class) return new CPacketHandshake();
      if (packetClass == SPacketSpawnObject.class) return new SPacketSpawnObject();
      if (packetClass == SPacketSpawnExperienceOrb.class) return new SPacketSpawnExperienceOrb();
      if (packetClass == SPacketSpawnGlobalEntity.class) return new SPacketSpawnGlobalEntity();
      if (packetClass == SPacketSpawnMob.class) return new SPacketSpawnMob();
      if (packetClass == SPacketSpawnPainting.class) return new SPacketSpawnPainting();
      if (packetClass == SPacketSpawnPlayer.class) return new SPacketSpawnPlayer();
      if (packetClass == SPacketAnimation.class) return new SPacketAnimation();
      if (packetClass == SPacketStatistics.class) return new SPacketStatistics();
      if (packetClass == SPacketBlockBreakAnim.class) return new SPacketBlockBreakAnim();
      if (packetClass == SPacketUpdateTileEntity.class) return new SPacketUpdateTileEntity();
      if (packetClass == SPacketBlockAction.class) return new SPacketBlockAction();
      if (packetClass == SPacketBlockChange.class) return new SPacketBlockChange();
      if (packetClass == SPacketUpdateBossInfo.class) return new SPacketUpdateBossInfo();
      if (packetClass == SPacketServerDifficulty.class) return new SPacketServerDifficulty();
      if (packetClass == SPacketChat.class) return new SPacketChat();
      if (packetClass == SPacketMultiBlockChange.class) return new SPacketMultiBlockChange();
      if (packetClass == SPacketTabComplete.class) return new SPacketTabComplete();
      if (packetClass == SPacketCommandList.class) return new SPacketCommandList();
      if (packetClass == SPacketConfirmTransaction.class) return new SPacketConfirmTransaction();
      if (packetClass == SPacketCloseWindow.class) return new SPacketCloseWindow();
      if (packetClass == SPacketOpenWindow.class) return new SPacketOpenWindow();
      if (packetClass == SPacketWindowItems.class) return new SPacketWindowItems();
      if (packetClass == SPacketWindowProperty.class) return new SPacketWindowProperty();
      if (packetClass == SPacketSetSlot.class) return new SPacketSetSlot();
      if (packetClass == SPacketCooldown.class) return new SPacketCooldown();
      if (packetClass == SPacketCustomPayload.class) return new SPacketCustomPayload();
      if (packetClass == SPacketCustomSound.class) return new SPacketCustomSound();
      if (packetClass == SPacketDisconnect.class) return new SPacketDisconnect();
      if (packetClass == SPacketEntityStatus.class) return new SPacketEntityStatus();
      if (packetClass == SPacketNBTQueryResponse.class) return new SPacketNBTQueryResponse();
      if (packetClass == SPacketExplosion.class) return new SPacketExplosion();
      if (packetClass == SPacketUnloadChunk.class) return new SPacketUnloadChunk();
      if (packetClass == SPacketChangeGameState.class) return new SPacketChangeGameState();
      if (packetClass == SPacketKeepAlive.class) return new SPacketKeepAlive();
      if (packetClass == SPacketChunkData.class) return new SPacketChunkData();
      if (packetClass == SPacketEffect.class) return new SPacketEffect();
      if (packetClass == SPacketParticles.class) return new SPacketParticles();
      if (packetClass == SPacketJoinGame.class) return new SPacketJoinGame();
      if (packetClass == SPacketMaps.class) return new SPacketMaps();
      if (packetClass == SPacketEntity.class) return new SPacketEntity();
      if (packetClass == SPacketEntity.RelMove.class) return new SPacketEntity.RelMove();
      if (packetClass == SPacketEntity.Move.class) return new SPacketEntity.Move();
      if (packetClass == SPacketEntity.Look.class) return new SPacketEntity.Look();
      if (packetClass == SPacketMoveVehicle.class) return new SPacketMoveVehicle();
      if (packetClass == SPacketSignEditorOpen.class) return new SPacketSignEditorOpen();
      if (packetClass == SPacketPlaceGhostRecipe.class) return new SPacketPlaceGhostRecipe();
      if (packetClass == SPacketPlayerAbilities.class) return new SPacketPlayerAbilities();
      if (packetClass == SPacketCombatEvent.class) return new SPacketCombatEvent();
      if (packetClass == SPacketPlayerListItem.class) return new SPacketPlayerListItem();
      if (packetClass == SPacketPlayerLook.class) return new SPacketPlayerLook();
      if (packetClass == SPacketPlayerPosLook.class) return new SPacketPlayerPosLook();
      if (packetClass == SPacketUseBed.class) return new SPacketUseBed();
      if (packetClass == SPacketRecipeBook.class) return new SPacketRecipeBook();
      if (packetClass == SPacketDestroyEntities.class) return new SPacketDestroyEntities();
      if (packetClass == SPacketRemoveEntityEffect.class) return new SPacketRemoveEntityEffect();
      if (packetClass == SPacketResourcePackSend.class) return new SPacketResourcePackSend();
      if (packetClass == SPacketRespawn.class) return new SPacketRespawn();
      if (packetClass == SPacketEntityHeadLook.class) return new SPacketEntityHeadLook();
      if (packetClass == SPacketSelectAdvancementsTab.class) return new SPacketSelectAdvancementsTab();
      if (packetClass == SPacketWorldBorder.class) return new SPacketWorldBorder();
      if (packetClass == SPacketCamera.class) return new SPacketCamera();
      if (packetClass == SPacketHeldItemChange.class) return new SPacketHeldItemChange();
      if (packetClass == SPacketDisplayObjective.class) return new SPacketDisplayObjective();
      if (packetClass == SPacketEntityMetadata.class) return new SPacketEntityMetadata();
      if (packetClass == SPacketEntityAttach.class) return new SPacketEntityAttach();
      if (packetClass == SPacketEntityVelocity.class) return new SPacketEntityVelocity();
      if (packetClass == SPacketEntityEquipment.class) return new SPacketEntityEquipment();
      if (packetClass == SPacketSetExperience.class) return new SPacketSetExperience();
      if (packetClass == SPacketUpdateHealth.class) return new SPacketUpdateHealth();
      if (packetClass == SPacketScoreboardObjective.class) return new SPacketScoreboardObjective();
      if (packetClass == SPacketSetPassengers.class) return new SPacketSetPassengers();
      if (packetClass == SPacketTeams.class) return new SPacketTeams();
      if (packetClass == SPacketUpdateScore.class) return new SPacketUpdateScore();
      if (packetClass == SPacketSpawnPosition.class) return new SPacketSpawnPosition();
      if (packetClass == SPacketTimeUpdate.class) return new SPacketTimeUpdate();
      if (packetClass == SPacketTitle.class) return new SPacketTitle();
      if (packetClass == SPacketStopSound.class) return new SPacketStopSound();
      if (packetClass == SPacketSoundEffect.class) return new SPacketSoundEffect();
      if (packetClass == SPacketPlayerListHeaderFooter.class) return new SPacketPlayerListHeaderFooter();
      if (packetClass == SPacketCollectItem.class) return new SPacketCollectItem();
      if (packetClass == SPacketEntityTeleport.class) return new SPacketEntityTeleport();
      if (packetClass == SPacketAdvancementInfo.class) return new SPacketAdvancementInfo();
      if (packetClass == SPacketEntityProperties.class) return new SPacketEntityProperties();
      if (packetClass == SPacketEntityEffect.class) return new SPacketEntityEffect();
      if (packetClass == SPacketUpdateRecipes.class) return new SPacketUpdateRecipes();
      if (packetClass == SPacketTagsList.class) return new SPacketTagsList();
      if (packetClass == CPacketConfirmTeleport.class) return new CPacketConfirmTeleport();
      if (packetClass == CPacketNBTQueryTileEntity.class) return new CPacketNBTQueryTileEntity();
      if (packetClass == CPacketChatMessage.class) return new CPacketChatMessage();
      if (packetClass == CPacketClientStatus.class) return new CPacketClientStatus();
      if (packetClass == CPacketClientSettings.class) return new CPacketClientSettings();
      if (packetClass == CPacketTabComplete.class) return new CPacketTabComplete();
      if (packetClass == CPacketConfirmTransaction.class) return new CPacketConfirmTransaction();
      if (packetClass == CPacketEnchantItem.class) return new CPacketEnchantItem();
      if (packetClass == CPacketClickWindow.class) return new CPacketClickWindow();
      if (packetClass == CPacketCloseWindow.class) return new CPacketCloseWindow();
      if (packetClass == CPacketCustomPayload.class) return new CPacketCustomPayload();
      if (packetClass == CPacketEditBook.class) return new CPacketEditBook();
      if (packetClass == CPacketNBTQueryEntity.class) return new CPacketNBTQueryEntity();
      if (packetClass == CPacketUseEntity.class) return new CPacketUseEntity();
      if (packetClass == CPacketKeepAlive.class) return new CPacketKeepAlive();
      if (packetClass == CPacketPlayer.class) return new CPacketPlayer();
      if (packetClass == CPacketPlayer.Position.class) return new CPacketPlayer.Position();
      if (packetClass == CPacketPlayer.PositionRotation.class) return new CPacketPlayer.PositionRotation();
      if (packetClass == CPacketPlayer.Rotation.class) return new CPacketPlayer.Rotation();
      if (packetClass == CPacketVehicleMove.class) return new CPacketVehicleMove();
      if (packetClass == CPacketSteerBoat.class) return new CPacketSteerBoat();
      if (packetClass == CPacketPickItem.class) return new CPacketPickItem();
      if (packetClass == CPacketPlaceRecipe.class) return new CPacketPlaceRecipe();
      if (packetClass == CPacketPlayerAbilities.class) return new CPacketPlayerAbilities();
      if (packetClass == CPacketPlayerDigging.class) return new CPacketPlayerDigging();
      if (packetClass == CPacketEntityAction.class) return new CPacketEntityAction();
      if (packetClass == CPacketInput.class) return new CPacketInput();
      if (packetClass == CPacketRecipeInfo.class) return new CPacketRecipeInfo();
      if (packetClass == CPacketRenameItem.class) return new CPacketRenameItem();
      if (packetClass == CPacketResourcePackStatus.class) return new CPacketResourcePackStatus();
      if (packetClass == CPacketSeenAdvancements.class) return new CPacketSeenAdvancements();
      if (packetClass == CPacketSelectTrade.class) return new CPacketSelectTrade();
      if (packetClass == CPacketUpdateBeacon.class) return new CPacketUpdateBeacon();
      if (packetClass == CPacketHeldItemChange.class) return new CPacketHeldItemChange();
      if (packetClass == CPacketUpdateCommandBlock.class) return new CPacketUpdateCommandBlock();
      if (packetClass == CPacketUpdateCommandMinecart.class) return new CPacketUpdateCommandMinecart();
      if (packetClass == CPacketCreativeInventoryAction.class) return new CPacketCreativeInventoryAction();
      if (packetClass == CPacketUpdateStructureBlock.class) return new CPacketUpdateStructureBlock();
      if (packetClass == CPacketUpdateSign.class) return new CPacketUpdateSign();
      if (packetClass == CPacketAnimation.class) return new CPacketAnimation();
      if (packetClass == CPacketSpectate.class) return new CPacketSpectate();
      if (packetClass == CPacketPlayerTryUseItemOnBlock.class) return new CPacketPlayerTryUseItemOnBlock();
      if (packetClass == CPacketPlayerTryUseItem.class) return new CPacketPlayerTryUseItem();
      if (packetClass == CPacketServerQuery.class) return new CPacketServerQuery();
      if (packetClass == SPacketServerInfo.class) return new SPacketServerInfo();
      if (packetClass == CPacketPing.class) return new CPacketPing();
      if (packetClass == SPacketPong.class) return new SPacketPong();
      if (packetClass == SPacketDisconnectLogin.class) return new SPacketDisconnectLogin();
      if (packetClass == SPacketEncryptionRequest.class) return new SPacketEncryptionRequest();
      if (packetClass == SPacketLoginSuccess.class) return new SPacketLoginSuccess();
      if (packetClass == SPacketEnableCompression.class) return new SPacketEnableCompression();
      if (packetClass == SPacketCustomPayloadLogin.class) return new SPacketCustomPayloadLogin();
      if (packetClass == CPacketLoginStart.class) return new CPacketLoginStart();
      if (packetClass == CPacketEncryptionResponse.class) return new CPacketEncryptionResponse();
      if (packetClass == CPacketCustomPayloadLogin.class) return new CPacketCustomPayloadLogin();
      return null;
   }

   @Nullable
   public Packet<?> getPacket(EnumPacketDirection direction, int packetId) throws IllegalAccessException, InstantiationException {
      Class<? extends Packet<?>> oclass = this.directionMaps.get(direction).get(Integer.valueOf(packetId));
      if (oclass == null) {
         return null;
      }
      Packet<?> packet = createPacketInstance(oclass);
      if (packet == null) {
         throw new InstantiationException(oclass.getName());
      }
      return packet;
   }

   public Packet<?> createPacket(EnumPacketDirection direction, int packetId, PacketBuffer buf) throws IOException {
      try {
         Packet<?> packet = this.getPacket(direction, packetId);
         if (packet != null) {
            packet.readPacketData(buf);
         }

         return packet;
      } catch (IllegalAccessException | InstantiationException exception) {
         throw new IOException(exception);
      }
   }

   public int getId() {
      return this.id;
   }

   public static EnumConnectionState getById(int stateId) {
      return stateId >= -1 && stateId <= 2 ? STATES_BY_ID[stateId - -1] : null;
   }

   public static EnumConnectionState getFromPacket(Packet<?> packetIn) {
      return STATES_BY_CLASS.get(packetIn.getClass());
   }

   static {
      for(EnumConnectionState enumconnectionstate : values()) {
         int i = enumconnectionstate.getId();
         if (i < -1 || i > 2) {
            throw new Error("Invalid protocol ID " + Integer.toString(i));
         }

         STATES_BY_ID[i - -1] = enumconnectionstate;

         for(EnumPacketDirection enumpacketdirection : enumconnectionstate.directionMaps.keySet()) {
            for(Class<? extends Packet<?>> oclass : enumconnectionstate.directionMaps.get(enumpacketdirection).values()) {
               if (STATES_BY_CLASS.containsKey(oclass) && STATES_BY_CLASS.get(oclass) != enumconnectionstate) {
                  throw new Error("Packet " + oclass + " is already assigned to protocol " + STATES_BY_CLASS.get(oclass) + " - can't reassign to " + enumconnectionstate);
               }

               if (createPacketInstance(oclass) == null) {
                  throw new Error("Packet " + oclass + " fails instantiation checks! " + oclass);
               }

               STATES_BY_CLASS.put(oclass, enumconnectionstate);
            }
         }
      }

   }
}
