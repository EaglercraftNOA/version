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

import net.lax1dude.eaglercraft.v1_8.sp.lan.LANServerController;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiConnecting;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenServerList;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.resources.I18n;

public class GuiScreenConnectOption extends GuiScreen {

	private final GuiScreen guiScreen;
	private String title;
	private String prompt;
	private ServerData directServerData;
	private final GuiNetworkSettingsButton relaysButton;

	public GuiScreenConnectOption(GuiScreen guiScreen) {
		this.guiScreen = guiScreen;
		this.relaysButton = new GuiNetworkSettingsButton(this);
	}

	@Override
	protected void initGui() {
		super.initGui();
		title = I18n.format("selectServer.direct");
		//audrey <3
		prompt = eaglerFormat("directConnect.prompt", "What would you like to do?");
		//audrey <3
		this.addButton(new GuiButton(1, this.width / 2 - 100, this.height / 4 - 60 + 90, 200, 20, eaglerFormat("directConnect.serverJoin", "Connect to Server")) {
			public void onClick(double mouseX, double mouseY) {
				directServerData = new ServerData(I18n.format("selectServer.defaultName"), "", false);
				GuiScreenConnectOption.this.mc.displayGuiScreen(new GuiScreenServerList(GuiScreenConnectOption.this, directServerData));
			}
		});
		//audrey <3
		this.addButton(new GuiButton(2, this.width / 2 - 100, this.height / 4 - 60 + 115, 200, 20, eaglerFormat("directConnect.lanWorld", "Join Shared World")) {
			public void onClick(double mouseX, double mouseY) {
				if(LANServerController.supported()) {
					GuiScreenConnectOption.this.mc.displayGuiScreen(GuiScreenLANInfo.showLANInfoScreen(new GuiScreenLANConnect(guiScreen)));
				}else {
					GuiScreenConnectOption.this.mc.displayGuiScreen(new GuiScreenLANNotSupported(GuiScreenConnectOption.this));
				}
			}
		});
		this.addButton(new GuiButton(0, this.width / 2 - 100, this.height / 4 - 60 + 155, 200, 20, I18n.format("gui.cancel")) {
			public void onClick(double mouseX, double mouseY) {
				GuiScreenConnectOption.this.mc.displayGuiScreen(guiScreen);
			}
		});
	}

	//audrey <3
	private static String eaglerFormat(String key, String fallback) {
		return I18n.hasKey(key) ? I18n.format(key) : fallback;
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		this.drawCenteredString(this.fontRenderer, title, this.width / 2, this.height / 4 - 60 + 20, 16777215);
		this.drawCenteredString(this.fontRenderer, prompt, this.width / 2, this.height / 4 - 60 + 55, 0x999999);
		super.render(mouseX, mouseY, partialTicks);
		relaysButton.drawScreen(mouseX, mouseY);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		relaysButton.mouseClicked((int)mouseX, (int)mouseY, button);
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public void confirmResult(boolean accepted, int id) {
		if(accepted && directServerData != null) {
			this.mc.displayGuiScreen(new GuiConnecting(this, this.mc, directServerData));
		}else {
			this.mc.displayGuiScreen(this);
		}
	}
}
