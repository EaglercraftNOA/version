package net.lax1dude.eaglercraft.socket;

import java.io.IOException;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.lax1dude.eaglercraft.internal.EnumEaglerConnectionState;
import net.lax1dude.eaglercraft.internal.IWebSocketClient;
import net.lax1dude.eaglercraft.internal.IWebSocketFrame;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

/**
 * Copyright (c) 2022-2024 lax1dude. All Rights Reserved.
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
public class WebSocketNetworkManager extends NetworkManager {

	protected final IWebSocketClient webSocketClient;

	public WebSocketNetworkManager(IWebSocketClient webSocketClient) {
		super(webSocketClient.getCurrentURI());
		this.webSocketClient = webSocketClient;
	}

	public void connect() {
	}

	public EnumEaglerConnectionState getConnectStatus() {
		return webSocketClient.getState();
	}

	public void closeChannel(ITextComponent reason) {
		webSocketClient.close();
		if(nethandler != null) {
			nethandler.onDisconnect(reason);
		}
		clientDisconnected = true;
	}

	public void processReceivedPackets() throws IOException {
		if(nethandler == null) return;
		if(webSocketClient.availableStringFrames() > 0) {
			logger.warn("discarding {} string frames recieved on a binary connection", webSocketClient.availableStringFrames());
			webSocketClient.clearStringFrames();
		}
		List<IWebSocketFrame> pkts = webSocketClient.getNextBinaryFrames();

		if(pkts == null) {
			return;
		}

		for(int i = 0, l = pkts.size(); i < l; ++i) {
			IWebSocketFrame next = pkts.get(i);
			++debugPacketCounter;
			try {
				byte[] asByteArray = next.getByteArray();
				ByteBuf nettyBuffer = Unpooled.buffer(asByteArray, asByteArray.length);
				nettyBuffer.writerIndex(asByteArray.length);
				PacketBuffer input = new PacketBuffer(nettyBuffer);
				int pktId = input.readVarIntFromBuffer();
				
				Packet pkt;
				try {
					pkt = packetState.getPacket(EnumPacketDirection.CLIENTBOUND, pktId);
				}catch(IllegalAccessException | InstantiationException ex) {
					throw new IOException("Recieved a packet with type " + pktId + " which is invalid!");
				}
				
				if(pkt == null) {
					throw new IOException("Recieved packet type " + pktId + " which is undefined in state " + packetState);
				}
				
				try {
					pkt.readPacketData(input);
				}catch(Throwable t) {
					throw new IOException("Failed to read packet type '" + pkt.getClass().getSimpleName() + "'", t);
				}
				
				try {
					pkt.processPacket(nethandler);
				}catch(Throwable t) {
					logger.error("Failed to process {}! It'll be skipped for debug purposes.", pkt.getClass().getSimpleName());
					logger.error(t);
				}
				
			}catch(Throwable t) {
				logger.error("Failed to process websocket frame {}! It'll be skipped for debug purposes.", debugPacketCounter);
				logger.error(t);
			}
		}
	}

	public void sendPacket(Packet pkt) {
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
		temporaryBuffer.writeVarIntToBuffer(i);
		try {
			pkt.writePacketData(temporaryBuffer);
		}catch(IOException ex) {
			logger.error("Failed to write packet {}!", pkt.getClass().getSimpleName());
			return;
		}
		
		int len = temporaryBuffer.writerIndex();
		byte[] bytes = new byte[len];
		temporaryBuffer.getBytes(0, bytes);
		
		webSocketClient.send(bytes);
	}

	public boolean checkDisconnected() {
		if(webSocketClient.isClosed()) {
			try {
				processReceivedPackets(); // catch kick message
			} catch (IOException e) {
			}
			doClientDisconnect(new TextComponentTranslation("disconnect.endOfStream"));
			return true;
		}else {
			return false;
		}
	}

}
