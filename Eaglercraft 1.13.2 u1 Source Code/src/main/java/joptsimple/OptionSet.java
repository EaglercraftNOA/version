package joptsimple;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class OptionSet {
	private final Set<String> present = new HashSet<>();
	private final Map<OptionSpec<?>, List<Object>> values = new HashMap<>();

	void add(OptionSpec<?> spec, Object value) {
		if (spec.nonOptions) {
			values.computeIfAbsent(spec, (k) -> new ArrayList<>()).add(value);
		} else {
			present.add(spec.name);
			if (value != null) {
				values.computeIfAbsent(spec, (k) -> new ArrayList<>()).add(value);
			}
		}
	}

	public boolean has(String name) {
		return present.contains(name);
	}

	public boolean has(OptionSpec<?> spec) {
		return spec.nonOptions ? values.containsKey(spec) : present.contains(spec.name);
	}

	public boolean has(AbstractOptionSpec<?> spec) {
		return this.has((OptionSpec<?>)spec);
	}

	public boolean hasOptions() {
		return !present.isEmpty();
	}

	@SuppressWarnings("unchecked")
	public <T> T valueOf(OptionSpec<T> spec) {
		List<Object> list = values.get(spec);
		if (list != null && !list.isEmpty()) {
			return (T) list.get(list.size() - 1);
		}
		return spec.defaults.isEmpty() ? null : spec.defaults.get(0);
	}

	@SuppressWarnings("unchecked")
	public <T> List<T> valuesOf(OptionSpec<T> spec) {
		List<Object> list = values.get(spec);
		if (list == null) {
			return spec.defaults.isEmpty() ? Collections.emptyList() : spec.defaults;
		}
		return (List<T>) (List<?>) Collections.unmodifiableList(list);
	}
}
