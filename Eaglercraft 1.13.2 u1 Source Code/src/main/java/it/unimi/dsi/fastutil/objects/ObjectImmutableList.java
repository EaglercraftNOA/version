/*
	* Copyright (C) 2020-2024 Sebastiano Vigna
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
import java.util.RandomAccess;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import java.util.stream.Collector;

/**
 * A type-specific array-based immutable list; provides some additional methods that use
 * polymorphism to avoid (un)boxing.
 *
 * <p>
 * Instances of this class are immutable and (contrarily to mutable array-based list
 * implementations) the backing array is not exposed. Instances can be built using a variety of
 * methods, but note that constructors using an array will not make a defensive copy.
 *
 * <p>
 * This class implements the bulk method {@code getElements()} using high-performance system calls
 * (e.g., {@link System#arraycopy(Object,int,Object,int,int) System.arraycopy()}) instead of
 * expensive loops.
 */
public class ObjectImmutableList<K> extends ObjectLists.ImmutableListBase<K> implements ObjectList<K>, RandomAccess, Cloneable, java.io.Serializable {
	private static final long serialVersionUID = 0L;
	@SuppressWarnings({ "unchecked", "rawtypes" })
	static final ObjectImmutableList EMPTY = new ObjectImmutableList(ObjectArrays.EMPTY_ARRAY);

	@SuppressWarnings("unchecked")
	private static final <K> K[] emptyArray() {
		return (K[])ObjectArrays.EMPTY_ARRAY;
	}

	/** The backing array; all elements are part of this list. */
	private final K a[];

	/**
	 * Creates a new immutable list using a given array.
	 *
	 * <p>
	 * Note that this constructor does not perform a defensive copy.
	 *
	 * @param a the array that will be used to back this immutable list.
	 */
	public ObjectImmutableList(final K a[]) {
		this.a = a;
	}

	/**
	 * Creates a new immutable list and fills it with a given collection.
	 *
	 * @param c a collection that will be used to fill the immutable list.
	 */
	public ObjectImmutableList(final Collection<? extends K> c) {
		this(c.isEmpty() ? emptyArray() : ObjectIterators.unwrap(c.iterator()));
	}

	/**
	 * Creates a new immutable list and fills it with a given type-specific collection.
	 *
	 * @param c a type-specific collection that will be used to fill the immutable list.
	 */
	public ObjectImmutableList(final ObjectCollection<? extends K> c) {
		this(c.isEmpty() ? emptyArray() : ObjectIterators.unwrap(c.iterator()));
	}

	/**
	 * Creates a new immutable list and fills it with a given type-specific list.
	 *
	 * @param l a type-specific list that will be used to fill the immutable list.
	 */
	@SuppressWarnings("unchecked")
	public ObjectImmutableList(final ObjectList<? extends K> l) {
		this(l.isEmpty() ? emptyArray() : (K[])new Object[l.size()]);
		l.getElements(0, a, 0, l.size());
	}

	/**
	 * Creates a new immutable list and fills it with the elements of a given array.
	 *
	 * @param a an array whose elements will be used to fill the immutable list.
	 * @param offset the first element to use.
	 * @param length the number of elements to use.
	 */
	@SuppressWarnings("unchecked")
	public ObjectImmutableList(final K a[], final int offset, final int length) {
		this(length == 0 ? emptyArray() : (K[])new Object[length]);
		System.arraycopy(a, offset, this.a, 0, length);
	}

	/**
	 * Creates a new immutable list and fills it with the elements returned by a type-specific
	 * iterator..
	 *
	 * @param i a type-specific iterator whose returned elements will fill the immutable list.
	 */
	public ObjectImmutableList(final ObjectIterator<? extends K> i) {
		this(i.hasNext() ? ObjectIterators.unwrap(i) : emptyArray());
	}

	/**
	 * Returns an empty immutable list.
	 * 
	 * @return an immutable list (possibly shared) that is empty.
	 */
	@SuppressWarnings("unchecked")
	public static <K> ObjectImmutableList<K> of() {
		return EMPTY;
	}

	/**
	 * Creates an immutable list using a list of elements.
	 *
	 * <p>
	 * Note that this method does not perform a defensive copy.
	 *
	 * @param init a list of elements that will be used to initialize the list.
	 * @return a new immutable list containing the given elements.
	 */
	@SafeVarargs
	public static <K> ObjectImmutableList<K> of(final K... init) {
		return init.length == 0 ? of() : new ObjectImmutableList<K>(init);
	}

	private static <K> ObjectImmutableList<K> convertTrustedToImmutableList(ObjectArrayList<K> arrayList) {
		if (arrayList.isEmpty()) {
			return of();
		}
		K backingArray[] = arrayList.elements();
		if (arrayList.size() != backingArray.length) {
			backingArray = java.util.Arrays.copyOf(backingArray, arrayList.size());
		}
		return new ObjectImmutableList<>(backingArray);
	}

	private static final Collector<Object, ?, ObjectImmutableList<Object>> TO_LIST_COLLECTOR = Collector.of(ObjectArrayList::new, ObjectArrayList::add, ObjectArrayList::combine, ObjectImmutableList::convertTrustedToImmutableList);

	/**
	 * Returns a {@link Collector} that collects a {@code Stream}'s elements into a new ImmutableList.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <K> Collector<K, ?, ObjectImmutableList<K>> toList() {
		return (Collector)TO_LIST_COLLECTOR;
	}

	/**
	 * Returns a {@link Collector} that collects a {@code Stream}'s elements into a new ImmutableList,
	 * potentially pre-allocated to handle the given size.
	 */
	public static <K> Collector<K, ?, ObjectImmutableList<K>> toListWithExpectedSize(int expectedSize) {
		if (expectedSize <= ObjectArrayList.DEFAULT_INITIAL_CAPACITY) {
			// Already below default capacity. Just use all default construction instead of fiddling with
			// atomics in SizeDecreasingSupplier
			return toList();
		}
		return Collector.<K, ObjectArrayList<K>, ObjectImmutableList<K>> of(new ObjectCollections.SizeDecreasingSupplier<K, ObjectArrayList<K>>(expectedSize, (int size) -> size <= ObjectArrayList.DEFAULT_INITIAL_CAPACITY ? new ObjectArrayList<K>() : new ObjectArrayList<K>(size)), ObjectArrayList::add, ObjectArrayList::combine, ObjectImmutableList::convertTrustedToImmutableList);
	}

	@Override
	public K get(final int index) {
		if (index >= a.length) throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + a.length + ")");
		return a[index];
	}

	@Override
	public int indexOf(final Object k) {
		for (int i = 0, size = a.length; i < size; i++) if (java.util.Objects.equals(k, a[i])) return i;
		return -1;
	}

	@Override
	public int lastIndexOf(final Object k) {
		for (int i = a.length; i-- != 0;) if (java.util.Objects.equals(k, a[i])) return i;
		return -1;
	}

	@Override
	public int size() {
		return a.length;
	}

	@Override
	public boolean isEmpty() {
		return a.length == 0;
	}

	/**
	 * Copies element of this type-specific list into the given array using optimized system calls.
	 *
	 * @param from the start index (inclusive).
	 * @param a the destination array.
	 * @param offset the offset into the destination array where to store the first element copied.
	 * @param length the number of elements to be copied.
	 */
	@Override
	public void getElements(final int from, final Object[] a, final int offset, final int length) {
		ObjectArrays.ensureOffsetLength(a, offset, length);
		System.arraycopy(this.a, from, a, offset, length);
	}

	@Override
	public void forEach(final Consumer<? super K> action) {
		for (int i = 0; i < a.length; ++i) {
			action.accept(a[i]);
		}
	}

	@Override
	public Object[] toArray() {
		// A subtle part of the spec says the returned array must be Object[] exactly.
		if (a.length == 0) return it.unimi.dsi.fastutil.objects.ObjectArrays.EMPTY_ARRAY;
		if (a.getClass() == Object[].class) return a.clone();
		return java.util.Arrays.copyOf(a, a.length, Object[].class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] toArray(T[] a) {
		if (a == null) {
			a = (T[])new Object[size()];
		} else if (a.length < size()) {
			a = java.util.Arrays.copyOf(a, size());
		}
		System.arraycopy(this.a, 0, a, 0, size());
		if (a.length > size()) {
			a[size()] = null;
		}
		return a;
	}

	@Override
	public ObjectListIterator<K> listIterator(final int index) {
		ensureIndex(index);
		return new ObjectListIterator<K>() {
			int pos = index;

			@Override
			public boolean hasNext() {
				return pos < a.length;
			}

			@Override
			public boolean hasPrevious() {
				return pos > 0;
			}

			@Override
			public K next() {
				if (!hasNext()) throw new NoSuchElementException();
				return a[pos++];
			}

			@Override
			public K previous() {
				if (!hasPrevious()) throw new NoSuchElementException();
				return a[--pos];
			}

			@Override
			public int nextIndex() {
				return pos;
			}

			@Override
			public int previousIndex() {
				return pos - 1;
			}

			@Override
			public void forEachRemaining(final Consumer<? super K> action) {
				while (pos < a.length) {
					action.accept(a[pos++]);
				}
			}

			@Override
			public void add(K k) {
				throw new UnsupportedOperationException();
			}

			@Override
			public void set(K k) {
				throw new UnsupportedOperationException();
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}

			@Override
			public int back(int n) {
				if (n < 0) throw new IllegalArgumentException("Argument must be nonnegative: " + n);
				final int remaining = pos;
				if (n < remaining) {
					pos -= n;
				} else {
					n = remaining;
					pos = 0;
				}
				return n;
			}

			@Override
			public int skip(int n) {
				if (n < 0) throw new IllegalArgumentException("Argument must be nonnegative: " + n);
				final int remaining = a.length - pos;
				if (n < remaining) {
					pos += n;
				} else {
					n = remaining;
					pos = a.length;
				}
				return n;
			}
		};
	}

	private final class Spliterator implements ObjectSpliterator<K> {
		int pos, max;

		public Spliterator() {
			this(0, a.length);
		}

		private Spliterator(int pos, int max) {
			assert pos <= max : "pos " + pos + " must be <= max " + max;
			this.pos = pos;
			this.max = max;
		}

		@Override
		public int characteristics() {
			return ObjectSpliterators.LIST_SPLITERATOR_CHARACTERISTICS | java.util.Spliterator.IMMUTABLE;
		}

		@Override
		public long estimateSize() {
			return max - pos;
		}

		@Override
		public boolean tryAdvance(final Consumer<? super K> action) {
			if (pos >= max) return false;
			action.accept(a[pos++]);
			return true;
		}

		@Override
		public void forEachRemaining(final Consumer<? super K> action) {
			for (; pos < max; ++pos) {
				action.accept(a[pos]);
			}
		}

		@Override
		public long skip(long n) {
			if (n < 0) throw new IllegalArgumentException("Argument must be nonnegative: " + n);
			if (pos >= max) return 0;
			final int remaining = max - pos;
			if (n < remaining) {
				pos = it.unimi.dsi.fastutil.SafeMath.safeLongToInt(pos + n);
				return n;
			}
			n = remaining;
			pos = max;
			return n;
		}

		@Override
		public ObjectSpliterator<K> trySplit() {
			int retLen = (max - pos) >> 1;
			if (retLen <= 1) return null;
			int myNewPos = pos + retLen;
			int retMax = myNewPos;
			int oldPos = pos;
			this.pos = myNewPos;
			return new Spliterator(oldPos, retMax);
		}
	}

	@Override
	public ObjectSpliterator<K> spliterator() {
		return new Spliterator();
	}

	private final static class ImmutableSubList<K> extends ObjectLists.ImmutableListBase<K> implements java.util.RandomAccess, java.io.Serializable {
		private static final long serialVersionUID = 7054639518438982401L;
		final ObjectImmutableList<K> innerList;
		final int from;
		final int to;
		/**
		 * An alias to {@code innerList}'s array to save some typing. Note that 0 in this array is actually
		 * the first element of the {@code innerList}, not this sublist. {@code a[from]} is the first
		 * element of this sublist.
		 */
		final transient K a[];

		/** No validation; callers must validate arguments. */
		ImmutableSubList(ObjectImmutableList<K> innerList, int from, int to) {
			this.innerList = innerList;
			this.from = from;
			this.to = to;
			this.a = innerList.a;
		}

		@Override
		public K get(final int index) {
			ensureRestrictedIndex(index);
			return a[index + from];
		}

		@Override
		public int indexOf(final Object k) {
			for (int i = from; i < to; i++) if (java.util.Objects.equals(k, a[i])) return i - from;
			return -1;
		}

		@Override
		public int lastIndexOf(final Object k) {
			for (int i = to; i-- != from;) if (java.util.Objects.equals(k, a[i])) return i - from;
			return -1;
		}

		@Override
		public int size() {
			return to - from;
		}

		@Override
		public boolean isEmpty() {
			return to <= from;
		}

		@Override
		public void getElements(final int fromSublistIndex, final Object[] a, final int offset, final int length) {
			ObjectArrays.ensureOffsetLength(a, offset, length);
			ensureRestrictedIndex(fromSublistIndex);
			if (from + length > to) throw new IndexOutOfBoundsException("Final index " + (from + length) + " (startingIndex: " + from + " + length: " + length + ") is greater then list length " + size());
			System.arraycopy(this.a, fromSublistIndex + from, a, offset, length);
		}

		@Override
		public void forEach(final Consumer<? super K> action) {
			for (int i = from; i < to; ++i) {
				action.accept(a[i]);
			}
		}

		@Override
		public Object[] toArray() {
			// A subtle part of the spec says the returned array must be Object[] exactly.
			return java.util.Arrays.copyOfRange(a, from, to, Object[].class);
		}

		@SuppressWarnings("unchecked")
		@Override
		public <K> K[] toArray(K[] a) {
			final int size = size();
			if (a == null) {
				a = (K[])new Object[size];
			} else if (a.length < size) {
				a = java.util.Arrays.copyOf(a, size);
			}
			System.arraycopy(this.a, from, a, 0, size);
			if (a.length > size) {
				a[size] = null;
			}
			return a;
		}

		@Override
		public ObjectListIterator<K> listIterator(final int index) {
			ensureRestrictedIndex(index + from);
			return new ObjectListIterator<K>() {
				int pos = index + from;

				@Override
				public boolean hasNext() {
					return pos < to;
				}

				@Override
				public boolean hasPrevious() {
					return pos > from;
				}

				@Override
				public K next() {
					if (!hasNext()) throw new NoSuchElementException();
					return a[pos++];
				}

				@Override
				public K previous() {
					if (!hasPrevious()) throw new NoSuchElementException();
					return a[--pos];
				}

				@Override
				public int nextIndex() {
					return pos - from;
				}

				@Override
				public int previousIndex() {
					return pos - from - 1;
				}

				@Override
				public void forEachRemaining(final Consumer<? super K> action) {
					while (pos < to) {
						action.accept(a[pos++]);
					}
				}

				@Override
				public void add(K k) {
					throw new UnsupportedOperationException();
				}

				@Override
				public void set(K k) {
					throw new UnsupportedOperationException();
				}

				@Override
				public void remove() {
					throw new UnsupportedOperationException();
				}

				@Override
				public int back(int n) {
					if (n < 0) throw new IllegalArgumentException("Argument must be nonnegative: " + n);
					final int remaining = pos - from;
					if (n < remaining) {
						pos -= n;
					} else {
						n = remaining;
						pos = from;
					}
					return n;
				}

				@Override
				public int skip(int n) {
					if (n < 0) throw new IllegalArgumentException("Argument must be nonnegative: " + n);
					final int remaining = to - pos;
					if (n < remaining) {
						pos += n;
					} else {
						n = remaining;
						pos = to;
					}
					return n;
				}
			};
		}

		private final class SubListSpliterator extends ObjectSpliterators.EarlyBindingSizeIndexBasedSpliterator<K> {
			// We are using pos == 0 to be 0 relative to real array 0
			SubListSpliterator() {
				super(from, to);
			}

			/** No validation; callers must validate arguments. */
			private SubListSpliterator(int pos, int maxPos) {
				super(pos, maxPos);
			}

			// Remember, the indexes we are getting is the real array's index, not our sublist relative index.
			@Override
			protected final K get(int i) {
				return a[i];
			}

			@Override
			protected final SubListSpliterator makeForSplit(int pos, int maxPos) {
				return new SubListSpliterator(pos, maxPos);
			}

			@Override
			public boolean tryAdvance(final Consumer<? super K> action) {
				if (pos >= maxPos) return false;
				action.accept(a[pos++]);
				return true;
			}

			@Override
			public void forEachRemaining(final Consumer<? super K> action) {
				final int max = maxPos;
				while (pos < max) {
					action.accept(a[pos++]);
				}
			}

			@Override
			public int characteristics() {
				return ObjectSpliterators.LIST_SPLITERATOR_CHARACTERISTICS | java.util.Spliterator.IMMUTABLE;
			}
		}

		@Override
		public ObjectSpliterator<K> spliterator() {
			return new SubListSpliterator();
		}

		boolean contentsEquals(K[] otherA, int otherAFrom, int otherATo) {
			if (a == otherA && from == otherAFrom && to == otherATo) {
				return true;
			}
			if (otherATo - otherAFrom != size()) {
				return false;
			}
			int pos = from, otherPos = otherAFrom;
			// We have already assured that the two ranges are the same size, so we only need to check one
			// bound.
			// TODO When minimum version of Java becomes Java 9, use the Arrays.equals which takes bounds, which
			// is vectorized.
			// Make sure to split out the reference equality case when you do this.
			while (pos < to) if (!java.util.Objects.equals(a[pos++], otherA[otherPos++])) return false;
			return true;
		}

		@Override
		public boolean equals(Object o) {
			if (o == this) return true;
			if (o == null) return false;
			if (!(o instanceof java.util.List)) return false;
			if (o instanceof ObjectImmutableList) {
				@SuppressWarnings("unchecked")
				ObjectImmutableList<K> other = (ObjectImmutableList<K>)o;
				return contentsEquals(other.a, 0, other.size());
			}
			if (o instanceof ImmutableSubList) {
				@SuppressWarnings("unchecked")
				ImmutableSubList<K> other = (ImmutableSubList<K>)o;
				return contentsEquals(other.a, other.from, other.to);
			}
			return super.equals(o);
		}

		@SuppressWarnings("unchecked")
		int contentsCompareTo(K[] otherA, int otherAFrom, int otherATo) {
			// TODO When minimum version of Java becomes Java 9, use Arrays.compare, which vectorizes.
			K e1, e2;
			int r, i, j;
			for (i = from, j = otherAFrom; i < to && i < otherATo; i++, j++) {
				e1 = a[i];
				e2 = otherA[j];
				if ((r = (((Comparable<K>)(e1)).compareTo(e2))) != 0) return r;
			}
			return i < otherATo ? -1 : (i < to ? 1 : 0);
		}

		@SuppressWarnings("unchecked")
		@Override
		public int compareTo(final java.util.List<? extends K> l) {
			if (l instanceof ObjectImmutableList) {
				@SuppressWarnings("unchecked")
				ObjectImmutableList<K> other = (ObjectImmutableList<K>)l;
				return contentsCompareTo(other.a, 0, other.size());
			}
			if (l instanceof ImmutableSubList) {
				@SuppressWarnings("unchecked")
				ImmutableSubList<K> other = (ImmutableSubList<K>)l;
				return contentsCompareTo(other.a, other.from, other.to);
			}
			return super.compareTo(l);
		}

		private Object readResolve() throws java.io.ObjectStreamException {
			// We need to recheck the invariants of the subList and reestablish our "a" array alias.
			// Easiest way to do this is to just make a subList anew.
			// This will not cause a recopy of contents as subLists are a view, so this is all constant time.
			try {
				return innerList.subList(from, to);
			} catch (IllegalArgumentException | IndexOutOfBoundsException ex) {
				throw (java.io.InvalidObjectException)(new java.io.InvalidObjectException(ex.getMessage()).initCause(ex));
			}
		}

		@SuppressWarnings("unchecked")
		@Override
		public ObjectList<K> subList(int from, int to) {
			// We don't need to worry about keeping all nested sublists up to date with bounds as we are
			// immutable.
			// So don't even nest; just return a sublist with the immediate parent of the root list.
			ensureIndex(from);
			ensureIndex(to);
			if (from == to) return EMPTY;
			if (from > to) throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
			return new ImmutableSubList<>(innerList, from + this.from, to + this.from);
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * @apiNote The returned list will be immutable, but is currently not declared to return an instance
	 *          of {@code ImmutableList} due to complications of implementation details. This may change
	 *          in a future version (in other words, do not consider the return type of this method to
	 *          be stable if making a subclass of {@code ImmutableList}).
	 */
	@SuppressWarnings("unchecked")
	@Override
	public ObjectList<K> subList(int from, int to) {
		if (from == 0 && to == size()) return this;
		ensureIndex(from);
		ensureIndex(to);
		if (from == to) return EMPTY;
		if (from > to) throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
		return new ImmutableSubList<>(this, from, to);
	}

	@Override
	public ObjectImmutableList<K> clone() {
		return this;
	}

	/**
	 * Compares this type-specific immutable list to another one.
	 *
	 * @apiNote This method exists only for sake of efficiency. The implementation inherited from the
	 *          abstract implementation would already work.
	 *
	 * @param l a type-specific immutable list.
	 * @return true if the argument contains the same elements of this type-specific immutable list.
	 */
	public boolean equals(final ObjectImmutableList<K> l) {
		if (l == this) return true;
		if (a == l.a) return true;
		int s = size();
		if (s != l.size()) return false;
		final K[] a1 = a;
		final K[] a2 = l.a;
		return java.util.Arrays.equals(a1, a2);
	}

	@SuppressWarnings({ "unchecked", "unlikely-arg-type" })
	@Override
	public boolean equals(final Object o) {
		if (o == this) return true;
		if (o == null) return false;
		if (!(o instanceof java.util.List)) return false;
		if (o instanceof ObjectImmutableList) {
			return equals((ObjectImmutableList<K>)o);
		}
		if (o instanceof ImmutableSubList) {
			// Sublist has an optimized sub-array based comparison, reuse that.
			return ((ImmutableSubList<K>)o).equals(this);
		}
		return super.equals(o);
	}

	/**
	 * Compares this immutable list to another immutable list.
	 *
	 * @apiNote This method exists only for sake of efficiency. The implementation inherited from the
	 *          abstract implementation would already work.
	 *
	 * @param l an immutable list.
	 * @return a negative integer, zero, or a positive integer as this list is lexicographically less
	 *         than, equal to, or greater than the argument.
	 */
	@SuppressWarnings("unchecked")
	public int compareTo(final ObjectImmutableList<? extends K> l) {
		// TODO When minimum version of Java becomes Java 9, use Arrays.compare, which vectorizes.
		final int s1 = size(), s2 = l.size();
		final K a1[] = a, a2[] = l.a;
		K e1, e2;
		int r, i;
		for (i = 0; i < s1 && i < s2; i++) {
			e1 = a1[i];
			e2 = a2[i];
			if ((r = (((Comparable<K>)(e1)).compareTo(e2))) != 0) return r;
		}
		return i < s2 ? -1 : (i < s1 ? 1 : 0);
	}

	@SuppressWarnings("unchecked")
	@Override
	public int compareTo(final java.util.List<? extends K> l) {
		if (l instanceof ObjectImmutableList) {
			return compareTo((ObjectImmutableList<? extends K>)l);
		}
		if (l instanceof ImmutableSubList) {
			// Safe to strip the "extends" because we will only ever take elements, never modify them
			// (especially because it is immutable).
			ImmutableSubList<K> other = (ImmutableSubList<K>)l;
			// Must negate because we are inverting the order of the comparison.
			return -other.compareTo(this);
		}
		return super.compareTo(l);
	}
}
