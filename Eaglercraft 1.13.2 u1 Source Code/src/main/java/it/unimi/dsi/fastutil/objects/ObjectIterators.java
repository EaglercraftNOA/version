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
package it.unimi.dsi.fastutil.objects;

import static it.unimi.dsi.fastutil.BigArrays.grow;
import static it.unimi.dsi.fastutil.BigArrays.length;
import static it.unimi.dsi.fastutil.BigArrays.set;
import static it.unimi.dsi.fastutil.BigArrays.trim;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * A class providing static methods and objects that do useful things with type-specific iterators.
 *
 * @see Iterator
 */
public final class ObjectIterators {
	private ObjectIterators() {
	}

	/**
	 * A class returning no elements and a type-specific iterator interface.
	 *
	 * <p>
	 * This class may be useful to implement your own in case you subclass a type-specific iterator.
	 */
	public static class EmptyIterator<K> implements ObjectListIterator<K>, java.io.Serializable, Cloneable {
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
		public K next() {
			throw new NoSuchElementException();
		}

		@Override
		public K previous() {
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
		public void forEachRemaining(final Consumer<? super K> action) {
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
	@SuppressWarnings("rawtypes")
	public static final EmptyIterator EMPTY_ITERATOR = new EmptyIterator();

	/**
	 * Returns an empty iterator. It is serializable and cloneable.
	 *
	 * <p>
	 * The class of the object returned represent an abstract empty iterator that can iterate as a
	 * type-specific (list) iterator.
	 *
	 * <p>
	 * This method provides a typesafe access to {@link #EMPTY_ITERATOR}.
	 * 
	 * @return an empty iterator.
	 */
	@SuppressWarnings("unchecked")
	public static <K> ObjectIterator<K> emptyIterator() {
		return EMPTY_ITERATOR;
	}

	/** An iterator returning a single element. */
	private static class SingletonIterator<K> implements ObjectListIterator<K> {
		private final K element;
		private byte curr;

		public SingletonIterator(final K element) {
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
		public K next() {
			if (!hasNext()) throw new NoSuchElementException();
			curr = 1;
			return element;
		}

		@Override
		public K previous() {
			if (!hasPrevious()) throw new NoSuchElementException();
			curr = 0;
			return element;
		}

		@Override
		public void forEachRemaining(final Consumer<? super K> action) {
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
	public static <K> ObjectListIterator<K> singleton(final K element) {
		return new SingletonIterator<>(element);
	}

	/** A class to wrap arrays in iterators. */
	private static class ArrayIterator<K> implements ObjectListIterator<K> {
		private final K[] array;
		private final int offset, length;
		private int curr;

		public ArrayIterator(final K[] array, final int offset, final int length) {
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
		public K next() {
			if (!hasNext()) throw new NoSuchElementException();
			return array[offset + curr++];
		}

		@Override
		public K previous() {
			if (!hasPrevious()) throw new NoSuchElementException();
			return array[offset + --curr];
		}

		@Override
		public void forEachRemaining(final Consumer<? super K> action) {
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
	public static <K> ObjectListIterator<K> wrap(final K[] array, final int offset, final int length) {
		ObjectArrays.ensureOffsetLength(array, offset, length);
		return new ArrayIterator<>(array, offset, length);
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
	public static <K> ObjectListIterator<K> wrap(final K[] array) {
		return new ArrayIterator<>(array, 0, array.length);
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
	public static <K> int unwrap(final Iterator<? extends K> i, final K array[], int offset, final int max) {
		if (max < 0) throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
		if (offset < 0 || offset + max > array.length) throw new IllegalArgumentException();
		int j = max;
		while (j-- != 0 && i.hasNext()) array[offset++] = i.next();
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
	public static <K> int unwrap(final Iterator<? extends K> i, final K array[]) {
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
	@SuppressWarnings("unchecked")
	public static <K> K[] unwrap(final Iterator<? extends K> i, int max) {
		if (max < 0) throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
		K array[] = (K[])new Object[16];
		int j = 0;
		while (max-- != 0 && i.hasNext()) {
			if (j == array.length) array = ObjectArrays.grow(array, j + 1);
			array[j++] = i.next();
		}
		return ObjectArrays.trim(array, j);
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
	public static <K> K[] unwrap(final Iterator<? extends K> i) {
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
	public static <K> long unwrap(final Iterator<? extends K> i, final K array[][], long offset, final long max) {
		if (max < 0) throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
		if (offset < 0 || offset + max > length(array)) throw new IllegalArgumentException();
		long j = max;
		while (j-- != 0 && i.hasNext()) set(array, offset++, i.next());
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
	public static <K> long unwrap(final Iterator<? extends K> i, final K array[][]) {
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
	public static <K> int unwrap(final Iterator<K> i, final ObjectCollection<? super K> c, final int max) {
		if (max < 0) throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
		int j = max;
		while (j-- != 0 && i.hasNext()) c.add(i.next());
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
	@SuppressWarnings("unchecked")
	public static <K> K[][] unwrapBig(final Iterator<? extends K> i, long max) {
		if (max < 0) throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
		K array[][] = (K[][])ObjectBigArrays.newBigArray(16);
		long j = 0;
		while (max-- != 0 && i.hasNext()) {
			if (j == length(array)) array = grow(array, j + 1);
			set(array, j++, i.next());
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
	public static <K> K[][] unwrapBig(final Iterator<? extends K> i) {
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
	public static <K> long unwrap(final Iterator<K> i, final ObjectCollection<? super K> c) {
		long n = 0;
		while (i.hasNext()) {
			c.add(i.next());
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
	public static <K> int pour(final Iterator<K> i, final ObjectCollection<? super K> s, final int max) {
		if (max < 0) throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
		int j = max;
		while (j-- != 0 && i.hasNext()) s.add(i.next());
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
	public static <K> int pour(final Iterator<K> i, final ObjectCollection<? super K> s) {
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
	public static <K> ObjectList<K> pour(final Iterator<K> i, int max) {
		final ObjectArrayList<K> l = new ObjectArrayList<>();
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
	public static <K> ObjectList<K> pour(final Iterator<K> i) {
		return pour(i, Integer.MAX_VALUE);
	}

	private static class IteratorWrapper<K> implements ObjectIterator<K> {
		final Iterator<K> i;

		public IteratorWrapper(final Iterator<K> i) {
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
		public K next() {
			return (i.next());
		}

		@Override
		public void forEachRemaining(final Consumer<? super K> action) {
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
	public static <K> ObjectIterator<K> asObjectIterator(final Iterator<K> i) {
		if (i instanceof ObjectIterator) return (ObjectIterator<K>)i;
		return new IteratorWrapper<>(i);
	}

	private static class ListIteratorWrapper<K> implements ObjectListIterator<K> {
		final ListIterator<K> i;

		public ListIteratorWrapper(final ListIterator<K> i) {
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
		public void set(K k) {
			i.set((k));
		}

		@Override
		public void add(K k) {
			i.add((k));
		}

		@Override
		public void remove() {
			i.remove();
		}

		@Override
		public K next() {
			return (i.next());
		}

		@Override
		public K previous() {
			return (i.previous());
		}

		@Override
		public void forEachRemaining(final Consumer<? super K> action) {
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
	public static <K> ObjectListIterator<K> asObjectIterator(final ListIterator<K> i) {
		if (i instanceof ObjectListIterator) return (ObjectListIterator<K>)i;
		return new ListIteratorWrapper<>(i);
	}

	/**
	 * Returns whether an element returned by the given iterator satisfies the given predicate.
	 * <p>
	 * Short circuit evaluation is performed; the first {@code true} from the predicate terminates the
	 * loop.
	 * 
	 * @return true if an element returned by {@code iterator} satisfies {@code predicate}.
	 */
	public static <K> boolean any(final Iterator<K> iterator, final Predicate<? super K> predicate) {
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
	public static <K> boolean all(final Iterator<K> iterator, final Predicate<? super K> predicate) {
		Objects.requireNonNull(predicate);
		do {
			if (!iterator.hasNext()) return true;
		} while (predicate.test(iterator.next()));
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
	public static <K> int indexOf(final Iterator<K> iterator, final Predicate<? super K> predicate) {
		Objects.requireNonNull(predicate);
		for (int i = 0; iterator.hasNext(); ++i) {
			if (predicate.test(iterator.next())) return i;
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
	public static abstract class AbstractIndexBasedIterator<K> extends AbstractObjectIterator<K> {
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
		protected abstract K get(int location);

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
		public K next() {
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
		public void forEachRemaining(final Consumer<? super K> action) {
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
	public static abstract class AbstractIndexBasedListIterator<K> extends AbstractIndexBasedIterator<K> implements ObjectListIterator<K> {
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
		protected abstract void add(int location, K k);

		/**
		 * Sets the given item at the given index.
		 *
		 * <p>
		 * See {@link #pos} and {@link #get(int)} for discussion on what the location means.
		 */
		protected abstract void set(int location, K k);

		@Override
		public boolean hasPrevious() {
			return pos > minPos;
		}

		@Override
		public K previous() {
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
		public void add(final K k) {
			add(pos++, k);
			lastReturned = -1;
		}

		@Override
		public void set(final K k) {
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

	private static class IteratorConcatenator<K> implements ObjectIterator<K> {
		final ObjectIterator<? extends K> a[];
		int offset, length, lastOffset = -1;

		public IteratorConcatenator(final ObjectIterator<? extends K> a[], int offset, int length) {
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
		public K next() {
			if (!hasNext()) throw new NoSuchElementException();
			K next = a[lastOffset = offset].next();
			advance();
			return next;
		}

		@Override
		public void forEachRemaining(final Consumer<? super K> action) {
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
	@SafeVarargs // Spliterators can only give K, never consume them, making this safe.
	public static <K> ObjectIterator<K> concat(final ObjectIterator<? extends K>... a) {
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
	public static <K> ObjectIterator<K> concat(final ObjectIterator<? extends K> a[], final int offset, final int length) {
		return new IteratorConcatenator<>(a, offset, length);
	}

	/** An unmodifiable wrapper class for iterators. */
	public static class UnmodifiableIterator<K> implements ObjectIterator<K> {
		protected final ObjectIterator<? extends K> i;

		public UnmodifiableIterator(final ObjectIterator<? extends K> i) {
			this.i = i;
		}

		@Override
		public boolean hasNext() {
			return i.hasNext();
		}

		@Override
		public K next() {
			return i.next();
		}

		@Override
		public void forEachRemaining(final Consumer<? super K> action) {
			i.forEachRemaining(action);
		}
	}

	/**
	 * Returns an unmodifiable iterator backed by the specified iterator.
	 *
	 * @param i the iterator to be wrapped in an unmodifiable iterator.
	 * @return an unmodifiable view of the specified iterator.
	 */
	public static <K> ObjectIterator<K> unmodifiable(final ObjectIterator<? extends K> i) {
		return new UnmodifiableIterator<>(i);
	}

	/** An unmodifiable wrapper class for bidirectional iterators. */
	public static class UnmodifiableBidirectionalIterator<K> implements ObjectBidirectionalIterator<K> {
		protected final ObjectBidirectionalIterator<? extends K> i;

		public UnmodifiableBidirectionalIterator(final ObjectBidirectionalIterator<? extends K> i) {
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
		public K next() {
			return i.next();
		}

		@Override
		public K previous() {
			return i.previous();
		}

		@Override
		public void forEachRemaining(final Consumer<? super K> action) {
			i.forEachRemaining(action);
		}
	}

	/**
	 * Returns an unmodifiable bidirectional iterator backed by the specified bidirectional iterator.
	 *
	 * @param i the bidirectional iterator to be wrapped in an unmodifiable bidirectional iterator.
	 * @return an unmodifiable view of the specified bidirectional iterator.
	 */
	public static <K> ObjectBidirectionalIterator<K> unmodifiable(final ObjectBidirectionalIterator<? extends K> i) {
		return new UnmodifiableBidirectionalIterator<>(i);
	}

	/** An unmodifiable wrapper class for list iterators. */
	public static class UnmodifiableListIterator<K> implements ObjectListIterator<K> {
		protected final ObjectListIterator<? extends K> i;

		public UnmodifiableListIterator(final ObjectListIterator<? extends K> i) {
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
		public K next() {
			return i.next();
		}

		@Override
		public K previous() {
			return i.previous();
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
		public void forEachRemaining(final Consumer<? super K> action) {
			i.forEachRemaining(action);
		}
	}

	/**
	 * Returns an unmodifiable list iterator backed by the specified list iterator.
	 *
	 * @param i the list iterator to be wrapped in an unmodifiable list iterator.
	 * @return an unmodifiable view of the specified list iterator.
	 */
	public static <K> ObjectListIterator<K> unmodifiable(final ObjectListIterator<? extends K> i) {
		return new UnmodifiableListIterator<>(i);
	}
}
