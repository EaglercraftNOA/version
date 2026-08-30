/*
 * Copyright (c) 2025 lax1dude. All Rights Reserved.
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
 * 
 */

package net.lax1dude.eaglercraft.v1_8.socket.protocol.handshake;

import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import com.mojang.authlib.GameProfile;

import io.netty.buffer.Unpooled;
import net.lax1dude.eaglercraft.v1_8.ArrayUtils;
import net.lax1dude.eaglercraft.v1_8.EaglerUUIDHelper;
import net.lax1dude.eaglercraft.v1_8.EaglerOutputStream;
import net.lax1dude.eaglercraft.v1_8.EaglercraftVersion;
import net.lax1dude.eaglercraft.v1_8.internal.IWebSocketClient;
import net.lax1dude.eaglercraft.v1_8.internal.IWebSocketFrame;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;
import net.lax1dude.eaglercraft.v1_8.profile.GuiAuthenticationScreen;
import net.lax1dude.eaglercraft.v1_8.socket.HandshakePacketTypes;
import net.lax1dude.eaglercraft.v1_8.socket.RateLimitTracker;
import net.lax1dude.eaglercraft.v1_8.socket.WebSocketNetworkManager;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.GamePluginMessageProtocol;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiConnecting;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

public class HandshakerHandler {
	
	static final Logger logger = LogManager.getLogger("HandshakerHandler");

	protected final Minecraft mc;
	protected final IWebSocketClient websocket;
	protected final GuiConnecting parent;
	protected final GuiScreen ret;
	protected final String username;
	protected final String password;
	protected final boolean allowPlaintext;
	protected final boolean enableCookies;
	protected final byte[] cookieData;
	protected HandshakerInstance handshaker;
	protected boolean nicknameSelection = true;
	protected int baseState = NEW;
	protected WebSocketNetworkManager networkManager;

	protected static final int NEW = 0, SENT_HANDSHAKE = 1, PROCESSING = 2, FINISHED = 3;

	public HandshakerHandler(GuiConnecting parent, IWebSocketClient websocket, String username, String password,
			boolean allowPlaintext, boolean enableCookies, byte[] cookieData) {
		this.mc = GuiConnecting.getMC(parent);
		this.websocket = websocket;
		this.parent = parent;
		this.ret = GuiConnecting.getPrevScreen(parent);
		this.username = username;
		this.password = password;
		this.allowPlaintext = allowPlaintext;
		this.enableCookies = enableCookies;
		this.cookieData = cookieData;
	}

	private static final int protocolV3 = 3;
	private static final int protocolV4 = 4;
	private static final int protocolV5 = 5;

	public static byte[] getSPHandshakeProtocolData() {
		try {
			EaglerOutputStream bao = new EaglerOutputStream();
			DataOutputStream d = new DataOutputStream(bao);
			if(SharedConstants.getProtocolVersion() > 340) {
				d.writeShort(1); 
				d.writeShort(protocolV5);
			}else {
				d.writeShort(3); 
				d.writeShort(protocolV3);
				d.writeShort(protocolV4);
				d.writeShort(protocolV5); 
			}
			return bao.toByteArray();
		}catch(IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	public WebSocketNetworkManager getNetworkManager() {
		return networkManager;
	}

	public void disconnect(ITextComponent reason) {
		if(networkManager != null) {
			networkManager.disconnect(reason);
		}else {
			websocket.close();
		}
		baseState = FINISHED;
	}

	public void tick() {
		if(baseState == NEW) {
			if(websocket.isClosed()) {
				handleError("Connection Closed", null);
				return;
			}
			baseState = SENT_HANDSHAKE;
			beginHandshake();
		}else if(baseState == SENT_HANDSHAKE) {
			IWebSocketFrame frame = websocket.getNextBinaryFrame();
			if(frame != null) {
				byte[] data = frame.getByteArray();
				handleServerHandshake(new PacketBuffer(Unpooled.wrappedBuffer(data)));
			}
		}else if(baseState == PROCESSING) {
			handshaker.tick();
		}else if(baseState == FINISHED) {
			if(networkManager != null) {
				try {
					networkManager.processReceivedPackets();
				} catch (IOException e) {
				}
			}
		}
	}

	protected void beginHandshake() {
		PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
		
		buffer.writeByte(HandshakePacketTypes.PROTOCOL_CLIENT_VERSION);
		
		buffer.writeByte(2); // legacy protocol version
		
		buffer.writeBytes(getSPHandshakeProtocolData()); // write supported eagler protocol versions
		
		buffer.writeShort(1); // supported game protocols count
		buffer.writeShort(SharedConstants.getProtocolVersion()); // client supports this Minecraft protocol
		
		String clientBrand = EaglercraftVersion.projectForkName;
		buffer.writeByte(clientBrand.length());
		writeASCII(buffer, clientBrand);
		
		String clientVers = EaglercraftVersion.projectOriginVersion;
		buffer.writeByte(clientVers.length());
		writeASCII(buffer, clientVers);
		
		buffer.writeBoolean(password != null);
		
		buffer.writeByte(username.length());
		writeASCII(buffer, username);
		
		websocket.send(bufferToBytes(buffer));
	}

	protected static void writeASCII(PacketBuffer buffer, String str) {
		for(int i = 0, l = str.length(); i < l; ++i) {
			buffer.writeByte(str.charAt(i));
		}
	}

	protected static byte[] bufferToBytes(PacketBuffer buffer) {
		int len = buffer.writerIndex();
		byte[] ret = new byte[len];
		buffer.getBytes(0, ret);
		return ret;
	}

	protected void handleServerHandshake(PacketBuffer packet) {
		try {
			int pktId = packet.readUnsignedByte();
			switch(pktId) {
			case HandshakePacketTypes.PROTOCOL_SERVER_VERSION:
				handleServerVersion(packet);
				break;
			case HandshakePacketTypes.PROTOCOL_VERSION_MISMATCH:
				handleVersionMismatch(packet);
				break;
			case HandshakePacketTypes.PROTOCOL_SERVER_ERROR:
				handleServerError(packet, false);
				break;
			default:
				handleError("connect.failed", new TextComponentString("Unknown packet type " + pktId + " received"));
				break;
			}
		}catch(Exception ex) {
			handleError("connect.failed", new TextComponentString("Invalid packet received"));
			logger.error("Invalid packet received");
			logger.error(ex);
		}
	}

	protected void handleServerVersion(PacketBuffer packet) {
		int protocolVersion = packet.readUnsignedShort();
		
		if(protocolVersion != protocolV3 && protocolVersion != protocolV4 && protocolVersion != protocolV5) {
			logger.info("Incompatible server version: {}", protocolVersion);
			handleError("connect.failed", new TextComponentString(protocolVersion < protocolV3 ? "Outdated Server" : "Outdated Client"));
			return;
		}
		
		int gameVers = packet.readUnsignedShort();
		int expectedGameVers = SharedConstants.getProtocolVersion();
		if(gameVers != expectedGameVers) {
			logger.info("Incompatible minecraft protocol version: {}", gameVers);
			handleError("connect.failed", new TextComponentString("This server does not support " + SharedConstants.getCurrentVersion().getName() + "!"));
			return;
		}
		
		logger.info("Server protocol: {}", protocolVersion);
		
		int msgLen = packet.readUnsignedByte();
		byte[] dat = new byte[msgLen];
		packet.readBytes(dat);
		String pluginBrand = ArrayUtils.asciiString(dat);
		
		msgLen = packet.readUnsignedByte();
		dat = new byte[msgLen];
		packet.readBytes(dat);
		String pluginVersion = ArrayUtils.asciiString(dat);
		
		logger.info("Server version: {}", pluginVersion);
		logger.info("Server brand: {}", pluginBrand);
		
		int authType = packet.readUnsignedByte();
		int saltLength = (int)packet.readUnsignedShort() & 0xFFFF;
		
		byte[] salt = new byte[saltLength];
		packet.readBytes(salt);
		
		if(protocolVersion >= protocolV5) {
			nicknameSelection = packet.readBoolean();
		}

		baseState = PROCESSING;
		switch(protocolVersion) {
		case protocolV3:
			handshaker = new HandshakerV3(this);
			break;
		case protocolV4:
			handshaker = new HandshakerV4(this);
			break;
		case protocolV5:
			handshaker = new HandshakerV5(this);
			break;
		}

		handshaker.begin(pluginBrand, pluginVersion, authType, salt);
	}

	protected void handleVersionMismatch(PacketBuffer packet) {
		StringBuilder protocols = new StringBuilder();
		int c = packet.readUnsignedShort();
		for(int i = 0; i < c; ++i) {
			if(i > 0) {
				protocols.append(", ");
			}
			protocols.append("v").append(packet.readUnsignedShort());
		}
		
		StringBuilder games = new StringBuilder();
		c = packet.readUnsignedShort();
		for(int i = 0; i < c; ++i) {
			if(i > 0) {
				games.append(", ");
			}
			games.append("mc").append(packet.readUnsignedShort());
		}
		
		logger.info("Incompatible client: v3/v4/v5 & mc{}", SharedConstants.getProtocolVersion());
		logger.info("Server supports: {}", protocols);
		logger.info("Server supports: {}", games);
		
		int msgLen = packet.readUnsignedByte();
		byte[] dat = new byte[msgLen];
		packet.readBytes(dat);
		String msg = new String(dat, StandardCharsets.UTF_8);
		
		handleError("connect.failed", new TextComponentString(msg));
	}

	protected void handleServerError(PacketBuffer packet, boolean v3) {
		int errCode = packet.readUnsignedByte();
		int msgLen;
		if(v3) {
			msgLen = packet.readUnsignedShort();
			if(msgLen == 0 && packet.readableBytes() == 65536) {
				// workaround for bug in EaglerXBungee 1.2.7 and below
				msgLen = 65536;
			}
		}else {
			msgLen = packet.readUnsignedByte();
			if(msgLen == 0 && packet.readableBytes() == 256) {
				// workaround for bug in EaglerXBungee 1.2.7 and below
				msgLen = 256;
			}
		}
		byte[] dat = new byte[msgLen];
		packet.readBytes(dat);
		String msg = new String(dat, StandardCharsets.UTF_8);
		if(errCode == HandshakePacketTypes.SERVER_ERROR_RATELIMIT_BLOCKED) {
			handleRatelimit(false, new TextComponentString(msg));
		}else if(errCode == HandshakePacketTypes.SERVER_ERROR_RATELIMIT_LOCKED) {
			handleRatelimit(true, new TextComponentString(msg));
		}else if(errCode == HandshakePacketTypes.SERVER_ERROR_AUTHENTICATION_REQUIRED) {
			handleAuthRequired(msg);
		}else if(errCode == HandshakePacketTypes.SERVER_ERROR_CUSTOM_MESSAGE) {
			ITextComponent ITextComponent = v3 ? parseServerTextComponent(msg) : new TextComponentString(msg);
			handleError("connect.failed", ITextComponent != null ? ITextComponent : new TextComponentString(msg));
		}else {
			handleError("connect.failed", new TextComponentString("Server Error Code " + errCode + "\n" + msg));
		}
	}

	static ITextComponent parseServerTextComponent(String msg) {
		if(msg != null && msg.length() > 0) {
			try {
				return ITextComponent.Serializer.fromJson(msg);
			}catch(Throwable t) {
			}
		}
		return null;
	}

	protected void handleSuccess() {
		if(baseState != FINISHED) {
			baseState = FINISHED;
			websocket.setEnableStringFrames(false);
			websocket.clearStringFrames();
			networkManager = new WebSocketNetworkManager(websocket);
			networkManager.setPluginInfo(handshaker.pluginBrand, handshaker.pluginVersion, new ServerCapabilities(
					handshaker.serverStandardCaps, handshaker.serverStandardCapVers, handshaker.extendedCaps));
			mc.bungeeOutdatedMsgTimer = 80;
			mc.gui.clear();
			networkManager.setConnectionState(EnumConnectionState.PLAY);
			NetHandlerPlayClient netHandler = new NetHandlerPlayClient(this.mc, ret, networkManager,
					new GameProfile(EaglerUUIDHelper.fromBits(handshaker.uuid.msb, handshaker.uuid.lsb), handshaker.username),
					GamePluginMessageProtocol.getByVersion(handshaker.getVersion()));
			networkManager.setListener(netHandler);
		}
	}

	protected void handleServerRedirectTo(String address) {
		mc.handleReconnectPacket(address);
		websocket.close();
		if(baseState != FINISHED) {
			baseState = FINISHED;
			mc.displayGuiScreen(ret);
		}
	}

	protected void handleRatelimit(boolean locked, ITextComponent detail) {
		if(locked) {
			RateLimitTracker.registerLockOut(websocket.getCurrentURI());
		}else {
			RateLimitTracker.registerBlock(websocket.getCurrentURI());
		}
		websocket.close();
		if(baseState != FINISHED) {
			baseState = FINISHED;
			mc.displayGuiScreen(new GuiDisconnected(ret, "connect.failed", detail));
		}
	}

	protected void handleError(String message, ITextComponent detail) {
		websocket.close();
		if(baseState != FINISHED) {
			baseState = FINISHED;
			mc.displayGuiScreen(new GuiDisconnected(ret, message, detail != null ? detail : TextComponentString.EMPTY));
		}
	}

	protected void handleAuthRequired(String message) {
		websocket.close();
		if(baseState != FINISHED) {
			baseState = FINISHED;
			mc.displayGuiScreen(new GuiAuthenticationScreen(parent, ret, message, parent::retryWithAuth));
		}
	}

}
