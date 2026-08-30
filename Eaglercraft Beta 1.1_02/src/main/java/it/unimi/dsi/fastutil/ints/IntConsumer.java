package it.unimi.dsi.fastutil.ints;

import java.util.Objects;
import java.util.function.Consumer;

@FunctionalInterface
public interface IntConsumer extends java.util.function.IntConsumer, Consumer<Integer> {
	@Deprecated
	@Override
	default void accept(Integer t) {
		accept(t.intValue());
	}

	default IntConsumer andThen(IntConsumer after) {
		Objects.requireNonNull(after);
		return t -> {
			accept(t);
			after.accept(t);
		};
	}

	@Deprecated
	@Override
	default Consumer<Integer> andThen(Consumer<? super Integer> after) {
		return t -> {
			this.accept(t);
			after.accept(t);
		};
	}
}