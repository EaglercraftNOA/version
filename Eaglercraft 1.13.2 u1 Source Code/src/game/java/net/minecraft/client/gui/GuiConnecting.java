package net.minecraft.client.gui;

import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EagRuntime;
import net.lax1dude.eaglercraft.v1_8.cookie.ServerCookieDataStore;
import net.lax1dude.eaglercraft.v1_8.internal.EnumEaglerConnectionState;
import net.lax1dude.eaglercraft.v1_8.internal.IWebSocketClient;
import net.lax1dude.eaglercraft.v1_8.internal.IWebSocketFrame;
import net.lax1dude.eaglercraft.v1_8.internal.PlatformNetworking;
import net.lax1dude.eaglercraft.v1_8.profile.EaglerProfile;
import net.lax1dude.eaglercraft.v1_8.socket.AddressResolver;
import net.lax1dude.eaglercraft.v1_8.socket.RateLimitTracker;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.handshake.HandshakerHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class GuiConnecting extends GuiScreen {
   private static final Logger LOGGER = LogManager.getLogger();
   private IWebSocketClient webSocket;
   private HandshakerHandler handshaker;
   private String currentAddress;
   private String currentPassword;
   private boolean allowPlaintext;
   private boolean allowCookies;
   private boolean cancel;
   private final GuiScreen previousGuiScreen;
   private int timer;

   public GuiConnecting(GuiScreen parent, Minecraft mcIn, ServerData serverDataIn) {
      this(parent, mcIn, serverDataIn, null, false);
   }

   public GuiConnecting(GuiScreen parent, Minecraft mcIn, ServerData serverDataIn, boolean allowPlaintext) {
      this(parent, mcIn, serverDataIn, null, allowPlaintext);
   }

   public GuiConnecting(GuiScreen parent, Minecraft mcIn, ServerData serverDataIn, String password) {
      this(parent, mcIn, serverDataIn, password, false);
   }

   public GuiConnecting(GuiScreen parent, Minecraft mcIn, ServerData serverDataIn, String password, boolean allowPlaintext) {
      this.mc = mcIn;
      this.previousGuiScreen = parent;
      String serveraddress = AddressResolver.resolveURI(serverDataIn);
      mcIn.loadWorld((WorldClient)null);
      mcIn.setServerData(serverDataIn);
      if(RateLimitTracker.isLockedOut(serveraddress)) {
         LOGGER.error("Server locked this client out on a previous connection, will not attempt to reconnect");
      }else {
         this.connect(serveraddress, password, allowPlaintext, serverDataIn.enableCookies && EagRuntime.getConfiguration().isEnableServerCookies());
      }
   }

   public GuiConnecting(GuiScreen parent, Minecraft mcIn, String hostName, int port) {
      this(parent, mcIn, hostName, port, null, false, EagRuntime.getConfiguration().isEnableServerCookies());
   }

   public GuiConnecting(GuiScreen parent, Minecraft mcIn, String hostName, int port, boolean allowCookies) {
      this(parent, mcIn, hostName, port, null, false, allowCookies);
   }

   public GuiConnecting(GuiScreen parent, Minecraft mcIn, String hostName, int port, boolean allowPlaintext, boolean allowCookies) {
      this(parent, mcIn, hostName, port, null, allowPlaintext, allowCookies);
   }

   public GuiConnecting(GuiScreen parent, Minecraft mcIn, String hostName, int port, String password, boolean allowCookies) {
      this(parent, mcIn, hostName, port, password, false, allowCookies);
   }

   public GuiConnecting(GuiScreen parent, Minecraft mcIn, String hostName, int port, String password, boolean allowPlaintext, boolean allowCookies) {
      this.mc = mcIn;
      this.previousGuiScreen = parent;
      mcIn.loadWorld((WorldClient)null);
      this.connect(AddressResolver.resolveURI(hostName, port), password, allowPlaintext, allowCookies && EagRuntime.getConfiguration().isEnableServerCookies());
   }

   public GuiConnecting(GuiConnecting previous, String password) {
      this(previous, password, false);
   }

   public GuiConnecting(GuiConnecting previous, String password, boolean allowPlaintext) {
      this.mc = previous.mc;
      this.previousGuiScreen = previous.previousGuiScreen;
      this.connect(previous.currentAddress, password, allowPlaintext, previous.allowCookies);
   }

   private void connect(String address, String password, boolean allowPlaintext, boolean allowCookies) {
      this.currentAddress = address;
      this.currentPassword = password;
      this.allowPlaintext = allowPlaintext;
      this.allowCookies = allowCookies;
   }

   public void retryWithAuth(String password, boolean allowPlaintext) {
      this.mc.displayGuiScreen(new GuiConnecting(this, password, allowPlaintext));
   }

   public static Minecraft getMC(GuiConnecting screen) {
      return screen.mc;
   }

   public static GuiScreen getPrevScreen(GuiConnecting screen) {
      return screen.previousGuiScreen;
   }

   public void tick() {
      ++this.timer;
      if(this.timer <= 1 || this.cancel) {
         return;
      }
      if(this.currentAddress == null) {
         this.mc.displayGuiScreen(new GuiDisconnected(this.previousGuiScreen, "connect.failed", new TextComponentTranslation("disconnect.genericReason", "Too many requests")));
         return;
      }
      if(this.webSocket == null) {
         LOGGER.info("Connecting to: {}", this.currentAddress);
         this.webSocket = PlatformNetworking.openWebSocket(this.currentAddress);
         if(this.webSocket == null) {
            this.mc.displayGuiScreen(new GuiDisconnected(this.previousGuiScreen, "connect.failed", new TextComponentString("Could not open WebSocket to \"" + this.currentAddress + "\"!")));
         }
         return;
      }
      EnumEaglerConnectionState state = this.webSocket.getState();
      if(state == EnumEaglerConnectionState.CONNECTED) {
         if(this.handshaker == null) {
            LOGGER.info("Logging in: {}", this.currentAddress);
            byte[] cookieData = null;
            if(this.allowCookies) {
               ServerCookieDataStore.ServerCookie cookie = ServerCookieDataStore.loadCookie(this.currentAddress);
               if(cookie != null) {
                  cookieData = cookie.cookie;
               }
            }
            this.handshaker = new HandshakerHandler(this, this.webSocket, EaglerProfile.getName(), this.currentPassword, this.allowPlaintext, this.allowCookies, cookieData);
         }
         this.handshaker.tick();
      }else if(state == EnumEaglerConnectionState.FAILED || state == EnumEaglerConnectionState.CLOSED) {
         if(this.handshaker != null) {
            this.handshaker.tick();
         }
         this.checkRatelimit();
         if(this.mc.currentScreen == this) {
            if(RateLimitTracker.isProbablyLockedOut(this.currentAddress)) {
               this.mc.displayGuiScreen(new GuiDisconnected(this.previousGuiScreen, "connect.failed", new TextComponentTranslation("disconnect.genericReason", "Too many requests")));
            }else {
               this.mc.displayGuiScreen(new GuiDisconnected(this.previousGuiScreen, "connect.failed", new TextComponentString("Connection Refused")));
            }
         }
      }else if(this.handshaker != null) {
         this.handshaker.tick();
      }
      if(this.timer > 200 && this.mc.currentScreen == this) {
         if(this.webSocket != null) {
            this.webSocket.close();
         }
         this.mc.displayGuiScreen(new GuiDisconnected(this.previousGuiScreen, "connect.failed", new TextComponentString("Handshake timed out")));
      }
   }

   public boolean allowCloseWithEscape() {
      return false;
   }

   protected void initGui() {
      this.addButton(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 120 + 12, I18n.format("gui.cancel")) {
         public void onClick(double mouseX, double mouseY) {
            GuiConnecting.this.cancel = true;
            if(GuiConnecting.this.webSocket != null) {
               GuiConnecting.this.webSocket.close();
            }
            GuiConnecting.this.mc.displayGuiScreen(GuiConnecting.this.previousGuiScreen);
         }
      });
   }

   public void render(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRenderer, I18n.format(this.handshaker == null ? "connect.connecting" : "connect.authorizing"), this.width / 2, this.height / 2 - 50, 16777215);
      super.render(mouseX, mouseY, partialTicks);
   }

   private void checkRatelimit() {
      if(this.webSocket != null) {
         List<IWebSocketFrame> stringFrames = this.webSocket.getNextStringFrames();
         if(stringFrames != null) {
            for(int i = 0, l = stringFrames.size(); i < l; ++i) {
               String str = stringFrames.get(i).getString();
               if(str.equalsIgnoreCase("BLOCKED")) {
                  RateLimitTracker.registerBlock(this.currentAddress);
                  this.mc.displayGuiScreen(new GuiDisconnected(this.previousGuiScreen, "connect.failed", new TextComponentTranslation("disconnect.genericReason", "Too many requests")));
                  LOGGER.info("Handshake Failure: Too Many Requests!");
               }else if(str.equalsIgnoreCase("LOCKED")) {
                  RateLimitTracker.registerLockOut(this.currentAddress);
                  this.mc.displayGuiScreen(new GuiDisconnected(this.previousGuiScreen, "connect.failed", new TextComponentTranslation("disconnect.genericReason", "Too many requests")));
                  LOGGER.info("Handshake Failure: Too Many Requests!");
                  LOGGER.info("Server has locked this client out");
               }
            }
         }
      }
   }
}
