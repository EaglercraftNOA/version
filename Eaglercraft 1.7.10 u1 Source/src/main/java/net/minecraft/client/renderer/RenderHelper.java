package net.minecraft.client.renderer;

import net.lax1dude.eaglercraft.internal.buffer.FloatBuffer;
import net.lax1dude.eaglercraft.opengl.GlStateManager;
import net.minecraft.util.Vec3;
import org.lwjgl.opengl.GL11;

public class RenderHelper {
	/** Float buffer used to set OpenGL material colors */
	private static FloatBuffer colorBuffer = GLAllocation.createDirectFloatBuffer(16);
	private static final Vec3 field_82884_b = Vec3.createVectorHelper(0.20000000298023224D, 1.0D, -0.699999988079071D)
			.normalize();
	private static final Vec3 field_82885_c = Vec3.createVectorHelper(-0.20000000298023224D, 1.0D, 0.699999988079071D)
			.normalize();
	private static final String __OBFID = "CL_00000629";

	/**
	 * Disables the OpenGL lighting properties enabled by enableStandardItemLighting
	 */
	public static void disableStandardItemLighting() {
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_LIGHT0);
		GL11.glDisable(GL11.GL_LIGHT1);
		GL11.glDisable(GL11.GL_COLOR_MATERIAL);
	}

	/**
	 * Sets the OpenGL lighting properties to the values used when rendering blocks
	 * as items
	 */
	public static void enableStandardItemLighting() {
		GlStateManager.enableLighting();
		GlStateManager.enableMCLight(0, 0.6f, field_82884_b.xCoord, field_82884_b.yCoord, field_82884_b.zCoord, 0.0D);
		GlStateManager.enableMCLight(1, 0.6f, field_82885_c.xCoord, field_82885_c.yCoord, field_82885_c.zCoord, 0.0D);
		GlStateManager.setMCLightAmbient(0.4f, 0.4f, 0.4f);
		GlStateManager.enableColorMaterial();
	}

	/**
	 * Update and return colorBuffer with the RGBA values passed as arguments
	 */
	private static FloatBuffer setColorBuffer(double p_74517_0_, double p_74517_2_, double p_74517_4_,
			double p_74517_6_) {
		return setColorBuffer((float) p_74517_0_, (float) p_74517_2_, (float) p_74517_4_, (float) p_74517_6_);
	}

	/**
	 * Update and return colorBuffer with the RGBA values passed as arguments
	 */
	private static FloatBuffer setColorBuffer(float p_74521_0_, float p_74521_1_, float p_74521_2_, float p_74521_3_) {
		colorBuffer.clear();
		colorBuffer.put(p_74521_0_).put(p_74521_1_).put(p_74521_2_).put(p_74521_3_);
		colorBuffer.flip();
		return colorBuffer;
	}

	/**
	 * Sets OpenGL lighting for rendering blocks as items inside GUI screens (such
	 * as containers).
	 */
	public static void enableGUIStandardItemLighting() {
		GL11.glPushMatrix();
		GL11.glRotatef(-30.0F, 0.0F, 1.0F, 0.0F);
		GL11.glRotatef(165.0F, 1.0F, 0.0F, 0.0F);
		enableStandardItemLighting();
		GL11.glPopMatrix();
	}
}
