package org.apache.logging.log4j.util;

@FunctionalInterface
public interface Supplier<T> {
	T get();
}
