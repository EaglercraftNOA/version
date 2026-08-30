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

import net.lax1dude.eaglercraft.v1_8.Keyboard;
import net.lax1dude.eaglercraft.v1_8.internal.PlatformWebRTC;
import net.lax1dude.eaglercraft.v1_8.minecraft.EnumInputEvent;
import net.lax1dude.eaglercraft.v1_8.sp.SingleplayerServerController;
import net.lax1dude.eaglercraft.v1_8.sp.lan.LANServerController;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenWorking;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.GameType;

public class GuiShareToLan extends GuiScreen {

	private final GuiScreen parentScreen;
	private GuiButton allowCommandsButton;
	private GuiButton gameModeButton;
	private GuiButton hiddenButton;
	private String gameMode;
	private boolean allowCommands = false;
	private final GuiNetworkSettingsButton relaysButton;
	private boolean hiddenToggle = false;
	private GuiTextField codeTextField;

	public GuiShareToLan(GuiScreen parentScreen, String gameMode) {
		this.parentScreen = parentScreen;
		this.relaysButton = new GuiNetworkSettingsButton(this);
		this.gameMode = gameMode;
	}

	@Override
	protected void initGui() {
		Keyboard.enableRepeatEvents(true);
		this.buttons.clear();
		this.addButton(new GuiButton(101, this.width / 2 - 155, this.height - 28, 140, 20, I18n.format("lanServer.start")) {
			public void onClick(double mouseX, double mouseY) {
				GuiShareToLan.this.startLAN();
			}
		});
		this.addButton(new GuiButton(102, this.width / 2 + 5, this.height - 28, 140, 20, I18n.format("gui.cancel")) {
			public void onClick(double mouseX, double mouseY) {
				GuiShareToLan.this.mc.displayGuiScreen(GuiShareToLan.this.parentScreen);
			}
		});
		this.gameModeButton = this.addButton(new GuiButton(104, this.width / 2 - 155, 135, 140, 20, I18n.format("selectWorld.gameMode")) {
			public void onClick(double mouseX, double mouseY) {
				GuiShareToLan.this.cycleGameMode();
			}
		});
		this.allowCommandsButton = this.addButton(new GuiButton(103, this.width / 2 + 5, 135, 140, 20, I18n.format("selectWorld.allowCommands")) {
			public void onClick(double mouseX, double mouseY) {
				if(!GuiShareToLan.this.mc.isDemo()) {
					GuiShareToLan.this.allowCommands = !GuiShareToLan.this.allowCommands;
					GuiShareToLan.this.updateButtonText();
				}
			}
		});
		this.gameModeButton.enabled = this.allowCommandsButton.enabled = !this.mc.isDemo();
		this.hiddenButton = this.addButton(new GuiButton(105, this.width / 2 - 75, 165, 140, 20, I18n.format("lanServer.hidden")) {
			public void onClick(double mouseX, double mouseY) {
				GuiShareToLan.this.hiddenToggle = !GuiShareToLan.this.hiddenToggle;
				GuiShareToLan.this.updateButtonText();
			}
		});
		this.codeTextField = new GuiTextField(0, this.fontRenderer, this.width / 2 - 100, 80, 200, 20);
		this.codeTextField.setText(playerWorldName());
		this.codeTextField.setFocused(true);
		this.codeTextField.setMaxStringLength(252);
		updateButtonText();
	}

	@Override
	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
	}

	private void updateButtonText() {
		this.gameModeButton.displayString = I18n.format("selectWorld.gameMode") + ": " + I18n.format("selectWorld.gameMode." + this.gameMode);
		this.allowCommandsButton.displayString = I18n.format("selectWorld.allowCommands") + " " + I18n.format(this.allowCommands ? "options.on" : "options.off");
		this.hiddenButton.displayString = I18n.format("lanServer.hidden") + " " + I18n.format(this.hiddenToggle ? "options.on" : "options.off");
	}

	private void cycleGameMode() {
		if(!this.mc.isDemo()) {
			if(this.gameMode.equals("survival")) {
				this.gameMode = "creative";
			}else if(this.gameMode.equals("creative")) {
				this.gameMode = "adventure";
			}else if(this.gameMode.equals("adventure")) {
				this.gameMode = "spectator";
			}else {
				this.gameMode = "survival";
			}
			updateButtonText();
		}
	}

	private void startLAN() {
		if(LANServerController.isLANOpen()) {
			return;
		}
		PlatformWebRTC.startRTCLANServer();
		String worldName = this.codeTextField.getText().trim();
		if(worldName.isEmpty()) {
			worldName = playerWorldName();
		}
		if(worldName.length() >= 252) {
			worldName = worldName.substring(0, 252);
		}
		this.mc.displayGuiScreen((GuiScreen)null);
		GuiScreenWorking ls = this.mc.loadingScreen;
		String code = LANServerController.shareToLAN((msg) -> ls.displayLoadingString(new TextComponentString(msg)), worldName, hiddenToggle);
		if(code != null) {
			SingleplayerServerController.configureLAN(GameType.getByName(this.gameMode), this.allowCommands);
			this.mc.ingameGUI.getChatGUI().printChatMessage(new TextComponentString(I18n.format("lanServer.opened")
					.replace("$relay$", LANServerController.getCurrentURI()).replace("$code$", code)));
		}else {
			this.mc.displayGuiScreen(new GuiScreenNoRelays(this, "noRelay.titleFail"));
		}
	}

	private String playerWorldName() {
		return (this.mc.player != null ? this.mc.player.getName().getString() : "Player") + "'s World";
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		return this.codeTextField.keyPressed(keyCode, scanCode, modifiers) || super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean charTyped(char codePoint, int modifiers) {
		return this.codeTextField.charTyped(codePoint, modifiers);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		this.relaysButton.mouseClicked((int)mouseX, (int)mouseY, button);
		return this.codeTextField.mouseClicked(mouseX, mouseY, button) || super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public void tick() {
		this.codeTextField.tick();
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		this.drawCenteredString(this.fontRenderer, I18n.format("lanServer.title"), this.width / 2, 35, 16777215);
		this.drawCenteredString(this.fontRenderer, I18n.format("lanServer.worldName"), this.width / 2, 62, 16777215);
		this.drawCenteredString(this.fontRenderer, I18n.format("lanServer.otherPlayers"), this.width / 2, 112, 16777215);
		this.drawCenteredString(this.fontRenderer, I18n.format("lanServer.ipGrabNote"), this.width / 2, 195, 16777215);
		this.codeTextField.drawTextField(mouseX, mouseY, partialTicks);
		super.render(mouseX, mouseY, partialTicks);
		this.relaysButton.drawScreen(mouseX, mouseY);
	}

	public boolean blockPTTKey() {
		return this.codeTextField.isFocused();
	}

	@Override
	public boolean showCopyPasteButtons() {
		return this.codeTextField.isFocused();
	}

	@Override
	public void fireInputEvent(EnumInputEvent event, String param) {
		if(event == EnumInputEvent.CLIPBOARD_COPY) {
			this.mc.keyboardListener.setClipboardString(codeTextField.getSelectedText());
		}else if(event == EnumInputEvent.CLIPBOARD_PASTE) {
			codeTextField.writeText(param != null ? param : this.mc.keyboardListener.getClipboardString());
		}
	}

}
