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
public interface Reference2ShortFunction<K> extends Function<K, Short>, java.util.function.ToIntFunction<K> {
	/**
	 * {@inheritDoc}
	 * 
	 * @since 8.0.0
	 */
	@Override
	default int applyAsInt(K operand) {
		return getShort(operand);
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
	default short put(final K key, final short value) {
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
	short getShort(Object key);

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
	default short getOrDefault(final Object key, final short defaultValue) {
		final short v;
		return ((v = getShort(key)) != defaultReturnValue() || containsKey(key)) ? v : defaultValue;
	}

	/**
	 * Removes the mapping with the given key (optional operation).
	 * 
	 * @param key the key.
	 * @return the old value, or the {@linkplain #defaultReturnValue() default return value} if no value
	 *         was present for the given key.
	 * @see Function#remove(Object)
	 */
	default short removeShort(final Object key) {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	default Short put(final K key, final Short value) {
		final K k = (key);
		final boolean containsKey = containsKey(k);
		final short v = put(k, (value).shortValue());
		return containsKey ? Short.valueOf(v) : null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	default Short get(final Object key) {
		final Object k = (key);
		final short v;
		return ((v = getShort(k)) != defaultReturnValue() || containsKey(k)) ? Short.valueOf(v) : null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	default Short getOrDefault(final Object key, Short defaultValue) {
		final Object k = (key);
		final short v = getShort(k);
		return (v != defaultReturnValue() || containsKey(k)) ? Short.valueOf(v) : defaultValue;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	default Short remove(final Object key) {
		final Object k = (key);
		return containsKey(k) ? Short.valueOf(removeShort(k)) : null;
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
	default void defaultReturnValue(short rv) {
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
	default short defaultReturnValue() {
		return ((short)0);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	default <T> java.util.function.Function<K, T> andThen(java.util.function.Function<? super Short, ? extends T> after) {
		return Function.super.andThen(after);
	}

	default it.unimi.dsi.fastutil.objects.Reference2ByteFunction<K> andThenByte(it.unimi.dsi.fastutil.shorts.Short2ByteFunction after) {
		return k -> after.get(getShort(k));
	}

	default it.unimi.dsi.fastutil.bytes.Byte2ShortFunction composeByte(it.unimi.dsi.fastutil.bytes.Byte2ReferenceFunction<K> before) {
		return k -> getShort(before.get(k));
	}

	default it.unimi.dsi.fastutil.objects.Reference2ShortFunction<K> andThenShort(it.unimi.dsi.fastutil.shorts.Short2ShortFunction after) {
		return k -> after.get(getShort(k));
	}

	default it.unimi.dsi.fastutil.shorts.Short2ShortFunction composeShort(it.unimi.dsi.fastutil.shorts.Short2ReferenceFunction<K> before) {
		return k -> getShort(before.get(k));
	}

	default it.unimi.dsi.fastutil.objects.Reference2IntFunction<K> andThenInt(it.unimi.dsi.fastutil.shorts.Short2IntFunction after) {
		return k -> after.get(getShort(k));
	}

	default it.unimi.dsi.fastutil.ints.Int2ShortFunction composeInt(it.unimi.dsi.fastutil.ints.Int2ReferenceFunction<K> before) {
		return k -> getShort(before.get(k));
	}

	default it.unimi.dsi.fastutil.objects.Reference2LongFunction<K> andThenLong(it.unimi.dsi.fastutil.shorts.Short2LongFunction after) {
		return k -> after.get(getShort(k));
	}

	default it.unimi.dsi.fastutil.longs.Long2ShortFunction composeLong(it.unimi.dsi.fastutil.longs.Long2ReferenceFunction<K> before) {
		return k -> getShort(before.get(k));
	}

	default it.unimi.dsi.fastutil.objects.Reference2CharFunction<K> andThenChar(it.unimi.dsi.fastutil.shorts.Short2CharFunction after) {
		return k -> after.get(getShort(k));
	}

	default it.unimi.dsi.fastutil.chars.Char2ShortFunction composeChar(it.unimi.dsi.fastutil.chars.Char2ReferenceFunction<K> before) {
		return k -> getShort(before.get(k));
	}

	default it.unimi.dsi.fastutil.objects.Reference2FloatFunction<K> andThenFloat(it.unimi.dsi.fastutil.shorts.Short2FloatFunction after) {
		return k -> after.get(getShort(k));
	}

	default it.unimi.dsi.fastutil.floats.Float2ShortFunction composeFloat(it.unimi.dsi.fastutil.floats.Float2ReferenceFunction<K> before) {
		return k -> getShort(before.get(k));
	}

	default it.unimi.dsi.fastutil.objects.Reference2DoubleFunction<K> andThenDouble(it.unimi.dsi.fastutil.shorts.Short2DoubleFunction after) {
		return k -> after.get(getShort(k));
	}

	default it.unimi.dsi.fastutil.doubles.Double2ShortFunction composeDouble(it.unimi.dsi.fastutil.doubles.Double2ReferenceFunction<K> before) {
		return k -> getShort(before.get(k));
	}

	default <T> it.unimi.dsi.fastutil.objects.Reference2ObjectFunction<K, T> andThenObject(it.unimi.dsi.fastutil.shorts.Short2ObjectFunction<? extends T> after) {
		return k -> after.get(getShort(k));
	}

	default <T> it.unimi.dsi.fastutil.objects.Object2ShortFunction<T> composeObject(it.unimi.dsi.fastutil.objects.Object2ReferenceFunction<? super T, ? extends K> before) {
		return k -> getShort(before.get(k));
	}

	default <T> it.unimi.dsi.fastutil.objects.Reference2ReferenceFunction<K, T> andThenReference(it.unimi.dsi.fastutil.shorts.Short2ReferenceFunction<? extends T> after) {
		return k -> after.get(getShort(k));
	}

	default <T> it.unimi.dsi.fastutil.objects.Reference2ShortFunction<T> composeReference(it.unimi.dsi.fastutil.objects.Reference2ReferenceFunction<? super T, ? extends K> before) {
		return k -> getShort(before.get(k));
	}
}
