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
package it.unimi.dsi.fastutil.bytes;

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
public abstract class AbstractByteBigList extends AbstractByteCollection implements ByteBigList, ByteStack {
	protected AbstractByteBigList() {
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
	public void add(final long index, final byte k) {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @implSpec This implementation delegates to the type-specific version of
	 *           {@link BigList#add(long, Object)}.
	 */
	@Override
	public boolean add(final byte k) {
		add(size64(), k);
		return true;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @implSpec This implementation always throws an {@link UnsupportedOperationException}.
	 */
	@Override
	public byte removeByte(long i) {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @implSpec This implementation always throws an {@link UnsupportedOperationException}.
	 */
	@Override
	public byte set(final long index, final byte k) {
		throw new UnsupportedOperationException();
	}

	/** Adds all of the elements in the specified collection to this list (optional operation). */
	@Override
	public boolean addAll(long index, final Collection<? extends Byte> c) {
		ensureIndex(index);
		final Iterator<? extends Byte> i = c.iterator();
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
	public boolean addAll(final Collection<? extends Byte> c) {
		return addAll(size64(), c);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @implSpec This implementation delegates to {@link #listIterator()}.
	 */
	@Override
	public ByteBigListIterator iterator() {
		return listIterator();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @implSpec This implementation delegates to {@link BigList#listIterator(long) listIterator(0)}.
	 */
	@Override
	public ByteBigListIterator listIterator() {
		return listIterator(0L);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @implSpec This implementation is based on the random-access methods.
	 */
	@Override
	public ByteBigListIterator listIterator(final long index) {
		ensureIndex(index);
		return new ByteBigListIterators.AbstractIndexBasedBigListIterator(0, index) {
			@Override
			protected final byte get(long i) {
				return AbstractByteBigList.this.getByte(i);
			}

			@Override
			protected final void add(long i, byte k) {
				AbstractByteBigList.this.add(i, k);
			}

			@Override
			protected final void set(long i, byte k) {
				AbstractByteBigList.this.set(i, k);
			}

			@Override
			protected final void remove(long i) {
				AbstractByteBigList.this.removeByte(i);
			}

			@Override
			protected final long getMaxPos() {
				return AbstractByteBigList.this.size64();
			}
		};
	}

	static final class IndexBasedSpliterator extends ByteBigSpliterators.LateBindingSizeIndexBasedSpliterator {
		final ByteBigList l;

		IndexBasedSpliterator(ByteBigList l, long pos) {
			super(pos);
			this.l = l;
		}

		IndexBasedSpliterator(ByteBigList l, long pos, long maxPos) {
			super(pos, maxPos);
			this.l = l;
		}

		@Override
		protected final long getMaxPosFromBackingStore() {
			return l.size64();
		}

		@Override
		protected final byte get(long i) {
			return l.getByte(i);
		}

		@Override
		protected final IndexBasedSpliterator makeForSplit(long pos, long maxPos) {
			return new IndexBasedSpliterator(l, pos, maxPos);
		}
	}

	@Override
	public it.unimi.dsi.fastutil.ints.IntSpliterator intSpliterator() {
		if (this instanceof java.util.RandomAccess) {
			return ByteSpliterators.widen(spliterator());
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
	public boolean contains(final byte k) {
		return indexOf(k) >= 0;
	}

	@Override
	public long indexOf(final byte k) {
		final ByteBigListIterator i = listIterator();
		byte e;
		while (i.hasNext()) {
			e = i.nextByte();
			if (((k) == (e))) return i.previousIndex();
		}
		return -1;
	}

	@Override
	public long lastIndexOf(final byte k) {
		ByteBigListIterator i = listIterator(size64());
		byte e;
		while (i.hasPrevious()) {
			e = i.previousByte();
			if (((k) == (e))) return i.nextIndex();
		}
		return -1;
	}

	@Override
	public void size(final long size) {
		long i = size64();
		if (size > i) while (i++ < size) add(((byte)0));
		else while (i-- != size) remove(i);
	}

	@Override
	public ByteBigList subList(final long from, final long to) {
		ensureIndex(from);
		ensureIndex(to);
		if (from > to) throw new IndexOutOfBoundsException("Start index (" + from + ") is greater than end index (" + to + ")");
		return this instanceof java.util.RandomAccess ? new ByteRandomAccessSubList(this, from, to) : new ByteSubList(this, from, to);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @implSpec If this list is {@link java.util.RandomAccess}, will iterate using a for loop and the
	 *           type-specific {@link java.util.List#get(int)} method. Otherwise it will fallback to
	 *           using the iterator based loop implementation from the superinterface.
	 */
	@Override
	public void forEach(final ByteConsumer action) {
		if (this instanceof java.util.RandomAccess) {
			for (long i = 0, max = size64(); i < max; ++i) {
				action.accept(getByte(i));
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
		ByteBigListIterator i = listIterator(from);
		long n = to - from;
		if (n < 0) throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
		while (n-- != 0) {
			i.nextByte();
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
	public void addElements(long index, final byte a[][], long offset, long length) {
		ensureIndex(index);
		ensureOffsetLength(a, offset, length);
		if (this instanceof java.util.RandomAccess) {
			while (length-- != 0) add(index++, BigArrays.get(a, offset++));
		} else {
			ByteBigListIterator iter = listIterator(index);
			while (length-- != 0) iter.add(BigArrays.get(a, offset++));
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * @implSpec This implementation delegates to the analogous method for big-array fragments.
	 */
	@Override
	public void addElements(final long index, final byte a[][]) {
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
	public void getElements(final long from, final byte a[][], long offset, long length) {
		ensureIndex(from);
		ensureOffsetLength(a, offset, length);
		if (from + length > size64()) throw new IndexOutOfBoundsException("End index (" + (from + length) + ") is greater than list size (" + size64() + ")");
		if (this instanceof java.util.RandomAccess) {
			long current = from;
			while (length-- != 0) BigArrays.set(a, offset++, getByte(current++));
		} else {
			ByteBigListIterator i = listIterator(from);
			while (length-- != 0) BigArrays.set(a, offset++, i.nextByte());
		}
	}

	@Override
	public void setElements(long index, byte a[][], long offset, long length) {
		ensureIndex(index);
		ensureOffsetLength(a, offset, length);
		if (index + length > size64()) throw new IndexOutOfBoundsException("End index (" + (index + length) + ") is greater than list size (" + size64() + ")");
		if (this instanceof java.util.RandomAccess) {
			for (long i = 0; i < length; ++i) {
				set(i + index, BigArrays.get(a, i + offset));
			}
		} else {
			ByteBigListIterator iter = listIterator(index);
			long i = 0;
			while (i < length) {
				iter.nextByte();
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
		ByteIterator i = iterator();
		int h = 1;
		long s = size64();
		while (s-- != 0) {
			byte k = i.nextByte();
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
		if (l instanceof ByteBigList) {
			final ByteBigListIterator i1 = listIterator(), i2 = ((ByteBigList)l).listIterator();
			while (s-- != 0) if (i1.nextByte() != i2.nextByte()) return false;
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
	public int compareTo(final BigList<? extends Byte> l) {
		if (l == this) return 0;
		if (l instanceof ByteBigList) {
			final ByteBigListIterator i1 = listIterator(), i2 = ((ByteBigList)l).listIterator();
			int r;
			byte e1, e2;
			while (i1.hasNext() && i2.hasNext()) {
				e1 = i1.nextByte();
				e2 = i2.nextByte();
				if ((r = (Byte.compare((e1), (e2)))) != 0) return r;
			}
			return i2.hasNext() ? -1 : (i1.hasNext() ? 1 : 0);
		}
		BigListIterator<? extends Byte> i1 = listIterator(), i2 = l.listIterator();
		int r;
		while (i1.hasNext() && i2.hasNext()) {
			if ((r = ((Comparable<? super Byte>)i1.next()).compareTo(i2.next())) != 0) return r;
		}
		return i2.hasNext() ? -1 : (i1.hasNext() ? 1 : 0);
	}

	@Override
	public void push(byte o) {
		add(o);
	}

	@Override
	public byte popByte() {
		if (isEmpty()) throw new NoSuchElementException();
		return removeByte(size64() - 1);
	}

	@Override
	public byte topByte() {
		if (isEmpty()) throw new NoSuchElementException();
		return getByte(size64() - 1);
	}

	@Override
	public byte peekByte(int i) {
		return getByte(size64() - 1 - i);
	}

	/**
	 * Removes a single instance of the specified element from this collection, if it is present
	 * (optional operation).
	 * 
	 * @implSpec This implementation delegates to {@code indexOf()}.
	 * @see BigList#remove(Object)
	 */
	@Override
	public boolean rem(byte k) {
		long index = indexOf(k);
		if (index == -1) return false;
		removeByte(index);
		return true;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @implSpec This implementation delegates to the type-specific version of
	 *           {@link #addAll(long, Collection)}.
	 */
	@Override
	public boolean addAll(final long index, final ByteCollection c) {
		return addAll(index, (Collection<? extends Byte>)c);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @implSpec This implementation delegates to the type-specific version of
	 *           {@link #addAll(long, Collection)}.
	 */
	@Override
	public boolean addAll(final ByteCollection c) {
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
	public void add(final long index, final Byte ok) {
		add(index, ok.byteValue());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @implSpec This implementation delegates to the corresponding type-specific method.
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	public Byte set(final long index, final Byte ok) {
		return Byte.valueOf(set(index, ok.byteValue()));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @implSpec This implementation delegates to the corresponding type-specific method.
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	public Byte get(final long index) {
		return Byte.valueOf(getByte(index));
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
		return indexOf(((Byte)(ok)).byteValue());
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
		return lastIndexOf(((Byte)(ok)).byteValue());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @implSpec This implementation delegates to the corresponding type-specific method.
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	public Byte remove(final long index) {
		return Byte.valueOf(removeByte(index));
	}

	/**
	 * {@inheritDoc}
	 *
	 * @implSpec This implementation delegates to the corresponding type-specific method.
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	public void push(Byte o) {
		push(o.byteValue());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @implSpec This implementation delegates to the corresponding type-specific method.
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	public Byte pop() {
		return Byte.valueOf(popByte());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @implSpec This implementation delegates to the corresponding type-specific method.
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	public Byte top() {
		return Byte.valueOf(topByte());
	}

	/**
	 * {@inheritDoc}
	 *
	 * @implSpec This implementation delegates to the corresponding type-specific method.
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	public Byte peek(int i) {
		return Byte.valueOf(peekByte(i));
	}

	@Override
	public String toString() {
		final StringBuilder s = new StringBuilder();
		final ByteIterator i = iterator();
		long n = size64();
		byte k;
		boolean first = true;
		s.append("[");
		while (n-- != 0) {
			if (first) first = false;
			else s.append(", ");
			k = i.nextByte();
			s.append(String.valueOf(k));
		}
		s.append("]");
		return s.toString();
	}

	/** A class implementing a sublist view. */
	public static class ByteSubList extends AbstractByteBigList implements java.io.Serializable {
		private static final long serialVersionUID = -7046029254386353129L;
		/** The list this sublist restricts. */
		protected final ByteBigList l;
		/** Initial (inclusive) index of this sublist. */
		protected final long from;
		/** Final (exclusive) index of this sublist. */
		protected long to;

		public ByteSubList(final ByteBigList l, final long from, final long to) {
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
		public boolean add(final byte k) {
			l.add(to, k);
			to++;
			assert assertRange();
			return true;
		}

		@Override
		public void add(final long index, final byte k) {
			ensureIndex(index);
			l.add(from + index, k);
			to++;
			assert assertRange();
		}

		@Override
		public boolean addAll(final long index, final Collection<? extends Byte> c) {
			ensureIndex(index);
			to += c.size();
			return l.addAll(from + index, c);
		}

		@Override
		public byte getByte(long index) {
			ensureRestrictedIndex(index);
			return l.getByte(from + index);
		}

		@Override
		public byte removeByte(long index) {
			ensureRestrictedIndex(index);
			to--;
			return l.removeByte(from + index);
		}

		@Override
		public byte set(long index, byte k) {
			ensureRestrictedIndex(index);
			return l.set(from + index, k);
		}

		@Override
		public long size64() {
			return to - from;
		}

		@Override
		public void getElements(final long from, final byte[][] a, final long offset, final long length) {
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
		public void addElements(final long index, final byte a[][], long offset, long length) {
			ensureIndex(index);
			l.addElements(this.from + index, a, offset, length);
			this.to += length;
			assert assertRange();
		}

		private final class RandomAccessIter extends ByteBigListIterators.AbstractIndexBasedBigListIterator {
			// We don't set the minPos to be "from" because we need to call our containing class'
			// add, set, and remove methods with 0 relative to the start of the sublist, not the
			// start of the original list.
			// Thus pos is relative to the start of the SubList, not the start of the original list.
			RandomAccessIter(long pos) {
				super(0, pos);
			}

			@Override
			protected final byte get(long i) {
				return l.getByte(from + i);
			}

			// Remember, these are calling SUBLIST's methods, meaning 0 is the start of the sublist for these.
			@Override
			protected final void add(long i, byte k) {
				ByteSubList.this.add(i, k);
			}

			@Override
			protected final void set(long i, byte k) {
				ByteSubList.this.set(i, k);
			}

			@Override
			protected final void remove(long i) {
				ByteSubList.this.removeByte(i);
			}

			@Override
			protected final long getMaxPos() {
				return to - from;
			}

			@Override
			public void add(byte k) {
				super.add(k);
				assert assertRange();
			}

			@Override
			public void remove() {
				super.remove();
				assert assertRange();
			}
		}

		private class ParentWrappingIter implements ByteBigListIterator {
			private ByteBigListIterator parent;

			ParentWrappingIter(ByteBigListIterator parent) {
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
			public byte nextByte() {
				if (!hasNext()) throw new NoSuchElementException();
				return parent.nextByte();
			}

			@Override
			public byte previousByte() {
				if (!hasPrevious()) throw new NoSuchElementException();
				return parent.previousByte();
			}

			@Override
			public void add(byte k) {
				parent.add(k);
			}

			@Override
			public void set(byte k) {
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
		public ByteBigListIterator listIterator(final long index) {
			ensureIndex(index);
			// If this class wasn't public, then RandomAccessIter would live in SUBLISTRandomAccess,
			// and the switching would be done in sublist(long, long). However, this is a public class
			// that may have existing implementors, so to get the benefit of RandomAccessIter class for
			// for existing uses, it has to be done in this class.
			return l instanceof java.util.RandomAccess ? new RandomAccessIter(index) : new ParentWrappingIter(l.listIterator(index + from));
		}

		@Override
		public ByteSpliterator spliterator() {
			return l instanceof java.util.RandomAccess ? new IndexBasedSpliterator(l, from, to) : super.spliterator();
		}

		@Override
		public it.unimi.dsi.fastutil.ints.IntSpliterator intSpliterator() {
			if (l instanceof java.util.RandomAccess) {
				return ByteSpliterators.widen(spliterator());
			} else {
				return super.intSpliterator();
			}
		}

		@Override
		public ByteBigList subList(final long from, final long to) {
			ensureIndex(from);
			ensureIndex(to);
			if (from > to) throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
			// Sadly we have to rewrap this, because if there is a sublist of a sublist, and the
			// subsublist adds, both sublists need to update their "to" value.
			return new ByteSubList(this, from, to);
		}

		@Override
		public boolean rem(byte k) {
			long index = indexOf(k);
			if (index == -1) return false;
			to--;
			l.removeByte(from + index);
			assert assertRange();
			return true;
		}

		@Override
		public boolean addAll(final long index, final ByteCollection c) {
			return super.addAll(index, c);
		}

		@Override
		public boolean addAll(final long index, final ByteBigList l) {
			return super.addAll(index, l);
		}
	}

	public static class ByteRandomAccessSubList extends ByteSubList implements java.util.RandomAccess {
		private static final long serialVersionUID = -107070782945191929L;

		public ByteRandomAccessSubList(final ByteBigList l, final long from, final long to) {
			super(l, from, to);
		}

		@Override
		public ByteBigList subList(final long from, final long to) {
			ensureIndex(from);
			ensureIndex(to);
			if (from > to) throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
			// Sadly we have to rewrap this, because if there is a sublist of a sublist, and the
			// subsublist adds, both sublists need to update their "to" value.
			return new ByteRandomAccessSubList(this, from, to);
		}
	}
}
