package net.minecraft.util;

import com.mojang.authlib.GameProfile;

import net.lax1dude.eaglercraft.EaglercraftRandom;
import net.lax1dude.eaglercraft.EaglercraftUUID;
import net.lax1dude.eaglercraft.profile.EaglerProfile;

public class Session {

	private GameProfile profile;

	private static final EaglercraftUUID offlineUUID;

	public Session() {
		update(EaglerProfile.getName(), offlineUUID);
	}

	public GameProfile getProfile() {
		return profile;
	}

	public String getUsername() {
		return this.profile == null ? EaglerProfile.getName() : this.profile.getName();
	}

	public void reset() {
		update(EaglerProfile.getName(), offlineUUID);
	}

	public void update(String serverUsername, EaglercraftUUID uuid) {
		profile = new GameProfile(uuid, serverUsername);
	}

	static {
		byte[] bytes = new byte[16];
		(new EaglercraftRandom()).nextBytes(bytes);
		offlineUUID = new EaglercraftUUID(bytes);
	}
}
