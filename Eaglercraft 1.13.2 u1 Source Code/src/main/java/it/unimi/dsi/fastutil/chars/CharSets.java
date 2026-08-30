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
package it.unimi.dsi.fastutil.chars;

import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntIterators;
import it.unimi.dsi.fastutil.ints.IntSpliterator;
import it.unimi.dsi.fastutil.ints.IntSpliterators;

/**
 * A class providing static methods and objects that do useful things with type-specific sets.
 *
 * @see java.util.Collections
 */
public final class CharSets {
	private CharSets() {
	}

	/** The maximum size to choose ArraySet over OpenHashSet for utilites that automatically choose. */
	static final int ARRAY_SET_CUTOFF = 4;

	/**
	 * An immutable class representing the empty set and implementing a type-specific set interface.
	 *
	 * <p>
	 * This class may be useful to implement your own in case you subclass a type-specific set.
	 */
	public static class EmptySet extends CharCollections.EmptyCollection implements CharSet, java.io.Serializable, Cloneable {
		private static final long serialVersionUID = -7046029254386353129L;

		protected EmptySet() {
		}

		@Override
		public boolean remove(char ok) {
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
		public boolean rem(final char k) {
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

	static final CharSet UNMODIFIABLE_EMPTY_SET = CharSets.unmodifiable(new CharArraySet(CharArrays.EMPTY_ARRAY));

	/**
	 * Returns an empty set (immutable). It is serializable and cloneable.
	 *
	 * <p>
	 * This method provides a typesafe access to {@link #EMPTY_SET}.
	 * 
	 * @return an empty set (immutable).
	 */
	@SuppressWarnings("unchecked")
	public static CharSet emptySet() {
		return EMPTY_SET;
	}

	/**
	 * An immutable class representing a type-specific singleton set.
	 *
	 * <p>
	 * This class may be useful to implement your own in case you subclass a type-specific set.
	 */
	public static class Singleton extends AbstractCharSet implements java.io.Serializable, Cloneable {
		private static final long serialVersionUID = -7046029254386353129L;
		protected final char element;

		protected Singleton(final char element) {
			this.element = element;
		}

		@Override
		public boolean contains(final char k) {
			return ((k) == (element));
		}

		@Override
		public boolean remove(final char k) {
			throw new UnsupportedOperationException();
		}

		@Override
		public CharListIterator iterator() {
			return CharIterators.singleton(element);
		}

		@Override
		public CharSpliterator spliterator() {
			return CharSpliterators.singleton(element);
		}

		@Override
		public int size() {
			return 1;
		}

		@Override
		public char[] toCharArray() {
			return new char[] { element };
		}

		@Deprecated
		@Override
		public void forEach(final Consumer<? super Character> action) {
			action.accept(Character.valueOf(element));
		}

		@Override
		public boolean addAll(final Collection<? extends Character> c) {
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
		public boolean removeIf(final java.util.function.Predicate<? super Character> filter) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void forEach(final CharConsumer action) {
			action.accept(element);
		}

		@Override
		public boolean addAll(final CharCollection c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean removeAll(final CharCollection c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean retainAll(final CharCollection c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean removeIf(final CharPredicate filter) {
			throw new UnsupportedOperationException();
		}

		@Override
		public IntIterator intIterator() {
			return IntIterators.singleton(element);
		}

		@Override
		public IntSpliterator intSpliterator() {
			return IntSpliterators.singleton(element);
		}

		@Deprecated
		@Override
		public Object[] toArray() {
			return new Object[] { Character.valueOf(element) };
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
	public static CharSet singleton(final char element) {
		return new Singleton(element);
	}

	/**
	 * Returns a type-specific immutable set containing only the specified element. The returned set is
	 * serializable and cloneable.
	 *
	 * @param element the only element of the returned set.
	 * @return a type-specific immutable set containing just {@code element}.
	 */
	public static CharSet singleton(final Character element) {
		return new Singleton((element).charValue());
	}

	/** A synchronized wrapper class for sets. */
	public static class SynchronizedSet extends CharCollections.SynchronizedCollection implements CharSet, java.io.Serializable {
		private static final long serialVersionUID = -7046029254386353129L;

		protected SynchronizedSet(final CharSet s, final Object sync) {
			super(s, sync);
		}

		protected SynchronizedSet(final CharSet s) {
			super(s);
		}

		@Override
		public boolean remove(final char k) {
			synchronized (sync) {
				return collection.rem(k);
			}
		}

		@Deprecated
		@Override
		public boolean rem(final char k) {
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
	public static CharSet synchronize(final CharSet s) {
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
	public static CharSet synchronize(final CharSet s, final Object sync) {
		return new SynchronizedSet(s, sync);
	}

	/** An unmodifiable wrapper class for sets. */
	public static class UnmodifiableSet extends CharCollections.UnmodifiableCollection implements CharSet, java.io.Serializable {
		private static final long serialVersionUID = -7046029254386353129L;

		protected UnmodifiableSet(final CharSet s) {
			super(s);
		}

		@Override
		public boolean remove(final char k) {
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
		public boolean rem(final char k) {
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
	public static CharSet unmodifiable(final CharSet s) {
		return new UnmodifiableSet(s);
	}

	/**
	 * Returns an unmodifiable type-specific set containing elements in the given range.
	 *
	 * @param from the starting element (lower bound) of the set (inclusive).
	 * @param to the ending element (upper bound) of the set (exclusive).
	 * @return an unmodifiable set containing the elements in the given range.
	 */
	public static CharSet fromTo(final char from, final char to) {
		return new AbstractCharSet() {
			@Override
			public boolean contains(final char x) {
				return x >= from && x < to;
			}

			@Override
			public CharIterator iterator() {
				return CharIterators.fromTo(from, to);
			}

			@Override
			public int size() {
				final long size = (long)to - (long)from;
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
	public static CharSet from(final char from) {
		return new AbstractCharSet() {
			@Override
			public boolean contains(final char x) {
				return x >= from;
			}

			@Override
			public CharIterator iterator() {
				return CharIterators.concat(new CharIterator[] { CharIterators.fromTo(from, Character.MAX_VALUE),
						CharSets.singleton(Character.MAX_VALUE).iterator() });
			}

			@Override
			public int size() {
				final long size = Character.MAX_VALUE - (long)from + 1;
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
	public static CharSet to(final char to) {
		return new AbstractCharSet() {
			@Override
			public boolean contains(final char x) {
				return x < to;
			}

			@Override
			public CharIterator iterator() {
				return CharIterators.fromTo(Character.MIN_VALUE, to);
			}

			@Override
			public int size() {
				final long size = (long)to - Character.MIN_VALUE;
				return size >= 0 && size <= Integer.MAX_VALUE ? (int)size : Integer.MAX_VALUE;
			}
		};
	}
}
