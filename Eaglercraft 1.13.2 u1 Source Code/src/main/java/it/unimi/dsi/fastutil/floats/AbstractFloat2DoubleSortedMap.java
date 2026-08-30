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
package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.AbstractDoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

/**
 * An abstract class providing basic methods for sorted maps implementing a type-specific interface.
 */
public abstract class AbstractFloat2DoubleSortedMap extends AbstractFloat2DoubleMap implements Float2DoubleSortedMap {
	private static final long serialVersionUID = -1773560792952436569L;

	protected AbstractFloat2DoubleSortedMap() {
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
	public FloatSortedSet keySet() {
		return new KeySet();
	}

	/** A wrapper exhibiting the keys of a map. */
	protected class KeySet extends AbstractFloatSortedSet {
		@Override
		public boolean contains(final float k) {
			return containsKey(k);
		}

		@Override
		public int size() {
			return AbstractFloat2DoubleSortedMap.this.size();
		}

		@Override
		public void clear() {
			AbstractFloat2DoubleSortedMap.this.clear();
		}

		@Override
		public FloatComparator comparator() {
			return AbstractFloat2DoubleSortedMap.this.comparator();
		}

		@Override
		public float firstFloat() {
			return firstFloatKey();
		}

		@Override
		public float lastFloat() {
			return lastFloatKey();
		}

		@Override
		public FloatSortedSet headSet(final float to) {
			return headMap(to).keySet();
		}

		@Override
		public FloatSortedSet tailSet(final float from) {
			return tailMap(from).keySet();
		}

		@Override
		public FloatSortedSet subSet(final float from, final float to) {
			return subMap(from, to).keySet();
		}

		@Override
		public FloatBidirectionalIterator iterator(final float from) {
			return new KeySetIterator(float2DoubleEntrySet().iterator(new BasicEntry(from, (0))));
		}

		@Override
		public FloatBidirectionalIterator iterator() {
			return new KeySetIterator(Float2DoubleSortedMaps.fastIterator(AbstractFloat2DoubleSortedMap.this));
		}
	}

	/**
	 * A wrapper exhibiting a map iterator as an iterator on keys.
	 *
	 * <p>
	 * To provide an iterator on keys, just create an instance of this class using the corresponding
	 * iterator on entries.
	 */
	protected static class KeySetIterator implements FloatBidirectionalIterator {
		protected final ObjectBidirectionalIterator<Float2DoubleMap.Entry> i;

		public KeySetIterator(ObjectBidirectionalIterator<Float2DoubleMap.Entry> i) {
			this.i = i;
		}

		@Override
		public float nextFloat() {
			return i.next().getFloatKey();
		};

		@Override
		public float previousFloat() {
			return i.previous().getFloatKey();
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
	public DoubleCollection values() {
		return new ValuesCollection();
	}

	/** A wrapper exhibiting the values of a map. */
	protected class ValuesCollection extends AbstractDoubleCollection {
		@Override
		public DoubleIterator iterator() {
			return new ValuesIterator(Float2DoubleSortedMaps.fastIterator(AbstractFloat2DoubleSortedMap.this));
		}

		@Override
		public boolean contains(final double k) {
			return containsValue(k);
		}

		@Override
		public int size() {
			return AbstractFloat2DoubleSortedMap.this.size();
		}

		@Override
		public void clear() {
			AbstractFloat2DoubleSortedMap.this.clear();
		}
	}

	/**
	 * A wrapper exhibiting a map iterator as an iterator on values.
	 *
	 * <p>
	 * To provide an iterator on values, just create an instance of this class using the corresponding
	 * iterator on entries.
	 */
	protected static class ValuesIterator implements DoubleIterator {
		protected final ObjectBidirectionalIterator<Float2DoubleMap.Entry> i;

		public ValuesIterator(ObjectBidirectionalIterator<Float2DoubleMap.Entry> i) {
			this.i = i;
		}

		@Override
		public double nextDouble() {
			return i.next().getDoubleValue();
		};

		@Override
		public boolean hasNext() {
			return i.hasNext();
		}
	}
}
