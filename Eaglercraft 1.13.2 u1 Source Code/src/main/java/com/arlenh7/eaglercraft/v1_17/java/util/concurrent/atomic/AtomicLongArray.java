package com.arlenh7.eaglercraft.v1_17.java.util.concurrent.atomic;

public class AtomicLongArray {
   private final long[] array;

   public AtomicLongArray(int length) {
      this.array = new long[length];
   }

   public AtomicLongArray(long[] array) {
      this.array = array.clone();
   }

   public int length() {
      return this.array.length;
   }

   public long get(int index) {
      return this.array[index];
   }

   public void set(int index, long value) {
      this.array[index] = value;
   }

   public void lazySet(int index, long value) {
      set(index, value);
   }

   public boolean compareAndSet(int index, long expect, long update) {
      if (this.array[index] == expect) {
         this.array[index] = update;
         return true;
      }
      return false;
   }

   public boolean weakCompareAndSet(int index, long expect, long update) {
      return compareAndSet(index, expect, update);
   }

   public long getAndSet(int index, long value) {
      long old = this.array[index];
      this.array[index] = value;
      return old;
   }

   public long getAndAdd(int index, long delta) {
      long old = this.array[index];
      this.array[index] = old + delta;
      return old;
   }

   public long addAndGet(int index, long delta) {
      this.array[index] += delta;
      return this.array[index];
   }

   public long incrementAndGet(int index) {
      return addAndGet(index, 1L);
   }

   public long decrementAndGet(int index) {
      return addAndGet(index, -1L);
   }
}
