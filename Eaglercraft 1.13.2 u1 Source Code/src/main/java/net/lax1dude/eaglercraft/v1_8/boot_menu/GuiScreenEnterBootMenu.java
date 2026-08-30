package net.lax1dude.eaglercraft.v1_8.boot_menu;

import net.lax1dude.eaglercraft.v1_8.EagRuntime;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

public class GuiScreenEnterBootMenu extends GuiScreen {

	private final GuiScreen parent;

	public GuiScreenEnterBootMenu(GuiScreen parent) {
		this.parent = parent;
	}

	protected void initGui() {
		EagRuntime.setDisplayBootMenuNextRefresh(true);
		this.addButton(new GuiButton(0, this.width / 2 - 100, this.height / 6 + 96, 200, 20, I18n.format("gui.cancel")) {
			public void onClick(double mouseX, double mouseY) {
				GuiScreenEnterBootMenu.this.mc.displayGuiScreen(GuiScreenEnterBootMenu.this.parent);
			}
		});
	}

	public void onGuiClosed() {
		EagRuntime.setDisplayBootMenuNextRefresh(false);
	}

	public void render(int mouseX, int mouseY, float partialTick) {
		this.drawDefaultBackground();
		drawCenteredString(this.fontRenderer, I18n.format("enterBootMenu.title"), this.width / 2, 70, 11184810);
		drawCenteredString(this.fontRenderer, I18n.format("enterBootMenu.text0"), this.width / 2, 90, 16777215);
		super.render(mouseX, mouseY, partialTick);
	}

}
