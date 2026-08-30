package net.minecraft.src;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.peyton.eagler.minecraft.TextureLocation;

public abstract class GuiContainer extends GuiScreen {
	private static RenderItem itemRenderer = new RenderItem();
	protected int xSize = 176;
	protected int ySize = 166;
	public CraftingInventoryCB inventorySlots;

	public GuiContainer(CraftingInventoryCB var1) {
		this.inventorySlots = var1;
	}

	public void initGui() {
		super.initGui();
		this.mc.thePlayer.field_20068_h = this.inventorySlots;
	}

	public void drawScreen(int var1, int var2, float var3) {
		this.drawDefaultBackground();
		int var4 = (this.width - this.xSize) / 2;
		int var5 = (this.height - this.ySize) / 2;
		this.drawGuiContainerBackgroundLayer(var3);
		GL11.glPushMatrix();
		GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
		RenderHelper.enableStandardItemLighting();
		GL11.glPopMatrix();
		GL11.glPushMatrix();
		GL11.glTranslatef((float)var4, (float)var5, 0.0F);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		Slot var6 = null;

		int var9;
		int var10;
		for(int var7 = 0; var7 < this.inventorySlots.field_20122_e.size(); ++var7) {
			Slot var8 = (Slot)this.inventorySlots.field_20122_e.get(var7);
			this.drawSlotInventory(var8);
			if(this.func_20081_a(var8, var1, var2)) {
				var6 = var8;
				GL11.glDisable(GL11.GL_LIGHTING);
				GL11.glDisable(GL11.GL_DEPTH_TEST);
				var9 = var8.field_20006_b;
				var10 = var8.field_20008_c;
				this.drawGradientRect(var9, var10, var9 + 16, var10 + 16, -2130706433, -2130706433);
				GL11.glEnable(GL11.GL_LIGHTING);
				GL11.glEnable(GL11.GL_DEPTH_TEST);
			}
		}

		InventoryPlayer var12 = this.mc.thePlayer.inventory;
		if(var12.func_20075_i() != null) {
			GL11.glTranslatef(0.0F, 0.0F, 32.0F);
			itemRenderer.renderItemIntoGUI(this.fontRenderer, this.mc.renderEngine, var12.func_20075_i(), var1 - var4 - 8, var2 - var5 - 8);
			itemRenderer.renderItemOverlayIntoGUI(this.fontRenderer, this.mc.renderEngine, var12.func_20075_i(), var1 - var4 - 8, var2 - var5 - 8);
		}

		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		RenderHelper.disableStandardItemLighting();
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		this.drawGuiContainerForegroundLayer();
		if(var12.func_20075_i() == null && var6 != null && var6.func_20005_c()) {
			String var13 = ("" + StringTranslate.func_20162_a().func_20161_b(var6.getStack().func_20109_f())).trim();
			if(var13.length() > 0) {
				var9 = var1 - var4 + 12;
				var10 = var2 - var5 - 12;
				int var11 = this.fontRenderer.getStringWidth(var13);
				this.drawGradientRect(var9 - 3, var10 - 3, var9 + var11 + 3, var10 + 8 + 3, -1073741824, -1073741824);
				this.fontRenderer.drawStringWithShadow(var13, var9, var10, -1);
			}
		}

		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glPopMatrix();
	}

	protected void drawGuiContainerForegroundLayer() {
	}

	protected abstract void drawGuiContainerBackgroundLayer(float var1);

	private void drawSlotInventory(Slot var1) {
		int var2 = var1.field_20006_b;
		int var3 = var1.field_20008_c;
		ItemStack var4 = var1.getStack();
		if(var4 == null) {
			int var5 = var1.func_775_c();
			if(var5 >= 0) {
				GL11.glDisable(GL11.GL_LIGHTING);
				TextureLocation.items.bindTexture();
				this.drawTexturedModalRect(var2, var3, var5 % 16 * 16, var5 / 16 * 16, 16, 16);
				GL11.glEnable(GL11.GL_LIGHTING);
				return;
			}
		}

		itemRenderer.renderItemIntoGUI(this.fontRenderer, this.mc.renderEngine, var4, var2, var3);
		itemRenderer.renderItemOverlayIntoGUI(this.fontRenderer, this.mc.renderEngine, var4, var2, var3);
	}

	private Slot getSlotAtPosition(int var1, int var2) {
		for(int var3 = 0; var3 < this.inventorySlots.field_20122_e.size(); ++var3) {
			Slot var4 = (Slot)this.inventorySlots.field_20122_e.get(var3);
			if(this.func_20081_a(var4, var1, var2)) {
				return var4;
			}
		}

		return null;
	}

	private boolean func_20081_a(Slot var1, int var2, int var3) {
		int var4 = (this.width - this.xSize) / 2;
		int var5 = (this.height - this.ySize) / 2;
		var2 -= var4;
		var3 -= var5;
		return var2 >= var1.field_20006_b - 1 && var2 < var1.field_20006_b + 16 + 1 && var3 >= var1.field_20008_c - 1 && var3 < var1.field_20008_c + 16 + 1;
	}

	protected void mouseClicked(int var1, int var2, int var3) {
		if(var3 == 0 || var3 == 1) {
			Slot var4 = this.getSlotAtPosition(var1, var2);
			int var5 = (this.width - this.xSize) / 2;
			int var6 = (this.height - this.ySize) / 2;
			boolean var7 = var1 < var5 || var2 < var6 || var1 >= var5 + this.xSize || var2 >= var6 + this.ySize;
			int var8 = -1;
			if(var4 != null) {
				var8 = var4.field_20007_a;
			}

			if(var7) {
				var8 = -999;
			}

			if(var8 != -1) {
				this.mc.playerController.func_20085_a(this.inventorySlots.unusedList, var8, var3, this.mc.thePlayer);
			}
		}

	}

	protected void mouseMovedOrUp(int var1, int var2, int var3) {
		if(var3 == 0) {
		}

	}

	protected void keyTyped(char var1, int var2) {
		if(var2 == 1 || var2 == this.mc.gameSettings.keyBindInventory.keyCode) {
			this.mc.thePlayer.func_20059_m();
		}

	}

	public void onGuiClosed() {
		if(this.mc.thePlayer != null) {
			this.mc.playerController.func_20086_a(this.inventorySlots.unusedList, this.mc.thePlayer);
		}
	}

	public boolean doesGuiPauseGame() {
		return false;
	}
}
