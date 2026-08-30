package org.apache.commons.io.function;

import java.io.IOException;
import java.io.UncheckedIOException;

public class Uncheck {

	public static <T> T get(final IOSupplier<T> supplier) {
		try {
			return supplier.get();
		} catch (final IOException e) {
			throw wrap(e);
		}
	}

	private static UncheckedIOException wrap(final IOException e) {
		return new UncheckedIOException(e);
	}

}
