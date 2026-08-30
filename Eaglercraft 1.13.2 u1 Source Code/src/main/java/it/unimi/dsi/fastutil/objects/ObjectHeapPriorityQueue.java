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
package it.unimi.dsi.fastutil.objects;

import java.util.Arrays;
import java.util.Comparator;
import it.unimi.dsi.fastutil.PriorityQueue;
import java.util.Collection;
import java.util.NoSuchElementException;

/**
 * A type-specific heap-based priority queue.
 *
 * <p>
 * Instances of this class represent a priority queue using a heap. The heap is enlarged as needed,
 * but it is never shrunk. Use the {@link #trim()} method to reduce its size, if necessary.
 */
public class ObjectHeapPriorityQueue<K> implements PriorityQueue<K>, java.io.Serializable {
	private static final long serialVersionUID = 1L;
	/** The heap array. */
	@SuppressWarnings("unchecked")
	protected transient K[] heap = (K[])ObjectArrays.EMPTY_ARRAY;
	/** The number of elements in this queue. */
	protected int size;
	/** The type-specific comparator used in this queue. */
	protected Comparator<? super K> c;

	/**
	 * Creates a new empty queue with a given capacity and comparator.
	 *
	 * @param capacity the initial capacity of this queue.
	 * @param c the comparator used in this queue, or {@code null} for the natural order.
	 */
	@SuppressWarnings("unchecked")
	public ObjectHeapPriorityQueue(int capacity, Comparator<? super K> c) {
		if (capacity > 0) this.heap = (K[])new Object[capacity];
		this.c = c;
	}

	/**
	 * Creates a new empty queue with a given capacity and using the natural order.
	 *
	 * @param capacity the initial capacity of this queue.
	 */
	public ObjectHeapPriorityQueue(int capacity) {
		this(capacity, null);
	}

	/**
	 * Creates a new empty queue with a given comparator.
	 *
	 * @param c the comparator used in this queue, or {@code null} for the natural order.
	 */
	public ObjectHeapPriorityQueue(Comparator<? super K> c) {
		this(0, c);
	}

	/**
	 * Creates a new empty queue using the natural order.
	 */
	public ObjectHeapPriorityQueue() {
		this(0, null);
	}

	/**
	 * Wraps a given array in a queue using a given comparator.
	 *
	 * <p>
	 * The queue returned by this method will be backed by the given array. The first {@code size}
	 * element of the array will be rearranged so to form a heap (this is more efficient than enqueing
	 * the elements of {@code a} one by one).
	 *
	 * @param a an array.
	 * @param size the number of elements to be included in the queue.
	 * @param c the comparator used in this queue, or {@code null} for the natural order.
	 */
	public ObjectHeapPriorityQueue(final K[] a, int size, final Comparator<? super K> c) {
		this(c);
		this.heap = a;
		this.size = size;
		ObjectHeaps.makeHeap(a, size, c);
	}

	/**
	 * Wraps a given array in a queue using a given comparator.
	 *
	 * <p>
	 * The queue returned by this method will be backed by the given array. The elements of the array
	 * will be rearranged so to form a heap (this is more efficient than enqueing the elements of
	 * {@code a} one by one).
	 *
	 * @param a an array.
	 * @param c the comparator used in this queue, or {@code null} for the natural order.
	 */
	public ObjectHeapPriorityQueue(final K[] a, final Comparator<? super K> c) {
		this(a, a.length, c);
	}

	/**
	 * Wraps a given array in a queue using the natural order.
	 *
	 * <p>
	 * The queue returned by this method will be backed by the given array. The first {@code size}
	 * element of the array will be rearranged so to form a heap (this is more efficient than enqueing
	 * the elements of {@code a} one by one).
	 *
	 * @param a an array.
	 * @param size the number of elements to be included in the queue.
	 */
	public ObjectHeapPriorityQueue(final K[] a, int size) {
		this(a, size, null);
	}

	/**
	 * Wraps a given array in a queue using the natural order.
	 *
	 * <p>
	 * The queue returned by this method will be backed by the given array. The elements of the array
	 * will be rearranged so to form a heap (this is more efficient than enqueing the elements of
	 * {@code a} one by one).
	 *
	 * @param a an array.
	 */
	public ObjectHeapPriorityQueue(final K[] a) {
		this(a, a.length);
	}

	/**
	 * Creates a queue using the elements in a collection using a given comparator.
	 *
	 * <p>
	 * This constructor is more efficient than enqueing the elements of {@code collection} one by one.
	 *
	 * @param collection a collection; its elements will be used to initialize the queue.
	 * @param c the comparator used in this queue, or {@code null} for the natural order.
	 */
	@SuppressWarnings("unchecked")
	public ObjectHeapPriorityQueue(final Collection<? extends K> collection, final Comparator<? super K> c) {
		this((K[])collection.toArray(), c);
	}

	/**
	 * Creates a queue using the elements in a collection using the natural order.
	 *
	 * <p>
	 * This constructor is more efficient than enqueing the elements of {@code collection} one by one.
	 *
	 * @param collection a collection; its elements will be used to initialize the queue.
	 */
	public ObjectHeapPriorityQueue(final Collection<? extends K> collection) {
		this(collection, null);
	}

	@Override
	public void enqueue(K x) {
		if (size == heap.length) heap = ObjectArrays.grow(heap, size + 1);
		heap[size++] = x;
		ObjectHeaps.upHeap(heap, size, size - 1, c);
	}

	@Override
	public K dequeue() {
		if (size == 0) throw new NoSuchElementException();
		final K result = heap[0];
		heap[0] = heap[--size];
		heap[size] = null;
		if (size != 0) ObjectHeaps.downHeap(heap, size, 0, c);
		return result;
	}

	@Override
	public K first() {
		if (size == 0) throw new NoSuchElementException();
		return heap[0];
	}

	@Override
	public void changed() {
		ObjectHeaps.downHeap(heap, size, 0, c);
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public void clear() {
		Arrays.fill(heap, 0, size, null);
		size = 0;
	}

	/** Trims the underlying heap array so that it has exactly {@link #size()} elements. */
	public void trim() {
		heap = ObjectArrays.trim(heap, size);
	}

	@Override
	public Comparator<? super K> comparator() {
		return c;
	}

	private void writeObject(java.io.ObjectOutputStream s) throws java.io.IOException {
		s.defaultWriteObject();
		s.writeInt(heap.length);
		for (int i = 0; i < size; i++) s.writeObject(heap[i]);
	}

	@SuppressWarnings("unchecked")
	private void readObject(java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();
		heap = (K[])new Object[s.readInt()];
		for (int i = 0; i < size; i++) heap[i] = (K)s.readObject();
	}
}
