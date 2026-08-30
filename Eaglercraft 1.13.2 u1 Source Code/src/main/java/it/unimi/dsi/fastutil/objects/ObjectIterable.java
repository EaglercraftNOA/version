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
package it.unimi.dsi.fastutil.objects;

import java.lang.Iterable;

/**
 * A type-specific {@link Iterable} that strengthens that specification of {@link #iterator()}.
 *
 * @see Iterable
 */
public interface ObjectIterable<K> extends Iterable<K> {
	/**
	 * Returns a type-specific iterator.
	 *
	 * @apiNote Note that this specification strengthens the one given in {@link Iterable#iterator()}.
	 *
	 * @return a type-specific iterator.
	 * @see Iterable#iterator()
	 */
	@Override
	ObjectIterator<K> iterator();

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
	default ObjectSpliterator<K> spliterator() {
		return ObjectSpliterators.asSpliteratorUnknownSize(iterator(), 0);
	}
}
