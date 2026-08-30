package net.minecraft.server.gui;

import net.minecraft.server.dedicated.DedicatedServer;

public class MinecraftServerGui {
   private final DedicatedServer server;
   private final StatsComponent statsComponent;
   private final PlayerListComponent playerListComponent;

   public static void createServerGui(final DedicatedServer serverIn) {
      (new MinecraftServerGui(serverIn)).start();
   }

   public MinecraftServerGui(DedicatedServer serverIn) {
      this.server = serverIn;
      this.statsComponent = new StatsComponent(serverIn);
      this.playerListComponent = new PlayerListComponent(serverIn);
   }

   public void start() {
      this.statsComponent.tick();
      this.playerListComponent.tick();
   }

   public DedicatedServer getServer() {
      return this.server;
   }

   public StatsComponent getStatsComponent() {
      return this.statsComponent;
   }

   public PlayerListComponent getPlayerListComponent() {
      return this.playerListComponent;
   }
}
