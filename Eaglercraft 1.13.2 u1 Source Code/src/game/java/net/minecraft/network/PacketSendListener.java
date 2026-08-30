package net.minecraft.network;

public interface PacketSendListener {
   PacketSendListener.Result SUCCESS = new PacketSendListener.Result(true, null);

   void accept(PacketSendListener.Result result);

   public static class Result {
      private final boolean success;
      private final Throwable cause;

      public Result(boolean success, Throwable cause) {
         this.success = success;
         this.cause = cause;
      }

      public boolean isSuccess() {
         return this.success;
      }

      public Throwable cause() {
         return this.cause;
      }
   }
}
