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
package it.unimi.dsi.fastutil.chars;

import static it.unimi.dsi.fastutil.Size64.sizeOf;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.AbstractBooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import it.unimi.dsi.fastutil.booleans.BooleanSpliterator;
import it.unimi.dsi.fastutil.booleans.BooleanSpliterators;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
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
public abstract class AbstractChar2BooleanMap extends AbstractChar2BooleanFunction implements Char2BooleanMap, java.io.Serializable {
	private static final long serialVersionUID = -4940583368468432370L;

	protected AbstractChar2BooleanMap() {
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
	public boolean containsKey(final char k) {
		final ObjectIterator<Char2BooleanMap.Entry> i = char2BooleanEntrySet().iterator();
		while (i.hasNext()) if (i.next().getCharKey() == k) return true;
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
	public boolean containsValue(final boolean v) {
		final ObjectIterator<Char2BooleanMap.Entry> i = char2BooleanEntrySet().iterator();
		while (i.hasNext()) if (i.next().getBooleanValue() == v) return true;
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
	public static class BasicEntry implements Char2BooleanMap.Entry {
		protected char key;
		protected boolean value;

		public BasicEntry() {
		}

		public BasicEntry(final Character key, final Boolean value) {
			this.key = (key).charValue();
			this.value = (value).booleanValue();
		}

		public BasicEntry(final char key, final boolean value) {
			this.key = key;
			this.value = value;
		}

		@Override
		public char getCharKey() {
			return key;
		}

		@Override
		public boolean getBooleanValue() {
			return value;
		}

		@Override
		public boolean setValue(final boolean value) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean equals(final Object o) {
			if (!(o instanceof Map.Entry)) return false;
			if (o instanceof Char2BooleanMap.Entry) {
				final Char2BooleanMap.Entry e = (Char2BooleanMap.Entry)o;
				return ((key) == (e.getCharKey())) && ((value) == (e.getBooleanValue()));
			}
			final Map.Entry<?, ?> e = (Map.Entry<?, ?>)o;
			final Object key = e.getKey();
			if (key == null || !(key instanceof Character)) return false;
			final Object value = e.getValue();
			if (value == null || !(value instanceof Boolean)) return false;
			return ((this.key) == (((Character)(key)).charValue())) && ((this.value) == (((Boolean)(value)).booleanValue()));
		}

		@Override
		public int hashCode() {
			return (key) ^ (value ? 1231 : 1237);
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
		protected final Char2BooleanMap map;

		public BasicEntrySet(final Char2BooleanMap map) {
			this.map = map;
		}

		@Override
		public boolean contains(final Object o) {
			if (!(o instanceof Map.Entry)) return false;
			if (o instanceof Char2BooleanMap.Entry) {
				final Char2BooleanMap.Entry e = (Char2BooleanMap.Entry)o;
				final char k = e.getCharKey();
				return map.containsKey(k) && ((map.get(k)) == (e.getBooleanValue()));
			}
			final Map.Entry<?, ?> e = (Map.Entry<?, ?>)o;
			final Object key = e.getKey();
			if (key == null || !(key instanceof Character)) return false;
			final char k = ((Character)(key)).charValue();
			final Object value = e.getValue();
			if (value == null || !(value instanceof Boolean)) return false;
			return map.containsKey(k) && ((map.get(k)) == (((Boolean)(value)).booleanValue()));
		}

		@Override
		public boolean remove(final Object o) {
			if (!(o instanceof Map.Entry)) return false;
			if (o instanceof Char2BooleanMap.Entry) {
				final Char2BooleanMap.Entry e = (Char2BooleanMap.Entry)o;
				return map.remove(e.getCharKey(), e.getBooleanValue());
			}
			Map.Entry<?, ?> e = (Map.Entry<?, ?>)o;
			final Object key = e.getKey();
			if (key == null || !(key instanceof Character)) return false;
			final char k = ((Character)(key)).charValue();
			final Object value = e.getValue();
			if (value == null || !(value instanceof Boolean)) return false;
			final boolean v = ((Boolean)(value)).booleanValue();
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
	public CharSet keySet() {
		return new AbstractCharSet() {
			@Override
			public boolean contains(final char k) {
				return containsKey(k);
			}

			@Override
			public int size() {
				return AbstractChar2BooleanMap.this.size();
			}

			@Override
			public void clear() {
				AbstractChar2BooleanMap.this.clear();
			}

			@Override
			public CharIterator iterator() {
				return new CharIterator() {
					private final ObjectIterator<Char2BooleanMap.Entry> i = Char2BooleanMaps.fastIterator(AbstractChar2BooleanMap.this);

					@Override
					public char nextChar() {
						return i.next().getCharKey();
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
						i.forEachRemaining(entry -> action.accept(entry.getCharKey()));
					}
				};
			}

			@Override
			public CharSpliterator spliterator() {
				return CharSpliterators.asSpliterator(iterator(), sizeOf(AbstractChar2BooleanMap.this), CharSpliterators.SET_SPLITERATOR_CHARACTERISTICS);
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
	public BooleanCollection values() {
		return new AbstractBooleanCollection() {
			@Override
			public boolean contains(final boolean k) {
				return containsValue(k);
			}

			@Override
			public int size() {
				return AbstractChar2BooleanMap.this.size();
			}

			@Override
			public void clear() {
				AbstractChar2BooleanMap.this.clear();
			}

			@Override
			public BooleanIterator iterator() {
				return new BooleanIterator() {
					private final ObjectIterator<Char2BooleanMap.Entry> i = Char2BooleanMaps.fastIterator(AbstractChar2BooleanMap.this);

					@Override
					public boolean nextBoolean() {
						return i.next().getBooleanValue();
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
					public void forEachRemaining(final BooleanConsumer action) {
						i.forEachRemaining(entry -> action.accept(entry.getBooleanValue()));
					}
				};
			}

			@Override
			public BooleanSpliterator spliterator() {
				return BooleanSpliterators.asSpliterator(iterator(), sizeOf(AbstractChar2BooleanMap.this), BooleanSpliterators.COLLECTION_SPLITERATOR_CHARACTERISTICS);
			}
		};
	}

	/** {@inheritDoc} */
	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public void putAll(final Map<? extends Character, ? extends Boolean> m) {
		if (m instanceof Char2BooleanMap) {
			ObjectIterator<Char2BooleanMap.Entry> i = Char2BooleanMaps.fastIterator((Char2BooleanMap)m);
			while (i.hasNext()) {
				final Char2BooleanMap.Entry e = i.next();
				put(e.getCharKey(), e.getBooleanValue());
			}
		} else {
			int n = m.size();
			final Iterator<? extends Map.Entry<? extends Character, ? extends Boolean>> i = m.entrySet().iterator();
			Map.Entry<? extends Character, ? extends Boolean> e;
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
		final ObjectIterator<Char2BooleanMap.Entry> i = Char2BooleanMaps.fastIterator(this);
		while (n-- != 0) h += i.next().hashCode();
		return h;
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) return true;
		if (!(o instanceof Map)) return false;
		final Map<?, ?> m = (Map<?, ?>)o;
		if (m.size() != size()) return false;
		return char2BooleanEntrySet().containsAll(m.entrySet());
	}

	@Override
	public String toString() {
		final StringBuilder s = new StringBuilder();
		final ObjectIterator<Char2BooleanMap.Entry> i = Char2BooleanMaps.fastIterator(this);
		int n = size();
		Char2BooleanMap.Entry e;
		boolean first = true;
		s.append("{");
		while (n-- != 0) {
			if (first) first = false;
			else s.append(", ");
			e = i.next();
			s.append(String.valueOf(e.getCharKey()));
			s.append("=>");
			s.append(String.valueOf(e.getBooleanValue()));
		}
		s.append("}");
		return s.toString();
	}
}
