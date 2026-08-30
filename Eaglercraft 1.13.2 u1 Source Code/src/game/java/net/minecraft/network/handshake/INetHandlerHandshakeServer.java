package net.minecraft.network.handshake;

import net.minecraft.network.INetHandler;
import net.minecraft.network.handshake.client.CPacketHandshake;

public interface INetHandlerHandshakeServer extends INetHandler {
   void processHandshake(CPacketHandshake packetIn);
}
