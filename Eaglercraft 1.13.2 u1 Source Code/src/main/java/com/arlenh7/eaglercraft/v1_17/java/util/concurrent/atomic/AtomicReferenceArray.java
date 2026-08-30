package com.arlenh7.eaglercraft.v1_17.java.util.concurrent.atomic;

public class AtomicReferenceArray<E> {
   private final Object[] array;

   public AtomicReferenceArray(int length) {
      this.array = new Object[length];
   }

   public AtomicReferenceArray(E[] array) {
      this.array = array.clone();
   }

   public int length() {
      return this.array.length;
   }

   @SuppressWarnings("unchecked")
   public E get(int index) {
      return (E)this.array[index];
   }

   public void set(int index, E value) {
      this.array[index] = value;
   }

   public void lazySet(int index, E value) {
      set(index, value);
   }

   public boolean compareAndSet(int index, E expect, E update) {
      if (this.array[index] == expect) {
         this.array[index] = update;
         return true;
      }
      return false;
   }

   public boolean weakCompareAndSet(int index, E expect, E update) {
      return compareAndSet(index, expect, update);
   }

   @SuppressWarnings("unchecked")
   public E getAndSet(int index, E value) {
      E old = (E)this.array[index];
      this.array[index] = value;
      return old;
   }
}
