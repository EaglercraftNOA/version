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
package it.unimi.dsi.fastutil.shorts;

import java.util.List;
import java.util.Collection;
import java.util.Random;
import java.util.RandomAccess;
import java.util.function.Consumer;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntIterators;
import it.unimi.dsi.fastutil.ints.IntSpliterator;
import it.unimi.dsi.fastutil.ints.IntSpliterators;

/**
 * A class providing static methods and objects that do useful things with type-specific lists.
 *
 * @see java.util.Collections
 */
public final class ShortLists {
	/* Only in the EmptyList and Singleton classes, where performance is critical, do we override
	 * the deprecated, Object based functional methods. For the rest, we just override the
	 * non-deprecated type-specific method, and let the default method from the interface
	 * filter into that. This is an extra method call and lambda creation, but it isn't worth
	 * complexifying the code generation for a case that is already marked as being inefficient.
	 */
	private ShortLists() {
	}

	/**
	 * Shuffles the specified list using the specified pseudorandom number generator.
	 *
	 * @param l the list to be shuffled.
	 * @param random a pseudorandom number generator.
	 * @return {@code l}.
	 */
	public static ShortList shuffle(final ShortList l, final Random random) {
		for (int i = l.size(); i-- != 0;) {
			final int p = random.nextInt(i + 1);
			final short t = l.getShort(i);
			l.set(i, l.getShort(p));
			l.set(p, t);
		}
		return l;
	}

	/**
	 * An immutable class representing an empty type-specific list.
	 *
	 * <p>
	 * This class may be useful to implement your own in case you subclass a type-specific list.
	 */
	public static class EmptyList extends ShortCollections.EmptyCollection implements ShortList, RandomAccess, java.io.Serializable, Cloneable {
		private static final long serialVersionUID = -7046029254386353129L;

		protected EmptyList() {
		}

		@Override
		public short getShort(int i) {
			throw new IndexOutOfBoundsException();
		}

		@Override
		public boolean rem(short k) {
			throw new UnsupportedOperationException();
		}

		@Override
		public short removeShort(int i) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void add(final int index, final short k) {
			throw new UnsupportedOperationException();
		}

		@Override
		public short set(final int index, final short k) {
			throw new UnsupportedOperationException();
		}

		@Override
		public int indexOf(short k) {
			return -1;
		}

		@Override
		public int lastIndexOf(short k) {
			return -1;
		}

		@Override
		public boolean addAll(int i, Collection<? extends Short> c) {
			throw new UnsupportedOperationException();
		}

		@Deprecated
		@Override
		public void replaceAll(final java.util.function.UnaryOperator<Short> operator) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void replaceAll(final ShortUnaryOperator operator) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(ShortList c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(int i, ShortCollection c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(int i, ShortList c) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@SuppressWarnings("deprecation")
		@Deprecated
		@Override
		public void add(final int index, final Short k) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@SuppressWarnings("deprecation")
		@Deprecated
		@Override
		public Short get(final int index) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@SuppressWarnings("deprecation")
		@Deprecated
		@Override
		public boolean add(final Short k) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@SuppressWarnings("deprecation")
		@Deprecated
		@Override
		public Short set(final int index, final Short k) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@SuppressWarnings("deprecation")
		@Deprecated
		@Override
		public Short remove(int k) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@SuppressWarnings("deprecation")
		@Deprecated
		@Override
		public int indexOf(Object k) {
			return -1;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@SuppressWarnings("deprecation")
		@Deprecated
		@Override
		public int lastIndexOf(Object k) {
			return -1;
		}

		// Empty lists are trivially always sorted
		@Override
		public void sort(final ShortComparator comparator) {
		}

		@Override
		public void unstableSort(final ShortComparator comparator) {
		}

		// Empty lists are trivially always sorted
		@Deprecated
		@Override
		public void sort(final java.util.Comparator<? super Short> comparator) {
		}

		@Deprecated
		@Override
		public void unstableSort(final java.util.Comparator<? super Short> comparator) {
		}

		@Override
		public ShortListIterator listIterator() {
			return ShortIterators.EMPTY_ITERATOR;
		}

		@Override
		public ShortListIterator iterator() {
			return ShortIterators.EMPTY_ITERATOR;
		}

		@Override
		public ShortListIterator listIterator(int i) {
			if (i == 0) return ShortIterators.EMPTY_ITERATOR;
			throw new IndexOutOfBoundsException(String.valueOf(i));
		}

		@Override
		public ShortList subList(int from, int to) {
			if (from == 0 && to == 0) return this;
			throw new IndexOutOfBoundsException();
		}

		@Override
		public void getElements(int from, short[] a, int offset, int length) {
			if (from == 0 && length == 0 && offset >= 0 && offset <= a.length) return;
			throw new IndexOutOfBoundsException();
		}

		@Override
		public void removeElements(int from, int to) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void addElements(int index, final short a[], int offset, int length) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void addElements(int index, final short a[]) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setElements(final short a[]) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setElements(int index, final short a[]) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setElements(int index, final short a[], int offset, int length) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void size(int s) {
			throw new UnsupportedOperationException();
		}

		@Override
		public int compareTo(final List<? extends Short> o) {
			if (o == this) return 0;
			return ((List<?>)o).isEmpty() ? 0 : -1;
		}

		@Override
		public Object clone() {
			return EMPTY_LIST;
		}

		@Override
		public int hashCode() {
			return 1;
		}

		@Override
		@SuppressWarnings("rawtypes")
		public boolean equals(Object o) {
			return o instanceof List && ((List)o).isEmpty();
		}

		@Override
		public String toString() {
			return "[]";
		}

		private Object readResolve() {
			return EMPTY_LIST;
		}
	}

	/**
	 * An empty list (immutable). It is serializable and cloneable.
	 */

	public static final EmptyList EMPTY_LIST = new EmptyList();

	/**
	 * Returns an empty list (immutable). It is serializable and cloneable.
	 *
	 * <p>
	 * This method provides a typesafe access to {@link #EMPTY_LIST}.
	 * 
	 * @return an empty list (immutable).
	 */
	@SuppressWarnings("unchecked")
	public static ShortList emptyList() {
		return EMPTY_LIST;
	}

	/**
	 * An immutable class representing a type-specific singleton list.
	 *
	 * <p>
	 * This class may be useful to implement your own in case you subclass a type-specific list.
	 */
	public static class Singleton extends AbstractShortList implements RandomAccess, java.io.Serializable, Cloneable {
		private static final long serialVersionUID = -7046029254386353129L;
		private final short element;

		protected Singleton(final short element) {
			this.element = element;
		}

		@Override
		public short getShort(final int i) {
			if (i == 0) return element;
			throw new IndexOutOfBoundsException();
		}

		@Override
		public boolean rem(short k) {
			throw new UnsupportedOperationException();
		}

		@Override
		public short removeShort(final int i) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean contains(final short k) {
			return ((k) == (element));
		}

		@Override
		public int indexOf(final short k) {
			return ((k) == (element)) ? 0 : -1;
		}

		/* Slightly optimized w.r.t. the one in ABSTRACT_SET. */
		@Override
		public short[] toShortArray() {
			return new short[] { element };
		}

		@Override
		public ShortListIterator listIterator() {
			return ShortIterators.singleton(element);
		}

		@Override
		public ShortListIterator iterator() {
			return listIterator();
		}

		@Override
		public ShortSpliterator spliterator() {
			return ShortSpliterators.singleton(element);
		}

		@Override
		public ShortListIterator listIterator(final int i) {
			if (i > 1 || i < 0) throw new IndexOutOfBoundsException();
			final ShortListIterator l = listIterator();
			if (i == 1) l.nextShort();
			return l;
		}

		@Override

		public ShortList subList(final int from, final int to) {
			ensureIndex(from);
			ensureIndex(to);
			if (from > to) throw new IndexOutOfBoundsException("Start index (" + from + ") is greater than end index (" + to + ")");
			if (from != 0 || to != 1) return EMPTY_LIST;
			return this;
		}

		@Deprecated
		@Override
		public void forEach(final Consumer<? super Short> action) {
			action.accept(Short.valueOf(element));
		}

		@Override
		public boolean addAll(int i, Collection<? extends Short> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(final Collection<? extends Short> c) {
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

		@Deprecated
		@Override
		public boolean removeIf(final java.util.function.Predicate<? super Short> filter) {
			throw new UnsupportedOperationException();
		}

		@Deprecated
		@Override
		public void replaceAll(final java.util.function.UnaryOperator<Short> operator) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void replaceAll(final ShortUnaryOperator operator) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void forEach(final ShortConsumer action) {
			action.accept(element);
		}

		@Override
		public boolean addAll(ShortList c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(int i, ShortList c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(int i, ShortCollection c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(final ShortCollection c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean removeAll(final ShortCollection c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean retainAll(final ShortCollection c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean removeIf(final ShortPredicate filter) {
			throw new UnsupportedOperationException();
		}

		@Override
		public IntIterator intIterator() {
			return IntIterators.singleton(element);
		}

		@Override
		public IntSpliterator intSpliterator() {
			return IntSpliterators.singleton(element);
		}

		@Deprecated
		@Override
		public Object[] toArray() {
			return new Object[] { Short.valueOf(element) };
		}

		// Lists of size 1 are trivially always sorted
		@Override
		public void sort(final ShortComparator comparator) {
		}

		@Override
		public void unstableSort(final ShortComparator comparator) {
		}

		// Lists of size 1 are trivially always sorted
		@Deprecated
		@Override
		public void sort(final java.util.Comparator<? super Short> comparator) {
		}

		@Deprecated
		@Override
		public void unstableSort(final java.util.Comparator<? super Short> comparator) {
		}

		@Override
		public void getElements(int from, short a[], int offset, int length) {
			if (offset < 0) throw new ArrayIndexOutOfBoundsException("Offset (" + offset + ") is negative");
			if (offset + length > a.length) throw new ArrayIndexOutOfBoundsException("End index (" + (offset + length) + ") is greater than array length (" + a.length + ")");
			if (from + length > size()) throw new IndexOutOfBoundsException("End index (" + (from + length) + ") is greater than list size (" + size() + ")");
			// Should be from == 0
			if (length <= 0) return;
			a[offset] = element;
		}

		@Override
		public void removeElements(int from, int to) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void addElements(int index, short a[]) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void addElements(int index, short a[], int offset, int length) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setElements(final short a[]) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setElements(int index, final short a[]) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setElements(int index, final short a[], int offset, int length) {
			throw new UnsupportedOperationException();
		}

		@Override
		public int size() {
			return 1;
		}

		@Override
		public void size(final int size) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void clear() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Object clone() {
			return this;
		}
	}

	/**
	 * Returns a type-specific immutable list containing only the specified element. The returned list
	 * is serializable and cloneable.
	 *
	 * @param element the only element of the returned list.
	 * @return a type-specific immutable list containing just {@code element}.
	 */
	public static ShortList singleton(final short element) {
		return new Singleton(element);
	}

	/**
	 * Returns a type-specific immutable list containing only the specified element. The returned list
	 * is serializable and cloneable.
	 *
	 * @param element the only element of the returned list.
	 * @return a type-specific immutable list containing just {@code element}.
	 */
	public static ShortList singleton(final Object element) {
		return new Singleton(((Short)(element)).shortValue());
	}

	/** A synchronized wrapper class for lists. */
	public static class SynchronizedList extends ShortCollections.SynchronizedCollection implements ShortList, java.io.Serializable {
		private static final long serialVersionUID = -7046029254386353129L;
		protected final ShortList list; // Due to the large number of methods that are not in COLLECTION, this is worth
										// caching.

		protected SynchronizedList(final ShortList l, final Object sync) {
			super(l, sync);
			this.list = l;
		}

		protected SynchronizedList(final ShortList l) {
			super(l);
			this.list = l;
		}

		@Override
		public short getShort(final int i) {
			synchronized (sync) {
				return list.getShort(i);
			}
		}

		@Override
		public short set(final int i, final short k) {
			synchronized (sync) {
				return list.set(i, k);
			}
		}

		@Override
		public void add(final int i, final short k) {
			synchronized (sync) {
				list.add(i, k);
			}
		}

		@Override
		public short removeShort(final int i) {
			synchronized (sync) {
				return list.removeShort(i);
			}
		}

		@Override
		public int indexOf(final short k) {
			synchronized (sync) {
				return list.indexOf(k);
			}
		}

		@Override
		public int lastIndexOf(final short k) {
			synchronized (sync) {
				return list.lastIndexOf(k);
			}
		}

		@Override
		public boolean removeIf(final ShortPredicate filter) {
			synchronized (sync) {
				return list.removeIf(filter);
			}
		}

		@Override
		public void replaceAll(final ShortUnaryOperator operator) {
			synchronized (sync) {
				list.replaceAll(operator);
			}
		}

		@Override
		public boolean addAll(final int index, final Collection<? extends Short> c) {
			synchronized (sync) {
				return list.addAll(index, c);
			}
		}

		@Override
		public void getElements(final int from, final short a[], final int offset, final int length) {
			synchronized (sync) {
				list.getElements(from, a, offset, length);
			}
		}

		@Override
		public void removeElements(final int from, final int to) {
			synchronized (sync) {
				list.removeElements(from, to);
			}
		}

		@Override
		public void addElements(int index, final short a[], int offset, int length) {
			synchronized (sync) {
				list.addElements(index, a, offset, length);
			}
		}

		@Override
		public void addElements(int index, final short a[]) {
			synchronized (sync) {
				list.addElements(index, a);
			}
		}

		@Override
		public void setElements(final short a[]) {
			synchronized (sync) {
				list.setElements(a);
			}
		}

		@Override
		public void setElements(int index, final short a[]) {
			synchronized (sync) {
				list.setElements(index, a);
			}
		}

		@Override
		public void setElements(int index, final short a[], int offset, int length) {
			synchronized (sync) {
				list.setElements(index, a, offset, length);
			}
		}

		@Override
		public void size(final int size) {
			synchronized (sync) {
				list.size(size);
			}
		}

		@Override
		public ShortListIterator listIterator() {
			return list.listIterator();
		}

		@Override
		public ShortListIterator iterator() {
			return listIterator();
		}

		@Override
		public ShortListIterator listIterator(final int i) {
			return list.listIterator(i);
		}

		@Override
		public ShortList subList(final int from, final int to) {
			synchronized (sync) {
				return new SynchronizedList(list.subList(from, to), sync);
			}
		}

		@Override
		public boolean equals(final Object o) {
			if (o == this) return true;
			synchronized (sync) {
				return collection.equals(o);
			}
		}

		@Override
		public int hashCode() {
			synchronized (sync) {
				return collection.hashCode();
			}
		}

		@Override
		public int compareTo(final List<? extends Short> o) {
			synchronized (sync) {
				return list.compareTo(o);
			}
		}

		@Override
		public boolean addAll(final int index, final ShortCollection c) {
			synchronized (sync) {
				return list.addAll(index, c);
			}
		}

		@Override
		public boolean addAll(final int index, ShortList l) {
			synchronized (sync) {
				return list.addAll(index, l);
			}
		}

		@Override
		public boolean addAll(ShortList l) {
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
		public Short get(final int i) {
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
		public void add(final int i, Short k) {
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
		public Short set(final int index, Short k) {
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
		public Short remove(final int i) {
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
		public int indexOf(final Object o) {
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
		public int lastIndexOf(final Object o) {
			synchronized (sync) {
				return list.lastIndexOf(o);
			}
		}

		@Override
		public void sort(final ShortComparator comparator) {
			synchronized (sync) {
				list.sort(comparator);
			}
		}

		@Override
		public void unstableSort(final ShortComparator comparator) {
			synchronized (sync) {
				list.unstableSort(comparator);
			}
		}

		@Deprecated
		@Override
		public void sort(final java.util.Comparator<? super Short> comparator) {
			synchronized (sync) {
				list.sort(comparator);
			}
		}

		@Deprecated
		@Override
		public void unstableSort(final java.util.Comparator<? super Short> comparator) {
			synchronized (sync) {
				list.unstableSort(comparator);
			}
		}

		private void writeObject(java.io.ObjectOutputStream s) throws java.io.IOException {
			synchronized (sync) {
				s.defaultWriteObject();
			}
		}
	}

	/** A synchronized wrapper class for random-access lists. */
	public static class SynchronizedRandomAccessList extends SynchronizedList implements RandomAccess, java.io.Serializable {
		private static final long serialVersionUID = 0L;

		protected SynchronizedRandomAccessList(final ShortList l, final Object sync) {
			super(l, sync);
		}

		protected SynchronizedRandomAccessList(final ShortList l) {
			super(l);
		}

		@Override
		public ShortList subList(final int from, final int to) {
			synchronized (sync) {
				return new SynchronizedRandomAccessList(list.subList(from, to), sync);
			}
		}
	}

	/**
	 * Returns a synchronized type-specific list backed by the given type-specific list.
	 *
	 * @param l the list to be wrapped in a synchronized list.
	 * @return a synchronized view of the specified list.
	 * @see java.util.Collections#synchronizedList(List)
	 */
	public static ShortList synchronize(final ShortList l) {
		return l instanceof RandomAccess ? new SynchronizedRandomAccessList(l) : new SynchronizedList(l);
	}

	/**
	 * Returns a synchronized type-specific list backed by the given type-specific list, using an
	 * assigned object to synchronize.
	 *
	 * @param l the list to be wrapped in a synchronized list.
	 * @param sync an object that will be used to synchronize the access to the list.
	 * @return a synchronized view of the specified list.
	 * @see java.util.Collections#synchronizedList(List)
	 */
	public static ShortList synchronize(final ShortList l, final Object sync) {
		return l instanceof RandomAccess ? new SynchronizedRandomAccessList(l, sync) : new SynchronizedList(l, sync);
	}

	/** An unmodifiable wrapper class for lists. */
	public static class UnmodifiableList extends ShortCollections.UnmodifiableCollection implements ShortList, java.io.Serializable {
		private static final long serialVersionUID = -7046029254386353129L;
		protected final ShortList list; // Due to the large number of methods that are not in COLLECTION, this is worth
										// caching.

		protected UnmodifiableList(final ShortList l) {
			super(l);
			this.list = l;
		}

		@Override
		public short getShort(final int i) {
			return list.getShort(i);
		}

		@Override
		public short set(final int i, final short k) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void add(final int i, final short k) {
			throw new UnsupportedOperationException();
		}

		@Override
		public short removeShort(final int i) {
			throw new UnsupportedOperationException();
		}

		@Override
		public int indexOf(final short k) {
			return list.indexOf(k);
		}

		@Override
		public int lastIndexOf(final short k) {
			return list.lastIndexOf(k);
		}

		@Override
		public boolean addAll(final int index, final Collection<? extends Short> c) {
			throw new UnsupportedOperationException();
		}

		@Deprecated
		@Override
		public void replaceAll(final java.util.function.UnaryOperator<Short> operator) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void getElements(final int from, final short a[], final int offset, final int length) {
			list.getElements(from, a, offset, length);
		}

		@Override
		public void removeElements(final int from, final int to) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void addElements(int index, final short a[], int offset, int length) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void addElements(int index, final short a[]) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setElements(final short a[]) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setElements(int index, final short a[]) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setElements(int index, final short a[], int offset, int length) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void size(final int size) {
			list.size(size);
		}

		@Override
		public ShortListIterator listIterator() {
			return ShortIterators.unmodifiable(list.listIterator());
		}

		@Override
		public ShortListIterator iterator() {
			return listIterator();
		}

		@Override
		public ShortListIterator listIterator(final int i) {
			return ShortIterators.unmodifiable(list.listIterator(i));
		}

		@Override
		public ShortList subList(final int from, final int to) {
			return new UnmodifiableList(list.subList(from, to));
		}

		@Override
		public boolean equals(final Object o) {
			if (o == this) return true;
			return collection.equals(o);
		}

		@Override
		public int hashCode() {
			return collection.hashCode();
		}

		@Override
		public int compareTo(final List<? extends Short> o) {
			return list.compareTo(o);
		}

		@Override
		public boolean addAll(final int index, final ShortCollection c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(final ShortList l) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(final int index, final ShortList l) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void replaceAll(final java.util.function.IntUnaryOperator operator) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public Short get(final int i) {
			return list.get(i);
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public void add(final int i, Short k) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public Short set(final int index, Short k) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public Short remove(final int i) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public int indexOf(final Object o) {
			return list.indexOf(o);
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public int lastIndexOf(final Object o) {
			return list.lastIndexOf(o);
		}

		@Override
		public void sort(final ShortComparator comparator) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void unstableSort(final ShortComparator comparator) {
			throw new UnsupportedOperationException();
		}

		@Deprecated
		@Override
		public void sort(final java.util.Comparator<? super Short> comparator) {
			throw new UnsupportedOperationException();
		}

		@Deprecated
		@Override
		public void unstableSort(final java.util.Comparator<? super Short> comparator) {
			throw new UnsupportedOperationException();
		}
	}

	/** An unmodifiable wrapper class for random-access lists. */
	public static class UnmodifiableRandomAccessList extends UnmodifiableList implements RandomAccess, java.io.Serializable {
		private static final long serialVersionUID = 0L;

		protected UnmodifiableRandomAccessList(final ShortList l) {
			super(l);
		}

		@Override
		public ShortList subList(final int from, final int to) {
			return new UnmodifiableRandomAccessList(list.subList(from, to));
		}
	}

	/**
	 * Returns an unmodifiable type-specific list backed by the given type-specific list.
	 *
	 * @param l the list to be wrapped in an unmodifiable list.
	 * @return an unmodifiable view of the specified list.
	 * @see java.util.Collections#unmodifiableList(List)
	 */
	public static ShortList unmodifiable(final ShortList l) {
		return l instanceof RandomAccess ? new UnmodifiableRandomAccessList(l) : new UnmodifiableList(l);
	}

	/** A stub class making all known mutation methods throw {@link UnsupportedOperationException}. */
	static abstract class ImmutableListBase extends AbstractShortList implements ShortList {
		/**
		 * @implSpec Always throws {@link UnsupportedOperationException} as this is an immutable type.
		 *
		 * @deprecated
		 */
		@Override
		@Deprecated
		public final void add(final int index, final short k) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @implSpec Always throws {@link UnsupportedOperationException} as this is an immutable type.
		 *
		 * @deprecated
		 */
		@Override
		@Deprecated
		public final boolean add(final short k) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @implSpec Always throws {@link UnsupportedOperationException} as this is an immutable type.
		 *
		 * @deprecated
		 */
		@Override
		@Deprecated
		public final boolean addAll(final java.util.Collection<? extends Short> c) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @implSpec Always throws {@link UnsupportedOperationException} as this is an immutable type.
		 *
		 * @deprecated
		 */
		@Override
		@Deprecated
		public final boolean addAll(int index, final java.util.Collection<? extends Short> c) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @implSpec Always throws {@link UnsupportedOperationException} as this is an immutable type.
		 *
		 * @deprecated
		 */
		@Override
		@Deprecated
		public final short removeShort(final int index) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @implSpec Always throws {@link UnsupportedOperationException} as this is an immutable type.
		 *
		 * @deprecated
		 */
		@Override
		@Deprecated
		public final boolean rem(final short k) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @implSpec Always throws {@link UnsupportedOperationException} as this is an immutable type.
		 *
		 * @deprecated
		 */
		@Override
		@Deprecated
		public final boolean removeAll(final java.util.Collection<?> c) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @implSpec Always throws {@link UnsupportedOperationException} as this is an immutable type.
		 *
		 * @deprecated
		 */
		@Override
		@Deprecated
		public final boolean retainAll(final java.util.Collection<?> c) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @implSpec Always throws {@link UnsupportedOperationException} as this is an immutable type.
		 *
		 * @deprecated
		 */
		@Override
		@Deprecated
		public final boolean removeIf(final java.util.function.Predicate<? super Short> c) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @implSpec Always throws {@link UnsupportedOperationException} as this is an immutable type.
		 *
		 * @deprecated
		 */
		@Override
		@Deprecated
		public final boolean removeIf(final ShortPredicate c) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @implSpec Always throws {@link UnsupportedOperationException} as this is an immutable type.
		 *
		 * @deprecated
		 */
		@Override
		@Deprecated
		public final void replaceAll(final java.util.function.UnaryOperator<Short> operator) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @implSpec Always throws {@link UnsupportedOperationException} as this is an immutable type.
		 *
		 * @deprecated
		 */
		@Override
		@Deprecated
		public final void replaceAll(final java.util.function.IntUnaryOperator operator) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @implSpec Always throws {@link UnsupportedOperationException} as this is an immutable type.
		 *
		 * @deprecated
		 */
		@Override
		@Deprecated
		public final void add(final int index, final Short k) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @implSpec Always throws {@link UnsupportedOperationException} as this is an immutable type.
		 *
		 * @deprecated
		 */
		@Override
		@Deprecated
		public final boolean add(final Short k) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @implSpec Always throws {@link UnsupportedOperationException} as this is an immutable type.
		 *
		 * @deprecated
		 */
		@Override
		@Deprecated
		public final Short remove(final int index) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @implSpec Always throws {@link UnsupportedOperationException} as this is an immutable type.
		 *
		 * @deprecated
		 */
		@Override
		@Deprecated
		public final boolean remove(final Object k) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @implSpec Always throws {@link UnsupportedOperationException} as this is an immutable type.
		 *
		 * @deprecated
		 */
		@Override
		@Deprecated
		public final Short set(final int index, final Short k) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @implSpec Always throws {@link UnsupportedOperationException} as this is an immutable type.
		 *
		 * @deprecated
		 */
		@Override
		@Deprecated
		public final boolean addAll(final ShortCollection c) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @implSpec Always throws {@link UnsupportedOperationException} as this is an immutable type.
		 *
		 * @deprecated
		 */
		@Override
		@Deprecated
		public final boolean addAll(final ShortList c) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @implSpec Always throws {@link UnsupportedOperationException} as this is an immutable type.
		 *
		 * @deprecated
		 */
		@Override
		@Deprecated
		public final boolean addAll(final int index, final ShortCollection c) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @implSpec Always throws {@link UnsupportedOperationException} as this is an immutable type.
		 *
		 * @deprecated
		 */
		@Override
		@Deprecated
		public final boolean addAll(final int index, final ShortList c) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @implSpec Always throws {@link UnsupportedOperationException} as this is an immutable type.
		 *
		 * @deprecated
		 */
		@Override
		@Deprecated
		public final boolean removeAll(final ShortCollection c) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @implSpec Always throws {@link UnsupportedOperationException} as this is an immutable type.
		 *
		 * @deprecated
		 */
		@Override
		@Deprecated
		public final boolean retainAll(final ShortCollection c) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @implSpec Always throws {@link UnsupportedOperationException} as this is an immutable type.
		 *
		 * @deprecated
		 */
		@Override
		@Deprecated
		public final short set(final int index, final short k) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @implSpec Always throws {@link UnsupportedOperationException} as this is an immutable type.
		 *
		 * @deprecated
		 */
		@Override
		@Deprecated
		public final void clear() {
			throw new UnsupportedOperationException();
		}

		/**
		 * @implSpec Always throws {@link UnsupportedOperationException} as this is an immutable type.
		 *
		 * @deprecated
		 */
		@Override
		@Deprecated
		public final void size(final int size) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @implSpec Always throws {@link UnsupportedOperationException} as this is an immutable type.
		 *
		 * @deprecated
		 */
		@Override
		@Deprecated
		public final void removeElements(final int from, final int to) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @implSpec Always throws {@link UnsupportedOperationException} as this is an immutable type.
		 *
		 * @deprecated
		 */
		@Override
		@Deprecated
		public final void addElements(final int index, final short a[], final int offset, final int length) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @implSpec Always throws {@link UnsupportedOperationException} as this is an immutable type.
		 *
		 * @deprecated
		 */
		@Override
		@Deprecated
		public final void setElements(final int index, final short a[], final int offset, final int length) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @implSpec Always throws {@link UnsupportedOperationException} as this is an immutable type.
		 *
		 * @deprecated
		 */
		@Override
		@Deprecated
		public final void sort(final ShortComparator comp) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @implSpec Always throws {@link UnsupportedOperationException} as this is an immutable type.
		 *
		 * @deprecated
		 */
		@Override
		@Deprecated
		public final void unstableSort(final ShortComparator comp) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @implSpec Always throws {@link UnsupportedOperationException} as this is an immutable type.
		 *
		 * @deprecated
		 */
		@Override
		@Deprecated
		public final void sort(final java.util.Comparator<? super Short> comparator) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @implSpec Always throws {@link UnsupportedOperationException} as this is an immutable type.
		 *
		 * @deprecated
		 */
		@Override
		@Deprecated
		public final void unstableSort(final java.util.Comparator<? super Short> comparator) {
			throw new UnsupportedOperationException();
		}
	}
}
