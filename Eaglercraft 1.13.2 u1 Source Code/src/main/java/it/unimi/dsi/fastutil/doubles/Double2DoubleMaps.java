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
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterable;
import it.unimi.dsi.fastutil.objects.ObjectSpliterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectSets;
import java.util.Map;
import java.util.function.Consumer;
import it.unimi.dsi.fastutil.doubles.Double2DoubleMap.FastEntrySet;

/**
 * A class providing static methods and objects that do useful things with type-specific maps.
 *
 * @see java.util.Collections
 */
public final class Double2DoubleMaps {
	private Double2DoubleMaps() {
	}

	/**
	 * Returns an iterator that will be {@linkplain FastEntrySet fast}, if possible, on the
	 * {@linkplain Map#entrySet() entry set} of the provided {@code map}.
	 * 
	 * @param map a map from which we will try to extract a (fast) iterator on the entry set.
	 * @return an iterator on the entry set of the given map that will be fast, if possible.
	 * @since 8.0.0
	 */

	public static ObjectIterator<Double2DoubleMap.Entry> fastIterator(Double2DoubleMap map) {
		final ObjectSet<Double2DoubleMap.Entry> entries = map.double2DoubleEntrySet();
		return entries instanceof Double2DoubleMap.FastEntrySet ? ((Double2DoubleMap.FastEntrySet)entries).fastIterator() : entries.iterator();
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

	public static void fastForEach(Double2DoubleMap map, final Consumer<? super Double2DoubleMap.Entry> consumer) {
		final ObjectSet<Double2DoubleMap.Entry> entries = map.double2DoubleEntrySet();
		if (entries instanceof Double2DoubleMap.FastEntrySet) ((Double2DoubleMap.FastEntrySet)entries).fastForEach(consumer);
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

	public static ObjectIterable<Double2DoubleMap.Entry> fastIterable(Double2DoubleMap map) {
		final ObjectSet<Double2DoubleMap.Entry> entries = map.double2DoubleEntrySet();
		return entries instanceof Double2DoubleMap.FastEntrySet ? new ObjectIterable<Double2DoubleMap.Entry>() {
			@Override
			public ObjectIterator<Double2DoubleMap.Entry> iterator() {
				return ((Double2DoubleMap.FastEntrySet)entries).fastIterator();
			}

			@Override
			public ObjectSpliterator<Double2DoubleMap.Entry> spliterator() {
				return entries.spliterator();
			}

			@Override
			public void forEach(final Consumer<? super Double2DoubleMap.Entry> consumer) {
				((Double2DoubleMap.FastEntrySet)entries).fastForEach(consumer);
			}
		} : entries;
	}

	/**
	 * An immutable class representing an empty type-specific map.
	 *
	 * <p>
	 * This class may be useful to implement your own in case you subclass a type-specific map.
	 */
	public static class EmptyMap extends Double2DoubleFunctions.EmptyFunction implements Double2DoubleMap, java.io.Serializable, Cloneable {
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
		public double getOrDefault(final double key, final double defaultValue) {
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
		public void putAll(final Map<? extends Double, ? extends Double> m) {
			throw new UnsupportedOperationException();
		}

		@Override
		public ObjectSet<Double2DoubleMap.Entry> double2DoubleEntrySet() {
			return ObjectSets.EMPTY_SET;
		}

		@Override
		public DoubleSet keySet() {
			return DoubleSets.EMPTY_SET;
		}

		@Override
		public DoubleCollection values() {
			return DoubleSets.EMPTY_SET;
		}

		@Override
		public void forEach(final java.util.function.BiConsumer<? super Double, ? super Double> consumer) {
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
	public static class Singleton extends Double2DoubleFunctions.Singleton implements Double2DoubleMap, java.io.Serializable, Cloneable {
		private static final long serialVersionUID = -7046029254386353129L;
		protected transient ObjectSet<Double2DoubleMap.Entry> entries;
		protected transient DoubleSet keys;
		protected transient DoubleCollection values;

		protected Singleton(final double key, final double value) {
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
		public void putAll(final Map<? extends Double, ? extends Double> m) {
			throw new UnsupportedOperationException();
		}

		@Override
		public ObjectSet<Double2DoubleMap.Entry> double2DoubleEntrySet() {
			if (entries == null) entries = ObjectSets.singleton(new AbstractDouble2DoubleMap.BasicEntry(key, value));
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
		public ObjectSet<Map.Entry<Double, Double>> entrySet() {
			return (ObjectSet)double2DoubleEntrySet();
		}

		@Override
		public DoubleSet keySet() {
			if (keys == null) keys = DoubleSets.singleton(key);
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
			return it.unimi.dsi.fastutil.HashCommon.double2int(key) ^ it.unimi.dsi.fastutil.HashCommon.double2int(value);
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
	public static Double2DoubleMap singleton(final double key, double value) {
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
	public static Double2DoubleMap singleton(final Double key, final Double value) {
		return new Singleton((key).doubleValue(), (value).doubleValue());
	}

	/** A synchronized wrapper class for maps. */
	public static class SynchronizedMap extends Double2DoubleFunctions.SynchronizedFunction implements Double2DoubleMap, java.io.Serializable {
		private static final long serialVersionUID = -7046029254386353129L;
		protected final Double2DoubleMap map;
		protected transient ObjectSet<Double2DoubleMap.Entry> entries;
		protected transient DoubleSet keys;
		protected transient DoubleCollection values;

		protected SynchronizedMap(final Double2DoubleMap m, final Object sync) {
			super(m, sync);
			this.map = m;
		}

		protected SynchronizedMap(final Double2DoubleMap m) {
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
		public void putAll(final Map<? extends Double, ? extends Double> m) {
			synchronized (sync) {
				map.putAll(m);
			}
		}

		@Override
		public ObjectSet<Double2DoubleMap.Entry> double2DoubleEntrySet() {
			synchronized (sync) {
				if (entries == null) entries = ObjectSets.synchronize(map.double2DoubleEntrySet(), sync);
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
		public ObjectSet<Map.Entry<Double, Double>> entrySet() {
			return (ObjectSet)double2DoubleEntrySet();
		}

		@Override
		public DoubleSet keySet() {
			synchronized (sync) {
				if (keys == null) keys = DoubleSets.synchronize(map.keySet(), sync);
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
		public double getOrDefault(final double key, final double defaultValue) {
			synchronized (sync) {
				return map.getOrDefault(key, defaultValue);
			}
		}

		@Override
		public void forEach(final java.util.function.BiConsumer<? super Double, ? super Double> action) {
			synchronized (sync) {
				map.forEach(action);
			}
		}

		@Override
		public void replaceAll(final java.util.function.BiFunction<? super Double, ? super Double, ? extends Double> function) {
			synchronized (sync) {
				map.replaceAll(function);
			}
		}

		@Override
		public double putIfAbsent(final double key, final double value) {
			synchronized (sync) {
				return map.putIfAbsent(key, value);
			}
		}

		@Override
		public boolean remove(final double key, final double value) {
			synchronized (sync) {
				return map.remove(key, value);
			}
		}

		@Override
		public double replace(final double key, final double value) {
			synchronized (sync) {
				return map.replace(key, value);
			}
		}

		@Override
		public boolean replace(final double key, final double oldValue, final double newValue) {
			synchronized (sync) {
				return map.replace(key, oldValue, newValue);
			}
		}

		@Override
		public double computeIfAbsent(final double key, final java.util.function.DoubleUnaryOperator mappingFunction) {
			synchronized (sync) {
				return map.computeIfAbsent(key, mappingFunction);
			}
		}

		@Override
		public double computeIfAbsentNullable(final double key, final java.util.function.DoubleFunction<? extends Double> mappingFunction) {
			synchronized (sync) {
				return map.computeIfAbsentNullable(key, mappingFunction);
			}
		}

		@Override
		public double computeIfAbsent(final double key, final Double2DoubleFunction mappingFunction) {
			synchronized (sync) {
				return map.computeIfAbsent(key, mappingFunction);
			}
		}

		@Override
		public double computeIfPresent(final double key, final java.util.function.BiFunction<? super Double, ? super Double, ? extends Double> remappingFunction) {
			synchronized (sync) {
				return map.computeIfPresent(key, remappingFunction);
			}
		}

		@Override
		public double compute(final double key, final java.util.function.BiFunction<? super Double, ? super Double, ? extends Double> remappingFunction) {
			synchronized (sync) {
				return map.compute(key, remappingFunction);
			}
		}

		@Override
		public double merge(final double key, final double value, final java.util.function.BiFunction<? super Double, ? super Double, ? extends Double> remappingFunction) {
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
		public Double replace(final Double key, final Double value) {
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
		public boolean replace(final Double key, final Double oldValue, final Double newValue) {
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
		public Double putIfAbsent(final Double key, final Double value) {
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
		public Double computeIfAbsent(final Double key, final java.util.function.Function<? super Double, ? extends Double> mappingFunction) {
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
		public Double computeIfPresent(final Double key, final java.util.function.BiFunction<? super Double, ? super Double, ? extends Double> remappingFunction) {
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
		public Double compute(final Double key, final java.util.function.BiFunction<? super Double, ? super Double, ? extends Double> remappingFunction) {
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
		public Double merge(final Double key, final Double value, final java.util.function.BiFunction<? super Double, ? super Double, ? extends Double> remappingFunction) {
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
	public static Double2DoubleMap synchronize(final Double2DoubleMap m) {
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
	public static Double2DoubleMap synchronize(final Double2DoubleMap m, final Object sync) {
		return new SynchronizedMap(m, sync);
	}

	/** An unmodifiable wrapper class for maps. */
	public static class UnmodifiableMap extends Double2DoubleFunctions.UnmodifiableFunction implements Double2DoubleMap, java.io.Serializable {
		private static final long serialVersionUID = -7046029254386353129L;
		protected final Double2DoubleMap map;
		protected transient ObjectSet<Double2DoubleMap.Entry> entries;
		protected transient DoubleSet keys;
		protected transient DoubleCollection values;

		protected UnmodifiableMap(final Double2DoubleMap m) {
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
		public void putAll(final Map<? extends Double, ? extends Double> m) {
			throw new UnsupportedOperationException();
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public ObjectSet<Double2DoubleMap.Entry> double2DoubleEntrySet() {
			if (entries == null) entries = ObjectSets.unmodifiable((ObjectSet)map.double2DoubleEntrySet());
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
		public ObjectSet<Map.Entry<Double, Double>> entrySet() {
			return (ObjectSet)double2DoubleEntrySet();
		}

		@Override
		public DoubleSet keySet() {
			if (keys == null) keys = DoubleSets.unmodifiable(map.keySet());
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
		public double getOrDefault(final double key, final double defaultValue) {
			return map.getOrDefault(key, defaultValue);
		}

		@Override
		public void forEach(final java.util.function.BiConsumer<? super Double, ? super Double> action) {
			map.forEach(action);
		}

		@Override
		public void replaceAll(final java.util.function.BiFunction<? super Double, ? super Double, ? extends Double> function) {
			throw new UnsupportedOperationException();
		}

		@Override
		public double putIfAbsent(final double key, final double value) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean remove(final double key, final double value) {
			throw new UnsupportedOperationException();
		}

		@Override
		public double replace(final double key, final double value) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean replace(final double key, final double oldValue, final double newValue) {
			throw new UnsupportedOperationException();
		}

		@Override
		public double computeIfAbsent(final double key, final java.util.function.DoubleUnaryOperator mappingFunction) {
			throw new UnsupportedOperationException();
		}

		@Override
		public double computeIfAbsentNullable(final double key, final java.util.function.DoubleFunction<? extends Double> mappingFunction) {
			throw new UnsupportedOperationException();
		}

		@Override
		public double computeIfAbsent(final double key, final Double2DoubleFunction mappingFunction) {
			throw new UnsupportedOperationException();
		}

		@Override
		public double computeIfPresent(final double key, final java.util.function.BiFunction<? super Double, ? super Double, ? extends Double> remappingFunction) {
			throw new UnsupportedOperationException();
		}

		@Override
		public double compute(final double key, final java.util.function.BiFunction<? super Double, ? super Double, ? extends Double> remappingFunction) {
			throw new UnsupportedOperationException();
		}

		@Override
		public double merge(final double key, final double value, final java.util.function.BiFunction<? super Double, ? super Double, ? extends Double> remappingFunction) {
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
		public Double replace(final Double key, final Double value) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public boolean replace(final Double key, final Double oldValue, final Double newValue) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public Double putIfAbsent(final Double key, final Double value) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public Double computeIfAbsent(final Double key, final java.util.function.Function<? super Double, ? extends Double> mappingFunction) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public Double computeIfPresent(final Double key, final java.util.function.BiFunction<? super Double, ? super Double, ? extends Double> remappingFunction) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public Double compute(final Double key, final java.util.function.BiFunction<? super Double, ? super Double, ? extends Double> remappingFunction) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public Double merge(final Double key, final Double value, final java.util.function.BiFunction<? super Double, ? super Double, ? extends Double> remappingFunction) {
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
	public static Double2DoubleMap unmodifiable(final Double2DoubleMap m) {
		return new UnmodifiableMap(m);
	}
}
