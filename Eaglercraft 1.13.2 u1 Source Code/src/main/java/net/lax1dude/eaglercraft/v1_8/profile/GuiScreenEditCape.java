package net.lax1dude.eaglercraft.v1_8.profile;

import net.lax1dude.eaglercraft.v1_8.EagRuntime;
import net.lax1dude.eaglercraft.v1_8.internal.FileChooserResult;
import net.lax1dude.eaglercraft.v1_8.opengl.ImageData;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class GuiScreenEditCape extends GuiScreen {

	private final GuiScreenEditProfile parent;
	private String[] capeNames = new String[0];
	private int selectedSlot = 0;
	private boolean dropDownOpen = false;
	private int scrollPos = 0;
	private int slotsVisible = 0;
	private int listHeight = 0;

	public GuiScreenEditCape(GuiScreenEditProfile parent) {
		this.parent = parent;
	}

	@Override
	protected void initGui() {
		updateOptions();
		selectedSlot = EaglerProfile.presetCapeId == -1 ? EaglerProfile.customCapeId
				: EaglerProfile.presetCapeId + EaglerProfile.customCapes.size();
		clampSelection();
		this.addButton(new GuiButton(0, this.width / 2 - 100, this.height / 6 + 168, 200, 20, I18n.format("gui.done")) {
			public void onClick(double mouseX, double mouseY) {
				GuiScreenEditCape.this.done();
			}
		});
		//audrey <3
		this.addButton(new GuiButton(1, this.width / 2 - 21, this.height / 6 + 80, 71, 20, eaglerFormat("editCape.addCape", "Add Cape")) {
			public void onClick(double mouseX, double mouseY) {
				EagRuntime.displayFileChooser("image/png", "png");
			}
		});
		//audrey <3
		this.addButton(new GuiButton(2, this.width / 2 + 50, this.height / 6 + 80, 72, 20, eaglerFormat("editCape.clearCape", "Clear Cape")) {
			public void onClick(double mouseX, double mouseY) {
				EaglerProfile.clearCustomCapes();
				selectedSlot = 0;
				updateOptions();
				saveCape();
				EaglerProfile.save();
			}
		});
	}

	//audrey <3
	private static String eaglerFormat(String key, String fallback) {
		return I18n.hasKey(key) ? I18n.format(key) : fallback;
	}

	private void done() {
		saveCape();
		EaglerProfile.save();
		this.mc.displayGuiScreen(parent);
	}

	@Override
	public void tick() {
		if(EagRuntime.fileChooserHasResult()) {
			FileChooserResult result = EagRuntime.getFileChooserResult();
			if(result != null) {
				ImageData loadedCape = ImageData.loadImageFile(result.fileData, ImageData.getMimeFromType(result.fileName));
				if(loadedCape != null) {
					if((loadedCape.width == 32 || loadedCape.width == 64) && loadedCape.height == 32) {
						byte[] resized = new byte[1173];
						SkinConverter.convertCape32x32RGBAto23x17RGB(loadedCape, resized);
						int slot = EaglerProfile.addCustomCape(result.fileName, resized);
						if(slot != -1) {
							selectedSlot = slot;
							updateOptions();
							saveCape();
							EaglerProfile.save();
						}
					}else {
						EagRuntime.showPopup("The selected image '" + result.fileName
								+ "' is not the right size!\nEaglercraft only supports 32x32 or 64x32 capes");
					}
				}else {
					EagRuntime.showPopup("The selected file '" + result.fileName + "' is not a supported format!");
				}
			}
		}
	}

	@Override
	public void render(int mouseX, int mouseY, float partialTick) {
		this.drawDefaultBackground();
		//audrey <3
		drawCenteredString(this.fontRenderer, eaglerFormat("editCape.title", "Edit Cape"), this.width / 2, 15, 16777215);
		//audrey <3
		drawString(this.fontRenderer, eaglerFormat("editCape.playerCape", "Player Cape"), this.width / 2 - 20, this.height / 6 + 36, 10526880);

		int previewX = this.width / 2 - 120;
		int previewY = this.height / 6 + 8;
		drawRect(previewX, previewY, previewX + 80, previewY + 130, 0xFFA0A0A0);
		drawRect(previewX + 1, previewY + 1, previewX + 79, previewY + 129, 0xFF000015);

		drawSelector(mouseX, mouseY, this.height / 6 + 52);
		super.render(dropDownOpen ? this.width / 2 : mouseX, dropDownOpen ? this.height / 2 : mouseY, partialTick);

		ResourceLocation skinTexture = parent.getSelectedSkinTexture();
		SkinModel skinModel = parent.getSelectedSkinModel();
		ResourceLocation capeTexture = getSelectedCapeTexture();
		SkinPreviewRenderer.renderPreview(previewX, previewY + 122, mouseX, mouseY, true, skinModel, skinTexture, capeTexture);

		if(dropDownOpen) {
			drawDropdown(mouseX, mouseY, this.height / 6 + 73);
		}
	}

	private void drawSelector(int mouseX, int mouseY, int y) {
		int x = this.width / 2 - 20;
		drawRect(x, y, x + 140, y + 22, -6250336);
		drawRect(x + 1, y + 1, x + 119, y + 21, -16777216);
		drawRect(x + 120, y + 1, x + 139, y + 21, -16777216);
		drawString(this.fontRenderer, capeNames[selectedSlot], x + 5, y + 7, 14737632);
		drawCenteredString(this.fontRenderer, dropDownOpen ? "^" : "v", x + 130, y + 6, 14737632);
	}

	private void drawDropdown(int mouseX, int mouseY, int y) {
		int x = this.width / 2 - 20;
		int width = 140;
		int available = Math.max(1, this.height - y - 10);
		slotsVisible = Math.min(capeNames.length, Math.max(1, available / 10));
		listHeight = slotsVisible * 10 + 7;
		scrollPos = MathHelper.clamp(scrollPos, 0, Math.max(0, capeNames.length - slotsVisible));
		drawRect(x, y, x + width, y + listHeight, -6250336);
		drawRect(x + 1, y + 1, x + width - 1, y + listHeight - 1, -16777216);
		for(int i = 0; i < slotsVisible; ++i) {
			int slot = i + scrollPos;
			if(slot >= capeNames.length) {
				break;
			}
			int rowY = y + 5 + i * 10;
			if(slot == selectedSlot) {
				drawRect(x + 1, rowY - 1, x + width - 1, rowY + 9, 0x77FFFFFF);
			}else if(mouseX >= x && mouseX < x + width - 10 && mouseY >= rowY && mouseY < rowY + 10) {
				drawRect(x + 1, rowY - 1, x + width - 1, rowY + 9, 0x55FFFFFF);
			}
			drawString(this.fontRenderer, capeNames[slot], x + 5, rowY, 14737632);
		}
		if(capeNames.length > slotsVisible) {
			int barSize = Math.max(8, listHeight * slotsVisible / capeNames.length);
			int barY = y + 1 + (listHeight - barSize - 2) * scrollPos / Math.max(1, capeNames.length - slotsVisible);
			drawRect(x + width - 4, barY, x + width - 1, barY + barSize, 0xFF888888);
		}
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if(button == 0) {
			int selectorX = this.width / 2 - 20;
			int selectorY = this.height / 6 + 52;
			if(mouseX >= selectorX && mouseX < selectorX + 140 && mouseY >= selectorY && mouseY < selectorY + 22) {
				dropDownOpen = !dropDownOpen;
				scrollPos = MathHelper.clamp(selectedSlot - 2, 0, Math.max(0, capeNames.length - slotsVisible));
				return true;
			}
			if(dropDownOpen) {
				int listX = this.width / 2 - 20;
				int listY = this.height / 6 + 73;
				if(mouseX >= listX && mouseX < listX + 130 && mouseY >= listY && mouseY < listY + listHeight) {
					int slot = scrollPos + ((int)mouseY - listY - 5) / 10;
					if(slot >= 0 && slot < capeNames.length) {
						selectedSlot = slot;
						dropDownOpen = false;
						saveCape();
					}
					return true;
				}
				dropDownOpen = false;
				return true;
			}
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseScrolled(double delta) {
		if(dropDownOpen) {
			scrollPos = MathHelper.clamp(scrollPos - (int)Math.signum(delta) * 3, 0, Math.max(0, capeNames.length - slotsVisible));
			return true;
		}
		return super.mouseScrolled(delta);
	}

	private void updateOptions() {
		int custom = EaglerProfile.customCapes.size();
		capeNames = new String[custom + DefaultCapes.defaultCapesMap.length];
		for(int i = 0; i < custom; ++i) {
			capeNames[i] = EaglerProfile.customCapes.get(i).name;
		}
		for(int i = 0; i < DefaultCapes.defaultCapesMap.length; ++i) {
			capeNames[custom + i] = DefaultCapes.defaultCapesMap[i].name;
		}
		clampSelection();
	}

	private void clampSelection() {
		if(capeNames.length == 0) {
			selectedSlot = 0;
		}else {
			selectedSlot = MathHelper.clamp(selectedSlot, 0, capeNames.length - 1);
		}
	}

	private ResourceLocation getSelectedCapeTexture() {
		int custom = EaglerProfile.customCapes.size();
		if(selectedSlot < custom) {
			return EaglerProfile.customCapes.get(selectedSlot).getResource();
		}
		return DefaultCapes.getCapeFromId(selectedSlot - custom).location;
	}

	private void saveCape() {
		int custom = EaglerProfile.customCapes.size();
		if(selectedSlot < custom) {
			EaglerProfile.presetCapeId = -1;
			EaglerProfile.customCapeId = selectedSlot;
		}else {
			EaglerProfile.presetCapeId = selectedSlot - custom;
			EaglerProfile.customCapeId = -1;
		}
	}

	@Override
	public void close() {
		done();
	}
}
