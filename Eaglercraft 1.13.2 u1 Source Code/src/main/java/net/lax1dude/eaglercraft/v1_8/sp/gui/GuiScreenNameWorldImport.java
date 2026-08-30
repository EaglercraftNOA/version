/*
 * Copyright (c) 2022-2024 lax1dude. All Rights Reserved.
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

import net.lax1dude.eaglercraft.v1_8.EagRuntime;
import net.lax1dude.eaglercraft.v1_8.Keyboard;
import net.lax1dude.eaglercraft.v1_8.internal.FileChooserResult;
import net.lax1dude.eaglercraft.v1_8.minecraft.EnumInputEvent;
import net.lax1dude.eaglercraft.v1_8.sp.SingleplayerServerController;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;

public class GuiScreenNameWorldImport extends GuiScreen {

	private final GuiScreen parentScreen;
	private GuiTextField nameField;
	private GuiButton continueButton;
	private GuiButton loadSpawnChunksButton;
	private GuiButton enhancedGameRulesButton;
	private final int importFormat;
	private final FileChooserResult world;
	private String name;
	private boolean timeToImport = false;
	private boolean definitelyTimeToImport = false;
	private boolean isImporting = false;
	private boolean loadSpawnChunks = false;
	private boolean enhancedGameRules = true;

	public GuiScreenNameWorldImport(GuiScreen menu, FileChooserResult world, int format) {
		this.parentScreen = menu;
		this.importFormat = format;
		this.world = world;
		this.name = world.fileName;
		if(name.length() > 4 && (name.endsWith(".epk") || name.endsWith(".zip"))) {
			name = name.substring(0, name.length() - 4);
		}
	}

	@Override
	public void tick() {
		if(!timeToImport) {
			this.nameField.tick();
		}
		if(definitelyTimeToImport && !isImporting) {
			isImporting = true;
			SingleplayerServerController.importWorld(makeLevelId(this.nameField.getText().trim()), world.fileData,
					importFormat, (byte)((loadSpawnChunks ? 2 : 0) | (enhancedGameRules ? 1 : 0)));
			this.mc.displayGuiScreen(new GuiScreenIntegratedServerBusy(parentScreen,
					"singleplayer.busy.importing." + (importFormat + 1),
					"singleplayer.failed.importing." + (importFormat + 1), SingleplayerServerController::isReady));
		}
	}

	@Override
	protected void initGui() {
		if(!timeToImport) {
			Keyboard.enableRepeatEvents(true);
			this.buttons.clear();
			this.continueButton = this.addButton(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 96 + 12, I18n.format("singleplayer.import.continue")) {
				public void onClick(double mouseX, double mouseY) {
					GuiScreenNameWorldImport.this.timeToImport = true;
					GuiScreenNameWorldImport.this.buttons.clear();
				}
			});
			this.addButton(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 120 + 12, I18n.format("gui.cancel")) {
				public void onClick(double mouseX, double mouseY) {
					EagRuntime.clearFileChooserResult();
					GuiScreenNameWorldImport.this.mc.displayGuiScreen(GuiScreenNameWorldImport.this.parentScreen);
				}
			});
			this.nameField = new GuiTextField(2, this.fontRenderer, this.width / 2 - 100, this.height / 4 + 3, 200, 20);
			this.nameField.setFocused(true);
			this.nameField.setText(name);
			this.nameField.setTextAcceptHandler((id, value) -> updateContinueButton());
			this.loadSpawnChunksButton = this.addButton(new GuiButton(2, this.width / 2 - 100, this.height / 4 + 24 + 12, loadSpawnChunksText()) {
				public void onClick(double mouseX, double mouseY) {
					GuiScreenNameWorldImport.this.loadSpawnChunks = !GuiScreenNameWorldImport.this.loadSpawnChunks;
					GuiScreenNameWorldImport.this.updateToggleButtons();
				}
			});
			this.enhancedGameRulesButton = this.addButton(new GuiButton(3, this.width / 2 - 100, this.height / 4 + 48 + 12, enhancedGameRulesText()) {
				public void onClick(double mouseX, double mouseY) {
					GuiScreenNameWorldImport.this.enhancedGameRules = !GuiScreenNameWorldImport.this.enhancedGameRules;
					GuiScreenNameWorldImport.this.updateToggleButtons();
				}
			});
			updateContinueButton();
		}
	}

	@Override
	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if(!timeToImport && (keyCode == 257 || keyCode == 335) && continueButton != null && continueButton.enabled) {
			timeToImport = true;
			this.buttons.clear();
			return true;
		}
		boolean ret = !timeToImport && this.nameField.keyPressed(keyCode, scanCode, modifiers);
		updateContinueButton();
		return ret || super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean charTyped(char codePoint, int modifiers) {
		if(timeToImport) {
			return false;
		}
		boolean ret = this.nameField.charTyped(codePoint, modifiers);
		updateContinueButton();
		return ret;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		boolean ret = !timeToImport && this.nameField.mouseClicked(mouseX, mouseY, button);
		return ret || super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		if(!timeToImport) {
			this.drawCenteredString(this.fontRenderer, I18n.format("singleplayer.import.title"), this.width / 2, this.height / 4 - 60 + 20, 16777215);
			this.drawString(this.fontRenderer, I18n.format("singleplayer.import.enterName"), this.width / 2 - 100, this.height / 4 - 60 + 50, 10526880);
			this.drawCenteredString(this.fontRenderer, I18n.format("createWorld.seedNote"), this.width / 2, this.height / 4 + 90, -6250336);
			this.nameField.drawTextField(mouseX, mouseY, partialTicks);
		}else {
			definitelyTimeToImport = true;
			long dots = (EagRuntime.steadyTimeMillis() / 500L) % 4L;
			String str = I18n.format("singleplayer.import.reading", world.fileName);
			String suffix = (dots > 0 ? "." : "") + (dots > 1 ? "." : "") + (dots > 2 ? "." : "");
			this.drawString(this.fontRenderer, str + suffix, (this.width - this.fontRenderer.getStringWidth(str)) / 2, this.height / 3 + 10, 0xFFFFFF);
		}
		super.render(mouseX, mouseY, partialTicks);
	}

	private void updateContinueButton() {
		if(this.continueButton != null && this.nameField != null) {
			this.continueButton.enabled = this.nameField.getText().trim().length() > 0;
		}
	}

	private void updateToggleButtons() {
		this.loadSpawnChunksButton.displayString = loadSpawnChunksText();
		this.enhancedGameRulesButton.displayString = enhancedGameRulesText();
	}

	private String loadSpawnChunksText() {
		return I18n.format("singleplayer.import.loadSpawnChunks", loadSpawnChunks ? I18n.format("gui.yes") : I18n.format("gui.no"));
	}

	private String enhancedGameRulesText() {
		return I18n.format("singleplayer.import.enhancedGameRules", enhancedGameRules ? I18n.format("gui.yes") : I18n.format("gui.no"));
	}

	private String makeLevelId(String displayName) {
		return SingleplayerServerController.makeAvailableWorldName(displayName);
	}

	@Override
	public boolean showCopyPasteButtons() {
		return nameField != null && nameField.isFocused();
	}

	@Override
	public void fireInputEvent(EnumInputEvent event, String param) {
		if(nameField == null) {
			return;
		}
		if(event == EnumInputEvent.CLIPBOARD_COPY) {
			this.mc.keyboardListener.setClipboardString(nameField.getSelectedText());
		}else if(event == EnumInputEvent.CLIPBOARD_PASTE) {
			nameField.writeText(param != null ? param : this.mc.keyboardListener.getClipboardString());
			updateContinueButton();
		}
	}

}
