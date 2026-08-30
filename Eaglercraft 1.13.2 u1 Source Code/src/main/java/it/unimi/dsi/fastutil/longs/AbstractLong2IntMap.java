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
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.AbstractIntCollection;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntSpliterator;
import it.unimi.dsi.fastutil.ints.IntSpliterators;
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
public abstract class AbstractLong2IntMap extends AbstractLong2IntFunction implements Long2IntMap, java.io.Serializable {
	private static final long serialVersionUID = -4940583368468432370L;

	protected AbstractLong2IntMap() {
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
		final ObjectIterator<Long2IntMap.Entry> i = long2IntEntrySet().iterator();
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
	public boolean containsValue(final int v) {
		final ObjectIterator<Long2IntMap.Entry> i = long2IntEntrySet().iterator();
		while (i.hasNext()) if (i.next().getIntValue() == v) return true;
		return false;
	}

	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @implSpec This method just delegates to the interface default method, as the default method, but
	 *           it is final, so it cannot be overridden.
	 */
	@Override
	public final int mergeInt(final long key, final int value, final it.unimi.dsi.fastutil.ints.IntBinaryOperator remappingFunction) {
		return mergeInt(key, value, (java.util.function.IntBinaryOperator)remappingFunction);
	}

	/**
	 * This class provides a basic but complete type-specific entry class for all those maps
	 * implementations that do not have entries on their own (e.g., most immutable maps).
	 *
	 * <p>
	 * This class does not implement {@link java.util.Map.Entry#setValue(Object) setValue()}, as the
	 * modification would not be reflected in the base map.
	 */
	public static class BasicEntry implements Long2IntMap.Entry {
		protected long key;
		protected int value;

		public BasicEntry() {
		}

		public BasicEntry(final Long key, final Integer value) {
			this.key = (key).longValue();
			this.value = (value).intValue();
		}

		public BasicEntry(final long key, final int value) {
			this.key = key;
			this.value = value;
		}

		@Override
		public long getLongKey() {
			return key;
		}

		@Override
		public int getIntValue() {
			return value;
		}

		@Override
		public int setValue(final int value) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean equals(final Object o) {
			if (!(o instanceof Map.Entry)) return false;
			if (o instanceof Long2IntMap.Entry) {
				final Long2IntMap.Entry e = (Long2IntMap.Entry)o;
				return ((key) == (e.getLongKey())) && ((value) == (e.getIntValue()));
			}
			final Map.Entry<?, ?> e = (Map.Entry<?, ?>)o;
			final Object key = e.getKey();
			if (key == null || !(key instanceof Long)) return false;
			final Object value = e.getValue();
			if (value == null || !(value instanceof Integer)) return false;
			return ((this.key) == (((Long)(key)).longValue())) && ((this.value) == (((Integer)(value)).intValue()));
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
		protected final Long2IntMap map;

		public BasicEntrySet(final Long2IntMap map) {
			this.map = map;
		}

		@Override
		public boolean contains(final Object o) {
			if (!(o instanceof Map.Entry)) return false;
			if (o instanceof Long2IntMap.Entry) {
				final Long2IntMap.Entry e = (Long2IntMap.Entry)o;
				final long k = e.getLongKey();
				return map.containsKey(k) && ((map.get(k)) == (e.getIntValue()));
			}
			final Map.Entry<?, ?> e = (Map.Entry<?, ?>)o;
			final Object key = e.getKey();
			if (key == null || !(key instanceof Long)) return false;
			final long k = ((Long)(key)).longValue();
			final Object value = e.getValue();
			if (value == null || !(value instanceof Integer)) return false;
			return map.containsKey(k) && ((map.get(k)) == (((Integer)(value)).intValue()));
		}

		@Override
		public boolean remove(final Object o) {
			if (!(o instanceof Map.Entry)) return false;
			if (o instanceof Long2IntMap.Entry) {
				final Long2IntMap.Entry e = (Long2IntMap.Entry)o;
				return map.remove(e.getLongKey(), e.getIntValue());
			}
			Map.Entry<?, ?> e = (Map.Entry<?, ?>)o;
			final Object key = e.getKey();
			if (key == null || !(key instanceof Long)) return false;
			final long k = ((Long)(key)).longValue();
			final Object value = e.getValue();
			if (value == null || !(value instanceof Integer)) return false;
			final int v = ((Integer)(value)).intValue();
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
				return AbstractLong2IntMap.this.size();
			}

			@Override
			public void clear() {
				AbstractLong2IntMap.this.clear();
			}

			@Override
			public LongIterator iterator() {
				return new LongIterator() {
					private final ObjectIterator<Long2IntMap.Entry> i = Long2IntMaps.fastIterator(AbstractLong2IntMap.this);

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
				return LongSpliterators.asSpliterator(iterator(), sizeOf(AbstractLong2IntMap.this), LongSpliterators.SET_SPLITERATOR_CHARACTERISTICS);
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
	public IntCollection values() {
		return new AbstractIntCollection() {
			@Override
			public boolean contains(final int k) {
				return containsValue(k);
			}

			@Override
			public int size() {
				return AbstractLong2IntMap.this.size();
			}

			@Override
			public void clear() {
				AbstractLong2IntMap.this.clear();
			}

			@Override
			public IntIterator iterator() {
				return new IntIterator() {
					private final ObjectIterator<Long2IntMap.Entry> i = Long2IntMaps.fastIterator(AbstractLong2IntMap.this);

					@Override
					public int nextInt() {
						return i.next().getIntValue();
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
					public void forEachRemaining(final java.util.function.IntConsumer action) {
						i.forEachRemaining(entry -> action.accept(entry.getIntValue()));
					}
				};
			}

			@Override
			public IntSpliterator spliterator() {
				return IntSpliterators.asSpliterator(iterator(), sizeOf(AbstractLong2IntMap.this), IntSpliterators.COLLECTION_SPLITERATOR_CHARACTERISTICS);
			}
		};
	}

	/** {@inheritDoc} */
	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public void putAll(final Map<? extends Long, ? extends Integer> m) {
		if (m instanceof Long2IntMap) {
			ObjectIterator<Long2IntMap.Entry> i = Long2IntMaps.fastIterator((Long2IntMap)m);
			while (i.hasNext()) {
				final Long2IntMap.Entry e = i.next();
				put(e.getLongKey(), e.getIntValue());
			}
		} else {
			int n = m.size();
			final Iterator<? extends Map.Entry<? extends Long, ? extends Integer>> i = m.entrySet().iterator();
			Map.Entry<? extends Long, ? extends Integer> e;
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
		final ObjectIterator<Long2IntMap.Entry> i = Long2IntMaps.fastIterator(this);
		while (n-- != 0) h += i.next().hashCode();
		return h;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		if (!(o instanceof Map)) return false;
		final Map<?, ?> m = (Map<?, ?>)o;
		if (m.size() != size()) return false;
		return long2IntEntrySet().containsAll(m.entrySet());
	}

	@Override
	public String toString() {
		final StringBuilder s = new StringBuilder();
		final ObjectIterator<Long2IntMap.Entry> i = Long2IntMaps.fastIterator(this);
		int n = size();
		Long2IntMap.Entry e;
		boolean first = true;
		s.append("{");
		while (n-- != 0) {
			if (first) first = false;
			else s.append(", ");
			e = i.next();
			s.append(String.valueOf(e.getLongKey()));
			s.append("=>");
			s.append(String.valueOf(e.getIntValue()));
		}
		s.append("}");
		return s.toString();
	}
}
