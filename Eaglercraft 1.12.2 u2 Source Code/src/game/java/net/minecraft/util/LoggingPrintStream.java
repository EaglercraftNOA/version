package net.minecraft.util;

import java.io.OutputStream;
import java.io.PrintStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.lax1dude.eaglercraft.internal.PlatformRuntime;

public class LoggingPrintStream extends PrintStream {
	protected final Logger LOGGER;
	protected final String domain;
	private final boolean err;

	public LoggingPrintStream(String domainIn, boolean err, OutputStream outStream) {
		super(outStream);
		this.domain = domainIn;
		this.LOGGER = LogManager.getLogger(domainIn);
		this.err = err;
	}

	public void println(String p_println_1_) {
		this.logString(p_println_1_);
	}

	public void println(Object p_println_1_) {
		this.logString(String.valueOf(p_println_1_));
	}

	private void logString(String string) {
		String callingClass = PlatformRuntime.getCallingClass(3);
		if (callingClass == null) {
			if (err) {
				LOGGER.error(string);
			} else {
				LOGGER.info(string);
			}
		} else {
			if (err) {
				LOGGER.error("@({}): {}", new Object[] { callingClass, string });
			} else {
				LOGGER.info("@({}): {}", new Object[] { callingClass, string });
			}
		}
	}
}
