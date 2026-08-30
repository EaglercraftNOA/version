package net.minecraft.client.gui.inventory;

import org.lwjgl.opengl.GL11;

import net.lax1dude.eaglercraft.opengl.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.achievement.GuiAchievements;
import net.minecraft.client.gui.achievement.GuiStats;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class GuiInventory extends InventoryEffectRenderer {
	private float field_147048_u;
	private float field_147047_v;
	private static final String __OBFID = "CL_00000761";

	public GuiInventory(EntityPlayer p_i1094_1_) {
		super(p_i1094_1_.inventoryContainer);
		this.field_146291_p = true;
	}

	/**
	 * Called from the main game loop to update the screen.
	 */
	public void updateScreen() {
		if (this.mc.playerController.isInCreativeMode()) {
			this.mc.displayGuiScreen(new GuiContainerCreative(this.mc.thePlayer));
		}
	}

	/**
	 * Adds the buttons (and other controls) to the screen in question.
	 */
	public void initGui() {
		this.buttonList.clear();

		if (this.mc.playerController.isInCreativeMode()) {
			this.mc.displayGuiScreen(new GuiContainerCreative(this.mc.thePlayer));
		} else {
			super.initGui();
		}
	}

	protected void func_146979_b(int p_146979_1_, int p_146979_2_) {
		this.fontRendererObj.drawString(I18n.format("container.crafting", new Object[0]), 86, 16, 4210752);
	}

	/**
	 * Draws the screen and all the components in it.
	 */
	public void drawScreen(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
		super.drawScreen(p_73863_1_, p_73863_2_, p_73863_3_);
		this.field_147048_u = (float) p_73863_1_;
		this.field_147047_v = (float) p_73863_2_;
	}

	protected void func_146976_a(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(field_147001_a);
		int var4 = this.field_147003_i;
		int var5 = this.field_147009_r;
		this.drawTexturedModalRect(var4, var5, 0, 0, this.field_146999_f, this.field_147000_g);
		
		GlStateManager.enableDepth();
		drawEntityOnScreen(var4 + 51, var5 + 75, 30, (float) (var4 + 51) - this.field_147048_u,
				(float) (var5 + 75 - 50) - this.field_147047_v, this.mc.thePlayer);
		GlStateManager.disableDepth();
	}

	public static void drawEntityOnScreen(int posX, int posY, int scale, float mouseX, float mouseY,
			EntityLivingBase ent) {
		GlStateManager.enableColorMaterial();
		GlStateManager.pushMatrix();
		GlStateManager.translate((float) posX, (float) posY - 50, 50.0F);
		GlStateManager.scale((float) (-scale), (float) scale, (float) scale);
		GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
		float f = ent.renderYawOffset;
		float f1 = ent.rotationYaw;
		float f2 = ent.rotationPitch;
		float f3 = ent.prevRotationYawHead;
		float f4 = ent.rotationYawHead;
		GlStateManager.rotate(135.0F, 0.0F, 1.0F, 0.0F);
		RenderHelper.enableStandardItemLighting();
		GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(-((float) Math.atan((double) (mouseY / 40.0F))) * 20.0F, 1.0F, 0.0F, 0.0F);
		ent.renderYawOffset = (float) Math.atan((double) (mouseX / 40.0F)) * 20.0F;
		ent.rotationYaw = (float) Math.atan((double) (mouseX / 40.0F)) * 40.0F;
		ent.rotationPitch = -((float) Math.atan((double) (mouseY / 40.0F))) * 20.0F;
		ent.rotationYawHead = ent.rotationYaw;
		ent.prevRotationYawHead = ent.rotationYaw;
		GlStateManager.translate(0.0F, 0.0F, 0.0F);
		RenderManager rendermanager = RenderManager.instance;
		float oldPlayerViewY = rendermanager.playerViewY;
		rendermanager.playerViewY = 180.0F;
		Render renderer = rendermanager.getEntityRenderObject(ent);
		//im gonna molest you
		if (renderer != null) {
			renderer.doRender(ent, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F);
		}
		rendermanager.playerViewY = oldPlayerViewY;
		ent.renderYawOffset = f;
		ent.rotationYaw = f1;
		ent.rotationPitch = f2;
		ent.prevRotationYawHead = f3;
		ent.rotationYawHead = f4;
		GlStateManager.popMatrix();
		RenderHelper.disableStandardItemLighting();
		GlStateManager.disableRescaleNormal();
		
		GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GlStateManager.disableTexture2D();
		GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
		GlStateManager.enableTexture2D();
		GlStateManager.disableColorMaterial();
	}

	protected void actionPerformed(GuiButton p_146284_1_) {
		if (p_146284_1_.id == 0) {
			this.mc.displayGuiScreen(new GuiAchievements(this, this.mc.thePlayer.func_146107_m()));
		}

		if (p_146284_1_.id == 1) {
			this.mc.displayGuiScreen(new GuiStats(this, this.mc.thePlayer.func_146107_m()));
		}
	}
}
