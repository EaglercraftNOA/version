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
package it.unimi.dsi.fastutil.floats;

import java.util.Iterator;
import java.util.Collection;
import java.util.NoSuchElementException;

/**
 * A type-specific heap-based priority queue.
 *
 * <p>
 * Instances of this class represent a priority queue using a heap. The heap is enlarged as needed,
 * but it is never shrunk. Use the {@link #trim()} method to reduce its size, if necessary.
 */
public class FloatHeapPriorityQueue implements FloatPriorityQueue, java.io.Serializable {
	private static final long serialVersionUID = 1L;
	/** The heap array. */

	protected transient float[] heap = FloatArrays.EMPTY_ARRAY;
	/** The number of elements in this queue. */
	protected int size;
	/** The type-specific comparator used in this queue. */
	protected FloatComparator c;

	/**
	 * Creates a new empty queue with a given capacity and comparator.
	 *
	 * @param capacity the initial capacity of this queue.
	 * @param c the comparator used in this queue, or {@code null} for the natural order.
	 */

	public FloatHeapPriorityQueue(int capacity, FloatComparator c) {
		if (capacity > 0) this.heap = new float[capacity];
		this.c = c;
	}

	/**
	 * Creates a new empty queue with a given capacity and using the natural order.
	 *
	 * @param capacity the initial capacity of this queue.
	 */
	public FloatHeapPriorityQueue(int capacity) {
		this(capacity, null);
	}

	/**
	 * Creates a new empty queue with a given comparator.
	 *
	 * @param c the comparator used in this queue, or {@code null} for the natural order.
	 */
	public FloatHeapPriorityQueue(FloatComparator c) {
		this(0, c);
	}

	/**
	 * Creates a new empty queue using the natural order.
	 */
	public FloatHeapPriorityQueue() {
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
	public FloatHeapPriorityQueue(final float[] a, int size, final FloatComparator c) {
		this(c);
		this.heap = a;
		this.size = size;
		FloatHeaps.makeHeap(a, size, c);
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
	public FloatHeapPriorityQueue(final float[] a, final FloatComparator c) {
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
	public FloatHeapPriorityQueue(final float[] a, int size) {
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
	public FloatHeapPriorityQueue(final float[] a) {
		this(a, a.length);
	}

	/**
	 * Creates a queue using the elements in a type-specific collection using a given comparator.
	 *
	 * <p>
	 * This constructor is more efficient than enqueing the elements of {@code collection} one by one.
	 *
	 * @param collection a collection; its elements will be used to initialize the queue.
	 * @param c the comparator used in this queue, or {@code null} for the natural order.
	 */
	public FloatHeapPriorityQueue(final FloatCollection collection, final FloatComparator c) {
		this(collection.toFloatArray(), c);
	}

	/**
	 * Creates a queue using the elements in a type-specific collection using the natural order.
	 *
	 * <p>
	 * This constructor is more efficient than enqueing the elements of {@code collection} one by one.
	 *
	 * @param collection a collection; its elements will be used to initialize the queue.
	 */
	public FloatHeapPriorityQueue(final FloatCollection collection) {
		this(collection, null);
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
	public FloatHeapPriorityQueue(final Collection<? extends Float> collection, final FloatComparator c) {
		this(collection.size(), c);
		final Iterator<? extends Float> iterator = collection.iterator();
		final int size = collection.size();
		for (int i = 0; i < size; i++) heap[i] = ((Float)(iterator.next())).floatValue();
	}

	/**
	 * Creates a queue using the elements in a collection using the natural order.
	 *
	 * <p>
	 * This constructor is more efficient than enqueing the elements of {@code collection} one by one.
	 *
	 * @param collection a collection; its elements will be used to initialize the queue.
	 */
	public FloatHeapPriorityQueue(final Collection<? extends Float> collection) {
		this(collection, null);
	}

	@Override
	public void enqueue(float x) {
		if (size == heap.length) heap = FloatArrays.grow(heap, size + 1);
		heap[size++] = x;
		FloatHeaps.upHeap(heap, size, size - 1, c);
	}

	@Override
	public float dequeueFloat() {
		if (size == 0) throw new NoSuchElementException();
		final float result = heap[0];
		heap[0] = heap[--size];
		if (size != 0) FloatHeaps.downHeap(heap, size, 0, c);
		return result;
	}

	@Override
	public float firstFloat() {
		if (size == 0) throw new NoSuchElementException();
		return heap[0];
	}

	@Override
	public void changed() {
		FloatHeaps.downHeap(heap, size, 0, c);
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public void clear() {
		size = 0;
	}

	/** Trims the underlying heap array so that it has exactly {@link #size()} elements. */
	public void trim() {
		heap = FloatArrays.trim(heap, size);
	}

	@Override
	public FloatComparator comparator() {
		return c;
	}

	private void writeObject(java.io.ObjectOutputStream s) throws java.io.IOException {
		s.defaultWriteObject();
		s.writeInt(heap.length);
		for (int i = 0; i < size; i++) s.writeFloat(heap[i]);
	}

	private void readObject(java.io.ObjectInputStream s) throws java.io.IOException, ClassNotFoundException {
		s.defaultReadObject();
		heap = new float[s.readInt()];
		for (int i = 0; i < size; i++) heap[i] = s.readFloat();
	}
}
