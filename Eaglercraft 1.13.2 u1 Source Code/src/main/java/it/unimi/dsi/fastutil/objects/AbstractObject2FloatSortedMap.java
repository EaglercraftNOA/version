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

import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.floats.AbstractFloatCollection;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import java.util.Comparator;

/**
 * An abstract class providing basic methods for sorted maps implementing a type-specific interface.
 */
public abstract class AbstractObject2FloatSortedMap<K> extends AbstractObject2FloatMap<K> implements Object2FloatSortedMap<K> {
	private static final long serialVersionUID = -1773560792952436569L;

	protected AbstractObject2FloatSortedMap() {
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The view is backed by the sorted set returned by {@link java.util.Map#entrySet()}. Note that
	 * <em>no attempt is made at caching the result of this method</em>, as this would require adding
	 * some attributes that lightweight implementations would not need. Subclasses may easily override
	 * this policy by calling this method and caching the result, but implementors are encouraged to
	 * write more efficient ad-hoc implementations.
	 *
	 * @return a sorted set view of the keys of this map; it may be safely cast to a type-specific
	 *         interface.
	 */
	@Override
	public ObjectSortedSet<K> keySet() {
		return new KeySet();
	}

	/** A wrapper exhibiting the keys of a map. */
	protected class KeySet extends AbstractObjectSortedSet<K> {
		@Override
		public boolean contains(final Object k) {
			return containsKey(k);
		}

		@Override
		public int size() {
			return AbstractObject2FloatSortedMap.this.size();
		}

		@Override
		public void clear() {
			AbstractObject2FloatSortedMap.this.clear();
		}

		@Override
		public Comparator<? super K> comparator() {
			return AbstractObject2FloatSortedMap.this.comparator();
		}

		@Override
		public K first() {
			return firstKey();
		}

		@Override
		public K last() {
			return lastKey();
		}

		@Override
		public ObjectSortedSet<K> headSet(final K to) {
			return headMap(to).keySet();
		}

		@Override
		public ObjectSortedSet<K> tailSet(final K from) {
			return tailMap(from).keySet();
		}

		@Override
		public ObjectSortedSet<K> subSet(final K from, final K to) {
			return subMap(from, to).keySet();
		}

		@Override
		public ObjectBidirectionalIterator<K> iterator(final K from) {
			return new KeySetIterator<>(object2FloatEntrySet().iterator(new BasicEntry<>(from, (0))));
		}

		@Override
		public ObjectBidirectionalIterator<K> iterator() {
			return new KeySetIterator<>(Object2FloatSortedMaps.fastIterator(AbstractObject2FloatSortedMap.this));
		}
	}

	/**
	 * A wrapper exhibiting a map iterator as an iterator on keys.
	 *
	 * <p>
	 * To provide an iterator on keys, just create an instance of this class using the corresponding
	 * iterator on entries.
	 */
	protected static class KeySetIterator<K> implements ObjectBidirectionalIterator<K> {
		protected final ObjectBidirectionalIterator<Object2FloatMap.Entry<K>> i;

		public KeySetIterator(ObjectBidirectionalIterator<Object2FloatMap.Entry<K>> i) {
			this.i = i;
		}

		@Override
		public K next() {
			return i.next().getKey();
		};

		@Override
		public K previous() {
			return i.previous().getKey();
		};

		@Override
		public boolean hasNext() {
			return i.hasNext();
		}

		@Override
		public boolean hasPrevious() {
			return i.hasPrevious();
		}
	}

	/**
	 * {@inheritDoc}
	 *
	 * <p>
	 * The view is backed by the sorted set returned by {@link java.util.Map#entrySet()}. Note that
	 * <em>no attempt is made at caching the result of this method</em>, as this would require adding
	 * some attributes that lightweight implementations would not need. Subclasses may easily override
	 * this policy by calling this method and caching the result, but implementors are encouraged to
	 * write more efficient ad-hoc implementations.
	 *
	 * @return a type-specific collection view of the values contained in this map.
	 */
	@Override
	public FloatCollection values() {
		return new ValuesCollection();
	}

	/** A wrapper exhibiting the values of a map. */
	protected class ValuesCollection extends AbstractFloatCollection {
		@Override
		public FloatIterator iterator() {
			return new ValuesIterator<>(Object2FloatSortedMaps.fastIterator(AbstractObject2FloatSortedMap.this));
		}

		@Override
		public boolean contains(final float k) {
			return containsValue(k);
		}

		@Override
		public int size() {
			return AbstractObject2FloatSortedMap.this.size();
		}

		@Override
		public void clear() {
			AbstractObject2FloatSortedMap.this.clear();
		}
	}

	/**
	 * A wrapper exhibiting a map iterator as an iterator on values.
	 *
	 * <p>
	 * To provide an iterator on values, just create an instance of this class using the corresponding
	 * iterator on entries.
	 */
	protected static class ValuesIterator<K> implements FloatIterator {
		protected final ObjectBidirectionalIterator<Object2FloatMap.Entry<K>> i;

		public ValuesIterator(ObjectBidirectionalIterator<Object2FloatMap.Entry<K>> i) {
			this.i = i;
		}

		@Override
		public float nextFloat() {
			return i.next().getFloatValue();
		};

		@Override
		public boolean hasNext() {
			return i.hasNext();
		}
	}
}
