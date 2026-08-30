/*
	* Copyright (C) 2003-2024 Paolo Boldi and Sebastiano Vigna
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

import java.util.NoSuchElementException;
import it.unimi.dsi.fastutil.PriorityQueue;

/**
 * A type-specific {@link PriorityQueue}; provides some additional methods that use polymorphism to
 * avoid (un)boxing.
 *
 * <p>
 * Additionally, this interface strengthens {@link #comparator()}.
 */
public interface LongPriorityQueue extends PriorityQueue<Long> {
	/**
	 * Enqueues a new element.
	 * 
	 * @see PriorityQueue#enqueue(Object)
	 * @param x the element to enqueue.
	 */
	void enqueue(long x);

	/**
	 * Dequeues the {@linkplain #first() first} element from the queue.
	 * 
	 * @see #dequeue()
	 * @return the dequeued element.
	 * @throws NoSuchElementException if the queue is empty.
	 */
	long dequeueLong();

	/**
	 * Returns the first element of the queue.
	 * 
	 * @see #first()
	 * @return the first element.
	 * @throws NoSuchElementException if the queue is empty.
	 */
	long firstLong();

	/**
	 * Returns the last element of the queue, that is, the element the would be dequeued last (optional
	 * operation).
	 * <p>
	 * This default implementation just throws an {@link UnsupportedOperationException}.
	 * 
	 * @see #last()
	 * @return the last element.
	 * @throws NoSuchElementException if the queue is empty.
	 */
	default long lastLong() {
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns the comparator associated with this priority queue, or null if it uses its elements'
	 * natural ordering.
	 *
	 * @apiNote Note that this specification strengthens the one given in
	 *          {@link PriorityQueue#comparator()}.
	 * @see PriorityQueue#comparator()
	 * @return the comparator associated with this priority queue.
	 */
	@Override
	LongComparator comparator();

	/**
	 * {@inheritDoc}
	 * <p>
	 * This default implementation delegates to the corresponding type-specific method.
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	default void enqueue(final Long x) {
		enqueue(x.longValue());
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * This default implementation delegates to the corresponding type-specific method.
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	default Long dequeue() {
		return Long.valueOf(dequeueLong());
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * This default implementation delegates to the corresponding type-specific method.
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	default Long first() {
		return Long.valueOf(firstLong());
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * This default implementation delegates to the corresponding type-specific method.
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	default Long last() {
		return Long.valueOf(lastLong());
	}
}
