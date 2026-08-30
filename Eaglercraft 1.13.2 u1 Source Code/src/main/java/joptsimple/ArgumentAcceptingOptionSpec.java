package joptsimple;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ArgumentAcceptingOptionSpec<T> extends OptionSpec<T> {

	ArgumentAcceptingOptionSpec(String name, Class<?> type, List<T> defaults) {
		super(name, false, type, defaults, false);
	}

	@SuppressWarnings("unchecked")
	public <U> ArgumentAcceptingOptionSpec<U> withRequiredArg() {
		this.acceptsArguments = true;
		return (ArgumentAcceptingOptionSpec<U>) this;
	}

	@SuppressWarnings("unchecked")
	public <U> ArgumentAcceptingOptionSpec<U> ofType(Class<U> type) {
		this.type = type;
		if (!this.defaults.isEmpty()) {
			List<U> convertedDefaults = new ArrayList<>(this.defaults.size());
			for (T value : this.defaults) {
				convertedDefaults.add((U) convert(value == null ? null : value.toString(), type));
			}
			this.defaults = (List<T>) convertedDefaults;
		}
		return (ArgumentAcceptingOptionSpec<U>) this;
	}

	@SuppressWarnings("unchecked")
	public <U> ArgumentAcceptingOptionSpec<U> defaultsTo(U value) {
		this.defaults = Collections.singletonList((T) convert(value == null ? null : value.toString(), this.type));
		return (ArgumentAcceptingOptionSpec<U>) this;
	}

	@SuppressWarnings("unchecked")
	public <U> ArgumentAcceptingOptionSpec<U> required() {
		return (ArgumentAcceptingOptionSpec<U>) this;
	}

	public List<T> defaultValues() {
		return this.defaults;
	}

	@SuppressWarnings("unchecked")
	T convertValue(String value) {
		return (T) convert(value, this.type);
	}

	static Object convert(String value, Class<?> type) {
		if (type == null || type == String.class) {
			return value;
		}
		if (type == Integer.class || type == Integer.TYPE) {
			return Integer.valueOf(value);
		}
		if (type == Long.class || type == Long.TYPE) {
			return Long.valueOf(value);
		}
		if (type == Boolean.class || type == Boolean.TYPE) {
			return Boolean.valueOf(value);
		}
		if (type == File.class) {
			return new File(value);
		}
		throw new IllegalArgumentException("Unsupported option type: " + type);
	}
}
