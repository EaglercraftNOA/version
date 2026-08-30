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

import net.lax1dude.eaglercraft.v1_8.EagRuntime;
import net.lax1dude.eaglercraft.v1_8.Keyboard;
import net.lax1dude.eaglercraft.v1_8.minecraft.EnumInputEvent;
import net.lax1dude.eaglercraft.v1_8.sp.relay.RelayManager;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;

public class GuiScreenAddRelay extends GuiScreen {

	private final GuiScreenRelay parentGui;
	private GuiTextField serverAddress;
	private GuiTextField serverName;
	private GuiButton addButton;
	private GuiButton primaryButton;

	public GuiScreenAddRelay(GuiScreenRelay par1Screen) {
		this.parentGui = par1Screen;
	}

	@Override
	public void tick() {
		this.serverName.tick();
		this.serverAddress.tick();
	}

	@Override
	protected void initGui() {
		super.initGui();
		Keyboard.enableRepeatEvents(true);
		this.parentGui.addNewName = RelayManager.relayManager.makeNewRelayName();
		this.parentGui.addNewAddr = "";
		this.parentGui.addNewPrimary = RelayManager.relayManager.count() == 0;
		int sslOff = EagRuntime.requireSSL() ? 36 : 0;
		this.addButton(addButton = new GuiButton(0, this.width / 2 - 100, this.height / 4 + 96 + 12 + sslOff, 200, 20, I18n.format("addRelay.add")) {
			public void onClick(double mouseX, double mouseY) {
				GuiScreenAddRelay.this.parentGui.addNewName = GuiScreenAddRelay.this.serverName.getText();
				GuiScreenAddRelay.this.parentGui.addNewAddr = GuiScreenAddRelay.this.serverAddress.getText();
				GuiScreenAddRelay.this.parentGui.confirmClicked(true, 0);
			}
		});
		this.addButton(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 120 + 12 + sslOff, 200, 20, I18n.format("gui.cancel")) {
			public void onClick(double mouseX, double mouseY) {
				GuiScreenAddRelay.this.parentGui.confirmClicked(false, 0);
			}
		});
		this.addButton(primaryButton = new GuiButton(2, this.width / 2 - 100, 142, 200, 20, getPrimaryText()) {
			public void onClick(double mouseX, double mouseY) {
				GuiScreenAddRelay.this.parentGui.addNewPrimary = !GuiScreenAddRelay.this.parentGui.addNewPrimary;
				GuiScreenAddRelay.this.primaryButton.displayString = getPrimaryText();
			}
		});
		this.serverName = new GuiTextField(3, this.fontRenderer, this.width / 2 - 100, 106, 200, 20);
		this.serverAddress = new GuiTextField(4, this.fontRenderer, this.width / 2 - 100, 66, 200, 20);
		this.serverAddress.setMaxStringLength(128);
		this.serverAddress.setFocused(true);
		this.serverName.setText(this.parentGui.addNewName);
		this.serverName.setTextAcceptHandler((id, value) -> updateAddButton());
		this.serverAddress.setTextAcceptHandler((id, value) -> updateAddButton());
		this.children.add(this.serverName);
		this.children.add(this.serverAddress);
		updateAddButton();
	}

	private String getPrimaryText() {
		return I18n.format("addRelay.primary") + ": " + (this.parentGui.addNewPrimary ? I18n.format("gui.yes") : I18n.format("gui.no"));
	}

	private void updateAddButton() {
		if(addButton != null && serverAddress != null && serverName != null) {
			addButton.enabled = this.serverAddress.getText().length() > 0
					&& this.serverAddress.getText().split(":").length > 0
					&& this.serverName.getText().length() > 0;
		}
	}

	@Override
	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if(keyCode == 258) {
			if(this.serverName.isFocused()) {
				this.serverName.setFocused(false);
				this.serverAddress.setFocused(true);
			}else {
				this.serverName.setFocused(true);
				this.serverAddress.setFocused(false);
			}
			return true;
		}
		if((keyCode == 257 || keyCode == 335) && addButton.enabled) {
			this.parentGui.addNewName = this.serverName.getText();
			this.parentGui.addNewAddr = this.serverAddress.getText();
			this.parentGui.confirmClicked(true, 0);
			return true;
		}
		boolean ret = this.serverName.keyPressed(keyCode, scanCode, modifiers)
				|| this.serverAddress.keyPressed(keyCode, scanCode, modifiers)
				|| super.keyPressed(keyCode, scanCode, modifiers);
		updateAddButton();
		return ret;
	}

	@Override
	public boolean charTyped(char codePoint, int modifiers) {
		boolean ret = this.serverName.charTyped(codePoint, modifiers) || this.serverAddress.charTyped(codePoint, modifiers);
		updateAddButton();
		return ret;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		boolean ret = super.mouseClicked(mouseX, mouseY, button);
		updateAddButton();
		return ret;
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		this.drawBackground(0);
		this.drawCenteredString(this.fontRenderer, I18n.format("addRelay.title"), this.width / 2, 17, 16777215);
		this.drawString(this.fontRenderer, I18n.format("addRelay.address"), this.width / 2 - 100, 53, 10526880);
		this.drawString(this.fontRenderer, I18n.format("addRelay.name"), this.width / 2 - 100, 94, 10526880);
		if(EagRuntime.requireSSL()) {
			this.drawCenteredString(this.fontRenderer, I18n.format("addServer.SSLWarn1"), this.width / 2, 169, 0xccccff);
			this.drawCenteredString(this.fontRenderer, I18n.format("addServer.SSLWarn2"), this.width / 2, 181, 0xccccff);
		}
		this.serverName.drawTextField(mouseX, mouseY, partialTicks);
		this.serverAddress.drawTextField(mouseX, mouseY, partialTicks);
		super.render(mouseX, mouseY, partialTicks);
	}

	public boolean blockPTTKey() {
		return this.serverName.isFocused() || this.serverAddress.isFocused();
	}

	@Override
	public boolean showCopyPasteButtons() {
		return this.serverName.isFocused() || this.serverAddress.isFocused();
	}

	@Override
	public void fireInputEvent(EnumInputEvent event, String param) {
		GuiTextField box = this.serverName.isFocused() ? this.serverName : this.serverAddress;
		if(event == EnumInputEvent.CLIPBOARD_COPY) {
			this.mc.keyboardListener.setClipboardString(box.getSelectedText());
		}else if(event == EnumInputEvent.CLIPBOARD_PASTE) {
			box.writeText(param != null ? param : this.mc.keyboardListener.getClipboardString());
			updateAddButton();
		}
	}
}
