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

package net.lax1dude.eaglercraft.socket;

import java.io.IOException;

import io.netty.buffer.Unpooled;
import net.lax1dude.eaglercraft.internal.EnumEaglerConnectionState;
import net.lax1dude.eaglercraft.socket.protocol.handshake.ServerCapabilities;
import net.lax1dude.eaglercraft.socket.protocol.message.InjectedMessageController;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IChatComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class EaglercraftNetworkManager {

	protected final String address;
	protected INetHandler nethandler = null;
	protected EnumConnectionState packetState = EnumConnectionState.HANDSHAKING;
	protected final PacketBuffer temporaryBuffer;
	protected int debugPacketCounter = 0;

	protected String pluginBrand = null;
	protected String pluginVersion = null;
	protected InjectedMessageController injectedController = null;

	protected ServerCapabilities serverCapabilities = null;

	public static final Logger logger = LogManager.getLogger("NetworkManager");

	public EaglercraftNetworkManager(String address) {
		this.address = address;
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

	/**
	 * Expose the injected message controller so callers can send custom
	 * GameMessagePacket frames (e.g., skin updates) over the existing connection.
	 */
	public InjectedMessageController getInjectedMessageController() {
		return injectedController;
	}

	public abstract void connect();

	public abstract EnumEaglerConnectionState getConnectStatus();

	public String getAddress() {
		return address;
	}

	public abstract void closeChannel(IChatComponent reason);

	public void setConnectionState(EnumConnectionState state) {
		packetState = state;
	}

	public abstract void processReceivedPackets() throws IOException;

	public abstract void sendPacket(Packet pkt);

	public void setNetHandler(INetHandler handler) {
		this.nethandler = handler;
	}

	public boolean isLocalChannel() {
		return false;
	}

	public boolean isChannelOpen() {
		return getConnectStatus() == EnumEaglerConnectionState.CONNECTED;
	}

	public boolean getIsencrypted() {
		return false;
	}

	public void setCompressionTreshold(int compressionTreshold) {
		throw new CompressionNotSupportedException();
	}

	public abstract boolean checkDisconnected();

	protected boolean clientDisconnected = false;

	protected void doClientDisconnect(IChatComponent msg) {
		if (!clientDisconnected) {
			clientDisconnected = true;
			if (nethandler != null) {
				this.nethandler.onDisconnect(msg);
			}
		}
	}

	public abstract void injectRawFrame(byte[] data);

}