package com.arlenh7.eaglercraft.v1_17.java.util.concurrent.atomic;

public class LongAdder extends Number {
   private long value;

   public void add(long x) {
      this.value += x;
   }

   public void increment() {
      ++this.value;
   }

   public void decrement() {
      --this.value;
   }

   public long sum() {
      return this.value;
   }

   @Override
   public int intValue() {
      return (int)this.value;
   }

   @Override
   public long longValue() {
      return this.value;
   }

   @Override
   public float floatValue() {
      return this.value;
   }

   @Override
   public double doubleValue() {
      return this.value;
   }
}
