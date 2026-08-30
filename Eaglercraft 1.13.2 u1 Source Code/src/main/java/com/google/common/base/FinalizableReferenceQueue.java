/*
 * Copyright (C) 2007 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0.
 */

package com.google.common.base;

import com.google.common.annotations.GwtIncompatible;
import java.io.Closeable;
import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Reference queue that performs cleanup from allocation/close paths.
 */
@GwtIncompatible
public class FinalizableReferenceQueue implements Closeable {
  private static final Logger logger = Logger.getLogger(FinalizableReferenceQueue.class.getName());

  final ReferenceQueue<Object> queue;
  final PhantomReference<Object> frqRef;
  final boolean threadStarted;

  public FinalizableReferenceQueue() {
    queue = new ReferenceQueue<Object>();
    frqRef = new PhantomReference<Object>(this, queue);
    threadStarted = false;
  }

  @Override
  public void close() {
    frqRef.enqueue();
    cleanUp();
  }

  void cleanUp() {
    Reference<?> reference;
    while ((reference = queue.poll()) != null) {
      reference.clear();
      if (reference == frqRef) {
        continue;
      }
      try {
        ((FinalizableReference) reference).finalizeReferent();
      } catch (Throwable t) {
        logger.log(Level.SEVERE, "Error cleaning up after reference.", t);
      }
    }
  }
}
