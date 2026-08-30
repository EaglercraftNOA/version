/*
	* Copyright (C) 2010-2024 Sebastiano Vigna
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
package it.unimi.dsi.fastutil.objects;

import java.util.List;
import java.util.Spliterator;
import static it.unimi.dsi.fastutil.BigArrays.length;
import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.BigList;

/**
 * A type-specific {@link BigList}; provides some additional methods that use polymorphism to avoid
 * (un)boxing.
 *
 * <p>
 * Additionally, this interface strengthens {@link #iterator()}, {@link #listIterator()},
 * {@link #listIterator(long)} and {@link #subList(long,long)}.
 *
 * <p>
 * Besides polymorphic methods, this interfaces specifies methods to copy into an array or remove
 * contiguous sublists. Although the abstract implementation of this interface provides simple,
 * one-by-one implementations of these methods, it is expected that concrete implementation override
 * them with optimized versions.
 *
 * @see List
 */
public interface ObjectBigList<K> extends BigList<K>, ObjectCollection<K>, Comparable<BigList<? extends K>> {
	/**
	 * Returns a type-specific iterator on the elements of this list.
	 *
	 * @apiNote This specification strengthens the one given in {@link java.util.Collection#iterator()}.
	 * @see java.util.Collection#iterator()
	 */
	@Override
	ObjectBigListIterator<K> iterator();

	/**
	 * Returns a type-specific big-list iterator on this type-specific big list.
	 *
	 * @apiNote This specification strengthens the one given in {@link BigList#listIterator()}.
	 * @see BigList#listIterator()
	 */
	@Override
	ObjectBigListIterator<K> listIterator();

	/**
	 * Returns a type-specific list iterator on this type-specific big list starting at a given index.
	 *
	 * @apiNote This specification strengthens the one given in {@link BigList#listIterator(long)}.
	 * @see BigList#listIterator(long)
	 */
	@Override
	ObjectBigListIterator<K> listIterator(long index);

	/**
	 * Returns a type-specific spliterator on the elements of this big-list.
	 *
	 * <p>
	 * BigList spliterators must report at least {@link Spliterator#SIZED} and
	 * {@link Spliterator#ORDERED}.
	 *
	 * <p>
	 * See {@link java.util.List#spliterator()} for more documentation on the requirements of the
	 * returned spliterator (despite {@code BigList} not being a {@code List}, most of the same
	 * requirements apply.
	 *
	 * @apiNote This is generally the only {@code spliterator} method subclasses should override.
	 *
	 * @implSpec The default implementation returns a late-binding spliterator (see {@link Spliterator}
	 *           for documentation on what binding policies mean).
	 *           <ul>
	 *           <li>For {@link java.util.RandomAccess RandomAccess} lists, this will return a
	 *           spliterator that calls the type-specific {@link #get(long)} method on the appropriate
	 *           indexes.</li>
	 *           <li>Otherwise, the spliterator returned will wrap this instance's type specific
	 *           {@link #iterator}.</li>
	 *           </ul>
	 *           <p>
	 *           In either case, the spliterator reports {@link Spliterator#SIZED},
	 *           {@link Spliterator#SUBSIZED}, and {@link Spliterator#ORDERED}.
	 *
	 * @implNote As the non-{@linkplain java.util.RandomAccess RandomAccess} case is based on the
	 *           iterator, and {@link java.util.Iterator} is an inherently linear API, the returned
	 *           spliterator will yield limited performance gains when run in parallel contexts, as the
	 *           returned spliterator's {@link Spliterator#trySplit() trySplit()} will have linear
	 *           runtime.
	 *           <p>
	 *           For {@link java.util.RandomAccess RandomAccess} lists, the parallel performance should
	 *           be reasonable assuming {@link #get(long)} is truly constant time like
	 *           {@link java.util.RandomAccess RandomAccess} suggests.
	 *
	 * @return {@inheritDoc}
	 * @since 8.5.0
	 */
	@Override
	default ObjectSpliterator<K> spliterator() {
		return ObjectSpliterators.asSpliterator(iterator(), size64(), ObjectSpliterators.LIST_SPLITERATOR_CHARACTERISTICS);
	}

	/**
	 * Returns a type-specific view of the portion of this type-specific big list from the index
	 * {@code from}, inclusive, to the index {@code to}, exclusive.
	 *
	 * @apiNote This specification strengthens the one given in {@link BigList#subList(long,long)}.
	 *
	 * @see BigList#subList(long,long)
	 */
	@Override
	ObjectBigList<K> subList(long from, long to);

	/**
	 * Copies (hopefully quickly) elements of this type-specific big list into the given big array.
	 *
	 * @param from the start index (inclusive).
	 * @param a the destination big array.
	 * @param offset the offset into the destination big array where to store the first element copied.
	 * @param length the number of elements to be copied.
	 */
	void getElements(long from, Object a[][], long offset, long length);

	/**
	 * Copies (hopefully quickly) elements of this type-specific big list into the given array.
	 *
	 * @param from the start index (inclusive).
	 * @param a the destination array.
	 * @param offset the offset into the destination array where to store the first element copied.
	 * @param length the number of elements to be copied.
	 */
	default void getElements(long from, Object a[], int offset, int length) {
		getElements(from, new Object[][] { a }, (long)offset, (long)length);
	}

	/**
	 * Removes (hopefully quickly) elements of this type-specific big list.
	 *
	 * @param from the start index (inclusive).
	 * @param to the end index (exclusive).
	 */
	void removeElements(long from, long to);

	/**
	 * Add (hopefully quickly) elements to this type-specific big list.
	 *
	 * @param index the index at which to add elements.
	 * @param a the big array containing the elements.
	 */
	void addElements(long index, K a[][]);

	/**
	 * Add (hopefully quickly) elements to this type-specific big list.
	 *
	 * @param index the index at which to add elements.
	 * @param a the big array containing the elements.
	 * @param offset the offset of the first element to add.
	 * @param length the number of elements to add.
	 */
	void addElements(long index, K a[][], long offset, long length);

	/**
	 * Set (hopefully quickly) elements to match the array given.
	 * 
	 * @param a the big array containing the elements.
	 * @since 8.5.0
	 */
	default void setElements(K a[][]) {
		setElements(0, a);
	}

	/**
	 * Set (hopefully quickly) elements to match the array given.
	 * 
	 * @param index the index at which to start setting elements.
	 * @param a the big array containing the elements.
	 * @since 8.5.0
	 */
	default void setElements(long index, K a[][]) {
		setElements(index, a, 0, length(a));
	}

	/**
	 * Set (hopefully quickly) elements to match the array given.
	 *
	 * Sets each in this list to the corresponding elements in the array, as if by
	 * 
	 * <pre>
	 * ListIterator iter = listIterator(index);
	 * long i = 0;
	 * while (i &lt; length) {
	 *   iter.next();
	 *   iter.set(BigArrays.get(a, offset + i++);
	 * }
	 * </pre>
	 * 
	 * However, the exact implementation may be more efficient, taking into account whether random
	 * access is faster or not, or at the discretion of subclasses, abuse internals.
	 *
	 * @param index the index at which to start setting elements.
	 * @param a the big array containing the elements.
	 * @param offset the offset of the first element to add.
	 * @param length the number of elements to add.
	 * @since 8.5.0
	 */
	default void setElements(long index, K a[][], long offset, long length) {
		// We can't use AbstractList#ensureIndex, sadly.
		if (index < 0) throw new IndexOutOfBoundsException("Index (" + index + ") is negative");
		if (index > size64()) throw new IndexOutOfBoundsException("Index (" + index + ") is greater than list size (" + (size64()) + ")");
		BigArrays.ensureOffsetLength(a, offset, length);
		if (index + length > size64()) throw new IndexOutOfBoundsException("End index (" + (index + length) + ") is greater than list size (" + size64() + ")");
		ObjectBigListIterator<K> iter = listIterator(index);
		long i = 0;
		while (i < length) {
			iter.next();
			iter.set(BigArrays.get(a, offset + i++));
		}
	}

	/**
	 * Inserts all of the elements in the specified type-specific big list into this type-specific big
	 * list at the specified position (optional operation).
	 * 
	 * @apiNote This method exists only for the sake of efficiency: override are expected to use
	 *          {@link #getElements}/{@link #addElements}.
	 * @implSpec This method delegates to the one accepting a collection, but it might be implemented
	 *           more efficiently.
	 * @see BigList#addAll(long,Collection)
	 */
	default boolean addAll(final long index, final ObjectBigList<? extends K> l) {
		return addAll(index, (ObjectCollection<? extends K>)l);
	}

	/**
	 * Appends all of the elements in the specified type-specific big list to the end of this
	 * type-specific big list (optional operation).
	 * 
	 * @implSpec This method delegates to the index-based version, passing {@link #size()} as first
	 *           argument.
	 * @see BigList#addAll(Collection)
	 */
	default boolean addAll(final ObjectBigList<? extends K> l) {
		return addAll(size64(), l);
	}

	/**
	 * Inserts all of the elements in the specified type-specific list into this type-specific big list
	 * at the specified position (optional operation).
	 * 
	 * @apiNote This method exists only for the sake of efficiency: override are expected to use
	 *          {@link #getElements}/{@link #addElements}.
	 * @implSpec This method delegates to the one accepting a collection, but it might be implemented
	 *           more efficiently.
	 * @see BigList#addAll(long,Collection)
	 */
	default boolean addAll(final long index, final ObjectList<? extends K> l) {
		return addAll(index, (ObjectCollection<? extends K>)l);
	}

	/**
	 * Appends all of the elements in the specified type-specific list to the end of this type-specific
	 * big list (optional operation).
	 * 
	 * @implSpec This method delegates to the index-based version, passing {@link #size()} as first
	 *           argument.
	 * @see BigList#addAll(Collection)
	 */
	default boolean addAll(final ObjectList<? extends K> l) {
		return addAll(size64(), l);
	}
	// Without any toBigArray methods, there is no sensible default sort methods we can have.
}
