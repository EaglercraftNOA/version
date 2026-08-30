package com.arlenh7.eaglercraft.v1_17.java.util.concurrent.locks;

public class LockSupport {
   public static void park() {
      parkNanos(1000000L);
   }

   public static void park(Object blocker) {
      park();
   }

   public static void parkNanos(Object blocker, long nanos) {
      parkNanos(nanos);
   }

   public static void parkNanos(long nanos) {
      if (nanos <= 0L) {
         return;
      }
      try {
         Thread.sleep(Math.max(1L, nanos / 1000000L));
      } catch (InterruptedException interruptedexception) {
         Thread.currentThread().interrupt();
      }
   }

   public static void parkUntil(Object blocker, long deadline) {
      parkUntil(deadline);
   }

   public static void parkUntil(long deadline) {
      long wait = deadline - System.currentTimeMillis();
      if (wait > 0L) {
         try {
            Thread.sleep(wait);
         } catch (InterruptedException interruptedexception) {
            Thread.currentThread().interrupt();
         }
      }
   }

   public static void unpark(Thread thread) {
      if (thread != null) {
         thread.interrupt();
      }
   }
}
