package net.minecraft.client.renderer.texture;

import net.lax1dude.eaglercraft.opengl.EaglercraftGPU;

public abstract class AbstractTexture implements ITextureObject {
	protected int glTextureId = -1;
	protected boolean blur;
	protected boolean mipmap;
	protected boolean blurLast;
	protected boolean mipmapLast;
	protected boolean hasAllocated;

	public void setBlurMipmapDirect(boolean blurIn, boolean mipmapIn) {
		this.blur = blurIn;
		this.mipmap = mipmapIn;
		int i;
		int j;

		if (blurIn) {
			i = mipmapIn ? 9987 : 9729;
			j = 9729;
		} else {
			i = mipmapIn ? 9986 : 9728;
			j = 9728;
		}

		EaglercraftGPU.glTexParameteri(3553, 10241, i);
		EaglercraftGPU.glTexParameteri(3553, 10240, j);
	}

	public void setBlurMipmap(boolean blurIn, boolean mipmapIn) {
		this.blurLast = this.blur;
		this.mipmapLast = this.mipmap;
		this.setBlurMipmapDirect(blurIn, mipmapIn);
	}

	public void restoreLastBlurMipmap() {
		this.setBlurMipmapDirect(this.blurLast, this.mipmapLast);
	}

	public int getGlTextureId() {
		if (this.glTextureId == -1) {
			this.glTextureId = TextureUtil.glGenTextures();
			hasAllocated = false;
		}

		return this.glTextureId;
	}

	public void deleteGlTexture() {
		if (this.glTextureId != -1) {
			TextureUtil.deleteTexture(this.glTextureId);
			this.glTextureId = -1;
		}
	}

	/**
	 * This function is needed due to Eaglercraft's use of glTexStorage2D to
	 * allocate memory for textures, some OpenGL implementations don't like it when
	 * you call glTexStorage2D on the same texture object more than once
	 */
	protected void regenerateIfNotAllocated() {
		if (this.glTextureId != -1) {
			if (hasAllocated) {
				EaglercraftGPU.regenerateTexture(glTextureId);
			}
			hasAllocated = true;
		}
	}
}