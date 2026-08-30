/*
 * Copyright (c) 2022-2023 lax1dude, ayunami2000. All Rights Reserved.
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

package net.lax1dude.eaglercraft.v1_8.profile;

import net.lax1dude.eaglercraft.v1_8.Keyboard;
import net.lax1dude.eaglercraft.v1_8.minecraft.EnumInputEvent;
import net.lax1dude.eaglercraft.v1_8.socket.GuiHandshakeApprove;
import net.lax1dude.eaglercraft.v1_8.socket.HandshakePacketTypes;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;

public class GuiAuthenticationScreen extends GuiScreen {

	public interface AuthCallback {
		void authenticate(String password, boolean allowPlaintext);
	}

	private final GuiScreen reconnectScreen;
	private final GuiScreen parent;
	private final AuthCallback callback;
	private GuiButton continueButton;
	private final String message;

	private GuiTextField password;
	private int authTypeForWarning = Integer.MAX_VALUE;
	private boolean allowPlaintext = false;

	public GuiAuthenticationScreen(GuiScreen reconnectScreen, GuiScreen parent, String message) {
		this(reconnectScreen, parent, message, null);
	}

	public GuiAuthenticationScreen(GuiScreen reconnectScreen, GuiScreen parent, String message, AuthCallback callback) {
		this.reconnectScreen = reconnectScreen;
		this.parent = parent;
		this.callback = callback;
		String authRequired = HandshakePacketTypes.AUTHENTICATION_REQUIRED;
		if(message.startsWith(authRequired)) {
			message = message.substring(authRequired.length()).trim();
		}
		if(message.length() > 0 && message.charAt(0) == '[') {
			int idx = message.indexOf(']', 1);
			if(idx != -1) {
				String authType = message.substring(1, idx);
				int type = Integer.MAX_VALUE;
				try {
					type = Integer.parseInt(authType);
				}catch(NumberFormatException ex) {
				}
				if(type != Integer.MAX_VALUE) {
					authTypeForWarning = type;
					message = message.substring(idx + 1).trim();
				}
			}
		}
		this.message = message;
	}

	@Override
	protected void initGui() {
		if(authTypeForWarning != Integer.MAX_VALUE) {
			GuiScreen scr = GuiHandshakeApprove.displayAuthProtocolConfirm(authTypeForWarning, parent, this);
			authTypeForWarning = Integer.MAX_VALUE;
			if(scr != null) {
				mc.displayGuiScreen(scr);
				allowPlaintext = true;
				return;
			}
		}
		Keyboard.enableRepeatEvents(true);
		this.password = new GuiTextField(2, this.fontRenderer, this.width / 2 - 100, this.height / 4 + 40, 200, 20);
		this.password.setTextFormatter((value, cursor) -> stars(value.length()));
		this.password.setTextAcceptHandler((id, value) -> this.continueButton.enabled = value.length() > 0);
		this.password.setFocused(true);
		this.password.setCanLoseFocus(false);
		this.children.add(this.password);
		this.setFocused(this.password);
		this.continueButton = this.addButton(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 80 + 12, 200, 20, I18n.format("auth.continue")) {
			public void onClick(double mouseX, double mouseY) {
				GuiAuthenticationScreen.this.submit();
			}
		});
		continueButton.enabled = false;
		this.addButton(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 80 + 37, 200, 20, I18n.format("gui.cancel")) {
			public void onClick(double mouseX, double mouseY) {
				GuiAuthenticationScreen.this.mc.displayGuiScreen(GuiAuthenticationScreen.this.parent);
			}
		});
	}

	private void submit() {
		String pass = password.getText();
		if(pass.length() > 0) {
			if(callback != null) {
				callback.authenticate(pass, allowPlaintext);
			}else {
				this.mc.displayGuiScreen(reconnectScreen);
			}
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		this.password.drawTextField(mouseX, mouseY, partialTicks);
		drawCenteredString(this.fontRenderer, I18n.format("auth.required"), this.width / 2, this.height / 4 - 5, 16777215);
		drawCenteredString(this.fontRenderer, message, this.width / 2, this.height / 4 + 15, 0xAAAAAA);
		super.render(mouseX, mouseY, partialTicks);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if((keyCode == 257 || keyCode == 335) && password.getText().length() > 0) {
			submit();
			return true;
		}
		boolean ret = this.password.keyPressed(keyCode, scanCode, modifiers) || super.keyPressed(keyCode, scanCode, modifiers);
		this.continueButton.enabled = password.getText().length() > 0;
		return ret;
	}

	@Override
	public boolean charTyped(char codePoint, int modifiers) {
		boolean ret = this.password.charTyped(codePoint, modifiers);
		this.continueButton.enabled = password.getText().length() > 0;
		return ret;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		boolean ret = super.mouseClicked(mouseX, mouseY, button);
		this.continueButton.enabled = password.getText().length() > 0;
		return ret;
	}

	@Override
	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
	}

	public boolean showCopyPasteButtons() {
		return password != null && password.isFocused();
	}

	public void fireInputEvent(EnumInputEvent event, String param) {
		if(password == null || !password.isFocused()) {
			return;
		}
		if(event == EnumInputEvent.CLIPBOARD_PASTE) {
			password.writeText(param != null ? param : mc.keyboardListener.getClipboardString());
		}else if(event == EnumInputEvent.CLIPBOARD_COPY) {
			mc.keyboardListener.setClipboardString(password.getSelectedText());
		}
	}

	private static String stars(int len) {
		char[] ret = new char[len];
		for(int i = 0; i < len; ++i) {
			ret[i] = '*';
		}
		return new String(ret);
	}

}
