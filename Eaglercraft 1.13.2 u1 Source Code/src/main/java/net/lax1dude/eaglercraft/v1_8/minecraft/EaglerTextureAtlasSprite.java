/*
 * Copyright (c) 2022-2025 lax1dude. All Rights Reserved.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 */

package net.lax1dude.eaglercraft.v1_8.minecraft;

import java.io.IOException;
import java.util.List;

import com.google.common.collect.Lists;

import net.lax1dude.eaglercraft.v1_8.HString;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;
import net.lax1dude.eaglercraft.v1_8.opengl.ImageData;
import net.minecraft.client.resources.data.AnimationFrame;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.optifine.util.CounterInt;

public class EaglerTextureAtlasSprite {

	private static final Logger logger = LogManager.getLogger("EaglerTextureAtlasSprite");

	protected final String iconName;
	protected List<int[][]> framesTextureData = Lists.newArrayList();
	protected int[][] interpolatedFrameData;
	protected AnimationMetadataSection animationMetadata;
	protected boolean rotated;
	protected int originX;
	protected int originY;
	protected int width;
	protected int height;
	protected float minU;
	protected float maxU;
	protected float minV;
	protected float maxV;
	protected int frameCounter;
	protected int tickCounter;
    private int indexInMap = -1;
	protected int[] animationFrameIndices = new int[0];
	protected int[] animationFrameTimes = new int[0];
	protected static String locationNameClock = "builtin/clock";
	protected static String locationNameCompass = "builtin/compass";

	protected TextureAnimationCache animationCache = null;

	public String optifineBaseTextureName = null;

	public EaglerTextureAtlasSprite(String spriteName) {
		this.iconName = spriteName;
	}

	public static EaglerTextureAtlasSprite makeAtlasSprite(ResourceLocation spriteResourceLocation) {
		String s = spriteResourceLocation.toString();
		return (EaglerTextureAtlasSprite) (locationNameClock.equals(s) ? new TextureClock(s)
				: (locationNameCompass.equals(s) ? new TextureCompass(s) : new EaglerTextureAtlasSprite(s)));
	}

	public static void setLocationNameClock(String clockName) {
		locationNameClock = clockName;
	}

	public static void setLocationNameCompass(String compassName) {
		locationNameCompass = compassName;
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

	public void copyFrom(EaglerTextureAtlasSprite atlasSpirit) {
		this.originX = atlasSpirit.originX;
		this.originY = atlasSpirit.originY;
		this.width = atlasSpirit.width;
		this.height = atlasSpirit.height;
		this.rotated = atlasSpirit.rotated;
		this.minU = atlasSpirit.minU;
		this.maxU = atlasSpirit.maxU;
		this.minV = atlasSpirit.minV;
		this.maxV = atlasSpirit.maxV;
		if (atlasSpirit.indexInMap >= 0) {
			this.indexInMap = atlasSpirit.indexInMap;
		}
	}

	public int getOriginX() {
		return this.originX;
	}

	public int getOriginY() {
		return this.originY;
	}

	public int getIconWidth() {
		return this.width;
	}

	public int getIconHeight() {
		return this.height;
	}

	public float getMinU() {
		return this.minU;
	}

	public float getMaxU() {
		return this.maxU;
	}

	public float getInterpolatedU(double u) {
		float f = this.maxU - this.minU;
		return this.minU + f * (float) u / 16.0F;
	}

	public float getMinV() {
		return this.minV;
	}

	public float getMaxV() {
		return this.maxV;
	}

	public float getInterpolatedV(double v) {
		float f = this.maxV - this.minV;
		return this.minV + f * ((float) v / 16.0F);
	}

	public String getIconName() {
		return this.iconName;
	}

	protected static interface IAnimCopyFunction {
		void updateAnimation(int mapWidth, int mapHeight, int mapLevel);
	}

	protected IAnimCopyFunction currentAnimUpdater = null;

	public void updateAnimation() {
		if(animationCache == null) {
			throw new IllegalStateException("Animation cache for '" + this.iconName + "' was never baked!");
		}
		++this.tickCounter;
		if (this.tickCounter >= this.getAnimationFrameTimeSingle(this.frameCounter)) {
			int i = this.getAnimationFrameIndex(this.frameCounter);
			int j = this.getAnimationFrameCount();
			this.frameCounter = (this.frameCounter + 1) % j;
			this.tickCounter = 0;
			int k = this.getAnimationFrameIndex(this.frameCounter);
			if (i != k && k >= 0 && k < this.framesTextureData.size()) {
				currentAnimUpdater = (mapWidth, mapHeight, mapLevel) -> {
					animationCache.copyFrameToTex2D(k, mapLevel, this.originX >> mapLevel, this.originY >> mapLevel,
							this.width >> mapLevel, this.height >> mapLevel, mapWidth, mapHeight);
				};
			}else {
				currentAnimUpdater = null;
			}
		} else if (this.animationMetadata.isInterpolate()) {
			float f = 1.0f - (float) this.tickCounter / (float) this.getAnimationFrameTimeSingle(this.frameCounter);
			int i = this.getAnimationFrameIndex(this.frameCounter);
			int j = this.getAnimationFrameCount();
			int k = this.getAnimationFrameIndex((this.frameCounter + 1) % j);
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
		return (int[][]) this.framesTextureData.get(index);
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
				if (k > 0 && ((bufferedimage.width) != i >> k || bufferedimage.height != j >> k)) {
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
			int frameSize = Math.min(i, j);
			int k1 = meta.getFrameWidth() == -1 ? frameSize : meta.getFrameWidth();
			int l = meta.getFrameHeight() == -1 ? frameSize : meta.getFrameHeight();
			int frameColumns = i / k1;
			int frameRows = j / l;
			int frameCount = frameColumns * frameRows;
			this.width = k1;
			this.height = l;
			List<AnimationFrame> declaredFrames = Lists.newArrayList();
			for(int m = 0, n = meta.getFrameCount(); m < n; ++m) {
				declaredFrames.add(new AnimationFrame(meta.getFrameIndex(m), meta.getFrameTimeSingle(m)));
			}
			if (!declaredFrames.isEmpty()) {
				for (AnimationFrame frame : declaredFrames) {
					int i1 = frame.getFrameIndex();
					if (i1 >= frameCount) {
						throw new RuntimeException("invalid frameindex " + i1);
					}

					this.allocateFrameTextureData(i1);
					this.framesTextureData.set(i1, getFrameTextureData(aint, i, k1, l, i1));
				}

				this.animationMetadata = new AnimationMetadataSection(declaredFrames, this.width, this.height,
						meta.getFrameTime(), meta.isInterpolate());
			} else {
				List<AnimationFrame> arraylist = Lists.newArrayList();

				for (int l1 = 0; l1 < frameCount; ++l1) {
					this.framesTextureData.add(getFrameTextureData(aint, i, k1, l, l1));
					arraylist.add(new AnimationFrame(l1, -1));
				}

				this.animationMetadata = new AnimationMetadataSection(arraylist, this.width, this.height,
						meta.getFrameTime(), meta.isInterpolate());
			}
			this.initializeAnimationFrames(this.animationMetadata);
		}

	}

	public void generateMipmaps(int level) {
		List<int[][]> arraylist = Lists.newArrayList();

		for (int i = 0; i < this.framesTextureData.size(); ++i) {
			final int[][] aint = this.framesTextureData.get(i);
			if (aint != null) {
				try {
					arraylist.add(generateMipmapData(level, this.width, aint));
				} catch (Throwable throwable) {
					CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Generating mipmaps for frame");
					CrashReportCategory crashreportcategory = crashreport.makeCategory("Frame being iterated");
					crashreportcategory.addDetail("Frame index", Integer.valueOf(i));
					crashreportcategory.addDetail("Frame sizes", () -> {
						StringBuilder stringbuilder = new StringBuilder();

						for (int j = 0; j < aint.length; ++j) {
							int[] aint1 = aint[j];
							if (stringbuilder.length() > 0) {
								stringbuilder.append(", ");
							}

							stringbuilder.append(aint1 == null ? "null" : Integer.valueOf(aint1.length));
						}

						return stringbuilder.toString();
					});
					throw new ReportedException(crashreport);
				}
			}
		}

		this.setFramesTextureData(arraylist);
		this.bakeAnimationCache();
	}

	public void bakeAnimationCache() {
		if(animationMetadata != null) {
			int mipLevels = framesTextureData.get(0).length;
			if(animationCache == null) {
				animationCache = new TextureAnimationCache(width, height, mipLevels);
			}
			animationCache.initialize(framesTextureData);
		}
	}

	protected void allocateFrameTextureData(int index) {
		if (this.framesTextureData.size() <= index) {
			for (int i = this.framesTextureData.size(); i <= index; ++i) {
				this.framesTextureData.add((int[][]) null);
			}
		}
	}

	protected static int[][] getFrameTextureData(int[][] data, int imageWidth, int frameWidth, int frameHeight, int frameIndex) {
		int[][] aint = new int[data.length][];

		for (int i = 0; i < data.length; ++i) {
			int[] aint1 = data[i];
			if (aint1 != null) {
				int levelImageWidth = Math.max(1, imageWidth >> i);
				int levelFrameWidth = Math.max(1, frameWidth >> i);
				int levelFrameHeight = Math.max(1, frameHeight >> i);
				int framesPerRow = Math.max(1, levelImageWidth / levelFrameWidth);
				int frameX = frameIndex % framesPerRow * levelFrameWidth;
				int frameY = frameIndex / framesPerRow * levelFrameHeight;
				aint[i] = new int[levelFrameWidth * levelFrameHeight];
				for (int y = 0; y < levelFrameHeight; ++y) {
					System.arraycopy(aint1, (frameY + y) * levelImageWidth + frameX, aint[i], y * levelFrameWidth,
							levelFrameWidth);
				}
			}
		}

		return aint;
	}

	protected void initializeAnimationFrames(AnimationMetadataSection meta) {
		List<AnimationFrame> frames = Lists.newArrayList();
		for(int i = 0, l = meta.getFrameCount(); i < l; ++i) {
			frames.add(new AnimationFrame(meta.getFrameIndex(i), meta.getFrameTimeSingle(i)));
		}
		this.animationFrameIndices = new int[frames.size()];
		this.animationFrameTimes = new int[frames.size()];
		for (int i = 0; i < frames.size(); ++i) {
			AnimationFrame frame = frames.get(i);
			this.animationFrameIndices[i] = frame.getFrameIndex();
			this.animationFrameTimes[i] = frame.hasNoTime() ? meta.getFrameTime() : frame.getFrameTime();
		}
	}

	protected int getAnimationFrameCount() {
		return this.animationFrameIndices.length == 0 ? this.framesTextureData.size() : this.animationFrameIndices.length;
	}

	protected int getAnimationFrameIndex(int index) {
		return this.animationFrameIndices.length == 0 ? index : this.animationFrameIndices[index];
	}

	protected int getAnimationFrameTimeSingle(int index) {
		return this.animationFrameTimes.length == 0 ? this.animationMetadata.getFrameTime() : this.animationFrameTimes[index];
	}

	protected static int[][] generateMipmapData(int level, int width, int[][] data) {
		int[][] result = new int[level + 1][];
		result[0] = data[0];
		boolean transparent = false;
		for (int color : result[0]) {
			if ((color >>> 24) == 0) {
				transparent = true;
				break;
			}
		}

		for (int i = 1; i <= level; ++i) {
			if (i < data.length && data[i] != null) {
				result[i] = data[i];
				continue;
			}

			int[] previous = result[i - 1];
			int previousWidth = Math.max(1, width >> (i - 1));
			int previousHeight = Math.max(1, previous.length / previousWidth);
			int nextWidth = Math.max(1, previousWidth >> 1);
			int nextHeight = Math.max(1, previousHeight >> 1);
			int[] next = new int[nextWidth * nextHeight];

			for (int y = 0; y < nextHeight; ++y) {
				for (int x = 0; x < nextWidth; ++x) {
					int x0 = Math.min(previousWidth - 1, x << 1);
					int y0 = Math.min(previousHeight - 1, y << 1);
					int x1 = Math.min(previousWidth - 1, x0 + 1);
					int y1 = Math.min(previousHeight - 1, y0 + 1);
					next[y * nextWidth + x] = blendColors(previous[y0 * previousWidth + x0],
							previous[y0 * previousWidth + x1], previous[y1 * previousWidth + x0],
							previous[y1 * previousWidth + x1], transparent);
				}
			}

			result[i] = next;
		}

		return result;
	}

	private static int blendColors(int c0, int c1, int c2, int c3, boolean transparent) {
		return transparent ? accumulateColor(c0, c1, c2, c3) : blendColorComponent(c0, c1, c2, c3, 24) << 24
				| blendColorComponent(c0, c1, c2, c3, 16) << 16 | blendColorComponent(c0, c1, c2, c3, 8) << 8
				| blendColorComponent(c0, c1, c2, c3, 0);
	}

	private static int accumulateColor(int c0, int c1, int c2, int c3) {
		int alpha = 0;
		int red = 0;
		int green = 0;
		int blue = 0;
		int samples = 0;
		int[] colors = new int[] { c0, c1, c2, c3 };
		for (int color : colors) {
			int sampleAlpha = color >>> 24;
			if (sampleAlpha != 0) {
				alpha += sampleAlpha;
				red += color >> 16 & 255;
				green += color >> 8 & 255;
				blue += color & 255;
				++samples;
			}
		}

		if (samples == 0) {
			return 0;
		}

		return alpha / samples << 24 | red / samples << 16 | green / samples << 8 | blue / samples;
	}

	private static int blendColorComponent(int c0, int c1, int c2, int c3, int shift) {
		float f0 = convertGamma(c0 >> shift & 255);
		float f1 = convertGamma(c1 >> shift & 255);
		float f2 = convertGamma(c2 >> shift & 255);
		float f3 = convertGamma(c3 >> shift & 255);
		return (int) (Math.pow((double) ((f0 + f1 + f2 + f3) * 0.25F), 0.45454545454545453D) * 255.0D);
	}

	private static float convertGamma(int value) {
		return (float) Math.pow((double) value / 255.0D, 2.2D);
	}

	public void clearFramesTextureData() {
		this.framesTextureData.clear();
		if(this.animationCache != null) {
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

	protected void resetSprite() {
		this.animationMetadata = null;
		this.animationFrameIndices = new int[0];
		this.animationFrameTimes = new int[0];
		this.setFramesTextureData(Lists.newArrayList());
		this.frameCounter = 0;
		this.tickCounter = 0;
		if(this.animationCache != null) {
			this.animationCache.free();
			this.animationCache = null;
		}
	}

	public String toString() {
		return "TextureAtlasSprite{name=\'" + this.iconName + '\'' + ", frameCount=" + this.framesTextureData.size()
				+ ", rotated=" + this.rotated + ", x=" + this.originX + ", y=" + this.originY + ", height="
				+ this.height + ", width=" + this.width + ", u0=" + this.minU + ", u1=" + this.maxU + ", v0="
				+ this.minV + ", v1=" + this.maxV + '}';
	}

	public void loadSpritePBR(ImageData[][] imageDatas, AnimationMetadataSection animationmetadatasection,
			boolean dontAnimateNormals, boolean dontAnimateMaterial) {
		Throwable t = new UnsupportedOperationException("PBR is not enabled");
		try {
			throw t;
		}catch(Throwable tt) {
			logger.error(t);
		}
	}

	public void updateAnimationPBR() {
		Throwable t = new UnsupportedOperationException("PBR is not enabled");
		try {
			throw t;
		}catch(Throwable tt) {
			logger.error(t);
		}
	}

	public void copyAnimationFramePBR(int pass, int width, int height, int level) {
		Throwable t = new UnsupportedOperationException("PBR is not enabled");
		try {
			throw t;
		}catch(Throwable tt) {
			logger.error(t);
		}
	}

	public int getIndexInMap() {
		return this.indexInMap;
	}

	public void setIndexInMap(int p_setIndexInMap_1_) {
		this.indexInMap = p_setIndexInMap_1_;
	}

	public void updateIndexInMap(CounterInt p_updateIndexInMap_1_) {
		if (this.indexInMap < 0) {
			this.indexInMap = p_updateIndexInMap_1_.nextValue();
		}
	}

	public double getSpriteU16(float p_getSpriteU16_1_) {
		float f = this.maxU - this.minU;
		return (double) ((p_getSpriteU16_1_ - this.minU) / f * 16.0F);
	}

	public double getSpriteV16(float p_getSpriteV16_1_) {
		float f = this.maxV - this.minV;
		return (double) ((p_getSpriteV16_1_ - this.minV) / f * 16.0F);
	}
}
