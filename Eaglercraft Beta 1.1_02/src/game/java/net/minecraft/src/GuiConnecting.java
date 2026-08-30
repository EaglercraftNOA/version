package net.minecraft.src;

import net.lax1dude.eaglercraft.EagRuntime;
import net.lax1dude.eaglercraft.internal.EnumEaglerConnectionState;
import net.lax1dude.eaglercraft.internal.IWebSocketClient;
import net.lax1dude.eaglercraft.internal.PlatformNetworking;
import net.minecraft.client.Minecraft;

public class GuiConnecting extends GuiScreen {
	private boolean connected = false;
	private NetClientHandler clientHandler;
	private int timer = 0;
	private String currentAddress;
	private boolean cancelled = false;
	private boolean successful = false;
	private IWebSocketClient webSocket;

	public GuiConnecting(Minecraft var1, String addr) {
		this.currentAddress = addr;
		var1.func_6261_a((World)null);

		if (currentAddress.contains("ws://") && EagRuntime.requireSSL()) {
			currentAddress = currentAddress.replace("ws://", "wss://");
		} else if (!currentAddress.contains("://")) {
			currentAddress = EagRuntime.requireSSL() ? "wss://" + currentAddress : "ws://" + currentAddress;
		}
		this.clientHandler = new NetClientHandler(var1);
	}

	public void updateScreen() {
		++timer;
		if(timer > 1) {
			if(this.webSocket == null) {
				this.webSocket = PlatformNetworking.openWebSocket(this.currentAddress);
				if(this.webSocket == null) {
					this.mc.displayGuiScreen(new GuiConnectFailed("connect.failed", "disconnect.genericReason", new Object[] {"Could not open websocket to\"" + this.currentAddress + "\"!"}));
				}
			} else {
				if(this.webSocket.getState() == EnumEaglerConnectionState.CONNECTED) {
					if(!this.successful) {
						this.clientHandler.netManager.setWebsocketClient(this.webSocket);
						this.clientHandler.addToSendQueue(new Packet2Handshake(this.mc.session.playerName));
						this.successful = true;
						this.connected = true;
					} else {
						this.clientHandler.processReadPackets();
					}
				} else if(this.webSocket.getState() == EnumEaglerConnectionState.FAILED) {
					if(this.webSocket != null) {
						this.webSocket.close();
						this.webSocket = null;
					}
					if (this.currentAddress.contains("ws://") && !EagRuntime.requireSSL()) {
						currentAddress = currentAddress.replace("ws://", "wss://");
						timer = 0;
					} else {
						this.mc.displayGuiScreen(new GuiConnectFailed("connect.failed", "disconnect.genericReason", new Object[]{"Connection Refused!"}));
					}
				}
			}
			if(timer > 200 && !this.connected) {
				if(this.webSocket != null) {
					this.webSocket.close();
					this.webSocket = null;
				}
				this.mc.displayGuiScreen(new GuiConnectFailed("connect.failed", "disconnect.genericReason", new Object[] {"Connection timed out"}));
			}
		}
	}

	protected void keyTyped(char var1, int var2) {
	}

	public void initGui() {
		StringTranslate var1 = StringTranslate.func_20162_a();
		this.controlList.clear();
		this.controlList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 120 + 12, var1.func_20163_a("gui.cancel")));
	}

	protected void actionPerformed(GuiButton var1) {
		if(var1.id == 0) {
			this.cancelled = true;
			if(this.clientHandler != null) {
				this.clientHandler.disconnect();
			}

			this.mc.displayGuiScreen(new GuiMainMenu());
		}

	}

	public void drawScreen(int var1, int var2, float var3) {
		this.drawDefaultBackground();
		StringTranslate var4 = StringTranslate.func_20162_a();
		if(this.clientHandler == null) {
			this.drawCenteredString(this.fontRenderer, var4.func_20163_a("connect.connecting"), this.width / 2, this.height / 2 - 50, 16777215);
			this.drawCenteredString(this.fontRenderer, "", this.width / 2, this.height / 2 - 10, 16777215);
		} else {
			this.drawCenteredString(this.fontRenderer, var4.func_20163_a("connect.authorizing"), this.width / 2, this.height / 2 - 50, 16777215);
			this.drawCenteredString(this.fontRenderer, this.clientHandler.field_1209_a, this.width / 2, this.height / 2 - 10, 16777215);
		}

		super.drawScreen(var1, var2, var3);
	}

	static NetClientHandler setNetClientHandler(GuiConnecting var0, NetClientHandler var1) {
		return var0.clientHandler = var1;
	}

	static boolean isCancelled(GuiConnecting var0) {
		return var0.cancelled;
	}

	static NetClientHandler getNetClientHandler(GuiConnecting var0) {
		return var0.clientHandler;
	}
}
