package net.eymenwsmc;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

public class GuiAlert extends GuiScreen {
	private GuiScreen parent;
	private String title;
	private String message;

	/*
	 * title is the title message is the message parent is when OK clicked it
	 * displays the wanted gui
	 */
	public GuiAlert(String title, String message, GuiScreen parent) {
		this.title = title;
		this.message = message;
		this.parent = parent;
	}

	@Override
	public void initGui() {
		this.buttonList.clear();
		int btnWidth = 100;
		int btnHeight = 20;
		int btnX = (this.width - btnWidth) / 2;
		int btnY = (this.height) / 2 + 30;
		this.buttonList.add(new GuiButton(1, btnX, btnY, btnWidth, btnHeight, "OK"));
	}

	/*
	 * Draws the screen with uh title and the text
	 */
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		drawRect(0, 0, this.width, this.height, 0x88000000);
		int boxWidth = 300;
		int boxHeight = 120;
		int boxX = (this.width - boxWidth) / 2;
		int boxY = (this.height - boxHeight) / 2;
		drawRect(boxX, boxY, boxX + boxWidth, boxY + boxHeight, 0xCC222222);
		this.mc.fontRenderer.drawString(this.title,
				this.width / 2 - this.mc.fontRenderer.getStringWidth(this.title) / 2, boxY + 20, 0xff0000);
		int messageMargin = 20;
		int messageWidth = boxWidth - 2 * messageMargin;
		int messageX = boxX + messageMargin + 10;
		int messageY = boxY + 40;
		this.mc.fontRenderer.drawSplitString(this.message, messageX, messageY, messageWidth, 0xCCCCCC);
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		if (button.id == 1) {
			mc.displayGuiScreen(parent);
		}
	}
}
