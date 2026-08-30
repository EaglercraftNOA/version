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
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import static it.unimi.dsi.fastutil.HashCommon.arraySize;
import static it.unimi.dsi.fastutil.HashCommon.maxFill;
import java.util.Map;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.AbstractBooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import it.unimi.dsi.fastutil.booleans.BooleanSpliterator;
import it.unimi.dsi.fastutil.booleans.BooleanSpliterators;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterators;

/**
 * A type-specific hash map with a fast, small-footprint implementation whose
 * {@linkplain it.unimi.dsi.fastutil.Hash.Strategy hashing strategy} is specified at creation time.
 *
 * <p>
 * Instances of this class use a hash table to represent a map. The table is filled up to a
 * specified <em>load factor</em>, and then doubled in size to accommodate new entries. If the table
 * is emptied below <em>one fourth</em> of the load factor, it is halved in size; however, the table
 * is never reduced to a size smaller than that at creation time: this approach makes it possible to
 * create maps with a large capacity in which insertions and deletions do not cause immediately
 * rehashing. Moreover, halving is not performed when deleting entries from an iterator, as it would
 * interfere with the iteration process.
 *
 * <p>
 * Note that {@link #clear()} does not modify the hash table size. Rather, a family of
 * {@linkplain #trim() trimming methods} lets you control the size of the table; this is
 * particularly useful if you reuse instances of this class.
 *
 * <p>
 * Entries returned by the type-specific {@link #entrySet()} method implement the suitable
 * type-specific {@link it.unimi.dsi.fastutil.Pair Pair} interface; only values are mutable.
 *
 * @see Hash
 * @see HashCommon
 */
public class Short2BooleanOpenCustomHashMap extends AbstractShort2BooleanMap implements java.io.Serializable, Cloneable, Hash {
	private static final long serialVersionUID = 0L;
	private static final boolean ASSERTS = false;
	/** The array of keys. */
	protected transient short[] key;
	/** The array of values. */
	protected transient boolean[] value;
	/** The mask for wrapping a position counter. */
	protected transient int mask;
	/** Whether this map contains the key zero. */
	protected transient boolean containsNullKey;
	/** The hash strategy of this custom map. */
	protected it.unimi.dsi.fastutil.shorts.ShortHash.Strategy strategy;
	/** The current table size. */
	protected transient int n;
	/** Threshold after which we rehash. It must be the table size times {@link #f}. */
	protected transient int maxFill;
	/** We never resize below this threshold, which is the construction-time {#n}. */
	protected final transient int minN;
	/** Number of entries in the set (including the key zero, if present). */
	protected int size;
	/** The acceptable load factor. */
	protected final float f;
	/** Cached set of entries. */
	protected transient FastEntrySet entries;
	/** Cached set of keys. */
	protected transient ShortSet keys;
	/** Cached collection of values. */
	protected transient BooleanCollection values;

	/**
	 * Creates a new hash map.
	 *
	 * <p>
	 * The actual table size will be the least power of two greater than {@code expected}/{@code f}.
	 *
	 * @param expected the expected number of elements in the hash map.
	 * @param f the load factor.
	 * @param strategy the strategy.
	 */

	public Short2BooleanOpenCustomHashMap(final int expected, final float f, final it.unimi.dsi.fastutil.shorts.ShortHash.Strategy strategy) {
		this.strategy = strategy;
		if (f <= 0 || f >= 1) throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than 1");
		if (expected < 0) throw new IllegalArgumentException("The expected number of elements must be nonnegative");
		this.f = f;
		minN = n = arraySize(expected, f);
		mask = n - 1;
		maxFill = maxFill(n, f);
		key = new short[n + 1];
		value = new boolean[n + 1];
	}

	/**
	 * Creates a new hash map with {@link Hash#DEFAULT_LOAD_FACTOR} as load factor.
	 *
	 * @param expected the expected number of elements in the hash map.
	 * @param strategy the strategy.
	 */
	public Short2BooleanOpenCustomHashMap(final int expected, final it.unimi.dsi.fastutil.shorts.ShortHash.Strategy strategy) {
		this(expected, DEFAULT_LOAD_FACTOR, strategy);
	}

	/**
	 * Creates a new hash map with initial expected {@link Hash#DEFAULT_INITIAL_SIZE} entries and
	 * {@link Hash#DEFAULT_LOAD_FACTOR} as load factor.
	 * 
	 * @param strategy the strategy.
	 */
	public Short2BooleanOpenCustomHashMap(final it.unimi.dsi.fastutil.shorts.ShortHash.Strategy strategy) {
		this(DEFAULT_INITIAL_SIZE, DEFAULT_LOAD_FACTOR, strategy);
	}

	/**
	 * Creates a new hash map copying a given one.
	 *
	 * @param m a {@link Map} to be copied into the new hash map.
	 * @param f the load factor.
	 * @param strategy the strategy.
	 */
	public Short2BooleanOpenCustomHashMap(final Map<? extends Short, ? extends Boolean> m, final float f, final it.unimi.dsi.fastutil.shorts.ShortHash.Strategy strategy) {
		this(m.size(), f, strategy);
		putAll(m);
	}

	/**
	 * Creates a new hash map with {@link Hash#DEFAULT_LOAD_FACTOR} as load factor copying a given one.
	 *
	 * @param m a {@link Map} to be copied into the new hash map.
	 * @param strategy the strategy.
	 */
	public Short2BooleanOpenCustomHashMap(final Map<? extends Short, ? extends Boolean> m, final it.unimi.dsi.fastutil.shorts.ShortHash.Strategy strategy) {
		this(m, DEFAULT_LOAD_FACTOR, strategy);
	}

	/**
	 * Creates a new hash map copying a given type-specific one.
	 *
	 * @param m a type-specific map to be copied into the new hash map.
	 * @param f the load factor.
	 * @param strategy the strategy.
	 */
	public Short2BooleanOpenCustomHashMap(final Short2BooleanMap m, final float f, final it.unimi.dsi.fastutil.shorts.ShortHash.Strategy strategy) {
		this(m.size(), f, strategy);
		putAll(m);
	}

	/**
	 * Creates a new hash map with {@link Hash#DEFAULT_LOAD_FACTOR} as load factor copying a given
	 * type-specific one.
	 *
	 * @param m a type-specific map to be copied into the new hash map.
	 * @param strategy the strategy.
	 */
	public Short2BooleanOpenCustomHashMap(final Short2BooleanMap m, final it.unimi.dsi.fastutil.shorts.ShortHash.Strategy strategy) {
		this(m, DEFAULT_LOAD_FACTOR, strategy);
	}

	/**
	 * Creates a new hash map using the elements of two parallel arrays.
	 *
	 * @param k the array of keys of the new hash map.
	 * @param v the array of corresponding values in the new hash map.
	 * @param f the load factor.
	 * @param strategy the strategy.
	 * @throws IllegalArgumentException if {@code k} and {@code v} have different lengths.
	 */
	public Short2BooleanOpenCustomHashMap(final short[] k, final boolean[] v, final float f, final it.unimi.dsi.fastutil.shorts.ShortHash.Strategy strategy) {
		this(k.length, f, strategy);
		if (k.length != v.length) throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
		for (int i = 0; i < k.length; i++) this.put(k[i], v[i]);
	}

	/**
	 * Creates a new hash map with {@link Hash#DEFAULT_LOAD_FACTOR} as load factor using the elements of
	 * two parallel arrays.
	 *
	 * @param k the array of keys of the new hash map.
	 * @param v the array of corresponding values in the new hash map.
	 * @param strategy the strategy.
	 * @throws IllegalArgumentException if {@code k} and {@code v} have different lengths.
	 */
	public Short2BooleanOpenCustomHashMap(final short[] k, final boolean[] v, final it.unimi.dsi.fastutil.shorts.ShortHash.Strategy strategy) {
		this(k, v, DEFAULT_LOAD_FACTOR, strategy);
	}

	/**
	 * Returns the hashing strategy.
	 *
	 * @return the hashing strategy of this custom hash map.
	 */
	public it.unimi.dsi.fastutil.shorts.ShortHash.Strategy strategy() {
		return strategy;
	}

	private int realSize() {
		return containsNullKey ? size - 1 : size;
	}

	/**
	 * Ensures that this map can hold a certain number of keys without rehashing.
	 *
	 * @param capacity a number of keys; there will be no rehashing unless the map {@linkplain #size()
	 *            size} exceeds this number.
	 */
	public void ensureCapacity(final int capacity) {
		final int needed = arraySize(capacity, f);
		if (needed > n) rehash(needed);
	}

	private void tryCapacity(final long capacity) {
		final int needed = (int)Math.min(1 << 30, Math.max(2, HashCommon.nextPowerOfTwo((long)Math.ceil(capacity / f))));
		if (needed > n) rehash(needed);
	}

	private boolean removeEntry(final int pos) {
		final boolean oldValue = value[pos];
		size--;
		shiftKeys(pos);
		if (n > minN && size < maxFill / 4 && n > DEFAULT_INITIAL_SIZE) rehash(n / 2);
		return oldValue;
	}

	private boolean removeNullEntry() {
		containsNullKey = false;
		final boolean oldValue = value[n];
		size--;
		if (n > minN && size < maxFill / 4 && n > DEFAULT_INITIAL_SIZE) rehash(n / 2);
		return oldValue;
	}

	@Override
	public void putAll(Map<? extends Short, ? extends Boolean> m) {
		if (f <= .5) ensureCapacity(m.size()); // The resulting map will be sized for m.size() elements
		else tryCapacity(size() + m.size()); // The resulting map will be tentatively sized for size() + m.size()
												// elements
		super.putAll(m);
	}

	private int find(final short k) {
		if ((strategy.equals((k), ((short)0)))) return containsNullKey ? n : -(n + 1);
		short curr;
		final short[] key = this.key;
		int pos;
		// The starting point.
		if (((curr = key[pos = (it.unimi.dsi.fastutil.HashCommon.mix(strategy.hashCode(k))) & mask]) == ((short)0))) return -(pos + 1);
		if ((strategy.equals((k), (curr)))) return pos;
		// There's always an unused entry.
		while (true) {
			if (((curr = key[pos = (pos + 1) & mask]) == ((short)0))) return -(pos + 1);
			if ((strategy.equals((k), (curr)))) return pos;
		}
	}

	private void insert(final int pos, final short k, final boolean v) {
		if (pos == n) containsNullKey = true;
		key[pos] = k;
		value[pos] = v;
		if (size++ >= maxFill) rehash(arraySize(size + 1, f));
		if (ASSERTS) checkTable();
	}

	@Override
	public boolean put(final short k, final boolean v) {
		final int pos = find(k);
		if (pos < 0) {
			insert(-pos - 1, k, v);
			return defRetValue;
		}
		final boolean oldValue = value[pos];
		value[pos] = v;
		return oldValue;
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
		short curr;
		final short[] key = this.key;
		for (;;) {
			pos = ((last = pos) + 1) & mask;
			for (;;) {
				if (((curr = key[pos]) == ((short)0))) {
					key[last] = ((short)0);
					return;
				}
				slot = (it.unimi.dsi.fastutil.HashCommon.mix(strategy.hashCode(curr))) & mask;
				if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
				pos = (pos + 1) & mask;
			}
			key[last] = curr;
			value[last] = value[pos];
		}
	}

	@Override

	public boolean remove(final short k) {
		if ((strategy.equals((k), ((short)0)))) {
			if (containsNullKey) return removeNullEntry();
			return defRetValue;
		}
		short curr;
		final short[] key = this.key;
		int pos;
		// The starting point.
		if (((curr = key[pos = (it.unimi.dsi.fastutil.HashCommon.mix(strategy.hashCode(k))) & mask]) == ((short)0))) return defRetValue;
		if ((strategy.equals((k), (curr)))) return removeEntry(pos);
		while (true) {
			if (((curr = key[pos = (pos + 1) & mask]) == ((short)0))) return defRetValue;
			if ((strategy.equals((k), (curr)))) return removeEntry(pos);
		}
	}

	@Override

	public boolean get(final short k) {
		if ((strategy.equals((k), ((short)0)))) return containsNullKey ? value[n] : defRetValue;
		short curr;
		final short[] key = this.key;
		int pos;
		// The starting point.
		if (((curr = key[pos = (it.unimi.dsi.fastutil.HashCommon.mix(strategy.hashCode(k))) & mask]) == ((short)0))) return defRetValue;
		if ((strategy.equals((k), (curr)))) return value[pos];
		// There's always an unused entry.
		while (true) {
			if (((curr = key[pos = (pos + 1) & mask]) == ((short)0))) return defRetValue;
			if ((strategy.equals((k), (curr)))) return value[pos];
		}
	}

	@Override

	public boolean containsKey(final short k) {
		if ((strategy.equals((k), ((short)0)))) return containsNullKey;
		short curr;
		final short[] key = this.key;
		int pos;
		// The starting point.
		if (((curr = key[pos = (it.unimi.dsi.fastutil.HashCommon.mix(strategy.hashCode(k))) & mask]) == ((short)0))) return false;
		if ((strategy.equals((k), (curr)))) return true;
		// There's always an unused entry.
		while (true) {
			if (((curr = key[pos = (pos + 1) & mask]) == ((short)0))) return false;
			if ((strategy.equals((k), (curr)))) return true;
		}
	}

	@Override
	public boolean containsValue(final boolean v) {
		final boolean value[] = this.value;
		final short key[] = this.key;
		if (containsNullKey && ((value[n]) == (v))) return true;
		for (int i = n; i-- != 0;) if (!((key[i]) == ((short)0)) && ((value[i]) == (v))) return true;
		return false;
	}

	/** {@inheritDoc} */
	@Override

	public boolean getOrDefault(final short k, final boolean defaultValue) {
		if ((strategy.equals((k), ((short)0)))) return containsNullKey ? value[n] : defaultValue;
		short curr;
		final short[] key = this.key;
		int pos;
		// The starting point.
		if (((curr = key[pos = (it.unimi.dsi.fastutil.HashCommon.mix(strategy.hashCode(k))) & mask]) == ((short)0))) return defaultValue;
		if ((strategy.equals((k), (curr)))) return value[pos];
		// There's always an unused entry.
		while (true) {
			if (((curr = key[pos = (pos + 1) & mask]) == ((short)0))) return defaultValue;
			if ((strategy.equals((k), (curr)))) return value[pos];
		}
	}

	/** {@inheritDoc} */
	@Override
	public boolean putIfAbsent(final short k, final boolean v) {
		final int pos = find(k);
		if (pos >= 0) return value[pos];
		insert(-pos - 1, k, v);
		return defRetValue;
	}

	/** {@inheritDoc} */
	@Override

	public boolean remove(final short k, final boolean v) {
		if ((strategy.equals((k), ((short)0)))) {
			if (containsNullKey && ((v) == (value[n]))) {
				removeNullEntry();
				return true;
			}
			return false;
		}
		short curr;
		final short[] key = this.key;
		int pos;
		// The starting point.
		if (((curr = key[pos = (it.unimi.dsi.fastutil.HashCommon.mix(strategy.hashCode(k))) & mask]) == ((short)0))) return false;
		if ((strategy.equals((k), (curr))) && ((v) == (value[pos]))) {
			removeEntry(pos);
			return true;
		}
		while (true) {
			if (((curr = key[pos = (pos + 1) & mask]) == ((short)0))) return false;
			if ((strategy.equals((k), (curr))) && ((v) == (value[pos]))) {
				removeEntry(pos);
				return true;
			}
		}
	}

	/** {@inheritDoc} */
	@Override
	public boolean replace(final short k, final boolean oldValue, final boolean v) {
		final int pos = find(k);
		if (pos < 0 || !((oldValue) == (value[pos]))) return false;
		value[pos] = v;
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public boolean replace(final short k, final boolean v) {
		final int pos = find(k);
		if (pos < 0) return defRetValue;
		final boolean oldValue = value[pos];
		value[pos] = v;
		return oldValue;
	}

	/** {@inheritDoc} */
	@Override
	public boolean computeIfAbsent(final short k, final java.util.function.IntPredicate mappingFunction) {
		java.util.Objects.requireNonNull(mappingFunction);
		final int pos = find(k);
		if (pos >= 0) return value[pos];
		final boolean newValue = mappingFunction.test(k);
		insert(-pos - 1, k, newValue);
		return newValue;
	}

	/** {@inheritDoc} */
	@Override
	public boolean computeIfAbsent(final short key, final Short2BooleanFunction mappingFunction) {
		java.util.Objects.requireNonNull(mappingFunction);
		final int pos = find(key);
		if (pos >= 0) return value[pos];
		if (!mappingFunction.containsKey(key)) return defRetValue;
		final boolean newValue = mappingFunction.get(key);
		insert(-pos - 1, key, newValue);
		return newValue;
	}

	/** {@inheritDoc} */
	@Override
	public boolean computeIfAbsentNullable(final short k, final java.util.function.IntFunction<? extends Boolean> mappingFunction) {
		java.util.Objects.requireNonNull(mappingFunction);
		final int pos = find(k);
		if (pos >= 0) return value[pos];
		final Boolean newValue = mappingFunction.apply(k);
		if (newValue == null) return defRetValue;
		final boolean v = (newValue).booleanValue();
		insert(-pos - 1, k, v);
		return v;
	}

	/** {@inheritDoc} */
	@Override
	public boolean computeIfPresent(final short k, final java.util.function.BiFunction<? super Short, ? super Boolean, ? extends Boolean> remappingFunction) {
		java.util.Objects.requireNonNull(remappingFunction);
		final int pos = find(k);
		if (pos < 0) return defRetValue;
		final Boolean newValue = remappingFunction.apply(Short.valueOf(k), Boolean.valueOf(value[pos]));
		if (newValue == null) {
			if ((strategy.equals((k), ((short)0)))) removeNullEntry();
			else removeEntry(pos);
			return defRetValue;
		}
		return value[pos] = (newValue).booleanValue();
	}

	/** {@inheritDoc} */
	@Override
	public boolean compute(final short k, final java.util.function.BiFunction<? super Short, ? super Boolean, ? extends Boolean> remappingFunction) {
		java.util.Objects.requireNonNull(remappingFunction);
		final int pos = find(k);
		final Boolean newValue = remappingFunction.apply(Short.valueOf(k), pos >= 0 ? Boolean.valueOf(value[pos]) : null);
		if (newValue == null) {
			if (pos >= 0) {
				if ((strategy.equals((k), ((short)0)))) removeNullEntry();
				else removeEntry(pos);
			}
			return defRetValue;
		}
		boolean newVal = (newValue).booleanValue();
		if (pos < 0) {
			insert(-pos - 1, k, newVal);
			return newVal;
		}
		return value[pos] = newVal;
	}

	/** {@inheritDoc} */
	@Override
	public boolean merge(final short k, final boolean v, final java.util.function.BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> remappingFunction) {
		java.util.Objects.requireNonNull(remappingFunction);

		final int pos = find(k);
		if (pos < 0) {
			if (pos < 0) insert(-pos - 1, k, v);
			else value[pos] = v;
			return v;
		}
		final Boolean newValue = remappingFunction.apply(Boolean.valueOf(value[pos]), Boolean.valueOf(v));
		if (newValue == null) {
			if ((strategy.equals((k), ((short)0)))) removeNullEntry();
			else removeEntry(pos);
			return defRetValue;
		}
		return value[pos] = (newValue).booleanValue();
	}

	/* Removes all elements from this map.
	 *
	 * <p>To increase object reuse, this method does not change the table size.
	 * If you want to reduce the table size, you must use {@link #trim()}.
	 *
	 */
	@Override
	public void clear() {
		if (size == 0) return;
		size = 0;
		containsNullKey = false;
		Arrays.fill(key, ((short)0));
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	/**
	 * The entry class for a hash map does not record key and value, but rather the position in the hash
	 * table of the corresponding entry. This is necessary so that calls to
	 * {@link java.util.Map.Entry#setValue(Object)} are reflected in the map
	 */
	final class MapEntry implements Short2BooleanMap.Entry, Map.Entry<Short, Boolean>, ShortBooleanPair {
		// The table index this entry refers to, or -1 if this entry has been deleted.
		int index;

		MapEntry(final int index) {
			this.index = index;
		}

		MapEntry() {
		}

		@Override
		public short getShortKey() {
			return key[index];
		}

		@Override
		public short leftShort() {
			return key[index];
		}

		@Override
		public boolean getBooleanValue() {
			return value[index];
		}

		@Override
		public boolean rightBoolean() {
			return value[index];
		}

		@Override
		public boolean setValue(final boolean v) {
			final boolean oldValue = value[index];
			value[index] = v;
			return oldValue;
		}

		@Override
		public ShortBooleanPair right(final boolean v) {
			value[index] = v;
			return this;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public Short getKey() {
			return Short.valueOf(key[index]);
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public Boolean getValue() {
			return Boolean.valueOf(value[index]);
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public Boolean setValue(final Boolean v) {
			return Boolean.valueOf(setValue((v).booleanValue()));
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean equals(final Object o) {
			if (!(o instanceof Map.Entry)) return false;
			Map.Entry<Short, Boolean> e = (Map.Entry<Short, Boolean>)o;
			return (strategy.equals((key[index]), ((e.getKey()).shortValue()))) && ((value[index]) == ((e.getValue()).booleanValue()));
		}

		@Override
		public int hashCode() {
			return (strategy.hashCode(key[index])) ^ (value[index] ? 1231 : 1237);
		}

		@Override
		public String toString() {
			return key[index] + "=>" + value[index];
		}
	}

	/** An iterator over a hash map. */
	private abstract class MapIterator<ConsumerType> {
		/**
		 * The index of the last entry returned, if positive or zero; initially, {@link #n}. If negative,
		 * the last entry returned was that of the key of index {@code - pos - 1} from the {@link #wrapped}
		 * list.
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
		/** A boolean telling us whether we should return the entry with the null key. */
		boolean mustReturnNullKey = Short2BooleanOpenCustomHashMap.this.containsNullKey;
		/**
		 * A lazily allocated list containing keys of entries that have wrapped around the table because of
		 * removals.
		 */
		ShortArrayList wrapped;

		@SuppressWarnings("unused")
		abstract void acceptOnIndex(final ConsumerType action, final int index);

		public boolean hasNext() {
			return c != 0;
		}

		public int nextEntry() {
			if (!hasNext()) throw new NoSuchElementException();
			c--;
			if (mustReturnNullKey) {
				mustReturnNullKey = false;
				return last = n;
			}
			final short key[] = Short2BooleanOpenCustomHashMap.this.key;
			for (;;) {
				if (--pos < 0) {
					// We are just enumerating elements from the wrapped list.
					last = Integer.MIN_VALUE;
					final short k = wrapped.getShort(-pos - 1);
					int p = (it.unimi.dsi.fastutil.HashCommon.mix(strategy.hashCode(k))) & mask;
					while (!(strategy.equals((k), (key[p])))) p = (p + 1) & mask;
					return p;
				}
				if (!((key[pos]) == ((short)0))) return last = pos;
			}
		}

		public void forEachRemaining(final ConsumerType action) {
			if (mustReturnNullKey) {
				mustReturnNullKey = false;
				acceptOnIndex(action, last = n);
				c--;
			}
			final short key[] = Short2BooleanOpenCustomHashMap.this.key;
			while (c != 0) {
				if (--pos < 0) {
					// We are just enumerating elements from the wrapped list.
					last = Integer.MIN_VALUE;
					final short k = wrapped.getShort(-pos - 1);
					int p = (it.unimi.dsi.fastutil.HashCommon.mix(strategy.hashCode(k))) & mask;
					while (!(strategy.equals((k), (key[p])))) p = (p + 1) & mask;
					acceptOnIndex(action, p);
					c--;
				} else if (!((key[pos]) == ((short)0))) {
					acceptOnIndex(action, last = pos);
					c--;
				}
			}
		}

		/**
		 * Shifts left entries with the specified hash code, starting at the specified position, and empties
		 * the resulting free entry.
		 *
		 * @param pos a starting position.
		 */
		private void shiftKeys(int pos) {
			// Shift entries with the same hash.
			int last, slot;
			short curr;
			final short[] key = Short2BooleanOpenCustomHashMap.this.key;
			for (;;) {
				pos = ((last = pos) + 1) & mask;
				for (;;) {
					if (((curr = key[pos]) == ((short)0))) {
						key[last] = ((short)0);
						return;
					}
					slot = (it.unimi.dsi.fastutil.HashCommon.mix(strategy.hashCode(curr))) & mask;
					if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) break;
					pos = (pos + 1) & mask;
				}
				if (pos < last) { // Wrapped entry.
					if (wrapped == null) wrapped = new ShortArrayList(2);
					wrapped.add(key[pos]);
				}
				key[last] = curr;
				value[last] = value[pos];
			}
		}

		public void remove() {
			if (last == -1) throw new IllegalStateException();
			if (last == n) {
				containsNullKey = false;
			} else if (pos >= 0) shiftKeys(last);
			else {
				// We're removing wrapped entries.
				Short2BooleanOpenCustomHashMap.this.remove(wrapped.getShort(-pos - 1));
				last = -1; // Note that we must not decrement size
				return;
			}
			size--;
			last = -1; // You can no longer remove this entry.
			if (ASSERTS) checkTable();
		}

		public int skip(final int n) {
			int i = n;
			while (i-- != 0 && hasNext()) nextEntry();
			return n - i - 1;
		}
	}

	private final class EntryIterator extends MapIterator<Consumer<? super Short2BooleanMap.Entry>> implements ObjectIterator<Short2BooleanMap.Entry> {
		private MapEntry entry;

		@Override
		public MapEntry next() {
			return entry = new MapEntry(nextEntry());
		}

		// forEachRemaining inherited from MapIterator superclass.
		@Override
		final void acceptOnIndex(final Consumer<? super Short2BooleanMap.Entry> action, final int index) {
			action.accept(entry = new MapEntry(index));
		}

		@Override
		public void remove() {
			super.remove();
			entry.index = -1; // You cannot use a deleted entry.
		}
	}

	private final class FastEntryIterator extends MapIterator<Consumer<? super Short2BooleanMap.Entry>> implements ObjectIterator<Short2BooleanMap.Entry> {
		private final MapEntry entry = new MapEntry();

		@Override
		public MapEntry next() {
			entry.index = nextEntry();
			return entry;
		}

		// forEachRemaining inherited from MapIterator superclass.
		@Override
		final void acceptOnIndex(final Consumer<? super Short2BooleanMap.Entry> action, final int index) {
			entry.index = index;
			action.accept(entry);
		}
	}

	private abstract class MapSpliterator<ConsumerType, SplitType extends MapSpliterator<ConsumerType, SplitType>> {
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
		boolean mustReturnNull = Short2BooleanOpenCustomHashMap.this.containsNullKey;
		boolean hasSplit = false;

		MapSpliterator() {
		}

		MapSpliterator(int pos, int max, boolean mustReturnNull, boolean hasSplit) {
			this.pos = pos;
			this.max = max;
			this.mustReturnNull = mustReturnNull;
			this.hasSplit = hasSplit;
		}

		abstract void acceptOnIndex(final ConsumerType action, final int index);

		abstract SplitType makeForSplit(int pos, int max, boolean mustReturnNull);

		public boolean tryAdvance(final ConsumerType action) {
			if (mustReturnNull) {
				mustReturnNull = false;
				++c;
				acceptOnIndex(action, n);
				return true;
			}
			final short key[] = Short2BooleanOpenCustomHashMap.this.key;
			while (pos < max) {
				if (!((key[pos]) == ((short)0))) {
					++c;
					acceptOnIndex(action, pos++);
					return true;
				}
				++pos;
			}
			return false;
		}

		public void forEachRemaining(final ConsumerType action) {
			if (mustReturnNull) {
				mustReturnNull = false;
				++c;
				acceptOnIndex(action, n);
			}
			final short key[] = Short2BooleanOpenCustomHashMap.this.key;
			while (pos < max) {
				if (!((key[pos]) == ((short)0))) {
					acceptOnIndex(action, pos);
					++c;
				}
				++pos;
			}
		}

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

		public SplitType trySplit() {
			if (pos >= max - 1) return null;
			int retLen = (max - pos) >> 1;
			if (retLen <= 1) return null;
			int myNewPos = pos + retLen;
			int retPos = pos;
			int retMax = myNewPos;
			// Since null is returned first, and the convention is that the returned split is the prefix of
			// elements,
			// the split will take care of returning null (if needed), and we won't return it anymore.
			SplitType split = makeForSplit(retPos, retMax, mustReturnNull);
			this.pos = myNewPos;
			this.mustReturnNull = false;
			this.hasSplit = true;
			return split;
		}

		public long skip(long n) {
			if (n < 0) throw new IllegalArgumentException("Argument must be nonnegative: " + n);
			if (n == 0) return 0;
			long skipped = 0;
			if (mustReturnNull) {
				mustReturnNull = false;
				++skipped;
				--n;
			}
			final short key[] = Short2BooleanOpenCustomHashMap.this.key;
			while (pos < max && n > 0) {
				if (!((key[pos++]) == ((short)0))) {
					++skipped;
					--n;
				}
			}
			return skipped;
		}
	}

	private final class EntrySpliterator extends MapSpliterator<Consumer<? super Short2BooleanMap.Entry>, EntrySpliterator> implements ObjectSpliterator<Short2BooleanMap.Entry> {
		private static final int POST_SPLIT_CHARACTERISTICS = ObjectSpliterators.SET_SPLITERATOR_CHARACTERISTICS & ~java.util.Spliterator.SIZED;

		EntrySpliterator() {
		}

		EntrySpliterator(int pos, int max, boolean mustReturnNull, boolean hasSplit) {
			super(pos, max, mustReturnNull, hasSplit);
		}

		@Override
		public int characteristics() {
			return hasSplit ? POST_SPLIT_CHARACTERISTICS : ObjectSpliterators.SET_SPLITERATOR_CHARACTERISTICS;
		}

		@Override
		final void acceptOnIndex(final Consumer<? super Short2BooleanMap.Entry> action, final int index) {
			action.accept(new MapEntry(index));
		}

		@Override
		final EntrySpliterator makeForSplit(int pos, int max, boolean mustReturnNull) {
			return new EntrySpliterator(pos, max, mustReturnNull, true);
		}
	}

	private final class MapEntrySet extends AbstractObjectSet<Short2BooleanMap.Entry> implements FastEntrySet {
		@Override
		public ObjectIterator<Short2BooleanMap.Entry> iterator() {
			return new EntryIterator();
		}

		@Override
		public ObjectIterator<Short2BooleanMap.Entry> fastIterator() {
			return new FastEntryIterator();
		}

		@Override
		public ObjectSpliterator<Short2BooleanMap.Entry> spliterator() {
			return new EntrySpliterator();
		}

		//
		@Override

		public boolean contains(final Object o) {
			if (!(o instanceof Map.Entry)) return false;
			final Map.Entry<?, ?> e = (Map.Entry<?, ?>)o;
			if (e.getKey() == null || !(e.getKey() instanceof Short)) return false;
			if (e.getValue() == null || !(e.getValue() instanceof Boolean)) return false;
			final short k = ((Short)(e.getKey())).shortValue();
			final boolean v = ((Boolean)(e.getValue())).booleanValue();
			if ((strategy.equals((k), ((short)0)))) return Short2BooleanOpenCustomHashMap.this.containsNullKey && ((value[n]) == (v));
			short curr;
			final short[] key = Short2BooleanOpenCustomHashMap.this.key;
			int pos;
			// The starting point.
			if (((curr = key[pos = (it.unimi.dsi.fastutil.HashCommon.mix(strategy.hashCode(k))) & mask]) == ((short)0))) return false;
			if ((strategy.equals((k), (curr)))) return ((value[pos]) == (v));
			// There's always an unused entry.
			while (true) {
				if (((curr = key[pos = (pos + 1) & mask]) == ((short)0))) return false;
				if ((strategy.equals((k), (curr)))) return ((value[pos]) == (v));
			}
		}

		@Override

		public boolean remove(final Object o) {
			if (!(o instanceof Map.Entry)) return false;
			final Map.Entry<?, ?> e = (Map.Entry<?, ?>)o;
			if (e.getKey() == null || !(e.getKey() instanceof Short)) return false;
			if (e.getValue() == null || !(e.getValue() instanceof Boolean)) return false;
			final short k = ((Short)(e.getKey())).shortValue();
			final boolean v = ((Boolean)(e.getValue())).booleanValue();
			if ((strategy.equals((k), ((short)0)))) {
				if (containsNullKey && ((value[n]) == (v))) {
					removeNullEntry();
					return true;
				}
				return false;
			}
			short curr;
			final short[] key = Short2BooleanOpenCustomHashMap.this.key;
			int pos;
			// The starting point.
			if (((curr = key[pos = (it.unimi.dsi.fastutil.HashCommon.mix(strategy.hashCode(k))) & mask]) == ((short)0))) return false;
			if ((strategy.equals((curr), (k)))) {
				if (((value[pos]) == (v))) {
					removeEntry(pos);
					return true;
				}
				return false;
			}
			while (true) {
				if (((curr = key[pos = (pos + 1) & mask]) == ((short)0))) return false;
				if ((strategy.equals((curr), (k)))) {
					if (((value[pos]) == (v))) {
						removeEntry(pos);
						return true;
					}
				}
			}
		}

		@Override
		public int size() {
			return size;
		}

		@Override
		public void clear() {
			Short2BooleanOpenCustomHashMap.this.clear();
		}

		/** {@inheritDoc} */
		@Override
		public void forEach(final Consumer<? super Short2BooleanMap.Entry> consumer) {
			if (containsNullKey) consumer.accept(new MapEntry(n));
			for (int pos = n; pos-- != 0;) if (!((key[pos]) == ((short)0))) consumer.accept(new MapEntry(pos));
		}

		/** {@inheritDoc} */
		@Override
		public void fastForEach(final Consumer<? super Short2BooleanMap.Entry> consumer) {
			final MapEntry entry = new MapEntry();
			if (containsNullKey) {
				entry.index = n;
				consumer.accept(entry);
			}
			for (int pos = n; pos-- != 0;) if (!((key[pos]) == ((short)0))) {
				entry.index = pos;
				consumer.accept(entry);
			}
		}
	}

	@Override
	public FastEntrySet short2BooleanEntrySet() {
		if (entries == null) entries = new MapEntrySet();
		return entries;
	}

	/**
	 * An iterator on keys.
	 *
	 * <p>
	 * We simply override the
	 * {@link java.util.ListIterator#next()}/{@link java.util.ListIterator#previous()} methods (and
	 * possibly their type-specific counterparts) so that they return keys instead of entries.
	 */
	private final class KeyIterator extends MapIterator<ShortConsumer> implements ShortIterator {
		public KeyIterator() {
			super();
		}

		// forEachRemaining inherited from MapIterator superclass.
		// Despite the superclass declared with generics, the way Java inherits and generates bridge methods
		// avoids the boxing/unboxing
		@Override
		final void acceptOnIndex(final ShortConsumer action, final int index) {
			action.accept(key[index]);
		}

		@Override
		public short nextShort() {
			return key[nextEntry()];
		}
	}

	private final class KeySpliterator extends MapSpliterator<ShortConsumer, KeySpliterator> implements ShortSpliterator {
		private static final int POST_SPLIT_CHARACTERISTICS = ShortSpliterators.SET_SPLITERATOR_CHARACTERISTICS & ~java.util.Spliterator.SIZED;

		KeySpliterator() {
		}

		KeySpliterator(int pos, int max, boolean mustReturnNull, boolean hasSplit) {
			super(pos, max, mustReturnNull, hasSplit);
		}

		@Override
		public int characteristics() {
			return hasSplit ? POST_SPLIT_CHARACTERISTICS : ShortSpliterators.SET_SPLITERATOR_CHARACTERISTICS;
		}

		@Override
		final void acceptOnIndex(final ShortConsumer action, final int index) {
			action.accept(key[index]);
		}

		@Override
		final KeySpliterator makeForSplit(int pos, int max, boolean mustReturnNull) {
			return new KeySpliterator(pos, max, mustReturnNull, true);
		}
	}

	private final class KeySet extends AbstractShortSet {
		@Override
		public ShortIterator iterator() {
			return new KeyIterator();
		}

		@Override
		public ShortSpliterator spliterator() {
			return new KeySpliterator();
		}

		/** {@inheritDoc} */
		@Override
		public void forEach(final ShortConsumer consumer) {
			if (containsNullKey) consumer.accept(key[n]);
			for (int pos = n; pos-- != 0;) {
				final short k = key[pos];
				if (!((k) == ((short)0))) consumer.accept(k);
			}
		}

		@Override
		public int size() {
			return size;
		}

		@Override
		public boolean contains(short k) {
			return containsKey(k);
		}

		@Override
		public boolean remove(short k) {
			final int oldSize = size;
			Short2BooleanOpenCustomHashMap.this.remove(k);
			return size != oldSize;
		}

		@Override
		public void clear() {
			Short2BooleanOpenCustomHashMap.this.clear();
		}
	}

	@Override
	public ShortSet keySet() {
		if (keys == null) keys = new KeySet();
		return keys;
	}

	/**
	 * An iterator on values.
	 *
	 * <p>
	 * We simply override the
	 * {@link java.util.ListIterator#next()}/{@link java.util.ListIterator#previous()} methods (and
	 * possibly their type-specific counterparts) so that they return values instead of entries.
	 */
	private final class ValueIterator extends MapIterator<BooleanConsumer> implements BooleanIterator {
		public ValueIterator() {
			super();
		}

		// forEachRemaining inherited from MapIterator superclass.
		// Despite the superclass declared with generics, the way Java inherits and generates bridge methods
		// avoids the boxing/unboxing
		@Override
		final void acceptOnIndex(final BooleanConsumer action, final int index) {
			action.accept(value[index]);
		}

		@Override
		public boolean nextBoolean() {
			return value[nextEntry()];
		}
	}

	private final class ValueSpliterator extends MapSpliterator<BooleanConsumer, ValueSpliterator> implements BooleanSpliterator {
		private static final int POST_SPLIT_CHARACTERISTICS = BooleanSpliterators.COLLECTION_SPLITERATOR_CHARACTERISTICS & ~java.util.Spliterator.SIZED;

		ValueSpliterator() {
		}

		ValueSpliterator(int pos, int max, boolean mustReturnNull, boolean hasSplit) {
			super(pos, max, mustReturnNull, hasSplit);
		}

		@Override
		public int characteristics() {
			return hasSplit ? POST_SPLIT_CHARACTERISTICS : BooleanSpliterators.COLLECTION_SPLITERATOR_CHARACTERISTICS;
		}

		@Override
		final void acceptOnIndex(final BooleanConsumer action, final int index) {
			action.accept(value[index]);
		}

		@Override
		final ValueSpliterator makeForSplit(int pos, int max, boolean mustReturnNull) {
			return new ValueSpliterator(pos, max, mustReturnNull, true);
		}
	}

	@Override
	public BooleanCollection values() {
		if (values == null) values = new AbstractBooleanCollection() {
			@Override
			public BooleanIterator iterator() {
				return new ValueIterator();
			}

			@Override
			public BooleanSpliterator spliterator() {
				return new ValueSpliterator();
			}

			/** {@inheritDoc} */
			@Override
			public void forEach(final BooleanConsumer consumer) {
				if (containsNullKey) consumer.accept(value[n]);
				for (int pos = n; pos-- != 0;) if (!((key[pos]) == ((short)0))) consumer.accept(value[pos]);
			}

			@Override
			public int size() {
				return size;
			}

			@Override
			public boolean contains(boolean v) {
				return containsValue(v);
			}

			@Override
			public void clear() {
				Short2BooleanOpenCustomHashMap.this.clear();
			}
		};
		return values;
	}

	/**
	 * Rehashes the map, making the table as small as possible.
	 *
	 * <p>
	 * This method rehashes the table to the smallest size satisfying the load factor. It can be used
	 * when the set will not be changed anymore, so to optimize access speed and size.
	 *
	 * <p>
	 * If the table size is already the minimum possible, this method does nothing.
	 *
	 * @return true if there was enough memory to trim the map.
	 * @see #trim(int)
	 */
	public boolean trim() {
		return trim(size);
	}

	/**
	 * Rehashes this map if the table is too large.
	 *
	 * <p>
	 * Let <var>N</var> be the smallest table size that can hold <code>max(n,{@link #size()})</code>
	 * entries, still satisfying the load factor. If the current table size is smaller than or equal to
	 * <var>N</var>, this method does nothing. Otherwise, it rehashes this map in a table of size
	 * <var>N</var>.
	 *
	 * <p>
	 * This method is useful when reusing maps. {@linkplain #clear() Clearing a map} leaves the table
	 * size untouched. If you are reusing a map many times, you can call this method with a typical size
	 * to avoid keeping around a very large table just because of a few large transient maps.
	 *
	 * @param n the threshold for the trimming.
	 * @return true if there was enough memory to trim the map.
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
	 * Rehashes the map.
	 *
	 * <p>
	 * This method implements the basic rehashing strategy, and may be overridden by subclasses
	 * implementing different rehashing strategies (e.g., disk-based rehashing). However, you should not
	 * override this method unless you understand the internal workings of this class.
	 *
	 * @param newN the new size
	 */

	protected void rehash(final int newN) {
		final short key[] = this.key;
		final boolean value[] = this.value;
		final int mask = newN - 1; // Note that this is used by the hashing macro
		final short newKey[] = new short[newN + 1];
		final boolean newValue[] = new boolean[newN + 1];
		int i = n, pos;
		for (int j = realSize(); j-- != 0;) {
			while (((key[--i]) == ((short)0)));
			if (!((newKey[pos = (it.unimi.dsi.fastutil.HashCommon.mix(strategy.hashCode(key[i]))) & mask]) == ((short)0))) while (!((newKey[pos = (pos + 1) & mask]) == ((short)0)));
			newKey[pos] = key[i];
			newValue[pos] = value[i];
		}
		newValue[newN] = value[n];
		n = newN;
		this.mask = mask;
		maxFill = maxFill(n, f);
		this.key = newKey;
		this.value = newValue;
	}

	/**
	 * Returns a deep copy of this map.
	 *
	 * <p>
	 * This method performs a deep copy of this hash map; the data stored in the map, however, is not
	 * cloned. Note that this makes a difference only for object keys.
	 *
	 * @return a deep copy of this map.
	 */
	@Override

	public Short2BooleanOpenCustomHashMap clone() {
		Short2BooleanOpenCustomHashMap c;
		try {
			c = (Short2BooleanOpenCustomHashMap)super.clone();
		} catch (CloneNotSupportedException cantHappen) {
			throw new InternalError();
		}
		c.keys = null;
		c.values = null;
		c.entries = null;
		c.containsNullKey = containsNullKey;
		c.key = key.clone();
		c.value = value.clone();
		c.strategy = strategy;
		return c;
	}

	/**
	 * Returns a hash code for this map.
	 *
	 * This method overrides the generic method provided by the superclass. Since {@code equals()} is
	 * not overriden, it is important that the value returned by this method is the same value as the
	 * one returned by the overriden method.
	 *
	 * @return a hash code for this map.
	 */
	@Override
	public int hashCode() {
		int h = 0;
		for (int j = realSize(), i = 0, t = 0; j-- != 0;) {
			while (((key[i]) == ((short)0))) i++;
			t = (strategy.hashCode(key[i]));
			t ^= (value[i] ? 1231 : 1237);
			h += t;
			i++;
		}
		// Zero / null keys have hash zero.
		if (containsNullKey) h += (value[n] ? 1231 : 1237);
		return h;
	}

	private void writeObject(java.io.ObjectOutputStream s) throws java.io.IOException {
		final short key[] = this.key;
		final boolean value[] = this.value;
		final EntryIterator i = new EntryIterator();
		s.defaultWriteObject();
		for (int j = size, e; j-- != 0;) {
			e = i.nextEntry();
			s.writeShort(key[e]);
			s.writeBoolean(value[e]);
		}
	}

	private void readObject(java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();
		n = arraySize(size, f);
		maxFill = maxFill(n, f);
		mask = n - 1;
		final short key[] = this.key = new short[n + 1];
		final boolean value[] = this.value = new boolean[n + 1];
		short k;
		boolean v;
		for (int i = size, pos; i-- != 0;) {
			k = s.readShort();
			v = s.readBoolean();
			if ((strategy.equals((k), ((short)0)))) {
				pos = n;
				containsNullKey = true;
			} else {
				pos = (it.unimi.dsi.fastutil.HashCommon.mix(strategy.hashCode(k))) & mask;
				while (!((key[pos]) == ((short)0))) pos = (pos + 1) & mask;
			}
			key[pos] = k;
			value[pos] = v;
		}
		if (ASSERTS) checkTable();
	}

	private void checkTable() {
	}
}
