package net.lax1dude.eaglercraft.sp.server.internal.lwjgl;

import net.lax1dude.eaglercraft.sp.server.EaglerIntegratedServerWorker;
import net.lax1dude.eaglercraft.sp.server.internal.ServerPlatformSingleplayer;

public class DesktopIntegratedServer implements Runnable {

	public static Thread serverThread = null;

	public static void startIntegratedServer() {
		if (serverThread == null) {
			serverThread = new Thread(new DesktopIntegratedServer(), "IntegratedServer");
			serverThread.setDaemon(true);
			serverThread.start();
		}
	}

	@Override
	public void run() {
		try {
			ServerPlatformSingleplayer.initializeContext();
			EaglerIntegratedServerWorker.serverMain();
		} finally {
			serverThread = null;
		}
	}

}
