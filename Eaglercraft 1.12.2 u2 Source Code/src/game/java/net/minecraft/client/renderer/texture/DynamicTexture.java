package net.minecraft.client.renderer.texture;

import java.io.IOException;

import net.lax1dude.eaglercraft.opengl.ImageData;
import net.minecraft.client.resources.IResourceManager;

public class DynamicTexture extends AbstractTexture {
	private final int[] dynamicTextureData;

	/** width of this icon in pixels */
	private final int width;

	/** height of this icon in pixels */
	private final int height;

	public DynamicTexture(ImageData bufferedImage) {
		this(bufferedImage.width, bufferedImage.height);
		System.arraycopy(bufferedImage.pixels, 0, dynamicTextureData, 0, bufferedImage.pixels.length);
		this.updateDynamicTexture();
	}

	public DynamicTexture(int textureWidth, int textureHeight) {
		this.width = textureWidth;
		this.height = textureHeight;
		this.dynamicTextureData = new int[textureWidth * textureHeight];
		this.hasAllocated = true;
		TextureUtil.allocateTexture(this.getGlTextureId(), textureWidth, textureHeight);
	}

	public void loadTexture(IResourceManager resourceManager) throws IOException {
	}

	public void updateDynamicTexture() {
		TextureUtil.uploadTexture(this.getGlTextureId(), this.dynamicTextureData, this.width, this.height);
	}

	public int[] getTextureData() {
		return this.dynamicTextureData;
	}
}