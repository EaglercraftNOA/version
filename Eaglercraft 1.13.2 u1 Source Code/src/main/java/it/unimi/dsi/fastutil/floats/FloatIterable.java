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
package it.unimi.dsi.fastutil.floats;

import java.lang.Iterable;
import java.util.Objects;
import java.util.function.Consumer;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.doubles.DoubleIterators;
import it.unimi.dsi.fastutil.doubles.DoubleSpliterator;
import it.unimi.dsi.fastutil.doubles.DoubleSpliterators;

/**
 * A type-specific {@link Iterable} that strengthens that specification of {@link #iterator()} and
 * {@link #forEach(Consumer)}.
 *
 * <p>
 * Note that whenever there exist a primitive consumer in {@link java.util.function} (e.g.,
 * {@link java.util.function.IntConsumer}), trying to access any version of
 * {@link #forEach(Consumer)} using a lambda expression with untyped arguments will generate an
 * ambiguous method error. This can be easily solved by specifying the type of the argument, as in
 * 
 * <pre>
*    intIterable.forEach((int x) -&gt; { // Do something with x });
 * </pre>
 * <p>
 * The same problem plagues, for example,
 * {@link java.util.PrimitiveIterator.OfInt#forEachRemaining(java.util.function.IntConsumer)}.
 *
 * <p>
 * <strong>Warning</strong>: Java will let you write &ldquo;colon&rdquo; {@code for} statements with
 * primitive-type loop variables; however, what is (unfortunately) really happening is that at each
 * iteration an unboxing (and, in the case of {@code fastutil} type-specific data structures, a
 * boxing) will be performed. Watch out.
 *
 * @see Iterable
 */
public interface FloatIterable extends Iterable<Float> {
	/**
	 * Returns a type-specific iterator.
	 *
	 * @apiNote Note that this specification strengthens the one given in {@link Iterable#iterator()}.
	 *
	 * @return a type-specific iterator.
	 * @see Iterable#iterator()
	 */
	@Override
	FloatIterator iterator();

	/**
	 * Returns a widened primitive iterator on the elements of this iterable.
	 * <p>
	 *
	 * This method is provided for the purpose of APIs that expect only the JDK's primitive iterators,
	 * of which there are only {@code int}, {@code long}, and {@code double}.
	 *
	 * @return a widened primitive iterator on the elements of this iterable.
	 * @since 8.5.0
	 */
	default DoubleIterator doubleIterator() {
		return DoubleIterators.wrap(iterator());
	}

	// If you change these default spliterator methods, you will likely need to update Collection, List,
	// Set, and SortedSet too.
	/**
	 * Returns a type-specific spliterator on the elements of this iterable.
	 *
	 * @apiNote Note that this specification strengthens the one given in
	 *          {@link java.lang.Iterable#spliterator()}.
	 *
	 * @return a type-specific spliterator on the elements of this iterable.
	 * @since 8.5.0
	 */
	@Override
	default FloatSpliterator spliterator() {
		return FloatSpliterators.asSpliteratorUnknownSize(iterator(), 0);
	}

	/**
	 * Returns widened primitive spliterator on the elements of this iterable.
	 * <p>
	 *
	 * This method is provided for the purpose of APIs that expect only the JDK's primitive
	 * spliterators, of which there are only {@code int}, {@code long}, and {@code double}.
	 *
	 * @implSpec The default implementation widens the spliterator from {@link #spliterator()}.
	 * @return a widened primitive spliterator on the elements of this iterable.
	 * @since 8.5.0
	 */
	default DoubleSpliterator doubleSpliterator() {
		return DoubleSpliterators.wrap(spliterator());
	}

	/**
	 * Performs the given action for each element of this type-specific {@link java.lang.Iterable} until
	 * all elements have been processed or the action throws an exception.
	 *
	 * @param action the action to be performed for each element.
	 * @see java.lang.Iterable#forEach(java.util.function.Consumer)
	 * @since 8.0.0
	 * @apiNote Implementing classes should generally override this method, and take the default
	 *          implementation of the other overloads which will delegate to this method (after proper
	 *          conversions).
	 */
	default void forEach(final FloatConsumer action) {
		Objects.requireNonNull(action);
		iterator().forEachRemaining(action);
	}

	/**
	 * Performs the given action for each element of this type-specific {@link java.lang.Iterable},
	 * performing widening primitive casts, until all elements have been processed or the action throws
	 * an exception.
	 *
	 * @param action the action to be performed for each element.
	 * @see java.lang.Iterable#forEach(java.util.function.Consumer)
	 * @since 8.0.0
	 * @implNote Unless the argument is type-specific, this method will introduce an intermediary lambda
	 *           to perform widening casts. Please use the type-specific overload to avoid this
	 *           overhead.
	 */
	default void forEach(final java.util.function.DoubleConsumer action) {
		Objects.requireNonNull(action);
		forEach(action instanceof FloatConsumer ? (FloatConsumer)action : (FloatConsumer)action::accept);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	default void forEach(final Consumer<? super Float> action) {
		Objects.requireNonNull(action);
		// The instanceof and cast is required for performance. Without it, calls routed through this
		// overload using a primitive consumer would go through the slow lambda.
		forEach(action instanceof FloatConsumer ? (FloatConsumer)action : (FloatConsumer)action::accept);
	}
}
