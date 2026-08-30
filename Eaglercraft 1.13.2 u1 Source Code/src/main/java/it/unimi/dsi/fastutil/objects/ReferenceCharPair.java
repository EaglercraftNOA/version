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
public interface ReferenceCharPair<K> extends it.unimi.dsi.fastutil.Pair<K, Character> {
	/**
	 * Returns the right element of this pair.
	 *
	 * @return the right element of this pair.
	 */
	public char rightChar();

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	public default Character right() {
		return Character.valueOf(rightChar());
	}

	/**
	 * Sets the right element of this pair (optional operation).
	 *
	 * @param r a new value for the right element.
	 *
	 * @implSpec This implementation throws an {@link UnsupportedOperationException}.
	 */
	public default ReferenceCharPair<K> right(final char r) {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	public default ReferenceCharPair<K> right(final Character l) {
		return right((l).charValue());
	}

	/**
	 * Returns the right element of this pair.
	 *
	 * @return the right element of this pair.
	 *
	 * @implSpec This implementation delegates to {@link #right()}.
	 *
	 */
	public default char secondChar() {
		return rightChar();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	public default Character second() {
		return Character.valueOf(secondChar());
	}

	/**
	 * Sets the right element of this pair (optional operation).
	 *
	 * @param r a new value for the right element.
	 *
	 * @implSpec This implementation delegates to {@link #right(Object)}.
	 */
	public default ReferenceCharPair<K> second(final char r) {
		return right(r);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	public default ReferenceCharPair<K> second(final Character l) {
		return second((l).charValue());
	}

	/**
	 * Returns the right element of this pair.
	 *
	 * @return the right element of this pair.
	 *
	 * @implSpec This implementation delegates to {@link #right()}.
	 *
	 */
	public default char valueChar() {
		return rightChar();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	public default Character value() {
		return Character.valueOf(valueChar());
	}

	/**
	 * Sets the right element of this pair (optional operation).
	 *
	 * @param r a new value for the right element.
	 *
	 * @implSpec This implementation delegates to {@link #right(Object)}.
	 */
	public default ReferenceCharPair<K> value(final char r) {
		return right(r);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	public default ReferenceCharPair<K> value(final Character l) {
		return value((l).charValue());
	}

	/**
	 * Returns a new type-specific immutable {@link it.unimi.dsi.fastutil.Pair Pair} with given left and
	 * right value.
	 * 
	 * @param left the left value.
	 * @param right the right value.
	 */
	public static <K> ReferenceCharPair<K> of(final K left, final char right) {
		return new ReferenceCharImmutablePair<K>(left, right);
	}
}
