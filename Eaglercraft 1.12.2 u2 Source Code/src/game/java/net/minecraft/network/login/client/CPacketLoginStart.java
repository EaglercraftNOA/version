package net.minecraft.network.login.client;

import com.mojang.authlib.GameProfile;
import java.io.IOException;
import net.lax1dude.eaglercraft.EaglercraftUUID;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.INetHandlerLoginServer;

public class CPacketLoginStart implements Packet<INetHandlerLoginServer> {
	private GameProfile profile;
	private byte[] skin;
	private byte[] cape;
	private byte[] protocols;
	private EaglercraftUUID brandUUID;

	public CPacketLoginStart() {
	}

	public CPacketLoginStart(GameProfile profileIn, byte[] skin, byte[] cape, byte[] protocols, EaglercraftUUID brandUUID) {
		this.profile = profileIn;
		this.skin = skin;
		this.cape = cape;
		this.protocols = protocols;
		this.brandUUID = brandUUID;
	}

	/**
	 * Reads the raw packet data from the data stream.
	 */
	public void readPacketData(PacketBuffer buf) throws IOException {
		this.profile = new GameProfile((EaglercraftUUID) null, buf.readStringFromBuffer(16));
		this.skin = buf.readByteArray();
		this.cape = buf.readableBytes() > 0 ? buf.readByteArray() : null;
		this.protocols = buf.readableBytes() > 0 ? buf.readByteArray() : null;
		this.brandUUID = buf.readableBytes() > 0 ? buf.readUuid() : null;
	}

	/**
	 * Writes the raw packet data to the data stream.
	 */
	public void writePacketData(PacketBuffer buf) throws IOException {
		buf.writeString(this.profile.getName());
		buf.writeByteArray(this.skin);
		buf.writeByteArray(this.cape);
		buf.writeByteArray(this.protocols);
		buf.writeUuid(brandUUID);
	}

	/**
	 * Passes this Packet on to the NetHandler for processing.
	 */
	public void processPacket(INetHandlerLoginServer handler) {
		handler.processLoginStart(this);
	}

	public GameProfile getProfile() {
		return this.profile;
	}

	public byte[] getSkin() {
		return this.skin;
	}

	public byte[] getCape() {
		return this.cape;
	}

	public byte[] getProtocols() {
		return this.protocols;
	}

	public EaglercraftUUID getBrandUUID() {
		return this.brandUUID;
	}
}
