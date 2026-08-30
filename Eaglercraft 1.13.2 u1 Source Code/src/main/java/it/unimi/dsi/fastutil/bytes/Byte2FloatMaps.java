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
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.floats.FloatCollections;
import it.unimi.dsi.fastutil.floats.FloatSets;
import java.util.Map;
import java.util.function.Consumer;
import it.unimi.dsi.fastutil.bytes.Byte2FloatMap.FastEntrySet;

/**
 * A class providing static methods and objects that do useful things with type-specific maps.
 *
 * @see java.util.Collections
 */
public final class Byte2FloatMaps {
	private Byte2FloatMaps() {
	}

	/**
	 * Returns an iterator that will be {@linkplain FastEntrySet fast}, if possible, on the
	 * {@linkplain Map#entrySet() entry set} of the provided {@code map}.
	 * 
	 * @param map a map from which we will try to extract a (fast) iterator on the entry set.
	 * @return an iterator on the entry set of the given map that will be fast, if possible.
	 * @since 8.0.0
	 */

	public static ObjectIterator<Byte2FloatMap.Entry> fastIterator(Byte2FloatMap map) {
		final ObjectSet<Byte2FloatMap.Entry> entries = map.byte2FloatEntrySet();
		return entries instanceof Byte2FloatMap.FastEntrySet ? ((Byte2FloatMap.FastEntrySet)entries).fastIterator() : entries.iterator();
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

	public static void fastForEach(Byte2FloatMap map, final Consumer<? super Byte2FloatMap.Entry> consumer) {
		final ObjectSet<Byte2FloatMap.Entry> entries = map.byte2FloatEntrySet();
		if (entries instanceof Byte2FloatMap.FastEntrySet) ((Byte2FloatMap.FastEntrySet)entries).fastForEach(consumer);
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

	public static ObjectIterable<Byte2FloatMap.Entry> fastIterable(Byte2FloatMap map) {
		final ObjectSet<Byte2FloatMap.Entry> entries = map.byte2FloatEntrySet();
		return entries instanceof Byte2FloatMap.FastEntrySet ? new ObjectIterable<Byte2FloatMap.Entry>() {
			@Override
			public ObjectIterator<Byte2FloatMap.Entry> iterator() {
				return ((Byte2FloatMap.FastEntrySet)entries).fastIterator();
			}

			@Override
			public ObjectSpliterator<Byte2FloatMap.Entry> spliterator() {
				return entries.spliterator();
			}

			@Override
			public void forEach(final Consumer<? super Byte2FloatMap.Entry> consumer) {
				((Byte2FloatMap.FastEntrySet)entries).fastForEach(consumer);
			}
		} : entries;
	}

	/**
	 * An immutable class representing an empty type-specific map.
	 *
	 * <p>
	 * This class may be useful to implement your own in case you subclass a type-specific map.
	 */
	public static class EmptyMap extends Byte2FloatFunctions.EmptyFunction implements Byte2FloatMap, java.io.Serializable, Cloneable {
		private static final long serialVersionUID = -7046029254386353129L;

		protected EmptyMap() {
		}

		@Override
		public boolean containsValue(final float v) {
			return false;
		}

		@Deprecated
		@Override
		public Float getOrDefault(final Object key, final Float defaultValue) {
			return defaultValue;
		}

		@Override
		public float getOrDefault(final byte key, final float defaultValue) {
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
		public void putAll(final Map<? extends Byte, ? extends Float> m) {
			throw new UnsupportedOperationException();
		}

		@Override
		public ObjectSet<Byte2FloatMap.Entry> byte2FloatEntrySet() {
			return ObjectSets.EMPTY_SET;
		}

		@Override
		public ByteSet keySet() {
			return ByteSets.EMPTY_SET;
		}

		@Override
		public FloatCollection values() {
			return FloatSets.EMPTY_SET;
		}

		@Override
		public void forEach(final java.util.function.BiConsumer<? super Byte, ? super Float> consumer) {
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
	public static class Singleton extends Byte2FloatFunctions.Singleton implements Byte2FloatMap, java.io.Serializable, Cloneable {
		private static final long serialVersionUID = -7046029254386353129L;
		protected transient ObjectSet<Byte2FloatMap.Entry> entries;
		protected transient ByteSet keys;
		protected transient FloatCollection values;

		protected Singleton(final byte key, final float value) {
			super(key, value);
		}

		@Override
		public boolean containsValue(final float v) {
			return (Float.floatToIntBits(value) == Float.floatToIntBits(v));
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public boolean containsValue(final Object ov) {
			return (Float.floatToIntBits(((Float)(ov)).floatValue()) == Float.floatToIntBits(value));
		}

		@Override
		public void putAll(final Map<? extends Byte, ? extends Float> m) {
			throw new UnsupportedOperationException();
		}

		@Override
		public ObjectSet<Byte2FloatMap.Entry> byte2FloatEntrySet() {
			if (entries == null) entries = ObjectSets.singleton(new AbstractByte2FloatMap.BasicEntry(key, value));
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
		public ObjectSet<Map.Entry<Byte, Float>> entrySet() {
			return (ObjectSet)byte2FloatEntrySet();
		}

		@Override
		public ByteSet keySet() {
			if (keys == null) keys = ByteSets.singleton(key);
			return keys;
		}

		@Override
		public FloatCollection values() {
			if (values == null) values = FloatSets.singleton(value);
			return values;
		}

		@Override
		public boolean isEmpty() {
			return false;
		}

		@Override
		public int hashCode() {
			return (key) ^ it.unimi.dsi.fastutil.HashCommon.float2int(value);
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
	public static Byte2FloatMap singleton(final byte key, float value) {
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
	public static Byte2FloatMap singleton(final Byte key, final Float value) {
		return new Singleton((key).byteValue(), (value).floatValue());
	}

	/** A synchronized wrapper class for maps. */
	public static class SynchronizedMap extends Byte2FloatFunctions.SynchronizedFunction implements Byte2FloatMap, java.io.Serializable {
		private static final long serialVersionUID = -7046029254386353129L;
		protected final Byte2FloatMap map;
		protected transient ObjectSet<Byte2FloatMap.Entry> entries;
		protected transient ByteSet keys;
		protected transient FloatCollection values;

		protected SynchronizedMap(final Byte2FloatMap m, final Object sync) {
			super(m, sync);
			this.map = m;
		}

		protected SynchronizedMap(final Byte2FloatMap m) {
			super(m);
			this.map = m;
		}

		@Override
		public boolean containsValue(final float v) {
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
		public void putAll(final Map<? extends Byte, ? extends Float> m) {
			synchronized (sync) {
				map.putAll(m);
			}
		}

		@Override
		public ObjectSet<Byte2FloatMap.Entry> byte2FloatEntrySet() {
			synchronized (sync) {
				if (entries == null) entries = ObjectSets.synchronize(map.byte2FloatEntrySet(), sync);
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
		public ObjectSet<Map.Entry<Byte, Float>> entrySet() {
			return (ObjectSet)byte2FloatEntrySet();
		}

		@Override
		public ByteSet keySet() {
			synchronized (sync) {
				if (keys == null) keys = ByteSets.synchronize(map.keySet(), sync);
				return keys;
			}
		}

		@Override
		public FloatCollection values() {
			synchronized (sync) {
				if (values == null) values = FloatCollections.synchronize(map.values(), sync);
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
		public float getOrDefault(final byte key, final float defaultValue) {
			synchronized (sync) {
				return map.getOrDefault(key, defaultValue);
			}
		}

		@Override
		public void forEach(final java.util.function.BiConsumer<? super Byte, ? super Float> action) {
			synchronized (sync) {
				map.forEach(action);
			}
		}

		@Override
		public void replaceAll(final java.util.function.BiFunction<? super Byte, ? super Float, ? extends Float> function) {
			synchronized (sync) {
				map.replaceAll(function);
			}
		}

		@Override
		public float putIfAbsent(final byte key, final float value) {
			synchronized (sync) {
				return map.putIfAbsent(key, value);
			}
		}

		@Override
		public boolean remove(final byte key, final float value) {
			synchronized (sync) {
				return map.remove(key, value);
			}
		}

		@Override
		public float replace(final byte key, final float value) {
			synchronized (sync) {
				return map.replace(key, value);
			}
		}

		@Override
		public boolean replace(final byte key, final float oldValue, final float newValue) {
			synchronized (sync) {
				return map.replace(key, oldValue, newValue);
			}
		}

		@Override
		public float computeIfAbsent(final byte key, final java.util.function.IntToDoubleFunction mappingFunction) {
			synchronized (sync) {
				return map.computeIfAbsent(key, mappingFunction);
			}
		}

		@Override
		public float computeIfAbsentNullable(final byte key, final java.util.function.IntFunction<? extends Float> mappingFunction) {
			synchronized (sync) {
				return map.computeIfAbsentNullable(key, mappingFunction);
			}
		}

		@Override
		public float computeIfAbsent(final byte key, final Byte2FloatFunction mappingFunction) {
			synchronized (sync) {
				return map.computeIfAbsent(key, mappingFunction);
			}
		}

		@Override
		public float computeIfPresent(final byte key, final java.util.function.BiFunction<? super Byte, ? super Float, ? extends Float> remappingFunction) {
			synchronized (sync) {
				return map.computeIfPresent(key, remappingFunction);
			}
		}

		@Override
		public float compute(final byte key, final java.util.function.BiFunction<? super Byte, ? super Float, ? extends Float> remappingFunction) {
			synchronized (sync) {
				return map.compute(key, remappingFunction);
			}
		}

		@Override
		public float merge(final byte key, final float value, final java.util.function.BiFunction<? super Float, ? super Float, ? extends Float> remappingFunction) {
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
		public Float getOrDefault(final Object key, final Float defaultValue) {
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
		public Float replace(final Byte key, final Float value) {
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
		public boolean replace(final Byte key, final Float oldValue, final Float newValue) {
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
		public Float putIfAbsent(final Byte key, final Float value) {
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
		public Float computeIfAbsent(final Byte key, final java.util.function.Function<? super Byte, ? extends Float> mappingFunction) {
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
		public Float computeIfPresent(final Byte key, final java.util.function.BiFunction<? super Byte, ? super Float, ? extends Float> remappingFunction) {
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
		public Float compute(final Byte key, final java.util.function.BiFunction<? super Byte, ? super Float, ? extends Float> remappingFunction) {
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
		public Float merge(final Byte key, final Float value, final java.util.function.BiFunction<? super Float, ? super Float, ? extends Float> remappingFunction) {
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
	public static Byte2FloatMap synchronize(final Byte2FloatMap m) {
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
	public static Byte2FloatMap synchronize(final Byte2FloatMap m, final Object sync) {
		return new SynchronizedMap(m, sync);
	}

	/** An unmodifiable wrapper class for maps. */
	public static class UnmodifiableMap extends Byte2FloatFunctions.UnmodifiableFunction implements Byte2FloatMap, java.io.Serializable {
		private static final long serialVersionUID = -7046029254386353129L;
		protected final Byte2FloatMap map;
		protected transient ObjectSet<Byte2FloatMap.Entry> entries;
		protected transient ByteSet keys;
		protected transient FloatCollection values;

		protected UnmodifiableMap(final Byte2FloatMap m) {
			super(m);
			this.map = m;
		}

		@Override
		public boolean containsValue(final float v) {
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
		public void putAll(final Map<? extends Byte, ? extends Float> m) {
			throw new UnsupportedOperationException();
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public ObjectSet<Byte2FloatMap.Entry> byte2FloatEntrySet() {
			if (entries == null) entries = ObjectSets.unmodifiable((ObjectSet)map.byte2FloatEntrySet());
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
		public ObjectSet<Map.Entry<Byte, Float>> entrySet() {
			return (ObjectSet)byte2FloatEntrySet();
		}

		@Override
		public ByteSet keySet() {
			if (keys == null) keys = ByteSets.unmodifiable(map.keySet());
			return keys;
		}

		@Override
		public FloatCollection values() {
			if (values == null) values = FloatCollections.unmodifiable(map.values());
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
		public float getOrDefault(final byte key, final float defaultValue) {
			return map.getOrDefault(key, defaultValue);
		}

		@Override
		public void forEach(final java.util.function.BiConsumer<? super Byte, ? super Float> action) {
			map.forEach(action);
		}

		@Override
		public void replaceAll(final java.util.function.BiFunction<? super Byte, ? super Float, ? extends Float> function) {
			throw new UnsupportedOperationException();
		}

		@Override
		public float putIfAbsent(final byte key, final float value) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean remove(final byte key, final float value) {
			throw new UnsupportedOperationException();
		}

		@Override
		public float replace(final byte key, final float value) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean replace(final byte key, final float oldValue, final float newValue) {
			throw new UnsupportedOperationException();
		}

		@Override
		public float computeIfAbsent(final byte key, final java.util.function.IntToDoubleFunction mappingFunction) {
			throw new UnsupportedOperationException();
		}

		@Override
		public float computeIfAbsentNullable(final byte key, final java.util.function.IntFunction<? extends Float> mappingFunction) {
			throw new UnsupportedOperationException();
		}

		@Override
		public float computeIfAbsent(final byte key, final Byte2FloatFunction mappingFunction) {
			throw new UnsupportedOperationException();
		}

		@Override
		public float computeIfPresent(final byte key, final java.util.function.BiFunction<? super Byte, ? super Float, ? extends Float> remappingFunction) {
			throw new UnsupportedOperationException();
		}

		@Override
		public float compute(final byte key, final java.util.function.BiFunction<? super Byte, ? super Float, ? extends Float> remappingFunction) {
			throw new UnsupportedOperationException();
		}

		@Override
		public float merge(final byte key, final float value, final java.util.function.BiFunction<? super Float, ? super Float, ? extends Float> remappingFunction) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public Float getOrDefault(final Object key, final Float defaultValue) {
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
		public Float replace(final Byte key, final Float value) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public boolean replace(final Byte key, final Float oldValue, final Float newValue) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public Float putIfAbsent(final Byte key, final Float value) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public Float computeIfAbsent(final Byte key, final java.util.function.Function<? super Byte, ? extends Float> mappingFunction) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public Float computeIfPresent(final Byte key, final java.util.function.BiFunction<? super Byte, ? super Float, ? extends Float> remappingFunction) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public Float compute(final Byte key, final java.util.function.BiFunction<? super Byte, ? super Float, ? extends Float> remappingFunction) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public Float merge(final Byte key, final Float value, final java.util.function.BiFunction<? super Float, ? super Float, ? extends Float> remappingFunction) {
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
	public static Byte2FloatMap unmodifiable(final Byte2FloatMap m) {
		return new UnmodifiableMap(m);
	}
}
