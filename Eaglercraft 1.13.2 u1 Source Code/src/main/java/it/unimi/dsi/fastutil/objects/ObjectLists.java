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

import java.util.List;
import java.util.Collection;
import java.util.Random;
import java.util.RandomAccess;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

/**
 * A class providing static methods and objects that do useful things with type-specific lists.
 *
 * @see java.util.Collections
 */
public final class ObjectLists {
	private ObjectLists() {
	}

	/**
	 * Shuffles the specified list using the specified pseudorandom number generator.
	 *
	 * @param l the list to be shuffled.
	 * @param random a pseudorandom number generator.
	 * @return {@code l}.
	 */
	public static <K> ObjectList<K> shuffle(final ObjectList<K> l, final Random random) {
		for (int i = l.size(); i-- != 0;) {
			final int p = random.nextInt(i + 1);
			final K t = l.get(i);
			l.set(i, l.get(p));
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
	public static class EmptyList<K> extends ObjectCollections.EmptyCollection<K> implements ObjectList<K>, RandomAccess, java.io.Serializable, Cloneable {
		private static final long serialVersionUID = -7046029254386353129L;

		protected EmptyList() {
		}

		@Override
		public K get(int i) {
			throw new IndexOutOfBoundsException();
		}

		@Override
		public boolean remove(Object k) {
			throw new UnsupportedOperationException();
		}

		@Override
		public K remove(int i) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void add(final int index, final K k) {
			throw new UnsupportedOperationException();
		}

		@Override
		public K set(final int index, final K k) {
			throw new UnsupportedOperationException();
		}

		@Override
		public int indexOf(Object k) {
			return -1;
		}

		@Override
		public int lastIndexOf(Object k) {
			return -1;
		}

		@Override
		public boolean addAll(int i, Collection<? extends K> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void replaceAll(final java.util.function.UnaryOperator<K> operator) {
			throw new UnsupportedOperationException();
		}
		// Empty lists are trivially always sorted

		@Override
		public void sort(final java.util.Comparator<? super K> comparator) {
		}

		@Override
		public void unstableSort(final java.util.Comparator<? super K> comparator) {
		}

		@SuppressWarnings("unchecked")
		@Override
		public ObjectListIterator<K> listIterator() {
			return ObjectIterators.EMPTY_ITERATOR;
		}

		@SuppressWarnings("unchecked")
		@Override
		public ObjectListIterator<K> iterator() {
			return ObjectIterators.EMPTY_ITERATOR;
		}

		@SuppressWarnings("unchecked")
		@Override
		public ObjectListIterator<K> listIterator(int i) {
			if (i == 0) return ObjectIterators.EMPTY_ITERATOR;
			throw new IndexOutOfBoundsException(String.valueOf(i));
		}

		@Override
		public ObjectList<K> subList(int from, int to) {
			if (from == 0 && to == 0) return this;
			throw new IndexOutOfBoundsException();
		}

		@Override
		public void getElements(int from, Object[] a, int offset, int length) {
			if (from == 0 && length == 0 && offset >= 0 && offset <= a.length) return;
			throw new IndexOutOfBoundsException();
		}

		@Override
		public void removeElements(int from, int to) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void addElements(int index, final K a[], int offset, int length) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void addElements(int index, final K a[]) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setElements(final K a[]) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setElements(int index, final K a[]) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setElements(int index, final K a[], int offset, int length) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void size(int s) {
			throw new UnsupportedOperationException();
		}

		@Override
		public int compareTo(final List<? extends K> o) {
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
	@SuppressWarnings("rawtypes")
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
	public static <K> ObjectList<K> emptyList() {
		return EMPTY_LIST;
	}

	/**
	 * An immutable class representing a type-specific singleton list.
	 *
	 * <p>
	 * This class may be useful to implement your own in case you subclass a type-specific list.
	 */
	public static class Singleton<K> extends AbstractObjectList<K> implements RandomAccess, java.io.Serializable, Cloneable {
		private static final long serialVersionUID = -7046029254386353129L;
		private final K element;

		protected Singleton(final K element) {
			this.element = element;
		}

		@Override
		public K get(final int i) {
			if (i == 0) return element;
			throw new IndexOutOfBoundsException();
		}

		@Override
		public boolean remove(Object k) {
			throw new UnsupportedOperationException();
		}

		@Override
		public K remove(final int i) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean contains(final Object k) {
			return java.util.Objects.equals(k, element);
		}

		@Override
		public int indexOf(final Object k) {
			return java.util.Objects.equals(k, element) ? 0 : -1;
		}

		/* Slightly optimized w.r.t. the one in ABSTRACT_SET. */
		@Override
		public Object[] toArray() {
			return new Object[] { element };
		}

		@Override
		public ObjectListIterator<K> listIterator() {
			return ObjectIterators.singleton(element);
		}

		@Override
		public ObjectListIterator<K> iterator() {
			return listIterator();
		}

		@Override
		public ObjectSpliterator<K> spliterator() {
			return ObjectSpliterators.singleton(element);
		}

		@Override
		public ObjectListIterator<K> listIterator(final int i) {
			if (i > 1 || i < 0) throw new IndexOutOfBoundsException();
			final ObjectListIterator<K> l = listIterator();
			if (i == 1) l.next();
			return l;
		}

		@Override
		@SuppressWarnings("unchecked")
		public ObjectList<K> subList(final int from, final int to) {
			ensureIndex(from);
			ensureIndex(to);
			if (from > to) throw new IndexOutOfBoundsException("Start index (" + from + ") is greater than end index (" + to + ")");
			if (from != 0 || to != 1) return EMPTY_LIST;
			return this;
		}

		@Override
		public void forEach(final Consumer<? super K> action) {
			action.accept((element));
		}

		@Override
		public boolean addAll(int i, Collection<? extends K> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean addAll(final Collection<? extends K> c) {
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
		public boolean removeIf(final java.util.function.Predicate<? super K> filter) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void replaceAll(final java.util.function.UnaryOperator<K> operator) {
			throw new UnsupportedOperationException();
		}
		// Lists of size 1 are trivially always sorted

		@Override
		public void sort(final java.util.Comparator<? super K> comparator) {
		}

		@Override
		public void unstableSort(final java.util.Comparator<? super K> comparator) {
		}

		@Override
		public void getElements(int from, Object a[], int offset, int length) {
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
		public void addElements(int index, K a[]) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void addElements(int index, K a[], int offset, int length) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setElements(final K a[]) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setElements(int index, final K a[]) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setElements(int index, final K a[], int offset, int length) {
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
	public static <K> ObjectList<K> singleton(final K element) {
		return new Singleton<>(element);
	}

	/** A synchronized wrapper class for lists. */
	public static class SynchronizedList<K> extends ObjectCollections.SynchronizedCollection<K> implements ObjectList<K>, java.io.Serializable {
		private static final long serialVersionUID = -7046029254386353129L;
		protected final ObjectList<K> list; // Due to the large number of methods that are not in COLLECTION, this is
											// worth caching.

		protected SynchronizedList(final ObjectList<K> l, final Object sync) {
			super(l, sync);
			this.list = l;
		}

		protected SynchronizedList(final ObjectList<K> l) {
			super(l);
			this.list = l;
		}

		@Override
		public K get(final int i) {
			synchronized (sync) {
				return list.get(i);
			}
		}

		@Override
		public K set(final int i, final K k) {
			synchronized (sync) {
				return list.set(i, k);
			}
		}

		@Override
		public void add(final int i, final K k) {
			synchronized (sync) {
				list.add(i, k);
			}
		}

		@Override
		public K remove(final int i) {
			synchronized (sync) {
				return list.remove(i);
			}
		}

		@Override
		public int indexOf(final Object k) {
			synchronized (sync) {
				return list.indexOf(k);
			}
		}

		@Override
		public int lastIndexOf(final Object k) {
			synchronized (sync) {
				return list.lastIndexOf(k);
			}
		}

		@Override
		public boolean removeIf(final Predicate<? super K> filter) {
			synchronized (sync) {
				return list.removeIf(filter);
			}
		}

		@Override
		public void replaceAll(final UnaryOperator<K> operator) {
			synchronized (sync) {
				list.replaceAll(operator);
			}
		}

		@Override
		public boolean addAll(final int index, final Collection<? extends K> c) {
			synchronized (sync) {
				return list.addAll(index, c);
			}
		}

		@Override
		public void getElements(final int from, final Object a[], final int offset, final int length) {
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
		public void addElements(int index, final K a[], int offset, int length) {
			synchronized (sync) {
				list.addElements(index, a, offset, length);
			}
		}

		@Override
		public void addElements(int index, final K a[]) {
			synchronized (sync) {
				list.addElements(index, a);
			}
		}

		@Override
		public void setElements(final K a[]) {
			synchronized (sync) {
				list.setElements(a);
			}
		}

		@Override
		public void setElements(int index, final K a[]) {
			synchronized (sync) {
				list.setElements(index, a);
			}
		}

		@Override
		public void setElements(int index, final K a[], int offset, int length) {
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
		public ObjectListIterator<K> listIterator() {
			return list.listIterator();
		}

		@Override
		public ObjectListIterator<K> iterator() {
			return listIterator();
		}

		@Override
		public ObjectListIterator<K> listIterator(final int i) {
			return list.listIterator(i);
		}

		@Override
		public ObjectList<K> subList(final int from, final int to) {
			synchronized (sync) {
				return new SynchronizedList<>(list.subList(from, to), sync);
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
		public int compareTo(final List<? extends K> o) {
			synchronized (sync) {
				return list.compareTo(o);
			}
		}

		@Override
		public void sort(final java.util.Comparator<? super K> comparator) {
			synchronized (sync) {
				list.sort(comparator);
			}
		}

		@Override
		public void unstableSort(final java.util.Comparator<? super K> comparator) {
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
	public static class SynchronizedRandomAccessList<K> extends SynchronizedList<K> implements RandomAccess, java.io.Serializable {
		private static final long serialVersionUID = 0L;

		protected SynchronizedRandomAccessList(final ObjectList<K> l, final Object sync) {
			super(l, sync);
		}

		protected SynchronizedRandomAccessList(final ObjectList<K> l) {
			super(l);
		}

		@Override
		public ObjectList<K> subList(final int from, final int to) {
			synchronized (sync) {
				return new SynchronizedRandomAccessList<>(list.subList(from, to), sync);
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
	public static <K> ObjectList<K> synchronize(final ObjectList<K> l) {
		return l instanceof RandomAccess ? new SynchronizedRandomAccessList<>(l) : new SynchronizedList<>(l);
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
	public static <K> ObjectList<K> synchronize(final ObjectList<K> l, final Object sync) {
		return l instanceof RandomAccess ? new SynchronizedRandomAccessList<>(l, sync) : new SynchronizedList<>(l, sync);
	}

	/** An unmodifiable wrapper class for lists. */
	public static class UnmodifiableList<K> extends ObjectCollections.UnmodifiableCollection<K> implements ObjectList<K>, java.io.Serializable {
		private static final long serialVersionUID = -7046029254386353129L;
		protected final ObjectList<? extends K> list; // Due to the large number of methods that are not in COLLECTION,
														// this is worth caching.

		protected UnmodifiableList(final ObjectList<? extends K> l) {
			super(l);
			this.list = l;
		}

		@Override
		public K get(final int i) {
			return list.get(i);
		}

		@Override
		public K set(final int i, final K k) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void add(final int i, final K k) {
			throw new UnsupportedOperationException();
		}

		@Override
		public K remove(final int i) {
			throw new UnsupportedOperationException();
		}

		@Override
		public int indexOf(final Object k) {
			return list.indexOf(k);
		}

		@Override
		public int lastIndexOf(final Object k) {
			return list.lastIndexOf(k);
		}

		@Override
		public boolean addAll(final int index, final Collection<? extends K> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void replaceAll(final java.util.function.UnaryOperator<K> operator) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void getElements(final int from, final Object a[], final int offset, final int length) {
			list.getElements(from, a, offset, length);
		}

		@Override
		public void removeElements(final int from, final int to) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void addElements(int index, final K a[], int offset, int length) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void addElements(int index, final K a[]) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setElements(final K a[]) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setElements(int index, final K a[]) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setElements(int index, final K a[], int offset, int length) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void size(final int size) {
			list.size(size);
		}

		@Override
		public ObjectListIterator<K> listIterator() {
			return ObjectIterators.unmodifiable(list.listIterator());
		}

		@Override
		public ObjectListIterator<K> iterator() {
			return listIterator();
		}

		@Override
		public ObjectListIterator<K> listIterator(final int i) {
			return ObjectIterators.unmodifiable(list.listIterator(i));
		}

		@Override
		public ObjectList<K> subList(final int from, final int to) {
			return new UnmodifiableList<>(list.subList(from, to));
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
		@SuppressWarnings("unchecked")
		public int compareTo(final List<? extends K> o) {
			return ((ObjectList<K>)list).compareTo(o);
		}

		@Override
		public void sort(final java.util.Comparator<? super K> comparator) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void unstableSort(final java.util.Comparator<? super K> comparator) {
			throw new UnsupportedOperationException();
		}
	}

	/** An unmodifiable wrapper class for random-access lists. */
	public static class UnmodifiableRandomAccessList<K> extends UnmodifiableList<K> implements RandomAccess, java.io.Serializable {
		private static final long serialVersionUID = 0L;

		protected UnmodifiableRandomAccessList(final ObjectList<? extends K> l) {
			super(l);
		}

		@Override
		public ObjectList<K> subList(final int from, final int to) {
			return new UnmodifiableRandomAccessList<>(list.subList(from, to));
		}
	}

	/**
	 * Returns an unmodifiable type-specific list backed by the given type-specific list.
	 *
	 * @param l the list to be wrapped in an unmodifiable list.
	 * @return an unmodifiable view of the specified list.
	 * @see java.util.Collections#unmodifiableList(List)
	 */
	public static <K> ObjectList<K> unmodifiable(final ObjectList<? extends K> l) {
		return l instanceof RandomAccess ? new UnmodifiableRandomAccessList<>(l) : new UnmodifiableList<>(l);
	}

	/** A stub class making all known mutation methods throw {@link UnsupportedOperationException}. */
	static abstract class ImmutableListBase<K> extends AbstractObjectList<K> implements ObjectList<K> {
		/**
		 * @implSpec Always throws {@link UnsupportedOperationException} as this is an immutable type.
		 *
		 * @deprecated
		 */
		@Override
		@Deprecated
		public final void add(final int index, final K k) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @implSpec Always throws {@link UnsupportedOperationException} as this is an immutable type.
		 *
		 * @deprecated
		 */
		@Override
		@Deprecated
		public final boolean add(final K k) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @implSpec Always throws {@link UnsupportedOperationException} as this is an immutable type.
		 *
		 * @deprecated
		 */
		@Override
		@Deprecated
		public final boolean addAll(final java.util.Collection<? extends K> c) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @implSpec Always throws {@link UnsupportedOperationException} as this is an immutable type.
		 *
		 * @deprecated
		 */
		@Override
		@Deprecated
		public final boolean addAll(int index, final java.util.Collection<? extends K> c) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @implSpec Always throws {@link UnsupportedOperationException} as this is an immutable type.
		 *
		 * @deprecated
		 */
		@Override
		@Deprecated
		public final K remove(final int index) {
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
		public final boolean removeIf(final java.util.function.Predicate<? super K> c) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @implSpec Always throws {@link UnsupportedOperationException} as this is an immutable type.
		 *
		 * @deprecated
		 */
		@Override
		@Deprecated
		public final void replaceAll(final java.util.function.UnaryOperator<K> operator) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @implSpec Always throws {@link UnsupportedOperationException} as this is an immutable type.
		 *
		 * @deprecated
		 */
		@Override
		@Deprecated
		public final K set(final int index, final K k) {
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
		public final void addElements(final int index, final K a[], final int offset, final int length) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @implSpec Always throws {@link UnsupportedOperationException} as this is an immutable type.
		 *
		 * @deprecated
		 */
		@Override
		@Deprecated
		public final void setElements(final int index, final K a[], final int offset, final int length) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @implSpec Always throws {@link UnsupportedOperationException} as this is an immutable type.
		 *
		 * @deprecated
		 */
		@Override
		@Deprecated
		public final void sort(final java.util.Comparator<? super K> comparator) {
			throw new UnsupportedOperationException();
		}

		/**
		 * @implSpec Always throws {@link UnsupportedOperationException} as this is an immutable type.
		 *
		 * @deprecated
		 */
		@Override
		@Deprecated
		public final void unstableSort(final java.util.Comparator<? super K> comparator) {
			throw new UnsupportedOperationException();
		}
	}
}
