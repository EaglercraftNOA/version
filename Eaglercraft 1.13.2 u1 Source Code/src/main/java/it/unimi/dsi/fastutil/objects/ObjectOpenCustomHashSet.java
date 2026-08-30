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

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import static it.unimi.dsi.fastutil.HashCommon.arraySize;
import static it.unimi.dsi.fastutil.HashCommon.maxFill;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

/**
 * A type-specific hash set with a fast, small-footprint implementation whose
 * {@linkplain it.unimi.dsi.fastutil.Hash.Strategy hashing strategy} is specified at creation time.
 *
 * <p>
 * Instances of this class use a hash table to represent a set. The table is filled up to a
 * specified <em>load factor</em>, and then doubled in size to accommodate new entries. If the table
 * is emptied below <em>one fourth</em> of the load factor, it is halved in size; however, the table
 * is never reduced to a size smaller than that at creation time: this approach makes it possible to
 * create sets with a large capacity in which insertions and deletions do not cause immediately
 * rehashing. Moreover, halving is not performed when deleting entries from an iterator, as it would
 * interfere with the iteration process.
 *
 * <p>
 * Note that {@link #clear()} does not modify the hash table size. Rather, a family of
 * {@linkplain #trim() trimming methods} lets you control the size of the table; this is
 * particularly useful if you reuse instances of this class.
 *
 * @see Hash
 * @see HashCommon
 */
public class ObjectOpenCustomHashSet<K> extends AbstractObjectSet<K> implements java.io.Serializable, Cloneable, Hash {
	private static final long serialVersionUID = 0L;
	private static final boolean ASSERTS = false;
	/** The array of keys. */
	protected transient K[] key;
	/** The mask for wrapping a position counter. */
	protected transient int mask;
	/** Whether this set contains the null key. */
	protected transient boolean containsNull;
	/** The hash strategy of this custom set. */
	protected Strategy<? super K> strategy;
	/**
	 * The current table size. Note that an additional element is allocated for storing the null key.
	 */
	protected transient int n;
	/** Threshold after which we rehash. It must be the table size times {@link #f}. */
	protected transient int maxFill;
	/** We never resize below this threshold, which is the construction-time {#n}. */
	protected final transient int minN;
	/** Number of entries in the set (including the null key, if present). */
	protected int size;
	/** The acceptable load factor. */
	protected final float f;

	/**
	 * Creates a new hash set.
	 *
	 * <p>
	 * The actual table size will be the least power of two greater than {@code expected}/{@code f}.
	 *
	 * @param expected the expected number of elements in the hash set.
	 * @param f the load factor.
	 * @param strategy the strategy.
	 */
	@SuppressWarnings("unchecked")
	public ObjectOpenCustomHashSet(final int expected, final float f, final Strategy<? super K> strategy) {
		this.strategy = strategy;
		if (f <= 0 || f >= 1) throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than 1");
		if (expected < 0) throw new IllegalArgumentException("The expected number of elements must be nonnegative");
		this.f = f;
		minN = n = arraySize(expected, f);
		mask = n - 1;
		maxFill = maxFill(n, f);
		key = (K[])new Object[n + 1];
	}

	/**
	 * Creates a new hash set with {@link Hash#DEFAULT_LOAD_FACTOR} as load factor.
	 *
	 * @param expected the expected number of elements in the hash set.
	 * @param strategy the strategy.
	 */
	public ObjectOpenCustomHashSet(final int expected, final Strategy<? super K> strategy) {
		this(expected, DEFAULT_LOAD_FACTOR, strategy);
	}

	/**
	 * Creates a new hash set with initial expected {@link Hash#DEFAULT_INITIAL_SIZE} elements and
	 * {@link Hash#DEFAULT_LOAD_FACTOR} as load factor.
	 * 
	 * @param strategy the strategy.
	 */
	public ObjectOpenCustomHashSet(final Strategy<? super K> strategy) {
		this(DEFAULT_INITIAL_SIZE, DEFAULT_LOAD_FACTOR, strategy);
	}

	/**
	 * Creates a new hash set copying a given collection.
	 *
	 * @param c a {@link Collection} to be copied into the new hash set.
	 * @param f the load factor.
	 * @param strategy the strategy.
	 */
	public ObjectOpenCustomHashSet(final Collection<? extends K> c, final float f, final Strategy<? super K> strategy) {
		this(c.size(), f, strategy);
		addAll(c);
	}

	/**
	 * Creates a new hash set with {@link Hash#DEFAULT_LOAD_FACTOR} as load factor copying a given
	 * collection.
	 *
	 * @param c a {@link Collection} to be copied into the new hash set.
	 * @param strategy the strategy.
	 */
	public ObjectOpenCustomHashSet(final Collection<? extends K> c, final Strategy<? super K> strategy) {
		this(c, DEFAULT_LOAD_FACTOR, strategy);
	}

	/**
	 * Creates a new hash set copying a given type-specific collection.
	 *
	 * @param c a type-specific collection to be copied into the new hash set.
	 * @param f the load factor.
	 * @param strategy the strategy.
	 */
	public ObjectOpenCustomHashSet(final ObjectCollection<? extends K> c, final float f, Strategy<? super K> strategy) {
		this(c.size(), f, strategy);
		addAll(c);
	}

	/**
	 * Creates a new hash set with {@link Hash#DEFAULT_LOAD_FACTOR} as load factor copying a given
	 * type-specific collection.
	 *
	 * @param c a type-specific collection to be copied into the new hash set.
	 * @param strategy the strategy.
	 */
	public ObjectOpenCustomHashSet(final ObjectCollection<? extends K> c, final Strategy<? super K> strategy) {
		this(c, DEFAULT_LOAD_FACTOR, strategy);
	}

	/**
	 * Creates a new hash set using elements provided by a type-specific iterator.
	 *
	 * @param i a type-specific iterator whose elements will fill the set.
	 * @param f the load factor.
	 * @param strategy the strategy.
	 */
	public ObjectOpenCustomHashSet(final Iterator<? extends K> i, final float f, final Strategy<? super K> strategy) {
		this(DEFAULT_INITIAL_SIZE, f, strategy);
		while (i.hasNext()) add(i.next());
	}

	/**
	 * Creates a new hash set with {@link Hash#DEFAULT_LOAD_FACTOR} as load factor using elements
	 * provided by a type-specific iterator.
	 *
	 * @param i a type-specific iterator whose elements will fill the set.
	 * @param strategy the strategy.
	 */
	public ObjectOpenCustomHashSet(final Iterator<? extends K> i, final Strategy<? super K> strategy) {
		this(i, DEFAULT_LOAD_FACTOR, strategy);
	}

	/**
	 * Creates a new hash set and fills it with the elements of a given array.
	 *
	 * @param a an array whose elements will be used to fill the set.
	 * @param offset the first element to use.
	 * @param length the number of elements to use.
	 * @param f the load factor.
	 * @param strategy the strategy.
	 */
	public ObjectOpenCustomHashSet(final K[] a, final int offset, final int length, final float f, final Strategy<? super K> strategy) {
		this(length < 0 ? 0 : length, f, strategy);
		ObjectArrays.ensureOffsetLength(a, offset, length);
		for (int i = 0; i < length; i++) add(a[offset + i]);
	}

	/**
	 * Creates a new hash set with {@link Hash#DEFAULT_LOAD_FACTOR} as load factor and fills it with the
	 * elements of a given array.
	 *
	 * @param a an array whose elements will be used to fill the set.
	 * @param offset the first element to use.
	 * @param length the number of elements to use.
	 * @param strategy the strategy.
	 */
	public ObjectOpenCustomHashSet(final K[] a, final int offset, final int length, final Strategy<? super K> strategy) {
		this(a, offset, length, DEFAULT_LOAD_FACTOR, strategy);
	}

	/**
	 * Creates a new hash set copying the elements of an array.
	 *
	 * @param a an array to be copied into the new hash set.
	 * @param f the load factor.
	 * @param strategy the strategy.
	 */
	public ObjectOpenCustomHashSet(final K[] a, final float f, final Strategy<? super K> strategy) {
		this(a, 0, a.length, f, strategy);
	}

	/**
	 * Creates a new hash set with {@link Hash#DEFAULT_LOAD_FACTOR} as load factor copying the elements
	 * of an array.
	 *
	 * @param a an array to be copied into the new hash set.
	 * @param strategy the strategy.
	 */
	public ObjectOpenCustomHashSet(final K[] a, final Strategy<? super K> strategy) {
		this(a, DEFAULT_LOAD_FACTOR, strategy);
	}

	/**
	 * Returns the hashing strategy.
	 *
	 * @return the hashing strategy of this custom hash set.
	 */
	public Strategy<? super K> strategy() {
		return strategy;
	}

	private int realSize() {
		return containsNull ? size - 1 : size;
	}

	/**
	 * Ensures that this set can hold a certain number of elements without rehashing.
	 *
	 * @param capacity a number of elements; there will be no rehashing unless the set
	 *            {@linkplain #size() size} exceeds this number.
	 */
	public void ensureCapacity(final int capacity) {
		final int needed = arraySize(capacity, f);
		if (needed > n) rehash(needed);
	}

	private void tryCapacity(final long capacity) {
		final int needed = (int)Math.min(1 << 30, Math.max(2, HashCommon.nextPowerOfTwo((long)Math.ceil(capacity / f))));
		if (needed > n) rehash(needed);
	}

	@Override
	public boolean addAll(Collection<? extends K> c) {
		// The resulting collection will be at least c.size() big
		if (f <= .5) ensureCapacity(c.size()); // The resulting collection will be sized for c.size() elements
		else tryCapacity(size() + c.size()); // The resulting collection will be tentatively sized for size() + c.size()
												// elements
		return super.addAll(c);
	}

	@Override
	public boolean add(final K k) {
		int pos;
		if ((strategy.equals((k), (null)))) {
			if (containsNull) return false;
			containsNull = true;
			key[n] = k;
		} else {
			K curr;
			final K[] key = this.key;
			// The starting point.
			if (!((curr = key[pos = (it.unimi.dsi.fastutil.HashCommon.mix(strategy.hashCode(k))) & mask]) == null)) {
				if ((strategy.equals((curr), (k)))) return false;
				while (!((curr = key[pos = (pos + 1) & mask]) == null)) if ((strategy.equals((curr), (k)))) return false;
			}
			key[pos] = k;
		}
		if (size++ >= maxFill) rehash(arraySize(size + 1, f));
		if (ASSERTS) checkTable();
		return true;
	}

	/**
	 * Add a random element if not present, get the existing value if already present.
	 *
	 * This is equivalent to (but faster than) doing a:
	 * 
	 * <pre>
	 * K exist = set.get(k);
	 * if (exist == null) {
	 * 	set.add(k);
	 * 	exist = k;
	 * }
	 * </pre>
	 */
	public K addOrGet(final K k) {
		int pos;
		if ((strategy.equals((k), (null)))) {
			if (containsNull) return key[n];
			containsNull = true;
			key[n] = k;
		} else {
			K curr;
			final K[] key = this.key;
			// The starting point.
			if (!((curr = key[pos = (it.unimi.dsi.fastutil.HashCommon.mix(strategy.hashCode(k))) & mask]) == null)) {
				if ((strategy.equals((curr), (k)))) return curr;
				while (!((curr = key[pos = (pos + 1) & mask]) == null)) if ((strategy.equals((curr), (k)))) return curr;
			}
			key[pos] = k;
		}
		if (size++ >= maxFill) rehash(arraySize(size + 1, f));
		if (ASSERTS) checkTable();
		return k;
	}

	/**
	 * Shifts left entries with the specified hash code, starting at the specified position, and empties
	 * the resulting free entry.
	 *
	 * @param pos a starting position.
	 */
	protected final void shiftKeys(int pos) {
		// Shift entries with the same hash.
		int last, slot;
		K curr;
		final K[] key = this.key;
		for (;;) {
			pos = ((last = pos) + 1) & mask;
			for (;;) {
				if (((curr = key[pos]) == null)) {
					key[last] = (null);
					return;
				}
				slot = (it.unimi.dsi.fastutil.HashCommon.mix(strategy.hashCode(curr))) & mask;
				if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
				pos = (pos + 1) & mask;
			}
			key[last] = curr;
		}
	}

	private boolean removeEntry(final int pos) {
		size--;
		shiftKeys(pos);
		if (n > minN && size < maxFill / 4 && n > DEFAULT_INITIAL_SIZE) rehash(n / 2);
		return true;
	}

	private boolean removeNullEntry() {
		containsNull = false;
		key[n] = (null);
		size--;
		if (n > minN && size < maxFill / 4 && n > DEFAULT_INITIAL_SIZE) rehash(n / 2);
		return true;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean remove(final Object k) {
		if ((strategy.equals(((K)k), (null)))) {
			if (containsNull) return removeNullEntry();
			return false;
		}
		K curr;
		final K[] key = this.key;
		int pos;
		// The starting point.
		if (((curr = key[pos = (it.unimi.dsi.fastutil.HashCommon.mix(strategy.hashCode((K)k))) & mask]) == null)) return false;
		if ((strategy.equals((K)(k), (curr)))) return removeEntry(pos);
		while (true) {
			if (((curr = key[pos = (pos + 1) & mask]) == null)) return false;
			if ((strategy.equals((K)(k), (curr)))) return removeEntry(pos);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean contains(final Object k) {
		if ((strategy.equals(((K)k), (null)))) return containsNull;
		K curr;
		final K[] key = this.key;
		int pos;
		// The starting point.
		if (((curr = key[pos = (it.unimi.dsi.fastutil.HashCommon.mix(strategy.hashCode((K)k))) & mask]) == null)) return false;
		if ((strategy.equals((K)(k), (curr)))) return true;
		while (true) {
			if (((curr = key[pos = (pos + 1) & mask]) == null)) return false;
			if ((strategy.equals((K)(k), (curr)))) return true;
		}
	}

	/**
	 * Returns the element of this set that is equal to the given key, or {@code null}.
	 * 
	 * @return the element of this set that is equal to the given key, or {@code null}.
	 */
	@SuppressWarnings("unchecked")
	public K get(final Object k) {
		if ((strategy.equals(((K)k), (null)))) return key[n]; // This is correct independently of the value of
																// containsNull and of the set being custom
		K curr;
		final K[] key = this.key;
		int pos;
		// The starting point.
		if (((curr = key[pos = (it.unimi.dsi.fastutil.HashCommon.mix(strategy.hashCode((K)k))) & mask]) == null)) return null;
		if ((strategy.equals((K)(k), (curr)))) return curr;
		// There's always an unused entry.
		while (true) {
			if (((curr = key[pos = (pos + 1) & mask]) == null)) return null;
			if ((strategy.equals((K)(k), (curr)))) return curr;
		}
	}

	/* Removes all elements from this set.
	 *
	 * <p>To increase object reuse, this method does not change the table size.
	 * If you want to reduce the table size, you must use {@link #trim()}.
	 *
	 */
	@Override
	public void clear() {
		if (size == 0) return;
		size = 0;
		containsNull = false;
		Arrays.fill(key, (null));
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	/** An iterator over a hash set. */
	private final class SetIterator implements ObjectIterator<K> {
		/**
		 * The index of the last entry returned, if positive or zero; initially, {@link #n}. If negative,
		 * the last element returned was that of index {@code - pos - 1} from the {@link #wrapped} list.
		 */
		int pos = n;
		/**
		 * The index of the last entry that has been returned (more precisely, the value of {@link #pos} if
		 * {@link #pos} is positive, or {@link Integer#MIN_VALUE} if {@link #pos} is negative). It is -1 if
		 * either we did not return an entry yet, or the last returned entry has been removed.
		 */
		int last = -1;
		/** A downward counter measuring how many entries must still be returned. */
		int c = size;
		/** A boolean telling us whether we should return the null key. */
		boolean mustReturnNull = ObjectOpenCustomHashSet.this.containsNull;
		/**
		 * A lazily allocated list containing elements that have wrapped around the table because of
		 * removals.
		 */
		ObjectArrayList<K> wrapped;

		@Override
		public boolean hasNext() {
			return c != 0;
		}

		@Override
		public K next() {
			if (!hasNext()) throw new NoSuchElementException();
			c--;
			if (mustReturnNull) {
				mustReturnNull = false;
				last = n;
				return key[n];
			}
			final K key[] = ObjectOpenCustomHashSet.this.key;
			for (;;) {
				if (--pos < 0) {
					// We are just enumerating elements from the wrapped list.
					last = Integer.MIN_VALUE;
					return wrapped.get(-pos - 1);
				}
				if (!((key[pos]) == null)) return key[last = pos];
			}
		}

		/**
		 * Shifts left entries with the specified hash code, starting at the specified position, and empties
		 * the resulting free entry.
		 *
		 * @param pos a starting position.
		 */
		private final void shiftKeys(int pos) {
			// Shift entries with the same hash.
			int last, slot;
			K curr;
			final K[] key = ObjectOpenCustomHashSet.this.key;
			for (;;) {
				pos = ((last = pos) + 1) & mask;
				for (;;) {
					if (((curr = key[pos]) == null)) {
						key[last] = (null);
						return;
					}
					slot = (it.unimi.dsi.fastutil.HashCommon.mix(strategy.hashCode(curr))) & mask;
					if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
					pos = (pos + 1) & mask;
				}
				if (pos < last) { // Wrapped entry.
					if (wrapped == null) wrapped = new ObjectArrayList<>(2);
					wrapped.add(key[pos]);
				}
				key[last] = curr;
			}
		}

		@Override
		public void remove() {
			if (last == -1) throw new IllegalStateException();
			if (last == n) {
				ObjectOpenCustomHashSet.this.containsNull = false;
				ObjectOpenCustomHashSet.this.key[n] = (null);
			} else if (pos >= 0) shiftKeys(last);
			else {
				// We're removing wrapped entries.
				ObjectOpenCustomHashSet.this.remove(wrapped.set(-pos - 1, null));
				last = -1; // Note that we must not decrement size
				return;
			}
			size--;
			last = -1; // You can no longer remove this entry.
			if (ASSERTS) checkTable();
		}

		@Override
		public void forEachRemaining(final Consumer<? super K> action) {
			final K key[] = ObjectOpenCustomHashSet.this.key;
			if (mustReturnNull) {
				mustReturnNull = false;
				last = n;
				action.accept(key[n]);
				c--;
			}
			while (c != 0) {
				if (--pos < 0) {
					// We are just enumerating elements from the wrapped list.
					last = Integer.MIN_VALUE;
					action.accept(wrapped.get(-pos - 1));
					c--;
				} else if (!((key[pos]) == null)) {
					action.accept(key[last = pos]);
					c--;
				}
			}
		}
	}

	@Override
	public ObjectIterator<K> iterator() {
		return new SetIterator();
	}

	private final class SetSpliterator implements ObjectSpliterator<K> {
		private static final int POST_SPLIT_CHARACTERISTICS = ObjectSpliterators.SET_SPLITERATOR_CHARACTERISTICS & ~java.util.Spliterator.SIZED;
		/**
		 * The index (which bucket) of the next item to give to the action. Unlike {@link SetIterator}, this
		 * counts up instead of down.
		 */
		int pos = 0;
		/** The maximum bucket (exclusive) to iterate to */
		int max = n;
		/** An upwards counter counting how many we have given */
		int c = 0;
		/** A boolean telling us whether we should return the null key. */
		boolean mustReturnNull = ObjectOpenCustomHashSet.this.containsNull;
		boolean hasSplit = false;

		SetSpliterator() {
		}

		SetSpliterator(int pos, int max, boolean mustReturnNull, boolean hasSplit) {
			this.pos = pos;
			this.max = max;
			this.mustReturnNull = mustReturnNull;
			this.hasSplit = hasSplit;
		}

		@Override
		public boolean tryAdvance(final Consumer<? super K> action) {
			if (mustReturnNull) {
				mustReturnNull = false;
				++c;
				action.accept(key[n]);
				return true;
			}
			final K key[] = ObjectOpenCustomHashSet.this.key;
			while (pos < max) {
				if (!((key[pos]) == null)) {
					++c;
					action.accept(key[pos++]);
					return true;
				} else {
					++pos;
				}
			}
			return false;
		}

		@Override
		public void forEachRemaining(final Consumer<? super K> action) {
			final K key[] = ObjectOpenCustomHashSet.this.key;
			if (mustReturnNull) {
				mustReturnNull = false;
				action.accept(key[n]);
				++c;
			}
			while (pos < max) {
				if (!((key[pos]) == null)) {
					action.accept(key[pos]);
					++c;
				}
				++pos;
			}
		}

		@Override
		public int characteristics() {
			return hasSplit ? POST_SPLIT_CHARACTERISTICS : ObjectSpliterators.SET_SPLITERATOR_CHARACTERISTICS;
		}

		@Override
		public long estimateSize() {
			if (!hasSplit) {
				// Root spliterator; we know how many are remaining.
				return size - c;
			} else {
				// After we split, we can no longer know exactly how many we have (or at least not efficiently).
				// (size / n) * (max - pos) aka currentTableDensity * numberOfBucketsLeft seems like a good
				// estimate.
				return Math.min(size - c, (long)(((double)realSize() / n) * (max - pos)) + (mustReturnNull ? 1 : 0));
			}
		}

		@Override
		public SetSpliterator trySplit() {
			if (pos >= max - 1) return null;
			int retLen = (max - pos) >> 1;
			if (retLen <= 1) return null;
			int myNewPos = pos + retLen;
			int retPos = pos;
			int retMax = myNewPos;
			// Since null is returned first, and the convention is that the returned split is the prefix of
			// elements,
			// the split will take care of returning null (if needed), and we won't return it anymore.
			SetSpliterator split = new SetSpliterator(retPos, retMax, mustReturnNull, true);
			this.pos = myNewPos;
			this.mustReturnNull = false;
			this.hasSplit = true;
			return split;
		}

		@Override
		public long skip(long n) {
			if (n < 0) throw new IllegalArgumentException("Argument must be nonnegative: " + n);
			if (n == 0) return 0;
			long skipped = 0;
			if (mustReturnNull) {
				mustReturnNull = false;
				++skipped;
				--n;
			}
			final K key[] = ObjectOpenCustomHashSet.this.key;
			while (pos < max && n > 0) {
				if (!((key[pos++]) == null)) {
					++skipped;
					--n;
				}
			}
			return skipped;
		}
	}

	@Override
	public ObjectSpliterator<K> spliterator() {
		return new SetSpliterator();
	}

	@Override
	public void forEach(final Consumer<? super K> action) {
		if (containsNull) action.accept(key[n]);
		final K key[] = this.key;
		for (int pos = n; pos-- != 0;) if (!((key[pos]) == null)) action.accept(key[pos]);
	}

	/**
	 * Rehashes this set, making the table as small as possible.
	 *
	 * <p>
	 * This method rehashes the table to the smallest size satisfying the load factor. It can be used
	 * when the set will not be changed anymore, so to optimize access speed and size.
	 *
	 * <p>
	 * If the table size is already the minimum possible, this method does nothing.
	 *
	 * @return true if there was enough memory to trim the set.
	 * @see #trim(int)
	 */
	public boolean trim() {
		return trim(size);
	}

	/**
	 * Rehashes this set if the table is too large.
	 *
	 * <p>
	 * Let <var>N</var> be the smallest table size that can hold <code>max(n,{@link #size()})</code>
	 * entries, still satisfying the load factor. If the current table size is smaller than or equal to
	 * <var>N</var>, this method does nothing. Otherwise, it rehashes this set in a table of size
	 * <var>N</var>.
	 *
	 * <p>
	 * This method is useful when reusing sets. {@linkplain #clear() Clearing a set} leaves the table
	 * size untouched. If you are reusing a set many times, you can call this method with a typical size
	 * to avoid keeping around a very large table just because of a few large transient sets.
	 *
	 * @param n the threshold for the trimming.
	 * @return true if there was enough memory to trim the set.
	 * @see #trim()
	 */
	public boolean trim(final int n) {
		final int l = HashCommon.nextPowerOfTwo((int)Math.ceil(n / f));
		if (l >= this.n || size > maxFill(l, f)) return true;
		try {
			rehash(l);
		} catch (OutOfMemoryError cantDoIt) {
			return false;
		}
		return true;
	}

	/**
	 * Rehashes the set.
	 *
	 * <p>
	 * This method implements the basic rehashing strategy, and may be overriden by subclasses
	 * implementing different rehashing strategies (e.g., disk-based rehashing). However, you should not
	 * override this method unless you understand the internal workings of this class.
	 *
	 * @param newN the new size
	 */
	@SuppressWarnings("unchecked")
	protected void rehash(final int newN) {
		final K key[] = this.key;
		final int mask = newN - 1; // Note that this is used by the hashing macro
		final K newKey[] = (K[])new Object[newN + 1];
		int i = n, pos;
		for (int j = realSize(); j-- != 0;) {
			while (((key[--i]) == null));
			if (!((newKey[pos = (it.unimi.dsi.fastutil.HashCommon.mix(strategy.hashCode(key[i]))) & mask]) == null)) while (!((newKey[pos = (pos + 1) & mask]) == null));
			newKey[pos] = key[i];
		}
		n = newN;
		this.mask = mask;
		maxFill = maxFill(n, f);
		this.key = newKey;
	}

	/**
	 * Returns a deep copy of this set.
	 *
	 * <p>
	 * This method performs a deep copy of this hash set; the data stored in the set, however, is not
	 * cloned. Note that this makes a difference only for object keys.
	 *
	 * @return a deep copy of this set.
	 */
	@Override
	@SuppressWarnings("unchecked")
	public ObjectOpenCustomHashSet<K> clone() {
		ObjectOpenCustomHashSet<K> c;
		try {
			c = (ObjectOpenCustomHashSet<K>)super.clone();
		} catch (CloneNotSupportedException cantHappen) {
			throw new InternalError();
		}
		c.key = key.clone();
		c.containsNull = containsNull;
		c.strategy = strategy;
		return c;
	}

	/**
	 * Returns a hash code for this set.
	 *
	 * This method overrides the generic method provided by the superclass. Since {@code equals()} is
	 * not overriden, it is important that the value returned by this method is the same value as the
	 * one returned by the overriden method.
	 *
	 * @return a hash code for this set.
	 */
	@Override
	public int hashCode() {
		int h = 0;
		for (int j = realSize(), i = 0; j-- != 0;) {
			while (((key[i]) == null)) i++;
			if (this != key[i]) h += (strategy.hashCode(key[i]));
			i++;
		}
		// Zero / null have hash zero.
		return h;
	}

	private void writeObject(java.io.ObjectOutputStream s) throws java.io.IOException {
		final ObjectIterator<K> i = iterator();
		s.defaultWriteObject();
		for (int j = size; j-- != 0;) s.writeObject(i.next());
	}

	@SuppressWarnings("unchecked")
	private void readObject(java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();
		n = arraySize(size, f);
		maxFill = maxFill(n, f);
		mask = n - 1;
		final K key[] = this.key = (K[])new Object[n + 1];
		K k;
		for (int i = size, pos; i-- != 0;) {
			k = (K)s.readObject();
			if ((strategy.equals((k), (null)))) {
				pos = n;
				containsNull = true;
			} else {
				if (!((key[pos = (it.unimi.dsi.fastutil.HashCommon.mix(strategy.hashCode(k))) & mask]) == null)) while (!((key[pos = (pos + 1) & mask]) == null));
			}
			key[pos] = k;
		}
		if (ASSERTS) checkTable();
	}

	private void checkTable() {
	}
}
