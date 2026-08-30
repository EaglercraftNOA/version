/*
	* Copyright (C) 2007-2024 Sebastiano Vigna
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
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.Consumer;

/**
 * A simple, brute-force implementation of a set based on a backing array.
 *
 * <p>
 * The main purpose of this implementation is that of wrapping cleanly the brute-force approach to
 * the storage of a very small number of items: just put them into an array and scan linearly to
 * find an item.
 */
public class ObjectArraySet<K> extends AbstractObjectSet<K> implements java.io.Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	/** The backing array (valid up to {@link #size}, excluded). */
	protected transient Object[] a;
	/** The number of valid entries in {@link #a}. */
	protected int size;

	/**
	 * Creates a new array set using the given backing array. The resulting set will have as many
	 * elements as the array.
	 *
	 * <p>
	 * It is the responsibility of the caller to ensure that the elements of {@code a} are distinct.
	 *
	 * @param a the backing array.
	 */
	public ObjectArraySet(final Object[] a) {
		this.a = a;
		size = a.length;
	}

	/**
	 * Creates a new empty array set.
	 */
	public ObjectArraySet() {
		this.a = ObjectArrays.EMPTY_ARRAY;
	}

	/**
	 * Creates a new empty array set of given initial capacity.
	 *
	 * @param capacity the initial capacity.
	 */
	public ObjectArraySet(final int capacity) {
		this.a = new Object[capacity];
	}

	/**
	 * Creates a new array set copying the contents of a given collection.
	 * 
	 * @param c a collection.
	 */
	public ObjectArraySet(ObjectCollection<K> c) {
		this(c.size());
		addAll(c);
	}

	/**
	 * Creates a new array set copying the contents of a given set.
	 * 
	 * @param c a collection.
	 */
	public ObjectArraySet(final Collection<? extends K> c) {
		this(c.size());
		addAll(c);
	}

	/**
	 * Creates a new array set copying the contents of a given collection.
	 * 
	 * @param c a collection.
	 */
	public ObjectArraySet(ObjectSet<K> c) {
		this(c.size());
		int i = 0;
		for (Object x : c) {
			a[i] = x;
			i++;
		}
		size = i;
	}

	/**
	 * Creates a new array set copying the contents of a given set.
	 * 
	 * @param c a collection.
	 */
	public ObjectArraySet(final Set<? extends K> c) {
		this(c.size());
		int i = 0;
		for (K x : c) {
			a[i] = (x);
			i++;
		}
		size = i;
	}

	/**
	 * Creates a new array set using the given backing array and the given number of elements of the
	 * array.
	 *
	 * <p>
	 * It is the responsibility of the caller to ensure that the first {@code size} elements of
	 * {@code a} are distinct.
	 *
	 * @param a the backing array.
	 * @param size the number of valid elements in {@code a}.
	 */
	public ObjectArraySet(final Object[] a, final int size) {
		this.a = a;
		this.size = size;
		if (size > a.length) throw new IllegalArgumentException("The provided size (" + size + ") is larger than or equal to the array size (" + a.length + ")");
	}

	// The 0 and 1 arg overloads allow us to skip the temporary hash set creation.
	/**
	 * Creates a new empty array set.
	 *
	 * @return a new empty array set.
	 */
	public static <K> ObjectArraySet<K> of() {
		return ofUnchecked();
	}

	/**
	 * Creates a new array set using the element given.
	 *
	 * @param e the element that the returned set will contain.
	 * @return a new array set containing {@code e}.
	 */
	public static <K> ObjectArraySet<K> of(final K e) {
		return ofUnchecked(e);
	}

	/**
	 * Creates a new array set using an array of elements.
	 *
	 * <p>
	 * Unlike the array accepting constructors, this method will throw if duplicate elements are
	 * encountered. This adds a non-trivial validation burden. Use {@link #ofUnchecked} if you know your
	 * input has no duplicates, which will skip this validation.
	 *
	 * @param a the backing array of the returned array set.
	 * @throws IllegalArgumentException if there were duplicate entries.
	 * @return a new array set containing the elements in {@code a}.
	 */
	@SafeVarargs
	public static <K> ObjectArraySet<K> of(final K... a) {
		if (a.length == 2) {
			if (java.util.Objects.equals(a[0], a[1])) {
				throw new IllegalArgumentException("Duplicate element: " + a[1]);
			}
		} else if (a.length > 2) {
			// Will throw on a duplicate entry for us.
			ObjectOpenHashSet.of(a);
		}
		return ofUnchecked(a);
	}

	/**
	 * Creates a new empty array set.
	 * 
	 * @return a new empty array set.
	 */
	public static <K> ObjectArraySet<K> ofUnchecked() {
		return new ObjectArraySet<>();
	}

	// No 1 element overload; we want the temporary array constructed for us in the varargs overload
	/**
	 * Creates a new array set using an array of elements.
	 *
	 * <p>
	 * It is the responsibility of the caller to ensure that the elements of {@code a} are distinct.
	 *
	 * @param a the backing array of the returned array set.
	 * @return a new array set containing the elements in {@code a}.
	 */
	@SafeVarargs
	public static <K> ObjectArraySet<K> ofUnchecked(final K... a) {
		return new ObjectArraySet<K>(a);
	}

	private int findKey(final Object o) {
		for (int i = size; i-- != 0;) if (java.util.Objects.equals(a[i], o)) return i;
		return -1;
	}

	// TODO Maybe make this return a list-iterator like the LinkedXHashSets do?
	@Override
	@SuppressWarnings("unchecked")
	public ObjectIterator<K> iterator() {
		return new ObjectIterator<K>() {
			int next = 0;

			@Override
			public boolean hasNext() {
				return next < size;
			}

			@Override
			public K next() {
				if (!hasNext()) throw new NoSuchElementException();
				return (K)a[next++];
			}

			@Override
			public void remove() {
				final int tail = size-- - next--;
				System.arraycopy(a, next + 1, a, next, tail);
				a[size] = null;
			}

			@Override
			public int skip(int n) {
				if (n < 0) throw new IllegalArgumentException("Argument must be nonnegative: " + n);
				final int remaining = size - next;
				if (n < remaining) {
					next += n;
					return n;
				}
				n = remaining;
				next = size;
				return n;
			}
		};
	}

	// If you update this, you will probably want to update ArrayList as well
	private final class Spliterator implements ObjectSpliterator<K> {
		// Until we split, we will track the size of the set
		// Once we split, then we stop updating on structural modifications.
		// Aka, size is late-binding.
		boolean hasSplit = false;
		int pos, max;

		public Spliterator() {
			this(0, ObjectArraySet.this.size, false);
		}

		private Spliterator(int pos, int max, boolean hasSplit) {
			assert pos <= max : "pos " + pos + " must be <= max " + max;
			this.pos = pos;
			this.max = max;
			this.hasSplit = hasSplit;
		}

		private int getWorkingMax() {
			return hasSplit ? max : ObjectArraySet.this.size;
		}

		@Override
		public int characteristics() {
			return ObjectSpliterators.SET_SPLITERATOR_CHARACTERISTICS | java.util.Spliterator.SUBSIZED | java.util.Spliterator.ORDERED;
		}

		@Override
		public long estimateSize() {
			return getWorkingMax() - pos;
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean tryAdvance(final Consumer<? super K> action) {
			if (pos >= getWorkingMax()) return false;
			action.accept((K)a[pos++]);
			return true;
		}

		@SuppressWarnings("unchecked")
		@Override
		public void forEachRemaining(final Consumer<? super K> action) {
			for (final int max = getWorkingMax(); pos < max; ++pos) {
				action.accept((K)a[pos]);
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
		public ObjectSpliterator<K> trySplit() {
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
	 * In addition to the usual trait of {@link java.util.Spliterator#DISTINCT DISTINCT} for sets, the
	 * returned spliterator will also {@linkplain java.util.Spliterator#characteristics() report} the
	 * trait {@link java.util.Spliterator#ORDERED ORDERED}.
	 *
	 * <p>
	 * The returned spliterator is late-binding; it will track structural changes after the current
	 * item, up until the first {@link java.util.Spliterator#trySplit() trySplit()}, at which point the
	 * maximum index will be fixed. <br>
	 * Structural changes before the current item or after the first
	 * {@link java.util.Spliterator#trySplit() trySplit()} will result in unspecified behavior.
	 */
	@Override
	public ObjectSpliterator<K> spliterator() {
		return new Spliterator();
	}

	@Override
	public boolean contains(final Object k) {
		return findKey(k) != -1;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean remove(final Object k) {
		final int pos = findKey(k);
		if (pos == -1) return false;
		final int tail = size - pos - 1;
		for (int i = 0; i < tail; i++) a[pos + i] = a[pos + i + 1];
		size--;
		a[size] = null;
		return true;
	}

	@Override
	public boolean add(final K k) {
		final int pos = findKey(k);
		if (pos != -1) return false;
		if (size == a.length) {
			final Object[] b = new Object[size == 0 ? 2 : size * 2];
			for (int i = size; i-- != 0;) b[i] = a[i];
			a = b;
		}
		a[size++] = k;
		return true;
	}

	@Override
	public void clear() {
		java.util.Arrays.fill(a, 0, size, null);
		size = 0;
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public Object[] toArray() {
		final int size = size();
		// A subtle part of the spec says the returned array must be Object[] exactly.
		if (size == 0) return it.unimi.dsi.fastutil.objects.ObjectArrays.EMPTY_ARRAY;
		return java.util.Arrays.copyOf(a, size, Object[].class);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T[] toArray(T[] a) {
		if (a == null) {
			a = (T[])new Object[size];
		} else if (a.length < size) {
			a = java.util.Arrays.copyOf(a, size);
		}
		System.arraycopy(this.a, 0, a, 0, size);
		if (a.length > size) {
			a[size] = null;
		}
		return a;
	}

	/**
	 * Returns a deep copy of this set.
	 *
	 * <p>
	 * This method performs a deep copy of this array set; the data stored in the set, however, is not
	 * cloned. Note that this makes a difference only for object keys.
	 *
	 * @return a deep copy of this set.
	 */
	@Override
	@SuppressWarnings("unchecked")
	public ObjectArraySet<K> clone() {
		ObjectArraySet<K> c;
		try {
			c = (ObjectArraySet<K>)super.clone();
		} catch (CloneNotSupportedException cantHappen) {
			throw new InternalError();
		}
		c.a = a.clone();
		return c;
	}

	private void writeObject(java.io.ObjectOutputStream s) throws java.io.IOException {
		s.defaultWriteObject();
		for (int i = 0; i < size; i++) s.writeObject(a[i]);
	}

	private void readObject(java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();
		a = new Object[size];
		for (int i = 0; i < size; i++) a[i] = s.readObject();
	}
}
