package net.lax1dude.eaglercraft.v1_8;

public class EaglerSocketAddress {

	private final String host;
	private final int port;

	public EaglerSocketAddress(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public String getHostString() {
		return host;
	}

	public int getPort() {
		return port;
	}

	@Override
	public String toString() {
		return host + ":" + port;
	}

}
