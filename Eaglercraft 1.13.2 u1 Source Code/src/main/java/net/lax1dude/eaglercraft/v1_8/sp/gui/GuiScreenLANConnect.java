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
import net.lax1dude.eaglercraft.v1_8.minecraft.EnumInputEvent;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;

public class GuiScreenLANConnect extends GuiScreen {

	private final GuiScreen parent;
	private GuiTextField codeTextField;
	private GuiButton joinButton;
	private final GuiNetworkSettingsButton relaysButton;
	private static String lastCode = "";

	public GuiScreenLANConnect(GuiScreen parent) {
		this.parent = parent;
		this.relaysButton = new GuiNetworkSettingsButton(this);
	}

	@Override
	protected void initGui() {
		Keyboard.enableRepeatEvents(true);
		this.buttons.clear();
		//audrey <3
		this.joinButton = this.addButton(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 96 + 12, eaglerFormat("directConnect.lanWorldJoin", "Join Shared World")) {
			public void onClick(double mouseX, double mouseY) {
				GuiScreenLANConnect.this.mc.displayGuiScreen(new GuiScreenLANConnecting(GuiScreenLANConnect.this.parent, GuiScreenLANConnect.this.codeTextField.getText().trim()));
			}
		});
		this.addButton(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 120 + 12, I18n.format("gui.cancel")) {
			public void onClick(double mouseX, double mouseY) {
				GuiScreenLANConnect.this.mc.displayGuiScreen(GuiScreenLANConnect.this.parent);
			}
		});
		this.codeTextField = new GuiTextField(2, this.fontRenderer, this.width / 2 - 100, this.height / 4 + 27, 200, 20);
		this.codeTextField.setMaxStringLength(48);
		this.codeTextField.setFocused(true);
		this.codeTextField.setText(lastCode);
		this.codeTextField.setTextAcceptHandler((id, value) -> updateJoinButton());
		updateJoinButton();
	}

	//audrey <3
	private static String eaglerFormat(String key, String fallback) {
		return I18n.hasKey(key) ? I18n.format(key) : fallback;
	}

	@Override
	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
		lastCode = this.codeTextField.getText().trim();
	}

	private void updateJoinButton() {
		if(joinButton != null && codeTextField != null) {
			joinButton.enabled = this.codeTextField.getText().trim().length() > 0;
		}
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if((keyCode == 257 || keyCode == 335) && joinButton.enabled) {
			this.mc.displayGuiScreen(new GuiScreenLANConnecting(parent, this.codeTextField.getText().trim()));
			return true;
		}
		boolean ret = this.codeTextField.keyPressed(keyCode, scanCode, modifiers) || super.keyPressed(keyCode, scanCode, modifiers);
		updateJoinButton();
		return ret;
	}

	@Override
	public boolean charTyped(char codePoint, int modifiers) {
		boolean ret = this.codeTextField.charTyped(codePoint, modifiers);
		updateJoinButton();
		return ret;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		relaysButton.mouseClicked((int)mouseX, (int)mouseY, button);
		boolean ret = this.codeTextField.mouseClicked(mouseX, mouseY, button) || super.mouseClicked(mouseX, mouseY, button);
		updateJoinButton();
		return ret;
	}

	@Override
	public void tick() {
		this.codeTextField.tick();
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		this.drawCenteredString(this.fontRenderer, I18n.format("selectServer.direct"), this.width / 2, this.height / 4 - 60 + 20, 16777215);
		//audrey <3
		this.drawString(this.fontRenderer, eaglerFormat("directConnect.lanWorldCode", "Shared World Code"), this.width / 2 - 100, this.height / 4 + 12, 10526880);
		//audrey <3
		this.drawCenteredString(this.fontRenderer, eaglerFormat("directConnect.networkSettingsNote", "Use Network Settings if your relay list is not working"), this.width / 2, this.height / 4 + 63, 10526880);
		//audrey <3
		this.drawCenteredString(this.fontRenderer, eaglerFormat("directConnect.ipGrabNote", "Only join shared worlds from people you trust"), this.width / 2, this.height / 4 + 77, 10526880);
		this.codeTextField.drawTextField(mouseX, mouseY, partialTicks);
		super.render(mouseX, mouseY, partialTicks);
		this.relaysButton.drawScreen(mouseX, mouseY);
	}

	@Override
	public boolean showCopyPasteButtons() {
		return codeTextField.isFocused();
	}

	@Override
	public void fireInputEvent(EnumInputEvent event, String param) {
		if(event == EnumInputEvent.CLIPBOARD_COPY) {
			this.mc.keyboardListener.setClipboardString(codeTextField.getSelectedText());
		}else if(event == EnumInputEvent.CLIPBOARD_PASTE) {
			codeTextField.writeText(param != null ? param : this.mc.keyboardListener.getClipboardString());
			updateJoinButton();
		}
	}
}
