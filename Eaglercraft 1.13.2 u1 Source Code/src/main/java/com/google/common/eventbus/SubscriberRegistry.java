/*
 * Copyright (C) 2014 The Guava Authors
 */
package com.google.common.eventbus;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableSet;
import com.google.j2objc.annotations.Weak;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

final class SubscriberRegistry {
  private final CopyOnWriteArraySet<Subscriber> subscribers = new CopyOnWriteArraySet<Subscriber>();

  @Weak private final EventBus bus;

  SubscriberRegistry(EventBus bus) {
    this.bus = checkNotNull(bus);
  }

  void register(Object listener) {
    if (listener instanceof EventBus.Listener) {
      subscribers.add(Subscriber.create(bus, listener, (EventBus.Listener) listener));
    }
  }

  void unregister(Object listener) {
    for (Subscriber subscriber : subscribers) {
      if (subscriber.target == listener) {
        subscribers.remove(subscriber);
      }
    }
  }

  @VisibleForTesting
  Set<Subscriber> getSubscribersForTesting(Object ignored) {
    return ImmutableSet.copyOf(subscribers);
  }

  Iterator<Subscriber> getSubscribers(Object event) {
    return subscribers.iterator();
  }
}
