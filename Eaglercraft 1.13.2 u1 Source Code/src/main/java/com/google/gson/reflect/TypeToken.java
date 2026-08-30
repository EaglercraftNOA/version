/*
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */

package com.google.gson.reflect;

import com.google.gson.internal.$Gson$Preconditions;
import java.lang.reflect.Type;

/**
 * TeaVM-safe type token. Generic reflection is deliberately ignored; adapters
 * are matched by raw class only so reflection metadata can be pruned.
 */
public class TypeToken<T> {
  final Class<? super T> rawType;
  final Type type;
  final int hashCode;

  @SuppressWarnings("unchecked")
  protected TypeToken() {
    this((Class<? super T>) Object.class);
  }

  @SuppressWarnings("unchecked")
  TypeToken(Object type) {
    Object checked = $Gson$Preconditions.checkNotNull(type);
    this.rawType = checked instanceof Class ? (Class<? super T>) checked : (Class<? super T>) Object.class;
    this.type = checked instanceof Type ? (Type)checked : this.rawType;
    this.hashCode = this.rawType.hashCode();
  }

  public final Class<? super T> getRawType() {
    return rawType;
  }

  public final Type getType() {
    return type;
  }

  @Deprecated
  public boolean isAssignableFrom(Class<?> cls) {
    return cls != null && rawType.isAssignableFrom(cls);
  }

  @Deprecated
  public boolean isAssignableFrom(Object from) {
    if (from instanceof TypeToken<?>) {
      return isAssignableFrom(((TypeToken<?>) from).getRawType());
    }
    return from instanceof Class<?> && isAssignableFrom((Class<?>) from);
  }

  @Deprecated
  public boolean isAssignableFrom(TypeToken<?> token) {
    return token != null && isAssignableFrom(token.getRawType());
  }

  @Override
  public final int hashCode() {
    return hashCode;
  }

  @Override
  public final boolean equals(Object o) {
    return o instanceof TypeToken<?> && rawType == ((TypeToken<?>) o).rawType;
  }

  @Override
  public final String toString() {
    return rawType.getName();
  }

  public static TypeToken<?> get(Object type) {
    return new TypeToken<Object>(type);
  }

  public static <T> TypeToken<T> get(Class<T> type) {
    return new TypeToken<T>(type);
  }

  public static TypeToken<?> getParameterized(Object rawType, Object... typeArguments) {
    return get(rawType);
  }

  public static TypeToken<?> getArray(Object componentType) {
    return get(Object[].class);
  }
}
