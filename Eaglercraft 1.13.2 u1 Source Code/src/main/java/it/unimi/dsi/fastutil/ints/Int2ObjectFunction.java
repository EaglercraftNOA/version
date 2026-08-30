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
package it.unimi.dsi.fastutil.ints;

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
public interface Int2ObjectFunction<V> extends Function<Integer, V>, java.util.function.IntFunction<V> {
	/**
	 * {@inheritDoc}
	 * 
	 * @since 8.0.0
	 */
	@Override
	default V apply(int operand) {
		return get(operand);
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
	default V put(final int key, final V value) {
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
	V get(int key);

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
	default V getOrDefault(final int key, final V defaultValue) {
		final V v;
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
	default V remove(final int key) {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	default V put(final Integer key, final V value) {
		final int k = (key).intValue();
		final boolean containsKey = containsKey(k);
		final V v = put(k, (value));
		return containsKey ? (v) : null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	default V get(final Object key) {
		if (key == null) return null;
		final int k = ((Integer)(key)).intValue();
		final V v;
		return ((v = get(k)) != defaultReturnValue() || containsKey(k)) ? (v) : null;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	default V getOrDefault(final Object key, V defaultValue) {
		if (key == null) return defaultValue;
		final int k = ((Integer)(key)).intValue();
		final V v = get(k);
		return (v != defaultReturnValue() || containsKey(k)) ? (v) : defaultValue;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	default V remove(final Object key) {
		if (key == null) return null;
		final int k = ((Integer)(key)).intValue();
		return containsKey(k) ? (remove(k)) : null;
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
	default boolean containsKey(int key) {
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
		return key == null ? false : containsKey(((Integer)(key)).intValue());
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
	default void defaultReturnValue(V rv) {
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
	default V defaultReturnValue() {
		return (null);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated Please use the corresponding type-specific method instead.
	 */
	@Deprecated
	@Override
	default <T> java.util.function.Function<T, V> compose(java.util.function.Function<? super T, ? extends Integer> before) {
		return Function.super.compose(before);
	}

	default it.unimi.dsi.fastutil.ints.Int2ByteFunction andThenByte(it.unimi.dsi.fastutil.objects.Object2ByteFunction<V> after) {
		return k -> after.getByte(get(k));
	}

	default it.unimi.dsi.fastutil.bytes.Byte2ObjectFunction<V> composeByte(it.unimi.dsi.fastutil.bytes.Byte2IntFunction before) {
		return k -> get(before.get(k));
	}

	default it.unimi.dsi.fastutil.ints.Int2ShortFunction andThenShort(it.unimi.dsi.fastutil.objects.Object2ShortFunction<V> after) {
		return k -> after.getShort(get(k));
	}

	default it.unimi.dsi.fastutil.shorts.Short2ObjectFunction<V> composeShort(it.unimi.dsi.fastutil.shorts.Short2IntFunction before) {
		return k -> get(before.get(k));
	}

	default it.unimi.dsi.fastutil.ints.Int2IntFunction andThenInt(it.unimi.dsi.fastutil.objects.Object2IntFunction<V> after) {
		return k -> after.getInt(get(k));
	}

	default it.unimi.dsi.fastutil.ints.Int2ObjectFunction<V> composeInt(it.unimi.dsi.fastutil.ints.Int2IntFunction before) {
		return k -> get(before.get(k));
	}

	default it.unimi.dsi.fastutil.ints.Int2LongFunction andThenLong(it.unimi.dsi.fastutil.objects.Object2LongFunction<V> after) {
		return k -> after.getLong(get(k));
	}

	default it.unimi.dsi.fastutil.longs.Long2ObjectFunction<V> composeLong(it.unimi.dsi.fastutil.longs.Long2IntFunction before) {
		return k -> get(before.get(k));
	}

	default it.unimi.dsi.fastutil.ints.Int2CharFunction andThenChar(it.unimi.dsi.fastutil.objects.Object2CharFunction<V> after) {
		return k -> after.getChar(get(k));
	}

	default it.unimi.dsi.fastutil.chars.Char2ObjectFunction<V> composeChar(it.unimi.dsi.fastutil.chars.Char2IntFunction before) {
		return k -> get(before.get(k));
	}

	default it.unimi.dsi.fastutil.ints.Int2FloatFunction andThenFloat(it.unimi.dsi.fastutil.objects.Object2FloatFunction<V> after) {
		return k -> after.getFloat(get(k));
	}

	default it.unimi.dsi.fastutil.floats.Float2ObjectFunction<V> composeFloat(it.unimi.dsi.fastutil.floats.Float2IntFunction before) {
		return k -> get(before.get(k));
	}

	default it.unimi.dsi.fastutil.ints.Int2DoubleFunction andThenDouble(it.unimi.dsi.fastutil.objects.Object2DoubleFunction<V> after) {
		return k -> after.getDouble(get(k));
	}

	default it.unimi.dsi.fastutil.doubles.Double2ObjectFunction<V> composeDouble(it.unimi.dsi.fastutil.doubles.Double2IntFunction before) {
		return k -> get(before.get(k));
	}

	default <T> it.unimi.dsi.fastutil.ints.Int2ObjectFunction<T> andThenObject(it.unimi.dsi.fastutil.objects.Object2ObjectFunction<? super V, ? extends T> after) {
		return k -> after.get(get(k));
	}

	default <T> it.unimi.dsi.fastutil.objects.Object2ObjectFunction<T, V> composeObject(it.unimi.dsi.fastutil.objects.Object2IntFunction<? super T> before) {
		return k -> get(before.getInt(k));
	}

	default <T> it.unimi.dsi.fastutil.ints.Int2ReferenceFunction<T> andThenReference(it.unimi.dsi.fastutil.objects.Object2ReferenceFunction<? super V, ? extends T> after) {
		return k -> after.get(get(k));
	}

	default <T> it.unimi.dsi.fastutil.objects.Reference2ObjectFunction<T, V> composeReference(it.unimi.dsi.fastutil.objects.Reference2IntFunction<? super T> before) {
		return k -> get(before.getInt(k));
	}
}
