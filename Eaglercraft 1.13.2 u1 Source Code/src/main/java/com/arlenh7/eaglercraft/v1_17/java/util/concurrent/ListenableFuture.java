package com.arlenh7.eaglercraft.v1_17.java.util.concurrent;

import java.util.concurrent.Executor;

public interface ListenableFuture<V> extends Future<V> {
   void addListener(Runnable listener, Executor executor);
}
