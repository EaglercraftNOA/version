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
public final class ObjectBigLists {
	private ObjectBigLists() {
	}

	/**
	 * Shuffles the specified big list using the specified pseudorandom number generator.
	 *
	 * @param l the big list to be shuffled.
	 * @param random a pseudorandom number generator.
	 * @return {@code l}.
	 */
	public static <K> ObjectBigList<K> shuffle(final ObjectBigList<K> l, final Random random) {
		for (long i = l.size64(); i-- != 0;) {
			final long p = (random.nextLong() & 0x7FFFFFFFFFFFFFFFL) % (i + 1);
			final K t = l.get(i);
			l.set(i, l.get(p));
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
	public static class EmptyBigList<K> extends ObjectCollections.EmptyCollection<K> implements ObjectBigList<K>, java.io.Serializable, Cloneable {
		private static final long serialVersionUID = -7046029254386353129L;

		protected EmptyBigList() {
		}

		@Override
		public K get(long i) {
			throw new IndexOutOfBoundsException();
		}

		@Override
		public boolean remove(Object k) {
			throw new UnsupportedOperationException();
		}

		@Override
		public K remove(long i) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void add(final long index, final K k) {
			throw new UnsupportedOperationException();
		}

		@Override
		public K set(final long index, final K k) {
			throw new UnsupportedOperationException();
		}

		@Override
		public long indexOf(Object k) {
			return -1;
		}

		@Override
		public long lastIndexOf(Object k) {
			return -1;
		}

		@Override
		public boolean addAll(long i, Collection<? extends K> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		@SuppressWarnings("unchecked")
		public ObjectBigListIterator<K> listIterator() {
			return ObjectBigListIterators.EMPTY_BIG_LIST_ITERATOR;
		}

		@Override
		@SuppressWarnings("unchecked")
		public ObjectBigListIterator<K> iterator() {
			return ObjectBigListIterators.EMPTY_BIG_LIST_ITERATOR;
		}

		@Override
		@SuppressWarnings("unchecked")
		public ObjectBigListIterator<K> listIterator(long i) {
			if (i == 0) return ObjectBigListIterators.EMPTY_BIG_LIST_ITERATOR;
			throw new IndexOutOfBoundsException(String.valueOf(i));
		}

		@Override
		@SuppressWarnings("unchecked")
		public ObjectSpliterator<K> spliterator() {
			return ObjectSpliterators.EMPTY_SPLITERATOR;
		}

		@Override
		public ObjectBigList<K> subList(long from, long to) {
			if (from == 0 && to == 0) return this;
			throw new IndexOutOfBoundsException();
		}

		@Override
		public void getElements(long from, Object[][] a, long offset, long length) {
			BigArrays.ensureOffsetLength(a, offset, length);
			if (from != 0) throw new IndexOutOfBoundsException();
		}

		@Override
		public void removeElements(long from, long to) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void addElements(long index, final K a[][], long offset, long length) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void addElements(long index, final K a[][]) {
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
		public int compareTo(final BigList<? extends K> o) {
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
	@SuppressWarnings("rawtypes")
	public static final EmptyBigList EMPTY_BIG_LIST = new EmptyBigList();

	/**
	 * Returns an empty big list (immutable). It is serializable and cloneable.
	 *
	 * <p>
	 * This method provides a typesafe access to {@link #EMPTY_BIG_LIST}.
	 * 
	 * @return an empty big list (immutable).
	 */
	@SuppressWarnings("unchecked")
	public static <K> ObjectBigList<K> emptyList() {
		return EMPTY_BIG_LIST;
	}

	/**
	 * An immutable class representing a type-specific singleton big list.
	 *
	 * <p>
	 * This class may be useful to implement your own in case you subclass a type-specific big list.
	 */
	public static class Singleton<K> extends AbstractObjectBigList<K> implements java.io.Serializable, Cloneable {
		private static final long serialVersionUID = -7046029254386353129L;
		private final K element;

		protected Singleton(final K element) {
			this.element = element;
		}

		@Override
		public K get(final long i) {
			if (i == 0) return element;
			throw new IndexOutOfBoundsException();
		}

		@Override
		public boolean remove(Object k) {
			throw new UnsupportedOperationException();
		}

		@Override
		public K remove(final long i) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean contains(final Object k) {
			return java.util.Objects.equals(k, element);
		}

		@Override
		public long indexOf(final Object k) {
			return java.util.Objects.equals(k, element) ? 0 : -1;
		}

		/* Slightly optimized w.r.t. the one in ABSTRACT_SET. */
		@Override
		public Object[] toArray() {
			Object a[] = { element };
			return a;
		}

		@Override
		public ObjectBigListIterator<K> listIterator() {
			return ObjectBigListIterators.singleton(element);
		}

		@Override
		public ObjectBigListIterator<K> listIterator(long i) {
			if (i > 1 || i < 0) throw new IndexOutOfBoundsException();
			ObjectBigListIterator<K> l = listIterator();
			if (i == 1) l.next();
			return l;
		}

		@Override
		public ObjectSpliterator<K> spliterator() {
			return ObjectSpliterators.singleton(element);
		}

		@Override
		@SuppressWarnings("unchecked")
		public ObjectBigList<K> subList(final long from, final long to) {
			ensureIndex(from);
			ensureIndex(to);
			if (from > to) throw new IndexOutOfBoundsException("Start index (" + from + ") is greater than end index (" + to + ")");
			if (from != 0 || to != 1) return EMPTY_BIG_LIST;
			return this;
		}

		@Override
		public boolean addAll(long i, Collection<? extends K> c) {
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
	public static <K> ObjectBigList<K> singleton(final K element) {
		return new Singleton<>(element);
	}

	/** A synchronized wrapper class for big lists. */
	public static class SynchronizedBigList<K> extends ObjectCollections.SynchronizedCollection<K> implements ObjectBigList<K>, java.io.Serializable {
		private static final long serialVersionUID = -7046029254386353129L;
		protected final ObjectBigList<K> list; // Due to the large number of methods that are not in COLLECTION, this is
												// worth caching.

		protected SynchronizedBigList(final ObjectBigList<K> l, final Object sync) {
			super(l, sync);
			this.list = l;
		}

		protected SynchronizedBigList(final ObjectBigList<K> l) {
			super(l);
			this.list = l;
		}

		@Override
		public K get(final long i) {
			synchronized (sync) {
				return list.get(i);
			}
		}

		@Override
		public K set(final long i, final K k) {
			synchronized (sync) {
				return list.set(i, k);
			}
		}

		@Override
		public void add(final long i, final K k) {
			synchronized (sync) {
				list.add(i, k);
			}
		}

		@Override
		public K remove(final long i) {
			synchronized (sync) {
				return list.remove(i);
			}
		}

		@Override
		public long indexOf(final Object k) {
			synchronized (sync) {
				return list.indexOf(k);
			}
		}

		@Override
		public long lastIndexOf(final Object k) {
			synchronized (sync) {
				return list.lastIndexOf(k);
			}
		}

		@Override
		public boolean addAll(final long index, final Collection<? extends K> c) {
			synchronized (sync) {
				return list.addAll(index, c);
			}
		}

		@Override
		public void getElements(final long from, final Object a[][], final long offset, final long length) {
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
		public void addElements(long index, final K a[][], long offset, long length) {
			synchronized (sync) {
				list.addElements(index, a, offset, length);
			}
		}

		@Override
		public void addElements(long index, final K a[][]) {
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
		public ObjectBigListIterator<K> iterator() {
			return list.listIterator();
		}

		@Override
		public ObjectBigListIterator<K> listIterator() {
			return list.listIterator();
		}

		@Override
		public ObjectBigListIterator<K> listIterator(final long i) {
			return list.listIterator(i);
		}

		@Override
		public ObjectBigList<K> subList(final long from, final long to) {
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
		public int compareTo(final BigList<? extends K> o) {
			synchronized (sync) {
				return list.compareTo(o);
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
	public static <K> ObjectBigList<K> synchronize(final ObjectBigList<K> l) {
		return new SynchronizedBigList<>(l);
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
	public static <K> ObjectBigList<K> synchronize(final ObjectBigList<K> l, final Object sync) {
		return new SynchronizedBigList<>(l, sync);
	}

	/** An unmodifiable wrapper class for big lists. */
	public static class UnmodifiableBigList<K> extends ObjectCollections.UnmodifiableCollection<K> implements ObjectBigList<K>, java.io.Serializable {
		private static final long serialVersionUID = -7046029254386353129L;
		protected final ObjectBigList<? extends K> list; // Due to the large number of methods that are not in
															// COLLECTION, this is worth caching.

		protected UnmodifiableBigList(final ObjectBigList<? extends K> l) {
			super(l);
			this.list = l;
		}

		@Override
		public K get(final long i) {
			return list.get(i);
		}

		@Override
		public K set(final long i, final K k) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void add(final long i, final K k) {
			throw new UnsupportedOperationException();
		}

		@Override
		public K remove(final long i) {
			throw new UnsupportedOperationException();
		}

		@Override
		public long indexOf(final Object k) {
			return list.indexOf(k);
		}

		@Override
		public long lastIndexOf(final Object k) {
			return list.lastIndexOf(k);
		}

		@Override
		public boolean addAll(final long index, final Collection<? extends K> c) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void getElements(final long from, final Object a[][], final long offset, final long length) {
			list.getElements(from, a, offset, length);
		}

		@Override
		public void removeElements(final long from, final long to) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void addElements(long index, final K a[][], long offset, long length) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void addElements(long index, final K a[][]) {
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
		public ObjectBigListIterator<K> iterator() {
			return listIterator();
		}

		@Override
		public ObjectBigListIterator<K> listIterator() {
			return ObjectBigListIterators.unmodifiable(list.listIterator());
		}

		@Override
		public ObjectBigListIterator<K> listIterator(final long i) {
			return ObjectBigListIterators.unmodifiable(list.listIterator(i));
		}

		@Override
		public ObjectBigList<K> subList(final long from, final long to) {
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
		@SuppressWarnings("unchecked")
		public int compareTo(final BigList<? extends K> o) {
			return ((ObjectBigList<K>)list).compareTo(o);
		}
	}

	/**
	 * Returns an unmodifiable type-specific big list backed by the given type-specific big list.
	 *
	 * @param l the big list to be wrapped in an unmodifiable big list.
	 * @return an unmodifiable view of the specified big list.
	 * @see java.util.Collections#unmodifiableList(List)
	 */
	public static <K> ObjectBigList<K> unmodifiable(final ObjectBigList<? extends K> l) {
		return new UnmodifiableBigList<>(l);
	}

	/** A class exposing a list as a big list. */
	public static class ListBigList<K> extends AbstractObjectBigList<K> implements java.io.Serializable {
		private static final long serialVersionUID = -7046029254386353129L;
		private final ObjectList<K> list;

		protected ListBigList(final ObjectList<K> list) {
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
		public ObjectBigListIterator<K> iterator() {
			return ObjectBigListIterators.asBigListIterator(list.iterator());
		}

		@Override
		public ObjectBigListIterator<K> listIterator() {
			return ObjectBigListIterators.asBigListIterator(list.listIterator());
		}

		@Override
		public ObjectBigListIterator<K> listIterator(final long index) {
			return ObjectBigListIterators.asBigListIterator(list.listIterator(intIndex(index)));
		}

		@Override
		public boolean addAll(final long index, final Collection<? extends K> c) {
			return list.addAll(intIndex(index), c);
		}

		@Override
		public ObjectBigList<K> subList(long from, long to) {
			return new ListBigList<>(list.subList(intIndex(from), intIndex(to)));
		}

		@Override
		public boolean contains(final Object key) {
			return list.contains(key);
		}

		@Override
		public Object[] toArray() {
			return list.toArray();
		}

		@Override
		public void removeElements(final long from, final long to) {
			list.removeElements(intIndex(from), intIndex(to));
		}

		@Override
		public void add(long index, K key) {
			list.add(intIndex(index), key);
		}

		@Override
		public boolean add(K key) {
			return list.add(key);
		}

		@Override
		public K get(long index) {
			return list.get(intIndex(index));
		}

		@Override
		public long indexOf(Object k) {
			return list.indexOf(k);
		}

		@Override
		public long lastIndexOf(Object k) {
			return list.lastIndexOf(k);
		}

		@Override
		public K remove(long index) {
			return list.remove(intIndex(index));
		}

		@Override
		public K set(long index, K k) {
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
		public boolean addAll(Collection<? extends K> c) {
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
	public static <K> ObjectBigList<K> asBigList(final ObjectList<K> list) {
		return new ListBigList<>(list);
	}
}
