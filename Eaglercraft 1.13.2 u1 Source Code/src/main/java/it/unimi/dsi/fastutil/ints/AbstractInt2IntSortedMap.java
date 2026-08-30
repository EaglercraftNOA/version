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

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

/**
 * An abstract class providing basic methods for sorted maps implementing a type-specific interface.
 */
public abstract class AbstractInt2IntSortedMap extends AbstractInt2IntMap implements Int2IntSortedMap {
	private static final long serialVersionUID = -1773560792952436569L;

	protected AbstractInt2IntSortedMap() {
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
	public IntSortedSet keySet() {
		return new KeySet();
	}

	/** A wrapper exhibiting the keys of a map. */
	protected class KeySet extends AbstractIntSortedSet {
		@Override
		public boolean contains(final int k) {
			return containsKey(k);
		}

		@Override
		public int size() {
			return AbstractInt2IntSortedMap.this.size();
		}

		@Override
		public void clear() {
			AbstractInt2IntSortedMap.this.clear();
		}

		@Override
		public IntComparator comparator() {
			return AbstractInt2IntSortedMap.this.comparator();
		}

		@Override
		public int firstInt() {
			return firstIntKey();
		}

		@Override
		public int lastInt() {
			return lastIntKey();
		}

		@Override
		public IntSortedSet headSet(final int to) {
			return headMap(to).keySet();
		}

		@Override
		public IntSortedSet tailSet(final int from) {
			return tailMap(from).keySet();
		}

		@Override
		public IntSortedSet subSet(final int from, final int to) {
			return subMap(from, to).keySet();
		}

		@Override
		public IntBidirectionalIterator iterator(final int from) {
			return new KeySetIterator(int2IntEntrySet().iterator(new BasicEntry(from, (0))));
		}

		@Override
		public IntBidirectionalIterator iterator() {
			return new KeySetIterator(Int2IntSortedMaps.fastIterator(AbstractInt2IntSortedMap.this));
		}
	}

	/**
	 * A wrapper exhibiting a map iterator as an iterator on keys.
	 *
	 * <p>
	 * To provide an iterator on keys, just create an instance of this class using the corresponding
	 * iterator on entries.
	 */
	protected static class KeySetIterator implements IntBidirectionalIterator {
		protected final ObjectBidirectionalIterator<Int2IntMap.Entry> i;

		public KeySetIterator(ObjectBidirectionalIterator<Int2IntMap.Entry> i) {
			this.i = i;
		}

		@Override
		public int nextInt() {
			return i.next().getIntKey();
		};

		@Override
		public int previousInt() {
			return i.previous().getIntKey();
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
	public IntCollection values() {
		return new ValuesCollection();
	}

	/** A wrapper exhibiting the values of a map. */
	protected class ValuesCollection extends AbstractIntCollection {
		@Override
		public IntIterator iterator() {
			return new ValuesIterator(Int2IntSortedMaps.fastIterator(AbstractInt2IntSortedMap.this));
		}

		@Override
		public boolean contains(final int k) {
			return containsValue(k);
		}

		@Override
		public int size() {
			return AbstractInt2IntSortedMap.this.size();
		}

		@Override
		public void clear() {
			AbstractInt2IntSortedMap.this.clear();
		}
	}

	/**
	 * A wrapper exhibiting a map iterator as an iterator on values.
	 *
	 * <p>
	 * To provide an iterator on values, just create an instance of this class using the corresponding
	 * iterator on entries.
	 */
	protected static class ValuesIterator implements IntIterator {
		protected final ObjectBidirectionalIterator<Int2IntMap.Entry> i;

		public ValuesIterator(ObjectBidirectionalIterator<Int2IntMap.Entry> i) {
			this.i = i;
		}

		@Override
		public int nextInt() {
			return i.next().getIntValue();
		};

		@Override
		public boolean hasNext() {
			return i.hasNext();
		}
	}
}
