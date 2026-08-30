/*
	* Copyright (C) 2017-2024 Sebastiano Vigna
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

import java.util.Objects;
import java.util.function.Consumer;

/**
 * A type-specific {@link Consumer}; provides methods to consume a primitive type both as object and
 * as primitive.
 *
 * <p>
 * Except for the boolean case, this interface extends both a parameterized
 * {@link java.util.function.Consumer} and a type-specific JDK consumer (e.g.,
 * {@link java.util.function.IntConsumer}). For types missing a type-specific JDK consumer (e.g.,
 * {@code short} or {@code float}), we extend the consumer associated with the smallest primitive
 * type that can represent the current type (e.g., {@code int} or {@code double}, respectively).
 *
 * @see Consumer
 * @since 8.0.0
 */
@FunctionalInterface
public interface BooleanConsumer extends Consumer<Boolean> {
	/**
	 * Performs this operation on the given input.
	 *
	 * @param t the input.
	 */
	void accept(boolean t);

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	default void accept(final Boolean t) {
		this.accept(t.booleanValue());
	}

	/**
	 * Returns a composed type-specific consumer that performs, in sequence, this operation followed by
	 * the {@code after} operation.
	 * 
	 * @param after the operation to perform after this operation.
	 * @return a composed {@code Consumer} that performs in sequence this operation followed by the
	 *         {@code after} operation.
	 * @see Consumer#andThen
	 * @apiNote Implementing classes should generally override this method and keep the default
	 *          implementation of the other overloads, which will delegate to this method (after proper
	 *          conversions).
	 */
	default BooleanConsumer andThen(final BooleanConsumer after) {
		Objects.requireNonNull(after);
		return t -> {
			accept(t);
			after.accept(t);
		};
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	default Consumer<Boolean> andThen(final Consumer<? super Boolean> after) {
		return Consumer.super.andThen(after);
	}
}
