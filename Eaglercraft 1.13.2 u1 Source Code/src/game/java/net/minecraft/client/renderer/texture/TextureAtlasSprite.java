package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import net.lax1dude.eaglercraft.v1_8.minecraft.TextureAnimationCache;
import net.minecraft.client.resources.data.AnimationFrame;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.resources.IResource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TextureAtlasSprite {
   private final ResourceLocation iconName;
   protected final int width;
   protected final int height;
   protected NativeImage[] frames;
   @Nullable
   protected int[] framesX;
   @Nullable
   protected int[] framesY;
   protected TextureAnimationCache animationCache;
   protected TextureAtlasSprite.IAnimCopyFunction currentAnimUpdater;
   private AnimationMetadataSection animationMetadata;
   protected boolean rotated;
   protected int x;
   protected int y;
   private float minU;
   private float maxU;
   private float minV;
   private float maxV;
   protected int frameCounter;
   protected int tickCounter;
   private static final int[] MIPMAP_BUFFER = new int[4];
   private static final float[] COLOR_GAMMAS = Util.make(new float[256], (p_203415_0_) -> {
      for(int i = 0; i < p_203415_0_.length; ++i) {
         p_203415_0_[i] = (float)Math.pow((double)((float)i / 255.0F), 2.2D);
      }

   });

   protected TextureAtlasSprite(ResourceLocation locationIn, int widthIn, int heightIn) {
      this.iconName = locationIn;
      this.width = widthIn;
      this.height = heightIn;
   }

   protected TextureAtlasSprite(ResourceLocation locationIn, PngSizeInfo sizeIn, @Nullable AnimationMetadataSection animationMetadataIn) {
      this.iconName = locationIn;
      if (animationMetadataIn != null) {
         int i = Math.min(sizeIn.width, sizeIn.height);
         this.height = this.width = i;
      } else {
         if (sizeIn.height != sizeIn.width) {
            throw new RuntimeException("broken aspect ratio and not an animation");
         }

         this.width = sizeIn.width;
         this.height = sizeIn.height;
      }

      this.animationMetadata = animationMetadataIn;
   }

   private void generateMipmapsUnchecked(int level) {
      NativeImage[] anativeimage = new NativeImage[level + 1];
      anativeimage[0] = this.frames[0];
      if (level > 0) {
         boolean flag = false;

         label71:
         for(int i = 0; i < this.frames[0].getWidth(); ++i) {
            for(int j = 0; j < this.frames[0].getHeight(); ++j) {
               if (this.frames[0].getPixelRGBA(i, j) >> 24 == 0) {
                  flag = true;
                  break label71;
               }
            }
         }

         for(int k1 = 1; k1 <= level; ++k1) {
            if (this.frames.length > k1 && this.frames[k1] != null) {
               anativeimage[k1] = this.frames[k1];
            } else {
               NativeImage nativeimage1 = anativeimage[k1 - 1];
               NativeImage nativeimage = new NativeImage(nativeimage1.getWidth() >> 1, nativeimage1.getHeight() >> 1, false);
               int k = nativeimage.getWidth();
               int l = nativeimage.getHeight();

               for(int i1 = 0; i1 < k; ++i1) {
                  for(int j1 = 0; j1 < l; ++j1) {
                     nativeimage.setPixelRGBA(i1, j1, blendColors(nativeimage1.getPixelRGBA(i1 * 2 + 0, j1 * 2 + 0), nativeimage1.getPixelRGBA(i1 * 2 + 1, j1 * 2 + 0), nativeimage1.getPixelRGBA(i1 * 2 + 0, j1 * 2 + 1), nativeimage1.getPixelRGBA(i1 * 2 + 1, j1 * 2 + 1), flag));
                  }
               }

               anativeimage[k1] = nativeimage;
            }
         }

         for(int l1 = level + 1; l1 < this.frames.length; ++l1) {
            if (this.frames[l1] != null) {
               this.frames[l1].close();
            }
         }
      }

      this.frames = anativeimage;
   }

   private static int blendColors(int col0, int col1, int col2, int col3, boolean transparent) {
      if (transparent) {
         MIPMAP_BUFFER[0] = col0;
         MIPMAP_BUFFER[1] = col1;
         MIPMAP_BUFFER[2] = col2;
         MIPMAP_BUFFER[3] = col3;
         float f = 0.0F;
         float f1 = 0.0F;
         float f2 = 0.0F;
         float f3 = 0.0F;

         for(int i1 = 0; i1 < 4; ++i1) {
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
         int i2 = (int)(Math.pow((double)f, 0.45454545454545453D) * 255.0D);
         int j1 = (int)(Math.pow((double)f1, 0.45454545454545453D) * 255.0D);
         int k1 = (int)(Math.pow((double)f2, 0.45454545454545453D) * 255.0D);
         int l1 = (int)(Math.pow((double)f3, 0.45454545454545453D) * 255.0D);
         if (i2 < 96) {
            i2 = 0;
         }

         return i2 << 24 | j1 << 16 | k1 << 8 | l1;
      } else {
         int i = blendColorComponent(col0, col1, col2, col3, 24);
         int j = blendColorComponent(col0, col1, col2, col3, 16);
         int k = blendColorComponent(col0, col1, col2, col3, 8);
         int l = blendColorComponent(col0, col1, col2, col3, 0);
         return i << 24 | j << 16 | k << 8 | l;
      }
   }

   private static int blendColorComponent(int col0, int col1, int col2, int col3, int bitOffset) {
      float f = getColorGamma(col0 >> bitOffset);
      float f1 = getColorGamma(col1 >> bitOffset);
      float f2 = getColorGamma(col2 >> bitOffset);
      float f3 = getColorGamma(col3 >> bitOffset);
      float f4 = (float)((double)((float)Math.pow((double)(f + f1 + f2 + f3) * 0.25D, 0.45454545454545453D)));
      return (int)((double)f4 * 255.0D);
   }

   private static float getColorGamma(int colorIn) {
      return COLOR_GAMMAS[colorIn & 255];
   }

   private void uploadFrames(int index) {
      int i = 0;
      int j = 0;
      if (this.framesX != null) {
         i = this.framesX[index] * this.width;
         j = this.framesY[index] * this.height;
      }

      this.uploadFrames(i, j, this.frames);
   }

   private void uploadFrames(int xOffsetIn, int yOffsetIn, NativeImage[] framesIn) {
      for(int i = 0; i < this.frames.length; ++i) {
         framesIn[i].uploadTextureSub(i, this.x >> i, this.y >> i, xOffsetIn >> i, yOffsetIn >> i, this.width >> i, this.height >> i, this.frames.length > 1);
      }

   }

   public void initSprite(int inX, int inY, int originInX, int originInY, boolean rotatedIn) {
      this.x = originInX;
      this.y = originInY;
      this.rotated = rotatedIn;
      float f = (float)((double)0.01F / (double)inX);
      float f1 = (float)((double)0.01F / (double)inY);
      this.minU = (float)originInX / (float)((double)inX) + f;
      this.maxU = (float)(originInX + this.width) / (float)((double)inX) - f;
      this.minV = (float)originInY / (float)inY + f1;
      this.maxV = (float)(originInY + this.height) / (float)inY - f1;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public int getOriginX() {
      return this.x;
   }

   public int getOriginY() {
      return this.y;
   }

   public float getMinU() {
      return this.minU;
   }

   public float getMaxU() {
      return this.maxU;
   }

   public float getInterpolatedU(double u) {
      float f = this.maxU - this.minU;
      return this.minU + f * (float)u / 16.0F;
   }

   public float getUnInterpolatedU(float u) {
      float f = this.maxU - this.minU;
      return (u - this.minU) / f * 16.0F;
   }

   public float getMinV() {
      return this.minV;
   }

   public float getMaxV() {
      return this.maxV;
   }

   public float getInterpolatedV(double v) {
      float f = this.maxV - this.minV;
      return this.minV + f * (float)v / 16.0F;
   }

   public float getUnInterpolatedV(float v) {
      float f = this.maxV - this.minV;
      return (v - this.minV) / f * 16.0F;
   }

   public ResourceLocation getName() {
      return this.iconName;
   }

   public void updateAnimation() {
      if (this.animationCache == null) {
         throw new IllegalStateException("Animation cache for '" + this.iconName + "' was never baked!");
      }

      ++this.tickCounter;
      if (this.tickCounter >= this.animationMetadata.getFrameTimeSingle(this.frameCounter)) {
         int i = this.animationMetadata.getFrameIndex(this.frameCounter);
         int j = this.animationMetadata.getFrameCount() == 0 ? this.getFrameCount() : this.animationMetadata.getFrameCount();
         this.frameCounter = (this.frameCounter + 1) % j;
         this.tickCounter = 0;
         int k = this.animationMetadata.getFrameIndex(this.frameCounter);
         if (i != k && k >= 0 && k < this.getFrameCount()) {
            this.currentAnimUpdater = (mapWidth, mapHeight, mapLevel) -> {
               this.animationCache.copyFrameToTex2D(k, mapLevel, this.x >> mapLevel, this.y >> mapLevel, this.width >> mapLevel, this.height >> mapLevel, mapWidth, mapHeight);
            };
         } else {
            this.currentAnimUpdater = null;
         }
      } else if (this.animationMetadata.isInterpolate()) {
         float f = 1.0F - (float)this.tickCounter / (float)this.animationMetadata.getFrameTimeSingle(this.frameCounter);
         int i = this.animationMetadata.getFrameIndex(this.frameCounter);
         int j = this.animationMetadata.getFrameCount() == 0 ? this.getFrameCount() : this.animationMetadata.getFrameCount();
         int k = this.animationMetadata.getFrameIndex((this.frameCounter + 1) % j);
         if (i != k && k >= 0 && k < this.getFrameCount()) {
            this.currentAnimUpdater = (mapWidth, mapHeight, mapLevel) -> {
               this.animationCache.copyInterpolatedFrameToTex2D(i, k, f, mapLevel, this.x >> mapLevel, this.y >> mapLevel, this.width >> mapLevel, this.height >> mapLevel, mapWidth, mapHeight);
            };
         } else {
            this.currentAnimUpdater = null;
         }
      } else {
         this.currentAnimUpdater = null;
      }

   }

   public void copyAnimationFrame(int mapWidth, int mapHeight, int mapLevel) {
      if (this.currentAnimUpdater != null) {
         this.currentAnimUpdater.updateAnimation(mapWidth, mapHeight, mapLevel);
      }

   }

   public void bakeAnimationCache() {
      if (this.animationCache != null) {
         this.animationCache.free();
         this.animationCache = null;
      }

      int i = this.getFrameCount();
      if (i > 1) {
         List<int[][]> list = Lists.newArrayList();

         for(int j = 0; j < i; ++j) {
            int[][] aint = new int[this.frames.length][];
            int k = this.framesX[j] < 0 ? 0 : this.framesX[j] * this.width;
            int l = this.framesY[j] < 0 ? 0 : this.framesY[j] * this.height;

            for(int i1 = 0; i1 < this.frames.length; ++i1) {
               int j1 = this.width >> i1;
               int k1 = this.height >> i1;
               int[] aint1 = new int[j1 * k1];
               NativeImage nativeimage = this.frames[i1];

               for(int l1 = 0; l1 < k1; ++l1) {
                  for(int i2 = 0; i2 < j1; ++i2) {
                     aint1[l1 * j1 + i2] = nativeimage.getPixelRGBA((k >> i1) + i2, (l >> i1) + l1);
                  }
               }

               aint[i1] = aint1;
            }

            list.add(aint);
         }

         this.animationCache = new TextureAnimationCache(this.width, this.height, this.frames.length);
         this.animationCache.initialize(list);
      }

   }

   public int getFrameCount() {
      return this.framesX == null ? 0 : this.framesX.length;
   }

   public void loadSpriteFrames(IResource resource, int count) throws IOException {
      if (this.frames != null) {
         for (NativeImage oldFrame : this.frames) {
            if (oldFrame != null) {
               oldFrame.close();
            }
         }
      }
      NativeImage nativeimage = NativeImage.read(resource.getInputStream());
      this.frames = new NativeImage[count];
      this.frames[0] = nativeimage;
      int i;
      if (this.animationMetadata != null && this.animationMetadata.getFrameWidth() != -1) {
         i = nativeimage.getWidth() / this.animationMetadata.getFrameWidth();
      } else {
         i = nativeimage.getWidth() / this.width;
      }

      int j;
      if (this.animationMetadata != null && this.animationMetadata.getFrameHeight() != -1) {
         j = nativeimage.getHeight() / this.animationMetadata.getFrameHeight();
      } else {
         j = nativeimage.getHeight() / this.height;
      }

      if (this.animationMetadata != null && this.animationMetadata.getFrameCount() > 0) {
         int k1 = this.animationMetadata.getFrameIndexSet().stream().max(Integer::compareTo).get() + 1;
         this.framesX = new int[k1];
         this.framesY = new int[k1];
         Arrays.fill(this.framesX, -1);
         Arrays.fill(this.framesY, -1);

         for(int i2 : this.animationMetadata.getFrameIndexSet()) {
            if (i2 >= i * j) {
               throw new RuntimeException("invalid frameindex " + i2);
            }

            int j2 = i2 / i;
            int k2 = i2 % i;
            this.framesX[i2] = k2;
            this.framesY[i2] = j2;
         }
      } else {
         List<AnimationFrame> list = Lists.newArrayList();
         int k = i * j;
         this.framesX = new int[k];
         this.framesY = new int[k];

         for(int l = 0; l < j; ++l) {
            for(int i1 = 0; i1 < i; ++i1) {
               int j1 = l * i + i1;
               this.framesX[j1] = i1;
               this.framesY[j1] = l;
               list.add(new AnimationFrame(j1, -1));
            }
         }

         int l1 = 1;
         boolean flag = false;
         if (this.animationMetadata != null) {
            l1 = this.animationMetadata.getFrameTime();
            flag = this.animationMetadata.isInterpolate();
         }

         this.animationMetadata = new AnimationMetadataSection(list, this.width, this.height, l1, flag);
      }

   }

   public void generateMipmaps(int level) {
      try {
         this.generateMipmapsUnchecked(level);
      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Generating mipmaps for frame");
         CrashReportCategory crashreportcategory = crashreport.makeCategory("Frame being iterated");
         crashreportcategory.addDetail("Frame sizes", () -> {
            StringBuilder stringbuilder = new StringBuilder();

            for(NativeImage nativeimage : this.frames) {
               if (stringbuilder.length() > 0) {
                  stringbuilder.append(", ");
               }

               stringbuilder.append(nativeimage == null ? "null" : nativeimage.getWidth() + "x" + nativeimage.getHeight());
            }

            return stringbuilder.toString();
         });
         throw new ReportedException(crashreport);
      }
   }

   public void clearFramesTextureData() {
      if (this.frames != null) {
         for(NativeImage nativeimage : this.frames) {
            if (nativeimage != null) {
               nativeimage.close();
            }
         }
      }

      this.frames = null;
      if (this.animationCache != null) {
         this.animationCache.free();
         this.animationCache = null;
      }

      this.currentAnimUpdater = null;
   }

   public boolean hasAnimationMetadata() {
      return this.animationMetadata != null && this.animationMetadata.getFrameCount() > 1;
   }

   public String toString() {
      int i = this.framesX == null ? 0 : this.framesX.length;
      return "TextureAtlasSprite{name='" + this.iconName + '\'' + ", frameCount=" + i + ", rotated=" + this.rotated + ", x=" + this.x + ", y=" + this.y + ", height=" + this.height + ", width=" + this.width + ", u0=" + this.minU + ", u1=" + this.maxU + ", v0=" + this.minV + ", v1=" + this.maxV + '}';
   }

   public boolean isPixelTransparent(int frameIndex, int pixelX, int pixelY) {
      return (this.frames[0].getPixelRGBA(pixelX + this.framesX[frameIndex] * this.width, pixelY + this.framesY[frameIndex] * this.height) >> 24 & 255) == 0;
   }

   public void uploadMipmaps() {
      this.uploadFrames(0);
   }

   @OnlyIn(Dist.CLIENT)
   protected interface IAnimCopyFunction {
      void updateAnimation(int mapWidth, int mapHeight, int mapLevel);
   }
}
