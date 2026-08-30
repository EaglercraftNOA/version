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
package it.unimi.dsi.fastutil.floats;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Consumer;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.doubles.DoubleIterators;
import it.unimi.dsi.fastutil.doubles.DoubleSpliterator;
import it.unimi.dsi.fastutil.doubles.DoubleSpliterators;
import it.unimi.dsi.fastutil.objects.ObjectArrays;

/**
 * A class providing static methods and objects that do useful things with type-specific
 * collections.
 *
 * @see java.util.Collections
 */
public final class FloatCollections {
	private FloatCollections() {
	}

	/* Only in the EmptyCollection class, where performance is critical, do we override the
	 * deprecated, Object based functional methods. For the rest, we just override the
	 * non-deprecated type-specific method, and let the default method from the interface
	 * filter into that. This is an extra method call and lambda creation, but it isn't worth
	 * complexifying the code generation for a case that is already marked as being inefficient.
	 */
	/**
	 * An immutable class representing an empty type-specific collection.
	 *
	 * <p>
	 * This class may be useful to implement your own in case you subclass a type-specific collection.
	 */
	public abstract static class EmptyCollection extends AbstractFloatCollection {
		protected EmptyCollection() {
		}

		@Override
		public boolean contains(float k) {
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

		public FloatBidirectionalIterator iterator() {
			return FloatIterators.EMPTY_ITERATOR;
		}

		@Override

		public FloatSpliterator spliterator() {
			return FloatSpliterators.EMPTY_SPLITERATOR;
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

		@Deprecated
		@Override
		public void forEach(final Consumer<? super Float> action) {
		}

		@Override
		public boolean containsAll(final Collection<?> c) {
			return c.isEmpty();
		}

		@Override
		public boolean addAll(final Collection<? extends Float> c) {
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

		@Deprecated
		@Override
		public boolean removeIf(final java.util.function.Predicate<? super Float> filter) {
			Objects.requireNonNull(filter);
			return false;
		}

		@Override
		public float[] toFloatArray() {
			return FloatArrays.EMPTY_ARRAY;
		}

		/* {@inheritDoc}
		 * @deprecated Please use {@code toArray()} instead&mdash;this method is redundant and will be removed in the future.
		 */
		@Deprecated
		@Override
		public float[] toFloatArray(float[] a) {
			return a;
		}

		@Override
		public void forEach(final FloatConsumer action) {
		}

		@Override
		public boolean containsAll(final FloatCollection c) {
			return c.isEmpty();
		}

		@Override
		public boolean addAll(final FloatCollection c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean removeAll(final FloatCollection c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean retainAll(final FloatCollection c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean removeIf(final FloatPredicate filter) {
			Objects.requireNonNull(filter);
			return false;
		}

		@Override
		public DoubleIterator doubleIterator() {
			return DoubleIterators.EMPTY_ITERATOR;
		}

		@Override
		public DoubleSpliterator doubleSpliterator() {
			return DoubleSpliterators.EMPTY_SPLITERATOR;
		}
	}

	/** A synchronized wrapper class for collections. */
	static class SynchronizedCollection implements FloatCollection, java.io.Serializable {
		private static final long serialVersionUID = -7046029254386353129L;
		protected final FloatCollection collection;
		protected final Object sync;

		protected SynchronizedCollection(final FloatCollection c, final Object sync) {
			this.collection = Objects.requireNonNull(c);
			this.sync = sync;
		}

		protected SynchronizedCollection(final FloatCollection c) {
			this.collection = Objects.requireNonNull(c);
			this.sync = this;
		}

		@Override
		public boolean add(final float k) {
			synchronized (sync) {
				return collection.add(k);
			}
		}

		@Override
		public boolean contains(final float k) {
			synchronized (sync) {
				return collection.contains(k);
			}
		}

		@Override
		public boolean rem(final float k) {
			synchronized (sync) {
				return collection.rem(k);
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
		public float[] toFloatArray() {
			synchronized (sync) {
				return collection.toFloatArray();
			}
		}

		@Override
		public Object[] toArray() {
			synchronized (sync) {
				return collection.toArray();
			}
		}

		/* {@inheritDoc}
		 * @deprecated Please use {@code toArray()} instead&mdash;this method is redundant and will be removed in the future.
		 */
		@Deprecated
		@Override
		public float[] toFloatArray(final float[] a) {
			return toArray(a);
		}

		@Override
		public float[] toArray(final float[] a) {
			synchronized (sync) {
				return collection.toArray(a);
			}
		}

		@Override
		public boolean addAll(final FloatCollection c) {
			synchronized (sync) {
				return collection.addAll(c);
			}
		}

		@Override
		public boolean containsAll(final FloatCollection c) {
			synchronized (sync) {
				return collection.containsAll(c);
			}
		}

		@Override
		public boolean removeAll(final FloatCollection c) {
			synchronized (sync) {
				return collection.removeAll(c);
			}
		}

		@Override
		public boolean retainAll(final FloatCollection c) {
			synchronized (sync) {
				return collection.retainAll(c);
			}
		}

		@Override
		@Deprecated
		public boolean add(final Float k) {
			synchronized (sync) {
				return collection.add(k);
			}
		}

		@Override
		@Deprecated
		public boolean contains(final Object k) {
			synchronized (sync) {
				return collection.contains(k);
			}
		}

		@Override
		@Deprecated
		public boolean remove(final Object k) {
			synchronized (sync) {
				return collection.remove(k);
			}
		}

		@Override
		public DoubleIterator doubleIterator() {
			return collection.doubleIterator();
		}

		@Override
		public DoubleSpliterator doubleSpliterator() {
			return collection.doubleSpliterator();
		}

		@Override
		public java.util.stream.DoubleStream doubleStream() {
			return collection.doubleStream();
		}

		@Override
		public java.util.stream.DoubleStream doubleParallelStream() {
			return collection.doubleParallelStream();
		}

		@Override
		public <T> T[] toArray(final T[] a) {
			synchronized (sync) {
				return collection.toArray(a);
			}
		}

		@Override
		public FloatIterator iterator() {
			return collection.iterator();
		}

		@Override
		public FloatSpliterator spliterator() {
			return collection.spliterator();
		}

		@Deprecated
		@Override
		public java.util.stream.Stream<Float> stream() {
			return collection.stream();
		}

		@Deprecated
		@Override
		public java.util.stream.Stream<Float> parallelStream() {
			return collection.parallelStream();
		}

		@Override
		public void forEach(final FloatConsumer action) {
			synchronized (sync) {
				collection.forEach(action);
			}
		}

		@Override
		public boolean addAll(final Collection<? extends Float> c) {
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
		public boolean removeIf(final FloatPredicate filter) {
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
	public static FloatCollection synchronize(final FloatCollection c) {
		return new SynchronizedCollection(c);
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
	public static FloatCollection synchronize(final FloatCollection c, final Object sync) {
		return new SynchronizedCollection(c, sync);
	}

	/** An unmodifiable wrapper class for collections. */
	static class UnmodifiableCollection implements FloatCollection, java.io.Serializable {
		private static final long serialVersionUID = -7046029254386353129L;
		protected final FloatCollection collection;

		protected UnmodifiableCollection(final FloatCollection c) {
			this.collection = Objects.requireNonNull(c);
		}

		@Override
		public boolean add(final float k) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean rem(final float k) {
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
		public boolean contains(final float o) {
			return collection.contains(o);
		}

		@Override
		public FloatIterator iterator() {
			return FloatIterators.unmodifiable(collection.iterator());
		}

		@Override
		public FloatSpliterator spliterator() {
			return collection.spliterator();
		}

		@Deprecated
		@Override
		public java.util.stream.Stream<Float> stream() {
			return collection.stream();
		}

		@Deprecated
		@Override
		public java.util.stream.Stream<Float> parallelStream() {
			return collection.parallelStream();
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
		public void forEach(final FloatConsumer action) {
			collection.forEach(action);
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			return collection.containsAll(c);
		}

		@Override
		public boolean addAll(Collection<? extends Float> c) {
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
		public boolean removeIf(final FloatPredicate filter) {
			throw new UnsupportedOperationException();
		}

		@Override
		@Deprecated
		public boolean add(final Float k) {
			throw new UnsupportedOperationException();
		}

		@Override
		@Deprecated
		public boolean contains(final Object k) {
			return collection.contains(k);
		}

		@Override
		@Deprecated
		public boolean remove(final Object k) {
			throw new UnsupportedOperationException();
		}

		@Override
		public float[] toFloatArray() {
			return collection.toFloatArray();
		}

		/* {@inheritDoc}
		 * @deprecated Please use {@code toArray()} instead&mdash;this method is redundant.
		 */
		@Deprecated
		@Override
		public float[] toFloatArray(final float[] a) {
			return toArray(a);
		}

		@Override
		public float[] toArray(final float[] a) {
			return collection.toArray(a);
		}

		@Override
		public boolean containsAll(FloatCollection c) {
			return collection.containsAll(c);
		}

		@Override
		public boolean addAll(FloatCollection c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean removeAll(FloatCollection c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean retainAll(FloatCollection c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public DoubleIterator doubleIterator() {
			return collection.doubleIterator();
		}

		@Override
		public DoubleSpliterator doubleSpliterator() {
			return collection.doubleSpliterator();
		}

		@Override
		public java.util.stream.DoubleStream doubleStream() {
			return collection.doubleStream();
		}

		@Override
		public java.util.stream.DoubleStream doubleParallelStream() {
			return collection.doubleParallelStream();
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
	public static FloatCollection unmodifiable(final FloatCollection c) {
		return new UnmodifiableCollection(c);
	}

	/** A collection wrapper class for iterables. */
	public static class IterableCollection extends AbstractFloatCollection implements java.io.Serializable {
		private static final long serialVersionUID = -7046029254386353129L;
		protected final FloatIterable iterable;

		protected IterableCollection(final FloatIterable iterable) {
			this.iterable = Objects.requireNonNull(iterable);
		}

		@Override
		public int size() {
			long size = iterable.spliterator().getExactSizeIfKnown();
			if (size >= 0) return (int)Math.min(Integer.MAX_VALUE, size);
			int c = 0;
			final FloatIterator iterator = iterator();
			while (iterator.hasNext()) {
				iterator.nextFloat();
				c++;
			}
			return c;
		}

		@Override
		public boolean isEmpty() {
			return !iterable.iterator().hasNext();
		}

		@Override
		public FloatIterator iterator() {
			return iterable.iterator();
		}

		@Override
		public FloatSpliterator spliterator() {
			return iterable.spliterator();
		}

		@Override
		public DoubleIterator doubleIterator() {
			return iterable.doubleIterator();
		}

		@Override
		public DoubleSpliterator doubleSpliterator() {
			return iterable.doubleSpliterator();
		}
	}

	/**
	 * Returns an unmodifiable collection backed by the specified iterable.
	 *
	 * @param iterable the iterable object to be wrapped in an unmodifiable collection.
	 * @return an unmodifiable collection view of the specified iterable.
	 */
	public static FloatCollection asCollection(final FloatIterable iterable) {
		if (iterable instanceof FloatCollection) return (FloatCollection)iterable;
		// TODO test for Collection but not our collection, and make a wrapper for it.
		return new IterableCollection(iterable);
	}
}
