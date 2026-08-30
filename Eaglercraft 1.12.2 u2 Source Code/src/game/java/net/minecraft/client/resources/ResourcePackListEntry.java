package net.minecraft.client.resources;

import java.util.List;

import net.lax1dude.eaglercraft.Keyboard;
import net.lax1dude.eaglercraft.KeyboardConstants;
import net.lax1dude.eaglercraft.minecraft.EaglerFolderResourcePack;
import net.lax1dude.eaglercraft.opengl.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiScreenResourcePacks;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public abstract class ResourcePackListEntry implements GuiListExtended.IGuiListEntry {
	private static final ResourceLocation RESOURCE_PACKS_TEXTURE = new ResourceLocation(
			"textures/gui/resource_packs.png");
	private static final ITextComponent INCOMPATIBLE = new TextComponentTranslation("resourcePack.incompatible",
			new Object[0]);
	private static final ITextComponent INCOMPATIBLE_OLD = new TextComponentTranslation("resourcePack.incompatible.old",
			new Object[0]);
	private static final ITextComponent INCOMPATIBLE_NEW = new TextComponentTranslation("resourcePack.incompatible.new",
			new Object[0]);
	protected final Minecraft mc;
	protected final GuiScreenResourcePacks resourcePacksGUI;

	public ResourcePackListEntry(GuiScreenResourcePacks resourcePacksGUIIn) {
		this.resourcePacksGUI = resourcePacksGUIIn;
		this.mc = Minecraft.getMinecraft();
	}

	public void func_192634_a(int p_192634_1_, int p_192634_2_, int p_192634_3_, int p_192634_4_, int p_192634_5_,
			int p_192634_6_, int p_192634_7_, boolean p_192634_8_, float p_192634_9_) {
		int i = this.getResourcePackFormat();

		if (i != 3) {
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			Gui.drawRect(p_192634_2_ - 1, p_192634_3_ - 1, p_192634_2_ + p_192634_4_ - 9, p_192634_3_ + p_192634_5_ + 1,
					-8978432);
		}

		this.bindResourcePackIcon();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		Gui.drawModalRectWithCustomSizedTexture(p_192634_2_, p_192634_3_, 0.0F, 0.0F, 32, 32, 32.0F, 32.0F);
		String s = this.getResourcePackName();
		String s1 = this.getResourcePackDescription();

		if (this.showHoverOverlay() && (this.mc.gameSettings.touchscreen || p_192634_8_)) {
			this.mc.getTextureManager().bindTexture(RESOURCE_PACKS_TEXTURE);
			Gui.drawRect(p_192634_2_, p_192634_3_, p_192634_2_ + 32, p_192634_3_ + 32, -1601138544);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			int j = p_192634_6_ - p_192634_2_;
			int k = p_192634_7_ - p_192634_3_;

			if (i < 3) {
				s = INCOMPATIBLE.getFormattedText();
				s1 = INCOMPATIBLE_OLD.getFormattedText();
			} else if (i > 3) {
				s = INCOMPATIBLE.getFormattedText();
				s1 = INCOMPATIBLE_NEW.getFormattedText();
			}

			if (this.canMoveRight()) {
				if (j < 32) {
					Gui.drawModalRectWithCustomSizedTexture(p_192634_2_, p_192634_3_, 0.0F, 32.0F, 32, 32, 256.0F,
							256.0F);
				} else {
					Gui.drawModalRectWithCustomSizedTexture(p_192634_2_, p_192634_3_, 0.0F, 0.0F, 32, 32, 256.0F,
							256.0F);
				}
			} else {
				if (this.canMoveLeft()) {
					if (j < 16) {
						Gui.drawModalRectWithCustomSizedTexture(p_192634_2_, p_192634_3_, 32.0F, 32.0F, 32, 32, 256.0F,
								256.0F);
					} else {
						Gui.drawModalRectWithCustomSizedTexture(p_192634_2_, p_192634_3_, 32.0F, 0.0F, 32, 32, 256.0F,
								256.0F);
					}
				}

				if (this.canMoveUp()) {
					if (j < 32 && j > 16 && k < 16) {
						Gui.drawModalRectWithCustomSizedTexture(p_192634_2_, p_192634_3_, 96.0F, 32.0F, 32, 32, 256.0F,
								256.0F);
					} else {
						Gui.drawModalRectWithCustomSizedTexture(p_192634_2_, p_192634_3_, 96.0F, 0.0F, 32, 32, 256.0F,
								256.0F);
					}
				}

				if (this.canMoveDown()) {
					if (j < 32 && j > 16 && k > 16) {
						Gui.drawModalRectWithCustomSizedTexture(p_192634_2_, p_192634_3_, 64.0F, 32.0F, 32, 32, 256.0F,
								256.0F);
					} else {
						Gui.drawModalRectWithCustomSizedTexture(p_192634_2_, p_192634_3_, 64.0F, 0.0F, 32, 32, 256.0F,
								256.0F);
					}
				}
			}
		}

		int i1 = this.mc.fontRendererObj.getStringWidth(s);

		if (i1 > 157) {
			s = this.mc.fontRendererObj.trimStringToWidth(s, 157 - this.mc.fontRendererObj.getStringWidth("..."))
					+ "...";
		}

		this.mc.fontRendererObj.drawStringWithShadow(s, (float) (p_192634_2_ + 32 + 2), (float) (p_192634_3_ + 1),
				16777215);
		List<String> list = this.mc.fontRendererObj.listFormattedStringToWidth(s1, 157);

		for (int l = 0; l < 2 && l < list.size(); ++l) {
			this.mc.fontRendererObj.drawStringWithShadow(list.get(l), (float) (p_192634_2_ + 32 + 2),
					(float) (p_192634_3_ + 12 + 10 * l), 8421504);
		}
	}

	protected abstract int getResourcePackFormat();

	protected abstract String getResourcePackDescription();

	protected abstract String getResourcePackName();

	protected abstract void bindResourcePackIcon();

	protected abstract String getEaglerFolderName();

	protected boolean showHoverOverlay() {
		return true;
	}

	protected boolean canMoveRight() {
		return !this.resourcePacksGUI.hasResourcePackEntry(this);
	}

	protected boolean canMoveLeft() {
		return this.resourcePacksGUI.hasResourcePackEntry(this);
	}

	protected boolean canMoveUp() {
		List<ResourcePackListEntry> list = this.resourcePacksGUI.getListContaining(this);
		int i = list.indexOf(this);
		return i > 0 && ((ResourcePackListEntry) list.get(i - 1)).showHoverOverlay();
	}

	protected boolean canMoveDown() {
		List<ResourcePackListEntry> list = this.resourcePacksGUI.getListContaining(this);
		int i = list.indexOf(this);
		return i >= 0 && i < list.size() - 1 && ((ResourcePackListEntry) list.get(i + 1)).showHoverOverlay();
	}

	private void proceedWithBs(int l, boolean deleteInstead) {
		if (!deleteInstead && l != 3) {
			String s1 = I18n.format("resourcePack.incompatible.confirm.title", new Object[0]);
			String s = I18n.format("resourcePack.incompatible.confirm." + (l > 3 ? "new" : "old"), new Object[0]);
			this.mc.displayGuiScreen(new GuiYesNo(new GuiYesNoCallback() {
				public void confirmClicked(boolean flag, int var2) {
					List list2 = ResourcePackListEntry.this.resourcePacksGUI
							.getListContaining(ResourcePackListEntry.this);
					ResourcePackListEntry.this.mc.displayGuiScreen(ResourcePackListEntry.this.resourcePacksGUI);
					if (flag) {
						list2.remove(ResourcePackListEntry.this);
						ResourcePackListEntry.this.resourcePacksGUI.getSelectedResourcePacks().add(0,
								ResourcePackListEntry.this);
					}

				}
			}, s1, s, 0).withOpaqueBackground());
		} else {
			this.mc.displayGuiScreen(this.resourcePacksGUI);
			this.resourcePacksGUI.getListContaining(this).remove(this);
			if (deleteInstead) {
				this.mc.loadingScreen.eaglerShow(I18n.format("resourcePack.load.deleting"), this.getResourcePackName());
				EaglerFolderResourcePack.deleteResourcePack(EaglerFolderResourcePack.RESOURCE_PACKS,
						this.getEaglerFolderName());
			} else {
				this.resourcePacksGUI.getSelectedResourcePacks().add(0, this);
			}
		}
	}

	/**
	 * Called when the mouse is clicked within this entry. Returning true means that
	 * something within this entry was clicked and the list should not be dragged.
	 */
	public boolean mousePressed(int var1, int var2, int var3, int var4, int i, int j) {
		if (this.showHoverOverlay() && i <= 32) {
			if (this.canMoveRight()) {
				this.resourcePacksGUI.markChanged();
				int l = this.getResourcePackFormat();
				if (Keyboard.isKeyDown(KeyboardConstants.KEY_LSHIFT)
						|| Keyboard.isKeyDown(KeyboardConstants.KEY_RSHIFT)) {
					proceedWithBs(l, false);
				} else {
					this.mc.displayGuiScreen(new GuiYesNo(new GuiYesNoCallback() {
						public void confirmClicked(boolean flag, int var2) {
							proceedWithBs(l, flag);

						}
					}, I18n.format("resourcePack.prompt.title", this.getResourcePackName()),
							I18n.format("resourcePack.prompt.text", new Object[0]),
							I18n.format("resourcePack.prompt.delete", new Object[0]),
							I18n.format("resourcePack.prompt.add", new Object[0]), 0).withOpaqueBackground());
				}
				return true;
			}

			if (i < 16 && this.canMoveLeft()) {
				this.resourcePacksGUI.getListContaining(this).remove(this);
				this.resourcePacksGUI.getAvailableResourcePacks().add(0, this);
				this.resourcePacksGUI.markChanged();
				return true;
			}

			if (i > 16 && j < 16 && this.canMoveUp()) {
				List list1 = this.resourcePacksGUI.getListContaining(this);
				int i1 = list1.indexOf(this);
				list1.remove(this);
				list1.add(i1 - 1, this);
				this.resourcePacksGUI.markChanged();
				return true;
			}

			if (i > 16 && j > 16 && this.canMoveDown()) {
				List list = this.resourcePacksGUI.getListContaining(this);
				int k = list.indexOf(this);
				list.remove(this);
				list.add(k + 1, this);
				this.resourcePacksGUI.markChanged();
				return true;
			}
		}

		return false;
	}

	public void func_192633_a(int p_192633_1_, int p_192633_2_, int p_192633_3_, float p_192633_4_) {
	}

	/**
	 * Fired when the mouse button is released. Arguments: index, x, y, mouseEvent,
	 * relativeX, relativeY
	 */
	public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {
	}
}
