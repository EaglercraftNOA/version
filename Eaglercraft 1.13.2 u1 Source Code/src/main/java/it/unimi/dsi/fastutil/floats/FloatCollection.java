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

import java.util.Collection;
import static it.unimi.dsi.fastutil.Size64.sizeOf;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.doubles.DoubleSpliterator;

/**
 * A type-specific {@link Collection}; provides some additional methods that use polymorphism to
 * avoid (un)boxing.
 *
 * <p>
 * Additionally, this class defines strengthens (again) {@link #iterator()}.
 *
 * <p>
 * This interface specifies reference equality semantics (members will be compared equal with
 * {@code ==} instead of {@link Object#equals(Object) equals}), which may result in breaks in
 * contract if attempted to be used with non reference-equality semantics based {@link Collection}s.
 * For example, a {@code aReferenceCollection.equals(aObjectCollection)} may return different a
 * different result then {@code aObjectCollection.equals(aReferenceCollection)}, in violation of
 * {@link Object#equals equals}'s contract requiring it being symmetric.
 *
 * @see Collection
 */
public interface FloatCollection extends Collection<Float>, FloatIterable {
	/**
	 * Returns a type-specific iterator on the elements of this collection.
	 *
	 * @apiNote This specification strengthens the one given in {@link java.lang.Iterable#iterator()},
	 *          which was already strengthened in the corresponding type-specific class, but was
	 *          weakened by the fact that this interface extends {@link Collection}.
	 *
	 * @return a type-specific iterator on the elements of this collection.
	 */
	@Override
	FloatIterator iterator();

	/**
	 * Returns a widened primitive iterator on the elements of this collection.
	 * <p>
	 *
	 * <p>
	 * This method is provided for the purpose of APIs that expect only the JDK's primitive iterators,
	 * of which there are only {@code int}, {@code long}, and {@code double}.
	 *
	 * @return a widened primitive iterator on the elements of this collection.
	 * @since 8.5.0
	 */
	@Override
	default DoubleIterator doubleIterator() {
		return FloatIterable.super.doubleIterator();
	}

	// If you change these default spliterator methods, you will likely need to update Iterable, List,
	// Set, and SortedSet too
	/**
	 * Returns a type-specific spliterator on the elements of this collection.
	 *
	 * <p>
	 * See {@link java.util.Collection#spliterator()} for more documentation on the requirements of the
	 * returned spliterator.
	 *
	 * @apiNote This specification strengthens the one given in
	 *          {@link java.util.Collection#spliterator()}.
	 *          <p>
	 *          Also, this is generally the only {@code spliterator} method subclasses should override.
	 *
	 * @implSpec The default implementation returns a late-binding spliterator (see
	 *           {@link java.util.Spliterator Spliterator} for documentation on what binding policies
	 *           mean) that wraps this instance's type specific {@link #iterator}.
	 *           <p>
	 *           Additionally, it reports {@link java.util.Spliterator#SIZED Spliterator.SIZED}
	 *
	 * @implNote As this default implementation wraps the iterator, and {@link java.util.Iterator} is an
	 *           inherently linear API, the returned spliterator will yield limited performance gains
	 *           when run in parallel contexts, as the returned spliterator's
	 *           {@link java.util.Spliterator#trySplit() trySplit()} will have linear runtime.
	 *
	 * @return a type-specific spliterator on the elements of this collection.
	 * @since 8.5.0
	 */
	@Override
	default FloatSpliterator spliterator() {
		return FloatSpliterators.asSpliterator(iterator(), sizeOf(this), FloatSpliterators.COLLECTION_SPLITERATOR_CHARACTERISTICS);
	}

	/**
	 * Returns widened primitive spliterator on the elements of this collection.
	 * <p>
	 *
	 * <p>
	 * This method is provided for the purpose of APIs that expect only the JDK's primitive
	 * spliterators, of which there are only {@code int}, {@code long}, and {@code double}.
	 *
	 * @return a widened primitive spliterator on the elements of this collection.
	 * @since 8.5.0
	 */
	@Override
	default DoubleSpliterator doubleSpliterator() {
		return FloatIterable.super.doubleSpliterator();
	}

	/**
	 * Ensures that this collection contains the specified element (optional operation).
	 * 
	 * @see Collection#add(Object)
	 */
	boolean add(float key);

	/**
	 * Returns {@code true} if this collection contains the specified element.
	 * 
	 * @see Collection#contains(Object)
	 */
	boolean contains(float key);

	/**
	 * Removes a single instance of the specified element from this collection, if it is present
	 * (optional operation).
	 *
	 * <p>
	 * Note that this method should be called {@link java.util.Collection#remove(Object) remove()}, but
	 * the clash with the similarly named index-based method in the {@link java.util.List} interface
	 * forces us to use a distinguished name. For simplicity, the set interfaces reinstates
	 * {@code remove()}.
	 *
	 * @see Collection#remove(Object)
	 */
	boolean rem(float key);

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	default boolean add(final Float key) {
		return add((key).floatValue());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	default boolean contains(final Object key) {
		if (key == null) return false;
		return contains(((Float)(key)).floatValue());
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use (and implement) the {@code rem()} method instead.
	 */
	@Deprecated
	@Override
	default boolean remove(final Object key) {
		if (key == null) return false;
		return rem(((Float)(key)).floatValue());
	}

	/**
	 * Returns a primitive type array containing the items of this collection.
	 * 
	 * @return a primitive type array containing the items of this collection.
	 * @see Collection#toArray()
	 */
	float[] toFloatArray();

	/**
	 * Returns a primitive type array containing the items of this collection.
	 *
	 * <p>
	 * Note that, contrarily to {@link Collection#toArray(Object[])}, this methods just writes all
	 * elements of this collection: no special value will be added after the last one.
	 *
	 * @param a if this array is big enough, it will be used to store this collection.
	 * @return a primitive type array containing the items of this collection.
	 * @see Collection#toArray(Object[])
	 * @deprecated Please use {@code toArray()} instead&mdash;this method is redundant and will be
	 *             removed in the future.
	 */
	@Deprecated
	default float[] toFloatArray(float a[]) {
		return toArray(a);
	}

	/**
	 * Returns an array containing all of the elements in this collection; the runtime type of the
	 * returned array is that of the specified array.
	 *
	 * <p>
	 * Note that, contrarily to {@link Collection#toArray(Object[])}, this methods just writes all
	 * elements of this collection: no special value will be added after the last one.
	 *
	 * @param a if this array is big enough, it will be used to store this collection.
	 * @return a primitive type array containing the items of this collection.
	 * @see Collection#toArray(Object[])
	 */
	float[] toArray(float a[]);

	/**
	 * Adds all elements of the given type-specific collection to this collection.
	 *
	 * @param c a type-specific collection.
	 * @see Collection#addAll(Collection)
	 * @return {@code true} if this collection changed as a result of the call.
	 */
	boolean addAll(FloatCollection c);

	/**
	 * Checks whether this collection contains all elements from the given type-specific collection.
	 *
	 * @param c a type-specific collection.
	 * @see Collection#containsAll(Collection)
	 * @return {@code true} if this collection contains all elements of the argument.
	 */
	boolean containsAll(FloatCollection c);

	/**
	 * Remove from this collection all elements in the given type-specific collection.
	 *
	 * @param c a type-specific collection.
	 * @see Collection#removeAll(Collection)
	 * @return {@code true} if this collection changed as a result of the call.
	 */
	boolean removeAll(FloatCollection c);

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	default boolean removeIf(final java.util.function.Predicate<? super Float> filter) {
		return removeIf(filter instanceof FloatPredicate ? ((FloatPredicate)filter) : (FloatPredicate)key -> filter.test(Float.valueOf(it.unimi.dsi.fastutil.SafeMath.safeDoubleToFloat(key))));
	}

	/**
	 * Remove from this collection all elements which satisfy the given predicate.
	 *
	 * @param filter a predicate which returns {@code true} for elements to be removed.
	 * @see Collection#removeIf(java.util.function.Predicate)
	 * @return {@code true} if any elements were removed.
	 * @apiNote Implementing classes should generally override this method, and take the default
	 *          implementation of the other overloads which will delegate to this method (after proper
	 *          conversions).
	 */
	default boolean removeIf(final FloatPredicate filter) {
		java.util.Objects.requireNonNull(filter);
		boolean removed = false;
		final FloatIterator each = iterator();
		while (each.hasNext()) {
			if (filter.test(each.nextFloat())) {
				each.remove();
				removed = true;
			}
		}
		return removed;
	}

	/**
	 * Remove from this collection all elements which satisfy the given predicate.
	 *
	 * @param filter a predicate which returns {@code true} for elements to be removed.
	 * @see Collection#removeIf(java.util.function.Predicate)
	 * @return {@code true} if any elements were removed.
	 * @implNote Unless the argument is type-specific, this method will introduce an intermediary lambda
	 *           to perform widening casts. Please use the type-specific overload to avoid this
	 *           overhead.
	 */
	@SuppressWarnings("overloads")
	default boolean removeIf(final java.util.function.DoublePredicate filter) {
		return removeIf(filter instanceof FloatPredicate ? (FloatPredicate)filter : (FloatPredicate)filter::test);
	}

	/**
	 * Retains in this collection only elements from the given type-specific collection.
	 *
	 * @param c a type-specific collection.
	 * @see Collection#retainAll(Collection)
	 * @return {@code true} if this collection changed as a result of the call.
	 */
	boolean retainAll(FloatCollection c);

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	default java.util.stream.Stream<Float> stream() {
		return Collection.super.stream();
	}

	/**
	 * Return a primitive stream over the elements, performing widening casts if needed.
	 * 
	 * @return a primitive stream over the elements.
	 * @see Collection#stream()
	 * @see java.util.stream.IntStream
	 */
	default java.util.stream.DoubleStream doubleStream() {
		return java.util.stream.StreamSupport.doubleStream(doubleSpliterator(), false);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	default java.util.stream.Stream<Float> parallelStream() {
		return Collection.super.parallelStream();
	}

	/**
	 * Return a parallel primitive stream over the elements, performing widening casts if needed.
	 * 
	 * @return a parallel primitive stream over the elements.
	 * @see Collection#parallelStream()
	 * @see java.util.stream.IntStream
	 */
	default java.util.stream.DoubleStream doubleParallelStream() {
		return java.util.stream.StreamSupport.doubleStream(doubleSpliterator(), true);
	}
}
