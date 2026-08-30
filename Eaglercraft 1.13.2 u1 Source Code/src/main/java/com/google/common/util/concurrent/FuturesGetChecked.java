/*
 * Copyright (C) 2006 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0.
 */

package com.google.common.util.concurrent;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.Thread.currentThread;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Static methods used to implement {@link Futures#getChecked(Future, Class)}.
 */
@GwtIncompatible
final class FuturesGetChecked {
  @CanIgnoreReturnValue
  static <V, X extends Exception> V getChecked(Future<V> future, Class<X> exceptionClass) throws X {
    return getChecked(bestGetCheckedTypeValidator(), future, exceptionClass);
  }

  @CanIgnoreReturnValue
  @VisibleForTesting
  static <V, X extends Exception> V getChecked(
      GetCheckedTypeValidator validator, Future<V> future, Class<X> exceptionClass) throws X {
    validator.validateClass(exceptionClass);
    try {
      return future.get();
    } catch (InterruptedException e) {
      currentThread().interrupt();
      throw newWithCause(exceptionClass, e);
    } catch (ExecutionException e) {
      wrapAndThrowExceptionOrError(e.getCause(), exceptionClass);
      throw new AssertionError();
    }
  }

  @CanIgnoreReturnValue
  static <V, X extends Exception> V getChecked(
      Future<V> future, Class<X> exceptionClass, long timeout, TimeUnit unit) throws X {
    bestGetCheckedTypeValidator().validateClass(exceptionClass);
    try {
      return future.get(timeout, unit);
    } catch (InterruptedException e) {
      currentThread().interrupt();
      throw newWithCause(exceptionClass, e);
    } catch (TimeoutException e) {
      throw newWithCause(exceptionClass, e);
    } catch (ExecutionException e) {
      wrapAndThrowExceptionOrError(e.getCause(), exceptionClass);
      throw new AssertionError();
    }
  }

  @VisibleForTesting
  interface GetCheckedTypeValidator {
    void validateClass(Class<? extends Exception> exceptionClass);
  }

  private static GetCheckedTypeValidator bestGetCheckedTypeValidator() {
    return GetCheckedTypeValidatorHolder.WeakSetValidator.INSTANCE;
  }

  @VisibleForTesting
  static GetCheckedTypeValidator weakSetValidator() {
    return GetCheckedTypeValidatorHolder.WeakSetValidator.INSTANCE;
  }

  @VisibleForTesting
  static GetCheckedTypeValidator classValueValidator() {
    return weakSetValidator();
  }

  @VisibleForTesting
  static class GetCheckedTypeValidatorHolder {
    enum WeakSetValidator implements GetCheckedTypeValidator {
      INSTANCE;

      @Override
      public void validateClass(Class<? extends Exception> exceptionClass) {
        checkExceptionClassValidity(exceptionClass);
      }
    }
  }

  private static <X extends Exception> void wrapAndThrowExceptionOrError(
      Throwable cause, Class<X> exceptionClass) throws X {
    if (cause instanceof Error) {
      throw new ExecutionError((Error) cause);
    }
    if (cause instanceof RuntimeException) {
      throw new UncheckedExecutionException(cause);
    }
    throw newWithCause(exceptionClass, cause);
  }

  @SuppressWarnings("unchecked")
  private static <X extends Exception> X newWithCause(Class<X> exceptionClass, Throwable cause) {
    if (exceptionClass == Exception.class) {
      return (X) new Exception(cause);
    }
    if (exceptionClass == ExecutionException.class) {
      return (X) new ExecutionException(cause);
    }
    if (exceptionClass == TimeoutException.class) {
      TimeoutException exception = new TimeoutException(cause.toString());
      exception.initCause(cause);
      return (X) exception;
    }
    if (exceptionClass == InterruptedException.class) {
      InterruptedException exception = new InterruptedException(cause.toString());
      exception.initCause(cause);
      return (X) exception;
    }
    return (X) new CheckedFutureException(cause);
  }

  @VisibleForTesting
  static boolean isCheckedException(Class<? extends Exception> type) {
    return !RuntimeException.class.isAssignableFrom(type);
  }

  @VisibleForTesting
  static void checkExceptionClassValidity(Class<? extends Exception> exceptionClass) {
    checkArgument(
        isCheckedException(exceptionClass),
        "Futures.getChecked exception type (%s) must not be a RuntimeException",
        exceptionClass);
  }

  private static final class CheckedFutureException extends Exception {
    CheckedFutureException(Throwable cause) {
      super(cause);
    }
  }

  private FuturesGetChecked() {}
}
