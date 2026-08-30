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
package it.unimi.dsi.fastutil.floats;

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
public interface Float2ShortFunction extends Function<Float, Short>, java.util.function.DoubleToIntFunction {
	/**
	 * {@inheritDoc}
	 *
	 * @implSpec This default implementation delegates to the type-specific {@code get()} method after
	 *           narrowing down the key to the actual key type, throwing an exception if the given key
	 *           cannot be represented in the restricted domain. This is done for interoperability with
	 *           the Java 8 function environment. The use of this method is discouraged, as unexpected
	 *           errors can occur. Instead, the corresponding classes should be used (e.g.,
	 *           {@link it.unimi.dsi.fastutil.ints.Int2IntFunction} instead of
	 *           {@link it.unimi.dsi.fastutil.shorts.Short2IntFunction}).
	 *
	 * @throws IllegalArgumentException If the given operand is not an element of the key domain.
	 * @since 8.0.0
	 * @deprecated Please use primitive types which do not have to be widened as keys.
	 */
	@Deprecated
	@Override
	default int applyAsInt(double operand) {
		return get(it.unimi.dsi.fastutil.SafeMath.safeDoubleToFloat(operand));
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
	default short put(final float key, final short value) {
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
	short get(float key);

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
	default short getOrDefault(final float key, final short defaultValue) {
		final short v;
		return ((v = get(key)) != defaultReturnValue() || containsKey(key)) ? v : defaultValue;
	}

	/**
	 * Removes the mapping with the given key (optional operation).
	 * 
	 * @param key the key.
	 * @return the old value, or the {@linkplain #defaultReturnValue() default return value} if no value
	 *         was present for the given key.
	 * @see Function#remove(Object)
	 */
	default short remove(final float key) {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	default Short put(final Float key, final Short value) {
		final float k = (key).floatValue();
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
		if (key == null) return null;
		final float k = ((Float)(key)).floatValue();
		final short v;
		return ((v = get(k)) != defaultReturnValue() || containsKey(k)) ? Short.valueOf(v) : null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	default Short getOrDefault(final Object key, Short defaultValue) {
		if (key == null) return defaultValue;
		final float k = ((Float)(key)).floatValue();
		final short v = get(k);
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
		if (key == null) return null;
		final float k = ((Float)(key)).floatValue();
		return containsKey(k) ? Short.valueOf(remove(k)) : null;
	}

	/**
	 * Returns true if this function contains a mapping for the specified key.
	 *
	 * <p>
	 * Note that for some kind of functions (e.g., hashes) this method will always return true. In
	 * particular, this default implementation always returns true.
	 *
	 * @param key the key.
	 * @return true if this function associates a value to {@code key}.
	 * @see Function#containsKey(Object)
	 */
	default boolean containsKey(float key) {
		return true;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	default boolean containsKey(final Object key) {
		return key == null ? false : containsKey(((Float)(key)).floatValue());
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
	default <T> java.util.function.Function<T, Short> compose(java.util.function.Function<? super T, ? extends Float> before) {
		return Function.super.compose(before);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	default <T> java.util.function.Function<Float, T> andThen(java.util.function.Function<? super Short, ? extends T> after) {
		return Function.super.andThen(after);
	}

	default it.unimi.dsi.fastutil.floats.Float2ByteFunction andThenByte(it.unimi.dsi.fastutil.shorts.Short2ByteFunction after) {
		return k -> after.get(get(k));
	}

	default it.unimi.dsi.fastutil.bytes.Byte2ShortFunction composeByte(it.unimi.dsi.fastutil.bytes.Byte2FloatFunction before) {
		return k -> get(before.get(k));
	}

	default it.unimi.dsi.fastutil.floats.Float2ShortFunction andThenShort(it.unimi.dsi.fastutil.shorts.Short2ShortFunction after) {
		return k -> after.get(get(k));
	}

	default it.unimi.dsi.fastutil.shorts.Short2ShortFunction composeShort(it.unimi.dsi.fastutil.shorts.Short2FloatFunction before) {
		return k -> get(before.get(k));
	}

	default it.unimi.dsi.fastutil.floats.Float2IntFunction andThenInt(it.unimi.dsi.fastutil.shorts.Short2IntFunction after) {
		return k -> after.get(get(k));
	}

	default it.unimi.dsi.fastutil.ints.Int2ShortFunction composeInt(it.unimi.dsi.fastutil.ints.Int2FloatFunction before) {
		return k -> get(before.get(k));
	}

	default it.unimi.dsi.fastutil.floats.Float2LongFunction andThenLong(it.unimi.dsi.fastutil.shorts.Short2LongFunction after) {
		return k -> after.get(get(k));
	}

	default it.unimi.dsi.fastutil.longs.Long2ShortFunction composeLong(it.unimi.dsi.fastutil.longs.Long2FloatFunction before) {
		return k -> get(before.get(k));
	}

	default it.unimi.dsi.fastutil.floats.Float2CharFunction andThenChar(it.unimi.dsi.fastutil.shorts.Short2CharFunction after) {
		return k -> after.get(get(k));
	}

	default it.unimi.dsi.fastutil.chars.Char2ShortFunction composeChar(it.unimi.dsi.fastutil.chars.Char2FloatFunction before) {
		return k -> get(before.get(k));
	}

	default it.unimi.dsi.fastutil.floats.Float2FloatFunction andThenFloat(it.unimi.dsi.fastutil.shorts.Short2FloatFunction after) {
		return k -> after.get(get(k));
	}

	default it.unimi.dsi.fastutil.floats.Float2ShortFunction composeFloat(it.unimi.dsi.fastutil.floats.Float2FloatFunction before) {
		return k -> get(before.get(k));
	}

	default it.unimi.dsi.fastutil.floats.Float2DoubleFunction andThenDouble(it.unimi.dsi.fastutil.shorts.Short2DoubleFunction after) {
		return k -> after.get(get(k));
	}

	default it.unimi.dsi.fastutil.doubles.Double2ShortFunction composeDouble(it.unimi.dsi.fastutil.doubles.Double2FloatFunction before) {
		return k -> get(before.get(k));
	}

	default <T> it.unimi.dsi.fastutil.floats.Float2ObjectFunction<T> andThenObject(it.unimi.dsi.fastutil.shorts.Short2ObjectFunction<? extends T> after) {
		return k -> after.get(get(k));
	}

	default <T> it.unimi.dsi.fastutil.objects.Object2ShortFunction<T> composeObject(it.unimi.dsi.fastutil.objects.Object2FloatFunction<? super T> before) {
		return k -> get(before.getFloat(k));
	}

	default <T> it.unimi.dsi.fastutil.floats.Float2ReferenceFunction<T> andThenReference(it.unimi.dsi.fastutil.shorts.Short2ReferenceFunction<? extends T> after) {
		return k -> after.get(get(k));
	}

	default <T> it.unimi.dsi.fastutil.objects.Reference2ShortFunction<T> composeReference(it.unimi.dsi.fastutil.objects.Reference2FloatFunction<? super T> before) {
		return k -> get(before.getFloat(k));
	}
}
