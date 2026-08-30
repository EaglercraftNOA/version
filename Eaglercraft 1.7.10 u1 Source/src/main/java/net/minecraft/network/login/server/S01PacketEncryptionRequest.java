package net.minecraft.network.login.server;

import java.io.IOException;
import java.security.PublicKey;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.INetHandlerLoginClient;
import net.minecraft.util.CryptManager;

public class S01PacketEncryptionRequest extends Packet {
	private String hashedServerId;
	private byte[] verifyToken;
	private static final String __OBFID = "CL_00001376";

	public S01PacketEncryptionRequest() {
	}

	public S01PacketEncryptionRequest(String p_i45268_1_, PublicKey p_i45268_2_, byte[] p_i45268_3_) {
		this.hashedServerId = p_i45268_1_;
		this.verifyToken = p_i45268_3_;
	}

	/**
	 * Reads the raw packet data from the data stream.
	 */
	public void readPacketData(PacketBuffer buf) throws IOException {
		this.hashedServerId = buf.readStringFromBuffer(20);
		this.verifyToken = buf.readByteArray();
	}

	/**
	 * Writes the raw packet data to the data stream.
	 */
	public void writePacketData(PacketBuffer buf) throws IOException {
		buf.writeStringToBuffer(this.hashedServerId);
		buf.writeByteArray(this.verifyToken);
	}

	public void processPacket(INetHandlerLoginClient p_148833_1_) {
		p_148833_1_.handleEncryptionRequest(this);
	}

	public String func_149609_c() {
		return this.hashedServerId;
	}

	public byte[] func_149607_e() {
		return this.verifyToken;
	}

	public void processPacket(INetHandler p_148833_1_) {
		this.processPacket((INetHandlerLoginClient) p_148833_1_);
	}
}
