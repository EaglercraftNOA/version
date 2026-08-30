/*
 * Copyright (c) 2026. Eaglercraft 1.17 port integration.
 */

package net.lax1dude.eaglercraft.v1_8.sp.gui;

import net.lax1dude.eaglercraft.v1_8.sp.SingleplayerServerController;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;

public class GuiScreenRenameWorld extends GuiScreen {

	private final GuiScreen parent;
	private final String worldName;
	private final String currentDisplayName;
	private GuiTextField nameField;
	private GuiButton renameButton;

	public GuiScreenRenameWorld(GuiScreen parent, String worldName, String currentDisplayName) {
		this.parent = parent;
		this.worldName = worldName;
		this.currentDisplayName = currentDisplayName;
	}

	@Override
	protected void initGui() {
		this.buttons.clear();
		this.nameField = new GuiTextField(0, this.fontRenderer, this.width / 2 - 100, this.height / 4 + 20, 200, 20);
		this.nameField.setText(this.currentDisplayName);
		this.nameField.setFocused(true);
		this.nameField.setTextAcceptHandler((id, value) -> updateButton());
		this.renameButton = this.addButton(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 55, I18n.format("selectWorld.edit")) {
			public void onClick(double mouseX, double mouseY) {
				GuiScreenRenameWorld.this.renameWorld();
			}
		});
		this.addButton(new GuiButton(2, this.width / 2 - 100, this.height / 4 + 80, I18n.format("gui.cancel")) {
			public void onClick(double mouseX, double mouseY) {
				GuiScreenRenameWorld.this.mc.displayGuiScreen(GuiScreenRenameWorld.this.parent);
			}
		});
		updateButton();
	}

	@Override
	public void tick() {
		this.nameField.tick();
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if((keyCode == 257 || keyCode == 335) && this.renameButton.enabled) {
			renameWorld();
			return true;
		}
		return this.nameField.keyPressed(keyCode, scanCode, modifiers) || super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean charTyped(char codePoint, int modifiers) {
		return this.nameField.charTyped(codePoint, modifiers);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		return this.nameField.mouseClicked(mouseX, mouseY, button) || super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		this.drawCenteredString(this.fontRenderer, I18n.format("selectWorld.edit"), this.width / 2, this.height / 4 - 20, 16777215);
		this.drawString(this.fontRenderer, I18n.format("selectWorld.enterName"), this.width / 2 - 100, this.height / 4 + 6, 10526880);
		this.nameField.drawTextField(mouseX, mouseY, partialTicks);
		super.render(mouseX, mouseY, partialTicks);
	}

	private void renameWorld() {
		String displayName = this.nameField.getText().trim();
		if(displayName.isEmpty()) {
			return;
		}
		SingleplayerServerController.instance.renameWorld(this.worldName, displayName);
		this.mc.displayGuiScreen(new GuiScreenIntegratedServerBusy(this.parent, "singleplayer.busy.renaming",
				"singleplayer.failed.renaming", SingleplayerServerController::isReady));
	}

	private void updateButton() {
		if(this.renameButton != null && this.nameField != null) {
			this.renameButton.enabled = !this.nameField.getText().trim().isEmpty();
		}
	}

}
