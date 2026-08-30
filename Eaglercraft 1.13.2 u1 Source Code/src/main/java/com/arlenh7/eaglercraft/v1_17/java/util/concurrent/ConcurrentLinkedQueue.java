package com.arlenh7.eaglercraft.v1_17.java.util.concurrent;

import java.util.ArrayDeque;
import java.util.Collection;

public class ConcurrentLinkedQueue<E> extends ArrayDeque<E> {
   public ConcurrentLinkedQueue() {
   }

   public ConcurrentLinkedQueue(Collection<? extends E> collection) {
      super(collection);
   }
}
