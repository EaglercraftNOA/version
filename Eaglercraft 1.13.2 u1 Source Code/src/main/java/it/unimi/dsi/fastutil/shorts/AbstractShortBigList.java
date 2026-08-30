/*
	* Copyright (C) 2010-2024 Sebastiano Vigna
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
package it.unimi.dsi.fastutil.shorts;

import static it.unimi.dsi.fastutil.BigArrays.ensureOffsetLength;
import static it.unimi.dsi.fastutil.BigArrays.length;
import it.unimi.dsi.fastutil.BigArrays;
import java.util.Iterator;
import java.util.Collection;
import java.util.NoSuchElementException;
import it.unimi.dsi.fastutil.BigList;
import it.unimi.dsi.fastutil.BigListIterator;

/**
 * An abstract class providing basic methods for big lists implementing a type-specific big list
 * interface.
 *
 * <p>
 * Most of the methods in this class are optimized with the assumption that the List will have
 * {@link java.util.RandomAccess have constant-time random access}. If this is not the case, you
 * should probably <em>at least</em> override {@link #listIterator(long)} and the {@code xAll()}
 * methods (such as {@link #addAll}) with a more appropriate iteration scheme. Note the
 * {@link #subList(long, long)} method is cognizant of random-access or not, so that need not be
 * reimplemented.
 */
public abstract class AbstractShortBigList extends AbstractShortCollection implements ShortBigList, ShortStack {
	protected AbstractShortBigList() {
	}

	/**
	 * Ensures that the given index is nonnegative and not greater than this big-list size.
	 *
	 * @param index an index.
	 * @throws IndexOutOfBoundsException if the given index is negative or greater than this big-list
	 *             size.
	 */
	protected void ensureIndex(final long index) {
		if (index < 0) throw new IndexOutOfBoundsException("Index (" + index + ") is negative");
		if (index > size64()) throw new IndexOutOfBoundsException("Index (" + index + ") is greater than list size (" + (size64()) + ")");
	}

	/**
	 * Ensures that the given index is nonnegative and smaller than this big-list size.
	 *
	 * @param index an index.
	 * @throws IndexOutOfBoundsException if the given index is negative or not smaller than this
	 *             big-list size.
	 */
	protected void ensureRestrictedIndex(final long index) {
		if (index < 0) throw new IndexOutOfBoundsException("Index (" + index + ") is negative");
		if (index >= size64()) throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + (size64()) + ")");
	}

	/**
	 * {@inheritDoc}
	 *
	 * @implSpec This implementation always throws an {@link UnsupportedOperationException}.
	 */
	@Override
	public void add(final long index, final short k) {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @implSpec This implementation delegates to the type-specific version of
	 *           {@link BigList#add(long, Object)}.
	 */
	@Override
	public boolean add(final short k) {
		add(size64(), k);
		return true;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @implSpec This implementation always throws an {@link UnsupportedOperationException}.
	 */
	@Override
	public short removeShort(long i) {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @implSpec This implementation always throws an {@link UnsupportedOperationException}.
	 */
	@Override
	public short set(final long index, final short k) {
		throw new UnsupportedOperationException();
	}

	/** Adds all of the elements in the specified collection to this list (optional operation). */
	@Override
	public boolean addAll(long index, final Collection<? extends Short> c) {
		ensureIndex(index);
		final Iterator<? extends Short> i = c.iterator();
		final boolean retVal = i.hasNext();
		while (i.hasNext()) add(index++, i.next());
		return retVal;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @implSpec This implementation delegates to the type-specific version of
	 *           {@link BigList#addAll(long, Collection)}.
	 */
	@Override
	public boolean addAll(final Collection<? extends Short> c) {
		return addAll(size64(), c);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @implSpec This implementation delegates to {@link #listIterator()}.
	 */
	@Override
	public ShortBigListIterator iterator() {
		return listIterator();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @implSpec This implementation delegates to {@link BigList#listIterator(long) listIterator(0)}.
	 */
	@Override
	public ShortBigListIterator listIterator() {
		return listIterator(0L);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @implSpec This implementation is based on the random-access methods.
	 */
	@Override
	public ShortBigListIterator listIterator(final long index) {
		ensureIndex(index);
		return new ShortBigListIterators.AbstractIndexBasedBigListIterator(0, index) {
			@Override
			protected final short get(long i) {
				return AbstractShortBigList.this.getShort(i);
			}

			@Override
			protected final void add(long i, short k) {
				AbstractShortBigList.this.add(i, k);
			}

			@Override
			protected final void set(long i, short k) {
				AbstractShortBigList.this.set(i, k);
			}

			@Override
			protected final void remove(long i) {
				AbstractShortBigList.this.removeShort(i);
			}

			@Override
			protected final long getMaxPos() {
				return AbstractShortBigList.this.size64();
			}
		};
	}

	static final class IndexBasedSpliterator extends ShortBigSpliterators.LateBindingSizeIndexBasedSpliterator {
		final ShortBigList l;

		IndexBasedSpliterator(ShortBigList l, long pos) {
			super(pos);
			this.l = l;
		}

		IndexBasedSpliterator(ShortBigList l, long pos, long maxPos) {
			super(pos, maxPos);
			this.l = l;
		}

		@Override
		protected final long getMaxPosFromBackingStore() {
			return l.size64();
		}

		@Override
		protected final short get(long i) {
			return l.getShort(i);
		}

		@Override
		protected final IndexBasedSpliterator makeForSplit(long pos, long maxPos) {
			return new IndexBasedSpliterator(l, pos, maxPos);
		}
	}

	@Override
	public it.unimi.dsi.fastutil.ints.IntSpliterator intSpliterator() {
		if (this instanceof java.util.RandomAccess) {
			return ShortSpliterators.widen(spliterator());
		} else {
			return super.intSpliterator();
		}
	}

	/**
	 * Returns true if this list contains the specified element.
	 * 
	 * @implSpec This implementation delegates to {@code indexOf()}.
	 * @see BigList#contains(Object)
	 */
	@Override
	public boolean contains(final short k) {
		return indexOf(k) >= 0;
	}

	@Override
	public long indexOf(final short k) {
		final ShortBigListIterator i = listIterator();
		short e;
		while (i.hasNext()) {
			e = i.nextShort();
			if (((k) == (e))) return i.previousIndex();
		}
		return -1;
	}

	@Override
	public long lastIndexOf(final short k) {
		ShortBigListIterator i = listIterator(size64());
		short e;
		while (i.hasPrevious()) {
			e = i.previousShort();
			if (((k) == (e))) return i.nextIndex();
		}
		return -1;
	}

	@Override
	public void size(final long size) {
		long i = size64();
		if (size > i) while (i++ < size) add(((short)0));
		else while (i-- != size) remove(i);
	}

	@Override
	public ShortBigList subList(final long from, final long to) {
		ensureIndex(from);
		ensureIndex(to);
		if (from > to) throw new IndexOutOfBoundsException("Start index (" + from + ") is greater than end index (" + to + ")");
		return this instanceof java.util.RandomAccess ? new ShortRandomAccessSubList(this, from, to) : new ShortSubList(this, from, to);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @implSpec If this list is {@link java.util.RandomAccess}, will iterate using a for loop and the
	 *           type-specific {@link java.util.List#get(int)} method. Otherwise it will fallback to
	 *           using the iterator based loop implementation from the superinterface.
	 */
	@Override
	public void forEach(final ShortConsumer action) {
		if (this instanceof java.util.RandomAccess) {
			for (long i = 0, max = size64(); i < max; ++i) {
				action.accept(getShort(i));
			}
		} else {
			super.forEach(action);
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
	public void removeElements(final long from, final long to) {
		ensureIndex(to);
		ShortBigListIterator i = listIterator(from);
		long n = to - from;
		if (n < 0) throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
		while (n-- != 0) {
			i.nextShort();
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
	public void addElements(long index, final short a[][], long offset, long length) {
		ensureIndex(index);
		ensureOffsetLength(a, offset, length);
		if (this instanceof java.util.RandomAccess) {
			while (length-- != 0) add(index++, BigArrays.get(a, offset++));
		} else {
			ShortBigListIterator iter = listIterator(index);
			while (length-- != 0) iter.add(BigArrays.get(a, offset++));
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * @implSpec This implementation delegates to the analogous method for big-array fragments.
	 */
	@Override
	public void addElements(final long index, final short a[][]) {
		addElements(index, a, 0, length(a));
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * This is a trivial iterator-based implementation. It is expected that implementations will
	 * override this method with a more optimized version.
	 */
	@Override
	public void getElements(final long from, final short a[][], long offset, long length) {
		ensureIndex(from);
		ensureOffsetLength(a, offset, length);
		if (from + length > size64()) throw new IndexOutOfBoundsException("End index (" + (from + length) + ") is greater than list size (" + size64() + ")");
		if (this instanceof java.util.RandomAccess) {
			long current = from;
			while (length-- != 0) BigArrays.set(a, offset++, getShort(current++));
		} else {
			ShortBigListIterator i = listIterator(from);
			while (length-- != 0) BigArrays.set(a, offset++, i.nextShort());
		}
	}

	@Override
	public void setElements(long index, short a[][], long offset, long length) {
		ensureIndex(index);
		ensureOffsetLength(a, offset, length);
		if (index + length > size64()) throw new IndexOutOfBoundsException("End index (" + (index + length) + ") is greater than list size (" + size64() + ")");
		if (this instanceof java.util.RandomAccess) {
			for (long i = 0; i < length; ++i) {
				set(i + index, BigArrays.get(a, i + offset));
			}
		} else {
			ShortBigListIterator iter = listIterator(index);
			long i = 0;
			while (i < length) {
				iter.nextShort();
				iter.set(BigArrays.get(a, offset + i++));
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @implSpec This implementation delegates to {@link #removeElements(long, long)}.
	 */
	@Override
	public void clear() {
		removeElements(0, size64());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @implSpec This implementation delegates to {@link #size64()}.
	 * @deprecated Please use {@link #size64()} instead.
	 */
	@Override
	@Deprecated
	public int size() {
		return (int)Math.min(Integer.MAX_VALUE, size64());
	}

	/**
	 * Returns the hash code for this big list, which is identical to {@link java.util.List#hashCode()}.
	 *
	 * @return the hash code for this big list.
	 */
	@Override
	public int hashCode() {
		ShortIterator i = iterator();
		int h = 1;
		long s = size64();
		while (s-- != 0) {
			short k = i.nextShort();
			h = 31 * h + (k);
		}
		return h;
	}

	@Override
	public boolean equals(final Object o) {
		if (o == this) return true;
		if (!(o instanceof BigList)) return false;
		final BigList<?> l = (BigList<?>)o;
		long s = size64();
		if (s != l.size64()) return false;
		if (l instanceof ShortBigList) {
			final ShortBigListIterator i1 = listIterator(), i2 = ((ShortBigList)l).listIterator();
			while (s-- != 0) if (i1.nextShort() != i2.nextShort()) return false;
			return true;
		}
		final BigListIterator<?> i1 = listIterator(), i2 = l.listIterator();
		while (s-- != 0) if (!java.util.Objects.equals(i1.next(), i2.next())) return false;
		return true;
	}

	/**
	 * Compares this big list to another object. If the argument is a {@link BigList}, this method
	 * performs a lexicographical comparison; otherwise, it throws a {@code ClassCastException}.
	 *
	 * @param l a big list.
	 * @return if the argument is a {@link BigList}, a negative integer, zero, or a positive integer as
	 *         this list is lexicographically less than, equal to, or greater than the argument.
	 * @throws ClassCastException if the argument is not a big list.
	 */

	@Override
	public int compareTo(final BigList<? extends Short> l) {
		if (l == this) return 0;
		if (l instanceof ShortBigList) {
			final ShortBigListIterator i1 = listIterator(), i2 = ((ShortBigList)l).listIterator();
			int r;
			short e1, e2;
			while (i1.hasNext() && i2.hasNext()) {
				e1 = i1.nextShort();
				e2 = i2.nextShort();
				if ((r = (Short.compare((e1), (e2)))) != 0) return r;
			}
			return i2.hasNext() ? -1 : (i1.hasNext() ? 1 : 0);
		}
		BigListIterator<? extends Short> i1 = listIterator(), i2 = l.listIterator();
		int r;
		while (i1.hasNext() && i2.hasNext()) {
			if ((r = ((Comparable<? super Short>)i1.next()).compareTo(i2.next())) != 0) return r;
		}
		return i2.hasNext() ? -1 : (i1.hasNext() ? 1 : 0);
	}

	@Override
	public void push(short o) {
		add(o);
	}

	@Override
	public short popShort() {
		if (isEmpty()) throw new NoSuchElementException();
		return removeShort(size64() - 1);
	}

	@Override
	public short topShort() {
		if (isEmpty()) throw new NoSuchElementException();
		return getShort(size64() - 1);
	}

	@Override
	public short peekShort(int i) {
		return getShort(size64() - 1 - i);
	}

	/**
	 * Removes a single instance of the specified element from this collection, if it is present
	 * (optional operation).
	 * 
	 * @implSpec This implementation delegates to {@code indexOf()}.
	 * @see BigList#remove(Object)
	 */
	@Override
	public boolean rem(short k) {
		long index = indexOf(k);
		if (index == -1) return false;
		removeShort(index);
		return true;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @implSpec This implementation delegates to the type-specific version of
	 *           {@link #addAll(long, Collection)}.
	 */
	@Override
	public boolean addAll(final long index, final ShortCollection c) {
		return addAll(index, (Collection<? extends Short>)c);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @implSpec This implementation delegates to the type-specific version of
	 *           {@link #addAll(long, Collection)}.
	 */
	@Override
	public boolean addAll(final ShortCollection c) {
		return addAll(size64(), c);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @implSpec This implementation delegates to the corresponding type-specific method.
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	public void add(final long index, final Short ok) {
		add(index, ok.shortValue());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @implSpec This implementation delegates to the corresponding type-specific method.
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	public Short set(final long index, final Short ok) {
		return Short.valueOf(set(index, ok.shortValue()));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @implSpec This implementation delegates to the corresponding type-specific method.
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	public Short get(final long index) {
		return Short.valueOf(getShort(index));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @implSpec This implementation delegates to the corresponding type-specific method.
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	public long indexOf(final Object ok) {
		return indexOf(((Short)(ok)).shortValue());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @implSpec This implementation delegates to the corresponding type-specific method.
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	public long lastIndexOf(final Object ok) {
		return lastIndexOf(((Short)(ok)).shortValue());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @implSpec This implementation delegates to the corresponding type-specific method.
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	public Short remove(final long index) {
		return Short.valueOf(removeShort(index));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @implSpec This implementation delegates to the corresponding type-specific method.
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	public void push(Short o) {
		push(o.shortValue());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @implSpec This implementation delegates to the corresponding type-specific method.
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	public Short pop() {
		return Short.valueOf(popShort());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @implSpec This implementation delegates to the corresponding type-specific method.
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	public Short top() {
		return Short.valueOf(topShort());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @implSpec This implementation delegates to the corresponding type-specific method.
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	public Short peek(int i) {
		return Short.valueOf(peekShort(i));
	}

	@Override
	public String toString() {
		final StringBuilder s = new StringBuilder();
		final ShortIterator i = iterator();
		long n = size64();
		short k;
		boolean first = true;
		s.append("[");
		while (n-- != 0) {
			if (first) first = false;
			else s.append(", ");
			k = i.nextShort();
			s.append(String.valueOf(k));
		}
		s.append("]");
		return s.toString();
	}

	/** A class implementing a sublist view. */
	public static class ShortSubList extends AbstractShortBigList implements java.io.Serializable {
		private static final long serialVersionUID = -7046029254386353129L;
		/** The list this sublist restricts. */
		protected final ShortBigList l;
		/** Initial (inclusive) index of this sublist. */
		protected final long from;
		/** Final (exclusive) index of this sublist. */
		protected long to;

		public ShortSubList(final ShortBigList l, final long from, final long to) {
			this.l = l;
			this.from = from;
			this.to = to;
		}

		private boolean assertRange() {
			assert from <= l.size64();
			assert to <= l.size64();
			assert to >= from;
			return true;
		}

		@Override
		public boolean add(final short k) {
			l.add(to, k);
			to++;
			assert assertRange();
			return true;
		}

		@Override
		public void add(final long index, final short k) {
			ensureIndex(index);
			l.add(from + index, k);
			to++;
			assert assertRange();
		}

		@Override
		public boolean addAll(final long index, final Collection<? extends Short> c) {
			ensureIndex(index);
			to += c.size();
			return l.addAll(from + index, c);
		}

		@Override
		public short getShort(long index) {
			ensureRestrictedIndex(index);
			return l.getShort(from + index);
		}

		@Override
		public short removeShort(long index) {
			ensureRestrictedIndex(index);
			to--;
			return l.removeShort(from + index);
		}

		@Override
		public short set(long index, short k) {
			ensureRestrictedIndex(index);
			return l.set(from + index, k);
		}

		@Override
		public long size64() {
			return to - from;
		}

		@Override
		public void getElements(final long from, final short[][] a, final long offset, final long length) {
			ensureIndex(from);
			if (from + length > size64()) throw new IndexOutOfBoundsException("End index (" + from + length + ") is greater than list size (" + size64() + ")");
			l.getElements(this.from + from, a, offset, length);
		}

		@Override
		public void removeElements(final long from, final long to) {
			ensureIndex(from);
			ensureIndex(to);
			l.removeElements(this.from + from, this.from + to);
			this.to -= (to - from);
			assert assertRange();
		}

		@Override
		public void addElements(final long index, final short a[][], long offset, long length) {
			ensureIndex(index);
			l.addElements(this.from + index, a, offset, length);
			this.to += length;
			assert assertRange();
		}

		private final class RandomAccessIter extends ShortBigListIterators.AbstractIndexBasedBigListIterator {
			// We don't set the minPos to be "from" because we need to call our containing class'
			// add, set, and remove methods with 0 relative to the start of the sublist, not the
			// start of the original list.
			// Thus pos is relative to the start of the SubList, not the start of the original list.
			RandomAccessIter(long pos) {
				super(0, pos);
			}

			@Override
			protected final short get(long i) {
				return l.getShort(from + i);
			}

			// Remember, these are calling SUBLIST's methods, meaning 0 is the start of the sublist for these.
			@Override
			protected final void add(long i, short k) {
				ShortSubList.this.add(i, k);
			}

			@Override
			protected final void set(long i, short k) {
				ShortSubList.this.set(i, k);
			}

			@Override
			protected final void remove(long i) {
				ShortSubList.this.removeShort(i);
			}

			@Override
			protected final long getMaxPos() {
				return to - from;
			}

			@Override
			public void add(short k) {
				super.add(k);
				assert assertRange();
			}

			@Override
			public void remove() {
				super.remove();
				assert assertRange();
			}
		}

		private class ParentWrappingIter implements ShortBigListIterator {
			private ShortBigListIterator parent;

			ParentWrappingIter(ShortBigListIterator parent) {
				this.parent = parent;
			}

			@Override
			public long nextIndex() {
				return parent.nextIndex() - from;
			}

			@Override
			public long previousIndex() {
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
			public short nextShort() {
				if (!hasNext()) throw new NoSuchElementException();
				return parent.nextShort();
			}

			@Override
			public short previousShort() {
				if (!hasPrevious()) throw new NoSuchElementException();
				return parent.previousShort();
			}

			@Override
			public void add(short k) {
				parent.add(k);
			}

			@Override
			public void set(short k) {
				parent.set(k);
			}

			@Override
			public void remove() {
				parent.remove();
			}

			@Override
			public long back(long n) {
				if (n < 0) throw new IllegalArgumentException("Argument must be nonnegative: " + n);
				long currentPos = parent.previousIndex();
				long parentNewPos = currentPos - n;
				// Remember, the minimum acceptable previousIndex is not from but (from - 1), since (from - 1)
				// means this subList is at the beginning of our sub range.
				// Same reason why previousIndex()'s minimum for the full list is not 0 but -1.
				if (parentNewPos < (from - 1)) parentNewPos = (from - 1);
				long toSkip = parentNewPos - currentPos;
				return parent.back(toSkip);
			}

			@Override
			public long skip(long n) {
				if (n < 0) throw new IllegalArgumentException("Argument must be nonnegative: " + n);
				long currentPos = parent.nextIndex();
				long parentNewPos = currentPos + n;
				if (parentNewPos > to) parentNewPos = to;
				long toSkip = parentNewPos - currentPos;
				return parent.skip(toSkip);
			}
		}

		@Override
		public ShortBigListIterator listIterator(final long index) {
			ensureIndex(index);
			// If this class wasn't public, then RandomAccessIter would live in SUBLISTRandomAccess,
			// and the switching would be done in sublist(long, long). However, this is a public class
			// that may have existing implementors, so to get the benefit of RandomAccessIter class for
			// for existing uses, it has to be done in this class.
			return l instanceof java.util.RandomAccess ? new RandomAccessIter(index) : new ParentWrappingIter(l.listIterator(index + from));
		}

		@Override
		public ShortSpliterator spliterator() {
			return l instanceof java.util.RandomAccess ? new IndexBasedSpliterator(l, from, to) : super.spliterator();
		}

		@Override
		public it.unimi.dsi.fastutil.ints.IntSpliterator intSpliterator() {
			if (l instanceof java.util.RandomAccess) {
				return ShortSpliterators.widen(spliterator());
			} else {
				return super.intSpliterator();
			}
		}

		@Override
		public ShortBigList subList(final long from, final long to) {
			ensureIndex(from);
			ensureIndex(to);
			if (from > to) throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
			// Sadly we have to rewrap this, because if there is a sublist of a sublist, and the
			// subsublist adds, both sublists need to update their "to" value.
			return new ShortSubList(this, from, to);
		}

		@Override
		public boolean rem(short k) {
			long index = indexOf(k);
			if (index == -1) return false;
			to--;
			l.removeShort(from + index);
			assert assertRange();
			return true;
		}

		@Override
		public boolean addAll(final long index, final ShortCollection c) {
			return super.addAll(index, c);
		}

		@Override
		public boolean addAll(final long index, final ShortBigList l) {
			return super.addAll(index, l);
		}
	}

	public static class ShortRandomAccessSubList extends ShortSubList implements java.util.RandomAccess {
		private static final long serialVersionUID = -107070782945191929L;

		public ShortRandomAccessSubList(final ShortBigList l, final long from, final long to) {
			super(l, from, to);
		}

		@Override
		public ShortBigList subList(final long from, final long to) {
			ensureIndex(from);
			ensureIndex(to);
			if (from > to) throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
			// Sadly we have to rewrap this, because if there is a sublist of a sublist, and the
			// subsublist adds, both sublists need to update their "to" value.
			return new ShortRandomAccessSubList(this, from, to);
		}
	}
}
