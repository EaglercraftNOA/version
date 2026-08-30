package net.minecraft.client.gui;

import java.util.List;

import net.lax1dude.eaglercraft.opengl.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

public class ServerListEntryNormal implements GuiListExtended.IGuiListEntry {
	private static final ResourceLocation UNKNOWN_SERVER = new ResourceLocation("textures/misc/unknown_server.png");
	private static final ResourceLocation SERVER_SELECTION_BUTTONS = new ResourceLocation(
			"textures/gui/server_selection.png");
	private final GuiMultiplayer owner;
	private final Minecraft mc;
	private final ServerData server;
	protected long lastClickTime;

	protected ServerListEntryNormal(GuiMultiplayer p_i45048_1_, ServerData serverIn) {
		this.owner = p_i45048_1_;
		this.server = serverIn;
		this.mc = Minecraft.getMinecraft();
	}

	public void func_192634_a(int p_192634_1_, int p_192634_2_, int p_192634_3_, int p_192634_4_, int p_192634_5_,
			int p_192634_6_, int p_192634_7_, boolean p_192634_8_, float p_192634_9_) {
		if (!this.server.pinged) {
			this.server.pinged = true;
			this.server.pingToServer = -2L;
			this.server.serverMOTD = "";
			this.server.populationInfo = "";
		}

		boolean flag = this.server.version > 340;
		boolean flag1 = this.server.version < 340;
		boolean flag2 = flag || flag1;
		this.mc.fontRendererObj.drawString(this.server.serverName, p_192634_2_ + 32 + 3, p_192634_3_ + 1, 16777215);
		List<String> list = this.mc.fontRendererObj.listFormattedStringToWidth(this.server.serverMOTD,
				p_192634_4_ - 32 - 2);

		for (int k1 = 0; k1 < 2; ++k1) {
			if (k1 < list.size()) {
				this.mc.fontRendererObj.drawString((String) list.get(k1), p_192634_2_ + 32 + 3,
						p_192634_3_ + 12 + this.mc.fontRendererObj.FONT_HEIGHT * k1, 8421504);
			} else if (k1 == 1) {
				this.mc.fontRendererObj.drawString(
						this.server.hideAddress ? I18n.format("selectServer.hiddenAddress", new Object[0])
								: this.server.serverIP,
								p_192634_2_ + 32 + 3, p_192634_3_ + 12 + this.mc.fontRendererObj.FONT_HEIGHT * k1 + k1, 0x444444);
			}
		}

		String s2 = flag2 ? TextFormatting.DARK_RED + this.server.gameVersion : this.server.populationInfo;
		int j = this.mc.fontRendererObj.getStringWidth(s2);
		this.mc.fontRendererObj.drawString(s2, p_192634_2_ + p_192634_4_ - j - 15 - 2, p_192634_3_ + 1, 8421504);
		int k = 0;
		String s = null;
		int l;
		String s1;

		if (flag2) {
			l = 5;
			s1 = I18n.format(flag ? "multiplayer.status.client_out_of_date" : "multiplayer.status.server_out_of_date");
			s = this.server.playerList;
		} else if (this.server.pinged && this.server.pingToServer != -2L) {
			if (this.server.pingToServer < 0L) {
				l = 5;
			} else if (this.server.pingToServer < 150L) {
				l = 0;
			} else if (this.server.pingToServer < 300L) {
				l = 1;
			} else if (this.server.pingToServer < 600L) {
				l = 2;
			} else if (this.server.pingToServer < 1000L) {
				l = 3;
			} else {
				l = 4;
			}

			if (this.server.pingToServer < 0L) {
				s1 = I18n.format("multiplayer.status.no_connection");
			} else {
				s1 = this.server.pingToServer + "ms";
				s = this.server.playerList;
			}
		} else {
			k = 1;
			l = (int) (Minecraft.getSystemTime() / 100L + (long) (p_192634_1_ * 2) & 7L);

			if (l > 4) {
				l = 8 - l;
			}

			s1 = I18n.format("multiplayer.status.pinging");
		}

		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(Gui.ICONS);
		Gui.drawModalRectWithCustomSizedTexture(p_192634_2_ + p_192634_4_ - 15, p_192634_3_, (float) (k * 10),
				(float) (176 + l * 8), 10, 8, 256.0F, 256.0F);
		
		if (this.mc.gameSettings.touchscreen || p_192634_8_) {
			GlStateManager.enableShaderBlendAdd();
			GlStateManager.setShaderBlendSrc(0.6f, 0.6f, 0.6f, 1.0f);
			GlStateManager.setShaderBlendAdd(0.3f, 0.3f, 0.3f, 0.0f);
		}

		if (this.server.iconTextureObject != null) {
			this.drawTextureAt(p_192634_2_, p_192634_3_, this.server.iconResourceLocation);
		} else {
			this.drawTextureAt(p_192634_2_, p_192634_3_, UNKNOWN_SERVER);
		}

		int i1 = p_192634_6_ - p_192634_2_;
		int j1 = p_192634_7_ - p_192634_3_;

		if (i1 >= p_192634_4_ - 15 && i1 <= p_192634_4_ - 5 && j1 >= 0 && j1 <= 8) {
			this.owner.setHoveringText(s1);
		} else if (i1 >= p_192634_4_ - j - 15 - 2 && i1 <= p_192634_4_ - 15 - 2 && j1 >= 0 && j1 <= 8) {
			this.owner.setHoveringText(s);
		}
		
		if (this.mc.gameSettings.touchscreen || p_192634_8_) {
			GlStateManager.disableShaderBlendAdd();
		}

		if (this.mc.gameSettings.touchscreen || p_192634_8_) {
			this.mc.getTextureManager().bindTexture(SERVER_SELECTION_BUTTONS);
			//Gui.drawRect(p_192634_2_, p_192634_3_, p_192634_2_ + 32, p_192634_3_ + 32, -1601138544);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			int k1 = p_192634_6_ - p_192634_2_;
			int l1 = p_192634_7_ - p_192634_3_;

			if (this.canJoin()) {
				if (k1 < 32 && k1 > 16) {
					Gui.drawModalRectWithCustomSizedTexture(p_192634_2_, p_192634_3_, 0.0F, 32.0F, 32, 32, 256.0F,
							256.0F);
				} else {
					Gui.drawModalRectWithCustomSizedTexture(p_192634_2_, p_192634_3_, 0.0F, 0.0F, 32, 32, 256.0F,
							256.0F);
				}
			}

			if (this.owner.canMoveUp(this, p_192634_1_)) {
				if (k1 < 16 && l1 < 16) {
					Gui.drawModalRectWithCustomSizedTexture(p_192634_2_, p_192634_3_, 96.0F, 32.0F, 32, 32, 256.0F,
							256.0F);
				} else {
					Gui.drawModalRectWithCustomSizedTexture(p_192634_2_, p_192634_3_, 96.0F, 0.0F, 32, 32, 256.0F,
							256.0F);
				}
			}

			if (this.owner.canMoveDown(this, p_192634_1_)) {
				if (k1 < 16 && l1 > 16) {
					Gui.drawModalRectWithCustomSizedTexture(p_192634_2_, p_192634_3_, 64.0F, 32.0F, 32, 32, 256.0F,
							256.0F);
				} else {
					Gui.drawModalRectWithCustomSizedTexture(p_192634_2_, p_192634_3_, 64.0F, 0.0F, 32, 32, 256.0F,
							256.0F);
				}
			}
		}
	}

	protected void drawTextureAt(int p_178012_1_, int p_178012_2_, ResourceLocation p_178012_3_) {
		this.mc.getTextureManager().bindTexture(p_178012_3_);
		GlStateManager.enableBlend();
		Gui.drawModalRectWithCustomSizedTexture(p_178012_1_, p_178012_2_, 0.0F, 0.0F, 32, 32, 32.0F, 32.0F);
		GlStateManager.disableBlend();
	}

	private boolean canJoin() {
		return true;
	}

	/**
	 * Called when the mouse is clicked within this entry. Returning true means that
	 * something within this entry was clicked and the list should not be dragged.
	 */
	public boolean mousePressed(int slotIndex, int mouseX, int mouseY, int mouseEvent, int relativeX, int relativeY) {
		if (relativeX <= 32) {
			if (relativeX < 32 && relativeX > 16 && this.canJoin()) {
				this.owner.selectServer(slotIndex);
				this.owner.connectToSelected();
				return true;
			}

			if (relativeX < 16 && relativeY < 16 && this.owner.canMoveUp(this, slotIndex)) {
				this.owner.moveServerUp(this, slotIndex, GuiScreen.isShiftKeyDown());
				return true;
			}

			if (relativeX < 16 && relativeY > 16 && this.owner.canMoveDown(this, slotIndex)) {
				this.owner.moveServerDown(this, slotIndex, GuiScreen.isShiftKeyDown());
				return true;
			}
		}

		this.owner.selectServer(slotIndex);

		if (Minecraft.getSystemTime() - this.lastClickTime < 250L) {
			this.owner.connectToSelected();
		}

		this.lastClickTime = Minecraft.getSystemTime();
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

	public ServerData getServerData() {
		return this.server;
	}
}