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
package it.unimi.dsi.fastutil.ints;

/**
 * A type-specific mutable {@link it.unimi.dsi.fastutil.Pair Pair}; provides some additional methods
 * that use polymorphism to avoid (un)boxing.
 */
public class IntFloatMutablePair implements IntFloatPair, java.io.Serializable {
	private static final long serialVersionUID = 0L;
	protected int left;
	protected float right;

	/**
	 * Creates a new type-specific mutable {@link it.unimi.dsi.fastutil.Pair Pair} with given left and
	 * right value.
	 * 
	 * @param left the left value.
	 * @param right the right value.
	 */
	public IntFloatMutablePair(final int left, final float right) {
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
	public static IntFloatMutablePair of(final int left, final float right) {
		return new IntFloatMutablePair(left, right);
	}

	@Override
	public int leftInt() {
		return left;
	}

	@Override
	public IntFloatMutablePair left(final int l) {
		left = l;
		return this;
	}

	@Override
	public float rightFloat() {
		return right;
	}

	@Override
	public IntFloatMutablePair right(final float r) {
		right = r;
		return this;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public boolean equals(Object other) {
		if (other == null) return false;
		if (other instanceof IntFloatPair) {
			return left == ((IntFloatPair)other).leftInt() && right == ((IntFloatPair)other).rightFloat();
		}
		if (other instanceof it.unimi.dsi.fastutil.Pair) {
			return java.util.Objects.equals(Integer.valueOf(left), ((it.unimi.dsi.fastutil.Pair)other).left()) && java.util.Objects.equals(Float.valueOf(right), ((it.unimi.dsi.fastutil.Pair)other).right());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return (left) * 19 + it.unimi.dsi.fastutil.HashCommon.float2int(right);
	}

	/**
	 * Returns a string representation of this pair in the form &lt;<var>l</var>,<var>r</var>&gt;.
	 *
	 * @return a string representation of this pair in the form &lt;<var>l</var>,<var>r</var>&gt;.
	 */
	@Override
	public String toString() {
		return "<" + leftInt() + "," + rightFloat() + ">";
	}
}
