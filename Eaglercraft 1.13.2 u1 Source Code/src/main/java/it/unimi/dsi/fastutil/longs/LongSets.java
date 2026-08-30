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
package it.unimi.dsi.fastutil.longs;

import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;

/**
 * A class providing static methods and objects that do useful things with type-specific sets.
 *
 * @see java.util.Collections
 */
public final class LongSets {
	private LongSets() {
	}

	/** The maximum size to choose ArraySet over OpenHashSet for utilites that automatically choose. */
	static final int ARRAY_SET_CUTOFF = 4;

	/**
	 * An immutable class representing the empty set and implementing a type-specific set interface.
	 *
	 * <p>
	 * This class may be useful to implement your own in case you subclass a type-specific set.
	 */
	public static class EmptySet extends LongCollections.EmptyCollection implements LongSet, java.io.Serializable, Cloneable {
		private static final long serialVersionUID = -7046029254386353129L;

		protected EmptySet() {
		}

		@Override
		public boolean remove(long ok) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Object clone() {
			return EMPTY_SET;
		}

		@Override
		@SuppressWarnings("rawtypes")
		public boolean equals(final Object o) {
			return o instanceof Set && ((Set)o).isEmpty();
		}

		@Deprecated
		@Override
		public boolean rem(final long k) {
			return super.rem(k);
		}

		private Object readResolve() {
			return EMPTY_SET;
		}
	}

	/**
	 * An empty set (immutable). It is serializable and cloneable.
	 */

	public static final EmptySet EMPTY_SET = new EmptySet();
	// Used by Sets.of() instead of SETS.EMPTY_SET to make a dimorphic call site.
	// See https://github.com/vigna/fastutil/issues/183

	static final LongSet UNMODIFIABLE_EMPTY_SET = LongSets.unmodifiable(new LongArraySet(LongArrays.EMPTY_ARRAY));

	/**
	 * Returns an empty set (immutable). It is serializable and cloneable.
	 *
	 * <p>
	 * This method provides a typesafe access to {@link #EMPTY_SET}.
	 * 
	 * @return an empty set (immutable).
	 */
	@SuppressWarnings("unchecked")
	public static LongSet emptySet() {
		return EMPTY_SET;
	}

	/**
	 * An immutable class representing a type-specific singleton set.
	 *
	 * <p>
	 * This class may be useful to implement your own in case you subclass a type-specific set.
	 */
	public static class Singleton extends AbstractLongSet implements java.io.Serializable, Cloneable {
		private static final long serialVersionUID = -7046029254386353129L;
		protected final long element;

		protected Singleton(final long element) {
			this.element = element;
		}

		@Override
		public boolean contains(final long k) {
			return ((k) == (element));
		}

		@Override
		public boolean remove(final long k) {
			throw new UnsupportedOperationException();
		}

		@Override
		public LongListIterator iterator() {
			return LongIterators.singleton(element);
		}

		@Override
		public LongSpliterator spliterator() {
			return LongSpliterators.singleton(element);
		}

		@Override
		public int size() {
			return 1;
		}

		@Override
		public long[] toLongArray() {
			return new long[] { element };
		}

		@Deprecated
		@Override
		public void forEach(final Consumer<? super Long> action) {
			action.accept(Long.valueOf(element));
		}

		@Override
		public boolean addAll(final Collection<? extends Long> c) {
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
		public boolean removeIf(final java.util.function.Predicate<? super Long> filter) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void forEach(final java.util.function.LongConsumer action) {
			action.accept(element);
		}

		@Override
		public boolean addAll(final LongCollection c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean removeAll(final LongCollection c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean retainAll(final LongCollection c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean removeIf(final java.util.function.LongPredicate filter) {
			throw new UnsupportedOperationException();
		}

		@Deprecated
		@Override
		public Object[] toArray() {
			return new Object[] { Long.valueOf(element) };
		}

		@Override
		public Object clone() {
			return this;
		}
	}

	/**
	 * Returns a type-specific immutable set containing only the specified element. The returned set is
	 * serializable and cloneable.
	 *
	 * @param element the only element of the returned set.
	 * @return a type-specific immutable set containing just {@code element}.
	 */
	public static LongSet singleton(final long element) {
		return new Singleton(element);
	}

	/**
	 * Returns a type-specific immutable set containing only the specified element. The returned set is
	 * serializable and cloneable.
	 *
	 * @param element the only element of the returned set.
	 * @return a type-specific immutable set containing just {@code element}.
	 */
	public static LongSet singleton(final Long element) {
		return new Singleton((element).longValue());
	}

	/** A synchronized wrapper class for sets. */
	public static class SynchronizedSet extends LongCollections.SynchronizedCollection implements LongSet, java.io.Serializable {
		private static final long serialVersionUID = -7046029254386353129L;

		protected SynchronizedSet(final LongSet s, final Object sync) {
			super(s, sync);
		}

		protected SynchronizedSet(final LongSet s) {
			super(s);
		}

		@Override
		public boolean remove(final long k) {
			synchronized (sync) {
				return collection.rem(k);
			}
		}

		@Deprecated
		@Override
		public boolean rem(final long k) {
			return super.rem(k);
		}
	}

	/**
	 * Returns a synchronized type-specific set backed by the given type-specific set.
	 *
	 * @param s the set to be wrapped in a synchronized set.
	 * @return a synchronized view of the specified set.
	 * @see java.util.Collections#synchronizedSet(Set)
	 */
	public static LongSet synchronize(final LongSet s) {
		return new SynchronizedSet(s);
	}

	/**
	 * Returns a synchronized type-specific set backed by the given type-specific set, using an assigned
	 * object to synchronize.
	 *
	 * @param s the set to be wrapped in a synchronized set.
	 * @param sync an object that will be used to synchronize the access to the set.
	 * @return a synchronized view of the specified set.
	 * @see java.util.Collections#synchronizedSet(Set)
	 */
	public static LongSet synchronize(final LongSet s, final Object sync) {
		return new SynchronizedSet(s, sync);
	}

	/** An unmodifiable wrapper class for sets. */
	public static class UnmodifiableSet extends LongCollections.UnmodifiableCollection implements LongSet, java.io.Serializable {
		private static final long serialVersionUID = -7046029254386353129L;

		protected UnmodifiableSet(final LongSet s) {
			super(s);
		}

		@Override
		public boolean remove(final long k) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean equals(final Object o) {
			if (o == this) return true;
			return collection.equals(o);
		}

		@Override
		public int hashCode() {
			return collection.hashCode();
		}

		@Deprecated
		@Override
		public boolean rem(final long k) {
			return super.rem(k);
		}
	}

	/**
	 * Returns an unmodifiable type-specific set backed by the given type-specific set.
	 *
	 * @param s the set to be wrapped in an unmodifiable set.
	 * @return an unmodifiable view of the specified set.
	 * @see java.util.Collections#unmodifiableSet(Set)
	 */
	public static LongSet unmodifiable(final LongSet s) {
		return new UnmodifiableSet(s);
	}

	/**
	 * Returns an unmodifiable type-specific set containing elements in the given range.
	 *
	 * @param from the starting element (lower bound) of the set (inclusive).
	 * @param to the ending element (upper bound) of the set (exclusive).
	 * @return an unmodifiable set containing the elements in the given range.
	 */
	public static LongSet fromTo(final long from, final long to) {
		return new AbstractLongSet() {
			@Override
			public boolean contains(final long x) {
				return x >= from && x < to;
			}

			@Override
			public LongIterator iterator() {
				return LongIterators.fromTo(from, to);
			}

			@Override
			public int size() {
				final long size = to - from;
				return size >= 0 && size <= Integer.MAX_VALUE ? (int)size : Integer.MAX_VALUE;
			}
		};
	}

	/**
	 * Returns an unmodifiable type-specific set containing elements greater than or equal to a given
	 * element.
	 *
	 * @param from the starting element (lower bound) of the set (inclusive).
	 * @return an unmodifiable set containing the elements greater than or equal to {@code from}.
	 */
	public static LongSet from(final long from) {
		return new AbstractLongSet() {
			@Override
			public boolean contains(final long x) {
				return x >= from;
			}

			@Override
			public LongIterator iterator() {
				return LongIterators.concat(new LongIterator[] { LongIterators.fromTo(from, Long.MAX_VALUE),
						LongSets.singleton(Long.MAX_VALUE).iterator() });
			}

			@Override
			public int size() {
				final long size = Long.MAX_VALUE - from + 1;
				return size >= 0 && size <= Integer.MAX_VALUE ? (int)size : Integer.MAX_VALUE;
			}
		};
	}

	/**
	 * Returns an unmodifiable type-specific set containing elements smaller than a given element.
	 *
	 * @param to the ending element (upper bound) of the set (exclusive).
	 * @return an unmodifiable set containing the elements smaller than {@code to}.
	 */
	public static LongSet to(final long to) {
		return new AbstractLongSet() {
			@Override
			public boolean contains(final long x) {
				return x < to;
			}

			@Override
			public LongIterator iterator() {
				return LongIterators.fromTo(Long.MIN_VALUE, to);
			}

			@Override
			public int size() {
				final long size = to - Long.MIN_VALUE;
				return size >= 0 && size <= Integer.MAX_VALUE ? (int)size : Integer.MAX_VALUE;
			}
		};
	}
}
