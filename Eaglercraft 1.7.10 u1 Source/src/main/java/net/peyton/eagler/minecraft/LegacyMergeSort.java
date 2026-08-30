package net.peyton.eagler.minecraft;

import java.lang.reflect.Array;
import java.util.Comparator;

/*
 * Slower but this is what vanilla uses.
 */
public class LegacyMergeSort {
	private LegacyMergeSort() {
	}

	public static void sort(long[] a) {
		sort1(a, 0, a.length);
	}

	public static void sort(long[] a, int fromIndex, int toIndex) {
		rangeCheck(a.length, fromIndex, toIndex);
		sort1(a, fromIndex, toIndex - fromIndex);
	}

	public static void sort(int[] a) {
		sort1(a, 0, a.length);
	}

	public static void sort(int[] a, int fromIndex, int toIndex) {
		rangeCheck(a.length, fromIndex, toIndex);
		sort1(a, fromIndex, toIndex - fromIndex);
	}

	public static void sort(short[] a) {
		sort1(a, 0, a.length);
	}

	public static void sort(short[] a, int fromIndex, int toIndex) {
		rangeCheck(a.length, fromIndex, toIndex);
		sort1(a, fromIndex, toIndex - fromIndex);
	}

	public static void sort(char[] a) {
		sort1(a, 0, a.length);
	}

	public static void sort(char[] a, int fromIndex, int toIndex) {
		rangeCheck(a.length, fromIndex, toIndex);
		sort1(a, fromIndex, toIndex - fromIndex);
	}

	public static void sort(byte[] a) {
		sort1(a, 0, a.length);
	}

	public static void sort(byte[] a, int fromIndex, int toIndex) {
		rangeCheck(a.length, fromIndex, toIndex);
		sort1(a, fromIndex, toIndex - fromIndex);
	}

	public static void sort(double[] a) {
		sort2(a, 0, a.length);
	}

	public static void sort(double[] a, int fromIndex, int toIndex) {
		rangeCheck(a.length, fromIndex, toIndex);
		sort2(a, fromIndex, toIndex);
	}

	public static void sort(float[] a) {
		sort2(a, 0, a.length);
	}

	public static void sort(float[] a, int fromIndex, int toIndex) {
		rangeCheck(a.length, fromIndex, toIndex);
		sort2(a, fromIndex, toIndex);
	}

	private static void sort2(double a[], int fromIndex, int toIndex) {
		final long NEG_ZERO_BITS = Double.doubleToLongBits(-0.0d);

		int numNegZeros = 0;
		int i = fromIndex, n = toIndex;
		while (i < n) {
			if (a[i] != a[i]) {
				swap(a, i, --n);
			} else {
				if (a[i] == 0 && Double.doubleToLongBits(a[i]) == NEG_ZERO_BITS) {
					a[i] = 0.0d;
					numNegZeros++;
				}
				i++;
			}
		}

		sort1(a, fromIndex, n - fromIndex);

		if (numNegZeros != 0) {
			int j = binarySearch0(a, fromIndex, n, 0.0d);
			do {
				j--;
			} while (j >= fromIndex && a[j] == 0.0d);

			for (int k = 0; k < numNegZeros; k++)
				a[++j] = -0.0d;
		}
	}

	private static void sort2(float a[], int fromIndex, int toIndex) {
		final int NEG_ZERO_BITS = Float.floatToIntBits(-0.0f);

		int numNegZeros = 0;
		int i = fromIndex, n = toIndex;
		while (i < n) {
			if (a[i] != a[i]) {
				swap(a, i, --n);
			} else {
				if (a[i] == 0 && Float.floatToIntBits(a[i]) == NEG_ZERO_BITS) {
					a[i] = 0.0f;
					numNegZeros++;
				}
				i++;
			}
		}

		sort1(a, fromIndex, n - fromIndex);

		if (numNegZeros != 0) {
			int j = binarySearch0(a, fromIndex, n, 0.0f);
			do {
				j--;
			} while (j >= fromIndex && a[j] == 0.0f);

			for (int k = 0; k < numNegZeros; k++)
				a[++j] = -0.0f;
		}
	}

	private static void sort1(long x[], int off, int len) {

		if (len < 7) {
			for (int i = off; i < len + off; i++)
				for (int j = i; j > off && x[j - 1] > x[j]; j--)
					swap(x, j, j - 1);
			return;
		}

		int m = off + (len >> 1);
		if (len > 7) {
			int l = off;
			int n = off + len - 1;
			if (len > 40) {
				int s = len / 8;
				l = med3(x, l, l + s, l + 2 * s);
				m = med3(x, m - s, m, m + s);
				n = med3(x, n - 2 * s, n - s, n);
			}
			m = med3(x, l, m, n);
		}
		long v = x[m];

		int a = off, b = a, c = off + len - 1, d = c;
		while (true) {
			while (b <= c && x[b] <= v) {
				if (x[b] == v)
					swap(x, a++, b);
				b++;
			}
			while (c >= b && x[c] >= v) {
				if (x[c] == v)
					swap(x, c, d--);
				c--;
			}
			if (b > c)
				break;
			swap(x, b++, c--);
		}

		int s, n = off + len;
		s = Math.min(a - off, b - a);
		vecswap(x, off, b - s, s);
		s = Math.min(d - c, n - d - 1);
		vecswap(x, b, n - s, s);

		if ((s = b - a) > 1)
			sort1(x, off, s);
		if ((s = d - c) > 1)
			sort1(x, n - s, s);
	}

	private static void swap(long x[], int a, int b) {
		long t = x[a];
		x[a] = x[b];
		x[b] = t;
	}

	private static void vecswap(long x[], int a, int b, int n) {
		for (int i = 0; i < n; i++, a++, b++)
			swap(x, a, b);
	}

	private static int med3(long x[], int a, int b, int c) {
		return (x[a] < x[b] ? (x[b] < x[c] ? b : x[a] < x[c] ? c : a) : (x[b] > x[c] ? b : x[a] > x[c] ? c : a));
	}

	private static void sort1(int x[], int off, int len) {

		if (len < 7) {
			for (int i = off; i < len + off; i++)
				for (int j = i; j > off && x[j - 1] > x[j]; j--)
					swap(x, j, j - 1);
			return;
		}

		int m = off + (len >> 1);
		if (len > 7) {
			int l = off;
			int n = off + len - 1;
			if (len > 40) {
				int s = len / 8;
				l = med3(x, l, l + s, l + 2 * s);
				m = med3(x, m - s, m, m + s);
				n = med3(x, n - 2 * s, n - s, n);
			}
			m = med3(x, l, m, n);
		}
		int v = x[m];

		int a = off, b = a, c = off + len - 1, d = c;
		while (true) {
			while (b <= c && x[b] <= v) {
				if (x[b] == v)
					swap(x, a++, b);
				b++;
			}
			while (c >= b && x[c] >= v) {
				if (x[c] == v)
					swap(x, c, d--);
				c--;
			}
			if (b > c)
				break;
			swap(x, b++, c--);
		}

		int s, n = off + len;
		s = Math.min(a - off, b - a);
		vecswap(x, off, b - s, s);
		s = Math.min(d - c, n - d - 1);
		vecswap(x, b, n - s, s);

		if ((s = b - a) > 1)
			sort1(x, off, s);
		if ((s = d - c) > 1)
			sort1(x, n - s, s);
	}

	private static void swap(int x[], int a, int b) {
		int t = x[a];
		x[a] = x[b];
		x[b] = t;
	}

	private static void vecswap(int x[], int a, int b, int n) {
		for (int i = 0; i < n; i++, a++, b++)
			swap(x, a, b);
	}

	private static int med3(int x[], int a, int b, int c) {
		return (x[a] < x[b] ? (x[b] < x[c] ? b : x[a] < x[c] ? c : a) : (x[b] > x[c] ? b : x[a] > x[c] ? c : a));
	}

	private static void sort1(short x[], int off, int len) {

		if (len < 7) {
			for (int i = off; i < len + off; i++)
				for (int j = i; j > off && x[j - 1] > x[j]; j--)
					swap(x, j, j - 1);
			return;
		}

		int m = off + (len >> 1);
		if (len > 7) {
			int l = off;
			int n = off + len - 1;
			if (len > 40) {
				int s = len / 8;
				l = med3(x, l, l + s, l + 2 * s);
				m = med3(x, m - s, m, m + s);
				n = med3(x, n - 2 * s, n - s, n);
			}
			m = med3(x, l, m, n);
		}
		short v = x[m];

		int a = off, b = a, c = off + len - 1, d = c;
		while (true) {
			while (b <= c && x[b] <= v) {
				if (x[b] == v)
					swap(x, a++, b);
				b++;
			}
			while (c >= b && x[c] >= v) {
				if (x[c] == v)
					swap(x, c, d--);
				c--;
			}
			if (b > c)
				break;
			swap(x, b++, c--);
		}

		int s, n = off + len;
		s = Math.min(a - off, b - a);
		vecswap(x, off, b - s, s);
		s = Math.min(d - c, n - d - 1);
		vecswap(x, b, n - s, s);

		if ((s = b - a) > 1)
			sort1(x, off, s);
		if ((s = d - c) > 1)
			sort1(x, n - s, s);
	}

	private static void swap(short x[], int a, int b) {
		short t = x[a];
		x[a] = x[b];
		x[b] = t;
	}

	private static void vecswap(short x[], int a, int b, int n) {
		for (int i = 0; i < n; i++, a++, b++)
			swap(x, a, b);
	}

	private static int med3(short x[], int a, int b, int c) {
		return (x[a] < x[b] ? (x[b] < x[c] ? b : x[a] < x[c] ? c : a) : (x[b] > x[c] ? b : x[a] > x[c] ? c : a));
	}

	private static void sort1(char x[], int off, int len) {

		if (len < 7) {
			for (int i = off; i < len + off; i++)
				for (int j = i; j > off && x[j - 1] > x[j]; j--)
					swap(x, j, j - 1);
			return;
		}

		int m = off + (len >> 1);
		if (len > 7) {
			int l = off;
			int n = off + len - 1;
			if (len > 40) {
				int s = len / 8;
				l = med3(x, l, l + s, l + 2 * s);
				m = med3(x, m - s, m, m + s);
				n = med3(x, n - 2 * s, n - s, n);
			}
			m = med3(x, l, m, n);
		}
		char v = x[m];

		int a = off, b = a, c = off + len - 1, d = c;
		while (true) {
			while (b <= c && x[b] <= v) {
				if (x[b] == v)
					swap(x, a++, b);
				b++;
			}
			while (c >= b && x[c] >= v) {
				if (x[c] == v)
					swap(x, c, d--);
				c--;
			}
			if (b > c)
				break;
			swap(x, b++, c--);
		}

		int s, n = off + len;
		s = Math.min(a - off, b - a);
		vecswap(x, off, b - s, s);
		s = Math.min(d - c, n - d - 1);
		vecswap(x, b, n - s, s);

		if ((s = b - a) > 1)
			sort1(x, off, s);
		if ((s = d - c) > 1)
			sort1(x, n - s, s);
	}

	private static void swap(char x[], int a, int b) {
		char t = x[a];
		x[a] = x[b];
		x[b] = t;
	}

	private static void vecswap(char x[], int a, int b, int n) {
		for (int i = 0; i < n; i++, a++, b++)
			swap(x, a, b);
	}

	private static int med3(char x[], int a, int b, int c) {
		return (x[a] < x[b] ? (x[b] < x[c] ? b : x[a] < x[c] ? c : a) : (x[b] > x[c] ? b : x[a] > x[c] ? c : a));
	}

	private static void sort1(byte x[], int off, int len) {

		if (len < 7) {
			for (int i = off; i < len + off; i++)
				for (int j = i; j > off && x[j - 1] > x[j]; j--)
					swap(x, j, j - 1);
			return;
		}

		int m = off + (len >> 1);
		if (len > 7) {
			int l = off;
			int n = off + len - 1;
			if (len > 40) {
				int s = len / 8;
				l = med3(x, l, l + s, l + 2 * s);
				m = med3(x, m - s, m, m + s);
				n = med3(x, n - 2 * s, n - s, n);
			}
			m = med3(x, l, m, n);
		}
		byte v = x[m];

		int a = off, b = a, c = off + len - 1, d = c;
		while (true) {
			while (b <= c && x[b] <= v) {
				if (x[b] == v)
					swap(x, a++, b);
				b++;
			}
			while (c >= b && x[c] >= v) {
				if (x[c] == v)
					swap(x, c, d--);
				c--;
			}
			if (b > c)
				break;
			swap(x, b++, c--);
		}

		int s, n = off + len;
		s = Math.min(a - off, b - a);
		vecswap(x, off, b - s, s);
		s = Math.min(d - c, n - d - 1);
		vecswap(x, b, n - s, s);

		if ((s = b - a) > 1)
			sort1(x, off, s);
		if ((s = d - c) > 1)
			sort1(x, n - s, s);
	}

	private static void swap(byte x[], int a, int b) {
		byte t = x[a];
		x[a] = x[b];
		x[b] = t;
	}

	private static void vecswap(byte x[], int a, int b, int n) {
		for (int i = 0; i < n; i++, a++, b++)
			swap(x, a, b);
	}

	private static int med3(byte x[], int a, int b, int c) {
		return (x[a] < x[b] ? (x[b] < x[c] ? b : x[a] < x[c] ? c : a) : (x[b] > x[c] ? b : x[a] > x[c] ? c : a));
	}

	private static void sort1(double x[], int off, int len) {

		if (len < 7) {
			for (int i = off; i < len + off; i++)
				for (int j = i; j > off && x[j - 1] > x[j]; j--)
					swap(x, j, j - 1);
			return;
		}

		int m = off + (len >> 1);
		if (len > 7) {
			int l = off;
			int n = off + len - 1;
			if (len > 40) {
				int s = len / 8;
				l = med3(x, l, l + s, l + 2 * s);
				m = med3(x, m - s, m, m + s);
				n = med3(x, n - 2 * s, n - s, n);
			}
			m = med3(x, l, m, n);
		}
		double v = x[m];

		int a = off, b = a, c = off + len - 1, d = c;
		while (true) {
			while (b <= c && x[b] <= v) {
				if (x[b] == v)
					swap(x, a++, b);
				b++;
			}
			while (c >= b && x[c] >= v) {
				if (x[c] == v)
					swap(x, c, d--);
				c--;
			}
			if (b > c)
				break;
			swap(x, b++, c--);
		}

		int s, n = off + len;
		s = Math.min(a - off, b - a);
		vecswap(x, off, b - s, s);
		s = Math.min(d - c, n - d - 1);
		vecswap(x, b, n - s, s);

		if ((s = b - a) > 1)
			sort1(x, off, s);
		if ((s = d - c) > 1)
			sort1(x, n - s, s);
	}

	private static void swap(double x[], int a, int b) {
		double t = x[a];
		x[a] = x[b];
		x[b] = t;
	}

	private static void vecswap(double x[], int a, int b, int n) {
		for (int i = 0; i < n; i++, a++, b++)
			swap(x, a, b);
	}

	private static int med3(double x[], int a, int b, int c) {
		return (x[a] < x[b] ? (x[b] < x[c] ? b : x[a] < x[c] ? c : a) : (x[b] > x[c] ? b : x[a] > x[c] ? c : a));
	}

	private static void sort1(float x[], int off, int len) {

		if (len < 7) {
			for (int i = off; i < len + off; i++)
				for (int j = i; j > off && x[j - 1] > x[j]; j--)
					swap(x, j, j - 1);
			return;
		}

		int m = off + (len >> 1);
		if (len > 7) {
			int l = off;
			int n = off + len - 1;
			if (len > 40) {
				int s = len / 8;
				l = med3(x, l, l + s, l + 2 * s);
				m = med3(x, m - s, m, m + s);
				n = med3(x, n - 2 * s, n - s, n);
			}
			m = med3(x, l, m, n);
		}
		float v = x[m];

		int a = off, b = a, c = off + len - 1, d = c;
		while (true) {
			while (b <= c && x[b] <= v) {
				if (x[b] == v)
					swap(x, a++, b);
				b++;
			}
			while (c >= b && x[c] >= v) {
				if (x[c] == v)
					swap(x, c, d--);
				c--;
			}
			if (b > c)
				break;
			swap(x, b++, c--);
		}

		int s, n = off + len;
		s = Math.min(a - off, b - a);
		vecswap(x, off, b - s, s);
		s = Math.min(d - c, n - d - 1);
		vecswap(x, b, n - s, s);

		if ((s = b - a) > 1)
			sort1(x, off, s);
		if ((s = d - c) > 1)
			sort1(x, n - s, s);
	}

	private static void swap(float x[], int a, int b) {
		float t = x[a];
		x[a] = x[b];
		x[b] = t;
	}

	private static void vecswap(float x[], int a, int b, int n) {
		for (int i = 0; i < n; i++, a++, b++)
			swap(x, a, b);
	}

	private static int med3(float x[], int a, int b, int c) {
		return (x[a] < x[b] ? (x[b] < x[c] ? b : x[a] < x[c] ? c : a) : (x[b] > x[c] ? b : x[a] > x[c] ? c : a));
	}

	public static void sort(Object[] a) {
		Object[] aux = a.clone();
		mergeSort(aux, a, 0, a.length, 0);
	}

	public static void sort(Object[] a, int fromIndex, int toIndex) {
		rangeCheck(a.length, fromIndex, toIndex);
		Object[] aux = copyOfRange(a, fromIndex, toIndex);
		mergeSort(aux, a, fromIndex, toIndex, -fromIndex);
	}

	private static final int INSERTIONSORT_THRESHOLD = 7;

	@SuppressWarnings("unchecked")
	private static void mergeSort(Object[] src, Object[] dest, int low, int high, int off) {
		int length = high - low;

		if (length < INSERTIONSORT_THRESHOLD) {
			for (int i = low; i < high; i++)
				for (int j = i; j > low && ((Comparable<Object>) dest[j - 1]).compareTo(dest[j]) > 0; j--)
					swap(dest, j, j - 1);
			return;
		}

		int destLow = low;
		int destHigh = high;
		low += off;
		high += off;
		int mid = (low + high) >>> 1;
		mergeSort(dest, src, low, mid, -off);
		mergeSort(dest, src, mid, high, -off);

		if (((Comparable<Object>) src[mid - 1]).compareTo(src[mid]) <= 0) {
			System.arraycopy(src, low, dest, destLow, length);
			return;
		}

		for (int i = destLow, p = low, q = mid; i < destHigh; i++) {
			if (q >= high || p < mid && ((Comparable<Object>) src[p]).compareTo(src[q]) <= 0)
				dest[i] = src[p++];
			else
				dest[i] = src[q++];
		}
	}

	private static void swap(Object[] x, int a, int b) {
		Object t = x[a];
		x[a] = x[b];
		x[b] = t;
	}

	public static <T> void sort(T[] a, Comparator<? super T> c) {
		T[] aux = a.clone();
		if (c == null)
			mergeSort(aux, a, 0, a.length, 0);
		else
			mergeSort(aux, a, 0, a.length, 0, c);
	}

	public static <T> void sort(T[] a, int fromIndex, int toIndex, Comparator<? super T> c) {
		rangeCheck(a.length, fromIndex, toIndex);
		T[] aux = copyOfRange(a, fromIndex, toIndex);
		if (c == null)
			mergeSort(aux, a, fromIndex, toIndex, -fromIndex);
		else
			mergeSort(aux, a, fromIndex, toIndex, -fromIndex, c);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static void mergeSort(Object[] src, Object[] dest, int low, int high, int off, Comparator c) {
		int length = high - low;

		if (length < INSERTIONSORT_THRESHOLD) {
			for (int i = low; i < high; i++)
				for (int j = i; j > low && c.compare(dest[j - 1], dest[j]) > 0; j--)
					swap(dest, j, j - 1);
			return;
		}

		int destLow = low;
		int destHigh = high;
		low += off;
		high += off;
		int mid = (low + high) >>> 1;
		mergeSort(dest, src, low, mid, -off, c);
		mergeSort(dest, src, mid, high, -off, c);

		if (c.compare(src[mid - 1], src[mid]) <= 0) {
			System.arraycopy(src, low, dest, destLow, length);
			return;
		}

		for (int i = destLow, p = low, q = mid; i < destHigh; i++) {
			if (q >= high || p < mid && c.compare(src[p], src[q]) <= 0)
				dest[i] = src[p++];
			else
				dest[i] = src[q++];
		}
	}

	private static void rangeCheck(int arrayLen, int fromIndex, int toIndex) {
		if (fromIndex > toIndex)
			throw new IllegalArgumentException("fromIndex(" + fromIndex + ") > toIndex(" + toIndex + ")");
		if (fromIndex < 0)
			throw new ArrayIndexOutOfBoundsException(fromIndex);
		if (toIndex > arrayLen)
			throw new ArrayIndexOutOfBoundsException(toIndex);
	}

	public static int binarySearch(long[] a, long key) {
		return binarySearch0(a, 0, a.length, key);
	}

	public static int binarySearch(long[] a, int fromIndex, int toIndex, long key) {
		rangeCheck(a.length, fromIndex, toIndex);
		return binarySearch0(a, fromIndex, toIndex, key);
	}

	private static int binarySearch0(long[] a, int fromIndex, int toIndex, long key) {
		int low = fromIndex;
		int high = toIndex - 1;

		while (low <= high) {
			int mid = (low + high) >>> 1;
			long midVal = a[mid];

			if (midVal < key)
				low = mid + 1;
			else if (midVal > key)
				high = mid - 1;
			else
				return mid;
		}
		return -(low + 1);
	}

	public static int binarySearch(int[] a, int key) {
		return binarySearch0(a, 0, a.length, key);
	}

	public static int binarySearch(int[] a, int fromIndex, int toIndex, int key) {
		rangeCheck(a.length, fromIndex, toIndex);
		return binarySearch0(a, fromIndex, toIndex, key);
	}

	private static int binarySearch0(int[] a, int fromIndex, int toIndex, int key) {
		int low = fromIndex;
		int high = toIndex - 1;

		while (low <= high) {
			int mid = (low + high) >>> 1;
			int midVal = a[mid];

			if (midVal < key)
				low = mid + 1;
			else if (midVal > key)
				high = mid - 1;
			else
				return mid;
		}
		return -(low + 1);
	}

	public static int binarySearch(short[] a, short key) {
		return binarySearch0(a, 0, a.length, key);
	}

	public static int binarySearch(short[] a, int fromIndex, int toIndex, short key) {
		rangeCheck(a.length, fromIndex, toIndex);
		return binarySearch0(a, fromIndex, toIndex, key);
	}

	private static int binarySearch0(short[] a, int fromIndex, int toIndex, short key) {
		int low = fromIndex;
		int high = toIndex - 1;

		while (low <= high) {
			int mid = (low + high) >>> 1;
			short midVal = a[mid];

			if (midVal < key)
				low = mid + 1;
			else if (midVal > key)
				high = mid - 1;
			else
				return mid;
		}
		return -(low + 1);
	}

	public static int binarySearch(char[] a, char key) {
		return binarySearch0(a, 0, a.length, key);
	}

	public static int binarySearch(char[] a, int fromIndex, int toIndex, char key) {
		rangeCheck(a.length, fromIndex, toIndex);
		return binarySearch0(a, fromIndex, toIndex, key);
	}

	private static int binarySearch0(char[] a, int fromIndex, int toIndex, char key) {
		int low = fromIndex;
		int high = toIndex - 1;

		while (low <= high) {
			int mid = (low + high) >>> 1;
			char midVal = a[mid];

			if (midVal < key)
				low = mid + 1;
			else if (midVal > key)
				high = mid - 1;
			else
				return mid;
		}
		return -(low + 1);
	}

	public static int binarySearch(byte[] a, byte key) {
		return binarySearch0(a, 0, a.length, key);
	}

	public static int binarySearch(byte[] a, int fromIndex, int toIndex, byte key) {
		rangeCheck(a.length, fromIndex, toIndex);
		return binarySearch0(a, fromIndex, toIndex, key);
	}

	private static int binarySearch0(byte[] a, int fromIndex, int toIndex, byte key) {
		int low = fromIndex;
		int high = toIndex - 1;

		while (low <= high) {
			int mid = (low + high) >>> 1;
			byte midVal = a[mid];

			if (midVal < key)
				low = mid + 1;
			else if (midVal > key)
				high = mid - 1;
			else
				return mid;
		}
		return -(low + 1);
	}

	public static int binarySearch(double[] a, double key) {
		return binarySearch0(a, 0, a.length, key);
	}

	public static int binarySearch(double[] a, int fromIndex, int toIndex, double key) {
		rangeCheck(a.length, fromIndex, toIndex);
		return binarySearch0(a, fromIndex, toIndex, key);
	}

	private static int binarySearch0(double[] a, int fromIndex, int toIndex, double key) {
		int low = fromIndex;
		int high = toIndex - 1;

		while (low <= high) {
			int mid = (low + high) >>> 1;
			double midVal = a[mid];

			if (midVal < key)
				low = mid + 1;
			else if (midVal > key)
				high = mid - 1;
			else {
				long midBits = Double.doubleToLongBits(midVal);
				long keyBits = Double.doubleToLongBits(key);
				if (midBits == keyBits)
					return mid;
				else if (midBits < keyBits)
					low = mid + 1;
				else
					high = mid - 1;
			}
		}
		return -(low + 1);
	}

	public static int binarySearch(float[] a, float key) {
		return binarySearch0(a, 0, a.length, key);
	}

	public static int binarySearch(float[] a, int fromIndex, int toIndex, float key) {
		rangeCheck(a.length, fromIndex, toIndex);
		return binarySearch0(a, fromIndex, toIndex, key);
	}

	private static int binarySearch0(float[] a, int fromIndex, int toIndex, float key) {
		int low = fromIndex;
		int high = toIndex - 1;

		while (low <= high) {
			int mid = (low + high) >>> 1;
			float midVal = a[mid];

			if (midVal < key)
				low = mid + 1;
			else if (midVal > key)
				high = mid - 1;
			else {
				int midBits = Float.floatToIntBits(midVal);
				int keyBits = Float.floatToIntBits(key);
				if (midBits == keyBits)
					return mid;
				else if (midBits < keyBits)
					low = mid + 1;
				else
					high = mid - 1;
			}
		}
		return -(low + 1);
	}

	public static int binarySearch(Object[] a, Object key) {
		return binarySearch0(a, 0, a.length, key);
	}

	public static int binarySearch(Object[] a, int fromIndex, int toIndex, Object key) {
		rangeCheck(a.length, fromIndex, toIndex);
		return binarySearch0(a, fromIndex, toIndex, key);
	}

	@SuppressWarnings("unchecked")
	private static int binarySearch0(Object[] a, int fromIndex, int toIndex, Object key) {
		int low = fromIndex;
		int high = toIndex - 1;

		while (low <= high) {
			int mid = (low + high) >>> 1;
			Comparable<Object> midVal = (Comparable<Object>) a[mid];
			int cmp = midVal.compareTo(key);

			if (cmp < 0)
				low = mid + 1;
			else if (cmp > 0)
				high = mid - 1;
			else
				return mid;
		}
		return -(low + 1);
	}

	public static <T> int binarySearch(T[] a, T key, Comparator<? super T> c) {
		return binarySearch0(a, 0, a.length, key, c);
	}

	public static <T> int binarySearch(T[] a, int fromIndex, int toIndex, T key, Comparator<? super T> c) {
		rangeCheck(a.length, fromIndex, toIndex);
		return binarySearch0(a, fromIndex, toIndex, key, c);
	}

	private static <T> int binarySearch0(T[] a, int fromIndex, int toIndex, T key, Comparator<? super T> c) {
		if (c == null) {
			return binarySearch0(a, fromIndex, toIndex, key);
		}
		int low = fromIndex;
		int high = toIndex - 1;

		while (low <= high) {
			int mid = (low + high) >>> 1;
			T midVal = a[mid];
			int cmp = c.compare(midVal, key);

			if (cmp < 0)
				low = mid + 1;
			else if (cmp > 0)
				high = mid - 1;
			else
				return mid;
		}
		return -(low + 1);
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] copyOfRange(T[] original, int from, int to) {
		return copyOfRange(original, from, to, (Class<T[]>) original.getClass());
	}

	@SuppressWarnings("unchecked")
	public static <T, U> T[] copyOfRange(U[] original, int from, int to, Class<? extends T[]> newType) {
		int newLength = to - from;
		if (newLength < 0)
			throw new IllegalArgumentException(from + " > " + to);
		T[] copy = ((Object) newType == (Object) Object[].class) ? (T[]) new Object[newLength]
				: (T[]) Array.newInstance(newType.getComponentType(), newLength);
		System.arraycopy(original, from, copy, 0, Math.min(original.length - from, newLength));
		return copy;
	}
}