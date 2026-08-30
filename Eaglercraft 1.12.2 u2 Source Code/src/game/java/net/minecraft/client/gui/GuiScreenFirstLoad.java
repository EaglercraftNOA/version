package net.minecraft.client.gui;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;

public class GuiScreenFirstLoad extends GuiScreen {
	private final GameSettings gameSettings;

	public GuiScreenFirstLoad(GameSettings settings) {
		this.gameSettings = settings;
	}

	@Override
	public void initGui() {
		this.buttonList.clear();
		this.buttonList.add(
				new GuiButton(0, this.width / 2 - 100, this.height / 2 + 30, I18n.format("gui.firstload.confirm")));
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();

		String title = I18n.format("gui.firstload.title");
		this.drawCenteredString(this.fontRendererObj, title, this.width / 2, 70, 0xFF0000);

		String message = I18n.format("gui.firstload.message");
		String[] lines = message.replace("\\n", "\n").split("\n");

		int lineY = 90;
		for (String line : lines) {
			this.drawCenteredString(this.fontRendererObj, line, this.width / 2, lineY, 0xAAAAAA);
			lineY += 12;
		}

		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	protected void actionPerformed(GuiButton button) {
		if (button.id == 0) {
			this.gameSettings.hasSeenFirstLoad = true;
			this.gameSettings.saveOptions();

			this.mc.displayGuiScreen(new GuiMainMenu());
		}
	}

	@Override
	public boolean doesGuiPauseGame() {
		return true;
	}
}