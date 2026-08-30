package net.minecraft.client.network;

import net.minecraft.network.NetHandlerLoginServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.INetHandlerHandshakeServer;
import net.minecraft.network.handshake.client.CPacketHandshake;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class NetHandlerHandshakeMemory implements INetHandlerHandshakeServer {
   private final MinecraftServer server;
   private final NetworkManager networkManager;

   public NetHandlerHandshakeMemory(MinecraftServer mcServerIn, NetworkManager networkManagerIn) {
      this.server = mcServerIn;
      this.networkManager = networkManagerIn;
   }

   public void processHandshake(CPacketHandshake packetIn) {
      this.networkManager.setConnectionState(packetIn.getRequestedState());
      this.networkManager.setNetHandler(new NetHandlerLoginServer(this.server, this.networkManager));
   }

   public void onDisconnect(ITextComponent reason) {
   }
}
