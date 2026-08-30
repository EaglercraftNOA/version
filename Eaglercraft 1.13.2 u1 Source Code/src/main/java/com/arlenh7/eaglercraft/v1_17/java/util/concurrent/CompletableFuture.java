package com.arlenh7.eaglercraft.v1_17.java.util.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class CompletableFuture<T> implements Future<T> {
   private T value;
   private Throwable error;
   private boolean done;
   private boolean cancelled;
   private final List<Runnable> listeners = new ArrayList<>();

   public CompletableFuture() {
   }

   public static <U> CompletableFuture<U> completedFuture(U value) {
      CompletableFuture<U> future = new CompletableFuture<>();
      future.complete(value);
      return future;
   }

   public static <U> CompletableFuture<U> failedFuture(Throwable throwable) {
      CompletableFuture<U> future = new CompletableFuture<>();
      future.completeExceptionally(throwable);
      return future;
   }

   public static CompletableFuture<Void> allOf(CompletableFuture<?>... futures) {
      CompletableFuture<Void> result = new CompletableFuture<>();
      if (futures.length == 0) {
         result.complete(null);
         return result;
      }

      final int[] remaining = new int[]{futures.length};
      for (CompletableFuture<?> future : futures) {
         future.whenComplete((value, error) -> {
            if (result.isDone()) {
               return;
            }
            if (error != null) {
               result.completeExceptionally(error);
            } else if (--remaining[0] == 0) {
               result.complete(null);
            }
         });
      }
      return result;
   }

   public static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier) {
      return supplyAsync(supplier, Runnable::run);
   }

   public static <U> CompletableFuture<U> supplyAsync(Supplier<U> supplier, Executor executor) {
      CompletableFuture<U> future = new CompletableFuture<>();
      executor.execute(() -> {
         try {
            future.complete(supplier.get());
         } catch (Throwable throwable) {
            future.completeExceptionally(throwable);
         }
      });
      return future;
   }

   public static CompletableFuture<Void> runAsync(Runnable runnable) {
      return runAsync(runnable, Runnable::run);
   }

   public static CompletableFuture<Void> runAsync(Runnable runnable, Executor executor) {
      return supplyAsync(() -> {
         runnable.run();
         return null;
      }, executor);
   }

   public boolean complete(T value) {
      return finish(value, null, false);
   }

   public boolean completeExceptionally(Throwable throwable) {
      return finish(null, throwable, false);
   }

   private boolean finish(T value, Throwable error, boolean cancelled) {
      List<Runnable> runNow;
      synchronized (this) {
         if (this.done) {
            return false;
         }
         this.value = value;
         this.error = error;
         this.cancelled = cancelled;
         this.done = true;
         runNow = new ArrayList<>(this.listeners);
         this.listeners.clear();
      }
      for (Runnable runnable : runNow) {
         runnable.run();
      }
      return true;
   }

   private void addListener(Runnable runnable) {
      boolean runNow;
      synchronized (this) {
         runNow = this.done;
         if (!runNow) {
            this.listeners.add(runnable);
         }
      }
      if (runNow) {
         runnable.run();
      }
   }

   public <U> CompletableFuture<U> thenApply(Function<? super T, ? extends U> function) {
      return thenApplyAsync(function, Runnable::run);
   }

   public <U> CompletableFuture<U> thenApplyAsync(Function<? super T, ? extends U> function) {
      return thenApplyAsync(function, Runnable::run);
   }

   public <U> CompletableFuture<U> thenApplyAsync(Function<? super T, ? extends U> function, Executor executor) {
      CompletableFuture<U> next = new CompletableFuture<>();
      addListener(() -> executor.execute(() -> {
         try {
            next.complete(function.apply(join()));
         } catch (Throwable throwable) {
            next.completeExceptionally(unwrap(throwable));
         }
      }));
      return next;
   }

   public CompletableFuture<Void> thenAccept(Consumer<? super T> consumer) {
      return thenAcceptAsync(consumer, Runnable::run);
   }

   public CompletableFuture<Void> thenAcceptAsync(Consumer<? super T> consumer) {
      return thenAcceptAsync(consumer, Runnable::run);
   }

   public CompletableFuture<Void> thenAcceptAsync(Consumer<? super T> consumer, Executor executor) {
      return thenApplyAsync((value) -> {
         consumer.accept(value);
         return null;
      }, executor);
   }

   public CompletableFuture<Void> thenRun(Runnable runnable) {
      return thenRunAsync(runnable, Runnable::run);
   }

   public CompletableFuture<Void> thenRunAsync(Runnable runnable) {
      return thenRunAsync(runnable, Runnable::run);
   }

   public CompletableFuture<Void> thenRunAsync(Runnable runnable, Executor executor) {
      return thenApplyAsync((value) -> {
         runnable.run();
         return null;
      }, executor);
   }

   public <U> CompletableFuture<U> thenCompose(Function<? super T, CompletableFuture<U>> function) {
      return thenComposeAsync(function, Runnable::run);
   }

   public <U> CompletableFuture<U> thenComposeAsync(Function<? super T, CompletableFuture<U>> function) {
      return thenComposeAsync(function, Runnable::run);
   }

   public <U> CompletableFuture<U> thenComposeAsync(Function<? super T, CompletableFuture<U>> function, Executor executor) {
      CompletableFuture<U> next = new CompletableFuture<>();
      addListener(() -> executor.execute(() -> {
         try {
            function.apply(join()).whenComplete((value, error) -> {
               if (error != null) {
                  next.completeExceptionally(error);
               } else {
                  next.complete(value);
               }
            });
         } catch (Throwable throwable) {
            next.completeExceptionally(unwrap(throwable));
         }
      }));
      return next;
   }

   public <U, V> CompletableFuture<V> thenCombine(CompletableFuture<? extends U> other, BiFunction<? super T, ? super U, ? extends V> function) {
      return thenCombineAsync(other, function, Runnable::run);
   }

   public <U, V> CompletableFuture<V> thenCombineAsync(CompletableFuture<? extends U> other, BiFunction<? super T, ? super U, ? extends V> function) {
      return thenCombineAsync(other, function, Runnable::run);
   }

   public <U, V> CompletableFuture<V> thenCombineAsync(CompletableFuture<? extends U> other, BiFunction<? super T, ? super U, ? extends V> function, Executor executor) {
      CompletableFuture<V> next = new CompletableFuture<>();
      this.whenComplete((left, leftError) -> {
         if (leftError != null) {
            next.completeExceptionally(leftError);
         } else {
            other.whenComplete((right, rightError) -> {
               if (rightError != null) {
                  next.completeExceptionally(rightError);
               } else {
                  executor.execute(() -> {
                     try {
                        next.complete(function.apply(left, right));
                     } catch (Throwable throwable) {
                        next.completeExceptionally(unwrap(throwable));
                     }
                  });
               }
            });
         }
      });
      return next;
   }

   public <U> CompletableFuture<U> handle(BiFunction<? super T, Throwable, ? extends U> function) {
      return handleAsync(function, Runnable::run);
   }

   public <U> CompletableFuture<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> function) {
      return handleAsync(function, Runnable::run);
   }

   public <U> CompletableFuture<U> handleAsync(BiFunction<? super T, Throwable, ? extends U> function, Executor executor) {
      CompletableFuture<U> next = new CompletableFuture<>();
      addListener(() -> executor.execute(() -> {
         try {
            next.complete(function.apply(this.value, this.error));
         } catch (Throwable throwable) {
            next.completeExceptionally(unwrap(throwable));
         }
      }));
      return next;
   }

   public CompletableFuture<T> whenComplete(BiConsumer<? super T, ? super Throwable> consumer) {
      return whenCompleteAsync(consumer, Runnable::run);
   }

   public CompletableFuture<T> whenCompleteAsync(BiConsumer<? super T, ? super Throwable> consumer) {
      return whenCompleteAsync(consumer, Runnable::run);
   }

   public CompletableFuture<T> whenCompleteAsync(BiConsumer<? super T, ? super Throwable> consumer, Executor executor) {
      CompletableFuture<T> next = new CompletableFuture<>();
      addListener(() -> executor.execute(() -> {
         try {
            consumer.accept(this.value, this.error);
            if (this.error != null) {
               next.completeExceptionally(this.error);
            } else {
               next.complete(this.value);
            }
         } catch (Throwable throwable) {
            next.completeExceptionally(throwable);
         }
      }));
      return next;
   }

   public CompletableFuture<T> exceptionally(Function<Throwable, ? extends T> function) {
      CompletableFuture<T> next = new CompletableFuture<>();
      addListener(() -> {
         if (this.error != null) {
            try {
               next.complete(function.apply(this.error));
            } catch (Throwable throwable) {
               next.completeExceptionally(throwable);
            }
         } else {
            next.complete(this.value);
         }
      });
      return next;
   }

   public <U> CompletableFuture<U> applyToEither(CompletableFuture<? extends T> other, Function<? super T, U> function) {
      CompletableFuture<U> next = new CompletableFuture<>();
      BiConsumer<T, Throwable> complete = (value, error) -> {
         if (next.isDone()) {
            return;
         }
         if (error != null) {
            next.completeExceptionally(error);
         } else {
            next.complete(function.apply(value));
         }
      };
      this.whenComplete(complete);
      other.whenComplete(complete);
      return next;
   }

   public T join() {
      try {
         return get();
      } catch (InterruptedException interruptedexception) {
         throw new CompletionException(interruptedexception);
      } catch (ExecutionException executionexception) {
         throw new CompletionException(executionexception.getCause());
      }
   }

   public T getNow(T valueIfAbsent) {
      if (!isDone()) {
         return valueIfAbsent;
      }
      return join();
   }

   public boolean isCompletedExceptionally() {
      return isDone() && this.error != null;
   }

   @Override
   public boolean cancel(boolean mayInterruptIfRunning) {
      return finish(null, new CancellationException(), true);
   }

   @Override
   public boolean isCancelled() {
      return this.cancelled;
   }

   @Override
   public synchronized boolean isDone() {
      return this.done;
   }

   @Override
   public synchronized T get() throws InterruptedException, ExecutionException {
      if (!this.done) {
         throw new IllegalStateException("Blocking CompletableFuture.get() is not available in the browser runtime");
      }
      if (this.error != null) {
         if (this.error instanceof CancellationException) {
            throw (CancellationException)this.error;
         }
         throw new ExecutionException(this.error);
      }
      return this.value;
   }

   @Override
   public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
      if (!isDone()) {
         throw new TimeoutException();
      }
      return get();
   }

   private static Throwable unwrap(Throwable throwable) {
      return throwable instanceof CompletionException && throwable.getCause() != null ? throwable.getCause() : throwable;
   }
}
