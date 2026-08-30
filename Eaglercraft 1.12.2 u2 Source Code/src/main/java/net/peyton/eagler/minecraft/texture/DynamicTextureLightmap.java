package net.peyton.eagler.minecraft.texture;

import java.io.IOException;

import net.lax1dude.eaglercraft.opengl.EaglercraftGPU;
import net.lax1dude.eaglercraft.opengl.GlStateManager;
import net.lax1dude.eaglercraft.opengl.ImageData;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResourceManager;

public class DynamicTextureLightmap extends AbstractTexture {
	private final int[] dynamicTextureData;

	/** width of this icon in pixels */
	private final int width;

	/** height of this icon in pixels */
	private final int height;

	public DynamicTextureLightmap(ImageData bufferedImage) {
		this(bufferedImage.width, bufferedImage.height);
		System.arraycopy(bufferedImage.pixels, 0, dynamicTextureData, 0, bufferedImage.pixels.length);
		this.updateDynamicTexture();
	}

	public DynamicTextureLightmap(int textureWidth, int textureHeight) {
		this.width = textureWidth;
		this.height = textureHeight;
		this.dynamicTextureData = new int[textureWidth * textureHeight];
		this.hasAllocated = true;
		TextureUtil.allocateTexture(this.getGlTextureId(), textureWidth, textureHeight);
	}

	public void loadTexture(IResourceManager resourceManager) throws IOException {
		GlStateManager.pushMatrix();
		GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GlStateManager.matrixMode(5890);
        GlStateManager.loadIdentity();
        float f = 0.00390625F;
        GlStateManager.scale(f, f, f);
        GlStateManager.translate(8.0F, 8.0F, 8.0F);
        GlStateManager.matrixMode(5888);
        GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
        GlStateManager.popMatrix();
	}

	public void updateDynamicTexture() {
		TextureUtil.uploadTexture(this.getGlTextureId(), this.dynamicTextureData, this.width, this.height);
	}

	public int[] getTextureData() {
		return this.dynamicTextureData;
	}
}