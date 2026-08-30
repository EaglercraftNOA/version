package net.minecraft.client.multiplayer;

import com.google.common.collect.Lists;
import java.io.File;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EagRuntime;
import net.lax1dude.eaglercraft.v1_8.EaglerInputStream;
import net.lax1dude.eaglercraft.v1_8.EaglerOutputStream;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.INBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ServerList {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Minecraft mc;
   private final List<ServerData> servers = Lists.newArrayList();

   public ServerList(Minecraft mcIn) {
      this.mc = mcIn;
      this.loadServerList();
   }

   public void loadServerList() {
      loadServerList(EagRuntime.getStorage("s"));
   }

   public void loadServerList(byte[] data) {
      try {
         if (data == null) {
            return;
         }
         this.servers.clear();
         NBTTagCompound nbttagcompound = CompressedStreamTools.readCompressed(new EaglerInputStream(data));
         if (nbttagcompound == null) {
            return;
         }

         NBTTagList nbttaglist = nbttagcompound.getList("servers", 10);

         for(int i = 0; i < nbttaglist.size(); ++i) {
            this.servers.add(ServerData.getServerDataFromNBTCompound(nbttaglist.getCompound(i)));
         }
      } catch (Exception exception) {
         LOGGER.error("Couldn't load server list", (Throwable)exception);
      }

   }

   public void saveServerList() {
      byte[] data = writeServerList();
      if (data != null) {
         EagRuntime.setStorage("s", data);
      }
   }

   public byte[] writeServerList() {
      try {
         NBTTagList nbttaglist = new NBTTagList();

         for(ServerData serverdata : this.servers) {
            nbttaglist.add((INBTBase)serverdata.getNBTCompound());
         }

         NBTTagCompound nbttagcompound = new NBTTagCompound();
         nbttagcompound.put("servers", nbttaglist);

         EaglerOutputStream bao = new EaglerOutputStream();
         CompressedStreamTools.writeCompressed(nbttagcompound, bao);
         return bao.toByteArray();
      } catch (Exception exception) {
         LOGGER.error("Couldn't save server list", (Throwable)exception);
         return null;
      }
   }

   public ServerData getServerData(int index) {
      return this.servers.get(index);
   }

   public void removeServerData(int index) {
      this.servers.remove(index);
   }

   public void addServerData(ServerData server) {
      this.servers.add(server);
   }

   public int countServers() {
      return this.servers.size();
   }

   public void swapServers(int pos1, int pos2) {
      ServerData serverdata = this.getServerData(pos1);
      this.servers.set(pos1, this.getServerData(pos2));
      this.servers.set(pos2, serverdata);
      this.saveServerList();
   }

   public void set(int index, ServerData server) {
      this.servers.set(index, server);
   }

   public static void saveSingleServer(ServerData server) {
      ServerList serverlist = new ServerList(Minecraft.getInstance());
      serverlist.loadServerList();

      for(int i = 0; i < serverlist.countServers(); ++i) {
         ServerData serverdata = serverlist.getServerData(i);
         if (serverdata.serverName.equals(server.serverName) && serverdata.serverIP.equals(server.serverIP)) {
            serverlist.set(i, server);
            break;
         }
      }

      serverlist.saveServerList();
   }
}
