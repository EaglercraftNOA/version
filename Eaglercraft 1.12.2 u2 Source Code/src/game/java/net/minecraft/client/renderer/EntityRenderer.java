package net.minecraft.client.renderer;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.util.List;

import net.lax1dude.eaglercraft.Display;
import net.lax1dude.eaglercraft.EagRuntime;
import net.lax1dude.eaglercraft.EaglercraftRandom;
import net.lax1dude.eaglercraft.HString;
import net.lax1dude.eaglercraft.Mouse;
import net.lax1dude.eaglercraft.internal.buffer.FloatBuffer;
import net.lax1dude.eaglercraft.opengl.WorldRenderer;
import net.lax1dude.eaglercraft.opengl.EaglercraftGPU;
import net.lax1dude.eaglercraft.opengl.GameOverlayFramebuffer;
import net.lax1dude.eaglercraft.opengl.GlStateManager;
import net.lax1dude.eaglercraft.opengl.RealOpenGLEnums;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.MapItemRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ICrashReportDetail;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MouseFilter;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.optifine.TextureUtils;
import net.peyton.eagler.minecraft.texture.DynamicTextureLightmap;

public class EntityRenderer implements IResourceManagerReloadListener {
	private static final ResourceLocation RAIN_TEXTURES = new ResourceLocation("textures/environment/rain.png");
	private static final ResourceLocation SNOW_TEXTURES = new ResourceLocation("textures/environment/snow.png");
	public static boolean anaglyphEnable;

	/** Anaglyph field (0=R, 1=GB) */
	public static int anaglyphField;

	/** A reference to the Minecraft object. */
	private final Minecraft mc;
	private final EaglercraftRandom random = new EaglercraftRandom();
	private float farPlaneDistance;
	public final ItemRenderer itemRenderer;
	private final MapItemRenderer theMapItemRenderer;

	/** Entity renderer update count */
	private int rendererUpdateCount;

	/** Pointed entity */
	private Entity pointedEntity;
	private final MouseFilter mouseFilterXAxis = new MouseFilter();
	private final MouseFilter mouseFilterYAxis = new MouseFilter();

	/** Previous third person distance */
	private float thirdPersonDistancePrev = 4.0F;

	/** Smooth cam yaw */
	private float smoothCamYaw;

	/** Smooth cam pitch */
	private float smoothCamPitch;

	/** Smooth cam filter X */
	private float smoothCamFilterX;

	/** Smooth cam filter Y */
	private float smoothCamFilterY;

	/** Smooth cam partial ticks */
	private float smoothCamPartialTicks;

	/** FOV modifier hand */
	private float fovModifierHand;

	/** FOV modifier hand prev */
	private float fovModifierHandPrev;
	private float bossColorModifier;
	private float bossColorModifierPrev;

	/** Cloud fog mode */
	private boolean cloudFog;
	private boolean renderHand = true;
	private boolean drawBlockOutline = true;
	private long timeWorldIcon;

	/** Previous frame time in milliseconds */
	private long prevFrameTime = Minecraft.getSystemTime();

	private final DynamicTextureLightmap lightmapTexture;
	private final int[] lightmapColors;
	private final ResourceLocation locationLightMap;
	private boolean lightmapUpdateNeeded;

	/** Torch flicker X */
	private float torchFlickerX;
	private float torchFlickerDX;

	/** Rain sound counter */
	private int rainSoundCounter;
	private final float[] rainXCoords = new float[1024];
	private final float[] rainYCoords = new float[1024];

	/** Fog color buffer */
	private final FloatBuffer fogColorBuffer = GLAllocation.createDirectFloatBuffer(16);
	private float fogColorRed;
	private float fogColorGreen;
	private float fogColorBlue;

	/** Fog color 2 */
	private float fogColor2;

	/** Fog color 1 */
	private float fogColor1;
	private int debugViewDirection;
	private boolean debugView;
	private double cameraZoom = 1.0D;
	private double cameraYaw;
	private double cameraPitch;
	private ItemStack field_190566_ab;
	private int field_190567_ac;
	private float field_190568_ad;
	private float field_190569_ae;
	private int frameCount;
	private GameOverlayFramebuffer overlayFramebuffer;
	public float currentProjMatrixFOV = 0.0f;
	public boolean fogStandard = false;

	public EntityRenderer(Minecraft mcIn, IResourceManager resourceManagerIn) {
		this.mc = mcIn;
		this.itemRenderer = mcIn.getItemRenderer();
		this.theMapItemRenderer = new MapItemRenderer(mcIn.getTextureManager());
		this.lightmapTexture = new DynamicTextureLightmap(16, 16);
		this.locationLightMap = mcIn.getTextureManager().getDynamicTextureLocation("lightMap", this.lightmapTexture);
		this.lightmapColors = this.lightmapTexture.getTextureData();
		this.overlayFramebuffer = new GameOverlayFramebuffer();
		
		for (int i = 0; i < 32; ++i) {
			for (int j = 0; j < 32; ++j) {
				float f = (float) (j - 16);
				float f1 = (float) (i - 16);
				float f2 = MathHelper.sqrt(f * f + f1 * f1);
				this.rainXCoords[i << 5 | j] = -f1 / f2;
				this.rainYCoords[i << 5 | j] = f / f2;
			}
		}
	}

	public void stopUseShader() {
	}

	public void switchUseShader() {
	}

	/**
	 * What shader to use when spectating this entity
	 */
	public void loadEntityShader(@Nullable Entity entityIn) {
	}

	public void onResourceManagerReload(IResourceManager resourceManager) {
	}

	/**
	 * Updates the entity renderer
	 */
	public void updateRenderer() {
		this.updateFovModifierHand();
		this.updateTorchFlicker();
		this.fogColor2 = this.fogColor1;
		this.thirdPersonDistancePrev = 4.0F;

		if (this.mc.gameSettings.smoothCamera) {
			float f = this.mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
			float f1 = f * f * f * 8.0F;
			this.smoothCamFilterX = this.mouseFilterXAxis.smooth(this.smoothCamYaw, 0.05F * f1);
			this.smoothCamFilterY = this.mouseFilterYAxis.smooth(this.smoothCamPitch, 0.05F * f1);
			this.smoothCamPartialTicks = 0.0F;
			this.smoothCamYaw = 0.0F;
			this.smoothCamPitch = 0.0F;
		} else {
			this.smoothCamFilterX = 0.0F;
			this.smoothCamFilterY = 0.0F;
			this.mouseFilterXAxis.reset();
			this.mouseFilterYAxis.reset();
		}

		if (this.mc.getRenderViewEntity() == null) {
			this.mc.setRenderViewEntity(this.mc.player);
		}

		float f3 = this.mc.world.getLightBrightness(new BlockPos(this.mc.getRenderViewEntity()));
		float f4 = (float) this.mc.gameSettings.renderDistanceChunks / 32.0F;
		float f2 = f3 * (1.0F - f4) + f4;
		this.fogColor1 += (f2 - this.fogColor1) * 0.1F;
		++this.rendererUpdateCount;
		this.itemRenderer.updateEquippedItem();
		this.addRainParticles();
		this.bossColorModifierPrev = this.bossColorModifier;

		if (this.mc.ingameGUI.getBossOverlay().shouldDarkenSky()) {
			this.bossColorModifier += 0.05F;

			if (this.bossColorModifier > 1.0F) {
				this.bossColorModifier = 1.0F;
			}
		} else if (this.bossColorModifier > 0.0F) {
			this.bossColorModifier -= 0.0125F;
		}

		if (this.field_190567_ac > 0) {
			--this.field_190567_ac;

			if (this.field_190567_ac == 0) {
				this.field_190566_ab = null;
			}
		}
	}

	public void updateShaderGroupSize(int width, int height) {
	}

	/**
	 * Gets the block or object that is being moused over.
	 */
	public void getMouseOver(float partialTicks) {
		Entity entity = this.mc.getRenderViewEntity();

		if (entity != null) {
			if (this.mc.world != null) {
				this.mc.pointedEntity = null;
				double d0 = (double) this.mc.playerController.getBlockReachDistance();
				this.mc.objectMouseOver = entity.rayTrace(d0, partialTicks);
				Vec3d vec3d = entity.getPositionEyes(partialTicks);
				boolean flag = false;
				double d1 = d0;

				if (this.mc.playerController.extendedReach()) {
					d1 = 6.0D;
					d0 = d1;
				} else {
					if (d0 > 3.0D) {
						flag = true;
					}
				}

				if (this.mc.objectMouseOver != null) {
					d1 = this.mc.objectMouseOver.hitVec.distanceTo(vec3d);
				}

				Vec3d vec3d1 = entity.getLook(1.0F);
				Vec3d vec3d2 = vec3d.addVector(vec3d1.xCoord * d0, vec3d1.yCoord * d0, vec3d1.zCoord * d0);
				this.pointedEntity = null;
				Vec3d vec3d3 = null;
				List<Entity> list = this.mc.world.getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox()
						.addCoord(vec3d1.xCoord * d0, vec3d1.yCoord * d0, vec3d1.zCoord * d0).expand(1.0D, 1.0D, 1.0D),
						Predicates.and(EntitySelectors.NOT_SPECTATING, new Predicate<Entity>() {
							public boolean apply(@Nullable Entity p_apply_1_) {
								return p_apply_1_ != null && p_apply_1_.canBeCollidedWith();
							}
						}));
				double d2 = d1;

				for (int j = 0; j < list.size(); ++j) {
					Entity entity1 = list.get(j);
					AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox()
							.expandXyz((double) entity1.getCollisionBorderSize());
					RayTraceResult raytraceresult = axisalignedbb.calculateIntercept(vec3d, vec3d2);

					if (axisalignedbb.isVecInside(vec3d)) {
						if (d2 >= 0.0D) {
							this.pointedEntity = entity1;
							vec3d3 = raytraceresult == null ? vec3d : raytraceresult.hitVec;
							d2 = 0.0D;
						}
					} else if (raytraceresult != null) {
						double d3 = vec3d.distanceTo(raytraceresult.hitVec);

						if (d3 < d2 || d2 == 0.0D) {
							if (entity1.getLowestRidingEntity() == entity.getLowestRidingEntity()) {
								if (d2 == 0.0D) {
									this.pointedEntity = entity1;
									vec3d3 = raytraceresult.hitVec;
								}
							} else {
								this.pointedEntity = entity1;
								vec3d3 = raytraceresult.hitVec;
								d2 = d3;
							}
						}
					}
				}

				if (this.pointedEntity != null && flag && vec3d.distanceTo(vec3d3) > 3.0D) {
					this.pointedEntity = null;
					this.mc.objectMouseOver = new RayTraceResult(RayTraceResult.Type.MISS, vec3d3, (EnumFacing) null,
							new BlockPos(vec3d3));
				}

				if (this.pointedEntity != null && (d2 < d1 || this.mc.objectMouseOver == null)) {
					this.mc.objectMouseOver = new RayTraceResult(this.pointedEntity, vec3d3);

					if (this.pointedEntity instanceof EntityLivingBase
							|| this.pointedEntity instanceof EntityItemFrame) {
						this.mc.pointedEntity = this.pointedEntity;
					}
				}
			}
		}
	}

	/**
	 * Update FOV modifier hand
	 */
	private void updateFovModifierHand() {
		float f = 1.0F;

		if (this.mc.getRenderViewEntity() instanceof AbstractClientPlayer) {
			AbstractClientPlayer abstractclientplayer = (AbstractClientPlayer) this.mc.getRenderViewEntity();
			f = abstractclientplayer.getFovModifier();
		}

		this.fovModifierHandPrev = this.fovModifierHand;
		this.fovModifierHand += (f - this.fovModifierHand) * 0.5F;

		if (this.fovModifierHand > 1.5F) {
			this.fovModifierHand = 1.5F;
		}

		if (this.fovModifierHand < 0.1F) {
			this.fovModifierHand = 0.1F;
		}
	}

	/**
	 * Changes the field of view of the player depending on if they are underwater
	 * or not
	 */
	private float getFOVModifier(float partialTicks, boolean useFOVSetting) {
		if (this.debugView) {
			return 90.0F;
		} else {
			Entity entity = this.mc.getRenderViewEntity();
			float f = 70.0F;

			if (useFOVSetting) {
				f = this.mc.gameSettings.fovSetting;
				f = f * (this.fovModifierHandPrev + (this.fovModifierHand - this.fovModifierHandPrev) * partialTicks);
			}

			if (entity instanceof EntityLivingBase && ((EntityLivingBase) entity).getHealth() <= 0.0F) {
				float f1 = (float) ((EntityLivingBase) entity).deathTime + partialTicks;
				f /= (1.0F - 500.0F / (f1 + 500.0F)) * 2.0F + 1.0F;
			}

			IBlockState iblockstate = ActiveRenderInfo.getBlockStateAtEntityViewpoint(this.mc.world, entity,
					partialTicks);

			if (iblockstate.getMaterial() == Material.WATER) {
				f = f * 60.0F / 70.0F;
			}

			return f;
		}
	}

	private void hurtCameraEffect(float partialTicks) {
		if (this.mc.getRenderViewEntity() instanceof EntityLivingBase) {
			EntityLivingBase entitylivingbase = (EntityLivingBase) this.mc.getRenderViewEntity();
			float f = (float) entitylivingbase.hurtTime - partialTicks;

			if (entitylivingbase.getHealth() <= 0.0F) {
				float f1 = (float) entitylivingbase.deathTime + partialTicks;
				GlStateManager.rotate(40.0F - 8000.0F / (f1 + 200.0F), 0.0F, 0.0F, 1.0F);
			}

			if (f < 0.0F) {
				return;
			}

			f = f / (float) entitylivingbase.maxHurtTime;
			f = MathHelper.sin(f * f * f * f * (float) Math.PI);
			float f2 = entitylivingbase.attackedAtYaw;
			GlStateManager.rotate(-f2, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotate(-f * 14.0F, 0.0F, 0.0F, 1.0F);
			GlStateManager.rotate(f2, 0.0F, 1.0F, 0.0F);
		}
	}

	/**
	 * Updates the bobbing render effect of the player.
	 */
	private void setupViewBobbing(float partialTicks) {
		if (this.mc.getRenderViewEntity() instanceof EntityPlayer) {
			EntityPlayer entityplayer = (EntityPlayer) this.mc.getRenderViewEntity();
			float f = entityplayer.distanceWalkedModified - entityplayer.prevDistanceWalkedModified;
			float f1 = -(entityplayer.distanceWalkedModified + f * partialTicks);
			float f2 = entityplayer.prevCameraYaw
					+ (entityplayer.cameraYaw - entityplayer.prevCameraYaw) * partialTicks;
			float f3 = entityplayer.prevCameraPitch
					+ (entityplayer.cameraPitch - entityplayer.prevCameraPitch) * partialTicks;
			GlStateManager.translate(MathHelper.sin(f1 * (float) Math.PI) * f2 * 0.5F,
					-Math.abs(MathHelper.cos(f1 * (float) Math.PI) * f2), 0.0F);
			GlStateManager.rotate(MathHelper.sin(f1 * (float) Math.PI) * f2 * 3.0F, 0.0F, 0.0F, 1.0F);
			GlStateManager.rotate(Math.abs(MathHelper.cos(f1 * (float) Math.PI - 0.2F) * f2) * 5.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotate(f3, 1.0F, 0.0F, 0.0F);
		}
	}

	/**
	 * sets up player's eye (or camera in third person mode)
	 */
	private void orientCamera(float partialTicks) {
		Entity entity = this.mc.getRenderViewEntity();
		float f = entity.getEyeHeight();
		double d0 = entity.prevPosX + (entity.posX - entity.prevPosX) * (double) partialTicks;
		double d1 = entity.prevPosY + (entity.posY - entity.prevPosY) * (double) partialTicks + (double) f;
		double d2 = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * (double) partialTicks;

		if (entity instanceof EntityLivingBase && ((EntityLivingBase) entity).isPlayerSleeping()) {
			f = (float) ((double) f + 1.0D);
			GlStateManager.translate(0.0F, 0.3F, 0.0F);

			if (!this.mc.gameSettings.debugCamEnable) {
				BlockPos blockpos = new BlockPos(entity);
				IBlockState iblockstate = this.mc.world.getBlockState(blockpos);
				Block block = iblockstate.getBlock();

				if (block == Blocks.BED) {
					int j = ((EnumFacing) iblockstate.getValue(BlockBed.FACING)).getHorizontalIndex();
					GlStateManager.rotate((float) (j * 90), 0.0F, 1.0F, 0.0F);
				}

				GlStateManager.rotate(
						entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks + 180.0F,
						0.0F, -1.0F, 0.0F);
				GlStateManager.rotate(
						entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks,
						-1.0F, 0.0F, 0.0F);
			}
		} else if (this.mc.gameSettings.thirdPersonView > 0) {
			double d3 = (double) (this.thirdPersonDistancePrev + (4.0F - this.thirdPersonDistancePrev) * partialTicks);

			if (this.mc.gameSettings.debugCamEnable) {
				GlStateManager.translate(0.0F, 0.0F, (float) (-d3));
			} else {
				float f1 = entity.rotationYaw;
				float f2 = entity.rotationPitch;

				if (this.mc.gameSettings.thirdPersonView == 2) {
					f2 += 180.0F;
				}

				double d4 = (double) (-MathHelper.sin(f1 * 0.017453292F) * MathHelper.cos(f2 * 0.017453292F)) * d3;
				double d5 = (double) (MathHelper.cos(f1 * 0.017453292F) * MathHelper.cos(f2 * 0.017453292F)) * d3;
				double d6 = (double) (-MathHelper.sin(f2 * 0.017453292F)) * d3;

				for (int i = 0; i < 8; ++i) {
					float f3 = (float) ((i & 1) * 2 - 1);
					float f4 = (float) ((i >> 1 & 1) * 2 - 1);
					float f5 = (float) ((i >> 2 & 1) * 2 - 1);
					f3 = f3 * 0.1F;
					f4 = f4 * 0.1F;
					f5 = f5 * 0.1F;
					RayTraceResult raytraceresult = this.mc.world
							.rayTraceBlocks(new Vec3d(d0 + (double) f3, d1 + (double) f4, d2 + (double) f5), new Vec3d(
									d0 - d4 + (double) f3 + (double) f5, d1 - d6 + (double) f4, d2 - d5 + (double) f5));

					if (raytraceresult != null) {
						double d7 = raytraceresult.hitVec.distanceTo(new Vec3d(d0, d1, d2));

						if (d7 < d3) {
							d3 = d7;
						}
					}
				}

				if (this.mc.gameSettings.thirdPersonView == 2) {
					GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
				}

				GlStateManager.rotate(entity.rotationPitch - f2, 1.0F, 0.0F, 0.0F);
				GlStateManager.rotate(entity.rotationYaw - f1, 0.0F, 1.0F, 0.0F);
				GlStateManager.translate(0.0F, 0.0F, (float) (-d3));
				GlStateManager.rotate(f1 - entity.rotationYaw, 0.0F, 1.0F, 0.0F);
				GlStateManager.rotate(f2 - entity.rotationPitch, 1.0F, 0.0F, 0.0F);
			}
		} else {
			GlStateManager.translate(0.0F, 0.0F, 0.05F);
		}

		if (!this.mc.gameSettings.debugCamEnable) {
			GlStateManager.rotate(
					entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks, 1.0F,
					0.0F, 0.0F);

			if (entity instanceof EntityAnimal) {
				EntityAnimal entityanimal = (EntityAnimal) entity;
				GlStateManager.rotate(entityanimal.prevRotationYawHead
						+ (entityanimal.rotationYawHead - entityanimal.prevRotationYawHead) * partialTicks + 180.0F,
						0.0F, 1.0F, 0.0F);
			} else {
				GlStateManager.rotate(
						entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks + 180.0F,
						0.0F, 1.0F, 0.0F);
			}
		}

		GlStateManager.translate(0.0F, -f, 0.0F);
		d0 = entity.prevPosX + (entity.posX - entity.prevPosX) * (double) partialTicks;
		d1 = entity.prevPosY + (entity.posY - entity.prevPosY) * (double) partialTicks + (double) f;
		d2 = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * (double) partialTicks;
		this.cloudFog = this.mc.renderGlobal.hasCloudFog(d0, d1, d2, partialTicks);
	}

	/**
	 * sets up projection, view effects, camera position/rotation
	 */
	private void setupCameraTransform(float partialTicks, int pass) {
		this.farPlaneDistance = (float) (this.mc.gameSettings.renderDistanceChunks * 16);
		GlStateManager.matrixMode(5889);
		GlStateManager.loadIdentity();
		float f = 0.07F;

		if (this.mc.gameSettings.anaglyph) {
			GlStateManager.translate((float) (-(pass * 2 - 1)) * 0.07F, 0.0F, 0.0F);
		}

		if (this.cameraZoom != 1.0D) {
			GlStateManager.translate((float) this.cameraYaw, (float) (-this.cameraPitch), 0.0F);
			GlStateManager.scale(this.cameraZoom, this.cameraZoom, 1.0D);
		}

		GlStateManager.gluPerspective(currentProjMatrixFOV = this.getFOVModifier(partialTicks, true),
				(float) this.mc.displayWidth / (float) this.mc.displayHeight, 0.05F,
				this.farPlaneDistance * MathHelper.SQRT_2);
		GlStateManager.matrixMode(5888);
		GlStateManager.loadIdentity();

		if (this.mc.gameSettings.anaglyph) {
			GlStateManager.translate((float) (pass * 2 - 1) * 0.1F, 0.0F, 0.0F);
		}

		this.hurtCameraEffect(partialTicks);

		if (this.mc.gameSettings.viewBobbing) {
			this.setupViewBobbing(partialTicks);
		}

		float f1 = this.mc.player.prevTimeInPortal
				+ (this.mc.player.timeInPortal - this.mc.player.prevTimeInPortal) * partialTicks;

		if (f1 > 0.0F) {
			int i = 20;

			if (this.mc.player.isPotionActive(MobEffects.NAUSEA)) {
				i = 7;
			}

			float f2 = 5.0F / (f1 * f1 + 5.0F) - f1 * 0.04F;
			f2 = f2 * f2;
			GlStateManager.rotate(((float) this.rendererUpdateCount + partialTicks) * (float) i, 0.0F, 1.0F, 1.0F);
			GlStateManager.scale(1.0F / f2, 1.0F, 1.0F);
			GlStateManager.rotate(-((float) this.rendererUpdateCount + partialTicks) * (float) i, 0.0F, 1.0F, 1.0F);
		}

		this.orientCamera(partialTicks);

		if (this.debugView) {
			switch (this.debugViewDirection) {
			case 0:
				GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
				break;

			case 1:
				GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
				break;

			case 2:
				GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
				break;

			case 3:
				GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
				break;

			case 4:
				GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
			}
		}
	}

	/**
	 * Render player hand
	 */
	private void renderHand(float partialTicks, int pass) {
		if (!this.debugView) {
			GlStateManager.matrixMode(5889);
			GlStateManager.loadIdentity();
			float f = 0.07F;

			if (this.mc.gameSettings.anaglyph) {
				GlStateManager.translate((float) (-(pass * 2 - 1)) * 0.07F, 0.0F, 0.0F);
			}

			GlStateManager.gluPerspective(this.getFOVModifier(partialTicks, false),
					(float) this.mc.displayWidth / (float) this.mc.displayHeight, 0.05F, this.farPlaneDistance * 2.0F);
			GlStateManager.matrixMode(5888);
			GlStateManager.loadIdentity();

			if (this.mc.gameSettings.anaglyph) {
				GlStateManager.translate((float) (pass * 2 - 1) * 0.1F, 0.0F, 0.0F);
			}

			GlStateManager.pushMatrix();
			this.hurtCameraEffect(partialTicks);

			if (this.mc.gameSettings.viewBobbing) {
				this.setupViewBobbing(partialTicks);
			}

			boolean flag = this.mc.getRenderViewEntity() instanceof EntityLivingBase
					&& ((EntityLivingBase) this.mc.getRenderViewEntity()).isPlayerSleeping();

			if (this.mc.gameSettings.thirdPersonView == 0 && !flag && !this.mc.gameSettings.hideGUI
					&& !this.mc.playerController.isSpectator()) {
				this.enableLightmap();
				this.itemRenderer.renderItemInFirstPerson(partialTicks);
				this.disableLightmap();
			}

			GlStateManager.popMatrix();

			if (this.mc.gameSettings.thirdPersonView == 0 && !flag) {
				this.itemRenderer.renderOverlays(partialTicks);
				this.hurtCameraEffect(partialTicks);
			}

			if (this.mc.gameSettings.viewBobbing) {
				this.setupViewBobbing(partialTicks);
			}
		}
	}

	public void disableLightmap() {
		GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GlStateManager.disableTexture2D();
		GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
	}

	public void enableLightmap() {
		GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		this.mc.getTextureManager().bindTexture(this.locationLightMap);
		GlStateManager.enableTexture2D();
		GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
	}

	/**
	 * Recompute a random value that is applied to block color in updateLightmap()
	 */
	private void updateTorchFlicker() {
		this.torchFlickerDX = (float) ((double) this.torchFlickerDX
				+ (Math.random() - Math.random()) * Math.random() * Math.random());
		this.torchFlickerDX = (float) ((double) this.torchFlickerDX * 0.9D);
		this.torchFlickerX += this.torchFlickerDX - this.torchFlickerX;
		this.lightmapUpdateNeeded = true;
	}

	private void updateLightmap(float partialTicks) {
		if (this.lightmapUpdateNeeded) {
			World world = this.mc.world;

			if (world != null) {
				float f = world.getSunBrightness(1.0F);
				float f1 = f * 0.95F + 0.05F;

				for (int i = 0; i < 256; ++i) {
					float f2 = world.provider.getLightBrightnessTable()[i / 16] * f1;
					float f3 = world.provider.getLightBrightnessTable()[i % 16] * (this.torchFlickerX * 0.1F + 1.5F);

					if (world.getLastLightningBolt() > 0) {
						f2 = world.provider.getLightBrightnessTable()[i / 16];
					}

					float f4 = f2 * (f * 0.65F + 0.35F);
					float f5 = f2 * (f * 0.65F + 0.35F);
					float f6 = f3 * ((f3 * 0.6F + 0.4F) * 0.6F + 0.4F);
					float f7 = f3 * (f3 * f3 * 0.6F + 0.4F);
					float f8 = f4 + f3;
					float f9 = f5 + f6;
					float f10 = f2 + f7;
					f8 = f8 * 0.96F + 0.03F;
					f9 = f9 * 0.96F + 0.03F;
					f10 = f10 * 0.96F + 0.03F;

					if (this.bossColorModifier > 0.0F) {
						float f11 = this.bossColorModifierPrev
								+ (this.bossColorModifier - this.bossColorModifierPrev) * partialTicks;
						f8 = f8 * (1.0F - f11) + f8 * 0.7F * f11;
						f9 = f9 * (1.0F - f11) + f9 * 0.6F * f11;
						f10 = f10 * (1.0F - f11) + f10 * 0.6F * f11;
					}

					if (world.provider.getDimensionType().getId() == 1) {
						f8 = 0.22F + f3 * 0.75F;
						f9 = 0.28F + f6 * 0.75F;
						f10 = 0.25F + f7 * 0.75F;
					}

					if (this.mc.player.isPotionActive(MobEffects.NIGHT_VISION)) {
						float f15 = this.getNightVisionBrightness(this.mc.player, partialTicks);
						float f12 = 1.0F / f8;

						if (f12 > 1.0F / f9) {
							f12 = 1.0F / f9;
						}

						if (f12 > 1.0F / f10) {
							f12 = 1.0F / f10;
						}

						f8 = f8 * (1.0F - f15) + f8 * f12 * f15;
						f9 = f9 * (1.0F - f15) + f9 * f12 * f15;
						f10 = f10 * (1.0F - f15) + f10 * f12 * f15;
					}

					if (f8 > 1.0F) {
						f8 = 1.0F;
					}

					if (f9 > 1.0F) {
						f9 = 1.0F;
					}

					if (f10 > 1.0F) {
						f10 = 1.0F;
					}

					float f16 = this.mc.gameSettings.gammaSetting;
					float f17 = 1.0F - f8;
					float f13 = 1.0F - f9;
					float f14 = 1.0F - f10;
					f17 = 1.0F - f17 * f17 * f17 * f17;
					f13 = 1.0F - f13 * f13 * f13 * f13;
					f14 = 1.0F - f14 * f14 * f14 * f14;
					f8 = f8 * (1.0F - f16) + f17 * f16;
					f9 = f9 * (1.0F - f16) + f13 * f16;
					f10 = f10 * (1.0F - f16) + f14 * f16;
					f8 = f8 * 0.96F + 0.03F;
					f9 = f9 * 0.96F + 0.03F;
					f10 = f10 * 0.96F + 0.03F;

					if (f8 > 1.0F) {
						f8 = 1.0F;
					}

					if (f9 > 1.0F) {
						f9 = 1.0F;
					}

					if (f10 > 1.0F) {
						f10 = 1.0F;
					}

					if (f8 < 0.0F) {
						f8 = 0.0F;
					}

					if (f9 < 0.0F) {
						f9 = 0.0F;
					}

					if (f10 < 0.0F) {
						f10 = 0.0F;
					}

					short short1 = 255;
					int j = (int) (f8 * 255.0F);
					int k = (int) (f9 * 255.0F);
					int l = (int) (f10 * 255.0F);
					this.lightmapColors[i] = short1 << 24 | j | k << 8 | l << 16;
				}

				this.lightmapTexture.updateDynamicTexture();
				GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
				this.mc.getTextureManager().bindTexture(this.locationLightMap);
				if (mc.gameSettings.fancyGraphics || mc.gameSettings.ambientOcclusion > 0) {
					EaglercraftGPU.glTexParameteri(RealOpenGLEnums.GL_TEXTURE_2D, RealOpenGLEnums.GL_TEXTURE_MIN_FILTER,
							RealOpenGLEnums.GL_LINEAR);
					EaglercraftGPU.glTexParameteri(RealOpenGLEnums.GL_TEXTURE_2D, RealOpenGLEnums.GL_TEXTURE_MAG_FILTER,
							RealOpenGLEnums.GL_LINEAR);
				} else {
					EaglercraftGPU.glTexParameteri(RealOpenGLEnums.GL_TEXTURE_2D, RealOpenGLEnums.GL_TEXTURE_MIN_FILTER,
							RealOpenGLEnums.GL_NEAREST);
					EaglercraftGPU.glTexParameteri(RealOpenGLEnums.GL_TEXTURE_2D, RealOpenGLEnums.GL_TEXTURE_MAG_FILTER,
							RealOpenGLEnums.GL_NEAREST);
				}
				EaglercraftGPU.glTexParameteri(RealOpenGLEnums.GL_TEXTURE_2D, RealOpenGLEnums.GL_TEXTURE_WRAP_S,
						RealOpenGLEnums.GL_CLAMP_TO_EDGE);
				EaglercraftGPU.glTexParameteri(RealOpenGLEnums.GL_TEXTURE_2D, RealOpenGLEnums.GL_TEXTURE_WRAP_T,
						RealOpenGLEnums.GL_CLAMP_TO_EDGE);
				GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);

				this.lightmapUpdateNeeded = false;
			}
		}
	}

	private float getNightVisionBrightness(EntityLivingBase entitylivingbaseIn, float partialTicks) {
		int i = entitylivingbaseIn.getActivePotionEffect(MobEffects.NIGHT_VISION).getDuration();
		return i > 200 ? 1.0F : 0.7F + MathHelper.sin(((float) i - partialTicks) * (float) Math.PI * 0.2F) * 0.3F;
	}

	private boolean initialized = false;
	public void updateCameraAndRender(float partialTicks, long nanoTime) {
		
		if(!this.initialized) {
			TextureUtils.registerResourceListener();
			initialized = true;
		}
		
		boolean flag = Display.isActive();

		if (!flag && this.mc.gameSettings.pauseOnLostFocus
				&& (!this.mc.gameSettings.touchscreen || !Mouse.isButtonDown(1))) {
			if (Minecraft.getSystemTime() - this.prevFrameTime > 500L) {
				this.mc.displayInGameMenu();
			}
		} else {
			this.prevFrameTime = Minecraft.getSystemTime();
		}

		if (flag && Minecraft.IS_RUNNING_ON_MAC && this.mc.inGameHasFocus && !Mouse.isInsideWindow()) {
			Mouse.setGrabbed(false);
			Mouse.setCursorPosition(Display.getWidth() / 2, Display.getHeight() / 2 - 20);
			Mouse.setGrabbed(true);
		}

		if (this.mc.inGameHasFocus && flag) {
			this.mc.mouseHelper.mouseXYChange();
			this.mc.func_193032_ao().func_193299_a(this.mc.mouseHelper);
			float f = this.mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
			float f1 = f * f * f * 8.0F;
			float f2 = (float) this.mc.mouseHelper.deltaX * f1;
			float f3 = (float) this.mc.mouseHelper.deltaY * f1;
			int i = 1;

			if (this.mc.gameSettings.invertMouse) {
				i = -1;
			}

			if (this.mc.gameSettings.smoothCamera) {
				this.smoothCamYaw += f2;
				this.smoothCamPitch += f3;
				float f4 = partialTicks - this.smoothCamPartialTicks;
				this.smoothCamPartialTicks = partialTicks;
				f2 = this.smoothCamFilterX * f4;
				f3 = this.smoothCamFilterY * f4;
				this.mc.player.setAngles(f2, f3 * (float) i);
			} else {
				this.smoothCamYaw = 0.0F;
				this.smoothCamPitch = 0.0F;
				this.mc.player.setAngles(f2, f3 * (float) i);
			}
		}

		if (!this.mc.skipRenderWorld) {
			anaglyphEnable = this.mc.gameSettings.anaglyph;
			final ScaledResolution scaledresolution = mc.scaledResolution;
			int l = scaledresolution.getScaledWidth();
			int i1 = scaledresolution.getScaledHeight();
			final int j1 = Mouse.getX() * l / this.mc.displayWidth;
			final int k1 = i1 - Mouse.getY() * i1 / this.mc.displayHeight - 1;
			int l1 = this.mc.gameSettings.limitFramerate;

			if (this.mc.world != null) {
				int i = Math.min(Minecraft.getDebugFPS(), l1);
				i = Math.max(i, 60);
				long j = EagRuntime.nanoTime() - nanoTime;
				long k = Math.max((long) (1000000000 / i / 4) - j, 0L);
				this.renderWorld(partialTicks, EagRuntime.nanoTime() + k);

				if (this.mc.isSingleplayer() && this.timeWorldIcon < Minecraft.getSystemTime() - 1000L) {
					this.timeWorldIcon = Minecraft.getSystemTime();

					// TODO: Add support for world icons
//                    if (!this.mc.getIntegratedServer().isWorldIconSet())
//                    {
//                        this.createWorldIcon();
//                    }
				}
				final boolean b = !this.mc.gameSettings.hideGUI || this.mc.currentScreen != null;
				if(b) {
					this.setupOverlayRendering();
					GlStateManager.alphaFunc(RealOpenGLEnums.GL_GREATER, 0.1F);
					long framebufferAge = this.overlayFramebuffer.getAge();
					if (framebufferAge == -1l || framebufferAge > (Minecraft.getDebugFPS() < 25 ? 125l : 75l)) {
						this.overlayFramebuffer.beginRender(mc.displayWidth, mc.displayHeight);
						GlStateManager.colorMask(true, true, true, true);
						GlStateManager.clearColor(0.0f, 0.0f, 0.0f, 0.0f);
						GlStateManager.clear(RealOpenGLEnums.GL_COLOR_BUFFER_BIT | RealOpenGLEnums.GL_DEPTH_BUFFER_BIT);
						GlStateManager.enableOverlayFramebufferBlending();
						if (b) {
							this.func_190563_a(i1, j1, partialTicks);
							this.mc.ingameGUI.renderGameOverlay(partialTicks);
						}
						GlStateManager.disableOverlayFramebufferBlending();
						this.overlayFramebuffer.endRender();
					}
				}
				if (b) {
					GlStateManager.disableLighting();
					GlStateManager.enableBlend();
					if (Minecraft.isFancyGraphicsEnabled()) {
						this.mc.ingameGUI.renderVignette(this.mc.player.getBrightness(), scaledresolution);
					}
					GlStateManager.enableBlend();
					GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
					this.mc.ingameGUI.renderAttackIndicator(partialTicks, scaledresolution);
					GlStateManager.tryBlendFuncSeparate(RealOpenGLEnums.GL_SRC_ALPHA,
							RealOpenGLEnums.GL_ONE_MINUS_SRC_ALPHA, RealOpenGLEnums.GL_ONE, RealOpenGLEnums.GL_ZERO);
					GlStateManager.bindTexture(this.overlayFramebuffer.getTexture());
					GlStateManager.enableBlend();
					GlStateManager.blendFunc(RealOpenGLEnums.GL_ONE, RealOpenGLEnums.GL_ONE_MINUS_SRC_ALPHA);
					GlStateManager.disableAlpha();
					GlStateManager.disableDepth();
					GlStateManager.depthMask(false);
					Tessellator tessellator = Tessellator.getInstance();
					WorldRenderer worldrenderer = tessellator.getWorldRenderer();
					worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
					worldrenderer.pos(0.0D, (double) i1, -90.0D).tex(0.0D, 0.0D).endVertex();
					worldrenderer.pos((double) l, (double) i1, -90.0D).tex(1.0D, 0.0D).endVertex();
					worldrenderer.pos((double) l, 0.0D, -90.0D).tex(1.0D, 1.0D).endVertex();
					worldrenderer.pos(0.0D, 0.0D, -90.0D).tex(0.0D, 1.0D).endVertex();
					tessellator.draw();
					GlStateManager.depthMask(true);
					GlStateManager.enableDepth();
					GlStateManager.disableBlend();
				}
			} else {
				GlStateManager.viewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
				GlStateManager.matrixMode(5889);
				GlStateManager.loadIdentity();
				GlStateManager.matrixMode(5888);
				GlStateManager.loadIdentity();
				this.setupOverlayRendering();
			}
			
			this.mc.notifRenderer.renderOverlay(j1, k1);

			if (this.mc.currentScreen != null) {
				GlStateManager.clear(256);

				try {
					this.mc.currentScreen.drawScreen(j1, k1, this.mc.func_193989_ak());
				} catch (Throwable throwable) {
					CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Rendering screen");
					CrashReportCategory crashreportcategory = crashreport.makeCategory("Screen render details");
					crashreportcategory.setDetail("Screen name", new ICrashReportDetail<String>() {
						public String call() throws Exception {
							return EntityRenderer.this.mc.currentScreen.getClass().getCanonicalName();
						}
					});
					crashreportcategory.setDetail("Mouse location", new ICrashReportDetail<String>() {
						public String call() throws Exception {
							return HString.format("Scaled: (%d, %d). Absolute: (%d, %d)",
									new Object[] { Integer.valueOf(k1), Integer.valueOf(l1),
											Integer.valueOf(Mouse.getX()), Integer.valueOf(Mouse.getY()) });
						}
					});
					crashreportcategory.setDetail("Screen size", new ICrashReportDetail<String>() {
						public String call() throws Exception {
							return HString.format("Scaled: (%d, %d). Absolute: (%d, %d). Scale factor of %d",
									new Object[] { Integer.valueOf(scaledresolution.getScaledWidth()),
											Integer.valueOf(scaledresolution.getScaledHeight()),
											Integer.valueOf(EntityRenderer.this.mc.displayWidth),
											Integer.valueOf(EntityRenderer.this.mc.displayHeight),
											Integer.valueOf(scaledresolution.getScaleFactor()) });
						}
					});
					throw new ReportedException(crashreport);
				}
			}
		}
	}

	private void createWorldIcon() {
		// TODO: Uh, rewrite this ig
		// bruh just use lax's image code ripped straight from eaglerX
//        if (this.mc.renderGlobal.getRenderedChunks() > 10 && this.mc.renderGlobal.hasNoChunkUpdates() && !this.mc.getIntegratedServer().isWorldIconSet())
//        {
//            BufferedImage bufferedimage = ScreenShotHelper.createScreenshot(this.mc.displayWidth, this.mc.displayHeight, this.mc.getFramebuffer());
//            int i = bufferedimage.getWidth();
//            int j = bufferedimage.getHeight();
//            int k = 0;
//            int l = 0;
//
//            if (i > j)
//            {
//                k = (i - j) / 2;
//                i = j;
//            }
//            else
//            {
//                l = (j - i) / 2;
//            }
//
//            try
//            {
//                BufferedImage bufferedimage1 = new BufferedImage(64, 64, 1);
//                Graphics graphics = bufferedimage1.createGraphics();
//                graphics.drawImage(bufferedimage, 0, 0, 64, 64, k, l, k + i, l + i, (ImageObserver)null);
//                graphics.dispose();
//                ImageIO.write(bufferedimage1, "png", this.mc.getIntegratedServer().getWorldIconFile());
//            }
//            catch (IOException ioexception)
//            {
//                LOGGER.warn("Couldn't save auto screenshot", (Throwable)ioexception);
//            }
//        }
	}

	private boolean isDrawBlockOutline() {
		if (!this.drawBlockOutline) {
			return false;
		} else {
			Entity entity = this.mc.getRenderViewEntity();
			boolean flag = entity instanceof EntityPlayer && !this.mc.gameSettings.hideGUI;

			if (flag && !((EntityPlayer) entity).capabilities.allowEdit) {
				ItemStack itemstack = ((EntityPlayer) entity).getHeldItemMainhand();

				if (this.mc.objectMouseOver != null && this.mc.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK) {
					BlockPos blockpos = this.mc.objectMouseOver.getBlockPos();
					Block block = this.mc.world.getBlockState(blockpos).getBlock();

					if (this.mc.playerController.getCurrentGameType() == GameType.SPECTATOR) {
						flag = block.hasTileEntity() && this.mc.world.getTileEntity(blockpos) instanceof IInventory;
					} else {
						flag = !itemstack.func_190926_b()
								&& (itemstack.canDestroy(block) || itemstack.canPlaceOn(block));
					}
				}
			}

			return flag;
		}
	}

	public void renderWorld(float partialTicks, long finishTimeNano) {
		this.updateLightmap(partialTicks);

		if (this.mc.getRenderViewEntity() == null) {
			this.mc.setRenderViewEntity(this.mc.player);
		}

		this.getMouseOver(partialTicks);
		GlStateManager.enableDepth();
		GlStateManager.enableAlpha();
		GlStateManager.alphaFunc(516, 0.5F);

		if (this.mc.gameSettings.anaglyph) {
			anaglyphField = 0;
			GlStateManager.colorMask(false, true, true, false);
			this.renderWorldPass(0, partialTicks, finishTimeNano);
			anaglyphField = 1;
			GlStateManager.colorMask(true, false, false, false);
			this.renderWorldPass(1, partialTicks, finishTimeNano);
			GlStateManager.colorMask(true, true, true, false);
		} else {
			this.renderWorldPass(2, partialTicks, finishTimeNano);
		}
	}

	private void renderWorldPass(int pass, float partialTicks, long finishTimeNano) {
		RenderGlobal renderglobal = this.mc.renderGlobal;
		ParticleManager particlemanager = this.mc.effectRenderer;
		boolean flag = this.isDrawBlockOutline();
		GlStateManager.enableCull();
		GlStateManager.viewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
		this.updateFogColor(partialTicks);
		GlStateManager.clear(16640);
		this.setupCameraTransform(partialTicks, pass);
		ActiveRenderInfo.updateRenderInfo(this.mc.player, this.mc.gameSettings.thirdPersonView == 2);
		ClippingHelperImpl.getInstance();
		ICamera icamera = new Frustum();
		Entity entity = this.mc.getRenderViewEntity();
		double d0 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) partialTicks;
		double d1 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) partialTicks;
		double d2 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) partialTicks;
		icamera.setPosition(d0, d1, d2);

		if (this.mc.gameSettings.renderDistanceChunks >= 4) {
			this.setupFog(-1, partialTicks);
			GlStateManager.matrixMode(5889);
			GlStateManager.loadIdentity();
			float vigg = this.getFOVModifier(partialTicks, true);
			GlStateManager.gluPerspective(vigg, (float) this.mc.displayWidth / (float) this.mc.displayHeight, 0.05F, this.farPlaneDistance * 2.0F);
			GlStateManager.matrixMode(5888);
			renderglobal.renderSky(partialTicks, pass);
			GlStateManager.matrixMode(5889);
			GlStateManager.loadIdentity();
			GlStateManager.gluPerspective(vigg, (float) this.mc.displayWidth / (float) this.mc.displayHeight, 0.05F, this.farPlaneDistance * MathHelper.SQRT_2);
			GlStateManager.matrixMode(5888);
		}

		this.setupFog(0, partialTicks);
		GlStateManager.shadeModel(7425);

		if (entity.posY + (double) entity.getEyeHeight() < 128.0D) {
			this.renderCloudsCheck(renderglobal, partialTicks, pass, d0, d1, d2);
		}

		this.setupFog(0, partialTicks);
		this.mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		RenderHelper.disableStandardItemLighting();
		renderglobal.setupTerrain(entity, (double) partialTicks, icamera, this.frameCount++,
				this.mc.player.isSpectator());

		if (pass == 0 || pass == 2) {
			this.mc.renderGlobal.updateChunks(finishTimeNano);
		}

		GlStateManager.matrixMode(5888);
		GlStateManager.pushMatrix();
		GlStateManager.disableAlpha();
		GlStateManager.disableBlend();
		renderglobal.renderBlockLayer(BlockRenderLayer.SOLID, (double) partialTicks, pass, entity);
		GlStateManager.enableAlpha();
		GlStateManager.alphaFunc(RealOpenGLEnums.GL_GREATER, 0.5F);
		renderglobal.renderBlockLayer(BlockRenderLayer.CUTOUT_MIPPED, (double) partialTicks, pass, entity);
		this.mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
		renderglobal.renderBlockLayer(BlockRenderLayer.CUTOUT, (double) partialTicks, pass, entity);
		this.mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
		GlStateManager.shadeModel(7424);
		GlStateManager.alphaFunc(RealOpenGLEnums.GL_GREATER, 0.1F);

		if (!this.debugView) {
			GlStateManager.matrixMode(5888);
			GlStateManager.popMatrix();
			GlStateManager.pushMatrix();
			RenderHelper.enableStandardItemLighting();
			renderglobal.renderEntities(entity, icamera, partialTicks);
			RenderHelper.disableStandardItemLighting();
			this.disableLightmap();
		}

		GlStateManager.matrixMode(5888);
		GlStateManager.popMatrix();

		if (flag && this.mc.objectMouseOver != null && !entity.isInsideOfMaterial(Material.WATER)) {
			EntityPlayer entityplayer = (EntityPlayer) entity;
			GlStateManager.disableAlpha();
			renderglobal.drawSelectionBox(entityplayer, this.mc.objectMouseOver, 0, partialTicks);
			GlStateManager.enableAlpha();
		}

		if (this.mc.debugRenderer.shouldRender()) {
			this.mc.debugRenderer.renderDebug(partialTicks, finishTimeNano);
		}

		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(RealOpenGLEnums.GL_SRC_ALPHA, RealOpenGLEnums.GL_ONE,
				RealOpenGLEnums.GL_ONE, RealOpenGLEnums.GL_ZERO);
		this.mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).setBlurMipmap(false, false);
		renderglobal.drawBlockDamageTexture(Tessellator.getInstance(), Tessellator.getInstance().getBuffer(), entity,
				partialTicks);
		this.mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
		GlStateManager.disableBlend();

		if (!this.debugView) {
			this.enableLightmap();
			particlemanager.renderLitParticles(entity, partialTicks);
			RenderHelper.disableStandardItemLighting();
			this.setupFog(0, partialTicks);
			particlemanager.renderParticles(entity, partialTicks);
			this.disableLightmap();
		}

		GlStateManager.depthMask(false);
		GlStateManager.enableCull();
		this.renderRainSnow(partialTicks);
		GlStateManager.depthMask(true);
		renderglobal.renderWorldBorder(entity, partialTicks);
		GlStateManager.disableBlend();
		GlStateManager.enableCull();
		GlStateManager.tryBlendFuncSeparate(RealOpenGLEnums.GL_SRC_ALPHA, RealOpenGLEnums.GL_ONE_MINUS_SRC_ALPHA,
				RealOpenGLEnums.GL_ONE, RealOpenGLEnums.GL_ZERO);
		GlStateManager.alphaFunc(516, 0.1F);
		this.setupFog(0, partialTicks);
		GlStateManager.enableBlend();
		GlStateManager.depthMask(false);
		this.mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
		GlStateManager.shadeModel(7425);
		renderglobal.renderBlockLayer(BlockRenderLayer.TRANSLUCENT, (double) partialTicks, pass, entity);
		GlStateManager.shadeModel(7424);
		GlStateManager.depthMask(true);
		GlStateManager.enableCull();
		GlStateManager.disableBlend();
		GlStateManager.disableFog();

		if (entity.posY + (double) entity.getEyeHeight() >= 128.0D) {
			this.renderCloudsCheck(renderglobal, partialTicks, pass, d0, d1, d2);
		}

		if (this.renderHand) {
			GlStateManager.clear(256);
			this.renderHand(partialTicks, pass);
		}
	}

	private void renderCloudsCheck(RenderGlobal renderGlobalIn, float partialTicks, int pass, double p_180437_4_,
			double p_180437_6_, double p_180437_8_) {
		if (this.mc.gameSettings.shouldRenderClouds() != 0) {
			GlStateManager.matrixMode(5889);
			GlStateManager.loadIdentity();
			GlStateManager.gluPerspective(this.getFOVModifier(partialTicks, true), (float) this.mc.displayWidth / (float) this.mc.displayHeight, 0.05F, this.farPlaneDistance * 4.0F);
			GlStateManager.matrixMode(5888);
			GlStateManager.pushMatrix();
			this.setupFog(0, partialTicks);
			renderGlobalIn.cloudRenderer.renderClouds(partialTicks, pass);
			GlStateManager.disableFog();
			GlStateManager.popMatrix();
			GlStateManager.matrixMode(5889);
			GlStateManager.loadIdentity();
			GlStateManager.gluPerspective(this.getFOVModifier(partialTicks, true), (float) this.mc.displayWidth / (float) this.mc.displayHeight, 0.05F, this.farPlaneDistance * MathHelper.SQRT_2);
			GlStateManager.matrixMode(5888);
		}
	}

	private void addRainParticles() {
		float f = this.mc.world.getRainStrength(1.0F);

		if (!this.mc.gameSettings.fancyGraphics) {
			f /= 2.0F;
		}

		if (f != 0.0F) {
			this.random.setSeed((long) this.rendererUpdateCount * 312987231L);
			Entity entity = this.mc.getRenderViewEntity();
			World world = this.mc.world;
			BlockPos blockpos = new BlockPos(entity);
			double d0 = 0.0D;
			double d1 = 0.0D;
			double d2 = 0.0D;
			int j = 0;
			int k = (int) (100.0F * f * f);

			if (this.mc.gameSettings.particleSetting == 1) {
				k >>= 1;
			} else if (this.mc.gameSettings.particleSetting == 2) {
				k = 0;
			}

			for (int l = 0; l < k; ++l) {
				BlockPos blockpos1 = world
						.getPrecipitationHeight(blockpos.add(this.random.nextInt(10) - this.random.nextInt(10), 0,
								this.random.nextInt(10) - this.random.nextInt(10)));
				Biome biome = world.getBiome(blockpos1);
				BlockPos blockpos2 = blockpos1.down();
				IBlockState iblockstate = world.getBlockState(blockpos2);

				if (blockpos1.getY() <= blockpos.getY() + 10 && blockpos1.getY() >= blockpos.getY() - 10
						&& biome.canRain() && biome.getFloatTemperature(blockpos1) >= 0.15F) {
					double d3 = this.random.nextDouble();
					double d4 = this.random.nextDouble();
					AxisAlignedBB axisalignedbb = iblockstate.getBoundingBox(world, blockpos2);

					if (iblockstate.getMaterial() != Material.LAVA && iblockstate.getBlock() != Blocks.MAGMA) {
						if (iblockstate.getMaterial() != Material.AIR) {
							++j;

							if (this.random.nextInt(j) == 0) {
								d0 = (double) blockpos2.getX() + d3;
								d1 = (double) ((float) blockpos2.getY() + 0.1F) + axisalignedbb.maxY - 1.0D;
								d2 = (double) blockpos2.getZ() + d4;
							}

							this.mc.world.spawnParticle(EnumParticleTypes.WATER_DROP, (double) blockpos2.getX() + d3,
									(double) ((float) blockpos2.getY() + 0.1F) + axisalignedbb.maxY,
									(double) blockpos2.getZ() + d4, 0.0D, 0.0D, 0.0D, new int[0]);
						}
					} else {
						this.mc.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, (double) blockpos1.getX() + d3,
								(double) ((float) blockpos1.getY() + 0.1F) - axisalignedbb.minY,
								(double) blockpos1.getZ() + d4, 0.0D, 0.0D, 0.0D, new int[0]);
					}
				}
			}

			if (j > 0 && this.random.nextInt(3) < this.rainSoundCounter++) {
				this.rainSoundCounter = 0;

				if (d1 > (double) (blockpos.getY() + 1)
						&& world.getPrecipitationHeight(blockpos).getY() > MathHelper.floor((float) blockpos.getY())) {
					this.mc.world.playSound(d0, d1, d2, SoundEvents.WEATHER_RAIN_ABOVE, SoundCategory.WEATHER, 0.1F,
							0.5F, false);
				} else {
					this.mc.world.playSound(d0, d1, d2, SoundEvents.WEATHER_RAIN, SoundCategory.WEATHER, 0.2F, 1.0F,
							false);
				}
			}
		}
	}

	/**
	 * Render rain and snow
	 */
	protected void renderRainSnow(float partialTicks) {
		float f = this.mc.world.getRainStrength(partialTicks);

		if (f > 0.0F) {
			this.enableLightmap();
			Entity entity = this.mc.getRenderViewEntity();
			World world = this.mc.world;
			int i = MathHelper.floor(entity.posX);
			int j = MathHelper.floor(entity.posY);
			int k = MathHelper.floor(entity.posZ);
			Tessellator tessellator = Tessellator.getInstance();
			WorldRenderer bufferbuilder = tessellator.getBuffer();
			GlStateManager.disableCull();
			EaglercraftGPU.glNormal3f(0.0F, 1.0F, 0.0F);
			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(RealOpenGLEnums.GL_SRC_ALPHA, RealOpenGLEnums.GL_ONE_MINUS_SRC_ALPHA,
					RealOpenGLEnums.GL_ONE, RealOpenGLEnums.GL_ZERO);
			GlStateManager.alphaFunc(516, 0.1F);
			double d0 = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) partialTicks;
			double d1 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) partialTicks;
			double d2 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) partialTicks;
			int l = MathHelper.floor(d1);
			int i1 = 5;

			if (this.mc.gameSettings.fancyGraphics) {
				i1 = 10;
			}

			int j1 = -1;
			float f1 = (float) this.rendererUpdateCount + partialTicks;
			bufferbuilder.setTranslation(-d0, -d1, -d2);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			BlockPos blockpos$mutableblockpos = new BlockPos();

			for (int k1 = k - i1; k1 <= k + i1; ++k1) {
				for (int l1 = i - i1; l1 <= i + i1; ++l1) {
					int i2 = (k1 - k + 16) * 32 + l1 - i + 16;
					double d3 = (double) this.rainXCoords[i2] * 0.5D;
					double d4 = (double) this.rainYCoords[i2] * 0.5D;
					blockpos$mutableblockpos.setPos(l1, 0, k1);
					Biome biome = world.getBiome(blockpos$mutableblockpos);

					if (biome.canRain() || biome.getEnableSnow()) {
						int j2 = world.getPrecipitationHeight(blockpos$mutableblockpos).getY();
						int k2 = j - i1;
						int l2 = j + i1;

						if (k2 < j2) {
							k2 = j2;
						}

						if (l2 < j2) {
							l2 = j2;
						}

						int i3 = j2;

						if (j2 < l) {
							i3 = l;
						}

						if (k2 != l2) {
							this.random
									.setSeed((long) (l1 * l1 * 3121 + l1 * 45238971 ^ k1 * k1 * 418711 + k1 * 13761));
							blockpos$mutableblockpos.setPos(l1, k2, k1);
							float f2 = biome.getFloatTemperature(blockpos$mutableblockpos);

							if (world.getBiomeProvider().getTemperatureAtHeight(f2, j2) >= 0.15F) {
								if (j1 != 0) {
									if (j1 >= 0) {
										tessellator.draw();
									}

									j1 = 0;
									this.mc.getTextureManager().bindTexture(RAIN_TEXTURES);
									bufferbuilder.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
								}

								double d5 = -((double) (this.rendererUpdateCount + l1 * l1 * 3121 + l1 * 45238971
										+ k1 * k1 * 418711 + k1 * 13761 & 31) + (double) partialTicks) / 32.0D
										* (3.0D + this.random.nextDouble());
								double d6 = (double) ((float) l1 + 0.5F) - entity.posX;
								double d7 = (double) ((float) k1 + 0.5F) - entity.posZ;
								float f3 = MathHelper.sqrt(d6 * d6 + d7 * d7) / (float) i1;
								float f4 = ((1.0F - f3 * f3) * 0.5F + 0.5F) * f;
								blockpos$mutableblockpos.setPos(l1, i3, k1);
								int j3 = world.getCombinedLight(blockpos$mutableblockpos, 0);
								int k3 = j3 >> 16 & 65535;
								int l3 = j3 & 65535;
								bufferbuilder.pos((double) l1 - d3 + 0.5D, (double) l2, (double) k1 - d4 + 0.5D)
										.tex(0.0D, (double) k2 * 0.25D + d5).color(1.0F, 1.0F, 1.0F, f4)
										.lightmap(k3, l3).endVertex();
								bufferbuilder.pos((double) l1 + d3 + 0.5D, (double) l2, (double) k1 + d4 + 0.5D)
										.tex(1.0D, (double) k2 * 0.25D + d5).color(1.0F, 1.0F, 1.0F, f4)
										.lightmap(k3, l3).endVertex();
								bufferbuilder.pos((double) l1 + d3 + 0.5D, (double) k2, (double) k1 + d4 + 0.5D)
										.tex(1.0D, (double) l2 * 0.25D + d5).color(1.0F, 1.0F, 1.0F, f4)
										.lightmap(k3, l3).endVertex();
								bufferbuilder.pos((double) l1 - d3 + 0.5D, (double) k2, (double) k1 - d4 + 0.5D)
										.tex(0.0D, (double) l2 * 0.25D + d5).color(1.0F, 1.0F, 1.0F, f4)
										.lightmap(k3, l3).endVertex();
							} else {
								if (j1 != 1) {
									if (j1 >= 0) {
										tessellator.draw();
									}

									j1 = 1;
									this.mc.getTextureManager().bindTexture(SNOW_TEXTURES);
									bufferbuilder.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
								}

								double d8 = (double) (-((float) (this.rendererUpdateCount & 511) + partialTicks)
										/ 512.0F);
								double d9 = this.random.nextDouble()
										+ (double) f1 * 0.01D * (double) ((float) this.random.nextGaussian());
								double d10 = this.random.nextDouble()
										+ (double) (f1 * (float) this.random.nextGaussian()) * 0.001D;
								double d11 = (double) ((float) l1 + 0.5F) - entity.posX;
								double d12 = (double) ((float) k1 + 0.5F) - entity.posZ;
								float f6 = MathHelper.sqrt(d11 * d11 + d12 * d12) / (float) i1;
								float f5 = ((1.0F - f6 * f6) * 0.3F + 0.5F) * f;
								blockpos$mutableblockpos.setPos(l1, i3, k1);
								int i4 = (world.getCombinedLight(blockpos$mutableblockpos, 0) * 3 + 15728880) / 4;
								int j4 = i4 >> 16 & 65535;
								int k4 = i4 & 65535;
								bufferbuilder.pos((double) l1 - d3 + 0.5D, (double) l2, (double) k1 - d4 + 0.5D)
										.tex(0.0D + d9, (double) k2 * 0.25D + d8 + d10).color(1.0F, 1.0F, 1.0F, f5)
										.lightmap(j4, k4).endVertex();
								bufferbuilder.pos((double) l1 + d3 + 0.5D, (double) l2, (double) k1 + d4 + 0.5D)
										.tex(1.0D + d9, (double) k2 * 0.25D + d8 + d10).color(1.0F, 1.0F, 1.0F, f5)
										.lightmap(j4, k4).endVertex();
								bufferbuilder.pos((double) l1 + d3 + 0.5D, (double) k2, (double) k1 + d4 + 0.5D)
										.tex(1.0D + d9, (double) l2 * 0.25D + d8 + d10).color(1.0F, 1.0F, 1.0F, f5)
										.lightmap(j4, k4).endVertex();
								bufferbuilder.pos((double) l1 - d3 + 0.5D, (double) k2, (double) k1 - d4 + 0.5D)
										.tex(0.0D + d9, (double) l2 * 0.25D + d8 + d10).color(1.0F, 1.0F, 1.0F, f5)
										.lightmap(j4, k4).endVertex();
							}
						}
					}
				}
			}

			if (j1 >= 0) {
				tessellator.draw();
			}

			bufferbuilder.setTranslation(0.0D, 0.0D, 0.0D);
			GlStateManager.enableCull();
			GlStateManager.disableBlend();
			GlStateManager.alphaFunc(516, 0.1F);
			this.disableLightmap();
		}
	}

	/**
	 * Setup orthogonal projection for rendering GUI screen overlays
	 */
	public void setupOverlayRendering() {
		ScaledResolution scaledresolution = mc.scaledResolution;
		GlStateManager.clear(256);
		GlStateManager.matrixMode(5889);
		GlStateManager.loadIdentity();
		GlStateManager.ortho(0.0D, scaledresolution.getScaledWidth_double(), scaledresolution.getScaledHeight_double(),
				0.0D, 1000.0D, 3000.0D);
		GlStateManager.matrixMode(5888);
		GlStateManager.loadIdentity();
		GlStateManager.translate(0.0F, 0.0F, -2000.0F);
	}

	/**
	 * calculates fog and calls glClearColor
	 */
	private void updateFogColor(float partialTicks) {
		World world = this.mc.world;
		Entity entity = this.mc.getRenderViewEntity();
		float f = 0.25F + 0.75F * (float) this.mc.gameSettings.renderDistanceChunks / 32.0F;
		f = 1.0F - (float) Math.pow((double) f, 0.25D);
		Vec3d vec3d = world.getSkyColor(this.mc.getRenderViewEntity(), partialTicks);
		float f1 = (float) vec3d.xCoord;
		float f2 = (float) vec3d.yCoord;
		float f3 = (float) vec3d.zCoord;
		Vec3d vec3d1 = world.getFogColor(partialTicks);
		this.fogColorRed = (float) vec3d1.xCoord;
		this.fogColorGreen = (float) vec3d1.yCoord;
		this.fogColorBlue = (float) vec3d1.zCoord;

		if (this.mc.gameSettings.renderDistanceChunks >= 4) {
			double d0 = MathHelper.sin(world.getCelestialAngleRadians(partialTicks)) > 0.0F ? -1.0D : 1.0D;
			Vec3d vec3d2 = new Vec3d(d0, 0.0D, 0.0D);
			float f5 = (float) entity.getLook(partialTicks).dotProduct(vec3d2);

			if (f5 < 0.0F) {
				f5 = 0.0F;
			}

			if (f5 > 0.0F) {
				float[] afloat = world.provider.calcSunriseSunsetColors(world.getCelestialAngle(partialTicks),
						partialTicks);

				if (afloat != null) {
					f5 = f5 * afloat[3];
					this.fogColorRed = this.fogColorRed * (1.0F - f5) + afloat[0] * f5;
					this.fogColorGreen = this.fogColorGreen * (1.0F - f5) + afloat[1] * f5;
					this.fogColorBlue = this.fogColorBlue * (1.0F - f5) + afloat[2] * f5;
				}
			}
		}

		this.fogColorRed += (f1 - this.fogColorRed) * f;
		this.fogColorGreen += (f2 - this.fogColorGreen) * f;
		this.fogColorBlue += (f3 - this.fogColorBlue) * f;
		float f8 = world.getRainStrength(partialTicks);

		if (f8 > 0.0F) {
			float f4 = 1.0F - f8 * 0.5F;
			float f10 = 1.0F - f8 * 0.4F;
			this.fogColorRed *= f4;
			this.fogColorGreen *= f4;
			this.fogColorBlue *= f10;
		}

		float f9 = world.getThunderStrength(partialTicks);

		if (f9 > 0.0F) {
			float f11 = 1.0F - f9 * 0.5F;
			this.fogColorRed *= f11;
			this.fogColorGreen *= f11;
			this.fogColorBlue *= f11;
		}

		IBlockState iblockstate = ActiveRenderInfo.getBlockStateAtEntityViewpoint(this.mc.world, entity, partialTicks);

		if (this.cloudFog) {
			Vec3d vec3d3 = world.getCloudColour(partialTicks);
			this.fogColorRed = (float) vec3d3.xCoord;
			this.fogColorGreen = (float) vec3d3.yCoord;
			this.fogColorBlue = (float) vec3d3.zCoord;
		} else if (iblockstate.getMaterial() == Material.WATER) {
			float f12 = 0.0F;
			if (entity instanceof EntityLivingBase) {
				f12 = (float)EnchantmentHelper.getRespirationModifier((EntityLivingBase)entity) * 0.2F;
				
				if(((EntityLivingBase)entity).isPotionActive(MobEffects.WATER_BREATHING)) {
					f12 = f12 * 0.3F + 0.6F;
				}
			}
			
			this.fogColorRed = 0.02F + f12;
			this.fogColorGreen = 0.02F + f12;
			this.fogColorBlue = 0.2F + f12;
		} else if (iblockstate.getMaterial() == Material.LAVA) {
			this.fogColorRed = 0.6F;
			this.fogColorGreen = 0.1F;
			this.fogColorBlue = 0.0F;
		}
		
		
		float f13 = this.fogColor2 + (this.fogColor1 - this.fogColor2) * partialTicks;
		this.fogColorRed *= f13;
		this.fogColorGreen *= f13;
		this.fogColorBlue *= f13;
		double d1 = (entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) partialTicks)
				* world.provider.getVoidFogYFactor();

		if (entity instanceof EntityLivingBase && ((EntityLivingBase) entity).isPotionActive(MobEffects.BLINDNESS)) {
			int i = ((EntityLivingBase) entity).getActivePotionEffect(MobEffects.BLINDNESS).getDuration();

			if (i < 20) {
				d1 *= (double) (1.0F - (float) i / 20.0F);
			} else {
				d1 = 0.0D;
			}
		}

		if (d1 < 1.0D) {
			if (d1 < 0.0D) {
				d1 = 0.0D;
			}

			d1 = d1 * d1;
			this.fogColorRed = (float) ((double) this.fogColorRed * d1);
			this.fogColorGreen = (float) ((double) this.fogColorGreen * d1);
			this.fogColorBlue = (float) ((double) this.fogColorBlue * d1);
		}

		if (this.bossColorModifier > 0.0F) {
			float f14 = this.bossColorModifierPrev
					+ (this.bossColorModifier - this.bossColorModifierPrev) * partialTicks;
			this.fogColorRed = this.fogColorRed * (1.0F - f14) + this.fogColorRed * 0.7F * f14;
			this.fogColorGreen = this.fogColorGreen * (1.0F - f14) + this.fogColorGreen * 0.6F * f14;
			this.fogColorBlue = this.fogColorBlue * (1.0F - f14) + this.fogColorBlue * 0.6F * f14;
		}

		if (entity instanceof EntityLivingBase && ((EntityLivingBase) entity).isPotionActive(MobEffects.NIGHT_VISION)) {
			float f15 = this.getNightVisionBrightness((EntityLivingBase) entity, partialTicks);
			float f6 = 1.0F / this.fogColorRed;

			if (f6 > 1.0F / this.fogColorGreen) {
				f6 = 1.0F / this.fogColorGreen;
			}

			if (f6 > 1.0F / this.fogColorBlue) {
				f6 = 1.0F / this.fogColorBlue;
			}

			this.fogColorRed = this.fogColorRed * (1.0F - f15) + this.fogColorRed * f6 * f15;
			this.fogColorGreen = this.fogColorGreen * (1.0F - f15) + this.fogColorGreen * f6 * f15;
			this.fogColorBlue = this.fogColorBlue * (1.0F - f15) + this.fogColorBlue * f6 * f15;
		}

		if (this.mc.gameSettings.anaglyph) {
			float f16 = (this.fogColorRed * 30.0F + this.fogColorGreen * 59.0F + this.fogColorBlue * 11.0F) / 100.0F;
			float f17 = (this.fogColorRed * 30.0F + this.fogColorGreen * 70.0F) / 100.0F;
			float f7 = (this.fogColorRed * 30.0F + this.fogColorBlue * 70.0F) / 100.0F;
			this.fogColorRed = f16;
			this.fogColorGreen = f17;
			this.fogColorBlue = f7;
		}

		GlStateManager.clearColor(this.fogColorRed, this.fogColorGreen, this.fogColorBlue, 0.0F);
	}

	/**
	 * Sets up the fog to be rendered. If the arg passed in is -1 the fog starts at
	 * 0 and goes to 80% of far plane distance and is used for sky rendering.
	 */
	private void setupFog(int startCoords, float partialTicks) {
		this.fogStandard = false;
		Entity entity = this.mc.getRenderViewEntity();
		EaglercraftGPU.glFog(RealOpenGLEnums.GL_FOG_COLOR, this.setFogColorBuffer(this.fogColorRed, this.fogColorGreen, this.fogColorBlue, 1.0F));
        EaglercraftGPU.glNormal3f(0.0F, -1.0F, 0.0F);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        IBlockState iblockstate = ActiveRenderInfo.getBlockStateAtEntityViewpoint(this.mc.world, entity, partialTicks);

        if (entity instanceof EntityLivingBase && ((EntityLivingBase)entity).isPotionActive(MobEffects.BLINDNESS)) {
            float f1 = 5.0F;
            int i = ((EntityLivingBase)entity).getActivePotionEffect(MobEffects.BLINDNESS).getDuration();

            if (i < 20) {
                f1 = 5.0F + (this.farPlaneDistance - 5.0F) * (1.0F - (float) i / 20.0F);
            }

            GlStateManager.setFog(RealOpenGLEnums.GL_LINEAR);

            if (startCoords == -1) {
                GlStateManager.setFogStart(0.0F);
                GlStateManager.setFogEnd(f1 * 0.8F);
            } else {
                GlStateManager.setFogStart(f1 * 0.25F);
                GlStateManager.setFogEnd(f1);
            }
        } else if (this.cloudFog) {
            GlStateManager.setFog(RealOpenGLEnums.GL_EXP);
            GlStateManager.setFogDensity(0.1F);
        } else if (iblockstate.getMaterial() == Material.WATER) {
            GlStateManager.setFog(RealOpenGLEnums.GL_EXP);
            
            if (entity instanceof EntityLivingBase) {
                if (((EntityLivingBase)entity).isPotionActive(MobEffects.WATER_BREATHING)) {
                    GlStateManager.setFogDensity(0.01F);
                } else {
                    GlStateManager.setFogDensity(0.1F - (float)EnchantmentHelper.getRespirationModifier((EntityLivingBase)entity) * 0.03F);
                }
            } else {
                GlStateManager.setFogDensity(0.1F);
            }
        } else if (iblockstate.getMaterial() == Material.LAVA) {
            GlStateManager.setFog(RealOpenGLEnums.GL_EXP);
            GlStateManager.setFogDensity(2.0F);
        } else {
            float f = this.farPlaneDistance;
            this.fogStandard = true;
            GlStateManager.setFog(RealOpenGLEnums.GL_LINEAR);

            if (startCoords == -1) {
                GlStateManager.setFogStart(0.0F);
                GlStateManager.setFogEnd(f);
            } else {
                GlStateManager.setFogStart(f * 0.75F);
                GlStateManager.setFogEnd(f);
            }

            if (this.mc.world.provider.doesXZShowFog((int)entity.posX, (int)entity.posZ) || this.mc.ingameGUI.getBossOverlay().shouldCreateFog()) {
                GlStateManager.setFogStart(f * 0.05F);
                GlStateManager.setFogEnd(Math.min(f, 192.0F) * 0.5F);
            }
        }

        GlStateManager.enableColorMaterial();
        GlStateManager.enableFog();
	}

	public void func_191514_d(boolean p_191514_1_) {
		if (p_191514_1_) {
			EaglercraftGPU.glFog(RealOpenGLEnums.GL_FOG_COLOR, this.setFogColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
		} else {
			EaglercraftGPU.glFog(RealOpenGLEnums.GL_FOG_COLOR,
					this.setFogColorBuffer(this.fogColorRed, this.fogColorGreen, this.fogColorBlue, 1.0F));
		}
	}

	/**
	 * Update and return fogColorBuffer with the RGBA values passed as arguments
	 */
	private FloatBuffer setFogColorBuffer(float red, float green, float blue, float alpha) {
		this.fogColorBuffer.clear();
		this.fogColorBuffer.put(red).put(green).put(blue).put(alpha);
		this.fogColorBuffer.flip();
		return this.fogColorBuffer;
	}

	public void func_190564_k() {
		this.field_190566_ab = null;
		this.theMapItemRenderer.clearLoadedMaps();
	}

	public MapItemRenderer getMapItemRenderer() {
		return this.theMapItemRenderer;
	}

	public static void drawNameplate(FontRenderer fontRendererIn, String str, float x, float y, float z,
			int verticalShift, float viewerYaw, float viewerPitch, boolean isThirdPersonFrontal, boolean isSneaking) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		EaglercraftGPU.glNormal3f(0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(-viewerYaw, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate((float) (isThirdPersonFrontal ? -1 : 1) * viewerPitch, 1.0F, 0.0F, 0.0F);
		GlStateManager.scale(-0.025F, -0.025F, 0.025F);
		GlStateManager.disableLighting();
		GlStateManager.depthMask(false);

		if (!isSneaking) {
			GlStateManager.disableDepth();
		}

		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(RealOpenGLEnums.GL_SRC_ALPHA, RealOpenGLEnums.GL_ONE_MINUS_SRC_ALPHA,
				RealOpenGLEnums.GL_ONE, RealOpenGLEnums.GL_ZERO);
		int i = fontRendererIn.getStringWidth(str) / 2;
		GlStateManager.disableTexture2D();
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer bufferbuilder = tessellator.getBuffer();
		bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
		bufferbuilder.pos((double) (-i - 1), (double) (-1 + verticalShift), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F)
				.endVertex();
		bufferbuilder.pos((double) (-i - 1), (double) (8 + verticalShift), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F)
				.endVertex();
		bufferbuilder.pos((double) (i + 1), (double) (8 + verticalShift), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F)
				.endVertex();
		bufferbuilder.pos((double) (i + 1), (double) (-1 + verticalShift), 0.0D).color(0.0F, 0.0F, 0.0F, 0.25F)
				.endVertex();
		tessellator.draw();
		GlStateManager.enableTexture2D();

		if (!isSneaking) {
			fontRendererIn.drawString(str, -fontRendererIn.getStringWidth(str) / 2, verticalShift, 553648127);
			GlStateManager.enableDepth();
		}

		GlStateManager.depthMask(true);
		fontRendererIn.drawString(str, -fontRendererIn.getStringWidth(str) / 2, verticalShift,
				isSneaking ? 553648127 : -1);
		GlStateManager.enableLighting();
		GlStateManager.disableBlend();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.popMatrix();
	}

	public void func_190565_a(ItemStack p_190565_1_) {
		this.field_190566_ab = p_190565_1_;
		this.field_190567_ac = 40;
		this.field_190568_ad = this.random.nextFloat() * 2.0F - 1.0F;
		this.field_190569_ae = this.random.nextFloat() * 2.0F - 1.0F;
	}

	private void func_190563_a(int p_190563_1_, int p_190563_2_, float p_190563_3_) {
		if (this.field_190566_ab != null && this.field_190567_ac > 0) {
			int i = 40 - this.field_190567_ac;
			float f = ((float) i + p_190563_3_) / 40.0F;
			float f1 = f * f;
			float f2 = f * f1;
			float f3 = 10.25F * f2 * f1 + -24.95F * f1 * f1 + 25.5F * f2 + -13.8F * f1 + 4.0F * f;
			float f4 = f3 * (float) Math.PI;
			float f5 = this.field_190568_ad * (float) (p_190563_1_ / 4);
			float f6 = this.field_190569_ae * (float) (p_190563_2_ / 4);
			GlStateManager.enableAlpha();
			GlStateManager.pushMatrix();
			GlStateManager.pushLightCoords();
			GlStateManager.enableDepth();
			GlStateManager.disableCull();
			RenderHelper.enableStandardItemLighting();
			GlStateManager.translate((float) (p_190563_1_ / 2) + f5 * MathHelper.abs(MathHelper.sin(f4 * 2.0F)),
					(float) (p_190563_2_ / 2) + f6 * MathHelper.abs(MathHelper.sin(f4 * 2.0F)), -50.0F);
			float f7 = 50.0F + 175.0F * MathHelper.sin(f4);
			GlStateManager.scale(f7, -f7, f7);
			GlStateManager.rotate(900.0F * MathHelper.abs(MathHelper.sin(f4)), 0.0F, 1.0F, 0.0F);
			GlStateManager.rotate(6.0F * MathHelper.cos(f * 8.0F), 1.0F, 0.0F, 0.0F);
			GlStateManager.rotate(6.0F * MathHelper.cos(f * 8.0F), 0.0F, 0.0F, 1.0F);
			this.mc.getRenderItem().renderItem(this.field_190566_ab, ItemCameraTransforms.TransformType.FIXED);
			GlStateManager.popLightCoords();
			GlStateManager.popMatrix();
			RenderHelper.disableStandardItemLighting();
			GlStateManager.enableCull();
			GlStateManager.disableDepth();
		}
	}
}
