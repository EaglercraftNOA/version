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
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortCollections;
import it.unimi.dsi.fastutil.shorts.ShortSets;
import java.util.Map;
import java.util.function.Consumer;
import it.unimi.dsi.fastutil.ints.Int2ShortMap.FastEntrySet;

/**
 * A class providing static methods and objects that do useful things with type-specific maps.
 *
 * @see java.util.Collections
 */
public final class Int2ShortMaps {
	private Int2ShortMaps() {
	}

	/**
	 * Returns an iterator that will be {@linkplain FastEntrySet fast}, if possible, on the
	 * {@linkplain Map#entrySet() entry set} of the provided {@code map}.
	 * 
	 * @param map a map from which we will try to extract a (fast) iterator on the entry set.
	 * @return an iterator on the entry set of the given map that will be fast, if possible.
	 * @since 8.0.0
	 */

	public static ObjectIterator<Int2ShortMap.Entry> fastIterator(Int2ShortMap map) {
		final ObjectSet<Int2ShortMap.Entry> entries = map.int2ShortEntrySet();
		return entries instanceof Int2ShortMap.FastEntrySet ? ((Int2ShortMap.FastEntrySet)entries).fastIterator() : entries.iterator();
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

	public static void fastForEach(Int2ShortMap map, final Consumer<? super Int2ShortMap.Entry> consumer) {
		final ObjectSet<Int2ShortMap.Entry> entries = map.int2ShortEntrySet();
		if (entries instanceof Int2ShortMap.FastEntrySet) ((Int2ShortMap.FastEntrySet)entries).fastForEach(consumer);
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

	public static ObjectIterable<Int2ShortMap.Entry> fastIterable(Int2ShortMap map) {
		final ObjectSet<Int2ShortMap.Entry> entries = map.int2ShortEntrySet();
		return entries instanceof Int2ShortMap.FastEntrySet ? new ObjectIterable<Int2ShortMap.Entry>() {
			@Override
			public ObjectIterator<Int2ShortMap.Entry> iterator() {
				return ((Int2ShortMap.FastEntrySet)entries).fastIterator();
			}

			@Override
			public ObjectSpliterator<Int2ShortMap.Entry> spliterator() {
				return entries.spliterator();
			}

			@Override
			public void forEach(final Consumer<? super Int2ShortMap.Entry> consumer) {
				((Int2ShortMap.FastEntrySet)entries).fastForEach(consumer);
			}
		} : entries;
	}

	/**
	 * An immutable class representing an empty type-specific map.
	 *
	 * <p>
	 * This class may be useful to implement your own in case you subclass a type-specific map.
	 */
	public static class EmptyMap extends Int2ShortFunctions.EmptyFunction implements Int2ShortMap, java.io.Serializable, Cloneable {
		private static final long serialVersionUID = -7046029254386353129L;

		protected EmptyMap() {
		}

		@Override
		public boolean containsValue(final short v) {
			return false;
		}

		@Deprecated
		@Override
		public Short getOrDefault(final Object key, final Short defaultValue) {
			return defaultValue;
		}

		@Override
		public short getOrDefault(final int key, final short defaultValue) {
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
		public void putAll(final Map<? extends Integer, ? extends Short> m) {
			throw new UnsupportedOperationException();
		}

		@Override
		public ObjectSet<Int2ShortMap.Entry> int2ShortEntrySet() {
			return ObjectSets.EMPTY_SET;
		}

		@Override
		public IntSet keySet() {
			return IntSets.EMPTY_SET;
		}

		@Override
		public ShortCollection values() {
			return ShortSets.EMPTY_SET;
		}

		@Override
		public void forEach(final java.util.function.BiConsumer<? super Integer, ? super Short> consumer) {
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
	public static class Singleton extends Int2ShortFunctions.Singleton implements Int2ShortMap, java.io.Serializable, Cloneable {
		private static final long serialVersionUID = -7046029254386353129L;
		protected transient ObjectSet<Int2ShortMap.Entry> entries;
		protected transient IntSet keys;
		protected transient ShortCollection values;

		protected Singleton(final int key, final short value) {
			super(key, value);
		}

		@Override
		public boolean containsValue(final short v) {
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
			return ((((Short)(ov)).shortValue()) == (value));
		}

		@Override
		public void putAll(final Map<? extends Integer, ? extends Short> m) {
			throw new UnsupportedOperationException();
		}

		@Override
		public ObjectSet<Int2ShortMap.Entry> int2ShortEntrySet() {
			if (entries == null) entries = ObjectSets.singleton(new AbstractInt2ShortMap.BasicEntry(key, value));
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
		public ObjectSet<Map.Entry<Integer, Short>> entrySet() {
			return (ObjectSet)int2ShortEntrySet();
		}

		@Override
		public IntSet keySet() {
			if (keys == null) keys = IntSets.singleton(key);
			return keys;
		}

		@Override
		public ShortCollection values() {
			if (values == null) values = ShortSets.singleton(value);
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
	public static Int2ShortMap singleton(final int key, short value) {
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
	public static Int2ShortMap singleton(final Integer key, final Short value) {
		return new Singleton((key).intValue(), (value).shortValue());
	}

	/** A synchronized wrapper class for maps. */
	public static class SynchronizedMap extends Int2ShortFunctions.SynchronizedFunction implements Int2ShortMap, java.io.Serializable {
		private static final long serialVersionUID = -7046029254386353129L;
		protected final Int2ShortMap map;
		protected transient ObjectSet<Int2ShortMap.Entry> entries;
		protected transient IntSet keys;
		protected transient ShortCollection values;

		protected SynchronizedMap(final Int2ShortMap m, final Object sync) {
			super(m, sync);
			this.map = m;
		}

		protected SynchronizedMap(final Int2ShortMap m) {
			super(m);
			this.map = m;
		}

		@Override
		public boolean containsValue(final short v) {
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
		public void putAll(final Map<? extends Integer, ? extends Short> m) {
			synchronized (sync) {
				map.putAll(m);
			}
		}

		@Override
		public ObjectSet<Int2ShortMap.Entry> int2ShortEntrySet() {
			synchronized (sync) {
				if (entries == null) entries = ObjectSets.synchronize(map.int2ShortEntrySet(), sync);
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
		public ObjectSet<Map.Entry<Integer, Short>> entrySet() {
			return (ObjectSet)int2ShortEntrySet();
		}

		@Override
		public IntSet keySet() {
			synchronized (sync) {
				if (keys == null) keys = IntSets.synchronize(map.keySet(), sync);
				return keys;
			}
		}

		@Override
		public ShortCollection values() {
			synchronized (sync) {
				if (values == null) values = ShortCollections.synchronize(map.values(), sync);
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
		public short getOrDefault(final int key, final short defaultValue) {
			synchronized (sync) {
				return map.getOrDefault(key, defaultValue);
			}
		}

		@Override
		public void forEach(final java.util.function.BiConsumer<? super Integer, ? super Short> action) {
			synchronized (sync) {
				map.forEach(action);
			}
		}

		@Override
		public void replaceAll(final java.util.function.BiFunction<? super Integer, ? super Short, ? extends Short> function) {
			synchronized (sync) {
				map.replaceAll(function);
			}
		}

		@Override
		public short putIfAbsent(final int key, final short value) {
			synchronized (sync) {
				return map.putIfAbsent(key, value);
			}
		}

		@Override
		public boolean remove(final int key, final short value) {
			synchronized (sync) {
				return map.remove(key, value);
			}
		}

		@Override
		public short replace(final int key, final short value) {
			synchronized (sync) {
				return map.replace(key, value);
			}
		}

		@Override
		public boolean replace(final int key, final short oldValue, final short newValue) {
			synchronized (sync) {
				return map.replace(key, oldValue, newValue);
			}
		}

		@Override
		public short computeIfAbsent(final int key, final java.util.function.IntUnaryOperator mappingFunction) {
			synchronized (sync) {
				return map.computeIfAbsent(key, mappingFunction);
			}
		}

		@Override
		public short computeIfAbsentNullable(final int key, final java.util.function.IntFunction<? extends Short> mappingFunction) {
			synchronized (sync) {
				return map.computeIfAbsentNullable(key, mappingFunction);
			}
		}

		@Override
		public short computeIfAbsent(final int key, final Int2ShortFunction mappingFunction) {
			synchronized (sync) {
				return map.computeIfAbsent(key, mappingFunction);
			}
		}

		@Override
		public short computeIfPresent(final int key, final java.util.function.BiFunction<? super Integer, ? super Short, ? extends Short> remappingFunction) {
			synchronized (sync) {
				return map.computeIfPresent(key, remappingFunction);
			}
		}

		@Override
		public short compute(final int key, final java.util.function.BiFunction<? super Integer, ? super Short, ? extends Short> remappingFunction) {
			synchronized (sync) {
				return map.compute(key, remappingFunction);
			}
		}

		@Override
		public short merge(final int key, final short value, final java.util.function.BiFunction<? super Short, ? super Short, ? extends Short> remappingFunction) {
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
		public Short getOrDefault(final Object key, final Short defaultValue) {
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
		public Short replace(final Integer key, final Short value) {
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
		public boolean replace(final Integer key, final Short oldValue, final Short newValue) {
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
		public Short putIfAbsent(final Integer key, final Short value) {
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
		public Short computeIfAbsent(final Integer key, final java.util.function.Function<? super Integer, ? extends Short> mappingFunction) {
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
		public Short computeIfPresent(final Integer key, final java.util.function.BiFunction<? super Integer, ? super Short, ? extends Short> remappingFunction) {
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
		public Short compute(final Integer key, final java.util.function.BiFunction<? super Integer, ? super Short, ? extends Short> remappingFunction) {
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
		public Short merge(final Integer key, final Short value, final java.util.function.BiFunction<? super Short, ? super Short, ? extends Short> remappingFunction) {
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
	public static Int2ShortMap synchronize(final Int2ShortMap m) {
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
	public static Int2ShortMap synchronize(final Int2ShortMap m, final Object sync) {
		return new SynchronizedMap(m, sync);
	}

	/** An unmodifiable wrapper class for maps. */
	public static class UnmodifiableMap extends Int2ShortFunctions.UnmodifiableFunction implements Int2ShortMap, java.io.Serializable {
		private static final long serialVersionUID = -7046029254386353129L;
		protected final Int2ShortMap map;
		protected transient ObjectSet<Int2ShortMap.Entry> entries;
		protected transient IntSet keys;
		protected transient ShortCollection values;

		protected UnmodifiableMap(final Int2ShortMap m) {
			super(m);
			this.map = m;
		}

		@Override
		public boolean containsValue(final short v) {
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
		public void putAll(final Map<? extends Integer, ? extends Short> m) {
			throw new UnsupportedOperationException();
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public ObjectSet<Int2ShortMap.Entry> int2ShortEntrySet() {
			if (entries == null) entries = ObjectSets.unmodifiable((ObjectSet)map.int2ShortEntrySet());
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
		public ObjectSet<Map.Entry<Integer, Short>> entrySet() {
			return (ObjectSet)int2ShortEntrySet();
		}

		@Override
		public IntSet keySet() {
			if (keys == null) keys = IntSets.unmodifiable(map.keySet());
			return keys;
		}

		@Override
		public ShortCollection values() {
			if (values == null) values = ShortCollections.unmodifiable(map.values());
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
		public short getOrDefault(final int key, final short defaultValue) {
			return map.getOrDefault(key, defaultValue);
		}

		@Override
		public void forEach(final java.util.function.BiConsumer<? super Integer, ? super Short> action) {
			map.forEach(action);
		}

		@Override
		public void replaceAll(final java.util.function.BiFunction<? super Integer, ? super Short, ? extends Short> function) {
			throw new UnsupportedOperationException();
		}

		@Override
		public short putIfAbsent(final int key, final short value) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean remove(final int key, final short value) {
			throw new UnsupportedOperationException();
		}

		@Override
		public short replace(final int key, final short value) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean replace(final int key, final short oldValue, final short newValue) {
			throw new UnsupportedOperationException();
		}

		@Override
		public short computeIfAbsent(final int key, final java.util.function.IntUnaryOperator mappingFunction) {
			throw new UnsupportedOperationException();
		}

		@Override
		public short computeIfAbsentNullable(final int key, final java.util.function.IntFunction<? extends Short> mappingFunction) {
			throw new UnsupportedOperationException();
		}

		@Override
		public short computeIfAbsent(final int key, final Int2ShortFunction mappingFunction) {
			throw new UnsupportedOperationException();
		}

		@Override
		public short computeIfPresent(final int key, final java.util.function.BiFunction<? super Integer, ? super Short, ? extends Short> remappingFunction) {
			throw new UnsupportedOperationException();
		}

		@Override
		public short compute(final int key, final java.util.function.BiFunction<? super Integer, ? super Short, ? extends Short> remappingFunction) {
			throw new UnsupportedOperationException();
		}

		@Override
		public short merge(final int key, final short value, final java.util.function.BiFunction<? super Short, ? super Short, ? extends Short> remappingFunction) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public Short getOrDefault(final Object key, final Short defaultValue) {
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
		public Short replace(final Integer key, final Short value) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public boolean replace(final Integer key, final Short oldValue, final Short newValue) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public Short putIfAbsent(final Integer key, final Short value) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public Short computeIfAbsent(final Integer key, final java.util.function.Function<? super Integer, ? extends Short> mappingFunction) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public Short computeIfPresent(final Integer key, final java.util.function.BiFunction<? super Integer, ? super Short, ? extends Short> remappingFunction) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public Short compute(final Integer key, final java.util.function.BiFunction<? super Integer, ? super Short, ? extends Short> remappingFunction) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public Short merge(final Integer key, final Short value, final java.util.function.BiFunction<? super Short, ? super Short, ? extends Short> remappingFunction) {
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
	public static Int2ShortMap unmodifiable(final Int2ShortMap m) {
		return new UnmodifiableMap(m);
	}
}
