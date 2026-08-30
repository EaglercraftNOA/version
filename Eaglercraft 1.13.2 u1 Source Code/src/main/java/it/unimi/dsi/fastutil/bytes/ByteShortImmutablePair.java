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
package it.unimi.dsi.fastutil.bytes;

/**
 * A type-specific immutable {@link it.unimi.dsi.fastutil.Pair Pair}; provides some additional
 * methods that use polymorphism to avoid (un)boxing.
 */
public class ByteShortImmutablePair implements ByteShortPair, java.io.Serializable {
	private static final long serialVersionUID = 0L;
	protected final byte left;
	protected final short right;

	/**
	 * Creates a new type-specific immutable {@link it.unimi.dsi.fastutil.Pair Pair} with given left and
	 * right value.
	 * 
	 * @param left the left value.
	 * @param right the right value.
	 */
	public ByteShortImmutablePair(final byte left, final short right) {
		this.left = left;
		this.right = right;
	}

	/**
	 * Returns a new type-specific immutable {@link it.unimi.dsi.fastutil.Pair Pair} with given left and
	 * right value.
	 * 
	 * @param left the left value.
	 * @param right the right value.
	 *
	 * @implSpec This factory method delegates to the constructor.
	 */
	public static ByteShortImmutablePair of(final byte left, final short right) {
		return new ByteShortImmutablePair(left, right);
	}

	@Override
	public byte leftByte() {
		return left;
	}

	@Override
	public short rightShort() {
		return right;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public boolean equals(Object other) {
		if (other == null) return false;
		if (other instanceof ByteShortPair) {
			return left == ((ByteShortPair)other).leftByte() && right == ((ByteShortPair)other).rightShort();
		}
		if (other instanceof it.unimi.dsi.fastutil.Pair) {
			return java.util.Objects.equals(Byte.valueOf(left), ((it.unimi.dsi.fastutil.Pair)other).left()) && java.util.Objects.equals(Short.valueOf(right), ((it.unimi.dsi.fastutil.Pair)other).right());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return (left) * 19 + (right);
	}

	/**
	 * Returns a string representation of this pair in the form &lt;<var>l</var>,<var>r</var>&gt;.
	 *
	 * @return a string representation of this pair in the form &lt;<var>l</var>,<var>r</var>&gt;.
	 */
	@Override
	public String toString() {
		return "<" + leftByte() + "," + rightShort() + ">";
	}
}
