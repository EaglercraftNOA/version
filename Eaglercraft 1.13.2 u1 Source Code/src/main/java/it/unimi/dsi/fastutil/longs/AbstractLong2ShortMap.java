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

import static it.unimi.dsi.fastutil.Size64.sizeOf;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import it.unimi.dsi.fastutil.shorts.AbstractShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import it.unimi.dsi.fastutil.shorts.ShortSpliterator;
import it.unimi.dsi.fastutil.shorts.ShortSpliterators;
import it.unimi.dsi.fastutil.shorts.ShortConsumer;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterators;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import java.util.Iterator;
import java.util.Map;

/**
 * An abstract class providing basic methods for maps implementing a type-specific interface.
 *
 * <p>
 * Optional operations just throw an {@link UnsupportedOperationException}. Generic versions of
 * accessors delegate to the corresponding type-specific counterparts following the interface rules
 * (they take care of returning {@code null} on a missing key).
 *
 * <p>
 * As a further help, this class provides a {@link BasicEntry BasicEntry} inner class that
 * implements a type-specific version of {@link java.util.Map.Entry}; it is particularly useful for
 * those classes that do not implement their own entries (e.g., most immutable maps).
 */
public abstract class AbstractLong2ShortMap extends AbstractLong2ShortFunction implements Long2ShortMap, java.io.Serializable {
	private static final long serialVersionUID = -4940583368468432370L;

	protected AbstractLong2ShortMap() {
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @implSpec This implementation does a linear search over the entry set, finding an entry that has
	 *           the key specified.
	 *           <p>
	 *           If you override {@link #keySet()}, you should probably override this method too to take
	 *           advantage of the (presumably) faster {@linkplain java.util.Set#contains key membership
	 *           test} your {@link #keySet()} provides.
	 *           <p>
	 *           If you override this method but not {@link #keySet()}, then the returned key set will
	 *           take advantage of this method.
	 */
	@Override
	public boolean containsKey(final long k) {
		final ObjectIterator<Long2ShortMap.Entry> i = long2ShortEntrySet().iterator();
		while (i.hasNext()) if (i.next().getLongKey() == k) return true;
		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @implSpec This implementation does a linear search over the entry set, finding an entry that has
	 *           the value specified.
	 *           <p>
	 *           If you override {@link #values()}, you should probably override this method too to take
	 *           advantage of the (presumably) faster {@linkplain java.util.Collection#contains value
	 *           membership test} your {@link #values()} provides.
	 *           <p>
	 *           If you override this method but not {@link #values()}, then the returned values
	 *           collection will take advantage of this method.
	 */
	@Override
	public boolean containsValue(final short v) {
		final ObjectIterator<Long2ShortMap.Entry> i = long2ShortEntrySet().iterator();
		while (i.hasNext()) if (i.next().getShortValue() == v) return true;
		return false;
	}

	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	/**
	 * This class provides a basic but complete type-specific entry class for all those maps
	 * implementations that do not have entries on their own (e.g., most immutable maps).
	 *
	 * <p>
	 * This class does not implement {@link java.util.Map.Entry#setValue(Object) setValue()}, as the
	 * modification would not be reflected in the base map.
	 */
	public static class BasicEntry implements Long2ShortMap.Entry {
		protected long key;
		protected short value;

		public BasicEntry() {
		}

		public BasicEntry(final Long key, final Short value) {
			this.key = (key).longValue();
			this.value = (value).shortValue();
		}

		public BasicEntry(final long key, final short value) {
			this.key = key;
			this.value = value;
		}

		@Override
		public long getLongKey() {
			return key;
		}

		@Override
		public short getShortValue() {
			return value;
		}

		@Override
		public short setValue(final short value) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean equals(final Object o) {
			if (!(o instanceof Map.Entry)) return false;
			if (o instanceof Long2ShortMap.Entry) {
				final Long2ShortMap.Entry e = (Long2ShortMap.Entry)o;
				return ((key) == (e.getLongKey())) && ((value) == (e.getShortValue()));
			}
			final Map.Entry<?, ?> e = (Map.Entry<?, ?>)o;
			final Object key = e.getKey();
			if (key == null || !(key instanceof Long)) return false;
			final Object value = e.getValue();
			if (value == null || !(value instanceof Short)) return false;
			return ((this.key) == (((Long)(key)).longValue())) && ((this.value) == (((Short)(value)).shortValue()));
		}

		@Override
		public int hashCode() {
			return it.unimi.dsi.fastutil.HashCommon.long2int(key) ^ (value);
		}

		@Override
		public String toString() {
			return key + "->" + value;
		}
	}

	/**
	 * This class provides a basic implementation for an Entry set which forwards some queries to the
	 * map.
	 */
	public abstract static class BasicEntrySet extends AbstractObjectSet<Entry> {
		protected final Long2ShortMap map;

		public BasicEntrySet(final Long2ShortMap map) {
			this.map = map;
		}

		@Override
		public boolean contains(final Object o) {
			if (!(o instanceof Map.Entry)) return false;
			if (o instanceof Long2ShortMap.Entry) {
				final Long2ShortMap.Entry e = (Long2ShortMap.Entry)o;
				final long k = e.getLongKey();
				return map.containsKey(k) && ((map.get(k)) == (e.getShortValue()));
			}
			final Map.Entry<?, ?> e = (Map.Entry<?, ?>)o;
			final Object key = e.getKey();
			if (key == null || !(key instanceof Long)) return false;
			final long k = ((Long)(key)).longValue();
			final Object value = e.getValue();
			if (value == null || !(value instanceof Short)) return false;
			return map.containsKey(k) && ((map.get(k)) == (((Short)(value)).shortValue()));
		}

		@Override
		public boolean remove(final Object o) {
			if (!(o instanceof Map.Entry)) return false;
			if (o instanceof Long2ShortMap.Entry) {
				final Long2ShortMap.Entry e = (Long2ShortMap.Entry)o;
				return map.remove(e.getLongKey(), e.getShortValue());
			}
			Map.Entry<?, ?> e = (Map.Entry<?, ?>)o;
			final Object key = e.getKey();
			if (key == null || !(key instanceof Long)) return false;
			final long k = ((Long)(key)).longValue();
			final Object value = e.getValue();
			if (value == null || !(value instanceof Short)) return false;
			final short v = ((Short)(value)).shortValue();
			return map.remove(k, v);
		}

		@Override
		public int size() {
			return map.size();
		}

		@Override
		public ObjectSpliterator<Entry> spliterator() {
			return ObjectSpliterators.asSpliterator(iterator(), sizeOf(map), ObjectSpliterators.SET_SPLITERATOR_CHARACTERISTICS);
		}
	}

	/**
	 * Returns a type-specific-set view of the keys of this map.
	 *
	 * <p>
	 * The view is backed by the set returned by {@link Map#entrySet()}. Note that <em>no attempt is
	 * made at caching the result of this method</em>, as this would require adding some attributes that
	 * lightweight implementations would not need. Subclasses may easily override this policy by calling
	 * this method and caching the result, but implementors are encouraged to write more efficient
	 * ad-hoc implementations.
	 *
	 * @return a set view of the keys of this map; it may be safely cast to a type-specific interface.
	 */
	@Override
	public LongSet keySet() {
		return new AbstractLongSet() {
			@Override
			public boolean contains(final long k) {
				return containsKey(k);
			}

			@Override
			public int size() {
				return AbstractLong2ShortMap.this.size();
			}

			@Override
			public void clear() {
				AbstractLong2ShortMap.this.clear();
			}

			@Override
			public LongIterator iterator() {
				return new LongIterator() {
					private final ObjectIterator<Long2ShortMap.Entry> i = Long2ShortMaps.fastIterator(AbstractLong2ShortMap.this);

					@Override
					public long nextLong() {
						return i.next().getLongKey();
					}

					@Override
					public boolean hasNext() {
						return i.hasNext();
					}

					@Override
					public void remove() {
						i.remove();
					}

					@Override
					public void forEachRemaining(final java.util.function.LongConsumer action) {
						i.forEachRemaining(entry -> action.accept(entry.getLongKey()));
					}
				};
			}

			@Override
			public LongSpliterator spliterator() {
				return LongSpliterators.asSpliterator(iterator(), sizeOf(AbstractLong2ShortMap.this), LongSpliterators.SET_SPLITERATOR_CHARACTERISTICS);
			}
		};
	}

	/**
	 * Returns a type-specific-set view of the values of this map.
	 *
	 * <p>
	 * The view is backed by the set returned by {@link Map#entrySet()}. Note that <em>no attempt is
	 * made at caching the result of this method</em>, as this would require adding some attributes that
	 * lightweight implementations would not need. Subclasses may easily override this policy by calling
	 * this method and caching the result, but implementors are encouraged to write more efficient
	 * ad-hoc implementations.
	 *
	 * @return a set view of the values of this map; it may be safely cast to a type-specific interface.
	 */
	@Override
	public ShortCollection values() {
		return new AbstractShortCollection() {
			@Override
			public boolean contains(final short k) {
				return containsValue(k);
			}

			@Override
			public int size() {
				return AbstractLong2ShortMap.this.size();
			}

			@Override
			public void clear() {
				AbstractLong2ShortMap.this.clear();
			}

			@Override
			public ShortIterator iterator() {
				return new ShortIterator() {
					private final ObjectIterator<Long2ShortMap.Entry> i = Long2ShortMaps.fastIterator(AbstractLong2ShortMap.this);

					@Override
					public short nextShort() {
						return i.next().getShortValue();
					}

					@Override
					public boolean hasNext() {
						return i.hasNext();
					}

					@Override
					public void remove() {
						i.remove();
					}

					@Override
					public void forEachRemaining(final ShortConsumer action) {
						i.forEachRemaining(entry -> action.accept(entry.getShortValue()));
					}
				};
			}

			@Override
			public ShortSpliterator spliterator() {
				return ShortSpliterators.asSpliterator(iterator(), sizeOf(AbstractLong2ShortMap.this), ShortSpliterators.COLLECTION_SPLITERATOR_CHARACTERISTICS);
			}
		};
	}

	/** {@inheritDoc} */
	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public void putAll(final Map<? extends Long, ? extends Short> m) {
		if (m instanceof Long2ShortMap) {
			ObjectIterator<Long2ShortMap.Entry> i = Long2ShortMaps.fastIterator((Long2ShortMap)m);
			while (i.hasNext()) {
				final Long2ShortMap.Entry e = i.next();
				put(e.getLongKey(), e.getShortValue());
			}
		} else {
			int n = m.size();
			final Iterator<? extends Map.Entry<? extends Long, ? extends Short>> i = m.entrySet().iterator();
			Map.Entry<? extends Long, ? extends Short> e;
			while (n-- != 0) {
				e = i.next();
				put(e.getKey(), e.getValue());
			}
		}
	}

	/**
	 * Returns a hash code for this map.
	 *
	 * The hash code of a map is computed by summing the hash codes of its entries.
	 *
	 * @return a hash code for this map.
	 */
	@Override
	public int hashCode() {
		int h = 0, n = size();
		final ObjectIterator<Long2ShortMap.Entry> i = Long2ShortMaps.fastIterator(this);
		while (n-- != 0) h += i.next().hashCode();
		return h;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		if (!(o instanceof Map)) return false;
		final Map<?, ?> m = (Map<?, ?>)o;
		if (m.size() != size()) return false;
		return long2ShortEntrySet().containsAll(m.entrySet());
	}

	@Override
	public String toString() {
		final StringBuilder s = new StringBuilder();
		final ObjectIterator<Long2ShortMap.Entry> i = Long2ShortMaps.fastIterator(this);
		int n = size();
		Long2ShortMap.Entry e;
		boolean first = true;
		s.append("{");
		while (n-- != 0) {
			if (first) first = false;
			else s.append(", ");
			e = i.next();
			s.append(String.valueOf(e.getLongKey()));
			s.append("=>");
			s.append(String.valueOf(e.getShortValue()));
		}
		s.append("}");
		return s.toString();
	}
}
