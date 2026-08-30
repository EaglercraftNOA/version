/*
 * Copyright (c) 2023-2025 lax1dude, ayunami2000. All Rights Reserved.
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

package net.lax1dude.eaglercraft.v1_8.sp.socket;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.lax1dude.eaglercraft.v1_8.internal.EnumEaglerConnectionState;
import net.lax1dude.eaglercraft.v1_8.internal.IPCPacketData;
import net.lax1dude.eaglercraft.v1_8.socket.EaglercraftNetworkManager;
import net.lax1dude.eaglercraft.v1_8.sp.SingleplayerServerController;
import net.lax1dude.eaglercraft.v1_8.sp.internal.ClientPlatformSingleplayer;
import net.lax1dude.eaglercraft.v1_8.sp.lan.LANServerController;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.network.Packet;
import net.minecraft.network.EnumPacketDirection;

public class ClientIntegratedServerNetworkManager extends EaglercraftNetworkManager {

	private int debugPacketCounter = 0;
	private final List<byte[]> recievedPacketBuffer = new LinkedList<>();
	public boolean isPlayerChannelOpen = false;

	public ClientIntegratedServerNetworkManager(String channel) {
		super(channel);
	}

	@Override
	public void connect() {
		clearRecieveQueue();
		clientDisconnected = false;
		disconnectedReason = null;
		setConnectionState(EnumConnectionState.HANDSHAKING);
		SingleplayerServerController.openLocalPlayerChannel();
	}

	@Override
	public EnumEaglerConnectionState getConnectStatus() {
		return isPlayerChannelOpen ? EnumEaglerConnectionState.CONNECTED : EnumEaglerConnectionState.CLOSED;
	}

	@Override
	public void closeChannel(ITextComponent reason) {
		LANServerController.closeLAN();
		SingleplayerServerController.closeLocalPlayerChannel();
		if(nethandler != null) {
			nethandler.onDisconnect(reason);
		}
		clearRecieveQueue();
		clientDisconnected = true;
	}

	public void addRecievedPacket(byte[] next) {
		recievedPacketBuffer.add(next);
	}

	@Override
	public void processReceivedPackets() throws IOException {
		if(nethandler == null) return;

		while(!recievedPacketBuffer.isEmpty()) {
			byte[] next = recievedPacketBuffer.remove(0);
			++debugPacketCounter;
			try {
				if(injectedController != null && injectedController.handlePacket(next, 0)) {
					continue;
				}
				
				ByteBuf nettyBuffer = Unpooled.wrappedBuffer(next);
				PacketBuffer input = new PacketBuffer(nettyBuffer);
				int pktId = input.readVarInt();
				
				Packet<?> pkt = packetState.createPacket(EnumPacketDirection.CLIENTBOUND, pktId, input);
				
				if(pkt == null) {
					throw new IOException("Recieved packet type " + pktId + " which is undefined in state " + packetState);
				}
				
				try {
					processPacket(pkt);
				}catch(Throwable t) {
					logger.error("Failed to process {}! It'll be skipped for debug purposes.", pkt.getClass().getSimpleName());
					logger.error(t);
				}
				
			}catch(Throwable t) {
				logger.error("Failed to process socket frame {}! It'll be skipped for debug purposes.", debugPacketCounter);
				logger.error(t);
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void processPacket(Packet<?> pkt) {
		((Packet)pkt).handle(nethandler);
	}

	@Override
	public void sendPacket(Packet<?> pkt) {
		if(!isChannelOpen()) {
			logger.error("Packet was sent on a closed connection: {}", pkt.getClass().getSimpleName());
			return;
		}
		
		int i;
		try {
			i = packetState.getPacketId(EnumPacketDirection.SERVERBOUND, pkt);
		}catch(Throwable t) {
			logger.error("Incorrect packet for state: {}", pkt.getClass().getSimpleName());
			return;
		}
		
		temporaryBuffer.clear();
		temporaryBuffer.writeVarInt(i);
		try {
			pkt.write(temporaryBuffer);
		}catch(Throwable ex) {
			logger.error("Failed to write packet {}!", pkt.getClass().getSimpleName());
			return;
		}
		
		int len = temporaryBuffer.writerIndex();
		byte[] bytes = new byte[len];
		temporaryBuffer.getBytes(0, bytes);
		
		ClientPlatformSingleplayer.sendPacket(new IPCPacketData(address, bytes));
	}

	@Override
	public boolean checkDisconnected() {
		if(!isPlayerChannelOpen) {
			try {
				processReceivedPackets(); // catch kick message
			} catch (IOException e) {
			}
			clearRecieveQueue();
			doClientDisconnect(new TextComponentTranslation("disconnect.endOfStream"));
			return true;
		}else {
			return false;
		}
	}

	@Override
	public boolean isLocalChannel() {
		return true;
	}

	public void clearRecieveQueue() {
		recievedPacketBuffer.clear();
	}

	@Override
	public void injectRawFrame(byte[] data) {
		if(!isChannelOpen()) {
			logger.error("Frame was injected on a closed connection");
			return;
		}
		ClientPlatformSingleplayer.sendPacket(new IPCPacketData(address, data));
	}

}
