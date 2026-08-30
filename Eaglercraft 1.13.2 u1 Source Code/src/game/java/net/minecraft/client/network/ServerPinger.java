package net.minecraft.client.network;

import com.google.common.collect.Lists;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.internal.IServerQuery;
import net.lax1dude.eaglercraft.v1_8.internal.QueryResponse;
import net.lax1dude.eaglercraft.v1_8.socket.AddressResolver;
import net.lax1dude.eaglercraft.v1_8.socket.ServerQueryDispatch;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

@OnlyIn(Dist.CLIENT)
public class ServerPinger {
   private static final Logger LOGGER = LogManager.getLogger();
   private final List<ServerPinger.PendingQuery> pingDestinations = Collections.synchronizedList(Lists.newArrayList());

   public void ping(final ServerData server) throws UnknownHostException {
      String address = AddressResolver.resolveURI(server);
      IServerQuery query = ServerQueryDispatch.sendServerQuery(address, "MOTD");
      server.serverMOTD = I18n.format("multiplayer.status.pinging");
      server.pingToServer = -1L;
      server.playerList = null;
      if (query != null) {
         this.pingDestinations.add(new ServerPinger.PendingQuery(server, query));
      } else {
         server.serverMOTD = TextFormatting.DARK_RED + I18n.format("multiplayer.status.cannot_connect");
         server.populationInfo = "";
      }
   }

   public void pingPendingNetworks() {
      synchronized(this.pingDestinations) {
         Iterator<ServerPinger.PendingQuery> iterator = this.pingDestinations.iterator();

         while(iterator.hasNext()) {
            ServerPinger.PendingQuery pending = iterator.next();
            pending.query.update();
            if (pending.query.responsesAvailable() > 0) {
               QueryResponse response = pending.query.getResponse();
               if (response.responseType.equalsIgnoreCase("MOTD") && response.isResponseJSON()) {
                  this.applyMOTD(pending.server, response);
               }
               pending.query.close();
               iterator.remove();
            } else if (pending.query.isClosed()) {
               pending.server.serverMOTD = TextFormatting.DARK_RED + I18n.format("multiplayer.status.cannot_connect");
               pending.server.populationInfo = "";
               iterator.remove();
            }
         }
      }
   }

   private void applyMOTD(ServerData server, QueryResponse response) {
      try {
         JSONObject motdData = response.getResponseJSON();
         JSONArray motd = motdData.getJSONArray("motd");
         server.serverMOTD = motd.length() > 0 ? (motd.length() > 1 ? motd.getString(0) + "\n" + motd.getString(1) : motd.getString(0)) : "";
         int max = motdData.getInt("max");
         server.populationInfo = max > 0 ? "" + motdData.getInt("online") + "/" + max : "" + motdData.getInt("online");
         server.playerList = null;
         JSONArray players = motdData.optJSONArray("players");
         if (players != null && players.length() > 0) {
            StringBuilder builder = new StringBuilder();
            for(int i = 0, l = players.length(); i < l; ++i) {
               if (i > 0) {
                  builder.append('\n');
               }
               builder.append(players.getString(i));
            }
            server.playerList = builder.toString();
         }
         server.gameVersion = response.serverVersion;
         server.pingToServer = response.ping;
      } catch (Throwable throwable) {
         server.pingToServer = -1L;
         server.serverMOTD = TextFormatting.DARK_RED + I18n.format("multiplayer.status.cannot_connect");
         server.populationInfo = "";
         LOGGER.error("Could not decode QueryResponse from: {}", server.serverIP);
         LOGGER.error(throwable);
      }
   }

   public void clearPendingNetworks() {
      synchronized(this.pingDestinations) {
         Iterator<ServerPinger.PendingQuery> iterator = this.pingDestinations.iterator();

         while(iterator.hasNext()) {
            ServerPinger.PendingQuery pending = iterator.next();
            iterator.remove();
            pending.query.close();
         }
      }
   }

   static class PendingQuery {
      final ServerData server;
      final IServerQuery query;

      PendingQuery(ServerData server, IServerQuery query) {
         this.server = server;
         this.query = query;
      }
   }
}
