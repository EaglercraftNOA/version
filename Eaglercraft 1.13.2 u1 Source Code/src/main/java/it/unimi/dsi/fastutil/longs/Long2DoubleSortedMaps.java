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

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterable;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectSortedSets;
import it.unimi.dsi.fastutil.longs.Long2DoubleSortedMap.FastSortedEntrySet;
import java.util.Comparator;
import java.util.Map;
import java.util.SortedMap;
import java.util.NoSuchElementException;

/**
 * A class providing static methods and objects that do useful things with type-specific sorted
 * maps.
 *
 * @see java.util.Collections
 */
public final class Long2DoubleSortedMaps {
	private Long2DoubleSortedMaps() {
	}

	/**
	 * Returns a comparator for entries based on a given comparator on keys.
	 *
	 * @param comparator a comparator on keys.
	 * @return the associated comparator on entries.
	 */
	public static Comparator<? super Map.Entry<Long, ?>> entryComparator(final LongComparator comparator) {
		return (Comparator<Map.Entry<Long, ?>>)(x, y) -> comparator.compare((x.getKey()).longValue(), (y.getKey()).longValue());
	}

	/**
	 * Returns a bidirectional iterator that will be {@linkplain FastSortedEntrySet fast}, if possible,
	 * on the {@linkplain Map#entrySet() entry set} of the provided {@code map}.
	 * 
	 * @param map a map from which we will try to extract a (fast) bidirectional iterator on the entry
	 *            set.
	 * @return a bidirectional iterator on the entry set of the given map that will be fast, if
	 *         possible.
	 * @since 8.0.0
	 */

	public static ObjectBidirectionalIterator<Long2DoubleMap.Entry> fastIterator(Long2DoubleSortedMap map) {
		final ObjectSortedSet<Long2DoubleMap.Entry> entries = map.long2DoubleEntrySet();
		return entries instanceof Long2DoubleSortedMap.FastSortedEntrySet ? ((Long2DoubleSortedMap.FastSortedEntrySet)entries).fastIterator() : entries.iterator();
	}

	/**
	 * Returns an iterable yielding a bidirectional iterator that will be {@linkplain FastSortedEntrySet
	 * fast}, if possible, on the {@linkplain Map#entrySet() entry set} of the provided {@code map}.
	 * 
	 * @param map a map from which we will try to extract an iterable yielding a (fast) bidirectional
	 *            iterator on the entry set.
	 * @return an iterable yielding a bidirectional iterator on the entry set of the given map that will
	 *         be fast, if possible.
	 * @since 8.0.0
	 */

	public static ObjectBidirectionalIterable<Long2DoubleMap.Entry> fastIterable(Long2DoubleSortedMap map) {
		final ObjectSortedSet<Long2DoubleMap.Entry> entries = map.long2DoubleEntrySet();
		return entries instanceof Long2DoubleSortedMap.FastSortedEntrySet ? ((Long2DoubleSortedMap.FastSortedEntrySet)entries)::fastIterator : entries;
	}

	/**
	 * An immutable class representing an empty type-specific sorted map.
	 *
	 * <p>
	 * This class may be useful to implement your own in case you subclass a type-specific sorted map.
	 */
	public static class EmptySortedMap extends Long2DoubleMaps.EmptyMap implements Long2DoubleSortedMap, java.io.Serializable, Cloneable {
		private static final long serialVersionUID = -7046029254386353129L;

		protected EmptySortedMap() {
		}

		@Override
		public LongComparator comparator() {
			return null;
		}

		@Override
		public ObjectSortedSet<Long2DoubleMap.Entry> long2DoubleEntrySet() {
			return ObjectSortedSets.EMPTY_SET;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public ObjectSortedSet<Map.Entry<Long, Double>> entrySet() {
			return ObjectSortedSets.EMPTY_SET;
		}

		@Override
		public LongSortedSet keySet() {
			return LongSortedSets.EMPTY_SET;
		}

		@Override
		public Long2DoubleSortedMap subMap(final long from, final long to) {
			return EMPTY_MAP;
		}

		@Override
		public Long2DoubleSortedMap headMap(final long to) {
			return EMPTY_MAP;
		}

		@Override
		public Long2DoubleSortedMap tailMap(final long from) {
			return EMPTY_MAP;
		}

		@Override
		public long firstLongKey() {
			throw new NoSuchElementException();
		}

		@Override
		public long lastLongKey() {
			throw new NoSuchElementException();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public Long2DoubleSortedMap headMap(Long oto) {
			return headMap((oto).longValue());
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public Long2DoubleSortedMap tailMap(Long ofrom) {
			return tailMap((ofrom).longValue());
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public Long2DoubleSortedMap subMap(Long ofrom, Long oto) {
			return subMap((ofrom).longValue(), (oto).longValue());
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public Long firstKey() {
			return Long.valueOf(firstLongKey());
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public Long lastKey() {
			return Long.valueOf(lastLongKey());
		}
	}

	/**
	 * An empty sorted map (immutable). It is serializable and cloneable.
	 */

	public static final EmptySortedMap EMPTY_MAP = new EmptySortedMap();

	/**
	 * An immutable class representing a type-specific singleton sorted map.
	 *
	 * <p>
	 * This class may be useful to implement your own in case you subclass a type-specific sorted map.
	 */
	public static class Singleton extends Long2DoubleMaps.Singleton implements Long2DoubleSortedMap, java.io.Serializable, Cloneable {
		private static final long serialVersionUID = -7046029254386353129L;
		protected final LongComparator comparator;

		protected Singleton(final long key, final double value, LongComparator comparator) {
			super(key, value);
			this.comparator = comparator;
		}

		protected Singleton(final long key, final double value) {
			this(key, value, null);
		}

		final int compare(final long k1, final long k2) {
			return comparator == null ? (Long.compare((k1), (k2))) : comparator.compare(k1, k2);
		}

		@Override
		public LongComparator comparator() {
			return comparator;
		}

		@Override
		public ObjectSortedSet<Long2DoubleMap.Entry> long2DoubleEntrySet() {
			if (entries == null) entries = ObjectSortedSets.singleton(new AbstractLong2DoubleMap.BasicEntry(key, value), entryComparator(comparator));
			return (ObjectSortedSet<Long2DoubleMap.Entry>)entries;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		@SuppressWarnings({ "rawtypes", "unchecked" })
		public ObjectSortedSet<Map.Entry<Long, Double>> entrySet() {
			return (ObjectSortedSet)long2DoubleEntrySet();
		}

		@Override
		public LongSortedSet keySet() {
			if (keys == null) keys = LongSortedSets.singleton(key, comparator);
			return (LongSortedSet)keys;
		}

		@Override
		public Long2DoubleSortedMap subMap(final long from, final long to) {
			if (compare(from, key) <= 0 && compare(key, to) < 0) return this;
			return EMPTY_MAP;
		}

		@Override
		public Long2DoubleSortedMap headMap(final long to) {
			if (compare(key, to) < 0) return this;
			return EMPTY_MAP;
		}

		@Override
		public Long2DoubleSortedMap tailMap(final long from) {
			if (compare(from, key) <= 0) return this;
			return EMPTY_MAP;
		}

		@Override
		public long firstLongKey() {
			return key;
		}

		@Override
		public long lastLongKey() {
			return key;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public Long2DoubleSortedMap headMap(Long oto) {
			return headMap((oto).longValue());
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public Long2DoubleSortedMap tailMap(Long ofrom) {
			return tailMap((ofrom).longValue());
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public Long2DoubleSortedMap subMap(Long ofrom, Long oto) {
			return subMap((ofrom).longValue(), (oto).longValue());
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public Long firstKey() {
			return Long.valueOf(firstLongKey());
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public Long lastKey() {
			return Long.valueOf(lastLongKey());
		}
	}

	/**
	 * Returns a type-specific immutable sorted map containing only the specified pair. The returned
	 * sorted map is serializable and cloneable.
	 *
	 * <p>
	 * Note that albeit the returned map is immutable, its default return value may be changed.
	 *
	 * @param key the only key of the returned sorted map.
	 * @param value the only value of the returned sorted map.
	 * @return a type-specific immutable sorted map containing just the pair {@code &lt;key,value&gt;}.
	 */
	public static Long2DoubleSortedMap singleton(final Long key, Double value) {
		return new Singleton((key).longValue(), (value).doubleValue());
	}

	/**
	 * Returns a type-specific immutable sorted map containing only the specified pair. The returned
	 * sorted map is serializable and cloneable.
	 *
	 * <p>
	 * Note that albeit the returned map is immutable, its default return value may be changed.
	 *
	 * @param key the only key of the returned sorted map.
	 * @param value the only value of the returned sorted map.
	 * @param comparator the comparator to use in the returned sorted map.
	 * @return a type-specific immutable sorted map containing just the pair {@code &lt;key,value&gt;}.
	 */
	public static Long2DoubleSortedMap singleton(final Long key, Double value, LongComparator comparator) {
		return new Singleton((key).longValue(), (value).doubleValue(), comparator);
	}

	/**
	 * Returns a type-specific immutable sorted map containing only the specified pair. The returned
	 * sorted map is serializable and cloneable.
	 *
	 * <p>
	 * Note that albeit the returned map is immutable, its default return value may be changed.
	 *
	 * @param key the only key of the returned sorted map.
	 * @param value the only value of the returned sorted map.
	 * @return a type-specific immutable sorted map containing just the pair {@code &lt;key,value&gt;}.
	 */
	public static Long2DoubleSortedMap singleton(final long key, final double value) {
		return new Singleton(key, value);
	}

	/**
	 * Returns a type-specific immutable sorted map containing only the specified pair. The returned
	 * sorted map is serializable and cloneable.
	 *
	 * <p>
	 * Note that albeit the returned map is immutable, its default return value may be changed.
	 *
	 * @param key the only key of the returned sorted map.
	 * @param value the only value of the returned sorted map.
	 * @param comparator the comparator to use in the returned sorted map.
	 * @return a type-specific immutable sorted map containing just the pair {@code &lt;key,value&gt;}.
	 */
	public static Long2DoubleSortedMap singleton(final long key, final double value, LongComparator comparator) {
		return new Singleton(key, value, comparator);
	}

	/** A synchronized wrapper class for sorted maps. */
	public static class SynchronizedSortedMap extends Long2DoubleMaps.SynchronizedMap implements Long2DoubleSortedMap, java.io.Serializable {
		private static final long serialVersionUID = -7046029254386353129L;
		protected final Long2DoubleSortedMap sortedMap;

		protected SynchronizedSortedMap(final Long2DoubleSortedMap m, final Object sync) {
			super(m, sync);
			sortedMap = m;
		}

		protected SynchronizedSortedMap(final Long2DoubleSortedMap m) {
			super(m);
			sortedMap = m;
		}

		@Override
		public LongComparator comparator() {
			synchronized (sync) {
				return sortedMap.comparator();
			}
		}

		@Override
		public ObjectSortedSet<Long2DoubleMap.Entry> long2DoubleEntrySet() {
			if (entries == null) entries = ObjectSortedSets.synchronize(sortedMap.long2DoubleEntrySet(), sync);
			return (ObjectSortedSet<Long2DoubleMap.Entry>)entries;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		@SuppressWarnings({ "rawtypes", "unchecked" })
		public ObjectSortedSet<Map.Entry<Long, Double>> entrySet() {
			return (ObjectSortedSet)long2DoubleEntrySet();
		}

		@Override
		public LongSortedSet keySet() {
			if (keys == null) keys = LongSortedSets.synchronize(sortedMap.keySet(), sync);
			return (LongSortedSet)keys;
		}

		@Override
		public Long2DoubleSortedMap subMap(final long from, final long to) {
			return new SynchronizedSortedMap(sortedMap.subMap(from, to), sync);
		}

		@Override
		public Long2DoubleSortedMap headMap(final long to) {
			return new SynchronizedSortedMap(sortedMap.headMap(to), sync);
		}

		@Override
		public Long2DoubleSortedMap tailMap(final long from) {
			return new SynchronizedSortedMap(sortedMap.tailMap(from), sync);
		}

		@Override
		public long firstLongKey() {
			synchronized (sync) {
				return sortedMap.firstLongKey();
			}
		}

		@Override
		public long lastLongKey() {
			synchronized (sync) {
				return sortedMap.lastLongKey();
			}
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public Long firstKey() {
			synchronized (sync) {
				return sortedMap.firstKey();
			}
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public Long lastKey() {
			synchronized (sync) {
				return sortedMap.lastKey();
			}
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public Long2DoubleSortedMap subMap(final Long from, final Long to) {
			return new SynchronizedSortedMap(sortedMap.subMap(from, to), sync);
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public Long2DoubleSortedMap headMap(final Long to) {
			return new SynchronizedSortedMap(sortedMap.headMap(to), sync);
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public Long2DoubleSortedMap tailMap(final Long from) {
			return new SynchronizedSortedMap(sortedMap.tailMap(from), sync);
		}
	}

	/**
	 * Returns a synchronized type-specific sorted map backed by the given type-specific sorted map.
	 *
	 * @param m the sorted map to be wrapped in a synchronized sorted map.
	 * @return a synchronized view of the specified sorted map.
	 * @see java.util.Collections#synchronizedSortedMap(SortedMap)
	 */
	public static Long2DoubleSortedMap synchronize(final Long2DoubleSortedMap m) {
		return new SynchronizedSortedMap(m);
	}

	/**
	 * Returns a synchronized type-specific sorted map backed by the given type-specific sorted map,
	 * using an assigned object to synchronize.
	 *
	 * @param m the sorted map to be wrapped in a synchronized sorted map.
	 * @param sync an object that will be used to synchronize the access to the sorted sorted map.
	 * @return a synchronized view of the specified sorted map.
	 * @see java.util.Collections#synchronizedSortedMap(SortedMap)
	 */
	public static Long2DoubleSortedMap synchronize(final Long2DoubleSortedMap m, final Object sync) {
		return new SynchronizedSortedMap(m, sync);
	}

	/** An unmodifiable wrapper class for sorted maps. */
	public static class UnmodifiableSortedMap extends Long2DoubleMaps.UnmodifiableMap implements Long2DoubleSortedMap, java.io.Serializable {
		private static final long serialVersionUID = -7046029254386353129L;
		protected final Long2DoubleSortedMap sortedMap;

		protected UnmodifiableSortedMap(final Long2DoubleSortedMap m) {
			super(m);
			sortedMap = m;
		}

		@Override
		public LongComparator comparator() {
			return sortedMap.comparator();
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		public ObjectSortedSet<Long2DoubleMap.Entry> long2DoubleEntrySet() {
			if (entries == null) entries = ObjectSortedSets.unmodifiable((ObjectSortedSet)sortedMap.long2DoubleEntrySet());
			return (ObjectSortedSet<Long2DoubleMap.Entry>)entries;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		@SuppressWarnings({ "rawtypes", "unchecked" })
		public ObjectSortedSet<Map.Entry<Long, Double>> entrySet() {
			return (ObjectSortedSet)long2DoubleEntrySet();
		}

		@Override
		public LongSortedSet keySet() {
			if (keys == null) keys = LongSortedSets.unmodifiable(sortedMap.keySet());
			return (LongSortedSet)keys;
		}

		@Override
		public Long2DoubleSortedMap subMap(final long from, final long to) {
			return new UnmodifiableSortedMap(sortedMap.subMap(from, to));
		}

		@Override
		public Long2DoubleSortedMap headMap(final long to) {
			return new UnmodifiableSortedMap(sortedMap.headMap(to));
		}

		@Override
		public Long2DoubleSortedMap tailMap(final long from) {
			return new UnmodifiableSortedMap(sortedMap.tailMap(from));
		}

		@Override
		public long firstLongKey() {
			return sortedMap.firstLongKey();
		}

		@Override
		public long lastLongKey() {
			return sortedMap.lastLongKey();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public Long firstKey() {
			return sortedMap.firstKey();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public Long lastKey() {
			return sortedMap.lastKey();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public Long2DoubleSortedMap subMap(final Long from, final Long to) {
			return new UnmodifiableSortedMap(sortedMap.subMap(from, to));
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public Long2DoubleSortedMap headMap(final Long to) {
			return new UnmodifiableSortedMap(sortedMap.headMap(to));
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public Long2DoubleSortedMap tailMap(final Long from) {
			return new UnmodifiableSortedMap(sortedMap.tailMap(from));
		}
	}

	/**
	 * Returns an unmodifiable type-specific sorted map backed by the given type-specific sorted map.
	 *
	 * @param m the sorted map to be wrapped in an unmodifiable sorted map.
	 * @return an unmodifiable view of the specified sorted map.
	 * @see java.util.Collections#unmodifiableSortedMap(SortedMap)
	 */
	public static Long2DoubleSortedMap unmodifiable(final Long2DoubleSortedMap m) {
		return new UnmodifiableSortedMap(m);
	}
}
