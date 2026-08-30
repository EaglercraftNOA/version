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

import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterable;
import it.unimi.dsi.fastutil.objects.ObjectSpliterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSets;
import java.util.Map;
import java.util.function.Consumer;
import it.unimi.dsi.fastutil.ints.Int2IntMap.FastEntrySet;

/**
 * A class providing static methods and objects that do useful things with type-specific maps.
 *
 * @see java.util.Collections
 */
public final class Int2IntMaps {
	private Int2IntMaps() {
	}

	/**
	 * Returns an iterator that will be {@linkplain FastEntrySet fast}, if possible, on the
	 * {@linkplain Map#entrySet() entry set} of the provided {@code map}.
	 * 
	 * @param map a map from which we will try to extract a (fast) iterator on the entry set.
	 * @return an iterator on the entry set of the given map that will be fast, if possible.
	 * @since 8.0.0
	 */

	public static ObjectIterator<Int2IntMap.Entry> fastIterator(Int2IntMap map) {
		final ObjectSet<Int2IntMap.Entry> entries = map.int2IntEntrySet();
		return entries instanceof Int2IntMap.FastEntrySet ? ((Int2IntMap.FastEntrySet)entries).fastIterator() : entries.iterator();
	}

	/**
	 * Iterates {@linkplain FastEntrySet#fastForEach(Consumer) quickly}, if possible, on the
	 * {@linkplain Map#entrySet() entry set} of the provided {@code map}.
	 * 
	 * @param map a map on which we will try to iterate {@linkplain FastEntrySet#fastForEach(Consumer)
	 *            quickly}.
	 * @param consumer the consumer that will be passed to {@link FastEntrySet#fastForEach(Consumer)},
	 *            if possible, or to {@link Iterable#forEach(Consumer)}.
	 * @since 8.1.0
	 */

	public static void fastForEach(Int2IntMap map, final Consumer<? super Int2IntMap.Entry> consumer) {
		final ObjectSet<Int2IntMap.Entry> entries = map.int2IntEntrySet();
		if (entries instanceof Int2IntMap.FastEntrySet) ((Int2IntMap.FastEntrySet)entries).fastForEach(consumer);
		else entries.forEach(consumer);
	}

	/**
	 * Returns an iterable yielding an iterator that will be {@linkplain FastEntrySet fast}, if
	 * possible, on the {@linkplain Map#entrySet() entry set} of the provided {@code map}.
	 * 
	 * @param map a map from which we will try to extract an iterable yielding a (fast) iterator on the
	 *            entry set.
	 * @return an iterable yielding an iterator on the entry set of the given map that will be fast, if
	 *         possible.
	 * @since 8.0.0
	 */

	public static ObjectIterable<Int2IntMap.Entry> fastIterable(Int2IntMap map) {
		final ObjectSet<Int2IntMap.Entry> entries = map.int2IntEntrySet();
		return entries instanceof Int2IntMap.FastEntrySet ? new ObjectIterable<Int2IntMap.Entry>() {
			@Override
			public ObjectIterator<Int2IntMap.Entry> iterator() {
				return ((Int2IntMap.FastEntrySet)entries).fastIterator();
			}

			@Override
			public ObjectSpliterator<Int2IntMap.Entry> spliterator() {
				return entries.spliterator();
			}

			@Override
			public void forEach(final Consumer<? super Int2IntMap.Entry> consumer) {
				((Int2IntMap.FastEntrySet)entries).fastForEach(consumer);
			}
		} : entries;
	}

	/**
	 * An immutable class representing an empty type-specific map.
	 *
	 * <p>
	 * This class may be useful to implement your own in case you subclass a type-specific map.
	 */
	public static class EmptyMap extends Int2IntFunctions.EmptyFunction implements Int2IntMap, java.io.Serializable, Cloneable {
		private static final long serialVersionUID = -7046029254386353129L;

		protected EmptyMap() {
		}

		@Override
		public boolean containsValue(final int v) {
			return false;
		}

		@Deprecated
		@Override
		public Integer getOrDefault(final Object key, final Integer defaultValue) {
			return defaultValue;
		}

		@Override
		public int getOrDefault(final int key, final int defaultValue) {
			return defaultValue;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public boolean containsValue(final Object ov) {
			return false;
		}

		@Override
		public void putAll(final Map<? extends Integer, ? extends Integer> m) {
			throw new UnsupportedOperationException();
		}

		@Override
		public ObjectSet<Int2IntMap.Entry> int2IntEntrySet() {
			return ObjectSets.EMPTY_SET;
		}

		@Override
		public IntSet keySet() {
			return IntSets.EMPTY_SET;
		}

		@Override
		public IntCollection values() {
			return IntSets.EMPTY_SET;
		}

		@Override
		public void forEach(final java.util.function.BiConsumer<? super Integer, ? super Integer> consumer) {
		}

		@Override
		public Object clone() {
			return EMPTY_MAP;
		}

		@Override
		public boolean isEmpty() {
			return true;
		}

		@Override
		public int hashCode() {
			return 0;
		}

		@Override
		public boolean equals(final Object o) {
			if (!(o instanceof Map)) return false;
			return ((Map<?, ?>)o).isEmpty();
		}

		@Override
		public String toString() {
			return "{}";
		}
	}

	/**
	 * An empty type-specific map (immutable). It is serializable and cloneable.
	 */

	public static final EmptyMap EMPTY_MAP = new EmptyMap();

	/**
	 * An immutable class representing a type-specific singleton map.
	 *
	 * <p>
	 * This class may be useful to implement your own in case you subclass a type-specific map.
	 */
	public static class Singleton extends Int2IntFunctions.Singleton implements Int2IntMap, java.io.Serializable, Cloneable {
		private static final long serialVersionUID = -7046029254386353129L;
		protected transient ObjectSet<Int2IntMap.Entry> entries;
		protected transient IntSet keys;
		protected transient IntCollection values;

		protected Singleton(final int key, final int value) {
			super(key, value);
		}

		@Override
		public boolean containsValue(final int v) {
			return ((value) == (v));
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public boolean containsValue(final Object ov) {
			return ((((Integer)(ov)).intValue()) == (value));
		}

		@Override
		public void putAll(final Map<? extends Integer, ? extends Integer> m) {
			throw new UnsupportedOperationException();
		}

		@Override
		public ObjectSet<Int2IntMap.Entry> int2IntEntrySet() {
			if (entries == null) entries = ObjectSets.singleton(new AbstractInt2IntMap.BasicEntry(key, value));
			return entries;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		@SuppressWarnings({ "rawtypes", "unchecked" })
		public ObjectSet<Map.Entry<Integer, Integer>> entrySet() {
			return (ObjectSet)int2IntEntrySet();
		}

		@Override
		public IntSet keySet() {
			if (keys == null) keys = IntSets.singleton(key);
			return keys;
		}

		@Override
		public IntCollection values() {
			if (values == null) values = IntSets.singleton(value);
			return values;
		}

		@Override
		public boolean isEmpty() {
			return false;
		}

		@Override
		public int hashCode() {
			return (key) ^ (value);
		}

		@Override
		public boolean equals(final Object o) {
			if (o == this) return true;
			if (!(o instanceof Map)) return false;
			Map<?, ?> m = (Map<?, ?>)o;
			if (m.size() != 1) return false;
			return m.entrySet().iterator().next().equals(entrySet().iterator().next());
		}

		@Override
		public String toString() {
			return "{" + key + "=>" + value + "}";
		}
	}

	/**
	 * Returns a type-specific immutable map containing only the specified pair. The returned map is
	 * serializable and cloneable.
	 *
	 * <p>
	 * Note that albeit the returned map is immutable, its default return value may be changed.
	 *
	 * @param key the only key of the returned map.
	 * @param value the only value of the returned map.
	 * @return a type-specific immutable map containing just the pair {@code &lt;key,value&gt;}.
	 */
	public static Int2IntMap singleton(final int key, int value) {
		return new Singleton(key, value);
	}

	/**
	 * Returns a type-specific immutable map containing only the specified pair. The returned map is
	 * serializable and cloneable.
	 *
	 * <p>
	 * Note that albeit the returned map is immutable, its default return value may be changed.
	 *
	 * @param key the only key of the returned map.
	 * @param value the only value of the returned map.
	 * @return a type-specific immutable map containing just the pair {@code &lt;key,value&gt;}.
	 */
	public static Int2IntMap singleton(final Integer key, final Integer value) {
		return new Singleton((key).intValue(), (value).intValue());
	}

	/** A synchronized wrapper class for maps. */
	public static class SynchronizedMap extends Int2IntFunctions.SynchronizedFunction implements Int2IntMap, java.io.Serializable {
		private static final long serialVersionUID = -7046029254386353129L;
		protected final Int2IntMap map;
		protected transient ObjectSet<Int2IntMap.Entry> entries;
		protected transient IntSet keys;
		protected transient IntCollection values;

		protected SynchronizedMap(final Int2IntMap m, final Object sync) {
			super(m, sync);
			this.map = m;
		}

		protected SynchronizedMap(final Int2IntMap m) {
			super(m);
			this.map = m;
		}

		@Override
		public boolean containsValue(final int v) {
			synchronized (sync) {
				return map.containsValue(v);
			}
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public boolean containsValue(final Object ov) {
			synchronized (sync) {
				return map.containsValue(ov);
			}
		}

		@Override
		public void putAll(final Map<? extends Integer, ? extends Integer> m) {
			synchronized (sync) {
				map.putAll(m);
			}
		}

		@Override
		public ObjectSet<Int2IntMap.Entry> int2IntEntrySet() {
			synchronized (sync) {
				if (entries == null) entries = ObjectSets.synchronize(map.int2IntEntrySet(), sync);
				return entries;
			}
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public ObjectSet<Map.Entry<Integer, Integer>> entrySet() {
			return (ObjectSet)int2IntEntrySet();
		}

		@Override
		public IntSet keySet() {
			synchronized (sync) {
				if (keys == null) keys = IntSets.synchronize(map.keySet(), sync);
				return keys;
			}
		}

		@Override
		public IntCollection values() {
			synchronized (sync) {
				if (values == null) values = IntCollections.synchronize(map.values(), sync);
				return values;
			}
		}

		@Override
		public boolean isEmpty() {
			synchronized (sync) {
				return map.isEmpty();
			}
		}

		@Override
		public int hashCode() {
			synchronized (sync) {
				return map.hashCode();
			}
		}

		@Override
		public boolean equals(final Object o) {
			if (o == this) return true;
			synchronized (sync) {
				return map.equals(o);
			}
		}

		private void writeObject(java.io.ObjectOutputStream s) throws java.io.IOException {
			synchronized (sync) {
				s.defaultWriteObject();
			}
		}

		// Defaultable methods
		@Override
		public int getOrDefault(final int key, final int defaultValue) {
			synchronized (sync) {
				return map.getOrDefault(key, defaultValue);
			}
		}

		@Override
		public void forEach(final java.util.function.BiConsumer<? super Integer, ? super Integer> action) {
			synchronized (sync) {
				map.forEach(action);
			}
		}

		@Override
		public void replaceAll(final java.util.function.BiFunction<? super Integer, ? super Integer, ? extends Integer> function) {
			synchronized (sync) {
				map.replaceAll(function);
			}
		}

		@Override
		public int putIfAbsent(final int key, final int value) {
			synchronized (sync) {
				return map.putIfAbsent(key, value);
			}
		}

		@Override
		public boolean remove(final int key, final int value) {
			synchronized (sync) {
				return map.remove(key, value);
			}
		}

		@Override
		public int replace(final int key, final int value) {
			synchronized (sync) {
				return map.replace(key, value);
			}
		}

		@Override
		public boolean replace(final int key, final int oldValue, final int newValue) {
			synchronized (sync) {
				return map.replace(key, oldValue, newValue);
			}
		}

		@Override
		public int computeIfAbsent(final int key, final java.util.function.IntUnaryOperator mappingFunction) {
			synchronized (sync) {
				return map.computeIfAbsent(key, mappingFunction);
			}
		}

		@Override
		public int computeIfAbsentNullable(final int key, final java.util.function.IntFunction<? extends Integer> mappingFunction) {
			synchronized (sync) {
				return map.computeIfAbsentNullable(key, mappingFunction);
			}
		}

		@Override
		public int computeIfAbsent(final int key, final Int2IntFunction mappingFunction) {
			synchronized (sync) {
				return map.computeIfAbsent(key, mappingFunction);
			}
		}

		@Override
		public int computeIfPresent(final int key, final java.util.function.BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
			synchronized (sync) {
				return map.computeIfPresent(key, remappingFunction);
			}
		}

		@Override
		public int compute(final int key, final java.util.function.BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
			synchronized (sync) {
				return map.compute(key, remappingFunction);
			}
		}

		@Override
		public int merge(final int key, final int value, final java.util.function.BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
			synchronized (sync) {
				return map.merge(key, value, remappingFunction);
			}
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public Integer getOrDefault(final Object key, final Integer defaultValue) {
			synchronized (sync) {
				return map.getOrDefault(key, defaultValue);
			}
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public boolean remove(final Object key, final Object value) {
			synchronized (sync) {
				return map.remove(key, value);
			}
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public Integer replace(final Integer key, final Integer value) {
			synchronized (sync) {
				return map.replace(key, value);
			}
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public boolean replace(final Integer key, final Integer oldValue, final Integer newValue) {
			synchronized (sync) {
				return map.replace(key, oldValue, newValue);
			}
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public Integer putIfAbsent(final Integer key, final Integer value) {
			synchronized (sync) {
				return map.putIfAbsent(key, value);
			}
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public Integer computeIfAbsent(final Integer key, final java.util.function.Function<? super Integer, ? extends Integer> mappingFunction) {
			synchronized (sync) {
				return map.computeIfAbsent(key, mappingFunction);
			}
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public Integer computeIfPresent(final Integer key, final java.util.function.BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
			synchronized (sync) {
				return map.computeIfPresent(key, remappingFunction);
			}
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public Integer compute(final Integer key, final java.util.function.BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
			synchronized (sync) {
				return map.compute(key, remappingFunction);
			}
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public Integer merge(final Integer key, final Integer value, final java.util.function.BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
			synchronized (sync) {
				return map.merge(key, value, remappingFunction);
			}
		}
	}

	/**
	 * Returns a synchronized type-specific map backed by the given type-specific map.
	 *
	 * @param m the map to be wrapped in a synchronized map.
	 * @return a synchronized view of the specified map.
	 * @see java.util.Collections#synchronizedMap(Map)
	 */
	public static Int2IntMap synchronize(final Int2IntMap m) {
		return new SynchronizedMap(m);
	}

	/**
	 * Returns a synchronized type-specific map backed by the given type-specific map, using an assigned
	 * object to synchronize.
	 *
	 * @param m the map to be wrapped in a synchronized map.
	 * @param sync an object that will be used to synchronize the access to the map.
	 * @return a synchronized view of the specified map.
	 * @see java.util.Collections#synchronizedMap(Map)
	 */
	public static Int2IntMap synchronize(final Int2IntMap m, final Object sync) {
		return new SynchronizedMap(m, sync);
	}

	/** An unmodifiable wrapper class for maps. */
	public static class UnmodifiableMap extends Int2IntFunctions.UnmodifiableFunction implements Int2IntMap, java.io.Serializable {
		private static final long serialVersionUID = -7046029254386353129L;
		protected final Int2IntMap map;
		protected transient ObjectSet<Int2IntMap.Entry> entries;
		protected transient IntSet keys;
		protected transient IntCollection values;

		protected UnmodifiableMap(final Int2IntMap m) {
			super(m);
			this.map = m;
		}

		@Override
		public boolean containsValue(final int v) {
			return map.containsValue(v);
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public boolean containsValue(final Object ov) {
			return map.containsValue(ov);
		}

		@Override
		public void putAll(final Map<? extends Integer, ? extends Integer> m) {
			throw new UnsupportedOperationException();
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public ObjectSet<Int2IntMap.Entry> int2IntEntrySet() {
			if (entries == null) entries = ObjectSets.unmodifiable((ObjectSet)map.int2IntEntrySet());
			return entries;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public ObjectSet<Map.Entry<Integer, Integer>> entrySet() {
			return (ObjectSet)int2IntEntrySet();
		}

		@Override
		public IntSet keySet() {
			if (keys == null) keys = IntSets.unmodifiable(map.keySet());
			return keys;
		}

		@Override
		public IntCollection values() {
			if (values == null) values = IntCollections.unmodifiable(map.values());
			return values;
		}

		@Override
		public boolean isEmpty() {
			return map.isEmpty();
		}

		@Override
		public int hashCode() {
			return map.hashCode();
		}

		@Override
		public boolean equals(final Object o) {
			if (o == this) return true;
			return map.equals(o);
		}

		// Defaultable methods
		@Override
		public int getOrDefault(final int key, final int defaultValue) {
			return map.getOrDefault(key, defaultValue);
		}

		@Override
		public void forEach(final java.util.function.BiConsumer<? super Integer, ? super Integer> action) {
			map.forEach(action);
		}

		@Override
		public void replaceAll(final java.util.function.BiFunction<? super Integer, ? super Integer, ? extends Integer> function) {
			throw new UnsupportedOperationException();
		}

		@Override
		public int putIfAbsent(final int key, final int value) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean remove(final int key, final int value) {
			throw new UnsupportedOperationException();
		}

		@Override
		public int replace(final int key, final int value) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean replace(final int key, final int oldValue, final int newValue) {
			throw new UnsupportedOperationException();
		}

		@Override
		public int computeIfAbsent(final int key, final java.util.function.IntUnaryOperator mappingFunction) {
			throw new UnsupportedOperationException();
		}

		@Override
		public int computeIfAbsentNullable(final int key, final java.util.function.IntFunction<? extends Integer> mappingFunction) {
			throw new UnsupportedOperationException();
		}

		@Override
		public int computeIfAbsent(final int key, final Int2IntFunction mappingFunction) {
			throw new UnsupportedOperationException();
		}

		@Override
		public int computeIfPresent(final int key, final java.util.function.BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
			throw new UnsupportedOperationException();
		}

		@Override
		public int compute(final int key, final java.util.function.BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
			throw new UnsupportedOperationException();
		}

		@Override
		public int merge(final int key, final int value, final java.util.function.BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public Integer getOrDefault(final Object key, final Integer defaultValue) {
			return map.getOrDefault(key, defaultValue);
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public boolean remove(final Object key, final Object value) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public Integer replace(final Integer key, final Integer value) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public boolean replace(final Integer key, final Integer oldValue, final Integer newValue) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public Integer putIfAbsent(final Integer key, final Integer value) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public Integer computeIfAbsent(final Integer key, final java.util.function.Function<? super Integer, ? extends Integer> mappingFunction) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public Integer computeIfPresent(final Integer key, final java.util.function.BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public Integer compute(final Integer key, final java.util.function.BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public Integer merge(final Integer key, final Integer value, final java.util.function.BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
			throw new UnsupportedOperationException();
		}
	}

	/**
	 * Returns an unmodifiable type-specific map backed by the given type-specific map.
	 *
	 * @param m the map to be wrapped in an unmodifiable map.
	 * @return an unmodifiable view of the specified map.
	 * @see java.util.Collections#unmodifiableMap(Map)
	 */
	public static Int2IntMap unmodifiable(final Int2IntMap m) {
		return new UnmodifiableMap(m);
	}
}
