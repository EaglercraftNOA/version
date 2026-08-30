package net.minecraft.server.network;

import com.google.common.base.Charsets;
import com.mojang.authlib.GameProfile;

import net.lax1dude.eaglercraft.Random;
import net.lax1dude.eaglercraft.EaglercraftUUID;
import java.util.concurrent.atomic.AtomicInteger;

import net.lax1dude.eaglercraft.sp.server.socket.IntegratedServerPlayerNetworkManager;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.login.INetHandlerLoginServer;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.network.login.client.C01PacketEncryptionResponse;
import net.minecraft.network.login.server.S00PacketDisconnect;
import net.minecraft.network.login.server.S02PacketLoginSuccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.entity.player.EntityPlayerMP;

public class NetHandlerLoginServer implements INetHandlerLoginServer, net.minecraft.util.ITickable {
	private static final AtomicInteger field_147331_b = new AtomicInteger(0);
	private static final Logger logger = LogManager.getLogger();
	private static final Random field_147329_d = new Random();
	private final byte[] field_147330_e = new byte[4];
	private final MinecraftServer field_147327_f;
	public final IntegratedServerPlayerNetworkManager field_147333_a;
	private LoginState currentLoginState = LoginState.HELLO;
	private int connectionTimer;
	private GameProfile loginGameProfile;
	private int selectedProtocol = 3;
	private EntityPlayerMP pendingPlayer;
	private static final String __OBFID = "CL_00001458";

	public NetHandlerLoginServer(MinecraftServer p_i45298_1_, IntegratedServerPlayerNetworkManager p_i45298_2_) {
		this.field_147327_f = p_i45298_1_;
		this.field_147333_a = p_i45298_2_;
		field_147329_d.nextBytes(this.field_147330_e);
	}

	/**
	 * For scheduled network tasks. Used in NetHandlerPlayServer to send keep-alive
	 * packets and in NetHandlerLoginServer for a login-timeout
	 */
	public void onNetworkTick() {
		if (this.currentLoginState == LoginState.READY_TO_ACCEPT) {
			this.tryAcceptPlayer();
		} else if (this.currentLoginState == LoginState.DELAY_ACCEPT) {
			EntityPlayerMP entityplayermp = this.field_147327_f.getConfigurationManager()
					.func_152612_a(this.loginGameProfile.getName());
			if (entityplayermp == null) {
				this.currentLoginState = LoginState.READY_TO_ACCEPT;
				this.field_147327_f.getConfigurationManager().initializeConnectionToPlayer(this.field_147333_a,
						this.pendingPlayer);
				this.pendingPlayer = null;
			}
		}
		if (this.connectionTimer++ == 600) {
			this.func_147322_a("Took too long to log in");
		}
	}

	public void func_147322_a(String p_147322_1_) {
		try {
			logger.info("Disconnecting " + this.func_147317_d() + ": " + p_147322_1_);
			ChatComponentText var2 = new ChatComponentText(p_147322_1_);
			this.field_147333_a.sendPacket(new S00PacketDisconnect(var2));
			this.field_147333_a.closeChannel(var2);
		} catch (Exception var3) {
			logger.error("Error whilst disconnecting player", var3);
		}
	}

	/**
	 * Invoked when disconnecting, the parameter is a ChatComponent describing the
	 * reason for termination
	 */
	public void onDisconnect(IChatComponent p_147231_1_) {
		logger.info(this.func_147317_d() + " lost connection: " + p_147231_1_.getUnformattedText());
	}

	public String func_147317_d() {
		return this.loginGameProfile != null
				? this.loginGameProfile.toString() + " (channel:" + this.field_147333_a.playerChannel + ")"
				: ("channel:" + this.field_147333_a.playerChannel);
	}

	/**
	 * Allows validation of the connection state transition. Parameters: from, to
	 * (connection state). Typically throws IllegalStateException or
	 * UnsupportedOperationException if validation fails
	 */
	public void onConnectionStateTransition(EnumConnectionState p_147232_1_, EnumConnectionState p_147232_2_) {
		Validate.validState(
				this.currentLoginState == NetHandlerLoginServer.LoginState.ACCEPTED
						|| this.currentLoginState == NetHandlerLoginServer.LoginState.HELLO,
				"Unexpected change in protocol", new Object[0]);
		Validate.validState(p_147232_2_ == EnumConnectionState.PLAY || p_147232_2_ == EnumConnectionState.LOGIN,
				"Unexpected protocol " + p_147232_2_, new Object[0]);
	}

	public void processLoginStart(C00PacketLoginStart p_147316_1_) {
		Validate.validState(this.currentLoginState == NetHandlerLoginServer.LoginState.HELLO, "Unexpected hello packet",
				new Object[0]);
		this.loginGameProfile = p_147316_1_.func_149304_c();

		if (!this.loginGameProfile.isComplete()) {
			this.loginGameProfile = this.func_152506_a(this.loginGameProfile);
		}

		this.currentLoginState = NetHandlerLoginServer.LoginState.READY_TO_ACCEPT;
		this.onNetworkTick();
	}

	protected GameProfile func_152506_a(GameProfile p_152506_1_) {
		EaglercraftUUID var2 = EaglercraftUUID
				.nameUUIDFromBytes(("OfflinePlayer:" + p_152506_1_.getName()).getBytes(Charsets.UTF_8));
		return new GameProfile(var2, p_152506_1_.getName());
	}

	static enum LoginState {
		HELLO("HELLO", 0), KEY("KEY", 1), AUTHENTICATING("AUTHENTICATING", 2), READY_TO_ACCEPT("READY_TO_ACCEPT", 3),
		ACCEPTED("ACCEPTED", 4), DELAY_ACCEPT("DELAY_ACCEPT", 5);

		private static final NetHandlerLoginServer.LoginState[] $VALUES = new NetHandlerLoginServer.LoginState[] {
				HELLO, KEY, AUTHENTICATING, READY_TO_ACCEPT, ACCEPTED, DELAY_ACCEPT };
		private static final String __OBFID = "CL_00001463";

		private LoginState(String p_i45297_1_, int p_i45297_2_) {
		}
	}

	private void tryAcceptPlayer() {
		String s = this.field_147327_f.getConfigurationManager().func_148542_a(this.loginGameProfile);
		if (s != null) {
			this.func_147322_a(s);
		} else {
			this.currentLoginState = NetHandlerLoginServer.LoginState.ACCEPTED;
			this.field_147333_a.sendPacket(new S02PacketLoginSuccess(this.loginGameProfile, this.selectedProtocol));
			this.field_147333_a.setConnectionState(EnumConnectionState.PLAY);
			EntityPlayerMP entityplayermp = this.field_147327_f.getConfigurationManager()
					.func_152612_a(this.loginGameProfile.getName());
			if (entityplayermp != null) {
				this.currentLoginState = NetHandlerLoginServer.LoginState.DELAY_ACCEPT;
				this.pendingPlayer = this.field_147327_f.getConfigurationManager().func_148545_a(this.loginGameProfile);
			} else {
				entityplayermp = this.field_147327_f.getConfigurationManager().func_148545_a(this.loginGameProfile);
				this.field_147327_f.getConfigurationManager().initializeConnectionToPlayer(this.field_147333_a,
						entityplayermp);
			}
		}
	}

	@Override
	public void processEncryptionResponse(C01PacketEncryptionResponse p_147315_1_) {

	}

	@Override
	public void update() {
		this.onNetworkTick();
	}
}
