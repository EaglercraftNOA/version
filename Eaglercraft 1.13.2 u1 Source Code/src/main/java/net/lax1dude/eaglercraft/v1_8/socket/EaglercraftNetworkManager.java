/*
 * Copyright (c) 2022-2025 lax1dude. All Rights Reserved.
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

package net.lax1dude.eaglercraft.v1_8.socket;

import java.io.IOException;

import javax.annotation.Nullable;

import io.netty.buffer.Unpooled;
import net.lax1dude.eaglercraft.v1_8.EaglerSocketAddress;
import net.lax1dude.eaglercraft.v1_8.internal.EnumEaglerConnectionState;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.handshake.ServerCapabilities;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.message.InjectedMessageController;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.PacketSendListener;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public abstract class EaglercraftNetworkManager extends NetworkManager {
	
	protected final String address;
	protected final EaglerSocketAddress remoteAddress;
	protected INetHandler nethandler = null;
	protected EnumConnectionState packetState = EnumConnectionState.HANDSHAKING;
	protected final PacketBuffer temporaryBuffer;
	protected int debugPacketCounter = 0;
	protected ITextComponent disconnectedReason = null;
	
	protected String pluginBrand = null;
	protected String pluginVersion = null;
	protected InjectedMessageController injectedController = null;

	protected ServerCapabilities serverCapabilities = null;
	
	public static final Logger logger = LogManager.getLogger("NetworkManager");

	public EaglercraftNetworkManager(String address) {
		super(EnumPacketDirection.CLIENTBOUND);
		this.address = address;
		this.remoteAddress = new EaglerSocketAddress(address, 0);
		this.temporaryBuffer = new PacketBuffer(Unpooled.buffer(0x1FFFF));
	}

	public void setPluginInfo(String pluginBrand, String pluginVersion, ServerCapabilities serverCapabilities) {
		this.pluginBrand = pluginBrand;
		this.pluginVersion = pluginVersion;
		this.serverCapabilities = serverCapabilities;
	}

	public void setLANInfo(int protocolVer) {
		this.pluginBrand = "integrated";
		this.pluginVersion = "v" + protocolVer;
		this.serverCapabilities = ServerCapabilities.getLAN();
	}
	
	public String getPluginBrand() {
		return pluginBrand;
	}
	
	public String getPluginVersion() {
		return pluginVersion;
	}
	
	public ServerCapabilities getServerCapabilities() {
		return serverCapabilities;
	}

	public void setInjectedMessageController(InjectedMessageController controller) {
		injectedController = controller;
	}
	
	public abstract void connect();
	
	public abstract EnumEaglerConnectionState getConnectStatus();
	
	public String getAddress() {
		return address;
	}
	
	@Override
	public abstract void closeChannel(ITextComponent reason);
	
	@Override
	public void setConnectionState(EnumConnectionState state) {
		packetState = state;
	}

	public void setProtocol(EnumConnectionState state) {
		setConnectionState(state);
	}
	
	public abstract void processReceivedPackets() throws IOException;

	@Override
	public abstract void sendPacket(Packet<?> pkt);

	public void send(Packet<?> pkt) {
		sendPacket(pkt);
	}

	public void send(Packet<?> pkt, @Nullable PacketSendListener listener) {
		sendPacket(pkt);
		if(listener != null) {
			listener.accept(PacketSendListener.SUCCESS);
		}
	}
	
	public void setListener(INetHandler nethandler) {
		setNetHandler(nethandler);
	}

	@Override
	public void setNetHandler(INetHandler nethandler) {
		this.nethandler = nethandler;
	}
	
	@Override
	public boolean isLocalChannel() {
		return false;
	}
	
	@Override
	public boolean isChannelOpen() {
		return getConnectStatus() == EnumEaglerConnectionState.CONNECTED;
	}

	public boolean isConnected() {
		return isChannelOpen();
	}

	public boolean isConnecting() {
		return getConnectStatus() == EnumEaglerConnectionState.CONNECTING;
	}

	public boolean getIsencrypted() {
		return false;
	}

	@Override
	public boolean isEncrypted() {
		return getIsencrypted();
	}

	public void setCompressionTreshold(int compressionTreshold) {
		throw new CompressionNotSupportedException();
	}

	@Override
	public void setCompressionThreshold(int compressionTreshold) {
		if(compressionTreshold >= 0) {
			setCompressionTreshold(compressionTreshold);
		}
	}

	public void setupCompression(int compressionTreshold, boolean validateDecompressed) {
		setCompressionThreshold(compressionTreshold);
	}

	public abstract boolean checkDisconnected();
	
	protected boolean clientDisconnected = false;
	
	protected void doClientDisconnect(ITextComponent msg) {
		if(!clientDisconnected) {
			clientDisconnected = true;
			if(nethandler != null) {
				this.nethandler.onDisconnect(msg);
			}
		}
	}

	public void disconnect(ITextComponent reason) {
		this.disconnectedReason = reason;
		closeChannel(reason);
	}

	@Override
	public EaglerSocketAddress getRemoteAddress() {
		return remoteAddress;
	}

	@Override
	public INetHandler getNetHandler() {
		return nethandler;
	}

	public INetHandler getPacketListener() {
		return nethandler;
	}

	@Override
	@Nullable
	public ITextComponent getExitMessage() {
		return disconnectedReason;
	}

	@Nullable
	public ITextComponent getDisconnectedReason() {
		return disconnectedReason;
	}

	@Override
	public void tick() {
		try {
			processReceivedPackets();
		}catch(IOException ex) {
			logger.error("Exception processing Eaglercraft packets", ex);
			disconnect(new TextComponentTranslation("disconnect.genericReason", "Internal Exception: " + ex));
		}
		checkDisconnected();
	}

	@Override
	public void handleDisconnection() {
		checkDisconnected();
	}

	public abstract void injectRawFrame(byte[] data);

}
