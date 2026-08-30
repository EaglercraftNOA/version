package joptsimple;

import java.util.Collections;
import java.util.List;

public class OptionSpec<T> implements AbstractOptionSpec<T> {
	final String name;
	boolean acceptsArguments;
	Class<?> type;
	List<T> defaults;
	final boolean nonOptions;
	boolean help;

	OptionSpec(String name, boolean acceptsArguments, Class<?> type, List<T> defaults, boolean nonOptions) {
		this.name = name;
		this.acceptsArguments = acceptsArguments;
		this.type = type;
		this.defaults = defaults == null ? Collections.emptyList() : defaults;
		this.nonOptions = nonOptions;
	}

	public T value(OptionSet set) {
		return set.valueOf(this);
	}

	@SuppressWarnings("unchecked")
	public <U> OptionSpec<U> forHelp() {
		this.help = true;
		return (OptionSpec<U>) this;
	}
}
