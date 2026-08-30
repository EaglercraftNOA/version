package net.lax1dude.eaglercraft.v1_8.profile;

import java.io.IOException;

import net.lax1dude.eaglercraft.v1_8.EagRuntime;
import net.lax1dude.eaglercraft.v1_8.internal.FileChooserResult;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

public class GuiScreenImportExportProfile extends GuiScreen {

	private final GuiScreen back;
	private boolean waitingForFile = false;
	private String status = "";

	public GuiScreenImportExportProfile(GuiScreen back) {
		this.back = back;
	}

	@Override
	protected void initGui() {
		this.addButton(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 40, 200, 20, "Import Profile") {
			public void onClick(double mouseX, double mouseY) {
				GuiScreenImportExportProfile.this.waitingForFile = true;
				GuiScreenImportExportProfile.this.status = "Choose a profile backup file";
				EagRuntime.displayFileChooser(null, "epk");
			}
		});
		this.addButton(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 65, 200, 20, "Export Profile") {
			public void onClick(double mouseX, double mouseY) {
				GuiScreenImportExportProfile.this.exportProfile();
			}
		});
		this.addButton(new GuiButton(2, this.width / 2 - 100, this.height / 4 + 130, 200, 20, I18n.format("gui.cancel")) {
			public void onClick(double mouseX, double mouseY) {
				GuiScreenImportExportProfile.this.mc.displayGuiScreen(GuiScreenImportExportProfile.this.back);
			}
		});
	}

	private void exportProfile() {
		try {
			EagRuntime.downloadFileWithName(EaglerProfile.getName() + "-backup.epk", ProfileBackupUtil.exportProfile());
			status = "Profile backup exported";
		} catch (IOException ex) {
			status = "Export failed: " + ex.getMessage();
		}
	}

	@Override
	public void tick() {
		if(waitingForFile && EagRuntime.fileChooserHasResult()) {
			waitingForFile = false;
			FileChooserResult result = EagRuntime.getFileChooserResult();
			if(result != null) {
				try {
					byte[] profileData = ProfileBackupUtil.readProfile(result.fileData);
					EaglerProfile.read(profileData);
					EaglerProfile.loadTextures();
					EagRuntime.setStorage("p", profileData);
					status = "Profile imported";
					this.mc.displayGuiScreen(back);
				} catch (IOException ex) {
					status = "Import failed: " + ex.getMessage();
				}
			}else {
				status = "";
			}
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTick) {
		this.drawDefaultBackground();
		drawCenteredString(this.fontRenderer, "Import / Export Profile", this.width / 2, this.height / 4, 16777215);
		if(!status.isEmpty()) {
			drawCenteredString(this.fontRenderer, status, this.width / 2, this.height / 4 + 105, 10526880);
		}
		super.render(mouseX, mouseY, partialTick);
	}

	@Override
	public void close() {
		this.mc.displayGuiScreen(back);
	}
}
