package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import javax.imageio.ImageIO;

import net.lax1dude.eaglercraft.minecraft.EaglerTextureAtlasSprite;
import net.lax1dude.eaglercraft.opengl.ImageData;
import net.lax1dude.eaglercraft.internal.ITextureGL;
import net.lax1dude.eaglercraft.opengl.EaglercraftGPU;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.StitcherException;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TextureMap extends AbstractTexture implements ITickableTextureObject, IIconRegister {
	private static final Logger logger = LogManager.getLogger();
	public static final ResourceLocation locationBlocksTexture = new ResourceLocation("textures/atlas/blocks.png");
	public static final ResourceLocation locationItemsTexture = new ResourceLocation("textures/atlas/items.png");
	private final List listAnimatedSprites = Lists.newArrayList();
	private final Map mapRegisteredSprites = Maps.newHashMap();
	private final Map mapUploadedSprites = Maps.newHashMap();

	/** 0 = terrain.png, 1 = items.png */
	private final int textureType;
	private final String basePath;
	private int field_147636_j;
	private int field_147637_k = 1;
	private final EaglerTextureAtlasSprite missingImage = new EaglerTextureAtlasSprite("missingno");
	private final EaglerTextureAtlasSprite missingImage1 = new EaglerTextureAtlasSprite("missingno");
	private static final String __OBFID = "CL_00001058";

	public TextureMap(int p_i1281_1_, String p_i1281_2_) {
		this.textureType = p_i1281_1_;
		this.basePath = p_i1281_2_;
		this.registerIcons();
	}

	private void initMissingImage() {
		int[] var1;

		if ((float) this.field_147637_k > 1.0F) {
			boolean var2 = true;
			boolean var3 = true;
			boolean var4 = true;
			this.missingImage.setIconWidth(32);
			this.missingImage.setIconHeight(32);
			var1 = new int[1024];
			System.arraycopy(TextureUtil.missingTextureData, 0, var1, 0, TextureUtil.missingTextureData.length);
		} else {
			var1 = TextureUtil.missingTextureData;
			this.missingImage.setIconWidth(16);
			this.missingImage.setIconHeight(16);
		}

		int[][] var5 = new int[this.field_147636_j + 1][];
		var5[0] = var1;
		this.missingImage.setFramesTextureData(Lists.newArrayList(new int[][][] { var5 }));
	}

	private void initMissingImage2() {
		int[] var1;

		if ((float) this.field_147637_k > 1.0F) {
			boolean var2 = true;
			boolean var3 = true;
			boolean var4 = true;
			this.missingImage1.setIconWidth(32);
			this.missingImage1.setIconHeight(32);
			var1 = new int[1024];
			System.arraycopy(TextureUtil.missingTextureData, 0, var1, 0, TextureUtil.missingTextureData.length);
		} else {
			var1 = TextureUtil.missingTextureData;
			this.missingImage1.setIconWidth(16);
			this.missingImage1.setIconHeight(16);
		}

		int[][] var5 = new int[this.field_147636_j + 1][];
		var5[0] = var1;
		this.missingImage1.setFramesTextureData(Lists.newArrayList(new int[][][] { var5 }));
	}

	public void loadTexture(IResourceManager p_110551_1_) throws IOException {
		this.initMissingImage();
		this.deleteGlTexture();
		this.registerIcons();
		this.loadTextureAtlas(p_110551_1_);
	}

	public void loadTextureAtlas(IResourceManager p_110571_1_) {
		int maxTextureSize = Minecraft.getGLMaximumTextureSize();
		Stitcher var3 = new Stitcher(maxTextureSize, maxTextureSize, true, 0, this.field_147636_j);
		this.mapUploadedSprites.clear();
		this.listAnimatedSprites.clear();
		int var4 = Integer.MAX_VALUE;
		Iterator var5 = this.mapRegisteredSprites.entrySet().iterator();
		EaglerTextureAtlasSprite var8;

		while (var5.hasNext()) {
			Entry var6 = (Entry) var5.next();
			ResourceLocation var7 = new ResourceLocation((String) var6.getKey());
			var8 = (EaglerTextureAtlasSprite) var6.getValue();
			ResourceLocation var9 = this.func_147634_a(var7, 0);

			try {
				ImageData[] var11 = null;
				TextureMetadataSection var12 = null;
				net.minecraft.client.resources.IResource chosenRes = null;
				java.util.List resourcesList = p_110571_1_.getAllResources(var9);
				if (resourcesList == null || resourcesList.isEmpty()) {
					logger.warn("Resource not found in packs: {}", var9);

					String cpPath = "/assets/" + var9.getResourceDomain() + "/" + var9.getResourcePath();
					try {
						java.io.InputStream is = net.lax1dude.eaglercraft.EagRuntime.getResourceStream(cpPath);
						if (is != null) {
							ImageData base = TextureUtil.readBufferedImage(is);
							if (base != null) {
								var11 = new ImageData[1 + this.field_147636_j];
								var11[0] = base;
								logger.debug("Loaded texture via classpath fallback (no packs): {}", cpPath);
							}
						}
					} catch (Throwable tt) {

					}
				}

				for (int i = resourcesList.size() - 1; i >= 0; --i) {
					net.minecraft.client.resources.IResource res = (net.minecraft.client.resources.IResource) resourcesList
							.get(i);
					try {
						ImageData base = TextureUtil.readBufferedImage(res.getInputStream());
						if (base == null) {

							logger.warn("Failed to load texture from pack, trying fallback: {}", var9);
							continue;
						}
						var11 = new ImageData[1 + this.field_147636_j];
						var11[0] = base;
						var12 = (TextureMetadataSection) res.getMetadata("texture");
						chosenRes = res;
						break;
					} catch (Throwable t) {
						logger.warn("Exception decoding texture from pack, trying fallback: {}", var9, t);

					}
				}

				if ((var11 == null || var11[0] == null)) {
					String cpPath = "/assets/" + var9.getResourceDomain() + "/" + var9.getResourcePath();
					try {
						java.io.InputStream is = net.lax1dude.eaglercraft.EagRuntime.getResourceStream(cpPath);
						if (is != null) {
							ImageData base = TextureUtil.readBufferedImage(is);
							if (base != null) {
								var11 = new ImageData[1 + this.field_147636_j];
								var11[0] = base;
								chosenRes = null;								logger.debug("Loaded texture via classpath fallback: {}", cpPath);
							}
						}
					} catch (Throwable tt) {

					}
				}

				if (var11 == null || var11[0] == null) {

					logger.error("Failed to load texture from all resource packs: {}", var9);
					var8 = this.missingImage;
					continue;
				}

				if (var12 != null) {
					List var13 = var12.func_148535_c();
					int var15;

					if (!var13.isEmpty()) {
						int var14 = var11[0].getWidth();
						var15 = var11[0].getHeight();

						if (MathHelper.roundUpToPowerOfTwo(var14) != var14
								|| MathHelper.roundUpToPowerOfTwo(var15) != var15) {
							throw new RuntimeException(
									"Unable to load extra miplevels, source-texture is not power of two");
						}
					}

					Iterator var35 = var13.iterator();

					while (var35.hasNext()) {
						var15 = ((Integer) var35.next()).intValue();

						if (var15 > 0 && var15 < var11.length - 1 && var11[var15] == null) {
							ResourceLocation var16 = this.func_147634_a(var7, var15);

							try {
								List mipList = p_110571_1_.getAllResources(var16);
								if (mipList != null && !mipList.isEmpty()) {
									ImageData loaded = null;
									for (int j = mipList.size() - 1; j >= 0; --j) {
										IResource mipRes = (IResource) mipList.get(j);
										try {
											loaded = TextureUtil.readBufferedImage(mipRes.getInputStream());
										} catch (Throwable tt) {

										}
										if (loaded != null)
											break;
									}
									var11[var15] = loaded;
								}

								if (var11[var15] == null) {
									String cpPathMip = "/assets/" + var16.getResourceDomain() + "/"
											+ var16.getResourcePath();
									try {
										java.io.InputStream is2 = net.lax1dude.eaglercraft.EagRuntime
												.getResourceStream(cpPathMip);
										if (is2 != null) {
											ImageData loaded2 = TextureUtil.readBufferedImage(is2);
											if (loaded2 != null) {
												var11[var15] = loaded2;
												logger.debug("Loaded mip via classpath fallback: {}", cpPathMip);
											}
										}
									} catch (Throwable ttt) {

									}
								}
							} catch (Throwable tt) {

							}
						}
					}
				}

				AnimationMetadataSection var34 = chosenRes != null
						? (AnimationMetadataSection) chosenRes.getMetadata("animation")
						: null;
				var8.loadSprite(var11, var34);
			} catch (Exception var22) {
				logger.error("Failed to load texture: " + var9, var22);
				var8 = this.missingImage;
				continue;
			}

			var4 = Math.min(var4, Math.min(var8.getIconWidth(), var8.getIconHeight()));
			var3.addSprite(var8);
		}

		int var24 = MathHelper.calculateLogBaseTwo(var4);

		if (var24 < this.field_147636_j) {
			int newLevel = var24;

			try {
				int af = net.minecraft.client.Minecraft.getMinecraft().gameSettings.anisotropicFiltering;

				if (af > 1 && newLevel < 1 && var4 >= 2) {
					logger.debug(
							"{}: would drop miplevel to 0 due to minTexel={}, but AF={} active and minTexel>=2; clamping to 1",
							new Object[] { this.basePath, Integer.valueOf(var4), Integer.valueOf(af) });
					newLevel = 1;				}
			} catch (Throwable t) {

			}
			logger.debug("{}: dropping miplevel from {} to {}, because of minTexel: {}", new Object[] { this.basePath,
					Integer.valueOf(this.field_147636_j), Integer.valueOf(newLevel), Integer.valueOf(var4) });
			this.field_147636_j = newLevel;
		}

		else {
			try {
				int af2 = net.minecraft.client.Minecraft.getMinecraft().gameSettings.anisotropicFiltering;
				if (af2 > 1 && this.field_147636_j < 1 && var4 >= 2) {
					logger.debug("{}: AF={} active and mipmap level computed as {} with minTexel>=2, forcing to 1",
							new Object[] { this.basePath, Integer.valueOf(af2), Integer.valueOf(this.field_147636_j) });
					this.field_147636_j = 1;				}
			} catch (Throwable t) {

			}
		}

		Iterator var25 = this.mapRegisteredSprites.values().iterator();

		while (var25.hasNext()) {
			final EaglerTextureAtlasSprite var27 = (EaglerTextureAtlasSprite) var25.next();

			try {
				var27.generateMipmaps(this.field_147636_j);
			} catch (Throwable var20) {
				CrashReport var29 = CrashReport.makeCrashReport(var20, "Applying mipmap");
				CrashReportCategory var31 = var29.makeCategory("Sprite being mipmapped");
				var31.addCrashSectionCallable("Sprite name", new Callable() {
					private static final String __OBFID = "CL_00001059";

					public String call() {
						return var27.getIconName();
					}
				});
				var31.addCrashSectionCallable("Sprite size", new Callable() {
					private static final String __OBFID = "CL_00001060";

					public String call() {
						return var27.getIconWidth() + " x " + var27.getIconHeight();
					}
				});
				var31.addCrashSectionCallable("Sprite frames", new Callable() {
					private static final String __OBFID = "CL_00001061";

					public String call() {
						return var27.getFrameCount() + " frames";
					}
				});
				var31.addCrashSection("Mipmap levels", Integer.valueOf(this.field_147636_j));
				throw new ReportedException(var29);
			}
		}

		this.missingImage.generateMipmaps(this.field_147636_j);
		var3.addSprite(this.missingImage);

		try {
			var3.doStitch();
		} catch (StitcherException var19) {
			throw var19;
		}

		logger.info("Created: {}x{} {}-atlas", new Object[] { Integer.valueOf(var3.getCurrentWidth()),
				Integer.valueOf(var3.getCurrentHeight()), this.basePath });
		TextureUtil.allocateTextureImpl(this.getGlTextureId(), this.field_147636_j, var3.getCurrentWidth(),
				var3.getCurrentHeight());

		HashMap var26 = Maps.newHashMap(this.mapRegisteredSprites);
		Iterator var28 = var3.getStichSlots().iterator();

		while (var28.hasNext()) {
			var8 = (EaglerTextureAtlasSprite) var28.next();
			String var30 = var8.getIconName();
			var26.remove(var30);
			this.mapUploadedSprites.put(var30, var8);

			try {
				TextureUtil.uploadTextureMipmap(var8.getFrameTextureData(0), var8.getIconWidth(), var8.getIconHeight(),
						var8.getOriginX(), var8.getOriginY(), false, false);
			} catch (Throwable var18) {
				CrashReport var32 = CrashReport.makeCrashReport(var18, "Stitching texture atlas");
				CrashReportCategory var33 = var32.makeCategory("Texture being stitched together");
				var33.addCrashSection("Atlas path", this.basePath);
				var33.addCrashSection("Sprite", var8);
				throw new ReportedException(var32);
			}

			if (var8.hasAnimationMetadata()) {
				this.listAnimatedSprites.add(var8);
			} else {
				var8.clearFramesTextureData();
			}
		}

		var28 = var26.values().iterator();

		while (var28.hasNext()) {
			var8 = (EaglerTextureAtlasSprite) var28.next();
			var8.copyFrom(this.missingImage);
		}

	}

	private ResourceLocation func_147634_a(ResourceLocation p_147634_1_, int p_147634_2_) {
		return p_147634_2_ == 0
				? new ResourceLocation(p_147634_1_.getResourceDomain(),
						String.format("%s/%s%s", new Object[] { this.basePath, p_147634_1_.getResourcePath(), ".png" }))
				: new ResourceLocation(p_147634_1_.getResourceDomain(),
						String.format("%s/mipmaps/%s.%d%s", new Object[] { this.basePath, p_147634_1_.getResourcePath(),
								Integer.valueOf(p_147634_2_), ".png" }));
	}

	private void registerIcons() {
		this.mapRegisteredSprites.clear();
		Iterator var1;

		if (this.textureType == 0) {
			var1 = Block.blockRegistry.iterator();

			while (var1.hasNext()) {
				Block var2 = (Block) var1.next();

				if (var2.getMaterial() != Material.air) {
					var2.registerBlockIcons(this);
				}
			}

			if (Minecraft.getMinecraft().renderGlobal != null) {
				Minecraft.getMinecraft().renderGlobal.registerDestroyBlockIcons(this);
			}
			RenderManager.instance.updateIcons(this);
		}

		var1 = Item.itemRegistry.iterator();

		while (var1.hasNext()) {
			Item var3 = (Item) var1.next();

			if (var3 != null && var3.getSpriteNumber() == this.textureType) {
				var3.registerIcons(this);
			}
		}
	}

	public EaglerTextureAtlasSprite getAtlasSprite(String p_110572_1_) {
		EaglerTextureAtlasSprite var2 = (EaglerTextureAtlasSprite) this.mapUploadedSprites.get(p_110572_1_);

		if (var2 == null) {
			var2 = this.missingImage;
		}

		return var2;
	}

	public void updateAnimations() {
		TextureUtil.bindTexture(this.getGlTextureId());
		Iterator var1 = this.listAnimatedSprites.iterator();

		while (var1.hasNext()) {
			EaglerTextureAtlasSprite var2 = (EaglerTextureAtlasSprite) var1.next();
			var2.updateAnimation();
		}
	}

	public IIcon registerIcon(String p_94245_1_) {
		if (p_94245_1_ == null) {
			throw new IllegalArgumentException("Name cannot be null!");
		} else if (p_94245_1_.indexOf(47) == -1 && p_94245_1_.indexOf(92) == -1) {
			Object var2 = (EaglerTextureAtlasSprite) this.mapRegisteredSprites.get(p_94245_1_);

			if (var2 == null) {
				if (this.textureType == 1) {
					if ("clock".equals(p_94245_1_)) {
						var2 = new TextureClock(p_94245_1_);
					} else if ("compass".equals(p_94245_1_)) {
						var2 = new TextureCompass(p_94245_1_);
					} else {
						var2 = new EaglerTextureAtlasSprite(p_94245_1_);
					}
				} else {
					var2 = new EaglerTextureAtlasSprite(p_94245_1_);
				}

				this.mapRegisteredSprites.put(p_94245_1_, var2);
			}

			return (IIcon) var2;
		} else {
			throw new IllegalArgumentException("Name cannot contain slashes!");
		}
	}

	public int getTextureType() {
		return this.textureType;
	}

	public void tick() {
		this.updateAnimations();
	}

	public void func_147633_a(int p_147633_1_) {
		this.field_147636_j = p_147633_1_;
	}

	public void func_147632_b(int p_147632_1_) {
		this.field_147637_k = p_147632_1_;
	}

	public EaglerTextureAtlasSprite getMissingSprite() {
		return missingImage1;
	}
}
