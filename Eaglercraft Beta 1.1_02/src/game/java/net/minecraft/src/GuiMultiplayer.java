package net.minecraft.src;

import org.lwjgl.input.Keyboard;

import net.lax1dude.eaglercraft.profile.EaglerProfile;

public class GuiMultiplayer extends GuiScreen {
	private GuiScreen updateCounter;
	
	private GuiTextField server;
	private GuiTextField username;
	
	public GuiMultiplayer(GuiScreen var1) {
		this.updateCounter = var1;
	}

	public void updateScreen() {
		this.username.onUpdate();
		this.server.onUpdate();
	}

	public void initGui() {
		StringTranslate var1 = StringTranslate.func_20162_a();
		Keyboard.enableRepeatEvents(true);
		if(this.mc.gameSettings.lastServer == null) {
			this.mc.gameSettings.lastServer = "";
		}
		this.controlList.clear();
		this.controlList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 96 + 12, var1.func_20163_a("multiplayer.connect")));
		this.controlList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 120 + 12, var1.func_20163_a("gui.cancel")));
		String server = this.mc.gameSettings.lastServer;
		if(server.contains("_")) {
			server = server.replaceAll("_", ":");
		}
		String username = EaglerProfile.getName();
		if(username == null) {
			username = "";
		}
		
		this.username = new GuiTextField(this.fontRenderer, this.width / 2 - 100, this.height / 4 - 10 + 25 + 18, 200, 20, username);
		this.server = new GuiTextField(this.fontRenderer, this.width / 2 - 100, this.height / 4 - 10 + 65 + 18, 200, 20, server);
		this.username.field_22082_a = true;
		this.username.setMaxLength(16);
		this.server.field_22082_a = false;
		this.server.setMaxLength(128);
		
		((GuiButton)this.controlList.get(0)).enabled = this.server.getTextBoxText().length() > 0 && this.username.getTextBoxText().length()  > 0;
	}

	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
	}

	protected void actionPerformed(GuiButton var1) {
		if(var1.enabled) {
			if(var1.id == 1) {
				this.mc.displayGuiScreen(this.updateCounter);
			} else if(var1.id == 0) {
				String server = this.server.getTextBoxText().trim();
				String username = this.username.getTextBoxText().trim();
				this.mc.gameSettings.lastServer = server.replaceAll(":", "_");
				EaglerProfile.setName(username);
				EaglerProfile.save();
				this.mc.gameSettings.saveOptions();
				this.mc.displayGuiScreen(new GuiConnecting(this.mc, server));
			}

		}
	}
	
	protected void mouseClicked(int var1, int var2, int var3) {
		super.mouseClicked(var1, var2, var3);
		this.server.handleMouseInput(var1, var2, var3);
		this.username.handleMouseInput(var1, var2, var3);
	}

	protected void keyTyped(char var1, int var2) {
		this.server.handleKeyboardInput(var1, var2);
		this.username.handleKeyboardInput(var1, var2);
		
		((GuiButton)this.controlList.get(0)).enabled = this.server.getTextBoxText().length() > 0 && this.username.getTextBoxText().length()  > 0;
	}

	public void drawScreen(int var1, int var2, float var3) {
		StringTranslate var4 = StringTranslate.func_20162_a();
		this.drawDefaultBackground();
		this.drawCenteredString(this.fontRenderer, var4.func_20163_a("multiplayer.title"), this.width / 2, this.height / 4 - 60 + 20, 16777215);
		this.drawString(this.fontRenderer, var4.func_20163_a("multiplayer.info1"), this.width / 2 - 140, this.height / 4 - 60 + 50 + 0, 10526880);
		this.drawString(this.fontRenderer, var4.func_20163_a("multiplayer.info2"), this.width / 2 - 140, this.height / 4 - 60 + 50 + 9, 10526880);
		this.drawString(this.fontRenderer, "Server IP:", this.width / 2 - 100, this.height / 4 - 60 + 85 + 36, 10526880);
		this.drawString(this.fontRenderer, "Username:", this.width / 2 - 100, this.height / 4 - 100 + 85 + 36, 10526880);
		this.server.drawTextBox();
		this.username.drawTextBox();
		super.drawScreen(var1, var2, var3);
	}
}
