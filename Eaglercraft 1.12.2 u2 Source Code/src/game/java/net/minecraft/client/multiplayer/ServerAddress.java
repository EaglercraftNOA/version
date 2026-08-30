package net.minecraft.client.multiplayer;

public class ServerAddress {
	private final String ipAddress;
	private final int serverPort;

	public ServerAddress(String address, int port) {
		this.ipAddress = address;
		this.serverPort = port;
	}

	public String getIP() {
		return this.ipAddress;
	}

	public int getPort() {
		return this.serverPort;
	}
}
