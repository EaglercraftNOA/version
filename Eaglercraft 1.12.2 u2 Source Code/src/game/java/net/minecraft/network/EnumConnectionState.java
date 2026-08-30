package net.minecraft.network;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

import javax.annotation.Nullable;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.CPacketEncryptionResponse;
import net.minecraft.network.login.client.CPacketLoginStart;
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
import net.minecraft.network.play.client.CPacketEnchantItem;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketInput;
import net.minecraft.network.play.client.CPacketKeepAlive;
import net.minecraft.network.play.client.CPacketPlaceRecipe;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerAbilities;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketRecipeInfo;
import net.minecraft.network.play.client.CPacketResourcePackStatus;
import net.minecraft.network.play.client.CPacketSeenAdvancements;
import net.minecraft.network.play.client.CPacketSpectate;
import net.minecraft.network.play.client.CPacketSteerBoat;
import net.minecraft.network.play.client.CPacketTabComplete;
import net.minecraft.network.play.client.CPacketUpdateSign;
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
import net.minecraft.network.play.server.SPacketOpenWindow;
import net.minecraft.network.play.server.SPacketParticles;
import net.minecraft.network.play.server.SPacketPlaceGhostRecipe;
import net.minecraft.network.play.server.SPacketPlayerAbilities;
import net.minecraft.network.play.server.SPacketPlayerListHeaderFooter;
import net.minecraft.network.play.server.SPacketPlayerListItem;
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
import net.minecraft.network.play.server.SPacketTabComplete;
import net.minecraft.network.play.server.SPacketTeams;
import net.minecraft.network.play.server.SPacketTimeUpdate;
import net.minecraft.network.play.server.SPacketTitle;
import net.minecraft.network.play.server.SPacketUnloadChunk;
import net.minecraft.network.play.server.SPacketUpdateBossInfo;
import net.minecraft.network.play.server.SPacketUpdateHealth;
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
			this.registerPacket(EnumPacketDirection.SERVERBOUND, C00Handshake.class, C00Handshake::new);
		}
	},
	PLAY(0) {
		{
			this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketSpawnObject.class, SPacketSpawnObject::new);
			this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketSpawnExperienceOrb.class, SPacketSpawnExperienceOrb::new);
			this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketSpawnGlobalEntity.class, SPacketSpawnGlobalEntity::new);
			this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketSpawnMob.class, SPacketSpawnMob::new);
			this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketSpawnPainting.class, SPacketSpawnPainting::new);
			this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketSpawnPlayer.class, SPacketSpawnPlayer::new);
			this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketAnimation.class, SPacketAnimation::new);
			this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketStatistics.class, SPacketStatistics::new);
			this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketBlockBreakAnim.class, SPacketBlockBreakAnim::new);
			this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketUpdateTileEntity.class, SPacketUpdateTileEntity::new);
			this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketBlockAction.class, SPacketBlockAction::new);
			this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketBlockChange.class, SPacketBlockChange::new);
			this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketUpdateBossInfo.class, SPacketUpdateBossInfo::new);
			this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketServerDifficulty.class, SPacketServerDifficulty::new);
			this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketTabComplete.class, SPacketTabComplete::new);
			this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketChat.class, SPacketChat::new);
			this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketMultiBlockChange.class, SPacketMultiBlockChange::new);
			this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketConfirmTransaction.class, SPacketConfirmTransaction::new);
			this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketCloseWindow.class, SPacketCloseWindow::new);
			this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketOpenWindow.class, SPacketOpenWindow::new);
			this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketWindowItems.class, SPacketWindowItems::new);
			this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketWindowProperty.class, SPacketWindowProperty::new);
			this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketSetSlot.class, SPacketSetSlot::new);
			this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketCooldown.class, SPacketCooldown::new);
			this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketCustomPayload.class, SPacketCustomPayload::new);
			this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketCustomSound.class, SPacketCustomSound::new);
			this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketDisconnect.class, SPacketDisconnect::new);
			this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketEntityStatus.class, SPacketEntityStatus::new);
			this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketExplosion.class, SPacketExplosion::new);
			this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketUnloadChunk.class, SPacketUnloadChunk::new);
			this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketChangeGameState.class, SPacketChangeGameState::new);
			this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketKeepAlive.class, SPacketKeepAlive::new);
			this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketChunkData.class, SPacketChunkData::new);
			this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketEffect.class, SPacketEffect::new);
			this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketParticles.class, SPacketParticles::new);
			this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketJoinGame.class, SPacketJoinGame::new);
			this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketMaps.class, SPacketMaps::new);
			this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketEntity.class, SPacketEntity::new);
			this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketEntity.S15PacketEntityRelMove.class, SPacketEntity.S15PacketEntityRelMove::new);
			this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketEntity.S17PacketEntityLookMove.class, SPacketEntity.S17PacketEntityLookMove::new);
			this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketEntity.S16PacketEntityLook.class, SPacketEntity.S16PacketEntityLook::new);
			this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketMoveVehicle.class, SPacketMoveVehicle::new);
			this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketSignEditorOpen.class, SPacketSignEditorOpen::new);
			this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketPlaceGhostRecipe.class, SPacketPlaceGhostRecipe::new);
			this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketPlayerAbilities.class, SPacketPlayerAbilities::new);
			this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketCombatEvent.class, SPacketCombatEvent::new);
			this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketPlayerListItem.class, SPacketPlayerListItem::new);
			this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketPlayerPosLook.class, SPacketPlayerPosLook::new);
			this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketUseBed.class, SPacketUseBed::new);
			this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketRecipeBook.class, SPacketRecipeBook::new);
			this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketDestroyEntities.class, SPacketDestroyEntities::new);
			this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketRemoveEntityEffect.class, SPacketRemoveEntityEffect::new);
			this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketResourcePackSend.class, SPacketResourcePackSend::new);
			this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketRespawn.class, SPacketRespawn::new);
			this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketEntityHeadLook.class, SPacketEntityHeadLook::new);
			this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketSelectAdvancementsTab.class, SPacketSelectAdvancementsTab::new);
			this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketWorldBorder.class, SPacketWorldBorder::new);
			this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketCamera.class, SPacketCamera::new);
			this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketHeldItemChange.class, SPacketHeldItemChange::new);
			this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketDisplayObjective.class, SPacketDisplayObjective::new);
			this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketEntityMetadata.class, SPacketEntityMetadata::new);
			this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketEntityAttach.class, SPacketEntityAttach::new);
			this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketEntityVelocity.class, SPacketEntityVelocity::new);
			this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketEntityEquipment.class, SPacketEntityEquipment::new);
			this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketSetExperience.class, SPacketSetExperience::new);
			this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketUpdateHealth.class, SPacketUpdateHealth::new);
			this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketScoreboardObjective.class, SPacketScoreboardObjective::new);
			this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketSetPassengers.class, SPacketSetPassengers::new);
			this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketTeams.class, SPacketTeams::new);
			this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketUpdateScore.class, SPacketUpdateScore::new);
			this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketSpawnPosition.class, SPacketSpawnPosition::new);
			this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketTimeUpdate.class, SPacketTimeUpdate::new);
			this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketTitle.class, SPacketTitle::new);
			this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketSoundEffect.class, SPacketSoundEffect::new);
			this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketPlayerListHeaderFooter.class, SPacketPlayerListHeaderFooter::new);
			this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketCollectItem.class, SPacketCollectItem::new);
			this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketEntityTeleport.class, SPacketEntityTeleport::new);
			this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketAdvancementInfo.class, SPacketAdvancementInfo::new);
			this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketEntityProperties.class, SPacketEntityProperties::new);
			this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketEntityEffect.class, SPacketEntityEffect::new);
			this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketConfirmTeleport.class, CPacketConfirmTeleport::new);
			this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketTabComplete.class, CPacketTabComplete::new);
			this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketChatMessage.class, CPacketChatMessage::new);
			this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketClientStatus.class, CPacketClientStatus::new);
			this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketClientSettings.class, CPacketClientSettings::new);
			this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketConfirmTransaction.class, CPacketConfirmTransaction::new);
			this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketEnchantItem.class, CPacketEnchantItem::new);
			this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketClickWindow.class, CPacketClickWindow::new);
			this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketCloseWindow.class, CPacketCloseWindow::new);
			this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketCustomPayload.class, CPacketCustomPayload::new);
			this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketUseEntity.class, CPacketUseEntity::new);
			this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketKeepAlive.class, CPacketKeepAlive::new);
			this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketPlayer.class, CPacketPlayer::new);
			this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketPlayer.Position.class, CPacketPlayer.Position::new);
			this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketPlayer.PositionRotation.class, CPacketPlayer.PositionRotation::new);
			this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketPlayer.Rotation.class, CPacketPlayer.Rotation::new);
			this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketVehicleMove.class, CPacketVehicleMove::new);
			this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketSteerBoat.class, CPacketSteerBoat::new);
			this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketPlaceRecipe.class, CPacketPlaceRecipe::new);
			this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketPlayerAbilities.class, CPacketPlayerAbilities::new);
			this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketPlayerDigging.class, CPacketPlayerDigging::new);
			this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketEntityAction.class, CPacketEntityAction::new);
			this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketInput.class, CPacketInput::new);
			this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketRecipeInfo.class, CPacketRecipeInfo::new);
			this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketResourcePackStatus.class, CPacketResourcePackStatus::new);
			this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketSeenAdvancements.class, CPacketSeenAdvancements::new);
			this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketHeldItemChange.class, CPacketHeldItemChange::new);
			this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketCreativeInventoryAction.class, CPacketCreativeInventoryAction::new);
			this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketUpdateSign.class, CPacketUpdateSign::new);
			this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketAnimation.class, CPacketAnimation::new);
			this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketSpectate.class, CPacketSpectate::new);
			this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketPlayerTryUseItemOnBlock.class, CPacketPlayerTryUseItemOnBlock::new);
			this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketPlayerTryUseItem.class, CPacketPlayerTryUseItem::new);
		}
	},
	STATUS(1) {
		{
			this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketServerQuery.class, CPacketServerQuery::new);
			this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketServerInfo.class, SPacketServerInfo::new);
			this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketPing.class, CPacketPing::new);
			this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketPong.class, SPacketPong::new);
		}
	},
	LOGIN(2) {
		{
			this.registerPacket(EnumPacketDirection.CLIENTBOUND,
					net.minecraft.network.login.server.SPacketDisconnect.class, net.minecraft.network.login.server.SPacketDisconnect::new);
			this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketEncryptionRequest.class, SPacketEncryptionRequest::new);
			this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketLoginSuccess.class, SPacketLoginSuccess::new);
			this.registerPacket(EnumPacketDirection.CLIENTBOUND, SPacketEnableCompression.class, SPacketEnableCompression::new);
			this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketLoginStart.class, CPacketLoginStart::new);
			this.registerPacket(EnumPacketDirection.SERVERBOUND, CPacketEncryptionResponse.class, CPacketEncryptionResponse::new);
		}
	};

	private static int field_181136_e = -1;
	private static int field_181137_f = 2;
	private static final EnumConnectionState[] STATES_BY_ID = new EnumConnectionState[field_181137_f - field_181136_e
			+ 1];
	private static final Map<Class<? extends Packet>, EnumConnectionState> STATES_BY_CLASS = Maps.newHashMap();
	private final int id;
	private final Map<EnumPacketDirection, BiMap<Integer, Class<? extends Packet>>> directionMaps;
	private final Map<EnumPacketDirection, Map<Integer, Supplier<Packet<?>>>> directionCtors;

	private EnumConnectionState(int protocolId) {
		this.directionMaps = Maps.newEnumMap(EnumPacketDirection.class);
		this.directionCtors = Maps.newEnumMap(EnumPacketDirection.class);
		this.id = protocolId;
	}

	protected EnumConnectionState registerPacket(EnumPacketDirection direction, Class<? extends Packet> packetClass, Supplier<Packet<?>> packetCtor) {
		BiMap<Integer, Class<? extends Packet>> object = this.directionMaps.get(direction);
		Map<Integer, Supplier<Packet<?>>> object2;
		if (object == null) {
			object = HashBiMap.create();
			object2 = Maps.newHashMap();
			this.directionMaps.put(direction, object);
			this.directionCtors.put(direction, object2);
		} else {
			object2 = this.directionCtors.get(direction);
		}

		if (object.containsValue(packetClass)) {
			String s = direction + " packet " + packetClass + " is already known to ID "
					+ object.inverse().get(packetClass);
			LogManager.getLogger().fatal(s);
			throw new IllegalArgumentException(s);
		} else {
			object.put(Integer.valueOf(object.size()), packetClass);
			object2.put(Integer.valueOf(object2.size()), packetCtor);
			return this;
		}
	}

	public Integer getPacketId(EnumPacketDirection direction, Packet<?> packetIn) throws Exception {
		return (Integer) ((BiMap) this.directionMaps.get(direction)).inverse().get(packetIn.getClass());
	}

	@Nullable
	public Packet<?> getPacket(EnumPacketDirection direction, int packetId) throws InstantiationException, IllegalAccessException {
		Supplier<Packet<?>> oclass = this.directionCtors.get(direction).get(Integer.valueOf(packetId));
		return oclass == null ? null : oclass.get();
	}

	public int getId() {
		return this.id;
	}

	public static EnumConnectionState getById(int stateId) {
		return stateId >= field_181136_e && stateId <= field_181137_f ? STATES_BY_ID[stateId - field_181136_e] : null;
	}

	public static EnumConnectionState getFromPacket(Packet<?> packetIn) {
		return (EnumConnectionState) STATES_BY_CLASS.get(packetIn.getClass());
	}

	static {
		EnumConnectionState[] states = values();
		for (int j = 0; j < states.length; ++j) {
			EnumConnectionState enumconnectionstate = states[j];
			int i = enumconnectionstate.getId();
			if (i < field_181136_e || i > field_181137_f) {
				throw new Error("Invalid protocol ID " + Integer.toString(i));
			}

			STATES_BY_ID[i - field_181136_e] = enumconnectionstate;

			for (EnumPacketDirection enumpacketdirection : enumconnectionstate.directionMaps.keySet()) {
				for (Class oclass : (Collection<Class>) ((BiMap) enumconnectionstate.directionMaps
						.get(enumpacketdirection)).values()) {
					if (STATES_BY_CLASS.containsKey(oclass) && STATES_BY_CLASS.get(oclass) != enumconnectionstate) {
						throw new Error("Packet " + oclass + " is already assigned to protocol "
								+ STATES_BY_CLASS.get(oclass) + " - can\'t reassign to " + enumconnectionstate);
					}

					STATES_BY_CLASS.put(oclass, enumconnectionstate);
				}
			}
		}
	}
}
