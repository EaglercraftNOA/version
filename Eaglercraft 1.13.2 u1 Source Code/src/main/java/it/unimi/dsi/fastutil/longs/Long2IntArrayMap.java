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
package it.unimi.dsi.fastutil.longs;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Consumer;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterators;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.AbstractIntCollection;
import it.unimi.dsi.fastutil.ints.IntArrays;

/**
 * A simple, brute-force implementation of a map based on two parallel backing arrays.
 *
 * <p>
 * The main purpose of this implementation is that of wrapping cleanly the brute-force approach to
 * the storage of a very small number of pairs: just put them into two parallel arrays and scan
 * linearly to find an item.
 */
public class Long2IntArrayMap extends AbstractLong2IntMap implements java.io.Serializable, Cloneable {
	private static final long serialVersionUID = 1L;
	/** The keys (valid up to {@link #size}, excluded). */
	protected transient long[] key;
	/** The values (parallel to {@link #key}). */
	protected transient int[] value;
	/** The number of valid entries in {@link #key} and {@link #value}. */
	protected int size;
	/** Cached set of entries. */
	protected transient FastEntrySet entries;
	/** Cached set of keys. */
	protected transient LongSet keys;
	/** Cached collection of values. */
	protected transient IntCollection values;

	/**
	 * Creates a new empty array map with given key and value backing arrays. The resulting map will
	 * have as many entries as the given arrays.
	 *
	 * <p>
	 * It is responsibility of the caller that the elements of {@code key} are distinct.
	 *
	 * @param key the key array.
	 * @param value the value array (it <em>must</em> have the same length as {@code key}).
	 */
	public Long2IntArrayMap(final long[] key, final int[] value) {
		this.key = key;
		this.value = value;
		size = key.length;
		if (key.length != value.length) throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
	}

	/**
	 * Creates a new empty array map.
	 */
	public Long2IntArrayMap() {
		this.key = LongArrays.EMPTY_ARRAY;
		this.value = IntArrays.EMPTY_ARRAY;
	}

	/**
	 * Creates a new empty array map of given capacity.
	 *
	 * @param capacity the initial capacity.
	 */
	public Long2IntArrayMap(final int capacity) {
		this.key = new long[capacity];
		this.value = new int[capacity];
	}

	/**
	 * Creates a new empty array map copying the entries of a given map.
	 *
	 * @param m a map.
	 */
	public Long2IntArrayMap(final Long2IntMap m) {
		this(m.size());
		int i = 0;
		for (Long2IntMap.Entry e : m.long2IntEntrySet()) {
			key[i] = e.getLongKey();
			value[i] = e.getIntValue();
			i++;
		}
		size = i;
	}

	/**
	 * Creates a new empty array map copying the entries of a given map.
	 *
	 * @param m a map.
	 */
	public Long2IntArrayMap(final Map<? extends Long, ? extends Integer> m) {
		this(m.size());
		int i = 0;
		for (Map.Entry<? extends Long, ? extends Integer> e : m.entrySet()) {
			key[i] = (e.getKey()).longValue();
			value[i] = (e.getValue()).intValue();
			i++;
		}
		size = i;
	}

	/**
	 * Creates a new array map with given key and value backing arrays, using the given number of
	 * elements.
	 *
	 * <p>
	 * It is responsibility of the caller that the first {@code size} elements of {@code key} are
	 * distinct.
	 *
	 * @param key the key array.
	 * @param value the value array (it <em>must</em> have the same length as {@code key}).
	 * @param size the number of valid elements in {@code key} and {@code value}.
	 */
	public Long2IntArrayMap(final long[] key, final int[] value, final int size) {
		this.key = key;
		this.value = value;
		this.size = size;
		if (key.length != value.length) throw new IllegalArgumentException("Keys and values have different lengths (" + key.length + ", " + value.length + ")");
		if (size > key.length) throw new IllegalArgumentException("The provided size (" + size + ") is larger than or equal to the backing-arrays size (" + key.length + ")");
	}

	private final class EntrySet extends AbstractObjectSet<Long2IntMap.Entry> implements FastEntrySet {
		// TODO Maybe make this return a list-iterator like the LinkedXHashMaps do? (same for other
		// collection view types)
		@Override
		public ObjectIterator<Long2IntMap.Entry> iterator() {
			return new ObjectIterator<Long2IntMap.Entry>() {
				int curr = -1, next = 0;

				@Override
				public boolean hasNext() {
					return next < size;
				}

				@Override

				public Entry next() {
					if (!hasNext()) throw new NoSuchElementException();
					return new AbstractLong2IntMap.BasicEntry(key[curr = next], value[next++]);
				}

				@Override
				public void remove() {
					if (curr == -1) throw new IllegalStateException();
					curr = -1;
					final int tail = size-- - next--;
					System.arraycopy(key, next + 1, key, next, tail);
					System.arraycopy(value, next + 1, value, next, tail);
				}

				@Override

				public void forEachRemaining(final Consumer<? super Long2IntMap.Entry> action) {
					// Hoist containing class field ref into local
					final int max = size;
					while (next < max) {
						action.accept(new AbstractLong2IntMap.BasicEntry(key[curr = next], value[next++]));
					}
				}
			};
		}

		@Override
		public ObjectIterator<Long2IntMap.Entry> fastIterator() {
			return new ObjectIterator<Long2IntMap.Entry>() {
				int next = 0, curr = -1;
				final BasicEntry entry = new BasicEntry();

				@Override
				public boolean hasNext() {
					return next < size;
				}

				@Override

				public Entry next() {
					if (!hasNext()) throw new NoSuchElementException();
					entry.key = key[curr = next];
					entry.value = value[next++];
					return entry;
				}

				@Override
				public void remove() {
					if (curr == -1) throw new IllegalStateException();
					curr = -1;
					final int tail = size-- - next--;
					System.arraycopy(key, next + 1, key, next, tail);
					System.arraycopy(value, next + 1, value, next, tail);
				}

				@Override

				public void forEachRemaining(final Consumer<? super Long2IntMap.Entry> action) {
					// Hoist containing class field ref into local
					final int max = size;
					while (next < max) {
						entry.key = key[curr = next];
						entry.value = value[next++];
						action.accept(entry);
					}
				}
			};
		}

		// We already have to create an Entry object for each iteration, so the overhead from having
		// skeletal implementations isn't significant.
		final class EntrySetSpliterator extends ObjectSpliterators.EarlyBindingSizeIndexBasedSpliterator<Long2IntMap.Entry> implements ObjectSpliterator<Long2IntMap.Entry> {
			EntrySetSpliterator(int pos, int maxPos) {
				super(pos, maxPos);
			}

			@Override
			public int characteristics() {
				return ObjectSpliterators.SET_SPLITERATOR_CHARACTERISTICS | java.util.Spliterator.SUBSIZED | java.util.Spliterator.ORDERED;
			}

			@Override

			protected final Long2IntMap.Entry get(int location) {
				return new AbstractLong2IntMap.BasicEntry(key[location], value[location]);
			}

			@Override
			protected final EntrySetSpliterator makeForSplit(int pos, int maxPos) {
				return new EntrySetSpliterator(pos, maxPos);
			}
		}

		@Override
		public ObjectSpliterator<Long2IntMap.Entry> spliterator() {
			return new EntrySetSpliterator(0, size);
		}

		/** {@inheritDoc} */
		@Override

		public void forEach(final Consumer<? super Long2IntMap.Entry> action) {
			// Hoist containing class field ref into local
			for (int i = 0, max = size; i < max; ++i) {
				action.accept(new AbstractLong2IntMap.BasicEntry(key[i], value[i]));
			}
		}

		/** {@inheritDoc} */
		@Override

		public void fastForEach(final Consumer<? super Long2IntMap.Entry> action) {
			final BasicEntry entry = new BasicEntry();
			// Hoist containing class field ref into local
			for (int i = 0, max = size; i < max; ++i) {
				entry.key = key[i];
				entry.value = value[i];
				action.accept(entry);
			}
		}

		@Override
		public int size() {
			return size;
		}

		@Override

		public boolean contains(Object o) {
			if (!(o instanceof Map.Entry)) return false;
			final Map.Entry<?, ?> e = (Map.Entry<?, ?>)o;
			if (e.getKey() == null || !(e.getKey() instanceof Long)) return false;
			if (e.getValue() == null || !(e.getValue() instanceof Integer)) return false;
			final long k = ((Long)(e.getKey())).longValue();
			return Long2IntArrayMap.this.containsKey(k) && ((Long2IntArrayMap.this.get(k)) == (((Integer)(e.getValue())).intValue()));
		}

		@Override

		public boolean remove(final Object o) {
			if (!(o instanceof Map.Entry)) return false;
			final Map.Entry<?, ?> e = (Map.Entry<?, ?>)o;
			if (e.getKey() == null || !(e.getKey() instanceof Long)) return false;
			if (e.getValue() == null || !(e.getValue() instanceof Integer)) return false;
			final long k = ((Long)(e.getKey())).longValue();
			final int v = ((Integer)(e.getValue())).intValue();
			final int oldPos = Long2IntArrayMap.this.findKey(k);
			if (oldPos == -1 || !((v) == (Long2IntArrayMap.this.value[oldPos]))) return false;
			final int tail = size - oldPos - 1;
			System.arraycopy(Long2IntArrayMap.this.key, oldPos + 1, Long2IntArrayMap.this.key, oldPos, tail);
			System.arraycopy(Long2IntArrayMap.this.value, oldPos + 1, Long2IntArrayMap.this.value, oldPos, tail);
			Long2IntArrayMap.this.size--;
			return true;
		}
	}

	@Override
	public FastEntrySet long2IntEntrySet() {
		if (entries == null) entries = new EntrySet();
		return entries;
	}

	private int findKey(final long k) {
		final long[] key = this.key;
		for (int i = size; i-- != 0;) if (((key[i]) == (k))) return i;
		return -1;
	}

	@Override

	public int get(final long k) {
		final long[] key = this.key;
		for (int i = size; i-- != 0;) if (((key[i]) == (k))) return value[i];
		return defRetValue;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public void clear() {
		size = 0;
	}

	@Override
	public boolean containsKey(final long k) {
		return findKey(k) != -1;
	}

	@Override
	public boolean containsValue(int v) {
		for (int i = size; i-- != 0;) if (((value[i]) == (v))) return true;
		return false;
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	@Override

	public int put(long k, int v) {
		final int oldKey = findKey(k);
		if (oldKey != -1) {
			final int oldValue = value[oldKey];
			value[oldKey] = v;
			return oldValue;
		}
		if (size == key.length) {
			final long[] newKey = new long[size == 0 ? 2 : size * 2];
			final int[] newValue = new int[size == 0 ? 2 : size * 2];
			for (int i = size; i-- != 0;) {
				newKey[i] = key[i];
				newValue[i] = value[i];
			}
			key = newKey;
			value = newValue;
		}
		key[size] = k;
		value[size] = v;
		size++;
		return defRetValue;
	}

	@Override

	public int remove(final long k) {
		final int oldPos = findKey(k);
		if (oldPos == -1) return defRetValue;
		final int oldValue = value[oldPos];
		final int tail = size - oldPos - 1;
		System.arraycopy(key, oldPos + 1, key, oldPos, tail);
		System.arraycopy(value, oldPos + 1, value, oldPos, tail);
		size--;
		return oldValue;
	}

	private final class KeySet extends AbstractLongSet {
		@Override
		public boolean contains(final long k) {
			return findKey(k) != -1;
		}

		@Override
		public boolean remove(final long k) {
			final int oldPos = findKey(k);
			if (oldPos == -1) return false;
			final int tail = size - oldPos - 1;
			System.arraycopy(key, oldPos + 1, key, oldPos, tail);
			System.arraycopy(value, oldPos + 1, value, oldPos, tail);
			size--;
			return true;
		}

		@Override
		public LongIterator iterator() {
			return new LongIterator() {
				int pos = 0;

				@Override
				public boolean hasNext() {
					return pos < size;
				}

				@Override

				public long nextLong() {
					if (!hasNext()) throw new NoSuchElementException();
					return key[pos++];
				}

				@Override
				public void remove() {
					if (pos == 0) throw new IllegalStateException();
					final int tail = size - pos;
					System.arraycopy(key, pos, key, pos - 1, tail);
					System.arraycopy(value, pos, value, pos - 1, tail);
					size--;
					pos--;
				}

				@Override

				public void forEachRemaining(final java.util.function.LongConsumer action) {
					// Hoist containing class field ref into local
					final int max = size;
					while (pos < max) {
						action.accept(key[pos++]);
					}
				}
				// TODO either override skip or extend from AbstractIndexBasedIterator.
			};
		}

		final class KeySetSpliterator extends LongSpliterators.EarlyBindingSizeIndexBasedSpliterator implements LongSpliterator {
			KeySetSpliterator(int pos, int maxPos) {
				super(pos, maxPos);
			}

			@Override
			public int characteristics() {
				return LongSpliterators.SET_SPLITERATOR_CHARACTERISTICS | java.util.Spliterator.SUBSIZED | java.util.Spliterator.ORDERED;
			}

			@Override

			protected final long get(int location) {
				return key[location];
			}

			@Override
			protected final KeySetSpliterator makeForSplit(int pos, int maxPos) {
				return new KeySetSpliterator(pos, maxPos);
			}

			@Override

			public void forEachRemaining(final java.util.function.LongConsumer action) {
				// Hoist containing class field ref into local
				final int max = size;
				while (pos < max) {
					action.accept(key[pos++]);
				}
			}
		}

		@Override
		public LongSpliterator spliterator() {
			return new KeySetSpliterator(0, size);
		}

		@Override

		public void forEach(java.util.function.LongConsumer action) {
			// Hoist containing class field ref into local
			for (int i = 0, max = size; i < max; ++i) {
				action.accept(key[i]);
			}
		}

		@Override
		public int size() {
			return size;
		}

		@Override
		public void clear() {
			Long2IntArrayMap.this.clear();
		}
	}

	@Override
	public LongSet keySet() {
		if (keys == null) keys = new KeySet();
		return keys;
	}

	private final class ValuesCollection extends AbstractIntCollection {
		@Override
		public boolean contains(final int v) {
			return containsValue(v);
		}

		@Override
		public it.unimi.dsi.fastutil.ints.IntIterator iterator() {
			return new it.unimi.dsi.fastutil.ints.IntIterator() {
				int pos = 0;

				@Override
				public boolean hasNext() {
					return pos < size;
				}

				@Override

				public int nextInt() {
					if (!hasNext()) throw new NoSuchElementException();
					return value[pos++];
				}

				@Override
				public void remove() {
					if (pos == 0) throw new IllegalStateException();
					final int tail = size - pos;
					System.arraycopy(key, pos, key, pos - 1, tail);
					System.arraycopy(value, pos, value, pos - 1, tail);
					size--;
					pos--;
				}

				@Override

				public void forEachRemaining(final java.util.function.IntConsumer action) {
					// Hoist containing class field ref into local
					final int max = size;
					while (pos < max) {
						action.accept(value[pos++]);
					}
				}
				// TODO either override skip or extend from AbstractIndexBasedIterator.
			};
		}

		final class ValuesSpliterator extends it.unimi.dsi.fastutil.ints.IntSpliterators.EarlyBindingSizeIndexBasedSpliterator implements it.unimi.dsi.fastutil.ints.IntSpliterator {
			ValuesSpliterator(int pos, int maxPos) {
				super(pos, maxPos);
			}

			@Override
			public int characteristics() {
				return it.unimi.dsi.fastutil.ints.IntSpliterators.COLLECTION_SPLITERATOR_CHARACTERISTICS | java.util.Spliterator.SUBSIZED | java.util.Spliterator.ORDERED;
			}

			@Override

			protected final int get(int location) {
				return value[location];
			}

			@Override
			protected final ValuesSpliterator makeForSplit(int pos, int maxPos) {
				return new ValuesSpliterator(pos, maxPos);
			}

			@Override

			public void forEachRemaining(final java.util.function.IntConsumer action) {
				// Hoist containing class field ref into local
				final int max = size;
				while (pos < max) {
					action.accept(value[pos++]);
				}
			}
		}

		@Override
		public it.unimi.dsi.fastutil.ints.IntSpliterator spliterator() {
			return new ValuesSpliterator(0, size);
		}

		@Override

		public void forEach(java.util.function.IntConsumer action) {
			// Hoist containing class field ref into local
			for (int i = 0, max = size; i < max; ++i) {
				action.accept(value[i]);
			}
		}

		@Override
		public int size() {
			return size;
		}

		@Override
		public void clear() {
			Long2IntArrayMap.this.clear();
		}
	}

	@Override
	public IntCollection values() {
		if (values == null) values = new ValuesCollection();
		return values;
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

	public Long2IntArrayMap clone() {
		Long2IntArrayMap c;
		try {
			c = (Long2IntArrayMap)super.clone();
		} catch (CloneNotSupportedException cantHappen) {
			throw new InternalError();
		}
		c.key = key.clone();
		c.value = value.clone();
		c.entries = null;
		c.keys = null;
		c.values = null;
		return c;
	}

	private void writeObject(java.io.ObjectOutputStream s) throws java.io.IOException {
		s.defaultWriteObject();
		for (int i = 0, max = size; i < max; i++) {
			s.writeLong(key[i]);
			s.writeInt(value[i]);
		}
	}

	private void readObject(java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();
		key = new long[size];
		value = new int[size];
		for (int i = 0; i < size; i++) {
			key[i] = s.readLong();
			value[i] = s.readInt();
		}
	}
}
