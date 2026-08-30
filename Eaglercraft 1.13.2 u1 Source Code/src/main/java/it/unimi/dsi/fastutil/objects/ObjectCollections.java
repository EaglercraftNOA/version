/*
	* Copyright (C) 2002-2024 Sebastiano Vigna
	*
	* Licensed under the Apache License, Version 2.0 (the "License");
	* you may not use this file except in compliance with the License.
	* You may obtain a copy of the License at
	*
	*     http://www.apache.org/licenses/LICENSE-2.0
	*
	* Unless required by applicable law or agreed to in writing, software
	* distributed under the License is distributed on an "AS IS" BASIS,
	* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	* See the License for the specific language governing permissions and
	* limitations under the License.
	*/
package it.unimi.dsi.fastutil.objects;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.function.Predicate;

/**
 * A class providing static methods and objects that do useful things with type-specific
 * collections.
 *
 * @see java.util.Collections
 */
public final class ObjectCollections {
	private ObjectCollections() {
	}

	/**
	 * An immutable class representing an empty type-specific collection.
	 *
	 * <p>
	 * This class may be useful to implement your own in case you subclass a type-specific collection.
	 */
	public abstract static class EmptyCollection<K> extends AbstractObjectCollection<K> {
		protected EmptyCollection() {
		}

		@Override
		public boolean contains(Object k) {
			return false;
		}

		@Override
		public Object[] toArray() {
			return ObjectArrays.EMPTY_ARRAY;
		}

		@Override
		public <T> T[] toArray(T[] array) {
			if (array.length > 0) array[0] = null;
			return array;
		}

		@Override
		@SuppressWarnings("unchecked")
		public ObjectBidirectionalIterator<K> iterator() {
			return ObjectIterators.EMPTY_ITERATOR;
		}

		@Override
		@SuppressWarnings("unchecked")
		public ObjectSpliterator<K> spliterator() {
			return ObjectSpliterators.EMPTY_SPLITERATOR;
		}

		@Override
		public int size() {
			return 0;
		}

		@Override
		public void clear() {
		}

		@Override
		public int hashCode() {
			return 0;
		}

		@Override
		public boolean equals(Object o) {
			if (o == this) return true;
			if (!(o instanceof Collection)) return false;
			return ((Collection<?>)o).isEmpty();
		}

		@Override
		public void forEach(final Consumer<? super K> action) {
		}

		@Override
		public boolean containsAll(final Collection<?> c) {
			return c.isEmpty();
		}

		@Override
		public boolean addAll(final Collection<? extends K> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean removeAll(final Collection<?> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean retainAll(final Collection<?> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean removeIf(final java.util.function.Predicate<? super K> filter) {
			Objects.requireNonNull(filter);
			return false;
		}
	}

	/** A synchronized wrapper class for collections. */
	static class SynchronizedCollection<K> implements ObjectCollection<K>, java.io.Serializable {
		private static final long serialVersionUID = -7046029254386353129L;
		protected final ObjectCollection<K> collection;
		protected final Object sync;

		protected SynchronizedCollection(final ObjectCollection<K> c, final Object sync) {
			this.collection = Objects.requireNonNull(c);
			this.sync = sync;
		}

		protected SynchronizedCollection(final ObjectCollection<K> c) {
			this.collection = Objects.requireNonNull(c);
			this.sync = this;
		}

		@Override
		public boolean add(final K k) {
			synchronized (sync) {
				return collection.add(k);
			}
		}

		@Override
		public boolean contains(final Object k) {
			synchronized (sync) {
				return collection.contains(k);
			}
		}

		@Override
		public boolean remove(final Object k) {
			synchronized (sync) {
				return collection.remove(k);
			}
		}

		@Override
		public int size() {
			synchronized (sync) {
				return collection.size();
			}
		}

		@Override
		public boolean isEmpty() {
			synchronized (sync) {
				return collection.isEmpty();
			}
		}

		@Override
		public Object[] toArray() {
			synchronized (sync) {
				return collection.toArray();
			}
		}

		@Override
		public <T> T[] toArray(final T[] a) {
			synchronized (sync) {
				return collection.toArray(a);
			}
		}

		@Override
		public ObjectIterator<K> iterator() {
			return collection.iterator();
		}

		@Override
		public ObjectSpliterator<K> spliterator() {
			return collection.spliterator();
		}

		@Override
		public java.util.stream.Stream<K> stream() {
			return collection.stream();
		}

		@Override
		public java.util.stream.Stream<K> parallelStream() {
			return collection.parallelStream();
		}

		@Override
		public void forEach(final Consumer<? super K> action) {
			synchronized (sync) {
				collection.forEach(action);
			}
		}

		@Override
		public boolean addAll(final Collection<? extends K> c) {
			synchronized (sync) {
				return collection.addAll(c);
			}
		}

		@Override
		public boolean containsAll(final Collection<?> c) {
			synchronized (sync) {
				return collection.containsAll(c);
			}
		}

		@Override
		public boolean removeAll(final Collection<?> c) {
			synchronized (sync) {
				return collection.removeAll(c);
			}
		}

		@Override
		public boolean retainAll(final Collection<?> c) {
			synchronized (sync) {
				return collection.retainAll(c);
			}
		}

		@Override
		public boolean removeIf(final Predicate<? super K> filter) {
			synchronized (sync) {
				return collection.removeIf(filter);
			}
		}

		@Override
		public void clear() {
			synchronized (sync) {
				collection.clear();
			}
		}

		@Override
		public String toString() {
			synchronized (sync) {
				return collection.toString();
			}
		}

		@Override
		public int hashCode() {
			synchronized (sync) {
				return collection.hashCode();
			}
		}

		@Override
		public boolean equals(final Object o) {
			if (o == this) return true;
			synchronized (sync) {
				return collection.equals(o);
			}
		}

		private void writeObject(java.io.ObjectOutputStream s) throws java.io.IOException {
			synchronized (sync) {
				s.defaultWriteObject();
			}
		}
	}

	/**
	 * Returns a synchronized collection backed by the specified collection.
	 *
	 * @param c the collection to be wrapped in a synchronized collection.
	 * @return a synchronized view of the specified collection.
	 * @see java.util.Collections#synchronizedCollection(Collection)
	 */
	public static <K> ObjectCollection<K> synchronize(final ObjectCollection<K> c) {
		return new SynchronizedCollection<>(c);
	}

	/**
	 * Returns a synchronized collection backed by the specified collection, using an assigned object to
	 * synchronize.
	 *
	 * @param c the collection to be wrapped in a synchronized collection.
	 * @param sync an object that will be used to synchronize the list access.
	 * @return a synchronized view of the specified collection.
	 * @see java.util.Collections#synchronizedCollection(Collection)
	 */
	public static <K> ObjectCollection<K> synchronize(final ObjectCollection<K> c, final Object sync) {
		return new SynchronizedCollection<>(c, sync);
	}

	/** An unmodifiable wrapper class for collections. */
	static class UnmodifiableCollection<K> implements ObjectCollection<K>, java.io.Serializable {
		private static final long serialVersionUID = -7046029254386353129L;
		protected final ObjectCollection<? extends K> collection;

		protected UnmodifiableCollection(final ObjectCollection<? extends K> c) {
			this.collection = Objects.requireNonNull(c);
		}

		@Override
		public boolean add(final K k) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean remove(final Object k) {
			throw new UnsupportedOperationException();
		}

		@Override
		public int size() {
			return collection.size();
		}

		@Override
		public boolean isEmpty() {
			return collection.isEmpty();
		}

		@Override
		public boolean contains(final Object o) {
			return collection.contains(o);
		}

		@Override
		public ObjectIterator<K> iterator() {
			return ObjectIterators.unmodifiable(collection.iterator());
		}

		@Override
		@SuppressWarnings("unchecked")
		public ObjectSpliterator<K> spliterator() {
			return (ObjectSpliterator<K>)collection.spliterator();
		}

		@Override
		@SuppressWarnings("unchecked")
		public java.util.stream.Stream<K> stream() {
			return (java.util.stream.Stream<K>)collection.stream();
		}

		@Override
		@SuppressWarnings("unchecked")
		public java.util.stream.Stream<K> parallelStream() {
			return (java.util.stream.Stream<K>)collection.parallelStream();
		}

		@Override
		public void clear() {
			throw new UnsupportedOperationException();
		}

		@Override
		public <T> T[] toArray(final T[] a) {
			return collection.toArray(a);
		}

		@Override
		public Object[] toArray() {
			return collection.toArray();
		}

		@Override
		public void forEach(final Consumer<? super K> action) {
			collection.forEach(action);
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			return collection.containsAll(c);
		}

		@Override
		public boolean addAll(Collection<? extends K> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean removeIf(final Predicate<? super K> filter) {
			throw new UnsupportedOperationException();
		}

		@Override
		public String toString() {
			return collection.toString();
		}

		@Override
		public int hashCode() {
			return collection.hashCode();
		}

		@Override
		public boolean equals(final Object o) {
			if (o == this) return true;
			return collection.equals(o);
		}
	}

	/**
	 * Returns an unmodifiable collection backed by the specified collection.
	 *
	 * @param c the collection to be wrapped in an unmodifiable collection.
	 * @return an unmodifiable view of the specified collection.
	 * @see java.util.Collections#unmodifiableCollection(Collection)
	 */
	public static <K> ObjectCollection<K> unmodifiable(final ObjectCollection<? extends K> c) {
		return new UnmodifiableCollection<>(c);
	}

	/** A collection wrapper class for iterables. */
	public static class IterableCollection<K> extends AbstractObjectCollection<K> implements java.io.Serializable {
		private static final long serialVersionUID = -7046029254386353129L;
		protected final ObjectIterable<K> iterable;

		protected IterableCollection(final ObjectIterable<K> iterable) {
			this.iterable = Objects.requireNonNull(iterable);
		}

		@Override
		public int size() {
			long size = iterable.spliterator().getExactSizeIfKnown();
			if (size >= 0) return (int)Math.min(Integer.MAX_VALUE, size);
			int c = 0;
			final ObjectIterator<K> iterator = iterator();
			while (iterator.hasNext()) {
				iterator.next();
				c++;
			}
			return c;
		}

		@Override
		public boolean isEmpty() {
			return !iterable.iterator().hasNext();
		}

		@Override
		public ObjectIterator<K> iterator() {
			return iterable.iterator();
		}

		@Override
		public ObjectSpliterator<K> spliterator() {
			return iterable.spliterator();
		}
	}

	/**
	 * Returns an unmodifiable collection backed by the specified iterable.
	 *
	 * @param iterable the iterable object to be wrapped in an unmodifiable collection.
	 * @return an unmodifiable collection view of the specified iterable.
	 */
	public static <K> ObjectCollection<K> asCollection(final ObjectIterable<K> iterable) {
		if (iterable instanceof ObjectCollection) return (ObjectCollection<K>)iterable;
		// TODO test for Collection but not our collection, and make a wrapper for it.
		return new IterableCollection<>(iterable);
	}

	/**
	 * Helper class for size decreasing size estimation.
	 *
	 * <p>
	 * Used to implement {@code toXWithExpectedSize} to prevent allocating a data structure big enough
	 * for the full stream in every thread, when only one thread (typically the first one) is going to
	 * need to "see" all of it at some point.
	 *
	 * <p>
	 * Currently set to always assume a roughly split-by-two strategy from the
	 * {@link java.util.Spliterator} backing the {@link java.util.stream.Stream} (or primitive
	 * equivalent). This may perform worse for {@code Spliterator}s that don't (for example the ones
	 * that wrap an {@link java.util.Iterator} as a {@code Spliterator}). This is quite rare in practice
	 * however. But note that even in such cases, this would <em>still</em> perform no worse (modulo
	 * some trivial amount) then the default "flat size for every list" method of combining. A way to
	 * detect such cases and use a different size estimation strategy may come in the future.
	 *
	 * <p>
	 * See the {@linkplain #SizeDecreasingSupplier constructor} for requirements of the {@link #builder}
	 * functor.
	 */
	static class SizeDecreasingSupplier<K, C extends ObjectCollection<K>> implements Supplier<C> {
		static final int RECOMMENDED_MIN_SIZE = 8;
		final AtomicInteger suppliedCount = new AtomicInteger(0);
		final int expectedFinalSize;
		final IntFunction<C> builder;

		/**
		 * Construct this {@link SizeDecreasingSupplier}.
		 *
		 * @param expectedFinalSize The expected size of the entire remaining contents of the {@code Stream}
		 *            and thus what size the Collection holding the final results should try to be
		 *            preallocated to.
		 * @param builder the builder function.
		 *            <p>
		 *            The input integer will be the size of the collection to preallocate.
		 *            <p>
		 *            It is recommended that you allocate a minimum size if the given {@code size} is small.
		 *            {@link #RECOMMENDED_MIN_SIZE} gives a good enough ballpark for most cases.
		 *            <p>
		 *            The subclass is free to use the default constructor for the Collection construction
		 *            instead of the {@code size} given if {@code size} falls below the default size used
		 *            for the default initial capacity for that Collection implementation. Often such
		 *            default constructors contain additional optimizations that giving an explicit initial
		 *            capacity would not do (even if given the default capacity as the initial capacity
		 *            explicitly).
		 *            <p>
		 *            {@link Supplier#get() get()} of the {@link IntFunction} must return non-{@code null}.
		 */
		SizeDecreasingSupplier(int expectedFinalSize, IntFunction<C> builder) {
			this.expectedFinalSize = expectedFinalSize;
			this.builder = builder;
		}

		// This method may be worth pulling into a shared superclass.
		@Override
		public C get() {
			// The "correct" splitting (assuming split by two) would be
			// expectedFinalSize / floor(ln_2(++suppliedCount)). But that seems too much trouble to be worth it.
			// Instead we will take a simple harmonically decreasing ratio (1, 1/2, 1/3, 1/4)
			//
			// Round up int math (to round up, not down) adapted from https://stackoverflow.com/a/503201.
			int expectedNeededNextSize = 1 + ((expectedFinalSize - 1) / suppliedCount.incrementAndGet());
			if (expectedNeededNextSize < 0) {
				// Overflow (and weird below zero results) failsafe
				expectedNeededNextSize = RECOMMENDED_MIN_SIZE;
			}
			return builder.apply(expectedNeededNextSize);
		}
	}
}
