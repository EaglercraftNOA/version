package net.lax1dude.eaglercraft.internal.lwjgl;

import net.lax1dude.eaglercraft.internal.LWJGLSafeJVMLauncher;

public class MainClass {

	public static void main(String[] args) {
		if (LWJGLSafeJVMLauncher.relaunchIfNeeded(MainClass.class.getName(), args)) {
			return;
		}
		LWJGLEntryPoint.main_(args);
	}

}
