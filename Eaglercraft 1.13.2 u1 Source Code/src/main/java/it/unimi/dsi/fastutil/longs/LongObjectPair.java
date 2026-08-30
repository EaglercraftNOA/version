/*
	* Copyright (C) 2020-2024 Sebastiano Vigna
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

/**
 * A type-specific {@link it.unimi.dsi.fastutil.Pair Pair}; provides some additional methods that
 * use polymorphism to avoid (un)boxing.
 */
public interface LongObjectPair<V> extends it.unimi.dsi.fastutil.Pair<Long, V> {
	/**
	 * Returns the left element of this pair.
	 *
	 * @return the left element of this pair.
	 */
	public long leftLong();

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	public default Long left() {
		return Long.valueOf(leftLong());
	}

	/**
	 * Sets the left element of this pair (optional operation).
	 *
	 * @param l a new value for the left element.
	 *
	 * @implSpec This implementation throws an {@link UnsupportedOperationException}.
	 */
	public default LongObjectPair<V> left(final long l) {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	public default LongObjectPair<V> left(final Long l) {
		return left((l).longValue());
	}

	/**
	 * Returns the left element of this pair.
	 *
	 * @return the left element of this pair.
	 *
	 * @implSpec This implementation delegates to {@link #left()}.
	 *
	 */
	public default long firstLong() {
		return leftLong();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	public default Long first() {
		return Long.valueOf(firstLong());
	}

	/**
	 * Sets the left element of this pair (optional operation).
	 *
	 * @param l a new value for the left element.
	 *
	 * @implSpec This implementation delegates to {@link #left(Object)}.
	 */
	public default LongObjectPair<V> first(final long l) {
		return left(l);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	public default LongObjectPair<V> first(final Long l) {
		return first((l).longValue());
	}

	/**
	 * Returns the left element of this pair.
	 *
	 * @return the left element of this pair.
	 *
	 * @implSpec This implementation delegates to {@link #left()}.
	 *
	 */
	public default long keyLong() {
		return firstLong();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	public default Long key() {
		return Long.valueOf(keyLong());
	}

	/**
	 * Sets the left element of this pair (optional operation).
	 *
	 * @param l a new value for the left element.
	 *
	 * @implSpec This implementation delegates to {@link #left(Object)}.
	 */
	public default LongObjectPair<V> key(final long l) {
		return left(l);
	}

	@Override
	@Deprecated
	public default LongObjectPair<V> key(Long l) {
		return key((l).longValue());
	}

	/**
	 * Returns a new type-specific immutable {@link it.unimi.dsi.fastutil.Pair Pair} with given left and
	 * right value.
	 * 
	 * @param left the left value.
	 * @param right the right value.
	 */
	public static <V> LongObjectPair<V> of(final long left, final V right) {
		return new LongObjectImmutablePair<V>(left, right);
	}

	/**
	 * Returns a lexicographical comparator for pairs.
	 *
	 * <p>
	 * The comparator returned by this method implements lexicographical order. It compares first the
	 * left elements: if the result of the comparison is nonzero, it returns said result. Otherwise,
	 * this comparator returns the result of the comparison of the right elements.
	 *
	 * @return a lexicographical comparator for pairs.
	 */
	@SuppressWarnings("unchecked")
	public static <V> java.util.Comparator<LongObjectPair<V>> lexComparator() {
		return (x, y) -> {
			final int t = Long.compare(x.leftLong(), y.leftLong());
			if (t != 0) return t;
			return ((Comparable<V>)x.right()).compareTo(y.right());
		};
	}
}
