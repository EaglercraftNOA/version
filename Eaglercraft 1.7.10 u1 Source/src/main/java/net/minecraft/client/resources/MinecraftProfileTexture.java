package net.minecraft.client.resources;

public class MinecraftProfileTexture {
	private final String hash;
	private final String url;

	public MinecraftProfileTexture(String hash, String url) {
		this.hash = hash;
		this.url = url;
	}

	public String getHash() {
		return hash;
	}

	public String getUrl() {
		return url;
	}

	public enum Type {
		SKIN, CAPE
	}
}