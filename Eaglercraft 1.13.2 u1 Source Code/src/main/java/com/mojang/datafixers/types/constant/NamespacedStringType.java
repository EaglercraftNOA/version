package com.mojang.datafixers.types.constant;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TypeTemplate;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import java.util.Optional;
import java.util.function.Function;

public final class NamespacedStringType extends Type<String> {
    public static Function<String, String> ENSURE_NAMESPACE = Function.identity();

    @Override
    public String toString() {
        return "NamespacedString";
    }

    @Override
    public Optional<String> point(final DynamicOps<?> ops) {
        return Optional.empty();
    }

    @Override
    public boolean equals(final Object o, final boolean ignoreRecursionPoints, final boolean checkIndex) {
        return this == o;
    }

    @Override
    public TypeTemplate buildTemplate() {
        return DSL.constType(this);
    }

    @Override
    protected Codec<String> buildCodec() {
        return Codec.STRING.xmap(ENSURE_NAMESPACE, Function.identity());
    }
}
