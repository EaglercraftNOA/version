/*
 * Copyright (C) 2013 The Guava Authors
 */
package com.google.common.eventbus;

import static com.google.common.base.Preconditions.checkNotNull;

public class SubscriberExceptionContext {
  private final EventBus eventBus;
  private final Object event;
  private final Object subscriber;
  private final String subscriberMethod;

  SubscriberExceptionContext(
      EventBus eventBus, Object event, Object subscriber, String subscriberMethod) {
    this.eventBus = checkNotNull(eventBus);
    this.event = checkNotNull(event);
    this.subscriber = checkNotNull(subscriber);
    this.subscriberMethod = checkNotNull(subscriberMethod);
  }

  public EventBus getEventBus() {
    return eventBus;
  }

  public Object getEvent() {
    return event;
  }

  public Object getSubscriber() {
    return subscriber;
  }

  public String getSubscriberMethod() {
    return subscriberMethod;
  }
}
