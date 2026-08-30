package net.minecraft.client.renderer;

import net.lax1dude.eaglercraft.opengl.WorldRenderer;
import net.lax1dude.eaglercraft.opengl.EaglercraftGPU;
import net.lax1dude.eaglercraft.opengl.GlStateManager;
import net.lax1dude.eaglercraft.opengl.RealOpenGLEnums;
import org.lwjgl.opengl.GL11;

public class OpenGlHelper {

	public static final int defaultTexUnit = RealOpenGLEnums.GL_TEXTURE0;

	public static final int lightmapTexUnit = RealOpenGLEnums.GL_TEXTURE1;
	public static boolean shadersSupported = false;

	/**
	 * Sets the current coordinates of the given lightmap texture
	 */
	public static void setLightmapTextureCoords(int unit, float x, float y) {
		GlStateManager.setActiveTexture(lightmapTexUnit);
		GlStateManager.texCoords2D(x, y);
		GlStateManager.setActiveTexture(defaultTexUnit);
	}

	public static void setActiveTexture(int texture) {
		GlStateManager.setActiveTexture(texture);
	}

	public static void glBlendFunc(int i, int i1, int i2, int i3) {

		GL11.glBlendFunc(i, i1);
	}
}
