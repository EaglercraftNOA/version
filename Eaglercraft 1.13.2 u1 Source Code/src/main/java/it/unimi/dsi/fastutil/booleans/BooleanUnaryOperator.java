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
package it.unimi.dsi.fastutil.booleans;

import java.util.function.UnaryOperator;

/**
 * A type-specific {@link UnaryOperator}; provides methods operating both on objects and on
 * primitives.
 *
 * @see UnaryOperator
 * @since 8.5.0
 */
@FunctionalInterface
public interface BooleanUnaryOperator extends UnaryOperator<Boolean> {
	/**
	 * Computes the operator on the given input.
	 *
	 * @param x the input.
	 * @return the output of the operator on the given input.
	 */
	boolean apply(boolean x);

	/**
	 * Returns a {@code UnaryOperator} that always returns the input unmodified.
	 * 
	 * @see java.util.function.UnaryOperator#identity()
	 */
	public static BooleanUnaryOperator identity() {
		// Java is smart enough to see this lambda is stateless and will return the same instance every
		// time.
		return i -> i;
	}

	/** Returns a {@code UnaryOperator} that always returns the logical negation of the input. */
	public static BooleanUnaryOperator negation() {
		return i -> !i;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @implSpec This default implementation delegates to {@link #apply} after narrowing down the
	 *           argument to the actual key type, throwing an exception if the argument cannot be
	 *           represented in the restricted domain. This is done for interoperability with the Java 8
	 *           function environment. The use of this method discouraged, as unexpected errors can
	 *           occur.
	 *
	 * @throws IllegalArgumentException If the given operands are not an element of the key domain.
	 * @since 8.5.0
	 * @deprecated Please use {@link #apply}.
	 */
	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	@SuppressWarnings("boxing")
	default Boolean apply(final Boolean x) {
		return apply(x.booleanValue());
	}
}
