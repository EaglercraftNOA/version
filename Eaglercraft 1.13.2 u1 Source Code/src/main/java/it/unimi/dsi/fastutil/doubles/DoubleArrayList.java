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
package it.unimi.dsi.fastutil.doubles;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.RandomAccess;
import java.util.NoSuchElementException;

/**
 * A type-specific array-based list; provides some additional methods that use polymorphism to avoid
 * (un)boxing.
 *
 * <p>
 * This class implements a lightweight, fast, open, optimized, reuse-oriented version of array-based
 * lists. Instances of this class represent a list with an array that is enlarged as needed when new
 * entries are created (by increasing its current length by 50%), but is <em>never</em> made smaller
 * (even on a {@link #clear()}). A family of {@linkplain #trim() trimming methods} lets you control
 * the size of the backing array; this is particularly useful if you reuse instances of this class.
 * Range checks are equivalent to those of {@code java.util}'s classes, but they are delayed as much
 * as possible. The backing array is exposed by the {@link #elements()} method.
 *
 * <p>
 * This class implements the bulk methods {@code removeElements()}, {@code addElements()} and
 * {@code getElements()} using high-performance system calls (e.g.,
 * {@link System#arraycopy(Object,int,Object,int,int) System.arraycopy()}) instead of expensive
 * loops.
 *
 * @see java.util.ArrayList
 */
public class DoubleArrayList extends AbstractDoubleList implements RandomAccess, Cloneable, java.io.Serializable {
	private static final long serialVersionUID = -7046029254386353130L;
	/** The initial default capacity of an array list. */
	public static final int DEFAULT_INITIAL_CAPACITY = 10;
	/** The backing array. */
	protected transient double a[];
	/** The current actual size of the list (never greater than the backing-array length). */
	protected int size;

	/**
	 * Ensures that the ITextComponent type of the given array is the proper type. This is irrelevant for
	 * primitive types, so it will just do a trivial copy. But for Reference types, you can have a
	 * {@code String[]} masquerading as an {@code Object[]}, which is a case we need to prepare for
	 * because we let the user give an array to use directly with {@link #wrap}.
	 */

	private static final double[] copyArraySafe(double[] a, int length) {
		if (length == 0) return DoubleArrays.EMPTY_ARRAY;
		return java.util.Arrays.copyOf(a, length);
	}

	private static final double[] copyArrayFromSafe(DoubleArrayList l) {
		return copyArraySafe(l.a, l.size);
	}

	/**
	 * Creates a new array list using a given array.
	 *
	 * <p>
	 * This constructor is only meant to be used by the wrapping methods.
	 *
	 * @param a the array that will be used to back this array list.
	 */
	protected DoubleArrayList(final double a[], @SuppressWarnings("unused") boolean wrapped) {
		this.a = a;
	}

	private void initArrayFromCapacity(final int capacity) {
		if (capacity < 0) throw new IllegalArgumentException("Initial capacity (" + capacity + ") is negative");
		if (capacity == 0) a = DoubleArrays.EMPTY_ARRAY;
		else a = new double[capacity];
	}

	/**
	 * Creates a new array list with given capacity.
	 *
	 * @param capacity the initial capacity of the array list (may be 0).
	 */
	public DoubleArrayList(final int capacity) {
		initArrayFromCapacity(capacity);
	}

	/** Creates a new array list with {@link #DEFAULT_INITIAL_CAPACITY} capacity. */

	public DoubleArrayList() {
		a = DoubleArrays.DEFAULT_EMPTY_ARRAY; // We delay allocation
	}

	/**
	 * Creates a new array list and fills it with a given collection.
	 *
	 * @param c a collection that will be used to fill the array list.
	 */
	public DoubleArrayList(final Collection<? extends Double> c) {
		if (c instanceof DoubleArrayList) {
			a = copyArrayFromSafe((DoubleArrayList)c);
			size = a.length;
		} else {
			initArrayFromCapacity(c.size());
			if (c instanceof DoubleList) {
				((DoubleList)c).getElements(0, a, 0, size = c.size());
			} else {
				size = DoubleIterators.unwrap(DoubleIterators.asDoubleIterator(c.iterator()), a);
			}
		}
	}

	/**
	 * Creates a new array list and fills it with a given type-specific collection.
	 *
	 * @param c a type-specific collection that will be used to fill the array list.
	 */
	public DoubleArrayList(final DoubleCollection c) {
		if (c instanceof DoubleArrayList) {
			a = copyArrayFromSafe((DoubleArrayList)c);
			size = a.length;
		} else {
			initArrayFromCapacity(c.size());
			if (c instanceof DoubleList) {
				((DoubleList)c).getElements(0, a, 0, size = c.size());
			} else {
				size = DoubleIterators.unwrap(c.iterator(), a);
			}
		}
	}

	/**
	 * Creates a new array list and fills it with a given type-specific list.
	 *
	 * @param l a type-specific list that will be used to fill the array list.
	 */
	public DoubleArrayList(final DoubleList l) {
		if (l instanceof DoubleArrayList) {
			a = copyArrayFromSafe((DoubleArrayList)l);
			size = a.length;
		} else {
			initArrayFromCapacity(l.size());
			l.getElements(0, a, 0, size = l.size());
		}
	}

	/**
	 * Creates a new array list and fills it with the elements of a given array.
	 *
	 * @param a an array whose elements will be used to fill the array list.
	 */
	public DoubleArrayList(final double a[]) {
		this(a, 0, a.length);
	}

	/**
	 * Creates a new array list and fills it with the elements of a given array.
	 *
	 * @param a an array whose elements will be used to fill the array list.
	 * @param offset the first element to use.
	 * @param length the number of elements to use.
	 */
	public DoubleArrayList(final double a[], final int offset, final int length) {
		this(length);
		System.arraycopy(a, offset, this.a, 0, length);
		size = length;
	}

	/**
	 * Creates a new array list and fills it with the elements returned by an iterator..
	 *
	 * @param i an iterator whose returned elements will fill the array list.
	 */
	public DoubleArrayList(final Iterator<? extends Double> i) {
		this();
		while (i.hasNext()) this.add((i.next()).doubleValue());
	}

	/**
	 * Creates a new array list and fills it with the elements returned by a type-specific iterator..
	 *
	 * @param i a type-specific iterator whose returned elements will fill the array list.
	 */
	public DoubleArrayList(final DoubleIterator i) {
		this();
		while (i.hasNext()) this.add(i.nextDouble());
	}

	/**
	 * Returns the backing array of this list.
	 *
	 * @return the backing array.
	 */
	public double[] elements() {
		return a;
	}

	/**
	 * Wraps a given array into an array list of given size.
	 *
	 * <p>
	 * Note it is guaranteed that the type of the array returned by {@link #elements()} will be the same
	 * (see the comments in the class documentation).
	 *
	 * @param a an array to wrap.
	 * @param length the length of the resulting array list.
	 * @return a new array list of the given size, wrapping the given array.
	 */
	public static DoubleArrayList wrap(final double a[], final int length) {
		if (length > a.length) throw new IllegalArgumentException("The specified length (" + length + ") is greater than the array size (" + a.length + ")");
		final DoubleArrayList l = new DoubleArrayList(a, true);
		l.size = length;
		return l;
	}

	/**
	 * Wraps a given array into an array list.
	 *
	 * <p>
	 * Note it is guaranteed that the type of the array returned by {@link #elements()} will be the same
	 * (see the comments in the class documentation).
	 *
	 * @param a an array to wrap.
	 * @return a new array list wrapping the given array.
	 */
	public static DoubleArrayList wrap(final double a[]) {
		return wrap(a, a.length);
	}

	/**
	 * Creates a new empty array list.
	 *
	 * @return a new empty array list.
	 */
	public static DoubleArrayList of() {
		return new DoubleArrayList();
	}

	/**
	 * Creates an array list using an array of elements.
	 *
	 * @param init a the array the will become the new backing array of the array list.
	 * @return a new array list backed by the given array.
	 * @see #wrap
	 */

	public static DoubleArrayList of(final double... init) {
		return wrap(init);
	}

	/**
	 * Collects the result of a primitive {@code Stream} into a new ArrayList.
	 *
	 * <p>
	 * This method performs a terminal operation on the given {@code Stream}
	 *
	 * @apiNote Taking a primitive stream instead of returning something like a
	 *          {@link java.util.stream.Collector Collector} is necessary because there is no primitive
	 *          {@code Collector} equivalent in the Java API.
	 */
	public static DoubleArrayList toList(java.util.stream.DoubleStream stream) {
		return stream.collect(DoubleArrayList::new, DoubleArrayList::add, DoubleArrayList::addAll);
	}

	/**
	 * Collects the result of a primitive {@code Stream} into a new ArrayList, potentially pre-allocated
	 * to handle the given size.
	 *
	 * <p>
	 * This method performs a terminal operation on the given {@code Stream}
	 *
	 * @apiNote Taking a primitive stream instead returning something like a
	 *          {@link java.util.stream.Collector Collector} is necessary because there is no primitive
	 *          {@code Collector} equivalent in the Java API.
	 */
	public static DoubleArrayList toListWithExpectedSize(java.util.stream.DoubleStream stream, int expectedSize) {
		if (expectedSize <= DEFAULT_INITIAL_CAPACITY) {
			// Already below default capacity. Just use all default construction instead of fiddling with
			// atomics in SizeDecreasingSupplier
			return toList(stream);
		}
		return stream.collect(new DoubleCollections.SizeDecreasingSupplier<DoubleArrayList>(expectedSize, (int size) -> size <= DEFAULT_INITIAL_CAPACITY ? new DoubleArrayList() : new DoubleArrayList(size)), DoubleArrayList::add, DoubleArrayList::addAll);
	}

	/**
	 * Ensures that this array list can contain the given number of entries without resizing.
	 *
	 * @param capacity the new minimum capacity for this array list.
	 */

	public void ensureCapacity(final int capacity) {
		if (capacity <= a.length || (a == DoubleArrays.DEFAULT_EMPTY_ARRAY && capacity <= DEFAULT_INITIAL_CAPACITY)) return;
		a = DoubleArrays.ensureCapacity(a, capacity, size);
		assert size <= a.length;
	}

	/**
	 * Grows this array list, ensuring that it can contain the given number of entries without resizing,
	 * and in case increasing the current capacity at least by a factor of 50%.
	 *
	 * @param capacity the new minimum capacity for this array list.
	 */

	private void grow(int capacity) {
		if (capacity <= a.length) return;
		if (a != DoubleArrays.DEFAULT_EMPTY_ARRAY) capacity = (int)Math.max(Math.min((long)a.length + (a.length >> 1), it.unimi.dsi.fastutil.Arrays.MAX_ARRAY_SIZE), capacity);
		else if (capacity < DEFAULT_INITIAL_CAPACITY) capacity = DEFAULT_INITIAL_CAPACITY;
		a = DoubleArrays.forceCapacity(a, capacity, size);
		assert size <= a.length;
	}

	@Override
	public void add(final int index, final double k) {
		ensureIndex(index);
		grow(size + 1);
		if (index != size) System.arraycopy(a, index, a, index + 1, size - index);
		a[index] = k;
		size++;
		assert size <= a.length;
	}

	@Override
	public boolean add(final double k) {
		grow(size + 1);
		a[size++] = k;
		assert size <= a.length;
		return true;
	}

	@Override
	public double getDouble(final int index) {
		if (index >= size) throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + size + ")");
		return a[index];
	}

	@Override
	public int indexOf(final double k) {
		for (int i = 0; i < size; i++) if ((Double.doubleToLongBits(k) == Double.doubleToLongBits(a[i]))) return i;
		return -1;
	}

	@Override
	public int lastIndexOf(final double k) {
		for (int i = size; i-- != 0;) if ((Double.doubleToLongBits(k) == Double.doubleToLongBits(a[i]))) return i;
		return -1;
	}

	@Override
	public double removeDouble(final int index) {
		if (index >= size) throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + size + ")");
		final double old = a[index];
		size--;
		if (index != size) System.arraycopy(a, index + 1, a, index, size - index);
		assert size <= a.length;
		return old;
	}

	@Override
	public boolean rem(final double k) {
		int index = indexOf(k);
		if (index == -1) return false;
		removeDouble(index);
		assert size <= a.length;
		return true;
	}

	@Override
	public double set(final int index, final double k) {
		if (index >= size) throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + size + ")");
		double old = a[index];
		a[index] = k;
		return old;
	}

	@Override
	public void clear() {
		size = 0;
		assert size <= a.length;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public void size(final int size) {
		if (size > a.length) a = DoubleArrays.forceCapacity(a, size, this.size);
		if (size > this.size) Arrays.fill(a, this.size, size, (0));
		this.size = size;
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	/**
	 * Trims this array list so that the capacity is equal to the size.
	 *
	 * @see java.util.ArrayList#trimToSize()
	 */
	public void trim() {
		trim(0);
	}

	/**
	 * Trims the backing array if it is too large.
	 *
	 * If the current array length is smaller than or equal to {@code n}, this method does nothing.
	 * Otherwise, it trims the array length to the maximum between {@code n} and {@link #size()}.
	 *
	 * <p>
	 * This method is useful when reusing lists. {@linkplain #clear() Clearing a list} leaves the array
	 * length untouched. If you are reusing a list many times, you can call this method with a typical
	 * size to avoid keeping around a very large array just because of a few large transient lists.
	 *
	 * @param n the threshold for the trimming.
	 */

	public void trim(final int n) {
		// TODO: use Arrays.trim() and preserve type only if necessary
		if (n >= a.length || size == a.length) return;
		final double t[] = new double[Math.max(n, size)];
		System.arraycopy(a, 0, t, 0, size);
		a = t;
		assert size <= a.length;
	}

	private class SubList extends AbstractDoubleList.DoubleRandomAccessSubList {
		private static final long serialVersionUID = -3185226345314976296L;

		protected SubList(int from, int to) {
			super(DoubleArrayList.this, from, to);
		}

		// Most of the inherited methods should be fine, but we can override a few of them for performance.
		// Needed because we can't access the parent class' instance variables directly in a different
		// instance of SubList.
		private double[] getParentArray() {
			return a;
		}

		@Override
		public double getDouble(int i) {
			ensureRestrictedIndex(i);
			return a[i + from];
		}

		private final class SubListIterator extends DoubleIterators.AbstractIndexBasedListIterator {
			// We are using pos == 0 to be 0 relative to SubList.from (meaning you need to do a[from + i] when
			// accessing array).
			SubListIterator(int index) {
				super(0, index);
			}

			@Override
			protected final double get(int i) {
				return a[from + i];
			}

			@Override
			protected final void add(int i, double k) {
				SubList.this.add(i, k);
			}

			@Override
			protected final void set(int i, double k) {
				SubList.this.set(i, k);
			}

			@Override
			protected final void remove(int i) {
				SubList.this.removeDouble(i);
			}

			@Override
			protected final int getMaxPos() {
				return to - from;
			}

			@Override
			public double nextDouble() {
				if (!hasNext()) throw new NoSuchElementException();
				return a[from + (lastReturned = pos++)];
			}

			@Override
			public double previousDouble() {
				if (!hasPrevious()) throw new NoSuchElementException();
				return a[from + (lastReturned = --pos)];
			}

			@Override
			public void forEachRemaining(final java.util.function.DoubleConsumer action) {
				final int max = to - from;
				while (pos < max) {
					action.accept(a[from + (lastReturned = pos++)]);
				}
			}
		}

		@Override
		public DoubleListIterator listIterator(int index) {
			return new SubListIterator(index);
		}

		private final class SubListSpliterator extends DoubleSpliterators.LateBindingSizeIndexBasedSpliterator {
			// We are using pos == 0 to be 0 relative to real array 0
			SubListSpliterator() {
				super(from);
			}

			private SubListSpliterator(int pos, int maxPos) {
				super(pos, maxPos);
			}

			@Override
			protected final int getMaxPosFromBackingStore() {
				return to;
			}

			@Override
			protected final double get(int i) {
				return a[i];
			}

			@Override
			protected final SubListSpliterator makeForSplit(int pos, int maxPos) {
				return new SubListSpliterator(pos, maxPos);
			}

			@Override
			public boolean tryAdvance(final java.util.function.DoubleConsumer action) {
				if (pos >= getMaxPos()) return false;
				action.accept(a[pos++]);
				return true;
			}

			@Override
			public void forEachRemaining(final java.util.function.DoubleConsumer action) {
				final int max = getMaxPos();
				while (pos < max) {
					action.accept(a[pos++]);
				}
			}
		}

		@Override
		public DoubleSpliterator spliterator() {
			return new SubListSpliterator();
		}

		boolean contentsEquals(double[] otherA, int otherAFrom, int otherATo) {
			if (a == otherA && from == otherAFrom && to == otherATo) return true;
			if (otherATo - otherAFrom != size()) {
				return false;
			}
			int pos = from, otherPos = otherAFrom;
			// We have already assured that the two ranges are the same size, so we only need to check one
			// bound.
			// TODO When minimum version of Java becomes Java 9, use the Arrays.equals which takes bounds, which
			// is vectorized.
			// Make sure to split out the reference equality case when you do this.
			while (pos < to) if (a[pos++] != otherA[otherPos++]) return false;
			return true;
		}

		@Override
		public boolean equals(Object o) {
			if (o == this) return true;
			if (o == null) return false;
			if (!(o instanceof java.util.List)) return false;
			if (o instanceof DoubleArrayList) {

				DoubleArrayList other = (DoubleArrayList)o;
				return contentsEquals(other.a, 0, other.size());
			}
			if (o instanceof DoubleArrayList.SubList) {

				DoubleArrayList.SubList other = (DoubleArrayList.SubList)o;
				return contentsEquals(other.getParentArray(), other.from, other.to);
			}
			return super.equals(o);
		}

		int contentsCompareTo(double[] otherA, int otherAFrom, int otherATo) {
			if (a == otherA && from == otherAFrom && to == otherATo) return 0;
			// TODO When minimum version of Java becomes Java 9, use Arrays.compare, which vectorizes.
			double e1, e2;
			int r, i, j;
			for (i = from, j = otherAFrom; i < to && i < otherATo; i++, j++) {
				e1 = a[i];
				e2 = otherA[j];
				if ((r = (Double.compare((e1), (e2)))) != 0) return r;
			}
			return i < otherATo ? -1 : (i < to ? 1 : 0);
		}

		@Override
		public int compareTo(final java.util.List<? extends Double> l) {
			if (l instanceof DoubleArrayList) {

				DoubleArrayList other = (DoubleArrayList)l;
				return contentsCompareTo(other.a, 0, other.size());
			}
			if (l instanceof DoubleArrayList.SubList) {

				DoubleArrayList.SubList other = (DoubleArrayList.SubList)l;
				return contentsCompareTo(other.getParentArray(), other.from, other.to);
			}
			return super.compareTo(l);
		}
		// We don't override subList as we want AbstractList's "sub-sublist" nesting handling,
		// which would be tricky to do here.
		// TODO Do override it so array access isn't sent through N indirections.
		// This will likely mean making this class static.
	}

	@Override
	public DoubleList subList(int from, int to) {
		if (from == 0 && to == size()) return this;
		ensureIndex(from);
		ensureIndex(to);
		if (from > to) throw new IndexOutOfBoundsException("Start index (" + from + ") is greater than end index (" + to + ")");
		return new SubList(from, to);
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
	public void getElements(final int from, final double[] a, final int offset, final int length) {
		DoubleArrays.ensureOffsetLength(a, offset, length);
		System.arraycopy(this.a, from, a, offset, length);
	}

	/**
	 * Removes elements of this type-specific list using optimized system calls.
	 *
	 * @param from the start index (inclusive).
	 * @param to the end index (exclusive).
	 */
	@Override
	public void removeElements(final int from, final int to) {
		it.unimi.dsi.fastutil.Arrays.ensureFromTo(size, from, to);
		System.arraycopy(a, to, a, from, size - to);
		size -= (to - from);
	}

	/**
	 * Adds elements to this type-specific list using optimized system calls.
	 *
	 * @param index the index at which to add elements.
	 * @param a the array containing the elements.
	 * @param offset the offset of the first element to add.
	 * @param length the number of elements to add.
	 */
	@Override
	public void addElements(final int index, final double a[], final int offset, final int length) {
		ensureIndex(index);
		DoubleArrays.ensureOffsetLength(a, offset, length);
		grow(size + length);
		System.arraycopy(this.a, index, this.a, index + length, size - index);
		System.arraycopy(a, offset, this.a, index, length);
		size += length;
	}

	/**
	 * Sets elements to this type-specific list using optimized system calls.
	 *
	 * @param index the index at which to start setting elements.
	 * @param a the array containing the elements.
	 * @param offset the offset of the first element to add.
	 * @param length the number of elements to add.
	 */
	@Override
	public void setElements(final int index, final double a[], final int offset, final int length) {
		ensureIndex(index);
		DoubleArrays.ensureOffsetLength(a, offset, length);
		if (index + length > size) throw new IndexOutOfBoundsException("End index (" + (index + length) + ") is greater than list size (" + size + ")");
		System.arraycopy(a, offset, this.a, index, length);
	}

	@Override
	public void forEach(final java.util.function.DoubleConsumer action) {
		for (int i = 0; i < size; ++i) {
			action.accept(a[i]);
		}
	}

	@Override
	public boolean addAll(int index, final DoubleCollection c) {
		if (c instanceof DoubleList) {
			return addAll(index, (DoubleList)c);
		}
		ensureIndex(index);
		int n = c.size();
		if (n == 0) return false;
		grow(size + n);
		System.arraycopy(a, index, a, index + n, size - index);
		final DoubleIterator i = c.iterator();
		size += n;
		while (n-- != 0) a[index++] = i.nextDouble();
		assert size <= a.length;
		return true;
	}

	@Override
	public boolean addAll(final int index, final DoubleList l) {
		ensureIndex(index);
		final int n = l.size();
		if (n == 0) return false;
		grow(size + n);
		System.arraycopy(a, index, a, index + n, size - index);
		l.getElements(0, a, index, n);
		size += n;
		assert size <= a.length;
		return true;
	}

	@Override
	public boolean removeAll(final DoubleCollection c) {
		final double[] a = this.a;
		int j = 0;
		for (int i = 0; i < size; i++) if (!c.contains(a[i])) a[j++] = a[i];
		final boolean modified = size != j;
		size = j;
		return modified;
	}

	@Override
	public boolean removeIf(final java.util.function.DoublePredicate filter) {
		final double[] a = this.a;
		int j = 0;
		for (int i = 0; i < size; i++) if (!filter.test(a[i])) a[j++] = a[i];
		final boolean modified = size != j;
		size = j;
		return modified;
	}

	@Override
	public double[] toArray(double[] a) {
		if (a == null || a.length < size) a = java.util.Arrays.copyOf(a, size);
		System.arraycopy(this.a, 0, a, 0, size);
		return a;
	}

	@Override
	public DoubleListIterator listIterator(final int index) {
		ensureIndex(index);
		return new DoubleListIterator() {
			int pos = index, last = -1;

			@Override
			public boolean hasNext() {
				return pos < size;
			}

			@Override
			public boolean hasPrevious() {
				return pos > 0;
			}

			@Override
			public double nextDouble() {
				if (!hasNext()) throw new NoSuchElementException();
				return a[last = pos++];
			}

			@Override
			public double previousDouble() {
				if (!hasPrevious()) throw new NoSuchElementException();
				return a[last = --pos];
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
			public void add(double k) {
				DoubleArrayList.this.add(pos++, k);
				last = -1;
			}

			@Override
			public void set(double k) {
				if (last == -1) throw new IllegalStateException();
				DoubleArrayList.this.set(last, k);
			}

			@Override
			public void remove() {
				if (last == -1) throw new IllegalStateException();
				DoubleArrayList.this.removeDouble(last);
				/* If the last operation was a next(), we are removing an element *before* us, and we must decrease pos correspondingly. */
				if (last < pos) pos--;
				last = -1;
			}

			@Override
			public void forEachRemaining(final java.util.function.DoubleConsumer action) {
				while (pos < size) {
					action.accept(a[last = pos++]);
				}
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
				last = pos;
				return n;
			}

			@Override
			public int skip(int n) {
				if (n < 0) throw new IllegalArgumentException("Argument must be nonnegative: " + n);
				final int remaining = size - pos;
				if (n < remaining) {
					pos += n;
				} else {
					n = remaining;
					pos = size;
				}
				last = pos - 1;
				return n;
			}
		};
	}

	// If you update this, you will probably want to update ArraySet as well
	private final class Spliterator implements DoubleSpliterator {
		// Until we split, we will track the size of the list.
		// Once we split, then we stop updating on structural modifications.
		// Aka, size is late-binding.
		boolean hasSplit = false;
		int pos, max;

		public Spliterator() {
			this(0, DoubleArrayList.this.size, false);
		}

		private Spliterator(int pos, int max, boolean hasSplit) {
			assert pos <= max : "pos " + pos + " must be <= max " + max;
			this.pos = pos;
			this.max = max;
			this.hasSplit = hasSplit;
		}

		private int getWorkingMax() {
			return hasSplit ? max : DoubleArrayList.this.size;
		}

		@Override
		public int characteristics() {
			return DoubleSpliterators.LIST_SPLITERATOR_CHARACTERISTICS;
		}

		@Override
		public long estimateSize() {
			return getWorkingMax() - pos;
		}

		@Override
		public boolean tryAdvance(final java.util.function.DoubleConsumer action) {
			if (pos >= getWorkingMax()) return false;
			action.accept(a[pos++]);
			return true;
		}

		@Override
		public void forEachRemaining(final java.util.function.DoubleConsumer action) {
			for (final int max = getWorkingMax(); pos < max; ++pos) {
				action.accept(a[pos]);
			}
		}

		@Override
		public long skip(long n) {
			if (n < 0) throw new IllegalArgumentException("Argument must be nonnegative: " + n);
			final int max = getWorkingMax();
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
		public DoubleSpliterator trySplit() {
			final int max = getWorkingMax();
			int retLen = (max - pos) >> 1;
			if (retLen <= 1) return null;
			// Update instance max with the last seen list size (if needed) before continuing
			this.max = max;
			int myNewPos = pos + retLen;
			int retMax = myNewPos;
			int oldPos = pos;
			this.pos = myNewPos;
			this.hasSplit = true;
			return new Spliterator(oldPos, retMax, true);
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The returned spliterator is late-binding; it will track structural changes after the current
	 * index, up until the first {@link java.util.Spliterator#trySplit() trySplit()}, at which point the
	 * maximum index will be fixed. <br>
	 * Structural changes before the current index or after the first
	 * {@link java.util.Spliterator#trySplit() trySplit()} will result in unspecified behavior.
	 */
	@Override
	public DoubleSpliterator spliterator() {
		// If it wasn't for the possibility of the list being expanded or shrunk,
		// we could return SPLITERATORS.wrap(a, 0, size).
		return new Spliterator();
	}

	@Override
	public void sort(final DoubleComparator comp) {
		if (comp == null) {
			DoubleArrays.stableSort(a, 0, size);
		} else {
			DoubleArrays.stableSort(a, 0, size, comp);
		}
	}

	@Override
	public void unstableSort(final DoubleComparator comp) {
		if (comp == null) {
			DoubleArrays.unstableSort(a, 0, size);
		} else {
			DoubleArrays.unstableSort(a, 0, size, comp);
		}
	}

	@Override

	public DoubleArrayList clone() {
		DoubleArrayList cloned = null;
		// Test for fastpath we can do if exactly an ArrayList
		if (getClass() == DoubleArrayList.class) {
			// Preserve backwards compatibility and make new list have Object[] even if it was wrapped from some
			// subclass.
			cloned = new DoubleArrayList(copyArraySafe(a, size), false);
			cloned.size = size;
		} else {
			try {
				cloned = (DoubleArrayList)super.clone();
			} catch (CloneNotSupportedException err) {
				// Can't happen
				throw new InternalError(err);
			}
			// Preserve backwards compatibility and make new list have Object[] even if it was wrapped from some
			// subclass.
			cloned.a = copyArraySafe(a, size);
		}
		return cloned;
	}

	/**
	 * Compares this type-specific array list to another one.
	 *
	 * @apiNote This method exists only for sake of efficiency. The implementation inherited from the
	 *          abstract implementation would already work.
	 *
	 * @param l a type-specific array list.
	 * @return true if the argument contains the same elements of this type-specific array list.
	 */
	public boolean equals(final DoubleArrayList l) {
		// TODO When minimum version of Java becomes Java 9, use the Arrays.equals which takes bounds, which
		// is vectorized.
		if (l == this) return true;
		int s = size();
		if (s != l.size()) return false;
		final double[] a1 = a;
		final double[] a2 = l.a;
		if (a1 == a2 && s == l.size()) return true;
		while (s-- != 0) if (a1[s] != a2[s]) return false;
		return true;
	}

	@SuppressWarnings("unlikely-arg-type")
	@Override
	public boolean equals(final Object o) {
		if (o == this) return true;
		if (o == null) return false;
		if (!(o instanceof java.util.List)) return false;
		if (o instanceof DoubleArrayList) {
			// Safe cast because we are only going to take elements from other list, never give them
			return equals((DoubleArrayList)o);
		}
		if (o instanceof DoubleArrayList.SubList) {
			// Safe cast because we are only going to take elements from other list, never give them
			// Sublist has an optimized sub-array based comparison, reuse that.
			return ((DoubleArrayList.SubList)o).equals(this);
		}
		return super.equals(o);
	}

	/**
	 * Compares this array list to another array list.
	 *
	 * @apiNote This method exists only for sake of efficiency. The implementation inherited from the
	 *          abstract implementation would already work.
	 *
	 * @param l an array list.
	 * @return a negative integer, zero, or a positive integer as this list is lexicographically less
	 *         than, equal to, or greater than the argument.
	 */

	public int compareTo(final DoubleArrayList l) {
		final int s1 = size(), s2 = l.size();
		final double a1[] = a, a2[] = l.a;
		if (a1 == a2 && s1 == s2) return 0;
		// TODO When minimum version of Java becomes Java 9, use Arrays.compare, which vectorizes.
		double e1, e2;
		int r, i;
		for (i = 0; i < s1 && i < s2; i++) {
			e1 = a1[i];
			e2 = a2[i];
			if ((r = (Double.compare((e1), (e2)))) != 0) return r;
		}
		return i < s2 ? -1 : (i < s1 ? 1 : 0);
	}

	@Override
	public int compareTo(final java.util.List<? extends Double> l) {
		if (l instanceof DoubleArrayList) {
			return compareTo((DoubleArrayList)l);
		}
		if (l instanceof DoubleArrayList.SubList) {
			// Must negate because we are inverting the order of the comparison.
			return -((DoubleArrayList.SubList)l).compareTo(this);
		}
		return super.compareTo(l);
	}

	private void writeObject(java.io.ObjectOutputStream s) throws java.io.IOException {
		s.defaultWriteObject();
		for (int i = 0; i < size; i++) s.writeDouble(a[i]);
	}

	private void readObject(java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();
		a = new double[size];
		for (int i = 0; i < size; i++) a[i] = s.readDouble();
	}
}
