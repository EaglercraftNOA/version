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
package it.unimi.dsi.fastutil.booleans;

import java.util.List;
import java.util.Collection;
import java.util.Random;
import java.util.RandomAccess;
import java.util.function.Consumer;

/**
 * A class providing static methods and objects that do useful things with type-specific lists.
 *
 * @see java.util.Collections
 */
public final class BooleanLists {
	/* Only in the EmptyList and Singleton classes, where performance is critical, do we override
	 * the deprecated, Object based functional methods. For the rest, we just override the
	 * non-deprecated type-specific method, and let the default method from the interface
	 * filter into that. This is an extra method call and lambda creation, but it isn't worth
	 * complexifying the code generation for a case that is already marked as being inefficient.
	 */
	private BooleanLists() {
	}

	/**
	 * Shuffles the specified list using the specified pseudorandom number generator.
	 *
	 * @param l the list to be shuffled.
	 * @param random a pseudorandom number generator.
	 * @return {@code l}.
	 */
	public static BooleanList shuffle(final BooleanList l, final Random random) {
		for (int i = l.size(); i-- != 0;) {
			final int p = random.nextInt(i + 1);
			final boolean t = l.getBoolean(i);
			l.set(i, l.getBoolean(p));
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
	public static class EmptyList extends BooleanCollections.EmptyCollection implements BooleanList, RandomAccess, java.io.Serializable, Cloneable {
		private static final long serialVersionUID = -7046029254386353129L;

		protected EmptyList() {
		}

		@Override
		public boolean getBoolean(int i) {
			throw new IndexOutOfBoundsException();
		}

		@Override
		public boolean rem(boolean k) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean removeBoolean(int i) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void add(final int index, final boolean k) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean set(final int index, final boolean k) {
			throw new UnsupportedOperationException();
		}

		@Override
		public int indexOf(boolean k) {
			return -1;
		}

		@Override
		public int lastIndexOf(boolean k) {
			return -1;
		}

		@Override
		public boolean addAll(int i, Collection<? extends Boolean> c) {
			throw new UnsupportedOperationException();
		}

		@Deprecated
		@Override
		public void replaceAll(final java.util.function.UnaryOperator<Boolean> operator) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void replaceAll(final BooleanUnaryOperator operator) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(BooleanList c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(int i, BooleanCollection c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(int i, BooleanList c) {
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
		public void add(final int index, final Boolean k) {
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
		public Boolean get(final int index) {
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
		public boolean add(final Boolean k) {
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
		public Boolean set(final int index, final Boolean k) {
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
		public Boolean remove(int k) {
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
		public void sort(final BooleanComparator comparator) {
		}

		@Override
		public void unstableSort(final BooleanComparator comparator) {
		}

		// Empty lists are trivially always sorted
		@Deprecated
		@Override
		public void sort(final java.util.Comparator<? super Boolean> comparator) {
		}

		@Deprecated
		@Override
		public void unstableSort(final java.util.Comparator<? super Boolean> comparator) {
		}

		@Override
		public BooleanListIterator listIterator() {
			return BooleanIterators.EMPTY_ITERATOR;
		}

		@Override
		public BooleanListIterator iterator() {
			return BooleanIterators.EMPTY_ITERATOR;
		}

		@Override
		public BooleanListIterator listIterator(int i) {
			if (i == 0) return BooleanIterators.EMPTY_ITERATOR;
			throw new IndexOutOfBoundsException(String.valueOf(i));
		}

		@Override
		public BooleanList subList(int from, int to) {
			if (from == 0 && to == 0) return this;
			throw new IndexOutOfBoundsException();
		}

		@Override
		public void getElements(int from, boolean[] a, int offset, int length) {
			if (from == 0 && length == 0 && offset >= 0 && offset <= a.length) return;
			throw new IndexOutOfBoundsException();
		}

		@Override
		public void removeElements(int from, int to) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void addElements(int index, final boolean a[], int offset, int length) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void addElements(int index, final boolean a[]) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setElements(final boolean a[]) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setElements(int index, final boolean a[]) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setElements(int index, final boolean a[], int offset, int length) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void size(int s) {
			throw new UnsupportedOperationException();
		}

		@Override
		public int compareTo(final List<? extends Boolean> o) {
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
	public static BooleanList emptyList() {
		return EMPTY_LIST;
	}

	/**
	 * An immutable class representing a type-specific singleton list.
	 *
	 * <p>
	 * This class may be useful to implement your own in case you subclass a type-specific list.
	 */
	public static class Singleton extends AbstractBooleanList implements RandomAccess, java.io.Serializable, Cloneable {
		private static final long serialVersionUID = -7046029254386353129L;
		private final boolean element;

		protected Singleton(final boolean element) {
			this.element = element;
		}

		@Override
		public boolean getBoolean(final int i) {
			if (i == 0) return element;
			throw new IndexOutOfBoundsException();
		}

		@Override
		public boolean rem(boolean k) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean removeBoolean(final int i) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean contains(final boolean k) {
			return ((k) == (element));
		}

		@Override
		public int indexOf(final boolean k) {
			return ((k) == (element)) ? 0 : -1;
		}

		/* Slightly optimized w.r.t. the one in ABSTRACT_SET. */
		@Override
		public boolean[] toBooleanArray() {
			return new boolean[] { element };
		}

		@Override
		public BooleanListIterator listIterator() {
			return BooleanIterators.singleton(element);
		}

		@Override
		public BooleanListIterator iterator() {
			return listIterator();
		}

		@Override
		public BooleanSpliterator spliterator() {
			return BooleanSpliterators.singleton(element);
		}

		@Override
		public BooleanListIterator listIterator(final int i) {
			if (i > 1 || i < 0) throw new IndexOutOfBoundsException();
			final BooleanListIterator l = listIterator();
			if (i == 1) l.nextBoolean();
			return l;
		}

		@Override

		public BooleanList subList(final int from, final int to) {
			ensureIndex(from);
			ensureIndex(to);
			if (from > to) throw new IndexOutOfBoundsException("Start index (" + from + ") is greater than end index (" + to + ")");
			if (from != 0 || to != 1) return EMPTY_LIST;
			return this;
		}

		@Deprecated
		@Override
		public void forEach(final Consumer<? super Boolean> action) {
			action.accept(Boolean.valueOf(element));
		}

		@Override
		public boolean addAll(int i, Collection<? extends Boolean> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(final Collection<? extends Boolean> c) {
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
		public boolean removeIf(final java.util.function.Predicate<? super Boolean> filter) {
			throw new UnsupportedOperationException();
		}

		@Deprecated
		@Override
		public void replaceAll(final java.util.function.UnaryOperator<Boolean> operator) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void replaceAll(final BooleanUnaryOperator operator) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void forEach(final BooleanConsumer action) {
			action.accept(element);
		}

		@Override
		public boolean addAll(BooleanList c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(int i, BooleanList c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(int i, BooleanCollection c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(final BooleanCollection c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean removeAll(final BooleanCollection c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean retainAll(final BooleanCollection c) {
			throw new UnsupportedOperationException();
		}

		@Deprecated
		@Override
		public Object[] toArray() {
			return new Object[] { Boolean.valueOf(element) };
		}

		// Lists of size 1 are trivially always sorted
		@Override
		public void sort(final BooleanComparator comparator) {
		}

		@Override
		public void unstableSort(final BooleanComparator comparator) {
		}

		// Lists of size 1 are trivially always sorted
		@Deprecated
		@Override
		public void sort(final java.util.Comparator<? super Boolean> comparator) {
		}

		@Deprecated
		@Override
		public void unstableSort(final java.util.Comparator<? super Boolean> comparator) {
		}

		@Override
		public void getElements(int from, boolean a[], int offset, int length) {
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
		public void addElements(int index, boolean a[]) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void addElements(int index, boolean a[], int offset, int length) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setElements(final boolean a[]) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setElements(int index, final boolean a[]) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setElements(int index, final boolean a[], int offset, int length) {
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
	public static BooleanList singleton(final boolean element) {
		return new Singleton(element);
	}

	/**
	 * Returns a type-specific immutable list containing only the specified element. The returned list
	 * is serializable and cloneable.
	 *
	 * @param element the only element of the returned list.
	 * @return a type-specific immutable list containing just {@code element}.
	 */
	public static BooleanList singleton(final Object element) {
		return new Singleton(((Boolean)(element)).booleanValue());
	}

	/** A synchronized wrapper class for lists. */
	public static class SynchronizedList extends BooleanCollections.SynchronizedCollection implements BooleanList, java.io.Serializable {
		private static final long serialVersionUID = -7046029254386353129L;
		protected final BooleanList list; // Due to the large number of methods that are not in COLLECTION, this is
											// worth caching.

		protected SynchronizedList(final BooleanList l, final Object sync) {
			super(l, sync);
			this.list = l;
		}

		protected SynchronizedList(final BooleanList l) {
			super(l);
			this.list = l;
		}

		@Override
		public boolean getBoolean(final int i) {
			synchronized (sync) {
				return list.getBoolean(i);
			}
		}

		@Override
		public boolean set(final int i, final boolean k) {
			synchronized (sync) {
				return list.set(i, k);
			}
		}

		@Override
		public void add(final int i, final boolean k) {
			synchronized (sync) {
				list.add(i, k);
			}
		}

		@Override
		public boolean removeBoolean(final int i) {
			synchronized (sync) {
				return list.removeBoolean(i);
			}
		}

		@Override
		public int indexOf(final boolean k) {
			synchronized (sync) {
				return list.indexOf(k);
			}
		}

		@Override
		public int lastIndexOf(final boolean k) {
			synchronized (sync) {
				return list.lastIndexOf(k);
			}
		}

		@Override
		public boolean removeIf(final BooleanPredicate filter) {
			synchronized (sync) {
				return list.removeIf(filter);
			}
		}

		@Override
		public void replaceAll(final BooleanUnaryOperator operator) {
			synchronized (sync) {
				list.replaceAll(operator);
			}
		}

		@Override
		public boolean addAll(final int index, final Collection<? extends Boolean> c) {
			synchronized (sync) {
				return list.addAll(index, c);
			}
		}

		@Override
		public void getElements(final int from, final boolean a[], final int offset, final int length) {
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
		public void addElements(int index, final boolean a[], int offset, int length) {
			synchronized (sync) {
				list.addElements(index, a, offset, length);
			}
		}

		@Override
		public void addElements(int index, final boolean a[]) {
			synchronized (sync) {
				list.addElements(index, a);
			}
		}

		@Override
		public void setElements(final boolean a[]) {
			synchronized (sync) {
				list.setElements(a);
			}
		}

		@Override
		public void setElements(int index, final boolean a[]) {
			synchronized (sync) {
				list.setElements(index, a);
			}
		}

		@Override
		public void setElements(int index, final boolean a[], int offset, int length) {
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
		public BooleanListIterator listIterator() {
			return list.listIterator();
		}

		@Override
		public BooleanListIterator iterator() {
			return listIterator();
		}

		@Override
		public BooleanListIterator listIterator(final int i) {
			return list.listIterator(i);
		}

		@Override
		public BooleanList subList(final int from, final int to) {
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
		public int compareTo(final List<? extends Boolean> o) {
			synchronized (sync) {
				return list.compareTo(o);
			}
		}

		@Override
		public boolean addAll(final int index, final BooleanCollection c) {
			synchronized (sync) {
				return list.addAll(index, c);
			}
		}

		@Override
		public boolean addAll(final int index, BooleanList l) {
			synchronized (sync) {
				return list.addAll(index, l);
			}
		}

		@Override
		public boolean addAll(BooleanList l) {
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
		public Boolean get(final int i) {
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
		public void add(final int i, Boolean k) {
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
		public Boolean set(final int index, Boolean k) {
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
		public Boolean remove(final int i) {
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
		public void sort(final BooleanComparator comparator) {
			synchronized (sync) {
				list.sort(comparator);
			}
		}

		@Override
		public void unstableSort(final BooleanComparator comparator) {
			synchronized (sync) {
				list.unstableSort(comparator);
			}
		}

		@Deprecated
		@Override
		public void sort(final java.util.Comparator<? super Boolean> comparator) {
			synchronized (sync) {
				list.sort(comparator);
			}
		}

		@Deprecated
		@Override
		public void unstableSort(final java.util.Comparator<? super Boolean> comparator) {
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

		protected SynchronizedRandomAccessList(final BooleanList l, final Object sync) {
			super(l, sync);
		}

		protected SynchronizedRandomAccessList(final BooleanList l) {
			super(l);
		}

		@Override
		public BooleanList subList(final int from, final int to) {
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
	public static BooleanList synchronize(final BooleanList l) {
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
	public static BooleanList synchronize(final BooleanList l, final Object sync) {
		return l instanceof RandomAccess ? new SynchronizedRandomAccessList(l, sync) : new SynchronizedList(l, sync);
	}

	/** An unmodifiable wrapper class for lists. */
	public static class UnmodifiableList extends BooleanCollections.UnmodifiableCollection implements BooleanList, java.io.Serializable {
		private static final long serialVersionUID = -7046029254386353129L;
		protected final BooleanList list; // Due to the large number of methods that are not in COLLECTION, this is
											// worth caching.

		protected UnmodifiableList(final BooleanList l) {
			super(l);
			this.list = l;
		}

		@Override
		public boolean getBoolean(final int i) {
			return list.getBoolean(i);
		}

		@Override
		public boolean set(final int i, final boolean k) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void add(final int i, final boolean k) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean removeBoolean(final int i) {
			throw new UnsupportedOperationException();
		}

		@Override
		public int indexOf(final boolean k) {
			return list.indexOf(k);
		}

		@Override
		public int lastIndexOf(final boolean k) {
			return list.lastIndexOf(k);
		}

		@Override
		public boolean addAll(final int index, final Collection<? extends Boolean> c) {
			throw new UnsupportedOperationException();
		}

		@Deprecated
		@Override
		public void replaceAll(final java.util.function.UnaryOperator<Boolean> operator) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void getElements(final int from, final boolean a[], final int offset, final int length) {
			list.getElements(from, a, offset, length);
		}

		@Override
		public void removeElements(final int from, final int to) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void addElements(int index, final boolean a[], int offset, int length) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void addElements(int index, final boolean a[]) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setElements(final boolean a[]) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setElements(int index, final boolean a[]) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setElements(int index, final boolean a[], int offset, int length) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void size(final int size) {
			list.size(size);
		}

		@Override
		public BooleanListIterator listIterator() {
			return BooleanIterators.unmodifiable(list.listIterator());
		}

		@Override
		public BooleanListIterator iterator() {
			return listIterator();
		}

		@Override
		public BooleanListIterator listIterator(final int i) {
			return BooleanIterators.unmodifiable(list.listIterator(i));
		}

		@Override
		public BooleanList subList(final int from, final int to) {
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
		public int compareTo(final List<? extends Boolean> o) {
			return list.compareTo(o);
		}

		@Override
		public boolean addAll(final int index, final BooleanCollection c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(final BooleanList l) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(final int index, final BooleanList l) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public Boolean get(final int i) {
			return list.get(i);
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public void add(final int i, Boolean k) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public Boolean set(final int index, Boolean k) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public Boolean remove(final int i) {
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
		public void sort(final BooleanComparator comparator) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void unstableSort(final BooleanComparator comparator) {
			throw new UnsupportedOperationException();
		}

		@Deprecated
		@Override
		public void sort(final java.util.Comparator<? super Boolean> comparator) {
			throw new UnsupportedOperationException();
		}

		@Deprecated
		@Override
		public void unstableSort(final java.util.Comparator<? super Boolean> comparator) {
			throw new UnsupportedOperationException();
		}
	}

	/** An unmodifiable wrapper class for random-access lists. */
	public static class UnmodifiableRandomAccessList extends UnmodifiableList implements RandomAccess, java.io.Serializable {
		private static final long serialVersionUID = 0L;

		protected UnmodifiableRandomAccessList(final BooleanList l) {
			super(l);
		}

		@Override
		public BooleanList subList(final int from, final int to) {
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
	public static BooleanList unmodifiable(final BooleanList l) {
		return l instanceof RandomAccess ? new UnmodifiableRandomAccessList(l) : new UnmodifiableList(l);
	}

	/** A stub class making all known mutation methods throw {@link UnsupportedOperationException}. */
	static abstract class ImmutableListBase extends AbstractBooleanList implements BooleanList {
		/**
		 * @implSpec Always throws {@link UnsupportedOperationException} as this is an immutable type.
		 *
		 * @deprecated
		 */
		@Override
		@Deprecated
		public final void add(final int index, final boolean k) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @implSpec Always throws {@link UnsupportedOperationException} as this is an immutable type.
		 *
		 * @deprecated
		 */
		@Override
		@Deprecated
		public final boolean add(final boolean k) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @implSpec Always throws {@link UnsupportedOperationException} as this is an immutable type.
		 *
		 * @deprecated
		 */
		@Override
		@Deprecated
		public final boolean addAll(final java.util.Collection<? extends Boolean> c) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @implSpec Always throws {@link UnsupportedOperationException} as this is an immutable type.
		 *
		 * @deprecated
		 */
		@Override
		@Deprecated
		public final boolean addAll(int index, final java.util.Collection<? extends Boolean> c) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @implSpec Always throws {@link UnsupportedOperationException} as this is an immutable type.
		 *
		 * @deprecated
		 */
		@Override
		@Deprecated
		public final boolean removeBoolean(final int index) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @implSpec Always throws {@link UnsupportedOperationException} as this is an immutable type.
		 *
		 * @deprecated
		 */
		@Override
		@Deprecated
		public final boolean rem(final boolean k) {
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
		public final boolean removeIf(final java.util.function.Predicate<? super Boolean> c) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @implSpec Always throws {@link UnsupportedOperationException} as this is an immutable type.
		 *
		 * @deprecated
		 */
		@Override
		@Deprecated
		public final boolean removeIf(final BooleanPredicate c) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @implSpec Always throws {@link UnsupportedOperationException} as this is an immutable type.
		 *
		 * @deprecated
		 */
		@Override
		@Deprecated
		public final void replaceAll(final java.util.function.UnaryOperator<Boolean> operator) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @implSpec Always throws {@link UnsupportedOperationException} as this is an immutable type.
		 *
		 * @deprecated
		 */
		@Override
		@Deprecated
		public final void add(final int index, final Boolean k) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @implSpec Always throws {@link UnsupportedOperationException} as this is an immutable type.
		 *
		 * @deprecated
		 */
		@Override
		@Deprecated
		public final boolean add(final Boolean k) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @implSpec Always throws {@link UnsupportedOperationException} as this is an immutable type.
		 *
		 * @deprecated
		 */
		@Override
		@Deprecated
		public final Boolean remove(final int index) {
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
		public final Boolean set(final int index, final Boolean k) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @implSpec Always throws {@link UnsupportedOperationException} as this is an immutable type.
		 *
		 * @deprecated
		 */
		@Override
		@Deprecated
		public final boolean addAll(final BooleanCollection c) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @implSpec Always throws {@link UnsupportedOperationException} as this is an immutable type.
		 *
		 * @deprecated
		 */
		@Override
		@Deprecated
		public final boolean addAll(final BooleanList c) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @implSpec Always throws {@link UnsupportedOperationException} as this is an immutable type.
		 *
		 * @deprecated
		 */
		@Override
		@Deprecated
		public final boolean addAll(final int index, final BooleanCollection c) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @implSpec Always throws {@link UnsupportedOperationException} as this is an immutable type.
		 *
		 * @deprecated
		 */
		@Override
		@Deprecated
		public final boolean addAll(final int index, final BooleanList c) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @implSpec Always throws {@link UnsupportedOperationException} as this is an immutable type.
		 *
		 * @deprecated
		 */
		@Override
		@Deprecated
		public final boolean removeAll(final BooleanCollection c) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @implSpec Always throws {@link UnsupportedOperationException} as this is an immutable type.
		 *
		 * @deprecated
		 */
		@Override
		@Deprecated
		public final boolean retainAll(final BooleanCollection c) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @implSpec Always throws {@link UnsupportedOperationException} as this is an immutable type.
		 *
		 * @deprecated
		 */
		@Override
		@Deprecated
		public final boolean set(final int index, final boolean k) {
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
		public final void addElements(final int index, final boolean a[], final int offset, final int length) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @implSpec Always throws {@link UnsupportedOperationException} as this is an immutable type.
		 *
		 * @deprecated
		 */
		@Override
		@Deprecated
		public final void setElements(final int index, final boolean a[], final int offset, final int length) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @implSpec Always throws {@link UnsupportedOperationException} as this is an immutable type.
		 *
		 * @deprecated
		 */
		@Override
		@Deprecated
		public final void sort(final BooleanComparator comp) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @implSpec Always throws {@link UnsupportedOperationException} as this is an immutable type.
		 *
		 * @deprecated
		 */
		@Override
		@Deprecated
		public final void unstableSort(final BooleanComparator comp) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @implSpec Always throws {@link UnsupportedOperationException} as this is an immutable type.
		 *
		 * @deprecated
		 */
		@Override
		@Deprecated
		public final void sort(final java.util.Comparator<? super Boolean> comparator) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @implSpec Always throws {@link UnsupportedOperationException} as this is an immutable type.
		 *
		 * @deprecated
		 */
		@Override
		@Deprecated
		public final void unstableSort(final java.util.Comparator<? super Boolean> comparator) {
			throw new UnsupportedOperationException();
		}
	}
}
