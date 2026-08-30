package net.lax1dude.eaglercraft.v1_8.profile;

import net.lax1dude.eaglercraft.v1_8.EagRuntime;
import net.lax1dude.eaglercraft.v1_8.Keyboard;
import net.lax1dude.eaglercraft.v1_8.internal.FileChooserResult;
import net.lax1dude.eaglercraft.v1_8.minecraft.EnumInputEvent;
import net.lax1dude.eaglercraft.v1_8.opengl.ImageData;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;

public class GuiScreenEditProfile extends GuiScreen {

	private final GuiScreen parent;
	private static final ResourceLocation eaglerGui = new ResourceLocation("eagler:gui/eagler_gui.png");

	private GuiTextField usernameField;
	private String[] skinNames = new String[0];
	private int selectedSlot = 0;
	private boolean dropDownOpen = false;
	private int scrollPos = 0;
	private int slotsVisible = 0;
	private int listHeight = 0;
	private boolean chooseNewSkinModel = false;

	public GuiScreenEditProfile(GuiScreen parent) {
		this.parent = parent;
	}

	@Override
	protected void initGui() {
		Keyboard.enableRepeatEvents(true);
		updateOptions();
		selectedSlot = EaglerProfile.presetSkinId == -1 ? EaglerProfile.customSkinId
				: EaglerProfile.presetSkinId + EaglerProfile.customSkins.size();
		clampSelection();

		this.usernameField = new GuiTextField(0, this.fontRenderer, this.width / 2 - 19, this.height / 6 + 25, 138, 20);
		this.usernameField.setMaxStringLength(16);
		this.usernameField.setText(EaglerProfile.getName());
		this.usernameField.setValidator((value) -> value.matches("[A-Za-z0-9_]*"));
		this.usernameField.setFocused(true);
		this.children.add(this.usernameField);
		this.setFocused(this.usernameField);

		this.addButton(new GuiButton(0, this.width / 2 - 100, this.height / 6 + 168, 200, 20, I18n.format("gui.done")) {
			public void onClick(double mouseX, double mouseY) {
				GuiScreenEditProfile.this.done();
			}
		});
		//audrey <3
		this.addButton(new GuiButton(1, this.width / 2 - 21, this.height / 6 + 110, 71, 20, eaglerFormat("editProfile.addSkin", "Add Skin")) {
			public void onClick(double mouseX, double mouseY) {
				EagRuntime.displayFileChooser("image/png", "png");
			}
		});
		//audrey <3
		this.addButton(new GuiButton(2, this.width / 2 + 50, this.height / 6 + 110, 72, 20, eaglerFormat("editProfile.clearSkin", "Clear Skin")) {
			public void onClick(double mouseX, double mouseY) {
				EaglerProfile.clearCustomSkins();
				selectedSlot = 0;
				chooseNewSkinModel = false;
				updateOptions();
				saveProfile();
				EaglerProfile.save();
			}
		});
	}

	//audrey <3
	private static String eaglerFormat(String key, String fallback) {
		return I18n.hasKey(key) ? I18n.format(key) : fallback;
	}

	private boolean capesLinkVisible() {
		SkinModel m = getSelectedSkinModel();
		return m == SkinModel.STEVE || m == SkinModel.ALEX || (m.highPoly != null && !this.mc.gameSettings.enableFNAWSkins);
	}

	private int previewLeft() {
		return this.width / 2 - 120;
	}

	private int previewTop() {
		return this.height / 6 + 8;
	}

	private boolean capesLinkHovered(double mouseX, double mouseY) {
		//audrey <3
		int textW = (int)(this.fontRenderer.getStringWidth(eaglerFormat("editProfile.capes", "Capes")) * 0.75F);
		int textH = (int)(this.fontRenderer.FONT_HEIGHT * 0.75F);
		int x = previewLeft();
		int y = previewTop() - textH - 2;
		return mouseX >= x - 6 && mouseX < x + textW + 6 && mouseY >= y - 2 && mouseY < y + textH + 2;
	}

	private boolean importExportHovered(double mouseX, double mouseY) {
		//audrey <3
		int w = (int)(this.fontRenderer.getStringWidth(eaglerFormat("editProfile.importExport", "Import/Export Profile")) * 0.75F);
		return mouseX >= 3 && mouseX < 5 + w + 2 && mouseY >= 3 && mouseY < 14;
	}

	private void done() {
		saveProfile();
		EaglerProfile.save();
		if(!this.mc.gameSettings.hideDefaultUsernameWarning && EaglerProfile.isDefaultUsername(EaglerProfile.getName())) {
			this.mc.displayGuiScreen(new GuiScreenDefaultUsernameNote(this, parent));
		}else {
			this.mc.displayGuiScreen(parent);
		}
	}

	@Override
	public void tick() {
		this.usernameField.tick();
		if(EagRuntime.fileChooserHasResult()) {
			FileChooserResult result = EagRuntime.getFileChooserResult();
			if(result != null) {
				ImageData loadedSkin = ImageData.loadImageFile(result.fileData, ImageData.getMimeFromType(result.fileName));
				if(loadedSkin != null) {
					boolean legacy = loadedSkin.width == 64 && loadedSkin.height == 32;
					boolean modern = loadedSkin.width == 64 && loadedSkin.height == 64;
					if(legacy) {
						ImageData newSkin = new ImageData(64, 64, true);
						SkinConverter.convert64x32to64x64(loadedSkin, newSkin);
						loadedSkin = newSkin;
						modern = true;
					}
					if(modern) {
						byte[] rawSkin = new byte[16384];
						for(int i = 0; i < 4096; ++i) {
							int j = i << 2;
							int k = loadedSkin.pixels[i];
							rawSkin[j] = (byte)(k >>> 24);
							rawSkin[j + 1] = (byte)(k >>> 16);
							rawSkin[j + 2] = (byte)(k >>> 8);
							rawSkin[j + 3] = (byte)(k & 0xFF);
						}
						for(int y = 20; y < 32; ++y) {
							for(int x = 16; x < 40; ++x) {
								rawSkin[(y << 8) | (x << 2)] = (byte)0xff;
							}
						}
						int slot = EaglerProfile.addCustomSkin(result.fileName, rawSkin);
						if(slot != -1) {
							selectedSlot = slot;
							chooseNewSkinModel = true;
							updateOptions();
							saveProfile();
							EaglerProfile.save();
						}
					}else {
						EagRuntime.showPopup("The selected image '" + result.fileName
								+ "' is not the right size!\nEaglercraft only supports 64x32 or 64x64 skins");
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
		drawCenteredString(this.fontRenderer, eaglerFormat("editProfile.title", "Edit Profile"), this.width / 2, 15, 16777215);
		//audrey <3
		drawString(this.fontRenderer, eaglerFormat("editProfile.username", "Username"), this.width / 2 - 20, this.height / 6 + 8, 10526880);
		//audrey <3
		drawString(this.fontRenderer, eaglerFormat("editProfile.playerSkin", "Player Skin"), this.width / 2 - 20, this.height / 6 + 66, 10526880);

		int previewX = previewLeft();
		int previewY = previewTop();
		drawRect(previewX, previewY, previewX + 80, previewY + 130, 0xFFA0A0A0);
		drawRect(previewX + 1, previewY + 1, previewX + 79, previewY + 129, 0xFF000015);

		drawClickableText(mouseX, mouseY);

		drawSelector(mouseX, mouseY);
		this.usernameField.drawTextField(mouseX, mouseY, partialTick);
		super.render((dropDownOpen || chooseNewSkinModel) ? this.width / 2 : mouseX, (dropDownOpen || chooseNewSkinModel) ? this.height / 2 : mouseY, partialTick);

		SkinModel selectedModel = getSelectedSkinModel();
		SkinPreviewRenderer.renderPreview(previewX, previewY + 122, (dropDownOpen || chooseNewSkinModel) ? this.width / 2 : mouseX,
				(dropDownOpen || chooseNewSkinModel) ? this.height / 2 : mouseY, false, selectedModel, getSelectedSkinTexture(), EaglerProfile.getActiveCapeResourceLocation());

		if(selectedModel.highPoly != null) {
			//audrey <3
			String note = this.mc.gameSettings.enableFNAWSkins ? eaglerFormat("editProfile.disableFNAW", "Disable FNAW skins in options") : eaglerFormat("editProfile.enableFNAW", "Enable FNAW skins in options");
			drawCenteredString(this.fontRenderer, note, this.width / 2, this.height / 6 + 150, 10526880);
		}

		if(dropDownOpen) {
			drawDropdown(mouseX, mouseY);
		}

		if(chooseNewSkinModel && selectedSlot < EaglerProfile.customSkins.size()) {
			drawModelChoice(mouseX, mouseY);
		}
	}

	private void drawClickableText(int mouseX, int mouseY) {
		if(capesLinkVisible()) {
			int color = capesLinkHovered(mouseX, mouseY) ? 0xFFCCCC44 : 10526880;
			int textH = (int)(this.fontRenderer.FONT_HEIGHT * 0.75F);
			GlStateManager.pushMatrix();
			GlStateManager.translatef(previewLeft(), previewTop() - textH - 2, 0.0F);
			GlStateManager.scalef(0.75F, 0.75F, 0.75F);
			//audrey <3
			drawString(this.fontRenderer, TextFormatting.UNDERLINE + eaglerFormat("editProfile.capes", "Capes"), 0, 0, color);
			GlStateManager.popMatrix();
		}

		if(!EagRuntime.getConfiguration().isDemo()) {
			int color = importExportHovered(mouseX, mouseY) ? 0xFFEEEE22 : 0xFFCCCCCC;
			GlStateManager.pushMatrix();
			GlStateManager.translatef(5.0F, 5.0F, 0.0F);
			GlStateManager.scalef(0.75F, 0.75F, 0.75F);
			//audrey <3
			drawString(this.fontRenderer, TextFormatting.UNDERLINE + eaglerFormat("editProfile.importExport", "Import/Export Profile"), 0, 0, color);
			GlStateManager.popMatrix();
		}
	}

	private void drawSelector(int mouseX, int mouseY) {
		int x = this.width / 2 - 20;
		int y = this.height / 6 + 82;
		drawRect(x, y, x + 140, y + 22, -6250336);
		drawRect(x + 1, y + 1, x + 119, y + 21, -16777216);
		drawRect(x + 120, y + 1, x + 139, y + 21, -16777216);
		GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
		this.mc.getTextureManager().bindTexture(eaglerGui);
		drawTexturedModalRect(x + 122, y + 3, 0, 0, 16, 16);
		drawString(this.fontRenderer, skinNames[selectedSlot], x + 5, y + 7, 14737632);
	}

	private void drawDropdown(int mouseX, int mouseY) {
		int x = this.width / 2 - 20;
		int y = this.height / 6 + 103;
		int width = 140;
		int available = Math.max(1, this.height - y - 10);
		slotsVisible = Math.min(skinNames.length, Math.max(1, available / 10));
		listHeight = slotsVisible * 10 + 7;
		scrollPos = MathHelper.clamp(scrollPos, 0, Math.max(0, skinNames.length - slotsVisible));
		drawRect(x, y, x + width, y + listHeight, -6250336);
		drawRect(x + 1, y + 1, x + width - 1, y + listHeight - 1, -16777216);
		for(int i = 0; i < slotsVisible; ++i) {
			int slot = i + scrollPos;
			if(slot >= skinNames.length) {
				break;
			}
			int rowY = y + 5 + i * 10;
			if(slot == selectedSlot) {
				drawRect(x + 1, rowY - 1, x + width - 1, rowY + 9, 0x77FFFFFF);
			}else if(mouseX >= x && mouseX < x + width - 10 && mouseY >= rowY && mouseY < rowY + 10) {
				drawRect(x + 1, rowY - 1, x + width - 1, rowY + 9, 0x55FFFFFF);
			}
			drawString(this.fontRenderer, skinNames[slot], x + 5, rowY, 14737632);
		}
		if(skinNames.length > slotsVisible) {
			int barSize = Math.max(8, listHeight * slotsVisible / skinNames.length);
			int barY = y + 1 + (listHeight - barSize - 2) * scrollPos / Math.max(1, skinNames.length - slotsVisible);
			drawRect(x + width - 4, barY, x + width - 1, barY + barSize, 0xFF888888);
		}
	}

	private void drawModelChoice(int mouseX, int mouseY) {
		drawRect(0, 0, this.width, this.height, 0xBB000000);
		int steveX = this.width / 2 - 90;
		int alexX = this.width / 2 + 20;
		int y = this.height / 4;
		drawChoiceBox(steveX, y, mouseX, mouseY, "Steve", SkinModel.STEVE);
		drawChoiceBox(alexX, y, mouseX, mouseY, "Alex", SkinModel.ALEX);
	}

	private void drawChoiceBox(int x, int y, int mouseX, int mouseY, String label, SkinModel model) {
		boolean hover = mouseX >= x && mouseX < x + 70 && mouseY >= y && mouseY < y + 120;
		int color = hover ? 0xFFDDDD99 : 0xFF555555;
		drawRect(x, y, x + 70, y + 120, 0xBB000000);
		drawRect(x, y, x + 70, y + 1, color);
		drawRect(x, y + 119, x + 70, y + 120, color);
		drawRect(x, y, x + 1, y + 120, color);
		drawRect(x + 69, y, x + 70, y + 120, color);
		SkinPreviewRenderer.renderPreview(x - 5, y + 117, mouseX, mouseY, false, model, EaglerProfile.customSkins.get(selectedSlot).getResource(), EaglerProfile.getActiveCapeResourceLocation());
		drawCenteredString(this.fontRenderer, label, x + 35, y + 126, color);
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		this.usernameField.mouseClicked(mouseX, mouseY, button);
		if(button == 0) {
			if(!dropDownOpen && !chooseNewSkinModel) {
				if(!EagRuntime.getConfiguration().isDemo() && importExportHovered(mouseX, mouseY)) {
					saveProfile();
					EaglerProfile.save();
					this.mc.displayGuiScreen(new GuiScreenImportExportProfile(this));
					return true;
				}
				if(capesLinkVisible() && capesLinkHovered(mouseX, mouseY)) {
					saveProfile();
					this.mc.displayGuiScreen(new GuiScreenEditCape(this));
					return true;
				}
			}

			if(chooseNewSkinModel && selectedSlot < EaglerProfile.customSkins.size()) {
				int y = this.height / 4;
				if(mouseY >= y && mouseY < y + 120) {
					if(mouseX >= this.width / 2 - 90 && mouseX < this.width / 2 - 20) {
						EaglerProfile.customSkins.get(selectedSlot).model = SkinModel.STEVE;
						chooseNewSkinModel = false;
						saveProfile();
						EaglerProfile.save();
						return true;
					}
					if(mouseX >= this.width / 2 + 20 && mouseX < this.width / 2 + 90) {
						EaglerProfile.customSkins.get(selectedSlot).model = SkinModel.ALEX;
						chooseNewSkinModel = false;
						saveProfile();
						EaglerProfile.save();
						return true;
					}
				}
				return true;
			}

			int selectorX = this.width / 2 - 20;
			int selectorY = this.height / 6 + 82;
			if(mouseX >= selectorX && mouseX < selectorX + 140 && mouseY >= selectorY && mouseY < selectorY + 22) {
				dropDownOpen = !dropDownOpen;
				scrollPos = MathHelper.clamp(selectedSlot - 2, 0, Math.max(0, skinNames.length - slotsVisible));
				return true;
			}
			if(dropDownOpen) {
				int listX = this.width / 2 - 20;
				int listY = this.height / 6 + 103;
				if(mouseX >= listX && mouseX < listX + 130 && mouseY >= listY && mouseY < listY + listHeight) {
					int slot = scrollPos + ((int)mouseY - listY - 5) / 10;
					if(slot >= 0 && slot < skinNames.length) {
						selectedSlot = slot;
						dropDownOpen = false;
						chooseNewSkinModel = false;
						saveProfile();
					}
					return true;
				}
				dropDownOpen = false;
				return true;
			}

			if(selectedSlot < EaglerProfile.customSkins.size()) {
				int previewX = previewLeft();
				int previewY = this.height / 6 + 18;
				if(mouseX >= previewX && mouseX < previewX + 80 && mouseY >= previewY && mouseY < previewY + 120) {
					chooseNewSkinModel = true;
					return true;
				}
			}
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}

	@Override
	public boolean mouseScrolled(double delta) {
		if(dropDownOpen) {
			scrollPos = MathHelper.clamp(scrollPos - (int)Math.signum(delta) * 3, 0, Math.max(0, skinNames.length - slotsVisible));
			return true;
		}
		return super.mouseScrolled(delta);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if(keyCode == 257 || keyCode == 335) {
			done();
			return true;
		}
		if(keyCode == 265 && selectedSlot > 0) {
			--selectedSlot;
			scrollPos = selectedSlot - 2;
			saveProfile();
			return true;
		}
		if(keyCode == 264 && selectedSlot < skinNames.length - 1) {
			++selectedSlot;
			scrollPos = selectedSlot - 2;
			saveProfile();
			return true;
		}
		return this.usernameField.keyPressed(keyCode, scanCode, modifiers) || super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean charTyped(char codePoint, int modifiers) {
		return this.usernameField.charTyped(codePoint, modifiers);
	}

	private void updateOptions() {
		int custom = EaglerProfile.customSkins.size();
		int defaults = getDefaultSkinCount();
		skinNames = new String[custom + defaults];
		for(int i = 0; i < custom; ++i) {
			skinNames[i] = EaglerProfile.customSkins.get(i).name;
		}
		for(int i = 0; i < defaults; ++i) {
			skinNames[custom + i] = DefaultSkins.defaultSkinsMap[i].name;
		}
		clampSelection();
	}

	private int getDefaultSkinCount() {
		int count = DefaultSkins.defaultSkinsMap.length;
		if(!EagRuntime.getConfiguration().isAllowFNAWSkins()) {
			count = Math.max(0, count - 5);
		}
		return count;
	}

	private void clampSelection() {
		if(skinNames.length == 0) {
			selectedSlot = 0;
		}else {
			selectedSlot = MathHelper.clamp(selectedSlot, 0, skinNames.length - 1);
		}
	}

	public ResourceLocation getSelectedSkinTexture() {
		int custom = EaglerProfile.customSkins.size();
		if(selectedSlot < custom) {
			return EaglerProfile.customSkins.get(selectedSlot).getResource();
		}
		return DefaultSkins.getSkinFromId(selectedSlot - custom).location;
	}

	public SkinModel getSelectedSkinModel() {
		int custom = EaglerProfile.customSkins.size();
		if(selectedSlot < custom) {
			return EaglerProfile.customSkins.get(selectedSlot).model;
		}
		return DefaultSkins.getSkinFromId(selectedSlot - custom).model;
	}

	private void saveProfile() {
		clampSelection();
		int custom = EaglerProfile.customSkins.size();
		if(selectedSlot < custom) {
			EaglerProfile.presetSkinId = -1;
			EaglerProfile.customSkinId = selectedSlot;
		}else {
			EaglerProfile.presetSkinId = selectedSlot - custom;
			EaglerProfile.customSkinId = -1;
		}
		String name = usernameField.getText().trim().replaceAll("[^A-Za-z0-9_]", "_");
		while(name.length() < 3) {
			name = name + "_";
		}
		if(name.length() > 16) {
			name = name.substring(0, 16);
		}
		usernameField.setText(name);
		EaglerProfile.setName(name);
	}

	public boolean showCopyPasteButtons() {
		return usernameField != null && usernameField.isFocused();
	}

	public void fireInputEvent(EnumInputEvent event, String param) {
		if(usernameField == null || !usernameField.isFocused()) {
			return;
		}
		if(event == EnumInputEvent.CLIPBOARD_PASTE) {
			usernameField.writeText(param != null ? param : mc.keyboardListener.getClipboardString());
		}else if(event == EnumInputEvent.CLIPBOARD_COPY) {
			mc.keyboardListener.setClipboardString(usernameField.getSelectedText());
		}
	}

	@Override
	public void onGuiClosed() {
		Keyboard.enableRepeatEvents(false);
	}

	@Override
	public void close() {
		done();
	}
}
