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
package it.unimi.dsi.fastutil.longs;

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
public abstract class AbstractLongBigList extends AbstractLongCollection implements LongBigList, LongStack {
	protected AbstractLongBigList() {
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
	public void add(final long index, final long k) {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @implSpec This implementation delegates to the type-specific version of
	 *           {@link BigList#add(long, Object)}.
	 */
	@Override
	public boolean add(final long k) {
		add(size64(), k);
		return true;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @implSpec This implementation always throws an {@link UnsupportedOperationException}.
	 */
	@Override
	public long removeLong(long i) {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @implSpec This implementation always throws an {@link UnsupportedOperationException}.
	 */
	@Override
	public long set(final long index, final long k) {
		throw new UnsupportedOperationException();
	}

	/** Adds all of the elements in the specified collection to this list (optional operation). */
	@Override
	public boolean addAll(long index, final Collection<? extends Long> c) {
		ensureIndex(index);
		final Iterator<? extends Long> i = c.iterator();
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
	public boolean addAll(final Collection<? extends Long> c) {
		return addAll(size64(), c);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @implSpec This implementation delegates to {@link #listIterator()}.
	 */
	@Override
	public LongBigListIterator iterator() {
		return listIterator();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @implSpec This implementation delegates to {@link BigList#listIterator(long) listIterator(0)}.
	 */
	@Override
	public LongBigListIterator listIterator() {
		return listIterator(0L);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @implSpec This implementation is based on the random-access methods.
	 */
	@Override
	public LongBigListIterator listIterator(final long index) {
		ensureIndex(index);
		return new LongBigListIterators.AbstractIndexBasedBigListIterator(0, index) {
			@Override
			protected final long get(long i) {
				return AbstractLongBigList.this.getLong(i);
			}

			@Override
			protected final void add(long i, long k) {
				AbstractLongBigList.this.add(i, k);
			}

			@Override
			protected final void set(long i, long k) {
				AbstractLongBigList.this.set(i, k);
			}

			@Override
			protected final void remove(long i) {
				AbstractLongBigList.this.removeLong(i);
			}

			@Override
			protected final long getMaxPos() {
				return AbstractLongBigList.this.size64();
			}
		};
	}

	static final class IndexBasedSpliterator extends LongBigSpliterators.LateBindingSizeIndexBasedSpliterator {
		final LongBigList l;

		IndexBasedSpliterator(LongBigList l, long pos) {
			super(pos);
			this.l = l;
		}

		IndexBasedSpliterator(LongBigList l, long pos, long maxPos) {
			super(pos, maxPos);
			this.l = l;
		}

		@Override
		protected final long getMaxPosFromBackingStore() {
			return l.size64();
		}

		@Override
		protected final long get(long i) {
			return l.getLong(i);
		}

		@Override
		protected final IndexBasedSpliterator makeForSplit(long pos, long maxPos) {
			return new IndexBasedSpliterator(l, pos, maxPos);
		}
	}

	/**
	 * Returns true if this list contains the specified element.
	 * 
	 * @implSpec This implementation delegates to {@code indexOf()}.
	 * @see BigList#contains(Object)
	 */
	@Override
	public boolean contains(final long k) {
		return indexOf(k) >= 0;
	}

	@Override
	public long indexOf(final long k) {
		final LongBigListIterator i = listIterator();
		long e;
		while (i.hasNext()) {
			e = i.nextLong();
			if (((k) == (e))) return i.previousIndex();
		}
		return -1;
	}

	@Override
	public long lastIndexOf(final long k) {
		LongBigListIterator i = listIterator(size64());
		long e;
		while (i.hasPrevious()) {
			e = i.previousLong();
			if (((k) == (e))) return i.nextIndex();
		}
		return -1;
	}

	@Override
	public void size(final long size) {
		long i = size64();
		if (size > i) while (i++ < size) add((0));
		else while (i-- != size) remove(i);
	}

	@Override
	public LongBigList subList(final long from, final long to) {
		ensureIndex(from);
		ensureIndex(to);
		if (from > to) throw new IndexOutOfBoundsException("Start index (" + from + ") is greater than end index (" + to + ")");
		return this instanceof java.util.RandomAccess ? new LongRandomAccessSubList(this, from, to) : new LongSubList(this, from, to);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @implSpec If this list is {@link java.util.RandomAccess}, will iterate using a for loop and the
	 *           type-specific {@link java.util.List#get(int)} method. Otherwise it will fallback to
	 *           using the iterator based loop implementation from the superinterface.
	 */
	@Override
	public void forEach(final java.util.function.LongConsumer action) {
		if (this instanceof java.util.RandomAccess) {
			for (long i = 0, max = size64(); i < max; ++i) {
				action.accept(getLong(i));
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
		LongBigListIterator i = listIterator(from);
		long n = to - from;
		if (n < 0) throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
		while (n-- != 0) {
			i.nextLong();
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
	public void addElements(long index, final long a[][], long offset, long length) {
		ensureIndex(index);
		ensureOffsetLength(a, offset, length);
		if (this instanceof java.util.RandomAccess) {
			while (length-- != 0) add(index++, BigArrays.get(a, offset++));
		} else {
			LongBigListIterator iter = listIterator(index);
			while (length-- != 0) iter.add(BigArrays.get(a, offset++));
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * @implSpec This implementation delegates to the analogous method for big-array fragments.
	 */
	@Override
	public void addElements(final long index, final long a[][]) {
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
	public void getElements(final long from, final long a[][], long offset, long length) {
		ensureIndex(from);
		ensureOffsetLength(a, offset, length);
		if (from + length > size64()) throw new IndexOutOfBoundsException("End index (" + (from + length) + ") is greater than list size (" + size64() + ")");
		if (this instanceof java.util.RandomAccess) {
			long current = from;
			while (length-- != 0) BigArrays.set(a, offset++, getLong(current++));
		} else {
			LongBigListIterator i = listIterator(from);
			while (length-- != 0) BigArrays.set(a, offset++, i.nextLong());
		}
	}

	@Override
	public void setElements(long index, long a[][], long offset, long length) {
		ensureIndex(index);
		ensureOffsetLength(a, offset, length);
		if (index + length > size64()) throw new IndexOutOfBoundsException("End index (" + (index + length) + ") is greater than list size (" + size64() + ")");
		if (this instanceof java.util.RandomAccess) {
			for (long i = 0; i < length; ++i) {
				set(i + index, BigArrays.get(a, i + offset));
			}
		} else {
			LongBigListIterator iter = listIterator(index);
			long i = 0;
			while (i < length) {
				iter.nextLong();
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
		LongIterator i = iterator();
		int h = 1;
		long s = size64();
		while (s-- != 0) {
			long k = i.nextLong();
			h = 31 * h + it.unimi.dsi.fastutil.HashCommon.long2int(k);
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
		if (l instanceof LongBigList) {
			final LongBigListIterator i1 = listIterator(), i2 = ((LongBigList)l).listIterator();
			while (s-- != 0) if (i1.nextLong() != i2.nextLong()) return false;
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
	public int compareTo(final BigList<? extends Long> l) {
		if (l == this) return 0;
		if (l instanceof LongBigList) {
			final LongBigListIterator i1 = listIterator(), i2 = ((LongBigList)l).listIterator();
			int r;
			long e1, e2;
			while (i1.hasNext() && i2.hasNext()) {
				e1 = i1.nextLong();
				e2 = i2.nextLong();
				if ((r = (Long.compare((e1), (e2)))) != 0) return r;
			}
			return i2.hasNext() ? -1 : (i1.hasNext() ? 1 : 0);
		}
		BigListIterator<? extends Long> i1 = listIterator(), i2 = l.listIterator();
		int r;
		while (i1.hasNext() && i2.hasNext()) {
			if ((r = ((Comparable<? super Long>)i1.next()).compareTo(i2.next())) != 0) return r;
		}
		return i2.hasNext() ? -1 : (i1.hasNext() ? 1 : 0);
	}

	@Override
	public void push(long o) {
		add(o);
	}

	@Override
	public long popLong() {
		if (isEmpty()) throw new NoSuchElementException();
		return removeLong(size64() - 1);
	}

	@Override
	public long topLong() {
		if (isEmpty()) throw new NoSuchElementException();
		return getLong(size64() - 1);
	}

	@Override
	public long peekLong(int i) {
		return getLong(size64() - 1 - i);
	}

	/**
	 * Removes a single instance of the specified element from this collection, if it is present
	 * (optional operation).
	 * 
	 * @implSpec This implementation delegates to {@code indexOf()}.
	 * @see BigList#remove(Object)
	 */
	@Override
	public boolean rem(long k) {
		long index = indexOf(k);
		if (index == -1) return false;
		removeLong(index);
		return true;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @implSpec This implementation delegates to the type-specific version of
	 *           {@link #addAll(long, Collection)}.
	 */
	@Override
	public boolean addAll(final long index, final LongCollection c) {
		return addAll(index, (Collection<? extends Long>)c);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @implSpec This implementation delegates to the type-specific version of
	 *           {@link #addAll(long, Collection)}.
	 */
	@Override
	public boolean addAll(final LongCollection c) {
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
	public void add(final long index, final Long ok) {
		add(index, ok.longValue());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @implSpec This implementation delegates to the corresponding type-specific method.
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	public Long set(final long index, final Long ok) {
		return Long.valueOf(set(index, ok.longValue()));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @implSpec This implementation delegates to the corresponding type-specific method.
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	public Long get(final long index) {
		return Long.valueOf(getLong(index));
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
		return indexOf(((Long)(ok)).longValue());
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
		return lastIndexOf(((Long)(ok)).longValue());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @implSpec This implementation delegates to the corresponding type-specific method.
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	public Long remove(final long index) {
		return Long.valueOf(removeLong(index));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @implSpec This implementation delegates to the corresponding type-specific method.
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	public void push(Long o) {
		push(o.longValue());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @implSpec This implementation delegates to the corresponding type-specific method.
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	public Long pop() {
		return Long.valueOf(popLong());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @implSpec This implementation delegates to the corresponding type-specific method.
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	public Long top() {
		return Long.valueOf(topLong());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @implSpec This implementation delegates to the corresponding type-specific method.
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	public Long peek(int i) {
		return Long.valueOf(peekLong(i));
	}

	@Override
	public String toString() {
		final StringBuilder s = new StringBuilder();
		final LongIterator i = iterator();
		long n = size64();
		long k;
		boolean first = true;
		s.append("[");
		while (n-- != 0) {
			if (first) first = false;
			else s.append(", ");
			k = i.nextLong();
			s.append(String.valueOf(k));
		}
		s.append("]");
		return s.toString();
	}

	/** A class implementing a sublist view. */
	public static class LongSubList extends AbstractLongBigList implements java.io.Serializable {
		private static final long serialVersionUID = -7046029254386353129L;
		/** The list this sublist restricts. */
		protected final LongBigList l;
		/** Initial (inclusive) index of this sublist. */
		protected final long from;
		/** Final (exclusive) index of this sublist. */
		protected long to;

		public LongSubList(final LongBigList l, final long from, final long to) {
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
		public boolean add(final long k) {
			l.add(to, k);
			to++;
			assert assertRange();
			return true;
		}

		@Override
		public void add(final long index, final long k) {
			ensureIndex(index);
			l.add(from + index, k);
			to++;
			assert assertRange();
		}

		@Override
		public boolean addAll(final long index, final Collection<? extends Long> c) {
			ensureIndex(index);
			to += c.size();
			return l.addAll(from + index, c);
		}

		@Override
		public long getLong(long index) {
			ensureRestrictedIndex(index);
			return l.getLong(from + index);
		}

		@Override
		public long removeLong(long index) {
			ensureRestrictedIndex(index);
			to--;
			return l.removeLong(from + index);
		}

		@Override
		public long set(long index, long k) {
			ensureRestrictedIndex(index);
			return l.set(from + index, k);
		}

		@Override
		public long size64() {
			return to - from;
		}

		@Override
		public void getElements(final long from, final long[][] a, final long offset, final long length) {
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
		public void addElements(final long index, final long a[][], long offset, long length) {
			ensureIndex(index);
			l.addElements(this.from + index, a, offset, length);
			this.to += length;
			assert assertRange();
		}

		private final class RandomAccessIter extends LongBigListIterators.AbstractIndexBasedBigListIterator {
			// We don't set the minPos to be "from" because we need to call our containing class'
			// add, set, and remove methods with 0 relative to the start of the sublist, not the
			// start of the original list.
			// Thus pos is relative to the start of the SubList, not the start of the original list.
			RandomAccessIter(long pos) {
				super(0, pos);
			}

			@Override
			protected final long get(long i) {
				return l.getLong(from + i);
			}

			// Remember, these are calling SUBLIST's methods, meaning 0 is the start of the sublist for these.
			@Override
			protected final void add(long i, long k) {
				LongSubList.this.add(i, k);
			}

			@Override
			protected final void set(long i, long k) {
				LongSubList.this.set(i, k);
			}

			@Override
			protected final void remove(long i) {
				LongSubList.this.removeLong(i);
			}

			@Override
			protected final long getMaxPos() {
				return to - from;
			}

			@Override
			public void add(long k) {
				super.add(k);
				assert assertRange();
			}

			@Override
			public void remove() {
				super.remove();
				assert assertRange();
			}
		}

		private class ParentWrappingIter implements LongBigListIterator {
			private LongBigListIterator parent;

			ParentWrappingIter(LongBigListIterator parent) {
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
			public long nextLong() {
				if (!hasNext()) throw new NoSuchElementException();
				return parent.nextLong();
			}

			@Override
			public long previousLong() {
				if (!hasPrevious()) throw new NoSuchElementException();
				return parent.previousLong();
			}

			@Override
			public void add(long k) {
				parent.add(k);
			}

			@Override
			public void set(long k) {
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
		public LongBigListIterator listIterator(final long index) {
			ensureIndex(index);
			// If this class wasn't public, then RandomAccessIter would live in SUBLISTRandomAccess,
			// and the switching would be done in sublist(long, long). However, this is a public class
			// that may have existing implementors, so to get the benefit of RandomAccessIter class for
			// for existing uses, it has to be done in this class.
			return l instanceof java.util.RandomAccess ? new RandomAccessIter(index) : new ParentWrappingIter(l.listIterator(index + from));
		}

		@Override
		public LongSpliterator spliterator() {
			return l instanceof java.util.RandomAccess ? new IndexBasedSpliterator(l, from, to) : super.spliterator();
		}

		@Override
		public LongBigList subList(final long from, final long to) {
			ensureIndex(from);
			ensureIndex(to);
			if (from > to) throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
			// Sadly we have to rewrap this, because if there is a sublist of a sublist, and the
			// subsublist adds, both sublists need to update their "to" value.
			return new LongSubList(this, from, to);
		}

		@Override
		public boolean rem(long k) {
			long index = indexOf(k);
			if (index == -1) return false;
			to--;
			l.removeLong(from + index);
			assert assertRange();
			return true;
		}

		@Override
		public boolean addAll(final long index, final LongCollection c) {
			return super.addAll(index, c);
		}

		@Override
		public boolean addAll(final long index, final LongBigList l) {
			return super.addAll(index, l);
		}
	}

	public static class LongRandomAccessSubList extends LongSubList implements java.util.RandomAccess {
		private static final long serialVersionUID = -107070782945191929L;

		public LongRandomAccessSubList(final LongBigList l, final long from, final long to) {
			super(l, from, to);
		}

		@Override
		public LongBigList subList(final long from, final long to) {
			ensureIndex(from);
			ensureIndex(to);
			if (from > to) throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
			// Sadly we have to rewrap this, because if there is a sublist of a sublist, and the
			// subsublist adds, both sublists need to update their "to" value.
			return new LongRandomAccessSubList(this, from, to);
		}
	}
}
