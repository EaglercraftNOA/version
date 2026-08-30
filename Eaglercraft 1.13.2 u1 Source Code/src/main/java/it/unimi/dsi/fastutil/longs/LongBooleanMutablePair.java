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
 * A type-specific mutable {@link it.unimi.dsi.fastutil.Pair Pair}; provides some additional methods
 * that use polymorphism to avoid (un)boxing.
 */
public class LongBooleanMutablePair implements LongBooleanPair, java.io.Serializable {
	private static final long serialVersionUID = 0L;
	protected long left;
	protected boolean right;

	/**
	 * Creates a new type-specific mutable {@link it.unimi.dsi.fastutil.Pair Pair} with given left and
	 * right value.
	 * 
	 * @param left the left value.
	 * @param right the right value.
	 */
	public LongBooleanMutablePair(final long left, final boolean right) {
		this.left = left;
		this.right = right;
	}

	/**
	 * Returns a new type-specific mutable {@link it.unimi.dsi.fastutil.Pair Pair} with given left and
	 * right value.
	 * 
	 * @param left the left value.
	 * @param right the right value.
	 *
	 * @implSpec This factory method delegates to the constructor.
	 */
	public static LongBooleanMutablePair of(final long left, final boolean right) {
		return new LongBooleanMutablePair(left, right);
	}

	@Override
	public long leftLong() {
		return left;
	}

	@Override
	public LongBooleanMutablePair left(final long l) {
		left = l;
		return this;
	}

	@Override
	public boolean rightBoolean() {
		return right;
	}

	@Override
	public LongBooleanMutablePair right(final boolean r) {
		right = r;
		return this;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public boolean equals(Object other) {
		if (other == null) return false;
		if (other instanceof LongBooleanPair) {
			return left == ((LongBooleanPair)other).leftLong() && right == ((LongBooleanPair)other).rightBoolean();
		}
		if (other instanceof it.unimi.dsi.fastutil.Pair) {
			return java.util.Objects.equals(Long.valueOf(left), ((it.unimi.dsi.fastutil.Pair)other).left()) && java.util.Objects.equals(Boolean.valueOf(right), ((it.unimi.dsi.fastutil.Pair)other).right());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return it.unimi.dsi.fastutil.HashCommon.long2int(left) * 19 + (right ? 1231 : 1237);
	}

	/**
	 * Returns a string representation of this pair in the form &lt;<var>l</var>,<var>r</var>&gt;.
	 *
	 * @return a string representation of this pair in the form &lt;<var>l</var>,<var>r</var>&gt;.
	 */
	@Override
	public String toString() {
		return "<" + leftLong() + "," + rightBoolean() + ">";
	}
}
