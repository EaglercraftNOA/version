package net.minecraft.client.renderer.texture;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

import net.lax1dude.eaglercraft.IOUtils;
import net.lax1dude.eaglercraft.internal.buffer.IntBuffer;
import net.lax1dude.eaglercraft.opengl.EaglercraftGPU;
import net.lax1dude.eaglercraft.opengl.GlStateManager;
import net.lax1dude.eaglercraft.opengl.ImageData;
import net.lax1dude.eaglercraft.opengl.RealOpenGLEnums;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TextureUtil {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final IntBuffer DATA_BUFFER = GLAllocation.createDirectIntBuffer(4194304);
	public static final DynamicTexture MISSING_TEXTURE = new DynamicTexture(16, 16);
	public static final int[] MISSING_TEXTURE_DATA = MISSING_TEXTURE.getTextureData();
	private static final float[] COLOR_GAMMAS;
	private static final int[] MIPMAP_BUFFER;

	private static float getColorGamma(int p_188543_0_) {
		return COLOR_GAMMAS[p_188543_0_ & 255];
	}

	public static int glGenTextures() {
		return GlStateManager.generateTexture();
	}

	public static void deleteTexture(int textureId) {
		GlStateManager.deleteTexture(textureId);
	}

	public static int uploadTextureImage(int textureId, ImageData texture) {
		return uploadTextureImageAllocate(textureId, texture, false, false);
	}

	public static void uploadTexture(int textureId, int[] p_110988_1_, int p_110988_2_, int p_110988_3_) {
		bindTexture(textureId);
		uploadTextureSub(0, p_110988_1_, p_110988_2_, p_110988_3_, 0, 0, false, false, false);
	}

	public static int[][] generateMipmapData(int p_147949_0_, int p_147949_1_, int[][] p_147949_2_) {
		int[][] aint = new int[p_147949_0_ + 1][];
		aint[0] = p_147949_2_[0];

		if (p_147949_0_ > 0) {
			boolean flag = false;

			for (int i = 0; i < p_147949_2_.length; ++i) {
				if (p_147949_2_[0][i] >> 24 == 0) {
					flag = true;
					break;
				}
			}

			for (int l1 = 1; l1 <= p_147949_0_; ++l1) {
				if (p_147949_2_[l1] != null) {
					aint[l1] = p_147949_2_[l1];
				} else {
					int[] aint1 = aint[l1 - 1];
					int[] aint2 = new int[aint1.length >> 2];
					int j = p_147949_1_ >> l1;
					int k = aint2.length / j;
					int l = j << 1;

					for (int i1 = 0; i1 < j; ++i1) {
						for (int j1 = 0; j1 < k; ++j1) {
							int k1 = 2 * (i1 + j1 * l);
							aint2[i1 + j1 * j] = blendColors(aint1[k1 + 0], aint1[k1 + 1], aint1[k1 + 0 + l],
									aint1[k1 + 1 + l], flag);
						}
					}

					aint[l1] = aint2;
				}
			}
		}

		return aint;
	}

	private static int blendColors(int p_147943_0_, int p_147943_1_, int p_147943_2_, int p_147943_3_,
			boolean p_147943_4_) {
		if (p_147943_4_) {
			MIPMAP_BUFFER[0] = p_147943_0_;
			MIPMAP_BUFFER[1] = p_147943_1_;
			MIPMAP_BUFFER[2] = p_147943_2_;
			MIPMAP_BUFFER[3] = p_147943_3_;
			float f = 0.0F;
			float f1 = 0.0F;
			float f2 = 0.0F;
			float f3 = 0.0F;

			for (int i1 = 0; i1 < 4; ++i1) {
				if (MIPMAP_BUFFER[i1] >> 24 != 0) {
					f += getColorGamma(MIPMAP_BUFFER[i1] >> 24);
					f1 += getColorGamma(MIPMAP_BUFFER[i1] >> 16);
					f2 += getColorGamma(MIPMAP_BUFFER[i1] >> 8);
					f3 += getColorGamma(MIPMAP_BUFFER[i1] >> 0);
				}
			}

			f = f / 4.0F;
			f1 = f1 / 4.0F;
			f2 = f2 / 4.0F;
			f3 = f3 / 4.0F;
			int i2 = (int) (Math.pow((double) f, 0.45454545454545453D) * 255.0D);
			int j1 = (int) (Math.pow((double) f1, 0.45454545454545453D) * 255.0D);
			int k1 = (int) (Math.pow((double) f2, 0.45454545454545453D) * 255.0D);
			int l1 = (int) (Math.pow((double) f3, 0.45454545454545453D) * 255.0D);

			if (i2 < 96) {
				i2 = 0;
			}

			return i2 << 24 | j1 << 16 | k1 << 8 | l1;
		} else {
			int i = blendColorComponent(p_147943_0_, p_147943_1_, p_147943_2_, p_147943_3_, 24);
			int j = blendColorComponent(p_147943_0_, p_147943_1_, p_147943_2_, p_147943_3_, 16);
			int k = blendColorComponent(p_147943_0_, p_147943_1_, p_147943_2_, p_147943_3_, 8);
			int l = blendColorComponent(p_147943_0_, p_147943_1_, p_147943_2_, p_147943_3_, 0);
			return i << 24 | j << 16 | k << 8 | l;
		}
	}

	private static int blendColorComponent(int p_147944_0_, int p_147944_1_, int p_147944_2_, int p_147944_3_,
			int p_147944_4_) {
		float f = getColorGamma(p_147944_0_ >> p_147944_4_);
		float f1 = getColorGamma(p_147944_1_ >> p_147944_4_);
		float f2 = getColorGamma(p_147944_2_ >> p_147944_4_);
		float f3 = getColorGamma(p_147944_3_ >> p_147944_4_);
		float f4 = (float) ((double) ((float) Math.pow((double) (f + f1 + f2 + f3) * 0.25D, 0.45454545454545453D)));
		return (int) ((double) f4 * 255.0D);
	}

	public static void uploadTextureMipmap(int[][] p_147955_0_, int p_147955_1_, int p_147955_2_, int p_147955_3_,
			int p_147955_4_, boolean p_147955_5_, boolean p_147955_6_) {
		for (int i = 0; i < p_147955_0_.length; ++i) {
			int[] aint = p_147955_0_[i];
			uploadTextureSub(i, aint, p_147955_1_ >> i, p_147955_2_ >> i, p_147955_3_ >> i, p_147955_4_ >> i,
					p_147955_5_, p_147955_6_, p_147955_0_.length > 1);
		}
	}

	private static void uploadTextureSub(int parInt1, int[] parArrayOfInt, int parInt2, int parInt3, int parInt4,
			int parInt5, boolean parFlag, boolean parFlag2, boolean parFlag3) {
		int i;
		if (parInt2 == 0) {
			i = 0;
		} else {
			i = 4194304 / parInt2;
		}
		setTextureBlurMipmap(parFlag, parFlag3);
		setTextureClamped(parFlag2);

		int l;
		for (int j = 0; j < parInt2 * parInt3; j += parInt2 * l) {
			int k = j / parInt2;
			l = Math.min(i, parInt3 - k);
			int i1 = parInt2 * l;
			copyToBufferPos(parArrayOfInt, j, i1);
			EaglercraftGPU.glTexSubImage2D(RealOpenGLEnums.GL_TEXTURE_2D, parInt1, parInt4, parInt5 + k, parInt2, l,
					RealOpenGLEnums.GL_RGBA, RealOpenGLEnums.GL_UNSIGNED_BYTE, DATA_BUFFER);
		}

	}

	public static int uploadTextureImageAllocate(int textureId, ImageData texture, boolean blur, boolean clamp) {
		allocateTexture(textureId, texture.width, texture.height);
		return uploadTextureImageSub(textureId, texture, 0, 0, blur, clamp);
	}

	public static void allocateTexture(int textureId, int width, int height) {
		allocateTextureImpl(textureId, 0, width, height);
	}

	public static void allocateTextureImpl(int glTextureId, int mipmapLevels, int width, int height) {
		bindTexture(glTextureId);

		if (mipmapLevels >= 0) {
			EaglercraftGPU.glTexParameteri(RealOpenGLEnums.GL_TEXTURE_2D, '\u813d', mipmapLevels);
			EaglercraftGPU.glTexParameterf(RealOpenGLEnums.GL_TEXTURE_2D, '\u813a', 0.0F);
			EaglercraftGPU.glTexParameterf(RealOpenGLEnums.GL_TEXTURE_2D, '\u813b', (float) mipmapLevels);
		}

		EaglercraftGPU.glTexStorage2D(RealOpenGLEnums.GL_TEXTURE_2D, mipmapLevels + 1, RealOpenGLEnums.GL_RGBA8, width,
				height);
	}

	public static int uploadTextureImageSub(int textureId, ImageData p_110995_1_, int p_110995_2_, int p_110995_3_,
			boolean p_110995_4_, boolean p_110995_5_) {
		bindTexture(textureId);
		uploadTextureImageSubImpl(p_110995_1_, p_110995_2_, p_110995_3_, p_110995_4_, p_110995_5_);
		return textureId;
	}

	private static void uploadTextureImageSubImpl(ImageData parBufferedImage, int parInt1, int parInt2, boolean parFlag,
			boolean parFlag2) {
		int i = parBufferedImage.width;
		int j = parBufferedImage.height;
		int k = 4194304 / i;
		int[] aint = new int[k * i];
		setTextureBlurred(parFlag);
		setTextureClamped(parFlag2);

		for (int l = 0; l < i * j; l += i * k) {
			int i1 = l / i;
			int j1 = Math.min(k, j - i1);
			int k1 = i * j1;
			parBufferedImage.getRGB(0, i1, i, j1, aint, 0, i);
			copyToBuffer(aint, k1);
			EaglercraftGPU.glTexSubImage2D(RealOpenGLEnums.GL_TEXTURE_2D, 0, parInt1, parInt2 + i1, i, j1,
					RealOpenGLEnums.GL_RGBA, RealOpenGLEnums.GL_UNSIGNED_BYTE, DATA_BUFFER);
		}

	}

	private static void setTextureClamped(boolean p_110997_0_) {
		if (p_110997_0_) {
			EaglercraftGPU.glTexParameteri(RealOpenGLEnums.GL_TEXTURE_2D, RealOpenGLEnums.GL_TEXTURE_WRAP_S,
					RealOpenGLEnums.GL_CLAMP_TO_EDGE);
			EaglercraftGPU.glTexParameteri(RealOpenGLEnums.GL_TEXTURE_2D, RealOpenGLEnums.GL_TEXTURE_WRAP_T,
					RealOpenGLEnums.GL_CLAMP_TO_EDGE);
		} else {
			EaglercraftGPU.glTexParameteri(RealOpenGLEnums.GL_TEXTURE_2D, RealOpenGLEnums.GL_TEXTURE_WRAP_S,
					RealOpenGLEnums.GL_REPEAT);
			EaglercraftGPU.glTexParameteri(RealOpenGLEnums.GL_TEXTURE_2D, RealOpenGLEnums.GL_TEXTURE_WRAP_T,
					RealOpenGLEnums.GL_REPEAT);
		}
	}

	private static void setTextureBlurred(boolean p_147951_0_) {
		setTextureBlurMipmap(p_147951_0_, false);
	}

	private static void setTextureBlurMipmap(boolean p_147954_0_, boolean p_147954_1_) {
		if (p_147954_0_) {
			EaglercraftGPU.glTexParameteri(3553, 10241, p_147954_1_ ? 9987 : 9729);
			EaglercraftGPU.glTexParameteri(3553, 10240, 9729);
		} else {
			EaglercraftGPU.glTexParameteri(3553, 10241, p_147954_1_ ? 9986 : 9728);
			EaglercraftGPU.glTexParameteri(3553, 10240, 9728);
		}
	}

	private static void copyToBuffer(int[] p_110990_0_, int p_110990_1_) {
		copyToBufferPos(p_110990_0_, 0, p_110990_1_);
	}

	private static void copyToBufferPos(int[] p_110994_0_, int p_110994_1_, int p_110994_2_) {
		int[] aint = p_110994_0_;

		if (Minecraft.getMinecraft().gameSettings.anaglyph) {
			aint = updateAnaglyph(p_110994_0_);
		}

		DATA_BUFFER.clear();
		DATA_BUFFER.put(aint, p_110994_1_, p_110994_2_);
		DATA_BUFFER.position(0).limit(p_110994_2_);
	}

	static void bindTexture(int p_94277_0_) {
		GlStateManager.bindTexture(p_94277_0_);
	}

	public static int[] readImageData(IResourceManager resourceManager, ResourceLocation imageLocation)
			throws IOException {
		IResource iresource = null;
		int[] aint1;

		try {
			iresource = resourceManager.getResource(imageLocation);
			ImageData bufferedimage = readBufferedImage(iresource.getInputStream());
			int i = bufferedimage.width;
			int j = bufferedimage.height;
			int[] aint = new int[i * j];
			bufferedimage.getRGB(0, 0, i, j, aint, 0, i);
			aint1 = aint;
		} finally {
			IOUtils.closeQuietly((Closeable) iresource);
		}

		return aint1;
	}

	public static int[] readImageData_SWAP_RB(IResourceManager resourceManager, ResourceLocation imageLocation)
			throws IOException {
		IResource iresource = null;
		int[] aint1;

		try {
			iresource = resourceManager.getResource(imageLocation);
			ImageData bufferedimage = readBufferedImage(iresource.getInputStream()).swapRB();
			int i = bufferedimage.width;
			int j = bufferedimage.height;
			int[] aint = new int[i * j];
			bufferedimage.getRGB(0, 0, i, j, aint, 0, i);
			aint1 = aint;
		} finally {
			IOUtils.closeQuietly((Closeable) iresource);
		}

		return aint1;
	}

	public static ImageData readBufferedImage(InputStream imageStream) throws IOException {
		ImageData bufferedimage;

		try {
			bufferedimage = ImageData.loadImageFile(imageStream);
		} finally {
			IOUtils.closeQuietly(imageStream);
		}

		return bufferedimage;
	}

	public static int[] updateAnaglyph(int[] p_110985_0_) {
		int[] aint = new int[p_110985_0_.length];

		for (int i = 0; i < p_110985_0_.length; ++i) {
			aint[i] = anaglyphColor(p_110985_0_[i]);
		}

		return aint;
	}

	public static int anaglyphColor(int p_177054_0_) {
		int i = p_177054_0_ >> 24 & 255;
		int j = p_177054_0_ >> 16 & 255;
		int k = p_177054_0_ >> 8 & 255;
		int l = p_177054_0_ & 255;
		int i1 = (j * 30 + k * 59 + l * 11) / 100;
		int j1 = (j * 30 + k * 70) / 100;
		int k1 = (j * 30 + l * 70) / 100;
		return i << 24 | i1 << 16 | j1 << 8 | k1;
	}

	public static void processPixelValues(int[] p_147953_0_, int p_147953_1_, int p_147953_2_) {
		int[] aint = new int[p_147953_1_];
		int i = p_147953_2_ / 2;

		for (int j = 0; j < i; ++j) {
			System.arraycopy(p_147953_0_, j * p_147953_1_, aint, 0, p_147953_1_);
			System.arraycopy(p_147953_0_, (p_147953_2_ - 1 - j) * p_147953_1_, p_147953_0_, j * p_147953_1_,
					p_147953_1_);
			System.arraycopy(aint, 0, p_147953_0_, (p_147953_2_ - 1 - j) * p_147953_1_, p_147953_1_);
		}
	}

	static {
		int i = -16777216;
		int j = -524040;
		int[] aint = new int[] { -524040, -524040, -524040, -524040, -524040, -524040, -524040, -524040 };
		int[] aint1 = new int[] { -16777216, -16777216, -16777216, -16777216, -16777216, -16777216, -16777216,
				-16777216 };
		int k = aint.length;

		for (int l = 0; l < 16; ++l) {
			System.arraycopy(l < k ? aint : aint1, 0, MISSING_TEXTURE_DATA, 16 * l, k);
			System.arraycopy(l < k ? aint1 : aint, 0, MISSING_TEXTURE_DATA, 16 * l + k, k);
		}

		MISSING_TEXTURE.updateDynamicTexture();
		COLOR_GAMMAS = new float[256];

		for (int i1 = 0; i1 < COLOR_GAMMAS.length; ++i1) {
			COLOR_GAMMAS[i1] = (float) Math.pow((double) ((float) i1 / 255.0F), 2.2D);
		}

		MIPMAP_BUFFER = new int[4];
	}
}
