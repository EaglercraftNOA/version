package net.eymenwsmc;

import net.lax1dude.eaglercraft.EagRuntime;

public class Sys {
	private static String version = "2.9.4";

	public static String getVersion() {
		return version;
	}

	public static long getTime() {
		return EagRuntime.steadyTimeMillis();
	}

	public static long getTimerResolution() {
		return 1000L;
	}

	public static void openURL(String url) {
		EagRuntime.openLink(url);
	}
}
