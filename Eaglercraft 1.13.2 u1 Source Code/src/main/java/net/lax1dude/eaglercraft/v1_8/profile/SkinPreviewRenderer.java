package net.lax1dude.eaglercraft.v1_8.profile;

import net.lax1dude.eaglercraft.v1_8.EagRuntime;
import net.lax1dude.eaglercraft.v1_8.opengl.EaglerMeshLoader;
import net.lax1dude.eaglercraft.v1_8.opengl.EaglercraftGPU;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.model.ModelBase;
import net.minecraft.client.renderer.entity.model.ModelBiped;
import net.minecraft.client.renderer.entity.model.ModelPlayer;
import net.minecraft.client.renderer.entity.model.ModelZombie;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class SkinPreviewRenderer {

	private static ModelPlayer playerModelSteve;
	private static ModelPlayer playerModelAlex;
	private static ModelZombie playerModelZombie;
	private static boolean initialized;

	private SkinPreviewRenderer() {
	}

	private static void lazyInit() {
		if(initialized) {
			return;
		}
		playerModelSteve = new ModelPlayer(0.0f, false);
		playerModelSteve.isChild = false;
		playerModelAlex = new ModelPlayer(0.0f, true);
		playerModelAlex.isChild = false;
		playerModelZombie = new ModelZombie(0.0f, false);
		playerModelZombie.isChild = false;
		initialized = true;
	}

	public static void renderPreview(int x, int y, int mx, int my, SkinModel skinModel) {
		renderPreview(x, y, mx, my, false, skinModel, null, null);
	}

	public static void renderPreview(int x, int y, int mx, int my, boolean capeMode, SkinModel skinModel, ResourceLocation skinTexture, ResourceLocation capeTexture) {
		lazyInit();
		x = x + 40;
		ModelBiped model;
		if(skinModel == null) {
			skinModel = SkinModel.STEVE;
		}
		switch(skinModel) {
		case ALEX:
			model = playerModelAlex;
			break;
		case ZOMBIE:
			model = playerModelZombie;
			break;
		case LONG_ARMS:
		case WEIRD_CLIMBER_DUDE:
		case LAXATIVE_DUDE:
		case BABY_CHARLES:
		case BABY_WINSTON:
			if(skinModel.highPoly != null && Minecraft.getInstance().gameSettings.enableFNAWSkins) {
				renderHighPoly(x, y, mx, my, skinModel.highPoly);
				return;
			}
			model = playerModelSteve;
			if(skinTexture == null) {
				skinTexture = skinModel.highPoly != null ? skinModel.highPoly.fallbackTexture : DefaultSkins.DEFAULT_STEVE.location;
			}
			break;
		case STEVE:
		default:
			model = playerModelSteve;
			break;
		}

		if(skinTexture == null) {
			skinTexture = DefaultSkins.DEFAULT_STEVE.location;
		}

		net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.enableDepth();
		GlStateManager.enableTexture2D();
		GlStateManager.disableBlend();
		GlStateManager.disableCull();
		GlStateManager.enableAlphaTest();
		GlStateManager.alphaFunc(516, 0.1F);
		GlStateManager.color4f(1.0f, 1.0f, 1.0f, 1.0f);

		GlStateManager.pushMatrix();
		GlStateManager.translatef((float)x, (float)(y - 80), 100.0f);
		GlStateManager.scalef(50.0f, 50.0f, 50.0f);
		GlStateManager.rotatef(180.0f, 1.0f, 0.0f, 0.0f);
		GlStateManager.scalef(1.0f, -1.0f, 1.0f);
		GlStateManager.rotatef(180.0f, 0.0f, 1.0f, 0.0f);

		RenderHelper.enableGUIStandardItemLighting();

		GlStateManager.translatef(0.0f, 1.0f, 0.0f);
		if(capeMode) {
			GlStateManager.rotatef(140.0f, 0.0f, 1.0f, 0.0f);
			mx = x - (x - mx) - 20;
			GlStateManager.rotatef((y - my) * -0.02f, 1.0f, 0.0f, 0.0f);
		}else {
			GlStateManager.rotatef((y - my) * -0.06f, 1.0f, 0.0f, 0.0f);
		}
		GlStateManager.rotatef((mx - x) * 0.06f, 0.0f, 1.0f, 0.0f);
		GlStateManager.translatef(0.0f, -1.0f, 0.0f);

		Minecraft.getInstance().getTextureManager().bindTexture(skinTexture);
		net.minecraft.entity.Entity player = Minecraft.getInstance().player;
		if(player != null) {
			model.render(player, 0.0f, 0.0f, (float)(EagRuntime.steadyTimeMillis() % 2000000) / 50f, (mx - x) * 0.06f, (y - my) * -0.1f, 0.0625f);
		} else {
			pose(model, (float)(EagRuntime.steadyTimeMillis() % 2000000) / 50f, (mx - x) * 0.06f, (y - my) * -0.1f);
			renderModel(model, 0.0625f);
		}

		if(capeTexture != null && model instanceof ModelPlayer) {
			Minecraft.getInstance().getTextureManager().bindTexture(capeTexture);
			GlStateManager.pushMatrix();
			GlStateManager.translatef(0.0F, 0.0F, 0.125F);
			GlStateManager.rotatef(6.0F, 1.0F, 0.0F, 0.0F);
			GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
			((ModelPlayer)model).renderCape(0.0625f);
			GlStateManager.popMatrix();
		}

		GlStateManager.popMatrix();
		net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.disableDepth();
		GlStateManager.disableLighting();
	}

	private static void pose(ModelBiped model, float ageInTicks, float netHeadYaw, float headPitch) {
		model.isChild = false;
		model.isRiding = false;
		model.isSneak = false;
		model.swingProgress = 0.0F;
		model.bipedHead.rotateAngleY = netHeadYaw * ((float)Math.PI / 180F);
		model.bipedHead.rotateAngleX = headPitch * ((float)Math.PI / 180F);
		model.bipedHead.rotateAngleZ = 0.0F;
		model.bipedHeadwear.rotateAngleX = model.bipedHead.rotateAngleX;
		model.bipedHeadwear.rotateAngleY = model.bipedHead.rotateAngleY;
		model.bipedHeadwear.rotateAngleZ = model.bipedHead.rotateAngleZ;
		model.bipedBody.rotateAngleX = 0.0F;
		model.bipedBody.rotateAngleY = 0.0F;
		model.bipedBody.rotateAngleZ = 0.0F;
		model.bipedRightArm.rotateAngleX = MathHelper.cos(ageInTicks * 0.067F) * 0.05F;
		model.bipedLeftArm.rotateAngleX = -MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
		model.bipedRightArm.rotateAngleY = 0.0F;
		model.bipedLeftArm.rotateAngleY = 0.0F;
		model.bipedRightArm.rotateAngleZ = MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
		model.bipedLeftArm.rotateAngleZ = -MathHelper.cos(ageInTicks * 0.09F) * 0.05F - 0.05F;
		model.bipedRightLeg.rotateAngleX = 0.0F;
		model.bipedLeftLeg.rotateAngleX = 0.0F;
		model.bipedRightLeg.rotateAngleY = 0.0F;
		model.bipedLeftLeg.rotateAngleY = 0.0F;
		model.bipedRightLeg.rotateAngleZ = 0.0F;
		model.bipedLeftLeg.rotateAngleZ = 0.0F;
		if(model instanceof ModelPlayer) {
			ModelPlayer pm = (ModelPlayer)model;
			ModelBase.copyModelAngles(pm.bipedLeftArm, pm.bipedLeftArmwear);
			ModelBase.copyModelAngles(pm.bipedRightArm, pm.bipedRightArmwear);
			ModelBase.copyModelAngles(pm.bipedLeftLeg, pm.bipedLeftLegwear);
			ModelBase.copyModelAngles(pm.bipedRightLeg, pm.bipedRightLegwear);
			ModelBase.copyModelAngles(pm.bipedBody, pm.bipedBodyWear);
		}
	}

	private static void renderModel(ModelBiped model, float scale) {
		model.bipedHead.render(scale);
		model.bipedBody.render(scale);
		model.bipedRightArm.render(scale);
		model.bipedLeftArm.render(scale);
		model.bipedRightLeg.render(scale);
		model.bipedLeftLeg.render(scale);
		model.bipedHeadwear.render(scale);
		if(model instanceof ModelPlayer) {
			ModelPlayer pm = (ModelPlayer)model;
			pm.bipedLeftLegwear.render(scale);
			pm.bipedRightLegwear.render(scale);
			pm.bipedLeftArmwear.render(scale);
			pm.bipedRightArmwear.render(scale);
			pm.bipedBodyWear.render(scale);
		}
	}

	private static void renderHighPoly(int x, int y, int mx, int my, HighPolySkin msh) {
		net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.enableTexture2D();
		net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.disableBlend();
		net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.disableCull();
		net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
		net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.pushMatrix();
		net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.translate(x, y - 80.0f, 100.0f);
		net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.scale(50.0f, 50.0f, 50.0f);
		net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.rotate(180.0f, 1.0f, 0.0f, 0.0f);
		net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.scale(1.0f, -1.0f, 1.0f);
		net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.translate(0.0f, 1.0f, 0.0f);
		net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.rotate((y - my) * -0.06f, 1.0f, 0.0f, 0.0f);
		net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.rotate((mx - x) * 0.06f, 0.0f, 1.0f, 0.0f);
		net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.rotate(180.0f, 0.0f, 0.0f, 1.0f);
		net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.translate(0.0f, -0.6f, 0.0f);
		net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.scale(HighPolySkin.highPolyScale, HighPolySkin.highPolyScale, HighPolySkin.highPolyScale);
		Minecraft.getInstance().getTextureManager().bindTexture(msh.texture);
		if(msh.bodyModel != null) {
			EaglercraftGPU.drawHighPoly(EaglerMeshLoader.getEaglerMesh(msh.bodyModel));
		}
		if(msh.headModel != null) {
			EaglercraftGPU.drawHighPoly(EaglerMeshLoader.getEaglerMesh(msh.headModel));
		}
		if(msh.limbsModel != null && msh.limbsModel.length > 0) {
			for(int i = 0; i < msh.limbsModel.length; ++i) {
				float offset = 0.0f;
				if(msh.limbsOffset != null) {
					offset = msh.limbsOffset.length == 1 ? msh.limbsOffset[0] : msh.limbsOffset[i];
				}
				if(offset != 0.0f || msh.limbsInitialRotation != 0.0f) {
					net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.pushMatrix();
					if(offset != 0.0f) {
						net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.translate(0.0f, offset, 0.0f);
					}
					if(msh.limbsInitialRotation != 0.0f) {
						net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.rotate(msh.limbsInitialRotation, 1.0f, 0.0f, 0.0f);
					}
				}
				EaglercraftGPU.drawHighPoly(EaglerMeshLoader.getEaglerMesh(msh.limbsModel[i]));
				if(offset != 0.0f || msh.limbsInitialRotation != 0.0f) {
					net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.popMatrix();
				}
			}
		}
		net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.popMatrix();
		net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.disableLighting();
	}
}
