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
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterable;
import it.unimi.dsi.fastutil.objects.ObjectSpliterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSets;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleCollections;
import it.unimi.dsi.fastutil.doubles.DoubleSets;
import java.util.Map;
import java.util.function.Consumer;
import it.unimi.dsi.fastutil.bytes.Byte2DoubleMap.FastEntrySet;

/**
 * A class providing static methods and objects that do useful things with type-specific maps.
 *
 * @see java.util.Collections
 */
public final class Byte2DoubleMaps {
	private Byte2DoubleMaps() {
	}

	/**
	 * Returns an iterator that will be {@linkplain FastEntrySet fast}, if possible, on the
	 * {@linkplain Map#entrySet() entry set} of the provided {@code map}.
	 * 
	 * @param map a map from which we will try to extract a (fast) iterator on the entry set.
	 * @return an iterator on the entry set of the given map that will be fast, if possible.
	 * @since 8.0.0
	 */

	public static ObjectIterator<Byte2DoubleMap.Entry> fastIterator(Byte2DoubleMap map) {
		final ObjectSet<Byte2DoubleMap.Entry> entries = map.byte2DoubleEntrySet();
		return entries instanceof Byte2DoubleMap.FastEntrySet ? ((Byte2DoubleMap.FastEntrySet)entries).fastIterator() : entries.iterator();
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

	public static void fastForEach(Byte2DoubleMap map, final Consumer<? super Byte2DoubleMap.Entry> consumer) {
		final ObjectSet<Byte2DoubleMap.Entry> entries = map.byte2DoubleEntrySet();
		if (entries instanceof Byte2DoubleMap.FastEntrySet) ((Byte2DoubleMap.FastEntrySet)entries).fastForEach(consumer);
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

	public static ObjectIterable<Byte2DoubleMap.Entry> fastIterable(Byte2DoubleMap map) {
		final ObjectSet<Byte2DoubleMap.Entry> entries = map.byte2DoubleEntrySet();
		return entries instanceof Byte2DoubleMap.FastEntrySet ? new ObjectIterable<Byte2DoubleMap.Entry>() {
			@Override
			public ObjectIterator<Byte2DoubleMap.Entry> iterator() {
				return ((Byte2DoubleMap.FastEntrySet)entries).fastIterator();
			}

			@Override
			public ObjectSpliterator<Byte2DoubleMap.Entry> spliterator() {
				return entries.spliterator();
			}

			@Override
			public void forEach(final Consumer<? super Byte2DoubleMap.Entry> consumer) {
				((Byte2DoubleMap.FastEntrySet)entries).fastForEach(consumer);
			}
		} : entries;
	}

	/**
	 * An immutable class representing an empty type-specific map.
	 *
	 * <p>
	 * This class may be useful to implement your own in case you subclass a type-specific map.
	 */
	public static class EmptyMap extends Byte2DoubleFunctions.EmptyFunction implements Byte2DoubleMap, java.io.Serializable, Cloneable {
		private static final long serialVersionUID = -7046029254386353129L;

		protected EmptyMap() {
		}

		@Override
		public boolean containsValue(final double v) {
			return false;
		}

		@Deprecated
		@Override
		public Double getOrDefault(final Object key, final Double defaultValue) {
			return defaultValue;
		}

		@Override
		public double getOrDefault(final byte key, final double defaultValue) {
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
		public void putAll(final Map<? extends Byte, ? extends Double> m) {
			throw new UnsupportedOperationException();
		}

		@Override
		public ObjectSet<Byte2DoubleMap.Entry> byte2DoubleEntrySet() {
			return ObjectSets.EMPTY_SET;
		}

		@Override
		public ByteSet keySet() {
			return ByteSets.EMPTY_SET;
		}

		@Override
		public DoubleCollection values() {
			return DoubleSets.EMPTY_SET;
		}

		@Override
		public void forEach(final java.util.function.BiConsumer<? super Byte, ? super Double> consumer) {
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
	public static class Singleton extends Byte2DoubleFunctions.Singleton implements Byte2DoubleMap, java.io.Serializable, Cloneable {
		private static final long serialVersionUID = -7046029254386353129L;
		protected transient ObjectSet<Byte2DoubleMap.Entry> entries;
		protected transient ByteSet keys;
		protected transient DoubleCollection values;

		protected Singleton(final byte key, final double value) {
			super(key, value);
		}

		@Override
		public boolean containsValue(final double v) {
			return (Double.doubleToLongBits(value) == Double.doubleToLongBits(v));
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public boolean containsValue(final Object ov) {
			return (Double.doubleToLongBits(((Double)(ov)).doubleValue()) == Double.doubleToLongBits(value));
		}

		@Override
		public void putAll(final Map<? extends Byte, ? extends Double> m) {
			throw new UnsupportedOperationException();
		}

		@Override
		public ObjectSet<Byte2DoubleMap.Entry> byte2DoubleEntrySet() {
			if (entries == null) entries = ObjectSets.singleton(new AbstractByte2DoubleMap.BasicEntry(key, value));
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
		public ObjectSet<Map.Entry<Byte, Double>> entrySet() {
			return (ObjectSet)byte2DoubleEntrySet();
		}

		@Override
		public ByteSet keySet() {
			if (keys == null) keys = ByteSets.singleton(key);
			return keys;
		}

		@Override
		public DoubleCollection values() {
			if (values == null) values = DoubleSets.singleton(value);
			return values;
		}

		@Override
		public boolean isEmpty() {
			return false;
		}

		@Override
		public int hashCode() {
			return (key) ^ it.unimi.dsi.fastutil.HashCommon.double2int(value);
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
	public static Byte2DoubleMap singleton(final byte key, double value) {
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
	public static Byte2DoubleMap singleton(final Byte key, final Double value) {
		return new Singleton((key).byteValue(), (value).doubleValue());
	}

	/** A synchronized wrapper class for maps. */
	public static class SynchronizedMap extends Byte2DoubleFunctions.SynchronizedFunction implements Byte2DoubleMap, java.io.Serializable {
		private static final long serialVersionUID = -7046029254386353129L;
		protected final Byte2DoubleMap map;
		protected transient ObjectSet<Byte2DoubleMap.Entry> entries;
		protected transient ByteSet keys;
		protected transient DoubleCollection values;

		protected SynchronizedMap(final Byte2DoubleMap m, final Object sync) {
			super(m, sync);
			this.map = m;
		}

		protected SynchronizedMap(final Byte2DoubleMap m) {
			super(m);
			this.map = m;
		}

		@Override
		public boolean containsValue(final double v) {
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
		public void putAll(final Map<? extends Byte, ? extends Double> m) {
			synchronized (sync) {
				map.putAll(m);
			}
		}

		@Override
		public ObjectSet<Byte2DoubleMap.Entry> byte2DoubleEntrySet() {
			synchronized (sync) {
				if (entries == null) entries = ObjectSets.synchronize(map.byte2DoubleEntrySet(), sync);
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
		public ObjectSet<Map.Entry<Byte, Double>> entrySet() {
			return (ObjectSet)byte2DoubleEntrySet();
		}

		@Override
		public ByteSet keySet() {
			synchronized (sync) {
				if (keys == null) keys = ByteSets.synchronize(map.keySet(), sync);
				return keys;
			}
		}

		@Override
		public DoubleCollection values() {
			synchronized (sync) {
				if (values == null) values = DoubleCollections.synchronize(map.values(), sync);
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
		public double getOrDefault(final byte key, final double defaultValue) {
			synchronized (sync) {
				return map.getOrDefault(key, defaultValue);
			}
		}

		@Override
		public void forEach(final java.util.function.BiConsumer<? super Byte, ? super Double> action) {
			synchronized (sync) {
				map.forEach(action);
			}
		}

		@Override
		public void replaceAll(final java.util.function.BiFunction<? super Byte, ? super Double, ? extends Double> function) {
			synchronized (sync) {
				map.replaceAll(function);
			}
		}

		@Override
		public double putIfAbsent(final byte key, final double value) {
			synchronized (sync) {
				return map.putIfAbsent(key, value);
			}
		}

		@Override
		public boolean remove(final byte key, final double value) {
			synchronized (sync) {
				return map.remove(key, value);
			}
		}

		@Override
		public double replace(final byte key, final double value) {
			synchronized (sync) {
				return map.replace(key, value);
			}
		}

		@Override
		public boolean replace(final byte key, final double oldValue, final double newValue) {
			synchronized (sync) {
				return map.replace(key, oldValue, newValue);
			}
		}

		@Override
		public double computeIfAbsent(final byte key, final java.util.function.IntToDoubleFunction mappingFunction) {
			synchronized (sync) {
				return map.computeIfAbsent(key, mappingFunction);
			}
		}

		@Override
		public double computeIfAbsentNullable(final byte key, final java.util.function.IntFunction<? extends Double> mappingFunction) {
			synchronized (sync) {
				return map.computeIfAbsentNullable(key, mappingFunction);
			}
		}

		@Override
		public double computeIfAbsent(final byte key, final Byte2DoubleFunction mappingFunction) {
			synchronized (sync) {
				return map.computeIfAbsent(key, mappingFunction);
			}
		}

		@Override
		public double computeIfPresent(final byte key, final java.util.function.BiFunction<? super Byte, ? super Double, ? extends Double> remappingFunction) {
			synchronized (sync) {
				return map.computeIfPresent(key, remappingFunction);
			}
		}

		@Override
		public double compute(final byte key, final java.util.function.BiFunction<? super Byte, ? super Double, ? extends Double> remappingFunction) {
			synchronized (sync) {
				return map.compute(key, remappingFunction);
			}
		}

		@Override
		public double merge(final byte key, final double value, final java.util.function.BiFunction<? super Double, ? super Double, ? extends Double> remappingFunction) {
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
		public Double getOrDefault(final Object key, final Double defaultValue) {
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
		public Double replace(final Byte key, final Double value) {
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
		public boolean replace(final Byte key, final Double oldValue, final Double newValue) {
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
		public Double putIfAbsent(final Byte key, final Double value) {
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
		public Double computeIfAbsent(final Byte key, final java.util.function.Function<? super Byte, ? extends Double> mappingFunction) {
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
		public Double computeIfPresent(final Byte key, final java.util.function.BiFunction<? super Byte, ? super Double, ? extends Double> remappingFunction) {
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
		public Double compute(final Byte key, final java.util.function.BiFunction<? super Byte, ? super Double, ? extends Double> remappingFunction) {
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
		public Double merge(final Byte key, final Double value, final java.util.function.BiFunction<? super Double, ? super Double, ? extends Double> remappingFunction) {
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
	public static Byte2DoubleMap synchronize(final Byte2DoubleMap m) {
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
	public static Byte2DoubleMap synchronize(final Byte2DoubleMap m, final Object sync) {
		return new SynchronizedMap(m, sync);
	}

	/** An unmodifiable wrapper class for maps. */
	public static class UnmodifiableMap extends Byte2DoubleFunctions.UnmodifiableFunction implements Byte2DoubleMap, java.io.Serializable {
		private static final long serialVersionUID = -7046029254386353129L;
		protected final Byte2DoubleMap map;
		protected transient ObjectSet<Byte2DoubleMap.Entry> entries;
		protected transient ByteSet keys;
		protected transient DoubleCollection values;

		protected UnmodifiableMap(final Byte2DoubleMap m) {
			super(m);
			this.map = m;
		}

		@Override
		public boolean containsValue(final double v) {
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
		public void putAll(final Map<? extends Byte, ? extends Double> m) {
			throw new UnsupportedOperationException();
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public ObjectSet<Byte2DoubleMap.Entry> byte2DoubleEntrySet() {
			if (entries == null) entries = ObjectSets.unmodifiable((ObjectSet)map.byte2DoubleEntrySet());
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
		public ObjectSet<Map.Entry<Byte, Double>> entrySet() {
			return (ObjectSet)byte2DoubleEntrySet();
		}

		@Override
		public ByteSet keySet() {
			if (keys == null) keys = ByteSets.unmodifiable(map.keySet());
			return keys;
		}

		@Override
		public DoubleCollection values() {
			if (values == null) values = DoubleCollections.unmodifiable(map.values());
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
		public double getOrDefault(final byte key, final double defaultValue) {
			return map.getOrDefault(key, defaultValue);
		}

		@Override
		public void forEach(final java.util.function.BiConsumer<? super Byte, ? super Double> action) {
			map.forEach(action);
		}

		@Override
		public void replaceAll(final java.util.function.BiFunction<? super Byte, ? super Double, ? extends Double> function) {
			throw new UnsupportedOperationException();
		}

		@Override
		public double putIfAbsent(final byte key, final double value) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean remove(final byte key, final double value) {
			throw new UnsupportedOperationException();
		}

		@Override
		public double replace(final byte key, final double value) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean replace(final byte key, final double oldValue, final double newValue) {
			throw new UnsupportedOperationException();
		}

		@Override
		public double computeIfAbsent(final byte key, final java.util.function.IntToDoubleFunction mappingFunction) {
			throw new UnsupportedOperationException();
		}

		@Override
		public double computeIfAbsentNullable(final byte key, final java.util.function.IntFunction<? extends Double> mappingFunction) {
			throw new UnsupportedOperationException();
		}

		@Override
		public double computeIfAbsent(final byte key, final Byte2DoubleFunction mappingFunction) {
			throw new UnsupportedOperationException();
		}

		@Override
		public double computeIfPresent(final byte key, final java.util.function.BiFunction<? super Byte, ? super Double, ? extends Double> remappingFunction) {
			throw new UnsupportedOperationException();
		}

		@Override
		public double compute(final byte key, final java.util.function.BiFunction<? super Byte, ? super Double, ? extends Double> remappingFunction) {
			throw new UnsupportedOperationException();
		}

		@Override
		public double merge(final byte key, final double value, final java.util.function.BiFunction<? super Double, ? super Double, ? extends Double> remappingFunction) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public Double getOrDefault(final Object key, final Double defaultValue) {
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
		public Double replace(final Byte key, final Double value) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public boolean replace(final Byte key, final Double oldValue, final Double newValue) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public Double putIfAbsent(final Byte key, final Double value) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public Double computeIfAbsent(final Byte key, final java.util.function.Function<? super Byte, ? extends Double> mappingFunction) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public Double computeIfPresent(final Byte key, final java.util.function.BiFunction<? super Byte, ? super Double, ? extends Double> remappingFunction) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public Double compute(final Byte key, final java.util.function.BiFunction<? super Byte, ? super Double, ? extends Double> remappingFunction) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public Double merge(final Byte key, final Double value, final java.util.function.BiFunction<? super Double, ? super Double, ? extends Double> remappingFunction) {
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
	public static Byte2DoubleMap unmodifiable(final Byte2DoubleMap m) {
		return new UnmodifiableMap(m);
	}
}
