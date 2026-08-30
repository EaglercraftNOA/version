
package net.eymenwsmc;

import java.io.IOException;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.lax1dude.eaglercraft.internal.EnumEaglerConnectionState;
import net.lax1dude.eaglercraft.internal.IWebSocketClient;
import net.lax1dude.eaglercraft.internal.IWebSocketFrame;
import net.lax1dude.eaglercraft.socket.EaglercraftNetworkManager;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C10PacketCreativeInventoryAction;

public class WebSocketNetworkManager extends EaglercraftNetworkManager {

	protected final IWebSocketClient webSocketClient;

	public WebSocketNetworkManager(IWebSocketClient webSocketClient) {
		super(webSocketClient.getCurrentURI());
		this.webSocketClient = webSocketClient;
	}

	@Override
	public void connect() {
		// no-op: connection is managed by IWebSocketClient provided
	}

	@Override
	public EnumEaglerConnectionState getConnectStatus() {
		return webSocketClient.getState();
	}

	@Override
	public void closeChannel(IChatComponent reason) {
		webSocketClient.close();
		if (nethandler != null) {
			nethandler.onDisconnect(reason);
		}
		clientDisconnected = true;
	}

	@Override
	public void processReceivedPackets() throws IOException {
		if (nethandler == null)
			return;
		if (webSocketClient.availableStringFrames() > 0) {
			logger.warn("discarding {} string frames recieved on a binary connection",
					webSocketClient.availableStringFrames());
			webSocketClient.clearStringFrames();
		}
		List<IWebSocketFrame> pkts = webSocketClient.getNextBinaryFrames();

		if (pkts == null) {
			return;
		}

		for (int i = 0, l = pkts.size(); i < l; ++i) {
			IWebSocketFrame next = pkts.get(i);
			++debugPacketCounter;
			try {
				byte[] asByteArray = next.getByteArray();

				if (injectedController != null && injectedController.handlePacket(asByteArray, 0)) {
					continue;
				}

				ByteBuf nettyBuffer = Unpooled.buffer(asByteArray, asByteArray.length);
				nettyBuffer.writerIndex(asByteArray.length);
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
				}

			} catch (Throwable t) {
				logger.error("Failed to process websocket frame {}! It'll be skipped for debug purposes.",
						debugPacketCounter);
				logger.error(t);
			}
		}
		// After processing all packets, tick ITickable handler if present
		if (nethandler != null && nethandler instanceof net.minecraft.util.ITickable) {
			((net.minecraft.util.ITickable) nethandler).update();
		}
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

		if (pkt instanceof C08PacketPlayerBlockPlacement) {
			logger.info("[CLIENT->WS] (com.demez) Sending C08PacketPlayerBlockPlacement id={}, bytes={}", i, len);
		} else if (pkt instanceof C10PacketCreativeInventoryAction) {
			logger.info("[CLIENT->WS] (com.demez) Sending C10PacketCreativeInventoryAction id={}, bytes={}", i, len);
		}

		webSocketClient.send(bytes);
	}

	@Override
	public boolean checkDisconnected() {
		if (webSocketClient.isClosed()) {
			try {
				processReceivedPackets(); // catch kick message if any
			} catch (IOException e) {
			}
			doClientDisconnect(new ChatComponentTranslation("disconnect.endOfStream"));
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void injectRawFrame(byte[] data) {
		if (!isChannelOpen()) {
			logger.error("Frame was injected on a closed connection");
			return;
		}
		webSocketClient.send(data);
	}
}
