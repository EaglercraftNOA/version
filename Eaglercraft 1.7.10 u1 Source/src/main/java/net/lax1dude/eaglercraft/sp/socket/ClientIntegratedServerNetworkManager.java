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

package net.lax1dude.eaglercraft.sp.socket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import net.lax1dude.eaglercraft.internal.EnumEaglerConnectionState;
import net.lax1dude.eaglercraft.internal.IPCPacketData;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.lax1dude.eaglercraft.socket.EaglercraftNetworkManager;
import net.lax1dude.eaglercraft.sp.SingleplayerServerController;
import net.lax1dude.eaglercraft.sp.internal.ClientPlatformSingleplayer;
import net.lax1dude.eaglercraft.sp.lan.LANServerController;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.INetHandlerLoginClient;
import net.minecraft.network.login.server.S02PacketLoginSuccess;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S0EPacketSpawnObject;
import net.minecraft.network.play.server.S0FPacketSpawnMob;
import net.minecraft.network.play.server.S10PacketSpawnPainting;
import net.minecraft.network.play.server.S11PacketSpawnExperienceOrb;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.network.play.server.S1CPacketEntityMetadata;
import net.minecraft.network.play.server.S20PacketEntityProperties;
import net.minecraft.network.play.server.S21PacketChunkData;
import net.minecraft.network.play.server.S26PacketMapChunkBulk;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;

public class ClientIntegratedServerNetworkManager extends EaglercraftNetworkManager {

	private int debugPacketCounter = 0;
	private final List<byte[]> recievedPacketBuffer = new LinkedList<>();
	private final Object recievedPacketBufferLock = new Object();
	private final List<byte[]> deferredPacketBuffer = new LinkedList<>();
	private boolean playerSpawned = false;
	public boolean isPlayerChannelOpen = false;

	public ClientIntegratedServerNetworkManager(String channel) {
		super("~!LOCAL_PLAYER");
	}

	@Override
	public void connect() {
		clearRecieveQueue();
		isPlayerChannelOpen = true; // Mark channel as open immediately
		SingleplayerServerController.openLocalPlayerChannel();
	}

	@Override
	public EnumEaglerConnectionState getConnectStatus() {
		EnumEaglerConnectionState status = isPlayerChannelOpen ? EnumEaglerConnectionState.CONNECTED
				: EnumEaglerConnectionState.CLOSED;
		return status;
	}

	@Override
	public void closeChannel(IChatComponent reason) {
		LANServerController.closeLAN();
		SingleplayerServerController.closeLocalPlayerChannel();
		if (nethandler != null) {
			nethandler.onDisconnect(reason);
		}
		clearRecieveQueue();
		clientDisconnected = true;
	}

	public void addRecievedPacket(byte[] next) {
		if (next == null) {
			return;
		}
		synchronized (recievedPacketBufferLock) {
			recievedPacketBuffer.add(next);
		}
	}

	@Override
	public void processReceivedPackets() throws IOException {
		if (nethandler == null) {
			return;
		}

		List<byte[]> packetsToProcess;
		synchronized (recievedPacketBufferLock) {
			if (recievedPacketBuffer.isEmpty()) {
				return;
			}
			packetsToProcess = new ArrayList<>(recievedPacketBuffer);
			recievedPacketBuffer.clear();
		}

		for (int packetIndex = 0; packetIndex < packetsToProcess.size(); ++packetIndex) {
			byte[] next = packetsToProcess.get(packetIndex);
			if (next == null) {
				continue;
			}

			++debugPacketCounter;
			try {
				if (injectedController != null && injectedController.handlePacket(next, 0)) {
					continue;
				}

				if (next.length == 0) {
					continue;
				}

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

				Minecraft mc = Minecraft.getMinecraft();

				// CRITICAL: Inject S08 after S01 if server doesn't send it
				if (pkt instanceof net.minecraft.network.play.server.S01PacketJoinGame) {
					try {
						S08PacketPlayerPosLook posPacket = new S08PacketPlayerPosLook(-8.5, 68.0, 229.5, 0.0f, 0.0f, false);
						ByteBuf tempBuf = Unpooled.buffer();
						PacketBuffer tempPacketBuf = new PacketBuffer(tempBuf);
						tempPacketBuf.writeVarIntToBuffer(0x08);
						posPacket.writePacketData(tempPacketBuf);
						byte[] packetBytes = new byte[tempBuf.writerIndex()];
						tempBuf.getBytes(0, packetBytes);
						synchronized (recievedPacketBufferLock) {
							recievedPacketBuffer.add(packetBytes);
						}
					} catch(Exception e) {
						logger.error("Failed to inject S08 packet", e);
					}
				}

				// Defer packets that need player (ENTITY PACKETS IMMEDIATE for combat)
				boolean needsPlayer = pkt instanceof net.minecraft.network.play.server.S39PacketPlayerAbilities
						// REMOVED: S0EPacketSpawnObject - immediate for entity tracking
						// REMOVED: S0FPacketSpawnMob - immediate for entity tracking
						|| pkt instanceof net.minecraft.network.play.server.S10PacketSpawnPainting
						|| pkt instanceof net.minecraft.network.play.server.S11PacketSpawnExperienceOrb
						// REMOVED: S12PacketEntityVelocity - immediate for combat
						// REMOVED: S14PacketEntity - immediate for entity updates
						// REMOVED: S18PacketEntityTeleport - immediate for entity position
						// REMOVED: S1CPacketEntityMetadata - immediate for combat
						|| pkt instanceof net.minecraft.network.play.server.S20PacketEntityProperties;
				
				if (needsPlayer && !playerSpawned && (mc == null || mc.theWorld == null || mc.thePlayer == null)) {
					synchronized(deferredPacketBuffer) {
						deferredPacketBuffer.add(next);
					}
					continue;
				}

				// Handle state transitions
				if (pkt instanceof net.minecraft.network.login.server.S02PacketLoginSuccess) {
					pkt.processPacket((net.minecraft.network.login.INetHandlerLoginClient) nethandler);
					this.packetState = EnumConnectionState.PLAY;
					this.nethandler = new net.minecraft.client.network.NetHandlerPlayClient(mc, null, this);
				} else if (pkt instanceof net.minecraft.network.play.server.S08PacketPlayerPosLook) {
					// Mark player as spawned BEFORE processing
					playerSpawned = true;
					try {
						pkt.processPacket(nethandler);
					} catch(Throwable t) {
						logger.error("Failed to process {}! It'll be skipped for debug purposes.", pkt.getClass().getSimpleName());
						logger.error(t);
					}
					// Process deferred packets
					synchronized(deferredPacketBuffer) {
						if (!deferredPacketBuffer.isEmpty()) {
							synchronized (recievedPacketBufferLock) {
								recievedPacketBuffer.addAll(0, deferredPacketBuffer);
							}
							deferredPacketBuffer.clear();
						}
					}
				} else {
					// Standard packet processing (1.8 style)
					try {
						pkt.processPacket(nethandler);
					} catch(Throwable t) {
						logger.error("Failed to process {}! It'll be skipped for debug purposes.", pkt.getClass().getSimpleName());
						logger.error(t);
					}
				}
			} catch (Throwable t) {
				logger.error("Failed to process socket frame {}! It'll be skipped for debug purposes.",
						debugPacketCounter);
				logger.error(t);
			}
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
			logger.error("Failed to write packet {}!", pkt.getClass().getSimpleName(), ex);
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
				processReceivedPackets(); // catch kick message
			} catch (IOException e) {
			}
			clearRecieveQueue();
			doClientDisconnect(new ChatComponentTranslation("disconnect.endOfStream"));
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
		synchronized (recievedPacketBufferLock) {
			recievedPacketBuffer.clear();
		}
		synchronized(deferredPacketBuffer) {
			deferredPacketBuffer.clear();
		}
		playerSpawned = false;
	}

	@Override
	public void injectRawFrame(byte[] data) {
		if (!isChannelOpen()) {
			logger.error("Frame was injected on a closed connection");
			return;
		}
		ClientPlatformSingleplayer.sendPacket(new IPCPacketData(address, data));
	}

}