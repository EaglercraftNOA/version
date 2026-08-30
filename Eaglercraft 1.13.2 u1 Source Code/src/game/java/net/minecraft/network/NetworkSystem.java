package net.minecraft.network;

import java.io.IOException;
import java.net.InetAddress;
import javax.annotation.Nullable;
import net.lax1dude.eaglercraft.v1_8.EaglerSocketAddress;
import net.lax1dude.eaglercraft.v1_8.sp.server.EaglerIntegratedServerWorker;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NetworkSystem {
   private static final Logger LOGGER = LogManager.getLogger();
   private final MinecraftServer server;
   public volatile boolean isAlive;

   public NetworkSystem(MinecraftServer server) {
      this.server = server;
      this.isAlive = true;
   }

   public void addEndpoint(@Nullable InetAddress address, int port) throws IOException {
      LOGGER.info("Eagler network system listening on websocket IPC");
   }

   @OnlyIn(Dist.CLIENT)
   public EaglerSocketAddress addLocalEndpoint() {
      return new EaglerSocketAddress("127.0.0.1", 0);
   }

   public void terminateEndpoints() {
      this.isAlive = false;
   }

   public void tick() {
      EaglerIntegratedServerWorker.tick();
   }

   public MinecraftServer getServer() {
      return this.server;
   }
}
