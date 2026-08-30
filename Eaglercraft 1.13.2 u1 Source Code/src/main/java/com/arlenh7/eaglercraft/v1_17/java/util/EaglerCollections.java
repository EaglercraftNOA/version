package com.arlenh7.eaglercraft.v1_17.java.util;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.SortedSet;

public final class EaglerCollections {

   private EaglerCollections() {
   }

   public static <E> SortedSet<E> unmodifiableSortedSet(SortedSet<E> set) {
      if (set instanceof UnmodifiableSortedSet) {
         return set;
      }
      return new UnmodifiableSortedSet<>(set);
   }

   private static final class UnmodifiableSortedSet<E> extends AbstractSet<E> implements SortedSet<E>, Serializable {
      private final SortedSet<E> delegate;

      private UnmodifiableSortedSet(SortedSet<E> delegate) {
         this.delegate = delegate;
      }

      @Override
      public Comparator<? super E> comparator() {
         return this.delegate.comparator();
      }

      @Override
      public SortedSet<E> subSet(E fromElement, E toElement) {
         return unmodifiableSortedSet(this.delegate.subSet(fromElement, toElement));
      }

      @Override
      public SortedSet<E> headSet(E toElement) {
         return unmodifiableSortedSet(this.delegate.headSet(toElement));
      }

      @Override
      public SortedSet<E> tailSet(E fromElement) {
         return unmodifiableSortedSet(this.delegate.tailSet(fromElement));
      }

      @Override
      public E first() {
         return this.delegate.first();
      }

      @Override
      public E last() {
         return this.delegate.last();
      }

      @Override
      public Iterator<E> iterator() {
         Collection<E> unmodifiable = Collections.unmodifiableCollection(this.delegate);
         return unmodifiable.iterator();
      }

      @Override
      public int size() {
         return this.delegate.size();
      }

      @Override
      public boolean contains(Object o) {
         return this.delegate.contains(o);
      }
   }
}
