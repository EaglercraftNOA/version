package net.minecraft.client.renderer;

import net.lax1dude.eaglercraft.opengl.WorldRenderer;
import net.lax1dude.eaglercraft.opengl.EaglercraftGPU;
import net.lax1dude.eaglercraft.opengl.GlStateManager;
import net.lax1dude.eaglercraft.opengl.RealOpenGLEnums;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class OpenGlHelper {

	public static final int defaultTexUnit = RealOpenGLEnums.GL_TEXTURE0;

	public static final int lightmapTexUnit = RealOpenGLEnums.GL_TEXTURE1;

	/**
	 * Sets the current coordinates of the given lightmap texture
	 */
	public static void setLightmapTextureCoords(int unit, float x, float y) {
		GlStateManager.setActiveTexture(lightmapTexUnit);
		GlStateManager.texCoords2D(x, y);
		GlStateManager.setActiveTexture(defaultTexUnit);
	}

	public static void renderDirections(int p_188785_0_) {
		GlStateManager.disableTexture2D();
		GlStateManager.depthMask(false);
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer bufferbuilder = tessellator.getBuffer();
		EaglercraftGPU.glLineWidth(4.0F);
		bufferbuilder.begin(1, DefaultVertexFormats.POSITION_COLOR);
		bufferbuilder.pos(0.0D, 0.0D, 0.0D).color(0, 0, 0, 255).endVertex();
		bufferbuilder.pos((double) p_188785_0_, 0.0D, 0.0D).color(0, 0, 0, 255).endVertex();
		bufferbuilder.pos(0.0D, 0.0D, 0.0D).color(0, 0, 0, 255).endVertex();
		bufferbuilder.pos(0.0D, (double) p_188785_0_, 0.0D).color(0, 0, 0, 255).endVertex();
		bufferbuilder.pos(0.0D, 0.0D, 0.0D).color(0, 0, 0, 255).endVertex();
		bufferbuilder.pos(0.0D, 0.0D, (double) p_188785_0_).color(0, 0, 0, 255).endVertex();
		tessellator.draw();
		EaglercraftGPU.glLineWidth(2.0F);
		bufferbuilder.begin(1, DefaultVertexFormats.POSITION_COLOR);
		bufferbuilder.pos(0.0D, 0.0D, 0.0D).color(255, 0, 0, 255).endVertex();
		bufferbuilder.pos((double) p_188785_0_, 0.0D, 0.0D).color(255, 0, 0, 255).endVertex();
		bufferbuilder.pos(0.0D, 0.0D, 0.0D).color(0, 255, 0, 255).endVertex();
		bufferbuilder.pos(0.0D, (double) p_188785_0_, 0.0D).color(0, 255, 0, 255).endVertex();
		bufferbuilder.pos(0.0D, 0.0D, 0.0D).color(127, 127, 255, 255).endVertex();
		bufferbuilder.pos(0.0D, 0.0D, (double) p_188785_0_).color(127, 127, 255, 255).endVertex();
		tessellator.draw();
		EaglercraftGPU.glLineWidth(1.0F);
		GlStateManager.depthMask(true);
		GlStateManager.enableTexture2D();
	}
}
