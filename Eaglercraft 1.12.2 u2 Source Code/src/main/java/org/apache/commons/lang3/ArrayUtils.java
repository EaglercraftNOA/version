package org.apache.commons.lang3;

import java.lang.reflect.Array;
import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.client.settings.KeyBinding;

public class ArrayUtils {

	/**
	 * The index value when an element is not found in a list or array: {@code -1}.
	 * This value is returned by methods in this class and can also be used in
	 * comparisons with values returned by various method from
	 * {@link java.util.List}.
	 */
	public static final int INDEX_NOT_FOUND = -1;

	/**
	 * Checks if an array of primitive booleans is empty or {@code null}.
	 *
	 * @param array the array to test
	 * @return {@code true} if the array is empty or {@code null}
	 * @since 2.1
	 */
	public static boolean isEmpty(final boolean[] array) {
		return isArrayEmpty(array);
	}

	/**
	 * Checks if an array of primitive bytes is empty or {@code null}.
	 *
	 * @param array the array to test
	 * @return {@code true} if the array is empty or {@code null}
	 * @since 2.1
	 */
	public static boolean isEmpty(final byte[] array) {
		return isArrayEmpty(array);
	}

	/**
	 * Checks if an array of primitive chars is empty or {@code null}.
	 *
	 * @param array the array to test
	 * @return {@code true} if the array is empty or {@code null}
	 * @since 2.1
	 */
	public static boolean isEmpty(final char[] array) {
		return isArrayEmpty(array);
	}

	/**
	 * Checks if an array of primitive doubles is empty or {@code null}.
	 *
	 * @param array the array to test
	 * @return {@code true} if the array is empty or {@code null}
	 * @since 2.1
	 */
	public static boolean isEmpty(final double[] array) {
		return isArrayEmpty(array);
	}

	/**
	 * Checks if an array of primitive floats is empty or {@code null}.
	 *
	 * @param array the array to test
	 * @return {@code true} if the array is empty or {@code null}
	 * @since 2.1
	 */
	public static boolean isEmpty(final float[] array) {
		return isArrayEmpty(array);
	}

	/**
	 * Checks if an array of primitive ints is empty or {@code null}.
	 *
	 * @param array the array to test
	 * @return {@code true} if the array is empty or {@code null}
	 * @since 2.1
	 */
	public static boolean isEmpty(final int[] array) {
		return isArrayEmpty(array);
	}

	/**
	 * Checks if an array of primitive longs is empty or {@code null}.
	 *
	 * @param array the array to test
	 * @return {@code true} if the array is empty or {@code null}
	 * @since 2.1
	 */
	public static boolean isEmpty(final long[] array) {
		return isArrayEmpty(array);
	}

	/**
	 * Checks if an array of Objects is empty or {@code null}.
	 *
	 * @param array the array to test
	 * @return {@code true} if the array is empty or {@code null}
	 * @since 2.1
	 */
	public static boolean isEmpty(final Object[] array) {
		return isArrayEmpty(array);
	}

	/**
	 * Checks if an array of primitive shorts is empty or {@code null}.
	 *
	 * @param array the array to test
	 * @return {@code true} if the array is empty or {@code null}
	 * @since 2.1
	 */
	public static boolean isEmpty(final short[] array) {
		return isArrayEmpty(array);
	}

	/**
	 * Checks if an array is empty or {@code null}.
	 *
	 * @param array the array to test
	 * @return {@code true} if the array is empty or {@code null}
	 */
	private static boolean isArrayEmpty(final Object array) {
		return getLength(array) == 0;
	}

	/**
	 * Returns the length of the specified array. This method can deal with
	 * {@link Object} arrays and with primitive arrays.
	 * <p>
	 * If the input array is {@code null}, {@code 0} is returned.
	 * </p>
	 * 
	 * <pre>
	 * ArrayUtils.getLength(null)            = 0
	 * ArrayUtils.getLength([])              = 0
	 * ArrayUtils.getLength([null])          = 1
	 * ArrayUtils.getLength([true, false])   = 2
	 * ArrayUtils.getLength([1, 2, 3])       = 3
	 * ArrayUtils.getLength(["a", "b", "c"]) = 3
	 * </pre>
	 *
	 * @param array the array to retrieve the length from, may be null
	 * @return The length of the array, or {@code 0} if the array is {@code null}
	 * @throws IllegalArgumentException if the object argument is not an array.
	 * @since 2.1
	 */
	public static int getLength(final Object array) {
		return array != null ? Array.getLength(array) : 0;
	}

	/**
	 * Clones an array or returns {@code null}.
	 * <p>
	 * This method returns {@code null} for a {@code null} input array.
	 * </p>
	 *
	 * @param array the array to clone, may be {@code null}
	 * @return the cloned array, {@code null} if {@code null} input
	 */
	public static boolean[] clone(final boolean[] array) {
		return array != null ? array.clone() : null;
	}

	/**
	 * Clones an array or returns {@code null}.
	 * <p>
	 * This method returns {@code null} for a {@code null} input array.
	 * </p>
	 *
	 * @param array the array to clone, may be {@code null}
	 * @return the cloned array, {@code null} if {@code null} input
	 */
	public static byte[] clone(final byte[] array) {
		return array != null ? array.clone() : null;
	}

	/**
	 * Clones an array or returns {@code null}.
	 * <p>
	 * This method returns {@code null} for a {@code null} input array.
	 * </p>
	 *
	 * @param array the array to clone, may be {@code null}
	 * @return the cloned array, {@code null} if {@code null} input
	 */
	public static char[] clone(final char[] array) {
		return array != null ? array.clone() : null;
	}

	/**
	 * Clones an array or returns {@code null}.
	 * <p>
	 * This method returns {@code null} for a {@code null} input array.
	 * </p>
	 *
	 * @param array the array to clone, may be {@code null}
	 * @return the cloned array, {@code null} if {@code null} input
	 */
	public static double[] clone(final double[] array) {
		return array != null ? array.clone() : null;
	}

	/**
	 * Clones an array or returns {@code null}.
	 * <p>
	 * This method returns {@code null} for a {@code null} input array.
	 * </p>
	 *
	 * @param array the array to clone, may be {@code null}
	 * @return the cloned array, {@code null} if {@code null} input
	 */
	public static float[] clone(final float[] array) {
		return array != null ? array.clone() : null;
	}

	/**
	 * Clones an array or returns {@code null}.
	 * <p>
	 * This method returns {@code null} for a {@code null} input array.
	 * </p>
	 *
	 * @param array the array to clone, may be {@code null}
	 * @return the cloned array, {@code null} if {@code null} input
	 */
	public static int[] clone(final int[] array) {
		return array != null ? array.clone() : null;
	}

	/**
	 * Clones an array or returns {@code null}.
	 * <p>
	 * This method returns {@code null} for a {@code null} input array.
	 * </p>
	 *
	 * @param array the array to clone, may be {@code null}
	 * @return the cloned array, {@code null} if {@code null} input
	 */
	public static long[] clone(final long[] array) {
		return array != null ? array.clone() : null;
	}

	/**
	 * Clones an array or returns {@code null}.
	 * <p>
	 * This method returns {@code null} for a {@code null} input array.
	 * </p>
	 *
	 * @param array the array to clone, may be {@code null}
	 * @return the cloned array, {@code null} if {@code null} input
	 */
	public static short[] clone(final short[] array) {
		return array != null ? array.clone() : null;
	}

	/**
	 * Shallow clones an array or returns {@code null}.
	 * <p>
	 * The objects in the array are not cloned, thus there is no special handling
	 * for multi-dimensional arrays.
	 * </p>
	 * <p>
	 * This method returns {@code null} for a {@code null} input array.
	 * </p>
	 *
	 * @param <T>   the component type of the array
	 * @param array the array to shallow clone, may be {@code null}
	 * @return the cloned array, {@code null} if {@code null} input
	 */
	public static <T> T[] clone(final T[] array) {
		return array != null ? array.clone() : null;
	}

	/**
	 * Checks if the value is in the given array.
	 * <p>
	 * The method returns {@code false} if a {@code null} array is passed in.
	 * </p>
	 *
	 * @param array       the array to search through
	 * @param valueToFind the value to find
	 * @return {@code true} if the array contains the object
	 */
	public static boolean contains(final boolean[] array, final boolean valueToFind) {
		return indexOf(array, valueToFind) != INDEX_NOT_FOUND;
	}

	/**
	 * Checks if the value is in the given array.
	 * <p>
	 * The method returns {@code false} if a {@code null} array is passed in.
	 * </p>
	 *
	 * @param array       the array to search through
	 * @param valueToFind the value to find
	 * @return {@code true} if the array contains the object
	 */
	public static boolean contains(final byte[] array, final byte valueToFind) {
		return indexOf(array, valueToFind) != INDEX_NOT_FOUND;
	}

	/**
	 * Checks if the value is in the given array.
	 * <p>
	 * The method returns {@code false} if a {@code null} array is passed in.
	 * </p>
	 *
	 * @param array       the array to search through
	 * @param valueToFind the value to find
	 * @return {@code true} if the array contains the object
	 * @since 2.1
	 */
	public static boolean contains(final char[] array, final char valueToFind) {
		return indexOf(array, valueToFind) != INDEX_NOT_FOUND;
	}

	/**
	 * Checks if the value is in the given array.
	 * <p>
	 * The method returns {@code false} if a {@code null} array is passed in.
	 * </p>
	 *
	 * @param array       the array to search through
	 * @param valueToFind the value to find
	 * @return {@code true} if the array contains the object
	 */
	public static boolean contains(final double[] array, final double valueToFind) {
		return indexOf(array, valueToFind) != INDEX_NOT_FOUND;
	}

	/**
	 * Checks if a value falling within the given tolerance is in the given array.
	 * If the array contains a value within the inclusive range defined by (value -
	 * tolerance) to (value + tolerance).
	 * <p>
	 * The method returns {@code false} if a {@code null} array is passed in.
	 * </p>
	 *
	 * @param array       the array to search
	 * @param valueToFind the value to find
	 * @param tolerance   the array contains the tolerance of the search
	 * @return true if value falling within tolerance is in array
	 */
	public static boolean contains(final double[] array, final double valueToFind, final double tolerance) {
		return indexOf(array, valueToFind, 0, tolerance) != INDEX_NOT_FOUND;
	}

	/**
	 * Checks if the value is in the given array.
	 * <p>
	 * The method returns {@code false} if a {@code null} array is passed in.
	 * </p>
	 *
	 * @param array       the array to search through
	 * @param valueToFind the value to find
	 * @return {@code true} if the array contains the object
	 */
	public static boolean contains(final float[] array, final float valueToFind) {
		return indexOf(array, valueToFind) != INDEX_NOT_FOUND;
	}

	/**
	 * Checks if the value is in the given array.
	 * <p>
	 * The method returns {@code false} if a {@code null} array is passed in.
	 * </p>
	 *
	 * @param array       the array to search through
	 * @param valueToFind the value to find
	 * @return {@code true} if the array contains the object
	 */
	public static boolean contains(final int[] array, final int valueToFind) {
		return indexOf(array, valueToFind) != INDEX_NOT_FOUND;
	}

	/**
	 * Checks if the value is in the given array.
	 * <p>
	 * The method returns {@code false} if a {@code null} array is passed in.
	 * </p>
	 *
	 * @param array       the array to search through
	 * @param valueToFind the value to find
	 * @return {@code true} if the array contains the object
	 */
	public static boolean contains(final long[] array, final long valueToFind) {
		return indexOf(array, valueToFind) != INDEX_NOT_FOUND;
	}

	/**
	 * Checks if the object is in the given array.
	 * <p>
	 * The method returns {@code false} if a {@code null} array is passed in.
	 * </p>
	 *
	 * @param array        the array to search through
	 * @param objectToFind the object to find
	 * @return {@code true} if the array contains the object
	 */
	public static boolean contains(final Object[] array, final Object objectToFind) {
		return indexOf(array, objectToFind) != INDEX_NOT_FOUND;
	}

	/**
	 * Checks if the value is in the given array.
	 * <p>
	 * The method returns {@code false} if a {@code null} array is passed in.
	 * </p>
	 *
	 * @param array       the array to search through
	 * @param valueToFind the value to find
	 * @return {@code true} if the array contains the object
	 */
	public static boolean contains(final short[] array, final short valueToFind) {
		return indexOf(array, valueToFind) != INDEX_NOT_FOUND;
	}

	/**
	 * Finds the index of the given value in the array.
	 * <p>
	 * This method returns {@link #INDEX_NOT_FOUND} ({@code -1}) for a {@code null}
	 * input array.
	 * </p>
	 *
	 * @param array       the array to search through for the object, may be
	 *                    {@code null}
	 * @param valueToFind the value to find
	 * @return the index of the value within the array, {@link #INDEX_NOT_FOUND}
	 *         ({@code -1}) if not found or {@code null} array input
	 */
	public static int indexOf(final boolean[] array, final boolean valueToFind) {
		return indexOf(array, valueToFind, 0);
	}

	/**
	 * Finds the index of the given value in the array starting at the given index.
	 * <p>
	 * This method returns {@link #INDEX_NOT_FOUND} ({@code -1}) for a {@code null}
	 * input array.
	 * </p>
	 * <p>
	 * A negative startIndex is treated as zero. A startIndex larger than the array
	 * length will return {@link #INDEX_NOT_FOUND} ({@code -1}).
	 * </p>
	 *
	 * @param array       the array to search through for the object, may be
	 *                    {@code null}
	 * @param valueToFind the value to find
	 * @param startIndex  the index to start searching at
	 * @return the index of the value within the array, {@link #INDEX_NOT_FOUND}
	 *         ({@code -1}) if not found or {@code null} array input
	 */
	public static int indexOf(final boolean[] array, final boolean valueToFind, final int startIndex) {
		if (isEmpty(array)) {
			return INDEX_NOT_FOUND;
		}
		for (int i = max0(startIndex); i < array.length; i++) {
			if (valueToFind == array[i]) {
				return i;
			}
		}
		return INDEX_NOT_FOUND;
	}

	/**
	 * Finds the index of the given value in the array.
	 * <p>
	 * This method returns {@link #INDEX_NOT_FOUND} ({@code -1}) for a {@code null}
	 * input array.
	 * </p>
	 *
	 * @param array       the array to search through for the object, may be
	 *                    {@code null}
	 * @param valueToFind the value to find
	 * @return the index of the value within the array, {@link #INDEX_NOT_FOUND}
	 *         ({@code -1}) if not found or {@code null} array input
	 */
	public static int indexOf(final byte[] array, final byte valueToFind) {
		return indexOf(array, valueToFind, 0);
	}

	/**
	 * Finds the index of the given value in the array starting at the given index.
	 * <p>
	 * This method returns {@link #INDEX_NOT_FOUND} ({@code -1}) for a {@code null}
	 * input array.
	 * </p>
	 * <p>
	 * A negative startIndex is treated as zero. A startIndex larger than the array
	 * length will return {@link #INDEX_NOT_FOUND} ({@code -1}).
	 * </p>
	 *
	 * @param array       the array to search through for the object, may be
	 *                    {@code null}
	 * @param valueToFind the value to find
	 * @param startIndex  the index to start searching at
	 * @return the index of the value within the array, {@link #INDEX_NOT_FOUND}
	 *         ({@code -1}) if not found or {@code null} array input
	 */
	public static int indexOf(final byte[] array, final byte valueToFind, final int startIndex) {
		if (array == null) {
			return INDEX_NOT_FOUND;
		}
		for (int i = max0(startIndex); i < array.length; i++) {
			if (valueToFind == array[i]) {
				return i;
			}
		}
		return INDEX_NOT_FOUND;
	}

	/**
	 * Finds the index of the given value in the array.
	 * <p>
	 * This method returns {@link #INDEX_NOT_FOUND} ({@code -1}) for a {@code null}
	 * input array.
	 * </p>
	 *
	 * @param array       the array to search through for the object, may be
	 *                    {@code null}
	 * @param valueToFind the value to find
	 * @return the index of the value within the array, {@link #INDEX_NOT_FOUND}
	 *         ({@code -1}) if not found or {@code null} array input
	 * @since 2.1
	 */
	public static int indexOf(final char[] array, final char valueToFind) {
		return indexOf(array, valueToFind, 0);
	}

	/**
	 * Finds the index of the given value in the array starting at the given index.
	 * <p>
	 * This method returns {@link #INDEX_NOT_FOUND} ({@code -1}) for a {@code null}
	 * input array.
	 * </p>
	 * <p>
	 * A negative startIndex is treated as zero. A startIndex larger than the array
	 * length will return {@link #INDEX_NOT_FOUND} ({@code -1}).
	 * </p>
	 *
	 * @param array       the array to search through for the object, may be
	 *                    {@code null}
	 * @param valueToFind the value to find
	 * @param startIndex  the index to start searching at
	 * @return the index of the value within the array, {@link #INDEX_NOT_FOUND}
	 *         ({@code -1}) if not found or {@code null} array input
	 * @since 2.1
	 */
	public static int indexOf(final char[] array, final char valueToFind, final int startIndex) {
		if (array == null) {
			return INDEX_NOT_FOUND;
		}
		for (int i = max0(startIndex); i < array.length; i++) {
			if (valueToFind == array[i]) {
				return i;
			}
		}
		return INDEX_NOT_FOUND;
	}

	/**
	 * Finds the index of the given value in the array.
	 * <p>
	 * This method returns {@link #INDEX_NOT_FOUND} ({@code -1}) for a {@code null}
	 * input array.
	 * </p>
	 *
	 * @param array       the array to search through for the object, may be
	 *                    {@code null}
	 * @param valueToFind the value to find
	 * @return the index of the value within the array, {@link #INDEX_NOT_FOUND}
	 *         ({@code -1}) if not found or {@code null} array input
	 */
	public static int indexOf(final double[] array, final double valueToFind) {
		return indexOf(array, valueToFind, 0);
	}

	/**
	 * Finds the index of the given value within a given tolerance in the array.
	 * This method will return the index of the first value which falls between the
	 * region defined by valueToFind - tolerance and valueToFind + tolerance.
	 * <p>
	 * This method returns {@link #INDEX_NOT_FOUND} ({@code -1}) for a {@code null}
	 * input array.
	 * </p>
	 *
	 * @param array       the array to search through for the object, may be
	 *                    {@code null}
	 * @param valueToFind the value to find
	 * @param tolerance   tolerance of the search
	 * @return the index of the value within the array, {@link #INDEX_NOT_FOUND}
	 *         ({@code -1}) if not found or {@code null} array input
	 */
	public static int indexOf(final double[] array, final double valueToFind, final double tolerance) {
		return indexOf(array, valueToFind, 0, tolerance);
	}

	/**
	 * Finds the index of the given value in the array starting at the given index.
	 * <p>
	 * This method returns {@link #INDEX_NOT_FOUND} ({@code -1}) for a {@code null}
	 * input array.
	 * </p>
	 * <p>
	 * A negative startIndex is treated as zero. A startIndex larger than the array
	 * length will return {@link #INDEX_NOT_FOUND} ({@code -1}).
	 * </p>
	 *
	 * @param array       the array to search through for the object, may be
	 *                    {@code null}
	 * @param valueToFind the value to find
	 * @param startIndex  the index to start searching at
	 * @return the index of the value within the array, {@link #INDEX_NOT_FOUND}
	 *         ({@code -1}) if not found or {@code null} array input
	 */
	public static int indexOf(final double[] array, final double valueToFind, final int startIndex) {
		if (isEmpty(array)) {
			return INDEX_NOT_FOUND;
		}
		final boolean searchNaN = Double.isNaN(valueToFind);
		for (int i = max0(startIndex); i < array.length; i++) {
			final double element = array[i];
			if (valueToFind == element || searchNaN && Double.isNaN(element)) {
				return i;
			}
		}
		return INDEX_NOT_FOUND;
	}

	/**
	 * Finds the index of the given value in the array starting at the given index.
	 * This method will return the index of the first value which falls between the
	 * region defined by valueToFind - tolerance and valueToFind + tolerance.
	 * <p>
	 * This method returns {@link #INDEX_NOT_FOUND} ({@code -1}) for a {@code null}
	 * input array.
	 * </p>
	 * <p>
	 * A negative startIndex is treated as zero. A startIndex larger than the array
	 * length will return {@link #INDEX_NOT_FOUND} ({@code -1}).
	 * </p>
	 *
	 * @param array       the array to search through for the object, may be
	 *                    {@code null}
	 * @param valueToFind the value to find
	 * @param startIndex  the index to start searching at
	 * @param tolerance   tolerance of the search
	 * @return the index of the value within the array, {@link #INDEX_NOT_FOUND}
	 *         ({@code -1}) if not found or {@code null} array input
	 */
	public static int indexOf(final double[] array, final double valueToFind, final int startIndex,
			final double tolerance) {
		if (isEmpty(array)) {
			return INDEX_NOT_FOUND;
		}
		final double min = valueToFind - tolerance;
		final double max = valueToFind + tolerance;
		for (int i = max0(startIndex); i < array.length; i++) {
			if (array[i] >= min && array[i] <= max) {
				return i;
			}
		}
		return INDEX_NOT_FOUND;
	}

	/**
	 * Finds the index of the given value in the array.
	 * <p>
	 * This method returns {@link #INDEX_NOT_FOUND} ({@code -1}) for a {@code null}
	 * input array.
	 * </p>
	 *
	 * @param array       the array to search through for the object, may be
	 *                    {@code null}
	 * @param valueToFind the value to find
	 * @return the index of the value within the array, {@link #INDEX_NOT_FOUND}
	 *         ({@code -1}) if not found or {@code null} array input
	 */
	public static int indexOf(final float[] array, final float valueToFind) {
		return indexOf(array, valueToFind, 0);
	}

	/**
	 * Finds the index of the given value in the array starting at the given index.
	 * <p>
	 * This method returns {@link #INDEX_NOT_FOUND} ({@code -1}) for a {@code null}
	 * input array.
	 * </p>
	 * <p>
	 * A negative startIndex is treated as zero. A startIndex larger than the array
	 * length will return {@link #INDEX_NOT_FOUND} ({@code -1}).
	 * </p>
	 *
	 * @param array       the array to search through for the object, may be
	 *                    {@code null}
	 * @param valueToFind the value to find
	 * @param startIndex  the index to start searching at
	 * @return the index of the value within the array, {@link #INDEX_NOT_FOUND}
	 *         ({@code -1}) if not found or {@code null} array input
	 */
	public static int indexOf(final float[] array, final float valueToFind, final int startIndex) {
		if (isEmpty(array)) {
			return INDEX_NOT_FOUND;
		}
		final boolean searchNaN = Float.isNaN(valueToFind);
		for (int i = max0(startIndex); i < array.length; i++) {
			final float element = array[i];
			if (valueToFind == element || searchNaN && Float.isNaN(element)) {
				return i;
			}
		}
		return INDEX_NOT_FOUND;
	}

	/**
	 * Finds the index of the given value in the array.
	 * <p>
	 * This method returns {@link #INDEX_NOT_FOUND} ({@code -1}) for a {@code null}
	 * input array.
	 * </p>
	 *
	 * @param array       the array to search through for the object, may be
	 *                    {@code null}
	 * @param valueToFind the value to find
	 * @return the index of the value within the array, {@link #INDEX_NOT_FOUND}
	 *         ({@code -1}) if not found or {@code null} array input
	 */
	public static int indexOf(final int[] array, final int valueToFind) {
		return indexOf(array, valueToFind, 0);
	}

	/**
	 * Finds the index of the given value in the array starting at the given index.
	 * <p>
	 * This method returns {@link #INDEX_NOT_FOUND} ({@code -1}) for a {@code null}
	 * input array.
	 * </p>
	 * <p>
	 * A negative startIndex is treated as zero. A startIndex larger than the array
	 * length will return {@link #INDEX_NOT_FOUND} ({@code -1}).
	 * </p>
	 *
	 * @param array       the array to search through for the object, may be
	 *                    {@code null}
	 * @param valueToFind the value to find
	 * @param startIndex  the index to start searching at
	 * @return the index of the value within the array, {@link #INDEX_NOT_FOUND}
	 *         ({@code -1}) if not found or {@code null} array input
	 */
	public static int indexOf(final int[] array, final int valueToFind, final int startIndex) {
		if (array == null) {
			return INDEX_NOT_FOUND;
		}
		for (int i = max0(startIndex); i < array.length; i++) {
			if (valueToFind == array[i]) {
				return i;
			}
		}
		return INDEX_NOT_FOUND;
	}

	/**
	 * Finds the index of the given value in the array.
	 * <p>
	 * This method returns {@link #INDEX_NOT_FOUND} ({@code -1}) for a {@code null}
	 * input array.
	 * </p>
	 *
	 * @param array       the array to search through for the object, may be
	 *                    {@code null}
	 * @param valueToFind the value to find
	 * @return the index of the value within the array, {@link #INDEX_NOT_FOUND}
	 *         ({@code -1}) if not found or {@code null} array input
	 */
	public static int indexOf(final long[] array, final long valueToFind) {
		return indexOf(array, valueToFind, 0);
	}

	/**
	 * Finds the index of the given value in the array starting at the given index.
	 * <p>
	 * This method returns {@link #INDEX_NOT_FOUND} ({@code -1}) for a {@code null}
	 * input array.
	 * </p>
	 * <p>
	 * A negative startIndex is treated as zero. A startIndex larger than the array
	 * length will return {@link #INDEX_NOT_FOUND} ({@code -1}).
	 * </p>
	 *
	 * @param array       the array to search through for the object, may be
	 *                    {@code null}
	 * @param valueToFind the value to find
	 * @param startIndex  the index to start searching at
	 * @return the index of the value within the array, {@link #INDEX_NOT_FOUND}
	 *         ({@code -1}) if not found or {@code null} array input
	 */
	public static int indexOf(final long[] array, final long valueToFind, final int startIndex) {
		if (array == null) {
			return INDEX_NOT_FOUND;
		}
		for (int i = max0(startIndex); i < array.length; i++) {
			if (valueToFind == array[i]) {
				return i;
			}
		}
		return INDEX_NOT_FOUND;
	}

	/**
	 * Finds the index of the given object in the array.
	 * <p>
	 * This method returns {@link #INDEX_NOT_FOUND} ({@code -1}) for a {@code null}
	 * input array.
	 * </p>
	 *
	 * @param array        the array to search through for the object, may be
	 *                     {@code null}
	 * @param objectToFind the object to find, may be {@code null}
	 * @return the index of the object within the array, {@link #INDEX_NOT_FOUND}
	 *         ({@code -1}) if not found or {@code null} array input
	 */
	public static int indexOf(final Object[] array, final Object objectToFind) {
		return indexOf(array, objectToFind, 0);
	}

	/**
	 * Finds the index of the given object in the array starting at the given index.
	 * <p>
	 * This method returns {@link #INDEX_NOT_FOUND} ({@code -1}) for a {@code null}
	 * input array.
	 * </p>
	 * <p>
	 * A negative startIndex is treated as zero. A startIndex larger than the array
	 * length will return {@link #INDEX_NOT_FOUND} ({@code -1}).
	 * </p>
	 *
	 * @param array        the array to search through for the object, may be
	 *                     {@code null}
	 * @param objectToFind the object to find, may be {@code null}
	 * @param startIndex   the index to start searching at
	 * @return the index of the object within the array starting at the index,
	 *         {@link #INDEX_NOT_FOUND} ({@code -1}) if not found or {@code null}
	 *         array input
	 */
	public static int indexOf(final Object[] array, final Object objectToFind, int startIndex) {
		if (array == null) {
			return INDEX_NOT_FOUND;
		}
		startIndex = max0(startIndex);
		if (objectToFind == null) {
			for (int i = startIndex; i < array.length; i++) {
				if (array[i] == null) {
					return i;
				}
			}
		} else {
			for (int i = startIndex; i < array.length; i++) {
				if (objectToFind.equals(array[i])) {
					return i;
				}
			}
		}
		return INDEX_NOT_FOUND;
	}

	/**
	 * Finds the index of the given value in the array.
	 * <p>
	 * This method returns {@link #INDEX_NOT_FOUND} ({@code -1}) for a {@code null}
	 * input array.
	 * </p>
	 *
	 * @param array       the array to search through for the object, may be
	 *                    {@code null}
	 * @param valueToFind the value to find
	 * @return the index of the value within the array, {@link #INDEX_NOT_FOUND}
	 *         ({@code -1}) if not found or {@code null} array input
	 */
	public static int indexOf(final short[] array, final short valueToFind) {
		return indexOf(array, valueToFind, 0);
	}

	/**
	 * Finds the index of the given value in the array starting at the given index.
	 * <p>
	 * This method returns {@link #INDEX_NOT_FOUND} ({@code -1}) for a {@code null}
	 * input array.
	 * </p>
	 * <p>
	 * A negative startIndex is treated as zero. A startIndex larger than the array
	 * length will return {@link #INDEX_NOT_FOUND} ({@code -1}).
	 * </p>
	 *
	 * @param array       the array to search through for the object, may be
	 *                    {@code null}
	 * @param valueToFind the value to find
	 * @param startIndex  the index to start searching at
	 * @return the index of the value within the array, {@link #INDEX_NOT_FOUND}
	 *         ({@code -1}) if not found or {@code null} array input
	 */
	public static int indexOf(final short[] array, final short valueToFind, final int startIndex) {
		if (array == null) {
			return INDEX_NOT_FOUND;
		}
		for (int i = max0(startIndex); i < array.length; i++) {
			if (valueToFind == array[i]) {
				return i;
			}
		}
		return INDEX_NOT_FOUND;
	}

	public static KeyBinding[] addAll(KeyBinding[] arr1, KeyBinding[] arr2) {
		KeyBinding[] clone = new KeyBinding[arr1.length + arr2.length];
		System.arraycopy(arr1, 0, clone, 0, arr1.length);
		System.arraycopy(arr2, 0, clone, arr1.length, arr2.length);
		return clone;
	}

	public static String[] subarray(String[] stackTrace, int i, int j) {
		String[] ret = new String[j - i];
		System.arraycopy(stackTrace, i, ret, 0, j - i);
		return ret;
	}

	private static int max0(final int other) {
		return Math.max(0, other);
	}

	/**
	 * A fluent version of {@link System#arraycopy(Object, int, Object, int, int)} that returns the destination array.
	 *
	 * @param <T>       the type.
	 * @param source    the source array.
	 * @param sourcePos starting position in the source array.
	 * @param destPos   starting position in the destination data.
	 * @param length    the number of array elements to be copied.
	 * @param allocator allocates the array to populate and return.
	 * @return dest
	 * @throws IndexOutOfBoundsException if copying would cause access of data outside array bounds.
	 * @throws ArrayStoreException       if an element in the <code>src</code> array could not be stored into the <code>dest</code> array because of a type
	 *                                   mismatch.
	 * @throws NullPointerException      if either <code>src</code> or <code>dest</code> is <code>null</code>.
	 * @since 3.15.0
	 */
	public static < T > T arraycopy(final T source, final int sourcePos, final int destPos, final int length, final Function < Integer, T > allocator) {
	    return arraycopy(source, sourcePos, allocator.apply(length), destPos, length);
	}

	/**
	 * A fluent version of {@link System#arraycopy(Object, int, Object, int, int)} that returns the destination array.
	 *
	 * @param <T>       the type.
	 * @param source    the source array.
	 * @param sourcePos starting position in the source array.
	 * @param destPos   starting position in the destination data.
	 * @param length    the number of array elements to be copied.
	 * @param allocator allocates the array to populate and return.
	 * @return dest
	 * @throws IndexOutOfBoundsException if copying would cause access of data outside array bounds.
	 * @throws ArrayStoreException       if an element in the <code>src</code> array could not be stored into the <code>dest</code> array because of a type
	 *                                   mismatch.
	 * @throws NullPointerException      if either <code>src</code> or <code>dest</code> is <code>null</code>.
	 * @since 3.15.0
	 */
	public static < T > T arraycopy(final T source, final int sourcePos, final int destPos, final int length, final Supplier < T > allocator) {
	    return arraycopy(source, sourcePos, allocator.get(), destPos, length);
	}

	/**
	 * A fluent version of {@link System#arraycopy(Object, int, Object, int, int)} that returns the destination array.
	 *
	 * @param <T>       the type
	 * @param source    the source array.
	 * @param sourcePos starting position in the source array.
	 * @param dest      the destination array.
	 * @param destPos   starting position in the destination data.
	 * @param length    the number of array elements to be copied.
	 * @return dest
	 * @throws IndexOutOfBoundsException if copying would cause access of data outside array bounds.
	 * @throws ArrayStoreException       if an element in the <code>src</code> array could not be stored into the <code>dest</code> array because of a type
	 *                                   mismatch.
	 * @throws NullPointerException      if either <code>src</code> or <code>dest</code> is <code>null</code>.
	 * @since 3.15.0
	 */
	public static < T > T arraycopy(final T source, final int sourcePos, final T dest, final int destPos, final int length) {
	    System.arraycopy(source, sourcePos, dest, destPos, length);
	    return dest;
	}

	public static String arrayToString(boolean[] arr, String separator) {
	    if (arr == null) {
	        return "";
	    } else {
	        StringBuffer stringbuffer = new StringBuffer(arr.length * 5);

	        for (int i = 0; i < arr.length; ++i) {
	            boolean flag = arr[i];

	            if (i > 0) {
	                stringbuffer.append(separator);
	            }

	            stringbuffer.append(String.valueOf(flag));
	        }

	        return stringbuffer.toString();
	    }
	}

	public static String arrayToString(float[] arr) {
	    return arrayToString(arr, ", ");
	}

	public static String arrayToString(float[] arr, String separator) {
	    if (arr == null) {
	        return "";
	    } else {
	        StringBuffer stringbuffer = new StringBuffer(arr.length * 5);

	        for (int i = 0; i < arr.length; ++i) {
	            float f = arr[i];

	            if (i > 0) {
	                stringbuffer.append(separator);
	            }

	            stringbuffer.append(String.valueOf(f));
	        }

	        return stringbuffer.toString();
	    }
	}

	public static String arrayToString(float[] arr, String separator, String format) {
	    if (arr == null) {
	        return "";
	    } else {
	        StringBuffer stringbuffer = new StringBuffer(arr.length * 5);

	        for (int i = 0; i < arr.length; ++i) {
	            float f = arr[i];

	            if (i > 0) {
	                stringbuffer.append(separator);
	            }

	            stringbuffer.append(String.format(format, new Object[] {
	                Float.valueOf(f)
	            }));
	        }

	        return stringbuffer.toString();
	    }
	}

	public static String arrayToString(int[] arr) {
	    return arrayToString(arr, ", ");
	}

	public static String arrayToString(int[] arr, String separator) {
	    if (arr == null) {
	        return "";
	    } else {
	        StringBuffer stringbuffer = new StringBuffer(arr.length * 5);

	        for (int i = 0; i < arr.length; ++i) {
	            int j = arr[i];

	            if (i > 0) {
	                stringbuffer.append(separator);
	            }

	            stringbuffer.append(String.valueOf(j));
	        }

	        return stringbuffer.toString();
	    }
	}

	public static String arrayToHexString(int[] arr, String separator) {
	    if (arr == null) {
	        return "";
	    } else {
	        StringBuffer stringbuffer = new StringBuffer(arr.length * 5);

	        for (int i = 0; i < arr.length; ++i) {
	            int j = arr[i];

	            if (i > 0) {
	                stringbuffer.append(separator);
	            }

	            stringbuffer.append("0x");
	            stringbuffer.append(Integer.toHexString(j));
	        }

	        return stringbuffer.toString();
	    }
	}

	public static String arrayToString(Object[] arr) {
	    return arrayToString(arr, ", ");
	}

	public static String arrayToString(Object[] arr, String separator) {
	    if (arr == null) {
	        return "";
	    } else {
	        StringBuffer stringbuffer = new StringBuffer(arr.length * 5);

	        for (int i = 0; i < arr.length; ++i) {
	            Object object = arr[i];

	            if (i > 0) {
	                stringbuffer.append(separator);
	            }

	            stringbuffer.append(String.valueOf(object));
	        }

	        return stringbuffer.toString();
	    }
	}
}
