package com.arlenh7.eaglercraft.v1_17.java.util.concurrent.atomic;

public class AtomicIntegerArray {
   private final int[] array;

   public AtomicIntegerArray(int length) {
      this.array = new int[length];
   }

   public AtomicIntegerArray(int[] array) {
      this.array = array.clone();
   }

   public int length() {
      return this.array.length;
   }

   public int get(int index) {
      return this.array[index];
   }

   public void set(int index, int value) {
      this.array[index] = value;
   }

   public void lazySet(int index, int value) {
      set(index, value);
   }

   public boolean compareAndSet(int index, int expect, int update) {
      if (this.array[index] == expect) {
         this.array[index] = update;
         return true;
      }
      return false;
   }

   public boolean weakCompareAndSet(int index, int expect, int update) {
      return compareAndSet(index, expect, update);
   }

   public int getAndSet(int index, int value) {
      int old = this.array[index];
      this.array[index] = value;
      return old;
   }

   public int getAndAdd(int index, int delta) {
      int old = this.array[index];
      this.array[index] = old + delta;
      return old;
   }

   public int addAndGet(int index, int delta) {
      this.array[index] += delta;
      return this.array[index];
   }

   public int incrementAndGet(int index) {
      return addAndGet(index, 1);
   }

   public int decrementAndGet(int index) {
      return addAndGet(index, -1);
   }
}
