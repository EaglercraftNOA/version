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

import java.util.function.UnaryOperator;

/**
 * A type-specific {@link UnaryOperator}; provides methods operating both on objects and on
 * primitives.
 *
 * @see UnaryOperator
 * @since 8.5.0
 */
@FunctionalInterface
public interface DoubleUnaryOperator extends UnaryOperator<Double>, java.util.function.DoubleUnaryOperator {
	/**
	 * Computes the operator on the given input.
	 *
	 * @param x the input.
	 * @return the output of the operator on the given input.
	 */
	double apply(double x);

	/**
	 * Returns a {@code UnaryOperator} that always returns the input unmodified.
	 * 
	 * @see java.util.function.UnaryOperator#identity()
	 */
	public static DoubleUnaryOperator identity() {
		// Java is smart enough to see this lambda is stateless and will return the same instance every
		// time.
		return i -> i;
	}

	/**
	 * Returns a {@code UnaryOperator} that always returns the arithmetic negation of the input.
	 * 
	 * @implNote As with all negation, be wary of unexpected behavior near the minimum value of the data
	 *           type. For example, -{@link Integer#MIN_VALUE} will result in {@link Integer#MIN_VALUE}
	 *           (still negative), as the positive value of {@link Integer#MIN_VALUE} is too big for
	 *           {@code int} (it would be 1 greater then {@link Integer#MAX_VALUE}).
	 */
	public static DoubleUnaryOperator negation() {
		return i -> -i;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @implSpec This default implementation delegates to {@link #apply}.
	 * @deprecated Please use {@link #apply}.
	 */
	@Deprecated
	@Override
	default double applyAsDouble(final double x) {
		return apply(x);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	@SuppressWarnings("boxing")
	default Double apply(final Double x) {
		return apply(x.doubleValue());
	}
}
