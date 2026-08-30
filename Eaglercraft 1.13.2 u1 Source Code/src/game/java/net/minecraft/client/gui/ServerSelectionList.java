package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.network.LanServerInfo;
import net.lax1dude.eaglercraft.v1_8.sp.lan.LANServerList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ServerSelectionList extends GuiListExtended<ServerSelectionList.Entry> {
   private final GuiMultiplayer owner;
   private final List<ServerListEntryNormal> serverListInternet = Lists.newArrayList();
   private final ServerSelectionList.Entry lanScanEntry = new ServerListEntryLanScan();
   private final List<ServerListEntryLanDetected> serverListLan = Lists.newArrayList();
   private int selectedSlotIndex = -1;

   private void func_195094_h() {
      this.clearEntries();
      this.serverListInternet.forEach(this::addEntry);
      this.serverListLan.forEach(this::addEntry);
      //audrey <3
      this.addEntry(this.lanScanEntry);
   }

   public ServerSelectionList(GuiMultiplayer ownerIn, Minecraft mcIn, int widthIn, int heightIn, int topIn, int bottomIn, int slotHeightIn) {
      super(mcIn, widthIn, heightIn, topIn, bottomIn, slotHeightIn);
      this.owner = ownerIn;
   }

   public void setSelectedSlotIndex(int selectedSlotIndexIn) {
      this.selectedSlotIndex = selectedSlotIndexIn;
   }

   protected boolean isSelected(int slotIndex) {
      return slotIndex == this.selectedSlotIndex;
   }

   public int getSelected() {
      return this.selectedSlotIndex;
   }

   public void updateOnlineServers(ServerList p_148195_1_) {
      this.serverListInternet.clear();

      for(int i = 0; i < p_148195_1_.countServers(); ++i) {
         this.serverListInternet.add(new ServerListEntryNormal(this.owner, p_148195_1_.getServerData(i)));
      }

      this.func_195094_h();
   }

   public void updateNetworkServers(List<LanServerInfo> p_148194_1_) {
      this.serverListLan.clear();

      for(LanServerInfo lanserverinfo : p_148194_1_) {
         this.serverListLan.add(new ServerListEntryLanDetected(this.owner, lanserverinfo));
      }

      this.func_195094_h();
   }

   //audrey <3
   public void updateEaglerLANServers(LANServerList lanServerList) {
      this.serverListLan.clear();

      if (lanServerList != null) {
         for(int i = 0; i < lanServerList.countServers(); ++i) {
            this.serverListLan.add(new ServerListEntryLanDetected(this.owner, lanServerList.getServer(i)));
         }
      }

      this.func_195094_h();
   }

   protected int getScrollBarX() {
      return super.getScrollBarX() + 30;
   }

   public int getListWidth() {
      return super.getListWidth() + 85;
   }

   @OnlyIn(Dist.CLIENT)
   public abstract static class Entry extends GuiListExtended.IGuiListEntry<ServerSelectionList.Entry> {
   }
}
