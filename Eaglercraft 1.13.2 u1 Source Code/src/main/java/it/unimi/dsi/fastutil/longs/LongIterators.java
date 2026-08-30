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

import static it.unimi.dsi.fastutil.BigArrays.grow;
import static it.unimi.dsi.fastutil.BigArrays.length;
import static it.unimi.dsi.fastutil.BigArrays.set;
import static it.unimi.dsi.fastutil.BigArrays.trim;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.PrimitiveIterator;

/**
 * A class providing static methods and objects that do useful things with type-specific iterators.
 *
 * @see Iterator
 */
public final class LongIterators {
	private LongIterators() {
	}

	/**
	 * A class returning no elements and a type-specific iterator interface.
	 *
	 * <p>
	 * This class may be useful to implement your own in case you subclass a type-specific iterator.
	 */
	public static class EmptyIterator implements LongListIterator, java.io.Serializable, Cloneable {
		private static final long serialVersionUID = -7046029254386353129L;

		protected EmptyIterator() {
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
		public long nextLong() {
			throw new NoSuchElementException();
		}

		@Override
		public long previousLong() {
			throw new NoSuchElementException();
		}

		@Override
		public int nextIndex() {
			return 0;
		}

		@Override
		public int previousIndex() {
			return -1;
		}

		@Override
		public int skip(int n) {
			return 0;
		}

		@Override
		public int back(int n) {
			return 0;
		}

		@Override
		public void forEachRemaining(final java.util.function.LongConsumer action) {
		}

		@Deprecated
		@Override
		public void forEachRemaining(final Consumer<? super Long> action) {
		}

		@Override
		public Object clone() {
			return EMPTY_ITERATOR;
		}

		private Object readResolve() {
			return EMPTY_ITERATOR;
		}
	}

	/**
	 * An empty iterator. It is serializable and cloneable.
	 *
	 * <p>
	 * The class of this objects represent an abstract empty iterator that can iterate as a
	 * type-specific (list) iterator.
	 */

	public static final EmptyIterator EMPTY_ITERATOR = new EmptyIterator();

	/** An iterator returning a single element. */
	private static class SingletonIterator implements LongListIterator {
		private final long element;
		private byte curr;

		public SingletonIterator(final long element) {
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
		public long nextLong() {
			if (!hasNext()) throw new NoSuchElementException();
			curr = 1;
			return element;
		}

		@Override
		public long previousLong() {
			if (!hasPrevious()) throw new NoSuchElementException();
			curr = 0;
			return element;
		}

		@Override
		public void forEachRemaining(final java.util.function.LongConsumer action) {
			Objects.requireNonNull(action);
			if (curr == 0) {
				action.accept(element);
				curr = 1;
			}
		}

		@Override
		public int nextIndex() {
			return curr;
		}

		@Override
		public int previousIndex() {
			return curr - 1;
		}

		@Override
		public int back(int n) {
			if (n < 0) throw new IllegalArgumentException("Argument must be nonnegative: " + n);
			if (n == 0 || curr < 1) return 0;
			curr = 1;
			return 1;
		}

		@Override
		public int skip(int n) {
			if (n < 0) throw new IllegalArgumentException("Argument must be nonnegative: " + n);
			if (n == 0 || curr > 0) return 0;
			curr = 0;
			return 1;
		}
	}

	/**
	 * Returns an immutable iterator that iterates just over the given element.
	 *
	 * @param element the only element to be returned by a type-specific list iterator.
	 * @return an immutable iterator that iterates just over {@code element}.
	 */
	public static LongListIterator singleton(final long element) {
		return new SingletonIterator(element);
	}

	/** A class to wrap arrays in iterators. */
	private static class ArrayIterator implements LongListIterator {
		private final long[] array;
		private final int offset, length;
		private int curr;

		public ArrayIterator(final long[] array, final int offset, final int length) {
			this.array = array;
			this.offset = offset;
			this.length = length;
		}

		@Override
		public boolean hasNext() {
			return curr < length;
		}

		@Override
		public boolean hasPrevious() {
			return curr > 0;
		}

		@Override
		public long nextLong() {
			if (!hasNext()) throw new NoSuchElementException();
			return array[offset + curr++];
		}

		@Override
		public long previousLong() {
			if (!hasPrevious()) throw new NoSuchElementException();
			return array[offset + --curr];
		}

		@Override
		public void forEachRemaining(final java.util.function.LongConsumer action) {
			Objects.requireNonNull(action);
			for (; curr < length; ++curr) {
				action.accept(array[offset + curr]);
			}
		}

		@Override
		public int skip(int n) {
			if (n < 0) throw new IllegalArgumentException("Argument must be nonnegative: " + n);
			if (n <= length - curr) {
				curr += n;
				return n;
			}
			n = length - curr;
			curr = length;
			return n;
		}

		@Override
		public int back(int n) {
			if (n < 0) throw new IllegalArgumentException("Argument must be nonnegative: " + n);
			if (n <= curr) {
				curr -= n;
				return n;
			}
			n = curr;
			curr = 0;
			return n;
		}

		@Override
		public int nextIndex() {
			return curr;
		}

		@Override
		public int previousIndex() {
			return curr - 1;
		}
	}

	/**
	 * Wraps the given part of an array into a type-specific list iterator.
	 *
	 * <p>
	 * The type-specific list iterator returned by this method will iterate {@code length} times,
	 * returning consecutive elements of the given array starting from the one with index
	 * {@code offset}.
	 *
	 * @param array an array to wrap into a type-specific list iterator.
	 * @param offset the first element of the array to be returned.
	 * @param length the number of elements to return.
	 * @return an iterator that will return {@code length} elements of {@code array} starting at
	 *         position {@code offset}.
	 */
	public static LongListIterator wrap(final long[] array, final int offset, final int length) {
		LongArrays.ensureOffsetLength(array, offset, length);
		return new ArrayIterator(array, offset, length);
	}

	/**
	 * Wraps the given array into a type-specific list iterator.
	 *
	 * <p>
	 * The type-specific list iterator returned by this method will return all elements of the given
	 * array.
	 *
	 * @param array an array to wrap into a type-specific list iterator.
	 * @return an iterator that will return the elements of {@code array}.
	 */
	public static LongListIterator wrap(final long[] array) {
		return new ArrayIterator(array, 0, array.length);
	}

	/**
	 * Unwraps an iterator into an array starting at a given offset for a given number of elements.
	 *
	 * <p>
	 * This method iterates over the given type-specific iterator and stores the elements returned, up
	 * to a maximum of {@code length}, in the given array starting at {@code offset}. The number of
	 * actually unwrapped elements is returned (it may be less than {@code max} if the iterator emits
	 * less than {@code max} elements).
	 *
	 * @param i a type-specific iterator.
	 * @param array an array to contain the output of the iterator.
	 * @param offset the first element of the array to be returned.
	 * @param max the maximum number of elements to unwrap.
	 * @return the number of elements unwrapped.
	 */
	public static int unwrap(final LongIterator i, final long array[], int offset, final int max) {
		if (max < 0) throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
		if (offset < 0 || offset + max > array.length) throw new IllegalArgumentException();
		int j = max;
		while (j-- != 0 && i.hasNext()) array[offset++] = i.nextLong();
		return max - j - 1;
	}

	/**
	 * Unwraps an iterator into an array.
	 *
	 * <p>
	 * This method iterates over the given type-specific iterator and stores the elements returned in
	 * the given array. The iteration will stop when the iterator has no more elements or when the end
	 * of the array has been reached.
	 *
	 * @param i a type-specific iterator.
	 * @param array an array to contain the output of the iterator.
	 * @return the number of elements unwrapped.
	 */
	public static int unwrap(final LongIterator i, final long array[]) {
		return unwrap(i, array, 0, array.length);
	}

	/**
	 * Unwraps an iterator, returning an array, with a limit on the number of elements.
	 *
	 * <p>
	 * This method iterates over the given type-specific iterator and returns an array containing the
	 * elements returned by the iterator. At most {@code max} elements will be returned.
	 *
	 * @param i a type-specific iterator.
	 * @param max the maximum number of elements to be unwrapped.
	 * @return an array containing the elements returned by the iterator (at most {@code max}).
	 */

	public static long[] unwrap(final LongIterator i, int max) {
		if (max < 0) throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
		long array[] = new long[16];
		int j = 0;
		while (max-- != 0 && i.hasNext()) {
			if (j == array.length) array = LongArrays.grow(array, j + 1);
			array[j++] = i.nextLong();
		}
		return LongArrays.trim(array, j);
	}

	/**
	 * Unwraps an iterator, returning an array.
	 *
	 * <p>
	 * This method iterates over the given type-specific iterator and returns an array containing the
	 * elements returned by the iterator.
	 *
	 * @param i a type-specific iterator.
	 * @return an array containing the elements returned by the iterator.
	 */
	public static long[] unwrap(final LongIterator i) {
		return unwrap(i, Integer.MAX_VALUE);
	}

	/**
	 * Unwraps an iterator into a big array starting at a given offset for a given number of elements.
	 *
	 * <p>
	 * This method iterates over the given type-specific iterator and stores the elements returned, up
	 * to a maximum of {@code length}, in the given big array starting at {@code offset}. The number of
	 * actually unwrapped elements is returned (it may be less than {@code max} if the iterator emits
	 * less than {@code max} elements).
	 *
	 * @param i a type-specific iterator.
	 * @param array a big array to contain the output of the iterator.
	 * @param offset the first element of the array to be returned.
	 * @param max the maximum number of elements to unwrap.
	 * @return the number of elements unwrapped.
	 */
	public static long unwrap(final LongIterator i, final long array[][], long offset, final long max) {
		if (max < 0) throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
		if (offset < 0 || offset + max > length(array)) throw new IllegalArgumentException();
		long j = max;
		while (j-- != 0 && i.hasNext()) set(array, offset++, i.nextLong());
		return max - j - 1;
	}

	/**
	 * Unwraps an iterator into a big array.
	 *
	 * <p>
	 * This method iterates over the given type-specific iterator and stores the elements returned in
	 * the given big array. The iteration will stop when the iterator has no more elements or when the
	 * end of the array has been reached.
	 *
	 * @param i a type-specific iterator.
	 * @param array a big array to contain the output of the iterator.
	 * @return the number of elements unwrapped.
	 */
	public static long unwrap(final LongIterator i, final long array[][]) {
		return unwrap(i, array, 0, length(array));
	}

	/**
	 * Unwraps an iterator into a type-specific collection, with a limit on the number of elements.
	 *
	 * <p>
	 * This method iterates over the given type-specific iterator and stores the elements returned, up
	 * to a maximum of {@code max}, in the given type-specific collection. The number of actually
	 * unwrapped elements is returned (it may be less than {@code max} if the iterator emits less than
	 * {@code max} elements).
	 *
	 * @param i a type-specific iterator.
	 * @param c a type-specific collection array to contain the output of the iterator.
	 * @param max the maximum number of elements to unwrap.
	 * @return the number of elements unwrapped. Note that this is the number of elements returned by
	 *         the iterator, which is not necessarily the number of elements that have been added to the
	 *         collection (because of duplicates).
	 */
	public static int unwrap(final LongIterator i, final LongCollection c, final int max) {
		if (max < 0) throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
		int j = max;
		while (j-- != 0 && i.hasNext()) c.add(i.nextLong());
		return max - j - 1;
	}

	/**
	 * Unwraps an iterator, returning a big array, with a limit on the number of elements.
	 *
	 * <p>
	 * This method iterates over the given type-specific iterator and returns a big array containing the
	 * elements returned by the iterator. At most {@code max} elements will be returned.
	 *
	 * @param i a type-specific iterator.
	 * @param max the maximum number of elements to be unwrapped.
	 * @return a big array containing the elements returned by the iterator (at most {@code max}).
	 */

	public static long[][] unwrapBig(final LongIterator i, long max) {
		if (max < 0) throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
		long array[][] = LongBigArrays.newBigArray(16);
		long j = 0;
		while (max-- != 0 && i.hasNext()) {
			if (j == length(array)) array = grow(array, j + 1);
			set(array, j++, i.nextLong());
		}
		return trim(array, j);
	}

	/**
	 * Unwraps an iterator, returning a big array.
	 *
	 * <p>
	 * This method iterates over the given type-specific iterator and returns a big array containing the
	 * elements returned by the iterator.
	 *
	 * @param i a type-specific iterator.
	 * @return a big array containing the elements returned by the iterator.
	 */
	public static long[][] unwrapBig(final LongIterator i) {
		return unwrapBig(i, Long.MAX_VALUE);
	}

	/**
	 * Unwraps an iterator into a type-specific collection.
	 *
	 * <p>
	 * This method iterates over the given type-specific iterator and stores the elements returned in
	 * the given type-specific collection. The returned count on the number unwrapped elements is a
	 * long, so that it will work also with very large collections.
	 *
	 * @param i a type-specific iterator.
	 * @param c a type-specific collection to contain the output of the iterator.
	 * @return the number of elements unwrapped. Note that this is the number of elements returned by
	 *         the iterator, which is not necessarily the number of elements that have been added to the
	 *         collection (because of duplicates).
	 */
	public static long unwrap(final LongIterator i, final LongCollection c) {
		long n = 0;
		while (i.hasNext()) {
			c.add(i.nextLong());
			n++;
		}
		return n;
	}

	/**
	 * Pours an iterator into a type-specific collection, with a limit on the number of elements.
	 *
	 * <p>
	 * This method iterates over the given type-specific iterator and adds the returned elements to the
	 * given collection (up to {@code max}).
	 *
	 * @param i a type-specific iterator.
	 * @param s a type-specific collection.
	 * @param max the maximum number of elements to be poured.
	 * @return the number of elements poured. Note that this is the number of elements returned by the
	 *         iterator, which is not necessarily the number of elements that have been added to the
	 *         collection (because of duplicates).
	 */
	public static int pour(final LongIterator i, final LongCollection s, final int max) {
		if (max < 0) throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
		int j = max;
		while (j-- != 0 && i.hasNext()) s.add(i.nextLong());
		return max - j - 1;
	}

	/**
	 * Pours an iterator into a type-specific collection.
	 *
	 * <p>
	 * This method iterates over the given type-specific iterator and adds the returned elements to the
	 * given collection.
	 *
	 * @param i a type-specific iterator.
	 * @param s a type-specific collection.
	 * @return the number of elements poured. Note that this is the number of elements returned by the
	 *         iterator, which is not necessarily the number of elements that have been added to the
	 *         collection (because of duplicates).
	 */
	public static int pour(final LongIterator i, final LongCollection s) {
		return pour(i, s, Integer.MAX_VALUE);
	}

	/**
	 * Pours an iterator, returning a type-specific list, with a limit on the number of elements.
	 *
	 * <p>
	 * This method iterates over the given type-specific iterator and returns a type-specific list
	 * containing the returned elements (up to {@code max}). Iteration on the returned list is
	 * guaranteed to produce the elements in the same order in which they appeared in the iterator.
	 *
	 *
	 * @param i a type-specific iterator.
	 * @param max the maximum number of elements to be poured.
	 * @return a type-specific list containing the returned elements, up to {@code max}.
	 */
	public static LongList pour(final LongIterator i, int max) {
		final LongArrayList l = new LongArrayList();
		pour(i, l, max);
		l.trim();
		return l;
	}

	/**
	 * Pours an iterator, returning a type-specific list.
	 *
	 * <p>
	 * This method iterates over the given type-specific iterator and returns a list containing the
	 * returned elements. Iteration on the returned list is guaranteed to produce the elements in the
	 * same order in which they appeared in the iterator.
	 *
	 * @param i a type-specific iterator.
	 * @return a type-specific list containing the returned elements.
	 */
	public static LongList pour(final LongIterator i) {
		return pour(i, Integer.MAX_VALUE);
	}

	private static class IteratorWrapper implements LongIterator {
		final Iterator<Long> i;

		public IteratorWrapper(final Iterator<Long> i) {
			this.i = i;
		}

		@Override
		public boolean hasNext() {
			return i.hasNext();
		}

		@Override
		public void remove() {
			i.remove();
		}

		@Override
		public long nextLong() {
			return (i.next()).longValue();
		}

		// This is pretty much the only time overriding this overload is correct; we want to
		// delegate as an Object consumer, not wrap it as a primitive one.
		@Override
		public void forEachRemaining(final LongConsumer action) {
			i.forEachRemaining(action);
		}

		@SuppressWarnings("unchecked")
		@Override
		public void forEachRemaining(final java.util.function.LongConsumer action) {
			Objects.requireNonNull(action);
			i.forEachRemaining(action instanceof Consumer ? (Consumer<? super Long>)action : action::accept);
		}

		@Deprecated
		@Override
		public void forEachRemaining(final Consumer<? super Long> action) {
			i.forEachRemaining(action);
		}
	}

	private static class PrimitiveIteratorWrapper implements LongIterator {
		final PrimitiveIterator.OfLong i;

		public PrimitiveIteratorWrapper(PrimitiveIterator.OfLong i) {
			this.i = i;
		}

		@Override
		public boolean hasNext() {
			return i.hasNext();
		}

		@Override
		public void remove() {
			i.remove();
		}

		@Override
		public long nextLong() {
			return i.nextLong();
		}

		@Override
		public void forEachRemaining(final java.util.function.LongConsumer action) {
			i.forEachRemaining(action);
		}
	}

	/**
	 * Wraps a standard iterator into a type-specific iterator.
	 *
	 * <p>
	 * This method wraps a standard iterator into a type-specific one which will handle the type
	 * conversions for you. Of course, any attempt to wrap an iterator returning the instances of the
	 * wrong class will generate a {@link ClassCastException}. The returned iterator is backed by
	 * {@code i}: changes to one of the iterators will affect the other, too.
	 *
	 * @implNote If {@code i} is already type-specific, it will returned and no new object will be
	 *           generated.
	 *
	 * @param i an iterator.
	 * @return a type-specific iterator backed by {@code i}.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static LongIterator asLongIterator(final Iterator i) {
		if (i instanceof LongIterator) return (LongIterator)i;
		if (i instanceof PrimitiveIterator.OfLong) return new PrimitiveIteratorWrapper((PrimitiveIterator.OfLong)i);
		return new IteratorWrapper(i);
	}

	private static class ListIteratorWrapper implements LongListIterator {
		final ListIterator<Long> i;

		public ListIteratorWrapper(final ListIterator<Long> i) {
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
		public int nextIndex() {
			return i.nextIndex();
		}

		@Override
		public int previousIndex() {
			return i.previousIndex();
		}

		@Override
		public void set(long k) {
			i.set(Long.valueOf(k));
		}

		@Override
		public void add(long k) {
			i.add(Long.valueOf(k));
		}

		@Override
		public void remove() {
			i.remove();
		}

		@Override
		public long nextLong() {
			return (i.next()).longValue();
		}

		@Override
		public long previousLong() {
			return (i.previous()).longValue();
		}

		// This is pretty much the only time overriding this overload is correct; we want to
		// delegate as an Object consumer, not wrap it as a primitive one.
		@Override
		public void forEachRemaining(final LongConsumer action) {
			i.forEachRemaining(action);
		}

		@SuppressWarnings("unchecked")
		@Override
		public void forEachRemaining(final java.util.function.LongConsumer action) {
			Objects.requireNonNull(action);
			i.forEachRemaining(action instanceof Consumer ? (Consumer<? super Long>)action : action::accept);
		}

		@Deprecated
		@Override
		public void forEachRemaining(final Consumer<? super Long> action) {
			i.forEachRemaining(action);
		}
	}

	/**
	 * Wraps a standard list iterator into a type-specific list iterator.
	 *
	 * <p>
	 * This method wraps a standard list iterator into a type-specific one which will handle the type
	 * conversions for you. Of course, any attempt to wrap an iterator returning the instances of the
	 * wrong class will generate a {@link ClassCastException}. The returned iterator is backed by
	 * {@code i}: changes to one of the iterators will affect the other, too.
	 *
	 * <p>
	 * If {@code i} is already type-specific, it will returned and no new object will be generated.
	 *
	 * @param i a list iterator.
	 * @return a type-specific list iterator backed by {@code i}.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static LongListIterator asLongIterator(final ListIterator i) {
		if (i instanceof LongListIterator) return (LongListIterator)i;
		return new ListIteratorWrapper(i);
	}

	/**
	 * Returns whether an element returned by the given iterator satisfies the given predicate.
	 * <p>
	 * Short circuit evaluation is performed; the first {@code true} from the predicate terminates the
	 * loop.
	 * 
	 * @return true if an element returned by {@code iterator} satisfies {@code predicate}.
	 */
	public static boolean any(final LongIterator iterator, final java.util.function.LongPredicate predicate) {
		return indexOf(iterator, predicate) != -1;
	}

	/**
	 * Returns whether all elements returned by the given iterator satisfy the given predicate.
	 * <p>
	 * Short circuit evaluation is performed; the first {@code false} from the predicate terminates the
	 * loop.
	 * 
	 * @return true if all elements returned by {@code iterator} satisfy {@code predicate}.
	 */
	public static boolean all(final LongIterator iterator, final java.util.function.LongPredicate predicate) {
		Objects.requireNonNull(predicate);
		do {
			if (!iterator.hasNext()) return true;
		} while (predicate.test(iterator.nextLong()));
		return false;
	}

	/**
	 * Returns the index of the first element returned by the given iterator that satisfies the given
	 * predicate, or &minus;1 if no such element was found.
	 * <p>
	 * The next element returned by the iterator always considered element 0, even for
	 * {@link java.util.ListIterator ListIterators}. In other words
	 * {@link java.util.ListIterator#nextIndex ListIterator.nextIndex} is ignored.
	 * 
	 * @return the index of the first element returned by {@code iterator} that satisfies
	 *         {@code predicate}, or &minus;1 if no such element was found.
	 */
	public static int indexOf(final LongIterator iterator, final java.util.function.LongPredicate predicate) {
		Objects.requireNonNull(predicate);
		for (int i = 0; iterator.hasNext(); ++i) {
			if (predicate.test(iterator.nextLong())) return i;
		}
		return -1;
	}

	/**
	 * A skeletal implementation for an iterator backed by an index-based data store. High performance
	 * concrete implementations (like the main Iterator of ArrayList) generally should avoid using this
	 * and just implement the interface directly, but should be decent for less performance critical
	 * implementations.
	 *
	 * <p>
	 * This class is only appropriate for sequences that are at most {@link Integer#MAX_VALUE} long. If
	 * your backing data store can be bigger then this, consider the equivalently named class in the
	 * type specific {@code BigListIterators} class.
	 *
	 * <p>
	 * As the abstract methods in this class are used in inner loops, it is generally a good idea to
	 * override the class as {@code final} as to encourage the JVM to inline them (or alternatively,
	 * override the abstract methods as final).
	 */
	public static abstract class AbstractIndexBasedIterator extends AbstractLongIterator {
		/**
		 * The minimum pos can be, and is the logical start of the "range". Usually set to the initialPos
		 * unless it is a ListIterator, in which case it can vary.
		 *
		 * There isn't any way for a range to shift its beginning like the end can (through
		 * {@link #remove}), so this is final.
		 */
		protected final int minPos;
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
		protected int pos;
		/**
		 * The last returned index by a call to {@link #next} or, if a list-iterator,
		 * {@link java.util.ListIterator#previous().
		 *
		 * It is &minus;1 if no such call has occurred or a mutation has occurred through this iterator and
		 * no advancement has been done.
		 */
		protected int lastReturned;

		protected AbstractIndexBasedIterator(int minPos, int initialPos) {
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
		protected abstract long get(int location);

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
		 * remove method, and having {@link #getMaxPos} track the parent {@linkplain Collection#size()
		 * collection's size}.
		 */
		protected abstract void remove(int location);

		/**
		 * The maximum pos can be, and is the logical end (exclusive) of the "range".
		 *
		 * <p>
		 * If pos is equal to the return of this method, this means the last element has been returned and
		 * the next call to {@link #next()} will throw.
		 *
		 * <p>
		 * Usually set return the parent {@linkplain Collection#size() collection's size}, but does not have
		 * to be (for example, sublists and subranges).
		 */
		protected abstract int getMaxPos();

		@Override
		public boolean hasNext() {
			return pos < getMaxPos();
		}

		@Override
		public long nextLong() {
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
		public void forEachRemaining(final java.util.function.LongConsumer action) {
			while (pos < getMaxPos()) {
				action.accept(get(lastReturned = pos++));
			}
		}

		// TODO since this method doesn't depend on the type at all, should it be "hoisted" into a
		// non type-specific superclass in it.unimi.dsi.fastutil?
		@Override
		public int skip(int n) {
			if (n < 0) throw new IllegalArgumentException("Argument must be nonnegative: " + n);
			final int max = getMaxPos();
			final int remaining = max - pos;
			if (n < remaining) {
				pos += n;
			} else {
				n = remaining;
				pos = max;
			}
			lastReturned = pos - 1;
			return n;
		}
	}

	/**
	 * A skeletal implementation for a list-iterator backed by an index-based data store. High
	 * performance concrete implementations (like the main ListIterator of ArrayList) generally should
	 * avoid using this and just implement the interface directly, but should be decent for less
	 * performance critical implementations.
	 *
	 * <p>
	 * This class is only appropriate for sequences that are at most {@link Integer#MAX_VALUE} long. If
	 * your backing data store can be bigger then this, consider the equivalently named class in the
	 * type specific {@code BigListSpliterators} class.
	 *
	 * <p>
	 * As the abstract methods in this class are used in inner loops, it is generally a good idea to
	 * override the class as {@code final} as to encourage the JVM to inline them (or alternatively,
	 * override the abstract methods as final).
	 */
	public static abstract class AbstractIndexBasedListIterator extends AbstractIndexBasedIterator implements LongListIterator {
		protected AbstractIndexBasedListIterator(int minPos, int initialPos) {
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
		protected abstract void add(int location, long k);

		/**
		 * Sets the given item at the given index.
		 *
		 * <p>
		 * See {@link #pos} and {@link #get(int)} for discussion on what the location means.
		 */
		protected abstract void set(int location, long k);

		@Override
		public boolean hasPrevious() {
			return pos > minPos;
		}

		@Override
		public long previousLong() {
			if (!hasPrevious()) throw new NoSuchElementException();
			return get(lastReturned = --pos);
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
		public void add(final long k) {
			add(pos++, k);
			lastReturned = -1;
		}

		@Override
		public void set(final long k) {
			if (lastReturned == -1) throw new IllegalStateException();
			set(lastReturned, k);
		}

		// TODO since this method doesn't depend on the type at all, should it be "hoisted" into a
		// non type-specific superclass in it.unimi.dsi.fastutil?
		@Override
		public int back(int n) {
			if (n < 0) throw new IllegalArgumentException("Argument must be nonnegative: " + n);
			final int remaining = pos - minPos;
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

	private static class IntervalIterator implements LongBidirectionalIterator {
		private final long from, to;
		long curr;

		public IntervalIterator(final long from, final long to) {
			this.from = this.curr = from;
			this.to = to;
		}

		@Override
		public boolean hasNext() {
			return curr < to;
		}

		@Override
		public boolean hasPrevious() {
			return curr > from;
		}

		@Override
		public long nextLong() {
			if (!hasNext()) throw new NoSuchElementException();
			return curr++;
		}

		@Override
		public long previousLong() {
			if (!hasPrevious()) throw new NoSuchElementException();
			return --curr;
		}

		@Override
		public void forEachRemaining(final java.util.function.LongConsumer action) {
			Objects.requireNonNull(action);
			for (; curr < to; ++curr) {
				action.accept(curr);
			}
		}

		@Override
		public int skip(int n) {
			if (n < 0) throw new IllegalArgumentException("Argument must be nonnegative: " + n);
			if (curr + n <= to) {
				curr += n;
				return n;
			}
			n = (int)(to - curr);
			curr = to;
			return n;
		}

		@Override
		public int back(int n) {
			if (curr - n >= from) {
				curr -= n;
				return n;
			}
			n = (int)(curr - from);
			curr = from;
			return n;
		}
	}

	/**
	 * Creates a type-specific bidirectional iterator over an interval.
	 *
	 * <p>
	 * The type-specific bidirectional iterator returned by this method will return the elements
	 * {@code from}, {@code from+1},&hellip;, {@code to-1}.
	 *
	 * <p>
	 * Note that all other type-specific interval iterator are <em>list</em> iterators. Of course, this
	 * is not possible with longs as the index returned by {@link java.util.ListIterator#nextIndex()
	 * nextIndex()}/{@link java.util.ListIterator#previousIndex() previousIndex()} would exceed an
	 * integer.
	 *
	 * @param from the starting element (inclusive).
	 * @param to the ending element (exclusive).
	 * @return a type-specific bidirectional iterator enumerating the elements from {@code from} to
	 *         {@code to}.
	 */
	public static LongBidirectionalIterator fromTo(final long from, final long to) {
		return new IntervalIterator(from, to);
	}

	private static class IteratorConcatenator implements LongIterator {
		final LongIterator a[];
		int offset, length, lastOffset = -1;

		public IteratorConcatenator(final LongIterator a[], int offset, int length) {
			this.a = a;
			this.offset = offset;
			this.length = length;
			advance();
		}

		private void advance() {
			while (length != 0) {
				if (a[offset].hasNext()) break;
				length--;
				offset++;
			}
			return;
		}

		@Override
		public boolean hasNext() {
			return length > 0;
		}

		@Override
		public long nextLong() {
			if (!hasNext()) throw new NoSuchElementException();
			long next = a[lastOffset = offset].nextLong();
			advance();
			return next;
		}

		@Override
		public void forEachRemaining(final java.util.function.LongConsumer action) {
			while (length > 0) {
				a[lastOffset = offset].forEachRemaining(action);
				advance();
			}
		}

		@Deprecated
		@Override
		public void forEachRemaining(final Consumer<? super Long> action) {
			while (length > 0) {
				a[lastOffset = offset].forEachRemaining(action);
				advance();
			}
		}

		@Override
		public void remove() {
			if (lastOffset == -1) throw new IllegalStateException();
			a[lastOffset].remove();
		}

		@Override
		public int skip(int n) {
			if (n < 0) throw new IllegalArgumentException("Argument must be nonnegative: " + n);
			lastOffset = -1;
			int skipped = 0;
			while (skipped < n && length != 0) {
				skipped += a[offset].skip(n - skipped);
				if (a[offset].hasNext()) break;
				length--;
				offset++;
			}
			return skipped;
		}
	}

	/**
	 * Concatenates all iterators contained in an array.
	 *
	 * <p>
	 * This method returns an iterator that will enumerate in order the elements returned by all
	 * iterators contained in the given array.
	 *
	 * @param a an array of iterators.
	 * @return an iterator obtained by concatenation.
	 */
	public static LongIterator concat(final LongIterator... a) {
		return concat(a, 0, a.length);
	}

	/**
	 * Concatenates a sequence of iterators contained in an array.
	 *
	 * <p>
	 * This method returns an iterator that will enumerate in order the elements returned by
	 * {@code a[offset]}, then those returned by {@code a[offset + 1]}, and so on up to
	 * {@code a[offset + length - 1]}.
	 *
	 * @param a an array of iterators.
	 * @param offset the index of the first iterator to concatenate.
	 * @param length the number of iterators to concatenate.
	 * @return an iterator obtained by concatenation of {@code length} elements of {@code a} starting at
	 *         {@code offset}.
	 */
	public static LongIterator concat(final LongIterator a[], final int offset, final int length) {
		return new IteratorConcatenator(a, offset, length);
	}

	/** An unmodifiable wrapper class for iterators. */
	public static class UnmodifiableIterator implements LongIterator {
		protected final LongIterator i;

		public UnmodifiableIterator(final LongIterator i) {
			this.i = i;
		}

		@Override
		public boolean hasNext() {
			return i.hasNext();
		}

		@Override
		public long nextLong() {
			return i.nextLong();
		}

		@Override
		public void forEachRemaining(final java.util.function.LongConsumer action) {
			i.forEachRemaining(action);
		}

		@Deprecated
		@Override
		public void forEachRemaining(final Consumer<? super Long> action) {
			i.forEachRemaining(action);
		}
	}

	/**
	 * Returns an unmodifiable iterator backed by the specified iterator.
	 *
	 * @param i the iterator to be wrapped in an unmodifiable iterator.
	 * @return an unmodifiable view of the specified iterator.
	 */
	public static LongIterator unmodifiable(final LongIterator i) {
		return new UnmodifiableIterator(i);
	}

	/** An unmodifiable wrapper class for bidirectional iterators. */
	public static class UnmodifiableBidirectionalIterator implements LongBidirectionalIterator {
		protected final LongBidirectionalIterator i;

		public UnmodifiableBidirectionalIterator(final LongBidirectionalIterator i) {
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
		public long nextLong() {
			return i.nextLong();
		}

		@Override
		public long previousLong() {
			return i.previousLong();
		}

		@Override
		public void forEachRemaining(final java.util.function.LongConsumer action) {
			i.forEachRemaining(action);
		}

		@Deprecated
		@Override
		public void forEachRemaining(final Consumer<? super Long> action) {
			i.forEachRemaining(action);
		}
	}

	/**
	 * Returns an unmodifiable bidirectional iterator backed by the specified bidirectional iterator.
	 *
	 * @param i the bidirectional iterator to be wrapped in an unmodifiable bidirectional iterator.
	 * @return an unmodifiable view of the specified bidirectional iterator.
	 */
	public static LongBidirectionalIterator unmodifiable(final LongBidirectionalIterator i) {
		return new UnmodifiableBidirectionalIterator(i);
	}

	/** An unmodifiable wrapper class for list iterators. */
	public static class UnmodifiableListIterator implements LongListIterator {
		protected final LongListIterator i;

		public UnmodifiableListIterator(final LongListIterator i) {
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
		public long nextLong() {
			return i.nextLong();
		}

		@Override
		public long previousLong() {
			return i.previousLong();
		}

		@Override
		public int nextIndex() {
			return i.nextIndex();
		}

		@Override
		public int previousIndex() {
			return i.previousIndex();
		}

		@Override
		public void forEachRemaining(final java.util.function.LongConsumer action) {
			i.forEachRemaining(action);
		}

		@Deprecated
		@Override
		public void forEachRemaining(final Consumer<? super Long> action) {
			i.forEachRemaining(action);
		}
	}

	/**
	 * Returns an unmodifiable list iterator backed by the specified list iterator.
	 *
	 * @param i the list iterator to be wrapped in an unmodifiable list iterator.
	 * @return an unmodifiable view of the specified list iterator.
	 */
	public static LongListIterator unmodifiable(final LongListIterator i) {
		return new UnmodifiableListIterator(i);
	}

	/** A wrapper promoting the results of a ByteIterator. */
	private final static class ByteIteratorWrapper implements LongIterator {
		final it.unimi.dsi.fastutil.bytes.ByteIterator iterator;

		public ByteIteratorWrapper(final it.unimi.dsi.fastutil.bytes.ByteIterator iterator) {
			this.iterator = iterator;
		}

		@Override
		public boolean hasNext() {
			return iterator.hasNext();
		}

		@Deprecated
		@Override
		public Long next() {
			return Long.valueOf(iterator.nextByte());
		}

		@Override
		public long nextLong() {
			return iterator.nextByte();
		}

		@Override
		public void forEachRemaining(final java.util.function.LongConsumer action) {
			Objects.requireNonNull(action);
			iterator.forEachRemaining(action::accept);
		}

		@Override
		public void remove() {
			iterator.remove();
		}

		@Override
		public int skip(final int n) {
			return iterator.skip(n);
		}
	}

	/**
	 * Returns an iterator backed by the specified byte iterator.
	 * 
	 * @param iterator a byte iterator.
	 * @return an iterator backed by the specified byte iterator.
	 */
	public static LongIterator wrap(final it.unimi.dsi.fastutil.bytes.ByteIterator iterator) {
		return new ByteIteratorWrapper(iterator);
	}

	/** A wrapper promoting the results of a ShortIterator. */
	private final static class ShortIteratorWrapper implements LongIterator {
		final it.unimi.dsi.fastutil.shorts.ShortIterator iterator;

		public ShortIteratorWrapper(final it.unimi.dsi.fastutil.shorts.ShortIterator iterator) {
			this.iterator = iterator;
		}

		@Override
		public boolean hasNext() {
			return iterator.hasNext();
		}

		@Deprecated
		@Override
		public Long next() {
			return Long.valueOf(iterator.nextShort());
		}

		@Override
		public long nextLong() {
			return iterator.nextShort();
		}

		@Override
		public void forEachRemaining(final java.util.function.LongConsumer action) {
			Objects.requireNonNull(action);
			iterator.forEachRemaining(action::accept);
		}

		@Override
		public void remove() {
			iterator.remove();
		}

		@Override
		public int skip(final int n) {
			return iterator.skip(n);
		}
	}

	/**
	 * Returns an iterator backed by the specified short iterator.
	 * 
	 * @param iterator a short iterator.
	 * @return an iterator backed by the specified short iterator.
	 */
	public static LongIterator wrap(final it.unimi.dsi.fastutil.shorts.ShortIterator iterator) {
		return new ShortIteratorWrapper(iterator);
	}

	/** A wrapper promoting the results of a CharIterator. */
	private final static class CharIteratorWrapper implements LongIterator {
		final it.unimi.dsi.fastutil.chars.CharIterator iterator;

		public CharIteratorWrapper(final it.unimi.dsi.fastutil.chars.CharIterator iterator) {
			this.iterator = iterator;
		}

		@Override
		public boolean hasNext() {
			return iterator.hasNext();
		}

		@Deprecated
		@Override
		public Long next() {
			return Long.valueOf(iterator.nextChar());
		}

		@Override
		public long nextLong() {
			return iterator.nextChar();
		}

		@Override
		public void forEachRemaining(final java.util.function.LongConsumer action) {
			Objects.requireNonNull(action);
			iterator.forEachRemaining(action::accept);
		}

		@Override
		public void remove() {
			iterator.remove();
		}

		@Override
		public int skip(final int n) {
			return iterator.skip(n);
		}
	}

	/**
	 * Returns an iterator backed by the specified char iterator.
	 *
	 * <p>
	 * <b>WARNING</b>: This is <em>not</em> the same as converting the source to a sequence of code
	 * points. This returned instance literally performs {@code (int)(charValue)} casts. Surrogate pairs
	 * will be left as separate elements instead of combined into a single element with the code point
	 * it represents. See {@link Character} for more discussion on code points, char values, and
	 * surrogate pairs.
	 *
	 * @param iterator a char iterator.
	 * @return an iterator backed by the specified char iterator.
	 */
	public static LongIterator wrap(final it.unimi.dsi.fastutil.chars.CharIterator iterator) {
		return new CharIteratorWrapper(iterator);
	}

	/** A wrapper promoting the results of an IntIterator. */
	private final static class IntIteratorWrapper implements LongIterator {
		final it.unimi.dsi.fastutil.ints.IntIterator iterator;

		public IntIteratorWrapper(final it.unimi.dsi.fastutil.ints.IntIterator iterator) {
			this.iterator = iterator;
		}

		@Override
		public boolean hasNext() {
			return iterator.hasNext();
		}

		@Deprecated
		@Override
		public Long next() {
			return Long.valueOf(iterator.nextInt());
		}

		@Override
		public long nextLong() {
			return iterator.nextInt();
		}

		@Override
		public void forEachRemaining(final java.util.function.LongConsumer action) {
			Objects.requireNonNull(action);
			iterator.forEachRemaining(action::accept);
		}

		@Override
		public void remove() {
			iterator.remove();
		}

		@Override
		public int skip(final int n) {
			return iterator.skip(n);
		}
	}

	/**
	 * Returns an iterator backed by the specified integer iterator.
	 * 
	 * @param iterator an integer iterator.
	 * @return an iterator backed by the specified integer iterator.
	 */
	public static LongIterator wrap(final it.unimi.dsi.fastutil.ints.IntIterator iterator) {
		return new IntIteratorWrapper(iterator);
	}
}
