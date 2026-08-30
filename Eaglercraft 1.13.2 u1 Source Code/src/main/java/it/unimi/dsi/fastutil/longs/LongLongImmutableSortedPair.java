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
 * A type-specific immutable {@link it.unimi.dsi.fastutil.SortedPair SortedPair}; provides some
 * additional methods that use polymorphism to avoid (un)boxing.
 */
public class LongLongImmutableSortedPair extends LongLongImmutablePair implements LongLongSortedPair, java.io.Serializable {
	private static final long serialVersionUID = 0L;

	private LongLongImmutableSortedPair(final long left, final long right) {
		super(left, right);
	}

	/**
	 * Returns a new type-specific immutable {@link it.unimi.dsi.fastutil.SortedPair SortedPair} with
	 * given left and right value.
	 *
	 * <p>
	 * Note that if {@code left} and {@code right} are in the wrong order, they will be exchanged.
	 *
	 * @param left the left value.
	 * @param right the right value.
	 *
	 * @implSpec This factory method delegates to the constructor.
	 */
	public static LongLongImmutableSortedPair of(final long left, final long right) {
		if (left <= right) return new LongLongImmutableSortedPair(left, right);
		else return new LongLongImmutableSortedPair(right, left);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public boolean equals(Object other) {
		if (other == null) return false;
		if (other instanceof LongLongSortedPair) {
			return left == ((LongLongSortedPair)other).leftLong() && right == ((LongLongSortedPair)other).rightLong();
		}
		if (other instanceof it.unimi.dsi.fastutil.SortedPair) {
			return java.util.Objects.equals(Long.valueOf(left), ((it.unimi.dsi.fastutil.SortedPair)other).left()) && java.util.Objects.equals(Long.valueOf(right), ((it.unimi.dsi.fastutil.SortedPair)other).right());
		}
		return false;
	}

	/**
	 * Returns a string representation of this sorted pair in the form {<var>l</var>,<var>r</var>}.
	 *
	 * @return a string representation of this pair sorted in the form {<var>l</var>,<var>r</var>}.
	 */
	@Override
	public String toString() {
		return "{" + leftLong() + "," + rightLong() + "}";
	}
}
