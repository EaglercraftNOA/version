// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT license.
package com.mojang.serialization;

import com.mojang.datafixers.util.Pair;

import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public final class OptionalDynamic<T> extends DynamicLike<T> {
    private final DataResult<Dynamic<T>> delegate;

    public OptionalDynamic(final DynamicOps<T> ops, final DataResult<Dynamic<T>> delegate) {
        super(ops);
        this.delegate = delegate;
    }

    public DataResult<Dynamic<T>> get() {
        return delegate;
    }

    public Optional<Dynamic<T>> result() {
        return delegate.result();
    }

    public boolean isPresent() {
        return result().isPresent();
    }

    public Dynamic<T> orElse(final Dynamic<T> other) {
        return result().orElse(other);
    }

    public Dynamic<T> orElseGet(final java.util.function.Supplier<? extends Dynamic<T>> other) {
        return result().orElseGet(other);
    }

    public <U> Optional<U> map(final Function<? super Dynamic<T>, U> mapper) {
        return result().map(mapper);
    }

    public <U> Optional<U> flatMap(final Function<? super Dynamic<T>, Optional<U>> mapper) {
        return result().flatMap(mapper);
    }

    private <U> DataResult<U> flatMapResult(final Function<? super Dynamic<T>, ? extends DataResult<U>> mapper) {
        return delegate.flatMap(mapper);
    }

    @Override
    public DataResult<Number> asNumber() {
        return flatMapResult(DynamicLike::asNumber);
    }

    @Override
    public DataResult<String> asString() {
        return flatMapResult(DynamicLike::asString);
    }

    @Override
    public DataResult<Stream<Dynamic<T>>> asStreamOpt() {
        return flatMapResult(DynamicLike::asStreamOpt);
    }

    @Override
    public DataResult<Stream<Pair<Dynamic<T>, Dynamic<T>>>> asMapOpt() {
        return flatMapResult(DynamicLike::asMapOpt);
    }

    @Override
    public DataResult<ByteBuffer> asByteBufferOpt() {
        return flatMapResult(DynamicLike::asByteBufferOpt);
    }

    @Override
    public DataResult<IntStream> asIntStreamOpt() {
        return flatMapResult(DynamicLike::asIntStreamOpt);
    }

    @Override
    public DataResult<LongStream> asLongStreamOpt() {
        return flatMapResult(DynamicLike::asLongStreamOpt);
    }

    @Override
    public OptionalDynamic<T> get(final String key) {
        return new OptionalDynamic<>(ops, delegate.flatMap(k -> k.get(key).delegate));
    }

    @Override
    public DataResult<T> getGeneric(final T key) {
        return flatMapResult(v -> v.getGeneric(key));
    }

    @Override
    public DataResult<T> getElement(final String key) {
        return flatMapResult(v -> v.getElement(key));
    }

    @Override
    public DataResult<T> getElementGeneric(final T key) {
        return flatMapResult(v -> v.getElementGeneric(key));
    }

    public Dynamic<T> orElseEmptyMap() {
        return result().orElseGet(this::emptyMap);
    }

    public Dynamic<T> orElseEmptyList() {
        return result().orElseGet(this::emptyList);
    }

    public <V> DataResult<V> into(final Function<? super Dynamic<T>, ? extends V> action) {
        return delegate.map(action);
    }

    @Override
    public <A> DataResult<Pair<A, T>> decode(final Decoder<? extends A> decoder) {
        return delegate.flatMap(t -> t.decode(decoder));
    }
}
