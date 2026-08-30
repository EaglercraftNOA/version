/*
 * Copyright (C) 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.gson.internal;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.google.gson.InstanceCreator;
import com.google.gson.JsonIOException;
import com.google.gson.reflect.TypeToken;

/**
 * Returns a function that can construct an instance of a requested type.
 */
public final class ConstructorConstructor {
  private final Map<Object, InstanceCreator<?>> instanceCreators;

  public ConstructorConstructor(Map<Object, InstanceCreator<?>> instanceCreators) {
    this.instanceCreators = instanceCreators;
  }

  public <T> ObjectConstructor<T> get(TypeToken<T> typeToken) {
    final Object type = typeToken.getType();
    final Class<? super T> rawType = typeToken.getRawType();

    // first try an instance creator

    @SuppressWarnings("unchecked") // types must agree
    final InstanceCreator<T> typeCreator = (InstanceCreator<T>) instanceCreators.get(type);
    if (typeCreator != null) {
      return new ObjectConstructor<T>() {
        @Override public T construct() {
          return typeCreator.createInstance(type);
        }
      };
    }

    // Next try raw Object match for instance creators
    @SuppressWarnings("unchecked") // types must agree
    final InstanceCreator<T> rawTypeCreator =
        (InstanceCreator<T>) instanceCreators.get(rawType);
    if (rawTypeCreator != null) {
      return new ObjectConstructor<T>() {
        @Override public T construct() {
          return rawTypeCreator.createInstance(type);
        }
      };
    }

    ObjectConstructor<T> defaultImplementation = newDefaultImplementationConstructor(rawType);
    if (defaultImplementation != null) {
      return defaultImplementation;
    }

    throw new JsonIOException("No TeaVM-safe object constructor for " + type
        + "; register an InstanceCreator or TypeAdapter for this type.");
  }

  /**
   * Constructors for common interface types like Map and List and their
   * subtypes.
   */
  @SuppressWarnings("unchecked") // use runtime checks to guarantee that 'T' is what it is
  private <T> ObjectConstructor<T> newDefaultImplementationConstructor(Class<? super T> rawType) {
    if (Collection.class.isAssignableFrom(rawType)) {
      if (SortedSet.class.isAssignableFrom(rawType)) {
        return new ObjectConstructor<T>() {
          @Override public T construct() {
            return (T) new TreeSet<Object>();
          }
        };
      } else if (Set.class.isAssignableFrom(rawType)) {
        return new ObjectConstructor<T>() {
          @Override public T construct() {
            return (T) new LinkedHashSet<Object>();
          }
        };
      } else if (Queue.class.isAssignableFrom(rawType)) {
        return new ObjectConstructor<T>() {
          @Override public T construct() {
            return (T) new ArrayDeque<Object>();
          }
        };
      } else {
        return new ObjectConstructor<T>() {
          @Override public T construct() {
            return (T) new ArrayList<Object>();
          }
        };
      }
    }

    if (Map.class.isAssignableFrom(rawType)) {
      if (ConcurrentMap.class.isAssignableFrom(rawType)) {
        return new ObjectConstructor<T>() {
          @Override public T construct() {
            return (T) new ConcurrentHashMap<Object, Object>();
          }
        };
      } else if (SortedMap.class.isAssignableFrom(rawType)) {
        return new ObjectConstructor<T>() {
          @Override public T construct() {
            return (T) new TreeMap<Object, Object>();
          }
        };
      } else if (LinkedHashMap.class.isAssignableFrom(rawType)) {
        return new ObjectConstructor<T>() {
          @Override public T construct() {
            return (T) new LinkedHashMap<Object, Object>();
          }
        };
      } else {
        return new ObjectConstructor<T>() {
          @Override public T construct() {
            return (T) new LinkedTreeMap<String, Object>();
          }
        };
      }
    }

    return null;
  }

  @Override public String toString() {
    return instanceCreators.toString();
  }
}
