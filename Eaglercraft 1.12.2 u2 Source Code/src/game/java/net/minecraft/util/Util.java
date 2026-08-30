package net.minecraft.util;

import java.util.List;
import java.util.Locale;
import org.apache.logging.log4j.Logger;

import net.lax1dude.eaglercraft.EagRuntime;
import net.lax1dude.eaglercraft.futures.ExecutionException;
import net.lax1dude.eaglercraft.futures.FutureTask;

public class Util {
	public static Util.EnumOS getOSType() {
		return EagRuntime.getPlatformOS().getMinecraftEnum();
	}

	public static <V> V runTask(FutureTask<V> task, Logger logger) {
		try {
			task.run();
			return (V) task.get();
		} catch (ExecutionException executionexception) {
			logger.fatal("Error executing task", (Throwable) executionexception);
		} catch (InterruptedException interruptedexception) {
			logger.fatal("Error executing task", (Throwable) interruptedexception);
		}

		return (V) null;
	}

	public static <T> T getLastElement(List<T> list) {
		return list.get(list.size() - 1);
	}

	public static enum EnumOS {
		LINUX, SOLARIS, WINDOWS, OSX, UNKNOWN;
	}
}
