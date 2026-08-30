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

import java.util.function.BinaryOperator;

/**
 * A type-specific {@link BinaryOperator}; provides methods operating both on objects and on
 * primitives.
 *
 * @see BinaryOperator
 * @since 8.5.0
 */
@FunctionalInterface
public interface LongBinaryOperator extends BinaryOperator<Long>, java.util.function.LongBinaryOperator {
	/**
	 * Computes the operator on the given inputs.
	 *
	 * @param x the first input.
	 * @param y the second input.
	 * @return the output of the operator on the given inputs.
	 */
	long apply(long x, long y);

	/**
	 * {@inheritDoc}
	 *
	 * @implSpec This default implementation delegates to {@link #apply}.
	 * @deprecated Please use {@link #apply}.
	 */
	@Deprecated
	@Override
	default long applyAsLong(final long x, final long y) {
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
	default Long apply(final Long x, final Long y) {
		return apply(x.longValue(), y.longValue());
	}
}
