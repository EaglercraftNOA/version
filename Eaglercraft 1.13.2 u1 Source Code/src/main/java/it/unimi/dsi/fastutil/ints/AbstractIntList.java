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
package it.unimi.dsi.fastutil.ints;

import java.util.List;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Collection;
import java.util.NoSuchElementException;

/**
 * An abstract class providing basic methods for lists implementing a type-specific list interface.
 *
 * <p>
 * As an additional bonus, this class implements on top of the list operations a type-specific
 * stack.
 *
 * <p>
 * Most of the methods in this class are optimized with the assumption that the List will have
 * {@link java.util.RandomAccess have constant-time random access}. If this is not the case, you
 * should probably <em>at least</em> override {@link #listIterator(int)} and the {@code xAll()}
 * methods (such as {@link #addAll}) with a more appropriate iteration scheme. Note the
 * {@link #subList(int, int)} method is cognizant of random-access or not, so that need not be
 * reimplemented.
 */
public abstract class AbstractIntList extends AbstractIntCollection implements IntList, IntStack {
	protected AbstractIntList() {
	}

	/**
	 * Ensures that the given index is nonnegative and not greater than the list size.
	 *
	 * @param index an index.
	 * @throws IndexOutOfBoundsException if the given index is negative or greater than the list size.
	 */
	protected void ensureIndex(final int index) {
		// TODO When Java 9 becomes the minimum java, use Objects#checkIndex(index, size() + 1) (as can be
		// an intrinsic)
		if (index < 0) throw new IndexOutOfBoundsException("Index (" + index + ") is negative");
		if (index > size()) throw new IndexOutOfBoundsException("Index (" + index + ") is greater than list size (" + (size()) + ")");
	}

	/**
	 * Ensures that the given index is nonnegative and smaller than the list size.
	 *
	 * @param index an index.
	 * @throws IndexOutOfBoundsException if the given index is negative or not smaller than the list
	 *             size.
	 */
	protected void ensureRestrictedIndex(final int index) {
		// TODO When Java 9 becomes the minimum java, use Objects#checkIndex (as can be an intrinsic)
		if (index < 0) throw new IndexOutOfBoundsException("Index (" + index + ") is negative");
		if (index >= size()) throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + (size()) + ")");
	}

	/**
	 * {@inheritDoc}
	 *
	 * @implSpec This implementation always throws an {@link UnsupportedOperationException}.
	 */
	@Override
	public void add(final int index, final int k) {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @implSpec This implementation delegates to the type-specific version of
	 *           {@link List#add(int, Object)}.
	 */
	@Override
	public boolean add(final int k) {
		add(size(), k);
		return true;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @implSpec This implementation always throws an {@link UnsupportedOperationException}.
	 */
	@Override
	public int removeInt(final int i) {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @implSpec This implementation always throws an {@link UnsupportedOperationException}.
	 */
	@Override
	public int set(final int index, final int k) {
		throw new UnsupportedOperationException();
	}

	/** Adds all of the elements in the specified collection to this list (optional operation). */
	@Override
	public boolean addAll(int index, final Collection<? extends Integer> c) {
		if (c instanceof IntCollection) {
			return addAll(index, (IntCollection)c);
		}
		ensureIndex(index);
		final Iterator<? extends Integer> i = c.iterator();
		final boolean retVal = i.hasNext();
		while (i.hasNext()) add(index++, (i.next()).intValue());
		return retVal;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @implSpec This implementation delegates to the type-specific version of
	 *           {@link List#addAll(int, Collection)}.
	 */
	@Override
	public boolean addAll(final Collection<? extends Integer> c) {
		return addAll(size(), c);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @implSpec This implementation delegates to {@link #listIterator()}.
	 */
	@Override
	public IntListIterator iterator() {
		return listIterator();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @implSpec This implementation delegates to {@link #listIterator(int) listIterator(0)}.
	 */
	@Override
	public IntListIterator listIterator() {
		return listIterator(0);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @implSpec This implementation is based on the random-access methods.
	 */
	@Override
	public IntListIterator listIterator(final int index) {
		ensureIndex(index);
		return new IntIterators.AbstractIndexBasedListIterator(0, index) {
			@Override
			protected final int get(int i) {
				return AbstractIntList.this.getInt(i);
			}

			@Override
			protected final void add(int i, int k) {
				AbstractIntList.this.add(i, k);
			}

			@Override
			protected final void set(int i, int k) {
				AbstractIntList.this.set(i, k);
			}

			@Override
			protected final void remove(int i) {
				AbstractIntList.this.removeInt(i);
			}

			@Override
			protected final int getMaxPos() {
				return AbstractIntList.this.size();
			}
		};
	}

	static final class IndexBasedSpliterator extends IntSpliterators.LateBindingSizeIndexBasedSpliterator {
		final IntList l;

		IndexBasedSpliterator(IntList l, int pos) {
			super(pos);
			this.l = l;
		}

		IndexBasedSpliterator(IntList l, int pos, int maxPos) {
			super(pos, maxPos);
			this.l = l;
		}

		@Override
		protected final int getMaxPosFromBackingStore() {
			return l.size();
		}

		@Override
		protected final int get(int i) {
			return l.getInt(i);
		}

		@Override
		protected final IndexBasedSpliterator makeForSplit(int pos, int maxPos) {
			return new IndexBasedSpliterator(l, pos, maxPos);
		}
	}

	/**
	 * Returns true if this list contains the specified element.
	 * 
	 * @implSpec This implementation delegates to {@code indexOf()}.
	 * @see List#contains(Object)
	 */
	@Override
	public boolean contains(final int k) {
		return indexOf(k) >= 0;
	}

	@Override
	public int indexOf(final int k) {
		final IntListIterator i = listIterator();
		int e;
		while (i.hasNext()) {
			e = i.nextInt();
			if (((k) == (e))) return i.previousIndex();
		}
		return -1;
	}

	@Override
	public int lastIndexOf(final int k) {
		IntListIterator i = listIterator(size());
		int e;
		while (i.hasPrevious()) {
			e = i.previousInt();
			if (((k) == (e))) return i.nextIndex();
		}
		return -1;
	}

	@Override
	public void size(final int size) {
		int i = size();
		if (size > i) while (i++ < size) add((0));
		else while (i-- != size) removeInt(i);
	}

	@Override
	public IntList subList(final int from, final int to) {
		ensureIndex(from);
		ensureIndex(to);
		if (from > to) throw new IndexOutOfBoundsException("Start index (" + from + ") is greater than end index (" + to + ")");
		return this instanceof java.util.RandomAccess ? new IntRandomAccessSubList(this, from, to) : new IntSubList(this, from, to);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @implSpec If this list is {@link java.util.RandomAccess}, will iterate using a for loop and the
	 *           type-specific {@link List#get(int)} method. Otherwise it will fallback to using the
	 *           iterator based loop implementation from the superinterface.
	 */
	@Override
	public void forEach(final java.util.function.IntConsumer action) {
		if (this instanceof java.util.RandomAccess) {
			for (int i = 0, max = size(); i < max; ++i) {
				action.accept(getInt(i));
			}
		} else {
			IntList.super.forEach(action);
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * This is a trivial iterator-based based implementation. It is expected that implementations will
	 * override this method with a more optimized version.
	 */
	@Override
	public void removeElements(final int from, final int to) {
		ensureIndex(to);
		// Always use the iterator based implementation even for RandomAccess so we don't have to worry
		// about shifting indexes.
		IntListIterator i = listIterator(from);
		int n = to - from;
		if (n < 0) throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
		while (n-- != 0) {
			i.nextInt();
			i.remove();
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * This is a trivial iterator-based implementation. It is expected that implementations will
	 * override this method with a more optimized version.
	 */
	@Override
	public void addElements(int index, final int a[], int offset, int length) {
		ensureIndex(index);
		IntArrays.ensureOffsetLength(a, offset, length);
		if (this instanceof java.util.RandomAccess) {
			while (length-- != 0) add(index++, a[offset++]);
		} else {
			IntListIterator iter = listIterator(index);
			while (length-- != 0) iter.add(a[offset++]);
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * @implSpec This implementation delegates to the analogous method for array fragments.
	 */
	@Override
	public void addElements(final int index, final int a[]) {
		addElements(index, a, 0, a.length);
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * This is a trivial iterator-based implementation. It is expected that implementations will
	 * override this method with a more optimized version.
	 */
	@Override
	public void getElements(final int from, final int a[], int offset, int length) {
		ensureIndex(from);
		IntArrays.ensureOffsetLength(a, offset, length);
		if (from + length > size()) throw new IndexOutOfBoundsException("End index (" + (from + length) + ") is greater than list size (" + size() + ")");
		if (this instanceof java.util.RandomAccess) {
			int current = from;
			while (length-- != 0) a[offset++] = getInt(current++);
		} else {
			IntListIterator i = listIterator(from);
			while (length-- != 0) a[offset++] = i.nextInt();
		}
	}

	@Override
	public void setElements(int index, int a[], int offset, int length) {
		ensureIndex(index);
		IntArrays.ensureOffsetLength(a, offset, length);
		if (index + length > size()) throw new IndexOutOfBoundsException("End index (" + (index + length) + ") is greater than list size (" + size() + ")");
		if (this instanceof java.util.RandomAccess) {
			for (int i = 0; i < length; ++i) {
				set(i + index, a[i + offset]);
			}
		} else {
			IntListIterator iter = listIterator(index);
			int i = 0;
			while (i < length) {
				iter.nextInt();
				iter.set(a[offset + i++]);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @implSpec This implementation delegates to {@link #removeElements(int, int)}.
	 */
	@Override
	public void clear() {
		removeElements(0, size());
	}

	/**
	 * Returns the hash code for this list, which is identical to {@link java.util.List#hashCode()}.
	 *
	 * @return the hash code for this list.
	 */
	@Override
	public int hashCode() {
		IntIterator i = iterator();
		int h = 1, s = size();
		while (s-- != 0) {
			int k = i.nextInt();
			h = 31 * h + (k);
		}
		return h;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == this) return true;
		if (!(o instanceof List)) return false;
		final List<?> l = (List<?>)o;
		int s = size();
		if (s != l.size()) return false;
		if (l instanceof IntList) {
			final IntListIterator i1 = listIterator(), i2 = ((IntList)l).listIterator();
			while (s-- != 0) if (i1.nextInt() != i2.nextInt()) return false;
			return true;
		}
		final ListIterator<?> i1 = listIterator(), i2 = l.listIterator();
		while (s-- != 0) if (!java.util.Objects.equals(i1.next(), i2.next())) return false;
		return true;
	}

	/**
	 * Compares this list to another object. If the argument is a {@link java.util.List}, this method
	 * performs a lexicographical comparison; otherwise, it throws a {@code ClassCastException}.
	 *
	 * @param l a list.
	 * @return if the argument is a {@link java.util.List}, a negative integer, zero, or a positive
	 *         integer as this list is lexicographically less than, equal to, or greater than the
	 *         argument.
	 * @throws ClassCastException if the argument is not a list.
	 */

	@Override
	public int compareTo(final List<? extends Integer> l) {
		if (l == this) return 0;
		if (l instanceof IntList) {
			final IntListIterator i1 = listIterator(), i2 = ((IntList)l).listIterator();
			int r;
			int e1, e2;
			while (i1.hasNext() && i2.hasNext()) {
				e1 = i1.nextInt();
				e2 = i2.nextInt();
				if ((r = (Integer.compare((e1), (e2)))) != 0) return r;
			}
			return i2.hasNext() ? -1 : (i1.hasNext() ? 1 : 0);
		}
		ListIterator<? extends Integer> i1 = listIterator(), i2 = l.listIterator();
		int r;
		while (i1.hasNext() && i2.hasNext()) {
			if ((r = ((Comparable<? super Integer>)i1.next()).compareTo(i2.next())) != 0) return r;
		}
		return i2.hasNext() ? -1 : (i1.hasNext() ? 1 : 0);
	}

	@Override
	public void push(final int o) {
		add(o);
	}

	@Override
	public int popInt() {
		if (isEmpty()) throw new NoSuchElementException();
		return removeInt(size() - 1);
	}

	@Override
	public int topInt() {
		if (isEmpty()) throw new NoSuchElementException();
		return getInt(size() - 1);
	}

	@Override
	public int peekInt(final int i) {
		return getInt(size() - 1 - i);
	}

	/**
	 * Removes a single instance of the specified element from this collection, if it is present
	 * (optional operation).
	 * 
	 * @implSpec This implementation delegates to {@code indexOf()}.
	 * @see List#remove(Object)
	 */
	@Override
	public boolean rem(final int k) {
		int index = indexOf(k);
		if (index == -1) return false;
		removeInt(index);
		return true;
	}

	@Override
	public int[] toIntArray() {
		final int size = size();
		if (size == 0) return IntArrays.EMPTY_ARRAY;
		int[] ret = new int[size];
		getElements(0, ret, 0, size);
		return ret;
	}

	@Override
	public int[] toArray(int[] a) {
		final int size = size();
		if (a.length < size) {
			a = java.util.Arrays.copyOf(a, size);
		}
		getElements(0, a, 0, size);
		return a;
	}

	@Override
	public boolean addAll(int index, final IntCollection c) {
		ensureIndex(index);
		final IntIterator i = c.iterator();
		final boolean retVal = i.hasNext();
		while (i.hasNext()) add(index++, i.nextInt());
		return retVal;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @implSpec This implementation delegates to the type-specific version of
	 *           {@link List#addAll(int, Collection)}.
	 */
	@Override
	public boolean addAll(final IntCollection c) {
		return addAll(size(), c);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @implSpec This method just delegates to the interface default method, as the default method, but
	 *           it is final, so it cannot be overridden.
	 */
	@Override
	public final void replaceAll(final IntUnaryOperator operator) {
		replaceAll((java.util.function.IntUnaryOperator)operator);
	}

	@Override
	public String toString() {
		final StringBuilder s = new StringBuilder();
		final IntIterator i = iterator();
		int n = size();
		int k;
		boolean first = true;
		s.append("[");
		while (n-- != 0) {
			if (first) first = false;
			else s.append(", ");
			k = i.nextInt();
			s.append(String.valueOf(k));
		}
		s.append("]");
		return s.toString();
	}

	/** A class implementing a sublist view. */
	public static class IntSubList extends AbstractIntList implements java.io.Serializable {
		private static final long serialVersionUID = -7046029254386353129L;
		/** The list this sublist restricts. */
		protected final IntList l;
		/** Initial (inclusive) index of this sublist. */
		protected final int from;
		/** Final (exclusive) index of this sublist. */
		protected int to;

		public IntSubList(final IntList l, final int from, final int to) {
			this.l = l;
			this.from = from;
			this.to = to;
		}

		private boolean assertRange() {
			assert from <= l.size();
			assert to <= l.size();
			assert to >= from;
			return true;
		}

		@Override
		public boolean add(final int k) {
			l.add(to, k);
			to++;
			assert assertRange();
			return true;
		}

		@Override
		public void add(final int index, final int k) {
			ensureIndex(index);
			l.add(from + index, k);
			to++;
			assert assertRange();
		}

		@Override
		public boolean addAll(final int index, final Collection<? extends Integer> c) {
			ensureIndex(index);
			to += c.size();
			return l.addAll(from + index, c);
		}

		@Override
		public int getInt(final int index) {
			ensureRestrictedIndex(index);
			return l.getInt(from + index);
		}

		@Override
		public int removeInt(final int index) {
			ensureRestrictedIndex(index);
			to--;
			return l.removeInt(from + index);
		}

		@Override
		public int set(final int index, final int k) {
			ensureRestrictedIndex(index);
			return l.set(from + index, k);
		}

		@Override
		public int size() {
			return to - from;
		}

		@Override
		public void getElements(final int from, final int[] a, final int offset, final int length) {
			ensureIndex(from);
			if (from + length > size()) throw new IndexOutOfBoundsException("End index (" + from + length + ") is greater than list size (" + size() + ")");
			l.getElements(this.from + from, a, offset, length);
		}

		@Override
		public void removeElements(final int from, final int to) {
			ensureIndex(from);
			ensureIndex(to);
			l.removeElements(this.from + from, this.from + to);
			this.to -= (to - from);
			assert assertRange();
		}

		@Override
		public void addElements(int index, final int a[], int offset, int length) {
			ensureIndex(index);
			l.addElements(this.from + index, a, offset, length);
			this.to += length;
			assert assertRange();
		}

		@Override
		public void setElements(int index, final int a[], int offset, int length) {
			ensureIndex(index);
			l.setElements(this.from + index, a, offset, length);
			assert assertRange();
		}

		private final class RandomAccessIter extends IntIterators.AbstractIndexBasedListIterator {
			// We don't set the minPos to be "from" because we need to call our containing class'
			// add, set, and remove methods with 0 relative to the start of the sublist, not the
			// start of the original list.
			// Thus pos is relative to the start of the SubList, not the start of the original list.
			RandomAccessIter(int pos) {
				super(0, pos);
			}

			@Override
			protected final int get(int i) {
				return l.getInt(from + i);
			}

			// Remember, these are calling SUBLIST's methods, meaning 0 is the start of the sublist for these.
			@Override
			protected final void add(int i, int k) {
				IntSubList.this.add(i, k);
			}

			@Override
			protected final void set(int i, int k) {
				IntSubList.this.set(i, k);
			}

			@Override
			protected final void remove(int i) {
				IntSubList.this.removeInt(i);
			}

			@Override
			protected final int getMaxPos() {
				return to - from;
			}

			@Override
			public void add(int k) {
				super.add(k);
				assert assertRange();
			}

			@Override
			public void remove() {
				super.remove();
				assert assertRange();
			}
		}

		private class ParentWrappingIter implements IntListIterator {
			private IntListIterator parent;

			ParentWrappingIter(IntListIterator parent) {
				this.parent = parent;
			}

			@Override
			public int nextIndex() {
				return parent.nextIndex() - from;
			}

			@Override
			public int previousIndex() {
				return parent.previousIndex() - from;
			}

			@Override
			public boolean hasNext() {
				return parent.nextIndex() < to;
			}

			@Override
			public boolean hasPrevious() {
				return parent.previousIndex() >= from;
			}

			@Override
			public int nextInt() {
				if (!hasNext()) throw new NoSuchElementException();
				return parent.nextInt();
			}

			@Override
			public int previousInt() {
				if (!hasPrevious()) throw new NoSuchElementException();
				return parent.previousInt();
			}

			@Override
			public void add(int k) {
				parent.add(k);
			}

			@Override
			public void set(int k) {
				parent.set(k);
			}

			@Override
			public void remove() {
				parent.remove();
			}

			@Override
			public int back(int n) {
				if (n < 0) throw new IllegalArgumentException("Argument must be nonnegative: " + n);
				int currentPos = parent.previousIndex();
				int parentNewPos = currentPos - n;
				// Remember, the minimum acceptable previousIndex is not from but (from - 1), since (from - 1)
				// means this subList is at the beginning of our sub range.
				// Same reason why previousIndex()'s minimum for the full list is not 0 but -1.
				if (parentNewPos < (from - 1)) parentNewPos = (from - 1);
				int toSkip = parentNewPos - currentPos;
				return parent.back(toSkip);
			}

			@Override
			public int skip(int n) {
				if (n < 0) throw new IllegalArgumentException("Argument must be nonnegative: " + n);
				int currentPos = parent.nextIndex();
				int parentNewPos = currentPos + n;
				if (parentNewPos > to) parentNewPos = to;
				int toSkip = parentNewPos - currentPos;
				return parent.skip(toSkip);
			}
		}

		@Override
		public IntListIterator listIterator(final int index) {
			ensureIndex(index);
			// If this class wasn't public, then RandomAccessIter would live in SUBLISTRandomAccess,
			// and the switching would be done in sublist(int, int). However, this is a public class
			// that may have existing implementors, so to get the benefit of RandomAccessIter class for
			// for existing uses, it has to be done in this class.
			return l instanceof java.util.RandomAccess ? new RandomAccessIter(index) : new ParentWrappingIter(l.listIterator(index + from));
		}

		@Override
		public IntSpliterator spliterator() {
			return l instanceof java.util.RandomAccess ? new IndexBasedSpliterator(l, from, to) : super.spliterator();
		}

		@Override
		public IntList subList(final int from, final int to) {
			ensureIndex(from);
			ensureIndex(to);
			if (from > to) throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
			// Sadly we have to rewrap this, because if there is a sublist of a sublist, and the
			// subsublist adds, both sublists need to update their "to" value.
			return new IntSubList(this, from, to);
		}

		@Override
		public boolean rem(final int k) {
			int index = indexOf(k);
			if (index == -1) return false;
			to--;
			l.removeInt(from + index);
			assert assertRange();
			return true;
		}

		@Override
		public boolean addAll(final int index, final IntCollection c) {
			ensureIndex(index);
			return super.addAll(index, c);
		}

		@Override
		public boolean addAll(final int index, final IntList l) {
			ensureIndex(index);
			return super.addAll(index, l);
		}
	}

	public static class IntRandomAccessSubList extends IntSubList implements java.util.RandomAccess {
		private static final long serialVersionUID = -107070782945191929L;

		public IntRandomAccessSubList(final IntList l, final int from, final int to) {
			super(l, from, to);
		}

		@Override
		public IntList subList(final int from, final int to) {
			ensureIndex(from);
			ensureIndex(to);
			if (from > to) throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
			// Sadly we have to rewrap this, because if there is a sublist of a sublist, and the
			// subsublist adds, both sublists need to update their "to" value.
			return new IntRandomAccessSubList(this, from, to);
		}
	}
}
