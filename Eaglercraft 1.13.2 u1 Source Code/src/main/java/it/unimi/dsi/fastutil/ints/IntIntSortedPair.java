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

/** A type-specific immutable {@link it.unimi.dsi.fastutil.SortedPair SortedPair}. */
public interface IntIntSortedPair extends IntIntPair, it.unimi.dsi.fastutil.SortedPair<Integer>, java.io.Serializable {
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
	 * @implSpec This factory method delegates to the factory method of the corresponding immutable
	 *           implementation.
	 */
	public static IntIntSortedPair of(final int left, final int right) {
		return IntIntImmutableSortedPair.of(left, right);
	}

	/**
	 * Returns true if one of the two elements of this sorted pair is equal to a given element.
	 *
	 * @param e an element.
	 * @return true if one of the two elements of this sorted pair is equal to {@code e}.
	 * @see it.unimi.dsi.fastutil.SortedPair#contains(Object)
	 */
	default boolean contains(final int e) {
		return e == leftInt() || e == rightInt();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	default boolean contains(final Object o) {
		if (o == null) return false;
		return contains(((Integer)(o)).intValue());
	}
}
