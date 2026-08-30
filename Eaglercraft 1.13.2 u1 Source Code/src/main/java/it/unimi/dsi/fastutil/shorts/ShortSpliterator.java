/*
	* Copyright (C) 2002-2024 Sebastiano Vigna
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
package it.unimi.dsi.fastutil.shorts;

import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * A type-specific {@link Spliterator}; provides an additional methods to avoid (un)boxing, and the
 * possibility to skip elements.
 *
 * @author C. Sean Young &lt;csyoung@google.com&gt;
 * @see Spliterator
 * @since 8.5.0
 */
public interface ShortSpliterator extends Spliterator.OfPrimitive<Short, ShortConsumer, ShortSpliterator> {
	// tryAdvance(KEY_CONSUMER action) declaration inherited from super interface.
	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	default boolean tryAdvance(final Consumer<? super Short> action) {
		// The instanceof and cast is required for performance. Without it, calls routed through this
		// overload using a primitive consumer would go through the slow lambda.
		return tryAdvance(action instanceof ShortConsumer ? (ShortConsumer)action : (ShortConsumer)action::accept);
	}

	// forEachRemaining(KEY_CONSUMER action) default impl inherited from super interface.
	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	default void forEachRemaining(final Consumer<? super Short> action) {
		// The instanceof and cast is required for performance. Without it, calls routed through this
		// overload using a primitive consumer would go through the slow lambda.
		// This is not just theoretical; Oracle's Stream implementation (Pipeline) routes primitive
		// consumer calls through this overload, and the difference in performance is an order
		// of magnitude.
		forEachRemaining(action instanceof ShortConsumer ? (ShortConsumer)action : (ShortConsumer)action::accept);
	}

	/**
	 * Skips the given number of elements.
	 *
	 * <p>
	 * The effect of this call is exactly the same as that of calling {@link #tryAdvance} for {@code n}
	 * times (possibly stopping if {@link #tryAdvance} returns false). The action called will do
	 * nothing; elements will be discarded.
	 *
	 * @implSpec This default implementation is linear in n. It is expected concrete implementations
	 *           that are capable of it will override it to run lower time, but be prepared for linear
	 *           time.
	 *
	 * @param n the number of elements to skip.
	 * @return the number of elements actually skipped.
	 * @see Spliterator#tryAdvance
	 */
	default long skip(final long n) {
		if (n < 0) throw new IllegalArgumentException("Argument must be nonnegative: " + n);
		long i = n;
		while (i-- != 0 && tryAdvance((short unused) -> {
		})) {
		} // No loop body; logic all happens in conditional
		return n - i - 1;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @apiNote Note that this specification strengthens the one given in
	 *          {@link Spliterator#trySplit()}.
	 */
	@Override
	ShortSpliterator trySplit();

	/**
	 * {@inheritDoc}
	 *
	 * @apiNote Note that this specification strengthens the one given in
	 *          {@link Spliterator#getComparator()}.
	 */
	@Override
	default ShortComparator getComparator() {
		throw new IllegalStateException();
	}
}
