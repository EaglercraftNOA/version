package net.minecraft.network;

import java.io.IOException;

public interface Packet<T extends INetHandler> {
   void readPacketData(PacketBuffer buf) throws IOException;

   void writePacketData(PacketBuffer buf) throws IOException;

   void processPacket(T handler);

   default void read(PacketBuffer buf) throws IOException {
      this.readPacketData(buf);
   }

   default void write(PacketBuffer buf) throws IOException {
      this.writePacketData(buf);
   }

   default void handle(INetHandler handler) {
      this.processPacket((T)handler);
   }

   default boolean shouldSkipErrors() {
      return false;
   }
}
