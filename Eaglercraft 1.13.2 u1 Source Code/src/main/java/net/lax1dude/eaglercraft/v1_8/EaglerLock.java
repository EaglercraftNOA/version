package net.lax1dude.eaglercraft.v1_8;

public class EaglerLock {
   private Thread owner;
   private int count;

   public void lock() {
      Thread thread = Thread.currentThread();
      if (this.owner != null && this.owner != thread) {
         throw new IllegalStateException("Lock is already held");
      }
      this.owner = thread;
      ++this.count;
   }

   public void unlock() {
      if (this.owner != Thread.currentThread()) {
         throw new IllegalStateException("Lock is not held by current thread");
      }
      if (--this.count == 0) {
         this.owner = null;
      }
   }
}
