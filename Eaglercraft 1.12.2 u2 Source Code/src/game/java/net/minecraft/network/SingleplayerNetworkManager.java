package net.minecraft.network;

import java.io.IOException;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.lax1dude.eaglercraft.internal.EnumEaglerConnectionState;
import net.lax1dude.eaglercraft.internal.IPCPacketData;
import net.lax1dude.eaglercraft.sp.SingleplayerServerController;
import net.lax1dude.eaglercraft.sp.internal.ClientPlatformSingleplayer;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

/**
 * Copyright (c) 2023-2024 lax1dude, ayunami2000. All Rights Reserved.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 */
public class SingleplayerNetworkManager extends NetworkManager {

	private int debugPacketCounter = 0;
	private byte[][] recievedPacketBuffer = new byte[16384][];
	private int recievedPacketBufferCounter = 0;
	public boolean isPlayerChannelOpen = false;

	public SingleplayerNetworkManager(String channel) {
		super(channel);
	}

	@Override
	public void connect() {
		clearRecieveQueue();
		SingleplayerServerController.openLocalPlayerChannel();
	}

	@Override
	public EnumEaglerConnectionState getConnectStatus() {
		return isPlayerChannelOpen ? EnumEaglerConnectionState.CONNECTED : EnumEaglerConnectionState.CLOSED;
	}

	@Override
	public void closeChannel(ITextComponent reason) {
		SingleplayerServerController.closeLocalPlayerChannel();
		if (nethandler != null) {
			nethandler.onDisconnect(reason);
		}
		clearRecieveQueue();
		clientDisconnected = true;
	}

	public void addRecievedPacket(byte[] next) {
		if (recievedPacketBufferCounter < recievedPacketBuffer.length - 1) {
			recievedPacketBuffer[recievedPacketBufferCounter++] = next;
		} else {
			logger.error("Dropping packets on recievedPacketBuffer for channel \"{}\"! (overflow)", address);
		}
	}

	@Override
	public void processReceivedPackets() throws IOException {
		if (nethandler == null)
			return;

		for (int i = 0; i < recievedPacketBufferCounter; ++i) {
			byte[] next = recievedPacketBuffer[i];
			recievedPacketBuffer[i] = null;
			++debugPacketCounter;
			try {
				ByteBuf nettyBuffer = Unpooled.buffer(next, next.length);
				nettyBuffer.writerIndex(next.length);
				PacketBuffer input = new PacketBuffer(nettyBuffer);
				int pktId = input.readVarIntFromBuffer();

				Packet pkt;
				try {
					pkt = packetState.getPacket(EnumPacketDirection.CLIENTBOUND, pktId);
				} catch (IllegalAccessException | InstantiationException ex) {
					throw new IOException("Recieved a packet with type " + pktId + " which is invalid!");
				}

				if (pkt == null) {
					throw new IOException(
							"Recieved packet type " + pktId + " which is undefined in state " + packetState);
				}

				try {
					pkt.readPacketData(input);
				} catch (Throwable t) {
					throw new IOException("Failed to read packet type '" + pkt.getClass().getSimpleName() + "'", t);
				}

				try {
					pkt.processPacket(nethandler);
				} catch (Throwable t) {
					logger.error("Failed to process {}! It'll be skipped for debug purposes.",
							pkt.getClass().getSimpleName());
					logger.error(t);
					t.printStackTrace();
				}

			} catch (Throwable t) {
				logger.error("Failed to process socket frame {}! It'll be skipped for debug purposes.",
						debugPacketCounter);
				logger.error(t);
			}
		}
		recievedPacketBufferCounter = 0;
	}

	@Override
	public void sendPacket(Packet pkt) {
		if (!isChannelOpen()) {
			logger.error("Packet was sent on a closed connection: {}", pkt.getClass().getSimpleName());
			return;
		}

		int i;
		try {
			i = packetState.getPacketId(EnumPacketDirection.SERVERBOUND, pkt);
		} catch (Throwable t) {
			logger.error("Incorrect packet for state: {}", pkt.getClass().getSimpleName());
			return;
		}

		temporaryBuffer.clear();
		temporaryBuffer.writeVarIntToBuffer(i);
		try {
			pkt.writePacketData(temporaryBuffer);
		} catch (IOException ex) {
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
		if (!isPlayerChannelOpen) {
			try {
				processReceivedPackets(); // catch kick message (if any)
			} catch (IOException e) {
			}
			clearRecieveQueue();
			doClientDisconnect(new TextComponentTranslation("disconnect.endOfStream"));
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean isLocalChannel() {
		return true;
	}

	public void clearRecieveQueue() {
		for (int i = 0; i < recievedPacketBufferCounter; ++i) {
			recievedPacketBuffer[i] = null;
		}
		recievedPacketBufferCounter = 0;
	}
}
