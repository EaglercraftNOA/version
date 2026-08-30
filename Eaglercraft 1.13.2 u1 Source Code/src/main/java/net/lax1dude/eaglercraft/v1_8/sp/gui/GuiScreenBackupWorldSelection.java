/*
 * Copyright (c) 2022-2024 lax1dude, ayunami2000. All Rights Reserved.
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

import com.mojang.datafixers.DataFixTypes;

import net.lax1dude.eaglercraft.v1_8.EagRuntime;
import net.lax1dude.eaglercraft.v1_8.profile.EaglerProfile;
import net.lax1dude.eaglercraft.v1_8.sp.SingleplayerServerController;
import net.lax1dude.eaglercraft.v1_8.sp.ipc.IPCPacket05RequestData;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.storage.WorldInfo;

public class GuiScreenBackupWorldSelection extends GuiScreen {

	private final GuiScreen selectWorld;
	private GuiButton worldRecreate;
	private GuiButton worldDuplicate;
	private GuiButton worldExport;
	private GuiButton worldConvert;
	private GuiButton worldBackup;
	private final long worldSeed;
	private final boolean oldRNG;
	private final NBTTagCompound levelDat;
	private final String worldName;

	public GuiScreenBackupWorldSelection(GuiScreen selectWorld, String worldName, NBTTagCompound levelDat) {
		this.selectWorld = selectWorld;
		this.worldName = worldName;
		this.levelDat = levelDat;
		NBTTagCompound data = levelDat.getCompound("Data");
		this.worldSeed = data.getLong("RandomSeed");
		this.oldRNG = data.getInt("eaglerVersionSerial") == 0;
	}

	@Override
	protected void initGui() {
		super.initGui();
		this.worldRecreate = this.addButton(new GuiButton(1, this.width / 2 - 100, this.height / 5 + 5, 200, 20, I18n.format("singleplayer.backup.recreate")) {
			public void onClick(double mouseX, double mouseY) {
				recreateWorld();
			}
		});
		this.worldDuplicate = this.addButton(new GuiButton(2, this.width / 2 - 100, this.height / 5 + 30, 200, 20, I18n.format("singleplayer.backup.duplicate")) {
			public void onClick(double mouseX, double mouseY) {
				GuiScreenBackupWorldSelection.this.mc.displayGuiScreen(new DuplicateWorldScreen(GuiScreenBackupWorldSelection.this, GuiScreenBackupWorldSelection.this.selectWorld, GuiScreenBackupWorldSelection.this.worldName));
			}
		});
		this.worldExport = this.addButton(new GuiButton(3, this.width / 2 - 100, this.height / 5 + 80, 200, 20, I18n.format("singleplayer.backup.export")) {
			public void onClick(double mouseX, double mouseY) {
				exportEaglerWorld();
			}
		});
		this.worldConvert = this.addButton(new GuiButton(4, this.width / 2 - 100, this.height / 5 + 105, 200, 20, I18n.format("singleplayer.backup.vanilla")) {
			public void onClick(double mouseX, double mouseY) {
				exportVanillaWorld();
			}
		});
		this.worldBackup = this.addButton(new GuiButton(5, this.width / 2 - 100, this.height / 5 + 136, 200, 20, I18n.format("singleplayer.backup.clearPlayerData")) {
			public void onClick(double mouseX, double mouseY) {
				GuiScreenBackupWorldSelection.this.mc.displayGuiScreen(new GuiYesNo(GuiScreenBackupWorldSelection.this, I18n.format("singleplayer.backup.clearPlayerData.warning1"), I18n.format("singleplayer.backup.clearPlayerData.warning2", worldName, EaglerProfile.getName()), I18n.format("gui.yes"), I18n.format("gui.cancel"), 5));
			}
		});
		this.addButton(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 155, 200, 20, I18n.format("gui.cancel")) {
			public void onClick(double mouseX, double mouseY) {
				GuiScreenBackupWorldSelection.this.mc.displayGuiScreen(selectWorld);
			}
		});
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		this.drawCenteredString(this.fontRenderer, I18n.format("singleplayer.backup.title", worldName), this.width / 2, this.height / 5 - 35, 16777215);
		String seedText = I18n.format("singleplayer.backup.seed") + " " + worldSeed + (oldRNG ? " " + TextFormatting.RED + "(pre-u34)" : "");
		this.drawCenteredString(this.fontRenderer, seedText, this.width / 2, this.height / 5 + 62, 0xAAAAFF);
		int toolTipColor = 0xDDDDAA;
		if(worldRecreate.isMouseOver()) {
			this.drawCenteredString(this.fontRenderer, I18n.format("singleplayer.backup.recreate.tooltip"), this.width / 2, this.height / 5 - 12, toolTipColor);
		}else if(worldDuplicate.isMouseOver()) {
			this.drawCenteredString(this.fontRenderer, I18n.format("singleplayer.backup.duplicate.tooltip"), this.width / 2, this.height / 5 - 12, toolTipColor);
		}else if(worldExport.isMouseOver()) {
			this.drawCenteredString(this.fontRenderer, I18n.format("singleplayer.backup.export.tooltip"), this.width / 2, this.height / 5 - 12, toolTipColor);
		}else if(worldConvert.isMouseOver()) {
			this.drawCenteredString(this.fontRenderer, I18n.format("singleplayer.backup.vanilla.tooltip"), this.width / 2, this.height / 5 - 12, toolTipColor);
		}else if(worldBackup.isMouseOver()) {
			this.drawCenteredString(this.fontRenderer, I18n.format("singleplayer.backup.clearPlayerData.tooltip"), this.width / 2, this.height / 5 - 12, toolTipColor);
		}
		super.render(mouseX, mouseY, partialTicks);
	}

	private void recreateWorld() {
		NBTTagCompound data = this.levelDat.getCompound("Data");
		int dataVersion = data.getInt("DataVersion");
		WorldInfo worldInfo = new WorldInfo(NBTUtil.update(this.mc.getDataFixer(), DataFixTypes.LEVEL, data, dataVersion), this.mc.getDataFixer(), dataVersion, null);
		GuiCreateWorld createWorld = new GuiCreateWorld(selectWorld);
		createWorld.recreateFromExistingWorld(worldInfo);
		if(oldRNG) {
			this.mc.displayGuiScreen(new GuiScreenOldSeedWarning(createWorld));
		}else {
			this.mc.displayGuiScreen(createWorld);
		}
	}

	private void exportEaglerWorld() {
		SingleplayerServerController.exportWorld(worldName, IPCPacket05RequestData.REQUEST_LEVEL_EAG);
		this.mc.displayGuiScreen(new GuiScreenIntegratedServerBusy(selectWorld, "singleplayer.busy.exporting.1", "singleplayer.failed.exporting.1", () -> {
			byte[] data = SingleplayerServerController.getExportResponse();
			if(data != null) {
				EagRuntime.downloadFileWithName(worldName + ".epk", data);
				return true;
			}
			return false;
		}));
	}

	private void exportVanillaWorld() {
		SingleplayerServerController.exportWorld(worldName, IPCPacket05RequestData.REQUEST_LEVEL_MCA);
		this.mc.displayGuiScreen(new GuiScreenIntegratedServerBusy(selectWorld, "singleplayer.busy.exporting.2", "singleplayer.failed.exporting.2", () -> {
			byte[] data = SingleplayerServerController.getExportResponse();
			if(data != null) {
				EagRuntime.downloadFileWithName(worldName + ".zip", data);
				return true;
			}
			return false;
		}));
	}

	@Override
	public void confirmResult(boolean confirmed, int id) {
		if(confirmed && id == 5) {
			SingleplayerServerController.clearPlayerData(worldName);
			this.mc.displayGuiScreen(new GuiScreenIntegratedServerBusy(this, "singleplayer.busy.clearplayers", "singleplayer.failed.clearplayers", SingleplayerServerController::isReady));
		}else {
			this.mc.displayGuiScreen(this);
		}
	}

	private static class DuplicateWorldScreen extends GuiScreen {

		private final GuiScreen parent;
		private final GuiScreen selectWorld;
		private final String sourceWorldName;
		private GuiTextField nameField;
		private GuiButton duplicateButton;

		private DuplicateWorldScreen(GuiScreen parent, GuiScreen selectWorld, String sourceWorldName) {
			this.parent = parent;
			this.selectWorld = selectWorld;
			this.sourceWorldName = sourceWorldName;
		}

		@Override
		protected void initGui() {
			super.initGui();
			this.nameField = new GuiTextField(0, this.fontRenderer, this.width / 2 - 100, this.height / 4 + 20, 200, 20);
			this.nameField.setText(sourceWorldName + " - Copy");
			this.nameField.setFocused(true);
			this.nameField.setTextAcceptHandler((id, value) -> updateButton());
			this.children.add(this.nameField);
			this.duplicateButton = this.addButton(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 55, 200, 20, I18n.format("singleplayer.backup.duplicate")) {
				public void onClick(double mouseX, double mouseY) {
					duplicateWorld();
				}
			});
			this.addButton(new GuiButton(2, this.width / 2 - 100, this.height / 4 + 80, 200, 20, I18n.format("gui.cancel")) {
				public void onClick(double mouseX, double mouseY) {
					DuplicateWorldScreen.this.mc.displayGuiScreen(parent);
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
			if((keyCode == 257 || keyCode == 335) && this.duplicateButton.enabled) {
				duplicateWorld();
				return true;
			}
			return this.nameField.keyPressed(keyCode, scanCode, modifiers) || super.keyPressed(keyCode, scanCode, modifiers);
		}

		@Override
		public boolean charTyped(char codePoint, int modifiers) {
			return this.nameField.charTyped(codePoint, modifiers);
		}

		@Override
		public void render(int mouseX, int mouseY, float partialTicks) {
			this.drawDefaultBackground();
			this.drawCenteredString(this.fontRenderer, I18n.format("singleplayer.backup.duplicate"), this.width / 2, this.height / 4 - 20, 16777215);
			this.drawString(this.fontRenderer, I18n.format("selectWorld.enterName"), this.width / 2 - 100, this.height / 4 + 6, 10526880);
			this.nameField.drawTextField(mouseX, mouseY, partialTicks);
			super.render(mouseX, mouseY, partialTicks);
		}

		private void duplicateWorld() {
			String displayName = this.nameField.getText().trim();
			if(displayName.isEmpty()) {
				return;
			}
			SingleplayerServerController.duplicateWorld(this.sourceWorldName, displayName);
			this.mc.displayGuiScreen(new GuiScreenIntegratedServerBusy(this.selectWorld, "singleplayer.busy.copying", "singleplayer.failed.copying", SingleplayerServerController::isReady));
		}

		private void updateButton() {
			if(this.duplicateButton != null && this.nameField != null) {
				this.duplicateButton.enabled = !this.nameField.getText().trim().isEmpty();
			}
		}
	}
}
