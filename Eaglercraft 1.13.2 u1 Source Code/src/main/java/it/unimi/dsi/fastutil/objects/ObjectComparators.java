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

import java.util.Comparator;

/**
 * A class providing static methods and objects that do useful things with comparators.
 */
public final class ObjectComparators {
	private ObjectComparators() {
	}

	/** A type-specific comparator mimicking the natural order. */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected static class NaturalImplicitComparator implements Comparator, java.io.Serializable {
		private static final long serialVersionUID = 1L;

		@Override
		public final int compare(final Object a, final Object b) {
			return ((Comparable)a).compareTo(b);
		}

		@Override
		public Comparator reversed() {
			return OPPOSITE_COMPARATOR;
		}

		private Object readResolve() {
			return NATURAL_COMPARATOR;
		}
	};

	@SuppressWarnings("rawtypes")
	public static final Comparator NATURAL_COMPARATOR = new NaturalImplicitComparator();

	/** A type-specific comparator mimicking the opposite of the natural order. */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected static class OppositeImplicitComparator implements Comparator, java.io.Serializable {
		private static final long serialVersionUID = 1L;

		@Override
		public final int compare(final Object a, final Object b) {
			return ((Comparable)b).compareTo(a);
		}

		@Override
		public Comparator reversed() {
			return NATURAL_COMPARATOR;
		}

		private Object readResolve() {
			return OPPOSITE_COMPARATOR;
		}
	};

	@SuppressWarnings("rawtypes")
	public static final Comparator OPPOSITE_COMPARATOR = new OppositeImplicitComparator();

	protected static class OppositeComparator<K> implements Comparator<K>, java.io.Serializable {
		private static final long serialVersionUID = 1L;
		final Comparator<K> comparator;

		protected OppositeComparator(final Comparator<K> c) {
			comparator = c;
		}

		@Override
		public final int compare(final K a, final K b) {
			return comparator.compare(b, a);
		}

		@Override
		public final Comparator<K> reversed() {
			return comparator;
		}
	};

	/**
	 * Returns a comparator representing the opposite order of the given comparator.
	 *
	 * @param c a comparator.
	 * @return a comparator representing the opposite order of {@code c}.
	 */
	public static <K> Comparator<K> oppositeComparator(final Comparator<K> c) {
		if (c instanceof OppositeComparator) return ((OppositeComparator<K>)c).comparator;
		return new OppositeComparator<>(c);
	}

	/**
	 * Returns a the comparator given unmodified.
	 *
	 * This method merely serves as a way to be compatible with primtive type-specific Comparators
	 * implementations, as they do have type-specific Comparators, but Object ones do not.
	 *
	 * @param c a comparator, or {@code null}.
	 * @return {@code c}, unmodified.
	 */
	public static <K> Comparator<K> asObjectComparator(final Comparator<K> c) {
		return c;
	}
}
