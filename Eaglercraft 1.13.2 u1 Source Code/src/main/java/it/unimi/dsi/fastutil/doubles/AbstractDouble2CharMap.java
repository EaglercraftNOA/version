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

import static it.unimi.dsi.fastutil.Size64.sizeOf;
import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.chars.AbstractCharCollection;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.chars.CharSpliterator;
import it.unimi.dsi.fastutil.chars.CharSpliterators;
import it.unimi.dsi.fastutil.chars.CharConsumer;
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
public abstract class AbstractDouble2CharMap extends AbstractDouble2CharFunction implements Double2CharMap, java.io.Serializable {
	private static final long serialVersionUID = -4940583368468432370L;

	protected AbstractDouble2CharMap() {
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
	public boolean containsKey(final double k) {
		final ObjectIterator<Double2CharMap.Entry> i = double2CharEntrySet().iterator();
		while (i.hasNext()) if (i.next().getDoubleKey() == k) return true;
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
	public boolean containsValue(final char v) {
		final ObjectIterator<Double2CharMap.Entry> i = double2CharEntrySet().iterator();
		while (i.hasNext()) if (i.next().getCharValue() == v) return true;
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
	public static class BasicEntry implements Double2CharMap.Entry {
		protected double key;
		protected char value;

		public BasicEntry() {
		}

		public BasicEntry(final Double key, final Character value) {
			this.key = (key).doubleValue();
			this.value = (value).charValue();
		}

		public BasicEntry(final double key, final char value) {
			this.key = key;
			this.value = value;
		}

		@Override
		public double getDoubleKey() {
			return key;
		}

		@Override
		public char getCharValue() {
			return value;
		}

		@Override
		public char setValue(final char value) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean equals(final Object o) {
			if (!(o instanceof Map.Entry)) return false;
			if (o instanceof Double2CharMap.Entry) {
				final Double2CharMap.Entry e = (Double2CharMap.Entry)o;
				return (Double.doubleToLongBits(key) == Double.doubleToLongBits(e.getDoubleKey())) && ((value) == (e.getCharValue()));
			}
			final Map.Entry<?, ?> e = (Map.Entry<?, ?>)o;
			final Object key = e.getKey();
			if (key == null || !(key instanceof Double)) return false;
			final Object value = e.getValue();
			if (value == null || !(value instanceof Character)) return false;
			return (Double.doubleToLongBits(this.key) == Double.doubleToLongBits(((Double)(key)).doubleValue())) && ((this.value) == (((Character)(value)).charValue()));
		}

		@Override
		public int hashCode() {
			return it.unimi.dsi.fastutil.HashCommon.double2int(key) ^ (value);
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
		protected final Double2CharMap map;

		public BasicEntrySet(final Double2CharMap map) {
			this.map = map;
		}

		@Override
		public boolean contains(final Object o) {
			if (!(o instanceof Map.Entry)) return false;
			if (o instanceof Double2CharMap.Entry) {
				final Double2CharMap.Entry e = (Double2CharMap.Entry)o;
				final double k = e.getDoubleKey();
				return map.containsKey(k) && ((map.get(k)) == (e.getCharValue()));
			}
			final Map.Entry<?, ?> e = (Map.Entry<?, ?>)o;
			final Object key = e.getKey();
			if (key == null || !(key instanceof Double)) return false;
			final double k = ((Double)(key)).doubleValue();
			final Object value = e.getValue();
			if (value == null || !(value instanceof Character)) return false;
			return map.containsKey(k) && ((map.get(k)) == (((Character)(value)).charValue()));
		}

		@Override
		public boolean remove(final Object o) {
			if (!(o instanceof Map.Entry)) return false;
			if (o instanceof Double2CharMap.Entry) {
				final Double2CharMap.Entry e = (Double2CharMap.Entry)o;
				return map.remove(e.getDoubleKey(), e.getCharValue());
			}
			Map.Entry<?, ?> e = (Map.Entry<?, ?>)o;
			final Object key = e.getKey();
			if (key == null || !(key instanceof Double)) return false;
			final double k = ((Double)(key)).doubleValue();
			final Object value = e.getValue();
			if (value == null || !(value instanceof Character)) return false;
			final char v = ((Character)(value)).charValue();
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
	public DoubleSet keySet() {
		return new AbstractDoubleSet() {
			@Override
			public boolean contains(final double k) {
				return containsKey(k);
			}

			@Override
			public int size() {
				return AbstractDouble2CharMap.this.size();
			}

			@Override
			public void clear() {
				AbstractDouble2CharMap.this.clear();
			}

			@Override
			public DoubleIterator iterator() {
				return new DoubleIterator() {
					private final ObjectIterator<Double2CharMap.Entry> i = Double2CharMaps.fastIterator(AbstractDouble2CharMap.this);

					@Override
					public double nextDouble() {
						return i.next().getDoubleKey();
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
					public void forEachRemaining(final java.util.function.DoubleConsumer action) {
						i.forEachRemaining(entry -> action.accept(entry.getDoubleKey()));
					}
				};
			}

			@Override
			public DoubleSpliterator spliterator() {
				return DoubleSpliterators.asSpliterator(iterator(), sizeOf(AbstractDouble2CharMap.this), DoubleSpliterators.SET_SPLITERATOR_CHARACTERISTICS);
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
	public CharCollection values() {
		return new AbstractCharCollection() {
			@Override
			public boolean contains(final char k) {
				return containsValue(k);
			}

			@Override
			public int size() {
				return AbstractDouble2CharMap.this.size();
			}

			@Override
			public void clear() {
				AbstractDouble2CharMap.this.clear();
			}

			@Override
			public CharIterator iterator() {
				return new CharIterator() {
					private final ObjectIterator<Double2CharMap.Entry> i = Double2CharMaps.fastIterator(AbstractDouble2CharMap.this);

					@Override
					public char nextChar() {
						return i.next().getCharValue();
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
					public void forEachRemaining(final CharConsumer action) {
						i.forEachRemaining(entry -> action.accept(entry.getCharValue()));
					}
				};
			}

			@Override
			public CharSpliterator spliterator() {
				return CharSpliterators.asSpliterator(iterator(), sizeOf(AbstractDouble2CharMap.this), CharSpliterators.COLLECTION_SPLITERATOR_CHARACTERISTICS);
			}
		};
	}

	/** {@inheritDoc} */
	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public void putAll(final Map<? extends Double, ? extends Character> m) {
		if (m instanceof Double2CharMap) {
			ObjectIterator<Double2CharMap.Entry> i = Double2CharMaps.fastIterator((Double2CharMap)m);
			while (i.hasNext()) {
				final Double2CharMap.Entry e = i.next();
				put(e.getDoubleKey(), e.getCharValue());
			}
		} else {
			int n = m.size();
			final Iterator<? extends Map.Entry<? extends Double, ? extends Character>> i = m.entrySet().iterator();
			Map.Entry<? extends Double, ? extends Character> e;
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
		final ObjectIterator<Double2CharMap.Entry> i = Double2CharMaps.fastIterator(this);
		while (n-- != 0) h += i.next().hashCode();
		return h;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		if (!(o instanceof Map)) return false;
		final Map<?, ?> m = (Map<?, ?>)o;
		if (m.size() != size()) return false;
		return double2CharEntrySet().containsAll(m.entrySet());
	}

	@Override
	public String toString() {
		final StringBuilder s = new StringBuilder();
		final ObjectIterator<Double2CharMap.Entry> i = Double2CharMaps.fastIterator(this);
		int n = size();
		Double2CharMap.Entry e;
		boolean first = true;
		s.append("{");
		while (n-- != 0) {
			if (first) first = false;
			else s.append(", ");
			e = i.next();
			s.append(String.valueOf(e.getDoubleKey()));
			s.append("=>");
			s.append(String.valueOf(e.getCharValue()));
		}
		s.append("}");
		return s.toString();
	}
}
