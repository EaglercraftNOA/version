package net.minecraft.server;

import java.io.OutputStream;
import net.minecraft.util.LoggingPrintStream;

public class DebugLoggingPrintStream extends LoggingPrintStream {
   public DebugLoggingPrintStream(String domainIn, OutputStream outStream) {
      super(domainIn, outStream);
   }

   protected void logString(String string) {
      StackTraceElement[] astacktraceelement = Thread.currentThread().getStackTrace();
      if (astacktraceelement.length == 0) {
         LOGGER.info("[{}]: {}", this.domain, string);
      } else {
         StackTraceElement stacktraceelement = astacktraceelement[Math.min(3, astacktraceelement.length - 1)];
         LOGGER.info("[{}]@.({}:{}): {}", this.domain, stacktraceelement.getFileName(), stacktraceelement.getLineNumber(), string);
      }

   }
}
