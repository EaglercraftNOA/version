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
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.Function;

/**
 * A class providing static methods and objects that do useful things with type-specific functions.
 *
 * @see it.unimi.dsi.fastutil.Function
 * @see java.util.Collections
 */
public final class Short2CharFunctions {
	private Short2CharFunctions() {
	}

	/**
	 * An immutable class representing an empty type-specific function.
	 *
	 * <p>
	 * This class may be useful to implement your own in case you subclass a type-specific function.
	 */
	public static class EmptyFunction extends AbstractShort2CharFunction implements java.io.Serializable, Cloneable {
		private static final long serialVersionUID = -7046029254386353129L;

		protected EmptyFunction() {
		}

		@Override
		public char get(final short k) {
			return ((char)0);
		}

		@Override
		public char getOrDefault(final short k, final char defaultValue) {
			return defaultValue;
		}

		@Override
		public boolean containsKey(final short k) {
			return false;
		}

		@Override
		public char defaultReturnValue() {
			return ((char)0);
		}

		@Override
		public void defaultReturnValue(final char defRetValue) {
			throw new UnsupportedOperationException();
		}

		@Override
		public int size() {
			return 0;
		}

		@Override
		public void clear() {
		}

		@Override
		public Object clone() {
			return EMPTY_FUNCTION;
		}

		@Override
		public int hashCode() {
			return 0;
		}

		@Override
		public boolean equals(final Object o) {
			if (!(o instanceof Function)) return false;
			return ((Function<?, ?>)o).size() == 0;
		}

		@Override
		public String toString() {
			return "{}";
		}

		private Object readResolve() {
			return EMPTY_FUNCTION;
		}
	}

	/** An empty type-specific function (immutable). It is serializable and cloneable. */

	public static final EmptyFunction EMPTY_FUNCTION = new EmptyFunction();

	/**
	 * An immutable class representing a type-specific singleton function. Note that the default return
	 * value is still settable.
	 *
	 * <p>
	 * Note that albeit the function is immutable, its default return value may be changed.
	 *
	 * <p>
	 * This class may be useful to implement your own in case you subclass a type-specific function.
	 */
	public static class Singleton extends AbstractShort2CharFunction implements java.io.Serializable, Cloneable {
		private static final long serialVersionUID = -7046029254386353129L;
		protected final short key;
		protected final char value;

		protected Singleton(final short key, final char value) {
			this.key = key;
			this.value = value;
		}

		@Override
		public boolean containsKey(final short k) {
			return ((key) == (k));
		}

		@Override
		public char get(final short k) {
			return ((key) == (k)) ? value : defRetValue;
		}

		@Override
		public char getOrDefault(final short k, final char defaultValue) {
			return ((key) == (k)) ? value : defaultValue;
		}

		@Override
		public int size() {
			return 1;
		}

		@Override
		public Object clone() {
			return this;
		}
	}

	/**
	 * Returns a type-specific immutable function containing only the specified pair. The returned
	 * function is serializable and cloneable.
	 *
	 * <p>
	 * Note that albeit the returned function is immutable, its default return value may be changed.
	 *
	 * @param key the only key of the returned function.
	 * @param value the only value of the returned function.
	 * @return a type-specific immutable function containing just the pair {@code &lt;key,value&gt;}.
	 */
	public static Short2CharFunction singleton(final short key, char value) {
		return new Singleton(key, value);
	}

	/**
	 * Returns a type-specific immutable function containing only the specified pair. The returned
	 * function is serializable and cloneable.
	 *
	 * <p>
	 * Note that albeit the returned function is immutable, its default return value may be changed.
	 *
	 * @param key the only key of the returned function.
	 * @param value the only value of the returned function.
	 * @return a type-specific immutable function containing just the pair {@code &lt;key,value&gt;}.
	 */
	public static Short2CharFunction singleton(final Short key, final Character value) {
		return new Singleton((key).shortValue(), (value).charValue());
	}

	/** A synchronized wrapper class for functions. */
	public static class SynchronizedFunction implements Short2CharFunction, java.io.Serializable {
		private static final long serialVersionUID = -7046029254386353129L;
		protected final Short2CharFunction function;
		protected final Object sync;

		protected SynchronizedFunction(final Short2CharFunction f, final Object sync) {
			if (f == null) throw new NullPointerException();
			this.function = f;
			this.sync = sync;
		}

		protected SynchronizedFunction(final Short2CharFunction f) {
			if (f == null) throw new NullPointerException();
			this.function = f;
			this.sync = this;
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public int applyAsInt(int operand) {
			synchronized (sync) {
				return function.applyAsInt(operand);
			}
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public Character apply(final Short key) {
			synchronized (sync) {
				return function.apply(key);
			}
		}

		@Override
		public int size() {
			synchronized (sync) {
				return function.size();
			}
		}

		@Override
		public char defaultReturnValue() {
			synchronized (sync) {
				return function.defaultReturnValue();
			}
		}

		@Override
		public void defaultReturnValue(final char defRetValue) {
			synchronized (sync) {
				function.defaultReturnValue(defRetValue);
			}
		}

		@Override
		public boolean containsKey(final short k) {
			synchronized (sync) {
				return function.containsKey(k);
			}
		}

		@Deprecated
		@Override
		public boolean containsKey(final Object k) {
			synchronized (sync) {
				return function.containsKey(k);
			}
		}

		@Override
		public char put(final short k, final char v) {
			synchronized (sync) {
				return function.put(k, v);
			}
		}

		@Override
		public char get(final short k) {
			synchronized (sync) {
				return function.get(k);
			}
		}

		@Override
		public char getOrDefault(final short k, final char defaultValue) {
			synchronized (sync) {
				return function.getOrDefault(k, defaultValue);
			}
		}

		@Override
		public char remove(final short k) {
			synchronized (sync) {
				return function.remove(k);
			}
		}

		@Override
		public void clear() {
			synchronized (sync) {
				function.clear();
			}
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public Character put(final Short k, final Character v) {
			synchronized (sync) {
				return function.put(k, v);
			}
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public Character get(final Object k) {
			synchronized (sync) {
				return function.get(k);
			}
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public Character getOrDefault(final Object k, final Character defaultValue) {
			synchronized (sync) {
				return function.getOrDefault(k, defaultValue);
			}
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public Character remove(final Object k) {
			synchronized (sync) {
				return function.remove(k);
			}
		}

		@Override
		public int hashCode() {
			synchronized (sync) {
				return function.hashCode();
			}
		}

		@Override
		public boolean equals(final Object o) {
			if (o == this) return true;
			synchronized (sync) {
				return function.equals(o);
			}
		}

		@Override
		public String toString() {
			synchronized (sync) {
				return function.toString();
			}
		}

		private void writeObject(java.io.ObjectOutputStream s) throws java.io.IOException {
			synchronized (sync) {
				s.defaultWriteObject();
			}
		}
	}

	/**
	 * Returns a synchronized type-specific function backed by the given type-specific function.
	 *
	 * @param f the function to be wrapped in a synchronized function.
	 * @return a synchronized view of the specified function.
	 * @see java.util.Collections#synchronizedMap(java.util.Map)
	 */
	public static Short2CharFunction synchronize(final Short2CharFunction f) {
		return new SynchronizedFunction(f);
	}

	/**
	 * Returns a synchronized type-specific function backed by the given type-specific function, using
	 * an assigned object to synchronize.
	 *
	 * @param f the function to be wrapped in a synchronized function.
	 * @param sync an object that will be used to synchronize the access to the function.
	 * @return a synchronized view of the specified function.
	 * @see java.util.Collections#synchronizedMap(java.util.Map)
	 */
	public static Short2CharFunction synchronize(final Short2CharFunction f, final Object sync) {
		return new SynchronizedFunction(f, sync);
	}

	/** An unmodifiable wrapper class for functions. */
	public static class UnmodifiableFunction extends AbstractShort2CharFunction implements java.io.Serializable {
		private static final long serialVersionUID = -7046029254386353129L;
		protected final Short2CharFunction function;

		protected UnmodifiableFunction(final Short2CharFunction f) {
			if (f == null) throw new NullPointerException();
			this.function = f;
		}

		@Override
		public int size() {
			return function.size();
		}

		@Override
		public char defaultReturnValue() {
			return function.defaultReturnValue();
		}

		@Override
		public void defaultReturnValue(final char defRetValue) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean containsKey(final short k) {
			return function.containsKey(k);
		}

		@Override
		public char put(final short k, final char v) {
			throw new UnsupportedOperationException();
		}

		@Override
		public char get(final short k) {
			return function.get(k);
		}

		@Override
		public char getOrDefault(final short k, final char defaultValue) {
			return function.getOrDefault(k, defaultValue);
		}

		@Override
		public char remove(final short k) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void clear() {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public Character put(final Short k, final Character v) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public Character get(final Object k) {
			return function.get(k);
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public Character getOrDefault(final Object k, final Character defaultValue) {
			return function.getOrDefault(k, defaultValue);
		}

		/**
		 * {@inheritDoc}
		 * 
		 * @deprecated Please use the corresponding type-specific method instead.
		 */
		@Deprecated
		@Override
		public Character remove(final Object k) {
			throw new UnsupportedOperationException();
		}

		@Override
		public int hashCode() {
			return function.hashCode();
		}

		@Override
		public boolean equals(Object o) {
			return o == this || function.equals(o);
		}

		@Override
		public String toString() {
			return function.toString();
		}
	}

	/**
	 * Returns an unmodifiable type-specific function backed by the given type-specific function.
	 *
	 * @param f the function to be wrapped in an unmodifiable function.
	 * @return an unmodifiable view of the specified function.
	 * @see java.util.Collections#unmodifiableMap(java.util.Map)
	 */
	public static Short2CharFunction unmodifiable(final Short2CharFunction f) {
		return new UnmodifiableFunction(f);
	}

	/** An adapter for mapping generic total functions to partial primitive functions. */
	public static class PrimitiveFunction implements Short2CharFunction {
		protected final java.util.function.Function<? super Short, ? extends Character> function;

		protected PrimitiveFunction(java.util.function.Function<? super Short, ? extends Character> function) {
			this.function = function;
		}

		@Override
		public boolean containsKey(short key) {
			return function.apply(Short.valueOf(key)) != null;
		}

		@Deprecated
		@Override
		public boolean containsKey(Object key) {
			if (key == null) return false;
			return function.apply((Short)(key)) != null;
		}

		@Override
		public char get(short key) {
			Character v = function.apply(Short.valueOf(key));
			if (v == null) return defaultReturnValue();
			return (v).charValue();
		}

		@Override
		public char getOrDefault(short key, char defaultValue) {
			Character v = function.apply(Short.valueOf(key));
			if (v == null) return defaultValue;
			return (v).charValue();
		}

		@Deprecated
		@Override
		public Character get(Object key) {
			if (key == null) return null;
			return function.apply((Short)key);
		}

		@Deprecated
		@Override
		public Character getOrDefault(Object key, Character defaultValue) {
			if (key == null) return defaultValue;
			final Character v;
			return (v = function.apply((Short)key)) == null ? defaultValue : v;
		}

		@Deprecated
		@Override
		public Character put(final Short key, final Character value) {
			throw new UnsupportedOperationException();
		}
	}

	/**
	 * Returns a (partial) type-specific function based on the given total generic function.
	 * <p>
	 * The returned function contains all keys which are not mapped to {@code null}. If the function
	 * already is a primitive function, it is returned without changes.
	 * <p>
	 * <strong>Warning</strong>: If the given function is a &ldquo;widened&rdquo; primitive function
	 * (e.g. an {@code Int2IntFunction} given to {@code Short2ShortFunctions}), it still is wrapped into
	 * a proxy, decreasing performance.
	 *
	 * @param f the function to be converted to a type-specific function.
	 * @return a primitive view of the specified function.
	 * @throws NullPointerException if {@code f} is null.
	 * @see PrimitiveFunction
	 * @since 8.1.0
	 */

	public static Short2CharFunction primitive(final java.util.function.Function<? super Short, ? extends Character> f) {
		java.util.Objects.requireNonNull(f);
		if (f instanceof Short2CharFunction) return (Short2CharFunction)f;
		if (f instanceof java.util.function.IntUnaryOperator) return key -> it.unimi.dsi.fastutil.SafeMath.safeIntToChar(((java.util.function.IntUnaryOperator)f).applyAsInt(key));
		return new PrimitiveFunction(f);
	}
}
