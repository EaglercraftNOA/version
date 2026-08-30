package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;

import net.lax1dude.eaglercraft.HString;
import net.lax1dude.eaglercraft.internal.IFramebufferGL;
import net.lax1dude.eaglercraft.internal.PlatformOpenGL;
import net.lax1dude.eaglercraft.opengl.EaglercraftGPU;
import net.lax1dude.eaglercraft.opengl.GlStateManager;
import net.lax1dude.eaglercraft.opengl.ImageData;
import net.lax1dude.eaglercraft.opengl.RealOpenGLEnums;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.StitcherException;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ICrashReportDetail;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.optifine.CustomItems;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TextureMap extends AbstractTexture implements ITickableTextureObject {
	private static final Logger LOGGER = LogManager.getLogger();
	public static final ResourceLocation LOCATION_MISSING_TEXTURE = new ResourceLocation("missingno");
	public static final ResourceLocation LOCATION_BLOCKS_TEXTURE = new ResourceLocation("textures/atlas/blocks.png");
	private final List<TextureAtlasSprite> listAnimatedSprites;
	private final Map<String, TextureAtlasSprite> mapRegisteredSprites;
	private final Map<String, TextureAtlasSprite> mapUploadedSprites;
	private final String basePath;
	private final ITextureMapPopulator iconCreator;
	private int mipmapLevels;
	private final TextureAtlasSprite missingImage;
	private int width;
	private int height;

	public static final int _GL_FRAMEBUFFER = 0x8D40;
	public static final int _GL_COLOR_ATTACHMENT0 = 0x8CE0;

	private IFramebufferGL[] copyColorFramebuffer = null;
	private IFramebufferGL[] copyMaterialFramebuffer = null;

	public TextureMap(String basePathIn) {
		this(basePathIn, (ITextureMapPopulator) null);
	}

	public TextureMap(String basePathIn, @Nullable ITextureMapPopulator iconCreatorIn) {
		this.listAnimatedSprites = Lists.<TextureAtlasSprite>newArrayList();
		this.mapRegisteredSprites = Maps.<String, TextureAtlasSprite>newHashMap();
		this.mapUploadedSprites = Maps.<String, TextureAtlasSprite>newHashMap();
		this.missingImage = new TextureAtlasSprite("missingno");
		this.basePath = basePathIn;
		this.iconCreator = iconCreatorIn;
	}

	private void initMissingImage() {
		int[] aint = TextureUtil.MISSING_TEXTURE_DATA;
		this.missingImage.setIconWidth(16);
		this.missingImage.setIconHeight(16);
		int[][] aint1 = new int[this.mipmapLevels + 1][];
		aint1[0] = aint;
		this.missingImage.setFramesTextureData(Lists.<int[][]>newArrayList(aint1));
	}

	public void loadTexture(IResourceManager resourceManager) throws IOException {
		if (this.iconCreator != null) {
			this.loadSprites(resourceManager, this.iconCreator);
		}
	}

	public void loadSprites(IResourceManager resourceManager, ITextureMapPopulator iconCreatorIn) {
		destroyAnimationCaches();
		this.mapRegisteredSprites.clear();
		iconCreatorIn.registerSprites(this);
		this.initMissingImage();
		this.deleteGlTexture();
		this.loadTextureAtlas(resourceManager);
	}

	public void loadTextureAtlas(IResourceManager resourceManager) {
		CustomItems.updateIcons(this);
		int i = Minecraft.getGLMaximumTextureSize();
		Stitcher stitcher = new Stitcher(i, i, 0, this.mipmapLevels);
		this.mapUploadedSprites.clear();
		this.listAnimatedSprites.clear();
		int j = Integer.MAX_VALUE;
		int k = 1 << this.mipmapLevels;

		if (copyColorFramebuffer != null) {
			for (int l = 0; l < copyColorFramebuffer.length; ++l) {
				PlatformOpenGL._wglDeleteFramebuffer(copyColorFramebuffer[l]);
			}
			copyColorFramebuffer = null;
		}

		if (copyMaterialFramebuffer != null) {
			for (int l = 0; l < copyMaterialFramebuffer.length; ++l) {
				PlatformOpenGL._wglDeleteFramebuffer(copyMaterialFramebuffer[l]);
			}
			copyMaterialFramebuffer = null;
		}

		for (Entry<String, TextureAtlasSprite> entry : this.mapRegisteredSprites.entrySet()) {
			TextureAtlasSprite textureatlassprite = entry.getValue();
			ResourceLocation resourcelocation = this.getResourceLocation(textureatlassprite);

			try {
				IResource iresource = resourceManager.getResource(resourcelocation);
				ImageData[] abufferedimage = new ImageData[1 + this.mipmapLevels];
				abufferedimage[0] = TextureUtil.readBufferedImage(iresource.getInputStream());
				AnimationMetadataSection animationmetadatasection = (AnimationMetadataSection) iresource
						.getMetadata("animation");
				textureatlassprite.loadSprite(abufferedimage, animationmetadatasection);
			} catch (RuntimeException runtimeexception) {
				LOGGER.error("Unable to parse metadata from {}", resourcelocation, runtimeexception);
				continue;
			} catch (IOException ioexception) {
				LOGGER.error("Using missing texture, unable to load {}", resourcelocation);
				LOGGER.error(ioexception);
				continue;
			}

			j = Math.min(j, Math.min(textureatlassprite.getIconWidth(), textureatlassprite.getIconHeight()));
			int j1 = Math.min(Integer.lowestOneBit(textureatlassprite.getIconWidth()),
					Integer.lowestOneBit(textureatlassprite.getIconHeight()));

			if (j1 < k) {
				LOGGER.warn("Texture {} with size {}x{} limits mip level from {} to {}", resourcelocation,
						Integer.valueOf(textureatlassprite.getIconWidth()),
						Integer.valueOf(textureatlassprite.getIconHeight()), Integer.valueOf(MathHelper.log2(k)),
						Integer.valueOf(MathHelper.log2(j1)));
				k = j1;
			}

			stitcher.addSprite(textureatlassprite);
		}

		int l = Math.min(j, k);
		int i1 = MathHelper.log2(l);

		if (i1 < this.mipmapLevels) {
			LOGGER.warn("{}: dropping miplevel from {} to {}, because of minimum power of two: {}", this.basePath,
					Integer.valueOf(this.mipmapLevels), Integer.valueOf(i1), Integer.valueOf(l));
			this.mipmapLevels = i1;
		}

		for (final TextureAtlasSprite textureatlassprite1 : this.mapRegisteredSprites.values()) {
			try {
				textureatlassprite1.generateMipmaps(this.mipmapLevels);
			} catch (Throwable throwable1) {
				CrashReport crashreport = CrashReport.makeCrashReport(throwable1, "Applying mipmap");
				CrashReportCategory crashreportcategory = crashreport.makeCategory("Sprite being mipmapped");
				crashreportcategory.setDetail("Sprite name", new ICrashReportDetail<String>() {
					public String call() throws Exception {
						return textureatlassprite1.getIconName();
					}
				});
				crashreportcategory.setDetail("Sprite size", new ICrashReportDetail<String>() {
					public String call() throws Exception {
						return textureatlassprite1.getIconWidth() + " x " + textureatlassprite1.getIconHeight();
					}
				});
				crashreportcategory.setDetail("Sprite frames", new ICrashReportDetail<String>() {
					public String call() throws Exception {
						return textureatlassprite1.getFrameCount() + " frames";
					}
				});
				crashreportcategory.addCrashSection("Mipmap levels", Integer.valueOf(this.mipmapLevels));
				throw new ReportedException(crashreport);
			}
		}

		this.missingImage.generateMipmaps(this.mipmapLevels);
		stitcher.addSprite(this.missingImage);

		try {
			stitcher.doStitch();
		} catch (StitcherException stitcherexception) {
			throw stitcherexception;
		}

		LOGGER.info("Created: {}x{} {}-atlas", Integer.valueOf(stitcher.getCurrentWidth()),
				Integer.valueOf(stitcher.getCurrentHeight()), this.basePath);
		regenerateIfNotAllocated();
		TextureUtil.allocateTextureImpl(this.getGlTextureId(), this.mipmapLevels, stitcher.getCurrentWidth(),
				stitcher.getCurrentHeight());

		TextureUtil.bindTexture(this.glTextureId);

		copyColorFramebuffer = new IFramebufferGL[this.mipmapLevels + 1];
		for (int l1 = 0; l1 < copyColorFramebuffer.length; ++l1) {
			copyColorFramebuffer[l1] = PlatformOpenGL._wglCreateFramebuffer();
			PlatformOpenGL._wglBindFramebuffer(_GL_FRAMEBUFFER, copyColorFramebuffer[l1]);
			PlatformOpenGL._wglFramebufferTexture2D(_GL_FRAMEBUFFER, _GL_COLOR_ATTACHMENT0,
					RealOpenGLEnums.GL_TEXTURE_2D, EaglercraftGPU.getNativeTexture(this.glTextureId), l1);
		}

		PlatformOpenGL._wglBindFramebuffer(_GL_FRAMEBUFFER, null);

		Map<String, TextureAtlasSprite> map = Maps.<String, TextureAtlasSprite>newHashMap(this.mapRegisteredSprites);

		width = stitcher.getCurrentWidth();
		height = stitcher.getCurrentHeight();

		List<TextureAtlasSprite> spriteList = stitcher.getStichSlots();
		for (int l1 = 0; l1 < spriteList.size(); ++l1) {
			TextureAtlasSprite textureatlassprite2 = spriteList.get(l1);
			String s = textureatlassprite2.getIconName();
			map.remove(s);
			this.mapUploadedSprites.put(s, textureatlassprite2);

			try {
				TextureUtil.bindTexture(this.glTextureId);
				TextureUtil.uploadTextureMipmap(textureatlassprite2.getFrameTextureData(0),
						textureatlassprite2.getIconWidth(), textureatlassprite2.getIconHeight(),
						textureatlassprite2.getOriginX(), textureatlassprite2.getOriginY(), false, false);
			} catch (Throwable throwable) {
				CrashReport crashreport1 = CrashReport.makeCrashReport(throwable, "Stitching texture atlas");
				CrashReportCategory crashreportcategory1 = crashreport1.makeCategory("Texture being stitched together");
				crashreportcategory1.addCrashSection("Atlas path", this.basePath);
				crashreportcategory1.addCrashSection("Sprite", textureatlassprite2);
				throw new ReportedException(crashreport1);
			}

			if (textureatlassprite2.hasAnimationMetadata()) {
				this.listAnimatedSprites.add(textureatlassprite2);
			}
		}

		for (TextureAtlasSprite textureatlassprite2 : map.values()) {
			textureatlassprite2.copyFrom(this.missingImage);
		}

		PlatformOpenGL._wglBindFramebuffer(_GL_FRAMEBUFFER, null);
	}

	public ResourceLocation getResourceLocation(TextureAtlasSprite p_184396_1_) {
        ResourceLocation resourcelocation1 = new ResourceLocation(p_184396_1_.getIconName());
        return this.completeResourceLocation(resourcelocation1);
    }

	public TextureAtlasSprite getAtlasSprite(String iconName) {
		TextureAtlasSprite textureatlassprite = this.mapUploadedSprites.get(iconName);

		if (textureatlassprite == null) {
			textureatlassprite = this.missingImage;
		}

		return textureatlassprite;
	}

	public void updateAnimations() {
		for (int j = 0, l = this.listAnimatedSprites.size(); j < l; ++j) {
			this.listAnimatedSprites.get(j).updateAnimation();
		}

		for (int i = 0; i < copyColorFramebuffer.length; ++i) {
			int w = width >> i;
			int h = height >> i;
			PlatformOpenGL._wglBindFramebuffer(_GL_FRAMEBUFFER, copyColorFramebuffer[i]);
			GlStateManager.viewport(0, 0, w, h);
			for (int j = 0, l = this.listAnimatedSprites.size(); j < l; ++j) {
				this.listAnimatedSprites.get(j).copyAnimationFrame(w, h, i);
			}
		}

		PlatformOpenGL._wglBindFramebuffer(_GL_FRAMEBUFFER, null);
	}

	private void destroyAnimationCaches() {
		for (int i = 0, l = this.listAnimatedSprites.size(); i < l; ++i) {
			this.listAnimatedSprites.get(i).clearFramesTextureData();
		}
	}

	public TextureAtlasSprite registerSprite(ResourceLocation location) {
		if (location == null) {
			throw new IllegalArgumentException("Location cannot be null!");
		} else {
			TextureAtlasSprite textureatlassprite = this.mapRegisteredSprites.get(location);

			if (textureatlassprite == null) {
				textureatlassprite = TextureAtlasSprite.makeAtlasSprite(location);
				this.mapRegisteredSprites.put(location.toString(), textureatlassprite);
			}

			return textureatlassprite;
		}
	}

	public void tick() {
		this.updateAnimations();
	}

	public void setMipmapLevels(int mipmapLevelsIn) {
		this.mipmapLevels = mipmapLevelsIn;
	}

	public TextureAtlasSprite getMissingSprite() {
		return this.missingImage;
	}

	public void deleteGlTexture() {
		super.deleteGlTexture();
		if (copyColorFramebuffer != null) {
			for (int i = 0; i < copyColorFramebuffer.length; ++i) {
				PlatformOpenGL._wglDeleteFramebuffer(copyColorFramebuffer[i]);
			}
			copyColorFramebuffer = null;
		}
		if (copyMaterialFramebuffer != null) {
			for (int i = 0; i < copyMaterialFramebuffer.length; ++i) {
				PlatformOpenGL._wglDeleteFramebuffer(copyMaterialFramebuffer[i]);
			}
			copyMaterialFramebuffer = null;
		}
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	private boolean isAbsoluteLocation(ResourceLocation p_isAbsoluteLocation_1_) {
        String s1 = p_isAbsoluteLocation_1_.getResourcePath();
        return this.isAbsoluteLocationPath(s1);
    }

    private boolean isAbsoluteLocationPath(String p_isAbsoluteLocationPath_1_) {
        String s1 = p_isAbsoluteLocationPath_1_.toLowerCase();
        return s1.startsWith("mcpatcher/") || s1.startsWith("optifine/");
    }
    
    public ResourceLocation completeResourceLocation(ResourceLocation p_completeResourceLocation_1_) {
        return this.isAbsoluteLocation(p_completeResourceLocation_1_) ? new ResourceLocation(p_completeResourceLocation_1_.getResourceDomain(), p_completeResourceLocation_1_.getResourcePath() + ".png") : new ResourceLocation(p_completeResourceLocation_1_.getResourceDomain(), HString.format("%s/%s%s", new Object[] { this.basePath, p_completeResourceLocation_1_.getResourcePath(), ".png" }));
    }
}