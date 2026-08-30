package com.arlenh7.eaglercraft.v1_17.java.util.concurrent;

public class CompletionException extends RuntimeException {
   public CompletionException(String message, Throwable cause) {
      super(message, cause);
   }

   public CompletionException(Throwable cause) {
      super(cause);
   }
}
