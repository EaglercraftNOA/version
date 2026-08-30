/*
 * Copyright (C) 2007 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.common.cache;

import com.google.common.annotations.GwtCompatible;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Eagler runtime replacement for Guava's old Unsafe-based LongAdder.
 */
@GwtCompatible(emulated = true)
final class LongAdder extends Number implements Serializable, LongAddable {
  private static final long serialVersionUID = 7249069246863182397L;

  private transient com.arlenh7.eaglercraft.v1_17.java.util.concurrent.atomic.LongAdder delegate =
      new com.arlenh7.eaglercraft.v1_17.java.util.concurrent.atomic.LongAdder();

  private com.arlenh7.eaglercraft.v1_17.java.util.concurrent.atomic.LongAdder delegate() {
    if (delegate == null) {
      delegate = new com.arlenh7.eaglercraft.v1_17.java.util.concurrent.atomic.LongAdder();
    }
    return delegate;
  }

  @Override
  public void add(long x) {
    delegate().add(x);
  }

  @Override
  public void increment() {
    delegate().increment();
  }

  public void decrement() {
    delegate().decrement();
  }

  public long sum() {
    return delegate().sum();
  }

  public void reset() {
    delegate = new com.arlenh7.eaglercraft.v1_17.java.util.concurrent.atomic.LongAdder();
  }

  public long sumThenReset() {
    long sum = sum();
    reset();
    return sum;
  }

  @Override
  public String toString() {
    return Long.toString(sum());
  }

  @Override
  public long longValue() {
    return sum();
  }

  @Override
  public int intValue() {
    return (int) sum();
  }

  @Override
  public float floatValue() {
    return (float) sum();
  }

  @Override
  public double doubleValue() {
    return (double) sum();
  }

  private void writeObject(ObjectOutputStream s) throws IOException {
    s.defaultWriteObject();
    s.writeLong(sum());
  }

  private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
    s.defaultReadObject();
    delegate = new com.arlenh7.eaglercraft.v1_17.java.util.concurrent.atomic.LongAdder();
    delegate.add(s.readLong());
  }
}
