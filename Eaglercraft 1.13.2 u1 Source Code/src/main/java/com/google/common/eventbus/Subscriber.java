/*
 * Copyright (C) 2014 The Guava Authors
 */
package com.google.common.eventbus;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.annotations.VisibleForTesting;
import com.google.j2objc.annotations.Weak;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;

class Subscriber {
  static Subscriber create(EventBus bus, Object listener, EventBus.Listener handler) {
    return new Subscriber(bus, listener, handler);
  }

  @Weak private EventBus bus;

  @VisibleForTesting final Object target;
  private final EventBus.Listener handler;
  private final Executor executor;

  private Subscriber(EventBus bus, Object target, EventBus.Listener handler) {
    this.bus = bus;
    this.target = checkNotNull(target);
    this.handler = checkNotNull(handler);
    this.executor = bus.executor();
  }

  final void dispatchEvent(final Object event) {
    executor.execute(
        new Runnable() {
          @Override
          public void run() {
            try {
              handler.handleEvent(checkNotNull(event));
            } catch (Throwable e) {
              bus.handleSubscriberException(e, context(event));
            }
          }
        });
  }

  private SubscriberExceptionContext context(Object event) {
    return new SubscriberExceptionContext(bus, event, target, "handleEvent");
  }

  @Override
  public final int hashCode() {
    return System.identityHashCode(target);
  }

  @Override
  public final boolean equals(@Nullable Object obj) {
    return obj instanceof Subscriber && target == ((Subscriber) obj).target;
  }
}
