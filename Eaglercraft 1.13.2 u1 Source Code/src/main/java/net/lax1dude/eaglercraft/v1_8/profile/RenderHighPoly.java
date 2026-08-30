package net.lax1dude.eaglercraft.v1_8.profile;

import static net.lax1dude.eaglercraft.v1_8.opengl.RealOpenGLEnums.*;

import net.lax1dude.eaglercraft.v1_8.EagRuntime;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;
import net.lax1dude.eaglercraft.v1_8.opengl.EaglerMeshLoader;
import net.lax1dude.eaglercraft.v1_8.opengl.EaglercraftGPU;
import net.lax1dude.eaglercraft.v1_8.vector.Matrix4f;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.MathHelper;

public class RenderHighPoly extends RenderPlayer {

	private static final Logger logger = LogManager.getLogger("RenderHighPoly");

	private static final Matrix4f tmpMatrix = new Matrix4f();

	public RenderHighPoly(RenderManager renderManager) {
		super(renderManager);
	}

	public void doRender(AbstractClientPlayer abstractclientplayer, double d0, double d1, double d2, float f, float f1) {
		if (!abstractclientplayer.isUser() || this.renderManager.renderViewEntity == abstractclientplayer) {
			double nameY = d1;
			HighPolySkin highPolySkin = abstractclientplayer.getEaglerSkinModel().highPoly;

			if (highPolySkin == null) {
				super.doRender(abstractclientplayer, d0, d1, d2, f, f1);
				return;
			} else if (highPolySkin == HighPolySkin.LAXATIVE_DUDE) {
				nameY += 0.1;
			} else if (highPolySkin == HighPolySkin.BABY_WINSTON) {
				nameY -= 1.0;
			}

			GlStateManager.pushMatrix();
			GlStateManager.disableCull();

			try {
				Minecraft mc = Minecraft.getInstance();
				float f2 = this.interpolateRotation(abstractclientplayer.prevRenderYawOffset,
						abstractclientplayer.renderYawOffset, f1);
				float f3 = this.interpolateRotation(abstractclientplayer.prevRotationYawHead,
						abstractclientplayer.rotationYawHead, f1);
				float f4 = f3 - f2;
				if (abstractclientplayer.isPassenger() && abstractclientplayer.getRidingEntity() instanceof EntityLivingBase) {
					EntityLivingBase entitylivingbase1 = (EntityLivingBase) abstractclientplayer.getRidingEntity();
					f2 = this.interpolateRotation(entitylivingbase1.prevRenderYawOffset, entitylivingbase1.renderYawOffset,
							f1);
					f4 = f3 - f2;
					float f5 = MathHelper.wrapDegrees(f4);
					if (f5 < -85.0F) {
						f5 = -85.0F;
					}

					if (f5 >= 85.0F) {
						f5 = 85.0F;
					}

					f2 = f3 - f5;
					if (f5 * f5 > 2500.0F) {
						f2 += f5 * 0.2F;
					}
				}

				this.renderLivingAt(abstractclientplayer, d0, d1, d2);
				float f10 = this.handleRotationFloat(abstractclientplayer, f1);
				this.applyRotations(abstractclientplayer, f10, f2, f1);
				GlStateManager.enableRescaleNormal();
				this.preRenderCallback(abstractclientplayer, f1);
				GlStateManager.scalef(HighPolySkin.highPolyScale, HighPolySkin.highPolyScale, HighPolySkin.highPolyScale);
				mc.getTextureManager().bindTexture(highPolySkin.texture);

				if (abstractclientplayer.isPlayerSleeping()) {
					if (highPolySkin == HighPolySkin.LAXATIVE_DUDE || highPolySkin == HighPolySkin.WEIRD_CLIMBER_DUDE) {
						GlStateManager.translatef(0.0f, -3.7f, 0.0f);
					} else if (highPolySkin == HighPolySkin.BABY_WINSTON) {
						GlStateManager.translatef(0.0f, -2.4f, 0.0f);
					} else {
						GlStateManager.translatef(0.0f, -3.0f, 0.0f);
					}
				}

				float var15 = abstractclientplayer.prevLimbSwingAmount
						+ (abstractclientplayer.limbSwingAmount - abstractclientplayer.prevLimbSwingAmount) * f1;
				float var16 = abstractclientplayer.limbSwing - abstractclientplayer.limbSwingAmount * (1.0F - f1);

				if (highPolySkin == HighPolySkin.LONG_ARMS) {
					GlStateManager.rotatef(MathHelper.sin(var16) * 20f * var15, 0.0f, 1.0f, 0.0f);
					GlStateManager.rotatef(MathHelper.cos(var16) * 7f * var15, 0.0f, 0.0f, 1.0f);
				} else if (highPolySkin == HighPolySkin.WEIRD_CLIMBER_DUDE) {
					GlStateManager.rotatef(MathHelper.sin(var16) * 7f * var15, 0.0f, 1.0f, 0.0f);
					GlStateManager.rotatef(MathHelper.cos(var16) * 3f * var15, 0.0f, 0.0f, 1.0f);
					GlStateManager.rotatef(-f3, 0.0f, 1.0f, 0.0f);
					float xd = (float) (abstractclientplayer.posX - abstractclientplayer.prevPosX);
					GlStateManager.rotatef(xd * 70.0f * var15, 0.0f, 0.0f, 1.0f);
					float zd = (float) (abstractclientplayer.posZ - abstractclientplayer.prevPosZ);
					GlStateManager.rotatef(zd * 70.0f * var15, 1.0f, 0.0f, 0.0f);
					GlStateManager.rotatef(f3, 0.0f, 1.0f, 0.0f);
				} else if (highPolySkin == HighPolySkin.LAXATIVE_DUDE) {
					GlStateManager.rotatef(-f3, 0.0f, 1.0f, 0.0f);
					float xd = (float) (abstractclientplayer.posX - abstractclientplayer.prevPosX);
					GlStateManager.rotatef(-xd * 40.0f * var15, 0.0f, 0.0f, 1.0f);
					float zd = (float) (abstractclientplayer.posZ - abstractclientplayer.prevPosZ);
					GlStateManager.rotatef(-zd * 40.0f * var15, 1.0f, 0.0f, 0.0f);
					GlStateManager.rotatef(f3, 0.0f, 1.0f, 0.0f);
				} else if (highPolySkin == HighPolySkin.BABY_WINSTON) {
					GlStateManager.translatef(0.0f, (MathHelper.cos(f10 % 100000.0f) + 1.0f) * var15 * 0.2f, 0.0f);
					GlStateManager.rotatef(MathHelper.sin(var16) * 5f * var15, 0.0f, 1.0f, 0.0f);
					GlStateManager.rotatef(MathHelper.cos(var16) * 5f * var15, 0.0f, 0.0f, 1.0f);
				}

				if (abstractclientplayer.hurtTime > 0 || abstractclientplayer.deathTime > 0) {
					GlStateManager.color4f(1.2f, 0.8F, 0.8F, 1.0F);
				}

				if (highPolySkin.bodyModel != null) {
					EaglercraftGPU.drawHighPoly(EaglerMeshLoader.getEaglerMesh(highPolySkin.bodyModel));
				}
				float jumpFactor = 0.0f;

				if (highPolySkin.headModel != null) {
					if (highPolySkin == HighPolySkin.BABY_CHARLES) {
						long millis = EagRuntime.steadyTimeMillis();
						float partialTicks = (float) ((millis - abstractclientplayer.eaglerHighPolyAnimationTick) * 0.02);
						abstractclientplayer.eaglerHighPolyAnimationTick = millis;

						if (partialTicks < 0.0f) {
							partialTicks = 0.0f;
						}
						if (partialTicks > 1.0f) {
							partialTicks = 1.0f;
						}

						float jumpFac = (float) (abstractclientplayer.posY - abstractclientplayer.prevPosY);
						if (jumpFac < 0.0f && !abstractclientplayer.collidedVertically) {
							jumpFac = -jumpFac;
							jumpFac *= 0.1f;
						}
						jumpFac -= 0.05f;
						if (jumpFac > 0.1f && !abstractclientplayer.collidedVertically) {
							jumpFac = 0.1f;
						} else if (jumpFac < 0.0f) {
							jumpFac = 0.0f;
						} else if (jumpFac > 0.1f && abstractclientplayer.collidedVertically) {
							jumpFac = 0.1f;
						} else if (jumpFac > 0.4f) {
							jumpFac = 0.4f;
						}
						jumpFac *= 10.0f;

						abstractclientplayer.eaglerHighPolyAnimationFloat3 += (jumpFac / (jumpFac + 1.0f)) * 6.0f * partialTicks;

						if (Float.isInfinite(abstractclientplayer.eaglerHighPolyAnimationFloat3)) {
							abstractclientplayer.eaglerHighPolyAnimationFloat3 = 1.0f;
						} else if (abstractclientplayer.eaglerHighPolyAnimationFloat3 > 1.0f) {
							abstractclientplayer.eaglerHighPolyAnimationFloat3 = 1.0f;
						} else if (abstractclientplayer.eaglerHighPolyAnimationFloat3 < -1.0f) {
							abstractclientplayer.eaglerHighPolyAnimationFloat3 = -1.0f;
						}

						abstractclientplayer.eaglerHighPolyAnimationFloat2 += abstractclientplayer.eaglerHighPolyAnimationFloat3
								* partialTicks;

						abstractclientplayer.eaglerHighPolyAnimationFloat5 += partialTicks;
						while (abstractclientplayer.eaglerHighPolyAnimationFloat5 > 0.05f) {
							abstractclientplayer.eaglerHighPolyAnimationFloat5 -= 0.05f;
							abstractclientplayer.eaglerHighPolyAnimationFloat3 *= 0.99f;
							abstractclientplayer.eaglerHighPolyAnimationFloat2 *= 0.9f;
						}

						jumpFactor = abstractclientplayer.eaglerHighPolyAnimationFloat2;
						jumpFactor -= 0.12f;
						if (jumpFactor < 0.0f) {
							jumpFactor = 0.0f;
						}
						jumpFactor = jumpFactor / (jumpFactor + 2.0f);
						if (jumpFactor > 1.0f) {
							jumpFactor = 1.0f;
						}
					}
					if (jumpFactor > 0.0f) {
						GlStateManager.pushMatrix();
						GlStateManager.translatef(0.0f, jumpFactor * 3.0f, 0.0f);
					}

					EaglercraftGPU.drawHighPoly(EaglerMeshLoader.getEaglerMesh(highPolySkin.headModel));

					if (jumpFactor > 0.0f) {
						GlStateManager.popMatrix();
					}
				}

				if (highPolySkin.limbsModel != null && highPolySkin.limbsModel.length > 0) {
					for (int i = 0; i < highPolySkin.limbsModel.length; ++i) {
						float offset = 0.0f;
						if (highPolySkin.limbsOffset != null) {
							if (highPolySkin.limbsOffset.length == 1) {
								offset = highPolySkin.limbsOffset[0];
							} else {
								offset = highPolySkin.limbsOffset[i];
							}
						}

						GlStateManager.pushMatrix();

						if (offset != 0.0f || highPolySkin.limbsInitialRotation != 0.0f) {
							if (offset != 0.0f) {
								GlStateManager.translatef(0.0f, offset, 0.0f);
							}
							if (highPolySkin.limbsInitialRotation != 0.0f) {
								GlStateManager.rotatef(highPolySkin.limbsInitialRotation, 1.0f, 0.0f, 0.0f);
							}
						}

						if (highPolySkin == HighPolySkin.LONG_ARMS) {
							if (abstractclientplayer.isSwingInProgress) {
								float var17 = MathHelper.cos(
										-abstractclientplayer.getSwingProgress(f1) * (float) Math.PI * 2.0f - 1.2f) - 0.362f;
								var17 *= var17;
								GlStateManager.rotatef(-var17 * 20.0f, 1.0f, 0.0f, 0.0f);
							}
						} else if (highPolySkin == HighPolySkin.WEIRD_CLIMBER_DUDE) {
							if (abstractclientplayer.isSwingInProgress) {
								float var17 = MathHelper.cos(
										-abstractclientplayer.getSwingProgress(f1) * (float) Math.PI * 2.0f - 1.2f) - 0.362f;
								var17 *= var17;
								GlStateManager.rotatef(var17 * 60.0f, 1.0f, 0.0f, 0.0f);
							}
							GlStateManager.rotatef(40.0f * var15, 1.0f, 0.0f, 0.0f);
						} else if (highPolySkin == HighPolySkin.LAXATIVE_DUDE) {
							float fff = (i == 0) ? 1.0f : -1.0f;
							float swing = (MathHelper.cos(f10 % 100000.0f) * fff + 0.2f) * var15;
							float swing2 = (MathHelper.cos(f10 % 100000.0f) * fff * 0.5f + 0.0f) * var15;
							GlStateManager.rotatef(swing * 25.0f, 1.0f, 0.0f, 0.0f);
							if (abstractclientplayer.isSwingInProgress) {
								float var17 = MathHelper.cos(
										-abstractclientplayer.getSwingProgress(f1) * (float) Math.PI * 2.0f - 1.2f) - 0.362f;
								var17 *= var17;
								GlStateManager.rotatef(-var17 * 25.0f, 1.0f, 0.0f, 0.0f);
							}

							tmpMatrix.setIdentity();
							tmpMatrix.m21 = swing2;
							tmpMatrix.m23 = swing2 * -0.2f;
							net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.multMatrix(tmpMatrix);
						}

						if (i != 0) {
							mc.getTextureManager().bindTexture(highPolySkin.texture);
							if (abstractclientplayer.hurtTime > 0 || abstractclientplayer.deathTime > 0) {
								GlStateManager.color4f(1.2f, 0.8F, 0.8F, 1.0F);
							} else {
								GlStateManager.color4f(1.0f, 1.0F, 1.0F, 1.0F);
							}
						}
						EaglercraftGPU.drawHighPoly(EaglerMeshLoader.getEaglerMesh(highPolySkin.limbsModel[i]));

						if (i == 0) {
							GlStateManager.pushMatrix();

							GlStateManager.translatef(-0.287f, 0.05f, 0.0f);

							if (highPolySkin == HighPolySkin.LONG_ARMS) {
								GlStateManager.translatef(1.72f, 2.05f, -0.24f);
								ItemStack stk = abstractclientplayer.getHeldItemMainhand();
								if (!stk.isEmpty()) {
									Item itm = stk.getItem();
									if (itm == Items.BOW) {
										GlStateManager.translatef(-0.22f, 0.8f, 0.6f);
										GlStateManager.rotatef(-90.0f, 1.0f, 0.0f, 0.0f);
									} else if (itm instanceof ItemBlock
											&& !((ItemBlock) itm).getBlock().getDefaultState().isNormalCube()) {
										GlStateManager.translatef(0.0f, -0.1f, 0.13f);
									}
								}
							} else if (highPolySkin == HighPolySkin.WEIRD_CLIMBER_DUDE) {
								GlStateManager.translatef(-0.029f, 1.2f, -3f);
								GlStateManager.rotatef(-5.0f, 0.0f, 1.0f, 0.0f);
								float var17 = -1.2f * var15;
								if (abstractclientplayer.isSwingInProgress) {
									float vvar17 = MathHelper.cos(
											-abstractclientplayer.getSwingProgress(f1) * (float) Math.PI * 2.0f - 1.2f) - 0.362f;
									var17 = vvar17 < var17 ? vvar17 : var17;
								}
								GlStateManager.translatef(-0.02f * var17, 0.42f * var17, var17 * 0.35f);
								GlStateManager.rotatef(var17 * 30.0f, 1.0f, 0.0f, 0.0f);
								GlStateManager.rotatef(110.0f, 1.0f, 0.0f, 0.0f);
								ItemStack stk = abstractclientplayer.getHeldItemMainhand();
								if (!stk.isEmpty()) {
									Item itm = stk.getItem();
									if (itm == Items.BOW) {
										GlStateManager.translatef(-0.18f, 1.0f, 0.4f);
										GlStateManager.rotatef(-95.0f, 1.0f, 0.0f, 0.0f);
									} else if (itm instanceof ItemBlock
											&& !((ItemBlock) itm).getBlock().getDefaultState().isNormalCube()) {
										GlStateManager.translatef(0.0f, -0.1f, 0.13f);
									}
								}
							} else if (highPolySkin == HighPolySkin.LAXATIVE_DUDE) {
								GlStateManager.translatef(1.291f, 2.44f, -2.18f);
								GlStateManager.rotatef(95.0f, 1.0f, 0.0f, 0.0f);
								ItemStack stk = abstractclientplayer.getHeldItemMainhand();
								if (!stk.isEmpty()) {
									Item itm = stk.getItem();
									if (itm == Items.BOW) {
										GlStateManager.translatef(-0.65f, 1.3f, -0.1f);
										GlStateManager.rotatef(180.0f, 0.0f, 0.0f, 1.0f);
										GlStateManager.rotatef(20.0f, 1.0f, 0.0f, 0.0f);
									} else if (itm instanceof ItemBlock
											&& !((ItemBlock) itm).getBlock().getDefaultState().isNormalCube()) {
										GlStateManager.translatef(0.0f, -0.35f, 0.4f);
									}
								}
							}

							renderHeldItem(abstractclientplayer, f1);
							GlStateManager.popMatrix();
						}

						GlStateManager.popMatrix();
					}
				}

				if (highPolySkin.eyesModel != null) {
					float ff = 0.00416f;
					int brightness = abstractclientplayer.getBrightnessForRender();
					float blockLight = (brightness % 65536) * ff;
					float skyLight = (brightness / 65536) * ff;
					float sunCurve = (float) ((abstractclientplayer.world.getDayTime() + 4000l) % 24000) / 24000.0f;
					sunCurve = MathHelper.clamp(
							9.8f - MathHelper.abs(sunCurve * 5.0f + sunCurve * sunCurve * 45.0f - 14.3f) * 0.7f, 0.0f, 1.0f);
					skyLight = skyLight * (sunCurve * 0.85f + 0.15f);
					blockLight = blockLight * (sunCurve * 0.3f + 0.7f);
					float eyeBrightness = blockLight;
					if (skyLight > eyeBrightness) {
						eyeBrightness = skyLight;
					}
					eyeBrightness += blockLight * 0.2f;
					eyeBrightness = 1.0f - eyeBrightness;
					eyeBrightness = MathHelper.clamp(eyeBrightness * 1.9f - 1.0f, 0.0f, 1.0f);
					if (eyeBrightness > 0.1f) {
						GlStateManager.enableBlend();
						GlStateManager.blendFunc(GL_ONE, GL_ONE);
						GlStateManager.color4f(eyeBrightness * 7.0f, eyeBrightness * 7.0f, eyeBrightness * 7.0f, 1.0f);
						if (jumpFactor > 0.0f) {
							GlStateManager.pushMatrix();
							GlStateManager.translatef(0.0f, jumpFactor * 3.0f, 0.0f);
						}
						GlStateManager.disableTexture2D();
						GlStateManager.disableLighting();
						GlStateManager.enableCull();

						EaglercraftGPU.drawHighPoly(EaglerMeshLoader.getEaglerMesh(highPolySkin.eyesModel));

						GlStateManager.enableTexture2D();
						GlStateManager.enableLighting();
						GlStateManager.disableCull();
						if (jumpFactor > 0.0f) {
							GlStateManager.popMatrix();
						}
						GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);
						GlStateManager.disableBlend();
					}
				}
			} catch (Throwable t) {
				logger.error("Couldn't render entity");
				logger.error(t);
			}
			GlStateManager.activeTexture(OpenGlHelper.GL_TEXTURE1);
			GlStateManager.enableTexture2D();
			GlStateManager.activeTexture(OpenGlHelper.GL_TEXTURE0);
			GlStateManager.enableCull();
			GlStateManager.popMatrix();
			if (!this.renderOutlines) {
				this.renderName(abstractclientplayer, d0, nameY, d2);
			}
		}
	}

	public void renderRightArm(AbstractClientPlayer clientPlayer) {
	}

	public void renderLeftArm(AbstractClientPlayer clientPlayer) {
	}

	protected void renderHeldItem(AbstractClientPlayer clientPlayer, float partialTicks) {
		ItemStack itemstack = clientPlayer.getHeldItemMainhand();
		if (!itemstack.isEmpty()) {
			GlStateManager.pushMatrix();
			GlStateManager.translatef(-0.11F, 0.475F, 0.25F);
			if (clientPlayer.fishEntity != null) {
				itemstack = new ItemStack(Items.FISHING_ROD, 0);
			}

			Item item = itemstack.getItem();
			Minecraft minecraft = Minecraft.getInstance();
			if (item instanceof ItemBlock && Block.getBlockFromItem(item).getDefaultState()
					.getRenderType() == EnumBlockRenderType.ENTITYBLOCK_ANIMATED) {
				GlStateManager.translatef(0.0F, 0.1875F, -0.3125F);
				GlStateManager.rotatef(20.0F, 1.0F, 0.0F, 0.0F);
				GlStateManager.rotatef(45.0F, 0.0F, 1.0F, 0.0F);
				float f1 = 0.375F;
				GlStateManager.scalef(-f1, -f1, f1);
			}

			if (clientPlayer.isSneaking()) {
				GlStateManager.translatef(0.0F, 0.203125F, 0.0F);
			}

			minecraft.getItemRenderer().renderItem(itemstack, clientPlayer,
					ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, false);
			GlStateManager.popMatrix();
		}
	}

	public void renderLivingAt(AbstractClientPlayer abstractclientplayer, double d0, double d1, double d2) {
		if (abstractclientplayer.isAlive() && abstractclientplayer.isPlayerSleeping()) {
			super.renderLivingAt(abstractclientplayer, d0 - (double) abstractclientplayer.renderOffsetX,
					d1 - (double) abstractclientplayer.renderOffsetY, d2 - (double) abstractclientplayer.renderOffsetZ);
		} else {
			super.renderLivingAt(abstractclientplayer, d0, d1, d2);
		}
	}
}
