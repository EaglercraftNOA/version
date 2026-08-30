package net.minecraft.client.gui;

import java.io.IOException;

import net.lax1dude.eaglercraft.EagRuntime;
import net.lax1dude.eaglercraft.Keyboard;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.resources.I18n;

public class GuiScreenAddServer extends GuiScreen {
	private final GuiScreen parentScreen;
	private final ServerData serverData;
	private GuiTextField serverIPField;
	private GuiTextField serverNameField;
	private GuiButton serverResourcePacks;
	private GuiButton hideAddress;
	private GuiButton enableCookies;

	public GuiScreenAddServer(GuiScreen p_i1033_1_, ServerData p_i1033_2_) {
		this.parentScreen = p_i1033_1_;
		this.serverData = p_i1033_2_;
	}

	/**
	 * Called from the main game loop to update the screen.
	 */
	public void updateScreen() {
		this.serverNameField.updateCursorCounter();
		this.serverIPField.updateCursorCounter();
	}

	/**
	 * Adds the buttons (and other controls) to the screen in question. Called when
	 * the GUI is displayed and when the window resizes, the buttonList is cleared
	 * beforehand.
	 */
	public void initGui() {
		Keyboard.enableRepeatEvents(true);
		int i = 80;
		this.buttonList.clear();
		GuiButton done;
		GuiButton cancel;
		this.buttonList
				.add(done = new GuiButton(0, this.width / 2 - 100, i + 96 + 12, I18n.format("addServer.add")));
		this.buttonList
				.add(cancel = new GuiButton(1, this.width / 2 - 100, i + 120 + 12, I18n.format("gui.cancel")));
		
		if (EagRuntime.requireSSL()) {
			done.yPosition = cancel.yPosition;
			done.width = (done.width / 2) - 2;
			cancel.width = (cancel.width / 2) - 2;
			done.xPosition += cancel.width + 4;
		}
		this.serverResourcePacks = this.addButton(new GuiButton(2, this.width / 2 - 100, i + 54, I18n.format("addServer.resourcePack") + ": " + this.serverData.getResourceMode().getMotd().getFormattedText()));
		
		if (EagRuntime.getConfiguration().isEnableServerCookies()) {
			this.buttonList.add(this.enableCookies = new GuiButton(4, this.width / 2 - 100, i + 78, 99, 20,
					I18n.format("addServer.enableCookies") + ": "
							+ I18n.format(this.serverData.enableCookies ? "addServer.enableCookies.enabled"
									: "addServer.enableCookies.disabled")));
			this.buttonList.add(this.hideAddress = new GuiButton(3, this.width / 2 + 1, i + 78, 99, 20,
					I18n.format("addServer.hideAddr", new Object[0]) + ": "
							+ I18n.format(this.serverData.hideAddress ? "gui.yes" : "gui.no", new Object[0])));
		} else {
			this.buttonList.add(this.hideAddress = new GuiButton(3, this.width / 2 - 100, i + 78,
					I18n.format("addServer.hideAddress", new Object[0]) + ": "
							+ I18n.format(this.serverData.hideAddress ? "gui.yes" : "gui.no", new Object[0])));
		}
		
		this.serverNameField = new GuiTextField(0, this.fontRendererObj, this.width / 2 - 100, 66, 200, 20);
		this.serverNameField.setFocused(true);
		this.serverNameField.setText(this.serverData.serverName);
		this.serverIPField = new GuiTextField(1, this.fontRendererObj, this.width / 2 - 100, 106, 200, 20);
		this.serverIPField.setMaxStringLength(128);
		this.serverIPField.setText(this.serverData.serverIP);
		(this.buttonList.get(0)).enabled = !this.serverIPField.getText().isEmpty()
				&& this.serverIPField.getText().split(":").length > 0 && !this.serverNameField.getText().isEmpty();
	}

	/**
	 * Called when the screen is unloaded. Used to disable keyboard repeat events
	 */
	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
	}

	/**
	 * Called by the controls from the buttonList when activated. (Mouse pressed for
	 * buttons)
	 */
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.enabled) {
			if (button.id == 3) {
				this.serverData.hideAddress = !this.serverData.hideAddress;
				this.hideAddress.displayString = I18n.format(EagRuntime.getConfiguration().isEnableServerCookies() ? "addServer.hideAddr" : "addServer.hideAddress", new Object[0]) + ": " + I18n.format(this.serverData.hideAddress ? "gui.yes" : "gui.no", new Object[0]);
			} else if (button.id == 4) {
				this.serverData.enableCookies = !this.serverData.enableCookies;
				this.enableCookies.displayString = I18n.format("addServer.enableCookies") + ": " + I18n.format(this.serverData.enableCookies ? "addServer.enableCookies.enabled" : "addServer.enableCookies.disabled");
			} else if (button.id == 2) {
				this.serverData.setResourceMode(
						ServerData.ServerResourceMode.values()[(this.serverData.getResourceMode().ordinal() + 1)
								% ServerData.ServerResourceMode.values().length]);
				this.serverResourcePacks.displayString = I18n.format("addServer.resourcePack") + ": "
						+ this.serverData.getResourceMode().getMotd().getFormattedText();
			} else if (button.id == 1) {
				this.parentScreen.confirmClicked(false, 0);
			} else if (button.id == 0) {
				this.serverData.serverName = this.serverNameField.getText();
				this.serverData.serverIP = this.serverIPField.getText();
				this.parentScreen.confirmClicked(true, 0);
			}
		}
	}

	/**
	 * Fired when a key is typed (except F11 which toggles full screen). This is the
	 * equivalent of KeyListener.keyTyped(KeyEvent e). Args : character (character
	 * on the key), keyCode (lwjgl Keyboard key code)
	 */
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		this.serverNameField.textboxKeyTyped(typedChar, keyCode);
		this.serverIPField.textboxKeyTyped(typedChar, keyCode);

		if (keyCode == 15) {
			this.serverNameField.setFocused(!this.serverNameField.isFocused());
			this.serverIPField.setFocused(!this.serverIPField.isFocused());
		}

		if (keyCode == 28 || keyCode == 156) {
			this.actionPerformed(this.buttonList.get(0));
		}

		(this.buttonList.get(0)).enabled = !this.serverIPField.getText().isEmpty()
				&& this.serverIPField.getText().split(":").length > 0 && !this.serverNameField.getText().isEmpty();
	}

	/**
	 * Called when the mouse is clicked. Args : mouseX, mouseY, clickedButton
	 */
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		this.serverIPField.mouseClicked(mouseX, mouseY, mouseButton);
		this.serverNameField.mouseClicked(mouseX, mouseY, mouseButton);
	}

	/**
	 * Draws the screen and all the components in it.
	 */
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		this.drawCenteredString(this.fontRendererObj, I18n.format("addServer.title"), this.width / 2, 17, 16777215);
		this.drawString(this.fontRendererObj, I18n.format("addServer.enterName"), this.width / 2 - 100, 53, 10526880);
		this.drawString(this.fontRendererObj, I18n.format("addServer.enterIp"), this.width / 2 - 100, 94, 10526880);
		
		if (EagRuntime.requireSSL()) {
			this.drawCenteredString(this.fontRendererObj, I18n.format("addServer.SSLWarn1"), this.width / 2, 184,
					0xccccff);
			this.drawCenteredString(this.fontRendererObj, I18n.format("addServer.SSLWarn2"), this.width / 2, 196,
					0xccccff);
		}
		
		this.serverNameField.drawTextBox();
		this.serverIPField.drawTextBox();
		super.drawScreen(mouseX, mouseY, partialTicks);
	}
}
