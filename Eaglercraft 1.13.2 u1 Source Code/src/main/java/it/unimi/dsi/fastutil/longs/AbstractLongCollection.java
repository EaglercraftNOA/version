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

import java.util.AbstractCollection;
import java.util.Collection;

/**
 * An abstract class providing basic methods for collections implementing a type-specific interface.
 *
 * <p>
 * In particular, this class provide {@link #iterator()}, {@code add()}, {@link #remove(Object)} and
 * {@link #contains(Object)} methods that just call the type-specific counterpart.
 *
 * <p>
 * <strong>Warning</strong>: Because of a name clash between the list and collection interfaces the
 * type-specific deletion method of a type-specific abstract collection is {@code rem()}, rather
 * then {@code remove()}. A subclass must thus override {@code rem()}, rather than {@code remove()},
 * to make all inherited methods work properly.
 */
public abstract class AbstractLongCollection extends AbstractCollection<Long> implements LongCollection {
	protected AbstractLongCollection() {
	}

	@Override
	public abstract LongIterator iterator();

	/**
	 * {@inheritDoc}
	 *
	 * @implSpec This implementation always throws an {@link UnsupportedOperationException}.
	 */
	@Override
	public boolean add(final long k) {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @implSpec This implementation iterates over the elements in the collection, looking for the
	 *           specified element.
	 */
	@Override
	public boolean contains(final long k) {
		final LongIterator iterator = iterator();
		while (iterator.hasNext()) if (k == iterator.nextLong()) return true;
		return false;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @implSpec This implementation iterates over the elements in the collection, looking for the
	 *           specified element and tries to remove it.
	 */
	@Override
	public boolean rem(final long k) {
		final LongIterator iterator = iterator();
		while (iterator.hasNext()) if (k == iterator.nextLong()) {
			iterator.remove();
			return true;
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	public boolean add(final Long key) {
		return LongCollection.super.add(key);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	public boolean contains(final Object key) {
		return LongCollection.super.contains(key);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	public boolean remove(final Object key) {
		return LongCollection.super.remove(key);
	}

	@Override
	public long[] toArray(long[] a) {
		final int size = size();
		if (a == null) {
			a = new long[size];
		} else if (a.length < size) {
			a = java.util.Arrays.copyOf(a, size);
		}
		LongIterators.unwrap(iterator(), a);
		return a;
	}

	@Override
	public long[] toLongArray() {
		final int size = size();
		if (size == 0) return LongArrays.EMPTY_ARRAY;
		final long a[] = new long[size];
		LongIterators.unwrap(iterator(), a);
		return a;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use {@code toArray()} instead&mdash;this method is redundant and will be
	 *             removed in the future.
	 */
	@Deprecated
	@Override
	public long[] toLongArray(final long[] a) {
		return toArray(a);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @apiNote This method exists to make final what should have been final in the interface.
	 */
	@Override
	public final void forEach(final LongConsumer action) {
		LongCollection.super.forEach(action);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @apiNote This method exists to make final what should have been final in the interface.
	 */
	@Override
	public final boolean removeIf(final LongPredicate filter) {
		return LongCollection.super.removeIf(filter);
	}

	@Override
	public boolean addAll(final LongCollection c) {
		boolean retVal = false;
		for (final LongIterator i = c.iterator(); i.hasNext();) if (add(i.nextLong())) retVal = true;
		return retVal;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @implSpec This implementation delegates to the type-specific version if given a type-specific
	 *           collection, otherwise is uses the implementation from {@link AbstractCollection}.
	 */
	@Override
	public boolean addAll(final Collection<? extends Long> c) {
		if (c instanceof LongCollection) {
			return addAll((LongCollection)c);
		}
		return super.addAll(c);
	}

	@Override
	public boolean containsAll(final LongCollection c) {
		for (final LongIterator i = c.iterator(); i.hasNext();) if (!contains(i.nextLong())) return false;
		return true;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @implSpec This implementation delegates to the type-specific version if given a type-specific
	 *           collection, otherwise is uses the implementation from {@link AbstractCollection}.
	 */
	@Override
	public boolean containsAll(final Collection<?> c) {
		if (c instanceof LongCollection) {
			return containsAll((LongCollection)c);
		}
		return super.containsAll(c);
	}

	@Override
	public boolean removeAll(final LongCollection c) {
		boolean retVal = false;
		for (final LongIterator i = c.iterator(); i.hasNext();) if (rem(i.nextLong())) retVal = true;
		return retVal;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @implSpec This implementation delegates to the type-specific version if given a type-specific
	 *           collection, otherwise is uses the implementation from {@link AbstractCollection}.
	 */
	@Override
	public boolean removeAll(final Collection<?> c) {
		if (c instanceof LongCollection) {
			return removeAll((LongCollection)c);
		}
		return super.removeAll(c);
	}

	@Override
	public boolean retainAll(final LongCollection c) {
		boolean retVal = false;
		for (final LongIterator i = iterator(); i.hasNext();) if (!c.contains(i.nextLong())) {
			i.remove();
			retVal = true;
		}
		return retVal;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @implSpec This implementation delegates to the type-specific version if given a type-specific
	 *           collection, otherwise is uses the implementation from {@link AbstractCollection}.
	 */
	@Override
	public boolean retainAll(final Collection<?> c) {
		if (c instanceof LongCollection) {
			return retainAll((LongCollection)c);
		}
		return super.retainAll(c);
	}

	@Override
	public String toString() {
		final StringBuilder s = new StringBuilder();
		final LongIterator i = iterator();
		int n = size();
		long k;
		boolean first = true;
		s.append("{");
		while (n-- != 0) {
			if (first) first = false;
			else s.append(", ");
			k = i.nextLong();
			s.append(String.valueOf(k));
		}
		s.append("}");
		return s.toString();
	}
}
