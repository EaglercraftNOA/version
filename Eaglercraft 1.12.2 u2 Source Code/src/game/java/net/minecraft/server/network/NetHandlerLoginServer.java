package net.minecraft.server.network;

import com.mojang.authlib.GameProfile;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import net.lax1dude.eaglercraft.ClientUUIDLoadingCache;
import net.lax1dude.eaglercraft.EaglerInputStream;
import net.lax1dude.eaglercraft.EaglercraftRandom;
import net.lax1dude.eaglercraft.EaglercraftUUID;
import net.lax1dude.eaglercraft.EaglercraftVersion;
import net.lax1dude.eaglercraft.sp.server.EaglerMinecraftServer;
import net.lax1dude.eaglercraft.sp.server.socket.IntegratedServerPlayerNetworkManager;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.login.INetHandlerLoginServer;
import net.minecraft.network.login.client.CPacketEncryptionResponse;
import net.minecraft.network.login.client.CPacketLoginStart;
import net.minecraft.network.login.server.SPacketDisconnect;
import net.minecraft.network.login.server.SPacketEncryptionRequest;
import net.minecraft.network.login.server.SPacketLoginSuccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NetHandlerLoginServer implements INetHandlerLoginServer, ITickable {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final EaglercraftRandom RANDOM = new EaglercraftRandom();
	private final byte[] verifyToken = new byte[4];
	private final MinecraftServer server;
	public final IntegratedServerPlayerNetworkManager networkManager;
	private NetHandlerLoginServer.LoginState currentLoginState = NetHandlerLoginServer.LoginState.HELLO;

	/** How long has player been trying to login into the server. */
	private int connectionTimer;
	private GameProfile loginGameProfile;
	private byte[] loginSkinPacket;
	private byte[] loginCapePacket;
	private int selectedProtocol = 3;
	private EaglercraftUUID clientBrandUUID;
	private String serverId = "";
	private EntityPlayerMP player;

	public NetHandlerLoginServer(MinecraftServer serverIn, IntegratedServerPlayerNetworkManager networkManagerIn) {
		this.server = serverIn;
		this.networkManager = networkManagerIn;
		RANDOM.nextBytes(this.verifyToken);
	}

	/**
	 * Like the old updateEntity(), except more generic.
	 */
	public void update() {
		if (this.currentLoginState == NetHandlerLoginServer.LoginState.READY_TO_ACCEPT) {
			this.tryAcceptPlayer();
		} else if (this.currentLoginState == NetHandlerLoginServer.LoginState.DELAY_ACCEPT) {
			EntityPlayerMP entityplayermp = this.server.getPlayerList().getPlayerByUUID(this.loginGameProfile.getId());

			if (entityplayermp == null) {
				this.currentLoginState = NetHandlerLoginServer.LoginState.READY_TO_ACCEPT;
				this.server.getConfigurationManager().initializeConnectionToPlayer(this.networkManager,
						this.player, this.selectedProtocol, this.clientBrandUUID);
				((EaglerMinecraftServer) player.mcServer).getSkinService()
						.processLoginPacket(this.loginSkinPacket, player, 3); // singleplayer always sends V3
																						// skin in handshake
				if (this.loginCapePacket != null) {
					((EaglerMinecraftServer) player.mcServer).getCapeService()
							.processLoginPacket(this.loginCapePacket, player);
				}
				this.player = null;
			}
		}

		if (this.connectionTimer++ == 600) {
			this.func_194026_b(new TextComponentTranslation("multiplayer.disconnect.slow_login", new Object[0]));
		}
	}

	public void func_194026_b(ITextComponent p_194026_1_) {
		try {
			LOGGER.info("Disconnecting {}: {}", this.getConnectionInfo(), p_194026_1_.getUnformattedText());
			this.networkManager.sendPacket(new SPacketDisconnect(p_194026_1_));
			this.networkManager.closeChannel(p_194026_1_);
		} catch (Exception exception) {
			LOGGER.error("Error whilst disconnecting player", (Throwable) exception);
		}
	}

	public void tryAcceptPlayer() {
		String s = this.server.getConfigurationManager().allowUserToConnect(this.loginGameProfile);
		if (s != null) {
			this.func_194026_b(new TextComponentString(s));
		} else {
			this.currentLoginState = NetHandlerLoginServer.LoginState.ACCEPTED;
			this.networkManager.sendPacket(new SPacketLoginSuccess(this.loginGameProfile, this.selectedProtocol));
			this.networkManager.setConnectionState(EnumConnectionState.PLAY);
			EntityPlayerMP entityplayermp = this.server.getConfigurationManager().getPlayerByUUID(this.loginGameProfile.getId());
			if (entityplayermp != null) {
				this.currentLoginState = NetHandlerLoginServer.LoginState.DELAY_ACCEPT;
				this.player = this.server.getConfigurationManager().createPlayerForUser(this.loginGameProfile);
			} else {
				entityplayermp = this.server.getConfigurationManager().createPlayerForUser(this.loginGameProfile);
				this.server.getConfigurationManager().initializeConnectionToPlayer(this.networkManager, entityplayermp,
						this.selectedProtocol, this.clientBrandUUID);
				((EaglerMinecraftServer) entityplayermp.mcServer).getSkinService()
						.processLoginPacket(this.loginSkinPacket, entityplayermp, 3); // singleplayer always sends V3
																						// skin in handshake
				if (this.loginCapePacket != null) {
					((EaglerMinecraftServer) entityplayermp.mcServer).getCapeService()
							.processLoginPacket(this.loginCapePacket, entityplayermp);
				}
			}
		}

	}

	/**
	 * Invoked when disconnecting, the parameter is a ChatComponent describing the
	 * reason for termination
	 */
	public void onDisconnect(ITextComponent reason) {
		LOGGER.info("{} lost connection: {}", this.getConnectionInfo(), reason.getUnformattedText());
	}

	public String getConnectionInfo() {
		return this.loginGameProfile != null
				? this.loginGameProfile.toString() + " (channel:" + this.networkManager.playerChannel + ")"
				: ("channel:" + this.networkManager.playerChannel);
	}

	public void processLoginStart(CPacketLoginStart packetIn) {
		Validate.validState(this.currentLoginState == NetHandlerLoginServer.LoginState.HELLO, "Unexpected hello packet");
		if (packetIn.getProtocols() != null) {
			try {
				DataInputStream dis = new DataInputStream(new EaglerInputStream(packetIn.getProtocols()));
				int maxSupported = -1;
				int protocolCount = dis.readUnsignedShort();
				for (int i = 0; i < protocolCount; ++i) {
					int p = dis.readUnsignedShort();
					if ((p == 3 || p == 4) && p > maxSupported) {
						maxSupported = p;
					}
				}
				if (maxSupported != -1) {
					selectedProtocol = maxSupported;
				} else {
					this.func_194026_b(new TextComponentString("Unknown protocol!"));
					return;
				}
			} catch (IOException ex) {
				selectedProtocol = 3;
			}
		} else {
			selectedProtocol = 3;
		}
		this.loginGameProfile = this.getOfflineProfile(packetIn.getProfile());
		this.loginSkinPacket = packetIn.getSkin();
		this.loginCapePacket = packetIn.getCape();
		this.clientBrandUUID = selectedProtocol <= 3 ? EaglercraftVersion.legacyClientUUIDInSharedWorld : packetIn.getBrandUUID();
		if (ClientUUIDLoadingCache.PENDING_UUID.equals(clientBrandUUID)
				|| ClientUUIDLoadingCache.VANILLA_UUID.equals(clientBrandUUID)) {
			this.clientBrandUUID = null;
		}
		this.currentLoginState = NetHandlerLoginServer.LoginState.READY_TO_ACCEPT;
	}

	public void processEncryptionResponse(CPacketEncryptionResponse packetIn) {
	}

	protected GameProfile getOfflineProfile(GameProfile original) {
		EaglercraftUUID uuid = EaglercraftUUID
				.nameUUIDFromBytes(("OfflinePlayer:" + original.getName()).getBytes(StandardCharsets.UTF_8));
		return new GameProfile(uuid, original.getName());
	}

	static enum LoginState {
		HELLO, KEY, AUTHENTICATING, READY_TO_ACCEPT, DELAY_ACCEPT, ACCEPTED;
	}
}
