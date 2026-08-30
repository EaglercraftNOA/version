package net.minecraft.server.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ITickable;

public class PlayerListComponent implements ITickable {
   private final MinecraftServer server;
   private int ticks;
   private List<String> players = Collections.emptyList();

   public PlayerListComponent(MinecraftServer server) {
      this.server = server;
      server.registerTickable(this);
   }

   public void tick() {
      if (this.ticks++ % 20 == 0) {
         List<String> list = new ArrayList<>();

         for(int i = 0; i < this.server.getPlayerList().getPlayers().size(); ++i) {
            list.add(this.server.getPlayerList().getPlayers().get(i).getGameProfile().getName());
         }

         this.players = Collections.unmodifiableList(list);
      }
   }

   public List<String> getPlayers() {
      return this.players;
   }
}
