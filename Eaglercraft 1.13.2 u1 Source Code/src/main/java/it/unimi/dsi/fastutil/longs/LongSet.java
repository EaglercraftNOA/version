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
package it.unimi.dsi.fastutil.longs;

import java.util.Spliterator;
import java.util.Set;
import static it.unimi.dsi.fastutil.Size64.sizeOf;

/**
 * A type-specific {@link Set}; provides some additional methods that use polymorphism to avoid
 * (un)boxing.
 *
 * <p>
 * Additionally, this interface strengthens (again) {@link #iterator()}.
 *
 * @see Set
 */
public interface LongSet extends LongCollection, Set<Long> {
	/**
	 * Returns a type-specific iterator on the elements of this set.
	 *
	 * @apiNote This specification strengthens the one given in {@link java.lang.Iterable#iterator()},
	 *          which was already strengthened in the corresponding type-specific class, but was
	 *          weakened by the fact that this interface extends {@link Set}.
	 *          <p>
	 *          Also, this is generally the only {@code iterator} method subclasses should override.
	 *
	 * @return a type-specific iterator on the elements of this set.
	 */
	@Override
	LongIterator iterator();

	/**
	 * Returns a type-specific spliterator on the elements of this set.
	 *
	 * <p>
	 * Set spliterators must report at least {@link Spliterator#DISTINCT}.
	 *
	 * <p>
	 * See {@link java.util.Set#spliterator()} for more documentation on the requirements of the
	 * returned spliterator.
	 *
	 * @apiNote This specification strengthens the one given in
	 *          {@link java.util.Collection#spliterator()}, which was already strengthened in the
	 *          corresponding type-specific class, but was weakened by the fact that this interface
	 *          extends {@link Set}.
	 *          <p>
	 *          Also, this is generally the only {@code spliterator} method subclasses should override.
	 *
	 * @implSpec The default implementation returns a late-binding spliterator (see {@link Spliterator}
	 *           for documentation on what binding policies mean) that wraps this instance's type
	 *           specific {@link #iterator}.
	 *           <p>
	 *           Additionally, it reports {@link Spliterator#SIZED} and {@link Spliterator#DISTINCT}.
	 *
	 * @implNote As this default implementation wraps the iterator, and {@link java.util.Iterator} is an
	 *           inherently linear API, the returned spliterator will yield limited performance gains
	 *           when run in parallel contexts, as the returned spliterator's
	 *           {@link Spliterator#trySplit() trySplit()} will have linear runtime.
	 *
	 * @return {@inheritDoc}
	 * @since 8.5.0
	 */
	@Override
	default LongSpliterator spliterator() {
		return LongSpliterators.asSpliterator(iterator(), sizeOf(this), LongSpliterators.SET_SPLITERATOR_CHARACTERISTICS);
	}

	/**
	 * Removes an element from this set.
	 *
	 * @apiNote Note that the corresponding method of a type-specific collection is {@code rem()}. This
	 *          unfortunate situation is caused by the clash with the similarly named index-based method
	 *          in the {@link java.util.List} interface.
	 *
	 * @see java.util.Collection#remove(Object)
	 */
	boolean remove(long k);

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@SuppressWarnings("deprecation")
	@Deprecated
	@Override
	default boolean remove(final Object o) {
		return LongCollection.super.remove(o);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@SuppressWarnings("deprecation")
	@Deprecated
	@Override
	default boolean add(final Long o) {
		return LongCollection.super.add(o);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@SuppressWarnings("deprecation")
	@Deprecated
	@Override
	default boolean contains(final Object o) {
		return LongCollection.super.contains(o);
	}

	/**
	 * Removes an element from this set.
	 *
	 * <p>
	 * This method is inherited from the type-specific collection this type-specific set is based on,
	 * but it should not used as this interface reinstates {@code remove()} as removal method.
	 *
	 * @deprecated Please use {@code remove()} instead.
	 */
	@Deprecated
	@Override
	default boolean rem(long k) {
		return remove(k);
	}

	/**
	 * Returns an immutable empty set.
	 *
	 * @return an immutable empty set.
	 */

	public static LongSet of() {
		return LongSets.UNMODIFIABLE_EMPTY_SET;
	}

	/**
	 * Returns an immutable set with the element given.
	 *
	 * @param e an element.
	 * @return an immutable set containing {@code e}.
	 */
	public static LongSet of(long e) {
		return LongSets.singleton(e);
	}

	/**
	 * Returns an immutable set with the elements given.
	 *
	 * @param e0 the first element.
	 * @param e1 the second element.
	 * @return an immutable set containing {@code e0} and {@code e1}.
	 * @throws IllegalArgumentException if there were duplicate entries.
	 */
	public static LongSet of(long e0, long e1) {
		LongArraySet innerSet = new LongArraySet(2);
		innerSet.add(e0);
		if (!innerSet.add(e1)) {
			throw new IllegalArgumentException("Duplicate element: " + e1);
		}
		return LongSets.unmodifiable(innerSet);
	}

	/**
	 * Returns an immutable set with the elements given.
	 *
	 * @param e0 the first element.
	 * @param e1 the second element.
	 * @param e2 the third element.
	 * @return an immutable set containing {@code e0}, {@code e1}, and {@code e2}.
	 * @throws IllegalArgumentException if there were duplicate entries.
	 */
	public static LongSet of(long e0, long e1, long e2) {
		LongArraySet innerSet = new LongArraySet(3);
		innerSet.add(e0);
		if (!innerSet.add(e1)) {
			throw new IllegalArgumentException("Duplicate element: " + e1);
		}
		if (!innerSet.add(e2)) {
			throw new IllegalArgumentException("Duplicate element: " + e2);
		}
		return LongSets.unmodifiable(innerSet);
	}

	/**
	 * Returns an immutable list with the elements given.
	 *
	 * @param a the list of elements that will be in the final set.
	 * @return an immutable set containing the elements in {@code a}.
	 * @throws IllegalArgumentException if there are any duplicate entries.
	 */

	public static LongSet of(long... a) {
		switch (a.length) {
		case 0:
			return of();
		case 1:
			return of(a[0]);
		case 2:
			return of(a[0], a[1]);
		case 3:
			return of(a[0], a[1], a[2]);
		default:
			// fall through
		}
		// Will copy, but that is the only way we assure immutability.
		LongSet innerSet = a.length <= LongSets.ARRAY_SET_CUTOFF ? new LongArraySet(a.length) : new LongOpenHashSet(a.length);
		for (long element : a) {
			if (!innerSet.add(element)) {
				throw new IllegalArgumentException("Duplicate element: " + element);
			}
		}
		return LongSets.unmodifiable(innerSet);
	}
}
