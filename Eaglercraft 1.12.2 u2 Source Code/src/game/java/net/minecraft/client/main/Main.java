package net.minecraft.client.main;

import net.lax1dude.eaglercraft.internal.PlatformRuntime;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;

public class Main {
	public static void appMain() {
		System.setProperty("java.net.preferIPv6Addresses", "true");
		GameConfiguration gameconfiguration = new GameConfiguration(
				new GameConfiguration.UserInformation(new Session()),
				new GameConfiguration.DisplayInformation(854, 480, false),
				new GameConfiguration.GameInformation(false, "1.12"));
		PlatformRuntime.setThreadName("Client thread");
		(new Minecraft(gameconfiguration)).run();
	}
}
