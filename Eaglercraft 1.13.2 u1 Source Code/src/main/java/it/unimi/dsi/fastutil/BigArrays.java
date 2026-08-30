/*
	* Copyright (C) 2010-2024 Sebastiano Vigna
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
	*
	* For the sorting code:
	*
	* Copyright (C) 1999 CERN - European Organization for Nuclear Research.
	*
	*   Permission to use, copy, modify, distribute and sell this software and
	*   its documentation for any purpose is hereby granted without fee,
	*   provided that the above copyright notice appear in all copies and that
	*   both that copyright notice and this permission notice appear in
	*   supporting documentation. CERN makes no representations about the
	*   suitability of this software for any purpose. It is provided "as is"
	*   without expressed or implied warranty.
	*/
package it.unimi.dsi.fastutil;

import it.unimi.dsi.fastutil.ints.IntBigArrayBigList;
import it.unimi.dsi.fastutil.longs.LongComparator;
import it.unimi.dsi.fastutil.bytes.ByteBigArrays;
import it.unimi.dsi.fastutil.booleans.BooleanBigArrays;
import it.unimi.dsi.fastutil.chars.CharBigArrays;
import it.unimi.dsi.fastutil.shorts.ShortBigArrays;
import it.unimi.dsi.fastutil.ints.IntBigArrays;
import it.unimi.dsi.fastutil.longs.LongBigArrays;
import it.unimi.dsi.fastutil.floats.FloatBigArrays;
import it.unimi.dsi.fastutil.doubles.DoubleBigArrays;
import it.unimi.dsi.fastutil.objects.ObjectBigArrays;
import it.unimi.dsi.fastutil.booleans.BooleanArrays;
import it.unimi.dsi.fastutil.bytes.ByteArrays;
import it.unimi.dsi.fastutil.chars.CharArrays;
import it.unimi.dsi.fastutil.shorts.ShortArrays;
import it.unimi.dsi.fastutil.ints.IntArrays;
import it.unimi.dsi.fastutil.longs.LongArrays;
import it.unimi.dsi.fastutil.floats.FloatArrays;
import it.unimi.dsi.fastutil.doubles.DoubleArrays;
import it.unimi.dsi.fastutil.objects.ObjectArrays;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicLongArray;
import java.util.Random;

/**
 * A class providing static methods and objects that do useful things with big arrays.
 *
 * <h2>Introducing big arrays</h2>
 *
 * <p>
 * A <em>big array</em> is an array-of-arrays representation of an array. The length of a big array
 * is bounded by {@link #SEGMENT_SIZE} * {@link Integer#MAX_VALUE} = {@value #SEGMENT_SIZE} *
 * (2<sup>31</sup> &minus; 1) rather than {@link Integer#MAX_VALUE}. The type of a big array is that
 * of an array-of-arrays, so a big array of integers is of type {@code int[][]}. Note that
 * {@link #SEGMENT_SIZE} has been chosen so that a single segment is smaller than 2<sup>31</sup>
 * bytes independently of the data type. It might be enlarged in the future.
 *
 * <p>
 * If {@code a} is a big array, {@code a[0]}, {@code a[1]}, &hellip; are called the
 * <em>segments</em> of the big array. All segments, except possibly for the last one, are of length
 * {@link #SEGMENT_SIZE}. Given an index {@code i} into a big array, there is an associated
 * <em>{@linkplain #segment(long) segment}</em> and an associated
 * <em>{@linkplain #displacement(long) displacement}</em> into that segment. Access to single
 * members happens by means of accessors (see, e.g., {@link #get(int[][], long)} and
 * {@link #set(int[][], long, int)}), but you can also use the methods
 * {@link #segment(long)}/{@link #displacement(long)} to access entries manually.
 *
 * <p>
 * The intended usage of most of the methods of this class is that they will be imported statically:
 * for example,
 * 
 * <pre>
 * import static it.unimi.dsi.fastutil.BigArrays.copy;
 * import static it.unimi.dsi.fastutil.BigArrays.get;
 * import static it.unimi.dsi.fastutil.BigArrays.length;
 * import static it.unimi.dsi.fastutil.BigArrays.set;
 * </pre>
 *
 * <p>
 * Dynamic binding will take care of selecting the right method depending on the array type.
 *
 * <h2>Scanning big arrays</h2>
 *
 * <p>
 * You can scan a big array using the following idiomatic form:
 *
 * <pre>
* for(int s = 0; s &lt; a.length; s++) {
*     final int[] t = a[s];
*     final int l = t.length;
*     for(int d = 0; d &lt; l; d++) {
*          do something with t[d]
*     }
* }
 * </pre>
 *
 * or using the simpler reversed version:
 *
 * <pre>
* for(int s = a.length; s-- != 0;) {
*     final int[] t = a[s];
*     for(int d = t.length; d-- != 0;) {
*         do something with t[d]
*     }
* }
 * </pre>
 * <p>
 * Inside the inner loop, the original index in {@code a} can be retrieved using
 * {@link #index(int, int) index(segment, displacement)}. You can also use an additional long to
 * keep track of the index.
 *
 * <p>
 * Note that caching is essential in making these loops essentially as fast as those scanning
 * standard arrays (as iterations of the outer loop happen very rarely). Using loops of this kind is
 * extremely faster than using a standard loop and accessors.
 *
 * <p>
 * In some situations, you might want to iterate over a part of a big array having an offset and a
 * length. In this case, the idiomatic loops are as follows:
 *
 * <pre>
* for(int s = segment(offset); s &lt; segment(offset + length + SEGMENT_MASK); s++) {
*     final int[] t = a[s];
*     final int l = (int)Math.min(t.length, offset + length - start(s));
*     for(int d = (int)Math.max(0, offset - start(s)); d &lt; l; d++) {
*         do something with t[d]
*     }
* }
 * </pre>
 *
 * or, in a reversed form,
 *
 * <pre>
* for(int s = segment(offset + length + SEGMENT_MASK); s-- != segment(offset);) {
*     final int[] t = a[s];
*     final int b = (int)Math.max(0, offset - start(s));
*     for(int d = (int)Math.min(t.length, offset + length - start(s)); d-- != b ;) {
*         do something with t[d]
*     }
* }
 * </pre>
 *
 * <h2>Literal big arrays</h2>
 *
 * <p>
 * A literal big array can be easily created by using the suitable type-specific {@code wrap()}
 * method (e.g., {@link BigArrays#wrap(int[])}) around a standard Java literal array.
 *
 * <h2>Atomic big arrays</h2>
 *
 * <p>
 * Limited support is available for atomic big arrays of integers and longs, with a similar syntax.
 * Atomic big arrays are arrays of instances of
 * {@link java.util.concurrent.atomic.AtomicIntegerArray} or
 * {@link java.util.concurrent.atomic.AtomicLongArray} of length {@link #SEGMENT_SIZE} (or less, for
 * the last segment, as usual) and their size cannot be changed. Some methods from those classes are
 * available in {@link BigArrays} for atomic big arrays (e.g.,
 * {@link BigArrays#incrementAndGet(AtomicIntegerArray[], long)}).
 *
 * <h2>Big alternatives</h2>
 *
 * <p>
 * If you find the kind of &ldquo;bare hands&rdquo; approach to big arrays not enough
 * object-oriented, please use big lists based on big arrays (e.g., {@link IntBigArrayBigList}). Big
 * arrays follow the Java tradition of considering arrays as a &ldquo;legal
 * alien&rdquo;&mdash;something in-between an object and a primitive type. This approach lacks the
 * consistency of a full object-oriented approach, but provides some significant performance gains.
 *
 * <h2>Additional methods</h2>
 *
 * <p>
 * In particular, the {@code ensureCapacity()}, {@code grow()}, {@code trim()} and
 * {@code setLength()} methods allow to handle arrays much like array lists.
 *
 * <p>
 * In addition to commodity methods, this class contains {@link BigSwapper}-based implementations of
 * {@linkplain #quickSort(long, long, LongComparator, BigSwapper) quicksort} and of a stable,
 * in-place {@linkplain #mergeSort(long, long, LongComparator, BigSwapper) mergesort}. These generic
 * sorting methods can be used to sort any kind of list, but they find their natural usage, for
 * instance, in sorting big arrays in parallel.
 *
 * @see it.unimi.dsi.fastutil.Arrays
 */
public class BigArrays {
	/**
	 * The shift used to compute the segment associated with an index (equivalently, the logarithm of
	 * the segment size).
	 */
	public static final int SEGMENT_SHIFT = 27;
	/**
	 * The current size of a segment (2<sup>27</sup>) is the largest size that makes the physical memory
	 * allocation for a single segment strictly smaller than 2<sup>31</sup> bytes.
	 */
	public static final int SEGMENT_SIZE = 1 << SEGMENT_SHIFT;
	/** The mask used to compute the displacement associated to an index. */
	public static final int SEGMENT_MASK = SEGMENT_SIZE - 1;

	protected BigArrays() {
	}

	/**
	 * Computes the segment associated with a given index.
	 *
	 * @param index an index into a big array.
	 * @return the associated segment.
	 */
	public static int segment(final long index) {
		return (int)(index >>> SEGMENT_SHIFT);
	}

	/**
	 * Computes the displacement associated with a given index.
	 *
	 * @param index an index into a big array.
	 * @return the associated displacement (in the associated {@linkplain #segment(long) segment}).
	 */
	public static int displacement(final long index) {
		return (int)(index & SEGMENT_MASK);
	}

	/**
	 * Computes the starting index of a given segment.
	 *
	 * @param segment the segment of a big array.
	 * @return the starting index of the segment.
	 */
	public static long start(final int segment) {
		return (long)segment << SEGMENT_SHIFT;
	}

	/**
	 * Computes the nearest segment starting index of a given index.
	 *
	 * <p>
	 * This will either be {@code start(segment(index)} or {@code start(segment(index) + 1)}, whichever
	 * is closer, given the bounds can be respected. If neither segment start is within the bounds, then
	 * the index is returned unmodified.
	 *
	 * <p>
	 * This method can be useful for operations that seek to align on the outer array's boundaries when
	 * possible.
	 *
	 * @implSpec The current implementation is branch heavy and is thus not suitable for use in inner
	 *           loops. However, it should be fine for the recursive step, where split points are
	 *           computed.
	 *
	 * @param index an index into a big array.
	 * @param min the minimum (inclusive) valid index of the big array in question
	 * @param max the maximum (exclusive) valid index of the big array in question
	 * @return the closest segment starting index to {@code index}
	 * @since 8.5.0
	 */
	public static long nearestSegmentStart(final long index, final long min, final long max) {
		// There probably is a less branchy, bit twiddly way to do this, but this is fine for now.
		// This isn't going to be used in inner loops, only the recursive call.
		final long lower = start(segment(index));
		final long upper = start(segment(index) + 1);
		if (upper >= max) {
			if (lower < min) {
				return index;
			}
			return lower;
		}
		if (lower < min) return upper;
		// Overflow avoiding midpoint computation
		final long mid = lower + ((upper - lower) >> 1);
		return index <= mid ? lower : upper;
	}

	/**
	 * Computes the index associated with given segment and displacement.
	 *
	 * @param segment the segment of a big array.
	 * @param displacement the displacement into the segment.
	 * @return the associated index: that is, {@link #segment(long) segment(index(segment,
	 *         displacement)) == segment} and {@link #displacement(long) displacement(index(segment,
	 *         displacement)) == displacement}.
	 */
	public static long index(final int segment, final int displacement) {
		return start(segment) + displacement;
	}

	/**
	 * Ensures that a range given by its first (inclusive) and last (exclusive) elements fits a big
	 * array of given length.
	 *
	 * <p>
	 * This method may be used whenever a big array range check is needed.
	 *
	 * @param bigArrayLength a big-array length (must be nonnegative).
	 * @param from a start index (inclusive).
	 * @param to an end index (inclusive).
	 * @throws IllegalArgumentException if {@code from} is greater than {@code to}.
	 * @throws ArrayIndexOutOfBoundsException if {@code from} or {@code to} are greater than
	 *             {@code bigArrayLength} or negative.
	 *
	 * @implNote An {@code assert} checks whether {@code bigArrayLength} is nonnegative.
	 */
	public static void ensureFromTo(final long bigArrayLength, final long from, final long to) {
		assert bigArrayLength >= 0;
		if (from < 0) throw new ArrayIndexOutOfBoundsException("Start index (" + from + ") is negative");
		if (from > to) throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
		if (to > bigArrayLength) throw new ArrayIndexOutOfBoundsException("End index (" + to + ") is greater than big-array length (" + bigArrayLength + ")");
	}

	/**
	 * Ensures that a range given by an offset and a length fits a big array of given length.
	 *
	 * <p>
	 * This method may be used whenever a big array range check is needed.
	 *
	 * @param bigArrayLength a big-array length (must be nonnegative).
	 * @param offset a start index for the fragment
	 * @param length a length (the number of elements in the fragment).
	 * @throws IllegalArgumentException if {@code length} is negative.
	 * @throws ArrayIndexOutOfBoundsException if {@code offset} is negative or {@code offset} +
	 *             {@code length} is greater than {@code bigArrayLength}.
	 *
	 * @implNote An {@code assert} checks whether {@code bigArrayLength} is nonnegative.
	 */
	public static void ensureOffsetLength(final long bigArrayLength, final long offset, final long length) {
		assert bigArrayLength >= 0;
		if (offset < 0) throw new ArrayIndexOutOfBoundsException("Offset (" + offset + ") is negative");
		if (length < 0) throw new IllegalArgumentException("Length (" + length + ") is negative");
		if (length > bigArrayLength - offset) throw new ArrayIndexOutOfBoundsException("Last index (" + Long.toUnsignedString(offset + length) + ") is greater than big-array length (" + bigArrayLength + ")");
	}

	/**
	 * Ensures that a big-array length is legal.
	 *
	 * @param bigArrayLength a big-array length.
	 * @throws IllegalArgumentException if {@code length} is negative, or larger than or equal to
	 *             {@link #SEGMENT_SIZE} * {@link Integer#MAX_VALUE}.
	 */
	public static void ensureLength(final long bigArrayLength) {
		if (bigArrayLength < 0) throw new IllegalArgumentException("Negative big-array size: " + bigArrayLength);
		if (bigArrayLength >= (long)Integer.MAX_VALUE << SEGMENT_SHIFT) throw new IllegalArgumentException("Big-array size too big: " + bigArrayLength);
	}

	private static final int SMALL = 7;
	private static final int MEDIUM = 40;

	/**
	 * Transforms two consecutive sorted ranges into a single sorted range. The initial ranges are
	 * {@code [first, middle)} and {@code [middle, last)}, and the resulting range is
	 * {@code [first, last)}. Elements in the first input range will precede equal elements in the
	 * second.
	 */
	private static void inPlaceMerge(final long from, long mid, final long to, final LongComparator comp, final BigSwapper swapper) {
		if (from >= mid || mid >= to) return;
		if (to - from == 2) {
			if (comp.compare(mid, from) < 0) {
				swapper.swap(from, mid);
			}
			return;
		}
		long firstCut;
		long secondCut;
		if (mid - from > to - mid) {
			firstCut = from + (mid - from) / 2;
			secondCut = lowerBound(mid, to, firstCut, comp);
		} else {
			secondCut = mid + (to - mid) / 2;
			firstCut = upperBound(from, mid, secondCut, comp);
		}
		long first2 = firstCut;
		long middle2 = mid;
		long last2 = secondCut;
		if (middle2 != first2 && middle2 != last2) {
			long first1 = first2;
			long last1 = middle2;
			while (first1 < --last1) swapper.swap(first1++, last1);
			first1 = middle2;
			last1 = last2;
			while (first1 < --last1) swapper.swap(first1++, last1);
			first1 = first2;
			last1 = last2;
			while (first1 < --last1) swapper.swap(first1++, last1);
		}
		mid = firstCut + (secondCut - mid);
		inPlaceMerge(from, firstCut, mid, comp, swapper);
		inPlaceMerge(mid, secondCut, to, comp, swapper);
	}

	/**
	 * Performs a binary search on an already sorted range: finds the first position where an element
	 * can be inserted without violating the ordering. Sorting is by a user-supplied comparison
	 * function.
	 *
	 * @param mid Beginning of the range.
	 * @param to One past the end of the range.
	 * @param firstCut Element to be searched for.
	 * @param comp Comparison function.
	 * @return The largest index i such that, for every j in the range {@code [first, i)},
	 *         {@code comp.apply(array[j], x)} is {@code true}.
	 */
	private static long lowerBound(long mid, final long to, final long firstCut, final LongComparator comp) {
		long len = to - mid;
		while (len > 0) {
			long half = len / 2;
			long middle = mid + half;
			if (comp.compare(middle, firstCut) < 0) {
				mid = middle + 1;
				len -= half + 1;
			} else {
				len = half;
			}
		}
		return mid;
	}

	/** Returns the index of the median of three elements. */
	private static long med3(final long a, final long b, final long c, final LongComparator comp) {
		final int ab = comp.compare(a, b);
		final int ac = comp.compare(a, c);
		final int bc = comp.compare(b, c);
		return (ab < 0 ? (bc < 0 ? b : ac < 0 ? c : a) : (bc > 0 ? b : ac > 0 ? c : a));
	}

	/**
	 * Sorts the specified range of elements using the specified big swapper and according to the order
	 * induced by the specified comparator using mergesort.
	 *
	 * <p>
	 * This sort is guaranteed to be <i>stable</i>: equal elements will not be reordered as a result of
	 * the sort. The sorting algorithm is an in-place mergesort that is significantly slower than a
	 * standard mergesort, as its running time is
	 * <i>O</i>(<var>n</var>&nbsp;(log&nbsp;<var>n</var>)<sup>2</sup>), but it does not allocate
	 * additional memory; as a result, it can be used as a generic sorting algorithm.
	 *
	 * @param from the index of the first element (inclusive) to be sorted.
	 * @param to the index of the last element (exclusive) to be sorted.
	 * @param comp the comparator to determine the order of the generic data (arguments are positions).
	 * @param swapper an object that knows how to swap the elements at any two positions.
	 */
	public static void mergeSort(final long from, final long to, final LongComparator comp, final BigSwapper swapper) {
		final long length = to - from;
		// Insertion sort on smallest arrays
		if (length < SMALL) {
			for (long i = from; i < to; i++) {
				for (long j = i; j > from && (comp.compare(j - 1, j) > 0); j--) {
					swapper.swap(j, j - 1);
				}
			}
			return;
		}
		// Recursively sort halves
		long mid = (from + to) >>> 1;
		mergeSort(from, mid, comp, swapper);
		mergeSort(mid, to, comp, swapper);
		// If list is already sorted, nothing left to do. This is an
		// optimization that results in faster sorts for nearly ordered lists.
		if (comp.compare(mid - 1, mid) <= 0) return;
		// Merge sorted halves
		inPlaceMerge(from, mid, to, comp, swapper);
	}

	/**
	 * Sorts the specified range of elements using the specified big swapper and according to the order
	 * induced by the specified comparator using quicksort.
	 *
	 * <p>
	 * The sorting algorithm is a tuned quicksort adapted from Jon L. Bentley and M. Douglas McIlroy,
	 * &ldquo;Engineering a Sort Function&rdquo;, <i>Software: Practice and Experience</i>, 23(11),
	 * pages 1249&minus;1265, 1993.
	 *
	 * @param from the index of the first element (inclusive) to be sorted.
	 * @param to the index of the last element (exclusive) to be sorted.
	 * @param comp the comparator to determine the order of the generic data.
	 * @param swapper an object that knows how to swap the elements at any two positions.
	 */
	public static void quickSort(final long from, final long to, final LongComparator comp, final BigSwapper swapper) {
		final long len = to - from;
		// Insertion sort on smallest arrays
		if (len < SMALL) {
			for (long i = from; i < to; i++) for (long j = i; j > from && (comp.compare(j - 1, j) > 0); j--) {
				swapper.swap(j, j - 1);
			}
			return;
		}
		// Choose a partition element, v
		long m = from + len / 2; // Small arrays, middle element
		if (len > SMALL) {
			long l = from, n = to - 1;
			if (len > MEDIUM) { // Big arrays, pseudomedian of 9
				long s = len / 8;
				l = med3(l, l + s, l + 2 * s, comp);
				m = med3(m - s, m, m + s, comp);
				n = med3(n - 2 * s, n - s, n, comp);
			}
			m = med3(l, m, n, comp); // Mid-size, med of 3
		}
		// long v = x[m];
		long a = from, b = a, c = to - 1, d = c;
		// Establish Invariant: v* (<v)* (>v)* v*
		while (true) {
			int comparison;
			while (b <= c && ((comparison = comp.compare(b, m)) <= 0)) {
				if (comparison == 0) {
					if (a == m) m = b; // moving target; DELTA to JDK !!!
					else if (b == m) m = a; // moving target; DELTA to JDK !!!
					swapper.swap(a++, b);
				}
				b++;
			}
			while (c >= b && ((comparison = comp.compare(c, m)) >= 0)) {
				if (comparison == 0) {
					if (c == m) m = d; // moving target; DELTA to JDK !!!
					else if (d == m) m = c; // moving target; DELTA to JDK !!!
					swapper.swap(c, d--);
				}
				c--;
			}
			if (b > c) break;
			if (b == m) m = d; // moving target; DELTA to JDK !!!
			else if (c == m) m = c; // moving target; DELTA to JDK !!!
			swapper.swap(b++, c--);
		}
		// Swap partition elements back to middle
		long s;
		long n = from + len;
		s = Math.min(a - from, b - a);
		vecSwap(swapper, from, b - s, s);
		s = Math.min(d - c, n - d - 1);
		vecSwap(swapper, b, n - s, s);
		// Recursively sort non-partition-elements
		if ((s = b - a) > 1) quickSort(from, from + s, comp, swapper);
		if ((s = d - c) > 1) quickSort(n - s, n, comp, swapper);
	}

	/**
	 * Performs a binary search on an already-sorted range: finds the last position where an element can
	 * be inserted without violating the ordering. Sorting is by a user-supplied comparison function.
	 *
	 * @param from Beginning of the range.
	 * @param mid One past the end of the range.
	 * @param secondCut Element to be searched for.
	 * @param comp Comparison function.
	 * @return The largest index i such that, for every j in the range {@code [first, i)},
	 *         {@code comp.apply(x, array[j])} is {@code false}.
	 */
	private static long upperBound(long from, final long mid, final long secondCut, final LongComparator comp) {
		long len = mid - from;
		while (len > 0) {
			long half = len / 2;
			long middle = from + half;
			if (comp.compare(secondCut, middle) < 0) {
				len = half;
			} else {
				from = middle + 1;
				len -= half + 1;
			}
		}
		return from;
	}

	/** Swaps x[a .. (a+n-1)] with x[b .. (b+n-1)]. */
	private static void vecSwap(final BigSwapper swapper, long from, long l, final long s) {
		for (int i = 0; i < s; i++, from++, l++) swapper.swap(from, l);
	}

	/* Generic definitions */
	/* Assertions (useful to generate conditional code) */
	/* Narrowing and widening */
	/* Current type and class (and size, if applicable) */
	/* Value methods */
	/* Interfaces (keys) */
	/* Interfaces (values) */
	/* Types and methods related to primitive-type support in the JDK */
	/* Abstract implementations (keys) */
	/* Abstract implementations (values) */
	/* Static containers (keys) */
	/* Static containers (values) */
	/* Implementations */
	/* Synchronized wrappers */
	/* Unmodifiable wrappers */
	/* Other wrappers */
	/* Methods (keys) */
	/* Methods (values) */
	/* Methods (keys/values) */
	/* Methods that have special names depending on keys (but the special names depend on values) */
	/* Equality */
	/* Object/Reference-only definitions (keys) */
	/* Primitive-type-only definitions (keys) */
	/* Object/Reference-only definitions (values) */
	/* START_OF_JAVA_SOURCE */
	/*
		* Copyright (C) 2004-2024 Sebastiano Vigna
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
	/**
	 * Returns the element of the given big array of specified index.
	 *
	 * @param array a big array.
	 * @param index a position in the big array.
	 * @return the element of the big array at the specified position.
	 */
	public static byte get(final byte[][] array, final long index) {
		return array[segment(index)][displacement(index)];
	}

	/**
	 * Sets the element of the given big array of specified index.
	 *
	 * @param array a big array.
	 * @param index a position in the big array.
	 * @param value the new value for the array element at the specified position.
	 */
	public static void set(final byte[][] array, final long index, byte value) {
		array[segment(index)][displacement(index)] = value;
	}

	/**
	 * Swaps the element of the given big array of specified indices.
	 *
	 * @param array a big array.
	 * @param first a position in the big array.
	 * @param second a position in the big array.
	 */
	public static void swap(final byte[][] array, final long first, final long second) {
		final byte t = array[segment(first)][displacement(first)];
		array[segment(first)][displacement(first)] = array[segment(second)][displacement(second)];
		array[segment(second)][displacement(second)] = t;
	}

	/**
	 * Reverses the order of the elements in the specified big array.
	 *
	 * @param a the big array to be reversed.
	 * @return {@code a}.
	 */
	public static byte[][] reverse(final byte[][] a) {
		final long length = length(a);
		for (long i = length / 2; i-- != 0;) swap(a, i, length - i - 1);
		return a;
	}

	/**
	 * Adds the specified increment the element of the given big array of specified index.
	 *
	 * @param array a big array.
	 * @param index a position in the big array.
	 * @param incr the increment
	 */
	public static void add(final byte[][] array, final long index, byte incr) {
		array[segment(index)][displacement(index)] += incr;
	}

	/**
	 * Multiplies by the specified factor the element of the given big array of specified index.
	 *
	 * @param array a big array.
	 * @param index a position in the big array.
	 * @param factor the factor
	 */
	public static void mul(final byte[][] array, final long index, byte factor) {
		array[segment(index)][displacement(index)] *= factor;
	}

	/**
	 * Increments the element of the given big array of specified index.
	 *
	 * @param array a big array.
	 * @param index a position in the big array.
	 */
	public static void incr(final byte[][] array, final long index) {
		array[segment(index)][displacement(index)]++;
	}

	/**
	 * Decrements the element of the given big array of specified index.
	 *
	 * @param array a big array.
	 * @param index a position in the big array.
	 */
	public static void decr(final byte[][] array, final long index) {
		array[segment(index)][displacement(index)]--;
	}

	/**
	 * Asserts that the provided big array is correct.
	 *
	 * @param array a big array.
	 * @throws IllegalStateException if the last segment is empty or longer than
	 *             {@link BigArrays#SEGMENT_SIZE}, or any other segment has length different from
	 *             {@link BigArrays#SEGMENT_SIZE}.
	 */
	public static void assertBigArray(final byte[][] array) {
		final int l = array.length;
		if (l == 0) return;
		for (int i = 0; i < l - 1; i++) if (array[i].length != SEGMENT_SIZE) throw new IllegalStateException("All segments except for the last one must be of length 2^" + Integer.toString(SEGMENT_SHIFT));
		if (array[l - 1].length > SEGMENT_SIZE) throw new IllegalStateException("The last segment must be of length at most 2^" + Integer.toString(SEGMENT_SHIFT));
		if (array[l - 1].length == 0 && l == 1) throw new IllegalStateException("The last segment must be of nonzero length");
	}

	/**
	 * Returns the length of the given big array.
	 *
	 * @param array a big array.
	 * @return the length of the given big array.
	 */
	public static long length(final byte[][] array) {
		final int length = array.length;
		return length == 0 ? 0 : start(length - 1) + array[length - 1].length;
	}

	/**
	 * Copies a big array from the specified source big array, beginning at the specified position, to
	 * the specified position of the destination big array. Handles correctly overlapping regions of the
	 * same big array.
	 *
	 * @param srcArray the source big array.
	 * @param srcPos the starting position in the source big array.
	 * @param destArray the destination big array.
	 * @param destPos the starting position in the destination data.
	 * @param length the number of elements to be copied.
	 */
	public static void copy(final byte[][] srcArray, final long srcPos, final byte[][] destArray, final long destPos, long length) {
		if (destPos <= srcPos) {
			int srcSegment = segment(srcPos);
			int destSegment = segment(destPos);
			int srcDispl = displacement(srcPos);
			int destDispl = displacement(destPos);
			while (length > 0) {
				final int l = (int)Math.min(length, Math.min(srcArray[srcSegment].length - srcDispl, destArray[destSegment].length - destDispl));
				if (l == 0) throw new ArrayIndexOutOfBoundsException();
				System.arraycopy(srcArray[srcSegment], srcDispl, destArray[destSegment], destDispl, l);
				if ((srcDispl += l) == SEGMENT_SIZE) {
					srcDispl = 0;
					srcSegment++;
				}
				if ((destDispl += l) == SEGMENT_SIZE) {
					destDispl = 0;
					destSegment++;
				}
				length -= l;
			}
		} else {
			int srcSegment = segment(srcPos + length);
			int destSegment = segment(destPos + length);
			int srcDispl = displacement(srcPos + length);
			int destDispl = displacement(destPos + length);
			while (length > 0) {
				if (srcDispl == 0) {
					srcDispl = SEGMENT_SIZE;
					srcSegment--;
				}
				if (destDispl == 0) {
					destDispl = SEGMENT_SIZE;
					destSegment--;
				}
				final int l = (int)Math.min(length, Math.min(srcDispl, destDispl));
				if (l == 0) throw new ArrayIndexOutOfBoundsException();
				System.arraycopy(srcArray[srcSegment], srcDispl - l, destArray[destSegment], destDispl - l, l);
				srcDispl -= l;
				destDispl -= l;
				length -= l;
			}
		}
	}

	/**
	 * Copies a big array from the specified source big array, beginning at the specified position, to
	 * the specified position of the destination array.
	 *
	 * @param srcArray the source big array.
	 * @param srcPos the starting position in the source big array.
	 * @param destArray the destination array.
	 * @param destPos the starting position in the destination data.
	 * @param length the number of elements to be copied.
	 */
	public static void copyFromBig(final byte[][] srcArray, final long srcPos, final byte[] destArray, int destPos, int length) {
		int srcSegment = segment(srcPos);
		int srcDispl = displacement(srcPos);
		while (length > 0) {
			final int l = Math.min(srcArray[srcSegment].length - srcDispl, length);
			if (l == 0) throw new ArrayIndexOutOfBoundsException();
			System.arraycopy(srcArray[srcSegment], srcDispl, destArray, destPos, l);
			if ((srcDispl += l) == SEGMENT_SIZE) {
				srcDispl = 0;
				srcSegment++;
			}
			destPos += l;
			length -= l;
		}
	}

	/**
	 * Copies an array from the specified source array, beginning at the specified position, to the
	 * specified position of the destination big array.
	 *
	 * @param srcArray the source array.
	 * @param srcPos the starting position in the source array.
	 * @param destArray the destination big array.
	 * @param destPos the starting position in the destination data.
	 * @param length the number of elements to be copied.
	 */
	public static void copyToBig(final byte[] srcArray, int srcPos, final byte[][] destArray, final long destPos, long length) {
		int destSegment = segment(destPos);
		int destDispl = displacement(destPos);
		while (length > 0) {
			final int l = (int)Math.min(destArray[destSegment].length - destDispl, length);
			if (l == 0) throw new ArrayIndexOutOfBoundsException();
			System.arraycopy(srcArray, srcPos, destArray[destSegment], destDispl, l);
			if ((destDispl += l) == SEGMENT_SIZE) {
				destDispl = 0;
				destSegment++;
			}
			srcPos += l;
			length -= l;
		}
	}

	/**
	 * Turns a standard array into a big array.
	 *
	 * <p>
	 * Note that the returned big array might contain as a segment the original array.
	 *
	 * @param array an array.
	 * @return a new big array with the same length and content of {@code array}.
	 */
	public static byte[][] wrap(final byte[] array) {
		if (array.length == 0) return ByteBigArrays.EMPTY_BIG_ARRAY;
		if (array.length <= SEGMENT_SIZE) return new byte[][] { array };
		final byte[][] bigArray = ByteBigArrays.newBigArray(array.length);
		for (int i = 0; i < bigArray.length; i++) System.arraycopy(array, (int)start(i), bigArray[i], 0, bigArray[i].length);
		return bigArray;
	}

	/**
	 * Ensures that a big array can contain the given number of entries.
	 *
	 * <p>
	 * If you cannot foresee whether this big array will need again to be enlarged, you should probably
	 * use {@code grow()} instead.
	 *
	 * <p>
	 * <strong>Warning:</strong> the returned array might use part of the segments of the original
	 * array, which must be considered read-only after calling this method.
	 *
	 * @param array a big array.
	 * @param length the new minimum length for this big array.
	 * @return {@code array}, if it contains {@code length} entries or more; otherwise, a big array with
	 *         {@code length} entries whose first {@code length(array)} entries are the same as those of
	 *         {@code array}.
	 */
	public static byte[][] ensureCapacity(final byte[][] array, final long length) {
		return ensureCapacity(array, length, length(array));
	}

	/**
	 * Forces a big array to contain the given number of entries, preserving just a part of the big
	 * array.
	 *
	 * <p>
	 * <strong>Warning:</strong> the returned array might use part of the segments of the original
	 * array, which must be considered read-only after calling this method.
	 *
	 * @param array a big array.
	 * @param length the new minimum length for this big array.
	 * @param preserve the number of elements of the big array that must be preserved in case a new
	 *            allocation is necessary.
	 * @return a big array with {@code length} entries whose first {@code preserve} entries are the same
	 *         as those of {@code array}.
	 */
	public static byte[][] forceCapacity(final byte[][] array, final long length, final long preserve) {
		ensureLength(length);
		final int valid = array.length - (array.length == 0 || array.length > 0 && array[array.length - 1].length == SEGMENT_SIZE ? 0 : 1);
		final int baseLength = (int)((length + SEGMENT_MASK) >>> SEGMENT_SHIFT);
		final byte[][] base = java.util.Arrays.copyOf(array, baseLength);
		final int residual = (int)(length & SEGMENT_MASK);
		if (residual != 0) {
			for (int i = valid; i < baseLength - 1; i++) base[i] = new byte[SEGMENT_SIZE];
			base[baseLength - 1] = new byte[residual];
		} else for (int i = valid; i < baseLength; i++) base[i] = new byte[SEGMENT_SIZE];
		if (preserve - (valid * (long)SEGMENT_SIZE) > 0) copy(array, valid * (long)SEGMENT_SIZE, base, valid * (long)SEGMENT_SIZE, preserve - (valid * (long)SEGMENT_SIZE));
		return base;
	}

	/**
	 * Ensures that a big array can contain the given number of entries, preserving just a part of the
	 * big array.
	 *
	 * <p>
	 * <strong>Warning:</strong> the returned array might use part of the segments of the original
	 * array, which must be considered read-only after calling this method.
	 *
	 * @param array a big array.
	 * @param length the new minimum length for this big array.
	 * @param preserve the number of elements of the big array that must be preserved in case a new
	 *            allocation is necessary.
	 * @return {@code array}, if it can contain {@code length} entries or more; otherwise, a big array
	 *         with {@code length} entries whose first {@code preserve} entries are the same as those of
	 *         {@code array}.
	 */
	public static byte[][] ensureCapacity(final byte[][] array, final long length, final long preserve) {
		return length > length(array) ? forceCapacity(array, length, preserve) : array;
	}

	/**
	 * Grows the given big array to the maximum between the given length and the current length
	 * increased by 50%, provided that the given length is larger than the current length.
	 *
	 * <p>
	 * If you want complete control on the big array growth, you should probably use
	 * {@code ensureCapacity()} instead.
	 *
	 * <p>
	 * <strong>Warning:</strong> the returned array might use part of the segments of the original
	 * array, which must be considered read-only after calling this method.
	 *
	 * @param array a big array.
	 * @param length the new minimum length for this big array.
	 * @return {@code array}, if it can contain {@code length} entries; otherwise, a big array with
	 *         max({@code length},{@code length(array)}/&phi;) entries whose first {@code length(array)}
	 *         entries are the same as those of {@code array}.
	 */
	public static byte[][] grow(final byte[][] array, final long length) {
		final long oldLength = length(array);
		return length > oldLength ? grow(array, length, oldLength) : array;
	}

	/**
	 * Grows the given big array to the maximum between the given length and the current length
	 * increased by 50%, provided that the given length is larger than the current length, preserving
	 * just a part of the big array.
	 *
	 * <p>
	 * If you want complete control on the big array growth, you should probably use
	 * {@code ensureCapacity()} instead.
	 *
	 * <p>
	 * <strong>Warning:</strong> the returned array might use part of the segments of the original
	 * array, which must be considered read-only after calling this method.
	 *
	 * @param array a big array.
	 * @param length the new minimum length for this big array.
	 * @param preserve the number of elements of the big array that must be preserved in case a new
	 *            allocation is necessary.
	 * @return {@code array}, if it can contain {@code length} entries; otherwise, a big array with
	 *         max({@code length},{@code length(array)}/&phi;) entries whose first {@code preserve}
	 *         entries are the same as those of {@code array}.
	 */
	public static byte[][] grow(final byte[][] array, final long length, final long preserve) {
		final long oldLength = length(array);
		return length > oldLength ? ensureCapacity(array, Math.max(oldLength + (oldLength >> 1), length), preserve) : array;
	}

	/**
	 * Trims the given big array to the given length.
	 *
	 * <p>
	 * <strong>Warning:</strong> the returned array might use part of the segments of the original
	 * array, which must be considered read-only after calling this method.
	 *
	 * @param array a big array.
	 * @param length the new maximum length for the big array.
	 * @return {@code array}, if it contains {@code length} entries or less; otherwise, a big array with
	 *         {@code length} entries whose entries are the same as the first {@code length} entries of
	 *         {@code array}.
	 *
	 */
	public static byte[][] trim(final byte[][] array, final long length) {
		ensureLength(length);
		final long oldLength = length(array);
		if (length >= oldLength) return array;
		final int baseLength = (int)((length + SEGMENT_MASK) >>> SEGMENT_SHIFT);
		final byte[][] base = java.util.Arrays.copyOf(array, baseLength);
		final int residual = (int)(length & SEGMENT_MASK);
		if (residual != 0) base[baseLength - 1] = ByteArrays.trim(base[baseLength - 1], residual);
		return base;
	}

	/**
	 * Sets the length of the given big array.
	 *
	 * <p>
	 * <strong>Warning:</strong> the returned array might use part of the segments of the original
	 * array, which must be considered read-only after calling this method.
	 *
	 * @param array a big array.
	 * @param length the new length for the big array.
	 * @return {@code array}, if it contains exactly {@code length} entries; otherwise, if it contains
	 *         <em>more</em> than {@code length} entries, a big array with {@code length} entries whose
	 *         entries are the same as the first {@code length} entries of {@code array}; otherwise, a
	 *         big array with {@code length} entries whose first {@code length(array)} entries are the
	 *         same as those of {@code array}.
	 *
	 */
	public static byte[][] setLength(final byte[][] array, final long length) {
		final long oldLength = length(array);
		if (length == oldLength) return array;
		if (length < oldLength) return trim(array, length);
		return ensureCapacity(array, length);
	}

	/**
	 * Returns a copy of a portion of a big array.
	 *
	 * @param array a big array.
	 * @param offset the first element to copy.
	 * @param length the number of elements to copy.
	 * @return a new big array containing {@code length} elements of {@code array} starting at
	 *         {@code offset}.
	 */
	public static byte[][] copy(final byte[][] array, final long offset, final long length) {
		ensureOffsetLength(array, offset, length);
		final byte[][] a = ByteBigArrays.newBigArray(length);
		copy(array, offset, a, 0, length);
		return a;
	}

	/**
	 * Returns a copy of a big array.
	 *
	 * @param array a big array.
	 * @return a copy of {@code array}.
	 */
	public static byte[][] copy(final byte[][] array) {
		final byte[][] base = array.clone();
		for (int i = base.length; i-- != 0;) base[i] = array[i].clone();
		return base;
	}

	/**
	 * Fills the given big array with the given value.
	 *
	 * <p>
	 * This method uses a backward loop. It is significantly faster than the corresponding method in
	 * {@link java.util.Arrays}.
	 *
	 * @param array a big array.
	 * @param value the new value for all elements of the big array.
	 */
	public static void fill(final byte[][] array, final byte value) {
		for (int i = array.length; i-- != 0;) java.util.Arrays.fill(array[i], value);
	}

	/**
	 * Fills a portion of the given big array with the given value.
	 *
	 * <p>
	 * If possible (i.e., {@code from} is 0) this method uses a backward loop. In this case, it is
	 * significantly faster than the corresponding method in {@link java.util.Arrays}.
	 *
	 * @param array a big array.
	 * @param from the starting index of the portion to fill.
	 * @param to the end index of the portion to fill.
	 * @param value the new value for all elements of the specified portion of the big array.
	 */
	public static void fill(final byte[][] array, final long from, long to, final byte value) {
		final long length = length(array);
		BigArrays.ensureFromTo(length, from, to);
		if (length == 0) return; // To avoid addressing array[0]
		int fromSegment = segment(from);
		int toSegment = segment(to);
		int fromDispl = displacement(from);
		int toDispl = displacement(to);
		if (fromSegment == toSegment) {
			java.util.Arrays.fill(array[fromSegment], fromDispl, toDispl, value);
			return;
		}
		if (toDispl != 0) java.util.Arrays.fill(array[toSegment], 0, toDispl, value);
		while (--toSegment > fromSegment) java.util.Arrays.fill(array[toSegment], value);
		java.util.Arrays.fill(array[fromSegment], fromDispl, SEGMENT_SIZE, value);
	}

	/**
	 * Returns true if the two big arrays are elementwise equal.
	 *
	 * <p>
	 * This method uses a backward loop. It is significantly faster than the corresponding method in
	 * {@link java.util.Arrays}.
	 *
	 * @param a1 a big array.
	 * @param a2 another big array.
	 * @return true if the two big arrays are of the same length, and their elements are equal.
	 */
	public static boolean equals(final byte[][] a1, final byte a2[][]) {
		if (length(a1) != length(a2)) return false;
		int i = a1.length, j;
		byte[] t, u;
		while (i-- != 0) {
			t = a1[i];
			u = a2[i];
			j = t.length;
			while (j-- != 0) if (!((t[j]) == (u[j]))) return false;
		}
		return true;
	}

	/* Returns a string representation of the contents of the specified big array.
	 *
	 * The string representation consists of a list of the big array's elements, enclosed in square brackets ("[]"). Adjacent elements are separated by the characters ", " (a comma followed by a space). Returns "null" if {@code a} is null.
	 * @param a the big array whose string representation to return.
	 * @return the string representation of {@code a}.
	 */
	public static String toString(final byte[][] a) {
		if (a == null) return "null";
		final long last = length(a) - 1;
		if (last == -1) return "[]";
		final StringBuilder b = new StringBuilder();
		b.append('[');
		for (long i = 0;; i++) {
			b.append(String.valueOf(get(a, i)));
			if (i == last) return b.append(']').toString();
			b.append(", ");
		}
	}

	/**
	 * Ensures that a range given by its first (inclusive) and last (exclusive) elements fits a big
	 * array.
	 *
	 * <p>
	 * This method may be used whenever a big array range check is needed.
	 *
	 * @param a a big array.
	 * @param from a start index (inclusive).
	 * @param to an end index (inclusive).
	 * @throws IllegalArgumentException if {@code from} is greater than {@code to}.
	 * @throws ArrayIndexOutOfBoundsException if {@code from} or {@code to} are greater than the big
	 *             array length or negative.
	 */
	public static void ensureFromTo(final byte[][] a, final long from, final long to) {
		BigArrays.ensureFromTo(length(a), from, to);
	}

	/**
	 * Ensures that a range given by an offset and a length fits a big array.
	 *
	 * <p>
	 * This method may be used whenever a big array range check is needed.
	 *
	 * @param a a big array.
	 * @param offset a start index.
	 * @param length a length (the number of elements in the range).
	 * @throws IllegalArgumentException if {@code length} is negative.
	 * @throws ArrayIndexOutOfBoundsException if {@code offset} is negative or
	 *             {@code offset}+{@code length} is greater than the big array length.
	 */
	public static void ensureOffsetLength(final byte[][] a, final long offset, final long length) {
		BigArrays.ensureOffsetLength(length(a), offset, length);
	}

	/**
	 * Ensures that two big arrays are of the same length.
	 *
	 * @param a a big array.
	 * @param b another big array.
	 * @throws IllegalArgumentException if the two argument arrays are not of the same length.
	 */
	public static void ensureSameLength(final byte[][] a, final byte[][] b) {
		if (length(a) != length(b)) throw new IllegalArgumentException("Array size mismatch: " + length(a) + " != " + length(b));
	}

	/**
	 * Shuffles the specified big array fragment using the specified pseudorandom number generator.
	 *
	 * @param a the big array to be shuffled.
	 * @param from the index of the first element (inclusive) to be shuffled.
	 * @param to the index of the last element (exclusive) to be shuffled.
	 * @param random a pseudorandom number generator.
	 * @return {@code a}.
	 */
	public static byte[][] shuffle(final byte[][] a, final long from, final long to, final Random random) {
		for (long i = to - from; i-- != 0;) {
			final long p = (random.nextLong() & 0x7FFFFFFFFFFFFFFFL) % (i + 1);
			final byte t = BigArrays.get(a, from + i);
			BigArrays.set(a, from + i, BigArrays.get(a, from + p));
			BigArrays.set(a, from + p, t);
		}
		return a;
	}

	/**
	 * Shuffles the specified big array using the specified pseudorandom number generator.
	 *
	 * @param a the big array to be shuffled.
	 * @param random a pseudorandom number generator.
	 * @return {@code a}.
	 */
	public static byte[][] shuffle(final byte[][] a, final Random random) {
		for (long i = length(a); i-- != 0;) {
			final long p = (random.nextLong() & 0x7FFFFFFFFFFFFFFFL) % (i + 1);
			final byte t = BigArrays.get(a, i);
			BigArrays.set(a, i, BigArrays.get(a, p));
			BigArrays.set(a, p, t);
		}
		return a;
	}

	/* Generic definitions */
	/* Assertions (useful to generate conditional code) */
	/* Narrowing and widening */
	/* Current type and class (and size, if applicable) */
	/* Value methods */
	/* Interfaces (keys) */
	/* Interfaces (values) */
	/* Types and methods related to primitive-type support in the JDK */
	/* Abstract implementations (keys) */
	/* Abstract implementations (values) */
	/* Static containers (keys) */
	/* Static containers (values) */
	/* Implementations */
	/* Synchronized wrappers */
	/* Unmodifiable wrappers */
	/* Other wrappers */
	/* Methods (keys) */
	/* Methods (values) */
	/* Methods (keys/values) */
	/* Methods that have special names depending on keys (but the special names depend on values) */
	/* Equality */
	/* Object/Reference-only definitions (keys) */
	/* Primitive-type-only definitions (keys) */
	/* Object/Reference-only definitions (values) */
	/* START_OF_JAVA_SOURCE */
	/*
		* Copyright (C) 2004-2024 Sebastiano Vigna
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
	/**
	 * Returns the element of the given big array of specified index.
	 *
	 * @param array a big array.
	 * @param index a position in the big array.
	 * @return the element of the big array at the specified position.
	 */
	public static int get(final int[][] array, final long index) {
		return array[segment(index)][displacement(index)];
	}

	/**
	 * Sets the element of the given big array of specified index.
	 *
	 * @param array a big array.
	 * @param index a position in the big array.
	 * @param value the new value for the array element at the specified position.
	 */
	public static void set(final int[][] array, final long index, int value) {
		array[segment(index)][displacement(index)] = value;
	}

	/**
	 * Returns the length of the given big atomic array.
	 *
	 * @param array a big atomic array.
	 * @return the length of the given big atomic array.
	 */
	public static long length(final AtomicIntegerArray[] array) {
		final int length = array.length;
		return length == 0 ? 0 : start(length - 1) + array[length - 1].length();
	}

	/**
	 * Returns the element of the given big atomic array of specified index.
	 *
	 * @param array a big atomic array.
	 * @param index a position in the big atomic array.
	 * @return the element of the big atomic array at the specified position.
	 */
	public static int get(final AtomicIntegerArray[] array, final long index) {
		return array[segment(index)].get(displacement(index));
	}

	/**
	 * Sets an element of the given big atomic array to a specified value
	 *
	 * @param array a big atomic array.
	 * @param index a position in the big atomic array.
	 * @param value a new value for the element of the big atomic array at the specified position.
	 */
	public static void set(final AtomicIntegerArray[] array, final long index, int value) {
		array[segment(index)].set(displacement(index), value);
	}

	/**
	 * Atomically sets an element of the given big atomic array to a specified value, returning the old
	 * value.
	 *
	 * @param array a big atomic array.
	 * @param index a position in the big atomic array.
	 * @param value a new value for the element of the big atomic array at the specified position.
	 * @return the old value of the element of the big atomic array at the specified position.
	 */
	public static int getAndSet(final AtomicIntegerArray[] array, final long index, int value) {
		return array[segment(index)].getAndSet(displacement(index), value);
	}

	/**
	 * Atomically adds a value to an element of the given big atomic array, returning the old value.
	 *
	 * @param array a big atomic array.
	 * @param index a position in the big atomic array.
	 * @param value a value to add to the element of the big atomic array at the specified position.
	 * @return the old value of the element of the big atomic array at the specified position.
	 */
	public static int getAndAdd(final AtomicIntegerArray[] array, final long index, int value) {
		return array[segment(index)].getAndAdd(displacement(index), value);
	}

	/**
	 * Atomically adds a value to an element of the given big atomic array, returning the new value.
	 *
	 * @param array a big atomic array.
	 * @param index a position in the big atomic array.
	 * @param value a value to add to the element of the big atomic array at the specified position.
	 * @return the new value of the element of the big atomic array at the specified position.
	 */
	public static int addAndGet(final AtomicIntegerArray[] array, final long index, int value) {
		return array[segment(index)].addAndGet(displacement(index), value);
	}

	/**
	 * Atomically increments an element of the given big atomic array, returning the old value.
	 *
	 * @param array a big atomic array.
	 * @param index a position in the big atomic array.
	 * @return the old value of the element of the big atomic array at the specified position.
	 */
	public static int getAndIncrement(final AtomicIntegerArray[] array, final long index) {
		return array[segment(index)].getAndDecrement(displacement(index));
	}

	/**
	 * Atomically increments an element of the given big atomic array, returning the new value.
	 *
	 * @param array a big atomic array.
	 * @param index a position in the big atomic array.
	 * @return the new value of the element of the big atomic array at the specified position.
	 */
	public static int incrementAndGet(final AtomicIntegerArray[] array, final long index) {
		return array[segment(index)].incrementAndGet(displacement(index));
	}

	/**
	 * Atomically decrements an element of the given big atomic array, returning the old value.
	 *
	 * @param array a big atomic array.
	 * @param index a position in the big atomic array.
	 * @return the old value of the element of the big atomic array at the specified position.
	 */
	public static int getAndDecrement(final AtomicIntegerArray[] array, final long index) {
		return array[segment(index)].getAndDecrement(displacement(index));
	}

	/**
	 * Atomically decrements an element of the given big atomic array, returning the new value.
	 *
	 * @param array a big atomic array.
	 * @param index a position in the big atomic array.
	 * @return the new value of the element of the big atomic array at the specified position.
	 */
	public static int decrementAndGet(final AtomicIntegerArray[] array, final long index) {
		return array[segment(index)].decrementAndGet(displacement(index));
	}

	/**
	 * Atomically sets an element of the given big atomic array of specified index to specified value,
	 * given the current value is equal to a given expected value.
	 *
	 * @param array a big atomic array.
	 * @param index a position in the big atomic array.
	 * @param expected an expected value for the element of the big atomic array at the specified
	 *            position.
	 * @param value a new value for the element of the big atomic array at the specified position.
	 * @return the element of the big atomic array at the specified position.
	 */
	public static boolean compareAndSet(final AtomicIntegerArray[] array, final long index, int expected, int value) {
		return array[segment(index)].compareAndSet(displacement(index), expected, value);
	}

	/**
	 * Swaps the element of the given big array of specified indices.
	 *
	 * @param array a big array.
	 * @param first a position in the big array.
	 * @param second a position in the big array.
	 */
	public static void swap(final int[][] array, final long first, final long second) {
		final int t = array[segment(first)][displacement(first)];
		array[segment(first)][displacement(first)] = array[segment(second)][displacement(second)];
		array[segment(second)][displacement(second)] = t;
	}

	/**
	 * Reverses the order of the elements in the specified big array.
	 *
	 * @param a the big array to be reversed.
	 * @return {@code a}.
	 */
	public static int[][] reverse(final int[][] a) {
		final long length = length(a);
		for (long i = length / 2; i-- != 0;) swap(a, i, length - i - 1);
		return a;
	}

	/**
	 * Adds the specified increment the element of the given big array of specified index.
	 *
	 * @param array a big array.
	 * @param index a position in the big array.
	 * @param incr the increment
	 */
	public static void add(final int[][] array, final long index, int incr) {
		array[segment(index)][displacement(index)] += incr;
	}

	/**
	 * Multiplies by the specified factor the element of the given big array of specified index.
	 *
	 * @param array a big array.
	 * @param index a position in the big array.
	 * @param factor the factor
	 */
	public static void mul(final int[][] array, final long index, int factor) {
		array[segment(index)][displacement(index)] *= factor;
	}

	/**
	 * Increments the element of the given big array of specified index.
	 *
	 * @param array a big array.
	 * @param index a position in the big array.
	 */
	public static void incr(final int[][] array, final long index) {
		array[segment(index)][displacement(index)]++;
	}

	/**
	 * Decrements the element of the given big array of specified index.
	 *
	 * @param array a big array.
	 * @param index a position in the big array.
	 */
	public static void decr(final int[][] array, final long index) {
		array[segment(index)][displacement(index)]--;
	}

	/**
	 * Asserts that the provided big array is correct.
	 *
	 * @param array a big array.
	 * @throws IllegalStateException if the last segment is empty or longer than
	 *             {@link BigArrays#SEGMENT_SIZE}, or any other segment has length different from
	 *             {@link BigArrays#SEGMENT_SIZE}.
	 */
	public static void assertBigArray(final int[][] array) {
		final int l = array.length;
		if (l == 0) return;
		for (int i = 0; i < l - 1; i++) if (array[i].length != SEGMENT_SIZE) throw new IllegalStateException("All segments except for the last one must be of length 2^" + Integer.toString(SEGMENT_SHIFT));
		if (array[l - 1].length > SEGMENT_SIZE) throw new IllegalStateException("The last segment must be of length at most 2^" + Integer.toString(SEGMENT_SHIFT));
		if (array[l - 1].length == 0 && l == 1) throw new IllegalStateException("The last segment must be of nonzero length");
	}

	/**
	 * Returns the length of the given big array.
	 *
	 * @param array a big array.
	 * @return the length of the given big array.
	 */
	public static long length(final int[][] array) {
		final int length = array.length;
		return length == 0 ? 0 : start(length - 1) + array[length - 1].length;
	}

	/**
	 * Copies a big array from the specified source big array, beginning at the specified position, to
	 * the specified position of the destination big array. Handles correctly overlapping regions of the
	 * same big array.
	 *
	 * @param srcArray the source big array.
	 * @param srcPos the starting position in the source big array.
	 * @param destArray the destination big array.
	 * @param destPos the starting position in the destination data.
	 * @param length the number of elements to be copied.
	 */
	public static void copy(final int[][] srcArray, final long srcPos, final int[][] destArray, final long destPos, long length) {
		if (destPos <= srcPos) {
			int srcSegment = segment(srcPos);
			int destSegment = segment(destPos);
			int srcDispl = displacement(srcPos);
			int destDispl = displacement(destPos);
			while (length > 0) {
				final int l = (int)Math.min(length, Math.min(srcArray[srcSegment].length - srcDispl, destArray[destSegment].length - destDispl));
				if (l == 0) throw new ArrayIndexOutOfBoundsException();
				System.arraycopy(srcArray[srcSegment], srcDispl, destArray[destSegment], destDispl, l);
				if ((srcDispl += l) == SEGMENT_SIZE) {
					srcDispl = 0;
					srcSegment++;
				}
				if ((destDispl += l) == SEGMENT_SIZE) {
					destDispl = 0;
					destSegment++;
				}
				length -= l;
			}
		} else {
			int srcSegment = segment(srcPos + length);
			int destSegment = segment(destPos + length);
			int srcDispl = displacement(srcPos + length);
			int destDispl = displacement(destPos + length);
			while (length > 0) {
				if (srcDispl == 0) {
					srcDispl = SEGMENT_SIZE;
					srcSegment--;
				}
				if (destDispl == 0) {
					destDispl = SEGMENT_SIZE;
					destSegment--;
				}
				final int l = (int)Math.min(length, Math.min(srcDispl, destDispl));
				if (l == 0) throw new ArrayIndexOutOfBoundsException();
				System.arraycopy(srcArray[srcSegment], srcDispl - l, destArray[destSegment], destDispl - l, l);
				srcDispl -= l;
				destDispl -= l;
				length -= l;
			}
		}
	}

	/**
	 * Copies a big array from the specified source big array, beginning at the specified position, to
	 * the specified position of the destination array.
	 *
	 * @param srcArray the source big array.
	 * @param srcPos the starting position in the source big array.
	 * @param destArray the destination array.
	 * @param destPos the starting position in the destination data.
	 * @param length the number of elements to be copied.
	 */
	public static void copyFromBig(final int[][] srcArray, final long srcPos, final int[] destArray, int destPos, int length) {
		int srcSegment = segment(srcPos);
		int srcDispl = displacement(srcPos);
		while (length > 0) {
			final int l = Math.min(srcArray[srcSegment].length - srcDispl, length);
			if (l == 0) throw new ArrayIndexOutOfBoundsException();
			System.arraycopy(srcArray[srcSegment], srcDispl, destArray, destPos, l);
			if ((srcDispl += l) == SEGMENT_SIZE) {
				srcDispl = 0;
				srcSegment++;
			}
			destPos += l;
			length -= l;
		}
	}

	/**
	 * Copies an array from the specified source array, beginning at the specified position, to the
	 * specified position of the destination big array.
	 *
	 * @param srcArray the source array.
	 * @param srcPos the starting position in the source array.
	 * @param destArray the destination big array.
	 * @param destPos the starting position in the destination data.
	 * @param length the number of elements to be copied.
	 */
	public static void copyToBig(final int[] srcArray, int srcPos, final int[][] destArray, final long destPos, long length) {
		int destSegment = segment(destPos);
		int destDispl = displacement(destPos);
		while (length > 0) {
			final int l = (int)Math.min(destArray[destSegment].length - destDispl, length);
			if (l == 0) throw new ArrayIndexOutOfBoundsException();
			System.arraycopy(srcArray, srcPos, destArray[destSegment], destDispl, l);
			if ((destDispl += l) == SEGMENT_SIZE) {
				destDispl = 0;
				destSegment++;
			}
			srcPos += l;
			length -= l;
		}
	}

	/**
	 * Turns a standard array into a big array.
	 *
	 * <p>
	 * Note that the returned big array might contain as a segment the original array.
	 *
	 * @param array an array.
	 * @return a new big array with the same length and content of {@code array}.
	 */
	public static int[][] wrap(final int[] array) {
		if (array.length == 0) return IntBigArrays.EMPTY_BIG_ARRAY;
		if (array.length <= SEGMENT_SIZE) return new int[][] { array };
		final int[][] bigArray = IntBigArrays.newBigArray(array.length);
		for (int i = 0; i < bigArray.length; i++) System.arraycopy(array, (int)start(i), bigArray[i], 0, bigArray[i].length);
		return bigArray;
	}

	/**
	 * Ensures that a big array can contain the given number of entries.
	 *
	 * <p>
	 * If you cannot foresee whether this big array will need again to be enlarged, you should probably
	 * use {@code grow()} instead.
	 *
	 * <p>
	 * <strong>Warning:</strong> the returned array might use part of the segments of the original
	 * array, which must be considered read-only after calling this method.
	 *
	 * @param array a big array.
	 * @param length the new minimum length for this big array.
	 * @return {@code array}, if it contains {@code length} entries or more; otherwise, a big array with
	 *         {@code length} entries whose first {@code length(array)} entries are the same as those of
	 *         {@code array}.
	 */
	public static int[][] ensureCapacity(final int[][] array, final long length) {
		return ensureCapacity(array, length, length(array));
	}

	/**
	 * Forces a big array to contain the given number of entries, preserving just a part of the big
	 * array.
	 *
	 * <p>
	 * <strong>Warning:</strong> the returned array might use part of the segments of the original
	 * array, which must be considered read-only after calling this method.
	 *
	 * @param array a big array.
	 * @param length the new minimum length for this big array.
	 * @param preserve the number of elements of the big array that must be preserved in case a new
	 *            allocation is necessary.
	 * @return a big array with {@code length} entries whose first {@code preserve} entries are the same
	 *         as those of {@code array}.
	 */
	public static int[][] forceCapacity(final int[][] array, final long length, final long preserve) {
		ensureLength(length);
		final int valid = array.length - (array.length == 0 || array.length > 0 && array[array.length - 1].length == SEGMENT_SIZE ? 0 : 1);
		final int baseLength = (int)((length + SEGMENT_MASK) >>> SEGMENT_SHIFT);
		final int[][] base = java.util.Arrays.copyOf(array, baseLength);
		final int residual = (int)(length & SEGMENT_MASK);
		if (residual != 0) {
			for (int i = valid; i < baseLength - 1; i++) base[i] = new int[SEGMENT_SIZE];
			base[baseLength - 1] = new int[residual];
		} else for (int i = valid; i < baseLength; i++) base[i] = new int[SEGMENT_SIZE];
		if (preserve - (valid * (long)SEGMENT_SIZE) > 0) copy(array, valid * (long)SEGMENT_SIZE, base, valid * (long)SEGMENT_SIZE, preserve - (valid * (long)SEGMENT_SIZE));
		return base;
	}

	/**
	 * Ensures that a big array can contain the given number of entries, preserving just a part of the
	 * big array.
	 *
	 * <p>
	 * <strong>Warning:</strong> the returned array might use part of the segments of the original
	 * array, which must be considered read-only after calling this method.
	 *
	 * @param array a big array.
	 * @param length the new minimum length for this big array.
	 * @param preserve the number of elements of the big array that must be preserved in case a new
	 *            allocation is necessary.
	 * @return {@code array}, if it can contain {@code length} entries or more; otherwise, a big array
	 *         with {@code length} entries whose first {@code preserve} entries are the same as those of
	 *         {@code array}.
	 */
	public static int[][] ensureCapacity(final int[][] array, final long length, final long preserve) {
		return length > length(array) ? forceCapacity(array, length, preserve) : array;
	}

	/**
	 * Grows the given big array to the maximum between the given length and the current length
	 * increased by 50%, provided that the given length is larger than the current length.
	 *
	 * <p>
	 * If you want complete control on the big array growth, you should probably use
	 * {@code ensureCapacity()} instead.
	 *
	 * <p>
	 * <strong>Warning:</strong> the returned array might use part of the segments of the original
	 * array, which must be considered read-only after calling this method.
	 *
	 * @param array a big array.
	 * @param length the new minimum length for this big array.
	 * @return {@code array}, if it can contain {@code length} entries; otherwise, a big array with
	 *         max({@code length},{@code length(array)}/&phi;) entries whose first {@code length(array)}
	 *         entries are the same as those of {@code array}.
	 */
	public static int[][] grow(final int[][] array, final long length) {
		final long oldLength = length(array);
		return length > oldLength ? grow(array, length, oldLength) : array;
	}

	/**
	 * Grows the given big array to the maximum between the given length and the current length
	 * increased by 50%, provided that the given length is larger than the current length, preserving
	 * just a part of the big array.
	 *
	 * <p>
	 * If you want complete control on the big array growth, you should probably use
	 * {@code ensureCapacity()} instead.
	 *
	 * <p>
	 * <strong>Warning:</strong> the returned array might use part of the segments of the original
	 * array, which must be considered read-only after calling this method.
	 *
	 * @param array a big array.
	 * @param length the new minimum length for this big array.
	 * @param preserve the number of elements of the big array that must be preserved in case a new
	 *            allocation is necessary.
	 * @return {@code array}, if it can contain {@code length} entries; otherwise, a big array with
	 *         max({@code length},{@code length(array)}/&phi;) entries whose first {@code preserve}
	 *         entries are the same as those of {@code array}.
	 */
	public static int[][] grow(final int[][] array, final long length, final long preserve) {
		final long oldLength = length(array);
		return length > oldLength ? ensureCapacity(array, Math.max(oldLength + (oldLength >> 1), length), preserve) : array;
	}

	/**
	 * Trims the given big array to the given length.
	 *
	 * <p>
	 * <strong>Warning:</strong> the returned array might use part of the segments of the original
	 * array, which must be considered read-only after calling this method.
	 *
	 * @param array a big array.
	 * @param length the new maximum length for the big array.
	 * @return {@code array}, if it contains {@code length} entries or less; otherwise, a big array with
	 *         {@code length} entries whose entries are the same as the first {@code length} entries of
	 *         {@code array}.
	 *
	 */
	public static int[][] trim(final int[][] array, final long length) {
		ensureLength(length);
		final long oldLength = length(array);
		if (length >= oldLength) return array;
		final int baseLength = (int)((length + SEGMENT_MASK) >>> SEGMENT_SHIFT);
		final int[][] base = java.util.Arrays.copyOf(array, baseLength);
		final int residual = (int)(length & SEGMENT_MASK);
		if (residual != 0) base[baseLength - 1] = IntArrays.trim(base[baseLength - 1], residual);
		return base;
	}

	/**
	 * Sets the length of the given big array.
	 *
	 * <p>
	 * <strong>Warning:</strong> the returned array might use part of the segments of the original
	 * array, which must be considered read-only after calling this method.
	 *
	 * @param array a big array.
	 * @param length the new length for the big array.
	 * @return {@code array}, if it contains exactly {@code length} entries; otherwise, if it contains
	 *         <em>more</em> than {@code length} entries, a big array with {@code length} entries whose
	 *         entries are the same as the first {@code length} entries of {@code array}; otherwise, a
	 *         big array with {@code length} entries whose first {@code length(array)} entries are the
	 *         same as those of {@code array}.
	 *
	 */
	public static int[][] setLength(final int[][] array, final long length) {
		final long oldLength = length(array);
		if (length == oldLength) return array;
		if (length < oldLength) return trim(array, length);
		return ensureCapacity(array, length);
	}

	/**
	 * Returns a copy of a portion of a big array.
	 *
	 * @param array a big array.
	 * @param offset the first element to copy.
	 * @param length the number of elements to copy.
	 * @return a new big array containing {@code length} elements of {@code array} starting at
	 *         {@code offset}.
	 */
	public static int[][] copy(final int[][] array, final long offset, final long length) {
		ensureOffsetLength(array, offset, length);
		final int[][] a = IntBigArrays.newBigArray(length);
		copy(array, offset, a, 0, length);
		return a;
	}

	/**
	 * Returns a copy of a big array.
	 *
	 * @param array a big array.
	 * @return a copy of {@code array}.
	 */
	public static int[][] copy(final int[][] array) {
		final int[][] base = array.clone();
		for (int i = base.length; i-- != 0;) base[i] = array[i].clone();
		return base;
	}

	/**
	 * Fills the given big array with the given value.
	 *
	 * <p>
	 * This method uses a backward loop. It is significantly faster than the corresponding method in
	 * {@link java.util.Arrays}.
	 *
	 * @param array a big array.
	 * @param value the new value for all elements of the big array.
	 */
	public static void fill(final int[][] array, final int value) {
		for (int i = array.length; i-- != 0;) java.util.Arrays.fill(array[i], value);
	}

	/**
	 * Fills a portion of the given big array with the given value.
	 *
	 * <p>
	 * If possible (i.e., {@code from} is 0) this method uses a backward loop. In this case, it is
	 * significantly faster than the corresponding method in {@link java.util.Arrays}.
	 *
	 * @param array a big array.
	 * @param from the starting index of the portion to fill.
	 * @param to the end index of the portion to fill.
	 * @param value the new value for all elements of the specified portion of the big array.
	 */
	public static void fill(final int[][] array, final long from, long to, final int value) {
		final long length = length(array);
		BigArrays.ensureFromTo(length, from, to);
		if (length == 0) return; // To avoid addressing array[0]
		int fromSegment = segment(from);
		int toSegment = segment(to);
		int fromDispl = displacement(from);
		int toDispl = displacement(to);
		if (fromSegment == toSegment) {
			java.util.Arrays.fill(array[fromSegment], fromDispl, toDispl, value);
			return;
		}
		if (toDispl != 0) java.util.Arrays.fill(array[toSegment], 0, toDispl, value);
		while (--toSegment > fromSegment) java.util.Arrays.fill(array[toSegment], value);
		java.util.Arrays.fill(array[fromSegment], fromDispl, SEGMENT_SIZE, value);
	}

	/**
	 * Returns true if the two big arrays are elementwise equal.
	 *
	 * <p>
	 * This method uses a backward loop. It is significantly faster than the corresponding method in
	 * {@link java.util.Arrays}.
	 *
	 * @param a1 a big array.
	 * @param a2 another big array.
	 * @return true if the two big arrays are of the same length, and their elements are equal.
	 */
	public static boolean equals(final int[][] a1, final int a2[][]) {
		if (length(a1) != length(a2)) return false;
		int i = a1.length, j;
		int[] t, u;
		while (i-- != 0) {
			t = a1[i];
			u = a2[i];
			j = t.length;
			while (j-- != 0) if (!((t[j]) == (u[j]))) return false;
		}
		return true;
	}

	/* Returns a string representation of the contents of the specified big array.
	 *
	 * The string representation consists of a list of the big array's elements, enclosed in square brackets ("[]"). Adjacent elements are separated by the characters ", " (a comma followed by a space). Returns "null" if {@code a} is null.
	 * @param a the big array whose string representation to return.
	 * @return the string representation of {@code a}.
	 */
	public static String toString(final int[][] a) {
		if (a == null) return "null";
		final long last = length(a) - 1;
		if (last == -1) return "[]";
		final StringBuilder b = new StringBuilder();
		b.append('[');
		for (long i = 0;; i++) {
			b.append(String.valueOf(get(a, i)));
			if (i == last) return b.append(']').toString();
			b.append(", ");
		}
	}

	/**
	 * Ensures that a range given by its first (inclusive) and last (exclusive) elements fits a big
	 * array.
	 *
	 * <p>
	 * This method may be used whenever a big array range check is needed.
	 *
	 * @param a a big array.
	 * @param from a start index (inclusive).
	 * @param to an end index (inclusive).
	 * @throws IllegalArgumentException if {@code from} is greater than {@code to}.
	 * @throws ArrayIndexOutOfBoundsException if {@code from} or {@code to} are greater than the big
	 *             array length or negative.
	 */
	public static void ensureFromTo(final int[][] a, final long from, final long to) {
		BigArrays.ensureFromTo(length(a), from, to);
	}

	/**
	 * Ensures that a range given by an offset and a length fits a big array.
	 *
	 * <p>
	 * This method may be used whenever a big array range check is needed.
	 *
	 * @param a a big array.
	 * @param offset a start index.
	 * @param length a length (the number of elements in the range).
	 * @throws IllegalArgumentException if {@code length} is negative.
	 * @throws ArrayIndexOutOfBoundsException if {@code offset} is negative or
	 *             {@code offset}+{@code length} is greater than the big array length.
	 */
	public static void ensureOffsetLength(final int[][] a, final long offset, final long length) {
		BigArrays.ensureOffsetLength(length(a), offset, length);
	}

	/**
	 * Ensures that two big arrays are of the same length.
	 *
	 * @param a a big array.
	 * @param b another big array.
	 * @throws IllegalArgumentException if the two argument arrays are not of the same length.
	 */
	public static void ensureSameLength(final int[][] a, final int[][] b) {
		if (length(a) != length(b)) throw new IllegalArgumentException("Array size mismatch: " + length(a) + " != " + length(b));
	}

	/**
	 * Shuffles the specified big array fragment using the specified pseudorandom number generator.
	 *
	 * @param a the big array to be shuffled.
	 * @param from the index of the first element (inclusive) to be shuffled.
	 * @param to the index of the last element (exclusive) to be shuffled.
	 * @param random a pseudorandom number generator.
	 * @return {@code a}.
	 */
	public static int[][] shuffle(final int[][] a, final long from, final long to, final Random random) {
		for (long i = to - from; i-- != 0;) {
			final long p = (random.nextLong() & 0x7FFFFFFFFFFFFFFFL) % (i + 1);
			final int t = BigArrays.get(a, from + i);
			BigArrays.set(a, from + i, BigArrays.get(a, from + p));
			BigArrays.set(a, from + p, t);
		}
		return a;
	}

	/**
	 * Shuffles the specified big array using the specified pseudorandom number generator.
	 *
	 * @param a the big array to be shuffled.
	 * @param random a pseudorandom number generator.
	 * @return {@code a}.
	 */
	public static int[][] shuffle(final int[][] a, final Random random) {
		for (long i = length(a); i-- != 0;) {
			final long p = (random.nextLong() & 0x7FFFFFFFFFFFFFFFL) % (i + 1);
			final int t = BigArrays.get(a, i);
			BigArrays.set(a, i, BigArrays.get(a, p));
			BigArrays.set(a, p, t);
		}
		return a;
	}

	/* Generic definitions */
	/* Assertions (useful to generate conditional code) */
	/* Narrowing and widening */
	/* Current type and class (and size, if applicable) */
	/* Value methods */
	/* Interfaces (keys) */
	/* Interfaces (values) */
	/* Types and methods related to primitive-type support in the JDK */
	/* Abstract implementations (keys) */
	/* Abstract implementations (values) */
	/* Static containers (keys) */
	/* Static containers (values) */
	/* Implementations */
	/* Synchronized wrappers */
	/* Unmodifiable wrappers */
	/* Other wrappers */
	/* Methods (keys) */
	/* Methods (values) */
	/* Methods (keys/values) */
	/* Methods that have special names depending on keys (but the special names depend on values) */
	/* Equality */
	/* Object/Reference-only definitions (keys) */
	/* Primitive-type-only definitions (keys) */
	/* Object/Reference-only definitions (values) */
	/* START_OF_JAVA_SOURCE */
	/*
		* Copyright (C) 2004-2024 Sebastiano Vigna
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
	/**
	 * Returns the element of the given big array of specified index.
	 *
	 * @param array a big array.
	 * @param index a position in the big array.
	 * @return the element of the big array at the specified position.
	 */
	public static long get(final long[][] array, final long index) {
		return array[segment(index)][displacement(index)];
	}

	/**
	 * Sets the element of the given big array of specified index.
	 *
	 * @param array a big array.
	 * @param index a position in the big array.
	 * @param value the new value for the array element at the specified position.
	 */
	public static void set(final long[][] array, final long index, long value) {
		array[segment(index)][displacement(index)] = value;
	}

	/**
	 * Returns the length of the given big atomic array.
	 *
	 * @param array a big atomic array.
	 * @return the length of the given big atomic array.
	 */
	public static long length(final AtomicLongArray[] array) {
		final int length = array.length;
		return length == 0 ? 0 : start(length - 1) + array[length - 1].length();
	}

	/**
	 * Returns the element of the given big atomic array of specified index.
	 *
	 * @param array a big atomic array.
	 * @param index a position in the big atomic array.
	 * @return the element of the big atomic array at the specified position.
	 */
	public static long get(final AtomicLongArray[] array, final long index) {
		return array[segment(index)].get(displacement(index));
	}

	/**
	 * Sets an element of the given big atomic array to a specified value
	 *
	 * @param array a big atomic array.
	 * @param index a position in the big atomic array.
	 * @param value a new value for the element of the big atomic array at the specified position.
	 */
	public static void set(final AtomicLongArray[] array, final long index, long value) {
		array[segment(index)].set(displacement(index), value);
	}

	/**
	 * Atomically sets an element of the given big atomic array to a specified value, returning the old
	 * value.
	 *
	 * @param array a big atomic array.
	 * @param index a position in the big atomic array.
	 * @param value a new value for the element of the big atomic array at the specified position.
	 * @return the old value of the element of the big atomic array at the specified position.
	 */
	public static long getAndSet(final AtomicLongArray[] array, final long index, long value) {
		return array[segment(index)].getAndSet(displacement(index), value);
	}

	/**
	 * Atomically adds a value to an element of the given big atomic array, returning the old value.
	 *
	 * @param array a big atomic array.
	 * @param index a position in the big atomic array.
	 * @param value a value to add to the element of the big atomic array at the specified position.
	 * @return the old value of the element of the big atomic array at the specified position.
	 */
	public static long getAndAdd(final AtomicLongArray[] array, final long index, long value) {
		return array[segment(index)].getAndAdd(displacement(index), value);
	}

	/**
	 * Atomically adds a value to an element of the given big atomic array, returning the new value.
	 *
	 * @param array a big atomic array.
	 * @param index a position in the big atomic array.
	 * @param value a value to add to the element of the big atomic array at the specified position.
	 * @return the new value of the element of the big atomic array at the specified position.
	 */
	public static long addAndGet(final AtomicLongArray[] array, final long index, long value) {
		return array[segment(index)].addAndGet(displacement(index), value);
	}

	/**
	 * Atomically increments an element of the given big atomic array, returning the old value.
	 *
	 * @param array a big atomic array.
	 * @param index a position in the big atomic array.
	 * @return the old value of the element of the big atomic array at the specified position.
	 */
	public static long getAndIncrement(final AtomicLongArray[] array, final long index) {
		return array[segment(index)].getAndDecrement(displacement(index));
	}

	/**
	 * Atomically increments an element of the given big atomic array, returning the new value.
	 *
	 * @param array a big atomic array.
	 * @param index a position in the big atomic array.
	 * @return the new value of the element of the big atomic array at the specified position.
	 */
	public static long incrementAndGet(final AtomicLongArray[] array, final long index) {
		return array[segment(index)].incrementAndGet(displacement(index));
	}

	/**
	 * Atomically decrements an element of the given big atomic array, returning the old value.
	 *
	 * @param array a big atomic array.
	 * @param index a position in the big atomic array.
	 * @return the old value of the element of the big atomic array at the specified position.
	 */
	public static long getAndDecrement(final AtomicLongArray[] array, final long index) {
		return array[segment(index)].getAndDecrement(displacement(index));
	}

	/**
	 * Atomically decrements an element of the given big atomic array, returning the new value.
	 *
	 * @param array a big atomic array.
	 * @param index a position in the big atomic array.
	 * @return the new value of the element of the big atomic array at the specified position.
	 */
	public static long decrementAndGet(final AtomicLongArray[] array, final long index) {
		return array[segment(index)].decrementAndGet(displacement(index));
	}

	/**
	 * Atomically sets an element of the given big atomic array of specified index to specified value,
	 * given the current value is equal to a given expected value.
	 *
	 * @param array a big atomic array.
	 * @param index a position in the big atomic array.
	 * @param expected an expected value for the element of the big atomic array at the specified
	 *            position.
	 * @param value a new value for the element of the big atomic array at the specified position.
	 * @return the element of the big atomic array at the specified position.
	 */
	public static boolean compareAndSet(final AtomicLongArray[] array, final long index, long expected, long value) {
		return array[segment(index)].compareAndSet(displacement(index), expected, value);
	}

	/**
	 * Swaps the element of the given big array of specified indices.
	 *
	 * @param array a big array.
	 * @param first a position in the big array.
	 * @param second a position in the big array.
	 */
	public static void swap(final long[][] array, final long first, final long second) {
		final long t = array[segment(first)][displacement(first)];
		array[segment(first)][displacement(first)] = array[segment(second)][displacement(second)];
		array[segment(second)][displacement(second)] = t;
	}

	/**
	 * Reverses the order of the elements in the specified big array.
	 *
	 * @param a the big array to be reversed.
	 * @return {@code a}.
	 */
	public static long[][] reverse(final long[][] a) {
		final long length = length(a);
		for (long i = length / 2; i-- != 0;) swap(a, i, length - i - 1);
		return a;
	}

	/**
	 * Adds the specified increment the element of the given big array of specified index.
	 *
	 * @param array a big array.
	 * @param index a position in the big array.
	 * @param incr the increment
	 */
	public static void add(final long[][] array, final long index, long incr) {
		array[segment(index)][displacement(index)] += incr;
	}

	/**
	 * Multiplies by the specified factor the element of the given big array of specified index.
	 *
	 * @param array a big array.
	 * @param index a position in the big array.
	 * @param factor the factor
	 */
	public static void mul(final long[][] array, final long index, long factor) {
		array[segment(index)][displacement(index)] *= factor;
	}

	/**
	 * Increments the element of the given big array of specified index.
	 *
	 * @param array a big array.
	 * @param index a position in the big array.
	 */
	public static void incr(final long[][] array, final long index) {
		array[segment(index)][displacement(index)]++;
	}

	/**
	 * Decrements the element of the given big array of specified index.
	 *
	 * @param array a big array.
	 * @param index a position in the big array.
	 */
	public static void decr(final long[][] array, final long index) {
		array[segment(index)][displacement(index)]--;
	}

	/**
	 * Asserts that the provided big array is correct.
	 *
	 * @param array a big array.
	 * @throws IllegalStateException if the last segment is empty or longer than
	 *             {@link BigArrays#SEGMENT_SIZE}, or any other segment has length different from
	 *             {@link BigArrays#SEGMENT_SIZE}.
	 */
	public static void assertBigArray(final long[][] array) {
		final int l = array.length;
		if (l == 0) return;
		for (int i = 0; i < l - 1; i++) if (array[i].length != SEGMENT_SIZE) throw new IllegalStateException("All segments except for the last one must be of length 2^" + Integer.toString(SEGMENT_SHIFT));
		if (array[l - 1].length > SEGMENT_SIZE) throw new IllegalStateException("The last segment must be of length at most 2^" + Integer.toString(SEGMENT_SHIFT));
		if (array[l - 1].length == 0 && l == 1) throw new IllegalStateException("The last segment must be of nonzero length");
	}

	/**
	 * Returns the length of the given big array.
	 *
	 * @param array a big array.
	 * @return the length of the given big array.
	 */
	public static long length(final long[][] array) {
		final int length = array.length;
		return length == 0 ? 0 : start(length - 1) + array[length - 1].length;
	}

	/**
	 * Copies a big array from the specified source big array, beginning at the specified position, to
	 * the specified position of the destination big array. Handles correctly overlapping regions of the
	 * same big array.
	 *
	 * @param srcArray the source big array.
	 * @param srcPos the starting position in the source big array.
	 * @param destArray the destination big array.
	 * @param destPos the starting position in the destination data.
	 * @param length the number of elements to be copied.
	 */
	public static void copy(final long[][] srcArray, final long srcPos, final long[][] destArray, final long destPos, long length) {
		if (destPos <= srcPos) {
			int srcSegment = segment(srcPos);
			int destSegment = segment(destPos);
			int srcDispl = displacement(srcPos);
			int destDispl = displacement(destPos);
			while (length > 0) {
				final int l = (int)Math.min(length, Math.min(srcArray[srcSegment].length - srcDispl, destArray[destSegment].length - destDispl));
				if (l == 0) throw new ArrayIndexOutOfBoundsException();
				System.arraycopy(srcArray[srcSegment], srcDispl, destArray[destSegment], destDispl, l);
				if ((srcDispl += l) == SEGMENT_SIZE) {
					srcDispl = 0;
					srcSegment++;
				}
				if ((destDispl += l) == SEGMENT_SIZE) {
					destDispl = 0;
					destSegment++;
				}
				length -= l;
			}
		} else {
			int srcSegment = segment(srcPos + length);
			int destSegment = segment(destPos + length);
			int srcDispl = displacement(srcPos + length);
			int destDispl = displacement(destPos + length);
			while (length > 0) {
				if (srcDispl == 0) {
					srcDispl = SEGMENT_SIZE;
					srcSegment--;
				}
				if (destDispl == 0) {
					destDispl = SEGMENT_SIZE;
					destSegment--;
				}
				final int l = (int)Math.min(length, Math.min(srcDispl, destDispl));
				if (l == 0) throw new ArrayIndexOutOfBoundsException();
				System.arraycopy(srcArray[srcSegment], srcDispl - l, destArray[destSegment], destDispl - l, l);
				srcDispl -= l;
				destDispl -= l;
				length -= l;
			}
		}
	}

	/**
	 * Copies a big array from the specified source big array, beginning at the specified position, to
	 * the specified position of the destination array.
	 *
	 * @param srcArray the source big array.
	 * @param srcPos the starting position in the source big array.
	 * @param destArray the destination array.
	 * @param destPos the starting position in the destination data.
	 * @param length the number of elements to be copied.
	 */
	public static void copyFromBig(final long[][] srcArray, final long srcPos, final long[] destArray, int destPos, int length) {
		int srcSegment = segment(srcPos);
		int srcDispl = displacement(srcPos);
		while (length > 0) {
			final int l = Math.min(srcArray[srcSegment].length - srcDispl, length);
			if (l == 0) throw new ArrayIndexOutOfBoundsException();
			System.arraycopy(srcArray[srcSegment], srcDispl, destArray, destPos, l);
			if ((srcDispl += l) == SEGMENT_SIZE) {
				srcDispl = 0;
				srcSegment++;
			}
			destPos += l;
			length -= l;
		}
	}

	/**
	 * Copies an array from the specified source array, beginning at the specified position, to the
	 * specified position of the destination big array.
	 *
	 * @param srcArray the source array.
	 * @param srcPos the starting position in the source array.
	 * @param destArray the destination big array.
	 * @param destPos the starting position in the destination data.
	 * @param length the number of elements to be copied.
	 */
	public static void copyToBig(final long[] srcArray, int srcPos, final long[][] destArray, final long destPos, long length) {
		int destSegment = segment(destPos);
		int destDispl = displacement(destPos);
		while (length > 0) {
			final int l = (int)Math.min(destArray[destSegment].length - destDispl, length);
			if (l == 0) throw new ArrayIndexOutOfBoundsException();
			System.arraycopy(srcArray, srcPos, destArray[destSegment], destDispl, l);
			if ((destDispl += l) == SEGMENT_SIZE) {
				destDispl = 0;
				destSegment++;
			}
			srcPos += l;
			length -= l;
		}
	}

	/**
	 * Turns a standard array into a big array.
	 *
	 * <p>
	 * Note that the returned big array might contain as a segment the original array.
	 *
	 * @param array an array.
	 * @return a new big array with the same length and content of {@code array}.
	 */
	public static long[][] wrap(final long[] array) {
		if (array.length == 0) return LongBigArrays.EMPTY_BIG_ARRAY;
		if (array.length <= SEGMENT_SIZE) return new long[][] { array };
		final long[][] bigArray = LongBigArrays.newBigArray(array.length);
		for (int i = 0; i < bigArray.length; i++) System.arraycopy(array, (int)start(i), bigArray[i], 0, bigArray[i].length);
		return bigArray;
	}

	/**
	 * Ensures that a big array can contain the given number of entries.
	 *
	 * <p>
	 * If you cannot foresee whether this big array will need again to be enlarged, you should probably
	 * use {@code grow()} instead.
	 *
	 * <p>
	 * <strong>Warning:</strong> the returned array might use part of the segments of the original
	 * array, which must be considered read-only after calling this method.
	 *
	 * @param array a big array.
	 * @param length the new minimum length for this big array.
	 * @return {@code array}, if it contains {@code length} entries or more; otherwise, a big array with
	 *         {@code length} entries whose first {@code length(array)} entries are the same as those of
	 *         {@code array}.
	 */
	public static long[][] ensureCapacity(final long[][] array, final long length) {
		return ensureCapacity(array, length, length(array));
	}

	/**
	 * Forces a big array to contain the given number of entries, preserving just a part of the big
	 * array.
	 *
	 * <p>
	 * <strong>Warning:</strong> the returned array might use part of the segments of the original
	 * array, which must be considered read-only after calling this method.
	 *
	 * @param array a big array.
	 * @param length the new minimum length for this big array.
	 * @param preserve the number of elements of the big array that must be preserved in case a new
	 *            allocation is necessary.
	 * @return a big array with {@code length} entries whose first {@code preserve} entries are the same
	 *         as those of {@code array}.
	 */
	public static long[][] forceCapacity(final long[][] array, final long length, final long preserve) {
		ensureLength(length);
		final int valid = array.length - (array.length == 0 || array.length > 0 && array[array.length - 1].length == SEGMENT_SIZE ? 0 : 1);
		final int baseLength = (int)((length + SEGMENT_MASK) >>> SEGMENT_SHIFT);
		final long[][] base = java.util.Arrays.copyOf(array, baseLength);
		final int residual = (int)(length & SEGMENT_MASK);
		if (residual != 0) {
			for (int i = valid; i < baseLength - 1; i++) base[i] = new long[SEGMENT_SIZE];
			base[baseLength - 1] = new long[residual];
		} else for (int i = valid; i < baseLength; i++) base[i] = new long[SEGMENT_SIZE];
		if (preserve - (valid * (long)SEGMENT_SIZE) > 0) copy(array, valid * (long)SEGMENT_SIZE, base, valid * (long)SEGMENT_SIZE, preserve - (valid * (long)SEGMENT_SIZE));
		return base;
	}

	/**
	 * Ensures that a big array can contain the given number of entries, preserving just a part of the
	 * big array.
	 *
	 * <p>
	 * <strong>Warning:</strong> the returned array might use part of the segments of the original
	 * array, which must be considered read-only after calling this method.
	 *
	 * @param array a big array.
	 * @param length the new minimum length for this big array.
	 * @param preserve the number of elements of the big array that must be preserved in case a new
	 *            allocation is necessary.
	 * @return {@code array}, if it can contain {@code length} entries or more; otherwise, a big array
	 *         with {@code length} entries whose first {@code preserve} entries are the same as those of
	 *         {@code array}.
	 */
	public static long[][] ensureCapacity(final long[][] array, final long length, final long preserve) {
		return length > length(array) ? forceCapacity(array, length, preserve) : array;
	}

	/**
	 * Grows the given big array to the maximum between the given length and the current length
	 * increased by 50%, provided that the given length is larger than the current length.
	 *
	 * <p>
	 * If you want complete control on the big array growth, you should probably use
	 * {@code ensureCapacity()} instead.
	 *
	 * <p>
	 * <strong>Warning:</strong> the returned array might use part of the segments of the original
	 * array, which must be considered read-only after calling this method.
	 *
	 * @param array a big array.
	 * @param length the new minimum length for this big array.
	 * @return {@code array}, if it can contain {@code length} entries; otherwise, a big array with
	 *         max({@code length},{@code length(array)}/&phi;) entries whose first {@code length(array)}
	 *         entries are the same as those of {@code array}.
	 */
	public static long[][] grow(final long[][] array, final long length) {
		final long oldLength = length(array);
		return length > oldLength ? grow(array, length, oldLength) : array;
	}

	/**
	 * Grows the given big array to the maximum between the given length and the current length
	 * increased by 50%, provided that the given length is larger than the current length, preserving
	 * just a part of the big array.
	 *
	 * <p>
	 * If you want complete control on the big array growth, you should probably use
	 * {@code ensureCapacity()} instead.
	 *
	 * <p>
	 * <strong>Warning:</strong> the returned array might use part of the segments of the original
	 * array, which must be considered read-only after calling this method.
	 *
	 * @param array a big array.
	 * @param length the new minimum length for this big array.
	 * @param preserve the number of elements of the big array that must be preserved in case a new
	 *            allocation is necessary.
	 * @return {@code array}, if it can contain {@code length} entries; otherwise, a big array with
	 *         max({@code length},{@code length(array)}/&phi;) entries whose first {@code preserve}
	 *         entries are the same as those of {@code array}.
	 */
	public static long[][] grow(final long[][] array, final long length, final long preserve) {
		final long oldLength = length(array);
		return length > oldLength ? ensureCapacity(array, Math.max(oldLength + (oldLength >> 1), length), preserve) : array;
	}

	/**
	 * Trims the given big array to the given length.
	 *
	 * <p>
	 * <strong>Warning:</strong> the returned array might use part of the segments of the original
	 * array, which must be considered read-only after calling this method.
	 *
	 * @param array a big array.
	 * @param length the new maximum length for the big array.
	 * @return {@code array}, if it contains {@code length} entries or less; otherwise, a big array with
	 *         {@code length} entries whose entries are the same as the first {@code length} entries of
	 *         {@code array}.
	 *
	 */
	public static long[][] trim(final long[][] array, final long length) {
		ensureLength(length);
		final long oldLength = length(array);
		if (length >= oldLength) return array;
		final int baseLength = (int)((length + SEGMENT_MASK) >>> SEGMENT_SHIFT);
		final long[][] base = java.util.Arrays.copyOf(array, baseLength);
		final int residual = (int)(length & SEGMENT_MASK);
		if (residual != 0) base[baseLength - 1] = LongArrays.trim(base[baseLength - 1], residual);
		return base;
	}

	/**
	 * Sets the length of the given big array.
	 *
	 * <p>
	 * <strong>Warning:</strong> the returned array might use part of the segments of the original
	 * array, which must be considered read-only after calling this method.
	 *
	 * @param array a big array.
	 * @param length the new length for the big array.
	 * @return {@code array}, if it contains exactly {@code length} entries; otherwise, if it contains
	 *         <em>more</em> than {@code length} entries, a big array with {@code length} entries whose
	 *         entries are the same as the first {@code length} entries of {@code array}; otherwise, a
	 *         big array with {@code length} entries whose first {@code length(array)} entries are the
	 *         same as those of {@code array}.
	 *
	 */
	public static long[][] setLength(final long[][] array, final long length) {
		final long oldLength = length(array);
		if (length == oldLength) return array;
		if (length < oldLength) return trim(array, length);
		return ensureCapacity(array, length);
	}

	/**
	 * Returns a copy of a portion of a big array.
	 *
	 * @param array a big array.
	 * @param offset the first element to copy.
	 * @param length the number of elements to copy.
	 * @return a new big array containing {@code length} elements of {@code array} starting at
	 *         {@code offset}.
	 */
	public static long[][] copy(final long[][] array, final long offset, final long length) {
		ensureOffsetLength(array, offset, length);
		final long[][] a = LongBigArrays.newBigArray(length);
		copy(array, offset, a, 0, length);
		return a;
	}

	/**
	 * Returns a copy of a big array.
	 *
	 * @param array a big array.
	 * @return a copy of {@code array}.
	 */
	public static long[][] copy(final long[][] array) {
		final long[][] base = array.clone();
		for (int i = base.length; i-- != 0;) base[i] = array[i].clone();
		return base;
	}

	/**
	 * Fills the given big array with the given value.
	 *
	 * <p>
	 * This method uses a backward loop. It is significantly faster than the corresponding method in
	 * {@link java.util.Arrays}.
	 *
	 * @param array a big array.
	 * @param value the new value for all elements of the big array.
	 */
	public static void fill(final long[][] array, final long value) {
		for (int i = array.length; i-- != 0;) java.util.Arrays.fill(array[i], value);
	}

	/**
	 * Fills a portion of the given big array with the given value.
	 *
	 * <p>
	 * If possible (i.e., {@code from} is 0) this method uses a backward loop. In this case, it is
	 * significantly faster than the corresponding method in {@link java.util.Arrays}.
	 *
	 * @param array a big array.
	 * @param from the starting index of the portion to fill.
	 * @param to the end index of the portion to fill.
	 * @param value the new value for all elements of the specified portion of the big array.
	 */
	public static void fill(final long[][] array, final long from, long to, final long value) {
		final long length = length(array);
		BigArrays.ensureFromTo(length, from, to);
		if (length == 0) return; // To avoid addressing array[0]
		int fromSegment = segment(from);
		int toSegment = segment(to);
		int fromDispl = displacement(from);
		int toDispl = displacement(to);
		if (fromSegment == toSegment) {
			java.util.Arrays.fill(array[fromSegment], fromDispl, toDispl, value);
			return;
		}
		if (toDispl != 0) java.util.Arrays.fill(array[toSegment], 0, toDispl, value);
		while (--toSegment > fromSegment) java.util.Arrays.fill(array[toSegment], value);
		java.util.Arrays.fill(array[fromSegment], fromDispl, SEGMENT_SIZE, value);
	}

	/**
	 * Returns true if the two big arrays are elementwise equal.
	 *
	 * <p>
	 * This method uses a backward loop. It is significantly faster than the corresponding method in
	 * {@link java.util.Arrays}.
	 *
	 * @param a1 a big array.
	 * @param a2 another big array.
	 * @return true if the two big arrays are of the same length, and their elements are equal.
	 */
	public static boolean equals(final long[][] a1, final long a2[][]) {
		if (length(a1) != length(a2)) return false;
		int i = a1.length, j;
		long[] t, u;
		while (i-- != 0) {
			t = a1[i];
			u = a2[i];
			j = t.length;
			while (j-- != 0) if (!((t[j]) == (u[j]))) return false;
		}
		return true;
	}

	/* Returns a string representation of the contents of the specified big array.
	 *
	 * The string representation consists of a list of the big array's elements, enclosed in square brackets ("[]"). Adjacent elements are separated by the characters ", " (a comma followed by a space). Returns "null" if {@code a} is null.
	 * @param a the big array whose string representation to return.
	 * @return the string representation of {@code a}.
	 */
	public static String toString(final long[][] a) {
		if (a == null) return "null";
		final long last = length(a) - 1;
		if (last == -1) return "[]";
		final StringBuilder b = new StringBuilder();
		b.append('[');
		for (long i = 0;; i++) {
			b.append(String.valueOf(get(a, i)));
			if (i == last) return b.append(']').toString();
			b.append(", ");
		}
	}

	/**
	 * Ensures that a range given by its first (inclusive) and last (exclusive) elements fits a big
	 * array.
	 *
	 * <p>
	 * This method may be used whenever a big array range check is needed.
	 *
	 * @param a a big array.
	 * @param from a start index (inclusive).
	 * @param to an end index (inclusive).
	 * @throws IllegalArgumentException if {@code from} is greater than {@code to}.
	 * @throws ArrayIndexOutOfBoundsException if {@code from} or {@code to} are greater than the big
	 *             array length or negative.
	 */
	public static void ensureFromTo(final long[][] a, final long from, final long to) {
		BigArrays.ensureFromTo(length(a), from, to);
	}

	/**
	 * Ensures that a range given by an offset and a length fits a big array.
	 *
	 * <p>
	 * This method may be used whenever a big array range check is needed.
	 *
	 * @param a a big array.
	 * @param offset a start index.
	 * @param length a length (the number of elements in the range).
	 * @throws IllegalArgumentException if {@code length} is negative.
	 * @throws ArrayIndexOutOfBoundsException if {@code offset} is negative or
	 *             {@code offset}+{@code length} is greater than the big array length.
	 */
	public static void ensureOffsetLength(final long[][] a, final long offset, final long length) {
		BigArrays.ensureOffsetLength(length(a), offset, length);
	}

	/**
	 * Ensures that two big arrays are of the same length.
	 *
	 * @param a a big array.
	 * @param b another big array.
	 * @throws IllegalArgumentException if the two argument arrays are not of the same length.
	 */
	public static void ensureSameLength(final long[][] a, final long[][] b) {
		if (length(a) != length(b)) throw new IllegalArgumentException("Array size mismatch: " + length(a) + " != " + length(b));
	}

	/**
	 * Shuffles the specified big array fragment using the specified pseudorandom number generator.
	 *
	 * @param a the big array to be shuffled.
	 * @param from the index of the first element (inclusive) to be shuffled.
	 * @param to the index of the last element (exclusive) to be shuffled.
	 * @param random a pseudorandom number generator.
	 * @return {@code a}.
	 */
	public static long[][] shuffle(final long[][] a, final long from, final long to, final Random random) {
		for (long i = to - from; i-- != 0;) {
			final long p = (random.nextLong() & 0x7FFFFFFFFFFFFFFFL) % (i + 1);
			final long t = BigArrays.get(a, from + i);
			BigArrays.set(a, from + i, BigArrays.get(a, from + p));
			BigArrays.set(a, from + p, t);
		}
		return a;
	}

	/**
	 * Shuffles the specified big array using the specified pseudorandom number generator.
	 *
	 * @param a the big array to be shuffled.
	 * @param random a pseudorandom number generator.
	 * @return {@code a}.
	 */
	public static long[][] shuffle(final long[][] a, final Random random) {
		for (long i = length(a); i-- != 0;) {
			final long p = (random.nextLong() & 0x7FFFFFFFFFFFFFFFL) % (i + 1);
			final long t = BigArrays.get(a, i);
			BigArrays.set(a, i, BigArrays.get(a, p));
			BigArrays.set(a, p, t);
		}
		return a;
	}

	/* Generic definitions */
	/* Assertions (useful to generate conditional code) */
	/* Narrowing and widening */
	/* Current type and class (and size, if applicable) */
	/* Value methods */
	/* Interfaces (keys) */
	/* Interfaces (values) */
	/* Types and methods related to primitive-type support in the JDK */
	/* Abstract implementations (keys) */
	/* Abstract implementations (values) */
	/* Static containers (keys) */
	/* Static containers (values) */
	/* Implementations */
	/* Synchronized wrappers */
	/* Unmodifiable wrappers */
	/* Other wrappers */
	/* Methods (keys) */
	/* Methods (values) */
	/* Methods (keys/values) */
	/* Methods that have special names depending on keys (but the special names depend on values) */
	/* Equality */
	/* Object/Reference-only definitions (keys) */
	/* Primitive-type-only definitions (keys) */
	/* Object/Reference-only definitions (values) */
	/* START_OF_JAVA_SOURCE */
	/*
		* Copyright (C) 2004-2024 Sebastiano Vigna
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
	/**
	 * Returns the element of the given big array of specified index.
	 *
	 * @param array a big array.
	 * @param index a position in the big array.
	 * @return the element of the big array at the specified position.
	 */
	public static double get(final double[][] array, final long index) {
		return array[segment(index)][displacement(index)];
	}

	/**
	 * Sets the element of the given big array of specified index.
	 *
	 * @param array a big array.
	 * @param index a position in the big array.
	 * @param value the new value for the array element at the specified position.
	 */
	public static void set(final double[][] array, final long index, double value) {
		array[segment(index)][displacement(index)] = value;
	}

	/**
	 * Swaps the element of the given big array of specified indices.
	 *
	 * @param array a big array.
	 * @param first a position in the big array.
	 * @param second a position in the big array.
	 */
	public static void swap(final double[][] array, final long first, final long second) {
		final double t = array[segment(first)][displacement(first)];
		array[segment(first)][displacement(first)] = array[segment(second)][displacement(second)];
		array[segment(second)][displacement(second)] = t;
	}

	/**
	 * Reverses the order of the elements in the specified big array.
	 *
	 * @param a the big array to be reversed.
	 * @return {@code a}.
	 */
	public static double[][] reverse(final double[][] a) {
		final long length = length(a);
		for (long i = length / 2; i-- != 0;) swap(a, i, length - i - 1);
		return a;
	}

	/**
	 * Adds the specified increment the element of the given big array of specified index.
	 *
	 * @param array a big array.
	 * @param index a position in the big array.
	 * @param incr the increment
	 */
	public static void add(final double[][] array, final long index, double incr) {
		array[segment(index)][displacement(index)] += incr;
	}

	/**
	 * Multiplies by the specified factor the element of the given big array of specified index.
	 *
	 * @param array a big array.
	 * @param index a position in the big array.
	 * @param factor the factor
	 */
	public static void mul(final double[][] array, final long index, double factor) {
		array[segment(index)][displacement(index)] *= factor;
	}

	/**
	 * Increments the element of the given big array of specified index.
	 *
	 * @param array a big array.
	 * @param index a position in the big array.
	 */
	public static void incr(final double[][] array, final long index) {
		array[segment(index)][displacement(index)]++;
	}

	/**
	 * Decrements the element of the given big array of specified index.
	 *
	 * @param array a big array.
	 * @param index a position in the big array.
	 */
	public static void decr(final double[][] array, final long index) {
		array[segment(index)][displacement(index)]--;
	}

	/**
	 * Asserts that the provided big array is correct.
	 *
	 * @param array a big array.
	 * @throws IllegalStateException if the last segment is empty or longer than
	 *             {@link BigArrays#SEGMENT_SIZE}, or any other segment has length different from
	 *             {@link BigArrays#SEGMENT_SIZE}.
	 */
	public static void assertBigArray(final double[][] array) {
		final int l = array.length;
		if (l == 0) return;
		for (int i = 0; i < l - 1; i++) if (array[i].length != SEGMENT_SIZE) throw new IllegalStateException("All segments except for the last one must be of length 2^" + Integer.toString(SEGMENT_SHIFT));
		if (array[l - 1].length > SEGMENT_SIZE) throw new IllegalStateException("The last segment must be of length at most 2^" + Integer.toString(SEGMENT_SHIFT));
		if (array[l - 1].length == 0 && l == 1) throw new IllegalStateException("The last segment must be of nonzero length");
	}

	/**
	 * Returns the length of the given big array.
	 *
	 * @param array a big array.
	 * @return the length of the given big array.
	 */
	public static long length(final double[][] array) {
		final int length = array.length;
		return length == 0 ? 0 : start(length - 1) + array[length - 1].length;
	}

	/**
	 * Copies a big array from the specified source big array, beginning at the specified position, to
	 * the specified position of the destination big array. Handles correctly overlapping regions of the
	 * same big array.
	 *
	 * @param srcArray the source big array.
	 * @param srcPos the starting position in the source big array.
	 * @param destArray the destination big array.
	 * @param destPos the starting position in the destination data.
	 * @param length the number of elements to be copied.
	 */
	public static void copy(final double[][] srcArray, final long srcPos, final double[][] destArray, final long destPos, long length) {
		if (destPos <= srcPos) {
			int srcSegment = segment(srcPos);
			int destSegment = segment(destPos);
			int srcDispl = displacement(srcPos);
			int destDispl = displacement(destPos);
			while (length > 0) {
				final int l = (int)Math.min(length, Math.min(srcArray[srcSegment].length - srcDispl, destArray[destSegment].length - destDispl));
				if (l == 0) throw new ArrayIndexOutOfBoundsException();
				System.arraycopy(srcArray[srcSegment], srcDispl, destArray[destSegment], destDispl, l);
				if ((srcDispl += l) == SEGMENT_SIZE) {
					srcDispl = 0;
					srcSegment++;
				}
				if ((destDispl += l) == SEGMENT_SIZE) {
					destDispl = 0;
					destSegment++;
				}
				length -= l;
			}
		} else {
			int srcSegment = segment(srcPos + length);
			int destSegment = segment(destPos + length);
			int srcDispl = displacement(srcPos + length);
			int destDispl = displacement(destPos + length);
			while (length > 0) {
				if (srcDispl == 0) {
					srcDispl = SEGMENT_SIZE;
					srcSegment--;
				}
				if (destDispl == 0) {
					destDispl = SEGMENT_SIZE;
					destSegment--;
				}
				final int l = (int)Math.min(length, Math.min(srcDispl, destDispl));
				if (l == 0) throw new ArrayIndexOutOfBoundsException();
				System.arraycopy(srcArray[srcSegment], srcDispl - l, destArray[destSegment], destDispl - l, l);
				srcDispl -= l;
				destDispl -= l;
				length -= l;
			}
		}
	}

	/**
	 * Copies a big array from the specified source big array, beginning at the specified position, to
	 * the specified position of the destination array.
	 *
	 * @param srcArray the source big array.
	 * @param srcPos the starting position in the source big array.
	 * @param destArray the destination array.
	 * @param destPos the starting position in the destination data.
	 * @param length the number of elements to be copied.
	 */
	public static void copyFromBig(final double[][] srcArray, final long srcPos, final double[] destArray, int destPos, int length) {
		int srcSegment = segment(srcPos);
		int srcDispl = displacement(srcPos);
		while (length > 0) {
			final int l = Math.min(srcArray[srcSegment].length - srcDispl, length);
			if (l == 0) throw new ArrayIndexOutOfBoundsException();
			System.arraycopy(srcArray[srcSegment], srcDispl, destArray, destPos, l);
			if ((srcDispl += l) == SEGMENT_SIZE) {
				srcDispl = 0;
				srcSegment++;
			}
			destPos += l;
			length -= l;
		}
	}

	/**
	 * Copies an array from the specified source array, beginning at the specified position, to the
	 * specified position of the destination big array.
	 *
	 * @param srcArray the source array.
	 * @param srcPos the starting position in the source array.
	 * @param destArray the destination big array.
	 * @param destPos the starting position in the destination data.
	 * @param length the number of elements to be copied.
	 */
	public static void copyToBig(final double[] srcArray, int srcPos, final double[][] destArray, final long destPos, long length) {
		int destSegment = segment(destPos);
		int destDispl = displacement(destPos);
		while (length > 0) {
			final int l = (int)Math.min(destArray[destSegment].length - destDispl, length);
			if (l == 0) throw new ArrayIndexOutOfBoundsException();
			System.arraycopy(srcArray, srcPos, destArray[destSegment], destDispl, l);
			if ((destDispl += l) == SEGMENT_SIZE) {
				destDispl = 0;
				destSegment++;
			}
			srcPos += l;
			length -= l;
		}
	}

	/**
	 * Turns a standard array into a big array.
	 *
	 * <p>
	 * Note that the returned big array might contain as a segment the original array.
	 *
	 * @param array an array.
	 * @return a new big array with the same length and content of {@code array}.
	 */
	public static double[][] wrap(final double[] array) {
		if (array.length == 0) return DoubleBigArrays.EMPTY_BIG_ARRAY;
		if (array.length <= SEGMENT_SIZE) return new double[][] { array };
		final double[][] bigArray = DoubleBigArrays.newBigArray(array.length);
		for (int i = 0; i < bigArray.length; i++) System.arraycopy(array, (int)start(i), bigArray[i], 0, bigArray[i].length);
		return bigArray;
	}

	/**
	 * Ensures that a big array can contain the given number of entries.
	 *
	 * <p>
	 * If you cannot foresee whether this big array will need again to be enlarged, you should probably
	 * use {@code grow()} instead.
	 *
	 * <p>
	 * <strong>Warning:</strong> the returned array might use part of the segments of the original
	 * array, which must be considered read-only after calling this method.
	 *
	 * @param array a big array.
	 * @param length the new minimum length for this big array.
	 * @return {@code array}, if it contains {@code length} entries or more; otherwise, a big array with
	 *         {@code length} entries whose first {@code length(array)} entries are the same as those of
	 *         {@code array}.
	 */
	public static double[][] ensureCapacity(final double[][] array, final long length) {
		return ensureCapacity(array, length, length(array));
	}

	/**
	 * Forces a big array to contain the given number of entries, preserving just a part of the big
	 * array.
	 *
	 * <p>
	 * <strong>Warning:</strong> the returned array might use part of the segments of the original
	 * array, which must be considered read-only after calling this method.
	 *
	 * @param array a big array.
	 * @param length the new minimum length for this big array.
	 * @param preserve the number of elements of the big array that must be preserved in case a new
	 *            allocation is necessary.
	 * @return a big array with {@code length} entries whose first {@code preserve} entries are the same
	 *         as those of {@code array}.
	 */
	public static double[][] forceCapacity(final double[][] array, final long length, final long preserve) {
		ensureLength(length);
		final int valid = array.length - (array.length == 0 || array.length > 0 && array[array.length - 1].length == SEGMENT_SIZE ? 0 : 1);
		final int baseLength = (int)((length + SEGMENT_MASK) >>> SEGMENT_SHIFT);
		final double[][] base = java.util.Arrays.copyOf(array, baseLength);
		final int residual = (int)(length & SEGMENT_MASK);
		if (residual != 0) {
			for (int i = valid; i < baseLength - 1; i++) base[i] = new double[SEGMENT_SIZE];
			base[baseLength - 1] = new double[residual];
		} else for (int i = valid; i < baseLength; i++) base[i] = new double[SEGMENT_SIZE];
		if (preserve - (valid * (long)SEGMENT_SIZE) > 0) copy(array, valid * (long)SEGMENT_SIZE, base, valid * (long)SEGMENT_SIZE, preserve - (valid * (long)SEGMENT_SIZE));
		return base;
	}

	/**
	 * Ensures that a big array can contain the given number of entries, preserving just a part of the
	 * big array.
	 *
	 * <p>
	 * <strong>Warning:</strong> the returned array might use part of the segments of the original
	 * array, which must be considered read-only after calling this method.
	 *
	 * @param array a big array.
	 * @param length the new minimum length for this big array.
	 * @param preserve the number of elements of the big array that must be preserved in case a new
	 *            allocation is necessary.
	 * @return {@code array}, if it can contain {@code length} entries or more; otherwise, a big array
	 *         with {@code length} entries whose first {@code preserve} entries are the same as those of
	 *         {@code array}.
	 */
	public static double[][] ensureCapacity(final double[][] array, final long length, final long preserve) {
		return length > length(array) ? forceCapacity(array, length, preserve) : array;
	}

	/**
	 * Grows the given big array to the maximum between the given length and the current length
	 * increased by 50%, provided that the given length is larger than the current length.
	 *
	 * <p>
	 * If you want complete control on the big array growth, you should probably use
	 * {@code ensureCapacity()} instead.
	 *
	 * <p>
	 * <strong>Warning:</strong> the returned array might use part of the segments of the original
	 * array, which must be considered read-only after calling this method.
	 *
	 * @param array a big array.
	 * @param length the new minimum length for this big array.
	 * @return {@code array}, if it can contain {@code length} entries; otherwise, a big array with
	 *         max({@code length},{@code length(array)}/&phi;) entries whose first {@code length(array)}
	 *         entries are the same as those of {@code array}.
	 */
	public static double[][] grow(final double[][] array, final long length) {
		final long oldLength = length(array);
		return length > oldLength ? grow(array, length, oldLength) : array;
	}

	/**
	 * Grows the given big array to the maximum between the given length and the current length
	 * increased by 50%, provided that the given length is larger than the current length, preserving
	 * just a part of the big array.
	 *
	 * <p>
	 * If you want complete control on the big array growth, you should probably use
	 * {@code ensureCapacity()} instead.
	 *
	 * <p>
	 * <strong>Warning:</strong> the returned array might use part of the segments of the original
	 * array, which must be considered read-only after calling this method.
	 *
	 * @param array a big array.
	 * @param length the new minimum length for this big array.
	 * @param preserve the number of elements of the big array that must be preserved in case a new
	 *            allocation is necessary.
	 * @return {@code array}, if it can contain {@code length} entries; otherwise, a big array with
	 *         max({@code length},{@code length(array)}/&phi;) entries whose first {@code preserve}
	 *         entries are the same as those of {@code array}.
	 */
	public static double[][] grow(final double[][] array, final long length, final long preserve) {
		final long oldLength = length(array);
		return length > oldLength ? ensureCapacity(array, Math.max(oldLength + (oldLength >> 1), length), preserve) : array;
	}

	/**
	 * Trims the given big array to the given length.
	 *
	 * <p>
	 * <strong>Warning:</strong> the returned array might use part of the segments of the original
	 * array, which must be considered read-only after calling this method.
	 *
	 * @param array a big array.
	 * @param length the new maximum length for the big array.
	 * @return {@code array}, if it contains {@code length} entries or less; otherwise, a big array with
	 *         {@code length} entries whose entries are the same as the first {@code length} entries of
	 *         {@code array}.
	 *
	 */
	public static double[][] trim(final double[][] array, final long length) {
		ensureLength(length);
		final long oldLength = length(array);
		if (length >= oldLength) return array;
		final int baseLength = (int)((length + SEGMENT_MASK) >>> SEGMENT_SHIFT);
		final double[][] base = java.util.Arrays.copyOf(array, baseLength);
		final int residual = (int)(length & SEGMENT_MASK);
		if (residual != 0) base[baseLength - 1] = DoubleArrays.trim(base[baseLength - 1], residual);
		return base;
	}

	/**
	 * Sets the length of the given big array.
	 *
	 * <p>
	 * <strong>Warning:</strong> the returned array might use part of the segments of the original
	 * array, which must be considered read-only after calling this method.
	 *
	 * @param array a big array.
	 * @param length the new length for the big array.
	 * @return {@code array}, if it contains exactly {@code length} entries; otherwise, if it contains
	 *         <em>more</em> than {@code length} entries, a big array with {@code length} entries whose
	 *         entries are the same as the first {@code length} entries of {@code array}; otherwise, a
	 *         big array with {@code length} entries whose first {@code length(array)} entries are the
	 *         same as those of {@code array}.
	 *
	 */
	public static double[][] setLength(final double[][] array, final long length) {
		final long oldLength = length(array);
		if (length == oldLength) return array;
		if (length < oldLength) return trim(array, length);
		return ensureCapacity(array, length);
	}

	/**
	 * Returns a copy of a portion of a big array.
	 *
	 * @param array a big array.
	 * @param offset the first element to copy.
	 * @param length the number of elements to copy.
	 * @return a new big array containing {@code length} elements of {@code array} starting at
	 *         {@code offset}.
	 */
	public static double[][] copy(final double[][] array, final long offset, final long length) {
		ensureOffsetLength(array, offset, length);
		final double[][] a = DoubleBigArrays.newBigArray(length);
		copy(array, offset, a, 0, length);
		return a;
	}

	/**
	 * Returns a copy of a big array.
	 *
	 * @param array a big array.
	 * @return a copy of {@code array}.
	 */
	public static double[][] copy(final double[][] array) {
		final double[][] base = array.clone();
		for (int i = base.length; i-- != 0;) base[i] = array[i].clone();
		return base;
	}

	/**
	 * Fills the given big array with the given value.
	 *
	 * <p>
	 * This method uses a backward loop. It is significantly faster than the corresponding method in
	 * {@link java.util.Arrays}.
	 *
	 * @param array a big array.
	 * @param value the new value for all elements of the big array.
	 */
	public static void fill(final double[][] array, final double value) {
		for (int i = array.length; i-- != 0;) java.util.Arrays.fill(array[i], value);
	}

	/**
	 * Fills a portion of the given big array with the given value.
	 *
	 * <p>
	 * If possible (i.e., {@code from} is 0) this method uses a backward loop. In this case, it is
	 * significantly faster than the corresponding method in {@link java.util.Arrays}.
	 *
	 * @param array a big array.
	 * @param from the starting index of the portion to fill.
	 * @param to the end index of the portion to fill.
	 * @param value the new value for all elements of the specified portion of the big array.
	 */
	public static void fill(final double[][] array, final long from, long to, final double value) {
		final long length = length(array);
		BigArrays.ensureFromTo(length, from, to);
		if (length == 0) return; // To avoid addressing array[0]
		int fromSegment = segment(from);
		int toSegment = segment(to);
		int fromDispl = displacement(from);
		int toDispl = displacement(to);
		if (fromSegment == toSegment) {
			java.util.Arrays.fill(array[fromSegment], fromDispl, toDispl, value);
			return;
		}
		if (toDispl != 0) java.util.Arrays.fill(array[toSegment], 0, toDispl, value);
		while (--toSegment > fromSegment) java.util.Arrays.fill(array[toSegment], value);
		java.util.Arrays.fill(array[fromSegment], fromDispl, SEGMENT_SIZE, value);
	}

	/**
	 * Returns true if the two big arrays are elementwise equal.
	 *
	 * <p>
	 * This method uses a backward loop. It is significantly faster than the corresponding method in
	 * {@link java.util.Arrays}.
	 *
	 * @param a1 a big array.
	 * @param a2 another big array.
	 * @return true if the two big arrays are of the same length, and their elements are equal.
	 */
	public static boolean equals(final double[][] a1, final double a2[][]) {
		if (length(a1) != length(a2)) return false;
		int i = a1.length, j;
		double[] t, u;
		while (i-- != 0) {
			t = a1[i];
			u = a2[i];
			j = t.length;
			while (j-- != 0) if (!(Double.doubleToLongBits(t[j]) == Double.doubleToLongBits(u[j]))) return false;
		}
		return true;
	}

	/* Returns a string representation of the contents of the specified big array.
	 *
	 * The string representation consists of a list of the big array's elements, enclosed in square brackets ("[]"). Adjacent elements are separated by the characters ", " (a comma followed by a space). Returns "null" if {@code a} is null.
	 * @param a the big array whose string representation to return.
	 * @return the string representation of {@code a}.
	 */
	public static String toString(final double[][] a) {
		if (a == null) return "null";
		final long last = length(a) - 1;
		if (last == -1) return "[]";
		final StringBuilder b = new StringBuilder();
		b.append('[');
		for (long i = 0;; i++) {
			b.append(String.valueOf(get(a, i)));
			if (i == last) return b.append(']').toString();
			b.append(", ");
		}
	}

	/**
	 * Ensures that a range given by its first (inclusive) and last (exclusive) elements fits a big
	 * array.
	 *
	 * <p>
	 * This method may be used whenever a big array range check is needed.
	 *
	 * @param a a big array.
	 * @param from a start index (inclusive).
	 * @param to an end index (inclusive).
	 * @throws IllegalArgumentException if {@code from} is greater than {@code to}.
	 * @throws ArrayIndexOutOfBoundsException if {@code from} or {@code to} are greater than the big
	 *             array length or negative.
	 */
	public static void ensureFromTo(final double[][] a, final long from, final long to) {
		BigArrays.ensureFromTo(length(a), from, to);
	}

	/**
	 * Ensures that a range given by an offset and a length fits a big array.
	 *
	 * <p>
	 * This method may be used whenever a big array range check is needed.
	 *
	 * @param a a big array.
	 * @param offset a start index.
	 * @param length a length (the number of elements in the range).
	 * @throws IllegalArgumentException if {@code length} is negative.
	 * @throws ArrayIndexOutOfBoundsException if {@code offset} is negative or
	 *             {@code offset}+{@code length} is greater than the big array length.
	 */
	public static void ensureOffsetLength(final double[][] a, final long offset, final long length) {
		BigArrays.ensureOffsetLength(length(a), offset, length);
	}

	/**
	 * Ensures that two big arrays are of the same length.
	 *
	 * @param a a big array.
	 * @param b another big array.
	 * @throws IllegalArgumentException if the two argument arrays are not of the same length.
	 */
	public static void ensureSameLength(final double[][] a, final double[][] b) {
		if (length(a) != length(b)) throw new IllegalArgumentException("Array size mismatch: " + length(a) + " != " + length(b));
	}

	/**
	 * Shuffles the specified big array fragment using the specified pseudorandom number generator.
	 *
	 * @param a the big array to be shuffled.
	 * @param from the index of the first element (inclusive) to be shuffled.
	 * @param to the index of the last element (exclusive) to be shuffled.
	 * @param random a pseudorandom number generator.
	 * @return {@code a}.
	 */
	public static double[][] shuffle(final double[][] a, final long from, final long to, final Random random) {
		for (long i = to - from; i-- != 0;) {
			final long p = (random.nextLong() & 0x7FFFFFFFFFFFFFFFL) % (i + 1);
			final double t = BigArrays.get(a, from + i);
			BigArrays.set(a, from + i, BigArrays.get(a, from + p));
			BigArrays.set(a, from + p, t);
		}
		return a;
	}

	/**
	 * Shuffles the specified big array using the specified pseudorandom number generator.
	 *
	 * @param a the big array to be shuffled.
	 * @param random a pseudorandom number generator.
	 * @return {@code a}.
	 */
	public static double[][] shuffle(final double[][] a, final Random random) {
		for (long i = length(a); i-- != 0;) {
			final long p = (random.nextLong() & 0x7FFFFFFFFFFFFFFFL) % (i + 1);
			final double t = BigArrays.get(a, i);
			BigArrays.set(a, i, BigArrays.get(a, p));
			BigArrays.set(a, p, t);
		}
		return a;
	}

	/* Generic definitions */
	/* Assertions (useful to generate conditional code) */
	/* Narrowing and widening */
	/* Current type and class (and size, if applicable) */
	/* Value methods */
	/* Interfaces (keys) */
	/* Interfaces (values) */
	/* Types and methods related to primitive-type support in the JDK */
	/* Abstract implementations (keys) */
	/* Abstract implementations (values) */
	/* Static containers (keys) */
	/* Static containers (values) */
	/* Implementations */
	/* Synchronized wrappers */
	/* Unmodifiable wrappers */
	/* Other wrappers */
	/* Methods (keys) */
	/* Methods (values) */
	/* Methods (keys/values) */
	/* Methods that have special names depending on keys (but the special names depend on values) */
	/* Equality */
	/* Object/Reference-only definitions (keys) */
	/* Primitive-type-only definitions (keys) */
	/* Object/Reference-only definitions (values) */
	/* START_OF_JAVA_SOURCE */
	/*
		* Copyright (C) 2004-2024 Sebastiano Vigna
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
	/**
	 * Returns the element of the given big array of specified index.
	 *
	 * @param array a big array.
	 * @param index a position in the big array.
	 * @return the element of the big array at the specified position.
	 */
	public static boolean get(final boolean[][] array, final long index) {
		return array[segment(index)][displacement(index)];
	}

	/**
	 * Sets the element of the given big array of specified index.
	 *
	 * @param array a big array.
	 * @param index a position in the big array.
	 * @param value the new value for the array element at the specified position.
	 */
	public static void set(final boolean[][] array, final long index, boolean value) {
		array[segment(index)][displacement(index)] = value;
	}

	/**
	 * Swaps the element of the given big array of specified indices.
	 *
	 * @param array a big array.
	 * @param first a position in the big array.
	 * @param second a position in the big array.
	 */
	public static void swap(final boolean[][] array, final long first, final long second) {
		final boolean t = array[segment(first)][displacement(first)];
		array[segment(first)][displacement(first)] = array[segment(second)][displacement(second)];
		array[segment(second)][displacement(second)] = t;
	}

	/**
	 * Reverses the order of the elements in the specified big array.
	 *
	 * @param a the big array to be reversed.
	 * @return {@code a}.
	 */
	public static boolean[][] reverse(final boolean[][] a) {
		final long length = length(a);
		for (long i = length / 2; i-- != 0;) swap(a, i, length - i - 1);
		return a;
	}

	/**
	 * Asserts that the provided big array is correct.
	 *
	 * @param array a big array.
	 * @throws IllegalStateException if the last segment is empty or longer than
	 *             {@link BigArrays#SEGMENT_SIZE}, or any other segment has length different from
	 *             {@link BigArrays#SEGMENT_SIZE}.
	 */
	public static void assertBigArray(final boolean[][] array) {
		final int l = array.length;
		if (l == 0) return;
		for (int i = 0; i < l - 1; i++) if (array[i].length != SEGMENT_SIZE) throw new IllegalStateException("All segments except for the last one must be of length 2^" + Integer.toString(SEGMENT_SHIFT));
		if (array[l - 1].length > SEGMENT_SIZE) throw new IllegalStateException("The last segment must be of length at most 2^" + Integer.toString(SEGMENT_SHIFT));
		if (array[l - 1].length == 0 && l == 1) throw new IllegalStateException("The last segment must be of nonzero length");
	}

	/**
	 * Returns the length of the given big array.
	 *
	 * @param array a big array.
	 * @return the length of the given big array.
	 */
	public static long length(final boolean[][] array) {
		final int length = array.length;
		return length == 0 ? 0 : start(length - 1) + array[length - 1].length;
	}

	/**
	 * Copies a big array from the specified source big array, beginning at the specified position, to
	 * the specified position of the destination big array. Handles correctly overlapping regions of the
	 * same big array.
	 *
	 * @param srcArray the source big array.
	 * @param srcPos the starting position in the source big array.
	 * @param destArray the destination big array.
	 * @param destPos the starting position in the destination data.
	 * @param length the number of elements to be copied.
	 */
	public static void copy(final boolean[][] srcArray, final long srcPos, final boolean[][] destArray, final long destPos, long length) {
		if (destPos <= srcPos) {
			int srcSegment = segment(srcPos);
			int destSegment = segment(destPos);
			int srcDispl = displacement(srcPos);
			int destDispl = displacement(destPos);
			while (length > 0) {
				final int l = (int)Math.min(length, Math.min(srcArray[srcSegment].length - srcDispl, destArray[destSegment].length - destDispl));
				if (l == 0) throw new ArrayIndexOutOfBoundsException();
				System.arraycopy(srcArray[srcSegment], srcDispl, destArray[destSegment], destDispl, l);
				if ((srcDispl += l) == SEGMENT_SIZE) {
					srcDispl = 0;
					srcSegment++;
				}
				if ((destDispl += l) == SEGMENT_SIZE) {
					destDispl = 0;
					destSegment++;
				}
				length -= l;
			}
		} else {
			int srcSegment = segment(srcPos + length);
			int destSegment = segment(destPos + length);
			int srcDispl = displacement(srcPos + length);
			int destDispl = displacement(destPos + length);
			while (length > 0) {
				if (srcDispl == 0) {
					srcDispl = SEGMENT_SIZE;
					srcSegment--;
				}
				if (destDispl == 0) {
					destDispl = SEGMENT_SIZE;
					destSegment--;
				}
				final int l = (int)Math.min(length, Math.min(srcDispl, destDispl));
				if (l == 0) throw new ArrayIndexOutOfBoundsException();
				System.arraycopy(srcArray[srcSegment], srcDispl - l, destArray[destSegment], destDispl - l, l);
				srcDispl -= l;
				destDispl -= l;
				length -= l;
			}
		}
	}

	/**
	 * Copies a big array from the specified source big array, beginning at the specified position, to
	 * the specified position of the destination array.
	 *
	 * @param srcArray the source big array.
	 * @param srcPos the starting position in the source big array.
	 * @param destArray the destination array.
	 * @param destPos the starting position in the destination data.
	 * @param length the number of elements to be copied.
	 */
	public static void copyFromBig(final boolean[][] srcArray, final long srcPos, final boolean[] destArray, int destPos, int length) {
		int srcSegment = segment(srcPos);
		int srcDispl = displacement(srcPos);
		while (length > 0) {
			final int l = Math.min(srcArray[srcSegment].length - srcDispl, length);
			if (l == 0) throw new ArrayIndexOutOfBoundsException();
			System.arraycopy(srcArray[srcSegment], srcDispl, destArray, destPos, l);
			if ((srcDispl += l) == SEGMENT_SIZE) {
				srcDispl = 0;
				srcSegment++;
			}
			destPos += l;
			length -= l;
		}
	}

	/**
	 * Copies an array from the specified source array, beginning at the specified position, to the
	 * specified position of the destination big array.
	 *
	 * @param srcArray the source array.
	 * @param srcPos the starting position in the source array.
	 * @param destArray the destination big array.
	 * @param destPos the starting position in the destination data.
	 * @param length the number of elements to be copied.
	 */
	public static void copyToBig(final boolean[] srcArray, int srcPos, final boolean[][] destArray, final long destPos, long length) {
		int destSegment = segment(destPos);
		int destDispl = displacement(destPos);
		while (length > 0) {
			final int l = (int)Math.min(destArray[destSegment].length - destDispl, length);
			if (l == 0) throw new ArrayIndexOutOfBoundsException();
			System.arraycopy(srcArray, srcPos, destArray[destSegment], destDispl, l);
			if ((destDispl += l) == SEGMENT_SIZE) {
				destDispl = 0;
				destSegment++;
			}
			srcPos += l;
			length -= l;
		}
	}

	/**
	 * Turns a standard array into a big array.
	 *
	 * <p>
	 * Note that the returned big array might contain as a segment the original array.
	 *
	 * @param array an array.
	 * @return a new big array with the same length and content of {@code array}.
	 */
	public static boolean[][] wrap(final boolean[] array) {
		if (array.length == 0) return BooleanBigArrays.EMPTY_BIG_ARRAY;
		if (array.length <= SEGMENT_SIZE) return new boolean[][] { array };
		final boolean[][] bigArray = BooleanBigArrays.newBigArray(array.length);
		for (int i = 0; i < bigArray.length; i++) System.arraycopy(array, (int)start(i), bigArray[i], 0, bigArray[i].length);
		return bigArray;
	}

	/**
	 * Ensures that a big array can contain the given number of entries.
	 *
	 * <p>
	 * If you cannot foresee whether this big array will need again to be enlarged, you should probably
	 * use {@code grow()} instead.
	 *
	 * <p>
	 * <strong>Warning:</strong> the returned array might use part of the segments of the original
	 * array, which must be considered read-only after calling this method.
	 *
	 * @param array a big array.
	 * @param length the new minimum length for this big array.
	 * @return {@code array}, if it contains {@code length} entries or more; otherwise, a big array with
	 *         {@code length} entries whose first {@code length(array)} entries are the same as those of
	 *         {@code array}.
	 */
	public static boolean[][] ensureCapacity(final boolean[][] array, final long length) {
		return ensureCapacity(array, length, length(array));
	}

	/**
	 * Forces a big array to contain the given number of entries, preserving just a part of the big
	 * array.
	 *
	 * <p>
	 * <strong>Warning:</strong> the returned array might use part of the segments of the original
	 * array, which must be considered read-only after calling this method.
	 *
	 * @param array a big array.
	 * @param length the new minimum length for this big array.
	 * @param preserve the number of elements of the big array that must be preserved in case a new
	 *            allocation is necessary.
	 * @return a big array with {@code length} entries whose first {@code preserve} entries are the same
	 *         as those of {@code array}.
	 */
	public static boolean[][] forceCapacity(final boolean[][] array, final long length, final long preserve) {
		ensureLength(length);
		final int valid = array.length - (array.length == 0 || array.length > 0 && array[array.length - 1].length == SEGMENT_SIZE ? 0 : 1);
		final int baseLength = (int)((length + SEGMENT_MASK) >>> SEGMENT_SHIFT);
		final boolean[][] base = java.util.Arrays.copyOf(array, baseLength);
		final int residual = (int)(length & SEGMENT_MASK);
		if (residual != 0) {
			for (int i = valid; i < baseLength - 1; i++) base[i] = new boolean[SEGMENT_SIZE];
			base[baseLength - 1] = new boolean[residual];
		} else for (int i = valid; i < baseLength; i++) base[i] = new boolean[SEGMENT_SIZE];
		if (preserve - (valid * (long)SEGMENT_SIZE) > 0) copy(array, valid * (long)SEGMENT_SIZE, base, valid * (long)SEGMENT_SIZE, preserve - (valid * (long)SEGMENT_SIZE));
		return base;
	}

	/**
	 * Ensures that a big array can contain the given number of entries, preserving just a part of the
	 * big array.
	 *
	 * <p>
	 * <strong>Warning:</strong> the returned array might use part of the segments of the original
	 * array, which must be considered read-only after calling this method.
	 *
	 * @param array a big array.
	 * @param length the new minimum length for this big array.
	 * @param preserve the number of elements of the big array that must be preserved in case a new
	 *            allocation is necessary.
	 * @return {@code array}, if it can contain {@code length} entries or more; otherwise, a big array
	 *         with {@code length} entries whose first {@code preserve} entries are the same as those of
	 *         {@code array}.
	 */
	public static boolean[][] ensureCapacity(final boolean[][] array, final long length, final long preserve) {
		return length > length(array) ? forceCapacity(array, length, preserve) : array;
	}

	/**
	 * Grows the given big array to the maximum between the given length and the current length
	 * increased by 50%, provided that the given length is larger than the current length.
	 *
	 * <p>
	 * If you want complete control on the big array growth, you should probably use
	 * {@code ensureCapacity()} instead.
	 *
	 * <p>
	 * <strong>Warning:</strong> the returned array might use part of the segments of the original
	 * array, which must be considered read-only after calling this method.
	 *
	 * @param array a big array.
	 * @param length the new minimum length for this big array.
	 * @return {@code array}, if it can contain {@code length} entries; otherwise, a big array with
	 *         max({@code length},{@code length(array)}/&phi;) entries whose first {@code length(array)}
	 *         entries are the same as those of {@code array}.
	 */
	public static boolean[][] grow(final boolean[][] array, final long length) {
		final long oldLength = length(array);
		return length > oldLength ? grow(array, length, oldLength) : array;
	}

	/**
	 * Grows the given big array to the maximum between the given length and the current length
	 * increased by 50%, provided that the given length is larger than the current length, preserving
	 * just a part of the big array.
	 *
	 * <p>
	 * If you want complete control on the big array growth, you should probably use
	 * {@code ensureCapacity()} instead.
	 *
	 * <p>
	 * <strong>Warning:</strong> the returned array might use part of the segments of the original
	 * array, which must be considered read-only after calling this method.
	 *
	 * @param array a big array.
	 * @param length the new minimum length for this big array.
	 * @param preserve the number of elements of the big array that must be preserved in case a new
	 *            allocation is necessary.
	 * @return {@code array}, if it can contain {@code length} entries; otherwise, a big array with
	 *         max({@code length},{@code length(array)}/&phi;) entries whose first {@code preserve}
	 *         entries are the same as those of {@code array}.
	 */
	public static boolean[][] grow(final boolean[][] array, final long length, final long preserve) {
		final long oldLength = length(array);
		return length > oldLength ? ensureCapacity(array, Math.max(oldLength + (oldLength >> 1), length), preserve) : array;
	}

	/**
	 * Trims the given big array to the given length.
	 *
	 * <p>
	 * <strong>Warning:</strong> the returned array might use part of the segments of the original
	 * array, which must be considered read-only after calling this method.
	 *
	 * @param array a big array.
	 * @param length the new maximum length for the big array.
	 * @return {@code array}, if it contains {@code length} entries or less; otherwise, a big array with
	 *         {@code length} entries whose entries are the same as the first {@code length} entries of
	 *         {@code array}.
	 *
	 */
	public static boolean[][] trim(final boolean[][] array, final long length) {
		ensureLength(length);
		final long oldLength = length(array);
		if (length >= oldLength) return array;
		final int baseLength = (int)((length + SEGMENT_MASK) >>> SEGMENT_SHIFT);
		final boolean[][] base = java.util.Arrays.copyOf(array, baseLength);
		final int residual = (int)(length & SEGMENT_MASK);
		if (residual != 0) base[baseLength - 1] = BooleanArrays.trim(base[baseLength - 1], residual);
		return base;
	}

	/**
	 * Sets the length of the given big array.
	 *
	 * <p>
	 * <strong>Warning:</strong> the returned array might use part of the segments of the original
	 * array, which must be considered read-only after calling this method.
	 *
	 * @param array a big array.
	 * @param length the new length for the big array.
	 * @return {@code array}, if it contains exactly {@code length} entries; otherwise, if it contains
	 *         <em>more</em> than {@code length} entries, a big array with {@code length} entries whose
	 *         entries are the same as the first {@code length} entries of {@code array}; otherwise, a
	 *         big array with {@code length} entries whose first {@code length(array)} entries are the
	 *         same as those of {@code array}.
	 *
	 */
	public static boolean[][] setLength(final boolean[][] array, final long length) {
		final long oldLength = length(array);
		if (length == oldLength) return array;
		if (length < oldLength) return trim(array, length);
		return ensureCapacity(array, length);
	}

	/**
	 * Returns a copy of a portion of a big array.
	 *
	 * @param array a big array.
	 * @param offset the first element to copy.
	 * @param length the number of elements to copy.
	 * @return a new big array containing {@code length} elements of {@code array} starting at
	 *         {@code offset}.
	 */
	public static boolean[][] copy(final boolean[][] array, final long offset, final long length) {
		ensureOffsetLength(array, offset, length);
		final boolean[][] a = BooleanBigArrays.newBigArray(length);
		copy(array, offset, a, 0, length);
		return a;
	}

	/**
	 * Returns a copy of a big array.
	 *
	 * @param array a big array.
	 * @return a copy of {@code array}.
	 */
	public static boolean[][] copy(final boolean[][] array) {
		final boolean[][] base = array.clone();
		for (int i = base.length; i-- != 0;) base[i] = array[i].clone();
		return base;
	}

	/**
	 * Fills the given big array with the given value.
	 *
	 * <p>
	 * This method uses a backward loop. It is significantly faster than the corresponding method in
	 * {@link java.util.Arrays}.
	 *
	 * @param array a big array.
	 * @param value the new value for all elements of the big array.
	 */
	public static void fill(final boolean[][] array, final boolean value) {
		for (int i = array.length; i-- != 0;) java.util.Arrays.fill(array[i], value);
	}

	/**
	 * Fills a portion of the given big array with the given value.
	 *
	 * <p>
	 * If possible (i.e., {@code from} is 0) this method uses a backward loop. In this case, it is
	 * significantly faster than the corresponding method in {@link java.util.Arrays}.
	 *
	 * @param array a big array.
	 * @param from the starting index of the portion to fill.
	 * @param to the end index of the portion to fill.
	 * @param value the new value for all elements of the specified portion of the big array.
	 */
	public static void fill(final boolean[][] array, final long from, long to, final boolean value) {
		final long length = length(array);
		BigArrays.ensureFromTo(length, from, to);
		if (length == 0) return; // To avoid addressing array[0]
		int fromSegment = segment(from);
		int toSegment = segment(to);
		int fromDispl = displacement(from);
		int toDispl = displacement(to);
		if (fromSegment == toSegment) {
			java.util.Arrays.fill(array[fromSegment], fromDispl, toDispl, value);
			return;
		}
		if (toDispl != 0) java.util.Arrays.fill(array[toSegment], 0, toDispl, value);
		while (--toSegment > fromSegment) java.util.Arrays.fill(array[toSegment], value);
		java.util.Arrays.fill(array[fromSegment], fromDispl, SEGMENT_SIZE, value);
	}

	/**
	 * Returns true if the two big arrays are elementwise equal.
	 *
	 * <p>
	 * This method uses a backward loop. It is significantly faster than the corresponding method in
	 * {@link java.util.Arrays}.
	 *
	 * @param a1 a big array.
	 * @param a2 another big array.
	 * @return true if the two big arrays are of the same length, and their elements are equal.
	 */
	public static boolean equals(final boolean[][] a1, final boolean a2[][]) {
		if (length(a1) != length(a2)) return false;
		int i = a1.length, j;
		boolean[] t, u;
		while (i-- != 0) {
			t = a1[i];
			u = a2[i];
			j = t.length;
			while (j-- != 0) if (!((t[j]) == (u[j]))) return false;
		}
		return true;
	}

	/* Returns a string representation of the contents of the specified big array.
	 *
	 * The string representation consists of a list of the big array's elements, enclosed in square brackets ("[]"). Adjacent elements are separated by the characters ", " (a comma followed by a space). Returns "null" if {@code a} is null.
	 * @param a the big array whose string representation to return.
	 * @return the string representation of {@code a}.
	 */
	public static String toString(final boolean[][] a) {
		if (a == null) return "null";
		final long last = length(a) - 1;
		if (last == -1) return "[]";
		final StringBuilder b = new StringBuilder();
		b.append('[');
		for (long i = 0;; i++) {
			b.append(String.valueOf(get(a, i)));
			if (i == last) return b.append(']').toString();
			b.append(", ");
		}
	}

	/**
	 * Ensures that a range given by its first (inclusive) and last (exclusive) elements fits a big
	 * array.
	 *
	 * <p>
	 * This method may be used whenever a big array range check is needed.
	 *
	 * @param a a big array.
	 * @param from a start index (inclusive).
	 * @param to an end index (inclusive).
	 * @throws IllegalArgumentException if {@code from} is greater than {@code to}.
	 * @throws ArrayIndexOutOfBoundsException if {@code from} or {@code to} are greater than the big
	 *             array length or negative.
	 */
	public static void ensureFromTo(final boolean[][] a, final long from, final long to) {
		BigArrays.ensureFromTo(length(a), from, to);
	}

	/**
	 * Ensures that a range given by an offset and a length fits a big array.
	 *
	 * <p>
	 * This method may be used whenever a big array range check is needed.
	 *
	 * @param a a big array.
	 * @param offset a start index.
	 * @param length a length (the number of elements in the range).
	 * @throws IllegalArgumentException if {@code length} is negative.
	 * @throws ArrayIndexOutOfBoundsException if {@code offset} is negative or
	 *             {@code offset}+{@code length} is greater than the big array length.
	 */
	public static void ensureOffsetLength(final boolean[][] a, final long offset, final long length) {
		BigArrays.ensureOffsetLength(length(a), offset, length);
	}

	/**
	 * Ensures that two big arrays are of the same length.
	 *
	 * @param a a big array.
	 * @param b another big array.
	 * @throws IllegalArgumentException if the two argument arrays are not of the same length.
	 */
	public static void ensureSameLength(final boolean[][] a, final boolean[][] b) {
		if (length(a) != length(b)) throw new IllegalArgumentException("Array size mismatch: " + length(a) + " != " + length(b));
	}

	/**
	 * Shuffles the specified big array fragment using the specified pseudorandom number generator.
	 *
	 * @param a the big array to be shuffled.
	 * @param from the index of the first element (inclusive) to be shuffled.
	 * @param to the index of the last element (exclusive) to be shuffled.
	 * @param random a pseudorandom number generator.
	 * @return {@code a}.
	 */
	public static boolean[][] shuffle(final boolean[][] a, final long from, final long to, final Random random) {
		for (long i = to - from; i-- != 0;) {
			final long p = (random.nextLong() & 0x7FFFFFFFFFFFFFFFL) % (i + 1);
			final boolean t = BigArrays.get(a, from + i);
			BigArrays.set(a, from + i, BigArrays.get(a, from + p));
			BigArrays.set(a, from + p, t);
		}
		return a;
	}

	/**
	 * Shuffles the specified big array using the specified pseudorandom number generator.
	 *
	 * @param a the big array to be shuffled.
	 * @param random a pseudorandom number generator.
	 * @return {@code a}.
	 */
	public static boolean[][] shuffle(final boolean[][] a, final Random random) {
		for (long i = length(a); i-- != 0;) {
			final long p = (random.nextLong() & 0x7FFFFFFFFFFFFFFFL) % (i + 1);
			final boolean t = BigArrays.get(a, i);
			BigArrays.set(a, i, BigArrays.get(a, p));
			BigArrays.set(a, p, t);
		}
		return a;
	}

	/* Generic definitions */
	/* Assertions (useful to generate conditional code) */
	/* Narrowing and widening */
	/* Current type and class (and size, if applicable) */
	/* Value methods */
	/* Interfaces (keys) */
	/* Interfaces (values) */
	/* Types and methods related to primitive-type support in the JDK */
	/* Abstract implementations (keys) */
	/* Abstract implementations (values) */
	/* Static containers (keys) */
	/* Static containers (values) */
	/* Implementations */
	/* Synchronized wrappers */
	/* Unmodifiable wrappers */
	/* Other wrappers */
	/* Methods (keys) */
	/* Methods (values) */
	/* Methods (keys/values) */
	/* Methods that have special names depending on keys (but the special names depend on values) */
	/* Equality */
	/* Object/Reference-only definitions (keys) */
	/* Primitive-type-only definitions (keys) */
	/* Object/Reference-only definitions (values) */
	/* START_OF_JAVA_SOURCE */
	/*
		* Copyright (C) 2004-2024 Sebastiano Vigna
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
	/**
	 * Returns the element of the given big array of specified index.
	 *
	 * @param array a big array.
	 * @param index a position in the big array.
	 * @return the element of the big array at the specified position.
	 */
	public static short get(final short[][] array, final long index) {
		return array[segment(index)][displacement(index)];
	}

	/**
	 * Sets the element of the given big array of specified index.
	 *
	 * @param array a big array.
	 * @param index a position in the big array.
	 * @param value the new value for the array element at the specified position.
	 */
	public static void set(final short[][] array, final long index, short value) {
		array[segment(index)][displacement(index)] = value;
	}

	/**
	 * Swaps the element of the given big array of specified indices.
	 *
	 * @param array a big array.
	 * @param first a position in the big array.
	 * @param second a position in the big array.
	 */
	public static void swap(final short[][] array, final long first, final long second) {
		final short t = array[segment(first)][displacement(first)];
		array[segment(first)][displacement(first)] = array[segment(second)][displacement(second)];
		array[segment(second)][displacement(second)] = t;
	}

	/**
	 * Reverses the order of the elements in the specified big array.
	 *
	 * @param a the big array to be reversed.
	 * @return {@code a}.
	 */
	public static short[][] reverse(final short[][] a) {
		final long length = length(a);
		for (long i = length / 2; i-- != 0;) swap(a, i, length - i - 1);
		return a;
	}

	/**
	 * Adds the specified increment the element of the given big array of specified index.
	 *
	 * @param array a big array.
	 * @param index a position in the big array.
	 * @param incr the increment
	 */
	public static void add(final short[][] array, final long index, short incr) {
		array[segment(index)][displacement(index)] += incr;
	}

	/**
	 * Multiplies by the specified factor the element of the given big array of specified index.
	 *
	 * @param array a big array.
	 * @param index a position in the big array.
	 * @param factor the factor
	 */
	public static void mul(final short[][] array, final long index, short factor) {
		array[segment(index)][displacement(index)] *= factor;
	}

	/**
	 * Increments the element of the given big array of specified index.
	 *
	 * @param array a big array.
	 * @param index a position in the big array.
	 */
	public static void incr(final short[][] array, final long index) {
		array[segment(index)][displacement(index)]++;
	}

	/**
	 * Decrements the element of the given big array of specified index.
	 *
	 * @param array a big array.
	 * @param index a position in the big array.
	 */
	public static void decr(final short[][] array, final long index) {
		array[segment(index)][displacement(index)]--;
	}

	/**
	 * Asserts that the provided big array is correct.
	 *
	 * @param array a big array.
	 * @throws IllegalStateException if the last segment is empty or longer than
	 *             {@link BigArrays#SEGMENT_SIZE}, or any other segment has length different from
	 *             {@link BigArrays#SEGMENT_SIZE}.
	 */
	public static void assertBigArray(final short[][] array) {
		final int l = array.length;
		if (l == 0) return;
		for (int i = 0; i < l - 1; i++) if (array[i].length != SEGMENT_SIZE) throw new IllegalStateException("All segments except for the last one must be of length 2^" + Integer.toString(SEGMENT_SHIFT));
		if (array[l - 1].length > SEGMENT_SIZE) throw new IllegalStateException("The last segment must be of length at most 2^" + Integer.toString(SEGMENT_SHIFT));
		if (array[l - 1].length == 0 && l == 1) throw new IllegalStateException("The last segment must be of nonzero length");
	}

	/**
	 * Returns the length of the given big array.
	 *
	 * @param array a big array.
	 * @return the length of the given big array.
	 */
	public static long length(final short[][] array) {
		final int length = array.length;
		return length == 0 ? 0 : start(length - 1) + array[length - 1].length;
	}

	/**
	 * Copies a big array from the specified source big array, beginning at the specified position, to
	 * the specified position of the destination big array. Handles correctly overlapping regions of the
	 * same big array.
	 *
	 * @param srcArray the source big array.
	 * @param srcPos the starting position in the source big array.
	 * @param destArray the destination big array.
	 * @param destPos the starting position in the destination data.
	 * @param length the number of elements to be copied.
	 */
	public static void copy(final short[][] srcArray, final long srcPos, final short[][] destArray, final long destPos, long length) {
		if (destPos <= srcPos) {
			int srcSegment = segment(srcPos);
			int destSegment = segment(destPos);
			int srcDispl = displacement(srcPos);
			int destDispl = displacement(destPos);
			while (length > 0) {
				final int l = (int)Math.min(length, Math.min(srcArray[srcSegment].length - srcDispl, destArray[destSegment].length - destDispl));
				if (l == 0) throw new ArrayIndexOutOfBoundsException();
				System.arraycopy(srcArray[srcSegment], srcDispl, destArray[destSegment], destDispl, l);
				if ((srcDispl += l) == SEGMENT_SIZE) {
					srcDispl = 0;
					srcSegment++;
				}
				if ((destDispl += l) == SEGMENT_SIZE) {
					destDispl = 0;
					destSegment++;
				}
				length -= l;
			}
		} else {
			int srcSegment = segment(srcPos + length);
			int destSegment = segment(destPos + length);
			int srcDispl = displacement(srcPos + length);
			int destDispl = displacement(destPos + length);
			while (length > 0) {
				if (srcDispl == 0) {
					srcDispl = SEGMENT_SIZE;
					srcSegment--;
				}
				if (destDispl == 0) {
					destDispl = SEGMENT_SIZE;
					destSegment--;
				}
				final int l = (int)Math.min(length, Math.min(srcDispl, destDispl));
				if (l == 0) throw new ArrayIndexOutOfBoundsException();
				System.arraycopy(srcArray[srcSegment], srcDispl - l, destArray[destSegment], destDispl - l, l);
				srcDispl -= l;
				destDispl -= l;
				length -= l;
			}
		}
	}

	/**
	 * Copies a big array from the specified source big array, beginning at the specified position, to
	 * the specified position of the destination array.
	 *
	 * @param srcArray the source big array.
	 * @param srcPos the starting position in the source big array.
	 * @param destArray the destination array.
	 * @param destPos the starting position in the destination data.
	 * @param length the number of elements to be copied.
	 */
	public static void copyFromBig(final short[][] srcArray, final long srcPos, final short[] destArray, int destPos, int length) {
		int srcSegment = segment(srcPos);
		int srcDispl = displacement(srcPos);
		while (length > 0) {
			final int l = Math.min(srcArray[srcSegment].length - srcDispl, length);
			if (l == 0) throw new ArrayIndexOutOfBoundsException();
			System.arraycopy(srcArray[srcSegment], srcDispl, destArray, destPos, l);
			if ((srcDispl += l) == SEGMENT_SIZE) {
				srcDispl = 0;
				srcSegment++;
			}
			destPos += l;
			length -= l;
		}
	}

	/**
	 * Copies an array from the specified source array, beginning at the specified position, to the
	 * specified position of the destination big array.
	 *
	 * @param srcArray the source array.
	 * @param srcPos the starting position in the source array.
	 * @param destArray the destination big array.
	 * @param destPos the starting position in the destination data.
	 * @param length the number of elements to be copied.
	 */
	public static void copyToBig(final short[] srcArray, int srcPos, final short[][] destArray, final long destPos, long length) {
		int destSegment = segment(destPos);
		int destDispl = displacement(destPos);
		while (length > 0) {
			final int l = (int)Math.min(destArray[destSegment].length - destDispl, length);
			if (l == 0) throw new ArrayIndexOutOfBoundsException();
			System.arraycopy(srcArray, srcPos, destArray[destSegment], destDispl, l);
			if ((destDispl += l) == SEGMENT_SIZE) {
				destDispl = 0;
				destSegment++;
			}
			srcPos += l;
			length -= l;
		}
	}

	/**
	 * Turns a standard array into a big array.
	 *
	 * <p>
	 * Note that the returned big array might contain as a segment the original array.
	 *
	 * @param array an array.
	 * @return a new big array with the same length and content of {@code array}.
	 */
	public static short[][] wrap(final short[] array) {
		if (array.length == 0) return ShortBigArrays.EMPTY_BIG_ARRAY;
		if (array.length <= SEGMENT_SIZE) return new short[][] { array };
		final short[][] bigArray = ShortBigArrays.newBigArray(array.length);
		for (int i = 0; i < bigArray.length; i++) System.arraycopy(array, (int)start(i), bigArray[i], 0, bigArray[i].length);
		return bigArray;
	}

	/**
	 * Ensures that a big array can contain the given number of entries.
	 *
	 * <p>
	 * If you cannot foresee whether this big array will need again to be enlarged, you should probably
	 * use {@code grow()} instead.
	 *
	 * <p>
	 * <strong>Warning:</strong> the returned array might use part of the segments of the original
	 * array, which must be considered read-only after calling this method.
	 *
	 * @param array a big array.
	 * @param length the new minimum length for this big array.
	 * @return {@code array}, if it contains {@code length} entries or more; otherwise, a big array with
	 *         {@code length} entries whose first {@code length(array)} entries are the same as those of
	 *         {@code array}.
	 */
	public static short[][] ensureCapacity(final short[][] array, final long length) {
		return ensureCapacity(array, length, length(array));
	}

	/**
	 * Forces a big array to contain the given number of entries, preserving just a part of the big
	 * array.
	 *
	 * <p>
	 * <strong>Warning:</strong> the returned array might use part of the segments of the original
	 * array, which must be considered read-only after calling this method.
	 *
	 * @param array a big array.
	 * @param length the new minimum length for this big array.
	 * @param preserve the number of elements of the big array that must be preserved in case a new
	 *            allocation is necessary.
	 * @return a big array with {@code length} entries whose first {@code preserve} entries are the same
	 *         as those of {@code array}.
	 */
	public static short[][] forceCapacity(final short[][] array, final long length, final long preserve) {
		ensureLength(length);
		final int valid = array.length - (array.length == 0 || array.length > 0 && array[array.length - 1].length == SEGMENT_SIZE ? 0 : 1);
		final int baseLength = (int)((length + SEGMENT_MASK) >>> SEGMENT_SHIFT);
		final short[][] base = java.util.Arrays.copyOf(array, baseLength);
		final int residual = (int)(length & SEGMENT_MASK);
		if (residual != 0) {
			for (int i = valid; i < baseLength - 1; i++) base[i] = new short[SEGMENT_SIZE];
			base[baseLength - 1] = new short[residual];
		} else for (int i = valid; i < baseLength; i++) base[i] = new short[SEGMENT_SIZE];
		if (preserve - (valid * (long)SEGMENT_SIZE) > 0) copy(array, valid * (long)SEGMENT_SIZE, base, valid * (long)SEGMENT_SIZE, preserve - (valid * (long)SEGMENT_SIZE));
		return base;
	}

	/**
	 * Ensures that a big array can contain the given number of entries, preserving just a part of the
	 * big array.
	 *
	 * <p>
	 * <strong>Warning:</strong> the returned array might use part of the segments of the original
	 * array, which must be considered read-only after calling this method.
	 *
	 * @param array a big array.
	 * @param length the new minimum length for this big array.
	 * @param preserve the number of elements of the big array that must be preserved in case a new
	 *            allocation is necessary.
	 * @return {@code array}, if it can contain {@code length} entries or more; otherwise, a big array
	 *         with {@code length} entries whose first {@code preserve} entries are the same as those of
	 *         {@code array}.
	 */
	public static short[][] ensureCapacity(final short[][] array, final long length, final long preserve) {
		return length > length(array) ? forceCapacity(array, length, preserve) : array;
	}

	/**
	 * Grows the given big array to the maximum between the given length and the current length
	 * increased by 50%, provided that the given length is larger than the current length.
	 *
	 * <p>
	 * If you want complete control on the big array growth, you should probably use
	 * {@code ensureCapacity()} instead.
	 *
	 * <p>
	 * <strong>Warning:</strong> the returned array might use part of the segments of the original
	 * array, which must be considered read-only after calling this method.
	 *
	 * @param array a big array.
	 * @param length the new minimum length for this big array.
	 * @return {@code array}, if it can contain {@code length} entries; otherwise, a big array with
	 *         max({@code length},{@code length(array)}/&phi;) entries whose first {@code length(array)}
	 *         entries are the same as those of {@code array}.
	 */
	public static short[][] grow(final short[][] array, final long length) {
		final long oldLength = length(array);
		return length > oldLength ? grow(array, length, oldLength) : array;
	}

	/**
	 * Grows the given big array to the maximum between the given length and the current length
	 * increased by 50%, provided that the given length is larger than the current length, preserving
	 * just a part of the big array.
	 *
	 * <p>
	 * If you want complete control on the big array growth, you should probably use
	 * {@code ensureCapacity()} instead.
	 *
	 * <p>
	 * <strong>Warning:</strong> the returned array might use part of the segments of the original
	 * array, which must be considered read-only after calling this method.
	 *
	 * @param array a big array.
	 * @param length the new minimum length for this big array.
	 * @param preserve the number of elements of the big array that must be preserved in case a new
	 *            allocation is necessary.
	 * @return {@code array}, if it can contain {@code length} entries; otherwise, a big array with
	 *         max({@code length},{@code length(array)}/&phi;) entries whose first {@code preserve}
	 *         entries are the same as those of {@code array}.
	 */
	public static short[][] grow(final short[][] array, final long length, final long preserve) {
		final long oldLength = length(array);
		return length > oldLength ? ensureCapacity(array, Math.max(oldLength + (oldLength >> 1), length), preserve) : array;
	}

	/**
	 * Trims the given big array to the given length.
	 *
	 * <p>
	 * <strong>Warning:</strong> the returned array might use part of the segments of the original
	 * array, which must be considered read-only after calling this method.
	 *
	 * @param array a big array.
	 * @param length the new maximum length for the big array.
	 * @return {@code array}, if it contains {@code length} entries or less; otherwise, a big array with
	 *         {@code length} entries whose entries are the same as the first {@code length} entries of
	 *         {@code array}.
	 *
	 */
	public static short[][] trim(final short[][] array, final long length) {
		ensureLength(length);
		final long oldLength = length(array);
		if (length >= oldLength) return array;
		final int baseLength = (int)((length + SEGMENT_MASK) >>> SEGMENT_SHIFT);
		final short[][] base = java.util.Arrays.copyOf(array, baseLength);
		final int residual = (int)(length & SEGMENT_MASK);
		if (residual != 0) base[baseLength - 1] = ShortArrays.trim(base[baseLength - 1], residual);
		return base;
	}

	/**
	 * Sets the length of the given big array.
	 *
	 * <p>
	 * <strong>Warning:</strong> the returned array might use part of the segments of the original
	 * array, which must be considered read-only after calling this method.
	 *
	 * @param array a big array.
	 * @param length the new length for the big array.
	 * @return {@code array}, if it contains exactly {@code length} entries; otherwise, if it contains
	 *         <em>more</em> than {@code length} entries, a big array with {@code length} entries whose
	 *         entries are the same as the first {@code length} entries of {@code array}; otherwise, a
	 *         big array with {@code length} entries whose first {@code length(array)} entries are the
	 *         same as those of {@code array}.
	 *
	 */
	public static short[][] setLength(final short[][] array, final long length) {
		final long oldLength = length(array);
		if (length == oldLength) return array;
		if (length < oldLength) return trim(array, length);
		return ensureCapacity(array, length);
	}

	/**
	 * Returns a copy of a portion of a big array.
	 *
	 * @param array a big array.
	 * @param offset the first element to copy.
	 * @param length the number of elements to copy.
	 * @return a new big array containing {@code length} elements of {@code array} starting at
	 *         {@code offset}.
	 */
	public static short[][] copy(final short[][] array, final long offset, final long length) {
		ensureOffsetLength(array, offset, length);
		final short[][] a = ShortBigArrays.newBigArray(length);
		copy(array, offset, a, 0, length);
		return a;
	}

	/**
	 * Returns a copy of a big array.
	 *
	 * @param array a big array.
	 * @return a copy of {@code array}.
	 */
	public static short[][] copy(final short[][] array) {
		final short[][] base = array.clone();
		for (int i = base.length; i-- != 0;) base[i] = array[i].clone();
		return base;
	}

	/**
	 * Fills the given big array with the given value.
	 *
	 * <p>
	 * This method uses a backward loop. It is significantly faster than the corresponding method in
	 * {@link java.util.Arrays}.
	 *
	 * @param array a big array.
	 * @param value the new value for all elements of the big array.
	 */
	public static void fill(final short[][] array, final short value) {
		for (int i = array.length; i-- != 0;) java.util.Arrays.fill(array[i], value);
	}

	/**
	 * Fills a portion of the given big array with the given value.
	 *
	 * <p>
	 * If possible (i.e., {@code from} is 0) this method uses a backward loop. In this case, it is
	 * significantly faster than the corresponding method in {@link java.util.Arrays}.
	 *
	 * @param array a big array.
	 * @param from the starting index of the portion to fill.
	 * @param to the end index of the portion to fill.
	 * @param value the new value for all elements of the specified portion of the big array.
	 */
	public static void fill(final short[][] array, final long from, long to, final short value) {
		final long length = length(array);
		BigArrays.ensureFromTo(length, from, to);
		if (length == 0) return; // To avoid addressing array[0]
		int fromSegment = segment(from);
		int toSegment = segment(to);
		int fromDispl = displacement(from);
		int toDispl = displacement(to);
		if (fromSegment == toSegment) {
			java.util.Arrays.fill(array[fromSegment], fromDispl, toDispl, value);
			return;
		}
		if (toDispl != 0) java.util.Arrays.fill(array[toSegment], 0, toDispl, value);
		while (--toSegment > fromSegment) java.util.Arrays.fill(array[toSegment], value);
		java.util.Arrays.fill(array[fromSegment], fromDispl, SEGMENT_SIZE, value);
	}

	/**
	 * Returns true if the two big arrays are elementwise equal.
	 *
	 * <p>
	 * This method uses a backward loop. It is significantly faster than the corresponding method in
	 * {@link java.util.Arrays}.
	 *
	 * @param a1 a big array.
	 * @param a2 another big array.
	 * @return true if the two big arrays are of the same length, and their elements are equal.
	 */
	public static boolean equals(final short[][] a1, final short a2[][]) {
		if (length(a1) != length(a2)) return false;
		int i = a1.length, j;
		short[] t, u;
		while (i-- != 0) {
			t = a1[i];
			u = a2[i];
			j = t.length;
			while (j-- != 0) if (!((t[j]) == (u[j]))) return false;
		}
		return true;
	}

	/* Returns a string representation of the contents of the specified big array.
	 *
	 * The string representation consists of a list of the big array's elements, enclosed in square brackets ("[]"). Adjacent elements are separated by the characters ", " (a comma followed by a space). Returns "null" if {@code a} is null.
	 * @param a the big array whose string representation to return.
	 * @return the string representation of {@code a}.
	 */
	public static String toString(final short[][] a) {
		if (a == null) return "null";
		final long last = length(a) - 1;
		if (last == -1) return "[]";
		final StringBuilder b = new StringBuilder();
		b.append('[');
		for (long i = 0;; i++) {
			b.append(String.valueOf(get(a, i)));
			if (i == last) return b.append(']').toString();
			b.append(", ");
		}
	}

	/**
	 * Ensures that a range given by its first (inclusive) and last (exclusive) elements fits a big
	 * array.
	 *
	 * <p>
	 * This method may be used whenever a big array range check is needed.
	 *
	 * @param a a big array.
	 * @param from a start index (inclusive).
	 * @param to an end index (inclusive).
	 * @throws IllegalArgumentException if {@code from} is greater than {@code to}.
	 * @throws ArrayIndexOutOfBoundsException if {@code from} or {@code to} are greater than the big
	 *             array length or negative.
	 */
	public static void ensureFromTo(final short[][] a, final long from, final long to) {
		BigArrays.ensureFromTo(length(a), from, to);
	}

	/**
	 * Ensures that a range given by an offset and a length fits a big array.
	 *
	 * <p>
	 * This method may be used whenever a big array range check is needed.
	 *
	 * @param a a big array.
	 * @param offset a start index.
	 * @param length a length (the number of elements in the range).
	 * @throws IllegalArgumentException if {@code length} is negative.
	 * @throws ArrayIndexOutOfBoundsException if {@code offset} is negative or
	 *             {@code offset}+{@code length} is greater than the big array length.
	 */
	public static void ensureOffsetLength(final short[][] a, final long offset, final long length) {
		BigArrays.ensureOffsetLength(length(a), offset, length);
	}

	/**
	 * Ensures that two big arrays are of the same length.
	 *
	 * @param a a big array.
	 * @param b another big array.
	 * @throws IllegalArgumentException if the two argument arrays are not of the same length.
	 */
	public static void ensureSameLength(final short[][] a, final short[][] b) {
		if (length(a) != length(b)) throw new IllegalArgumentException("Array size mismatch: " + length(a) + " != " + length(b));
	}

	/**
	 * Shuffles the specified big array fragment using the specified pseudorandom number generator.
	 *
	 * @param a the big array to be shuffled.
	 * @param from the index of the first element (inclusive) to be shuffled.
	 * @param to the index of the last element (exclusive) to be shuffled.
	 * @param random a pseudorandom number generator.
	 * @return {@code a}.
	 */
	public static short[][] shuffle(final short[][] a, final long from, final long to, final Random random) {
		for (long i = to - from; i-- != 0;) {
			final long p = (random.nextLong() & 0x7FFFFFFFFFFFFFFFL) % (i + 1);
			final short t = BigArrays.get(a, from + i);
			BigArrays.set(a, from + i, BigArrays.get(a, from + p));
			BigArrays.set(a, from + p, t);
		}
		return a;
	}

	/**
	 * Shuffles the specified big array using the specified pseudorandom number generator.
	 *
	 * @param a the big array to be shuffled.
	 * @param random a pseudorandom number generator.
	 * @return {@code a}.
	 */
	public static short[][] shuffle(final short[][] a, final Random random) {
		for (long i = length(a); i-- != 0;) {
			final long p = (random.nextLong() & 0x7FFFFFFFFFFFFFFFL) % (i + 1);
			final short t = BigArrays.get(a, i);
			BigArrays.set(a, i, BigArrays.get(a, p));
			BigArrays.set(a, p, t);
		}
		return a;
	}

	/* Generic definitions */
	/* Assertions (useful to generate conditional code) */
	/* Narrowing and widening */
	/* Current type and class (and size, if applicable) */
	/* Value methods */
	/* Interfaces (keys) */
	/* Interfaces (values) */
	/* Types and methods related to primitive-type support in the JDK */
	/* Abstract implementations (keys) */
	/* Abstract implementations (values) */
	/* Static containers (keys) */
	/* Static containers (values) */
	/* Implementations */
	/* Synchronized wrappers */
	/* Unmodifiable wrappers */
	/* Other wrappers */
	/* Methods (keys) */
	/* Methods (values) */
	/* Methods (keys/values) */
	/* Methods that have special names depending on keys (but the special names depend on values) */
	/* Equality */
	/* Object/Reference-only definitions (keys) */
	/* Primitive-type-only definitions (keys) */
	/* Object/Reference-only definitions (values) */
	/* START_OF_JAVA_SOURCE */
	/*
		* Copyright (C) 2004-2024 Sebastiano Vigna
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
	/**
	 * Returns the element of the given big array of specified index.
	 *
	 * @param array a big array.
	 * @param index a position in the big array.
	 * @return the element of the big array at the specified position.
	 */
	public static char get(final char[][] array, final long index) {
		return array[segment(index)][displacement(index)];
	}

	/**
	 * Sets the element of the given big array of specified index.
	 *
	 * @param array a big array.
	 * @param index a position in the big array.
	 * @param value the new value for the array element at the specified position.
	 */
	public static void set(final char[][] array, final long index, char value) {
		array[segment(index)][displacement(index)] = value;
	}

	/**
	 * Swaps the element of the given big array of specified indices.
	 *
	 * @param array a big array.
	 * @param first a position in the big array.
	 * @param second a position in the big array.
	 */
	public static void swap(final char[][] array, final long first, final long second) {
		final char t = array[segment(first)][displacement(first)];
		array[segment(first)][displacement(first)] = array[segment(second)][displacement(second)];
		array[segment(second)][displacement(second)] = t;
	}

	/**
	 * Reverses the order of the elements in the specified big array.
	 *
	 * @param a the big array to be reversed.
	 * @return {@code a}.
	 */
	public static char[][] reverse(final char[][] a) {
		final long length = length(a);
		for (long i = length / 2; i-- != 0;) swap(a, i, length - i - 1);
		return a;
	}

	/**
	 * Adds the specified increment the element of the given big array of specified index.
	 *
	 * @param array a big array.
	 * @param index a position in the big array.
	 * @param incr the increment
	 */
	public static void add(final char[][] array, final long index, char incr) {
		array[segment(index)][displacement(index)] += incr;
	}

	/**
	 * Multiplies by the specified factor the element of the given big array of specified index.
	 *
	 * @param array a big array.
	 * @param index a position in the big array.
	 * @param factor the factor
	 */
	public static void mul(final char[][] array, final long index, char factor) {
		array[segment(index)][displacement(index)] *= factor;
	}

	/**
	 * Increments the element of the given big array of specified index.
	 *
	 * @param array a big array.
	 * @param index a position in the big array.
	 */
	public static void incr(final char[][] array, final long index) {
		array[segment(index)][displacement(index)]++;
	}

	/**
	 * Decrements the element of the given big array of specified index.
	 *
	 * @param array a big array.
	 * @param index a position in the big array.
	 */
	public static void decr(final char[][] array, final long index) {
		array[segment(index)][displacement(index)]--;
	}

	/**
	 * Asserts that the provided big array is correct.
	 *
	 * @param array a big array.
	 * @throws IllegalStateException if the last segment is empty or longer than
	 *             {@link BigArrays#SEGMENT_SIZE}, or any other segment has length different from
	 *             {@link BigArrays#SEGMENT_SIZE}.
	 */
	public static void assertBigArray(final char[][] array) {
		final int l = array.length;
		if (l == 0) return;
		for (int i = 0; i < l - 1; i++) if (array[i].length != SEGMENT_SIZE) throw new IllegalStateException("All segments except for the last one must be of length 2^" + Integer.toString(SEGMENT_SHIFT));
		if (array[l - 1].length > SEGMENT_SIZE) throw new IllegalStateException("The last segment must be of length at most 2^" + Integer.toString(SEGMENT_SHIFT));
		if (array[l - 1].length == 0 && l == 1) throw new IllegalStateException("The last segment must be of nonzero length");
	}

	/**
	 * Returns the length of the given big array.
	 *
	 * @param array a big array.
	 * @return the length of the given big array.
	 */
	public static long length(final char[][] array) {
		final int length = array.length;
		return length == 0 ? 0 : start(length - 1) + array[length - 1].length;
	}

	/**
	 * Copies a big array from the specified source big array, beginning at the specified position, to
	 * the specified position of the destination big array. Handles correctly overlapping regions of the
	 * same big array.
	 *
	 * @param srcArray the source big array.
	 * @param srcPos the starting position in the source big array.
	 * @param destArray the destination big array.
	 * @param destPos the starting position in the destination data.
	 * @param length the number of elements to be copied.
	 */
	public static void copy(final char[][] srcArray, final long srcPos, final char[][] destArray, final long destPos, long length) {
		if (destPos <= srcPos) {
			int srcSegment = segment(srcPos);
			int destSegment = segment(destPos);
			int srcDispl = displacement(srcPos);
			int destDispl = displacement(destPos);
			while (length > 0) {
				final int l = (int)Math.min(length, Math.min(srcArray[srcSegment].length - srcDispl, destArray[destSegment].length - destDispl));
				if (l == 0) throw new ArrayIndexOutOfBoundsException();
				System.arraycopy(srcArray[srcSegment], srcDispl, destArray[destSegment], destDispl, l);
				if ((srcDispl += l) == SEGMENT_SIZE) {
					srcDispl = 0;
					srcSegment++;
				}
				if ((destDispl += l) == SEGMENT_SIZE) {
					destDispl = 0;
					destSegment++;
				}
				length -= l;
			}
		} else {
			int srcSegment = segment(srcPos + length);
			int destSegment = segment(destPos + length);
			int srcDispl = displacement(srcPos + length);
			int destDispl = displacement(destPos + length);
			while (length > 0) {
				if (srcDispl == 0) {
					srcDispl = SEGMENT_SIZE;
					srcSegment--;
				}
				if (destDispl == 0) {
					destDispl = SEGMENT_SIZE;
					destSegment--;
				}
				final int l = (int)Math.min(length, Math.min(srcDispl, destDispl));
				if (l == 0) throw new ArrayIndexOutOfBoundsException();
				System.arraycopy(srcArray[srcSegment], srcDispl - l, destArray[destSegment], destDispl - l, l);
				srcDispl -= l;
				destDispl -= l;
				length -= l;
			}
		}
	}

	/**
	 * Copies a big array from the specified source big array, beginning at the specified position, to
	 * the specified position of the destination array.
	 *
	 * @param srcArray the source big array.
	 * @param srcPos the starting position in the source big array.
	 * @param destArray the destination array.
	 * @param destPos the starting position in the destination data.
	 * @param length the number of elements to be copied.
	 */
	public static void copyFromBig(final char[][] srcArray, final long srcPos, final char[] destArray, int destPos, int length) {
		int srcSegment = segment(srcPos);
		int srcDispl = displacement(srcPos);
		while (length > 0) {
			final int l = Math.min(srcArray[srcSegment].length - srcDispl, length);
			if (l == 0) throw new ArrayIndexOutOfBoundsException();
			System.arraycopy(srcArray[srcSegment], srcDispl, destArray, destPos, l);
			if ((srcDispl += l) == SEGMENT_SIZE) {
				srcDispl = 0;
				srcSegment++;
			}
			destPos += l;
			length -= l;
		}
	}

	/**
	 * Copies an array from the specified source array, beginning at the specified position, to the
	 * specified position of the destination big array.
	 *
	 * @param srcArray the source array.
	 * @param srcPos the starting position in the source array.
	 * @param destArray the destination big array.
	 * @param destPos the starting position in the destination data.
	 * @param length the number of elements to be copied.
	 */
	public static void copyToBig(final char[] srcArray, int srcPos, final char[][] destArray, final long destPos, long length) {
		int destSegment = segment(destPos);
		int destDispl = displacement(destPos);
		while (length > 0) {
			final int l = (int)Math.min(destArray[destSegment].length - destDispl, length);
			if (l == 0) throw new ArrayIndexOutOfBoundsException();
			System.arraycopy(srcArray, srcPos, destArray[destSegment], destDispl, l);
			if ((destDispl += l) == SEGMENT_SIZE) {
				destDispl = 0;
				destSegment++;
			}
			srcPos += l;
			length -= l;
		}
	}

	/**
	 * Turns a standard array into a big array.
	 *
	 * <p>
	 * Note that the returned big array might contain as a segment the original array.
	 *
	 * @param array an array.
	 * @return a new big array with the same length and content of {@code array}.
	 */
	public static char[][] wrap(final char[] array) {
		if (array.length == 0) return CharBigArrays.EMPTY_BIG_ARRAY;
		if (array.length <= SEGMENT_SIZE) return new char[][] { array };
		final char[][] bigArray = CharBigArrays.newBigArray(array.length);
		for (int i = 0; i < bigArray.length; i++) System.arraycopy(array, (int)start(i), bigArray[i], 0, bigArray[i].length);
		return bigArray;
	}

	/**
	 * Ensures that a big array can contain the given number of entries.
	 *
	 * <p>
	 * If you cannot foresee whether this big array will need again to be enlarged, you should probably
	 * use {@code grow()} instead.
	 *
	 * <p>
	 * <strong>Warning:</strong> the returned array might use part of the segments of the original
	 * array, which must be considered read-only after calling this method.
	 *
	 * @param array a big array.
	 * @param length the new minimum length for this big array.
	 * @return {@code array}, if it contains {@code length} entries or more; otherwise, a big array with
	 *         {@code length} entries whose first {@code length(array)} entries are the same as those of
	 *         {@code array}.
	 */
	public static char[][] ensureCapacity(final char[][] array, final long length) {
		return ensureCapacity(array, length, length(array));
	}

	/**
	 * Forces a big array to contain the given number of entries, preserving just a part of the big
	 * array.
	 *
	 * <p>
	 * <strong>Warning:</strong> the returned array might use part of the segments of the original
	 * array, which must be considered read-only after calling this method.
	 *
	 * @param array a big array.
	 * @param length the new minimum length for this big array.
	 * @param preserve the number of elements of the big array that must be preserved in case a new
	 *            allocation is necessary.
	 * @return a big array with {@code length} entries whose first {@code preserve} entries are the same
	 *         as those of {@code array}.
	 */
	public static char[][] forceCapacity(final char[][] array, final long length, final long preserve) {
		ensureLength(length);
		final int valid = array.length - (array.length == 0 || array.length > 0 && array[array.length - 1].length == SEGMENT_SIZE ? 0 : 1);
		final int baseLength = (int)((length + SEGMENT_MASK) >>> SEGMENT_SHIFT);
		final char[][] base = java.util.Arrays.copyOf(array, baseLength);
		final int residual = (int)(length & SEGMENT_MASK);
		if (residual != 0) {
			for (int i = valid; i < baseLength - 1; i++) base[i] = new char[SEGMENT_SIZE];
			base[baseLength - 1] = new char[residual];
		} else for (int i = valid; i < baseLength; i++) base[i] = new char[SEGMENT_SIZE];
		if (preserve - (valid * (long)SEGMENT_SIZE) > 0) copy(array, valid * (long)SEGMENT_SIZE, base, valid * (long)SEGMENT_SIZE, preserve - (valid * (long)SEGMENT_SIZE));
		return base;
	}

	/**
	 * Ensures that a big array can contain the given number of entries, preserving just a part of the
	 * big array.
	 *
	 * <p>
	 * <strong>Warning:</strong> the returned array might use part of the segments of the original
	 * array, which must be considered read-only after calling this method.
	 *
	 * @param array a big array.
	 * @param length the new minimum length for this big array.
	 * @param preserve the number of elements of the big array that must be preserved in case a new
	 *            allocation is necessary.
	 * @return {@code array}, if it can contain {@code length} entries or more; otherwise, a big array
	 *         with {@code length} entries whose first {@code preserve} entries are the same as those of
	 *         {@code array}.
	 */
	public static char[][] ensureCapacity(final char[][] array, final long length, final long preserve) {
		return length > length(array) ? forceCapacity(array, length, preserve) : array;
	}

	/**
	 * Grows the given big array to the maximum between the given length and the current length
	 * increased by 50%, provided that the given length is larger than the current length.
	 *
	 * <p>
	 * If you want complete control on the big array growth, you should probably use
	 * {@code ensureCapacity()} instead.
	 *
	 * <p>
	 * <strong>Warning:</strong> the returned array might use part of the segments of the original
	 * array, which must be considered read-only after calling this method.
	 *
	 * @param array a big array.
	 * @param length the new minimum length for this big array.
	 * @return {@code array}, if it can contain {@code length} entries; otherwise, a big array with
	 *         max({@code length},{@code length(array)}/&phi;) entries whose first {@code length(array)}
	 *         entries are the same as those of {@code array}.
	 */
	public static char[][] grow(final char[][] array, final long length) {
		final long oldLength = length(array);
		return length > oldLength ? grow(array, length, oldLength) : array;
	}

	/**
	 * Grows the given big array to the maximum between the given length and the current length
	 * increased by 50%, provided that the given length is larger than the current length, preserving
	 * just a part of the big array.
	 *
	 * <p>
	 * If you want complete control on the big array growth, you should probably use
	 * {@code ensureCapacity()} instead.
	 *
	 * <p>
	 * <strong>Warning:</strong> the returned array might use part of the segments of the original
	 * array, which must be considered read-only after calling this method.
	 *
	 * @param array a big array.
	 * @param length the new minimum length for this big array.
	 * @param preserve the number of elements of the big array that must be preserved in case a new
	 *            allocation is necessary.
	 * @return {@code array}, if it can contain {@code length} entries; otherwise, a big array with
	 *         max({@code length},{@code length(array)}/&phi;) entries whose first {@code preserve}
	 *         entries are the same as those of {@code array}.
	 */
	public static char[][] grow(final char[][] array, final long length, final long preserve) {
		final long oldLength = length(array);
		return length > oldLength ? ensureCapacity(array, Math.max(oldLength + (oldLength >> 1), length), preserve) : array;
	}

	/**
	 * Trims the given big array to the given length.
	 *
	 * <p>
	 * <strong>Warning:</strong> the returned array might use part of the segments of the original
	 * array, which must be considered read-only after calling this method.
	 *
	 * @param array a big array.
	 * @param length the new maximum length for the big array.
	 * @return {@code array}, if it contains {@code length} entries or less; otherwise, a big array with
	 *         {@code length} entries whose entries are the same as the first {@code length} entries of
	 *         {@code array}.
	 *
	 */
	public static char[][] trim(final char[][] array, final long length) {
		ensureLength(length);
		final long oldLength = length(array);
		if (length >= oldLength) return array;
		final int baseLength = (int)((length + SEGMENT_MASK) >>> SEGMENT_SHIFT);
		final char[][] base = java.util.Arrays.copyOf(array, baseLength);
		final int residual = (int)(length & SEGMENT_MASK);
		if (residual != 0) base[baseLength - 1] = CharArrays.trim(base[baseLength - 1], residual);
		return base;
	}

	/**
	 * Sets the length of the given big array.
	 *
	 * <p>
	 * <strong>Warning:</strong> the returned array might use part of the segments of the original
	 * array, which must be considered read-only after calling this method.
	 *
	 * @param array a big array.
	 * @param length the new length for the big array.
	 * @return {@code array}, if it contains exactly {@code length} entries; otherwise, if it contains
	 *         <em>more</em> than {@code length} entries, a big array with {@code length} entries whose
	 *         entries are the same as the first {@code length} entries of {@code array}; otherwise, a
	 *         big array with {@code length} entries whose first {@code length(array)} entries are the
	 *         same as those of {@code array}.
	 *
	 */
	public static char[][] setLength(final char[][] array, final long length) {
		final long oldLength = length(array);
		if (length == oldLength) return array;
		if (length < oldLength) return trim(array, length);
		return ensureCapacity(array, length);
	}

	/**
	 * Returns a copy of a portion of a big array.
	 *
	 * @param array a big array.
	 * @param offset the first element to copy.
	 * @param length the number of elements to copy.
	 * @return a new big array containing {@code length} elements of {@code array} starting at
	 *         {@code offset}.
	 */
	public static char[][] copy(final char[][] array, final long offset, final long length) {
		ensureOffsetLength(array, offset, length);
		final char[][] a = CharBigArrays.newBigArray(length);
		copy(array, offset, a, 0, length);
		return a;
	}

	/**
	 * Returns a copy of a big array.
	 *
	 * @param array a big array.
	 * @return a copy of {@code array}.
	 */
	public static char[][] copy(final char[][] array) {
		final char[][] base = array.clone();
		for (int i = base.length; i-- != 0;) base[i] = array[i].clone();
		return base;
	}

	/**
	 * Fills the given big array with the given value.
	 *
	 * <p>
	 * This method uses a backward loop. It is significantly faster than the corresponding method in
	 * {@link java.util.Arrays}.
	 *
	 * @param array a big array.
	 * @param value the new value for all elements of the big array.
	 */
	public static void fill(final char[][] array, final char value) {
		for (int i = array.length; i-- != 0;) java.util.Arrays.fill(array[i], value);
	}

	/**
	 * Fills a portion of the given big array with the given value.
	 *
	 * <p>
	 * If possible (i.e., {@code from} is 0) this method uses a backward loop. In this case, it is
	 * significantly faster than the corresponding method in {@link java.util.Arrays}.
	 *
	 * @param array a big array.
	 * @param from the starting index of the portion to fill.
	 * @param to the end index of the portion to fill.
	 * @param value the new value for all elements of the specified portion of the big array.
	 */
	public static void fill(final char[][] array, final long from, long to, final char value) {
		final long length = length(array);
		BigArrays.ensureFromTo(length, from, to);
		if (length == 0) return; // To avoid addressing array[0]
		int fromSegment = segment(from);
		int toSegment = segment(to);
		int fromDispl = displacement(from);
		int toDispl = displacement(to);
		if (fromSegment == toSegment) {
			java.util.Arrays.fill(array[fromSegment], fromDispl, toDispl, value);
			return;
		}
		if (toDispl != 0) java.util.Arrays.fill(array[toSegment], 0, toDispl, value);
		while (--toSegment > fromSegment) java.util.Arrays.fill(array[toSegment], value);
		java.util.Arrays.fill(array[fromSegment], fromDispl, SEGMENT_SIZE, value);
	}

	/**
	 * Returns true if the two big arrays are elementwise equal.
	 *
	 * <p>
	 * This method uses a backward loop. It is significantly faster than the corresponding method in
	 * {@link java.util.Arrays}.
	 *
	 * @param a1 a big array.
	 * @param a2 another big array.
	 * @return true if the two big arrays are of the same length, and their elements are equal.
	 */
	public static boolean equals(final char[][] a1, final char a2[][]) {
		if (length(a1) != length(a2)) return false;
		int i = a1.length, j;
		char[] t, u;
		while (i-- != 0) {
			t = a1[i];
			u = a2[i];
			j = t.length;
			while (j-- != 0) if (!((t[j]) == (u[j]))) return false;
		}
		return true;
	}

	/* Returns a string representation of the contents of the specified big array.
	 *
	 * The string representation consists of a list of the big array's elements, enclosed in square brackets ("[]"). Adjacent elements are separated by the characters ", " (a comma followed by a space). Returns "null" if {@code a} is null.
	 * @param a the big array whose string representation to return.
	 * @return the string representation of {@code a}.
	 */
	public static String toString(final char[][] a) {
		if (a == null) return "null";
		final long last = length(a) - 1;
		if (last == -1) return "[]";
		final StringBuilder b = new StringBuilder();
		b.append('[');
		for (long i = 0;; i++) {
			b.append(String.valueOf(get(a, i)));
			if (i == last) return b.append(']').toString();
			b.append(", ");
		}
	}

	/**
	 * Ensures that a range given by its first (inclusive) and last (exclusive) elements fits a big
	 * array.
	 *
	 * <p>
	 * This method may be used whenever a big array range check is needed.
	 *
	 * @param a a big array.
	 * @param from a start index (inclusive).
	 * @param to an end index (inclusive).
	 * @throws IllegalArgumentException if {@code from} is greater than {@code to}.
	 * @throws ArrayIndexOutOfBoundsException if {@code from} or {@code to} are greater than the big
	 *             array length or negative.
	 */
	public static void ensureFromTo(final char[][] a, final long from, final long to) {
		BigArrays.ensureFromTo(length(a), from, to);
	}

	/**
	 * Ensures that a range given by an offset and a length fits a big array.
	 *
	 * <p>
	 * This method may be used whenever a big array range check is needed.
	 *
	 * @param a a big array.
	 * @param offset a start index.
	 * @param length a length (the number of elements in the range).
	 * @throws IllegalArgumentException if {@code length} is negative.
	 * @throws ArrayIndexOutOfBoundsException if {@code offset} is negative or
	 *             {@code offset}+{@code length} is greater than the big array length.
	 */
	public static void ensureOffsetLength(final char[][] a, final long offset, final long length) {
		BigArrays.ensureOffsetLength(length(a), offset, length);
	}

	/**
	 * Ensures that two big arrays are of the same length.
	 *
	 * @param a a big array.
	 * @param b another big array.
	 * @throws IllegalArgumentException if the two argument arrays are not of the same length.
	 */
	public static void ensureSameLength(final char[][] a, final char[][] b) {
		if (length(a) != length(b)) throw new IllegalArgumentException("Array size mismatch: " + length(a) + " != " + length(b));
	}

	/**
	 * Shuffles the specified big array fragment using the specified pseudorandom number generator.
	 *
	 * @param a the big array to be shuffled.
	 * @param from the index of the first element (inclusive) to be shuffled.
	 * @param to the index of the last element (exclusive) to be shuffled.
	 * @param random a pseudorandom number generator.
	 * @return {@code a}.
	 */
	public static char[][] shuffle(final char[][] a, final long from, final long to, final Random random) {
		for (long i = to - from; i-- != 0;) {
			final long p = (random.nextLong() & 0x7FFFFFFFFFFFFFFFL) % (i + 1);
			final char t = BigArrays.get(a, from + i);
			BigArrays.set(a, from + i, BigArrays.get(a, from + p));
			BigArrays.set(a, from + p, t);
		}
		return a;
	}

	/**
	 * Shuffles the specified big array using the specified pseudorandom number generator.
	 *
	 * @param a the big array to be shuffled.
	 * @param random a pseudorandom number generator.
	 * @return {@code a}.
	 */
	public static char[][] shuffle(final char[][] a, final Random random) {
		for (long i = length(a); i-- != 0;) {
			final long p = (random.nextLong() & 0x7FFFFFFFFFFFFFFFL) % (i + 1);
			final char t = BigArrays.get(a, i);
			BigArrays.set(a, i, BigArrays.get(a, p));
			BigArrays.set(a, p, t);
		}
		return a;
	}

	/* Generic definitions */
	/* Assertions (useful to generate conditional code) */
	/* Narrowing and widening */
	/* Current type and class (and size, if applicable) */
	/* Value methods */
	/* Interfaces (keys) */
	/* Interfaces (values) */
	/* Types and methods related to primitive-type support in the JDK */
	/* Abstract implementations (keys) */
	/* Abstract implementations (values) */
	/* Static containers (keys) */
	/* Static containers (values) */
	/* Implementations */
	/* Synchronized wrappers */
	/* Unmodifiable wrappers */
	/* Other wrappers */
	/* Methods (keys) */
	/* Methods (values) */
	/* Methods (keys/values) */
	/* Methods that have special names depending on keys (but the special names depend on values) */
	/* Equality */
	/* Object/Reference-only definitions (keys) */
	/* Primitive-type-only definitions (keys) */
	/* Object/Reference-only definitions (values) */
	/* START_OF_JAVA_SOURCE */
	/*
		* Copyright (C) 2004-2024 Sebastiano Vigna
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
	/**
	 * Returns the element of the given big array of specified index.
	 *
	 * @param array a big array.
	 * @param index a position in the big array.
	 * @return the element of the big array at the specified position.
	 */
	public static float get(final float[][] array, final long index) {
		return array[segment(index)][displacement(index)];
	}

	/**
	 * Sets the element of the given big array of specified index.
	 *
	 * @param array a big array.
	 * @param index a position in the big array.
	 * @param value the new value for the array element at the specified position.
	 */
	public static void set(final float[][] array, final long index, float value) {
		array[segment(index)][displacement(index)] = value;
	}

	/**
	 * Swaps the element of the given big array of specified indices.
	 *
	 * @param array a big array.
	 * @param first a position in the big array.
	 * @param second a position in the big array.
	 */
	public static void swap(final float[][] array, final long first, final long second) {
		final float t = array[segment(first)][displacement(first)];
		array[segment(first)][displacement(first)] = array[segment(second)][displacement(second)];
		array[segment(second)][displacement(second)] = t;
	}

	/**
	 * Reverses the order of the elements in the specified big array.
	 *
	 * @param a the big array to be reversed.
	 * @return {@code a}.
	 */
	public static float[][] reverse(final float[][] a) {
		final long length = length(a);
		for (long i = length / 2; i-- != 0;) swap(a, i, length - i - 1);
		return a;
	}

	/**
	 * Adds the specified increment the element of the given big array of specified index.
	 *
	 * @param array a big array.
	 * @param index a position in the big array.
	 * @param incr the increment
	 */
	public static void add(final float[][] array, final long index, float incr) {
		array[segment(index)][displacement(index)] += incr;
	}

	/**
	 * Multiplies by the specified factor the element of the given big array of specified index.
	 *
	 * @param array a big array.
	 * @param index a position in the big array.
	 * @param factor the factor
	 */
	public static void mul(final float[][] array, final long index, float factor) {
		array[segment(index)][displacement(index)] *= factor;
	}

	/**
	 * Increments the element of the given big array of specified index.
	 *
	 * @param array a big array.
	 * @param index a position in the big array.
	 */
	public static void incr(final float[][] array, final long index) {
		array[segment(index)][displacement(index)]++;
	}

	/**
	 * Decrements the element of the given big array of specified index.
	 *
	 * @param array a big array.
	 * @param index a position in the big array.
	 */
	public static void decr(final float[][] array, final long index) {
		array[segment(index)][displacement(index)]--;
	}

	/**
	 * Asserts that the provided big array is correct.
	 *
	 * @param array a big array.
	 * @throws IllegalStateException if the last segment is empty or longer than
	 *             {@link BigArrays#SEGMENT_SIZE}, or any other segment has length different from
	 *             {@link BigArrays#SEGMENT_SIZE}.
	 */
	public static void assertBigArray(final float[][] array) {
		final int l = array.length;
		if (l == 0) return;
		for (int i = 0; i < l - 1; i++) if (array[i].length != SEGMENT_SIZE) throw new IllegalStateException("All segments except for the last one must be of length 2^" + Integer.toString(SEGMENT_SHIFT));
		if (array[l - 1].length > SEGMENT_SIZE) throw new IllegalStateException("The last segment must be of length at most 2^" + Integer.toString(SEGMENT_SHIFT));
		if (array[l - 1].length == 0 && l == 1) throw new IllegalStateException("The last segment must be of nonzero length");
	}

	/**
	 * Returns the length of the given big array.
	 *
	 * @param array a big array.
	 * @return the length of the given big array.
	 */
	public static long length(final float[][] array) {
		final int length = array.length;
		return length == 0 ? 0 : start(length - 1) + array[length - 1].length;
	}

	/**
	 * Copies a big array from the specified source big array, beginning at the specified position, to
	 * the specified position of the destination big array. Handles correctly overlapping regions of the
	 * same big array.
	 *
	 * @param srcArray the source big array.
	 * @param srcPos the starting position in the source big array.
	 * @param destArray the destination big array.
	 * @param destPos the starting position in the destination data.
	 * @param length the number of elements to be copied.
	 */
	public static void copy(final float[][] srcArray, final long srcPos, final float[][] destArray, final long destPos, long length) {
		if (destPos <= srcPos) {
			int srcSegment = segment(srcPos);
			int destSegment = segment(destPos);
			int srcDispl = displacement(srcPos);
			int destDispl = displacement(destPos);
			while (length > 0) {
				final int l = (int)Math.min(length, Math.min(srcArray[srcSegment].length - srcDispl, destArray[destSegment].length - destDispl));
				if (l == 0) throw new ArrayIndexOutOfBoundsException();
				System.arraycopy(srcArray[srcSegment], srcDispl, destArray[destSegment], destDispl, l);
				if ((srcDispl += l) == SEGMENT_SIZE) {
					srcDispl = 0;
					srcSegment++;
				}
				if ((destDispl += l) == SEGMENT_SIZE) {
					destDispl = 0;
					destSegment++;
				}
				length -= l;
			}
		} else {
			int srcSegment = segment(srcPos + length);
			int destSegment = segment(destPos + length);
			int srcDispl = displacement(srcPos + length);
			int destDispl = displacement(destPos + length);
			while (length > 0) {
				if (srcDispl == 0) {
					srcDispl = SEGMENT_SIZE;
					srcSegment--;
				}
				if (destDispl == 0) {
					destDispl = SEGMENT_SIZE;
					destSegment--;
				}
				final int l = (int)Math.min(length, Math.min(srcDispl, destDispl));
				if (l == 0) throw new ArrayIndexOutOfBoundsException();
				System.arraycopy(srcArray[srcSegment], srcDispl - l, destArray[destSegment], destDispl - l, l);
				srcDispl -= l;
				destDispl -= l;
				length -= l;
			}
		}
	}

	/**
	 * Copies a big array from the specified source big array, beginning at the specified position, to
	 * the specified position of the destination array.
	 *
	 * @param srcArray the source big array.
	 * @param srcPos the starting position in the source big array.
	 * @param destArray the destination array.
	 * @param destPos the starting position in the destination data.
	 * @param length the number of elements to be copied.
	 */
	public static void copyFromBig(final float[][] srcArray, final long srcPos, final float[] destArray, int destPos, int length) {
		int srcSegment = segment(srcPos);
		int srcDispl = displacement(srcPos);
		while (length > 0) {
			final int l = Math.min(srcArray[srcSegment].length - srcDispl, length);
			if (l == 0) throw new ArrayIndexOutOfBoundsException();
			System.arraycopy(srcArray[srcSegment], srcDispl, destArray, destPos, l);
			if ((srcDispl += l) == SEGMENT_SIZE) {
				srcDispl = 0;
				srcSegment++;
			}
			destPos += l;
			length -= l;
		}
	}

	/**
	 * Copies an array from the specified source array, beginning at the specified position, to the
	 * specified position of the destination big array.
	 *
	 * @param srcArray the source array.
	 * @param srcPos the starting position in the source array.
	 * @param destArray the destination big array.
	 * @param destPos the starting position in the destination data.
	 * @param length the number of elements to be copied.
	 */
	public static void copyToBig(final float[] srcArray, int srcPos, final float[][] destArray, final long destPos, long length) {
		int destSegment = segment(destPos);
		int destDispl = displacement(destPos);
		while (length > 0) {
			final int l = (int)Math.min(destArray[destSegment].length - destDispl, length);
			if (l == 0) throw new ArrayIndexOutOfBoundsException();
			System.arraycopy(srcArray, srcPos, destArray[destSegment], destDispl, l);
			if ((destDispl += l) == SEGMENT_SIZE) {
				destDispl = 0;
				destSegment++;
			}
			srcPos += l;
			length -= l;
		}
	}

	/**
	 * Turns a standard array into a big array.
	 *
	 * <p>
	 * Note that the returned big array might contain as a segment the original array.
	 *
	 * @param array an array.
	 * @return a new big array with the same length and content of {@code array}.
	 */
	public static float[][] wrap(final float[] array) {
		if (array.length == 0) return FloatBigArrays.EMPTY_BIG_ARRAY;
		if (array.length <= SEGMENT_SIZE) return new float[][] { array };
		final float[][] bigArray = FloatBigArrays.newBigArray(array.length);
		for (int i = 0; i < bigArray.length; i++) System.arraycopy(array, (int)start(i), bigArray[i], 0, bigArray[i].length);
		return bigArray;
	}

	/**
	 * Ensures that a big array can contain the given number of entries.
	 *
	 * <p>
	 * If you cannot foresee whether this big array will need again to be enlarged, you should probably
	 * use {@code grow()} instead.
	 *
	 * <p>
	 * <strong>Warning:</strong> the returned array might use part of the segments of the original
	 * array, which must be considered read-only after calling this method.
	 *
	 * @param array a big array.
	 * @param length the new minimum length for this big array.
	 * @return {@code array}, if it contains {@code length} entries or more; otherwise, a big array with
	 *         {@code length} entries whose first {@code length(array)} entries are the same as those of
	 *         {@code array}.
	 */
	public static float[][] ensureCapacity(final float[][] array, final long length) {
		return ensureCapacity(array, length, length(array));
	}

	/**
	 * Forces a big array to contain the given number of entries, preserving just a part of the big
	 * array.
	 *
	 * <p>
	 * <strong>Warning:</strong> the returned array might use part of the segments of the original
	 * array, which must be considered read-only after calling this method.
	 *
	 * @param array a big array.
	 * @param length the new minimum length for this big array.
	 * @param preserve the number of elements of the big array that must be preserved in case a new
	 *            allocation is necessary.
	 * @return a big array with {@code length} entries whose first {@code preserve} entries are the same
	 *         as those of {@code array}.
	 */
	public static float[][] forceCapacity(final float[][] array, final long length, final long preserve) {
		ensureLength(length);
		final int valid = array.length - (array.length == 0 || array.length > 0 && array[array.length - 1].length == SEGMENT_SIZE ? 0 : 1);
		final int baseLength = (int)((length + SEGMENT_MASK) >>> SEGMENT_SHIFT);
		final float[][] base = java.util.Arrays.copyOf(array, baseLength);
		final int residual = (int)(length & SEGMENT_MASK);
		if (residual != 0) {
			for (int i = valid; i < baseLength - 1; i++) base[i] = new float[SEGMENT_SIZE];
			base[baseLength - 1] = new float[residual];
		} else for (int i = valid; i < baseLength; i++) base[i] = new float[SEGMENT_SIZE];
		if (preserve - (valid * (long)SEGMENT_SIZE) > 0) copy(array, valid * (long)SEGMENT_SIZE, base, valid * (long)SEGMENT_SIZE, preserve - (valid * (long)SEGMENT_SIZE));
		return base;
	}

	/**
	 * Ensures that a big array can contain the given number of entries, preserving just a part of the
	 * big array.
	 *
	 * <p>
	 * <strong>Warning:</strong> the returned array might use part of the segments of the original
	 * array, which must be considered read-only after calling this method.
	 *
	 * @param array a big array.
	 * @param length the new minimum length for this big array.
	 * @param preserve the number of elements of the big array that must be preserved in case a new
	 *            allocation is necessary.
	 * @return {@code array}, if it can contain {@code length} entries or more; otherwise, a big array
	 *         with {@code length} entries whose first {@code preserve} entries are the same as those of
	 *         {@code array}.
	 */
	public static float[][] ensureCapacity(final float[][] array, final long length, final long preserve) {
		return length > length(array) ? forceCapacity(array, length, preserve) : array;
	}

	/**
	 * Grows the given big array to the maximum between the given length and the current length
	 * increased by 50%, provided that the given length is larger than the current length.
	 *
	 * <p>
	 * If you want complete control on the big array growth, you should probably use
	 * {@code ensureCapacity()} instead.
	 *
	 * <p>
	 * <strong>Warning:</strong> the returned array might use part of the segments of the original
	 * array, which must be considered read-only after calling this method.
	 *
	 * @param array a big array.
	 * @param length the new minimum length for this big array.
	 * @return {@code array}, if it can contain {@code length} entries; otherwise, a big array with
	 *         max({@code length},{@code length(array)}/&phi;) entries whose first {@code length(array)}
	 *         entries are the same as those of {@code array}.
	 */
	public static float[][] grow(final float[][] array, final long length) {
		final long oldLength = length(array);
		return length > oldLength ? grow(array, length, oldLength) : array;
	}

	/**
	 * Grows the given big array to the maximum between the given length and the current length
	 * increased by 50%, provided that the given length is larger than the current length, preserving
	 * just a part of the big array.
	 *
	 * <p>
	 * If you want complete control on the big array growth, you should probably use
	 * {@code ensureCapacity()} instead.
	 *
	 * <p>
	 * <strong>Warning:</strong> the returned array might use part of the segments of the original
	 * array, which must be considered read-only after calling this method.
	 *
	 * @param array a big array.
	 * @param length the new minimum length for this big array.
	 * @param preserve the number of elements of the big array that must be preserved in case a new
	 *            allocation is necessary.
	 * @return {@code array}, if it can contain {@code length} entries; otherwise, a big array with
	 *         max({@code length},{@code length(array)}/&phi;) entries whose first {@code preserve}
	 *         entries are the same as those of {@code array}.
	 */
	public static float[][] grow(final float[][] array, final long length, final long preserve) {
		final long oldLength = length(array);
		return length > oldLength ? ensureCapacity(array, Math.max(oldLength + (oldLength >> 1), length), preserve) : array;
	}

	/**
	 * Trims the given big array to the given length.
	 *
	 * <p>
	 * <strong>Warning:</strong> the returned array might use part of the segments of the original
	 * array, which must be considered read-only after calling this method.
	 *
	 * @param array a big array.
	 * @param length the new maximum length for the big array.
	 * @return {@code array}, if it contains {@code length} entries or less; otherwise, a big array with
	 *         {@code length} entries whose entries are the same as the first {@code length} entries of
	 *         {@code array}.
	 *
	 */
	public static float[][] trim(final float[][] array, final long length) {
		ensureLength(length);
		final long oldLength = length(array);
		if (length >= oldLength) return array;
		final int baseLength = (int)((length + SEGMENT_MASK) >>> SEGMENT_SHIFT);
		final float[][] base = java.util.Arrays.copyOf(array, baseLength);
		final int residual = (int)(length & SEGMENT_MASK);
		if (residual != 0) base[baseLength - 1] = FloatArrays.trim(base[baseLength - 1], residual);
		return base;
	}

	/**
	 * Sets the length of the given big array.
	 *
	 * <p>
	 * <strong>Warning:</strong> the returned array might use part of the segments of the original
	 * array, which must be considered read-only after calling this method.
	 *
	 * @param array a big array.
	 * @param length the new length for the big array.
	 * @return {@code array}, if it contains exactly {@code length} entries; otherwise, if it contains
	 *         <em>more</em> than {@code length} entries, a big array with {@code length} entries whose
	 *         entries are the same as the first {@code length} entries of {@code array}; otherwise, a
	 *         big array with {@code length} entries whose first {@code length(array)} entries are the
	 *         same as those of {@code array}.
	 *
	 */
	public static float[][] setLength(final float[][] array, final long length) {
		final long oldLength = length(array);
		if (length == oldLength) return array;
		if (length < oldLength) return trim(array, length);
		return ensureCapacity(array, length);
	}

	/**
	 * Returns a copy of a portion of a big array.
	 *
	 * @param array a big array.
	 * @param offset the first element to copy.
	 * @param length the number of elements to copy.
	 * @return a new big array containing {@code length} elements of {@code array} starting at
	 *         {@code offset}.
	 */
	public static float[][] copy(final float[][] array, final long offset, final long length) {
		ensureOffsetLength(array, offset, length);
		final float[][] a = FloatBigArrays.newBigArray(length);
		copy(array, offset, a, 0, length);
		return a;
	}

	/**
	 * Returns a copy of a big array.
	 *
	 * @param array a big array.
	 * @return a copy of {@code array}.
	 */
	public static float[][] copy(final float[][] array) {
		final float[][] base = array.clone();
		for (int i = base.length; i-- != 0;) base[i] = array[i].clone();
		return base;
	}

	/**
	 * Fills the given big array with the given value.
	 *
	 * <p>
	 * This method uses a backward loop. It is significantly faster than the corresponding method in
	 * {@link java.util.Arrays}.
	 *
	 * @param array a big array.
	 * @param value the new value for all elements of the big array.
	 */
	public static void fill(final float[][] array, final float value) {
		for (int i = array.length; i-- != 0;) java.util.Arrays.fill(array[i], value);
	}

	/**
	 * Fills a portion of the given big array with the given value.
	 *
	 * <p>
	 * If possible (i.e., {@code from} is 0) this method uses a backward loop. In this case, it is
	 * significantly faster than the corresponding method in {@link java.util.Arrays}.
	 *
	 * @param array a big array.
	 * @param from the starting index of the portion to fill.
	 * @param to the end index of the portion to fill.
	 * @param value the new value for all elements of the specified portion of the big array.
	 */
	public static void fill(final float[][] array, final long from, long to, final float value) {
		final long length = length(array);
		BigArrays.ensureFromTo(length, from, to);
		if (length == 0) return; // To avoid addressing array[0]
		int fromSegment = segment(from);
		int toSegment = segment(to);
		int fromDispl = displacement(from);
		int toDispl = displacement(to);
		if (fromSegment == toSegment) {
			java.util.Arrays.fill(array[fromSegment], fromDispl, toDispl, value);
			return;
		}
		if (toDispl != 0) java.util.Arrays.fill(array[toSegment], 0, toDispl, value);
		while (--toSegment > fromSegment) java.util.Arrays.fill(array[toSegment], value);
		java.util.Arrays.fill(array[fromSegment], fromDispl, SEGMENT_SIZE, value);
	}

	/**
	 * Returns true if the two big arrays are elementwise equal.
	 *
	 * <p>
	 * This method uses a backward loop. It is significantly faster than the corresponding method in
	 * {@link java.util.Arrays}.
	 *
	 * @param a1 a big array.
	 * @param a2 another big array.
	 * @return true if the two big arrays are of the same length, and their elements are equal.
	 */
	public static boolean equals(final float[][] a1, final float a2[][]) {
		if (length(a1) != length(a2)) return false;
		int i = a1.length, j;
		float[] t, u;
		while (i-- != 0) {
			t = a1[i];
			u = a2[i];
			j = t.length;
			while (j-- != 0) if (!(Float.floatToIntBits(t[j]) == Float.floatToIntBits(u[j]))) return false;
		}
		return true;
	}

	/* Returns a string representation of the contents of the specified big array.
	 *
	 * The string representation consists of a list of the big array's elements, enclosed in square brackets ("[]"). Adjacent elements are separated by the characters ", " (a comma followed by a space). Returns "null" if {@code a} is null.
	 * @param a the big array whose string representation to return.
	 * @return the string representation of {@code a}.
	 */
	public static String toString(final float[][] a) {
		if (a == null) return "null";
		final long last = length(a) - 1;
		if (last == -1) return "[]";
		final StringBuilder b = new StringBuilder();
		b.append('[');
		for (long i = 0;; i++) {
			b.append(String.valueOf(get(a, i)));
			if (i == last) return b.append(']').toString();
			b.append(", ");
		}
	}

	/**
	 * Ensures that a range given by its first (inclusive) and last (exclusive) elements fits a big
	 * array.
	 *
	 * <p>
	 * This method may be used whenever a big array range check is needed.
	 *
	 * @param a a big array.
	 * @param from a start index (inclusive).
	 * @param to an end index (inclusive).
	 * @throws IllegalArgumentException if {@code from} is greater than {@code to}.
	 * @throws ArrayIndexOutOfBoundsException if {@code from} or {@code to} are greater than the big
	 *             array length or negative.
	 */
	public static void ensureFromTo(final float[][] a, final long from, final long to) {
		BigArrays.ensureFromTo(length(a), from, to);
	}

	/**
	 * Ensures that a range given by an offset and a length fits a big array.
	 *
	 * <p>
	 * This method may be used whenever a big array range check is needed.
	 *
	 * @param a a big array.
	 * @param offset a start index.
	 * @param length a length (the number of elements in the range).
	 * @throws IllegalArgumentException if {@code length} is negative.
	 * @throws ArrayIndexOutOfBoundsException if {@code offset} is negative or
	 *             {@code offset}+{@code length} is greater than the big array length.
	 */
	public static void ensureOffsetLength(final float[][] a, final long offset, final long length) {
		BigArrays.ensureOffsetLength(length(a), offset, length);
	}

	/**
	 * Ensures that two big arrays are of the same length.
	 *
	 * @param a a big array.
	 * @param b another big array.
	 * @throws IllegalArgumentException if the two argument arrays are not of the same length.
	 */
	public static void ensureSameLength(final float[][] a, final float[][] b) {
		if (length(a) != length(b)) throw new IllegalArgumentException("Array size mismatch: " + length(a) + " != " + length(b));
	}

	/**
	 * Shuffles the specified big array fragment using the specified pseudorandom number generator.
	 *
	 * @param a the big array to be shuffled.
	 * @param from the index of the first element (inclusive) to be shuffled.
	 * @param to the index of the last element (exclusive) to be shuffled.
	 * @param random a pseudorandom number generator.
	 * @return {@code a}.
	 */
	public static float[][] shuffle(final float[][] a, final long from, final long to, final Random random) {
		for (long i = to - from; i-- != 0;) {
			final long p = (random.nextLong() & 0x7FFFFFFFFFFFFFFFL) % (i + 1);
			final float t = BigArrays.get(a, from + i);
			BigArrays.set(a, from + i, BigArrays.get(a, from + p));
			BigArrays.set(a, from + p, t);
		}
		return a;
	}

	/**
	 * Shuffles the specified big array using the specified pseudorandom number generator.
	 *
	 * @param a the big array to be shuffled.
	 * @param random a pseudorandom number generator.
	 * @return {@code a}.
	 */
	public static float[][] shuffle(final float[][] a, final Random random) {
		for (long i = length(a); i-- != 0;) {
			final long p = (random.nextLong() & 0x7FFFFFFFFFFFFFFFL) % (i + 1);
			final float t = BigArrays.get(a, i);
			BigArrays.set(a, i, BigArrays.get(a, p));
			BigArrays.set(a, p, t);
		}
		return a;
	}

	/* Generic definitions */
	/* Assertions (useful to generate conditional code) */
	/* Narrowing and widening */
	/* Current type and class (and size, if applicable) */
	/* Value methods */
	/* Interfaces (keys) */
	/* Interfaces (values) */
	/* Types and methods related to primitive-type support in the JDK */
	/* Abstract implementations (keys) */
	/* Abstract implementations (values) */
	/* Static containers (keys) */
	/* Static containers (values) */
	/* Implementations */
	/* Synchronized wrappers */
	/* Unmodifiable wrappers */
	/* Other wrappers */
	/* Methods (keys) */
	/* Methods (values) */
	/* Methods (keys/values) */
	/* Methods that have special names depending on keys (but the special names depend on values) */
	/* Equality */
	/* Object/Reference-only definitions (keys) */
	/* Object/Reference-only definitions (values) */
	/* START_OF_JAVA_SOURCE */
	/*
		* Copyright (C) 2004-2024 Sebastiano Vigna
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
	/**
	 * Returns the element of the given big array of specified index.
	 *
	 * @param array a big array.
	 * @param index a position in the big array.
	 * @return the element of the big array at the specified position.
	 */
	public static <K> K get(final K[][] array, final long index) {
		return array[segment(index)][displacement(index)];
	}

	/**
	 * Sets the element of the given big array of specified index.
	 *
	 * @param array a big array.
	 * @param index a position in the big array.
	 * @param value the new value for the array element at the specified position.
	 */
	public static <K> void set(final K[][] array, final long index, K value) {
		array[segment(index)][displacement(index)] = value;
	}

	/**
	 * Swaps the element of the given big array of specified indices.
	 *
	 * @param array a big array.
	 * @param first a position in the big array.
	 * @param second a position in the big array.
	 */
	public static <K> void swap(final K[][] array, final long first, final long second) {
		final K t = array[segment(first)][displacement(first)];
		array[segment(first)][displacement(first)] = array[segment(second)][displacement(second)];
		array[segment(second)][displacement(second)] = t;
	}

	/**
	 * Reverses the order of the elements in the specified big array.
	 *
	 * @param a the big array to be reversed.
	 * @return {@code a}.
	 */
	public static <K> K[][] reverse(final K[][] a) {
		final long length = length(a);
		for (long i = length / 2; i-- != 0;) swap(a, i, length - i - 1);
		return a;
	}

	/**
	 * Asserts that the provided big array is correct.
	 *
	 * @param array a big array.
	 * @throws IllegalStateException if the last segment is empty or longer than
	 *             {@link BigArrays#SEGMENT_SIZE}, or any other segment has length different from
	 *             {@link BigArrays#SEGMENT_SIZE}.
	 */
	public static <K> void assertBigArray(final K[][] array) {
		final int l = array.length;
		if (l == 0) return;
		for (int i = 0; i < l - 1; i++) if (array[i].length != SEGMENT_SIZE) throw new IllegalStateException("All segments except for the last one must be of length 2^" + Integer.toString(SEGMENT_SHIFT));
		if (array[l - 1].length > SEGMENT_SIZE) throw new IllegalStateException("The last segment must be of length at most 2^" + Integer.toString(SEGMENT_SHIFT));
		if (array[l - 1].length == 0 && l == 1) throw new IllegalStateException("The last segment must be of nonzero length");
	}

	/**
	 * Returns the length of the given big array.
	 *
	 * @param array a big array.
	 * @return the length of the given big array.
	 */
	public static <K> long length(final K[][] array) {
		final int length = array.length;
		return length == 0 ? 0 : start(length - 1) + array[length - 1].length;
	}

	/**
	 * Copies a big array from the specified source big array, beginning at the specified position, to
	 * the specified position of the destination big array. Handles correctly overlapping regions of the
	 * same big array.
	 *
	 * @param srcArray the source big array.
	 * @param srcPos the starting position in the source big array.
	 * @param destArray the destination big array.
	 * @param destPos the starting position in the destination data.
	 * @param length the number of elements to be copied.
	 */
	public static <K> void copy(final K[][] srcArray, final long srcPos, final K[][] destArray, final long destPos, long length) {
		if (destPos <= srcPos) {
			int srcSegment = segment(srcPos);
			int destSegment = segment(destPos);
			int srcDispl = displacement(srcPos);
			int destDispl = displacement(destPos);
			while (length > 0) {
				final int l = (int)Math.min(length, Math.min(srcArray[srcSegment].length - srcDispl, destArray[destSegment].length - destDispl));
				if (l == 0) throw new ArrayIndexOutOfBoundsException();
				System.arraycopy(srcArray[srcSegment], srcDispl, destArray[destSegment], destDispl, l);
				if ((srcDispl += l) == SEGMENT_SIZE) {
					srcDispl = 0;
					srcSegment++;
				}
				if ((destDispl += l) == SEGMENT_SIZE) {
					destDispl = 0;
					destSegment++;
				}
				length -= l;
			}
		} else {
			int srcSegment = segment(srcPos + length);
			int destSegment = segment(destPos + length);
			int srcDispl = displacement(srcPos + length);
			int destDispl = displacement(destPos + length);
			while (length > 0) {
				if (srcDispl == 0) {
					srcDispl = SEGMENT_SIZE;
					srcSegment--;
				}
				if (destDispl == 0) {
					destDispl = SEGMENT_SIZE;
					destSegment--;
				}
				final int l = (int)Math.min(length, Math.min(srcDispl, destDispl));
				if (l == 0) throw new ArrayIndexOutOfBoundsException();
				System.arraycopy(srcArray[srcSegment], srcDispl - l, destArray[destSegment], destDispl - l, l);
				srcDispl -= l;
				destDispl -= l;
				length -= l;
			}
		}
	}

	/**
	 * Copies a big array from the specified source big array, beginning at the specified position, to
	 * the specified position of the destination array.
	 *
	 * @param srcArray the source big array.
	 * @param srcPos the starting position in the source big array.
	 * @param destArray the destination array.
	 * @param destPos the starting position in the destination data.
	 * @param length the number of elements to be copied.
	 */
	public static <K> void copyFromBig(final K[][] srcArray, final long srcPos, final K[] destArray, int destPos, int length) {
		int srcSegment = segment(srcPos);
		int srcDispl = displacement(srcPos);
		while (length > 0) {
			final int l = Math.min(srcArray[srcSegment].length - srcDispl, length);
			if (l == 0) throw new ArrayIndexOutOfBoundsException();
			System.arraycopy(srcArray[srcSegment], srcDispl, destArray, destPos, l);
			if ((srcDispl += l) == SEGMENT_SIZE) {
				srcDispl = 0;
				srcSegment++;
			}
			destPos += l;
			length -= l;
		}
	}

	/**
	 * Copies an array from the specified source array, beginning at the specified position, to the
	 * specified position of the destination big array.
	 *
	 * @param srcArray the source array.
	 * @param srcPos the starting position in the source array.
	 * @param destArray the destination big array.
	 * @param destPos the starting position in the destination data.
	 * @param length the number of elements to be copied.
	 */
	public static <K> void copyToBig(final K[] srcArray, int srcPos, final K[][] destArray, final long destPos, long length) {
		int destSegment = segment(destPos);
		int destDispl = displacement(destPos);
		while (length > 0) {
			final int l = (int)Math.min(destArray[destSegment].length - destDispl, length);
			if (l == 0) throw new ArrayIndexOutOfBoundsException();
			System.arraycopy(srcArray, srcPos, destArray[destSegment], destDispl, l);
			if ((destDispl += l) == SEGMENT_SIZE) {
				destDispl = 0;
				destSegment++;
			}
			srcPos += l;
			length -= l;
		}
	}

	/**
	 * Turns a standard array into a big array.
	 *
	 * <p>
	 * Note that the returned big array might contain as a segment the original array.
	 *
	 * @param array an array.
	 * @return a new big array with the same length and content of {@code array}.
	 */
	@SuppressWarnings("unchecked")
	public static <K> K[][] wrap(final K[] array) {
		if (array.length == 0 && array.getClass() == Object[].class) return (K[][])ObjectBigArrays.EMPTY_BIG_ARRAY;
		if (array.length <= SEGMENT_SIZE) {
			final K[][] bigArray = (K[][])new Object[1][];
			bigArray[0] = array;
			return bigArray;
		}
		final K[][] bigArray = (K[][])ObjectBigArrays.newBigArray(array.getClass(), array.length);
		for (int i = 0; i < bigArray.length; i++) System.arraycopy(array, (int)start(i), bigArray[i], 0, bigArray[i].length);
		return bigArray;
	}

	/**
	 * Ensures that a big array can contain the given number of entries.
	 *
	 * <p>
	 * If you cannot foresee whether this big array will need again to be enlarged, you should probably
	 * use {@code grow()} instead.
	 *
	 * <p>
	 * <strong>Warning:</strong> the returned array might use part of the segments of the original
	 * array, which must be considered read-only after calling this method.
	 *
	 * @param array a big array.
	 * @param length the new minimum length for this big array.
	 * @return {@code array}, if it contains {@code length} entries or more; otherwise, a big array with
	 *         {@code length} entries whose first {@code length(array)} entries are the same as those of
	 *         {@code array}.
	 */
	public static <K> K[][] ensureCapacity(final K[][] array, final long length) {
		return ensureCapacity(array, length, length(array));
	}

	/**
	 * Forces a big array to contain the given number of entries, preserving just a part of the big
	 * array.
	 *
	 * <p>
	 * This method returns a new big array of the given length whose element are of the same class as of
	 * those of {@code array}.
	 *
	 * <p>
	 * <strong>Warning:</strong> the returned array might use part of the segments of the original
	 * array, which must be considered read-only after calling this method.
	 *
	 * @param array a big array.
	 * @param length the new minimum length for this big array.
	 * @param preserve the number of elements of the big array that must be preserved in case a new
	 *            allocation is necessary.
	 * @return a big array with {@code length} entries whose first {@code preserve} entries are the same
	 *         as those of {@code array}.
	 */
	@SuppressWarnings("unchecked")
	public static <K> K[][] forceCapacity(final K[][] array, final long length, final long preserve) {
		ensureLength(length);
		final int valid = array.length - (array.length == 0 || array.length > 0 && array[array.length - 1].length == SEGMENT_SIZE ? 0 : 1);
		final int baseLength = (int)((length + SEGMENT_MASK) >>> SEGMENT_SHIFT);
		final K[][] base = java.util.Arrays.copyOf(array, baseLength);
		final int residual = (int)(length & SEGMENT_MASK);
		if (residual != 0) {
			for (int i = valid; i < baseLength - 1; i++) base[i] = (K[])new Object[SEGMENT_SIZE];
			base[baseLength - 1] = (K[])new Object[residual];
		} else for (int i = valid; i < baseLength; i++) base[i] = (K[])new Object[SEGMENT_SIZE];
		if (preserve - (valid * (long)SEGMENT_SIZE) > 0) copy(array, valid * (long)SEGMENT_SIZE, base, valid * (long)SEGMENT_SIZE, preserve - (valid * (long)SEGMENT_SIZE));
		return base;
	}

	/**
	 * Ensures that a big array can contain the given number of entries, preserving just a part of the
	 * big array.
	 *
	 * <p>
	 * This method returns a new big array of the given length whose element are of the same class as of
	 * those of {@code array}.
	 *
	 * <p>
	 * <strong>Warning:</strong> the returned array might use part of the segments of the original
	 * array, which must be considered read-only after calling this method.
	 *
	 * @param array a big array.
	 * @param length the new minimum length for this big array.
	 * @param preserve the number of elements of the big array that must be preserved in case a new
	 *            allocation is necessary.
	 * @return {@code array}, if it can contain {@code length} entries or more; otherwise, a big array
	 *         with {@code length} entries whose first {@code preserve} entries are the same as those of
	 *         {@code array}.
	 */
	public static <K> K[][] ensureCapacity(final K[][] array, final long length, final long preserve) {
		return length > length(array) ? forceCapacity(array, length, preserve) : array;
	}

	/**
	 * Grows the given big array to the maximum between the given length and the current length
	 * increased by 50%, provided that the given length is larger than the current length.
	 *
	 * <p>
	 * If you want complete control on the big array growth, you should probably use
	 * {@code ensureCapacity()} instead.
	 *
	 * <p>
	 * <strong>Warning:</strong> the returned array might use part of the segments of the original
	 * array, which must be considered read-only after calling this method.
	 *
	 * @param array a big array.
	 * @param length the new minimum length for this big array.
	 * @return {@code array}, if it can contain {@code length} entries; otherwise, a big array with
	 *         max({@code length},{@code length(array)}/&phi;) entries whose first {@code length(array)}
	 *         entries are the same as those of {@code array}.
	 */
	public static <K> K[][] grow(final K[][] array, final long length) {
		final long oldLength = length(array);
		return length > oldLength ? grow(array, length, oldLength) : array;
	}

	/**
	 * Grows the given big array to the maximum between the given length and the current length
	 * increased by 50%, provided that the given length is larger than the current length, preserving
	 * just a part of the big array.
	 *
	 * <p>
	 * If you want complete control on the big array growth, you should probably use
	 * {@code ensureCapacity()} instead.
	 *
	 * <p>
	 * <strong>Warning:</strong> the returned array might use part of the segments of the original
	 * array, which must be considered read-only after calling this method.
	 *
	 * @param array a big array.
	 * @param length the new minimum length for this big array.
	 * @param preserve the number of elements of the big array that must be preserved in case a new
	 *            allocation is necessary.
	 * @return {@code array}, if it can contain {@code length} entries; otherwise, a big array with
	 *         max({@code length},{@code length(array)}/&phi;) entries whose first {@code preserve}
	 *         entries are the same as those of {@code array}.
	 */
	public static <K> K[][] grow(final K[][] array, final long length, final long preserve) {
		final long oldLength = length(array);
		return length > oldLength ? ensureCapacity(array, Math.max(oldLength + (oldLength >> 1), length), preserve) : array;
	}

	/**
	 * Trims the given big array to the given length.
	 *
	 * <p>
	 * <strong>Warning:</strong> the returned array might use part of the segments of the original
	 * array, which must be considered read-only after calling this method.
	 *
	 * @param array a big array.
	 * @param length the new maximum length for the big array.
	 * @return {@code array}, if it contains {@code length} entries or less; otherwise, a big array with
	 *         {@code length} entries whose entries are the same as the first {@code length} entries of
	 *         {@code array}.
	 *
	 */
	public static <K> K[][] trim(final K[][] array, final long length) {
		ensureLength(length);
		final long oldLength = length(array);
		if (length >= oldLength) return array;
		final int baseLength = (int)((length + SEGMENT_MASK) >>> SEGMENT_SHIFT);
		final K[][] base = java.util.Arrays.copyOf(array, baseLength);
		final int residual = (int)(length & SEGMENT_MASK);
		if (residual != 0) base[baseLength - 1] = ObjectArrays.trim(base[baseLength - 1], residual);
		return base;
	}

	/**
	 * Sets the length of the given big array.
	 *
	 * <p>
	 * <strong>Warning:</strong> the returned array might use part of the segments of the original
	 * array, which must be considered read-only after calling this method.
	 *
	 * @param array a big array.
	 * @param length the new length for the big array.
	 * @return {@code array}, if it contains exactly {@code length} entries; otherwise, if it contains
	 *         <em>more</em> than {@code length} entries, a big array with {@code length} entries whose
	 *         entries are the same as the first {@code length} entries of {@code array}; otherwise, a
	 *         big array with {@code length} entries whose first {@code length(array)} entries are the
	 *         same as those of {@code array}.
	 *
	 */
	public static <K> K[][] setLength(final K[][] array, final long length) {
		final long oldLength = length(array);
		if (length == oldLength) return array;
		if (length < oldLength) return trim(array, length);
		return ensureCapacity(array, length);
	}

	/**
	 * Returns a copy of a portion of a big array.
	 *
	 * @param array a big array.
	 * @param offset the first element to copy.
	 * @param length the number of elements to copy.
	 * @return a new big array containing {@code length} elements of {@code array} starting at
	 *         {@code offset}.
	 */
	public static <K> K[][] copy(final K[][] array, final long offset, final long length) {
		ensureOffsetLength(array, offset, length);
		final K[][] a = ObjectBigArrays.newBigArray(array, length);
		copy(array, offset, a, 0, length);
		return a;
	}

	/**
	 * Returns a copy of a big array.
	 *
	 * @param array a big array.
	 * @return a copy of {@code array}.
	 */
	public static <K> K[][] copy(final K[][] array) {
		final K[][] base = array.clone();
		for (int i = base.length; i-- != 0;) base[i] = array[i].clone();
		return base;
	}

	/**
	 * Fills the given big array with the given value.
	 *
	 * <p>
	 * This method uses a backward loop. It is significantly faster than the corresponding method in
	 * {@link java.util.Arrays}.
	 *
	 * @param array a big array.
	 * @param value the new value for all elements of the big array.
	 */
	public static <K> void fill(final K[][] array, final K value) {
		for (int i = array.length; i-- != 0;) java.util.Arrays.fill(array[i], value);
	}

	/**
	 * Fills a portion of the given big array with the given value.
	 *
	 * <p>
	 * If possible (i.e., {@code from} is 0) this method uses a backward loop. In this case, it is
	 * significantly faster than the corresponding method in {@link java.util.Arrays}.
	 *
	 * @param array a big array.
	 * @param from the starting index of the portion to fill.
	 * @param to the end index of the portion to fill.
	 * @param value the new value for all elements of the specified portion of the big array.
	 */
	public static <K> void fill(final K[][] array, final long from, long to, final K value) {
		final long length = length(array);
		BigArrays.ensureFromTo(length, from, to);
		if (length == 0) return; // To avoid addressing array[0]
		int fromSegment = segment(from);
		int toSegment = segment(to);
		int fromDispl = displacement(from);
		int toDispl = displacement(to);
		if (fromSegment == toSegment) {
			java.util.Arrays.fill(array[fromSegment], fromDispl, toDispl, value);
			return;
		}
		if (toDispl != 0) java.util.Arrays.fill(array[toSegment], 0, toDispl, value);
		while (--toSegment > fromSegment) java.util.Arrays.fill(array[toSegment], value);
		java.util.Arrays.fill(array[fromSegment], fromDispl, SEGMENT_SIZE, value);
	}

	/**
	 * Returns true if the two big arrays are elementwise equal.
	 *
	 * <p>
	 * This method uses a backward loop. It is significantly faster than the corresponding method in
	 * {@link java.util.Arrays}.
	 *
	 * @param a1 a big array.
	 * @param a2 another big array.
	 * @return true if the two big arrays are of the same length, and their elements are equal.
	 */
	public static <K> boolean equals(final K[][] a1, final K a2[][]) {
		if (length(a1) != length(a2)) return false;
		int i = a1.length, j;
		K[] t, u;
		while (i-- != 0) {
			t = a1[i];
			u = a2[i];
			j = t.length;
			while (j-- != 0) if (!java.util.Objects.equals(t[j], u[j])) return false;
		}
		return true;
	}

	/* Returns a string representation of the contents of the specified big array.
	 *
	 * The string representation consists of a list of the big array's elements, enclosed in square brackets ("[]"). Adjacent elements are separated by the characters ", " (a comma followed by a space). Returns "null" if {@code a} is null.
	 * @param a the big array whose string representation to return.
	 * @return the string representation of {@code a}.
	 */
	public static <K> String toString(final K[][] a) {
		if (a == null) return "null";
		final long last = length(a) - 1;
		if (last == -1) return "[]";
		final StringBuilder b = new StringBuilder();
		b.append('[');
		for (long i = 0;; i++) {
			b.append(String.valueOf(get(a, i)));
			if (i == last) return b.append(']').toString();
			b.append(", ");
		}
	}

	/**
	 * Ensures that a range given by its first (inclusive) and last (exclusive) elements fits a big
	 * array.
	 *
	 * <p>
	 * This method may be used whenever a big array range check is needed.
	 *
	 * @param a a big array.
	 * @param from a start index (inclusive).
	 * @param to an end index (inclusive).
	 * @throws IllegalArgumentException if {@code from} is greater than {@code to}.
	 * @throws ArrayIndexOutOfBoundsException if {@code from} or {@code to} are greater than the big
	 *             array length or negative.
	 */
	public static <K> void ensureFromTo(final K[][] a, final long from, final long to) {
		BigArrays.ensureFromTo(length(a), from, to);
	}

	/**
	 * Ensures that a range given by an offset and a length fits a big array.
	 *
	 * <p>
	 * This method may be used whenever a big array range check is needed.
	 *
	 * @param a a big array.
	 * @param offset a start index.
	 * @param length a length (the number of elements in the range).
	 * @throws IllegalArgumentException if {@code length} is negative.
	 * @throws ArrayIndexOutOfBoundsException if {@code offset} is negative or
	 *             {@code offset}+{@code length} is greater than the big array length.
	 */
	public static <K> void ensureOffsetLength(final K[][] a, final long offset, final long length) {
		BigArrays.ensureOffsetLength(length(a), offset, length);
	}

	/**
	 * Ensures that two big arrays are of the same length.
	 *
	 * @param a a big array.
	 * @param b another big array.
	 * @throws IllegalArgumentException if the two argument arrays are not of the same length.
	 */
	public static <K> void ensureSameLength(final K[][] a, final K[][] b) {
		if (length(a) != length(b)) throw new IllegalArgumentException("Array size mismatch: " + length(a) + " != " + length(b));
	}

	/**
	 * Shuffles the specified big array fragment using the specified pseudorandom number generator.
	 *
	 * @param a the big array to be shuffled.
	 * @param from the index of the first element (inclusive) to be shuffled.
	 * @param to the index of the last element (exclusive) to be shuffled.
	 * @param random a pseudorandom number generator.
	 * @return {@code a}.
	 */
	public static <K> K[][] shuffle(final K[][] a, final long from, final long to, final Random random) {
		for (long i = to - from; i-- != 0;) {
			final long p = (random.nextLong() & 0x7FFFFFFFFFFFFFFFL) % (i + 1);
			final K t = BigArrays.get(a, from + i);
			BigArrays.set(a, from + i, BigArrays.get(a, from + p));
			BigArrays.set(a, from + p, t);
		}
		return a;
	}

	/**
	 * Shuffles the specified big array using the specified pseudorandom number generator.
	 *
	 * @param a the big array to be shuffled.
	 * @param random a pseudorandom number generator.
	 * @return {@code a}.
	 */
	public static <K> K[][] shuffle(final K[][] a, final Random random) {
		for (long i = length(a); i-- != 0;) {
			final long p = (random.nextLong() & 0x7FFFFFFFFFFFFFFFL) % (i + 1);
			final K t = BigArrays.get(a, i);
			BigArrays.set(a, i, BigArrays.get(a, p));
			BigArrays.set(a, p, t);
		}
		return a;
	}

	public static void main(final String arg[]) {
		int[][] a = IntBigArrays.newBigArray(1L << Integer.parseInt(arg[0]));
		long x, y, z, start;
		for (int k = 10; k-- != 0;) {
			start = -System.currentTimeMillis();
			x = 0;
			for (long i = length(a); i-- != 0;) x ^= i ^ get(a, i);
			if (x == 0) System.err.println();
			System.out.println("Single loop: " + (start + System.currentTimeMillis()) + "ms");
			start = -System.currentTimeMillis();
			y = 0;
			for (int i = a.length; i-- != 0;) {
				final int[] t = a[i];
				for (int d = t.length; d-- != 0;) y ^= t[d] ^ index(i, d);
			}
			if (y == 0) System.err.println();
			if (x != y) throw new AssertionError();
			System.out.println("Double loop: " + (start + System.currentTimeMillis()) + "ms");
			z = 0;
			long j = length(a);
			for (int i = a.length; i-- != 0;) {
				final int[] t = a[i];
				for (int d = t.length; d-- != 0;) y ^= t[d] ^ --j;
			}
			if (z == 0) System.err.println();
			if (x != z) throw new AssertionError();
			System.out.println("Double loop (with additional index): " + (start + System.currentTimeMillis()) + "ms");
		}
	}
}
