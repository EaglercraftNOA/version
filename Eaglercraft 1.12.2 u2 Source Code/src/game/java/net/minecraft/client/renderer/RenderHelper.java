package net.minecraft.client.renderer;

import net.lax1dude.eaglercraft.opengl.GlStateManager;
import net.minecraft.util.math.Vec3d;

public class RenderHelper {
	private static final Vec3d LIGHT0_POS = (new Vec3d(0.20000000298023224D, 1.0D, -0.699999988079071D)).normalize();
	private static final Vec3d LIGHT1_POS = (new Vec3d(-0.20000000298023224D, 1.0D, 0.699999988079071D)).normalize();

	/**
	 * Disables the OpenGL lighting properties enabled by enableStandardItemLighting
	 */
	public static void disableStandardItemLighting() {
		GlStateManager.disableLighting();
		GlStateManager.disableMCLight(0);
		GlStateManager.disableMCLight(1);
		GlStateManager.disableColorMaterial();
	}

	/**
	 * Sets the OpenGL lighting properties to the values used when rendering blocks
	 * as items
	 */
	public static void enableStandardItemLighting() {
		GlStateManager.enableLighting();
		GlStateManager.enableMCLight(0, 0.6f, LIGHT0_POS.xCoord, LIGHT0_POS.yCoord, LIGHT0_POS.zCoord, 0.0D);
		GlStateManager.enableMCLight(1, 0.6f, LIGHT1_POS.xCoord, LIGHT1_POS.yCoord, LIGHT1_POS.zCoord, 0.0D);
		GlStateManager.setMCLightAmbient(0.4f, 0.4f, 0.4f);
		GlStateManager.enableColorMaterial();
	}

	/**
	 * Sets OpenGL lighting for rendering blocks as items inside GUI screens (such
	 * as containers).
	 */
	public static void enableGUIStandardItemLighting() {
		GlStateManager.pushMatrix();
		GlStateManager.rotate(-30.0F, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(165.0F, 1.0F, 0.0F, 0.0F);
		enableStandardItemLighting();
		GlStateManager.popMatrix();
	}
}
