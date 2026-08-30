/*
 * Copyright (C) 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.gson.internal.bind;

import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

final class TypeAdapterRuntimeTypeWrapper<T> extends TypeAdapter<T> {
  private final Gson context;
  private final TypeAdapter<T> delegate;
  private final Object type;

  TypeAdapterRuntimeTypeWrapper(Gson context, TypeAdapter<T> delegate, Object type) {
    this.context = context;
    this.delegate = delegate;
    this.type = type;
  }

  @Override
  public T read(JsonReader in) throws IOException {
    return delegate.read(in);
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  @Override
  public void write(JsonWriter out, T value) throws IOException {
    // Order of preference for choosing Object adapters
    // First preference: a Object adapter registered for the runtime type
    // Second preference: a Object adapter registered for the declared type
    // TeaVM does not include Gson's reflective adapter. Prefer an explicit
    // runtime adapter when one exists, otherwise keep the declared adapter.

    TypeAdapter chosen = delegate;
    Object runtimeType = getRuntimeTypeIfMoreSpecific(type, value);
    if (runtimeType != type) {
      TypeAdapter runtimeTypeAdapter = context.getAdapter(TypeToken.get(runtimeType));
      if (runtimeTypeAdapter != delegate) {
        chosen = runtimeTypeAdapter;
      }
    }
    chosen.write(out, value);
  }

  /**
   * Finds a compatible runtime Object if it is more specific
   */
  private Object getRuntimeTypeIfMoreSpecific(Object type, Object value) {
    if (value != null
        && (type == Object.class || type instanceof Class<?>)) {
      type = value.getClass();
    }
    return type;
  }
}
