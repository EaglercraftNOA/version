package net.lax1dude.eaglercraft.internal;

import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

public class LWJGLSafeJVMLauncher {

	private static final String RELAUNCH_PROPERTY = "eaglercraft.safeJvmRelaunched";

	public static boolean relaunchIfNeeded(String mainClass, String[] args) {
		if (Boolean.getBoolean(RELAUNCH_PROPERTY) || isSerialGCRequested() || javaMajorVersion() < 21) {
			return false;
		}
		try {
			List<String> cmd = new ArrayList<>();
			cmd.add(javaBinary());
			cmd.add("-D" + RELAUNCH_PROPERTY + "=true");
			cmd.add("-XX:+UseSerialGC");
			cmd.add("-XX:+DisableExplicitGC");
			cmd.add("-XX:-UseCompressedOops");
			cmd.add("-XX:-UseCompressedClassPointers");
			for (String arg : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
				if (!isGCSelectionFlag(arg) && !arg.startsWith("-D" + RELAUNCH_PROPERTY + "=")) {
					cmd.add(arg);
				}
			}
			cmd.add("-Djava.library.path=" + System.getProperty("java.library.path", ""));
			cmd.add("-cp");
			cmd.add(System.getProperty("java.class.path", ""));
			cmd.add(mainClass);
			for (String arg : args) {
				cmd.add(arg);
			}
			new ProcessBuilder(cmd).inheritIO().start();
			return true;
		} catch (IOException ex) {
			ex.printStackTrace();
			return false;
		}
	}

	private static boolean isSerialGCRequested() {
		for (String arg : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
			if ("-XX:+UseSerialGC".equals(arg)) {
				return true;
			}
		}
		return false;
	}

	private static boolean isGCSelectionFlag(String arg) {
		return arg.equals("-XX:+UseG1GC") || arg.equals("-XX:-UseG1GC") || arg.equals("-XX:+UseSerialGC")
				|| arg.equals("-XX:-UseSerialGC") || arg.equals("-XX:+UseParallelGC")
				|| arg.equals("-XX:-UseParallelGC") || arg.equals("-XX:+UseZGC") || arg.equals("-XX:-UseZGC")
				|| arg.equals("-XX:+UseShenandoahGC") || arg.equals("-XX:-UseShenandoahGC")
				|| arg.equals("-XX:+UseEpsilonGC") || arg.equals("-XX:-UseEpsilonGC")
				|| arg.equals("-XX:+DisableExplicitGC") || arg.equals("-XX:-DisableExplicitGC")
				|| arg.equals("-XX:+UseCompressedOops") || arg.equals("-XX:-UseCompressedOops")
				|| arg.equals("-XX:+UseCompressedClassPointers") || arg.equals("-XX:-UseCompressedClassPointers");
	}

	private static int javaMajorVersion() {
		String version = System.getProperty("java.specification.version", "0");
		if (version.startsWith("1.")) {
			version = version.substring(2);
		}
		int dot = version.indexOf('.');
		if (dot != -1) {
			version = version.substring(0, dot);
		}
		try {
			return Integer.parseInt(version);
		} catch (NumberFormatException ex) {
			return 0;
		}
	}

	private static String javaBinary() {
		String bin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
		if (System.getProperty("os.name", "").toLowerCase().contains("win")) {
			bin += ".exe";
		}
		return bin;
	}

}
