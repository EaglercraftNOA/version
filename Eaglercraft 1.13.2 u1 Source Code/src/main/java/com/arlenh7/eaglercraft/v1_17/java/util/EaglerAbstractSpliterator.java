package com.arlenh7.eaglercraft.v1_17.java.util;

import java.util.Spliterator;
import java.util.function.Consumer;

public abstract class EaglerAbstractSpliterator<T> implements Spliterator<T> {
   private final long estimateSize;
   private final int characteristics;

   protected EaglerAbstractSpliterator(long estimateSize, int characteristics) {
      this.estimateSize = estimateSize;
      this.characteristics = characteristics;
   }

   @Override
   public Spliterator<T> trySplit() {
      return null;
   }

   @Override
   public long estimateSize() {
      return this.estimateSize;
   }

   @Override
   public int characteristics() {
      return this.characteristics;
   }

   @Override
   public void forEachRemaining(Consumer<? super T> action) {
      while (this.tryAdvance(action)) {
      }
   }
}
