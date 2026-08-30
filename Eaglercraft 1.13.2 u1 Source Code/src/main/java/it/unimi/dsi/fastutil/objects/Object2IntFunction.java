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

import it.unimi.dsi.fastutil.Function;

/**
 * A type-specific {@link Function}; provides some additional methods that use polymorphism to avoid
 * (un)boxing.
 *
 * <p>
 * Type-specific versions of {@code get()}, {@code put()} and {@code remove()} cannot rely on
 * {@code null} to denote absence of a key. Rather, they return a {@linkplain #defaultReturnValue()
 * default return value}, which is set to 0/false at creation, but can be changed using the
 * {@code defaultReturnValue()} method.
 *
 * <p>
 * For uniformity reasons, even functions returning objects implement the default return value (of
 * course, in this case the default return value is initialized to {@code null}).
 *
 * <p>
 * The default implementation of optional operations just throw an
 * {@link UnsupportedOperationException}, except for the type-specific {@code
* containsKey()}, which return true. Generic versions of accessors delegate to the corresponding
 * type-specific counterparts following the interface rules.
 *
 * <p>
 * <strong>Warning:</strong> to fall in line as much as possible with the {@linkplain java.util.Map
 * standard map interface}, it is required that standard versions of {@code get()}, {@code put()}
 * and {@code remove()} for maps with primitive-type keys or values <em>return {@code null} to
 * denote missing keys </em> rather than wrap the default return value in an object. In case both
 * keys and values are reference types, the default return value must be returned instead, thus
 * violating the {@linkplain java.util.Map standard map interface} when the default return value is
 * not {@code null}.
 *
 * @see Function
 */
@FunctionalInterface
public interface Object2IntFunction<K> extends Function<K, Integer>, java.util.function.ToIntFunction<K> {
	/**
	 * {@inheritDoc}
	 * 
	 * @since 8.0.0
	 */
	@Override
	default int applyAsInt(K operand) {
		return getInt(operand);
	}

	/**
	 * Adds a pair to the map (optional operation).
	 *
	 * @param key the key.
	 * @param value the value.
	 * @return the old value, or the {@linkplain #defaultReturnValue() default return value} if no value
	 *         was present for the given key.
	 * @see Function#put(Object,Object)
	 */
	default int put(final K key, final int value) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Returns the value to which the given key is mapped.
	 *
	 * @param key the key.
	 * @return the corresponding value, or the {@linkplain #defaultReturnValue() default return value}
	 *         if no value was present for the given key.
	 * @see Function#get(Object)
	 */
	int getInt(Object key);

	/**
	 * Returns the value associated by this function to the specified key, or give the specified value
	 * if not present.
	 *
	 * @param key the key.
	 * @param defaultValue the value to return if not present.
	 * @return the corresponding value, or {@code defaultValue} if no value was present for the given
	 *         key.
	 * @see Function#getOrDefault(Object, Object)
	 * @since 8.5.0
	 */
	default int getOrDefault(final Object key, final int defaultValue) {
		final int v;
		return ((v = getInt(key)) != defaultReturnValue() || containsKey(key)) ? v : defaultValue;
	}

	/**
	 * Removes the mapping with the given key (optional operation).
	 * 
	 * @param key the key.
	 * @return the old value, or the {@linkplain #defaultReturnValue() default return value} if no value
	 *         was present for the given key.
	 * @see Function#remove(Object)
	 */
	default int removeInt(final Object key) {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	default Integer put(final K key, final Integer value) {
		final K k = (key);
		final boolean containsKey = containsKey(k);
		final int v = put(k, (value).intValue());
		return containsKey ? Integer.valueOf(v) : null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	default Integer get(final Object key) {
		final Object k = (key);
		final int v;
		return ((v = getInt(k)) != defaultReturnValue() || containsKey(k)) ? Integer.valueOf(v) : null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	default Integer getOrDefault(final Object key, Integer defaultValue) {
		final Object k = (key);
		final int v = getInt(k);
		return (v != defaultReturnValue() || containsKey(k)) ? Integer.valueOf(v) : defaultValue;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	default Integer remove(final Object key) {
		final Object k = (key);
		return containsKey(k) ? Integer.valueOf(removeInt(k)) : null;
	}

	/**
	 * Sets the default return value (optional operation).
	 *
	 * This value must be returned by type-specific versions of {@code get()}, {@code put()} and
	 * {@code remove()} to denote that the map does not contain the specified key. It must be
	 * 0/{@code false}/{@code null} by default.
	 *
	 * @param rv the new default return value.
	 * @see #defaultReturnValue()
	 */
	default void defaultReturnValue(int rv) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Gets the default return value.
	 *
	 * <p>
	 * This default implementation just return the default null value of the type ({@code null} for
	 * objects, 0 for scalars, false for Booleans).
	 *
	 * @return the current default return value.
	 */
	default int defaultReturnValue() {
		return (0);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	default <T> java.util.function.Function<K, T> andThen(java.util.function.Function<? super Integer, ? extends T> after) {
		return Function.super.andThen(after);
	}

	default it.unimi.dsi.fastutil.objects.Object2ByteFunction<K> andThenByte(it.unimi.dsi.fastutil.ints.Int2ByteFunction after) {
		return k -> after.get(getInt(k));
	}

	default it.unimi.dsi.fastutil.bytes.Byte2IntFunction composeByte(it.unimi.dsi.fastutil.bytes.Byte2ObjectFunction<K> before) {
		return k -> getInt(before.get(k));
	}

	default it.unimi.dsi.fastutil.objects.Object2ShortFunction<K> andThenShort(it.unimi.dsi.fastutil.ints.Int2ShortFunction after) {
		return k -> after.get(getInt(k));
	}

	default it.unimi.dsi.fastutil.shorts.Short2IntFunction composeShort(it.unimi.dsi.fastutil.shorts.Short2ObjectFunction<K> before) {
		return k -> getInt(before.get(k));
	}

	default it.unimi.dsi.fastutil.objects.Object2IntFunction<K> andThenInt(it.unimi.dsi.fastutil.ints.Int2IntFunction after) {
		return k -> after.get(getInt(k));
	}

	default it.unimi.dsi.fastutil.ints.Int2IntFunction composeInt(it.unimi.dsi.fastutil.ints.Int2ObjectFunction<K> before) {
		return k -> getInt(before.get(k));
	}

	default it.unimi.dsi.fastutil.objects.Object2LongFunction<K> andThenLong(it.unimi.dsi.fastutil.ints.Int2LongFunction after) {
		return k -> after.get(getInt(k));
	}

	default it.unimi.dsi.fastutil.longs.Long2IntFunction composeLong(it.unimi.dsi.fastutil.longs.Long2ObjectFunction<K> before) {
		return k -> getInt(before.get(k));
	}

	default it.unimi.dsi.fastutil.objects.Object2CharFunction<K> andThenChar(it.unimi.dsi.fastutil.ints.Int2CharFunction after) {
		return k -> after.get(getInt(k));
	}

	default it.unimi.dsi.fastutil.chars.Char2IntFunction composeChar(it.unimi.dsi.fastutil.chars.Char2ObjectFunction<K> before) {
		return k -> getInt(before.get(k));
	}

	default it.unimi.dsi.fastutil.objects.Object2FloatFunction<K> andThenFloat(it.unimi.dsi.fastutil.ints.Int2FloatFunction after) {
		return k -> after.get(getInt(k));
	}

	default it.unimi.dsi.fastutil.floats.Float2IntFunction composeFloat(it.unimi.dsi.fastutil.floats.Float2ObjectFunction<K> before) {
		return k -> getInt(before.get(k));
	}

	default it.unimi.dsi.fastutil.objects.Object2DoubleFunction<K> andThenDouble(it.unimi.dsi.fastutil.ints.Int2DoubleFunction after) {
		return k -> after.get(getInt(k));
	}

	default it.unimi.dsi.fastutil.doubles.Double2IntFunction composeDouble(it.unimi.dsi.fastutil.doubles.Double2ObjectFunction<K> before) {
		return k -> getInt(before.get(k));
	}

	default <T> it.unimi.dsi.fastutil.objects.Object2ObjectFunction<K, T> andThenObject(it.unimi.dsi.fastutil.ints.Int2ObjectFunction<? extends T> after) {
		return k -> after.get(getInt(k));
	}

	default <T> it.unimi.dsi.fastutil.objects.Object2IntFunction<T> composeObject(it.unimi.dsi.fastutil.objects.Object2ObjectFunction<? super T, ? extends K> before) {
		return k -> getInt(before.get(k));
	}

	default <T> it.unimi.dsi.fastutil.objects.Object2ReferenceFunction<K, T> andThenReference(it.unimi.dsi.fastutil.ints.Int2ReferenceFunction<? extends T> after) {
		return k -> after.get(getInt(k));
	}

	default <T> it.unimi.dsi.fastutil.objects.Reference2IntFunction<T> composeReference(it.unimi.dsi.fastutil.objects.Reference2ObjectFunction<? super T, ? extends K> before) {
		return k -> getInt(before.get(k));
	}
}
