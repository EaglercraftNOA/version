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
package it.unimi.dsi.fastutil.ints;

import java.util.Collection;
import static it.unimi.dsi.fastutil.Size64.sizeOf;

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
public interface IntCollection extends Collection<Integer>, IntIterable {
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
	IntIterator iterator();

	/**
	 * Returns a primitive iterator on the elements of this collection.
	 * <p>
	 *
	 * <p>
	 * This method is identical to {@link #iterator()}, as the type-specific iterator is already
	 * compatible with the JDK's primitive iterators. It only exists for compatibility with the other
	 * primitive types' {@code Collection}s that have use for widened iterators.
	 *
	 * @return a primitive iterator on the elements of this collection.
	 * @since 8.5.0
	 */
	@Override
	default IntIterator intIterator() {
		return iterator();
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
	default IntSpliterator spliterator() {
		return IntSpliterators.asSpliterator(iterator(), sizeOf(this), IntSpliterators.COLLECTION_SPLITERATOR_CHARACTERISTICS);
	}

	/**
	 * Returns a primitive spliterator on the elements of this collection.
	 * <p>
	 *
	 * <p>
	 * This method is identical to {@link #spliterator()}, as the type-specific spliterator is already
	 * compatible with the JDK's primitive spliterators. It only exists for compatibility with the other
	 * primitive types' {@code Collection}s that have use for widened spliterators.
	 *
	 * @return a primitive spliterator on the elements of this collection.
	 * @since 8.5.0
	 */
	@Override
	default IntSpliterator intSpliterator() {
		return spliterator();
	}

	/**
	 * Ensures that this collection contains the specified element (optional operation).
	 * 
	 * @see Collection#add(Object)
	 */
	boolean add(int key);

	/**
	 * Returns {@code true} if this collection contains the specified element.
	 * 
	 * @see Collection#contains(Object)
	 */
	boolean contains(int key);

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
	boolean rem(int key);

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	default boolean add(final Integer key) {
		return add((key).intValue());
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
		return contains(((Integer)(key)).intValue());
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
		return rem(((Integer)(key)).intValue());
	}

	/**
	 * Returns a primitive type array containing the items of this collection.
	 * 
	 * @return a primitive type array containing the items of this collection.
	 * @see Collection#toArray()
	 */
	int[] toIntArray();

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
	default int[] toIntArray(int a[]) {
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
	int[] toArray(int a[]);

	/**
	 * Adds all elements of the given type-specific collection to this collection.
	 *
	 * @param c a type-specific collection.
	 * @see Collection#addAll(Collection)
	 * @return {@code true} if this collection changed as a result of the call.
	 */
	boolean addAll(IntCollection c);

	/**
	 * Checks whether this collection contains all elements from the given type-specific collection.
	 *
	 * @param c a type-specific collection.
	 * @see Collection#containsAll(Collection)
	 * @return {@code true} if this collection contains all elements of the argument.
	 */
	boolean containsAll(IntCollection c);

	/**
	 * Remove from this collection all elements in the given type-specific collection.
	 *
	 * @param c a type-specific collection.
	 * @see Collection#removeAll(Collection)
	 * @return {@code true} if this collection changed as a result of the call.
	 */
	boolean removeAll(IntCollection c);

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	default boolean removeIf(final java.util.function.Predicate<? super Integer> filter) {
		return removeIf(filter instanceof java.util.function.IntPredicate ? ((java.util.function.IntPredicate)filter) : (java.util.function.IntPredicate)key -> filter.test(Integer.valueOf(key)));
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
	default boolean removeIf(final java.util.function.IntPredicate filter) {
		java.util.Objects.requireNonNull(filter);
		boolean removed = false;
		final IntIterator each = iterator();
		while (each.hasNext()) {
			if (filter.test(each.nextInt())) {
				each.remove();
				removed = true;
			}
		}
		return removed;
	}

	// Because our primitive Predicate interface extends both the JDK's primitive
	// and object Predicate interfaces, calling this method with it would be ambiguous.
	// This overload exists to pass it to the proper primitive overload.
	/**
	 * Remove from this collection all elements which satisfy the given predicate.
	 *
	 * <p>
	 * <b>WARNING</b>: Overriding this method is almost always a mistake, as this overload only exists
	 * to disambiguate. Instead, override the {@code removeIf()} overload that uses the JDK's primitive
	 * predicate type (e.g. {@link java.util.function.IntPredicate}).
	 *
	 * <p>
	 * If Java supported final default methods, this would be one, but sadly it does not.
	 *
	 * <p>
	 * If you checked and are overriding the version with {@code java.util.function.XPredicate}, and
	 * still see this warning, then your IDE is incorrectly conflating this method with the proper
	 * method to override, and you can safely ignore this message.
	 *
	 * @param filter a predicate which returns {@code true} for elements to be removed.
	 * @see Collection#removeIf(java.util.function.Predicate)
	 * @return {@code true} if any elements were removed.
	 */
	default boolean removeIf(final IntPredicate filter) {
		return removeIf((java.util.function.IntPredicate)filter);
	}

	/**
	 * Retains in this collection only elements from the given type-specific collection.
	 *
	 * @param c a type-specific collection.
	 * @see Collection#retainAll(Collection)
	 * @return {@code true} if this collection changed as a result of the call.
	 */
	boolean retainAll(IntCollection c);

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	default java.util.stream.Stream<Integer> stream() {
		return Collection.super.stream();
	}

	/**
	 * Return a primitive stream over the elements, performing widening casts if needed.
	 * 
	 * @return a primitive stream over the elements.
	 * @see Collection#stream()
	 * @see java.util.stream.IntStream
	 */
	default java.util.stream.IntStream intStream() {
		return java.util.stream.StreamSupport.intStream(intSpliterator(), false);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	default java.util.stream.Stream<Integer> parallelStream() {
		return Collection.super.parallelStream();
	}

	/**
	 * Return a parallel primitive stream over the elements, performing widening casts if needed.
	 * 
	 * @return a parallel primitive stream over the elements.
	 * @see Collection#parallelStream()
	 * @see java.util.stream.IntStream
	 */
	default java.util.stream.IntStream intParallelStream() {
		return java.util.stream.StreamSupport.intStream(intSpliterator(), true);
	}
}
