package net.lax1dude.eaglercraft.v1_8.profile;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

public class GuiScreenDefaultUsernameNote extends GuiScreen {

	private final GuiScreen back;
	private final GuiScreen cont;

	public GuiScreenDefaultUsernameNote(GuiScreen back, GuiScreen cont) {
		this.back = back;
		this.cont = cont;
	}

	@Override
	protected void initGui() {
		//audrey <3
		this.addButton(new GuiButton(0, this.width / 2 - 100, this.height / 6 + 112, 200, 20, eaglerFormat("defaultUsernameDetected.changeUsername", "Change Username")) {
			public void onClick(double mouseX, double mouseY) {
				GuiScreenDefaultUsernameNote.this.mc.displayGuiScreen(GuiScreenDefaultUsernameNote.this.back);
			}
		});
		//audrey <3
		this.addButton(new GuiButton(1, this.width / 2 - 100, this.height / 6 + 142, 200, 20, eaglerFormat("defaultUsernameDetected.continueAnyway", "Continue Anyway")) {
			public void onClick(double mouseX, double mouseY) {
				GuiScreenDefaultUsernameNote.this.mc.displayGuiScreen(GuiScreenDefaultUsernameNote.this.cont);
			}
		});
		//audrey <3
		this.addButton(new GuiButton(2, this.width / 2 - 100, this.height / 6 + 172, 200, 20, eaglerFormat("defaultUsernameDetected.doNotShow", "Do Not Show Again")) {
			public void onClick(double mouseX, double mouseY) {
				GuiScreenDefaultUsernameNote.this.mc.gameSettings.hideDefaultUsernameWarning = true;
				GuiScreenDefaultUsernameNote.this.mc.gameSettings.saveOptions();
				GuiScreenDefaultUsernameNote.this.mc.displayGuiScreen(GuiScreenDefaultUsernameNote.this.cont);
			}
		});
	}

	//audrey <3
	private static String eaglerFormat(String key, String fallback) {
		return I18n.hasKey(key) ? I18n.format(key) : fallback;
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTick) {
		this.drawDefaultBackground();
		//audrey <3
		drawCenteredString(this.fontRenderer, eaglerFormat("defaultUsernameDetected.title", "Default Username Detected"), this.width / 2, 70, 11184810);
		//audrey <3
		drawCenteredString(this.fontRenderer, I18n.hasKey("defaultUsernameDetected.text0") ? I18n.format("defaultUsernameDetected.text0", EaglerProfile.getName()) : "Your username is still " + EaglerProfile.getName() + ".", this.width / 2, 90, 16777215);
		//audrey <3
		drawCenteredString(this.fontRenderer, eaglerFormat("defaultUsernameDetected.text1", "You can change it from Edit Profile."), this.width / 2, 105, 16777215);
		//audrey <3
		drawCenteredString(this.fontRenderer, eaglerFormat("defaultUsernameDetected.text2", "Servers may not recognize default usernames."), this.width / 2, 120, 16777215);
		super.render(mouseX, mouseY, partialTick);
	}

	@Override
	public void close() {
		this.mc.displayGuiScreen(back);
	}
}
