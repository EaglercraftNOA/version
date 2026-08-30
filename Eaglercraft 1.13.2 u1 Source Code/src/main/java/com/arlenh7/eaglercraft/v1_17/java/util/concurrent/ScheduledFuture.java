package com.arlenh7.eaglercraft.v1_17.java.util.concurrent;

import java.util.concurrent.TimeUnit;

public interface ScheduledFuture<V> extends Future<V> {
   long getDelay(TimeUnit unit);
}
