package net.minecraft.client.renderer.texture;

import static net.lax1dude.eaglercraft.v1_8.internal.PlatformOpenGL.*;
import static net.lax1dude.eaglercraft.v1_8.opengl.ExtGLEnums.*;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.lax1dude.eaglercraft.v1_8.internal.IFramebufferGL;
import net.lax1dude.eaglercraft.v1_8.opengl.EaglercraftGPU;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.StitcherException;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class TextureMap extends AbstractTexture implements ITickableTextureObject {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final ResourceLocation LOCATION_BLOCKS_TEXTURE = new ResourceLocation("textures/atlas/blocks.png");
   private final List<TextureAtlasSprite> listAnimatedSprites = Lists.newArrayList();
   private final Set<ResourceLocation> sprites = Sets.newHashSet();
   private final Map<ResourceLocation, TextureAtlasSprite> mapUploadedSprites = Maps.newHashMap();
   private final String basePath;
   private int mipmapLevels;
   private final TextureAtlasSprite missingImage = MissingTextureSprite.getSprite();
   private int atlasWidth;
   private int atlasHeight;
   private IFramebufferGL[] copyColorFramebuffer = null;

   public TextureMap(String basePathIn) {
      this.basePath = basePathIn;
   }

   public void loadTexture(IResourceManager manager) throws IOException {
   }

   public void stitch(IResourceManager manager, Iterable<ResourceLocation> locations) {
      this.sprites.clear();
      locations.forEach((p_195423_2_) -> {
         this.registerSprite(manager, p_195423_2_);
      });
      this.stitch(manager);
   }

   public void stitch(IResourceManager manager) {
      int i = Minecraft.getGLMaximumTextureSize();
      Stitcher stitcher = new Stitcher(i, i, 0, this.mipmapLevels);
      this.clear();
      int j = Integer.MAX_VALUE;
      int k = 1 << this.mipmapLevels;

      for(ResourceLocation resourcelocation : this.sprites) {
         if (!this.missingImage.getName().equals(resourcelocation)) {
            ResourceLocation resourcelocation1 = this.getSpritePath(resourcelocation);

            TextureAtlasSprite textureatlassprite;
            try (IResource iresource = manager.getResource(resourcelocation1)) {
               PngSizeInfo pngsizeinfo = new PngSizeInfo(iresource);
               AnimationMetadataSection animationmetadatasection = iresource.getMetadata(AnimationMetadataSection.SERIALIZER);
               textureatlassprite = new TextureAtlasSprite(resourcelocation, pngsizeinfo, animationmetadatasection);
            } catch (RuntimeException runtimeexception) {
               LOGGER.error("Unable to parse metadata from {} : {}", resourcelocation1, runtimeexception);
               continue;
            } catch (IOException ioexception) {
               LOGGER.error("Using missing texture, unable to load {} : {}", resourcelocation1, ioexception);
               continue;
            }

            j = Math.min(j, Math.min(textureatlassprite.getWidth(), textureatlassprite.getHeight()));
            int j1 = Math.min(Integer.lowestOneBit(textureatlassprite.getWidth()), Integer.lowestOneBit(textureatlassprite.getHeight()));
            if (j1 < k) {
               LOGGER.warn("Texture {} with size {}x{} limits mip level from {} to {}", resourcelocation1, textureatlassprite.getWidth(), textureatlassprite.getHeight(), MathHelper.log2(k), MathHelper.log2(j1));
               k = j1;
            }

            stitcher.addSprite(textureatlassprite);
         }
      }

      int l = Math.min(j, k);
      int i1 = MathHelper.log2(l);
      if (i1 < this.mipmapLevels) {
         LOGGER.warn("{}: dropping miplevel from {} to {}, because of minimum power of two: {}", this.basePath, this.mipmapLevels, i1, l);
         this.mipmapLevels = i1;
      }

      this.missingImage.generateMipmaps(this.mipmapLevels);
      stitcher.addSprite(this.missingImage);

      try {
         stitcher.doStitch();
      } catch (StitcherException stitcherexception) {
         throw stitcherexception;
      }

      this.atlasWidth = stitcher.getCurrentWidth();
      this.atlasHeight = stitcher.getCurrentHeight();
      LOGGER.info("Created: {}x{} {}-atlas", this.atlasWidth, this.atlasHeight, this.basePath);
      TextureUtil.allocateTextureImpl(this.getGlTextureId(), this.mipmapLevels, this.atlasWidth, this.atlasHeight);
      this.copyColorFramebuffer = new IFramebufferGL[this.mipmapLevels + 1];

      for(int j1 = 0; j1 < this.copyColorFramebuffer.length; ++j1) {
         this.copyColorFramebuffer[j1] = _wglCreateFramebuffer();
         _wglBindFramebuffer(_GL_FRAMEBUFFER, this.copyColorFramebuffer[j1]);
         _wglFramebufferTexture2D(_GL_FRAMEBUFFER, _GL_COLOR_ATTACHMENT0, 3553, EaglercraftGPU.getNativeTexture(this.getGlTextureId()), j1);
      }

      _wglBindFramebuffer(_GL_FRAMEBUFFER, null);

      for(TextureAtlasSprite textureatlassprite1 : stitcher.getStichSlots()) {
         if (textureatlassprite1 == this.missingImage || this.loadSprite(manager, textureatlassprite1)) {
            this.mapUploadedSprites.put(textureatlassprite1.getName(), textureatlassprite1);

            try {
               textureatlassprite1.uploadMipmaps();
            } catch (Throwable throwable) {
               CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Stitching texture atlas");
               CrashReportCategory crashreportcategory = crashreport.makeCategory("Texture being stitched together");
               crashreportcategory.addDetail("Atlas path", this.basePath);
               crashreportcategory.addDetail("Sprite", textureatlassprite1);
               throw new ReportedException(crashreport);
            }

            if (textureatlassprite1.hasAnimationMetadata()) {
               this.listAnimatedSprites.add(textureatlassprite1);
            }
         }
      }

      for(int k1 = 0, l1 = this.listAnimatedSprites.size(); k1 < l1; ++k1) {
         this.listAnimatedSprites.get(k1).bakeAnimationCache();
      }

   }

   private boolean loadSprite(IResourceManager manager, TextureAtlasSprite sprite) {
      ResourceLocation resourcelocation = this.getSpritePath(sprite.getName());
      IResource iresource = null;

      label62: {
         boolean flag;
         try {
            iresource = manager.getResource(resourcelocation);
            sprite.loadSpriteFrames(iresource, this.mipmapLevels + 1);
            break label62;
         } catch (RuntimeException runtimeexception) {
            LOGGER.error("Unable to parse metadata from {}", resourcelocation, runtimeexception);
            flag = false;
         } catch (IOException ioexception) {
            LOGGER.error("Using missing texture, unable to load {}", resourcelocation, ioexception);
            flag = false;
            return flag;
         } finally {
            IOUtils.closeQuietly((Closeable)iresource);
         }

         return flag;
      }

      try {
         sprite.generateMipmaps(this.mipmapLevels);
         return true;
      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Applying mipmap");
         CrashReportCategory crashreportcategory = crashreport.makeCategory("Sprite being mipmapped");
         crashreportcategory.addDetail("Sprite name", () -> {
            return sprite.getName().toString();
         });
         crashreportcategory.addDetail("Sprite size", () -> {
            return sprite.getWidth() + " x " + sprite.getHeight();
         });
         crashreportcategory.addDetail("Sprite frames", () -> {
            return sprite.getFrameCount() + " frames";
         });
         crashreportcategory.addDetail("Mipmap levels", this.mipmapLevels);
         throw new ReportedException(crashreport);
      }
   }

   private ResourceLocation getSpritePath(ResourceLocation location) {
      return new ResourceLocation(location.getNamespace(), String.format("%s/%s%s", this.basePath, location.getPath(), ".png"));
   }

   public TextureAtlasSprite getAtlasSprite(String iconName) {
      return this.getSprite(new ResourceLocation(iconName));
   }

   public void updateAnimations() {
      if (this.copyColorFramebuffer == null) {
         return;
      }

      for(int i = 0, j = this.listAnimatedSprites.size(); i < j; ++i) {
         this.listAnimatedSprites.get(i).updateAnimation();
      }

      for(int k = 0; k < this.copyColorFramebuffer.length; ++k) {
         int l = this.atlasWidth >> k;
         int i1 = this.atlasHeight >> k;
         _wglBindFramebuffer(_GL_FRAMEBUFFER, this.copyColorFramebuffer[k]);
         GlStateManager.viewport(0, 0, l, i1);

         for(int j1 = 0, k1 = this.listAnimatedSprites.size(); j1 < k1; ++j1) {
            this.listAnimatedSprites.get(j1).copyAnimationFrame(l, i1, k);
         }
      }

      _wglBindFramebuffer(_GL_FRAMEBUFFER, null);
   }

   public void registerSprite(IResourceManager manager, ResourceLocation location) {
      if (location == null) {
         throw new IllegalArgumentException("Location cannot be null!");
      } else {
         this.sprites.add(location);
      }
   }

   public void tick() {
      this.updateAnimations();
   }

   public void setMipmapLevels(int mipmapLevelsIn) {
      this.mipmapLevels = mipmapLevelsIn;
   }

   public int getAtlasWidth() {
      return this.atlasWidth;
   }

   public int getAtlasHeight() {
      return this.atlasHeight;
   }

   public TextureAtlasSprite getSprite(ResourceLocation location) {
      TextureAtlasSprite textureatlassprite = this.mapUploadedSprites.get(location);
      return textureatlassprite == null ? this.missingImage : textureatlassprite;
   }

   public void clear() {
      for(TextureAtlasSprite textureatlassprite : this.mapUploadedSprites.values()) {
         textureatlassprite.clearFramesTextureData();
      }

      this.mapUploadedSprites.clear();
      this.listAnimatedSprites.clear();
      if (this.copyColorFramebuffer != null) {
         for(int i = 0; i < this.copyColorFramebuffer.length; ++i) {
            _wglDeleteFramebuffer(this.copyColorFramebuffer[i]);
         }

         this.copyColorFramebuffer = null;
      }

   }
}
