/*
 * Copyright (c) 2022-2024 lax1dude, ayunami2000. All Rights Reserved.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 */

package net.lax1dude.eaglercraft.v1_8.sp.gui;

import net.lax1dude.eaglercraft.v1_8.EaglercraftVersion;
import net.lax1dude.eaglercraft.v1_8.internal.PlatformWebRTC;
import net.lax1dude.eaglercraft.v1_8.profile.EaglerProfile;
import net.lax1dude.eaglercraft.v1_8.socket.protocol.handshake.HandshakerHandler;
import net.lax1dude.eaglercraft.v1_8.sp.lan.LANClientNetworkManager;
import net.lax1dude.eaglercraft.v1_8.sp.relay.RelayManager;
import net.lax1dude.eaglercraft.v1_8.sp.relay.RelayServer;
import net.lax1dude.eaglercraft.v1_8.sp.relay.RelayServerSocket;
import net.lax1dude.eaglercraft.v1_8.sp.socket.NetHandlerSingleplayerLogin;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenWorking;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.login.client.CPacketLoginStart;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

import java.io.IOException;

public class GuiScreenLANConnecting extends GuiScreen {

	private final GuiScreen parent;
	private final String code;
	private final RelayServer relay;

	private boolean completed = false;

	private LANClientNetworkManager networkManager = null;

	private int renderCount = 0;

	public GuiScreenLANConnecting(GuiScreen parent, String code) {
		this.parent = parent;
		this.code = code;
		this.relay = null;
	}

	public GuiScreenLANConnecting(GuiScreen parent, String code, RelayServer relay) {
		this.parent = parent;
		this.code = code;
		this.relay = relay;
		Minecraft.getInstance().setServerData(new ServerData("Shared World", "shared:" + relay.address, false));
	}

	// audrey <3
	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	@Override
	public void tick() {
		if(networkManager != null) {
			if (networkManager.isChannelOpen()) {
				try {
					networkManager.processReceivedPackets();
				} catch (IOException ex) {
				}
			} else {
				if (networkManager.checkDisconnected()) {
					this.mc.getSession().reset();
					if (this.mc.currentScreen == this) {
						this.mc.loadWorld(null);
						this.mc.displayGuiScreen(new GuiDisconnected(parent, "connect.failed", new TextComponentString("LAN Connection Refused")));
					}
				}
			}
		}
	}

	@Override
	public void render(int par1, int par2, float par3) {
		this.drawDefaultBackground();
		if(completed) {
			String message = I18n.format("connect.authorizing");
			this.drawString(this.fontRenderer, message, (this.width - this.fontRenderer.getStringWidth(message)) / 2, this.height / 3 + 10, 0xFFFFFF);
		}else {
			GuiScreenWorking ls = this.mc.loadingScreen;
			String message = I18n.format("lanServer.pleaseWait");
			this.drawString(this.fontRenderer, message, (this.width - this.fontRenderer.getStringWidth(message)) / 2, this.height / 3 + 10, 0xFFFFFF);

			PlatformWebRTC.startRTCLANClient();

			if(++renderCount > 1) {
				RelayServerSocket sock;
				if(relay == null) {
					ls.resetProgressAndMessage(new TextComponentString("Connecting to '" + code + "'..."));
					sock = RelayManager.relayManager.getWorkingRelay((str) -> ls.displayLoadingString(new TextComponentString("Connecting: " + str)), 0x02, code);
				}else {
					ls.resetProgressAndMessage(new TextComponentString("Connecting to '" + code + "'..."));
					ls.displayLoadingString(new TextComponentString("Connecting: " + relay.address));
					sock = RelayManager.relayManager.connectHandshake(relay, 0x02, code);
				}
				if(sock == null) {
					this.mc.displayGuiScreen(new GuiScreenNoRelays(parent, I18n.format("noRelay.worldNotFound1").replace("$code$", code),
							I18n.format("noRelay.worldNotFound2").replace("$code$", code), I18n.format("noRelay.worldNotFound3")));
					return;
				}

				networkManager = LANClientNetworkManager.connectToWorld(sock, code, sock.getURI());
				if(networkManager == null) {
					this.mc.displayGuiScreen(new GuiDisconnected(parent, "connect.failed", new TextComponentString(I18n.format("noRelay.worldFail").replace("$code$", code))));
					return;
				}

				completed = true;

				this.mc.getSession().setLAN();
				this.mc.ingameGUI.displayTitle(null, null, -1, -1, -1);
				networkManager.setConnectionState(EnumConnectionState.LOGIN);
				networkManager.setNetHandler(new NetHandlerSingleplayerLogin(networkManager, this.mc, parent));
				networkManager.sendPacket(new CPacketLoginStart(this.mc.getSession().getProfile(),
						EaglerProfile.getSkinPacket(3), EaglerProfile.getCapePacket(),
						HandshakerHandler.getSPHandshakeProtocolData(), EaglercraftVersion.clientBrandUUID));
			}
		}
	}

	public boolean canCloseGui() {
		return false;
	}

}
