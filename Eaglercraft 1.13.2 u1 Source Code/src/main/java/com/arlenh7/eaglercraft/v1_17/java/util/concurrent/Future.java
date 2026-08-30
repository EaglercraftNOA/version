package com.arlenh7.eaglercraft.v1_17.java.util.concurrent;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public interface Future<V> {
   boolean cancel(boolean mayInterruptIfRunning);

   boolean isCancelled();

   boolean isDone();

   V get() throws InterruptedException, ExecutionException;

   V get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException;
}
