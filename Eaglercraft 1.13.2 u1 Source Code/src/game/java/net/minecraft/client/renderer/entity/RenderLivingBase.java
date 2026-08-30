package net.minecraft.client.renderer.entity;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.ModelBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public abstract class RenderLivingBase<T extends EntityLivingBase> extends Render<T> {
   private static final Logger LOGGER = LogManager.getLogger();
   protected ModelBase mainModel;
   protected List<LayerRenderer<T>> layerRenderers = Lists.newArrayList();
   protected boolean renderMarker;

   public RenderLivingBase(RenderManager renderManagerIn, ModelBase modelBaseIn, float shadowSizeIn) {
      super(renderManagerIn);
      this.mainModel = modelBaseIn;
      this.shadowSize = shadowSizeIn;
   }

   protected <V extends EntityLivingBase, U extends LayerRenderer<V>> boolean addLayer(U layer) {
      return this.layerRenderers.add((LayerRenderer<T>)layer);
   }

   public ModelBase getMainModel() {
      return this.mainModel;
   }

   protected float interpolateRotation(float prevYawOffset, float yawOffset, float partialTicks) {
      float f;
      for(f = yawOffset - prevYawOffset; f < -180.0F; f += 360.0F) {
         ;
      }

      while(f >= 180.0F) {
         f -= 360.0F;
      }

      return prevYawOffset + partialTicks * f;
   }

   public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
      GlStateManager.pushMatrix();
      GlStateManager.disableCull();
      this.mainModel.swingProgress = this.getSwingProgress(entity, partialTicks);
      this.mainModel.isRiding = entity.isPassenger();
      this.mainModel.isChild = entity.isChild();

      try {
         float f = this.interpolateRotation(entity.prevRenderYawOffset, entity.renderYawOffset, partialTicks);
         float f1 = this.interpolateRotation(entity.prevRotationYawHead, entity.rotationYawHead, partialTicks);
         float f2 = f1 - f;
         if (entity.isPassenger() && entity.getRidingEntity() instanceof EntityLivingBase) {
            EntityLivingBase entitylivingbase = (EntityLivingBase)entity.getRidingEntity();
            f = this.interpolateRotation(entitylivingbase.prevRenderYawOffset, entitylivingbase.renderYawOffset, partialTicks);
            f2 = f1 - f;
            float f3 = MathHelper.wrapDegrees(f2);
            if (f3 < -85.0F) {
               f3 = -85.0F;
            }

            if (f3 >= 85.0F) {
               f3 = 85.0F;
            }

            f = f1 - f3;
            if (f3 * f3 > 2500.0F) {
               f += f3 * 0.2F;
            }

            f2 = f1 - f;
         }

         float f7 = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
         this.renderLivingAt(entity, x, y, z);
         float f8 = this.handleRotationFloat(entity, partialTicks);
         this.applyRotations(entity, f8, f, partialTicks);
         float f4 = this.prepareScale(entity, partialTicks);
         float f5 = 0.0F;
         float f6 = 0.0F;
         if (!entity.isPassenger()) {
            f5 = entity.prevLimbSwingAmount + (entity.limbSwingAmount - entity.prevLimbSwingAmount) * partialTicks;
            f6 = entity.limbSwing - entity.limbSwingAmount * (1.0F - partialTicks);
            if (entity.isChild()) {
               f6 *= 3.0F;
            }

            if (f5 > 1.0F) {
               f5 = 1.0F;
            }
         }

         GlStateManager.enableAlphaTest();
         this.mainModel.setLivingAnimations(entity, f6, f5, partialTicks);
         this.mainModel.setRotationAngles(f6, f5, f8, f2, f7, f4, entity);
         if (this.renderOutlines) {
            boolean flag1 = this.setScoreTeamColor(entity);
            GlStateManager.enableColorMaterial();
            GlStateManager.enableOutlineMode(this.getTeamColor(entity));
            if (!this.renderMarker) {
               this.renderModel(entity, f6, f5, f8, f2, f7, f4);
            }

            if (!(entity instanceof EntityPlayer) || !((EntityPlayer)entity).isSpectator()) {
               this.renderLayers(entity, f6, f5, partialTicks, f8, f2, f7, f4);
            }

            GlStateManager.disableOutlineMode();
            GlStateManager.disableColorMaterial();
            if (flag1) {
               this.unsetScoreTeamColor();
            }
         } else {
            boolean flag = this.setDoRenderBrightness(entity, partialTicks);
            this.renderModel(entity, f6, f5, f8, f2, f7, f4);
            if (flag) {
               this.unsetBrightness();
            }

            GlStateManager.depthMask(true);
            if (!(entity instanceof EntityPlayer) || !((EntityPlayer)entity).isSpectator()) {
               this.renderLayers(entity, f6, f5, partialTicks, f8, f2, f7, f4);
            }
         }

         GlStateManager.disableRescaleNormal();
      } catch (Exception exception) {
         LOGGER.error("Couldn't render entity", (Throwable)exception);
      }

      GlStateManager.activeTexture(OpenGlHelper.GL_TEXTURE1);
      GlStateManager.enableTexture2D();
      GlStateManager.activeTexture(OpenGlHelper.GL_TEXTURE0);
      GlStateManager.enableCull();
      GlStateManager.popMatrix();
      super.doRender(entity, x, y, z, entityYaw, partialTicks);
   }

   public float prepareScale(T entitylivingbaseIn, float partialTicks) {
      GlStateManager.enableRescaleNormal();
      GlStateManager.scalef(-1.0F, -1.0F, 1.0F);
      this.preRenderCallback(entitylivingbaseIn, partialTicks);
      float f = 0.0625F;
      GlStateManager.translatef(0.0F, -1.501F, 0.0F);
      return 0.0625F;
   }

   protected boolean setScoreTeamColor(T entityLivingBaseIn) {
      GlStateManager.disableLighting();
      GlStateManager.activeTexture(OpenGlHelper.GL_TEXTURE1);
      GlStateManager.disableTexture2D();
      GlStateManager.activeTexture(OpenGlHelper.GL_TEXTURE0);
      return true;
   }

   protected void unsetScoreTeamColor() {
      GlStateManager.enableLighting();
      GlStateManager.activeTexture(OpenGlHelper.GL_TEXTURE1);
      GlStateManager.enableTexture2D();
      GlStateManager.activeTexture(OpenGlHelper.GL_TEXTURE0);
   }

   protected void renderModel(T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor) {
      boolean flag = this.isVisible(entitylivingbaseIn);
      boolean flag1 = !flag && !entitylivingbaseIn.isInvisibleToPlayer(Minecraft.getInstance().player);
      if (flag || flag1) {
         if (!this.bindEntityTexture(entitylivingbaseIn)) {
            return;
         }

         if (flag1) {
            GlStateManager.enableBlendProfile(GlStateManager.Profile.TRANSPARENT_MODEL);
         }

         this.mainModel.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scaleFactor);
         if (flag1) {
            GlStateManager.disableBlendProfile(GlStateManager.Profile.TRANSPARENT_MODEL);
         }
      }

   }

   protected boolean isVisible(T p_193115_1_) {
      return !p_193115_1_.isInvisible() || this.renderOutlines;
   }

   protected boolean setDoRenderBrightness(T entityLivingBaseIn, float partialTicks) {
      return this.setBrightness(entityLivingBaseIn, partialTicks, true);
   }

   protected boolean setBrightness(T entitylivingbaseIn, float partialTicks, boolean combineTextures) {
      float f = entitylivingbaseIn.getBrightness();
      int i = this.getColorMultiplier(entitylivingbaseIn, f, partialTicks);
      boolean flag = (i >> 24 & 255) > 0;
      boolean flag1 = entitylivingbaseIn.hurtTime > 0 || entitylivingbaseIn.deathTime > 0;
      if (!flag && !flag1) {
         return false;
      } else if (!flag && !combineTextures) {
         return false;
      } else {
         net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.enableShaderBlendAdd();
         float f1 = 1.0F - (float)(i >>> 24 & 255) / 255.0F;
         float f2 = (float)(i >>> 16 & 255) / 255.0F;
         float f3 = (float)(i >>> 8 & 255) / 255.0F;
         float f4 = (float)(i & 255) / 255.0F;
         net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.setShaderBlendSrc(f1, f1, f1, 1.0F);
         net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.setShaderBlendAdd(f2 * f1 + 0.4F, f3 * f1, f4 * f1, 0.0F);
         return true;
      }
   }

   protected void unsetBrightness() {
      net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager.disableShaderBlendAdd();
   }

   protected void renderLivingAt(T entityLivingBaseIn, double x, double y, double z) {
      GlStateManager.translatef((float)x, (float)y, (float)z);
   }

   protected void applyRotations(T entityLiving, float ageInTicks, float rotationYaw, float partialTicks) {
      GlStateManager.rotatef(180.0F - rotationYaw, 0.0F, 1.0F, 0.0F);
      if (entityLiving.deathTime > 0) {
         float f = ((float)entityLiving.deathTime + partialTicks - 1.0F) / 20.0F * 1.6F;
         f = MathHelper.sqrt(f);
         if (f > 1.0F) {
            f = 1.0F;
         }

         GlStateManager.rotatef(f * this.getDeathMaxRotation(entityLiving), 0.0F, 0.0F, 1.0F);
      } else if (entityLiving.isSpinAttacking()) {
         GlStateManager.rotatef(-90.0F - entityLiving.rotationPitch, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotatef(((float)entityLiving.ticksExisted + partialTicks) * -75.0F, 0.0F, 1.0F, 0.0F);
      } else if (entityLiving.hasCustomName() || entityLiving instanceof EntityPlayer) {
         String s = TextFormatting.getTextWithoutFormattingCodes(entityLiving.getName().getString());
         if (s != null && ("Dinnerbone".equals(s) || "Grumm".equals(s)) && (!(entityLiving instanceof EntityPlayer) || ((EntityPlayer)entityLiving).isWearing(EnumPlayerModelParts.CAPE))) {
            GlStateManager.translatef(0.0F, entityLiving.height + 0.1F, 0.0F);
            GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
         }
      }

   }

   protected float getSwingProgress(T livingBase, float partialTickTime) {
      return livingBase.getSwingProgress(partialTickTime);
   }

   protected float handleRotationFloat(T livingBase, float partialTicks) {
      return (float)livingBase.ticksExisted + partialTicks;
   }

   protected void renderLayers(T entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scaleIn) {
      for(LayerRenderer<T> layerrenderer : this.layerRenderers) {
         boolean flag = this.setBrightness(entitylivingbaseIn, partialTicks, layerrenderer.shouldCombineTextures());
         layerrenderer.render(entitylivingbaseIn, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scaleIn);
         if (flag) {
            this.unsetBrightness();
         }
      }

   }

   protected float getDeathMaxRotation(T entityLivingBaseIn) {
      return 90.0F;
   }

   protected int getColorMultiplier(T entitylivingbaseIn, float lightBrightness, float partialTickTime) {
      return 0;
   }

   protected void preRenderCallback(T entitylivingbaseIn, float partialTickTime) {
   }

   public void renderName(T entity, double x, double y, double z) {
      if (this.canRenderName(entity)) {
         double d0 = entity.getDistanceSq(this.renderManager.renderViewEntity);
         float f = entity.isSneaking() ? 32.0F : 64.0F;
         if (!(d0 >= (double)(f * f))) {
            String s = entity.getDisplayName().getFormattedText();
            GlStateManager.alphaFunc(516, 0.1F);
            this.renderEntityName(entity, x, y, z, s, d0);
         }
      }
   }

   protected boolean canRenderName(T entity) {
      EntityPlayerSP entityplayersp = Minecraft.getInstance().player;
      boolean flag = !entity.isInvisibleToPlayer(entityplayersp);
      if (entity != entityplayersp) {
         Team team = entity.getTeam();
         Team team1 = entityplayersp.getTeam();
         if (team != null) {
            Team.EnumVisible team$enumvisible = team.getNameTagVisibility();
            switch(team$enumvisible) {
            case ALWAYS:
               return flag;
            case NEVER:
               return false;
            case HIDE_FOR_OTHER_TEAMS:
               return team1 == null ? flag : team.isSameTeam(team1) && (team.getSeeFriendlyInvisiblesEnabled() || flag);
            case HIDE_FOR_OWN_TEAM:
               return team1 == null ? flag : !team.isSameTeam(team1) && flag;
            default:
               return true;
            }
         }
      }

      return Minecraft.isGuiEnabled() && entity != this.renderManager.renderViewEntity && flag && !entity.isBeingRidden();
   }
}
