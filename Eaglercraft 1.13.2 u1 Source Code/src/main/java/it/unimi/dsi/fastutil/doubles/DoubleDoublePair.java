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
package it.unimi.dsi.fastutil.doubles;

/**
 * A type-specific {@link it.unimi.dsi.fastutil.Pair Pair}; provides some additional methods that
 * use polymorphism to avoid (un)boxing.
 */
public interface DoubleDoublePair extends it.unimi.dsi.fastutil.Pair<Double, Double> {
	/**
	 * Returns the left element of this pair.
	 *
	 * @return the left element of this pair.
	 */
	public double leftDouble();

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	public default Double left() {
		return Double.valueOf(leftDouble());
	}

	/**
	 * Sets the left element of this pair (optional operation).
	 *
	 * @param l a new value for the left element.
	 *
	 * @implSpec This implementation throws an {@link UnsupportedOperationException}.
	 */
	public default DoubleDoublePair left(final double l) {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	public default DoubleDoublePair left(final Double l) {
		return left((l).doubleValue());
	}

	/**
	 * Returns the left element of this pair.
	 *
	 * @return the left element of this pair.
	 *
	 * @implSpec This implementation delegates to {@link #left()}.
	 *
	 */
	public default double firstDouble() {
		return leftDouble();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	public default Double first() {
		return Double.valueOf(firstDouble());
	}

	/**
	 * Sets the left element of this pair (optional operation).
	 *
	 * @param l a new value for the left element.
	 *
	 * @implSpec This implementation delegates to {@link #left(Object)}.
	 */
	public default DoubleDoublePair first(final double l) {
		return left(l);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	public default DoubleDoublePair first(final Double l) {
		return first((l).doubleValue());
	}

	/**
	 * Returns the left element of this pair.
	 *
	 * @return the left element of this pair.
	 *
	 * @implSpec This implementation delegates to {@link #left()}.
	 *
	 */
	public default double keyDouble() {
		return firstDouble();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	public default Double key() {
		return Double.valueOf(keyDouble());
	}

	/**
	 * Sets the left element of this pair (optional operation).
	 *
	 * @param l a new value for the left element.
	 *
	 * @implSpec This implementation delegates to {@link #left(Object)}.
	 */
	public default DoubleDoublePair key(final double l) {
		return left(l);
	}

	@Override
	@Deprecated
	public default DoubleDoublePair key(Double l) {
		return key((l).doubleValue());
	}

	/**
	 * Returns the right element of this pair.
	 *
	 * @return the right element of this pair.
	 */
	public double rightDouble();

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	public default Double right() {
		return Double.valueOf(rightDouble());
	}

	/**
	 * Sets the right element of this pair (optional operation).
	 *
	 * @param r a new value for the right element.
	 *
	 * @implSpec This implementation throws an {@link UnsupportedOperationException}.
	 */
	public default DoubleDoublePair right(final double r) {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	public default DoubleDoublePair right(final Double l) {
		return right((l).doubleValue());
	}

	/**
	 * Returns the right element of this pair.
	 *
	 * @return the right element of this pair.
	 *
	 * @implSpec This implementation delegates to {@link #right()}.
	 *
	 */
	public default double secondDouble() {
		return rightDouble();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	public default Double second() {
		return Double.valueOf(secondDouble());
	}

	/**
	 * Sets the right element of this pair (optional operation).
	 *
	 * @param r a new value for the right element.
	 *
	 * @implSpec This implementation delegates to {@link #right(Object)}.
	 */
	public default DoubleDoublePair second(final double r) {
		return right(r);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	public default DoubleDoublePair second(final Double l) {
		return second((l).doubleValue());
	}

	/**
	 * Returns the right element of this pair.
	 *
	 * @return the right element of this pair.
	 *
	 * @implSpec This implementation delegates to {@link #right()}.
	 *
	 */
	public default double valueDouble() {
		return rightDouble();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	public default Double value() {
		return Double.valueOf(valueDouble());
	}

	/**
	 * Sets the right element of this pair (optional operation).
	 *
	 * @param r a new value for the right element.
	 *
	 * @implSpec This implementation delegates to {@link #right(Object)}.
	 */
	public default DoubleDoublePair value(final double r) {
		return right(r);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	public default DoubleDoublePair value(final Double l) {
		return value((l).doubleValue());
	}

	/**
	 * Returns a new type-specific immutable {@link it.unimi.dsi.fastutil.Pair Pair} with given left and
	 * right value.
	 * 
	 * @param left the left value.
	 * @param right the right value.
	 */
	public static DoubleDoublePair of(final double left, final double right) {
		return new DoubleDoubleImmutablePair(left, right);
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

	public static java.util.Comparator<DoubleDoublePair> lexComparator() {
		return (x, y) -> {
			final int t = Double.compare(x.leftDouble(), y.leftDouble());
			if (t != 0) return t;
			return Double.compare(x.rightDouble(), y.rightDouble());
		};
	}
}
