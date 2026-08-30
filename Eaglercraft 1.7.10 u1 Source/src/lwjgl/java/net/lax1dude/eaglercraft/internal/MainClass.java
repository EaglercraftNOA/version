package net.lax1dude.eaglercraft.internal;

public class MainClass {

	public static void main(String[] args) {
		if (LWJGLSafeJVMLauncher.relaunchIfNeeded(MainClass.class.getName(), args)) {
			return;
		}
		LWJGLEntryPoint.main_(args);
	}

}
