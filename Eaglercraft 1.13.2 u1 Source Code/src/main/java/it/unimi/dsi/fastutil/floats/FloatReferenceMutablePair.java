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
package it.unimi.dsi.fastutil.floats;

/**
 * A type-specific mutable {@link it.unimi.dsi.fastutil.Pair Pair}; provides some additional methods
 * that use polymorphism to avoid (un)boxing.
 */
public class FloatReferenceMutablePair<V> implements FloatReferencePair<V>, java.io.Serializable {
	private static final long serialVersionUID = 0L;
	protected float left;
	protected V right;

	/**
	 * Creates a new type-specific mutable {@link it.unimi.dsi.fastutil.Pair Pair} with given left and
	 * right value.
	 * 
	 * @param left the left value.
	 * @param right the right value.
	 */
	public FloatReferenceMutablePair(final float left, final V right) {
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
	public static <V> FloatReferenceMutablePair<V> of(final float left, final V right) {
		return new FloatReferenceMutablePair<V>(left, right);
	}

	@Override
	public float leftFloat() {
		return left;
	}

	@Override
	public FloatReferenceMutablePair<V> left(final float l) {
		left = l;
		return this;
	}

	@Override
	public V right() {
		return right;
	}

	@Override
	public FloatReferenceMutablePair<V> right(final V r) {
		right = r;
		return this;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public boolean equals(Object other) {
		if (other == null) return false;
		if (other instanceof FloatReferencePair) {
			return left == ((FloatReferencePair)other).leftFloat() && right == ((FloatReferencePair)other).right();
		}
		if (other instanceof it.unimi.dsi.fastutil.Pair) {
			return java.util.Objects.equals(Float.valueOf(left), ((it.unimi.dsi.fastutil.Pair)other).left()) && right == ((it.unimi.dsi.fastutil.Pair)other).right();
		}
		return false;
	}

	@Override
	public int hashCode() {
		return it.unimi.dsi.fastutil.HashCommon.float2int(left) * 19 + ((right) == null ? 0 : System.identityHashCode(right));
	}

	/**
	 * Returns a string representation of this pair in the form &lt;<var>l</var>,<var>r</var>&gt;.
	 *
	 * @return a string representation of this pair in the form &lt;<var>l</var>,<var>r</var>&gt;.
	 */
	@Override
	public String toString() {
		return "<" + leftFloat() + "," + right() + ">";
	}
}
