package net.minecraft.client.gui;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EagRuntime;
import net.lax1dude.eaglercraft.v1_8.Mouse;
import net.lax1dude.eaglercraft.v1_8.cookie.ServerCookieDataStore;
import net.lax1dude.eaglercraft.v1_8.internal.EnumCursorType;
import net.lax1dude.eaglercraft.v1_8.sp.gui.GuiNetworkSettingsButton;
import net.lax1dude.eaglercraft.v1_8.sp.gui.GuiScreenConnectOption;
import net.lax1dude.eaglercraft.v1_8.sp.gui.GuiScreenLANConnecting;
import net.lax1dude.eaglercraft.v1_8.sp.lan.LANServerList;
import net.lax1dude.eaglercraft.v1_8.sp.relay.RelayServer;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.network.LanServerInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.network.ServerPinger;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class GuiMultiplayer extends GuiScreen {
   private static final Logger LOGGER = LogManager.getLogger();
   private final ServerPinger oldServerPinger = new ServerPinger();
   private final GuiScreen parentScreen;
   private ServerSelectionList serverListSelector;
   private ServerList savedServerList;
   private GuiButton btnEditServer;
   private GuiButton btnSelectServer;
   private GuiButton btnDeleteServer;
   private boolean deletingServer;
   private boolean addingServer;
   private boolean editingServer;
   private boolean directConnect;
   private String hoveringText;
   private ServerData selectedServer;
   private boolean initialized;
   //audrey <3
   private static long lastRefreshCommit;
   //audrey <3
   private static LANServerList lanServerList;
   //audrey <3
   public int ticksOpened;
   //audrey <3
   private final GuiNetworkSettingsButton relaysButton;

   public GuiMultiplayer(GuiScreen parentScreen) {
      this.parentScreen = parentScreen;
      //audrey <3
      this.relaysButton = new GuiNetworkSettingsButton(this);
      //audrey <3
      if (lanServerList != null) {
         lanServerList.forceRefresh();
      }
   }

   public IGuiEventListener getFocused() {
      return this.serverListSelector;
   }

   protected void initGui() {
      super.initGui();
      this.mc.keyboardListener.enableRepeatEvents(true);
      if (this.initialized) {
         this.serverListSelector.setDimensions(this.width, this.height, 32, this.height - 64);
      } else {
         this.initialized = true;
         this.savedServerList = new ServerList(this.mc);
         this.savedServerList.loadServerList();
         this.serverListSelector = new ServerSelectionList(this, this.mc, this.width, this.height, 32, this.height - 64, 36);
         this.serverListSelector.updateOnlineServers(this.savedServerList);
         //audrey <3
         if (lanServerList == null) {
            lanServerList = new LANServerList();
         }
         //audrey <3
         lanServerList.forceRefresh();
      }

      //audrey <3
      this.serverListSelector.updateEaglerLANServers(lanServerList);
      this.createButtons();
   }

   public void createButtons() {
      this.btnEditServer = this.addButton(new GuiButton(7, this.width / 2 - 154, this.height - 28, 70, 20, I18n.format("selectServer.edit")) {
         public void onClick(double mouseX, double mouseY) {
            GuiListExtended.IGuiListEntry<?> iguilistentry = GuiMultiplayer.this.serverListSelector.getSelected() < 0 ? null : GuiMultiplayer.this.serverListSelector.getChildren().get(GuiMultiplayer.this.serverListSelector.getSelected());
            GuiMultiplayer.this.editingServer = true;
            if (iguilistentry instanceof ServerListEntryNormal) {
               ServerData serverdata = ((ServerListEntryNormal)iguilistentry).getServerData();
               GuiMultiplayer.this.selectedServer = new ServerData(serverdata.serverName, serverdata.serverIP, false);
               GuiMultiplayer.this.selectedServer.copyFrom(serverdata);
               GuiMultiplayer.this.mc.displayGuiScreen(new GuiScreenAddServer(GuiMultiplayer.this, GuiMultiplayer.this.selectedServer));
            }

         }
      });
      this.btnDeleteServer = this.addButton(new GuiButton(2, this.width / 2 - 74, this.height - 28, 70, 20, I18n.format("selectServer.delete")) {
         public void onClick(double mouseX, double mouseY) {
            GuiListExtended.IGuiListEntry<?> iguilistentry = GuiMultiplayer.this.serverListSelector.getSelected() < 0 ? null : GuiMultiplayer.this.serverListSelector.getChildren().get(GuiMultiplayer.this.serverListSelector.getSelected());
            if (iguilistentry instanceof ServerListEntryNormal) {
               String s = ((ServerListEntryNormal)iguilistentry).getServerData().serverName;
               if (s != null) {
                  GuiMultiplayer.this.deletingServer = true;
                  String s1 = I18n.format("selectServer.deleteQuestion");
                  String s2 = I18n.format("selectServer.deleteWarning", s);
                  String s3 = I18n.format("selectServer.deleteButton");
                  String s4 = I18n.format("gui.cancel");
                  GuiYesNo guiyesno = new GuiYesNo(GuiMultiplayer.this, s1, s2, s3, s4, GuiMultiplayer.this.serverListSelector.getSelected());
                  GuiMultiplayer.this.mc.displayGuiScreen(guiyesno);
               }
            }

         }
      });
      this.btnSelectServer = this.addButton(new GuiButton(1, this.width / 2 - 154, this.height - 52, 100, 20, I18n.format("selectServer.select")) {
         public void onClick(double mouseX, double mouseY) {
            GuiMultiplayer.this.connectToSelected();
         }
      });
      this.addButton(new GuiButton(4, this.width / 2 - 50, this.height - 52, 100, 20, I18n.format("selectServer.direct")) {
         public void onClick(double mouseX, double mouseY) {
            GuiMultiplayer.this.directConnect = true;
            GuiMultiplayer.this.selectedServer = new ServerData(I18n.format("selectServer.defaultName"), "", false);
            //audrey <3
            GuiMultiplayer.this.mc.displayGuiScreen(new GuiScreenConnectOption(GuiMultiplayer.this));
         }
      });
      this.addButton(new GuiButton(3, this.width / 2 + 4 + 50, this.height - 52, 100, 20, I18n.format("selectServer.add")) {
         public void onClick(double mouseX, double mouseY) {
            GuiMultiplayer.this.addingServer = true;
            GuiMultiplayer.this.selectedServer = new ServerData(I18n.format("selectServer.defaultName"), "", false);
            GuiMultiplayer.this.mc.displayGuiScreen(new GuiScreenAddServer(GuiMultiplayer.this, GuiMultiplayer.this.selectedServer));
         }
      });
      this.addButton(new GuiButton(8, this.width / 2 + 4, this.height - 28, 70, 20, I18n.format("selectServer.refresh")) {
         public void onClick(double mouseX, double mouseY) {
            GuiMultiplayer.this.refreshServerList();
         }
      });
      this.addButton(new GuiButton(0, this.width / 2 + 4 + 76, this.height - 28, 75, 20, I18n.format("gui.cancel")) {
         public void onClick(double mouseX, double mouseY) {
            GuiMultiplayer.this.mc.displayGuiScreen(GuiMultiplayer.this.parentScreen);
         }
      });
      this.children.add(this.serverListSelector);
      this.selectServer(this.serverListSelector.getSelected());
   }

   public void tick() {
      super.tick();
      //audrey <3
      if (lanServerList != null && lanServerList.update()) {
         this.serverListSelector.updateEaglerLANServers(lanServerList);
         this.selectServer(-1);
      }

      //audrey <3
      ++this.ticksOpened;
      this.oldServerPinger.pingPendingNetworks();
   }

   public void onGuiClosed() {
      this.mc.keyboardListener.enableRepeatEvents(false);
      this.oldServerPinger.clearPendingNetworks();
   }

   public void refreshServerList() {
      this.mc.displayGuiScreen(new GuiMultiplayer(this.parentScreen));
   }

   public void confirmResult(boolean p_confirmResult_1_, int p_confirmResult_2_) {
      GuiListExtended.IGuiListEntry<?> iguilistentry = this.serverListSelector.getSelected() < 0 ? null : this.serverListSelector.getChildren().get(this.serverListSelector.getSelected());
      if (this.deletingServer) {
         this.deletingServer = false;
         if (p_confirmResult_1_ && iguilistentry instanceof ServerListEntryNormal) {
            this.savedServerList.removeServerData(this.serverListSelector.getSelected());
            this.savedServerList.saveServerList();
            this.serverListSelector.setSelectedSlotIndex(-1);
            this.serverListSelector.updateOnlineServers(this.savedServerList);
         }

         this.mc.displayGuiScreen(this);
      } else if (this.directConnect) {
         this.directConnect = false;
         if (p_confirmResult_1_) {
            this.connectToServer(this.selectedServer);
         } else {
            this.mc.displayGuiScreen(this);
         }
      } else if (this.addingServer) {
         this.addingServer = false;
         if (p_confirmResult_1_) {
            //audrey <3
            if (!this.selectedServer.enableCookies) {
               ServerCookieDataStore.clearCookie(this.selectedServer.serverIP);
            }
            this.savedServerList.addServerData(this.selectedServer);
            this.savedServerList.saveServerList();
            this.serverListSelector.setSelectedSlotIndex(-1);
            this.serverListSelector.updateOnlineServers(this.savedServerList);
         }

         this.mc.displayGuiScreen(this);
      } else if (this.editingServer) {
         this.editingServer = false;
         if (p_confirmResult_1_ && iguilistentry instanceof ServerListEntryNormal) {
            ServerData serverdata = ((ServerListEntryNormal)iguilistentry).getServerData();
            serverdata.serverName = this.selectedServer.serverName;
            serverdata.serverIP = this.selectedServer.serverIP;
            //audrey <3
            if (serverdata.enableCookies && !this.selectedServer.enableCookies) {
               ServerCookieDataStore.clearCookie(this.selectedServer.serverIP);
            }
            serverdata.copyFrom(this.selectedServer);
            this.savedServerList.saveServerList();
            this.serverListSelector.updateOnlineServers(this.savedServerList);
         }

         this.mc.displayGuiScreen(this);
      }

   }

   //audrey <3
   public void cancelDirectConnect() {
      this.directConnect = false;
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      int i = this.serverListSelector.getSelected();
      GuiListExtended.IGuiListEntry<?> iguilistentry = i < 0 ? null : this.serverListSelector.getChildren().get(i);
      if (p_keyPressed_1_ == 294) {
         this.refreshServerList();
         return true;
      } else {
         if (i >= 0) {
            if (p_keyPressed_1_ == 265) {
               if (isShiftKeyDown()) {
                  if (i > 0 && iguilistentry instanceof ServerListEntryNormal) {
                     this.savedServerList.swapServers(i, i - 1);
                     this.selectServer(this.serverListSelector.getSelected() - 1);
                     this.serverListSelector.scrollBy(-this.serverListSelector.getSlotHeight());
                     this.serverListSelector.updateOnlineServers(this.savedServerList);
                  }
               } else if (i > 0) {
                  this.selectServer(this.serverListSelector.getSelected() - 1);
                  this.serverListSelector.scrollBy(-this.serverListSelector.getSlotHeight());
                  if (this.serverListSelector.getChildren().get(this.serverListSelector.getSelected()) instanceof ServerListEntryLanScan) {
                     if (this.serverListSelector.getSelected() > 0) {
                        this.selectServer(this.serverListSelector.getChildren().size() - 1);
                        this.serverListSelector.scrollBy(-this.serverListSelector.getSlotHeight());
                     } else {
                        this.selectServer(-1);
                     }
                  }
               } else {
                  this.selectServer(-1);
               }

               return true;
            }

            if (p_keyPressed_1_ == 264) {
               if (isShiftKeyDown()) {
                  if (i < this.savedServerList.countServers() - 1) {
                     this.savedServerList.swapServers(i, i + 1);
                     this.selectServer(i + 1);
                     this.serverListSelector.scrollBy(this.serverListSelector.getSlotHeight());
                     this.serverListSelector.updateOnlineServers(this.savedServerList);
                  }
               } else if (i < this.serverListSelector.getChildren().size()) {
                  this.selectServer(this.serverListSelector.getSelected() + 1);
                  this.serverListSelector.scrollBy(this.serverListSelector.getSlotHeight());
                  if (this.serverListSelector.getChildren().get(this.serverListSelector.getSelected()) instanceof ServerListEntryLanScan) {
                     if (this.serverListSelector.getSelected() < this.serverListSelector.getChildren().size() - 1) {
                        this.selectServer(this.serverListSelector.getChildren().size() + 1);
                        this.serverListSelector.scrollBy(this.serverListSelector.getSlotHeight());
                     } else {
                        this.selectServer(-1);
                     }
                  }
               } else {
                  this.selectServer(-1);
               }

               return true;
            }

            if (p_keyPressed_1_ == 257 || p_keyPressed_1_ == 335) {
               this.connectToSelected();
               return true;
            }
         }

         return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
      }
   }

   public void render(int mouseX, int mouseY, float partialTicks) {
      this.hoveringText = null;
      this.drawDefaultBackground();
      this.serverListSelector.drawScreen(mouseX, mouseY, partialTicks);
      this.drawCenteredString(this.fontRenderer, I18n.format("multiplayer.title"), this.width / 2, 20, 16777215);
      super.render(mouseX, mouseY, partialTicks);
      //audrey <3
      this.relaysButton.drawScreen(mouseX, mouseY);
      //audrey <3
      this.drawPluginDownloadLink(mouseX, mouseY);
      if (this.hoveringText != null) {
         this.drawHoveringText(Lists.newArrayList(Splitter.on("\n").split(this.hoveringText)), mouseX, mouseY);
      }

   }

   //audrey <3
   private void drawPluginDownloadLink(int mouseX, int mouseY) {
      GlStateManager.pushMatrix();
      GlStateManager.scalef(0.75F, 0.75F, 0.75F);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);

      String s = "Download EaglerXServer";
      int i = this.mc.fontRenderer.getStringWidth(s);
      boolean flag = mouseX > this.width - 5 - (i + 5) * 3 / 4 && mouseY > 1 && mouseX < this.width - 2 && mouseY < 12;
      if (flag) {
         Mouse.showCursor(EnumCursorType.HAND);
      }

      drawString(this.mc.fontRenderer, TextFormatting.UNDERLINE + s, (this.width - 1) * 4 / 3 - i - 5, 5, flag ? 0xFFEEEE22 : 0xFFCCCCCC);
      GlStateManager.popMatrix();
   }

   //audrey <3
   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      this.relaysButton.mouseClicked((int)mouseX, (int)mouseY, button);
      String s = "Download EaglerXServer";
      int i = this.mc.fontRenderer.getStringWidth(s);
      if (mouseX > (double)(this.width - 5 - (i + 5) * 3 / 4) && mouseY > 1.0D && mouseX < (double)(this.width - 2) && mouseY < 12.0D) {
         EagRuntime.openLink("https://lax1dude.net/eaglerxserver");
         return true;
      }

      return super.mouseClicked(mouseX, mouseY, button);
   }

   public void connectToSelected() {
      GuiListExtended.IGuiListEntry<?> iguilistentry = this.serverListSelector.getSelected() < 0 ? null : this.serverListSelector.getChildren().get(this.serverListSelector.getSelected());
      if (iguilistentry instanceof ServerListEntryNormal) {
         this.connectToServer(((ServerListEntryNormal)iguilistentry).getServerData());
      } else if (iguilistentry instanceof ServerListEntryLanDetected) {
         //audrey <3
         LANServerList.LanServer lanserver = ((ServerListEntryLanDetected)iguilistentry).getEaglerServerData();
         if (lanserver != null) {
            this.connectToLAN("Connecting to '" + lanserver.getLanServerMotd() + "'...", lanserver.getLanServerCode(), lanserver.getLanServerRelay());
         } else {
            LanServerInfo lanserverinfo = ((ServerListEntryLanDetected)iguilistentry).getServerData();
            this.connectToServer(new ServerData(lanserverinfo.getServerMotd(), lanserverinfo.getServerIpPort(), true));
         }
      }

   }

   private void connectToServer(ServerData server) {
      this.mc.displayGuiScreen(new GuiConnecting(this, this.mc, server));
   }

   //audrey <3
   private void connectToLAN(String text, String code, RelayServer relay) {
      //audrey <3
      this.mc.loadingScreen.resetProgressAndMessage(new TextComponentString(text));
      this.mc.displayGuiScreen(new GuiScreenLANConnecting(this, code, relay));
   }

   public void selectServer(int index) {
      this.serverListSelector.setSelectedSlotIndex(index);
      GuiListExtended.IGuiListEntry<?> iguilistentry = index < 0 ? null : this.serverListSelector.getChildren().get(index);
      this.btnSelectServer.enabled = false;
      this.btnEditServer.enabled = false;
      this.btnDeleteServer.enabled = false;
      if (iguilistentry != null && !(iguilistentry instanceof ServerListEntryLanScan)) {
         this.btnSelectServer.enabled = true;
         if (iguilistentry instanceof ServerListEntryNormal) {
            this.btnEditServer.enabled = true;
            this.btnDeleteServer.enabled = true;
         }
      }

   }

   public ServerPinger getOldServerPinger() {
      return this.oldServerPinger;
   }

   public void setHoveringText(String p_146793_1_) {
      this.hoveringText = p_146793_1_;
   }

   public ServerList getServerList() {
      return this.savedServerList;
   }

   //audrey <3
   public ServerData getSelectedServer() {
      return this.selectedServer;
   }

   //audrey <3
   static LANServerList getLanServerList() {
      return lanServerList;
   }

   public boolean canMoveUp(ServerListEntryNormal p_175392_1_, int p_175392_2_) {
      return p_175392_2_ > 0;
   }

   public boolean canMoveDown(ServerListEntryNormal p_175394_1_, int p_175394_2_) {
      return p_175394_2_ < this.savedServerList.countServers() - 1;
   }

   public void moveServerUp(ServerListEntryNormal p_175391_1_, int p_175391_2_, boolean p_175391_3_) {
      int i = p_175391_3_ ? 0 : p_175391_2_ - 1;
      this.savedServerList.swapServers(p_175391_2_, i);
      if (this.serverListSelector.getSelected() == p_175391_2_) {
         this.selectServer(i);
      }

      this.serverListSelector.updateOnlineServers(this.savedServerList);
   }

   public void moveServerDown(ServerListEntryNormal p_175393_1_, int p_175393_2_, boolean p_175393_3_) {
      int i = p_175393_3_ ? this.savedServerList.countServers() - 1 : p_175393_2_ + 1;
      this.savedServerList.swapServers(p_175393_2_, i);
      if (this.serverListSelector.getSelected() == p_175393_2_) {
         this.selectServer(i);
      }

      this.serverListSelector.updateOnlineServers(this.savedServerList);
   }
}
