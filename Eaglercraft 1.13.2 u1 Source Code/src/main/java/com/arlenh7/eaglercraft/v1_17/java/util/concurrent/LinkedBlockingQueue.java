package com.arlenh7.eaglercraft.v1_17.java.util.concurrent;

import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class LinkedBlockingQueue<E> extends AbstractQueue<E> implements BlockingQueue<E> {
   private final LinkedList<E> queue = new LinkedList<>();
   private final int capacity;

   public LinkedBlockingQueue() {
      this(Integer.MAX_VALUE);
   }

   public LinkedBlockingQueue(int capacity) {
      if (capacity <= 0) {
         throw new IllegalArgumentException();
      }
      this.capacity = capacity;
   }

   @Override
   public synchronized boolean offer(E e) {
      if (e == null) {
         throw new NullPointerException();
      }
      if (this.queue.size() >= this.capacity) {
         return false;
      }
      boolean added = this.queue.offer(e);
      if (added) {
         this.notifyAll();
      }
      return added;
   }

   @Override
   public synchronized void put(E e) throws InterruptedException {
      while (!this.offer(e)) {
         this.wait();
      }
   }

   @Override
   public synchronized boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
      long deadline = System.currentTimeMillis() + unit.toMillis(timeout);
      while (!this.offer(e)) {
         long wait = deadline - System.currentTimeMillis();
         if (wait <= 0L) {
            return false;
         }
         this.wait(wait);
      }
      return true;
   }

   @Override
   public synchronized E take() throws InterruptedException {
      while (this.queue.isEmpty()) {
         this.wait();
      }
      E value = this.queue.removeFirst();
      this.notifyAll();
      return value;
   }

   @Override
   public synchronized E poll(long timeout, TimeUnit unit) throws InterruptedException {
      long deadline = System.currentTimeMillis() + unit.toMillis(timeout);
      while (this.queue.isEmpty()) {
         long wait = deadline - System.currentTimeMillis();
         if (wait <= 0L) {
            return null;
         }
         this.wait(wait);
      }
      return this.poll();
   }

   @Override
   public synchronized E poll() {
      E value = this.queue.poll();
      if (value != null) {
         this.notifyAll();
      }
      return value;
   }

   @Override
   public synchronized E peek() {
      return this.queue.peek();
   }

   @Override
   public synchronized int remainingCapacity() {
      return this.capacity - this.queue.size();
   }

   @Override
   public synchronized int drainTo(Collection<? super E> c) {
      return this.drainTo(c, Integer.MAX_VALUE);
   }

   @Override
   public synchronized int drainTo(Collection<? super E> c, int maxElements) {
      int count = 0;
      while (count < maxElements) {
         E value = this.poll();
         if (value == null) {
            break;
         }
         c.add(value);
         ++count;
      }
      return count;
   }

   @Override
   public synchronized Iterator<E> iterator() {
      return new LinkedList<>(this.queue).iterator();
   }

   @Override
   public synchronized int size() {
      return this.queue.size();
   }
}
