package net.minecraft.network.status.server;

import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.ServerStatusResponse;
import net.minecraft.network.status.INetHandlerStatusClient;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.IChatComponent;
import org.json.JSONObject;
import org.json.JSONException;

public class S00PacketServerInfo extends Packet {

	private ServerStatusResponse field_149296_b;
	private static final String __OBFID = "CL_00001384";

	public S00PacketServerInfo() {
	}

	public S00PacketServerInfo(ServerStatusResponse p_i45273_1_) {
		this.field_149296_b = p_i45273_1_;
	}

	/**
	 * Reads the raw packet data from the data stream.
	 */
	public void readPacketData(PacketBuffer p_148837_1_) throws IOException {
		try {
			String jsonStr = p_148837_1_.readStringFromBuffer(32767);
			JSONObject obj = new JSONObject(jsonStr);
			this.field_149296_b = ServerStatusResponse.Serializer.fromJson(obj);
		} catch (JSONException e) {
			throw new IOException("Failed to parse server info JSON", e);
		}
	}

	/**
	 * Writes the raw packet data to the data stream.
	 */
	public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
		try {
			JSONObject obj = ServerStatusResponse.Serializer.toJson(this.field_149296_b);
			p_148840_1_.writeStringToBuffer(obj.toString());
		} catch (JSONException e) {
			throw new IOException("Failed to serialize server info JSON", e);
		}
	}

	public void processPacket(INetHandlerStatusClient p_148833_1_) {
		p_148833_1_.handleServerInfo(this);
	}

	public ServerStatusResponse func_149294_c() {
		return this.field_149296_b;
	}

	/**
	 * If true, the network manager will process the packet immediately when
	 * received, otherwise it will queue it for processing. Currently true for:
	 * Disconnect, LoginSuccess, KeepAlive, ServerQuery/Info, Ping/Pong
	 */
	public boolean hasPriority() {
		return true;
	}

	public void processPacket(INetHandler p_148833_1_) {
		this.processPacket((INetHandlerStatusClient) p_148833_1_);
	}
}
