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
package it.unimi.dsi.fastutil.doubles;

import java.util.Comparator;

/**
 * A class providing static methods and objects that do useful things with comparators.
 */
public final class DoubleComparators {
	private DoubleComparators() {
	}

	/** A type-specific comparator mimicking the natural order. */
	protected static class NaturalImplicitComparator implements DoubleComparator, java.io.Serializable {
		private static final long serialVersionUID = 1L;

		@Override
		public final int compare(final double a, final double b) {
			return (Double.compare((a), (b)));
		}

		@Override
		public DoubleComparator reversed() {
			return OPPOSITE_COMPARATOR;
		}

		private Object readResolve() {
			return NATURAL_COMPARATOR;
		}
	};

	public static final DoubleComparator NATURAL_COMPARATOR = new NaturalImplicitComparator();

	/** A type-specific comparator mimicking the opposite of the natural order. */
	protected static class OppositeImplicitComparator implements DoubleComparator, java.io.Serializable {
		private static final long serialVersionUID = 1L;

		@Override
		public final int compare(final double a, final double b) {
			return -(Double.compare((a), (b)));
		}

		@Override
		public DoubleComparator reversed() {
			return NATURAL_COMPARATOR;
		}

		private Object readResolve() {
			return OPPOSITE_COMPARATOR;
		}
	};

	public static final DoubleComparator OPPOSITE_COMPARATOR = new OppositeImplicitComparator();

	protected static class OppositeComparator implements DoubleComparator, java.io.Serializable {
		private static final long serialVersionUID = 1L;
		final DoubleComparator comparator;

		protected OppositeComparator(final DoubleComparator c) {
			comparator = c;
		}

		@Override
		public final int compare(final double a, final double b) {
			return comparator.compare(b, a);
		}

		@Override
		public final DoubleComparator reversed() {
			return comparator;
		}
	};

	/**
	 * Returns a comparator representing the opposite order of the given comparator.
	 *
	 * @param c a comparator.
	 * @return a comparator representing the opposite order of {@code c}.
	 */
	public static DoubleComparator oppositeComparator(final DoubleComparator c) {
		if (c instanceof OppositeComparator) return ((OppositeComparator)c).comparator;
		return new OppositeComparator(c);
	}

	/**
	 * Returns a type-specific comparator that is equivalent to the given comparator.
	 *
	 * @param c a comparator, or {@code null}.
	 * @return a type-specific comparator representing the order of {@code c}.
	 */
	public static DoubleComparator asDoubleComparator(final Comparator<? super Double> c) {
		if (c == null || c instanceof DoubleComparator) return (DoubleComparator)c;
		return new DoubleComparator() {
			@Override
			public int compare(double x, double y) {
				return c.compare(Double.valueOf(x), Double.valueOf(y));
			}

			@SuppressWarnings("deprecation")
			@Override
			public int compare(Double x, Double y) {
				return c.compare(x, y);
			}
		};
	}
}
