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

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;
import it.unimi.dsi.fastutil.SafeMath;

/**
 * A class providing static methods and objects that do useful things with type-specific iterators.
 *
 * @see Iterator
 */
public final class IntBigListIterators {
	private IntBigListIterators() {
	}

	/**
	 * A class returning no elements and a type-specific big list iterator interface.
	 *
	 * <p>
	 * This class may be useful to implement your own in case you subclass a type-specific iterator.
	 */
	public static class EmptyBigListIterator implements IntBigListIterator, java.io.Serializable, Cloneable {
		private static final long serialVersionUID = -7046029254386353129L;

		protected EmptyBigListIterator() {
		}

		@Override
		public boolean hasNext() {
			return false;
		}

		@Override
		public boolean hasPrevious() {
			return false;
		}

		@Override
		public int nextInt() {
			throw new NoSuchElementException();
		}

		@Override
		public int previousInt() {
			throw new NoSuchElementException();
		}

		@Override
		public long nextIndex() {
			return 0;
		}

		@Override
		public long previousIndex() {
			return -1;
		}

		@Override
		public long skip(long n) {
			return 0;
		}

		@Override
		public long back(long n) {
			return 0;
		}

		@Override
		public Object clone() {
			return EMPTY_BIG_LIST_ITERATOR;
		}

		@Override
		public void forEachRemaining(final java.util.function.IntConsumer action) {
		}

		@Deprecated
		@Override
		public void forEachRemaining(final Consumer<? super Integer> action) {
		}

		private Object readResolve() {
			return EMPTY_BIG_LIST_ITERATOR;
		}
	}

	/**
	 * An empty iterator (immutable). It is serializable and cloneable.
	 *
	 * <p>
	 * The class of this objects represent an abstract empty iterator that can iterate as a
	 * type-specific (list) iterator.
	 */

	public static final EmptyBigListIterator EMPTY_BIG_LIST_ITERATOR = new EmptyBigListIterator();

	/** An iterator returning a single element. */
	private static class SingletonBigListIterator implements IntBigListIterator {
		private final int element;
		private int curr;

		public SingletonBigListIterator(final int element) {
			this.element = element;
		}

		@Override
		public boolean hasNext() {
			return curr == 0;
		}

		@Override
		public boolean hasPrevious() {
			return curr == 1;
		}

		@Override
		public int nextInt() {
			if (!hasNext()) throw new NoSuchElementException();
			curr = 1;
			return element;
		}

		@Override
		public int previousInt() {
			if (!hasPrevious()) throw new NoSuchElementException();
			curr = 0;
			return element;
		}

		@Override
		public void forEachRemaining(final java.util.function.IntConsumer action) {
			Objects.requireNonNull(action);
			if (curr == 0) {
				action.accept(element);
				curr = 1;
			}
		}

		@Override
		public long nextIndex() {
			return curr;
		}

		@Override
		public long previousIndex() {
			return curr - 1;
		}

		@Override
		public long back(long n) {
			if (n < 0) throw new IllegalArgumentException("Argument must be nonnegative: " + n);
			if (n == 0 || curr < 1) return 0;
			curr = 1;
			return 1;
		}

		@Override
		public long skip(long n) {
			if (n < 0) throw new IllegalArgumentException("Argument must be nonnegative: " + n);
			if (n == 0 || curr > 0) return 0;
			curr = 0;
			return 1;
		}
	}

	/**
	 * Returns an iterator that iterates just over the given element.
	 *
	 * @param element the only element to be returned by a type-specific list iterator.
	 * @return an iterator that iterates just over {@code element}.
	 */
	public static IntBigListIterator singleton(final int element) {
		return new SingletonBigListIterator(element);
	}

	/** An unmodifiable wrapper class for big list iterators. */
	public static class UnmodifiableBigListIterator implements IntBigListIterator {
		protected final IntBigListIterator i;

		public UnmodifiableBigListIterator(final IntBigListIterator i) {
			this.i = i;
		}

		@Override
		public boolean hasNext() {
			return i.hasNext();
		}

		@Override
		public boolean hasPrevious() {
			return i.hasPrevious();
		}

		@Override
		public int nextInt() {
			return i.nextInt();
		}

		@Override
		public int previousInt() {
			return i.previousInt();
		}

		@Override
		public long nextIndex() {
			return i.nextIndex();
		}

		@Override
		public long previousIndex() {
			return i.previousIndex();
		}

		@Override
		public void forEachRemaining(final java.util.function.IntConsumer action) {
			i.forEachRemaining(action);
		}

		@Deprecated
		@Override
		public void forEachRemaining(final Consumer<? super Integer> action) {
			i.forEachRemaining(action);
		}
	}

	/**
	 * Returns an unmodifiable list iterator backed by the specified list iterator.
	 *
	 * @param i the list iterator to be wrapped in an unmodifiable list iterator.
	 * @return an unmodifiable view of the specified list iterator.
	 */
	public static IntBigListIterator unmodifiable(final IntBigListIterator i) {
		return new UnmodifiableBigListIterator(i);
	}

	/** A class exposing a list iterator as a big-list iterator.. */
	public static class BigListIteratorListIterator implements IntBigListIterator {
		protected final IntListIterator i;

		protected BigListIteratorListIterator(final IntListIterator i) {
			this.i = i;
		}

		private int intDisplacement(long n) {
			if (n < Integer.MIN_VALUE || n > Integer.MAX_VALUE) throw new IndexOutOfBoundsException("This big iterator is restricted to 32-bit displacements");
			return (int)n;
		}

		@Override
		public void set(int ok) {
			i.set(ok);
		}

		@Override
		public void add(int ok) {
			i.add(ok);
		}

		@Override
		public int back(int n) {
			return i.back(n);
		}

		@Override
		public long back(long n) {
			return i.back(intDisplacement(n));
		}

		@Override
		public void remove() {
			i.remove();
		}

		@Override
		public int skip(int n) {
			return i.skip(n);
		}

		@Override
		public long skip(long n) {
			return i.skip(intDisplacement(n));
		}

		@Override
		public boolean hasNext() {
			return i.hasNext();
		}

		@Override
		public boolean hasPrevious() {
			return i.hasPrevious();
		}

		@Override
		public int nextInt() {
			return i.nextInt();
		}

		@Override
		public int previousInt() {
			return i.previousInt();
		}

		@Override
		public long nextIndex() {
			return i.nextIndex();
		}

		@Override
		public long previousIndex() {
			return i.previousIndex();
		}

		@Override
		public void forEachRemaining(final java.util.function.IntConsumer action) {
			i.forEachRemaining(action);
		}

		@Deprecated
		@Override
		public void forEachRemaining(final Consumer<? super Integer> action) {
			i.forEachRemaining(action);
		}
	}

	/**
	 * Returns a big-list iterator backed by the specified list iterator.
	 *
	 * @param i the list iterator to adapted to the big-list-iterator interface.
	 * @return a big-list iterator backed by the specified list iterator.
	 */
	public static IntBigListIterator asBigListIterator(final IntListIterator i) {
		return new BigListIteratorListIterator(i);
	}

	/**
	 * A skeletal implementation for an iterator backed by an index based data store. High performance
	 * concrete implementations (like the main Iterator of BigArrayBigList) generally should avoid using
	 * this and just implement the interface directly, but should be decent for less performance
	 * critical implementations.
	 *
	 * <p>
	 * As the abstract methods in this class are used in inner loops, it is generally a good idea to
	 * override the class as {@code final} as to encourage the JVM to inline them (or alternatively,
	 * override the abstract methods as final).
	 */
	public static abstract class AbstractIndexBasedBigIterator extends AbstractIntIterator {
		/**
		 * The minimum pos can be, and is the logical start of the "range". Usually set to the initialPos
		 * unless it is a ListIterator, in which case it can vary.
		 *
		 * There isn't any way for a range to shift its beginning like the end can (through
		 * {@link #remove}), so this is final.
		 */
		protected final long minPos;
		/**
		 * The current position index, the index of the item to be returned after the next call to
		 * {@link #next()}.
		 *
		 * <p>
		 * This value will be between {@code minPos} and {@link #getMaxPos()} (exclusive) (on a best effort,
		 * so concurrent structural modifications outside this iterator may cause this to be violated, but
		 * that usually invalidates iterators anyways). Thus {@code pos} being {@code minPos + 2} would mean
		 * {@link #next()} was called twice and the next call will return the third element of this
		 * iterator.
		 */
		protected long pos;
		/**
		 * The last returned index by a call to {@link #next} or, if a list-iterator,
		 * {@link java.util.ListIterator#previous().
		 *
		 * Is {@code -1} if no such call has occurred or a mutation has occurred through this iterator and
		 * no advancement has been done.
		 */
		protected long lastReturned;

		protected AbstractIndexBasedBigIterator(long minPos, long initialPos) {
			this.minPos = minPos;
			this.pos = initialPos;
		}

		// When you implement these, you should probably declare them final to encourage the JVM to inline
		// them.
		/**
		 * Get the item corresponding to the given index location.
		 *
		 * <p>
		 * Do <em>not</em> advance {@link #pos} in this method; the default {@code next} method takes care
		 * of this.
		 *
		 * <p>
		 * The {@code location} given will be between {@code minPos} and {@link #getMaxPos()} (exclusive).
		 * Thus, a {@code location} of {@code minPos + 2} would mean {@link #next()} was called twice and
		 * this method should return what the next call to {@link #next()} should return.
		 */
		protected abstract int get(long location);

		/**
		 * Remove the item at the given index.
		 *
		 * <p>
		 * Do <em>not</em> modify {@link #pos} in this method; the default {@code #remove()} method takes
		 * care of this.
		 *
		 * <p>
		 * This method should also do what is needed to track the change to the {@link #getMaxPos}. Usually
		 * this is accomplished by having this method call the parent {@link Collection}'s appropriate
		 * remove method, and having {@link #getMaxPos} track the parent collection's {@code size64()}.
		 */
		protected abstract void remove(long location);

		/**
		 * The maximum pos can be, and is the logical end of the "range".
		 *
		 * <p>
		 * If pos is equal to the return of this method, this means the last element has been returned and
		 * the next call to {@link #next()} will throw.
		 *
		 * <p>
		 * Usually set return the parent collection's {@code size64()}, but does not have to be (for
		 * example, sublists and subranges).
		 */
		protected abstract long getMaxPos();

		@Override
		public boolean hasNext() {
			return pos < getMaxPos();
		}

		@Override
		public int nextInt() {
			if (!hasNext()) throw new NoSuchElementException();
			return get(lastReturned = pos++);
		}

		@Override
		public void remove() {
			if (lastReturned == -1) throw new IllegalStateException();
			remove(lastReturned);
			/* If the last operation was a next(), we are removing an element *before* us, and we must decrease pos correspondingly. */
			if (lastReturned < pos) pos--;
			lastReturned = -1;
		}

		@Override
		public void forEachRemaining(final java.util.function.IntConsumer action) {
			while (pos < getMaxPos()) {
				action.accept(get(lastReturned = pos++));
			}
		}

		public long skip(long n) {
			if (n < 0) throw new IllegalArgumentException("Argument must be nonnegative: " + n);
			final long max = getMaxPos();
			final long remaining = max - pos;
			if (n < remaining) {
				pos += n;
			} else {
				n = remaining;
				pos = max;
			}
			lastReturned = pos - 1;
			return n;
		}

		@Override
		public int skip(int n) {
			return SafeMath.safeLongToInt(skip((long)n));
		}
	}

	/**
	 * A skeletal implementation for a list-iterator backed by an index based data store. High
	 * performance concrete implementations (like the main ListIterator of ArrayList) generally should
	 * avoid using this and just implement the interface directly, but should be decent for less
	 * performance critical implementations.
	 *
	 * <p>
	 * As the abstract methods in this class are used in inner loops, it is generally a good idea to
	 * override the class as {@code final} as to encourage the JVM to inline them (or alternatively,
	 * override the abstract methods as final).
	 */
	public static abstract class AbstractIndexBasedBigListIterator extends AbstractIndexBasedBigIterator implements IntBigListIterator {
		protected AbstractIndexBasedBigListIterator(long minPos, long initialPos) {
			super(minPos, initialPos);
		}

		// When you implement these, you should probably declare them final to encourage the JVM to inline
		// them.
		/**
		 * Add the given item at the given index.
		 *
		 * <p>
		 * This method should also do what is needed to track the change to the {@link #getMaxPos}. Usually
		 * this is accomplished by having this method call the parent {@link Collection}'s appropriate add
		 * method, and having {@link #getMaxPos} track the parent {@linkplain Collection#size() collection's
		 * size}.
		 *
		 * <p>
		 * Do <em>not</em> modify {@link #pos} in this method; the default {@code #add()} method takes care
		 * of this.
		 *
		 * <p>
		 * See {@link #pos} and {@link #get(int)} for discussion on what the location means.
		 */
		protected abstract void add(long location, int k);

		/**
		 * Sets the given item at the given index.
		 *
		 * <p>
		 * See {@link #pos} and {@link #get(int)} for discussion on what the location means.
		 */
		protected abstract void set(long location, int k);

		@Override
		public boolean hasPrevious() {
			return pos > minPos;
		}

		@Override
		public int previousInt() {
			if (!hasPrevious()) throw new NoSuchElementException();
			return get(lastReturned = --pos);
		}

		@Override
		public long nextIndex() {
			return pos;
		}

		@Override
		public long previousIndex() {
			return pos - 1;
		}

		@Override
		public void add(final int k) {
			add(pos++, k);
			lastReturned = -1;
		}

		@Override
		public void set(final int k) {
			if (lastReturned == -1) throw new IllegalStateException();
			set(lastReturned, k);
		}

		// TODO since this method doesn't depend on the type at all, should it be "hoisted" into a
		// non type-specific superclass in it.unimi.dsi.fastutil?
		@Override
		public long back(long n) {
			if (n < 0) throw new IllegalArgumentException("Argument must be nonnegative: " + n);
			final long remaining = pos - minPos;
			if (n < remaining) {
				pos -= n;
			} else {
				n = remaining;
				pos = minPos;
			}
			lastReturned = pos;
			return n;
		}
	}
}
