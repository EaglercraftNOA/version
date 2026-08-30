package net.minecraft.network.login.server;

import com.mojang.authlib.GameProfile;
import java.io.IOException;
import net.lax1dude.eaglercraft.EaglercraftUUID;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.INetHandlerLoginClient;

public class SPacketLoginSuccess implements Packet<INetHandlerLoginClient> {
	private GameProfile profile;
	private int selectedProtocol = 3;

	public SPacketLoginSuccess() {
	}

	public SPacketLoginSuccess(GameProfile profileIn, int selectedProtocol) {
		this.profile = profileIn;
		this.selectedProtocol = selectedProtocol;
	}

	/**
	 * Reads the raw packet data from the data stream.
	 */
	public void readPacketData(PacketBuffer buf) throws IOException {
		String s = buf.readStringFromBuffer(36);
		String s1 = buf.readStringFromBuffer(16);
		selectedProtocol = buf.readableBytes() > 0 ? buf.readShort() : 3;
		EaglercraftUUID uuid = EaglercraftUUID.fromString(s);
		this.profile = new GameProfile(uuid, s1);
	}

	/**
	 * Writes the raw packet data to the data stream.
	 */
	public void writePacketData(PacketBuffer buf) throws IOException {
		EaglercraftUUID uuid = this.profile.getId();
		buf.writeString(uuid == null ? "" : uuid.toString());
		buf.writeString(this.profile.getName());
		if (selectedProtocol != 3) {
			buf.writeShort(selectedProtocol);
		}
	}

	/**
	 * Passes this Packet on to the NetHandler for processing.
	 */
	public void processPacket(INetHandlerLoginClient handler) {
		handler.handleLoginSuccess(this);
	}

	public GameProfile getProfile() {
		return this.profile;
	}
	
	public int getSelectedProtocol() {
		return selectedProtocol;
	}
}
