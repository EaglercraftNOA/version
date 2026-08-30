package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.lax1dude.eaglercraft.HString;
import net.lax1dude.eaglercraft.internal.IFramebufferGL;
import net.lax1dude.eaglercraft.minecraft.TextureAnimationCache;
import net.lax1dude.eaglercraft.opengl.ImageData;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.data.AnimationFrame;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ICrashReportDetail;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;

public class TextureAtlasSprite {
	private final String iconName;
	protected List<int[][]> framesTextureData = Lists.<int[][]>newArrayList();
	private AnimationMetadataSection animationMetadata;
	protected boolean rotated;
	protected int originX;
	protected int originY;
	protected int width;
	protected int height;
	private float minU;
	private float maxU;
	private float minV;
	private float maxV;
	protected int frameCounter;
	protected int tickCounter;

	protected TextureAnimationCache animationCache = null;

	protected TextureAtlasSprite(String spriteName) {
		this.iconName = spriteName;
	}

	protected static TextureAtlasSprite makeAtlasSprite(ResourceLocation spriteResourceLocation) {
		return new TextureAtlasSprite(spriteResourceLocation.toString());
	}

	public void initSprite(int inX, int inY, int originInX, int originInY, boolean rotatedIn) {
		this.originX = originInX;
		this.originY = originInY;
		this.rotated = rotatedIn;
		float f = (float) (0.009999999776482582D / (double) inX);
		float f1 = (float) (0.009999999776482582D / (double) inY);
		this.minU = (float) originInX / (float) ((double) inX) + f;
		this.maxU = (float) (originInX + this.width) / (float) ((double) inX) - f;
		this.minV = (float) originInY / (float) inY + f1;
		this.maxV = (float) (originInY + this.height) / (float) inY - f1;
	}

	public void copyFrom(TextureAtlasSprite atlasSpirit) {
		this.originX = atlasSpirit.originX;
		this.originY = atlasSpirit.originY;
		this.width = atlasSpirit.width;
		this.height = atlasSpirit.height;
		this.rotated = atlasSpirit.rotated;
		this.minU = atlasSpirit.minU;
		this.maxU = atlasSpirit.maxU;
		this.minV = atlasSpirit.minV;
		this.maxV = atlasSpirit.maxV;
	}

	/**
	 * Returns the X position of this icon on its texture sheet, in pixels.
	 */
	public int getOriginX() {
		return this.originX;
	}

	/**
	 * Returns the Y position of this icon on its texture sheet, in pixels.
	 */
	public int getOriginY() {
		return this.originY;
	}

	/**
	 * Returns the width of the icon, in pixels.
	 */
	public int getIconWidth() {
		return this.width;
	}

	/**
	 * Returns the height of the icon, in pixels.
	 */
	public int getIconHeight() {
		return this.height;
	}

	/**
	 * Returns the minimum U coordinate to use when rendering with this icon.
	 */
	public float getMinU() {
		return this.minU;
	}

	/**
	 * Returns the maximum U coordinate to use when rendering with this icon.
	 */
	public float getMaxU() {
		return this.maxU;
	}

	/**
	 * Gets a U coordinate on the icon. 0 returns uMin and 16 returns uMax. Other
	 * arguments return in-between values.
	 */
	public float getInterpolatedU(double u) {
		float f = this.maxU - this.minU;
		return this.minU + f * (float) u / 16.0F;
	}

	/**
	 * The opposite of getInterpolatedU. Takes the return value of that method and
	 * returns the input to it.
	 */
	public float getUnInterpolatedU(float p_188537_1_) {
		float f = this.maxU - this.minU;
		return (p_188537_1_ - this.minU) / f * 16.0F;
	}

	/**
	 * Returns the minimum V coordinate to use when rendering with this icon.
	 */
	public float getMinV() {
		return this.minV;
	}

	/**
	 * Returns the maximum V coordinate to use when rendering with this icon.
	 */
	public float getMaxV() {
		return this.maxV;
	}

	/**
	 * Gets a V coordinate on the icon. 0 returns vMin and 16 returns vMax. Other
	 * arguments return in-between values.
	 */
	public float getInterpolatedV(double v) {
		float f = this.maxV - this.minV;
		return this.minV + f * (float) v / 16.0F;
	}

	/**
	 * The opposite of getInterpolatedV. Takes the return value of that method and
	 * returns the input to it.
	 */
	public float getUnInterpolatedV(float p_188536_1_) {
		float f = this.maxV - this.minV;
		return (p_188536_1_ - this.minV) / f * 16.0F;
	}

	public String getIconName() {
		return this.iconName;
	}
	
	protected static interface IAnimCopyFunction {
		void updateAnimation(int mapWidth, int mapHeight, int mapLevel);
	}

	protected IAnimCopyFunction currentAnimUpdater = null;

	public void updateAnimation() {
		if (animationCache == null) {
			throw new IllegalStateException("Animation cache for '" + this.iconName + "' was never baked!");
		}
		++this.tickCounter;

		if (this.tickCounter >= this.animationMetadata.getFrameTimeSingle(this.frameCounter)) {
			int i = this.animationMetadata.getFrameIndex(this.frameCounter);
			int j = this.animationMetadata.getFrameCount() == 0 ? this.framesTextureData.size()
					: this.animationMetadata.getFrameCount();
			this.frameCounter = (this.frameCounter + 1) % j;
			this.tickCounter = 0;
			int k = this.animationMetadata.getFrameIndex(this.frameCounter);

			if (i != k && k >= 0 && k < this.framesTextureData.size()) {
				currentAnimUpdater = (mapWidth, mapHeight, mapLevel) -> {
					animationCache.copyFrameToTex2D(k, mapLevel, this.originX >> mapLevel, this.originY >> mapLevel,
							this.width >> mapLevel, this.height >> mapLevel, mapWidth, mapHeight);
				};
			}else {
				currentAnimUpdater = null;
			}
		} else if (this.animationMetadata.isInterpolate()) {
			float f = 1.0f
					- (float) this.tickCounter / (float) this.animationMetadata.getFrameTimeSingle(this.frameCounter);
			int i = this.animationMetadata.getFrameIndex(this.frameCounter);
			int j = this.animationMetadata.getFrameCount() == 0 ? this.framesTextureData.size()
					: this.animationMetadata.getFrameCount();
			int k = this.animationMetadata.getFrameIndex((this.frameCounter + 1) % j);
			if (i != k && k >= 0 && k < this.framesTextureData.size()) {
				currentAnimUpdater = (mapWidth, mapHeight, mapLevel) -> {
					animationCache.copyInterpolatedFrameToTex2D(i, k, f, mapLevel, this.originX >> mapLevel,
							this.originY >> mapLevel, this.width >> mapLevel, this.height >> mapLevel, mapWidth,
							mapHeight);
				};
			}else {
				currentAnimUpdater = null;
			}
		} else {
			currentAnimUpdater = null;
		}
	}
	
	public void copyAnimationFrame(int mapWidth, int mapHeight, int mapLevel) {
		if(currentAnimUpdater != null) {
			currentAnimUpdater.updateAnimation(mapWidth, mapHeight, mapLevel);
		}
	}

	public int[][] getFrameTextureData(int index) {
		return this.framesTextureData.get(index);
	}

	public int getFrameCount() {
		return this.framesTextureData.size();
	}

	public void setIconWidth(int newWidth) {
		this.width = newWidth;
	}

	public void setIconHeight(int newHeight) {
		this.height = newHeight;
	}

	public void loadSprite(ImageData[] images, AnimationMetadataSection meta) throws IOException {
		this.resetSprite();
		int i = images[0].width;
		int j = images[0].height;
		this.width = i;
		this.height = j;
		int[][] aint = new int[images.length][];

		for (int k = 0; k < images.length; ++k) {
			ImageData bufferedimage = images[k];
			if (bufferedimage != null) {
				if (k > 0 && (bufferedimage.width) != i >> k || bufferedimage.height != j >> k) {
					throw new RuntimeException(
							HString.format("Unable to load miplevel: %d, image is size: %dx%d, expected %dx%d",
									new Object[] { Integer.valueOf(k), Integer.valueOf(bufferedimage.width),
											Integer.valueOf(bufferedimage.height), Integer.valueOf(i >> k),
											Integer.valueOf(j >> k) }));
				}

				aint[k] = new int[bufferedimage.width * bufferedimage.height];
				bufferedimage.getRGB(0, 0, bufferedimage.width, bufferedimage.height, aint[k], 0, bufferedimage.width);
			}
		}

		if (meta == null) {
			if (j != i) {
				throw new RuntimeException("broken aspect ratio and not an animation");
			}

			this.framesTextureData.add(aint);
		} else {
			int j1 = j / i;
			int k1 = i;
			int l = i;
			this.height = this.width;
			if (meta.getFrameCount() > 0) {
				Iterator iterator = meta.getFrameIndexSet().iterator();

				while (iterator.hasNext()) {
					int i1 = ((Integer) iterator.next()).intValue();
					if (i1 >= j1) {
						throw new RuntimeException("invalid frameindex " + i1);
					}

					this.allocateFrameTextureData(i1);
					this.framesTextureData.set(i1, getFrameTextureData(aint, k1, l, i1));
				}

				this.animationMetadata = meta;
			} else {
				ArrayList arraylist = Lists.newArrayList();

				for (int l1 = 0; l1 < j1; ++l1) {
					this.framesTextureData.add(getFrameTextureData(aint, k1, l, l1));
					arraylist.add(new AnimationFrame(l1, -1));
				}

				this.animationMetadata = new AnimationMetadataSection(arraylist, this.width, this.height,
						meta.getFrameTime(), meta.isInterpolate());
			}
		}
	}

	public void generateMipmaps(int level) {
		List<int[][]> list = Lists.<int[][]>newArrayList();

		for (int i = 0; i < this.framesTextureData.size(); ++i) {
			final int[][] aint = this.framesTextureData.get(i);

			if (aint != null) {
				try {
					list.add(TextureUtil.generateMipmapData(level, this.width, aint));
				} catch (Throwable throwable) {
					CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Generating mipmaps for frame");
					CrashReportCategory crashreportcategory = crashreport.makeCategory("Frame being iterated");
					crashreportcategory.addCrashSection("Frame index", Integer.valueOf(i));
					crashreportcategory.setDetail("Frame sizes", new ICrashReportDetail<String>() {
						public String call() throws Exception {
							StringBuilder stringbuilder = new StringBuilder();

							for (int[] aint1 : aint) {
								if (stringbuilder.length() > 0) {
									stringbuilder.append(", ");
								}

								stringbuilder.append(aint1 == null ? "null" : aint1.length);
							}

							return stringbuilder.toString();
						}
					});
					throw new ReportedException(crashreport);
				}
			}
		}

		this.setFramesTextureData(list);
		this.bakeAnimationCache();
	}

	public void bakeAnimationCache() {
		if (animationMetadata != null) {
			int mipLevels = framesTextureData.get(0).length;
			if (animationCache == null) {
				animationCache = new TextureAnimationCache(width, height, mipLevels);
			}
			animationCache.initialize(framesTextureData);
		}
	}

	private void allocateFrameTextureData(int index) {
		if (this.framesTextureData.size() <= index) {
			for (int i = this.framesTextureData.size(); i <= index; ++i) {
				this.framesTextureData.add(null);
			}
		}
	}

	private static int[][] getFrameTextureData(int[][] data, int rows, int columns, int p_147962_3_) {
		int[][] aint = new int[data.length][];

		for (int i = 0; i < data.length; ++i) {
			int[] aint1 = data[i];

			if (aint1 != null) {
				aint[i] = new int[(rows >> i) * (columns >> i)];
				System.arraycopy(aint1, p_147962_3_ * aint[i].length, aint[i], 0, aint[i].length);
			}
		}

		return aint;
	}

	public void clearFramesTextureData() {
		this.framesTextureData.clear();
		if (this.animationCache != null) {
			this.animationCache.free();
			this.animationCache = null;
		}
	}

	public boolean hasAnimationMetadata() {
		return this.animationMetadata != null;
	}

	public void setFramesTextureData(List<int[][]> newFramesTextureData) {
		this.framesTextureData = newFramesTextureData;
	}

	private void resetSprite() {
		this.animationMetadata = null;
		this.setFramesTextureData(Lists.newArrayList());
		this.frameCounter = 0;
		this.tickCounter = 0;
		if (this.animationCache != null) {
			this.animationCache.free();
			this.animationCache = null;
		}
	}

	public String toString() {
		return "TextureAtlasSprite{name='" + this.iconName + '\'' + ", frameCount=" + this.framesTextureData.size()
				+ ", rotated=" + this.rotated + ", x=" + this.originX + ", y=" + this.originY + ", height="
				+ this.height + ", width=" + this.width + ", u0=" + this.minU + ", u1=" + this.maxU + ", v0="
				+ this.minV + ", v1=" + this.maxV + '}';
	}
}