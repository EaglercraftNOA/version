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

import static it.unimi.dsi.fastutil.BigArrays.copy;
import static it.unimi.dsi.fastutil.BigArrays.fill;
import static it.unimi.dsi.fastutil.BigArrays.set;
import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.HashCommon;
import static it.unimi.dsi.fastutil.HashCommon.bigArraySize;
import static it.unimi.dsi.fastutil.HashCommon.maxFill;
import java.util.function.Consumer;
import java.util.stream.Collector;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A type-specific hash big set with with a fast, small-footprint implementation.
 *
 * <p>
 * Instances of this class use a hash table to represent a big set: the number of elements in the
 * set is limited only by the amount of core memory. The table (backed by a
 * {@linkplain it.unimi.dsi.fastutil.BigArrays big array}) is filled up to a specified <em>load
 * factor</em>, and then doubled in size to accommodate new entries. If the table is emptied below
 * <em>one fourth</em> of the load factor, it is halved in size; however, the table is never reduced
 * to a size smaller than that at creation time: this approach makes it possible to create sets with
 * a large capacity in which insertions and deletions do not cause immediately rehashing. Moreover,
 * halving is not performed when deleting entries from an iterator, as it would interfere with the
 * iteration process.
 *
 * <p>
 * Note that {@link #clear()} does not modify the hash table size. Rather, a family of
 * {@linkplain #trim() trimming methods} lets you control the size of the table; this is
 * particularly useful if you reuse instances of this class.
 *
 * <p>
 * The methods of this class are about 30% slower than those of the corresponding non-big set.
 *
 * @see Hash
 * @see HashCommon
 */
public class ObjectOpenHashBigSet<K> extends AbstractObjectSet<K> implements java.io.Serializable, Cloneable, Hash, Size64 {
	private static final long serialVersionUID = 0L;
	private static final boolean ASSERTS = false;
	/** The big array of keys. */
	protected transient K[][] key;
	/** The mask for wrapping a position counter. */
	protected transient long mask;
	/** The mask for wrapping a segment counter. */
	protected transient int segmentMask;
	/** The mask for wrapping a base counter. */
	protected transient int baseMask;
	/** Whether this set contains the null key. */
	protected transient boolean containsNull;
	/** The current table size (always a power of 2). */
	protected transient long n;
	/** Threshold after which we rehash. It must be the table size times {@link #f}. */
	protected transient long maxFill;
	/** We never resize below this threshold, which is the construction-time {#n}. */
	protected final transient long minN;
	/** The acceptable load factor. */
	protected final float f;
	/** Number of entries in the set. */
	protected long size;

	/** Initialises the mask values. */
	private void initMasks() {
		mask = n - 1;
		/* Note that either we have more than one segment, and in this case all segments
		 * are BigArrays.SEGMENT_SIZE long, or we have exactly one segment whose length
		 * is a power of two. */
		segmentMask = key[0].length - 1;
		baseMask = key.length - 1;
	}

	/**
	 * Creates a new hash big set.
	 *
	 * <p>
	 * The actual table size will be the least power of two greater than {@code expected}/{@code f}.
	 *
	 * @param expected the expected number of elements in the set.
	 * @param f the load factor.
	 */
	@SuppressWarnings("unchecked")
	public ObjectOpenHashBigSet(final long expected, final float f) {
		if (f <= 0 || f > 1) throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than or equal to 1");
		if (n < 0) throw new IllegalArgumentException("The expected number of elements must be nonnegative");
		this.f = f;
		minN = n = bigArraySize(expected, f);
		maxFill = maxFill(n, f);
		key = (K[][])ObjectBigArrays.newBigArray(n);
		initMasks();
	}

	/**
	 * Creates a new hash big set with {@link Hash#DEFAULT_LOAD_FACTOR} as load factor.
	 *
	 * @param expected the expected number of elements in the hash big set.
	 */
	public ObjectOpenHashBigSet(final long expected) {
		this(expected, DEFAULT_LOAD_FACTOR);
	}

	/**
	 * Creates a new hash big set with initial expected {@link Hash#DEFAULT_INITIAL_SIZE} elements and
	 * {@link Hash#DEFAULT_LOAD_FACTOR} as load factor.
	 */
	public ObjectOpenHashBigSet() {
		this(DEFAULT_INITIAL_SIZE, DEFAULT_LOAD_FACTOR);
	}

	/**
	 * Creates a new hash big set copying a given collection.
	 *
	 * @param c a {@link Collection} to be copied into the new hash big set.
	 * @param f the load factor.
	 */
	public ObjectOpenHashBigSet(final Collection<? extends K> c, final float f) {
		this(Size64.sizeOf(c), f);
		addAll(c);
	}

	/**
	 * Creates a new hash big set with {@link Hash#DEFAULT_LOAD_FACTOR} as load factor copying a given
	 * collection.
	 *
	 * @param c a {@link Collection} to be copied into the new hash big set.
	 */
	public ObjectOpenHashBigSet(final Collection<? extends K> c) {
		this(c, DEFAULT_LOAD_FACTOR);
	}

	/**
	 * Creates a new hash big set copying a given type-specific collection.
	 *
	 * @param c a type-specific collection to be copied into the new hash big set.
	 * @param f the load factor.
	 */
	public ObjectOpenHashBigSet(final ObjectCollection<? extends K> c, final float f) {
		this(Size64.sizeOf(c), f);
		addAll(c);
	}

	/**
	 * Creates a new hash big set with {@link Hash#DEFAULT_LOAD_FACTOR} as load factor copying a given
	 * type-specific collection.
	 *
	 * @param c a type-specific collection to be copied into the new hash big set.
	 */
	public ObjectOpenHashBigSet(final ObjectCollection<? extends K> c) {
		this(c, DEFAULT_LOAD_FACTOR);
	}

	/**
	 * Creates a new hash big set using elements provided by a type-specific iterator.
	 *
	 * @param i a type-specific iterator whose elements will fill the new hash big set.
	 * @param f the load factor.
	 */
	public ObjectOpenHashBigSet(final Iterator<? extends K> i, final float f) {
		this(DEFAULT_INITIAL_SIZE, f);
		while (i.hasNext()) add(i.next());
	}

	/**
	 * Creates a new hash big set with {@link Hash#DEFAULT_LOAD_FACTOR} as load factor using elements
	 * provided by a type-specific iterator.
	 *
	 * @param i a type-specific iterator whose elements will fill the new hash big set.
	 */
	public ObjectOpenHashBigSet(final Iterator<? extends K> i) {
		this(i, DEFAULT_LOAD_FACTOR);
	}

	/**
	 * Creates a new hash big set and fills it with the elements of a given array.
	 *
	 * @param a an array whose elements will be used to fill the new hash big set.
	 * @param offset the first element to use.
	 * @param length the number of elements to use.
	 * @param f the load factor.
	 */
	public ObjectOpenHashBigSet(final K[] a, final int offset, final int length, final float f) {
		this(length < 0 ? 0 : length, f);
		ObjectArrays.ensureOffsetLength(a, offset, length);
		for (int i = 0; i < length; i++) add(a[offset + i]);
	}

	/**
	 * Creates a new hash big set with {@link Hash#DEFAULT_LOAD_FACTOR} as load factor and fills it with
	 * the elements of a given array.
	 *
	 * @param a an array whose elements will be used to fill the new hash big set.
	 * @param offset the first element to use.
	 * @param length the number of elements to use.
	 */
	public ObjectOpenHashBigSet(final K[] a, final int offset, final int length) {
		this(a, offset, length, DEFAULT_LOAD_FACTOR);
	}

	/**
	 * Creates a new hash big set copying the elements of an array.
	 *
	 * @param a an array to be copied into the new hash big set.
	 * @param f the load factor.
	 */
	public ObjectOpenHashBigSet(final K[] a, final float f) {
		this(a, 0, a.length, f);
	}

	/**
	 * Creates a new hash big set with {@link Hash#DEFAULT_LOAD_FACTOR} as load factor copying the
	 * elements of an array.
	 *
	 * @param a an array to be copied into the new hash big set.
	 */
	public ObjectOpenHashBigSet(final K[] a) {
		this(a, DEFAULT_LOAD_FACTOR);
	}

	// Collector wants a function that returns the collection being added to.
	private ObjectOpenHashBigSet<K> combine(ObjectOpenHashBigSet<? extends K> toAddFrom) {
		addAll(toAddFrom);
		return this;
	}

	private static final Collector<Object, ?, ObjectOpenHashBigSet<Object>> TO_SET_COLLECTOR = Collector.of(ObjectOpenHashBigSet::new, ObjectOpenHashBigSet::add, ObjectOpenHashBigSet::combine);

	/**
	 * Returns a {@link Collector} that collects a {@code Stream}'s elements into a new big hash set.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <K> Collector<K, ?, ObjectOpenHashBigSet<K>> toBigSet() {
		return (Collector)TO_SET_COLLECTOR;
	}

	/**
	 * Returns a {@link Collector} that collects a {@code Stream}'s elements into a new big hash set.
	 */
	public static <K> Collector<K, ?, ObjectOpenHashBigSet<K>> toBigSetWithExpectedSize(long expectedSize) {
		return Collector.of(() -> new ObjectOpenHashBigSet<K>(expectedSize), ObjectOpenHashBigSet::add, ObjectOpenHashBigSet::combine);
	}

	private long realSize() {
		return containsNull ? size - 1 : size;
	}

	/**
	 * Ensures that this big set can hold a certain number of elements without rehashing.
	 *
	 * @param capacity a number of elements; there will be no rehashing unless the set
	 *            {@linkplain #size64() size} exceeds this number.
	 */
	public void ensureCapacity(final long capacity) {
		final long needed = bigArraySize(capacity, f);
		if (needed > n) rehash(needed);
	}

	@Override
	public boolean addAll(Collection<? extends K> c) {
		final long size = Size64.sizeOf(c);
		// The resulting collection will be at least c.size() big
		if (f <= .5) ensureCapacity(size); // The resulting collection will be sized for c.size() elements
		else ensureCapacity(size64() + size); // The resulting collection will be sized for size() + c.size() elements
		return super.addAll(c);
	}

	@Override
	public boolean add(final K k) {
		int displ, base;
		if (((k) == null)) {
			if (containsNull) return false;
			containsNull = true;
		} else {
			K curr;
			final K[][] key = this.key;
			final long h = (it.unimi.dsi.fastutil.HashCommon.mix((long)((k).hashCode())));
			// The starting point.
			if (!((curr = key[base = (int)((h & mask) >>> BigArrays.SEGMENT_SHIFT)][displ = (int)(h & segmentMask)]) == null)) {
				if (((curr).equals(k))) return false;
				while (!((curr = key[base = (base + ((displ = (displ + 1) & segmentMask) == 0 ? 1 : 0)) & baseMask][displ]) == null)) if (((curr).equals(k))) return false;
			}
			key[base][displ] = k;
		}
		if (size++ >= maxFill) rehash(2 * n);
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
		int displ, base;
		if (((k) == null)) {
			if (containsNull) return null;
			containsNull = true;
		} else {
			K curr;
			final K[][] key = this.key;
			final long h = (it.unimi.dsi.fastutil.HashCommon.mix((long)((k).hashCode())));
			// The starting point.
			if (!((curr = key[base = (int)((h & mask) >>> BigArrays.SEGMENT_SHIFT)][displ = (int)(h & segmentMask)]) == null)) {
				if (((curr).equals(k))) return curr;
				while (!((curr = key[base = (base + ((displ = (displ + 1) & segmentMask) == 0 ? 1 : 0)) & baseMask][displ]) == null)) if (((curr).equals(k))) return curr;
			}
			key[base][displ] = k;
		}
		if (size++ >= maxFill) rehash(2 * n);
		if (ASSERTS) checkTable();
		return k;
	}

	/**
	 * Shifts left entries with the specified hash code, starting at the specified position, and empties
	 * the resulting free entry.
	 *
	 * @param pos a starting position.
	 */
	protected final void shiftKeys(long pos) {
		// Shift entries with the same hash.
		long last, slot;
		final K[][] key = this.key;
		for (;;) {
			pos = ((last = pos) + 1) & mask;
			for (;;) {
				if (((BigArrays.get(key, pos)) == null)) {
					set(key, last, (null));
					return;
				}
				slot = (it.unimi.dsi.fastutil.HashCommon.mix((long)((BigArrays.get(key, pos)).hashCode()))) & mask;
				if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
				pos = (pos + 1) & mask;
			}
			set(key, last, BigArrays.get(key, pos));
		}
	}

	private boolean removeEntry(final int base, final int displ) {
		size--;
		shiftKeys(base * (long)BigArrays.SEGMENT_SIZE + displ);
		if (n > minN && size < maxFill / 4 && n > DEFAULT_INITIAL_SIZE) rehash(n / 2);
		return true;
	}

	private boolean removeNullEntry() {
		containsNull = false;
		size--;
		if (n > minN && size < maxFill / 4 && n > DEFAULT_INITIAL_SIZE) rehash(n / 2);
		return true;
	}

	@Override
	public boolean remove(final Object k) {
		if (((k) == null)) {
			if (containsNull) return removeNullEntry();
			return false;
		}
		K curr;
		final K[][] key = this.key;
		final long h = (it.unimi.dsi.fastutil.HashCommon.mix((long)((k).hashCode())));
		int displ, base;
		// The starting point.
		if (((curr = key[base = (int)((h & mask) >>> BigArrays.SEGMENT_SHIFT)][displ = (int)(h & segmentMask)]) == null)) return false;
		if (((curr).equals(k))) return removeEntry(base, displ);
		while (true) {
			if (((curr = key[base = (base + ((displ = (displ + 1) & segmentMask) == 0 ? 1 : 0)) & baseMask][displ]) == null)) return false;
			if (((curr).equals(k))) return removeEntry(base, displ);
		}
	}

	@Override
	public boolean contains(final Object k) {
		if (((k) == null)) return containsNull;
		K curr;
		final K[][] key = this.key;
		final long h = (it.unimi.dsi.fastutil.HashCommon.mix((long)((k).hashCode())));
		int displ, base;
		// The starting point.
		if (((curr = key[base = (int)((h & mask) >>> BigArrays.SEGMENT_SHIFT)][displ = (int)(h & segmentMask)]) == null)) return false;
		if (((curr).equals(k))) return true;
		while (true) {
			if (((curr = key[base = (base + ((displ = (displ + 1) & segmentMask) == 0 ? 1 : 0)) & baseMask][displ]) == null)) return false;
			if (((curr).equals(k))) return true;
		}
	}

	/**
	 * Returns the element of this set that is equal to the given key, or {@code null}.
	 * 
	 * @return the element of this set that is equal to the given key, or {@code null}.
	 */
	public K get(final Object k) {
		if (k == null) return null; // This is correct independently of the value of containsNull
		K curr;
		final K[][] key = this.key;
		final long h = (it.unimi.dsi.fastutil.HashCommon.mix((long)((k).hashCode())));
		int displ, base;
		// The starting point.
		if (((curr = key[base = (int)((h & mask) >>> BigArrays.SEGMENT_SHIFT)][displ = (int)(h & segmentMask)]) == null)) return null;
		if (((curr).equals(k))) return curr;
		while (true) {
			if (((curr = key[base = (base + ((displ = (displ + 1) & segmentMask) == 0 ? 1 : 0)) & baseMask][displ]) == null)) return null;
			if (((curr).equals(k))) return curr;
		}
	}

	/* Removes all elements from this set.
	 *
	 */
	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * To increase object reuse, this method does not change the table size. If you want to reduce the
	 * table size, you must use {@link #trim(long)}.
	 */
	@Override
	public void clear() {
		if (size == 0) return;
		size = 0;
		containsNull = false;
		fill(key, (null));
	}

	/** An iterator over a hash big set. */
	private class SetIterator implements ObjectIterator<K> {
		/**
		 * The base of the last entry returned, if positive or zero; initially, the number of components of
		 * the key array. If negative, the last element returned was that of index {@code - base - 1} from
		 * the {@link #wrapped} list.
		 */
		int base = key.length;
		/** The displacement of the last entry returned; initially, zero. */
		int displ;
		/**
		 * The index of the last entry that has been returned (or {@link Long#MIN_VALUE} if {@link #base} is
		 * negative). It is -1 if either we did not return an entry yet, or the last returned entry has been
		 * removed.
		 */
		long last = -1;
		/** A downward counter measuring how many entries must still be returned. */
		long c = size;
		/** A boolean telling us whether we should return the null key. */
		boolean mustReturnNull = ObjectOpenHashBigSet.this.containsNull;
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
				return (null);
			}
			final K[][] key = ObjectOpenHashBigSet.this.key;
			for (;;) {
				if (displ == 0 && base <= 0) {
					// We are just enumerating elements from the wrapped list.
					last = Long.MIN_VALUE;
					return wrapped.get(-(--base) - 1);
				}
				if (displ-- == 0) displ = key[--base].length - 1;
				final K k = key[base][displ];
				if (!((k) == null)) {
					last = base * (long)BigArrays.SEGMENT_SIZE + displ;
					return k;
				}
			}
		}

		/**
		 * Shifts left entries with the specified hash code, starting at the specified position, and empties
		 * the resulting free entry.
		 *
		 * @param pos a starting position.
		 */
		private final void shiftKeys(long pos) {
			// Shift entries with the same hash.
			long last, slot;
			K curr;
			final K[][] key = ObjectOpenHashBigSet.this.key;
			for (;;) {
				pos = ((last = pos) + 1) & mask;
				for (;;) {
					if (((curr = BigArrays.get(key, pos)) == null)) {
						set(key, last, (null));
						return;
					}
					slot = (it.unimi.dsi.fastutil.HashCommon.mix((long)((curr).hashCode()))) & mask;
					if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
					pos = (pos + 1) & mask;
				}
				if (pos < last) { // Wrapped entry.
					if (wrapped == null) wrapped = new ObjectArrayList<>();
					wrapped.add(BigArrays.get(key, pos));
				}
				set(key, last, curr);
			}
		}

		@Override
		public void remove() {
			if (last == -1) throw new IllegalStateException();
			if (last == n) ObjectOpenHashBigSet.this.containsNull = false;
			else if (base >= 0) shiftKeys(last);
			else {
				// We're removing wrapped entries.
				ObjectOpenHashBigSet.this.remove(wrapped.set(-base - 1, null));
				last = -1; // Note that we must not decrement size
				return;
			}
			size--;
			last = -1; // You can no longer remove this entry.
			if (ASSERTS) checkTable();
		}
	}

	@Override
	public ObjectIterator<K> iterator() {
		return new SetIterator();
	}

	private class SetSpliterator implements ObjectSpliterator<K> {
		/* For the sake of keeping things at least somewhat simple
		 * (aka. my sanity), the spliterator will NOT handle the indexing
		 * of the subarrays directly, like iterator does. Instead, it will
		 * delegate to BigArrays and have only a single, unified index it
		 * will fence on. This is probably less effecient, but it avoids having
		 * to track what it means to split on two sets of indexes.
		 * This may change in the future if the performance hit high.
		 */
		private static final int POST_SPLIT_CHARACTERISTICS = ObjectSpliterators.SET_SPLITERATOR_CHARACTERISTICS & ~java.util.Spliterator.SIZED;
		/** The index (which bucket) of the next item to give to the action. */
		long pos = 0;
		/** The maximum bucket (exclusive) to iterate to */
		long max = n;
		/** An upwards counter counting how many we have given */
		long c = 0;
		/** A boolean telling us whether we should return the null key. */
		boolean mustReturnNull = ObjectOpenHashBigSet.this.containsNull;
		boolean hasSplit = false;

		SetSpliterator() {
		}

		SetSpliterator(long pos, long max, boolean mustReturnNull, boolean hasSplit) {
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
				action.accept((null));
				return true;
			}
			final K key[][] = ObjectOpenHashBigSet.this.key;
			while (pos < max) {
				K gotten = BigArrays.get(key, pos);
				if (!((gotten) == null)) {
					++c;
					++pos;
					action.accept(gotten);
					return true;
				} else {
					++pos;
				}
			}
			return false;
		}

		@Override
		public void forEachRemaining(final Consumer<? super K> action) {
			if (mustReturnNull) {
				mustReturnNull = false;
				action.accept((null));
				++c;
			}
			final K key[][] = ObjectOpenHashBigSet.this.key;
			while (pos < max) {
				K gotten = BigArrays.get(key, pos);
				if (!((gotten) == null)) {
					action.accept(gotten);
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
			long retLen = (max - pos) >> 1;
			if (retLen <= 1) return null;
			long myNewPos = pos + retLen;
			// Align to an outer array boundary if possible
			// We add/subtract one to the bounds to ensure the new pos will always shrink the range
			myNewPos = BigArrays.nearestSegmentStart(myNewPos, pos + 1, max - 1);
			long retPos = pos;
			long retMax = myNewPos;
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
			final K key[][] = ObjectOpenHashBigSet.this.key;
			while (pos < max && n > 0) {
				if (!((BigArrays.get(key, pos++)) == null)) {
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
		if (containsNull) {
			action.accept((null));
		}
		long pos = 0;
		final long max = n;
		final K key[][] = this.key;
		while (pos < max) {
			K gotten = BigArrays.get(key, pos++);
			if (!((gotten) == null)) {
				action.accept(gotten);
			}
		}
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
	 * @see #trim(long)
	 */
	public boolean trim() {
		return trim(size);
	}

	/**
	 * Rehashes this set if the table is too large.
	 *
	 * <p>
	 * Let <var>N</var> be the smallest table size that can hold <code>max(n,{@link #size64()})</code>
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
	public boolean trim(final long n) {
		final long l = bigArraySize(n, f);
		if (l >= this.n || size > maxFill(l, f)) return true;
		try {
			rehash(l);
		} catch (OutOfMemoryError cantDoIt) {
			return false;
		}
		return true;
	}

	/**
	 * Resizes the set.
	 *
	 * <p>
	 * This method implements the basic rehashing strategy, and may be overriden by subclasses
	 * implementing different rehashing strategies (e.g., disk-based rehashing). However, you should not
	 * override this method unless you understand the internal workings of this class.
	 *
	 * @param newN the new size
	 */
	@SuppressWarnings("unchecked")
	protected void rehash(final long newN) {
		final K key[][] = this.key;
		final K newKey[][] = (K[][])ObjectBigArrays.newBigArray(newN);
		final long mask = newN - 1; // Note that this is used by the hashing macro
		final int newSegmentMask = newKey[0].length - 1;
		final int newBaseMask = newKey.length - 1;
		int base = 0, displ = 0, b, d;
		long h;
		K k;
		for (long i = realSize(); i-- != 0;) {
			while (((key[base][displ]) == null)) base = (base + ((displ = (displ + 1) & segmentMask) == 0 ? 1 : 0));
			k = key[base][displ];
			h = (it.unimi.dsi.fastutil.HashCommon.mix((long)((k).hashCode())));
			// The starting point.
			if (!((newKey[b = (int)((h & mask) >>> BigArrays.SEGMENT_SHIFT)][d = (int)(h & newSegmentMask)]) == null)) while (!((newKey[b = (b + ((d = (d + 1) & newSegmentMask) == 0 ? 1 : 0)) & newBaseMask][d]) == null));
			newKey[b][d] = k;
			base = (base + ((displ = (displ + 1) & segmentMask) == 0 ? 1 : 0));
		}
		this.n = newN;
		this.key = newKey;
		initMasks();
		maxFill = maxFill(n, f);
	}

	@Deprecated
	@Override
	public int size() {
		return (int)Math.min(Integer.MAX_VALUE, size);
	}

	@Override
	public long size64() {
		return size;
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	/**
	 * Returns a deep copy of this big set.
	 *
	 * <p>
	 * This method performs a deep copy of this big hash set; the data stored in the set, however, is
	 * not cloned. Note that this makes a difference only for object keys.
	 *
	 * @return a deep copy of this big set.
	 */
	@Override
	@SuppressWarnings("unchecked")
	public ObjectOpenHashBigSet<K> clone() {
		ObjectOpenHashBigSet<K> c;
		try {
			c = (ObjectOpenHashBigSet<K>)super.clone();
		} catch (CloneNotSupportedException cantHappen) {
			throw new InternalError();
		}
		c.key = copy(key);
		c.containsNull = containsNull;
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
		final K key[][] = this.key;
		int h = 0, base = 0, displ = 0;
		for (long j = realSize(); j-- != 0;) {
			while (((key[base][displ]) == null)) base = (base + ((displ = (displ + 1) & segmentMask) == 0 ? 1 : 0));
			if (this != key[base][displ]) h += ((key[base][displ]).hashCode());
			base = (base + ((displ = (displ + 1) & segmentMask) == 0 ? 1 : 0));
		}
		return h;
	}

	private void writeObject(java.io.ObjectOutputStream s) throws java.io.IOException {
		final ObjectIterator<K> i = iterator();
		s.defaultWriteObject();
		for (long j = size; j-- != 0;) s.writeObject(i.next());
	}

	@SuppressWarnings("unchecked")
	private void readObject(java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();
		n = bigArraySize(size, f);
		maxFill = maxFill(n, f);
		final K[][] key = this.key = (K[][])ObjectBigArrays.newBigArray(n);
		initMasks();
		long h;
		K k;
		int base, displ;
		for (long i = size; i-- != 0;) {
			k = (K)s.readObject();
			if (((k) == null)) containsNull = true;
			else {
				h = (it.unimi.dsi.fastutil.HashCommon.mix((long)((k).hashCode())));
				if (!((key[base = (int)((h & mask) >>> BigArrays.SEGMENT_SHIFT)][displ = (int)(h & segmentMask)]) == null)) while (!((key[base = (base + ((displ = (displ + 1) & segmentMask) == 0 ? 1 : 0)) & baseMask][displ]) == null));
				key[base][displ] = k;
			}
		}
		if (ASSERTS) checkTable();
	}

	private void checkTable() {
	}
}
