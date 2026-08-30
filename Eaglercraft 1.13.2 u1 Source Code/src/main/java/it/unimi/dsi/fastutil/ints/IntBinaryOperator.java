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

import java.util.function.BinaryOperator;

/**
 * A type-specific {@link BinaryOperator}; provides methods operating both on objects and on
 * primitives.
 *
 * @see BinaryOperator
 * @since 8.5.0
 */
@FunctionalInterface
public interface IntBinaryOperator extends BinaryOperator<Integer>, java.util.function.IntBinaryOperator {
	/**
	 * Computes the operator on the given inputs.
	 *
	 * @param x the first input.
	 * @param y the second input.
	 * @return the output of the operator on the given inputs.
	 */
	int apply(int x, int y);

	/**
	 * {@inheritDoc}
	 *
	 * @implSpec This default implementation delegates to {@link #apply}.
	 * @deprecated Please use {@link #apply}.
	 */
	@Deprecated
	@Override
	default int applyAsInt(final int x, final int y) {
		return apply(x, y);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	@SuppressWarnings("boxing")
	default Integer apply(final Integer x, final Integer y) {
		return apply(x.intValue(), y.intValue());
	}
}
