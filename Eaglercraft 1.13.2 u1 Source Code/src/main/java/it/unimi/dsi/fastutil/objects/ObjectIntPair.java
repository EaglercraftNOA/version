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
package it.unimi.dsi.fastutil.objects;

/**
 * A type-specific {@link it.unimi.dsi.fastutil.Pair Pair}; provides some additional methods that
 * use polymorphism to avoid (un)boxing.
 */
public interface ObjectIntPair<K> extends it.unimi.dsi.fastutil.Pair<K, Integer> {
	/**
	 * Returns the right element of this pair.
	 *
	 * @return the right element of this pair.
	 */
	public int rightInt();

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	public default Integer right() {
		return Integer.valueOf(rightInt());
	}

	/**
	 * Sets the right element of this pair (optional operation).
	 *
	 * @param r a new value for the right element.
	 *
	 * @implSpec This implementation throws an {@link UnsupportedOperationException}.
	 */
	public default ObjectIntPair<K> right(final int r) {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	public default ObjectIntPair<K> right(final Integer l) {
		return right((l).intValue());
	}

	/**
	 * Returns the right element of this pair.
	 *
	 * @return the right element of this pair.
	 *
	 * @implSpec This implementation delegates to {@link #right()}.
	 *
	 */
	public default int secondInt() {
		return rightInt();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	public default Integer second() {
		return Integer.valueOf(secondInt());
	}

	/**
	 * Sets the right element of this pair (optional operation).
	 *
	 * @param r a new value for the right element.
	 *
	 * @implSpec This implementation delegates to {@link #right(Object)}.
	 */
	public default ObjectIntPair<K> second(final int r) {
		return right(r);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	public default ObjectIntPair<K> second(final Integer l) {
		return second((l).intValue());
	}

	/**
	 * Returns the right element of this pair.
	 *
	 * @return the right element of this pair.
	 *
	 * @implSpec This implementation delegates to {@link #right()}.
	 *
	 */
	public default int valueInt() {
		return rightInt();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	public default Integer value() {
		return Integer.valueOf(valueInt());
	}

	/**
	 * Sets the right element of this pair (optional operation).
	 *
	 * @param r a new value for the right element.
	 *
	 * @implSpec This implementation delegates to {@link #right(Object)}.
	 */
	public default ObjectIntPair<K> value(final int r) {
		return right(r);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	public default ObjectIntPair<K> value(final Integer l) {
		return value((l).intValue());
	}

	/**
	 * Returns a new type-specific immutable {@link it.unimi.dsi.fastutil.Pair Pair} with given left and
	 * right value.
	 * 
	 * @param left the left value.
	 * @param right the right value.
	 */
	public static <K> ObjectIntPair<K> of(final K left, final int right) {
		return new ObjectIntImmutablePair<K>(left, right);
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
	public static <K> java.util.Comparator<ObjectIntPair<K>> lexComparator() {
		return (x, y) -> {
			final int t = ((Comparable<K>)x.left()).compareTo(y.left());
			if (t != 0) return t;
			return Integer.compare(x.rightInt(), y.rightInt());
		};
	}
}
