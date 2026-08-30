package net.minecraft.realms;

import com.mojang.authlib.GameProfile;
import java.net.Proxy;
import net.lax1dude.eaglercraft.EaglercraftUUID;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;
import net.minecraft.world.WorldSettings;

public class Realms {
	private static final String __OBFID = "CL_00001892";

	public static boolean isTouchScreen() {
		return Minecraft.getMinecraft().gameSettings.touchscreen;
	}

	public static String sessionId() {
		Session var0 = Minecraft.getMinecraft().getSession();
		return var0 == null ? null : var0.getSessionID();
	}

	public static String userName() {
		Session var0 = Minecraft.getMinecraft().getSession();
		return var0 == null ? null : var0.getUsername();
	}

	public static long currentTimeMillis() {
		return Minecraft.getSystemTime();
	}

	public static String getSessionId() {
		return Minecraft.getMinecraft().getSession().getSessionID();
	}

	public static String getName() {
		return Minecraft.getMinecraft().getSession().getUsername();
	}

	public static String uuidToName(String p_uuidToName_0_) {
		return 
				new GameProfile(
						EaglercraftUUID.fromString(p_uuidToName_0_
								.replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5")),
						(String) null)
				.getName();
	}

	public static void setScreen(RealmsScreen p_setScreen_0_) {
		Minecraft.getMinecraft().displayGuiScreen(p_setScreen_0_.getProxy());
	}

	public static String getGameDirectoryPath() {
		return Minecraft.getMinecraft().mcDataDir.getPath();
	}

	public static int survivalId() {
		return WorldSettings.GameType.SURVIVAL.getID();
	}

	public static int creativeId() {
		return WorldSettings.GameType.CREATIVE.getID();
	}

	public static int adventureId() {
		return WorldSettings.GameType.ADVENTURE.getID();
	}
}
