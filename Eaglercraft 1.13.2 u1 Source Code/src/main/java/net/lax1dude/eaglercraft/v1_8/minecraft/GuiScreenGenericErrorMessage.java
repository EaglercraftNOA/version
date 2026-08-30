package net.lax1dude.eaglercraft.v1_8.minecraft;

import org.apache.commons.lang3.StringUtils;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

public class GuiScreenGenericErrorMessage extends GuiScreen {

	private final String str1;
	private final String str2;
	private final GuiScreen cont;

	public GuiScreenGenericErrorMessage(String str1, String str2, GuiScreen cont) {
		this.str1 = StringUtils.isAllEmpty(str1) ? "" : I18n.format(str1);
		this.str2 = StringUtils.isAllEmpty(str2) ? "" : I18n.format(str2);
		this.cont = cont;
	}

	protected void initGui() {
		this.addButton(new GuiButton(0, this.width / 2 - 100, this.height / 6 + 96, 200, 20, I18n.format("gui.done")) {
			public void onClick(double mouseX, double mouseY) {
				GuiScreenGenericErrorMessage.this.mc.displayGuiScreen(GuiScreenGenericErrorMessage.this.cont);
			}
		});
	}

	public void render(int mouseX, int mouseY, float partialTick) {
		this.drawDefaultBackground();
		drawCenteredString(this.fontRenderer, str1, this.width / 2, 70, 11184810);
		drawCenteredString(this.fontRenderer, str2, this.width / 2, 90, 16777215);
		super.render(mouseX, mouseY, partialTick);
	}

	public void close() {
		this.mc.displayGuiScreen(cont);
	}
}
