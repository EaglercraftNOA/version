package joptsimple;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.LinkedHashMap;
import java.util.Map;

public class OptionParser {
	private final Map<String, ArgumentAcceptingOptionSpec<?>> specs = new LinkedHashMap<>();
	private OptionSpec<String> nonOptions;

	public void allowsUnrecognizedOptions() {
	}

	public OptionSpecBuilder accepts(String name) {
		OptionSpecBuilder spec = new OptionSpecBuilder(name);
		specs.put(name, spec);
		return spec;
	}

	public OptionSpecBuilder accepts(String name, String description) {
		return accepts(name);
	}

	public OptionSpec<String> nonOptions() {
		this.nonOptions = new OptionSpec<>("<non-options>", true, String.class, null, true);
		return this.nonOptions;
	}

	public OptionSet parse(String[] args) {
		OptionSet set = new OptionSet();
		for (int i = 0; i < args.length; ++i) {
			String arg = args[i];
			if (arg.startsWith("--")) {
				String name = arg.substring(2);
				String value = null;
				int eq = name.indexOf('=');
				if (eq >= 0) {
					value = name.substring(eq + 1);
					name = name.substring(0, eq);
				}
				ArgumentAcceptingOptionSpec<?> spec = specs.get(name);
				if (spec == null) {
					continue;
				}
				if (spec.acceptsArguments) {
					if (value == null && i + 1 < args.length) {
						value = args[++i];
					}
					set.add(spec, spec.convertValue(value));
				} else {
					set.add(spec, Boolean.TRUE);
				}
			} else if (this.nonOptions != null) {
				set.add(this.nonOptions, arg);
			}
		}
		return set;
	}

	public void printHelpOn(OutputStream out) throws IOException {
		PrintStream printStream = out instanceof PrintStream ? (PrintStream) out : new PrintStream(out);
		for (String name : specs.keySet()) {
			printStream.println("--" + name);
		}
	}
}
