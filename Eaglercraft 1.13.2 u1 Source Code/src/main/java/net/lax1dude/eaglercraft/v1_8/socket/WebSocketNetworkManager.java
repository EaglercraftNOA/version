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

import java.util.ArrayDeque;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.lax1dude.eaglercraft.v1_8.EagRuntime;
import net.lax1dude.eaglercraft.v1_8.internal.EnumEaglerConnectionState;
import net.lax1dude.eaglercraft.v1_8.internal.IWebSocketClient;
import net.lax1dude.eaglercraft.v1_8.internal.IWebSocketFrame;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.network.Packet;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.network.play.server.SPacketCommandList;
import net.minecraft.network.play.server.SPacketKeepAlive;
import net.minecraft.network.play.server.SPacketMultiBlockChange;

public class WebSocketNetworkManager extends EaglercraftNetworkManager {

	private static final int MAX_PACKET_WORK_PER_TICK = 96;
	private static final int MAX_PRIORITY_CONTROL_PACKETS = 8;
	private static final int MAX_PRIORITY_SCAN_FRAMES = 16;
	private static final int MAX_PACKETS_PER_CLOSED_DRAIN = 256;
	private static final long PACKET_PROCESS_BUDGET_MS = 3L;
	private static final long PACKET_ERROR_LOG_INTERVAL_MS = 5000L;
	private static final long STRING_FRAME_LOG_INTERVAL_MS = 5000L;

	protected final IWebSocketClient webSocketClient;
	private final ArrayDeque<IWebSocketFrame> pendingBinaryFrames = new ArrayDeque<>();
	private long nextDetailedPacketErrorLog = 0L;
	private long nextStringFrameLog = 0L;
	private int suppressedPacketErrors = 0;
	private int suppressedStringFrameWarnings = 0;
	private EnumConnectionState cachedPriorityState = null;
	private Integer cachedKeepAlivePacketId = null;
	private Integer cachedPingPacketId = null;

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
		this.disconnectedReason = reason;
		webSocketClient.close();
		if(nethandler != null) {
			nethandler.onDisconnect(reason);
		}
		clientDisconnected = true;
	}

	public void processReceivedPackets() throws IOException {
		if(nethandler == null) return;
		drainIncomingFrames();
		processPriorityControlFrames();
		processPendingFrames(MAX_PACKET_WORK_PER_TICK, EagRuntime.steadyTimeMillis() + PACKET_PROCESS_BUDGET_MS);
	}

	private void drainIncomingFrames() {
		int stringFrames = webSocketClient.availableStringFrames();
		if(stringFrames > 0) {
			logStringFramesDiscarded(stringFrames);
			webSocketClient.clearStringFrames();
		}
		List<IWebSocketFrame> pkts = webSocketClient.getNextBinaryFrames();
		if(pkts == null || pkts.isEmpty()) {
			return;
		}
		for(int i = 0, l = pkts.size(); i < l; ++i) {
			pendingBinaryFrames.add(pkts.get(i));
		}
	}

	private void processPendingFrames(int maxPacketWork, long deadlineMillis) throws IOException {
		int workDone = 0;
		while(workDone < maxPacketWork && !pendingBinaryFrames.isEmpty()) {
			if(workDone > 0 && EagRuntime.steadyTimeMillis() >= deadlineMillis) {
				break;
			}
			IWebSocketFrame next = pendingBinaryFrames.poll();
			++debugPacketCounter;
			try {
				workDone += processFrame(next);
			}catch(Throwable t) {
				++workDone;
				logPacketError("Failed to process websocket frame " + debugPacketCounter + "! It'll be skipped for debug purposes.", t);
			}
		}
	}

	private void processPriorityControlFrames() throws IOException {
		if(packetState != EnumConnectionState.PLAY || pendingBinaryFrames.isEmpty()) {
			return;
		}
		updatePriorityPacketIds();
		Integer keepAlivePacketId = cachedKeepAlivePacketId;
		Integer pingPacketId = cachedPingPacketId;
		if(keepAlivePacketId == null && pingPacketId == null) {
			return;
		}
		List<IWebSocketFrame> priorityFrames = null;
		List<IWebSocketFrame> scannedFrames = null;
		int processed = 0;
		int scanned = 0;
		while(!pendingBinaryFrames.isEmpty() && processed < MAX_PRIORITY_CONTROL_PACKETS && scanned < MAX_PRIORITY_SCAN_FRAMES) {
			IWebSocketFrame next = pendingBinaryFrames.poll();
			++scanned;
			int packetId = readPacketId(next.getByteArray());
			if((keepAlivePacketId != null && packetId == keepAlivePacketId.intValue())
					|| (pingPacketId != null && packetId == pingPacketId.intValue())) {
				if(priorityFrames == null) {
					priorityFrames = new ArrayList<>();
				}
				priorityFrames.add(next);
				++processed;
			} else {
				if(scannedFrames == null) {
					scannedFrames = new ArrayList<>();
				}
				scannedFrames.add(next);
			}
		}
		if(scannedFrames != null) {
			for(int i = scannedFrames.size() - 1; i >= 0; --i) {
				pendingBinaryFrames.addFirst(scannedFrames.get(i));
			}
		}
		if(priorityFrames != null) {
			for(int i = 0, l = priorityFrames.size(); i < l; ++i) {
				++debugPacketCounter;
				try {
					processFrame(priorityFrames.get(i));
				}catch(Throwable t) {
					logPacketError("Failed to process priority websocket frame " + debugPacketCounter + "! It'll be skipped for debug purposes.", t);
				}
			}
		}
	}

	private void updatePriorityPacketIds() {
		if(cachedPriorityState == packetState) {
			return;
		}
		cachedPriorityState = packetState;
		if(packetState == EnumConnectionState.PLAY) {
			try {
				cachedKeepAlivePacketId = packetState.getPacketId(EnumPacketDirection.CLIENTBOUND, new SPacketKeepAlive(0L));
			}catch(Exception ex) {
				cachedKeepAlivePacketId = null;
			}
			cachedPingPacketId = null;
		}else {
			cachedKeepAlivePacketId = null;
			cachedPingPacketId = null;
		}
	}

	private int readPacketId(byte[] bytes) {
		int result = 0;
		int shift = 0;
		for(int i = 0; i < 5; ++i) {
			if(i >= bytes.length) {
				return -1;
			}
			int b = bytes[i] & 255;
			result |= (b & 127) << shift;
			if((b & 128) == 0) {
				return result;
			}
			shift += 7;
		}
		return -1;
	}

	private int processFrame(IWebSocketFrame next) throws IOException {
		byte[] asByteArray = next.getByteArray();

		if(injectedController != null && injectedController.handlePacket(asByteArray, 0)) {
			return 1;
		}

		ByteBuf nettyBuffer = Unpooled.wrappedBuffer(asByteArray);
		PacketBuffer input = new PacketBuffer(nettyBuffer);
		int pktId = input.readVarInt();

		Packet<?> pkt;
		try {
			pkt = packetState.getPacket(EnumPacketDirection.CLIENTBOUND, pktId);
			if(pkt != null) {
				pkt.readPacketData(input);
			}
		}catch(Throwable t) {
			throw new IOException("Failed to read packet type " + pktId + " in state " + packetState, t);
		}

		if(pkt == null) {
			throw new IOException("Recieved packet type " + pktId + " which is undefined in state " + packetState);
		}

		int extraBytes = input.readableBytes();
		if(extraBytes > 0) {
			throw new IOException("Packet " + pkt.getClass().getSimpleName() + " (" + pktId + ") was larger than expected, found " + extraBytes + " extra bytes");
		}

		try {
			processPacket(pkt);
		}catch(Throwable t) {
			logPacketError("Failed to process " + pkt.getClass().getSimpleName() + "! It'll be skipped for debug purposes.", t);
		}
		return getPacketWorkCost(pkt);
	}

	private int getPacketWorkCost(Packet<?> pkt) {
		if(pkt instanceof SPacketChunkData || pkt instanceof SPacketCommandList) {
			return 8;
		}
		if(pkt instanceof SPacketMultiBlockChange || pkt instanceof SPacketBlockChange) {
			return 1;
		}
		return 1;
	}

	private void logPacketError(String message, Throwable t) {
		long now = EagRuntime.steadyTimeMillis();
		if(now >= nextDetailedPacketErrorLog) {
			if(suppressedPacketErrors > 0) {
				logger.error("Suppressed {} repeated packet processing errors while the client caught up", suppressedPacketErrors);
				suppressedPacketErrors = 0;
			}
			logger.error(message);
			logger.error(t);
			nextDetailedPacketErrorLog = now + PACKET_ERROR_LOG_INTERVAL_MS;
		}else {
			++suppressedPacketErrors;
		}
	}

	private void logStringFramesDiscarded(int count) {
		long now = EagRuntime.steadyTimeMillis();
		if(now >= nextStringFrameLog) {
			if(suppressedStringFrameWarnings > 0) {
				logger.warn("Suppressed {} repeated string frame warnings while the client caught up", suppressedStringFrameWarnings);
				suppressedStringFrameWarnings = 0;
			}
			logger.warn("discarding {} string frames recieved on a binary connection", count);
			nextStringFrameLog = now + STRING_FRAME_LOG_INTERVAL_MS;
		}else {
			suppressedStringFrameWarnings += count;
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void processPacket(Packet<?> pkt) {
		((Packet)pkt).processPacket(nethandler);
	}

	public void sendPacket(Packet<?> pkt) {
		if(!isChannelOpen()) {
			logger.error("Packet was sent on a closed connection: {}", pkt.getClass().getSimpleName());
			return;
		}
		
		Integer i;
		try {
			i = packetState.getPacketId(EnumPacketDirection.SERVERBOUND, pkt);
		}catch(Exception ex) {
			logger.error("Failed to find packet ID for: {}", pkt.getClass().getSimpleName());
			return;
		}
		if(i == null) {
			logger.error("Incorrect packet for state: {}", pkt.getClass().getSimpleName());
			return;
		}
		
		temporaryBuffer.clear();
		temporaryBuffer.writeVarInt(i);
		try {
			pkt.writePacketData(temporaryBuffer);
		}catch(Throwable ex) {
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
				if(nethandler != null) {
					drainIncomingFrames();
					processPendingFrames(MAX_PACKETS_PER_CLOSED_DRAIN, EagRuntime.steadyTimeMillis() + 20L); // catch kick message
				}
			} catch (IOException e) {
			}
			doClientDisconnect(new TextComponentTranslation("disconnect.endOfStream"));
			return true;
		}else {
			return false;
		}
	}

	@Override
	public void injectRawFrame(byte[] data) {
		if(!isChannelOpen()) {
			logger.error("Frame was injected on a closed connection");
			return;
		}
		webSocketClient.send(data);
	}

}
