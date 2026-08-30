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
package it.unimi.dsi.fastutil.doubles;

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
public abstract class AbstractDoubleCollection extends AbstractCollection<Double> implements DoubleCollection {
	protected AbstractDoubleCollection() {
	}

	@Override
	public abstract DoubleIterator iterator();

	/**
	 * {@inheritDoc}
	 *
	 * @implSpec This implementation always throws an {@link UnsupportedOperationException}.
	 */
	@Override
	public boolean add(final double k) {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 *
	 * @implSpec This implementation iterates over the elements in the collection, looking for the
	 *           specified element.
	 */
	@Override
	public boolean contains(final double k) {
		final DoubleIterator iterator = iterator();
		while (iterator.hasNext()) if (k == iterator.nextDouble()) return true;
		return false;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @implSpec This implementation iterates over the elements in the collection, looking for the
	 *           specified element and tries to remove it.
	 */
	@Override
	public boolean rem(final double k) {
		final DoubleIterator iterator = iterator();
		while (iterator.hasNext()) if (k == iterator.nextDouble()) {
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
	public boolean add(final Double key) {
		return DoubleCollection.super.add(key);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	public boolean contains(final Object key) {
		return DoubleCollection.super.contains(key);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	public boolean remove(final Object key) {
		return DoubleCollection.super.remove(key);
	}

	@Override
	public double[] toArray(double[] a) {
		final int size = size();
		if (a == null) {
			a = new double[size];
		} else if (a.length < size) {
			a = java.util.Arrays.copyOf(a, size);
		}
		DoubleIterators.unwrap(iterator(), a);
		return a;
	}

	@Override
	public double[] toDoubleArray() {
		final int size = size();
		if (size == 0) return DoubleArrays.EMPTY_ARRAY;
		final double a[] = new double[size];
		DoubleIterators.unwrap(iterator(), a);
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
	public double[] toDoubleArray(final double[] a) {
		return toArray(a);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @apiNote This method exists to make final what should have been final in the interface.
	 */
	@Override
	public final void forEach(final DoubleConsumer action) {
		DoubleCollection.super.forEach(action);
	}

	/**
	 * {@inheritDoc}
	 *
	 * @apiNote This method exists to make final what should have been final in the interface.
	 */
	@Override
	public final boolean removeIf(final DoublePredicate filter) {
		return DoubleCollection.super.removeIf(filter);
	}

	@Override
	public boolean addAll(final DoubleCollection c) {
		boolean retVal = false;
		for (final DoubleIterator i = c.iterator(); i.hasNext();) if (add(i.nextDouble())) retVal = true;
		return retVal;
	}

	/**
	 * {@inheritDoc}
	 *
	 * @implSpec This implementation delegates to the type-specific version if given a type-specific
	 *           collection, otherwise is uses the implementation from {@link AbstractCollection}.
	 */
	@Override
	public boolean addAll(final Collection<? extends Double> c) {
		if (c instanceof DoubleCollection) {
			return addAll((DoubleCollection)c);
		}
		return super.addAll(c);
	}

	@Override
	public boolean containsAll(final DoubleCollection c) {
		for (final DoubleIterator i = c.iterator(); i.hasNext();) if (!contains(i.nextDouble())) return false;
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
		if (c instanceof DoubleCollection) {
			return containsAll((DoubleCollection)c);
		}
		return super.containsAll(c);
	}

	@Override
	public boolean removeAll(final DoubleCollection c) {
		boolean retVal = false;
		for (final DoubleIterator i = c.iterator(); i.hasNext();) if (rem(i.nextDouble())) retVal = true;
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
		if (c instanceof DoubleCollection) {
			return removeAll((DoubleCollection)c);
		}
		return super.removeAll(c);
	}

	@Override
	public boolean retainAll(final DoubleCollection c) {
		boolean retVal = false;
		for (final DoubleIterator i = iterator(); i.hasNext();) if (!c.contains(i.nextDouble())) {
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
		if (c instanceof DoubleCollection) {
			return retainAll((DoubleCollection)c);
		}
		return super.retainAll(c);
	}

	@Override
	public String toString() {
		final StringBuilder s = new StringBuilder();
		final DoubleIterator i = iterator();
		int n = size();
		double k;
		boolean first = true;
		s.append("{");
		while (n-- != 0) {
			if (first) first = false;
			else s.append(", ");
			k = i.nextDouble();
			s.append(String.valueOf(k));
		}
		s.append("}");
		return s.toString();
	}
}
