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

import it.unimi.dsi.fastutil.BigList;
import it.unimi.dsi.fastutil.BigArrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * A class providing static methods and objects that do useful things with type-specific big lists.
 *
 * @see java.util.Collections
 * @see it.unimi.dsi.fastutil.BigList
 */
public final class DoubleBigLists {
	private DoubleBigLists() {
	}

	/**
	 * Shuffles the specified big list using the specified pseudorandom number generator.
	 *
	 * @param l the big list to be shuffled.
	 * @param random a pseudorandom number generator.
	 * @return {@code l}.
	 */
	public static DoubleBigList shuffle(final DoubleBigList l, final Random random) {
		for (long i = l.size64(); i-- != 0;) {
			final long p = (random.nextLong() & 0x7FFFFFFFFFFFFFFFL) % (i + 1);
			final double t = l.getDouble(i);
			l.set(i, l.getDouble(p));
			l.set(p, t);
		}
		return l;
	}

	/**
	 * An immutable class representing an empty type-specific big list.
	 *
	 * <p>
	 * This class may be useful to implement your own in case you subclass a type-specific list.
	 */
	public static class EmptyBigList extends DoubleCollections.EmptyCollection implements DoubleBigList, java.io.Serializable, Cloneable {
		private static final long serialVersionUID = -7046029254386353129L;

		protected EmptyBigList() {
		}

		@Override
		public double getDouble(long i) {
			throw new IndexOutOfBoundsException();
		}

		@Override
		public boolean rem(double k) {
			throw new UnsupportedOperationException();
		}

		@Override
		public double removeDouble(long i) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void add(final long index, final double k) {
			throw new UnsupportedOperationException();
		}

		@Override
		public double set(final long index, final double k) {
			throw new UnsupportedOperationException();
		}

		@Override
		public long indexOf(double k) {
			return -1;
		}

		@Override
		public long lastIndexOf(double k) {
			return -1;
		}

		@Override
		public boolean addAll(long i, Collection<? extends Double> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(DoubleCollection c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(DoubleBigList c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(long i, DoubleCollection c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(long i, DoubleBigList c) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public void add(final long index, final Double k) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public boolean add(final Double k) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public Double get(long i) {
			throw new IndexOutOfBoundsException();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public Double set(final long index, final Double k) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public Double remove(long k) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public long indexOf(Object k) {
			return -1;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public long lastIndexOf(Object k) {
			return -1;
		}

		@Override

		public DoubleBigListIterator listIterator() {
			return DoubleBigListIterators.EMPTY_BIG_LIST_ITERATOR;
		}

		@Override

		public DoubleBigListIterator iterator() {
			return DoubleBigListIterators.EMPTY_BIG_LIST_ITERATOR;
		}

		@Override

		public DoubleBigListIterator listIterator(long i) {
			if (i == 0) return DoubleBigListIterators.EMPTY_BIG_LIST_ITERATOR;
			throw new IndexOutOfBoundsException(String.valueOf(i));
		}

		@Override

		public DoubleSpliterator spliterator() {
			return DoubleSpliterators.EMPTY_SPLITERATOR;
		}

		@Override
		public DoubleBigList subList(long from, long to) {
			if (from == 0 && to == 0) return this;
			throw new IndexOutOfBoundsException();
		}

		@Override
		public void getElements(long from, double[][] a, long offset, long length) {
			BigArrays.ensureOffsetLength(a, offset, length);
			if (from != 0) throw new IndexOutOfBoundsException();
		}

		@Override
		public void removeElements(long from, long to) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void addElements(long index, final double a[][], long offset, long length) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void addElements(long index, final double a[][]) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void size(long s) {
			throw new UnsupportedOperationException();
		}

		@Override
		public long size64() {
			return 0;
		}

		@Override
		public int compareTo(final BigList<? extends Double> o) {
			if (o == this) return 0;
			return ((BigList<?>)o).isEmpty() ? 0 : -1;
		}

		@Override
		public Object clone() {
			return EMPTY_BIG_LIST;
		}

		@Override
		public int hashCode() {
			return 1;
		}

		@Override
		@SuppressWarnings("rawtypes")
		public boolean equals(Object o) {
			return o instanceof BigList && ((BigList)o).isEmpty();
		}

		@Override
		public String toString() {
			return "[]";
		}

		private Object readResolve() {
			return EMPTY_BIG_LIST;
		}
	}

	/**
	 * An empty big list (immutable). It is serializable and cloneable.
	 */

	public static final EmptyBigList EMPTY_BIG_LIST = new EmptyBigList();

	/**
	 * An immutable class representing a type-specific singleton big list.
	 *
	 * <p>
	 * This class may be useful to implement your own in case you subclass a type-specific big list.
	 */
	public static class Singleton extends AbstractDoubleBigList implements java.io.Serializable, Cloneable {
		private static final long serialVersionUID = -7046029254386353129L;
		private final double element;

		protected Singleton(final double element) {
			this.element = element;
		}

		@Override
		public double getDouble(final long i) {
			if (i == 0) return element;
			throw new IndexOutOfBoundsException();
		}

		@Override
		public boolean rem(double k) {
			throw new UnsupportedOperationException();
		}

		@Override
		public double removeDouble(final long i) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean contains(final double k) {
			return (Double.doubleToLongBits(k) == Double.doubleToLongBits(element));
		}

		@Override
		public long indexOf(final double k) {
			return (Double.doubleToLongBits(k) == Double.doubleToLongBits(element)) ? 0 : -1;
		}

		/* Slightly optimized w.r.t. the one in ABSTRACT_SET. */
		@Override
		public double[] toDoubleArray() {
			double a[] = { element };
			return a;
		}

		@Override
		public DoubleBigListIterator listIterator() {
			return DoubleBigListIterators.singleton(element);
		}

		@Override
		public DoubleBigListIterator listIterator(long i) {
			if (i > 1 || i < 0) throw new IndexOutOfBoundsException();
			DoubleBigListIterator l = listIterator();
			if (i == 1) l.nextDouble();
			return l;
		}

		@Override
		public DoubleSpliterator spliterator() {
			return DoubleSpliterators.singleton(element);
		}

		@Override

		public DoubleBigList subList(final long from, final long to) {
			ensureIndex(from);
			ensureIndex(to);
			if (from > to) throw new IndexOutOfBoundsException("Start index (" + from + ") is greater than end index (" + to + ")");
			if (from != 0 || to != 1) return EMPTY_BIG_LIST;
			return this;
		}

		@Override
		public boolean addAll(long i, Collection<? extends Double> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(final Collection<? extends Double> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean removeAll(final Collection<?> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean retainAll(final Collection<?> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(DoubleBigList c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(long i, DoubleBigList c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(long i, DoubleCollection c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(final DoubleCollection c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean removeAll(final DoubleCollection c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean retainAll(final DoubleCollection c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void clear() {
			throw new UnsupportedOperationException();
		}

		@Override
		public long size64() {
			return 1;
		}

		@Override
		public Object clone() {
			return this;
		}
	}

	/**
	 * Returns a type-specific immutable big list containing only the specified element. The returned
	 * big list is serializable and cloneable.
	 *
	 * @param element the only element of the returned big list.
	 * @return a type-specific immutable big list containing just {@code element}.
	 */
	public static DoubleBigList singleton(final double element) {
		return new Singleton(element);
	}

	/**
	 * Returns a type-specific immutable big list containing only the specified element. The returned
	 * big list is serializable and cloneable.
	 *
	 * @param element the only element of the returned big list.
	 * @return a type-specific immutable big list containing just {@code element}.
	 */
	public static DoubleBigList singleton(final Object element) {
		return new Singleton(((Double)(element)).doubleValue());
	}

	/** A synchronized wrapper class for big lists. */
	public static class SynchronizedBigList extends DoubleCollections.SynchronizedCollection implements DoubleBigList, java.io.Serializable {
		private static final long serialVersionUID = -7046029254386353129L;
		protected final DoubleBigList list; // Due to the large number of methods that are not in COLLECTION, this is
											// worth caching.

		protected SynchronizedBigList(final DoubleBigList l, final Object sync) {
			super(l, sync);
			this.list = l;
		}

		protected SynchronizedBigList(final DoubleBigList l) {
			super(l);
			this.list = l;
		}

		@Override
		public double getDouble(final long i) {
			synchronized (sync) {
				return list.getDouble(i);
			}
		}

		@Override
		public double set(final long i, final double k) {
			synchronized (sync) {
				return list.set(i, k);
			}
		}

		@Override
		public void add(final long i, final double k) {
			synchronized (sync) {
				list.add(i, k);
			}
		}

		@Override
		public double removeDouble(final long i) {
			synchronized (sync) {
				return list.removeDouble(i);
			}
		}

		@Override
		public long indexOf(final double k) {
			synchronized (sync) {
				return list.indexOf(k);
			}
		}

		@Override
		public long lastIndexOf(final double k) {
			synchronized (sync) {
				return list.lastIndexOf(k);
			}
		}

		@Override
		public boolean addAll(final long index, final Collection<? extends Double> c) {
			synchronized (sync) {
				return list.addAll(index, c);
			}
		}

		@Override
		public void getElements(final long from, final double a[][], final long offset, final long length) {
			synchronized (sync) {
				list.getElements(from, a, offset, length);
			}
		}

		@Override
		public void removeElements(final long from, final long to) {
			synchronized (sync) {
				list.removeElements(from, to);
			}
		}

		@Override
		public void addElements(long index, final double a[][], long offset, long length) {
			synchronized (sync) {
				list.addElements(index, a, offset, length);
			}
		}

		@Override
		public void addElements(long index, final double a[][]) {
			synchronized (sync) {
				list.addElements(index, a);
			}
		}

		/* {@inheritDoc}
		 * @deprecated Use {@link #size64()} instead.
		 */
		@Deprecated
		@Override
		public void size(final long size) {
			synchronized (sync) {
				list.size(size);
			}
		}

		@Override
		public long size64() {
			synchronized (sync) {
				return list.size64();
			}
		}

		@Override
		public DoubleBigListIterator iterator() {
			return list.listIterator();
		}

		@Override
		public DoubleBigListIterator listIterator() {
			return list.listIterator();
		}

		@Override
		public DoubleBigListIterator listIterator(final long i) {
			return list.listIterator(i);
		}

		@Override
		public DoubleBigList subList(final long from, final long to) {
			synchronized (sync) {
				return synchronize(list.subList(from, to), sync);
			}
		}

		@Override
		public boolean equals(final Object o) {
			if (o == this) return true;
			synchronized (sync) {
				return list.equals(o);
			}
		}

		@Override
		public int hashCode() {
			synchronized (sync) {
				return list.hashCode();
			}
		}

		@Override
		public int compareTo(final BigList<? extends Double> o) {
			synchronized (sync) {
				return list.compareTo(o);
			}
		}

		@Override
		public boolean addAll(final long index, final DoubleCollection c) {
			synchronized (sync) {
				return list.addAll(index, c);
			}
		}

		@Override
		public boolean addAll(final long index, DoubleBigList l) {
			synchronized (sync) {
				return list.addAll(index, l);
			}
		}

		@Override
		public boolean addAll(DoubleBigList l) {
			synchronized (sync) {
				return list.addAll(l);
			}
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public void add(final long i, Double k) {
			synchronized (sync) {
				list.add(i, k);
			}
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public Double get(final long i) {
			synchronized (sync) {
				return list.get(i);
			}
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public Double set(final long index, Double k) {
			synchronized (sync) {
				return list.set(index, k);
			}
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public Double remove(final long i) {
			synchronized (sync) {
				return list.remove(i);
			}
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public long indexOf(final Object o) {
			synchronized (sync) {
				return list.indexOf(o);
			}
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public long lastIndexOf(final Object o) {
			synchronized (sync) {
				return list.lastIndexOf(o);
			}
		}
	}

	/**
	 * Returns a synchronized type-specific big list backed by the given type-specific big list.
	 *
	 * @param l the big list to be wrapped in a synchronized big list.
	 * @return a synchronized view of the specified big list.
	 * @see java.util.Collections#synchronizedList(List)
	 */
	public static DoubleBigList synchronize(final DoubleBigList l) {
		return new SynchronizedBigList(l);
	}

	/**
	 * Returns a synchronized type-specific big list backed by the given type-specific big list, using
	 * an assigned object to synchronize.
	 *
	 * @param l the big list to be wrapped in a synchronized big list.
	 * @param sync an object that will be used to synchronize the access to the big list.
	 * @return a synchronized view of the specified big list.
	 * @see java.util.Collections#synchronizedList(List)
	 */
	public static DoubleBigList synchronize(final DoubleBigList l, final Object sync) {
		return new SynchronizedBigList(l, sync);
	}

	/** An unmodifiable wrapper class for big lists. */
	public static class UnmodifiableBigList extends DoubleCollections.UnmodifiableCollection implements DoubleBigList, java.io.Serializable {
		private static final long serialVersionUID = -7046029254386353129L;
		protected final DoubleBigList list; // Due to the large number of methods that are not in COLLECTION, this is
											// worth caching.

		protected UnmodifiableBigList(final DoubleBigList l) {
			super(l);
			this.list = l;
		}

		@Override
		public double getDouble(final long i) {
			return list.getDouble(i);
		}

		@Override
		public double set(final long i, final double k) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void add(final long i, final double k) {
			throw new UnsupportedOperationException();
		}

		@Override
		public double removeDouble(final long i) {
			throw new UnsupportedOperationException();
		}

		@Override
		public long indexOf(final double k) {
			return list.indexOf(k);
		}

		@Override
		public long lastIndexOf(final double k) {
			return list.lastIndexOf(k);
		}

		@Override
		public boolean addAll(final long index, final Collection<? extends Double> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void getElements(final long from, final double a[][], final long offset, final long length) {
			list.getElements(from, a, offset, length);
		}

		@Override
		public void removeElements(final long from, final long to) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void addElements(long index, final double a[][], long offset, long length) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void addElements(long index, final double a[][]) {
			throw new UnsupportedOperationException();
		}

		/* {@inheritDoc}
		 * @deprecated Use {@link #size64()} instead.
		 */
		@Deprecated
		@Override
		public void size(final long size) {
			list.size(size);
		}

		@Override
		public long size64() {
			return list.size64();
		}

		@Override
		public DoubleBigListIterator iterator() {
			return listIterator();
		}

		@Override
		public DoubleBigListIterator listIterator() {
			return DoubleBigListIterators.unmodifiable(list.listIterator());
		}

		@Override
		public DoubleBigListIterator listIterator(final long i) {
			return DoubleBigListIterators.unmodifiable(list.listIterator(i));
		}

		@Override
		public DoubleBigList subList(final long from, final long to) {
			return unmodifiable(list.subList(from, to));
		}

		@Override
		public boolean equals(final Object o) {
			if (o == this) return true;
			return list.equals(o);
		}

		@Override
		public int hashCode() {
			return list.hashCode();
		}

		@Override
		public int compareTo(final BigList<? extends Double> o) {
			return list.compareTo(o);
		}

		@Override
		public boolean addAll(final long index, final DoubleCollection c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(final DoubleBigList l) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(final long index, final DoubleBigList l) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public Double get(final long i) {
			return list.get(i);
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public void add(final long i, Double k) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public Double set(final long index, Double k) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public Double remove(final long i) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public long indexOf(final Object o) {
			return list.indexOf(o);
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public long lastIndexOf(final Object o) {
			return list.lastIndexOf(o);
		}
	}

	/**
	 * Returns an unmodifiable type-specific big list backed by the given type-specific big list.
	 *
	 * @param l the big list to be wrapped in an unmodifiable big list.
	 * @return an unmodifiable view of the specified big list.
	 * @see java.util.Collections#unmodifiableList(List)
	 */
	public static DoubleBigList unmodifiable(final DoubleBigList l) {
		return new UnmodifiableBigList(l);
	}

	/** A class exposing a list as a big list. */
	public static class ListBigList extends AbstractDoubleBigList implements java.io.Serializable {
		private static final long serialVersionUID = -7046029254386353129L;
		private final DoubleList list;

		protected ListBigList(final DoubleList list) {
			this.list = list;
		}

		private int intIndex(long index) {
			if (index >= Integer.MAX_VALUE) throw new IndexOutOfBoundsException("This big list is restricted to 32-bit indices");
			return (int)index;
		}

		@Override
		public long size64() {
			return list.size();
		}

		@Override
		public void size(final long size) {
			list.size(intIndex(size));
		}

		@Override
		public DoubleBigListIterator iterator() {
			return DoubleBigListIterators.asBigListIterator(list.iterator());
		}

		@Override
		public DoubleBigListIterator listIterator() {
			return DoubleBigListIterators.asBigListIterator(list.listIterator());
		}

		@Override
		public DoubleBigListIterator listIterator(final long index) {
			return DoubleBigListIterators.asBigListIterator(list.listIterator(intIndex(index)));
		}

		@Override
		public boolean addAll(final long index, final Collection<? extends Double> c) {
			return list.addAll(intIndex(index), c);
		}

		@Override
		public DoubleBigList subList(long from, long to) {
			return new ListBigList(list.subList(intIndex(from), intIndex(to)));
		}

		@Override
		public boolean contains(final double key) {
			return list.contains(key);
		}

		@Override
		public double[] toDoubleArray() {
			return list.toDoubleArray();
		}

		@Override
		public void removeElements(final long from, final long to) {
			list.removeElements(intIndex(from), intIndex(to));
		}

		/* {@inheritDoc}
		 * @deprecated Please use {@code toArray()} instead&mdash;this method is redundant and will be removed in the future.
		 */
		@Deprecated
		@Override
		public double[] toDoubleArray(double[] a) {
			return list.toArray(a);
		}

		@Override
		public boolean addAll(long index, DoubleCollection c) {
			return list.addAll(intIndex(index), c);
		}

		@Override
		public boolean addAll(DoubleCollection c) {
			return list.addAll(c);
		}

		@Override
		public boolean addAll(long index, DoubleBigList c) {
			return list.addAll(intIndex(index), c);
		}

		@Override
		public boolean addAll(DoubleBigList c) {
			return list.addAll(c);
		}

		@Override
		public boolean containsAll(DoubleCollection c) {
			return list.containsAll(c);
		}

		@Override
		public boolean removeAll(DoubleCollection c) {
			return list.removeAll(c);
		}

		@Override
		public boolean retainAll(DoubleCollection c) {
			return list.retainAll(c);
		}

		@Override
		public void add(long index, double key) {
			list.add(intIndex(index), key);
		}

		@Override
		public boolean add(double key) {
			return list.add(key);
		}

		@Override
		public double getDouble(long index) {
			return list.getDouble(intIndex(index));
		}

		@Override
		public long indexOf(double k) {
			return list.indexOf(k);
		}

		@Override
		public long lastIndexOf(double k) {
			return list.lastIndexOf(k);
		}

		@Override
		public double removeDouble(long index) {
			return list.removeDouble(intIndex(index));
		}

		@Override
		public double set(long index, double k) {
			return list.set(intIndex(index), k);
		}

		@Override
		public boolean isEmpty() {
			return list.isEmpty();
		}

		@Override
		public <T> T[] toArray(T[] a) {
			return list.toArray(a);
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			return list.containsAll(c);
		}

		@Override
		public boolean addAll(Collection<? extends Double> c) {
			return list.addAll(c);
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			return list.removeAll(c);
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			return list.retainAll(c);
		}

		@Override
		public void clear() {
			list.clear();
		}

		@Override
		public int hashCode() {
			return list.hashCode();
		}
	}

	/**
	 * Returns a big list backed by the specified list.
	 *
	 * @param list a list.
	 * @return a big list backed by the specified list.
	 */
	public static DoubleBigList asBigList(final DoubleList list) {
		return new ListBigList(list);
	}
}
