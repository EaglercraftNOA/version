/*
 * Copyright (C) 2006 The Guava Authors
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

package com.google.common.util.concurrent;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * A TimeLimiter that runs method calls in the background using an {@link ExecutorService}. If the
 * time limit expires for a given method call, the thread running the call will be interrupted.
 *
 * @author Kevin Bourrillion
 * @since 1.0
 */
@Beta
@GwtIncompatible
public final class SimpleTimeLimiter implements TimeLimiter {

  private final ExecutorService executor;

  /**
   * Constructs a TimeLimiter instance using the given executor service to execute proxied method
   * calls.
   *
   * <p><b>Warning:</b> using a bounded executor may be counterproductive! If the thread pool fills
   * up, any time callers spend waiting for a thread may count toward their time limit, and in this
   * case the call may even time out before the target method is ever invoked.
   *
   * @param executor the ExecutorService that will execute the method calls on the target objects;
   *     for example, a {@link Executors#newCachedThreadPool()}.
   */
  public SimpleTimeLimiter(ExecutorService executor) {
    this.executor = checkNotNull(executor);
  }

  /**
   * Constructs a TimeLimiter instance using a {@link Executors#newCachedThreadPool()} to execute
   * proxied method calls.
   *
   * <p><b>Warning:</b> using a bounded executor may be counterproductive! If the thread pool fills
   * up, any time callers spend waiting for a thread may count toward their time limit, and in this
   * case the call may even time out before the target method is ever invoked.
   */
  public SimpleTimeLimiter() {
    this(Executors.newCachedThreadPool());
  }

  @Override
  public <T> T newProxy(
      final T target,
      Class<T> interfaceType,
      final long timeoutDuration,
      final TimeUnit timeoutUnit) {
    checkNotNull(target);
    checkNotNull(interfaceType);
    checkNotNull(timeoutUnit);
    checkArgument(timeoutDuration > 0, "bad timeout: %s", timeoutDuration);
    throw new UnsupportedOperationException("Dynamic proxies are not available in the TeaVM runtime");
  }

  // TODO: should this actually throw only ExecutionException?
  @CanIgnoreReturnValue
  @Override
  public <T> T callWithTimeout(
      Callable<T> callable, long timeoutDuration, TimeUnit timeoutUnit, boolean amInterruptible)
      throws Exception {
    checkNotNull(callable);
    checkNotNull(timeoutUnit);
    checkArgument(timeoutDuration > 0, "timeout must be positive: %s", timeoutDuration);
    Future<T> future = executor.submit(callable);
    try {
      if (amInterruptible) {
        try {
          return future.get(timeoutDuration, timeoutUnit);
        } catch (InterruptedException e) {
          future.cancel(true);
          throw e;
        }
      } else {
        boolean interrupted = false;
        try {
          long remainingNanos = timeoutUnit.toNanos(timeoutDuration);
          long end = System.nanoTime() + remainingNanos;
          while (true) {
            try {
              return future.get(remainingNanos, java.util.concurrent.TimeUnit.NANOSECONDS);
            } catch (InterruptedException e) {
              interrupted = true;
              remainingNanos = end - System.nanoTime();
            }
          }
        } finally {
          if (interrupted) {
            Thread.currentThread().interrupt();
          }
        }
      }
    } catch (ExecutionException e) {
      throw throwCause(e, true);
    } catch (TimeoutException e) {
      future.cancel(true);
      throw new UncheckedTimeoutException(e);
    }
  }

  private static Exception throwCause(Exception e, boolean combineStackTraces) throws Exception {
    Throwable cause = e.getCause();
    if (cause == null) {
      throw e;
    }
    if (combineStackTraces) {
      StackTraceElement[] causeTrace = cause.getStackTrace();
      StackTraceElement[] outerTrace = e.getStackTrace();
      StackTraceElement[] combined = new StackTraceElement[causeTrace.length + outerTrace.length];
      System.arraycopy(causeTrace, 0, combined, 0, causeTrace.length);
      System.arraycopy(outerTrace, 0, combined, causeTrace.length, outerTrace.length);
      cause.setStackTrace(combined);
    }
    if (cause instanceof Exception) {
      throw (Exception) cause;
    }
    if (cause instanceof Error) {
      throw (Error) cause;
    }
    // The cause is a weird kind of Throwable, so throw the outer exception.
    throw e;
  }

}
