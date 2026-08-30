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

import it.unimi.dsi.fastutil.objects.Reference2CharSortedMap.FastSortedEntrySet;
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
public final class Reference2CharSortedMaps {
	private Reference2CharSortedMaps() {
	}

	/**
	 * Returns a comparator for entries based on a given comparator on keys.
	 *
	 * @param comparator a comparator on keys.
	 * @return the associated comparator on entries.
	 */
	public static <K> Comparator<? super Map.Entry<K, ?>> entryComparator(final Comparator<? super K> comparator) {
		return (Comparator<Map.Entry<K, ?>>)(x, y) -> comparator.compare((x.getKey()), (y.getKey()));
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
	@SuppressWarnings("unchecked")
	public static <K> ObjectBidirectionalIterator<Reference2CharMap.Entry<K>> fastIterator(Reference2CharSortedMap<K> map) {
		final ObjectSortedSet<Reference2CharMap.Entry<K>> entries = map.reference2CharEntrySet();
		return entries instanceof Reference2CharSortedMap.FastSortedEntrySet ? ((Reference2CharSortedMap.FastSortedEntrySet<K>)entries).fastIterator() : entries.iterator();
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
	@SuppressWarnings("unchecked")
	public static <K> ObjectBidirectionalIterable<Reference2CharMap.Entry<K>> fastIterable(Reference2CharSortedMap<K> map) {
		final ObjectSortedSet<Reference2CharMap.Entry<K>> entries = map.reference2CharEntrySet();
		return entries instanceof Reference2CharSortedMap.FastSortedEntrySet ? ((Reference2CharSortedMap.FastSortedEntrySet<K>)entries)::fastIterator : entries;
	}

	/**
	 * An immutable class representing an empty type-specific sorted map.
	 *
	 * <p>
	 * This class may be useful to implement your own in case you subclass a type-specific sorted map.
	 */
	public static class EmptySortedMap<K> extends Reference2CharMaps.EmptyMap<K> implements Reference2CharSortedMap<K>, java.io.Serializable, Cloneable {
		private static final long serialVersionUID = -7046029254386353129L;

		protected EmptySortedMap() {
		}

		@Override
		public Comparator<? super K> comparator() {
			return null;
		}

		@Override
		public ObjectSortedSet<Reference2CharMap.Entry<K>> reference2CharEntrySet() {
			return ObjectSortedSets.EMPTY_SET;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public ObjectSortedSet<Map.Entry<K, Character>> entrySet() {
			return ObjectSortedSets.EMPTY_SET;
		}

		@SuppressWarnings("unchecked")
		@Override
		public ReferenceSortedSet<K> keySet() {
			return ReferenceSortedSets.EMPTY_SET;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Reference2CharSortedMap<K> subMap(final K from, final K to) {
			return EMPTY_MAP;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Reference2CharSortedMap<K> headMap(final K to) {
			return EMPTY_MAP;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Reference2CharSortedMap<K> tailMap(final K from) {
			return EMPTY_MAP;
		}

		@Override
		public K firstKey() {
			throw new NoSuchElementException();
		}

		@Override
		public K lastKey() {
			throw new NoSuchElementException();
		}
	}

	/**
	 * An empty sorted map (immutable). It is serializable and cloneable.
	 */
	@SuppressWarnings("rawtypes")
	public static final EmptySortedMap EMPTY_MAP = new EmptySortedMap();

	/**
	 * Returns an empty sorted map (immutable). It is serializable and cloneable.
	 *
	 * <p>
	 * This method provides a typesafe access to {@link #EMPTY_MAP}.
	 * 
	 * @return an empty sorted map (immutable).
	 */
	@SuppressWarnings("unchecked")
	public static <K> Reference2CharSortedMap<K> emptyMap() {
		return EMPTY_MAP;
	}

	/**
	 * An immutable class representing a type-specific singleton sorted map.
	 *
	 * <p>
	 * This class may be useful to implement your own in case you subclass a type-specific sorted map.
	 */
	public static class Singleton<K> extends Reference2CharMaps.Singleton<K> implements Reference2CharSortedMap<K>, java.io.Serializable, Cloneable {
		private static final long serialVersionUID = -7046029254386353129L;
		protected final Comparator<? super K> comparator;

		protected Singleton(final K key, final char value, Comparator<? super K> comparator) {
			super(key, value);
			this.comparator = comparator;
		}

		protected Singleton(final K key, final char value) {
			this(key, value, null);
		}

		@SuppressWarnings("unchecked")
		final int compare(final K k1, final K k2) {
			return comparator == null ? (((Comparable<K>)(k1)).compareTo(k2)) : comparator.compare(k1, k2);
		}

		@Override
		public Comparator<? super K> comparator() {
			return comparator;
		}

		@SuppressWarnings("unchecked")
		@Override
		public ObjectSortedSet<Reference2CharMap.Entry<K>> reference2CharEntrySet() {
			if (entries == null) entries = ObjectSortedSets.singleton(new AbstractReference2CharMap.BasicEntry<>(key, value), entryComparator(comparator));
			return (ObjectSortedSet<Reference2CharMap.Entry<K>>)entries;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		@SuppressWarnings({ "rawtypes", "unchecked" })
		public ObjectSortedSet<Map.Entry<K, Character>> entrySet() {
			return (ObjectSortedSet)reference2CharEntrySet();
		}

		@Override
		public ReferenceSortedSet<K> keySet() {
			if (keys == null) keys = ReferenceSortedSets.singleton(key, comparator);
			return (ReferenceSortedSet<K>)keys;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Reference2CharSortedMap<K> subMap(final K from, final K to) {
			if (compare(from, key) <= 0 && compare(key, to) < 0) return this;
			return EMPTY_MAP;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Reference2CharSortedMap<K> headMap(final K to) {
			if (compare(key, to) < 0) return this;
			return EMPTY_MAP;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Reference2CharSortedMap<K> tailMap(final K from) {
			if (compare(from, key) <= 0) return this;
			return EMPTY_MAP;
		}

		@Override
		public K firstKey() {
			return key;
		}

		@Override
		public K lastKey() {
			return key;
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
	public static <K> Reference2CharSortedMap<K> singleton(final K key, Character value) {
		return new Singleton<>((key), (value).charValue());
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
	public static <K> Reference2CharSortedMap<K> singleton(final K key, Character value, Comparator<? super K> comparator) {
		return new Singleton<>((key), (value).charValue(), comparator);
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
	public static <K> Reference2CharSortedMap<K> singleton(final K key, final char value) {
		return new Singleton<>(key, value);
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
	public static <K> Reference2CharSortedMap<K> singleton(final K key, final char value, Comparator<? super K> comparator) {
		return new Singleton<>(key, value, comparator);
	}

	/** A synchronized wrapper class for sorted maps. */
	public static class SynchronizedSortedMap<K> extends Reference2CharMaps.SynchronizedMap<K> implements Reference2CharSortedMap<K>, java.io.Serializable {
		private static final long serialVersionUID = -7046029254386353129L;
		protected final Reference2CharSortedMap<K> sortedMap;

		protected SynchronizedSortedMap(final Reference2CharSortedMap<K> m, final Object sync) {
			super(m, sync);
			sortedMap = m;
		}

		protected SynchronizedSortedMap(final Reference2CharSortedMap<K> m) {
			super(m);
			sortedMap = m;
		}

		@Override
		public Comparator<? super K> comparator() {
			synchronized (sync) {
				return sortedMap.comparator();
			}
		}

		@Override
		public ObjectSortedSet<Reference2CharMap.Entry<K>> reference2CharEntrySet() {
			if (entries == null) entries = ObjectSortedSets.synchronize(sortedMap.reference2CharEntrySet(), sync);
			return (ObjectSortedSet<Reference2CharMap.Entry<K>>)entries;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		@SuppressWarnings({ "rawtypes", "unchecked" })
		public ObjectSortedSet<Map.Entry<K, Character>> entrySet() {
			return (ObjectSortedSet)reference2CharEntrySet();
		}

		@Override
		public ReferenceSortedSet<K> keySet() {
			if (keys == null) keys = ReferenceSortedSets.synchronize(sortedMap.keySet(), sync);
			return (ReferenceSortedSet<K>)keys;
		}

		@Override
		public Reference2CharSortedMap<K> subMap(final K from, final K to) {
			return new SynchronizedSortedMap<>(sortedMap.subMap(from, to), sync);
		}

		@Override
		public Reference2CharSortedMap<K> headMap(final K to) {
			return new SynchronizedSortedMap<>(sortedMap.headMap(to), sync);
		}

		@Override
		public Reference2CharSortedMap<K> tailMap(final K from) {
			return new SynchronizedSortedMap<>(sortedMap.tailMap(from), sync);
		}

		@Override
		public K firstKey() {
			synchronized (sync) {
				return sortedMap.firstKey();
			}
		}

		@Override
		public K lastKey() {
			synchronized (sync) {
				return sortedMap.lastKey();
			}
		}
	}

	/**
	 * Returns a synchronized type-specific sorted map backed by the given type-specific sorted map.
	 *
	 * @param m the sorted map to be wrapped in a synchronized sorted map.
	 * @return a synchronized view of the specified sorted map.
	 * @see java.util.Collections#synchronizedSortedMap(SortedMap)
	 */
	public static <K> Reference2CharSortedMap<K> synchronize(final Reference2CharSortedMap<K> m) {
		return new SynchronizedSortedMap<>(m);
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
	public static <K> Reference2CharSortedMap<K> synchronize(final Reference2CharSortedMap<K> m, final Object sync) {
		return new SynchronizedSortedMap<>(m, sync);
	}

	/** An unmodifiable wrapper class for sorted maps. */
	public static class UnmodifiableSortedMap<K> extends Reference2CharMaps.UnmodifiableMap<K> implements Reference2CharSortedMap<K>, java.io.Serializable {
		private static final long serialVersionUID = -7046029254386353129L;
		protected final Reference2CharSortedMap<K> sortedMap;

		protected UnmodifiableSortedMap(final Reference2CharSortedMap<K> m) {
			super(m);
			sortedMap = m;
		}

		@Override
		public Comparator<? super K> comparator() {
			return sortedMap.comparator();
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		public ObjectSortedSet<Reference2CharMap.Entry<K>> reference2CharEntrySet() {
			if (entries == null) entries = ObjectSortedSets.unmodifiable((ObjectSortedSet)sortedMap.reference2CharEntrySet());
			return (ObjectSortedSet<Reference2CharMap.Entry<K>>)entries;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		@SuppressWarnings({ "rawtypes", "unchecked" })
		public ObjectSortedSet<Map.Entry<K, Character>> entrySet() {
			return (ObjectSortedSet)reference2CharEntrySet();
		}

		@Override
		public ReferenceSortedSet<K> keySet() {
			if (keys == null) keys = ReferenceSortedSets.unmodifiable(sortedMap.keySet());
			return (ReferenceSortedSet<K>)keys;
		}

		@Override
		public Reference2CharSortedMap<K> subMap(final K from, final K to) {
			return new UnmodifiableSortedMap<>(sortedMap.subMap(from, to));
		}

		@Override
		public Reference2CharSortedMap<K> headMap(final K to) {
			return new UnmodifiableSortedMap<>(sortedMap.headMap(to));
		}

		@Override
		public Reference2CharSortedMap<K> tailMap(final K from) {
			return new UnmodifiableSortedMap<>(sortedMap.tailMap(from));
		}

		@Override
		public K firstKey() {
			return sortedMap.firstKey();
		}

		@Override
		public K lastKey() {
			return sortedMap.lastKey();
		}
	}

	/**
	 * Returns an unmodifiable type-specific sorted map backed by the given type-specific sorted map.
	 *
	 * @param m the sorted map to be wrapped in an unmodifiable sorted map.
	 * @return an unmodifiable view of the specified sorted map.
	 * @see java.util.Collections#unmodifiableSortedMap(SortedMap)
	 */
	public static <K> Reference2CharSortedMap<K> unmodifiable(final Reference2CharSortedMap<K> m) {
		return new UnmodifiableSortedMap<>(m);
	}
}
