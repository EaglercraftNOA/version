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

/**
 * Do sneaky things to allocate objects without invoking their constructors.
 *
 * @author Joel Leitch
 * @author Jesse Wilson
 */
public abstract class UnsafeAllocator {
  public abstract <T> T newInstance(Class<T> c) throws Exception;

  public static UnsafeAllocator create() {
    return new UnsafeAllocator() {
      @Override
      public <T> T newInstance(Class<T> c) {
        assertInstantiable(c);
        throw new UnsupportedOperationException("Cannot allocate " + c);
      }
    };
  }

  /**
   * Check if the class can be instantiated by unsafe allocator. If the instance has interface or abstract modifiers
   * throw an {@link java.lang.UnsupportedOperationException}
   * @param c instance of the class to be checked
   */
  private static void assertInstantiable(Class<?> c) {
    int modifiers = c.getModifiers();
    if ((modifiers & 0x200) != 0) {
      throw new UnsupportedOperationException("Interface can't be instantiated! Interface name: " + c.getName());
    }
    if ((modifiers & 0x400) != 0) {
      throw new UnsupportedOperationException("Abstract class can't be instantiated! Class name: " + c.getName());
    }
  }
}
