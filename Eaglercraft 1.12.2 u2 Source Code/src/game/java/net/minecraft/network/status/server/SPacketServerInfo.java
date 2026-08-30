package net.minecraft.network.status.server;

import java.io.IOException;

import org.json.JSONObject;

import net.lax1dude.eaglercraft.json.JSONTypeProvider;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.ServerStatusResponse;
import net.minecraft.network.status.INetHandlerStatusClient;

public class SPacketServerInfo implements Packet<INetHandlerStatusClient> {
	private ServerStatusResponse response;

	public SPacketServerInfo() {
	}

	public SPacketServerInfo(ServerStatusResponse responseIn) {
		this.response = responseIn;
	}

	/**
	 * Reads the raw packet data from the data stream.
	 */
	public void readPacketData(PacketBuffer buf) throws IOException {
		this.response = (ServerStatusResponse) JSONTypeProvider
				.deserialize(JSONTypeProvider.parse(buf.readStringFromBuffer(32767)), ServerStatusResponse.class);
	}

	/**
	 * Writes the raw packet data to the data stream.
	 */
	public void writePacketData(PacketBuffer buf) throws IOException {
		buf.writeString(JSONTypeProvider.serialize(this.response).toString());
	}

	/**
	 * Passes this Packet on to the NetHandler for processing.
	 */
	public void processPacket(INetHandlerStatusClient handler) {
		handler.handleServerInfo(this);
	}

	public ServerStatusResponse getResponse() {
		return this.response;
	}
}
