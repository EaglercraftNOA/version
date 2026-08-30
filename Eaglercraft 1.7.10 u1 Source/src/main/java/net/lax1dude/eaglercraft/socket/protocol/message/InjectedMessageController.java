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

package net.lax1dude.eaglercraft.socket.protocol.message;

import net.lax1dude.eaglercraft.socket.protocol.GamePacketOutputBuffer;
import net.lax1dude.eaglercraft.socket.protocol.GamePluginMessageProtocol;
import net.lax1dude.eaglercraft.socket.protocol.pkt.GameMessageHandler;
import net.lax1dude.eaglercraft.socket.protocol.pkt.GameMessagePacket;
import net.lax1dude.eaglercraft.socket.protocol.util.ReusableByteArrayInputStream;
import net.lax1dude.eaglercraft.socket.protocol.util.ReusableByteArrayOutputStream;
import net.lax1dude.eaglercraft.socket.protocol.util.SimpleInputBufferImpl;
import net.lax1dude.eaglercraft.socket.protocol.util.SimpleOutputBufferImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.List;

public class InjectedMessageController extends MessageController {

	private static final Logger logger = LogManager.getLogger("InjectedMessageController");

	private final ReusableByteArrayInputStream byteInputStreamSingleton = new ReusableByteArrayInputStream();
	private final ReusableByteArrayOutputStream byteOutputStreamSingleton = new ReusableByteArrayOutputStream();
	private final SimpleInputBufferImpl inputStreamSingleton = new SimpleInputBufferImpl(byteInputStreamSingleton);
	private final SimpleOutputBufferImpl outputStreamSingleton = new SimpleOutputBufferImpl(byteOutputStreamSingleton);

	public interface IBinarySendFunction {
		void sendBinaryFrame(byte[] contents);
	}

	private final IBinarySendFunction send;

	public InjectedMessageController(GamePluginMessageProtocol protocol, GameMessageHandler handler, int direction,
			IBinarySendFunction send) {
		super(protocol, handler, direction);
		this.send = send;
	}

	public boolean handlePacket(byte[] data, int offset) throws IOException {
		if (data.length - offset <= 1 || data[offset] != (byte) 0xEE) {
			return false;
		}

		// Single or multi-packet
		if (data[offset + 1] == (byte) 0xFF) {
			// 0xEEFF <count varint> [len varint][payload]...
			byteInputStreamSingleton.feedBuffer(data, offset + 2);
			inputStreamSingleton.setToByteArrayReturns(data.length - offset - 2);
			int count = inputStreamSingleton.readVarInt();
			for (int i = 0; i < count; ++i) {
				int len = inputStreamSingleton.readVarInt();
				if (len < 0 || len > inputStreamSingleton.available()) {
					throw new IOException(
							"Packet fragment is too long: " + len + " > " + inputStreamSingleton.available());
				}
				// Read this fragment as a V5 packet
				byteInputStreamSingleton.feedBuffer(data, byteInputStreamSingleton.getReaderIndex());
				inputStreamSingleton.setToByteArrayReturns(len);
				GameMessagePacket pkt = protocol.readPacketV5(receiveDirection, inputStreamSingleton);
				if (pkt != null) {
					handlePacket(pkt);
				}
				// Advance reader index by remaining bytes in this fragment if any
				int remain = inputStreamSingleton.available();
				if (remain > 0) {
					byteInputStreamSingleton.setReaderIndex(byteInputStreamSingleton.getReaderIndex() + remain);
				}
			}
			return true;
		} else {
			// Single: 0xEE [payload]
			byteInputStreamSingleton.feedBuffer(data, offset + 1);
			inputStreamSingleton.setToByteArrayReturns(data.length - offset - 1);
			GameMessagePacket pkt = protocol.readPacketV5(receiveDirection, inputStreamSingleton);
			if (pkt != null) {
				handlePacket(pkt);
			}
			return true;
		}
	}

	@Override
	protected void writePacket(GameMessagePacket packet) throws IOException {
		// Build binary frame: 0xEE followed by V5 payload (id + body)
		int payloadLen = packet.length();
		byteOutputStreamSingleton.feedBuffer(payloadLen == 0 ? new byte[64] : new byte[1 + payloadLen]);
		byteOutputStreamSingleton.write(0xEE);
		// serialize payload into the remaining buffer using V5 writer (continues after
		// 0xEE)
		protocol.writePacketV5(sendDirection, outputStreamSingleton, packet);
		byte[] data = byteOutputStreamSingleton.returnBuffer();
		byteOutputStreamSingleton.feedBuffer(null);
		int expected = 1 + payloadLen;
		if (payloadLen != 0 && data.length != expected) {
			logger.warn("Packet " + packet.getClass().getSimpleName() + " was the wrong length after serialization, "
					+ data.length + " != " + expected);
		}
		send.sendBinaryFrame(data);
	}

	@Override
	protected void writeMultiPacket(List<GameMessagePacket> packets) throws IOException {
		int total = packets.size();
		// Prepare serialized payloads (excluding the leading 0xEE)
		byte[][] payloads = new byte[total][];
		for (int idx = 0; idx < total; ++idx) {
			GameMessagePacket packet = packets.get(idx);
			int payloadLen = packet.length();
			byteOutputStreamSingleton.feedBuffer(payloadLen == 0 ? new byte[64] : new byte[payloadLen]);
			// write packet id + body into buffer starting at 0 (fresh buffer)
			protocol.writePacketV5(sendDirection, outputStreamSingleton, packet);
			payloads[idx] = byteOutputStreamSingleton.returnBuffer();
			byteOutputStreamSingleton.feedBuffer(null);
		}
		int start = 0;
		int sendCount, totalLen, lastLen;
		while (total > start) {
			sendCount = 0;
			totalLen = 0;
			do {
				int i = payloads[start + sendCount].length;
				lastLen = GamePacketOutputBuffer.getVarIntSize(i) + i;
				totalLen += lastLen;
				++sendCount;
			} while (totalLen < 32760 && sendCount < total - start && sendCount < maxMultiPacket);
			if (totalLen >= 32760) {
				--sendCount;
				totalLen -= lastLen;
			}
			if (sendCount <= 1) {
				// single packet: send as simple 0xEE + payload
				byte[] body = payloads[start++];
				byteOutputStreamSingleton.feedBuffer(new byte[1 + body.length]);
				byteOutputStreamSingleton.write(0xEE);
				outputStreamSingleton.write(body, 0, body.length);
				send.sendBinaryFrame(byteOutputStreamSingleton.returnBuffer());
				byteOutputStreamSingleton.feedBuffer(null);
				continue;
			}
			// multi packet: 0xEEFF <count varint> [len varint][payload]...
			byteOutputStreamSingleton
					.feedBuffer(new byte[2 + totalLen + GamePacketOutputBuffer.getVarIntSize(sendCount)]);
			outputStreamSingleton.writeShort(0xEEFF);
			outputStreamSingleton.writeVarInt(sendCount);
			for (int j = 0; j < sendCount; ++j) {
				byte[] dat = payloads[start++];
				outputStreamSingleton.writeVarInt(dat.length);
				outputStreamSingleton.write(dat, 0, dat.length);
			}
			send.sendBinaryFrame(byteOutputStreamSingleton.returnBuffer());
			byteOutputStreamSingleton.feedBuffer(null);
		}
	}

}
